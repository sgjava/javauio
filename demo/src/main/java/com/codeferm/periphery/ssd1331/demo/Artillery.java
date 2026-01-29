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
 * Artillery Duel featuring a fast-advancing tank and bracketing AI.
 * <p>
 * This version increases the tank's movement speed, requiring the turret to adjust its trajectory more rapidly. Both units
 * physically pivot their barrels to find the ballistic solution while maintaining a 50/50 win ratio through balanced jitter.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Artillery", mixinStandardHelpOptions = true, version = "1.0.0",
        description = "Artillery Duel with fast-moving tank and balanced AI")
public class Artillery extends Base {

    /**
     * Picocli command specification for CLI option handling.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Random generator for AI jitter and reload timing.
     */
    private final Random random = new Random();

    /**
     * Gravitational constant ($9.81 m/s^2$) for ballistic math.
     */
    private static final double G = 9.81;

    /**
     * Turret Sprite (10x6) bitmap.
     */
    private final int[][] TURRET_SPRITE = {
        {0, 0, 3, 3, 3, 3, 3, 0, 0, 0},
        {0, 3, 1, 1, 1, 1, 1, 3, 0, 0},
        {3, 1, 1, 1, 2, 1, 1, 1, 3, 0},
        {3, 1, 1, 1, 1, 1, 1, 1, 3, 0},
        {3, 3, 3, 3, 3, 3, 3, 3, 3, 0},
        {0, 3, 3, 3, 3, 3, 3, 3, 0, 0}
    };

