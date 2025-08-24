package com.sandesh.racer.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class WelcomePanel extends JPanel {

    private final JTextField nameField = new PromptTextField("Enter your name…", 18);
    private final JButton startBtn = new NeonButton("Start Game");
    private final JButton quitBtn = new NeonButton("Quit");

    // Theme
    private static final Color BG_TOP = new Color(16, 20, 36);
    private static final Color BG_BOTTOM = new Color(6, 8, 16);
    private static final Color ACCENT = new Color(0, 255, 200);
    private static final Color ACCENT_DIM = new Color(0, 255, 200, 120);
    private static final Color TITLE_COLOR = new Color(120, 200, 255);
    private static final Color SUBTITLE_COLOR = new Color(200, 220, 240);

    private final Timer animTimer;
    private long lastNanos = System.nanoTime();
    private double tMillis = 0; // animation clock

    public WelcomePanel(Consumer<String> onStart) {
        setPreferredSize(new Dimension(GamePanel.WIDTH + 40, GamePanel.HEIGHT + 40));
        setLayout(new GridBagLayout());
        setOpaque(true);
        setBackground(BG_BOTTOM);

        // Title labels (we paint a custom neon title, but include accessible labels for screen readers)
        JLabel hiddenTitle = new JLabel("Neon Rush");
        hiddenTitle.setFont(hiddenTitle.getFont().deriveFont(Font.BOLD, 0.1f));
        hiddenTitle.setForeground(new Color(0,0,0,0)); // invisible, for a11y/metrics only

//        JLabel subtitle = new JLabel("Top‑View Car Racing");
//        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 18f));
//        subtitle.setForeground(SUBTITLE_COLOR);

        // Form card
        JPanel formCard = new GlassCard();
        formCard.setOpaque(false);
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints fc = new GridBagConstraints();
        fc.insets = new Insets(8, 8, 8, 8);

        JLabel nameLabel = new JLabel("Player name");
        nameLabel.setForeground(new Color(230, 245, 255));
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 14f));

        nameField.setColumns(16);
        nameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        nameField.setForeground(new Color(230, 245, 255));
        nameField.setCaretColor(new Color(220, 255, 250));
        nameField.setBorder(new EmptyBorder(8, 12, 8, 12));

        // Hook Enter key to start
        nameField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "start");
        nameField.getActionMap().put("start", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { start(onStart); }
        });

        ((NeonButton) startBtn).setAccent(ACCENT);
        startBtn.addActionListener(e -> start(onStart));
        ((NeonButton) quitBtn).setAccent(new Color(255, 110, 150));
        quitBtn.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(WelcomePanel.this);
            if (w != null) w.dispose();
            System.exit(0);
        });

        fc.gridx = 0; fc.gridy = 0; fc.anchor = GridBagConstraints.LINE_START;
        formCard.add(nameLabel, fc);
        fc.gridx = 0; fc.gridy = 1; fc.gridwidth = 2; fc.fill = GridBagConstraints.HORIZONTAL;
        formCard.add(nameField, fc);
        fc.gridy = 2; fc.gridwidth = 1; fc.fill = GridBagConstraints.NONE; fc.insets = new Insets(14, 8, 8, 8);
        formCard.add(startBtn, fc);
        fc.gridx = 1;
        formCard.add(quitBtn, fc);

        // Layout welcome screen
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
        add(hiddenTitle, c); // invisible but keeps vertical spacing predictable
