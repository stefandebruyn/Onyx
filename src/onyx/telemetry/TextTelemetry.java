package onyx.telemetry;

/**
 * A type of telemetry represented by text.
 */
public abstract class TextTelemetry extends Telemetry {

    /**
     * Creates a new piece of telemetry at some position.
     *
     * @param x horizontal position from interface left
     * @param y vertical position from interface top
     * @param coded whether or not data contains Onyx metacharacters
     */
    public TextTelemetry(int x, int y, boolean coded) {
        super(x, y, coded);
    }

    /**
     * Gets the telemetry in text form.
     *
     * @return telemetry
     */
    public abstract String[] data();
}
