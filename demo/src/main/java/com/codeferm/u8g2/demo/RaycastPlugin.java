/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;

/**
 * Wolfenstein-style raycasting demo.
 */
public class RaycastPlugin implements DemoPlugin {

    // 8x8 map: 1 = Wall, 0 = Empty
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

    @Override
    public void run(final long u8g2, final int width, final int height, final Display display, final int fps) {
        float posX = 3.5f, posY = 3.5f;  // Start position
        float dirX = -1.0f, dirY = 0.0f; // Initial direction
        float planeX = 0.0f, planeY = 0.66f; // Camera plane (FOV)
        float time = 0.0f;

        while (true) {
            long start = System.currentTimeMillis();
            U8g2.clearBuffer(u8g2);

            // Rotate camera slowly to simulate looking around
            float rotSpeed = 0.05f;
            float oldDirX = dirX;
            dirX = (float) (dirX * Math.cos(rotSpeed) - dirY * Math.sin(rotSpeed));
            dirY = (float) (oldDirX * Math.sin(rotSpeed) + dirY * Math.cos(rotSpeed));
            float oldPlaneX = planeX;
            planeX = (float) (planeX * Math.cos(rotSpeed) - planeY * Math.sin(rotSpeed));
            planeY = (float) (oldPlaneX * Math.sin(rotSpeed) + planeY * Math.cos(rotSpeed));

            // Raycasting loop for each vertical column of pixels
            for (int x = 0; x < width; x += 2) {
                float cameraX = 2 * x / (float) width - 1;
                float rayDirX = dirX + planeX * cameraX;
                float rayDirY = dirY + planeY * cameraX;

                int mapX = (int) posX;
                int mapY = (int) posY;

                float sideDistX, sideDistY;
                float deltaDistX = (float) Math.abs(1 / rayDirX);
                float deltaDistY = (float) Math.abs(1 / rayDirY);
                float perpWallDist;

                int stepX, stepY;
                int hit = 0;
                int side = 0;

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

                // DDA (Digital Differential Analyzer) to find wall
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

                if (side == 0) perpWallDist = (mapX - posX + (1 - stepX) / 2) / rayDirX;
                else perpWallDist = (mapY - posY + (1 - stepY) / 2) / rayDirY;

                // Calculate height of line to draw on screen
                int lineHeight = (int) (height / perpWallDist);

                // Draw the vertical line (wall slice)
                int drawStart = -lineHeight / 2 + height / 2;
                if (drawStart < 0) drawStart = 0;
                int drawEnd = lineHeight / 2 + height / 2;
                if (drawEnd >= height) drawEnd = height - 1;

                U8g2.drawVLine(u8g2, x, drawStart, drawEnd - drawStart);
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
