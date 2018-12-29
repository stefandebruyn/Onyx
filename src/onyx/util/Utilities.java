package onyx.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * General-use utility functions.
 */
public class Utilities {

    private Utilities() {
    }

    /**
     * Clones a {@link BufferedImage}.
     *
     * @param source source image
     * @return clone of source image
     */
    public static BufferedImage cloneImage(BufferedImage source) {
        BufferedImage clone = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = clone.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return clone;
    }
}
