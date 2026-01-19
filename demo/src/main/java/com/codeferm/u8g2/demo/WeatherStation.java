/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.FontType;
import com.codeferm.u8g2.U8g2;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Weather station simulation for U8g2 displays.
 * <p>
 * Implements realistic wind smoothing, cardinal directions, and historical data tracking.
 * UI elements are dynamically positioned based on display resolution.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "WeatherStation", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class WeatherStation extends Base {

    /**
     * Update rate in frames per second.
     */
    @Option(names = {"-f", "--fps"}, description = "Update rate", defaultValue = "10")
    private int fps;
    /**
     * Maximum number of frames to run (0 for infinite).
     */
    @Option(names = {"-m", "--max-frames"}, description = "Exit after N frames (0 for infinite)", defaultValue = "0")
    private int maxFrames;
    /**
     * Internal frame counter.
     */
    private int frameCount = 0;
    /**
     * Random source for weather variance.
     */
    private final Random random = new Random();
    /**
     * Temperature history buffer for trend graph.
     */
    private final float[] tempHistory = new float[35];
    /**
     * Current temperature in Celsius.
     */
    private float tempC = 21.2f;
    /**
     * Current relative humidity.
     */
    private int humidity = 43;
    /**
     * Counter to slow down data updates relative to render loop.
     */
    private int slowUpdateCounter = 0;
    /**
     * Target wind speed for smoothing.
     */
    private float targetWindMph = 5.0f;
    /**
     * Current calculated wind speed.
     */
    private float currentWindMph = 5.0f;
    /**
     * Smoothing factor for wind transitions.
     */
    private final float windSmoothing = 0.08f;
    /**
     * Target wind direction in degrees.
     */
    private float targetDir = 180.0f;
    /**
     * Current calculated wind direction.
     */
    private float currentDir = 180.0f;

    /**
     * Updates internal weather simulation values.
     */
    public void updateWeather() {
        if (slowUpdateCounter++ >= 60) {
            tempC += (random.nextFloat() - 0.5f) * 0.02f;
            humidity = Math.max(30, Math.min(70, humidity + (random.nextInt(3) - 1)));
            slowUpdateCounter = 0;
            System.arraycopy(tempHistory, 1, tempHistory, 0, tempHistory.length - 1);
            tempHistory[tempHistory.length - 1] = tempC;
        }
        if (random.nextDouble() > 0.95) {
            targetWindMph = 2.0f + random.nextFloat() * 12.0f;
        }
        if (random.nextDouble() > 0.97) {
            targetDir = (targetDir + (random.nextFloat() - 0.5f) * 60.0f + 360) % 360;
        }
        currentWindMph += (targetWindMph - currentWindMph) * windSmoothing;
        currentWindMph += (random.nextFloat() - 0.5f) * 0.2f;
        currentWindMph = Math.max(0, currentWindMph);
        final var dirDiff = (targetDir - currentDir + 540) % 360 - 180;
        currentDir = (currentDir + dirDiff * 0.05f + 360) % 360;
    }

    /**
     * Draws the compass UI element.
     *
     * @param u8 Native U8g2 pointer.
     * @param x Center X coordinate.
     * @param y Center Y coordinate.
     * @param r Compass radius.
     * @param angle Current wind angle.
     */
    private void drawAdvancedCompass(final long u8, final int x, final int y, final int r, final float angle) {
        U8g2.drawCircle(u8, x, y, r, U8g2.U8G2_DRAW_ALL);
        U8g2.drawPixel(u8, x, y);
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_4X6_TF));
        U8g2.drawStr(u8, x - 2, y - r - 2, "N");
        U8g2.drawStr(u8, x - 2, y + r + 6, "S");
        U8g2.drawStr(u8, x + r + 3, y + 2, "E");
        U8g2.drawStr(u8, x - r - 8, y + 2, "W");
        final var rad = Math.toRadians(angle - 90);
        U8g2.drawLine(u8, x, y, x + (int) (Math.cos(rad) * (r - 2)), y + (int) (Math.sin(rad) * (r - 2)));
    }

    /**
     * Renders the dashboard with resolution-independent positioning.
     */
    public void render() {
        final var u8 = getU8g2();
        final var w = getWidth();
        final var h = getHeight();
        U8g2.clearBuffer(u8);
        // Temp (Top Left)
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_6X12_TF));
        U8g2.drawStr(u8, 2, 10, String.format("%.1f F", (tempC * 9 / 5) + 32));
        // Trend Graph (Fixed size frame, relative position)
        final var graphX = 4;
        final var graphY = 14;
        U8g2.drawFrame(u8, graphX, graphY, 37, 22);
        for (var i = 0; i < tempHistory.length - 1; i++) {
            final var y1 = graphY + 11 - (int) ((tempHistory[i] - 20) * 4);
            final var y2 = graphY + 11 - (int) ((tempHistory[i + 1] - 20) * 4);
            U8g2.drawLine(u8, graphX + 1 + i, Math.max(graphY + 1, Math.min(graphY + 21, y1)), 
                               graphX + 2 + i, Math.max(graphY + 1, Math.min(graphY + 21, y2)));
        }
        // Humidity (Centered horizontally)
        final var humX = (w / 2) - 4;
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_4X6_TF));
        U8g2.drawStr(u8, humX - 2, 8, "RH%");
        U8g2.drawFrame(u8, humX, 12, 8, 26);
        final var barHeight = (int) (24 * (humidity / 100.0));
        U8g2.drawBox(u8, humX + 1, 37 - barHeight, 6, barHeight);
        // Compass (Right-aligned)
        final var compassR = Math.min(w, h) / 4;
        drawAdvancedCompass(u8, w - compassR - 12, h / 2, compassR, currentDir);
        // Wind Speed (Bottom Left)
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_6X12_TF));
        U8g2.drawStr(u8, 2, h - 4, String.format("%.1f", currentWindMph));
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_5X7_TF));
        U8g2.drawStr(u8, 35, h - 4, "MPH WIND");
        U8g2.sendBuffer(u8);
    }

    /**
     * Execution entry point for simulation.
     *
     * @return Exit status code.
     * @throws InterruptedException if thread sleep is interrupted.
     */
    @Override
    public Integer call() throws InterruptedException {
        super.call();
        for (var i = 0; i < tempHistory.length; i++) {
            tempHistory[i] = tempC;
        }
        while (maxFrames == 0 || frameCount < maxFrames) {
            updateWeather();
            render();
            getDisplay().sleep(1000 / fps);
            frameCount++;
        }
        done();
        return 0;
    }

    /**
     * Main CLI entry point.
     *
     * @param args CLI arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new WeatherStation())
                .registerConverter(Integer.class, Integer::decode)
                .registerConverter(Integer.TYPE, Integer::decode)
                .execute(args));
    }
}
