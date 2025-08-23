package com.sandesh.racer.ui;

import com.sandesh.racer.logic.OpponentManager;
import com.sandesh.racer.logic.ScoreManager;
import com.sandesh.racer.model.OpponentCar;
import com.sandesh.racer.model.PlayerCar;
import com.sandesh.racer.util.UiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener, ComponentListener {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 720;

    private final String playerName;
    private final Timer timer;
    private long lastUpdateNanos;
    private final PlayerCar player;
    private final OpponentManager opponentManager;
    private final ScoreManager scoreManager;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private final int roadX = 60;
    private final int roadWidth = 360;
    private final int lanes = 3;

    private boolean isRunning = true;
    private final GameOverListener listener;

    public interface GameOverListener {
        void onGameOver(String playerName, int finalScore);
    }

    public GamePanel(String playerName, GameOverListener listener) {
        this.playerName = playerName;
        this.listener = listener;

        setPreferredSize(new Dimension(WIDTH + 40, HEIGHT + 40));
        setBackground(new Color(8, 10, 16));
        setFocusable(true);
        addKeyListener(this);
        addComponentListener(this);

        int laneWidth = roadWidth / lanes;
        int playerW = 54;
        int playerH = 92;
        int baseY = HEIGHT - playerH - 50;

        player = new PlayerCar(roadX + laneWidth / 2 - playerW / 2, baseY, playerW, playerH, 6.0, roadX, roadX + roadWidth);
        opponentManager = new OpponentManager(lanes, roadX, roadWidth, HEIGHT);
        scoreManager = new ScoreManager();

        timer = new Timer(16, this); // ~60 FPS
        lastUpdateNanos = System.nanoTime();
        timer.start();
    }

    public void requestGameFocus() {
        requestFocusInWindow();
    }

    public void cleanup() {
        timer.stop();
        removeKeyListener(this);
        removeComponentListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Create drawing area with margin
        Graphics2D g2 = (Graphics2D) g.create();
        UiUtils.enableQuality(g2);

        int ox = 20, oy = 20; // outer margin
        g2.translate(ox, oy);

        // Background gradient (sky glow)
        GradientPaint bg = new GradientPaint(0, 0, new Color(16, 20, 36),
                0, HEIGHT, new Color(6, 8, 16));
        g2.setPaint(bg);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        drawRoad(g2);
        drawHud(g2);

        // Draw opponents and player
        for (OpponentCar car : opponentManager.getOpponents()) {
            car.draw(g2);
        }
        player.draw(g2);

        g2.dispose();
    }

    private void drawRoad(Graphics2D g2) {
        // Road gradient
        GradientPaint roadPaint = new GradientPaint(roadX, 0, new Color(28, 28, 32),
                roadX + roadWidth, 0, new Color(20, 20, 24));
        g2.setPaint(roadPaint);
        g2.fillRoundRect(roadX, 0, roadWidth, HEIGHT, 20, 20);

        // Neon borders
        g2.setColor(new Color(0, 255, 200, 60));
        g2.setStroke(new BasicStroke(6f));
        g2.drawRoundRect(roadX, 0, roadWidth, HEIGHT, 20, 20);

        // Lane markers (moving illusion)
        g2.setColor(new Color(240, 240, 240, 180));
        g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{22f, 18f}, scoreManager.getDashPhase()));
        int laneWidth = roadWidth / lanes;
        for (int i = 1; i < lanes; i++) {
            int x = roadX + i * laneWidth;
            g2.drawLine(x, 0, x, HEIGHT);
        }

        // Side glow
        g2.setPaint(new GradientPaint(roadX - 10, 0, new Color(0, 255, 200, 35),
                roadX + 10, 0, new Color(0, 0, 0, 0)));
        g2.fillRect(roadX - 12, 0, 12, HEIGHT);
        g2.setPaint(new GradientPaint(roadX + roadWidth + 10, 0, new Color(0, 255, 200, 35),
                roadX + roadWidth - 10, 0, new Color(0, 0, 0, 0)));
        g2.fillRect(roadX + roadWidth, 0, 12, HEIGHT);
    }

    private void drawHud(Graphics2D g2) {
        String scoreStr = "Score: " + scoreManager.getScore();
        String speedStr = String.format("Speed: %.1f", opponentManager.getCurrentSpeed());
        String levelStr = "Level: " + opponentManager.getLevel();

        // HUD background
        int pad = 14;
        Font f = getFont().deriveFont(Font.BOLD, 16f);
        g2.setFont(f);
        FontMetrics fm = g2.getFontMetrics();
        int w1 = fm.stringWidth(scoreStr);
        int w2 = fm.stringWidth(speedStr);
        int w3 = fm.stringWidth(levelStr);
        int maxw = Math.max(w1, Math.max(w2, w3)) + pad * 2;
        int h = fm.getHeight() * 3 + pad * 2 - 6;

        int x = 16, y = 16;
        UiUtils.drawPanel(g2, x, y, maxw, h, new Color(0, 0, 0, 120), new Color(0, 255, 200, 90));

        g2.setColor(new Color(230, 250, 255));
        g2.drawString(scoreStr, x + pad, y + fm.getAscent() + pad - 6);
        g2.setColor(new Color(170, 230, 255));
        g2.drawString(speedStr, x + pad, y + fm.getAscent() * 2 + pad - 2);
        g2.setColor(new Color(255, 180, 220));
        g2.drawString(levelStr, x + pad, y + fm.getAscent() * 3 + pad + 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double dt = (now - lastUpdateNanos) / 1_000_000.0; // millis
        if (dt < 0) dt = 0;
        if (dt > 100) dt = 100; // clamp
        lastUpdateNanos = now;

        if (!isRunning) return;

        // Input -> move player
        if (leftPressed && !rightPressed) player.moveLeft();
        if (rightPressed && !leftPressed) player.moveRight();

        // Animate player (lights/wheels pulse)
        player.tick(dt);

        // Update world
        opponentManager.update(dt);
        scoreManager.update(dt, opponentManager.getSpeedFactor());

        // Check pass events for scoring bonus
        int passed = opponentManager.consumePassedCount(player.getY() + player.getHeight());
        if (passed > 0) scoreManager.addPoints(passed * 5);

        // Collision?
        if (opponentManager.collidesWith(player.getBounds())) {
            gameOver();
            return;
        }

        repaint();
    }

    private void gameOver() {
        isRunning = false;
        timer.stop();
        Toolkit.getDefaultToolkit().beep(); // simple crash sound
        listener.onGameOver(playerName, scoreManager.getScore());
    }

    // KeyListener
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        if (!isRunning) return;
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) leftPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) rightPressed = true;
    }
    @Override public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) rightPressed = false;
    }

    // Ensure focus when shown
    @Override public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override public void componentResized(ComponentEvent e) { requestFocusInWindow(); }
    @Override public void componentMoved(ComponentEvent e) { }
    @Override public void componentShown(ComponentEvent e) { requestFocusInWindow(); }
    @Override public void componentHidden(ComponentEvent e) { }
}