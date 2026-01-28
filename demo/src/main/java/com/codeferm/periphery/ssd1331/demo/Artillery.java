/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.ssd1331.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.Color;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * AI-driven Artillery Combat simulation for SSD1331.
 * <p>
 * This class simulates a turret defending against an advancing tank. 
 * The turret features a dynamic barrel that adjusts its elevation ($\theta$) 
 * and muzzle velocity ($v$) based on a feedback loop. To simulate human-like 
 * error, the AI includes a randomized variance in its adjustments.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.4.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Artillery", mixinStandardHelpOptions = true, version = "1.4.0",
        description = "AI Artillery with dynamic barrel, scoring, and life system")
public class Artillery extends Base {

    /**
     * Picocli command specification for CLI option parsing.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Random number generator for AI error and target respawn positions.
     */
    private final Random random = new Random();

    /**
     * Gravitational constant in $m/s^2$.
     */
    private static final double GRAVITY = 9.81;
    
    /**
     * Turret Base Sprite (9x5).
     * 1: Dark Steel, 2: Highlight, 3: Black.
     */
    private final int[][] TURRET_BASE = {
        {0, 3, 3, 3, 3, 3, 3, 3, 0},
        {3, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 3, 3, 3, 3, 3, 3, 3, 3},
        {3, 3, 3, 3, 3, 3, 3, 3, 3}
    };

