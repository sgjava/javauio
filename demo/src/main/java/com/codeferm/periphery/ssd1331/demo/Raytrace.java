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
 * Raytrace demo for SSD1331 featuring a shaded 3D sphere.
 * <p>
 * This demo calculates diffuse (Lambertian) shading for each pixel in real-time. It uses the 65k color depth 
 * to render smooth gradients based on a moving light source.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Raytrace", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Shaded Raytrace sphere demo for SSD1331")
public class Raytrace extends Base {

    @Spec
    private CommandSpec spec;

    /**
     * Renders a shaded sphere using ray-sphere intersection and Lambertian shading.
     * <p>
     * Uses {@code var} and {@code final} per standards. The light source orbits the sphere to demonstrate
     * the 16-bit color gradients.
     * </p>
     *
     * @param oled SSD1331 driver instance.
     */
    public void runDemo(final Ssd1331 oled) {
        final var width = getWidth();
        final var height = getHeight();
        final var g2d = getG2d();
        final var radius = 25.0;
        final var centerX = width / 2.0;
        final var centerY = height / 2.0;
        
        var lightAngle = 0.0;
        final var frameDelay = 1000 / getFps();

        log.info("Starting Shaded Raytrace at {} FPS", getFps());

        while (true) {
            final var startTime = System.currentTimeMillis();
            
            // Clear frame
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);

            // Light direction vector (oscillating for movement)
            final var lx = Math.cos(lightAngle);
            final var ly = Math.sin(lightAngle);
            final var lz = -1.0; // Light coming from "front"
            
            // Normalize light vector
            final var lMag = Math.sqrt(lx * lx + ly * ly + lz * lz);
            final var nlx = lx / lMag;
            final var nly = ly / lMag;
            final var nlz = lz / lMag;

            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    final var dx = x - centerX;
                    final var dy = y - centerY;
                    final var distSq = dx * dx + dy * dy;

                    if (distSq <= radius * radius) {
                        // Calculate Z on sphere surface
                        final var dz = Math.sqrt(radius * radius - distSq);
                        
                        // Normal vector at intersection (Normalized by radius)
                        final var nx = dx / radius;
                        final var ny = dy / radius;
                        final var nz = -dz / radius;

                        // Dot product for Lambertian shading (max(0, N·L))
                        final var dot = Math.max(0.0, (nx * nlx + ny * nly + nz * nlz));
                        
                        // Map dot product to RGB (e.g., a Blue-ish sphere)
                        final var r = (int) (20 * dot);
                        final var g = (int) (100 * dot);
                        final var b = (int) (255 * dot);

                        g2d.setColor(new Color(Math.clamp(r, 0, 255), Math.clamp(g, 0, 255), Math.clamp(b, 0, 255)));
                        g2d.drawRect(x, y, 0, 0); // Plot pixel
                    }
                }
            }

            oled.drawImage(getImage());
            lightAngle += 0.1;

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
     * Execution logic with context-aware FPS.
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

    public static void main(final String... args) {
        System.exit(new CommandLine(new Raytrace()).execute(args));
    }
}
