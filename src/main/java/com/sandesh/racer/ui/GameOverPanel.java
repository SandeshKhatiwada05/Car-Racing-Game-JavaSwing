package com.sandesh.racer.ui;

import com.sandesh.racer.logic.HighScoreManager.ScoreEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class GameOverPanel extends JPanel {

    private final JLabel title = new JLabel("Game Over!");
    private final JLabel scoreLabel = new JLabel("Your Score: 0");
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> highScoreList = new JList<>(listModel);
    private String lastPlayerName = "";

    // Theme colors
    private static final Color BG = new Color(10, 12, 20);
    private static final Color PANEL_BG = new Color(16, 20, 32);
    private static final Color TEXT = new Color(230, 245, 255);
    private static final Color SUBTEXT = new Color(180, 210, 255);
    private static final Color ACCENT = new Color(0, 255, 200);
    private static final Color PANEL_BORDER = new Color(0, 200, 170);
    private static final Color ROW_ALT = new Color(20, 26, 38);
    private static final Color ROW_BASE = new Color(16, 22, 34);
    private static final Color SELECTION_BG = new Color(12, 40, 50);
    private static final Color SELECTION_ACCENT = new Color(0, 255, 200);

    public GameOverPanel(Runnable onRestart, Runnable onExit) {
        setPreferredSize(new Dimension(GamePanel.WIDTH + 40, GamePanel.HEIGHT + 40));
        setLayout(new GridBagLayout());
        setBackground(BG);

        title.setFont(title.getFont().deriveFont(Font.BOLD, 36f));
        title.setForeground(new Color(255, 110, 150));
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.PLAIN, 20f));
        scoreLabel.setForeground(TEXT);

        JLabel hsTitle = new JLabel("High Scores");
        hsTitle.setForeground(SUBTEXT);
        hsTitle.setFont(hsTitle.getFont().deriveFont(Font.BOLD, 18f));

        // High score list styling
        highScoreList.setOpaque(true);
        highScoreList.setBackground(ROW_BASE);
        highScoreList.setForeground(TEXT);
        highScoreList.setSelectionBackground(SELECTION_BG);
        highScoreList.setSelectionForeground(TEXT);
        highScoreList.setBorder(new EmptyBorder(6, 10, 6, 10));
        highScoreList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        highScoreList.setFixedCellHeight(26);
        highScoreList.setCellRenderer(new NeonListCellRenderer());

        JScrollPane sp = new JScrollPane(highScoreList);
        sp.setPreferredSize(new Dimension(340, 250));
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PANEL_BORDER, 1, true),
                new EmptyBorder(6, 6, 6, 6)
        ));
        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(PANEL_BG);
        sp.setOpaque(true);
        sp.setBackground(PANEL_BG);

        JButton restartBtn = new JButton("Restart");
        styleButton(restartBtn);
        restartBtn.addActionListener(e -> onRestart.run());

        JButton exitBtn = new JButton("Exit");
        styleButton(exitBtn);
        exitBtn.addActionListener(e -> onExit.run());

        // Layout
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        add(title, c);
        c.gridy = 1;
        add(scoreLabel, c);
        c.gridy = 2;
        add(hsTitle, c);
        c.gridy = 3;
        add(sp, c);
        c.gridy = 4; c.gridwidth = 1;
        add(restartBtn, c);
        c.gridx = 1;
        add(exitBtn, c);
    }

    private void styleButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(new Color(18, 24, 36));
        b.setForeground(TEXT);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
    }

    public void setResults(String playerName, int finalScore, List<ScoreEntry> topScores) {
        lastPlayerName = playerName;
        title.setText("Game Over, " + playerName + "!");
        scoreLabel.setText("Your Score: " + finalScore);
        listModel.clear();
        int rank = 1;
        for (ScoreEntry e : topScores) {
            listModel.addElement(String.format("%2d. %-12s %6d", rank++, e.name(), e.score()));
        }
        // Ensure first item visible and nothing is pre-selected to avoid focus color surprises
        highScoreList.clearSelection();
        if (!listModel.isEmpty()) {
            highScoreList.ensureIndexIsVisible(0);
        }
    }

    public String getLastPlayerName() {
        return lastPlayerName;
    }

    // Custom renderer for alternating rows + neon selection accent
    private static class NeonListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            lbl.setOpaque(true);
            lbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            lbl.setForeground(TEXT);
            lbl.setBorder(new EmptyBorder(3, 6, 3, 6));

            if (isSelected) {
                lbl.setBackground(SELECTION_BG);
                lbl.setForeground(TEXT);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, SELECTION_ACCENT),
                        new EmptyBorder(3, 6, 3, 6)
                ));
            } else {
                lbl.setBackground((index % 2 == 0) ? ROW_BASE : ROW_ALT);
            }
            return lbl;
        }
    }
}