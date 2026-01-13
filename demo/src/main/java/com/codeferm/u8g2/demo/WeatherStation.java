/*
 * Copyright (c) Steven P. Goldberg. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.FontType;
import com.codeferm.u8g2.U8g2;
import java.util.Random;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Weather station simulation for U8g2 displays.
 * 
 * Implements realistic wind smoothing, cardinal directions, and historical data tracking.
 */
@Command(name = "WeatherStation", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class WeatherStation extends Base {

    @Option(names = {"-f", "--fps"}, description = "Update rate", defaultValue = "10")
    public int fps;

    @Option(names = {"-m", "--max-frames"}, description = "Exit after N frames (0 for infinite)", defaultValue = "0")
    public int maxFrames;

    private int frameCount = 0;
    private final Random random = new Random();
    private final float[] tempHistory = new float[35];    
    private float tempC = 21.2f; 
    private int humidity = 43;
    private int slowUpdateCounter = 0;
    private float targetWindMph = 5.0f;
    private float currentWindMph = 5.0f;
    final private float windSmoothing = 0.08f; 
    private float targetDir = 180.0f;
    private float currentDir = 180.0f;

    /**
     * Updates environmental variables using a persistence-based model.
     */
    public void updateWeather() {
        // Increment counter for slow-changing variables
        if (slowUpdateCounter++ >= 60) {
            // Apply tiny drift to temperature
            tempC += (random.nextFloat() - 0.5f) * 0.02f;
            // Shift humidity within logical bounds
            humidity = Math.max(30, Math.min(70, humidity + (random.nextInt(3) - 1)));
            slowUpdateCounter = 0;
            // Update historical trend array
            System.arraycopy(tempHistory, 1, tempHistory, 0, tempHistory.length - 1);
            tempHistory[tempHistory.length - 1] = tempC;
        }
        // Randomly trigger new wind targets to simulate weather fronts
        if (random.nextDouble() > 0.95) {
            targetWindMph = 2.0f + random.nextFloat() * 12.0f;
        }
        if (random.nextDouble() > 0.97) {
            targetDir = (targetDir + (random.nextFloat() - 0.5f) * 60.0f + 360) % 360;
        }
        // Move current values toward targets using exponential smoothing
        currentWindMph += (targetWindMph - currentWindMph) * windSmoothing;
        // Add micro-turbulence jitter
        currentWindMph += (random.nextFloat() - 0.5f) * 0.2f;
        currentWindMph = Math.max(0, currentWindMph);
        // Calculate shortest path for circular direction smoothing
        var dirDiff = (targetDir - currentDir + 540) % 360 - 180;
        currentDir = (currentDir + dirDiff * 0.05f + 360) % 360;
    }

    /**
     * Draws an advanced compass rose with cardinal labels and intermediate markers.
     * * @param u8 The U8g2 display handle
     * @param x Center X coordinate
     * @param y Center Y coordinate
     * @param r Radius of the compass circle
     * @param angle Current wind direction in degrees
     */
    private void drawAdvancedCompass(long u8, int x, int y, int r, float angle) {
        // Draw the main outer ring
        U8g2.drawCircle(u8, x, y, r, U8g2.U8G2_DRAW_ALL);
        // Draw the center pivot point
        U8g2.drawPixel(u8, x, y);
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_4X6_TF));
        // Primary cardinal labels
        U8g2.drawStr(u8, x - 2, y - r - 4, "N");   
        U8g2.drawStr(u8, x - 2, y + r + 8, "S");   
        U8g2.drawStr(u8, x + r + 5, y + 3, "E");   
        U8g2.drawStr(u8, x - r - 10, y + 3, "W");  
        // Intermediate 45-degree dots
        for (var i = 45; i < 360; i += 90) {
            var rad = Math.toRadians(i - 90);
            U8g2.drawPixel(u8, x + (int)Math.round(Math.cos(rad) * r), y + (int)Math.round(Math.sin(rad) * r));
        }
        // Directional needle indicating wind origin
        var rad = Math.toRadians(angle - 90);
        U8g2.drawLine(u8, x, y, x + (int)(Math.cos(rad) * (r - 2)), y + (int)(Math.sin(rad) * (r - 2)));
    }

    /**
     * Renders the complete weather dashboard to the display buffer.
     */
    public void render() {
        var u8 = getU8g2();
        U8g2.clearBuffer(u8);
        // Render Temperature Header
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_6X12_TF));
        U8g2.drawStr(u8, 2, 10, String.format("%.1f F", (tempC * 9/5) + 32));  
        // Render Historical Trend Graph
        U8g2.drawFrame(u8, 4, 14, 37, 22); 
        for (var i = 0; i < tempHistory.length - 1; i++) {
            if (tempHistory[i] == 0) continue;
            // Map temperature to box Y-coordinates
            var y1 = 25 - (int)((tempHistory[i] - 20) * 4);
            var y2 = 25 - (int)((tempHistory[i+1] - 20) * 4);
            // Clamp line within frame boundaries
            U8g2.drawLine(u8, 5 + i, Math.max(15, Math.min(35, y1)), 6 + i, Math.max(15, Math.min(35, y2)));
        }
        // Render Humidity Level
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_4X6_TF));
        U8g2.drawStr(u8, 48, 8, "RH%");
        U8g2.drawFrame(u8, 50, 12, 8, 26);
        // Calculate vertical fill height
        var barHeight = (int)(24 * (humidity / 100.0));
        U8g2.drawBox(u8, 51, 37 - barHeight, 6, barHeight);
        U8g2.drawStr(u8, 47, 48, humidity + "%");
        // Render the Compass assembly
        drawAdvancedCompass(u8, 100, 32, 16, currentDir);     
        // Render Wind Speed Footer
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_6X12_TF));
        U8g2.drawStr(u8, 2, 60, String.format("%.1f", currentWindMph));
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_5X7_TF));
        U8g2.drawStr(u8, 35, 60, "MPH WIND");
        U8g2.sendBuffer(u8);
    }

    /**
     * Execution entry point for the simulation.
     * 
     * @return Exit status code
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Override
    public Integer call() throws InterruptedException {
        super.call();
        // Initialize history with current reading
        for (var i = 0; i < tempHistory.length; i++) {
            tempHistory[i] = tempC;
        }
        // Main execution loop
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
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new WeatherStation()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}