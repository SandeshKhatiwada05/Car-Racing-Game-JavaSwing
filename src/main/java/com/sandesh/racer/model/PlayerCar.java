package com.sandesh.racer.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class PlayerCar {
    private double x;
    private final double y;
    private final int width;
    private final int height;
    private final double speed;
    private final int minX;
    private final int maxX;

    private final Color bodyColor = new Color(100, 220, 255);
    private final Color trimColor = new Color(0, 255, 200);

    // Animation
    private double pulseMs = 0;    // for subtle light pulsing
    private double wheelPhase = 0; // fake "rotation" accent

    public PlayerCar(double x, double y, int width, int height, double speed, int minX, int maxX) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.minX = minX + 8;
        this.maxX = maxX - width - 8;
    }

    public void tick(double dtMillis) {
        pulseMs += dtMillis;
        if (pulseMs > 10_000) pulseMs = 0;
        // Tie wheel phase loosely to time; this is just for a subtle animated accent
        wheelPhase += dtMillis * 0.08;
        if (wheelPhase > 10_000) wheelPhase = 0;
    }

    public void moveLeft() {
        x -= speed;
        if (x < minX) x = minX;
    }

    public void moveRight() {
        x += speed;
        if (x > maxX) x = maxX;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), width, height);
    }

    public void draw(Graphics2D g2) {
        int ix = (int) Math.round(x);
        int iy = (int) Math.round(y);

        // Soft drop shadow under the car
        drawShadow(g2, ix, iy);

        // Neon underglow
        drawUnderGlow(g2, ix, iy);

        // Car body
        RoundRectangle2D body = new RoundRectangle2D.Double(ix, iy, width, height, 18, 18);
        Paint bodyPaint = new GradientPaint(ix, iy, bodyColor.darker(),
                ix, iy + height, bodyColor.brighter());
        g2.setPaint(bodyPaint);
        g2.fill(body);

        // Body border glow
        g2.setColor(new Color(0, 255, 220, 110));
        g2.setStroke(new BasicStroke(2.2f));
        g2.draw(body);

        // Wheels
        drawWheel(g2, ix + 8, iy + 14, 12, 20);
        drawWheel(g2, ix + width - 20, iy + 14, 12, 20);
        drawWheel(g2, ix + 8, iy + height - 34, 12, 20);
        drawWheel(g2, ix + width - 20, iy + height - 34, 12, 20);

        // Center trim stripe
        g2.setColor(trimColor);
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(ix + width / 2, iy + 8, ix + width / 2, iy + height - 8);

        // Side neon accents
        g2.setColor(new Color(0, 255, 200, 160));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(ix + 6, iy + 10, ix + 6, iy + height - 10);
        g2.drawLine(ix + width - 6, iy + 10, ix + width - 6, iy + height - 10);

        // Windows (single glossy canopy look)
        Shape canopy = new RoundRectangle2D.Double(ix + 8, iy + 12, width - 16, height - 24, 14, 14);
        Paint glass = new GradientPaint(ix, iy, new Color(20, 30, 40, 180),
                ix, iy + height, new Color(60, 90, 120, 120));
        g2.setPaint(glass);
        g2.fill(canopy);

        // Gloss highlight
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillRoundRect(ix + 12, iy + 16, width - 24, 10, 10, 10);

        // Headlights + subtle cones with pulse
        double pulse = 0.6 + 0.4 * Math.sin((pulseMs / 1200.0) * Math.PI * 2);
        drawHeadlights(g2, ix, iy, (float) pulse);
    }

    private void drawShadow(Graphics2D g2, int ix, int iy) {
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.SrcOver.derive(0.35f));
        g2.setColor(new Color(0, 0, 0));
        int sw = (int) (width * 0.95);
        int sh = (int) (height * 0.32);
        g2.fillOval(ix + (width - sw) / 2, iy + height - sh / 2, sw, sh);
        g2.setComposite(old);
    }

    private void drawUnderGlow(Graphics2D g2, int ix, int iy) {
        // Radial glow centered near the rear axle
        int gx = ix + width / 2;
        int gy = iy + (int) (height * 0.7);
        float radius = Math.max(width, height) * 0.65f;
        Color c0 = new Color(0, 255, 220, 110);
        Color c1 = new Color(0, 255, 220, 0);
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point2D.Float(gx, gy),
                radius,
                new float[]{0f, 1f},
                new Color[]{c0, c1}
        );
        Paint old = g2.getPaint();
        g2.setPaint(rgp);
        g2.fillOval((int) (gx - radius), (int) (gy - radius), (int) (radius * 2), (int) (radius * 2));
        g2.setPaint(old);
    }

    private void drawWheel(Graphics2D g2, int x, int y, int w, int h) {
        // Tire
        g2.setColor(new Color(18, 18, 22));
        g2.fillRoundRect(x, y, w, h, 8, 8);

        // Rim
        g2.setColor(new Color(140, 160, 180));
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 6, 6);

        // Simple "rotating" spokes hint (animated brightness)
        float a = (float) (120 + 60 * Math.sin((wheelPhase + x + y) * 0.12));
        g2.setColor(new Color(200, 220, 255, Math.min(255, Math.max(0, (int) a))));
        g2.drawLine(x + w / 2, y + 3, x + w / 2, y + h - 3);
    }

    private void drawHeadlights(Graphics2D g2, int ix, int iy, float pulse) {
        // Light sources
        g2.setColor(new Color(255, 255, 200, (int) (180 * pulse)));
        g2.fillOval(ix + 6, iy + 2, 12, 6);
        g2.fillOval(ix + width - 18, iy + 2, 12, 6);

        // Cones (triangular glow forward)
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.SrcOver.derive(0.14f * pulse));
        g2.setColor(new Color(255, 255, 200));
        int coneLen = 80;
        Polygon leftCone = new Polygon(
                new int[]{ix + 10, ix + 2, ix + width / 2 - 4},
                new int[]{iy + 2, iy - coneLen, iy - coneLen},
                3
        );
        Polygon rightCone = new Polygon(
                new int[]{ix + width - 10, ix + width - 2, ix + width / 2 + 4},
                new int[]{iy + 2, iy - coneLen, iy - coneLen},
                3
        );
        g2.fill(leftCone);
        g2.fill(rightCone);
        g2.setComposite(old);
    }

    public int getY() { return (int) Math.round(y); }
    public int getHeight() { return height; }
}