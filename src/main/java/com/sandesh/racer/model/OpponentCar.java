package com.sandesh.racer.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class OpponentCar {
    private static final Random RNG = new Random();

    private double x;
    private double y;
    private final int width;
    private final int height;
    private double speed;
    private boolean countedPass = false;

    private final Color body;
    private final Color trim;

    public OpponentCar(double x, double y, int width, int height, double speed) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        this.speed = speed;

        // Neon palette
        Color[] palettes = {
                new Color(255, 110, 150), // pink
                new Color(255, 180, 60),  // orange
                new Color(160, 255, 120), // lime
                new Color(120, 180, 255), // blue
                new Color(255, 120, 220)  // magenta
        };
        body = palettes[RNG.nextInt(palettes.length)];
        trim = new Color(255, 255, 255, 180);
    }

    public void update(double dtMillis, double globalSpeed) {
        y += (speed + globalSpeed) * (dtMillis / 16.0);
    }

    public void draw(Graphics2D g2) {
        int ix = (int) Math.round(x);
        int iy = (int) Math.round(y);

        // Soft shadow
        Composite oldC = g2.getComposite();
        g2.setComposite(AlphaComposite.SrcOver.derive(0.30f));
        g2.setColor(Color.BLACK);
        int sw = (int) (width * 0.92);
        int sh = (int) (height * 0.30);
        g2.fillOval(ix + (width - sw) / 2, iy + height - sh / 2, sw, sh);
        g2.setComposite(oldC);

        // Taillight glow (rear)
        drawTailGlow(g2, ix, iy);

        // Body
        RoundRectangle2D bodyShape = new RoundRectangle2D.Double(ix, iy, width, height, 18, 18);
        g2.setPaint(new GradientPaint(ix, iy, body.darker(), ix, iy + height, body.brighter()));
        g2.fill(bodyShape);

        // Trim line
        g2.setColor(trim);
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(ix + width / 2, iy + 8, ix + width / 2, iy + height - 8);

        // Rear lights
        g2.setColor(new Color(255, 90, 90, 200));
        g2.fillOval(ix + 6, iy + height - 10, 12, 6);
        g2.fillOval(ix + width - 18, iy + height - 10, 12, 6);

        // Side accents (dim)
        g2.setColor(new Color(body.getRed(), body.getGreen(), body.getBlue(), 120));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(ix + 6, iy + 10, ix + 6, iy + height - 10);
        g2.drawLine(ix + width - 6, iy + 10, ix + width - 6, iy + height - 10);
    }

    private void drawTailGlow(Graphics2D g2, int ix, int iy) {
        int gx = ix + width / 2;
        int gy = iy + (int) (height * 0.9);
        float radius = Math.max(18, width * 0.9f);
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point2D.Float(gx, gy),
                radius,
                new float[]{0f, 1f},
                new Color[]{new Color(255, 60, 60, 80), new Color(255, 60, 60, 0)}
        );
        Paint old = g2.getPaint();
        g2.setPaint(rgp);
        g2.fillOval((int) (gx - radius), (int) (gy - radius), (int) (radius * 2), (int) (radius * 2));
        g2.setPaint(old);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), width, height);
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight + 50;
    }

    public void setPosition(double newX, double newY) {
        this.x = newX; this.y = newY;
        this.countedPass = false;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean hasBeenCounted() { return countedPass; }
    public void markCounted() { this.countedPass = true; }

    public double getY() { return y; }
    public int getHeight() { return height; }
}