    /**
     * Tank Target Sprite (12x6).
     * 1: Olive, 2: Forest Camo, 3: Black.
     */
    private final int[][] TANK_SPRITE = {
        {0, 0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0},
        {3, 3, 1, 1, 1, 1, 3, 3, 0, 0, 0, 0},
        {3, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3},
        {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3},
        {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
        {0, 3, 0, 3, 0, 3, 0, 3, 0, 3, 0, 0}
    };

    /**
     * Renders a bitmapped sprite to the graphics buffer.
     *
     * @param sprite  2D pixel array defining the bitmap.
     * @param x       Top-left X coordinate.
     * @param y       Top-left Y coordinate.
     * @param palette Array of Colors mapped to sprite indices.
     */
    private void drawSprite(final int[][] sprite, final int x, final int y, final Color[] palette) {
        final var g2d = getG2d();
        for (var row = 0; row < sprite.length; row++) {
            for (var col = 0; col < sprite[row].length; col++) {
                final var colorIdx = sprite[row][col];
                if (colorIdx > 0) {
                    g2d.setColor(palette[colorIdx - 1]);
                    g2d.drawLine(x + col, y + row, x + col, y + row);
                }
            }
        }
    }

    /**
     * Executes the main simulation loop.
     * <p>
     * Handles the iterative AI ranging, 3D-to-2D projectile projection, 
     * and on-screen HUD rendering.
     * </p>
     *
     * @param oled SSD1331 hardware driver.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth();
        final var g2d = getG2d();

        final var turretPal = new Color[]{new Color(55, 55, 65), new Color(140, 140, 150), Color.BLACK};
        final var tankPal = new Color[]{new Color(85, 95, 55), new Color(45, 55, 35), Color.BLACK};

        var score = 0;
        var lives = 3;
        var tankX = 82.0;
        final var tankY = 52;
        
        var velocity = 30.0;
        var angle = Math.toRadians(35); 
        var shellX = 0.0; var shellY = 0.0;
        var time = 0.0;
        var shellActive = true;

        final var frameDelay = 1000 / getFps();

        while (lives > 0) {
            final var startTime = System.currentTimeMillis();

            // Render Environment
            g2d.setColor(new Color(12, 18, 28)); g2d.fillRect(0, 0, width, 58);
            g2d.setColor(new Color(22, 28, 22)); g2d.fillRect(0, 58, width, 6);

            // Render HUD
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.format("SCORE:%03d L:%d", score, lives), 2, 10);

            // Render Turret Base
            drawSprite(TURRET_BASE, 2, 53, turretPal);

            // Render Dynamic Barrel
            final var barrelLen = 9;
            final var pivotX = 6;
            final var pivotY = 54;
            final var endX = pivotX + (int)(Math.cos(angle) * barrelLen);
            final var endY = pivotY - (int)(Math.sin(angle) * barrelLen);
            g2d.setColor(new Color(110, 110, 120));
            g2d.drawLine(pivotX, pivotY, endX, endY);
            g2d.drawLine(pivotX, pivotY - 1, endX, endY - 1); 

            // Render Tank
            drawSprite(TANK_SPRITE, (int) tankX, tankY, tankPal);

            if (shellActive) {
                // Ballistic Equation
                shellX = (velocity * Math.cos(angle) * time) + endX;
                shellY = endY - (velocity * Math.sin(angle) * time - 0.5 * GRAVITY * time * time);

                g2d.setColor(Color.YELLOW);
                g2d.fillRect((int) shellX, (int) shellY, 2, 2);

                // Check for Hit
                if (shellX >= tankX && shellX <= tankX + 12 && shellY >= tankY && shellY <= tankY + 6) {
                    performExplosion((int) tankX + 6, tankY + 3, oled);
                    score += 10;
                    tankX = 75 + random.nextInt(15);
                    shellActive = false; time = 0;
                } 
                // Check for Miss (Ground or Out of Bounds)
                else if (shellY > 58 || shellX > width) {
                    // AI Adjustment with nerf (randomized error)
                    final var error = (random.nextDouble() - 0.5) * 1.8;
                    if (shellX < tankX) {
                        velocity += 1.4 + error;
                        angle += Math.toRadians(2.5 + error);
                    } else {
                        velocity -= 1.4 + error;
                        angle -= Math.toRadians(2.5 + error);
                    }
                    // Constrain barrel elevation
                    angle = Math.clamp(angle, Math.toRadians(15), Math.toRadians(75));
                    shellActive = false; time = 0;
                }
                time += 0.20;
            } else {
                shellActive = true; 
            }

            // Tank Movement logic
            tankX -= 0.18;
            if (tankX < 11) {
                lives--;
                triggerDamageEffect(oled);
                tankX = 82;
            }

            oled.drawImage(getImage());

            final var diff = System.currentTimeMillis() - startTime;
            if (diff < frameDelay) {
                try { TimeUnit.MILLISECONDS.sleep(frameDelay - diff); } catch (Exception ignored) {}
            }
        }
        log.info("Game Over. Score: {}", score);
    }

    /**
     * Renders a procedural expanding explosion at the specified coordinates.
     *
     * @param x    Center X position.
     * @param y    Center Y position.
     * @param oled Hardware driver for real-time frame buffer updates.
     */
    private void performExplosion(final int x, final int y, final Ssd1331 oled) {
        final var g2d = getG2d();
        for (var i = 2; i < 20; i += 2) {
            g2d.setColor(i % 4 == 0 ? Color.WHITE : Color.ORANGE);
            g2d.drawOval(x - i / 2, y - i / 2, i, i);
            oled.drawImage(getImage());
            try { TimeUnit.MILLISECONDS.sleep(12); } catch (Exception ignored) {}
        }
    }

    /**
     * Triggers a full-screen red flash when the base is breached.
     *
     * @param oled Hardware driver.
     */
    private void triggerDamageEffect(final Ssd1331 oled) {
        final var g2d = getG2d();
        g2d.setColor(new Color(180, 0, 0));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        oled.drawImage(getImage());
        try { TimeUnit.MILLISECONDS.sleep(400); } catch (Exception ignored) {}
    }

    /**
     * Initializes the demo and starts the simulation.
     *
     * @return Exit code.
     * @throws Exception If hardware communication fails.
     */
    @Override
    public Integer call() throws Exception {
        if (!spec.commandLine().getParseResult().hasMatchedOption("f")) {
            setFps(30);
        }
        super.call();
        runDemo(getOled());
        done();
        return 0;
    }

    /**
     * Application entry point.
     *
     * @param args CLI arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Artillery()).execute(args));
    }
}
