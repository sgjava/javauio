/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;
import java.util.Random;

public class RaycastPlugin implements DemoPlugin {
    // 8x8 Map (1=Wall, 0=Space)
    private final int[][] map = {
        {1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 1, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1}
    };

    private final float seed;

    public RaycastPlugin() {
        // Unique seed per instance to ensure different movement/starting view
        this.seed = new Random(System.nanoTime()).nextFloat() * 100.0f;
    }

    @Override
    public void run(final long u8g2, final int width, final int height, final Display display, final int fps) {
        float time = seed;

        while (true) {
            long start = System.currentTimeMillis();
            U8g2.clearBuffer(u8g2);

            // RANDOM MOVEMENT LOGIC
            // Instead of static posX/posY, we wander inside the 8x8 grid (clamped to 1.5 - 6.5)
            float posX = 4.0f + (float) Math.sin(time * 0.3f) * 2.5f;
            float posY = 4.0f + (float) Math.cos(time * 0.5f) * 2.5f;
            
            // Direction rotates based on a different frequency
            float dirX = (float) Math.cos(time);
            float dirY = (float) Math.sin(time);
            
            // Camera plane (FOV) - perpendicular to direction
            float planeX = -dirY * 0.66f;
            float planeY = dirX * 0.66f;

            

            // Render columns
            for (int x = 0; x < width; x += 2) {
                float cameraX = 2 * x / (float) width - 1;
                float rayDirX = dirX + planeX * cameraX;
                float rayDirY = dirY + planeY * cameraX;

                int mapX = (int) posX;
                int mapY = (int) posY;

                float deltaDistX = (float) Math.abs(1 / rayDirX);
                float deltaDistY = (float) Math.abs(1 / rayDirY);
                
                float sideDistX, sideDistY;
                int stepX, stepY;

                if (rayDirX < 0) {
                    stepX = -1;
                    sideDistX = (posX - mapX) * deltaDistX;
                } else {
                    stepX = 1;
                    sideDistX = (mapX + 1.0f - posX) * deltaDistX;
                }
                if (rayDirY < 0) {
                    stepY = -1;
                    sideDistY = (posY - mapY) * deltaDistY;
                } else {
                    stepY = 1;
                    sideDistY = (mapY + 1.0f - posY) * deltaDistY;
                }

                // DDA
                int hit = 0, side = 0;
                while (hit == 0) {
                    if (sideDistX < sideDistY) {
                        sideDistX += deltaDistX;
                        mapX += stepX;
                        side = 0;
                    } else {
                        sideDistY += deltaDistY;
                        mapY += stepY;
                        side = 1;
                    }
                    if (map[mapX][mapY] > 0) hit = 1;
                }

                float perpWallDist = (side == 0) ? 
                    (mapX - posX + (1 - stepX) / 2) / rayDirX : 
                    (mapY - posY + (1 - stepY) / 2) / rayDirY;

                int lineHeight = (int) (height / (perpWallDist + 0.0001f));
                int drawStart = Math.max(0, -lineHeight / 2 + height / 2);
                
                // Draw wall slice
                U8g2.drawVLine(u8g2, x, drawStart, Math.min(height, lineHeight));
            }

            U8g2.sendBuffer(u8g2);
            time += 0.02f; // Movement speed

            if (fps > 0) {
                long elapsed = System.currentTimeMillis() - start;
                long sleepTime = (1000 / fps) - elapsed;
                if (sleepTime > 0) display.sleep((int) sleepTime);
            }
        }
    }
}
