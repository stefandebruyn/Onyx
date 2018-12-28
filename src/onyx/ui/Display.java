package onyx.ui;

import onyx.telemetry.Telemetry;
import onyx.telemetry.TextTelemetry;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JPanel;

/**
 * Low-level graphical drawing panel used for telemetry interfaces.
 */
public class Display extends JPanel {
    public static final HashMap<String, Color> COLOR_CODES = new HashMap<>() {{
        put("r", Color.RED);
        put("g", Color.GREEN);
        put("b", Color.BLUE);
        put("w", Color.WHITE);
        put("o", Color.ORANGE);
    }};
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
    private int fontSize, lineHeight;

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

        bgColor = theme.get("bg");
        textColor = theme.get("text");
    }

    /**
     * Renders telemetry currently in the queue.
     */
    public void renderTelemetry() {
        // Create a clean drawing surface
        telemetryImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        targetSurface = telemetryImage.createGraphics();
        targetSurface.setFont(font);

        // Draw all telemetry modules
        for (Telemetry t : telemetryMap.values()) {
            // TextTelemetry instances
            if (t instanceof TextTelemetry) {
                TextTelemetry tel = (TextTelemetry)t;
                String[] data = tel.data();
                boolean coded = tel.coded();

                // Draw each line separately
                for (int i = 0; i < data.length; i++) {
                    int x = tel.x();
                    int y = tel.y() + i * lineHeight;

                    // Only process metacharacters if necessary
                    if (coded)
                        drawTextCoded(x, y, data[i]);
                    else
                        targetSurface.drawString(data[i], x, y);
                }
            }
        }

        targetSurface.dispose();
    }

    /**
     * Adds a new piece of telemetry to the display.
     *
     * @param identifier identifying name
     * @param tel telemetry instance
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
     * Drawing loop.
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
     * Text drawing with support for color and weight coding.
     *
     * @param x   horizontal position from surface left
     * @param y   vertical position from surface top
     * @param str text to draw
     */
    private void drawTextCoded(int x, int y, String str) {
        targetSurface.setColor(textColor);
        targetSurface.setFont(new Font(fontName, Font.PLAIN, fontSize));

        int horizOffset = 0;
        int index = 0;

        // Each character is drawn separately
        while (index < str.length()) {
            char c = str.charAt(index);

            // Color code was found
            if (c == BEGIN_COLOR_CODE) {
                targetSurface.setColor(COLOR_CODES.get("" + str.charAt(index + 1)));
                index += 2;
                continue;

            // Font weight code was found
            } else if (c == BEGIN_WEIGHT_CODE) {
                targetSurface.setFont(new Font(fontName, WEIGHT_CODES.get("" + str.charAt(index + 1)), fontSize));
                index += 2;
                continue;
            }

            targetSurface.drawString("" + c, x + horizOffset, y + lineHeight);

            horizOffset += fontMetrics.stringWidth("" + c);
            index++;
        }
    }

    public FontMetrics fontMetrics() {
        return fontMetrics;
    }
}
