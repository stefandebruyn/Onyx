package onyx.telemetry;

import onyx.graphics.Theme;
import onyx.util.Utilities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A static, unmoving, 2D graph of telemetry.
 */
public class GraphTelemetry extends ImageTelemetry {
    private static final int AXIS_LABEL_GUTTER = 4;
    public static final int PLOT_MODE_SCATTER = 0;
    public static final int PLOT_MODE_CONNECT = 1;

    protected ArrayList<Coordinate> points = new ArrayList<>();
    protected BufferedImage dataImage, composite;
    protected DecimalFormat xFormat, yFormat;
    protected Theme theme;
    protected FontMetrics fontMetrics;
    protected Font font;
    protected String xAxisLabel, yAxisLabel;
    protected double xAxisLower, xAxisUpper, yAxisLower, yAxisUpper, xAxisInterval, yAxisInterval;
    protected boolean upToDate = true;
    protected int lineHeight, xAxisLabelWidth, plotMode = PLOT_MODE_SCATTER;

    /**
     * Parameter composition for clearer construction.
     */
    public static class Parameters {
        public DecimalFormat xFormat, yFormat;
        public FontMetrics fontMetrics;
        public Font font;
        public String xAxisLabel, yAxisLabel;
        public Theme theme;
        public double xAxisLower, xAxisUpper, yAxisLower, yAxisUpper, xAxisInterval, yAxisInterval;
        public int x, y, width, height;
        public boolean coded;
    }

    /**
     * Represents a single point of data on the graph.
     */
    public static class Coordinate {
        public double x, y;

        public Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Creates a new graph with nothing plotted.
     *
     * @param p graph parameters
     */
    public GraphTelemetry(Parameters p) {
        super(p.x, p.y, p.coded, p.width, p.height);
        x = p.x;
        y = p.y;
        coded = p.coded;
        width = p.width;
        height = p.height;
        xFormat = p.xFormat;
        yFormat = p.yFormat;
        fontMetrics = p.fontMetrics;
        font = p.font;
        xAxisLower = p.xAxisLower;
        xAxisUpper = p.xAxisUpper;
        xAxisInterval = p.xAxisInterval;
        xAxisLabel = p.xAxisLabel;
        yAxisLower = p.yAxisLower;
        yAxisUpper = p.yAxisUpper;
        yAxisInterval = p.yAxisInterval;
        yAxisLabel = p.yAxisLabel;
        lineHeight = fontMetrics.getHeight();
        theme = p.theme;
        xAxisLabelWidth = fontMetrics.stringWidth(xAxisLabel);
        renderAxes();
    }

    /**
     * Draws the base graph image, including axes and labels.
     */
    private void renderAxes() {
        image = new BufferedImage(width + xAxisLabelWidth + AXIS_LABEL_GUTTER,
                height + lineHeight + AXIS_LABEL_GUTTER, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setFont(font);

        // Axis labels
        g.setColor(theme.getColor("graph_axis_label"));
        g.drawString(yAxisLabel, 0, lineHeight + 1 - AXIS_LABEL_GUTTER);
        g.drawString(xAxisLabel, width + AXIS_LABEL_GUTTER, image.getHeight());

        // Axes
        g.setColor(theme.getColor("graph_axis"));
        g.drawLine(0, lineHeight + 1, 0, image.getHeight());
        g.drawLine(0, image.getHeight() - 1, width - 1, image.getHeight() - 1);

        // Intervals
        final int TICK_LENGTH = 5;
        final int X_AXIS_VERTICAL_GUTTER = 4;
        final int X_AXIS_HORIZONTAL_GUTTER = 1;
        final int Y_AXIS_VERTICAL_GUTTER = 1;
        final int Y_AXIS_HORIZONTAL_GUTTER = 2;

        // x-axis intervals
        for (double x = xAxisLower; x <= xAxisUpper; x += xAxisInterval) {
            // Ignore ticks superposed with the x-axis
            if (x == 0)
                continue;

            String lab = xFormat.format(x);
            double perc = x / (xAxisUpper - xAxisLower);
            int xPos = (int) (perc * width) - 1;
            g.drawLine(xPos, image.getHeight() - TICK_LENGTH - 1, xPos, image.getHeight() - 1);

            g.drawString(lab,
                    xPos - fontMetrics.stringWidth(lab) + X_AXIS_HORIZONTAL_GUTTER,
                    image.getHeight() - (TICK_LENGTH + lineHeight / 2) + X_AXIS_VERTICAL_GUTTER);
        }

        // y-axis intervals
        for (double y = yAxisLower; y <= yAxisUpper; y += yAxisInterval) {
            // Ignore ticks superposed with the y-axis
            if (y == 0)
                continue;

            String lab = yFormat.format(y);
            double perc = y / (yAxisUpper - yAxisLower);
            int yPos = height - (int) (perc * height) + lineHeight + 1;
            g.drawLine(0, yPos, TICK_LENGTH, yPos);

            g.drawString(lab,
                    TICK_LENGTH + Y_AXIS_HORIZONTAL_GUTTER,
                    yPos + lineHeight / 2 + Y_AXIS_VERTICAL_GUTTER);
        }

        g.dispose();
    }

    /**
     * Plots a point on the graph.
     *
     * @param x     horizontal axis position
     * @param y     vertical axis position
     * @param clean whether or not to wipe the previous points
     */
    public void addPoint(double x, double y, boolean clean) {
        points.add(new Coordinate(x, y));

        if (clean || dataImage == null)
            dataImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = dataImage.createGraphics();
        g.setColor(theme.getColor("graph_data"));
        // pixel:unit ratios
        double xRatio = width / (xAxisUpper - xAxisLower);
        double yRatio = height / (yAxisUpper - yAxisLower);

        // Draw the initial point
        Coordinate firstPoint = points.get(0);
        int xLast = (int) ((firstPoint.x - xAxisLower) * xRatio);
        int yLast = height - (int) ((firstPoint.y - yAxisLower) * yRatio) + lineHeight + AXIS_LABEL_GUTTER;
        g.drawLine(xLast, yLast, xLast, yLast);

        // Draw all subsequent points
        for (int i = 1; i < points.size(); i++) {
            Coordinate point = points.get(i);
            int xPos = (int) ((point.x - xAxisLower) * xRatio);
            int yPos = height - (int) ((point.y - yAxisLower) * yRatio) + lineHeight + AXIS_LABEL_GUTTER;
            // Connect if specified by the plot mode
            g.drawLine(xPos, yPos,
                    plotMode == PLOT_MODE_CONNECT ? xLast : xPos,
                    plotMode == PLOT_MODE_CONNECT ? yLast : yPos);
            xLast = xPos;
            yLast = yPos;
        }

        g.dispose();
    }

    /**
     * Renders and returns an image of the graph.
     *
     * @return image of graph
     */
    public BufferedImage render() {
        if (upToDate && composite != null)
            return composite;

        // If no data has been provided yet, return the base image
        if (dataImage == null)
            return image;

        // Clone the data image and composite the base (axes) image on top
        composite = Utilities.cloneImage(dataImage);
        Graphics2D g = composite.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return composite;
    }

    /**
     * Sets the mode for point plotting. {@link #PLOT_MODE_CONNECT} will connect subsequent points, and
     * {@link #PLOT_MODE_SCATTER} will leave them disconnected.
     *
     * @param mode plot mode
     */
    public void setPlotMode(int mode) {
        plotMode = mode;
    }
}
