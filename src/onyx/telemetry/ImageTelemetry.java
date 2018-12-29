package onyx.telemetry;

import java.awt.image.BufferedImage;

/**
 * A telemetry module represented by an image.
 */
public abstract class ImageTelemetry extends Telemetry {
    protected BufferedImage image;
    protected int width, height;

    /**
     * Creates a new module.
     *
     * @param x      horizontal position from interface left
     * @param y      vertical position from interface top
     * @param coded  whether or not data contains Onyx metacharacters
     * @param width  image width in pixels
     * @param height image height in pixels
     */
    public ImageTelemetry(int x, int y, boolean coded, int width, int height) {
        super(x, y, coded);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the width of the image.
     *
     * @return width in pixels
     */
    public int width() {
        return width;
    }

    /**
     * Gets the height of the image.
     *
     * @return height in pixels
     */
    public int height() {
        return height;
    }

    /**
     * Renders a single frame of the image.
     *
     * @return image
     */
    public abstract BufferedImage render();
}
