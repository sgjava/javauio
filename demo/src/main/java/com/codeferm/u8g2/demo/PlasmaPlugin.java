/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;
import java.util.Random;

/**
 * Plasma effect producing an organic "lava lamp" motion through wave interference.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlasmaPlugin implements DemoPlugin {

    /**
     * Spatial seed for pattern randomization.
     */
    private final float seed;

    /**
     * Horizontal oscillation multiplier.
     */
    private final float speedX;

    /**
     * Vertical oscillation multiplier.
     */
    private final float speedY;

    /**
     * Constructs the plugin with randomized movement seeds.
     */
    public PlasmaPlugin() {
        final var rand = new Random(System.nanoTime());
        this.seed = rand.nextFloat() * 1000.0f;
        this.speedX = 0.2f + rand.nextFloat() * 0.4f;
        this.speedY = 0.2f + rand.nextFloat() * 0.4f;
    }

    /**
     * Renders oscillating plasma blobs.
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
        final var startTime = System.currentTimeMillis();
        var tick = 0.0f;

        while (true) {
            final var start = System.currentTimeMillis();
            if (duration > 0 && (start - startTime) > duration) {
                break;
            }

            U8g2.clearBuffer(u8g2);
            final var movX = (float) Math.sin(tick * speedX) * 15.0f;
            final var movY = (float) Math.cos(tick * speedY) * 10.0f;

            for (var y = 0; y < height; y += 2) {
                for (var x = 0; x < width; x += 2) {
                    final var v1 = Math.sin((x + movX + seed) / 12.0);
                    final var v2 = Math.sin((y + movY + seed) / 10.0);
                    final var v3 = Math.sin((x + y + movX + movY) / 18.0);
                    if ((v1 + v2 + v3) > 0.7) {
                        U8g2.drawPixel(u8g2, x, y);
                    }
                }
            }
            U8g2.sendBuffer(u8g2);
            tick += 0.05f;

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
