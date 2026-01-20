/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;
import java.util.Random;

public class PlasmaPlugin implements DemoPlugin {

    private final float seedX;
    private final float seedY;
    private final float phaseShift;

    /**
     * Constructor seeds the random values to ensure every instance is unique.
     */
    public PlasmaPlugin() {
        // Use nanoTime for high-resolution pseudo-random seeding
        Random rand = new Random(System.nanoTime());
        this.seedX = rand.nextFloat() * 100.0f;
        this.seedY = rand.nextFloat() * 100.0f;
        this.phaseShift = 0.5f + rand.nextFloat(); // Unique speed multiplier
    }

    @Override
    public void run(final long u8g2, final int width, final int height, final Display display, final int fps) {
        var tick = 0.0f;
        while (true) {
            long start = System.currentTimeMillis();
            U8g2.clearBuffer(u8g2);

            for (var y = 0; y < height; y += 2) {
                for (var x = 0; x < width; x += 2) {
                    // Lava lamp logic: combine multiple sine waves moving in different directions
                    // wave1: horizontal movement
                    // wave2: diagonal movement
                    // wave3: vertical movement
                    final double wave1 = Math.sin((x + seedX) / 12.0 + tick);
                    final double wave2 = Math.sin((y + seedY) / 8.0 + tick * phaseShift);
                    final double wave3 = Math.sin((x + y + seedX + seedY) / 16.0 + tick / 2.0);

                    final double value = wave1 + wave2 + wave3;

                    // Threshold for "blob" appearance
                    if (value > 0.8) {
                        U8g2.drawPixel(u8g2, x, y);
                    }
                }
            }
            U8g2.sendBuffer(u8g2);
            tick += 0.08f; // Slowed down for smoother lava effect

            if (fps > 0) {
                long elapsed = System.currentTimeMillis() - start;
                long sleepTime = (1000 / fps) - elapsed;
                if (sleepTime > 0) {
                    display.sleep((int) sleepTime);
                }
            }
        }
    }
}
