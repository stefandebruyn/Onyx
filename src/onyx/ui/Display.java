package onyx.ui;

import onyx.telemetry.ImageTelemetry;
import onyx.telemetry.Telemetry;
import onyx.telemetry.TextTelemetry;
import onyx.util.ColorLibrary;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.*;

/**
 * A graphics drawing panel used for telemetry interfaces. Displays use a common font family and size, but support
 * dynamic color and weight changes mid-line.
 * <p>
 * An overarching {@link Theme} acts as a color palette. Text color and weight can still be forcibly changed mid-line
 * with the '!' and '#' characters, respectively, where the following character is a formatting metacharacter indicating
 * the change to be made. See {@link #COLOR_CODES} and {@link #WEIGHT_CODES} for valid metacharacters.
 * <p>
 * The display's refresh rate defaults to 60 Hz. This can be updated via {@link #setRefreshRate(int)}.
 */
public class Display extends JPanel {
    /**
     * Characters that, when preceded by '!' in coded text, trigger a color change. Note that color codes with more than
     * one character must be enclosed in curly braces (e.g. "!{mv}").
     */
    public static final HashMap<String, Color> COLOR_CODES = new HashMap<>() {{
        put("r", Color.RED);
        put("g", Color.GREEN);
        put("b", Color.BLUE);
        put("w", Color.WHITE);
        put("o", Color.ORANGE);
        put("pr", ColorLibrary.PALE_RED);
        put("lg", ColorLibrary.LIGHT_GRAY);
        put("lb", ColorLibrary.LIGHT_BLUE);
        put("c", ColorLibrary.CYAN);
        put("ly", ColorLibrary.LIGHT_YELLOW);
        put("a", ColorLibrary.AQUAMARINE);
        put("pe", ColorLibrary.PEACH);
        put("mr", ColorLibrary.MAROON);
        put("gg", ColorLibrary.GREEN_GRASS);
        put("db", ColorLibrary.DEEP_BLUE);
        put("mv", ColorLibrary.MAUVE);
    }};
    /**
     * Characters that, when preceded by '#' in coded text, trigger a weight change.
     */
    public static final HashMap<String, Integer> WEIGHT_CODES = new HashMap<>() {{
        put("p", Font.PLAIN);
        put("b", Font.BOLD);
        put("i", Font.ITALIC);
    }};

    private static final char BEGIN_COLOR_CODE = '!';
    private static final char BEGIN_WEIGHT_CODE = '#';

    private HashMap<String, Telemetry> telemetryMap = new HashMap<>();
    private BufferedImage telemetryImage;
    private Graphics2D targetSurface;
    private Font font;
    private FontMetrics fontMetrics;
    private Theme theme;
    private Color bgColor, textColor;

    private String fontName;
    private int fontSize, lineHeight, fps = 60;

    /**
     * Initializes an empty display.
     *
     * @param width    width in pixels
     * @param height   height in pixels
     * @param fontSize font size
     * @param fontName font name
     * @param theme    display theme
     */
    public Display(int width, int height, int fontSize, String fontName, Theme theme) {
        setPreferredSize(new Dimension(width, height));

        this.fontSize = fontSize;
        this.fontName = fontName;
        this.theme = theme;

        font = new Font(fontName, Font.PLAIN, fontSize);
        fontMetrics = getFontMetrics(font);
        lineHeight = fontMetrics.getHeight();

        bgColor = theme.getColor("bg");
        textColor = theme.getColor("text");
    }

    /**
     * Sets the refresh rate of the display.
     *
     * @param fps frequency (Hz)
     */
    public void setRefreshRate(int fps) {
        this.fps = fps;
    }

