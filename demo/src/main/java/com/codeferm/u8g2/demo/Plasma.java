/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.U8g2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Procedural Plasma Effect for I2C OLED. Target: 30 FPS (Optimized for 400 KHz I2C).
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "Plasma", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class Plasma extends Base {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Plasma.class);
    /**
     * FPS.
     */
    @Option(names = {"-f", "--fps"}, description = "Target frames per second", defaultValue = "30")
    public int fps;

    /**
     * Executes the plasma animation logic. Exits automatically after 60 seconds based on frame count.
     */
    public void demo() {
        var u8 = getU8g2();
        var w = getWidth();
        var h = getHeight();
        var display = getDisplay();
        var maxFrames = fps * 60;
        var frameCount = 0;
        var time = 0.0f;
        logger.info("Starting 30 FPS Plasma (60s run)...");
        while (frameCount < maxFrames) {
            U8g2.clearBuffer(u8);
            for (var y = 0; y < h; y++) {
                // Pre-calculating Y-based components outside the X-loop for speed
                var vY = Math.sin(y / 8.0 + time / 2.0);
                var cyBase = y + 8.0 * Math.cos(time / 2.0);
                for (var x = 0; x < w; x++) {
                    // Combine vertical, horizontal, and diagonal sine waves
                    var v = Math.sin(x / 16.0 + time) + vY;
                    v += Math.sin((x + y + time) / 16.0);
                    // Circular/Radial interference component
                    var cx = x + 8.0 * Math.sin(time / 3.0);
                    v += Math.sin(Math.sqrt(cx * cx + cyBase * cyBase + 1.0) / 8.0);
                    // Threshold value to 1-bit monochrome
                    if (v > 0) {
                        U8g2.drawPixel(u8, x, y);
                    }
                }
            }
            U8g2.sendBuffer(u8);
            time += 0.08f;
            frameCount++;
            // Maintain timing for FPS
            display.sleep(1000L / fps);
        }
        logger.info("Demo complete.");
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
        demo();
        done();
        return 0;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new Plasma()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
