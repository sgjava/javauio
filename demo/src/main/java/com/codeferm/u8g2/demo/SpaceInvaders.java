/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.FontType;
import com.codeferm.u8g2.U8g2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Space Invaders where player is "AI" driven.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "SpaceInvaders", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Space Invaders - Corrected Jitter-Free Demo")
public class SpaceInvaders extends Base {

    @Option(names = {"-f", "--fps"}, description = "Frames per second", defaultValue = "60")
    public int fps;

    public final Random random = new Random();
    public boolean running = true;

    /**
     * Game state machine.
     */
    public enum State {
        PLAYING, EXPLODING, GAME_OVER
    }
    public State gameState = State.PLAYING;

    public int score = 0;
    public int lives = 3;
    public int playerExplosionTimer = 0;
    public boolean hitLock = false;

    // Fixed-point scaling (x100) for sub-pixel precision
    public int playerXScaled;
    public final int PLAYER_SPEED_SCALED = 200;

    // AI Balancing
    public int aiDecisionTimer = 0;
    public int aiTargetX = 0;
    public int aiErrorMargin = 0;

    public Projectile playerShot = null;
    public final List<Projectile> alienMissiles = new ArrayList<>();
    public final List<Invader> invaders = new ArrayList<>();
    public final List<Explosion> explosions = new ArrayList<>();

    public int rackX = 10, rackY = 10, rackDir = 2, moveTimer = 0;
    public final int[][] bunkers = new int[3][3];
    public int saucerX = -20, saucerTimer = 0;
    public boolean saucerActive = false;

    /**
     * Simple record for projectile coordinates.
     */
    public static record Projectile(int x, int y) {

    }

    /**
     * Represents an individual alien invader.
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
     * Visual effect for destroyed entities.
     */
    public static class Explosion {

        public int x, y, timer;

        public Explosion(int x, int y) {
            this.x = x;
            this.y = y;
            this.timer = 6;
        }
    }

    public static final int[] SAUCER_BITS = {0x0F0, 0x3FC, 0x7FE, 0xAA8, 0x444};
    public static final int[] EXPLOSION_BITS = {0x24, 0x50, 0x18, 0x50, 0x24};

    /**
     * Initializes levels, score, and invader grid.
     *
     * @param fullReset Resets lives and score if true.
     */
    public void initLevel(final boolean fullReset) {
        if (fullReset) {
            lives = 3;
            score = 0;
        }
        // Initialize player position in scaled space
        playerXScaled = (getWidth() / 2) * 100;
        aiTargetX = playerXScaled / 100;
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

        // Build 11x5 invader grid
        for (var row = 0; row < 5; row++) {
            for (var col = 0; col < 11; col++) {
                invaders.add(new Invader(col * 9, row * 7, (row == 0 ? 0 : (row < 3 ? 1 : 2)), true));
            }
        }
        // Reset 3 protective bunkers
        for (var i = 0; i < 3; i++) {
            bunkers[i][0] = 0x3E;
            bunkers[i][1] = 0x7F;
            bunkers[i][2] = 0x63;
        }
    }

