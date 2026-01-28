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
 * Combat simulation featuring an AI-controlled turret defending against a realistic tank.
 * <p>
 * This class simulates ballistic trajectories with a mechanical barrel that articulates 
 * based on the firing solution. The AI incorporates "human" error and random reload 
 * times to prevent perfect accuracy. The HUD is minimalist, mirroring retro arcade 
 * aesthetics.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.6.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Artillery", mixinStandardHelpOptions = true, version = "1.6.0",
        description = "AI Artillery vs Realistic Tank with randomized firing loops")
public class Artillery extends Base {

    /**
     * Picocli command specification for CLI parsing.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Random generator for AI jitter, reload timing, and tank spawning.
     */
    private final Random random = new Random();

    /**
     * Earth's gravity constant in $m/s^2$.
     */
    private static final double GRAVITY = 9.81;

    /**
     * Minimalist Turret Base (5x4).
     * 1: Steel Grey, 2: Deep Shadow.
     */
    private final int[][] TURRET_SPRITE = {
        {0, 1, 1, 1, 0},
        {1, 1, 1, 1, 1},
        {1, 2, 2, 2, 1},
        {1, 1, 1, 1, 1}
    };

    /**
     * Realistic Combat Tank Sprite (13x7).
     * 1: Olive Drab, 2: Dark Camo, 3: Black, 4: Highlight.
     */
    private final int[][] TANK_SPRITE = {
        {0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0},
        {3, 3, 3, 3, 1, 4, 1, 3, 3, 0, 0, 0, 0},
        {0, 3, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3},
        {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
        {0, 3, 0, 3, 0, 3, 0, 3, 0, 3, 0, 3, 0}
    };

    /**
     * Draws a pixel-mapped sprite to the G2D buffer.
     *
     * @param sprite  2D array representing pixel indices.
     * @param x       Horizontal screen offset.
     * @param y       Vertical screen offset.
     * @param palette Color array mapped to indices.
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
     * Main simulation loop for the Artillery combat.
     *
     * @param oled SSD1331 hardware device instance.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth();
        final var g2d = getG2d();

        final var turretPal = new Color[]{new Color(100, 100, 110), Color.BLACK};
        final var tankPal = new Color[]{new Color(65, 80, 35), new Color(30, 40, 20), Color.BLACK, new Color(110, 130, 70)};

        var score = 0;
        var lives = 3;
        var tankX = 80.0;
        final var tankY = 53;
        
        var velocity = 29.0;
        var angle = Math.toRadians(35); 
        var shellX = 0.0; var shellY = 0.0;
        var time = 0.0;
        
        var shellActive = false;
        var reloadTicks = 20;

        final var frameDelay = 1000 / getFps();

        while (lives > 0) {
            final var startTime = System.currentTimeMillis();

            // Clear buffer: Deep space black with a thin ground line
            g2d.setColor(Color.BLACK); g2d.fillRect(0, 0, width, 64);
            g2d.setColor(new Color(30, 30, 30)); g2d.fillRect(0, 60, width, 4);

            // Small HUD
            g2d.setColor(Color.WHITE);
            g2d.drawString(score + " | " + lives, 2, 8);

            // Calculate barrel articulation
            final var pivotX = 4;
            final var pivotY = 56;
            final var barrelLength = 7;
            final var endX = pivotX + (int)(Math.cos(angle) * barrelLength);
            final var endY = pivotY - (int)(Math.sin(angle) * barrelLength);
            
            // Draw articulating barrel
            g2d.setColor(Color.WHITE);
            g2d.drawLine(pivotX, pivotY, endX, endY);

            // Draw Turret and Tank
            drawSprite(TURRET_SPRITE, 2, 56, turretPal);
            drawSprite(TANK_SPRITE, (int) tankX, tankY, tankPal);

            if (shellActive) {
                // Ballistic Calculation
                shellX = (velocity * Math.cos(angle) * time) + endX;
                shellY = endY - (velocity * Math.sin(angle) * time - 0.5 * GRAVITY * time * time);

                g2d.setColor(Color.ORANGE);
                g2d.fillRect((int) shellX, (int) shellY, 1, 1);

                // Hit logic
                if (shellX >= tankX && shellX <= tankX + 13 && shellY >= tankY && shellY <= tankY + 7) {
                    triggerExplosion((int) tankX + 6, tankY + 3, oled);
                    score++;
                    tankX = 75 + random.nextInt(15);
                    shellActive = false;
                    reloadTicks = 10 + random.nextInt(40); // Random reload delay
                } 
                // Miss logic (Ground hit or off-screen)
                else if (shellY > 60 || shellX > width) {
                    final var jitter = (random.nextDouble() - 0.5) * 3.5; 
                    if (shellX < tankX) {
                        velocity += 1.2 + jitter;
                        angle += Math.toRadians(2.0 + jitter);
                    } else {
                        velocity -= 1.2 + jitter;
                        angle -= Math.toRadians(2.0 + jitter);
                    }
                    angle = Math.clamp(angle, Math.toRadians(15), Math.toRadians(80));
                    shellActive = false;
                    reloadTicks = 5 + random.nextInt(25);
                }
                time += 0.22;
            } else {
                if (reloadTicks > 0) {
                    reloadTicks--;
                } else {
                    shellActive = true;
                    time = 0;
                }
            }

            // Tank progression
            tankX -= 0.16;
            if (tankX < 10) {
                lives--;
                tankX = 80;
                flashScreen(oled);
            }

            oled.drawImage(getImage());

            final var diff = System.currentTimeMillis() - startTime;
            if (diff < frameDelay) {
                try { TimeUnit.MILLISECONDS.sleep(frameDelay - diff); } catch (Exception ignored) {}
            }
        }
        log.info("Simulation Terminated. Score: {}", score);
    }

    /**
     * Visual effect for shell detonation.
     *
     * @param x    Center X.
     * @param y    Center Y.
     * @param oled Device instance for immediate feedback.
     */
    private void triggerExplosion(final int x, final int y, final Ssd1331 oled) {
        final var g2d = getG2d();
        for (var i = 1; i < 12; i += 2) {
            g2d.setColor(i % 3 == 0 ? Color.WHITE : Color.RED);
            g2d.drawRect(x - i / 2, y - i / 2, i, i);
            oled.drawImage(getImage());
            try { TimeUnit.MILLISECONDS.sleep(15); } catch (Exception ignored) {}
        }
    }

    /**
     * Full-screen flash effect for base damage.
     *
     * @param oled Hardware driver.
     */
    private void flashScreen(final Ssd1331 oled) {
        final var g2d = getG2d();
        g2d.setColor(new Color(150, 0, 0));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        oled.drawImage(getImage());
        try { TimeUnit.MILLISECONDS.sleep(250); } catch (Exception ignored) {}
    }

    /**
     * Entry point for the Picocli command.
     *
     * @return Execution status.
     * @throws Exception Hardware errors.
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
     * Application main.
     *
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Artillery()).execute(args));
    }
}
