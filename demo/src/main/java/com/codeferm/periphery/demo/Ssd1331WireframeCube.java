/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * 3D Wireframe Cube for SSD1331 with phasing color transitions.
 * <p>
 * This demo renders a rotating 3D cube that smoothly cycles through colors (Red -> Green -> Blue) using sine-wave oscillators.
 * It utilizes {@code BufferedImage} for frame preparation and {@code Graphics2D} for high-quality rendering before pushing 
 * to the SSD1331 hardware.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Ssd1331WireframeCube", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Bouncing 3D cube with phasing colors for SSD1331")
public class Ssd1331WireframeCube extends Ssd1331Base {

    /**
     * Picocli command spec used to differentiate between default FPS and user-provided FPS.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Random number generator for movement randomization.
     */
    private final Random random = new Random();

    /**
     * 3D vertices for a unit cube (coordinates from -1 to 1).
     */
    private final double[][] vertices = {
        {-1, -1, 1}, {1, -1, 1}, {1, 1, 1}, {-1, 1, 1},
        {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1}
    };

    /**
     * The 12 edges connecting the 8 vertices of the cube.
     */
    private final int[][] edges = {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7}
    };

    /**
     * Calculates a phasing RGB color based on an oscillator input.
     * <p>
     * Uses three sine waves shifted by 120 degrees (2π/3) to create a smooth transition through the color spectrum.
     * </p>
     *
     * @param time The current animation time/angle used as the oscillator input.
     * @return A {@link Color} object representing the phased color.
     */
    private Color getPhasingColor(final double time) {
        // Frequency of the color cycle
        final var frequency = 0.5;
        // Shift phases for R, G, and B to create the cycling effect
        final var r = (int) (Math.sin(frequency * time + 0) * 127 + 128);
        final var g = (int) (Math.sin(frequency * time + 2 * Math.PI / 3) * 127 + 128);
        final var b = (int) (Math.sin(frequency * time + 4 * Math.PI / 3) * 127 + 128);
        return new Color(Math.clamp(r, 0, 255), Math.clamp(g, 0, 255), Math.clamp(b, 0, 255));
    }

    /**
     * Main animation loop for the rotating, bouncing, and color-phasing cube.
     * <p>
     * Renders to a {@code BufferedImage} to ensure flicker-free updates on the SSD1331.
     * </p>
     *
     * @param oled SSD1331 driver instance.
     */
    public void drawCube(final Ssd1331 oled) {
        final var screenW = getWidth();
        final var screenH = getHeight();
        final var image = new BufferedImage(screenW, screenH, BufferedImage.TYPE_INT_RGB);
        final var g2d = image.createGraphics();

        // Animation state
        var angleX = 0.0;
        var angleY = 0.0;
        var angleZ = 0.0;
        var posX = (double) screenW / 2.0;
        var posY = (double) screenH / 2.0;

        // Physics/Movement state
        final var targetSpeed = 1.3;
        var velX = 1.0;
        var velY = 0.7;
        final var cameraDistance = 3.6;
        final var projectionScale = screenH * 0.60;
        final var frameDelay = 1000 / getFps();

        log.info("Starting Phasing Wireframe Cube at {} FPS", getFps());

        while (true) {
            final var startTime = System.currentTimeMillis();
            
            // Clear background to black
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, screenW, screenH);

            // Set the phasing color for the cube edges
            g2d.setColor(getPhasingColor(angleX));

            final var projected = new int[8][2];
            var minX = 1000; var maxX = -1000;
            var minY = 1000; var maxY = -1000;

            for (var i = 0; i < 8; i++) {
                final var x = vertices[i][0];
                final var y = vertices[i][1];
                final var z = vertices[i][2];

                // --- 3D Rotation ---
                final var cX = Math.cos(angleX); final var sX = Math.sin(angleX);
                final var y1 = y * cX - z * sX; final var z1 = y * sX + z * cX;
                final var cY = Math.cos(angleY); final var sY = Math.sin(angleY);
                final var x2 = x * cY + z1 * sY; final var z2 = -x * sY + z1 * cY;
                final var cZ = Math.cos(angleZ); final var sZ = Math.sin(angleZ);
                final var x3 = x2 * cZ - y1 * sZ; final var y3 = x2 * sZ + y1 * cZ;

                // --- Perspective Projection ---
                final var pZ = z2 + cameraDistance;
                final var offX = (int) (x3 * projectionScale / pZ);
                final var offY = (int) (y3 * projectionScale / pZ);

                // Update bounding box for collision detection
                if (offX < minX) minX = offX; if (offX > maxX) maxX = offX;
                if (offY < minY) minY = offY; if (offY > maxY) maxY = offY;

                projected[i][0] = (int) (posX + offX);
                projected[i][1] = (int) (posY + offY);
            }

            // Draw edges
            for (final var edge : edges) {
                g2d.drawLine(projected[edge[0]][0], projected[edge[0]][1],
                             projected[edge[1]][0], projected[edge[1]][1]);
            }

            // Flush buffer to hardware
            oled.drawImage(image);

            // Update rotation and position
            angleX += 0.035; angleY += 0.05; angleZ += 0.025;
            posX += velX; posY += velY;

            // Handle bouncing and randomization
            var bounced = false;
            if (posX + minX <= 0 || posX + maxX >= screenW) {
                velX = -velX;
                posX = (posX + minX <= 0) ? -minX + 1 : screenW - maxX - 1;
                bounced = true;
            }
            if (posY + minY <= 0 || posY + maxY >= screenH) {
                velY = -velY;
                posY = (posY + minY <= 0) ? -minY + 1 : screenH - maxY - 1;
                bounced = true;
            }
            if (bounced) {
                final var newAngle = Math.atan2(velY, velX) + (random.nextDouble() - 0.5) * 0.3;
                velX = Math.cos(newAngle) * targetSpeed;
                velY = Math.sin(newAngle) * targetSpeed;
            }

            // Enforce target FPS
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
        g2d.dispose();
    }

    /**
     * Application entry point for the demo.
     * <p>
     * Initializes hardware and manages the animation lifecycle. If the user does not provide an FPS via CLI,
     * it defaults to 30.
     * </p>
     *
     * @return Exit code.
     * @throws Exception If hardware initialization fails.
     */
    @Override
    public Integer call() throws Exception {
        // Use spec to detect if the user explicitly provided -f/--fps
        final var fpsMatched = spec.commandLine().getParseResult().hasMatchedOption("f");
        
        // If not passed, override base default (60) to 30 for this specific demo
        if (!fpsMatched) {
            setFps(30);
        }

        super.call();
        drawCube(getOled());
        done();
        return 0;
    }

    /**
     * Main method.
     *
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Ssd1331WireframeCube()).execute(args));
    }
}
