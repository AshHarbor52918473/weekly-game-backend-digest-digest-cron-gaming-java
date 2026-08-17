package learning.game.digest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class JsonEnvelope {
    private static final Pattern OK = Pattern.compile("\\\"ok\\\"\\s*:\\s*(true|false)");
    private static final Pattern JOB_ID = Pattern.compile("\\\"job_id\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"");
    private static final Pattern ERROR = Pattern.compile("\\\"error\\\"\\s*:\\s*(\\{.*?})", Pattern.DOTALL);

    record Decoded(boolean ok, String jobId, String error) {}

    static Decoded decode(String json) {
        Matcher ok = OK.matcher(json);
        if (!ok.find()) {
            throw new IllegalArgumentException("Response is not an Infrai envelope.");
        }
        Matcher id = JOB_ID.matcher(json);
        Matcher error = ERROR.matcher(json);
        return new Decoded(
                Boolean.parseBoolean(ok.group(1)),
                id.find() ? unescape(id.group(1)) : null,
                error.find() ? error.group(1) : "unknown error");
    }

    static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static String unescape(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private JsonEnvelope() {}
}
