/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * drawImage performance demo.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Perf", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "SSD1331 drawImage performance demo")
public class Ssd1331Perf implements Callable<Integer> {

    /**
     * SPI device option.
     */
    @Option(names = {"-d", "--device"}, description = "SPI device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/spidev1.0";
    /**
     * SPI mode option.
     */
    @Option(names = {"-m", "--mode"}, description = "SPI mode, ${DEFAULT-VALUE} by default.")
    private int mode = 3;
    /**
     * SPI Hz speed option.
     */
    @Option(names = {"-s", "--speed"}, description = "Max speed in Hz, ${DEFAULT-VALUE} by default.")
    private int speed = 8000000;
    /**
     * GPIO device option.
     */
    @Option(names = {"-g", "--gpio-device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String gpioDevice = "/dev/gpiochip0";
    /**
     * DC line option.
     */
    @Option(names = {"-dc", "--dc-line"}, description = "DC line, ${DEFAULT-VALUE} by default.")
    private int dc = 199;
    /**
     * RES line option.
     */
    @Option(names = {"-res", "--res-line"}, description = "RES line, ${DEFAULT-VALUE} by default.")
    private int res = 198;
    /**
     * Samples option.
     */
    @Option(names = {"--samples"}, description = "Samples, ${DEFAULT-VALUE} by default.")
    private int samples = 1000;

    /**
     * Calculate drawImage FPS.
     *
     * @return Exit code.
     * @throws Exception Possible hardware exception.
     */
    @Override
    public Integer call() throws Exception {
        try (final var oled = new Ssd1331(device, mode, speed, gpioDevice, dc, res)) {
            oled.setup();
            oled.clear();

            // Use dynamic dimensions via Lombok getters from your driver
            final int width = oled.getWidth();
            final int height = oled.getHeight();

            // Create a static image for testing raw transfer speed
            final var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            final var g2d = image.createGraphics();
            g2d.setColor(java.awt.Color.BLUE);
            g2d.fillRect(0, 0, width, height);
            g2d.dispose();

            log.info(String.format("Timing %d drawImage transfers at %d Hz", samples, speed));
            
            final var start = Instant.now();
            for (var i = 0; i < samples; i++) {
                oled.drawImage(image);
            }
            final var finish = Instant.now();

            // Calculate metrics
            final var timeElapsed = Duration.between(start, finish);
            final double seconds = timeElapsed.toMillis() / 1000.0;
            final double fps = samples / seconds;

            log.info(String.format("Total time: %.2f seconds", seconds));
            log.info(String.format("%.2f drawImage per second (FPS)", fps));
        }
        return 0;
    }

    /**
     * Main entry point.
     *
     * @param args Argument list.
     */
    public static void main(final String[] args) {
        System.exit(new CommandLine(new Ssd1331Perf()).execute(args));
    }
}
