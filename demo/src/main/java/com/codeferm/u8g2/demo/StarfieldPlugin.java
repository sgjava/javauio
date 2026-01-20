/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;
import java.util.Random;

/**
 * 3D Starfield warp effect plugin using perspective projection.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class StarfieldPlugin implements DemoPlugin {

    /**
     * Number of stars to maintain in the field.
     */
    private final int numStars = 64;

    private final float[] starsX = new float[numStars];
    private final float[] starsY = new float[numStars];
    private final float[] starsZ = new float[numStars];

    /**
     * Random generator for initial and reset star positions.
     */
    private final Random rand;

    /**
     * Initializes the starfield and positions stars randomly.
     */
    public StarfieldPlugin() {
        this.rand = new Random(System.nanoTime());
        for (var i = 0; i < numStars; i++) {
            initStar(i);
        }
    }

    /**
     * Resets a specific star to a random 3D coordinate.
     *
     * @param i Index of the star array.
     */
    private void initStar(final int i) {
        starsX[i] = (rand.nextFloat() - 0.5f) * 200.0f;
        starsY[i] = (rand.nextFloat() - 0.5f) * 200.0f;
        starsZ[i] = rand.nextFloat() * 100.0f + 1.0f;
    }

    /**
     * Renders a moving starfield.
     *
     * @param u8g2 Native display handle.
     * @param display Display helper.
     * @param fps Target frames per second.
     * @param duration Execution duration in milliseconds.
     */
    @Override
    public void run(final long u8g2, final Display display, final int fps, final long duration) {
        final var width = U8g2.getDisplayWidth(u8g2);
        final var height = U8g2.getDisplayHeight(u8g2);
        final var halfWidth = width / 2.0f;
        final var halfHeight = height / 2.0f;
        final var startTime = System.currentTimeMillis();

        while (true) {
            final var start = System.currentTimeMillis();
            if (duration > 0 && (start - startTime) > duration) {
                break;
            }

            U8g2.clearBuffer(u8g2);
            for (var i = 0; i < numStars; i++) {
                starsZ[i] -= 2.0f;
                if (starsZ[i] <= 0) {
                    initStar(i);
                }

                final var x = (int) (starsX[i] / starsZ[i] * 100.0f + halfWidth);
                final var y = (int) (starsY[i] / starsZ[i] * 100.0f + halfHeight);

                if (x >= 0 && x < width && y >= 0 && y < height) {
                    U8g2.drawPixel(u8g2, x, y);
                    if (starsZ[i] < 15.0f) {
                        U8g2.drawPixel(u8g2, x + 1, y);
                    }
                } else {
                    initStar(i);
                }
            }
            U8g2.sendBuffer(u8g2);

            if (fps > 0) {
                final var elapsed = System.currentTimeMillis() - start;
                final var sleepTime = (1000 / fps) - elapsed;
                if (sleepTime > 0) {
                    display.sleep((int) sleepTime);
                }
            }
        }
    }
}
