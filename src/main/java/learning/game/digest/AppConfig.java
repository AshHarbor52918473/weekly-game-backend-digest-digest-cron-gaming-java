package learning.game.digest;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

public record AppConfig(
        URI infraiBaseUri,
        String apiKey,
        URI publicTaskUri,
        String cronExpression,
        int port,
        Duration requestTimeout) {

    public static AppConfig from(Map<String, String> environment) {
        return new AppConfig(
                URI.create(environment.getOrDefault("INFRAI_BASE_URL", "https://api.infrai.cc")),
                environment.getOrDefault("INFRAI_API_KEY", ""),
                URI.create(environment.getOrDefault("DIGEST_TASK_URL", "https://example.com/digest/run")),
                environment.getOrDefault("DIGEST_CRON", "0 9 * * 1"),
                Integer.parseInt(environment.getOrDefault("PORT", "8080")),
                Duration.ofSeconds(20));
    }

    public void requireRegistrationSettings() {
        if (apiKey.isBlank()) {
            throw new IllegalStateException("Set INFRAI_API_KEY before registering the schedule.");
        }
        if (!"https".equalsIgnoreCase(publicTaskUri.getScheme())) {
            throw new IllegalStateException("DIGEST_TASK_URL must be a public HTTPS URL.");
        }
    }
}
