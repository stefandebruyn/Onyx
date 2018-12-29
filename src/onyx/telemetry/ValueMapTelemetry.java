package onyx.telemetry;

import java.awt.FontMetrics;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * An variation of the paragraph-style telemetry that displays a map of values and their labels. The module begins with
 * a title header, and is followed by indented lines, each a label-value pair separated by a colon.
 */
public class ValueMapTelemetry extends ParagraphTelemetry {
    protected HashMap<String, Object> valueMap = new HashMap<>();
    protected HashMap<String, Integer> indexMap = new HashMap<>();
    protected HashMap<String, DecimalFormat> formatMap = new HashMap<>();
    protected FontMetrics fontMetrics;
    protected String title;

    /**
     * Creates a new telemetry module with an empty value map.
     *
     * @param x           horizontal position from interface left
     * @param y           vertical position from interface top
     * @param coded       whether or not the data contains Onyx metacharacters
     * @param fontMetrics font metrics for text dimension calculations
     * @param title       title text
     */
    public ValueMapTelemetry(int x, int y, boolean coded, FontMetrics fontMetrics, String title) {
        super(x, y, coded, fontMetrics, title);

        this.fontMetrics = fontMetrics;
        this.title = title;
    }

    /**
     * Puts a label-value pair into the map. New labels are added to the end of the telemetry, and new labels overwrite
     * the old.
     *
     * @param identifier label
     * @param value      value
     */
    public void put(String identifier, Object value) {
        int oldSize = valueMap.size();

        valueMap.put(identifier, value);

        // Map changed size; increase size of data array and map the new value to the newly created index
        if (oldSize != valueMap.size()) {
            indexMap.put(identifier, valueMap.size() - 1);
            resize();
        }

        // Format value if a format was specified
        DecimalFormat format = formatMap.get(identifier);
        String valueString = format == null ? value.toString() : format.format(value);

        data[indexMap.get(identifier) + 1] = TAB + identifier + ": " + valueString;
    }

    /**
     * Identifies a decimal format to be used when printing a label's numeric value.
     *
     * @param identifier identifying name
     * @param formatter  decimal format
     */
    public void addDecimalFormat(String identifier, DecimalFormat formatter) {
        formatMap.put(identifier, formatter);
    }

    /**
     * Resizes the data array to have an additional empty index at the end.
     */
    private void resize() {
        String[] temp = new String[data.length + 1];

        for (int i = 0; i < data.length; i++)
            temp[i] = data[i];

        data = temp;
    }
}
