package onyx.telemetry;

/**
 * A thread-safe collection of data for printing to a Display.
 */
public abstract class Telemetry {
    protected int x, y;
    protected boolean coded;

    /**
     * Creates a new piece of telemetry at some position.
     *
     * @param x horizontal position from interface left
     * @param y vertical position from interface top
     * @param coded whether or not data contains Onyx metacharacters
     */
    public Telemetry(int x, int y, boolean coded) {
        this.x = x;
        this.y = y;
        this.coded = coded;
    }

    /**
     * Gets whether or not the contained data should be processed for special codes by the interface (codes indicating
     * font color, weight, etc.).
     *
     * @return if the contained data should be treated as coded
     */
    public boolean coded() {
        return coded;
    }

    /**
     * Gets the x position of the telemetry.
     *
     * @return pixels from interface left
     */
    public int x() {
        return x;
    }

    /**
     * Gets the y position of the telemetry.
     *
     * @return pixels from interface top
     */
    public int y() {
        return y;
    }

    /**
     * Gets the width of this telemetry in pixels.
     *
     * @return width in pixels
     */
    public abstract int width();

    /**
     * Gets the height of this telemetry in pixels.
     *
     * @return height in pixels
     */
    public abstract int height();
}