    /**
     * Tank Sprite (14x7) bitmap.
     */
    private final int[][] TANK_SPRITE = {
        {0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0},
        {0, 0, 3, 3, 1, 1, 4, 1, 3, 3, 0, 0, 0, 0},
        {3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3},
        {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
        {0, 3, 0, 3, 0, 3, 0, 3, 0, 3, 0, 3, 0, 0}
    };

    /**
     * Renders bitmapped sprites using a color palette.
     *
     * @param sprite 2D array of pixel color indices.
     * @param x The horizontal pixel offset on screen.
     * @param y The vertical pixel offset on screen.
     * @param palette Array of Color objects for the sprite.
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
     * Primary loop handling physics, AI logic, and hardware rendering.
     *
     * @param oled The SSD1331 driver instance.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth();
        final var g2d = getG2d();
        final var tPal = new Color[]{new Color(70, 70, 75), new Color(160, 160, 170), Color.BLACK};
        final var ePal = new Color[]{new Color(85, 107, 47), new Color(40, 50, 20), Color.BLACK, new Color(130, 150, 90)};

        var score = 0;
        var lives = 3;
        var tankX = 82.0;
        final double jitter = 14.0;

        // Turret: Adjusted for higher fire rate
        var tVel = 22.0;
        var tAng = Math.toRadians(25);
        var tShellX = 0.0;
        var tShellY = 0.0;
        var tTime = 0.0;
        var tActive = false;
        var tWait = 25;
        var tFlash = 0;

        // Tank: Fast attack parameters
        var eVel = 22.0;
        var eAng = Math.toRadians(155);
        var eShellX = 0.0;
        var eShellY = 0.0;
        var eTime = 0.0;
        var eActive = false;
        var eWait = 50;
        var eFlash = 0;

        final var frameDelay = 1000 / getFps();

        while (lives > 0) {
            final var start = System.currentTimeMillis();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, 64);
            g2d.setColor(new Color(25, 25, 25));
            g2d.fillRect(0, 60, width, 4);

            // HUD
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(score), 4, 11);
            g2d.setColor(Color.GREEN);
            for (var i = 0; i < lives; i++) {
                g2d.fillRect(width - 12 - (i * 6), 5, 3, 3);
            }

            // 1. Pivot Turret Cannon (8px)
            final int tpX = 7, tpY = 54;
            int teX = tpX + (int) (Math.cos(tAng) * 8), teY = tpY - (int) (Math.sin(tAng) * 8);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawLine(tpX, tpY, teX, teY);
            if (tFlash > 0) {
                g2d.setColor(Color.YELLOW);
                g2d.drawOval(teX - 2, teY - 2, 4, 4);
                tFlash--;
            }

            // 2. Pivot Tank Cannon (6px)
            final int epX = (int) tankX + 5, epY = 54;
            int eeX = epX + (int) (Math.cos(eAng) * 6), eeY = epY - (int) (Math.sin(eAng) * 6);
            g2d.setColor(new Color(130, 150, 90));
            g2d.drawLine(epX, epY, eeX, eeY);
            if (eFlash > 0) {
                g2d.setColor(Color.ORANGE);
                g2d.drawOval(eeX - 2, eeY - 2, 4, 4);
                eFlash--;
            }

            drawSprite(TURRET_SPRITE, 2, 54, tPal);
            drawSprite(TANK_SPRITE, (int) tankX, 53, ePal);

            // 3. Turret AI Logic (Faster reload to counter fast tank)
            if (tActive) {
                tShellX = (tVel * Math.cos(tAng) * tTime) + teX;
                tShellY = teY - (tVel * Math.sin(tAng) * tTime - 0.5 * G * tTime * tTime);
                g2d.setColor(Color.WHITE);
                g2d.fillRect((int) tShellX, (int) tShellY, 1, 1);

                if (tShellX >= tankX && tShellX <= tankX + 14 && tShellY >= 53 && tShellY <= 61) {
                    performExplosion((int) tankX + 7, 56, oled);
                    score += 100;
                    tankX = 82;
                    tActive = false;
                    tWait = 40;
                } else if (tShellY > 60 || tShellX > width || tShellX < 0) {
                    double j = (random.nextDouble() - 0.5) * jitter;
                    if (tShellX < tankX) {
                        tAng += Math.toRadians(4 + j);
                        tVel += 1.4;
                    } else {
                        tAng -= Math.toRadians(4 + j);
                        tVel -= 1.4;
                    }
                    tAng = Math.clamp(tAng, Math.toRadians(5), Math.toRadians(80));
                    tActive = false;
                    tWait = 20 + random.nextInt(25);
                }
                tTime += 0.32;
            } else if (tWait-- <= 0) {
                tActive = true;
                tTime = 0;
                tFlash = 3;
            }

            // 4. Tank AI Logic
            if (eActive) {
                eShellX = (eVel * Math.cos(eAng) * eTime) + eeX;
                eShellY = eeY - (eVel * Math.sin(eAng) * eTime - 0.5 * G * eTime * eTime);
                g2d.setColor(Color.YELLOW);
                g2d.fillRect((int) eShellX, (int) eShellY, 1, 1);

                if (eShellX <= 12 && eShellX >= 0 && eShellY >= 54 && eShellY <= 61) {
                    lives--;
                    performExplosion(6, 56, oled);
                    eActive = false;
                    tankX = 82;
                    eWait = 60;
                } else if (eShellY > 60 || eShellX < 0 || eShellX > width) {
                    double j = (random.nextDouble() - 0.5) * jitter;
                    if (eShellX > 12) {
                        eAng -= Math.toRadians(4 + j);
                        eVel += 1.2;
                    } else {
                        eAng += Math.toRadians(4 + j);
                        eVel -= 1.2;
                    }
                    eAng = Math.clamp(eAng, Math.toRadians(100), Math.toRadians(175));
                    eActive = false;
                    eWait = 30 + random.nextInt(30);
                }
                eTime += 0.32;
            } else if (eWait-- <= 0) {
                eActive = true;
                eTime = 0;
                eFlash = 3;
            }

            // Faster advancement: Tank now moves at 0.12 pixels per frame
            tankX -= 0.12;
            if (tankX < 12) {
                lives--;
                tankX = 82;
                flashDamage(oled);
            }

            oled.drawImage(getImage());
            final var diff = System.currentTimeMillis() - start;
            if (diff < frameDelay) {
                try {
                    TimeUnit.MILLISECONDS.sleep(frameDelay - diff);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Renders a circular expanding explosion sequence.
     *
     * @param x Center X coordinate.
     * @param y Center Y coordinate.
     * @param oled SSD1331 hardware driver.
     */
    private void performExplosion(final int x, final int y, final Ssd1331 oled) {
        final var g2d = getG2d();
        for (var r = 1; r < 20; r += 3) {
            g2d.setColor(r % 2 == 0 ? Color.ORANGE : Color.WHITE);
            g2d.drawOval(x - r / 2, y - r / 2, r, r);
            oled.drawImage(getImage());
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Flashes the display red when damage is taken.
     *
     * @param oled hardware driver.
     */
    private void flashDamage(final Ssd1331 oled) {
        final var g2d = getG2d();
        g2d.setColor(new Color(150, 0, 0));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        oled.drawImage(getImage());
        try {
            TimeUnit.MILLISECONDS.sleep(250);
        } catch (Exception ignored) {
        }
    }

    /**
     * Picocli command entry point.
     *
     * @return 0 on success.
     * @throws Exception hardware/IO failure.
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
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Artillery()).execute(args));
    }
}
