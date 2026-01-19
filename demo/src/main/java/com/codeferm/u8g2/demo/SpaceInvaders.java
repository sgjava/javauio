/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.FontType;
import com.codeferm.u8g2.U8g2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Space Invaders demo with AI-driven player and resolution-aware scaling.
 * <p>
 * This class calculates invader grid density and entity positioning relative to the display resolution (e.g., 128x64, 256x128,
 * 128x32).
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "SpaceInvaders", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Space Invaders - Dynamic Resolution Demo")
public class SpaceInvaders extends Base {

    /**
     * Target frames per second.
     */
    @Option(names = {"-f", "--fps"}, description = "Frames per second", defaultValue = "60")
    private int fps;
    /**
     * Random number generator for game logic.
     */
    private final Random random = new Random();
    /**
     * Control flag for game loop.
     */
    private boolean running = true;

    /**
     * Internal game states.
     */
    public enum State {
        PLAYING, EXPLODING, GAME_OVER
    }
    /**
     * Current state of the game.
     */
    private State gameState = State.PLAYING;
    /**
     * Current player score.
     */
    private int score = 0;
    /**
     * Number of remaining player lives.
     */
    private int lives = 3;
    /**
     * Countdown for explosion animation.
     */
    private int playerExplosionTimer = 0;
    /**
     * Prevents multiple hits in a single update cycle.
     */
    private boolean hitLock = false;
    /**
     * Player X position scaled by 100 for sub-pixel precision.
     */
    private int playerXScaled;
    /**
     * Scaled speed of player movement.
     */
    private static final int PLAYER_SPEED_SCALED = 200;
    /**
     * Countdown until AI recalculates target.
     */
    private int aiDecisionTimer = 0;
    /**
     * Target X coordinate for the AI player.
     */
    private int aiTargetX = 0;
    /**
     * Active player projectile.
     */
    private Projectile playerShot = null;
    /**
     * Active missiles fired by invaders.
     */
    private final List<Projectile> alienMissiles = new ArrayList<>();
    /**
     * List of all active and inactive invaders.
     */
    private final List<Invader> invaders = new ArrayList<>();
    /**
     * Visual explosion effects currently on screen.
     */
    private final List<Explosion> explosions = new ArrayList<>();
    /**
     * Horizontal position of the invader rack.
     */
    private int rackX;
    /**
     * Vertical position of the invader rack.
     */
    private int rackY;
    /**
     * Current movement direction and speed of the rack.
     */
    private int rackDir = 2;
    /**
     * Counter to pace invader movement based on active count.
     */
    private int moveTimer = 0;
    /**
     * Bitmasks for the 3 defensive bunkers.
     */
    private final int[][] bunkers = new int[3][3];
    /**
     * Horizontal position of the mystery saucer.
     */
    private int saucerX = -20;
    /**
     * Countdown until next saucer appearance.
     */
    private int saucerTimer = 0;
    /**
     * Flag indicating if a saucer is currently active.
     */
    private boolean saucerActive = false;

    /**
     * Record representing coordinates of a projectile.
     */
    public static record Projectile(int x, int y) {

    }

    /**
     * Class representing an individual invader.
     */
    public static class Invader {

        public int x, y, type;
        public boolean active;

