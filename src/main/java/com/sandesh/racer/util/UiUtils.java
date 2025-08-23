package com.sandesh.racer.util;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UiUtils {

    public static void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public static void drawPanel(Graphics2D g2, int x, int y, int w, int h, Color bg, Color glow) {
        // Shadow
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(x + 3, y + 4, w, h, 18, 18);

        // Panel
        RoundRectangle2D rr = new RoundRectangle2D.Double(x, y, w, h, 18, 18);
        g2.setColor(bg);
        g2.fill(rr);

        // Glow border
        g2.setColor(glow);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(rr);
    }
}