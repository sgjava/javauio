package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;
import java.util.Random;

public class PlasmaPlugin implements DemoPlugin {
    private final float offsetX;
    private final float offsetY;

    public PlasmaPlugin() {
        Random rand = new Random();
        this.offsetX = rand.nextFloat() * 100.0f;
        this.offsetY = rand.nextFloat() * 100.0f;
    }

    @Override
    public void run(final long u8g2, final int width, final int height, final Display display, final int fps) {
        var tick = 0.0f;
        while (true) {
            long start = System.currentTimeMillis();
            U8g2.clearBuffer(u8g2);
            for (var y = 0; y < height; y += 2) {
                for (var x = 0; x < width; x += 2) {
                    final var val = Math.sin((x + offsetX) / 16.0 + tick) + Math.sin((y + offsetY) / 8.0 + tick);
                    if (val > 0) U8g2.drawPixel(u8g2, x, y);
                }
            }
            U8g2.sendBuffer(u8g2);
            tick += 0.1f;

            if (fps > 0) {
                long elapsed = System.currentTimeMillis() - start;
                long sleepTime = (1000 / fps) - elapsed;
                // If draw time < target frame time, sleep. Otherwise, run full speed.
                if (sleepTime > 0) display.sleep((int) sleepTime);
            }
        }
    }
}