        /**
         * Invader constructor.
         *
         * @param x X offset within the rack.
         * @param y Y offset within the rack.
         * @param type Visual type (0-2).
         * @param active Initial status.
         */
        public Invader(final int x, final int y, final int type, final boolean active) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.active = active;
        }
    }

    /**
     * Class representing a visual explosion effect.
     */
    public static class Explosion {

        public int x, y, timer;

        /**
         * Explosion constructor.
         *
         * @param x X coordinate.
         * @param y Y coordinate.
         */
        public Explosion(final int x, final int y) {
            this.x = x;
            this.y = y;
            this.timer = 6;
        }
    }

    /**
     * Bitmask for saucer graphic.
     */
    private static final int[] SAUCER_BITS = {0x0F0, 0x3FC, 0x7FE, 0xAA8, 0x444};

    /**
     * Bitmask for explosion graphic.
     */
    private static final int[] EXPLOSION_BITS = {0x24, 0x50, 0x18, 0x50, 0x24};

    /**
     * Initializes level, resets entities, and calculates dynamic grid layout.
     *
     * @param fullReset True to reset score and lives.
     */
    public void initLevel(final boolean fullReset) {
        if (fullReset) {
            lives = 3;
            score = 0;
        }
        final var w = getWidth();
        final var h = getHeight();

        playerXScaled = (w / 2) * 100;
        aiTargetX = w / 2;
        playerShot = null;
        alienMissiles.clear();
        invaders.clear();
        explosions.clear();
        hitLock = false;
        saucerActive = false;
        saucerTimer = 0;
        // Dynamic Grid: fill ~70% of width and ~40% of height
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
     * Handles player hit logic and game over transitions.
     *
     * @param isLanding True if an invader reached the bottom.
     */
    private void registerHit(final boolean isLanding) {
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
     * Updates game state including AI, movement, and collision detection.
     */
    public void update() {
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
            final var it = explosions.iterator();
            while (it.hasNext()) {
                if (--it.next().timer <= 0) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Updates saucer appearance and movement.
     */
    private void updateSaucer() {
        if (!saucerActive) {
            if (++saucerTimer > (400 + random.nextInt(800))) {
                saucerActive = true;
                saucerX = -20;
            }
        } else {
            saucerX += 1;
            if (saucerX > getWidth()) {
                saucerActive = false;
                saucerTimer = 0;
            }
        }
    }

    /**
     * AI logic for tracking invaders and dodging missiles.
     */
    private void updateAI() {
        final var h = getHeight();
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
        playerXScaled = Math.max(800, Math.min((getWidth() - 8) * 100, playerXScaled));
    }

    /**
     * Updates invader rack movement and landing condition checks.
     */
    private void updateInvaders() {
        var activeCount = 0;
        var minX = 1000;
        var maxX = -1000;
        final var w = getWidth();
        final var h = getHeight();

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
            initLevel(false);
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
     * Handles projectile firing and collision detection for all entities.
     */
    private void updateCombat() {
        final var h = getHeight();
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
            } else if (ny < 10 || checkBunkerCollision(nx, ny)) {
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
            if (ny > h || checkBunkerCollision(m.x, ny)) {
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
     * Checks if a point hits a bunker and updates the bitmask.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return True if a bunker was hit.
     */
    private boolean checkBunkerCollision(final int x, final int y) {
        final var h = getHeight();
        final var w = getWidth();
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
     * Renders UI and game entities to the U8g2 buffer.
     */
    public void render() {
        final var u8g2 = getU8g2();
        U8g2.clearBuffer(u8g2);
        final var w = getWidth();
        final var h = getHeight();
        U8g2.setFont(u8g2, getDisplay().getFontPtr(FontType.FONT_5X7_TF));
        U8g2.drawStr(u8g2, 1, 7, "S:" + score);
        U8g2.drawStr(u8g2, w - 25, 7, "P:" + lives);
        final var px = playerXScaled / 100;
        if (gameState == State.EXPLODING) {
            for (var i = 0; i < 20; i++) {
                U8g2.drawPixel(u8g2, px + random.nextInt(15) - 7, h - 5 + random.nextInt(10) - 5);
            }
        } else if (gameState != State.GAME_OVER) {
            U8g2.drawBox(u8g2, px - 4, h - 5, 9, 4);
            U8g2.drawBox(u8g2, px - 1, h - 7, 3, 2);
        }
        renderWorld(u8g2);
        if (gameState == State.GAME_OVER) {
            U8g2.setFont(u8g2, getDisplay().getFontPtr(FontType.FONT_6X12_TF));
            final var msg = "GAME OVER";
            final var tw = U8g2.getStrWidth(u8g2, msg);
            final var tx = (w - tw) / 2;
            final var ty = (h / 2) + 4;
            U8g2.setDrawColor(u8g2, 0);
            U8g2.drawBox(u8g2, tx - 2, ty - 10, tw + 4, 14);
            U8g2.setDrawColor(u8g2, 1);
            U8g2.drawStr(u8g2, tx, ty, msg);
        }
        U8g2.sendBuffer(u8g2);
    }

    /**
     * Renders game entities (saucer, bunkers, invaders, effects).
     *
     * @param u8g2 Native buffer handle.
     */
    private void renderWorld(final long u8g2) {
        final var h = getHeight();
        final var w = getWidth();
        if (saucerActive) {
            final int sy = h / 6;
            for (var i = 0; i < 5; i++) {
                for (var b = 0; b < 12; b++) {
                    if (((SAUCER_BITS[i] >> (11 - b)) & 1) == 1) {
                        U8g2.drawPixel(u8g2, saucerX + b, sy + i);
                    }
                }
            }
        }
        final int bunkerYStart = h - 24;
        final int spacing = w / 3;
        for (var i = 0; i < 3; i++) {
            final var bx = (spacing / 2) + (i * spacing) - 4;
            for (var r = 0; r < 3; r++) {
                for (var c = 0; c < 7; c++) {
                    if (((bunkers[i][r] >> (6 - c)) & 1) == 1) {
                        U8g2.drawBox(u8g2, bx + c, bunkerYStart + (r * 2), 1, 2);
                    }
                }
            }
        }
        for (var inv : invaders) {
            if (inv.active) {
                final int[] bts = (inv.type == 0) ? new int[]{0x10, 0x38, 0x7C, 0x28} : (inv.type == 1)
                        ? new int[]{0x44, 0x38, 0x7C, 0x10} : new int[]{0x38, 0x7C, 0x7C, 0x44};
                for (var i = 0; i < 4; i++) {
                    for (var b = 0; b < 7; b++) {
                        if (((bts[i] >> (6 - b)) & 1) == 1) {
                            U8g2.drawPixel(u8g2, inv.x + rackX + b, inv.y + rackY + i);
                        }
                    }
                }
            }
        }
        for (var exp : explosions) {
            for (var i = 0; i < 5; i++) {
                for (var b = 0; b < 8; b++) {
                    if (((EXPLOSION_BITS[i] >> (7 - b)) & 1) == 1) {
                        U8g2.drawPixel(u8g2, exp.x + b, exp.y + i);
                    }
                }
            }
        }
        if (playerShot != null) {
            U8g2.drawVLine(u8g2, playerShot.x, playerShot.y, 3);
        }
        for (var m : alienMissiles) {
            U8g2.drawVLine(u8g2, m.x, m.y, 3);
        }
    }

    /**
     * Executes the primary game loop.
     *
     * @return Exit code.
     * @throws InterruptedException On sleep failure.
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
     * Main entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new SpaceInvaders())
                .registerConverter(Integer.class, Integer::decode)
                .registerConverter(Integer.TYPE, Integer::decode)
                .execute(args));
    }
}
