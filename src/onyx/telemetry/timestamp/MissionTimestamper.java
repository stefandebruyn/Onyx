package onyx.telemetry.timestamp;

import java.text.DecimalFormat;

/**
 * A variation on FormatTimestamper that uses the T+- notation common in aerospace missions.
 */
public class MissionTimestamper extends FormatTimestamper {

    /**
     * Creates a new timestamper with some format.
     *
     * @param format time value decimal format
     * @param open   string preceding the stamp
     * @param close  string succeeding the stamp
     */
    public MissionTimestamper(DecimalFormat format, String open, String close) {
        super(format, open, close);
    }

    /**
     * Creates a timestamp string from a numeric time value.
     *
     * @param time numeric point in time
     * @return timestamp text
     */
    @Override
    public String stamp(double time) {
        StringBuilder sb = new StringBuilder(timestampFormat.format(time));
        sb.insert(0, "T" + (time < 0 ? "" : "+"));
        return openBrace + sb.toString() + closeBrace;
    }
}
