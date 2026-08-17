package learning.game.digest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public final class GameDigestApplication {
    public static void main(String[] args) throws Exception {
        String command = args.length == 0 ? "preview" : args[0];
        AppConfig config = AppConfig.from(System.getenv());
        switch (command) {
            case "preview" -> printPreview();
            case "register" -> register(config);
            case "serve" -> serve(config);
            default -> throw new IllegalArgumentException("Use preview, register, or serve.");
        }
    }

    private static void printPreview() {
        new DigestComposer().compose(sampleSignals()).ifPresent(digest -> {
            System.out.println(digest.subject());
            System.out.println(digest.body());
        });
    }

    private static void register(AppConfig config) throws IOException, InterruptedException {
        String week = LocalDate.now(ZoneOffset.UTC).toString();
        String jobId = new InfraiCronClient(config).createWeeklyDigest("game-digest-" + week);
        System.out.println("Weekly digest scheduled with job id: " + jobId);
    }

    private static void serve(AppConfig config) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(config.port()), 0);
        server.createContext("/digest/run", GameDigestApplication::handleDigest);
        server.start();
        System.out.println("Digest endpoint listening on port " + config.port());
    }

    private static void handleDigest(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }
        String response = new DigestComposer().compose(sampleSignals())
                .map(digest -> digest.subject() + "\n" + digest.body())
                .orElse("No digest needed this week.");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static DigestComposer.WeeklySignals sampleSignals() {
        return new DigestComposer.WeeklySignals(
                "game backend team",
                List.of("Player-made tutorial island"),
                List.of("Weekend co-op tournament"),
                List.of("Review reported classroom badge"));
    }

    private GameDigestApplication() {}
}
