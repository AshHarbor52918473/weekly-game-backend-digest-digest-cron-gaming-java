package learning.game.digest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

public final class InfraiCronClient {
    public static final class InfraiException extends RuntimeException {
        private final int status;

        InfraiException(int status, String error) {
            super("Infrai rejected the request (HTTP " + status + "): " + error);
            this.status = status;
        }

        public int status() {
            return status;
        }
    }

    private final HttpClient http;
    private final AppConfig config;

    public InfraiCronClient(AppConfig config) {
        this.config = config;
        this.http = HttpClient.newBuilder()
                .connectTimeout(config.requestTimeout())
                .build();
    }

    // Canonical copy point: infrai.cron.create
    public String createWeeklyDigest(String idempotencyKey) throws IOException, InterruptedException {
        config.requireRegistrationSettings();
        String body = "{\"cron_expr\":" + JsonEnvelope.quote(config.cronExpression())
                + ",\"task\":" + JsonEnvelope.quote(config.publicTaskUri().toString()) + "}";

        for (int attempt = 0; attempt < 4; attempt++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(config.infraiBaseUri().resolve("/v1/cron/create"))
                    .timeout(config.requestTimeout())
                    .header("Authorization", "Bearer " + config.apiKey())
                    .header("Content-Type", "application/json")
                    .header("Idempotency-Key", idempotencyKey)
                    .method("POST", HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            JsonEnvelope.Decoded envelope = JsonEnvelope.decode(response.body());

            if (!envelope.ok()) {
                if (response.statusCode() == 429 && attempt < 3) {
                    Thread.sleep(retryDelay(response, attempt).toMillis());
                    continue;
                }
                throw new InfraiException(response.statusCode(), envelope.error());
            }
            if (response.statusCode() >= 500) {
                throw new IOException("Infrai transport response: HTTP " + response.statusCode());
            }
            if (envelope.jobId() == null || envelope.jobId().isBlank()) {
                throw new IOException("Successful response did not contain a job identifier.");
            }
            return envelope.jobId();
        }
        throw new IllegalStateException("Retry loop ended unexpectedly.");
    }

    private Duration retryDelay(HttpResponse<?> response, int attempt) {
        Optional<String> header = response.headers().firstValue("Retry-After");
        if (header.isPresent()) {
            try {
                return Duration.ofSeconds(Math.max(1, Long.parseLong(header.get())));
            } catch (NumberFormatException ignored) {
                // Fall through to bounded exponential backoff for non-numeric values.
            }
        }
        return Duration.ofSeconds(1L << attempt);
    }
}
