/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Full-scale Space Invaders for SSD1331.
 * * Includes AI player, missiles, explosions, level progression, and landing logic.
 *
 * @author Steven P. Goldsmith
 * @version 1.1.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Invaders", mixinStandardHelpOptions = true, version = "1.1.0-SNAPSHOT")
public class Ssd1331SpaceInvaders implements Callable<Integer> {

    @Option(names = {"-d", "--device"}, description = "SPI device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/spidev1.0";
    @Option(names = {"-m", "--mode"}, description = "SPI mode, ${DEFAULT-VALUE} by default.")
    private int mode = 3;
    @Option(names = {"-s", "--speed"}, description = "Max speed in Hz, ${DEFAULT-VALUE} by default.")
    private int speed = 8000000;
    @Option(names = {"-g", "--gpio-device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String gpioDevice = "/dev/gpiochip0";
    @Option(names = {"-dc", "--dc-line"}, description = "DC line, ${DEFAULT-VALUE} by default.")
    private int dc = 199;
    @Option(names = {"-res", "--res-line"}, description = "RES line, ${DEFAULT-VALUE} by default.")
    private int res = 198;
    @Option(names = {"-f", "--fps"}, description = "Target frames per second", defaultValue = "30")
    public int fps;

    private final Random random = new Random();
    private boolean running = true;
    private int score = 0;
    private int level = 1;
    private int playerX;
    private int aiTargetX;
    private Projectile playerShot = null;
    private final List<Invader> invaders = new ArrayList<>();
    private final List<Projectile> alienMissiles = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    
    private int rackX, rackY, rackDir;
    private enum State { PLAYING, GAME_OVER }
    private State gameState = State.PLAYING;

    // Sprite Bitmasks (8x8)
    private static final int[] INV_TOP = {0x18, 0x3C, 0x7E, 0xDB, 0xFF, 0x24, 0x42, 0x24};
    private static final int[] INV_MID = {0x24, 0x00, 0x3C, 0x7E, 0xFF, 0xFF, 0x66, 0x24};
    private static final int[] INV_BOT = {0x18, 0x3C, 0x7E, 0xDB, 0xFF, 0xFF, 0xA5, 0x00};
    private static final int[] EXPLOSION = {0x44, 0x22, 0x11, 0x00, 0x11, 0x22, 0x44, 0x00};

    public record Projectile(int x, int y) {}
    public record Explosion(int x, int y, int timer) {}
    public static class Invader {
        public int x, y, type;
        public boolean active = true;
        public Invader(int x, int y, int type) { this.x = x; this.y = y; this.type = type; }
    }

    private void initLevel(int w, int h) {
        playerX = w / 2 - 4;
        aiTargetX = playerX;
        invaders.clear();
        alienMissiles.clear();
        explosions.clear();
        playerShot = null;
        rackX = 4;
        rackY = 12;
        rackDir = 1 + (level / 2); // Accelerate rack based on level
        
        // Define grid based on original logic: 5 columns, 3 rows
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 6; c++) {
                invaders.add(new Invader(c * 12, r * 10, r));
            }
        }
    }

    private void update(int w, int h) {
        if (gameState == State.GAME_OVER) return;

        // AI Logic: Track threats and targets
        if (random.nextInt(10) > 2) {
            if (!alienMissiles.isEmpty()) {
                aiTargetX = alienMissiles.get(0).x - 4; // Dodge first missile
            } else {
                invaders.stream().filter(i -> i.active).findFirst().ifPresent(i -> aiTargetX = i.x + rackX);
            }
        }
        if (playerX < aiTargetX) playerX += 2; else if (playerX > aiTargetX) playerX -= 2;
        playerX = Math.max(0, Math.min(w - 9, playerX));

        // Move Rack
        int maxX = invaders.stream().filter(i -> i.active).mapToInt(i -> i.x + rackX).max().orElse(0);
        int minX = invaders.stream().filter(i -> i.active).mapToInt(i -> i.x + rackX).min().orElse(w);
        int maxY = invaders.stream().filter(i -> i.active).mapToInt(i -> i.y + rackY).max().orElse(0);

        if (rackDir > 0 && maxX > w - 10) { rackDir *= -1; rackY += 4; }
        else if (rackDir < 0 && minX < 2) { rackDir *= -1; rackY += 4; }
        else { rackX += rackDir; }

        // Landing/Game Over check
        if (maxY >= h - 14) gameState = State.GAME_OVER;

        // Player Firing
        if (playerShot == null) playerShot = new Projectile(playerX + 4, h - 10);
        else {
            playerShot = new Projectile(playerShot.x, playerShot.y - 4);
            if (playerShot.y < 0) playerShot = null;
            else {
                for (var inv : invaders) {
                    if (inv.active && playerShot.x >= inv.x + rackX && playerShot.x <= inv.x + rackX + 8 
                        && playerShot.y >= inv.y + rackY && playerShot.y <= inv.y + rackY + 8) {
                        inv.active = false;
                        explosions.add(new Explosion(inv.x + rackX, inv.y + rackY, 5));
                        playerShot = null;
                        score += 10;
                        break;
                    }
                }
            }
        }

        // Alien Firing
        if (random.nextInt(100) < 5 + level && alienMissiles.size() < 3) {
            invaders.stream().filter(i -> i.active).skip(random.nextInt(1)).findFirst()
                    .ifPresent(i -> alienMissiles.add(new Projectile(i.x + rackX + 4, i.y + rackY + 8)));
        }

        // Move Alien Missiles
        for (int i = alienMissiles.size() - 1; i >= 0; i--) {
            var m = alienMissiles.get(i);
            alienMissiles.set(i, new Projectile(m.x, m.y + 2));
            if (m.y > h) alienMissiles.remove(i);
            else if (m.x >= playerX && m.x <= playerX + 9 && m.y >= h - 10 && m.y <= h - 4) {
                gameState = State.GAME_OVER;
            }
        }

        // Update Explosions
        explosions.removeIf(e -> e.timer <= 0);
        for (int i = 0; i < explosions.size(); i++) {
            var e = explosions.get(i);
            explosions.set(i, new Explosion(e.x, e.y, e.timer - 1));
        }

        // Next Level check
        if (invaders.stream().noneMatch(i -> i.active)) {
            level++;
            initLevel(w, h);
        }
    }

    private void drawMask(Graphics2D g, int x, int y, int[] mask, Color color) {
        g.setColor(color);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (((mask[r] >> (7 - c)) & 1) == 1) g.fillRect(x + c, y + r, 1, 1);
            }
        }
    }

    private void render(Ssd1331 oled, BufferedImage img, Graphics2D g) {
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, oled.getWidth(), oled.getHeight());

        if (gameState == State.GAME_OVER) {
            g.setColor(Color.RED);
            g.setFont(new Font("Monospaced", Font.BOLD, 12));
            g.drawString("GAME OVER", 15, 30);
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 15, 45);
            oled.drawImage(img);
            return;
        }

        // Draw HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 8));
        g.drawString("S:" + score, 2, 8);
        g.drawString("L:" + level, oled.getWidth() - 25, 8);

        // Draw Player (Yellow)
        g.setColor(Color.YELLOW);
        g.fillRect(playerX, oled.getHeight() - 6, 9, 4);
        g.fillRect(playerX + 3, oled.getHeight() - 8, 3, 2);

        // Draw Invaders
        for (var inv : invaders) {
            if (!inv.active) continue;
            int[] mask = (inv.type == 0) ? INV_TOP : (inv.type == 1 ? INV_MID : INV_BOT);
            Color color = (inv.type == 0) ? Color.MAGENTA : (inv.type == 1 ? Color.CYAN : Color.GREEN);
            drawMask(g, inv.x + rackX, inv.y + rackY, mask, color);
        }

        // Draw Missiles (Red for aliens, White for player)
        g.setColor(Color.WHITE);
        if (playerShot != null) g.drawLine(playerShot.x, playerShot.y, playerShot.x, playerShot.y - 2);
        
        g.setColor(Color.RED);
        for (var m : alienMissiles) g.drawLine(m.x, m.y, m.x, m.y + 2);

        // Draw Explosions (Orange)
        for (var e : explosions) drawMask(g, e.x, e.y, EXPLOSION, Color.ORANGE);

        oled.drawImage(img);
    }

    @Override
    public Integer call() throws Exception {
        try (final var oled = new Ssd1331(device, mode, speed, gpioDevice, dc, res)) {
            oled.setup();
            oled.clear();
            final var image = new BufferedImage(oled.getWidth(), oled.getHeight(), BufferedImage.TYPE_INT_RGB);
            final var g2d = image.createGraphics();
            
            initLevel(oled.getWidth(), oled.getHeight());

            while (running) {
                update(oled.getWidth(), oled.getHeight());
                render(oled, image, g2d);
                if (gameState == State.GAME_OVER) {
                    TimeUnit.SECONDS.sleep(3);
                    running = false;
                }
                TimeUnit.MILLISECONDS.sleep(1000 / fps);
            }
            g2d.dispose();
        }
        return 0;
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new Ssd1331SpaceInvaders()).execute(args));
    }
}
