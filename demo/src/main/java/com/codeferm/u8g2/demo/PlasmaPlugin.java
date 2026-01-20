/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;

public class PlasmaPlugin implements DemoPlugin {

    @Override
    public void run(final long u8g2, final int width, final int height, final Display display) {
        var tick = 0.0f;
        while (true) {
            U8g2.clearBuffer(u8g2);
            for (var y = 0; y < height; y += 2) {
                for (var x = 0; x < width; x += 2) {
                    final var value = Math.sin(x / 16.0 + tick) + Math.sin(y / 8.0 + tick);
                    if (value > 0) {
                        U8g2.drawPixel(u8g2, x, y);
                    }
                }
            }
            U8g2.sendBuffer(u8g2);
            tick += 0.1f;
            display.sleep(10);
        }
    }
}
