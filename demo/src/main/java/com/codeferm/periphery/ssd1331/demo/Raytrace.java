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
 * 3D Raycasting demo for SSD1331 featuring procedurally stable brick textures and autonomous navigation.
 * <p>
 * This engine utilizes a DDA (Digital Differential Analyzer) algorithm to render a pseudo-3D environment.
 * It implements stable mortar line logic using integer-scaled texture coordinates to prevent aliasing
 * artifacts at acute viewing angles. The navigation system uses a state-based random walk with 
 * collision detection against the world map.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.2.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Raytrace", mixinStandardHelpOptions = true, version = "1.2.0",
        description = "Stable brick-wall raycaster with autonomous random-walk navigation")
public class Raytrace extends Base {

    /**
     * Picocli command specification used to inspect matched CLI options.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Random number generator used for selecting movement states and durations.
     */
    private final Random random = new Random();

    /**
     * World map defining the environment layout. 1 represents a brick wall, 0 is walkable space.
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
     * Executes the rendering loop and movement state machine.
     * <p>
     * Performs per-pixel raycasting. For each wall hit, it calculates fixed-point 
     * texture coordinates to ensure the brick-and-mortar pattern remains visually 
     * consistent as the camera moves.
     * </p>
     *
     * @param oled SSD1331 driver instance used for frame pushing.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth();
        final var height = getHeight();
        final var g2d = getG2d();

        // Player initial position and FOV vectors
        var posX = 4.5; var posY = 4.5;
        var dirX = -1.0; var dirY = 0.0;
        var planeX = 0.0; var planeY = 0.66;

        // Navigation state: 0=Forward, 1=Back, 2=Left, 3=Right
        var moveState = 0;
        var stateTicks = 0;

        final var frameDelay = 1000 / getFps();
        log.info("Starting Stable Brick Raytrace at {} FPS", getFps());

        while (true) {
            final var startTime = System.currentTimeMillis();

            // Render ceiling and floor with ambient dark tones
            g2d.setColor(new Color(15, 15, 15)); g2d.fillRect(0, 0, width, height / 2);
            g2d.setColor(new Color(30, 30, 30)); g2d.fillRect(0, height / 2, width, height / 2);

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

                // Determine DDA step direction and initial side distances
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

                // Ray-casting loop (DDA)
                while (hit == 0) {
                    if (sideDistX < sideDistY) {
                        sideDistX += deltaDistX; mapX += stepX; side = 0;
                    } else {
                        sideDistY += deltaDistY; mapY += stepY; side = 1;
                    }
                    if (worldMap[mapX][mapY] > 0) hit = 1;
                }

                // Project distance onto camera plane to avoid fisheye effect
                final var perpWallDist = (side == 0) ? (sideDistX - deltaDistX) : (sideDistY - deltaDistY);
                final var lineHeight = (int) (height / perpWallDist);
                final var drawStart = Math.max(0, -lineHeight / 2 + height / 2);
                final var drawEnd = Math.min(height - 1, lineHeight / 2 + height / 2);

                // Calculate horizontal texture coordinate (0.0 to 1.0)
                var wallX = (side == 0) ? posY + perpWallDist * rayDirY : posX + perpWallDist * rayDirX;
                wallX -= Math.floor(wallX);
                
                // Fixed-point horizontal coordinate for stable vertical lines
                final var texX = (int) (wallX * 64.0);
                final var distIntensity = Math.clamp(1.5 / (1.0 + perpWallDist * 0.8), 0.1, 1.0);

                for (var y = drawStart; y <= drawEnd; y++) {
                    // Fixed-point vertical coordinate for stable horizontal lines
                    final var texY = (int) (64.0 * (y - (-lineHeight / 2.0 + height / 2.0)) / lineHeight);
                    
                    // Procedural brick pattern logic:
                    // Stretcher bond pattern (offset vertical lines based on row)
                    final var brickRow = texY / 16;
                    final var isHorizontalMortar = (texY % 16 == 0);
                    final var isVerticalMortar = ((texX + (brickRow % 2 * 16)) % 32 == 0);
                    final var isMortar = isHorizontalMortar || isVerticalMortar;
                    
                    // Apply lighting: Top is 100% light, bottom is 50%, scaled by distance
                    final var verticalFactor = (double) (y - drawStart) / (drawEnd - drawStart + 1);
                    final var shadeFactor = (1.0 - (verticalFactor * 0.5)) * distIntensity;

                    if (isMortar) {
                        final var gray = (int) (150 * shadeFactor);
                        g2d.setColor(new Color(gray, gray, gray));
                    } else {
                        var r = (int) (165 * shadeFactor);
                        var g = (int) (55 * shadeFactor);
                        var b = (int) (45 * shadeFactor);
                        // Darken Y-axis walls for orientation depth
                        if (side == 1) { r /= 1.35; g /= 1.35; b /= 1.35; }
                        g2d.setColor(new Color(Math.clamp(r, 0, 255), Math.clamp(g, 0, 255), Math.clamp(b, 0, 255)));
                    }
                    g2d.drawLine(x, y, x, y);
                }
            }

            // Flush the back-buffer to the OLED display
            oled.drawImage(getImage());

            // --- Autonomous Navigation State Machine ---
            final var moveSpeed = 0.08;
            final var rotSpeed = 0.06;

            if (stateTicks <= 0) {
                moveState = random.nextInt(4);
                stateTicks = 15 + random.nextInt(40);
            }

            switch (moveState) {
                case 0 -> { // Forward move with collision check
                    if (worldMap[(int) (posX + dirX * moveSpeed)][(int) posY] == 0) posX += dirX * moveSpeed;
                    if (worldMap[(int) posX][(int) (posY + dirY * moveSpeed)] == 0) posY += dirY * moveSpeed;
                }
                case 1 -> { // Backward move with collision check
                    if (worldMap[(int) (posX - dirX * moveSpeed)][(int) posY] == 0) posX -= dirX * moveSpeed;
                    if (worldMap[(int) posX][(int) (posY - dirY * moveSpeed)] == 0) posY -= dirY * moveSpeed;
                }
                case 2, 3 -> { // Rotation (Left/Right)
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

            // Maintain target framerate
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
     * Initializes hardware and starts the Raytrace demo.
     * <p>
     * Implements context-aware FPS: Defaults to 30 FPS for this CPU-intensive task 
     * unless the user explicitly passes the {@code -f} option.
     * </p>
     *
     * @return Exit code (0 for success).
     * @throws Exception If SPI or GPIO initialization fails.
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
     * Entry point for the Raytrace application.
     *
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Raytrace()).execute(args));
    }
}
