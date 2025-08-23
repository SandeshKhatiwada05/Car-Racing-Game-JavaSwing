package com.sandesh.racer.logic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HighScoreManager {

    private final Path storagePath;

    public HighScoreManager() {
        String home = System.getProperty("user.home");
        Path dir = Path.of(home, ".neon-rush");
        try {
            Files.createDirectories(dir);
        } catch (IOException ignored) { }
        storagePath = dir.resolve("highscores.txt");
    }

    public record ScoreEntry(String name, int score) {}

    public synchronized void addScore(String name, int score) {
        List<ScoreEntry> all = readAll();
        all.add(new ScoreEntry(sanitize(name), score));
        all.sort((a, b) -> Integer.compare(b.score(), a.score()));
        // keep top 50
        if (all.size() > 50) all = new ArrayList<>(all.subList(0, 50));
        writeAll(all);
    }

    public synchronized List<ScoreEntry> getTopScores(int n) {
        List<ScoreEntry> all = readAll();
        all.sort((a, b) -> Integer.compare(b.score(), a.score()));
        return all.subList(0, Math.min(n, all.size()));
    }

    private List<ScoreEntry> readAll() {
        List<ScoreEntry> list = new ArrayList<>();
        if (!Files.exists(storagePath)) return list;
        try (BufferedReader br = Files.newBufferedReader(storagePath)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    list.add(new ScoreEntry(name, score));
                }
            }
        } catch (Exception ignored) { }
        return list;
    }

    private void writeAll(List<ScoreEntry> list) {
        try (BufferedWriter bw = Files.newBufferedWriter(storagePath)) {
            for (ScoreEntry e : list) {
                bw.write(e.name() + "," + e.score());
                bw.newLine();
            }
        } catch (IOException ignored) { }
    }

    private String sanitize(String s) {
        if (s == null) return "Player";
        String t = s.replaceAll("[,\\n\\r]", " ").trim();
        if (t.isEmpty()) t = "Player";
        if (t.length() > 20) t = t.substring(0, 20);
        return t;
    }
}