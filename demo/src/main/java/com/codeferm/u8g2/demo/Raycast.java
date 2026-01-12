/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.U8g2;
import java.util.Random;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This demo simulates a 3D environment on a 128x64 monochrome display.
 */
@Command(name = "Raycast", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Clean 3D Raycasting walkthrough with full var and final")
public class Raycast extends Base {

    @Option(names = {"-f", "--fps"}, description = "Frames per second", defaultValue = "30")
    private int fps;
    /**
     * World Map: 1 represents a wall, 0 represents empty space.
     */
    private static final int[][] MAP = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 1, 0, 1, 0, 1},
        {1, 0, 1, 0, 0, 1, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 0, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    // Current player coordinates
    private double posX = 1.5, posY = 1.5;
    // Current direction vector (Where the player is facing)
    private double dirX = 1.0, dirY = 0.0;
    // Camera plane (Field of View - must be perpendicular to direction)
    private double planeX = 0.0, planeY = 0.66;
    private final Random random = new Random();

    /**
     * Renders the 3D scene by casting a ray for every horizontal pixel.
     */
    public void render() {
        final var u8g2 = getU8g2();
        final var width = getWidth();
        final var height = getHeight();
        U8g2.clearBuffer(u8g2);
        // Scan every vertical stripe of the screen
        for (var x = 0; x < width; x++) {
            // Transform screen coordinate x to camera-space coordinate (-1 to 1)
            final var cameraX = 2 * x / (double) width - 1;
            // Calculate direction of the ray
            final var rayDirX = dirX + planeX * cameraX;
            final var rayDirY = dirY + planeY * cameraX;
            // Which grid square the ray is currently in
            var mapX = (int) posX;
            var mapY = (int) posY;
            // Distance the ray has to travel to cross one grid cell line
            final var deltaDistX = Math.abs(1 / rayDirX);
            final var deltaDistY = Math.abs(1 / rayDirY);
            // Distance from current position to the first grid lines
            var sideDistX = 0.0;
            var sideDistY = 0.0;
            // Length of the perpendicular ray (to avoid fisheye effect)
            var perpWallDist = 0.0;
            // What direction to step in the grid
            var stepX = 0;
            var stepY = 0;
            // Track which side of the wall was hit (0 for X-side, 1 for Y-side)
            var side = 0;
            // Initialize step and sideDist based on ray direction
            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (posX - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - posX) * deltaDistX;
            }
            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (posY - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - posY) * deltaDistY;
            }
            // --- DDA Algorithm Loop ---
            // Move the ray through the grid until it hits a wall (MAP entry != 0)
            while (MAP[mapX][mapY] == 0) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
            }
            // Calculate distance to the wall projected onto the camera direction
            if (side == 0) {
                perpWallDist = (mapX - posX + (1 - stepX) / 2.0) / rayDirX;
            } else {
                perpWallDist = (mapY - posY + (1 - stepY) / 2.0) / rayDirY;
            }
            // Calculate height of the wall slice based on distance
            final var lineHeight = (int) (height / perpWallDist);
            // Calculate the screen pixels where the wall slice starts and ends
            final var drawStart = Math.max(0, -lineHeight / 2 + height / 2);
            final var drawEnd = Math.min(height - 1, lineHeight / 2 + height / 2);
            // Draw the vertical line representing the wall
            U8g2.drawLine(u8g2, x, drawStart, x, drawEnd);
        }
        U8g2.sendBuffer(u8g2);
    }

    /**
     * Updates player position and handles collisions.
     */
    private void update(final int action) {
        final var rotSpeed = 0.08;
        final var moveSpeed = 0.05;
        switch (action) {
            case 0 -> { // Forward
                if (MAP[(int) (posX + dirX * moveSpeed)][(int) posY] == 0) {
                    posX += dirX * moveSpeed;
                }
                if (MAP[(int) posX][(int) (posY + dirY * moveSpeed)] == 0) {
                    posY += dirY * moveSpeed;
                }
            }
            case 1 -> { // Backward
                if (MAP[(int) (posX - dirX * moveSpeed)][(int) posY] == 0) {
                    posX -= dirX * moveSpeed;
                }
                if (MAP[(int) posX][(int) (posY - dirY * moveSpeed)] == 0) {
                    posY -= dirY * moveSpeed;
                }
            }
            case 2 ->
                rotate(rotSpeed);  // Turn Left
            case 3 ->
                rotate(-rotSpeed); // Turn Right
        }
    }

    /**
     * Rotates the camera using a 2D rotation matrix.
     */
    private void rotate(final double angle) {
        final var cosA = Math.cos(angle);
        final var sinA = Math.sin(angle);
        // Rotate direction vector
        final var oldDirX = dirX;
        dirX = dirX * cosA - dirY * sinA;
        dirY = oldDirX * sinA + dirY * cosA;
        // Rotate camera plane
        final var oldPlaneX = planeX;
        planeX = planeX * cosA - planeY * sinA;
        planeY = oldPlaneX * sinA + planeY * cosA;
    }

    /**
     * Run demo.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        super.call();
        final var display = getDisplay();
        final var frameDelay = 1000L / Math.max(1, fps);
        showText("3D Tour");
        var currentAction = 0;
        for (var i = 0; i < 2000; i++) {
            // Pick a new random movement every 20-40 frames
            if (i % (20 + random.nextInt(20)) == 0) {
                currentAction = random.nextInt(4);
            }
            render();
            update(currentAction);
            display.sleep(frameDelay);
        }
        done();
        return 0;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Raycast())
                .registerConverter(Integer.class, Integer::decode)
                .registerConverter(Long.class, Long::decode)
                .execute(args));
    }
}
