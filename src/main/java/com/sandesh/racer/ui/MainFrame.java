package com.sandesh.racer.ui;

import com.sandesh.racer.logic.HighScoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MainFrame extends JFrame implements GamePanel.GameOverListener {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final WelcomePanel welcomePanel;
    private GamePanel gamePanel;
    private final GameOverPanel gameOverPanel;
    private final HighScoreManager highScoreManager = new HighScoreManager();

    public MainFrame() {
        super("Neon Rush — Car Racing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        welcomePanel = new WelcomePanel(this::startGame);
        gameOverPanel = new GameOverPanel(this::restart, this::exitApp);

        cards.add(welcomePanel, "welcome");
        cards.add(gameOverPanel, "gameover");
        setContentPane(cards);

        pack();
        setMinimumSize(new Dimension(520, 800)); // window border margin

        showWelcome();
    }

    private void showWelcome() {
        cardLayout.show(cards, "welcome");
        welcomePanel.requestFocusInWindow();
    }

    public void startGame(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name to start.", "Name required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (gamePanel != null) {
            cards.remove(gamePanel);
            gamePanel.cleanup();
        }
        gamePanel = new GamePanel(playerName, this);
        cards.add(gamePanel, "game");
        cardLayout.show(cards, "game");
        pack();
        gamePanel.requestGameFocus();
    }

    private void restart() {
        // Bring back the welcome screen with the last name pre-filled.
        String lastName = gameOverPanel.getLastPlayerName();
        welcomePanel.setPlayerName(Objects.toString(lastName, ""));
        showWelcome();
    }

    private void exitApp() {
        dispose();
        System.exit(0);
    }

    @Override
    public void onGameOver(String playerName, int finalScore) {
        // Save to high score list
        highScoreManager.addScore(playerName, finalScore);
        gameOverPanel.setResults(playerName, finalScore, highScoreManager.getTopScores(10));
        cardLayout.show(cards, "gameover");
        gameOverPanel.requestFocusInWindow();

        if (gamePanel != null) {
            gamePanel.cleanup();
        }
    }
}