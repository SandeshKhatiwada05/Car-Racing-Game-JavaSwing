package com.sandesh.racer.logic;

import com.sandesh.racer.model.OpponentCar;
import com.sandesh.racer.model.PlayerCar;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OpponentManager {
    private final int lanes;
    private final int roadX;
    private final int roadWidth;
    private final int screenHeight;
    private final int laneWidth;

    private final List<OpponentCar> opponents = new ArrayList<>();
    private final Random rng = new Random();

    // spawn control
    private double spawnTimer = 0; // millis
    private double spawnInterval = 900; // millis
    private double baseSpeed = 3.6; // pixels per frame at 60fps
    private double globalSpeed = 0; // added to all cars
    private int level = 1;
    private double difficultyTimer = 0; // millis
    private int passedCounter = 0;

    public OpponentManager(int lanes, int roadX, int roadWidth, int screenHeight) {
        this.lanes = lanes;
        this.roadX = roadX;
        this.roadWidth = roadWidth;
        this.screenHeight = screenHeight;
        this.laneWidth = roadWidth / lanes;
    }

    public void update(double dtMillis) {
        spawnTimer += dtMillis;
        difficultyTimer += dtMillis;

        // Difficulty scaling: every 6 seconds, increase speed and spawn rate slightly
        if (difficultyTimer >= 6000) {
            difficultyTimer = 0;
            level++;
            globalSpeed += 0.35;
            spawnInterval = Math.max(420, spawnInterval - 40);
        }

        // Spawn new opponents
        while (spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;
            spawnOpponent();
        }

        // Update cars
        List<OpponentCar> toRemove = new ArrayList<>();
        for (OpponentCar car : opponents) {
            car.update(dtMillis, globalSpeed);
            if (!car.hasBeenCounted() && car.getY() > screenHeight - 100) {
                car.markCounted();
                passedCounter++;
            }
            if (car.isOffScreen(screenHeight)) {
                toRemove.add(car);
            }
        }
        opponents.removeAll(toRemove);
    }

    private void spawnOpponent() {
        int laneIndex = rng.nextInt(lanes);
        int carW = 52 + rng.nextInt(8);
        int carH = 86 + rng.nextInt(10);
        int laneCenter = roadX + laneWidth * laneIndex + laneWidth / 2;
        int x = laneCenter - carW / 2;
        int y = -carH - rng.nextInt(140);

        OpponentCar car = new OpponentCar(x, y, carW, carH, baseSpeed + rng.nextDouble() * 1.8);
        opponents.add(car);
    }

    public boolean collidesWith(Rectangle playerBounds) {
        for (OpponentCar car : opponents) {
            if (car.getBounds().intersects(playerBounds)) {
                return true;
            }
        }
        return false;
    }

    public List<OpponentCar> getOpponents() {
        return opponents;
    }

    public int consumePassedCount(int playerBottomY) {
        int c = passedCounter;
        passedCounter = 0;
        return c;
    }

    public double getCurrentSpeed() {
        return baseSpeed + globalSpeed;
    }

    public int getLevel() { return level; }

    public double getSpeedFactor() {
        // For scoring, map speed roughly
        return 1.0 + (globalSpeed / 4.0) + (level - 1) * 0.1;
    }
}