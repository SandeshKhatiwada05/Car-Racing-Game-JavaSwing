package com.sandesh.racer.ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class WelcomePanel extends JPanel {

    private final JTextField nameField = new JTextField(18);

    public WelcomePanel(Consumer<String> onStart) {
        setPreferredSize(new Dimension(GamePanel.WIDTH + 40, GamePanel.HEIGHT + 40));
        setLayout(new GridBagLayout());

        JLabel title = new JLabel("Neon Rush");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 42f));
        title.setForeground(new Color(120, 200, 255));

        JLabel subtitle = new JLabel("Top‑View Car Racing");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 18f));
        subtitle.setForeground(new Color(200, 220, 240));

        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setForeground(Color.WHITE);

        JButton startBtn = new JButton("Start Game");
        startBtn.addActionListener(e -> onStart.accept(nameField.getText().trim()));

        JButton quitBtn = new JButton("Quit");
        quitBtn.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispose();
            System.exit(0);
        });

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints fc = new GridBagConstraints();
        fc.insets = new Insets(8, 8, 8, 8);
        fc.gridx = 0; fc.gridy = 0; fc.anchor = GridBagConstraints.LINE_END;
        form.add(nameLabel, fc);
        fc.gridx = 1; fc.gridy = 0; fc.anchor = GridBagConstraints.LINE_START;
        form.add(nameField, fc);
        fc.gridx = 0; fc.gridy = 1; fc.gridwidth = 2; fc.anchor = GridBagConstraints.CENTER;
        form.add(startBtn, fc);
        fc.gridy = 2;
        form.add(quitBtn, fc);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.gridx = 0; c.gridy = 0;
        add(title, c);
        c.gridy = 1;
        add(subtitle, c);
        c.gridy = 2;
        add(form, c);

        setBackground(new Color(10, 12, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Subtle gradient background
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(12, 16, 30),
                0, h, new Color(6, 8, 16));
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

    public void setPlayerName(String name) {
        nameField.setText(name);
    }
}