//        c.gridy = 1;
//        add(subtitle, c);
        c.gridy = 2;
        add(formCard, c);

        // Animate
        animTimer = new Timer(16, e -> {
            long now = System.nanoTime();
            tMillis += Math.min(100, (now - lastNanos) / 1_000_000.0);
            lastNanos = now;
            repaint();
        });
        animTimer.start();

        SwingUtilities.invokeLater(() -> nameField.requestFocusInWindow());
    }

    private void start(Consumer<String> onStart) {
        String playerName = nameField.getText().trim();
        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name to start.", "Name required",
                    JOptionPane.WARNING_MESSAGE);
            nameField.requestFocusInWindow();
            return;
        }
        onStart.accept(playerName);
    }

    public void setPlayerName(String name) {
        nameField.setText(name);
        SwingUtilities.invokeLater(() -> nameField.requestFocusInWindow());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Primary animated background
        Graphics2D g2 = (Graphics2D) g.create();
        enableQuality(g2);

        int w = getWidth(), h = getHeight();

        // Vertical gradient base
        GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOTTOM);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // Vignette with two radial glows (teal left, purple right)
        paintRadialGlow(g2, new Point((int)(w*0.25), (int)(h*0.3)), Math.max(w,h)*0.7f,
                new Color(0, 255, 200, 40), new Color(0, 255, 200, 0));
        paintRadialGlow(g2, new Point((int)(w*0.85), (int)(h*0.15)), Math.max(w,h)*0.6f,
                new Color(180, 120, 255, 50), new Color(180, 120, 255, 0));

        // Animated diagonal streaks
        paintStreaks(g2, w, h);

        // Neon title (custom painted, centered near top)
        paintNeonTitle(g2, w, h);

        g2.dispose();
    }

    private void paintNeonTitle(Graphics2D g2, int w, int h) {
        String title = "Neon Rush";
        Font font = getFont().deriveFont(Font.BOLD, 56f);
        g2.setFont(font);

        GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), title);
        Shape outline = gv.getOutline();
        Rectangle bounds = outline.getBounds();

        int tx = (w - bounds.width) / 2 - bounds.x;
        int ty = (int) (h * 0.18) - bounds.y;

        // Position outline
        g2.translate(tx, ty);

        // Pulse for glow
        double pulse = 0.6 + 0.4 * Math.sin((tMillis / 1200.0) * Math.PI * 2);

        // Outer glow strokes (fake blur with multiple strokes)
        for (int i = 10; i >= 3; i -= 2) {
            float alpha = (float) (0.020 * pulse * (i / 10.0));
            g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), (int) (alpha * 255)));
            g2.setStroke(new BasicStroke(i, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(outline);
        }

        // Fill gradient
        Paint p = new GradientPaint(0, 0, TITLE_COLOR, 0, bounds.height, new Color(255, 160, 210));
        g2.setPaint(p);
        g2.fill(outline);

        // Sharp inner stroke
        g2.setColor(new Color(0, 0, 0, 120));
        g2.setStroke(new BasicStroke(1.8f));
        g2.draw(outline);

        // Subtitle under title (painted for better centering)
        String sub = "Top‑View Car Racing";
        Font sf = getFont().deriveFont(Font.PLAIN, 18f);
        g2.setFont(sf);
        FontMetrics fm = g2.getFontMetrics();
        int sw = fm.stringWidth(sub);
        int sx = (bounds.width - sw) / 2;
        int sy = bounds.height + 28;
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawString(sub, sx + 1, sy + 1);
        g2.setColor(SUBTITLE_COLOR);
        g2.drawString(sub, sx, sy);

        // Reset translate
        g2.translate(-tx, -ty);
    }

    private void paintStreaks(Graphics2D g2, int w, int h) {
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double speed = 0.06; // px/ms for streak motion
        double offset = (tMillis * speed) % 40;

        for (int y = -h; y < h * 2; y += 40) {
            int yy = (int) (y + offset);
            int x1 = -100, x2 = w + 100;
            // Alternate colors and alphas
            int a = 20 + (int) (20 * ((yy / 40) % 2 == 0 ? 1 : 0.4));
            g2.setColor(new Color(0, 255, 200, a));
            // Draw diagonal line
            g2.drawLine(x1, yy, x2, yy - 80);
        }
    }

    private void paintRadialGlow(Graphics2D g2, Point center, float radius, Color c0, Color c1) {
        Paint old = g2.getPaint();
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point2D.Float(center.x, center.y),
                radius,
                new float[]{0f, 1f},
                new Color[]{c0, c1}
        );
        g2.setPaint(rgp);
        g2.fillOval((int) (center.x - radius), (int) (center.y - radius), (int) (radius * 2), (int) (radius * 2));
        g2.setPaint(old);
    }

    private static void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    // Rounded glass card panel
    private static class GlassCard extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableQuality(g2);
            int w = getWidth(), h = getHeight();

            // Glow shadow
            g2.setColor(new Color(0, 0, 0, 140));
            g2.fillRoundRect(6, 8, w - 12, h - 12, 20, 20);

            // Glass fill
            RoundRectangle2D rr = new RoundRectangle2D.Double(0, 0, w, h, 20, 20);
            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 26, 38, 200),
                    0, h, new Color(16, 22, 34, 220));
            g2.setPaint(gp);
            g2.fill(rr);

            // Border glow
            g2.setColor(new Color(0, 255, 200, 90));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(rr);

            super.paintComponent(g);
            g2.dispose();
        }

        @Override public boolean isOpaque() { return false; }

        @Override public Insets getInsets() {
            return new Insets(18, 18, 18, 18);
        }
    }

    // Fancy neon button
    private static class NeonButton extends JButton {
        private Color accent = ACCENT;

        public NeonButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(new Color(230, 245, 255));
            setBorder(new EmptyBorder(8, 16, 8, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void setAccent(Color c) { this.accent = c; }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableQuality(g2);

            int w = getWidth(), h = getHeight();
            float arc = 14f;

            // Hover/press states
            boolean hover = getModel().isRollover();
            boolean press = getModel().isArmed();

            // Background
            Color bg1 = new Color(18, 24, 36);
            Color bg2 = new Color(14, 18, 28);
            if (press) {
                bg1 = bg1.darker();
                bg2 = bg2.darker();
            }
            g2.setPaint(new GradientPaint(0, 0, bg1, 0, h, bg2));
            g2.fillRoundRect(0, 0, w, h, (int) arc, (int) arc);

            // Border
            Color border = hover ? accent : new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 160);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, (int) arc, (int) arc);

            // Outer glow
            if (hover) {
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 70));
                g2.setStroke(new BasicStroke(6f));
                g2.drawRoundRect(2, 2, w - 5, h - 5, (int) arc, (int) arc);
            }

            // Text
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h + fm.getAscent()) / 2 - 2;
            if (press) { tx += 1; ty += 1; }
            // Soft highlight
            g2.setColor(new Color(255, 255, 255, 40));
            g2.drawString(getText(), tx + 1, ty + 1);
            g2.setColor(getForeground());
            g2.drawString(getText(), tx, ty);

            g2.dispose();
        }

        @Override public void updateUI() {
            // Prevent LAF from forcing default UI
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
        }
    }

    // Text field with placeholder + neon frame
    private static class PromptTextField extends JTextField {
        private final String prompt;

        public PromptTextField(String prompt, int cols) {
            super(cols);
            this.prompt = prompt;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableQuality(g2);

            int w = getWidth(), h = getHeight();
            RoundRectangle2D rr = new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 14, 14);

            // Fill
            g2.setColor(new Color(18, 24, 36, 220));
            g2.fill(rr);

            // Border
            g2.setColor(new Color(0, 255, 200, 90));
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(rr);

            g2.dispose();
            super.paintComponent(g);

            // Prompt
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g3 = (Graphics2D) g.create();
                enableQuality(g3);
                g3.setFont(getFont());
                g3.setColor(new Color(200, 220, 230, 120));
                Insets ins = getInsets();
                g3.drawString(prompt, ins.left, h / 2 + g3.getFontMetrics().getAscent() / 2 - 2);
                g3.dispose();
            }
        }
    }
}