/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;
import java.util.Random;

public class RaycastPlugin implements DemoPlugin {
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

    private final Random rand;

    public RaycastPlugin() {
        this.rand = new Random(System.nanoTime());
    }

    @Override
    public void run(final long u8g2, final int width, final int height, final Display display, final int fps) {
        float posX = 3.5f, posY = 3.5f; 
        float dirX = -1.0f, dirY = 0.0f;
        float planeX = 0.0f, planeY = 0.66f;

        // Movement states
        float moveSpeed = 0.08f;
        float rotSpeed = 0.05f;
        int actionFrames = 0;
        int currentAction = 0; // 0: Forward, 1: Back, 2: Left, 3: Right

        while (true) {
            long start = System.currentTimeMillis();
            U8g2.clearBuffer(u8g2);

            // AI Logic: Pick a random movement every 20-50 frames
            if (actionFrames <= 0) {
                currentAction = rand.nextInt(4);
                actionFrames = 20 + rand.nextInt(30);
            }
            actionFrames--;

            // Execute Movement with Collision Detection
            if (currentAction == 0) { // Forward
                if (map[(int)(posX + dirX * moveSpeed)][(int)posY] == 0) posX += dirX * moveSpeed;
                if (map[(int)posX][(int)(posY + dirY * moveSpeed)] == 0) posY += dirY * moveSpeed;
            } else if (currentAction == 1) { // Back
                if (map[(int)(posX - dirX * moveSpeed)][(int)posY] == 0) posX -= dirX * moveSpeed;
                if (map[(int)posX][(int)(posY - dirY * moveSpeed)] == 0) posY -= dirY * moveSpeed;
            } else { // Turn
                float actualRot = (currentAction == 2) ? -rotSpeed : rotSpeed;
                float oldDirX = dirX;
                dirX = (float) (dirX * Math.cos(actualRot) - dirY * Math.sin(actualRot));
                dirY = (float) (oldDirX * Math.sin(actualRot) + dirY * Math.cos(actualRot));
                float oldPlaneX = planeX;
                planeX = (float) (planeX * Math.cos(actualRot) - planeY * Math.sin(actualRot));
                planeY = (float) (oldPlaneX * Math.sin(actualRot) + planeY * Math.cos(actualRot));
            }

            

            // Render
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

                if (rayDirX < 0) { stepX = -1; sideDistX = (posX - mapX) * deltaDistX; }
                else { stepX = 1; sideDistX = (mapX + 1.0f - posX) * deltaDistX; }
                if (rayDirY < 0) { stepY = -1; sideDistY = (posY - mapY) * deltaDistY; }
                else { stepY = 1; sideDistY = (mapY + 1.0f - posY) * deltaDistY; }

                int hit = 0, side = 0;
                while (hit == 0) {
                    if (sideDistX < sideDistY) { sideDistX += deltaDistX; mapX += stepX; side = 0; }
                    else { sideDistY += deltaDistY; mapY += stepY; side = 1; }
                    if (map[mapX][mapY] > 0) hit = 1;
                }

                float perpWallDist = (side == 0) ? (mapX - posX + (1 - stepX) / 2) / rayDirX : (mapY - posY + (1 - stepY) / 2) / rayDirY;
                int lineHeight = (int) (height / (perpWallDist + 0.0001f));
                int drawStart = Math.max(0, -lineHeight / 2 + height / 2);
                U8g2.drawVLine(u8g2, x, drawStart, Math.min(height, lineHeight));
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
