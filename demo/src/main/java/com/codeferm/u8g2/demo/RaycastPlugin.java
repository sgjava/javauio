package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;

public class RaycastPlugin implements DemoPlugin {
    @Override
    public void run(final long u8g2, final int width, final int height, final Display display, final int fps) {
        var angle = 0.0f;
        while (true) {
            long start = System.currentTimeMillis();
            U8g2.clearBuffer(u8g2);
            for (var x = 0; x < width; x += 4) {
                final var rayAngle = (angle - 0.5f) + (x / (float) width);
                final var dist = 1.0f + (float) Math.abs(Math.sin(rayAngle * 2.0) * 10.0);
                final var h = (int) (height / dist);
                U8g2.drawVLine(u8g2, x, (height - h) / 2, h);
            }
            U8g2.sendBuffer(u8g2);
            angle += 0.05f;

            if (fps > 0) {
                long elapsed = System.currentTimeMillis() - start;
                long sleepTime = (1000 / fps) - elapsed;
                if (sleepTime > 0) display.sleep((int) sleepTime);
            }
        }
    }
}