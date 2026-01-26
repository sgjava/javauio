/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
 * Space Invaders for SSD1331 with AI-driven player and resolution-aware scaling.
 * <p>
 * This class calculates invader grid density and entity positioning relative to the display resolution. It utilizes sub-pixel
 * precision for movement and bitmask-level collision for bunkers.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Ssd1331SpaceInvaders", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "SSD1331 Space Invaders")
public class Ssd1331SpaceInvaders implements Callable<Integer> {

    /**
     * SPI device path.
     */
    @Option(names = {"-d", "--device"}, description = "SPI device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/spidev1.0";

    /**
     * SPI mode.
     */
    @Option(names = {"-m", "--mode"}, description = "SPI mode, ${DEFAULT-VALUE} by default.")
    private int mode = 3;

    /**
     * Max SPI speed in Hz.
     */
    @Option(names = {"-s", "--speed"}, description = "Max speed in Hz, ${DEFAULT-VALUE} by default.")
    private int speed = 10000000;

    /**
     * GPIO device path.
     */
    @Option(names = {"-g", "--gpio-device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String gpioDevice = "/dev/gpiochip0";

    /**
     * Data/Command GPIO line.
     */
    @Option(names = {"-dc", "--dc-line"}, description = "DC line, ${DEFAULT-VALUE} by default.")
    private int dc = 199;

    /**
     * Reset GPIO line.
     */
    @Option(names = {"-res", "--res-line"}, description = "RES line, ${DEFAULT-VALUE} by default.")
    private int res = 198;

    /**
     * Target frames per second.
     */
    @Option(names = {"-f", "--fps"}, description = "Target frames per second", defaultValue = "60")
    private int fps;

    /**
     * Internal game state.
     */
    public enum State {
        PLAYING, EXPLODING, GAME_OVER
    }

    /**
     * Random number generator for game logic.
     */
    private final Random random = new Random();

    /**
     * Main loop control flag.
     */
    private boolean running = true;

    /**
     * Current game state.
     */
    private State gameState = State.PLAYING;

    /**
     * Current player score.
     */
    private int score = 0;

    /**
     * Remaining player lives.
     */
    private int lives = 3;

    /**
     * Frame counter for player explosion animation.
     */
    private int playerExplosionTimer = 0;

    /**
     * Flag to prevent multiple hits in a single frame.
     */
    private boolean hitLock = false;

    /**
     * Player X position scaled by 100 for sub-pixel movement.
     */
    private int playerXScaled;

    /**
     * Constant for player movement speed.
     */
    private static final int PLAYER_SPEED_SCALED = 200;

    /**
     * Timer for AI trajectory changes.
     */
    private int aiDecisionTimer = 0;

    /**
     * Current X coordinate target for the AI.
     */
    private int aiTargetX = 0;

    /**
     * Active player projectile.
     */
    private Projectile playerShot = null;

    /**
     * List of active alien projectiles.
     */
    private final List<Projectile> alienMissiles = new ArrayList<>();

    /**
     * List of active invaders.
     */
    private final List<Invader> invaders = new ArrayList<>();

    /**
     * List of active particle explosions.
     */
    private final List<Explosion> explosions = new ArrayList<>();

    /**
     * Bunker bitmasks [bunker index][row].
     */
    private final int[][] bunkers = new int[3][3];

    /**
     * X offset of the invader rack.
     */
    private int rackX;

    /**
     * Y offset of the invader rack.
     */
    private int rackY;

    /**
     * Movement direction of the rack.
     */
    private int rackDir = 2;

    /**
     * Timer for invader rack stepping.
     */
    private int moveTimer = 0;

    /**
     * Saucer X position.
     */
    private int saucerX = -20;

    /**
     * Timer for saucer spawn intervals.
     */
    private int saucerTimer = 0;

    /**
     * Saucer activity flag.
     */
    private boolean saucerActive = false;

    /**
     * Bitmask for the Saucer sprite.
     */
    private static final int[] SAUCER_BITS = {0x0F0, 0x3FC, 0x7FE, 0xAA8, 0x444};

    /**
     * Bitmask for the Invader explosion sprite.
     */
    private static final int[] EXPLOSION_BITS = {0x24, 0x50, 0x18, 0x50, 0x24};

    /**
     * Projectile data record.
     */
    public record Projectile(int x, int y) {

    }

    /**
     * Invader entity class.
     */
    public static class Invader {

        public int x, y, type;
        public boolean active;

        public Invader(int x, int y, int type, boolean active) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.active = active;
        }
    }

    /**
     * Particle explosion entity class.
     */
    public static class Explosion {

        public int x, y, timer = 6;

        public Explosion(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Initializes the level state, including grid calculation and bunker resets.
     *
     * @param w Display width.
     * @param h Display height.
     * @param fullReset True if lives/score should be reset.
     */
    public void initLevel(int w, int h, boolean fullReset) {
        if (fullReset) {
            score = 0;
            lives = 3;
        }
        playerXScaled = (w / 2) * 100;
        aiTargetX = w / 2;
        playerShot = null;
        alienMissiles.clear();
        invaders.clear();
        explosions.clear();
        hitLock = false;
        saucerActive = false;
        saucerTimer = 0;

        final var cols = Math.max(5, (w * 7 / 10) / 9);
        final var rows = Math.max(1, (h * 4 / 10) / 7);
        rackX = (w - (cols * 9)) / 2;
        rackY = h / 6;
        for (var row = 0; row < rows; row++) {
            for (var col = 0; col < cols; col++) {
                var type = (row == 0) ? 0 : (row < rows / 2 ? 1 : 2);
                invaders.add(new Invader(col * 9, row * 7, type, true));
            }
        }
        for (var i = 0; i < 3; i++) {
            bunkers[i][0] = 0x3E;
            bunkers[i][1] = 0x7F;
            bunkers[i][2] = 0x63;
        }
    }

    /**
     * Triggers player hit state.
     *
     * * @param isLanding True if invaders have reached the bottom (Instant Game Over).
     */
    private void registerHit(boolean isLanding) {
        if (!hitLock) {
            hitLock = true;
            lives = isLanding ? 0 : lives - 1;
            playerExplosionTimer = 90;
            gameState = State.EXPLODING;
            playerShot = null;
            alienMissiles.clear();
        }
    }

    /**
     * Main update logic for AI, movement, and combat.
     */
    private void update(int w, int h) {
        if (gameState == State.EXPLODING) {
            if (--playerExplosionTimer <= 0) {
                if (lives > 0) {
                    initLevel(w, h, false);
                    gameState = State.PLAYING;
                } else {
                    gameState = State.GAME_OVER;
                }
            }
            return;
        }
        if (gameState == State.PLAYING) {
            updateAI(w, h);
            updateSaucer(w);
            updateInvaders(w, h);
            updateCombat(w, h);
            explosions.removeIf(e -> --e.timer <= 0);
        }
    }

    /**
     * AI logic for dodging missiles and targeting invaders.
     */
    private void updateAI(int w, int h) {
        if (--aiDecisionTimer <= 0) {
            aiDecisionTimer = 8;
            final var currentX = playerXScaled / 100;
            var newTargetX = currentX;
            Projectile threat = null;
            for (var m : alienMissiles) {
                if (Math.abs(m.x - currentX) < 12 && m.y > h - 35) {
                    if (threat == null || m.y > threat.y) {
                        threat = m;
                    }
                }
            }
            if (threat != null) {
                final var dodgeDist = 18 + random.nextInt(8);
                newTargetX = (threat.x < currentX) ? currentX + dodgeDist : currentX - dodgeDist;
            } else {
                Invader target = null;
                for (var inv : invaders) {
                    if (inv.active && (target == null || inv.y > target.y)) {
                        target = inv;
                    }
                }
                if (target != null) {
                    newTargetX = target.x + rackX + 3 + (random.nextInt(5) - 2);
                }
            }
            aiTargetX = newTargetX;
        }
        final var targetXScaled = aiTargetX * 100;
        if (playerXScaled < targetXScaled) {
            playerXScaled += Math.min(PLAYER_SPEED_SCALED, targetXScaled - playerXScaled);
        } else if (playerXScaled > targetXScaled) {
            playerXScaled -= Math.min(PLAYER_SPEED_SCALED, playerXScaled - targetXScaled);
        }
        playerXScaled = Math.max(800, Math.min((w - 8) * 100, playerXScaled));
    }

    /**
     * Updates invader rack movement.
     */
    private void updateInvaders(int w, int h) {
        int activeCount = 0, minX = 1000, maxX = -1000;
        for (var inv : invaders) {
            if (inv.active) {
                activeCount++;
                final var curX = inv.x + rackX;
                if (curX < minX) {
                    minX = curX;
                }
                if (curX + 7 > maxX) {
                    maxX = curX + 7;
                }
                if (inv.y + rackY + 5 >= h - 12) {
                    registerHit(true);
                    return;
                }
            }
        }
        if (activeCount == 0) {
            initLevel(w, h, false);
            return;
        }
        if (++moveTimer >= Math.max(2, activeCount / 6)) {
            if (rackDir > 0 && maxX >= w - 2) {
                rackDir = -2;
                rackY += 3;
            } else if (rackDir < 0 && minX <= 2) {
                rackDir = 2;
                rackY += 3;
            } else {
                rackX += rackDir;
            }
            moveTimer = 0;
        }
    }

    /**
     * Handles projectile movement and collision detection.
     */
    private void updateCombat(int w, int h) {
        final var px = playerXScaled / 100;
        final var saucerY = h / 6;
        if (playerShot == null) {
            playerShot = new Projectile(px, h - 10);
        } else {
            final var nx = playerShot.x;
            final var ny = playerShot.y - 4;
            if (saucerActive && nx >= saucerX && nx <= saucerX + 11 && ny >= saucerY && ny <= saucerY + 6) {
                score += 150;
                saucerActive = false;
                playerShot = null;
                explosions.add(new Explosion(saucerX + 2, saucerY));
            } else if (ny < 10 || checkBunkerCollision(w, h, nx, ny)) {
                playerShot = null;
            } else {
                playerShot = new Projectile(nx, ny);
                for (var inv : invaders) {
                    if (inv.active && nx >= inv.x + rackX && nx <= inv.x + rackX + 7 && ny >= inv.y + rackY && ny <= inv.y + rackY
                            + 5) {
                        inv.active = false;
                        score += 20;
                        playerShot = null;
                        explosions.add(new Explosion(inv.x + rackX, inv.y + rackY));
                        break;
                    }
                }
            }
        }
        if (random.nextInt(45) == 0 && alienMissiles.size() < 3) {
            final var activeOnes = invaders.stream().filter(i -> i.active).toList();
            if (!activeOnes.isEmpty()) {
                final var s = activeOnes.get(random.nextInt(activeOnes.size()));
                alienMissiles.add(new Projectile(s.x + rackX + 3, s.y + rackY + 6));
            }
        }
        for (var i = 0; i < alienMissiles.size(); i++) {
            final var m = alienMissiles.get(i);
            if (m == null) {
                continue;
            }
            final var ny = m.y + 2;
            if (ny > h || checkBunkerCollision(w, h, m.x, ny)) {
                alienMissiles.set(i, null);
            } else if (ny > h - 10 && Math.abs(m.x - px) < 5) {
                registerHit(false);
                return;
            } else {
                alienMissiles.set(i, new Projectile(m.x, ny));
            }
        }
        alienMissiles.removeIf(m -> m == null);
    }

    /**
     * Checks for collision with bunkers and updates bitmasks upon impact.
     */
    private boolean checkBunkerCollision(int w, int h, int x, int y) {
        final var bunkerYStart = h - 24;
        if (y < bunkerYStart || y > bunkerYStart + 5) {
            return false;
        }
        final var spacing = w / 3;
        for (var i = 0; i < 3; i++) {
            final var bx = (spacing / 2) + (i * spacing) - 4;
            if (x >= bx && x < bx + 7) {
                final var row = (y - bunkerYStart) / 2;
                if (row >= 0 && row < 3 && ((bunkers[i][row] >> (6 - (x - bx))) & 1) == 1) {
                    bunkers[i][row] &= ~(1 << (6 - (x - bx)));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates mystery saucer position and spawn timer.
     */
    private void updateSaucer(int w) {
        if (!saucerActive) {
            if (++saucerTimer > (400 + random.nextInt(800))) {
                saucerActive = true;
                saucerX = -20;
            }
        } else {
            saucerX += 1;
            if (saucerX > w) {
                saucerActive = false;
                saucerTimer = 0;
            }
        }
    }

    /**
     * Renders centered text with a cleared background rectangle.
     */
    private void drawCenteredText(Graphics2D g, String text, int w, int y, Color color) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        g.setColor(Color.BLACK);
        g.fillRect(x - 2, y - fm.getAscent(), fm.stringWidth(text) + 4, fm.getHeight());
        g.setColor(color);
        g.drawString(text, x, y);
    }

    /**
     * Main rendering loop.
     */
    private void render(Ssd1331 oled, BufferedImage img, Graphics2D g) {
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, oled.getWidth(), oled.getHeight());
        int w = oled.getWidth(), h = oled.getHeight(), px = playerXScaled / 100;

        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g.setColor(Color.WHITE);
        g.drawString(String.format("%04d", score), 2, 10);
        g.setColor(Color.GREEN);
        for (int i = 0; i < lives; i++) {
            g.fillRect(w - (i * 4) - 5, h - 3, 2, 2);
        }

        if (saucerActive) {
            g.setColor(Color.RED);
            for (int i = 0; i < 5; i++) {
                for (int b = 0; b < 12; b++) {
                    if (((SAUCER_BITS[i] >> (11 - b)) & 1) == 1) {
                        g.fillRect(saucerX + b, (h / 6) + i, 1, 1);
                    }
                }
            }
        }
        g.setColor(Color.CYAN);
        int spacing = w / 3;
        for (var i = 0; i < 3; i++) {
            int bx = (spacing / 2) + (i * spacing) - 4;
            for (var r = 0; r < 3; r++) {
                for (var c = 0; c < 7; c++) {
                    if (((bunkers[i][r] >> (6 - c)) & 1) == 1) {
                        g.fillRect(bx + c, (h - 24) + (r * 2), 1, 2);
                    }
                }
            }
        }
        for (var inv : invaders) {
            if (!inv.active) {
                continue;
            }
            g.setColor(inv.type == 0 ? Color.MAGENTA : (inv.type == 1 ? Color.CYAN : Color.GREEN));
            int[] bts = (inv.type == 0) ? new int[]{0x10, 0x38, 0x7C, 0x28} : (inv.type == 1)
                    ? new int[]{0x44, 0x38, 0x7C, 0x10} : new int[]{0x38, 0x7C, 0x7C, 0x44};
            for (int i = 0; i < 4; i++) {
                for (int b = 0; b < 7; b++) {
                    if (((bts[i] >> (6 - b)) & 1) == 1) {
                        g.fillRect(inv.x + rackX + b, inv.y + rackY + i, 1, 1);
                    }
                }
            }
        }
        for (var exp : explosions) {
            g.setColor(Color.ORANGE);
            for (int i = 0; i < 5; i++) {
                for (int b = 0; b < 8; b++) {
                    if (((EXPLOSION_BITS[i] >> (7 - b)) & 1) == 1) {
                        g.fillRect(exp.x + b, exp.y + i, 1, 1);
                    }
                }
            }
        }

        g.setColor(Color.WHITE);
        if (playerShot != null) {
            g.fillRect(playerShot.x, playerShot.y, 1, 3);
        }
        g.setColor(Color.RED);
        for (var m : alienMissiles) {
            g.fillRect(m.x, m.y, 1, 3);
        }

        if (gameState == State.EXPLODING) {
            g.setColor(Color.WHITE);
            for (int i = 0; i < 20; i++) {
                g.fillRect(px + random.nextInt(15) - 7, h - 5 + random.nextInt(10) - 5, 1, 1);
            }
        } else if (gameState == State.GAME_OVER) {
            drawCenteredText(g, "GAME OVER", w, h / 2, Color.RED);
        } else {
            g.setColor(Color.YELLOW);
            g.fillRect(px - 4, h - 5, 9, 4);
            g.fillRect(px - 1, h - 7, 3, 2);
        }

        oled.drawImage(img);
    }

    @Override
    public Integer call() throws Exception {
        try (final var oled = new Ssd1331(device, mode, speed, gpioDevice, dc, res)) {
            oled.setup();
            final var image = new BufferedImage(oled.getWidth(), oled.getHeight(), BufferedImage.TYPE_INT_RGB);
            final var g2d = image.createGraphics();
            initLevel(oled.getWidth(), oled.getHeight(), true);
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

    /**
     * Main entry point.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new Ssd1331SpaceInvaders()).execute(args));
    }
}
