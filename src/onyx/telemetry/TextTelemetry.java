package onyx.telemetry;

/**
 * A telemetry module composed of plaintext.
 */
public abstract class TextTelemetry extends Telemetry {

    /**
     * Creates a new TextTelemetry at some position.
     *
     * @param x     horizontal position from interface left
     * @param y     vertical position from interface top
     * @param coded whether or not data contains formatting metacharacters
     */
    public TextTelemetry(int x, int y, boolean coded) {
        super(x, y, coded);
    }

    /**
     * Gets the telemetry text.
     *
     * @return array of individual lines of data
     */
    public abstract String[] data();
}
