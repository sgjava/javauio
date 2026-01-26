/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * SSD1331 Demo using BufferedImage.
 *
 * @author Steven P. Goldsmith
 * @version 1.1.1
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Ssd1331BufImage", mixinStandardHelpOptions = true)
public class Ssd1331BufImage implements Callable<Integer> {

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
    private int speed = 1000000;
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
     * Demo execution logic.
     *
     * @return Exit code.
     * @throws Exception Hardware or timing exception.
     */
    @Override
    public Integer call() throws Exception {
        log.info("Starting SSD1331 Demo");
        try (final var oled = new Ssd1331(device, mode, speed, gpioDevice, dc, res)) {
            // Initialization
            oled.setup();
            oled.clear();
            // Pull dimensions from driver via Lombok getters
            final int width = oled.getWidth();
            final int height = oled.getHeight();
            // Prepare Java2D image
            final var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            final var g2d = image.createGraphics();
            // Background
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
            // Dynamic Border
            g2d.setColor(Color.WHITE);
            g2d.drawRect(0, 0, width - 1, height - 1);
            // Text
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2d.drawString("Java 2D", 20, 35);
            g2d.dispose();
            log.info("Rendering {}x{} frame...", width, height);
            oled.drawImage(image);
            TimeUnit.SECONDS.sleep(10);
        }
        return 0;
    }

    /**
     * Main entry point using picocli.
     *
     * @param args Argument list.
     */
    public static void main(final String[] args) {
        System.exit(new CommandLine(new Ssd1331BufImage()).execute(args));
    }
}
