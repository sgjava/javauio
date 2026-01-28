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
 * High-fidelity Artillery Combat simulation for SSD1331.
 * <p>
 * This demo utilizes bitmapped sprites and projectile motion physics. It calculates 
 * the shell trajectory using:
 * $$y = x \tan(\theta) - \frac{g x^2}{2 v^2 \cos^2(\theta)}$$
 * Features include collision detection, procedural explosions, and a moving target.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.1.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Artillery", mixinStandardHelpOptions = true, version = "1.1.0",
        description = "Sprite-based ballistic combat demo")
public class Artillery extends Base {

    /**
     * Picocli command spec for CLI argument handling.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Random generator for target repositioning.
     */
    private final Random random = new Random();

    /**
     * Artillery Cannon Sprite (10x6).
     * 1: Steel Grey, 2: Light Chrome, 3: Black, 4: Tread Brown.
     */
    private final int[][] CANNON_SPRITE = {
        {0, 0, 0, 0, 3, 3, 3, 3, 3, 3},
        {0, 0, 3, 3, 2, 2, 2, 2, 2, 0},
        {3, 3, 1, 1, 1, 1, 1, 3, 3, 0},
        {3, 1, 1, 1, 1, 1, 1, 1, 3, 0},
        {3, 4, 4, 4, 4, 4, 4, 4, 3, 0},
        {0, 3, 3, 3, 3, 3, 3, 3, 0, 0}
    };

    /**
     * Armored Tank Target Sprite (12x7).
     * 1: Olive, 2: Forest Green, 3: Black, 4: Tread Dark.
     */
    private final int[][] TANK_SPRITE = {
        {0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0},
        {0, 0, 3, 3, 1, 1, 1, 3, 3, 0, 0, 0},
        {3, 3, 1, 1, 1, 1, 1, 1, 1, 3, 3, 0},
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 0},
        {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3},
        {3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3},
        {0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0}
    };

    /**
     * Renders a bitmapped sprite to the graphics buffer.
     *
     * @param sprite  2D pixel map.
     * @param x       Top-left X coordinate.
     * @param y       Top-left Y coordinate.
     * @param palette Array of Color objects mapped to sprite indices.
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
     * Main animation and physics loop.
     *
     * @param oled SSD1331 hardware instance.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth();
        final var height = getHeight();
        final var g2d = getG2d();

        // Palettes
        final var cannonPalette = new Color[]{new Color(80, 80, 85), new Color(180, 180, 190), Color.BLACK, new Color(60, 40, 30)};
        final var tankPalette = new Color[]{new Color(107, 142, 35), new Color(34, 139, 34), Color.BLACK, new Color(40, 40, 40)};

        var shellX = -1.0; var shellY = -1.0;
        var time = 0.0;
        final var gravity = 9.81;
        var velocity = 36.0;
        var angle = Math.toRadians(50);
        
        var targetX = 65;
        final var targetY = 51;
        final var frameDelay = 1000 / getFps();

        log.info("Starting Artillery Combat Simulation");

        while (true) {
            final var startTime = System.currentTimeMillis();

            // Sky and Ground
            g2d.setColor(new Color(20, 24, 40)); g2d.fillRect(0, 0, width, 58); // Night sky
            g2d.setColor(new Color(20, 20, 15)); g2d.fillRect(0, 58, width, 6);  // Dark dirt

            // Draw Units
            drawSprite(CANNON_SPRITE, 2, 52, cannonPalette);
            drawSprite(TANK_SPRITE, targetX, targetY, tankPalette);

            // Ballistics logic
            if (shellY < 64) {
                shellX = (velocity * Math.cos(angle) * time) + 10;
                shellY = 52 - (velocity * Math.sin(angle) * time - 0.5 * gravity * time * time);

                // Draw Shell with glowing tracer
                g2d.setColor(Color.YELLOW);
                g2d.fillRect((int) shellX, (int) shellY, 2, 2);

                // Collision detection
                if (shellX >= targetX && shellX <= targetX + 12 && shellY >= targetY && shellY <= targetY + 7) {
                    triggerExplosion(targetX + 6, targetY + 3, oled);
                    targetX = 30 + random.nextInt(50); // Move tank
                    time = 0; shellY = 100; // Reset shell
                }
                time += 0.12;
            } else {
                // Reset shot and randomize power/angle for variety
                time = 0;
                velocity = 32.0 + random.nextDouble() * 10.0;
                angle = Math.toRadians(40 + random.nextInt(25));
            }

            oled.drawImage(getImage());

            final var diff = System.currentTimeMillis() - startTime;
            if (diff < frameDelay) {
                try { TimeUnit.MILLISECONDS.sleep(frameDelay - diff); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Renders a procedural expanding explosion at hit coordinates.
     *
     * @param x    Center X.
     * @param y    Center Y.
     * @param oled Device to push intermediate frames.
     */
    private void triggerExplosion(final int x, final int y, final Ssd1331 oled) {
        final var g2d = getG2d();
        for (var i = 1; i < 12; i++) {
            g2d.setColor(i % 2 == 0 ? Color.ORANGE : Color.WHITE);
            g2d.drawOval(x - i / 2, y - i / 2, i, i);
            oled.drawImage(getImage());
            try { TimeUnit.MILLISECONDS.sleep(15); } catch (Exception ignored) {}
        }
    }

    /**
     * Entry point for CLI.
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

    public static void main(final String... args) {
        System.exit(new CommandLine(new Artillery()).execute(args));
    }
}
