/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;
import java.util.Random;

/**
 * 3D Starfield warp effect.
 */
public class StarfieldPlugin implements DemoPlugin {

    private final int numStars = 64;
    private final float[] starsX = new float[numStars];
    private final float[] starsY = new float[numStars];
    private final float[] starsZ = new float[numStars];
    private final Random rand = new Random();

    public StarfieldPlugin() {
        // Initialize stars at random positions
        for (int i = 0; i < numStars; i++) {
            initStar(i);
        }
    }

    private void initStar(int i) {
        starsX[i] = (rand.nextFloat() - 0.5f) * 200.0f;
        starsY[i] = (rand.nextFloat() - 0.5f) * 200.0f;
        starsZ[i] = rand.nextFloat() * 100.0f + 1.0f;
    }

    @Override
    public void run(final long u8g2, final int width, final int height, final Display display, final int fps) {
        final float halfWidth = width / 2.0f;
        final float halfHeight = height / 2.0f;

        while (true) {
            long start = System.currentTimeMillis();
            U8g2.clearBuffer(u8g2);

            for (int i = 0; i < numStars; i++) {
                // Move star closer (decrease Z)
                starsZ[i] -= 2.0f;

                // Reset star if it passes the screen
                if (starsZ[i] <= 0) {
                    initStar(i);
                }

                // 3D to 2D Projection
                int x = (int) (starsX[i] / starsZ[i] * 100.0f + halfWidth);
                int y = (int) (starsY[i] / starsZ[i] * 100.0f + halfHeight);

                // Draw if on screen
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    U8g2.drawPixel(u8g2, x, y);
                    
                    // Draw a small tail for a "warp" effect if Z is low
                    if (starsZ[i] < 20.0f) {
                        U8g2.drawPixel(u8g2, x + 1, y);
                    }
                } else {
                    initStar(i);
                }
            }

            U8g2.sendBuffer(u8g2);

            if (fps > 0) {
                long elapsed = System.currentTimeMillis() - start;
                long sleepTime = (1000 / fps) - elapsed;
                if (sleepTime > 0) display.sleep((int) sleepTime);
            }
        }
    }
}
