/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.ssd1331.demo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * drawImage performance demo refactored to use Ssd1331Base.
 * <p>
 * This class measures the transfer speed of a static image to the SSD1331 display,
 * calculating frames per second (FPS) based on a configurable number of samples.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Perf", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "SSD1331 drawImage performance demo")
public class Perf extends Base {

    /**
     * Samples option.
     */
    @Option(names = {"--samples"}, description = "Samples, ${DEFAULT-VALUE} by default.")
    private int samples = 1000;

    /**
     * Calculate drawImage FPS using cached dimensions from the base class.
     * <p>
     * Uses var type inference and final modifiers for all local variables.
     * </p>
     *
     * @return Exit code.
     * @throws Exception Possible hardware exception.
     */
    @Override
    public Integer call() throws Exception {
        // super.call() initializes hardware and caches width/height in Base
        super.call();
        
        final var w = getWidth();
        final var h = getHeight();
        final var oled = getOled();

        // Create a static image for testing raw transfer speed
        final var image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        final var g2d = image.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();

        log.info("Timing {} drawImage transfers at {} Hz", samples, getSpeed());
        
        final var start = Instant.now();
        for (var i = 0; i < samples; i++) {
            oled.drawImage(image);
        }
        final var finish = Instant.now();

        // Calculate metrics using final var
        final var timeElapsed = Duration.between(start, finish);
        final var seconds = timeElapsed.toMillis() / 1000.0;
        final var fps = samples / seconds;

        log.info("Total time: {} seconds", String.format("%.2f", seconds));
        log.info("{} drawImage per second (FPS)", String.format("%.2f", fps));
        
        done();
        return 0;
    }

    /**
     * Main entry point using picocli.
     *
     * @param args Argument list.
     */
    public static void main(final String[] args) {
        System.exit(new CommandLine(new Perf()).execute(args));
    }
}
