package onyx.ui;

import java.awt.*;
import java.util.HashMap;

/**
 * A collection of colors with associated tags used for coloring a telemetry display.
 */
public class Theme {
    public static final Theme DARK = new Theme(new Object[]{
            "bg", Color.BLACK,
            "text", Color.WHITE
    });

    private static final Color DEFAULT_COLOR = Color.BLACK;

    private HashMap<String, Color> colors = new HashMap<>();

    private Theme(Object[] pairs) {
        for (int i = 0; i < pairs.length; i += 2)
            addColor((String)pairs[i], (Color)pairs[i + 1]);
    }

    public Theme(Color bgColor, Color textColor) {
        addColor("bg", bgColor);
        addColor("text", textColor);
    }

    public void addColor(String name, Color col) {
        colors.put(name, col);
    }

    public Color get(String name) {
        Color col = colors.get(name);
        return col == null ? DEFAULT_COLOR : col;
    }
}
