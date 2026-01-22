/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.Gpio;
import com.codeferm.periphery.Spi;
import com.codeferm.periphery.device.Ssd1331;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * SSD1331 OLED demo using Java 2D AWT to draw "Hello World".
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Ssd1331Demo", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Display Hello World on SSD1331 using Java 2D AWT.")
public class Ssd1331Demo implements Callable<Integer> {

    /**
     * SPI device option based on SpiLoopback defaults.
     */
    @Option(names = {"-s", "--spi"}, description = "SPI device, ${DEFAULT-VALUE} by default.")
    private String spiDevice = "/dev/spidev1.0";

    /**
     * SPI mode (SSD1331 uses 3).
     */
    @Option(names = {"-m", "--mode"}, description = "SPI mode, ${DEFAULT-VALUE} by default.")
    private int mode = 3;

    /**
     * SPI speed.
     */
    @Option(names = {"-f", "--speed"}, description = "SPI speed Hz, ${DEFAULT-VALUE} by default.")
    private int speed = 10000000;

    /**
     * GPIO device option (chip 0 or 1).
     */
    @Option(names = {"-g", "--gpio"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String gpioDevice = "/dev/gpiochip0";

    /**
     * Data/Command pin.
     */
    @Option(names = {"-dc", "--dcpin"}, description = "GPIO DC pin, ${DEFAULT-VALUE} by default.")
    private int dcPin = 199;

    /**
     * Reset pin.
     */
    @Option(names = {"-rst", "--rstpin"}, description = "GPIO RST pin, ${DEFAULT-VALUE} by default.")
    private int rstPin = 198;

    /**
     * Perform hardware reset, initialize display, and draw "Hello World".
     *
     * @return Exit code.
     */
    @Override
    public Integer call() {
        var exitCode = 0;
        log.info("Starting SSD1331 Demo (D/C: {}, RST: {})", dcPin, rstPin);

        try (final var spi = new Spi(spiDevice, mode, speed);
             final var dc = new Gpio(gpioDevice, dcPin, Gpio.GPIO_DIR_OUT);
             final var rst = new Gpio(gpioDevice, rstPin, Gpio.GPIO_DIR_OUT);
             final var oled = new Ssd1331(spi, dc)) {

            // 1. Hardware Reset Pulse
            log.debug("Performing hardware reset...");
            rst.gpioWrite(rst.getHandle(), true);
            TimeUnit.MILLISECONDS.sleep(10);
            rst.gpioWrite(rst.getHandle(), false);
            TimeUnit.MILLISECONDS.sleep(10);
            rst.gpioWrite(rst.getHandle(), true);
            TimeUnit.MILLISECONDS.sleep(100);

            // 2. Create Frame Buffer (96x64 RGB565)
            final var width = 96;
            final var height = 64;
            final var image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
            final var g2d = image.createGraphics();

            // Set AWT quality hints
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Draw Background and Text
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
            
            g2d.setColor(Color.CYAN);
            g2d.drawRect(0, 0, width - 1, height - 1);

            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.setColor(Color.WHITE);
            g2d.drawString("Hello", 25, 25);
            g2d.setColor(Color.YELLOW);
            g2d.drawString("World!", 25, 45);
            g2d.dispose();

            // 3. Extract and convert to Big Endian byte array for SPI
            final var data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
            final var byteBuf = ByteBuffer.allocate(data.length * 2);
            byteBuf.order(ByteOrder.BIG_ENDIAN);
            for (final var val : data) {
                byteBuf.putShort(val);
            }

            // 4. Update display
            log.info("Transmitting Java 2D frame buffer...");
            oled.clear();
            oled.drawBuffer(byteBuf.array());
            log.info("Finished.");

        } catch (Exception e) {
            log.error("SSD1331 demo failed: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Main entry point.
     *
     * @param args Argument list.
     */
    public static void main(final String[] args) {
        final var exitCode = new CommandLine(new Ssd1331Demo()).execute(args);
        System.exit(exitCode);
    }
}
