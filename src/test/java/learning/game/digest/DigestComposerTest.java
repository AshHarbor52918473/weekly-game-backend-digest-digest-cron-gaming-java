package learning.game.digest;

import java.util.List;

public final class DigestComposerTest {
    public static void main(String[] args) {
        DigestComposer composer = new DigestComposer();
        var empty = new DigestComposer.WeeklySignals("teachers", List.of(), List.of(), List.of());
        check(composer.compose(empty).isEmpty(), "an inactive week should not send a digest");

        var active = new DigestComposer.WeeklySignals(
                "course game operators",
                List.of("Student-built history map"),
                List.of("Friday study tournament"),
                List.of("Review reported quiz skin"));
        DigestComposer.Digest digest = composer.compose(active).orElseThrow();
        check(digest.subject().contains("2 action items"), "events and moderation should count as actions");
        check(digest.body().contains("Review first: Review reported quiz skin"),
                "moderation should be taught as the first review priority");
        System.out.println("DigestComposerTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private DigestComposerTest() {}
}
