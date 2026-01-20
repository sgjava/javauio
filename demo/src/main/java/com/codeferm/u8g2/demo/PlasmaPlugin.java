/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;
import java.util.Random;

public class PlasmaPlugin implements DemoPlugin {
    private final float seed;
    private final float speedX;
    private final float speedY;

    public PlasmaPlugin() {
        // Unique seed so displays don't match
        Random rand = new Random(System.nanoTime());
        this.seed = rand.nextFloat() * 1000.0f;
        // Randomize the "swing" speed for X and Y movement
        this.speedX = 0.2f + rand.nextFloat() * 0.5f;
        this.speedY = 0.2f + rand.nextFloat() * 0.5f;
    }

    @Override
    public void run(final long u8g2, final int width, final int height, final Display display, final int fps) {
        float tick = 0.0f;
        
        while (true) {
            long start = System.currentTimeMillis();
            U8g2.clearBuffer(u8g2);

            // Calculate oscillating offsets to create left-to-right/up-to-down movement
            // This prevents the "constant shift" look
            float movX = (float) Math.sin(tick * speedX) * 20.0f;
            float movY = (float) Math.cos(tick * speedY) * 15.0f;

            for (int y = 0; y < height; y += 2) {
                for (int x = 0; x < width; x += 2) {
                    // Combine multiple sine waves for the blob effect
                    double v1 = Math.sin((x + movX + seed) / 10.0);
                    double v2 = Math.sin((y + movY + seed) / 15.0);
                    double v3 = Math.sin((x + y + movX + movY) / 20.0);
                    
                    double result = v1 + v2 + v3;

                    // Only draw pixels where waves interfere constructively
                    if (result > 0.6) {
                        U8g2.drawPixel(u8g2, x, y);
                    }
                }
            }
            
            U8g2.sendBuffer(u8g2);
            tick += 0.05f;

            if (fps > 0) {
                long elapsed = System.currentTimeMillis() - start;
                long sleepTime = (1000 / fps) - elapsed;
                if (sleepTime > 0) display.sleep((int) sleepTime);
            }
        }
    }
}
