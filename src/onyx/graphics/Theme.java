package onyx.graphics;

import onyx.util.ColorLibrary;

import java.awt.*;
import java.util.HashMap;

/**
 * A color palette dictating the appearance of a {@link Display}. Colors are fetched by name and used in the rendering
 * of various {@link onyx.telemetry.Telemetry} objects. Custom Telemetry objects may make use of custom colors, which
 * can be added to the Theme with {@link #addColor(String, Color)}.
 */
public class Theme {
    public static final Theme DARK = new Theme(new Object[]{
            "bg", Color.BLACK,
            "text", Color.WHITE,
            "graph_axis", Color.GRAY,
            "graph_axis_label", Color.WHITE,
            "graph_data", ColorLibrary.PALE_RED
    });

    /**
     * Default return value for {@link #getColor(String)} if the specified identifier was not found.
     */
    public static final Color DEFAULT_COLOR = Color.BLACK;

    private HashMap<String, Color> colors = new HashMap<>();

    /**
     * One-call construction and color population for static constants initialization.
     *
     * @param pairs an array of even length; index n holds a string identifier, index n+1 holds the mapped color
     */
    private Theme(Object[] pairs) {
        for (int i = 0; i < pairs.length; i += 2)
            addColor((String) pairs[i], (Color) pairs[i + 1]);
    }

    /**
     * Creates a basic theme with a background and foreground color.
     *
     * @param bgColor   background color
     * @param textColor foreground (default text) color
     */
    public Theme(Color bgColor, Color textColor) {
        addColor("bg", bgColor);
        addColor("text", textColor);
    }

    /**
     * Maps a new color to an identifying string (presumably for use by {@link onyx.telemetry.Telemetry} objects).
     *
     * @param name identifying name
     * @param col  color
     */
    public void addColor(String name, Color col) {
        colors.put(name, col);
    }

    /**
     * Gets the color mapped to an identifying string.
     *
     * @param name identifying string
     * @return corresponding color, or {@link #DEFAULT_COLOR} if not found
     */
    public Color getColor(String name) {
        Color col = colors.get(name);
        return col == null ? DEFAULT_COLOR : col;
    }
}