    /**
     * Triggers player destruction and potential game over.
     *
     * @param isLanding If true, aliens reached the bottom.
     */
    public void registerHit(final boolean isLanding) {
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
     * Updates game logic, AI, and collision states.
     */
    public void update() {
        if (gameState == State.EXPLODING) {
            // Check if explosion animation is finished
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
            // Remove expired explosion effects
            var it = explosions.iterator();
            while (it.hasNext()) {
                if (--it.next().timer <= 0) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Controls UFO spawning and horizontal flight.
     */
    public void updateSaucer() {
        if (!saucerActive) {
            // Roll for saucer spawn chance
            if (++saucerTimer > (400 + random.nextInt(800))) {
                saucerActive = true;
                saucerX = -20;
            }
        } else {
            // Move saucer at constant 1px/frame
            saucerX += 1;
            // Get local integer x coordinate
            var px = playerXScaled / 100;
            // Drop missile if player is horizontally aligned
            if (random.nextInt(30) == 0 && Math.abs(saucerX - px) < 15) {
                alienMissiles.add(new Projectile(saucerX + 6, 18));
            }
            // Deactivate when off screen
            if (saucerX > getWidth()) {
                saucerActive = false;
                saucerTimer = 0;
            }
        }
    }

    /**
     * Balanced AI movement with reaction latency and target noise.
     */
    public void updateAI() {
        // Evaluate logic every 8 frames to simulate reaction time
        if (--aiDecisionTimer <= 0) {
            aiDecisionTimer = 8;
            // Get local integer x coordinate
            var currentX = playerXScaled / 100;
            var newTargetX = currentX;
            Projectile threat = null;

            // Find lowest/closest missile threat
            for (var m : alienMissiles) {
                if (Math.abs(m.x - currentX) < 12 && m.y > getHeight() - 35) {
                    if (threat == null || m.y > threat.y) {
                        threat = m;
                    }
                }
            }
            if (threat != null) {
                // Dodge by a randomized distance
                var dodgeDist = 18 + random.nextInt(8);
                newTargetX = (threat.x < currentX) ? currentX + dodgeDist : currentX - dodgeDist;
            } else {
                // Determine lowest row of active invaders
                var lowestY = -1;
                for (var inv : invaders) {
                    if (inv.active && inv.y > lowestY) {
                        lowestY = inv.y;
                    }
                }

                Invader best = null;
                for (var inv : invaders) {
                    if (inv.active && inv.y == lowestY) {
                        if (best == null || Math.abs(inv.x + rackX - currentX) < Math.abs(best.x + rackX - currentX)) {
                            best = inv;
                        }
                    }
                }
                if (best != null) {
                    // Apply a target jitter
                    aiErrorMargin = random.nextInt(5) - 2;
                    newTargetX = best.x + rackX + 3 + aiErrorMargin;
                }
            }
            aiTargetX = newTargetX;
        }
        // Apply high-precision movement toward the delayed AI target
        var targetXScaled = aiTargetX * 100;
        if (playerXScaled < targetXScaled) {
            playerXScaled += Math.min(PLAYER_SPEED_SCALED, targetXScaled - playerXScaled);
        } else if (playerXScaled > targetXScaled) {
            playerXScaled -= Math.min(PLAYER_SPEED_SCALED, playerXScaled - targetXScaled);
        }
        // Keep player in bounds
        playerXScaled = Math.max(800, Math.min((getWidth() - 8) * 100, playerXScaled));
    }

    /**
     * Updates the invader rack position and direction.
     */
    public void updateInvaders() {
        var activeCount = 0;
        var minX = 1000;
        var maxX = -1000;
        for (var inv : invaders) {
            if (inv.active) {
                activeCount++;
                var curX = inv.x + rackX;
                if (curX < minX) {
                    minX = curX;
                }
                if (curX + 7 > maxX) {
                    maxX = curX + 7;
                }
                // Check if aliens landed
                if (inv.y + rackY + 5 >= getHeight() - 12) {
                    registerHit(true);
                    return;
                }
            }
        }
        // Advance level if cleared
        if (activeCount == 0) {
            initLevel(false);
            return;
        }
        // Move speed increases as aliens are destroyed
        var threshold = Math.max(2, activeCount / 6);
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

    /**
     * Handles shooting logic and collision checks.
     */
    public void updateCombat() {
        // Get local integer x coordinate
        var px = playerXScaled / 100;
        if (playerShot == null) {
            playerShot = new Projectile(px, getHeight() - 10);
        } else {
            var nx = playerShot.x;
            var ny = playerShot.y - 4;
            // Check UFO hit
            if (saucerActive && nx >= saucerX && nx <= saucerX + 11 && ny >= 12 && ny <= 18) {
                score += 100 + (random.nextInt(3) * 50);
                saucerActive = false;
                playerShot = null;
                explosions.add(new Explosion(saucerX + 2, 12));
            } else if (ny < 12 || checkBunkerCollision(nx, ny)) {
                playerShot = null;
            } else {
                playerShot = new Projectile(nx, ny);
                // Check Invader hit
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
        // Random alien fire
        if (random.nextInt(45) == 0 && alienMissiles.size() < 3) {
            var activeOnes = invaders.stream().filter(i -> i.active).toList();
            if (!activeOnes.isEmpty()) {
                var s = activeOnes.get(random.nextInt(activeOnes.size()));
                alienMissiles.add(new Projectile(s.x + rackX + 3, s.y + rackY + 6));
            }
        }
        // Update alien missiles
        for (var i = 0; i < alienMissiles.size(); i++) {
            var m = alienMissiles.get(i);
            if (m == null) {
                continue;
            }
            var ny = m.y + 2;
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

    /**
     * Bitmask collision for destructible bunkers.
     *
     * @param x Screen X coordinate.
     * @param y Screen Y coordinate.
     * @return True if collision occurred.
     */
    public boolean checkBunkerCollision(int x, int y) {
        if (y < 48 || y > 53) {
            return false;
        }
        for (var i = 0; i < 3; i++) {
            var bx = 25 + (i * 35);
            if (x >= bx && x < bx + 7) {
                var row = (y - 48) / 2;
                if (row >= 0 && row < 3 && ((bunkers[i][row] >> (6 - (x - bx))) & 1) == 1) {
                    bunkers[i][row] &= ~(1 << (6 - (x - bx)));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Draws UI, world, and player.
     */
    public void render() {
        var u8g2 = getU8g2();
        U8g2.clearBuffer(u8g2);
        U8g2.setFont(u8g2, getDisplay().getFontPtr(FontType.FONT_5X7_TF));
        U8g2.drawStr(u8g2, 1, 7, "S:" + score);
        U8g2.drawStr(u8g2, 105, 7, "P:" + lives);

        var px = playerXScaled / 100;
        if (gameState == State.EXPLODING) {
            // Particle effect
            for (var i = 0; i < 20; i++) {
                U8g2.drawPixel(u8g2, px + random.nextInt(15) - 7, getHeight() - 5 + random.nextInt(10) - 5);
            }
        } else if (gameState != State.GAME_OVER) {
            // Draw player ship
            U8g2.drawBox(u8g2, px - 4, getHeight() - 5, 9, 4);
            U8g2.drawBox(u8g2, px - 1, getHeight() - 7, 3, 2);
        }
        renderWorld(u8g2);

        if (gameState == State.GAME_OVER) {
            var msg = "GAME OVER";
            U8g2.setFont(u8g2, getDisplay().getFontPtr(FontType.FONT_6X12_TF));
            var tw = U8g2.getStrWidth(u8g2, msg);
            var tx = (getWidth() - tw) / 2;
            var ty = (getHeight() / 2) + 4;

            // Clear text area background
            U8g2.setDrawColor(u8g2, 0);
            U8g2.drawBox(u8g2, tx - 2, ty - 10, tw + 4, 14);
            U8g2.setDrawColor(u8g2, 1);
            U8g2.drawStr(u8g2, tx, ty, msg);
        }
        U8g2.sendBuffer(u8g2);
    }

    /**
     * Renders environmental objects.
     *
     * @param u8g2 Pointer to the U8g2 context.
     */
    public void renderWorld(long u8g2) {
        if (saucerActive) {
            for (var i = 0; i < 5; i++) {
                for (var b = 0; b < 12; b++) {
                    if (((SAUCER_BITS[i] >> (11 - b)) & 1) == 1) {
                        U8g2.drawPixel(u8g2, saucerX + b, 12 + i);
                    }
                }
            }
        }
        // Render Bunkers
        for (var i = 0; i < 3; i++) {
            for (var r = 0; r < 3; r++) {
                for (var c = 0; c < 7; c++) {
                    if (((bunkers[i][r] >> (6 - c)) & 1) == 1) {
                        U8g2.drawBox(u8g2, 25 + (i * 35) + c, 48 + (r * 2), 1, 2);
                    }
                }
            }
        }
        // Render Invaders
        for (var inv : invaders) {
            if (inv.active) {
                int[] bts = (inv.type == 0) ? new int[]{0x10, 0x38, 0x7C, 0x28} : (inv.type == 1)
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
        // Render Explosions
        for (var exp : explosions) {
            for (var i = 0; i < 5; i++) {
                for (var b = 0; b < 8; b++) {
                    if (((EXPLOSION_BITS[i] >> (7 - b)) & 1) == 1) {
                        U8g2.drawPixel(u8g2, exp.x + b, exp.y + i);
                    }
                }
            }
        }
        // Render Projectiles
        if (playerShot != null) {
            U8g2.drawVLine(u8g2, playerShot.x, playerShot.y, 3);
        }
        for (var m : alienMissiles) {
            U8g2.drawVLine(u8g2, m.x, m.y, 3);
        }
    }

    /**
     * Executes the demo loop.
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
    public static void main(String... args) {
        System.exit(new CommandLine(new SpaceInvaders()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
