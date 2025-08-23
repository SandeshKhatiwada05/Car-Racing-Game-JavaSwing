package com.sandesh.racer.logic;

public class ScoreManager {
    private int score = 0;
    private double timeAccumulator = 0; // millis
    private float dashPhase = 0f;

    public void update(double dtMillis, double speedFactor) {
        timeAccumulator += dtMillis;
        // Every 100ms, add points scaled by speed/difficulty
        while (timeAccumulator >= 100) {
            timeAccumulator -= 100;
            score += (int) Math.max(1, Math.round(1 * speedFactor));
        }

        // Animate lane dash offset for visual movement
        dashPhase += (float) (dtMillis * 0.08);
        if (dashPhase > 40_000) dashPhase = 0;
    }

    public int getScore() { return score; }

    public void addPoints(int pts) { score += Math.max(0, pts); }

    public float getDashPhase() { return dashPhase; }
}