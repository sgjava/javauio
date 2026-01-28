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
 * 3D Raycasting demo for SSD1331 featuring procedural irregular castle stone facades.
 * <p>
 * This engine generates a "Castle" look by using coordinate hashing to jitter mortar lines
 * and randomize stone dimensions. It avoids the uniform red-brick grid by using an earthy 
 * palette and distance-compensated line thickness.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.5.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Raytrace", mixinStandardHelpOptions = true, version = "1.5.0",
        description = "Castle stone raycaster with randomized irregular masonry")
public class Raytrace extends Base {

    /**
     * Picocli command specification for CLI option parsing.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Random generator for autonomous navigation states.
     */
    private final Random random = new Random();

    /**
     * World map: 1 represents a castle stone wall, 0 represents walkable space.
     */
    private final int[][] worldMap = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 0, 1, 1, 0, 1},
        {1, 0, 1, 0, 1, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 1, 1, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 1, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 0, 1, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    /**
     * Renders the scene and manages the random-walk navigation.
     * <p>
     * Implements a "Castle Facade" algorithm using bitwise hashing of map and texture 
     * coordinates to create non-uniform stone blocks.
     * </p>
     *
     * @param oled SSD1331 hardware driver.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth();
        final var height = getHeight();
        final var g2d = getG2d();

        var posX = 4.5; var posY = 4.5;
        var dirX = -1.0; var dirY = 0.0;
        var planeX = 0.0; var planeY = 0.66;

        var moveState = 0;
        var stateTicks = 0;

        final var frameDelay = 1000 / getFps();
        log.info("Starting Castle Stone Raytrace at {} FPS", getFps());

        while (true) {
            final var startTime = System.currentTimeMillis();

            // Background colors: very dark grey to near black
            g2d.setColor(new Color(12, 12, 12)); g2d.fillRect(0, 0, width, height / 2);
            g2d.setColor(new Color(22, 22, 22)); g2d.fillRect(0, height / 2, width, height / 2);

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

                while (hit == 0) {
                    if (sideDistX < sideDistY) {
                        sideDistX += deltaDistX; mapX += stepX; side = 0;
                    } else {
                        sideDistY += deltaDistY; mapY += stepY; side = 1;
                    }
                    if (worldMap[mapX][mapY] > 0) hit = 1;
                }

                final var perpWallDist = (side == 0) ? (sideDistX - deltaDistX) : (sideDistY - deltaDistY);
                final var lineHeight = (int) (height / perpWallDist);
                final var drawStart = Math.max(0, -lineHeight / 2 + height / 2);
                final var drawEnd = Math.min(height - 1, lineHeight / 2 + height / 2);

                var wallX = (side == 0) ? posY + perpWallDist * rayDirY : posX + perpWallDist * rayDirX;
                wallX -= Math.floor(wallX);

                final var texX = (int) (wallX * 128.0);
                final var distIntensity = Math.clamp(1.3 / (1.0 + perpWallDist * 0.7), 0.05, 1.0);
                final var thickComp = (int) Math.max(1, perpWallDist / 2.0);

                // Unique seed for this specific wall segment
                final var wallSeed = (mapX * 73 + mapY * 31);

                for (var y = drawStart; y <= drawEnd; y++) {
                    final var texY = (int) (128.0 * (y - (-lineHeight / 2.0 + height / 2.0)) / lineHeight);
                    
                    // Create irregular stone rows by shifting texY with a bit of wallSeed noise
                    final var noisyTexY = texY + ((wallSeed ^ (texY >> 4)) & 7);
                    final var stoneRow = noisyTexY / 28; 
                    
                    // Jitter the vertical mortar lines based on which row we are in
                    final var horizontalJitter = (wallSeed ^ (stoneRow * 127)) & 63;
                    
                    final var isHorizontalMortar = (noisyTexY % 28 < thickComp);
                    final var isVerticalMortar = ((texX + horizontalJitter) % 48 < thickComp);
                    
                    final var isMortar = isHorizontalMortar || isVerticalMortar;
                    final var verticalFactor = (double) (y - drawStart) / (drawEnd - drawStart + 1);
                    final var shadeFactor = (1.0 - (verticalFactor * 0.45)) * distIntensity;

                    if (isMortar) {
                        // Dark mortar: Deep brownish-grey
                        final var mVal = (int) (70 * shadeFactor);
                        g2d.setColor(new Color(mVal, (int) (mVal * 0.9), (int) (mVal * 0.8)));
                    } else {
                        // Castle Palette: Brown, Tan, and Grey stone tones
                        // Use a hash of the stone coordinates to vary the color of individual stones
                        final var stoneHash = (wallSeed ^ (stoneRow * 53) ^ ((texX + horizontalJitter) / 48)) & 3;
                        
                        var r = 0; var g = 0; var b = 0;
                        switch (stoneHash) {
                            case 0 -> { r = 100; g = 80; b = 65; } // Brown stone
                            case 1 -> { r = 90; g = 85; b = 80; }  // Grey stone
                            case 2 -> { r = 115; g = 100; b = 80; } // Tan stone
                            default -> { r = 80; g = 70; b = 60; }  // Darker stone
                        }
                        
                        // Per-pixel noise for weathered look
                        final var grain = ((texX ^ texY) & 3) * 5;
                        r = (int) ((r - grain) * shadeFactor);
                        g = (int) ((g - grain) * shadeFactor);
                        b = (int) ((b - grain) * shadeFactor);

                        if (side == 1) { r /= 1.4; g /= 1.4; b /= 1.4; }
                        g2d.setColor(new Color(Math.clamp(r, 0, 255), Math.clamp(g, 0, 255), Math.clamp(b, 0, 255)));
                    }
                    g2d.drawLine(x, y, x, y);
                }
            }

            oled.drawImage(getImage());

            // --- Movement Logic ---
            final var mSpeed = 0.07;
            final var rSpeed = 0.05;
            if (stateTicks <= 0) {
                moveState = random.nextInt(4);
                stateTicks = 20 + random.nextInt(40);
            }
            switch (moveState) {
                case 0 -> { // Forward
                    if (worldMap[(int) (posX + dirX * mSpeed)][(int) posY] == 0) posX += dirX * mSpeed;
                    if (worldMap[(int) posX][(int) (posY + dirY * mSpeed)] == 0) posY += dirY * mSpeed;
                }
                case 1 -> { // Backward
                    if (worldMap[(int) (posX - dirX * mSpeed)][(int) posY] == 0) posX -= dirX * mSpeed;
                    if (worldMap[(int) posX][(int) (posY - dirY * mSpeed)] == 0) posY -= dirY * mSpeed;
                }
                case 2, 3 -> { // Rotation
                    final var rot = (moveState == 2) ? 1.0 : -1.0;
                    final var oldDirX = dirX;
                    dirX = dirX * Math.cos(rot * rSpeed) - dirY * Math.sin(rot * rSpeed);
                    dirY = oldDirX * Math.sin(rot * rSpeed) + dirY * Math.cos(rot * rSpeed);
                    final var oldPlaneX = planeX;
                    planeX = planeX * Math.cos(rot * rSpeed) - planeY * Math.sin(rot * rSpeed);
                    planeY = oldPlaneX * Math.sin(rot * rSpeed) + planeY * Math.cos(rot * rSpeed);
                }
            }
            stateTicks--;

            final var diff = System.currentTimeMillis() - startTime;
            if (diff < frameDelay) {
                try { TimeUnit.MILLISECONDS.sleep(frameDelay - diff); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Initializes hardware and executes the raytrace demo.
     *
     * @return Exit code.
     * @throws Exception If hardware setup fails.
     */
    @Override
    public Integer call() throws Exception {
        if (!spec.commandLine().getParseResult().hasMatchedOption("f")) {
            setFps(30);
        }
        super.call();
        runDemo(getOled());
        done();
        return 0;
    }

    /**
     * Entry point.
     *
     * @param args CLI arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Raytrace()).execute(args));
    }
}
