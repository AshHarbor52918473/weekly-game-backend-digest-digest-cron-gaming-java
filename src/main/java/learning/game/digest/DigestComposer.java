package learning.game.digest;

import java.util.List;
import java.util.Optional;

public final class DigestComposer {
    public record WeeklySignals(
            String audience,
            List<String> playerAssets,
            List<String> liveEvents,
            List<String> moderationQueue) {
        public WeeklySignals {
            playerAssets = List.copyOf(playerAssets);
            liveEvents = List.copyOf(liveEvents);
            moderationQueue = List.copyOf(moderationQueue);
        }
    }

    public record Digest(String subject, String body) {}

    public Optional<Digest> compose(WeeklySignals signals) {
        int actionCount = signals.liveEvents().size() + signals.moderationQueue().size();
        if (signals.playerAssets().isEmpty() && actionCount == 0) {
            return Optional.empty();
        }

        String subject = "Weekly game backend digest: " + actionCount + " action items";
        String body = String.join("\n",
                "Audience: " + signals.audience(),
                "Player-generated assets: " + signals.playerAssets().size(),
                "Live events: " + signals.liveEvents().size(),
                "Moderation queue: " + signals.moderationQueue().size(),
                "Review first: " + firstPriority(signals));
        return Optional.of(new Digest(subject, body));
    }

    private String firstPriority(WeeklySignals signals) {
        if (!signals.moderationQueue().isEmpty()) {
            return signals.moderationQueue().get(0);
        }
        if (!signals.liveEvents().isEmpty()) {
            return signals.liveEvents().get(0);
        }
        return signals.playerAssets().get(0);
    }
}
