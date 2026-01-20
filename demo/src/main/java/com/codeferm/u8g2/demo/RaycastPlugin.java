/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.U8g2;
import java.util.Random;

/**
 * Wolfenstein-style raycasting demo with randomized wandering and collision detection.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class RaycastPlugin implements DemoPlugin {

    /**
     * 2D World Map where 1 is a wall and 0 is walkable space.
     */
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

    /**
     * Random generator for AI movement decisions.
     */
    private final Random rand;

    /**
     * Initializes the plugin with a unique nano-time seed.
     */
    public RaycastPlugin() {
        this.rand = new Random(System.nanoTime());
    }

    /**
     * Renders a pseudo-3D environment using DDA.
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

        var posX = 3.5f;
        var posY = 3.5f;
        var dirX = -1.0f;
        var dirY = 0.0f;
        var planeX = 0.0f;
        var planeY = 0.66f;

        final var moveSpeed = 0.08f;
        final var rotSpeed = 0.05f;
        var actionFrames = 0;
        var currentAction = 0;

        while (true) {
            final var start = System.currentTimeMillis();
            if (duration > 0 && (start - startTime) > duration) {
                break;
            }

            if (actionFrames <= 0) {
                currentAction = rand.nextInt(4);
                actionFrames = 20 + rand.nextInt(30);
            }
            actionFrames--;

            if (currentAction == 0) {
                if (map[(int) (posX + dirX * moveSpeed)][(int) posY] == 0) {
                    posX += dirX * moveSpeed;
                }
                if (map[(int) posX][(int) (posY + dirY * moveSpeed)] == 0) {
                    posY += dirY * moveSpeed;
                }
            } else if (currentAction == 1) {
                if (map[(int) (posX - dirX * moveSpeed)][(int) posY] == 0) {
                    posX -= dirX * moveSpeed;
                }
                if (map[(int) posX][(int) (posY - dirY * moveSpeed)] == 0) {
                    posY -= dirY * moveSpeed;
                }
            } else {
                final var actualRot = (currentAction == 2) ? -rotSpeed : rotSpeed;
                final var oldDirX = dirX;
                dirX = (float) (dirX * Math.cos(actualRot) - dirY * Math.sin(actualRot));
                dirY = (float) (oldDirX * Math.sin(actualRot) + dirY * Math.cos(actualRot));
                final var oldPlaneX = planeX;
                planeX = (float) (planeX * Math.cos(actualRot) - planeY * Math.sin(actualRot));
                planeY = (float) (oldPlaneX * Math.sin(actualRot) + planeY * Math.cos(actualRot));
            }

            U8g2.clearBuffer(u8g2);
            for (var x = 0; x < width; x += 2) {
                final var cameraX = 2 * x / (float) width - 1;
                final var rayDirX = dirX + planeX * cameraX;
                final var rayDirY = dirY + planeY * cameraX;

                var mapX = (int) posX;
                var mapY = (int) posY;

                final var deltaDistX = (float) Math.abs(1 / rayDirX);
                final var deltaDistY = (float) Math.abs(1 / rayDirY);
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

                var hit = 0;
                var side = 0;
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
                    if (map[mapX][mapY] > 0) {
                        hit = 1;
                    }
                }

                final var perpWallDist = (side == 0) ? (mapX - posX + (1 - stepX) / 2) / rayDirX : (mapY - posY + (1 - stepY) / 2)
                        / rayDirY;
                final var lineHeight = (int) (height / (perpWallDist + 0.0001f));
                U8g2.drawVLine(u8g2, x, Math.max(0, -lineHeight / 2 + height / 2), Math.min(height, lineHeight));
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
