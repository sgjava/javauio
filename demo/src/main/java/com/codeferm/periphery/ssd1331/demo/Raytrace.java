/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.ssd1331.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.Color;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * 3D Raycasting demo for SSD1331 featuring procedural brick textures and autonomous navigation.
 * <p>
 * This engine renders a Wolfenstein 3D-style environment. It uses the DDA algorithm for raycasting,
 * applying a vertical gradient and a calculated brick/mortar pattern to wall surfaces. The navigation 
 * system performs a random walk with collision detection to ensure the bot explores the map effectively.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Raytrace", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Brick-wall raycaster with random-walk navigation for SSD1331")
public class Raytrace extends Base {

    /**
     * Picocli command spec for inspecting parse results.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Random generator for autonomous movement logic.
     */
    private final Random random = new Random();

    /**
     * World map grid: 1 represents a brick wall, 0 represents empty space.
     */
    private final int[][] worldMap = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 1, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    /**
     * Main rendering loop and autonomous navigation controller.
     * <p>
     * Implements a procedural brick texture by checking the intersection coordinates (wallX) 
     * and vertical projection (texY) to draw mortar lines over a "Brick Red" gradient.
     * </p>
     *
     * @param oled SSD1331 driver instance.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth();
        final var height = getHeight();
        final var g2d = getG2d();

        // Player starting position and direction
        var posX = 4.5; var posY = 4.5;
        var dirX = -1.0; var dirY = 0.0;
        var planeX = 0.0; var planeY = 0.66;

        // Navigation state: 0:Forward, 1:Back, 2:Left, 3:Right
        var moveState = 0;
        var stateTicks = 0;

        final var frameDelay = 1000 / getFps();
        log.info("Starting Brick Raytrace at {} FPS", getFps());

        while (true) {
            final var startTime = System.currentTimeMillis();

            // Render ceiling and floor with static dark colors
            g2d.setColor(new Color(20, 20, 20)); g2d.fillRect(0, 0, width, height / 2);
            g2d.setColor(new Color(40, 40, 40)); g2d.fillRect(0, height / 2, width, height / 2);

            for (var x = 0; x < width; x++) {
                final var cameraX = 2.0 * x / (double) width - 1.0;
                final var rayDirX = dirX + planeX * cameraX;
                final var rayDirY = dirY + planeY * cameraX;

                var mapX = (int) posX; var mapY = (int) posY;
                final var deltaDistX = Math.abs(1 / rayDirX);
                final var deltaDistY = Math.abs(1 / rayDirY);

                var sideDistX = 0.0; var sideDistY = 0.0;
                var stepX = 0; var stepY = 0;
                var hit = 0; var side = 0;

                // Calculate DDA steps
                if (rayDirX < 0) {
                    stepX = -1; sideDistX = (posX - mapX) * deltaDistX;
                } else {
                    stepX = 1; sideDistX = (mapX + 1.0 - posX) * deltaDistX;
                }
                if (rayDirY < 0) {
                    stepY = -1; sideDistY = (posY - mapY) * deltaDistY;
                } else {
                    stepY = 1; sideDistY = (mapY + 1.0 - posY) * deltaDistY;
                }

                // Execute DDA
                while (hit == 0) {
                    if (sideDistX < sideDistY) {
                        sideDistX += deltaDistX; mapX += stepX; side = 0;
                    } else {
                        sideDistY += deltaDistY; mapY += stepY; side = 1;
                    }
                    if (worldMap[mapX][mapY] > 0) hit = 1;
                }

                // Calculate distance and projected wall height
                final var perpWallDist = (side == 0) ? (sideDistX - deltaDistX) : (sideDistY - deltaDistY);
                final var lineHeight = (int) (height / perpWallDist);
                final var drawStart = Math.max(0, -lineHeight / 2 + height / 2);
                final var drawEnd = Math.min(height - 1, lineHeight / 2 + height / 2);

                // Calculate horizontal wall hit position (0.0 to 1.0) for brick pattern
                var wallX = (side == 0) ? posY + perpWallDist * rayDirY : posX + perpWallDist * rayDirX;
                wallX -= Math.floor(wallX);

                // Distance-based intensity scaling
                final var distIntensity = Math.clamp(1.5 / (1.0 + perpWallDist * 0.8), 0.1, 1.0);

                // Per-pixel column rendering
                for (var y = drawStart; y <= drawEnd; y++) {
                    // Vertical texture coordinate for pattern generation
                    final var texY = (int) (64 * (y - (-lineHeight / 2 + height / 2)) / lineHeight);
                    
                    // Procedural brick logic: Horizontal mortar every 16 units, vertical every 32 with row offset
                    final var isMortar = (texY % 16 == 0) || ((int) (wallX * 64 + (texY / 16 % 2 * 16)) % 32 == 0);
                    
                    final var verticalFactor = (double) (y - drawStart) / (drawEnd - drawStart + 1);
                    final var shadeFactor = (1.0 - (verticalFactor * 0.5)) * distIntensity;

                    if (isMortar) {
                        // Light grey mortar color
                        final var gray = (int) (160 * shadeFactor);
                        g2d.setColor(new Color(gray, gray, gray));
                    } else {
                        // Brick Red color base
                        var r = (int) (160 * shadeFactor);
                        var g = (int) (55 * shadeFactor);
                        var b = (int) (45 * shadeFactor);
                        // Darken Y-axis walls for depth contrast
                        if (side == 1) { r /= 1.3; g /= 1.3; b /= 1.3; }
                        g2d.setColor(new Color(Math.clamp(r, 0, 255), Math.clamp(g, 0, 255), Math.clamp(b, 0, 255)));
                    }
                    g2d.drawLine(x, y, x, y);
                }
            }

            oled.drawImage(getImage());

            // --- Autonomous Navigation Logic ---
            final var moveSpeed = 0.08;
            final var rotSpeed = 0.06;

            if (stateTicks <= 0) {
                moveState = random.nextInt(4);
                stateTicks = 10 + random.nextInt(35);
            }

            switch (moveState) {
                case 0 -> { // Forward
                    if (worldMap[(int) (posX + dirX * moveSpeed)][(int) posY] == 0) posX += dirX * moveSpeed;
                    if (worldMap[(int) posX][(int) (posY + dirY * moveSpeed)] == 0) posY += dirY * moveSpeed;
                }
                case 1 -> { // Backward
                    if (worldMap[(int) (posX - dirX * moveSpeed)][(int) posY] == 0) posX -= dirX * moveSpeed;
                    if (worldMap[(int) posX][(int) (posY - dirY * moveSpeed)] == 0) posY -= dirY * moveSpeed;
                }
                case 2, 3 -> { // Rotation
                    final var rot = (moveState == 2) ? 1.0 : -1.0;
                    final var oldDirX = dirX;
                    dirX = dirX * Math.cos(rot * rotSpeed) - dirY * Math.sin(rot * rotSpeed);
                    dirY = oldDirX * Math.sin(rot * rotSpeed) + dirY * Math.cos(rot * rotSpeed);
                    final var oldPlaneX = planeX;
                    planeX = planeX * Math.cos(rot * rotSpeed) - planeY * Math.sin(rot * rotSpeed);
                    planeY = oldPlaneX * Math.sin(rot * rotSpeed) + planeY * Math.cos(rot * rotSpeed);
                }
            }
            stateTicks--;

            // Frame synchronization
            final var diff = System.currentTimeMillis() - startTime;
            if (diff < frameDelay) {
                try {
                    TimeUnit.MILLISECONDS.sleep(frameDelay - diff);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * Executes the Raytrace demo. 
     * <p>
     * Detects CLI-provided FPS; defaults to 30 FPS for this demo if not specified.
     * </p>
     *
     * @return Exit code.
     * @throws Exception Hardware exception.
     */
    @Override
    public Integer call() throws Exception {
        final var fpsMatched = spec.commandLine().getParseResult().hasMatchedOption("f");
        if (!fpsMatched) {
            setFps(30);
        }
        super.call();
        runDemo(getOled());
        done();
        return 0;
    }

    /**
     * Main method.
     *
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Raytrace()).execute(args));
    }
}
