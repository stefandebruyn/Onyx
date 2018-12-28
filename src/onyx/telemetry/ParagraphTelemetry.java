package onyx.telemetry;

import java.awt.*;

/**
 * A telemetry readout with a title followed by several indented lines. Fully mutable.
 */
public class ParagraphTelemetry extends TextTelemetry {
    public static final String TAB = "    ";

    private final FontMetrics fontMetrics;
    private volatile String[] data;
    private int width = -1, height = -1;

    /**
     * Creates a new ParagraphTelemetry with data. Immutable once constructed.
     *
     * @param x     horizontal position from interface left
     * @param y     vertical position from interface top
     * @param coded whether or not the data contains Onyx metacharacters
     * @param title title text
     * @param data  text appearing in lines indented under the title
     */
    public ParagraphTelemetry(int x, int y, boolean coded, FontMetrics fontMetrics, String title, String... data) {
        super(x, y, coded);

        this.fontMetrics = fontMetrics;
        this.data = new String[data.length + 1];
        this.data[0] = title;

        for (int i = 0; i < data.length; i++)
            this.data[i + 1] = TAB + data[i];
    }

    /**
     * Gets the contained data.
     *
     * @return data
     */
    public String[] data() {
        return data;
    }

    /**
     * Gets the pixel width of paragraph (the longest line).
     *
     * @return width in pixels
     */
    public int width() {
        if (width != -1)
            return width;

        int longestLine = Integer.MIN_VALUE;

        for (String line : data) {
            int lineWidth = fontMetrics.stringWidth(line);
            if (lineWidth > longestLine)
                longestLine = lineWidth;
        }

        width = longestLine;
        return width;
    }

    /**
     * Gets the pixel height of the paragraph.
     *
     * @return height in pixels
     */
    public int height() {
        if (height != -1)
            return height;

        height = fontMetrics.getHeight() * (1 + data.length);
        return height;
    }

    /**
     * Updates the paragraph title text.
     *
     * @param newTitle new title text
     */
    public void setTitle(String newTitle) {
        data[0] = newTitle;
    }

    /**
     * Updates the text on a specific line in the paragraph.
     *
     * @param index   line index (0 for first line, just below the title)
     * @param newLine new line text (indenting is added automatically)
     */
    public void setLine(int index, String newLine) {
        data[index + 1] = TAB + newLine;
    }
}
