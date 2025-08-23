# Neon Rush — Car Racing (Java Swing + OOP) 🚗⚡

A polished, neon‑styled, top‑down car racing game built with Java Swing. Steer through lanes, avoid oncoming traffic, rack up points over time, and save your high scores locally. Clean OOP design and no external assets required.

---

## Features

- Welcome screen with player name entry and Start/Exit
- Smooth ~60 FPS loop via Swing Timer
- Player movement: Left/Right arrows or A/D
- Opponents spawn in random lanes and get faster over time
- Live HUD: score, speed, level with neon styling
- Collision detection and animated lane markers
- Game Over screen showing player name + final score
- Persistent high‑scores saved to ~/.neon-rush/highscores.txt
- Clean OOP layers: model, logic, UI, util

---

## Screenshots

- docs/screenshots/welcome.png — Welcome screen
- docs/screenshots/gameplay.png — Gameplay with HUD
- docs/screenshots/gameover.png — Game Over with high scores

(Place screenshots in docs/screenshots to display them here.)

---

## Requirements

- Java 17 or newer
- Maven 3.8+ (recommended) or a Java‑capable IDE (IntelliJ IDEA, VS Code + Java, Eclipse)

---

## Quick Start

### Run with Maven (recommended)
```bash
mvn -q clean package
mvn -q exec:java
```

### Run with plain Java
```bash
# From project root
javac -d out $(find src/main/java -name "*.java")
java -cp out com.sandesh.racer.App
```

### Run in IntelliJ IDEA
1. Open the project folder.
2. Wait for Maven indexing to finish.
3. Locate App.java (src/main/java/com/sandesh/racer/App.java).
4. Right‑click App.main() → Run.

---

## Controls

- Left Arrow / A — Move left
- Right Arrow / D — Move right

Tip: Keep to a lane and anticipate spawns as speed increases.

---

## How It Works

- Game loop: Swing Timer at ~16 ms (about 60 FPS).
- Drawing: paintComponent renders background, road, cars, and HUD.
- Input: KeyListener captures left/right (and A/D) for movement.
- Spawns: Opponents appear in random lanes with slight speed variance.
- Difficulty: Spawn interval and global speed increase over time.
- Scoring: Time‑based points scaled by difficulty + pass bonuses for near misses.
- High scores: Stored in ~/.neon-rush/highscores.txt (top scores kept).

---

## Project Structure

```
src/main/java/com/sandesh/racer/
├─ App.java                     # Entry point
├─ ui/
│  ├─ MainFrame.java            # CardLayout host for screens
│  ├─ WelcomePanel.java         # Name input + Start/Exit
│  ├─ GamePanel.java            # Game loop + rendering + input
│  └─ GameOverPanel.java        # Final score + high-score list + Restart/Exit
├─ model/
│  ├─ PlayerCar.java
│  └─ OpponentCar.java
├─ logic/
│  ├─ OpponentManager.java      # Spawning, movement, difficulty, collisions
│  ├─ ScoreManager.java         # Time-based scoring + lane dash animation
│  └─ HighScoreManager.java     # Persistent highscores
└─ util/
   └─ UiUtils.java              # Antialiasing + panel drawing helpers
```

---

## Configuration & Assets

- Theme: Neon glow with gradients; no external images required.
- Sounds: A simple system beep on crash. You can plug in WAV/AIFF via Clip if desired.
- Window size: 480×720 gameplay area with margins in GamePanel.

To use custom car images:
- Add resources under src/main/resources, load with Toolkit/ImageIO in PlayerCar/OpponentCar, and draw via Graphics2D.drawImage.

---

## High Scores

- Location: ~/.neon-rush/highscores.txt
- Format: CSV lines "name,score"
- Keeps up to 50 top scores.
- Names sanitized to avoid commas/newlines and capped at 20 chars.

---

## Troubleshooting

- No window focus for controls? Click the game area or press Tab once. GamePanel requests focus on show/resizes.
- Using Java 22? Supported. Ensure your IDE is using the same JDK version you compiled with.
- Font errors from older code? This project sets monospaced fonts with new Font(Font.MONOSPACED, ...). If you pulled an older commit, update GameOverPanel accordingly.

---

## Roadmap Ideas

- Multiple themes (Neon, Desert, Night Rain)
- Power‑ups (shield, slow‑mo)
- Car images and sprite animation
- Crash/score sound effects and music
- Pause menu and resume
- In‑game achievements

---

## Credits

Designed and coded with Java Swing. Inspired by classic lane‑runner games and neon synthwave vibes.
