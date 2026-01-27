/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.ssd1331.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Procedural RGB Plasma Effect for SSD1331 refactored to use Base.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Plasma", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class Plasma extends Base {

    /**
     * Executes the plasma animation logic using cached dimensions from Base.
     * <p>
     * Uses var type inference and final modifiers. Direct buffer access is used 
     * for procedural pixel generation.
     * </p>
     *
     * @param oled SSD1331 driver instance.
     * @throws Exception On SPI or timing errors.
     */
    public void demo(final Ssd1331 oled) throws Exception {
        // Access cached dimensions from Base class
        final var w = getWidth();
        final var h = getHeight();
        final var targetFps = getFps();
        final var maxFrames = targetFps * 60;
        var frameCount = 0;
        var time = 0.0f;

        // Optimized for Java 2D bit-packing in Ssd1331 class
        final var canvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        final int[] pixelData = ((DataBufferInt) canvas.getRaster().getDataBuffer()).getData();

        log.info("Starting RGB Plasma (60s run) at {} FPS...", targetFps);
        while (frameCount < maxFrames) {
            for (var y = 0; y < h; y++) {
                // Pre-calculate Y components
                final var vY = Math.sin(y / 8.0 + time / 2.0);
                final var cyBase = y + 8.0 * Math.cos(time / 2.0);

                for (var x = 0; x < w; x++) {
                    // Combine interference patterns
                    var v = Math.sin(x / 16.0 + time) + vY;
                    v += Math.sin((x + y + time) / 16.0);
                    final var cx = x + 8.0 * Math.sin(time / 3.0);
                    v += Math.sin(Math.sqrt(cx * cx + cyBase * cyBase + 1.0) / 8.0);

                    // Generate different color blobs based on wave value 'v'
                    final int r = (int) (128.0 + 127.0 * Math.sin(v * Math.PI + time));
                    final int g = (int) (128.0 + 127.0 * Math.sin(v * Math.PI + time / 2.0 + Math.PI / 2.0));
                    final int b = (int) (128.0 + 127.0 * Math.sin(v * Math.PI + time / 4.0 + Math.PI));

                    // Direct write to buffer for maximum performance
                    pixelData[y * w + x] = (r << 16) | (g << 8) | b;
                }
            }

            // Push the procedural image to the hardware
            oled.drawImage(canvas);

            time += 0.08f;
            frameCount++;

            // Maintain FPS timing
            TimeUnit.MILLISECONDS.sleep(1000L / targetFps);
        }
        log.info("Demo complete.");
    }

    /**
     * Execution logic for plasma demo.
     *
     * @return Exit code.
     * @throws Exception Possible hardware exception.
     */
    @Override
    public Integer call() throws Exception {
        // super.call() initializes the hardware and caches dimensions
        super.call();
        demo(getOled());
        done();
        return 0;
    }

    /**
     * Main entry point using picocli.
     *
     * @param args Argument list.
     */
    public static void main(final String[] args) {
        System.exit(new CommandLine(new Plasma()).execute(args));
    }
}
