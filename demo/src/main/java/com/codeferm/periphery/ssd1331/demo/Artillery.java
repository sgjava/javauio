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
 * AI-controlled Ballistic Combat simulation for SSD1331.
 * <p>
 * This class implements a closed-loop control system where a turret AI adjusts 
 * its firing velocity based on the landing position of previous shells relative 
 * to a moving tank. Physics are calculated using standard projectile motion:
 * </p>
 * $$x(t) = v_0 \cos(\theta) t$$
 * $$y(t) = v_0 \sin(\theta) t - \frac{1}{2} g t^2$$
 *
 * @author Steven P. Goldsmith
 * @version 1.3.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Artillery", mixinStandardHelpOptions = true, version = "1.3.0",
        description = "Turret AI vs Moving Tank with ballistic feedback loop")
public class Artillery extends Base {

    /**
     * Picocli command specification for CLI option parsing.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Random number generator for tank respawn logic.
     */
    private final Random random = new Random();

    /**
     * Fixed gravitational constant ($m/s^2$).
     */
    private static final double GRAVITY = 9.81;

    /**
     * Heavy Turret Sprite (9x8).
     * 1: Dark Steel, 2: Light Chrome, 3: Black.
     */
    private final int[][] TURRET_SPRITE = {
        {0, 0, 0, 3, 3, 3, 0, 0, 0},
        {0, 0, 3, 2, 2, 2, 3, 0, 0},
        {0, 3, 2, 2, 2, 2, 2, 3, 0},
        {3, 2, 2, 2, 2, 2, 2, 2, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 3, 3, 3, 3, 3, 3, 3, 3},
        {3, 3, 3, 3, 3, 3, 3, 3, 3}
    };

    /**
     * Combat Tank Sprite (12x6).
     * 1: Olive Drab, 2: Forest Camo, 3: Black.
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
     * Renders a multi-color bitmapped sprite to the back buffer.
     *
     * @param sprite  2D integer array defining the bitmap.
     * @param x       Screen X offset.
     * @param y       Screen Y offset.
     * @param palette Array of Colors mapped to sprite indices (1-indexed).
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
     * Executes the Artillery AI simulation.
     * <p>
     * Manages the state machine for shell flight, AI velocity adjustment, 
     * and tank movement. Uses a fixed 45-degree angle for optimal range efficiency.
     * </p>
     *
     * @param oled SSD1331 driver instance.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth();
        final var g2d = getG2d();

        final var turretPal = new Color[]{new Color(45, 45, 50), new Color(140, 145, 155), Color.BLACK};
        final var tankPal = new Color[]{new Color(75, 85, 45), new Color(40, 50, 25), Color.BLACK};

        // Tank initial state
        var tankX = 82.0;
        final var tankY = 52;
        final var tankSpeed = 0.12;

        // Ballistic state
        var velocity = 32.0; // Initial guess for AI
        final var angle = Math.toRadians(45);
        var shellX = 0.0; var shellY = 0.0;
        var time = 0.0;
        var shellActive = true;

        final var frameDelay = 1000 / getFps();
        log.info("Artillery AI initialized. Target engaged.");

        while (true) {
            final var startTime = System.currentTimeMillis();

            // Render Environment
            g2d.setColor(new Color(15, 20, 35)); g2d.fillRect(0, 0, width, 58); // Night
            g2d.setColor(new Color(25, 20, 15)); g2d.fillRect(0, 58, width, 6);  // Ground

            drawSprite(TURRET_SPRITE, 2, 50, turretPal);
            drawSprite(TANK_SPRITE, (int) tankX, tankY, tankPal);

            if (shellActive) {
                // Projectile Physics
                shellX = (velocity * Math.cos(angle) * time) + 9;
                shellY = 50 - (velocity * Math.sin(angle) * time - 0.5 * GRAVITY * time * time);

                // Draw Shell with tracer
                g2d.setColor(Color.WHITE);
                g2d.fillRect((int) shellX, (int) shellY, 2, 2);

                // Hit Detection
                if (shellX >= tankX && shellX <= tankX + 12 && shellY >= tankY && shellY <= tankY + 6) {
                    performExplosion((int) tankX + 6, tankY + 3, oled);
                    tankX = 75 + random.nextInt(15); // Respawn tank
                    shellActive = false; time = 0;
                } 
                // Miss Detection (Ground collision)
                else if (shellY > 58 || shellX > width) {
                    // AI FEEDBACK LOOP: Adjust velocity for next shot
                    if (shellX < tankX) {
                        velocity += 1.2; // Increase power if short
                    } else {
                        velocity -= 1.2; // Decrease power if long
                    }
                    shellActive = false; time = 0;
                }
                time += 0.18;
            } else {
                shellActive = true; // Reloading
            }

            // Move Tank toward base
            tankX -= tankSpeed;
            if (tankX < 11) {
                handleBaseLost(oled);
                tankX = 82; velocity = 32; // Reset simulation
            }

            oled.drawImage(getImage());

            final var diff = System.currentTimeMillis() - startTime;
            if (diff < frameDelay) {
                try { TimeUnit.MILLISECONDS.sleep(frameDelay - diff); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Triggers a procedural explosion effect on the display.
     *
     * @param x    Center X coordinate.
     * @param y    Center Y coordinate.
     * @param oled Hardware driver for real-time frame pushing.
     */
    private void performExplosion(final int x, final int y, final Ssd1331 oled) {
        final var g2d = getG2d();
        for (var i = 2; i < 18; i += 2) {
            g2d.setColor(i % 4 == 0 ? Color.YELLOW : Color.RED);
            g2d.drawOval(x - i / 2, y - i / 2, i, i);
            oled.drawImage(getImage());
            try { TimeUnit.MILLISECONDS.sleep(12); } catch (Exception ignored) {}
        }
    }

    /**
     * Visual sequence for when the tank reaches the turret.
     *
     * @param oled Hardware driver.
     */
    private void handleBaseLost(final Ssd1331 oled) {
        final var g2d = getG2d();
        g2d.setColor(new Color(150, 0, 0));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        oled.drawImage(getImage());
        try { TimeUnit.SECONDS.sleep(1); } catch (Exception ignored) {}
    }

    /**
     * Initializes hardware and starts the Artillery demo.
     *
     * @return Exit code.
     * @throws Exception Hardware initialization error.
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
     * Main entry point.
     *
     * @param args CLI arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Artillery()).execute(args));
    }
}
