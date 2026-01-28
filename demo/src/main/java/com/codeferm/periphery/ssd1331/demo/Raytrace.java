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
 * 3D Raycasting demo for SSD1331 featuring irregular "Castle Ashlar" stone facades.
 * <p>
 * This engine renders an old-school dungeon environment. It uses coordinate-based 
 * hashing to generate irregular stone sizes and varying earthy tones. The navigation 
 * system is tuned to prioritize forward exploration to eliminate long pauses.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.6.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Raytrace", mixinStandardHelpOptions = true, version = "1.6.0",
        description = "Castle raycaster with improved navigation and irregular masonry")
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
     * World map grid: 1 represents a stone wall, 0 represents walkable space.
     */
    private final int[][] worldMap = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 0, 1, 1, 0, 1},
        {1, 0, 1, 0, 1, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 1, 1, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    /**
     * Main rendering and exploration loop.
     * <p>
     * Features "Stretcher-Jitter" logic to create irregular stone blocks and a 
     * weighted random-walk to ensure continuous movement.
     * </p>
     *
     * @param oled SSD1331 driver instance.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth(); //
        final var height = getHeight(); //
        final var g2d = getG2d(); //

        // Player state
        var posX = 4.5; var posY = 1.5;
        var dirX = -1.0; var dirY = 0.0;
        var planeX = 0.0; var planeY = 0.66;

        // Navigation state
        var moveState = 0; // 0: Forward, 1: Back, 2: Left, 3: Right
        var stateTicks = 0;

        final var frameDelay = 1000 / getFps(); //
        log.info("Starting Castle Exploration at {} FPS", getFps());

        while (true) {
            final var startTime = System.currentTimeMillis();

            // Clear ceiling/floor
            g2d.setColor(new Color(15, 12, 10)); g2d.fillRect(0, 0, width, height / 2);
            g2d.setColor(new Color(25, 25, 25)); g2d.fillRect(0, height / 2, width, height / 2);

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
                final var distIntensity = Math.clamp(1.4 / (1.0 + perpWallDist * 0.8), 0.1, 1.0);
                final var thickComp = (int) Math.max(1, perpWallDist / 2.0);
                
                // Wall-specific noise seed
                final var wallSeed = (mapX * 59 + mapY * 43);

                for (var y = drawStart; y <= drawEnd; y++) {
                    final var texY = (int) (128.0 * (y - (-lineHeight / 2.0 + height / 2.0)) / lineHeight);
                    
                    // Procedural Castle Stone Logic
                    // Vary stone row height and horizontal offset based on map position
                    final var rowHeight = 24 + (wallSeed % 8);
                    final var stoneRow = texY / rowHeight;
                    final var rowOffset = (wallSeed ^ (stoneRow * 97)) & 63;
                    
                    final var isHorizontalMortar = (texY % rowHeight < thickComp);
                    final var isVerticalMortar = ((texX + rowOffset) % 48 < thickComp);
                    
                    final var isMortar = isHorizontalMortar || isVerticalMortar;
                    final var shadeFactor = (1.0 - ((double)(y - drawStart) / (drawEnd - drawStart + 1) * 0.5)) * distIntensity;

                    if (isMortar) {
                        final var m = (int) (60 * shadeFactor);
                        g2d.setColor(new Color(m, (int)(m*0.9), (int)(m*0.8)));
                    } else {
                        // Earthy stone palette: rotate between Tan, Brown, and Grey
                        final var stoneHash = (wallSeed ^ stoneRow ^ ((texX + rowOffset) / 48)) & 3;
                        var r = 0; var g = 0; var b = 0;
                        switch (stoneHash) {
                            case 0 -> { r = 90; g = 70; b = 55; }  // Dark Brown
                            case 1 -> { r = 110; g = 100; b = 85; } // Tan
                            case 2 -> { r = 80; g = 80; b = 85; }   // Grey
                            default -> { r = 70; g = 60; b = 50; }  // Deep Shadow
                        }
                        
                        // Pixel noise for texture
                        final var grain = ((texX ^ texY) & 7) * 4;
                        r = (int) Math.clamp((r - grain) * shadeFactor, 0, 255);
                        g = (int) Math.clamp((g - grain) * shadeFactor, 0, 255);
                        b = (int) Math.clamp((b - grain) * shadeFactor, 0, 255);

                        if (side == 1) { r /= 1.4; g /= 1.4; b /= 1.4; }
                        g2d.setColor(new Color(r, g, b));
                    }
                    g2d.drawLine(x, y, x, y);
                }
            }

            oled.drawImage(getImage()); //

            // --- Optimized Navigation (Weighted to reduce pauses) ---
            final var moveSpeed = 0.08;
            final var rotSpeed = 0.06;

            if (stateTicks <= 0) {
                final var roll = random.nextInt(10);
                if (roll < 6) moveState = 0;      // 60% chance: Forward
                else if (roll < 7) moveState = 1; // 10% chance: Back
                else if (roll < 9) moveState = 2; // 20% chance: Left
                else moveState = 3;               // 10% chance: Right
                
                stateTicks = 10 + random.nextInt(25); // Shorter cycles to keep things snappy
            }

            switch (moveState) {
                case 0 -> {
                    if (worldMap[(int) (posX + dirX * moveSpeed)][(int) posY] == 0) posX += dirX * moveSpeed;
                    if (worldMap[(int) posX][(int) (posY + dirY * moveSpeed)] == 0) posY += dirY * moveSpeed;
                }
                case 1 -> {
                    if (worldMap[(int) (posX - dirX * moveSpeed)][(int) posY] == 0) posX -= dirX * moveSpeed;
                    if (worldMap[(int) posX][(int) (posY - dirY * moveSpeed)] == 0) posY -= dirY * moveSpeed;
                }
                case 2, 3 -> {
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

            final var diff = System.currentTimeMillis() - startTime;
            if (diff < frameDelay) {
                try { TimeUnit.MILLISECONDS.sleep(frameDelay - diff); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Entry point for CLI execution.
     */
    @Override
    public Integer call() throws Exception {
        if (!spec.commandLine().getParseResult().hasMatchedOption("f")) {
            setFps(30);
        }
        super.call(); //
        runDemo(getOled()); //
        done(); //
        return 0;
    }

    public static void main(final String... args) {
        System.exit(new CommandLine(new Raytrace()).execute(args));
    }
}
