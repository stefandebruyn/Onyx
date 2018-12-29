package onyx.telemetry.timestamp;

import java.text.DecimalFormat;

/**
 * A timestamp that formats the time with a decimal format and claps the resulting string in braces.
 */
public class FormatTimestamper implements Timestamper {
    protected DecimalFormat timestampFormat;
    protected String openBrace, closeBrace;

    /**
     * Creates a new timestamper with some format.
     *
     * @param format time value decimal format
     * @param open   string preceding the stamp
     * @param close  string succeeding the stamp
     */
    public FormatTimestamper(DecimalFormat format, String open, String close) {
        timestampFormat = format;
        openBrace = open;
        closeBrace = close;
    }

    /**
     * Creates a timestamp string from a numeric time value.
     *
     * @param time numeric point in time
     * @return timestamp text
     */
    public String stamp(double time) {
        return openBrace + timestampFormat.format(time) + closeBrace;
    }
}
