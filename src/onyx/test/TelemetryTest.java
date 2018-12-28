package onyx.test;

import onyx.telemetry.ParagraphTelemetry;
import onyx.telemetry.Telemetry;
import onyx.ui.Display;
import onyx.ui.OnyxInterface;
import onyx.ui.Theme;

import java.awt.*;

public class TelemetryTest {
    public static void main(String[] args) {
        OnyxInterface inter = new OnyxInterface(
                new Display(640, 480, 10, "default", Theme.DARK));
        FontMetrics fm = inter.display().fontMetrics();
        inter.display().addTelemetry("time", new ParagraphTelemetry(10, 10, true, fm, "#bTime", ""));

        while (true) {
            ParagraphTelemetry t = (ParagraphTelemetry)inter.display().getTelemetry("time");
            t.setLine(0, "!g" + (System.currentTimeMillis() / 1000.0));
            inter.display().repaint();
            try { Thread.sleep(25); } catch (InterruptedException e) {}
        }
    }
}
