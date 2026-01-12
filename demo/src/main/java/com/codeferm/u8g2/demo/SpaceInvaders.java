/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.FontType;
import com.codeferm.u8g2.U8g2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Space Invaders. Includes AI dodging, dynamic difficulty scaling, and classic visual effects.
 */
@Command(name = "SpaceInvaders", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Space Invaders - Final Worthy Demo with AI and polished FX")
public class SpaceInvaders extends Base {

    @Option(names = {"-f", "--fps"}, description = "Frames per second", defaultValue = "60")
    private int fps;
    private final Random random = new Random();
    private boolean running = true;

    private enum State {
        PLAYING, EXPLODING, GAME_OVER
    }
    private State gameState = State.PLAYING;
    private int score = 0;
    private int lives = 3;
    private int playerExplosionTimer = 0;
    private boolean hitLock = false;
    // Movement: Fixed-point scaling (x100) for sub-pixel AI precision
    private int playerXScaled;
    private final int PLAYER_SPEED_SCALED = 120;
    private Projectile playerShot = null;
    private final List<Projectile> alienMissiles = new ArrayList<>();
    private final List<Invader> invaders = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private int rackX = 10, rackY = 10, rackDir = 2, moveTimer = 0;
    private final int[][] bunkers = new int[3][3];
    private int saucerX = -20, saucerTimer = 0;
    private boolean saucerActive = false;

    // Simple records and classes for entity management
    private static record Projectile(int x, int y) {

    }

    private static class Invader {

        int x, y, type;
        boolean active;

        Invader(int x, int y, int type, boolean active) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.active = active;
        }
    }

    private static class Explosion {

        int x, y, timer;

        Explosion(int x, int y) {
            this.x = x;
            this.y = y;
            this.timer = 6;
        }
    }

    // Sprite Bitmasks
    private static final int[] SAUCER_BITS = {0x0F0, 0x3FC, 0x7FE, 0xAA8, 0x444};
    private static final int[] EXPLOSION_BITS = {0x24, 0x50, 0x18, 0x50, 0x24};

    /**
     * Initializes levels, resets bunkers, and populates the invader grid.
     */
    private void initLevel(final boolean fullReset) {
        if (fullReset) {
            lives = 3;
            score = 0;
        }
        playerXScaled = (getWidth() / 2) * 100;
        playerShot = null;
        alienMissiles.clear();
        invaders.clear();
        explosions.clear();
        hitLock = false;
        saucerActive = false;
        saucerTimer = 0;
        rackX = 10;
        rackY = 10;
        rackDir = 2;

        // Populate 55 invaders
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 11; col++) {
                invaders.add(new Invader(col * 9, row * 7, (row == 0 ? 0 : (row < 3 ? 1 : 2)), true));
            }
        }

        // Initialize Low-Profile Bunkers (3 rows high)
        // Shape: Convex top, solid middle, concave bottom
        for (int i = 0; i < 3; i++) {
            bunkers[i][0] = 0x3E;
            bunkers[i][1] = 0x7F;
            bunkers[i][2] = 0x63;
        }
    }

    private void registerHit(final boolean isLanding) {
        if (!hitLock) {
            hitLock = true;
            if (isLanding) {
                lives = 0;
            } else {
                lives--;
            }
            playerExplosionTimer = 90;
            gameState = State.EXPLODING;
            playerShot = null;
            alienMissiles.clear();
        }
    }

    private void update() {
        if (gameState == State.EXPLODING) {
            if (--playerExplosionTimer <= 0) {
                if (lives > 0) {
                    initLevel(false);
                    gameState = State.PLAYING;
                } else {
                    gameState = State.GAME_OVER;
                }
            }
            return;
        }
        if (gameState == State.PLAYING) {
            updateAI();
            updateSaucer();
            updateInvaders();
            updateCombat();

            // Handle invader explosion lifecycles
            final var it = explosions.iterator();
            while (it.hasNext()) {
                if (--it.next().timer <= 0) {
                    it.remove();
                }
            }
        }
    }

    private void updateSaucer() {
        if (!saucerActive) {
            // Random spawn window (approx 5-15 seconds)
            if (++saucerTimer > (300 + random.nextInt(600))) {
                saucerActive = true;
                saucerX = -20;
            }
        } else {
            saucerX += 1;
            final var px = playerXScaled / 100;
            // Increased UFO aggression
            if (random.nextInt(30) == 0 && Math.abs(saucerX - px) < 15) {
                alienMissiles.add(new Projectile(saucerX + 6, 18));
            }
            if (saucerX > getWidth()) {
                saucerActive = false;
                saucerTimer = 0;
            }
        }
    }

    private void updateAI() {
        final var currentX = playerXScaled / 100;
        var targetX = currentX;
        Projectile threat = null;

        // Predictive Dodging
        for (final var m : alienMissiles) {
            if (Math.abs(m.x - currentX) < 10 && m.y > getHeight() - 32) {
                if (threat == null || m.y > threat.y) {
                    threat = m;
                }
            }
        }

        if (threat != null) {
            targetX = (threat.x < currentX) ? currentX + 22 : currentX - 22;
        } else {
            // Targeting logic: track the lowest active invader
            var lowestY = -1;
            for (final var inv : invaders) {
                if (inv.active && inv.y > lowestY) {
                    lowestY = inv.y;
                }
            }

            Invader best = null;
            for (final var inv : invaders) {
                if (inv.active && inv.y == lowestY) {
                    if (best == null || Math.abs(inv.x + rackX - currentX) < Math.abs(best.x + rackX - currentX)) {
                        best = inv;
                    }
                }
            }
            if (best != null) {
                targetX = best.x + rackX + 3;
            }
        }

        final var targetXScaled = targetX * 100;
        if (playerXScaled < targetXScaled) {
            playerXScaled += Math.min(PLAYER_SPEED_SCALED, targetXScaled - playerXScaled);
        } else if (playerXScaled > targetXScaled) {
            playerXScaled -= Math.min(PLAYER_SPEED_SCALED, playerXScaled - targetXScaled);
        }

        final var minX = 8 * 100;
        final var maxX = (getWidth() - 8) * 100;
        if (playerXScaled < minX) {
            playerXScaled = minX;
        }
        if (playerXScaled > maxX) {
            playerXScaled = maxX;
        }
    }

    private void updateInvaders() {
        var activeCount = 0;
        var minX = 1000;
        var maxX = -1000;
        for (final var inv : invaders) {
            if (inv.active) {
                activeCount++;
                final var curX = inv.x + rackX;
                if (curX < minX) {
                    minX = curX;
                }
                if (curX + 7 > maxX) {
                    maxX = curX + 7;
                }
                if (inv.y + rackY + 5 >= getHeight() - 12) {
                    registerHit(true);
                    return;
                }
            }
        }
        if (activeCount == 0) {
            initLevel(false);
            return;
        }

        // Dynamic speed: move faster as fewer invaders remain
        final var threshold = Math.max(2, activeCount / 6);
        if (++moveTimer >= threshold) {
            if (rackDir > 0 && maxX >= getWidth() - 2) {
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

    private void updateCombat() {
        final var px = playerXScaled / 100;
        if (playerShot == null) {
            playerShot = new Projectile(px, getHeight() - 10);
        } else {
            final var nx = playerShot.x;
            final var ny = playerShot.y - 4;
            if (ny < 12 || checkBunkerCollision(nx, ny)) {
                playerShot = null;
            } else {
                playerShot = new Projectile(nx, ny);
                for (final var inv : invaders) {
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

        // Alien Missile Spawning (Max 3 projectiles)
        if (random.nextInt(45) == 0 && alienMissiles.size() < 3) {
            Invader shooter = null;
            for (final var inv : invaders) {
                if (inv.active && Math.abs((inv.x + rackX) - px) < 15) {
                    shooter = inv;
                    break;
                }
            }
            if (shooter == null) {
                final var activeOnes = invaders.stream().filter(i -> i.active).toList();
                if (!activeOnes.isEmpty()) {
                    shooter = activeOnes.get(random.nextInt(activeOnes.size()));
                }
            }
            if (shooter != null) {
                alienMissiles.add(new Projectile(shooter.x + rackX + 3, shooter.y + rackY + 6));
            }
        }

        // Missile movement and collision
        for (int i = 0; i < alienMissiles.size(); i++) {
            final var m = alienMissiles.get(i);
            if (m == null) {
                continue;
            }
            final var ny = m.y + 2;
            if (ny > getHeight() || checkBunkerCollision(m.x, ny)) {
                alienMissiles.set(i, null);
            } else if (ny > getHeight() - 10 && Math.abs(m.x - px) < 5) {
                registerHit(false);
                return;
            } else {
                alienMissiles.set(i, new Projectile(m.x, ny));
            }
        }
        alienMissiles.removeIf(m -> m == null);
    }

    private boolean checkBunkerCollision(final int x, final int y) {
        if (y < 48 || y > 53) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            final var bx = 25 + (i * 35);
            if (x >= bx && x < bx + 7) {
                final var row = (y - 48) / 2;
                if (row >= 0 && row < 3 && ((bunkers[i][row] >> (6 - (x - bx))) & 1) == 1) {
                    bunkers[i][row] &= ~(1 << (6 - (x - bx)));
                    return true;
                }
            }
        }
        return false;
    }

    public void render() {
        final var u8g2 = getU8g2();
        U8g2.clearBuffer(u8g2);
        U8g2.setFont(u8g2, getDisplay().getFontPtr(FontType.FONT_5X7_TF));
        U8g2.drawStr(u8g2, 1, 7, "S:" + score);
        U8g2.drawStr(u8g2, 105, 7, "P:" + lives);

        final var px = playerXScaled / 100;
        if (gameState == State.EXPLODING) {
            for (int i = 0; i < 20; i++) {
                U8g2.drawPixel(u8g2, px + random.nextInt(15) - 7, getHeight() - 5 + random.nextInt(10) - 5);
            }
        } else if (gameState != State.GAME_OVER) {
            U8g2.drawBox(u8g2, px - 4, getHeight() - 5, 9, 4);
            U8g2.drawBox(u8g2, px - 1, getHeight() - 7, 3, 2);
        }

        renderWorld(u8g2);
        if (gameState == State.GAME_OVER) {
            U8g2.setFont(u8g2, getDisplay().getFontPtr(FontType.FONT_6X12_TF));
            U8g2.drawStr(u8g2, (getWidth() - U8g2.getStrWidth(u8g2, "GAME OVER")) / 2, (getHeight() / 2) + 4, "GAME OVER");
        }
        U8g2.sendBuffer(u8g2);
    }

    private void renderWorld(final long u8g2) {
        if (saucerActive) {
            for (int i = 0; i < 5; i++) {
                for (int b = 0; b < 12; b++) {
                    if (((SAUCER_BITS[i] >> (11 - b)) & 1) == 1) {
                        U8g2.drawPixel(u8g2, saucerX + b, 12 + i);
                    }
                }
            }
        }
        // Bunkers (Low Profile)
        for (int i = 0; i < 3; i++) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 7; c++) {
                    if (((bunkers[i][r] >> (6 - c)) & 1) == 1) {
                        U8g2.drawBox(u8g2, 25 + (i * 35) + c, 48 + (r * 2), 1, 2);
                    }
                }
            }
        }
        // Invaders
        for (final var inv : invaders) {
            if (inv.active) {
                final var bits = (inv.type == 0) ? new int[]{0x10, 0x38, 0x7C, 0x28} : (inv.type == 1) ? new int[]{0x44, 0x38, 0x7C,
                    0x10} : new int[]{0x38, 0x7C, 0x7C, 0x44};
                for (int i = 0; i < 4; i++) {
                    for (int b = 0; b < 7; b++) {
                        if (((bits[i] >> (6 - b)) & 1) == 1) {
                            U8g2.drawPixel(u8g2, inv.x + rackX + b, inv.y + rackY + i);
                        }
                    }
                }
            }
        }
        // Invader pops
        for (final var exp : explosions) {
            for (int i = 0; i < 5; i++) {
                for (int b = 0; b < 8; b++) {
                    if (((EXPLOSION_BITS[i] >> (7 - b)) & 1) == 1) {
                        U8g2.drawPixel(u8g2, exp.x + b, exp.y + i);
                    }
                }
            }
        }

        if (playerShot != null) {
            U8g2.drawVLine(u8g2, playerShot.x, playerShot.y, 3);
        }
        for (final var m : alienMissiles) {
            U8g2.drawVLine(u8g2, m.x, m.y, 3);
        }
    }

    /**
     * Run demo.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        super.call();
        initLevel(true);
        while (running) {
            update();
            render();
            getDisplay().sleep(1000L / fps);
            if (gameState == State.GAME_OVER) {
                getDisplay().sleep(3000);
                running = false;
            }
        }
        done();
        return 0;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new SpaceInvaders())
                .registerConverter(Integer.class, Integer::decode)
                .registerConverter(Long.class, Long::decode)
                .execute(args));
    }
}
