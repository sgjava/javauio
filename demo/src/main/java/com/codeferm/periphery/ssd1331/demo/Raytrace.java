/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.ssd1331.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * 3D Raycasting demo for SSD1331 featuring distance-based color scaling and shading.
 * <p>
 * This "Doom-style" engine renders a pseudo-3D environment using the Digital Differential Analyzer (DDA) algorithm.
 * It utilizes the SSD1331's 65k color depth to provide smooth gradients for depth shading and lighting contrast.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Raytrace", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Doom-style raycaster with color depth shading for SSD1331")
public class Raytrace extends Base {

    /**
     * Picocli command spec for inspecting parse results.
     */
    @Spec
    private CommandSpec spec;

    /**
     * World map: 1-4 represent different wall colors, 0 represents empty space.
     */
    private final int[][] worldMap = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 2, 2, 0, 3, 3, 0, 0, 1},
        {1, 0, 2, 0, 0, 0, 3, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 4, 0, 0, 4, 0, 0, 1},
        {1, 0, 0, 4, 4, 4, 4, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    /**
     * Renders a single frame of the 3D world.
     * <p>
     * Uses DDA to calculate wall distances and applies a shading model based on distance
     * and wall orientation (side shading).
     * </p>
     *
     * @param oled SSD1331 driver instance.
     */
    public void render(final Ssd1331 oled) {
        final var width = getWidth();
        final var height = getHeight();
        final var g2d = getG2d();

        // Player starting position and direction
        var posX = 4.0; var posY = 4.0;
        var dirX = -1.0; var dirY = 0.0;
        var planeX = 0.0; var planeY = 0.66; // Camera plane (FOV)

        final var frameDelay = 1000 / getFps();
        log.info("Starting Raytrace Walkthrough at {} FPS", getFps());

        while (true) {
            final var startTime = System.currentTimeMillis();

            // Clear frame with basic ceiling and floor colors
            g2d.setColor(new Color(25, 25, 25)); // Ceiling
            g2d.fillRect(0, 0, width, height / 2);
            g2d.setColor(new Color(45, 45, 45)); // Floor
            g2d.fillRect(0, height / 2, width, height / 2);

            for (var x = 0; x < width; x++) {
                final var cameraX = 2.0 * x / (double) width - 1.0;
                final var rayDirX = dirX + planeX * cameraX;
                final var rayDirY = dirY + planeY * cameraX;

                var mapX = (int) posX;
                var mapY = (int) posY;

                final var deltaDistX = Math.abs(1 / rayDirX);
                final var deltaDistY = Math.abs(1 / rayDirY);

                var sideDistX = 0.0; var sideDistY = 0.0;
                var stepX = 0; var stepY = 0;
                var hit = 0; var side = 0;

                // Step and initial side distances
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

                // DDA search
                while (hit == 0) {
                    if (sideDistX < sideDistY) {
                        sideDistX += deltaDistX; mapX += stepX; side = 0;
                    } else {
                        sideDistY += deltaDistY; mapY += stepY; side = 1;
                    }
                    if (worldMap[mapX][mapY] > 0) hit = 1;
                }

                // Calculate wall distance and projection height
                final var perpWallDist = (side == 0) ? (sideDistX - deltaDistX) : (sideDistY - deltaDistY);
                final var lineHeight = (int) (height / perpWallDist);

                var drawStart = Math.max(0, -lineHeight / 2 + height / 2);
                var drawEnd = Math.min(height - 1, lineHeight / 2 + height / 2);

                // Determine base color from map
                final var mapValue = worldMap[mapX][mapY];
                var color = switch (mapValue) {
                    case 1 -> new Color(220, 0, 0);   // Red
                    case 2 -> new Color(0, 220, 0);   // Green
                    case 3 -> new Color(0, 0, 220);   // Blue
                    default -> new Color(220, 220, 0); // Yellow
                };

                // --- Depth Shading / Scaling ---
                // Diminish intensity based on distance
                final var intensity = Math.clamp(1.2 / (1.0 + perpWallDist * 0.7), 0.1, 1.0);
                
                var r = (int) (color.getRed() * intensity);
                var g = (int) (color.getGreen() * intensity);
                var b = (int) (color.getBlue() * intensity);

                // Darken Y-aligned walls for 3D shadowing/contrast
                if (side == 1) { r /= 1.5; g /= 1.5; b /= 1.5; }

                g2d.setColor(new Color(r, g, b));
                g2d.drawLine(x, drawStart, x, drawEnd);
            }

            // Render to OLED
            oled.drawImage(getImage());

            // Rotation for walkthrough effect
            final var rotSpeed = 0.045;
            final var oldDirX = dirX;
            dirX = dirX * Math.cos(rotSpeed) - dirY * Math.sin(rotSpeed);
            dirY = oldDirX * Math.sin(rotSpeed) + dirY * Math.cos(rotSpeed);
            final var oldPlaneX = planeX;
            planeX = planeX * Math.cos(rotSpeed) - planeY * Math.sin(rotSpeed);
            planeY = oldPlaneX * Math.sin(rotSpeed) + planeY * Math.cos(rotSpeed);

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
     * Executes the raytrace demo.
     * <p>
     * Overrides the default FPS to 30 if not specified via CLI, maintaining standard behavior
     * for other demos via {@link Base}.
     * </p>
     *
     * @return Exit code.
     * @throws Exception Hardware exception.
     */
    @Override
    public Integer call() throws Exception {
        // Detect if user specifically requested an FPS value
        final var fpsMatched = spec.commandLine().getParseResult().hasMatchedOption("f");
        
        // Default to 30 FPS for Raytrace if no argument provided
        if (!fpsMatched) {
            setFps(30);
        }

        super.call();
        render(getOled());
        done();
        return 0;
    }

    public static void main(final String... args) {
        System.exit(new CommandLine(new Raytrace()).execute(args));
    }
}
