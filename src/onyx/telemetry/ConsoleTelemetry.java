package onyx.telemetry;

import onyx.telemetry.timestamp.Timestamper;

import java.awt.FontMetrics;

/**
 * A variation of the paragraph-style telemetry that displays a log of timestamped data.
 */
public class ConsoleTelemetry extends ParagraphTelemetry {
    private Timestamper timestamper;
    private int consolePosition = 0;

    /**
     * Creates a new module with an empty console.
     *
     * @param x           horizontal position from interface left
     * @param y           vertical position from interface top
     * @param coded       whether or not the data contains Onyx metacharacters
     * @param fontMetrics font metrics for text dimension calculations
     * @param title       title text
     * @param capacity    number of lines in the console
     */
    public ConsoleTelemetry(int x, int y, boolean coded, FontMetrics fontMetrics, String title, int capacity) {
        super(x, y, coded, fontMetrics, title);

        data = new String[capacity + 1];
        data[0] = title;
    }

    /**
     * Identifies a format to be used for timestamping console entries.
     *
     * @param stamper timestamp format
     */
    public void setTimestamper(Timestamper stamper) {
        timestamper = stamper;
    }

    /**
     * Adds a new entry to the console. If the console is full, all current entries will be pushed down one position,
     * and the entry in the first position will be lost.
     *
     * @param line new entry
     */
    public void log(String line) {
        if (consolePosition == data.length - 1)
            shuffle();

        data[consolePosition + 1] = line;
        consolePosition++;
    }

    /**
     * Adds a new entry with a timestamp.
     *
     * @param line new entry
     * @param time associated time
     * @see {@link #setTimestamper(Timestamper)}
     */
    public void log(String line, double time) {
        String stamp = timestamper == null ? "" : timestamper.stamp(time);
        log(stamp + line);
    }

    /**
     * Pushes all console entries down one position.
     */
    private void shuffle() {
        for (int i = 2; i < data.length; i++)
            data[i - 1] = data[i];

        consolePosition--;
    }
}