    /**
     * Renders a single frame to the display. After the rendering is complete, the thread will sleep for 1000 /{@link #fps}
     * milliseconds to smooth the interaction between the display and update threads (the thread placing calls to
     * update). Ideally, calls to this method are placed in a loop running at or above the refresh rate for the duration
     * of the program.
     *
     * @see {@link #setRefreshRate(int)}
     */
    public void update() {
        repaint();

        try {
            Thread.sleep(1000 / fps);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Renders all telemetry to the target graphics surface.
     */
    private void renderTelemetry() {
        // Create a clean drawing surface
        telemetryImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        targetSurface = telemetryImage.createGraphics();
        targetSurface.setFont(font);

        // Draw all telemetry modules
        for (Telemetry t : telemetryMap.values()) {
            // TextTelemetry instances
            if (t instanceof TextTelemetry) {
                TextTelemetry tel = (TextTelemetry) t;
                String[] data = tel.data();
                boolean coded = tel.coded();

                // Draw each line separately
                for (int i = 0; i < data.length; i++) {
                    int x = tel.x();
                    int y = tel.y() + i * lineHeight;

                    // Only process metacharacters if necessary
                    if (coded)
                        drawTextFormatted(x, y, data[i]);
                    else
                        targetSurface.drawString(data[i], x, y);
                }
            } else if (t instanceof ImageTelemetry) {
                ImageTelemetry tel = (ImageTelemetry) t;
                targetSurface.drawImage(tel.render(), tel.x(), tel.y(), null);
            }
        }

        targetSurface.dispose();
    }

    /**
     * Adds a new piece of telemetry to the display.
     *
     * @param identifier identifying name
     * @param tel        telemetry instance
     */
    public void addTelemetry(String identifier, Telemetry tel) {
        telemetryMap.put(identifier, tel);
    }

    /**
     * Gets a telemetry instance by name.
     *
     * @param identifier identifying name
     * @return corresponding telemetry object, or null if not found
     */
    public Telemetry getTelemetry(String identifier) {
        return telemetryMap.get(identifier);
    }

    /**
     * Adds a new color code, or overwrites a native one.
     *
     * @param code format code to follow '!'
     * @param col  color
     */
    public void addColorCode(String code, Color col) {
        COLOR_CODES.put(code, col);
    }

    /**
     * Renders a single frame of telemetry and draws it to a graphics surface.
     *
     * @param g drawing surface
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(bgColor);
        renderTelemetry();
        g.drawImage(telemetryImage, 0, 0, this);
    }

    /**
     * Text drawing with support for format metacharacters.
     *
     * @param x   horizontal position from surface left
     * @param y   vertical position from surface top
     * @param str text to draw
     */
    private void drawTextFormatted(int x, int y, String str) {
        if (str == null)
            return;

        targetSurface.setColor(textColor);
        targetSurface.setFont(new Font(fontName, Font.PLAIN, fontSize));

        int horizOffset = 0;
        int index = 0;

        // Each character is drawn separately
        while (index < str.length()) {
            char c = str.charAt(index);

            // Color code was found
            if (c == BEGIN_COLOR_CODE && index != str.length() - 1) {
                char nextChar = str.charAt(index + 1);
                Color col = COLOR_CODES.get("" + nextChar);

                // Single-character code
                if (col != null) {
                    targetSurface.setColor(col);
                    index += 2;
                    continue;

                    // Multi-character code enclosed in brackets
                } else if (nextChar == '{') {
                    // Find index of closing bracket
                    int closeIndex = -1;
                    for (int i = index + 2; i < str.length() && closeIndex == -1; i++)
                        if (str.charAt(i) == '}')
                            closeIndex = i;

                    // Look up next color
                    if (closeIndex != -1) {
                        String code = str.substring(index + 2, closeIndex);
                        col = COLOR_CODES.get(code);

                        if (col != null) {
                            targetSurface.setColor(col);
                            index += 3 + code.length();
                            continue;
                        }
                    }
                }

                // Weight code was found
            } else if (c == BEGIN_WEIGHT_CODE) {
                Integer weight = WEIGHT_CODES.get("" + str.charAt(index + 1));

                if (weight != null) {
                    targetSurface.setFont(new Font(fontName, weight, fontSize));
                    index += 2;
                    continue;
                }
            }

            targetSurface.drawString("" + c, x + horizOffset, y + lineHeight);

            horizOffset += fontMetrics.stringWidth("" + c);
            index++;
        }
    }

    /**
     * Gets the {@link FontMetrics} used for text dimension calculations.
     *
     * @return font metrics
     */
    public FontMetrics fontMetrics() {
        return fontMetrics;
    }

    /**
     * Gets the font used for drawing.
     *
     * @return display font
     */
    public Font font() {
        return font;
    }

    /**
     * Gets the height in pixels of a single line of text.
     *
     * @return line height in pixels
     */
    public int lineHeight() {
        return lineHeight;
    }

    /**
     * Updates the Theme used for coloring.
     *
     * @param theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * Launches this display as the content frame of a {@link JPanel}.
     *
     * @param frameName window name
     * @return frame
     */
    public JFrame launch(String frameName) {
        JFrame frame = new JFrame(frameName);
        frame.setContentPane(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }
}
