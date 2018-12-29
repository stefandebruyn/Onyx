package onyx.telemetry.timestamp;

/**
 * A formatter for creating timestamp strings.
 */
public interface Timestamper {

    /**
     * Creates a new timestamp.
     *
     * @param time numeric point in time
     * @return timestamp text
     */
    public String stamp(double time);
}
