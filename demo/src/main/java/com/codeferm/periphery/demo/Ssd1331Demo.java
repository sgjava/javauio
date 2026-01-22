/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.Gpio;
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

@Slf4j
@Command(name = "Ssd1331Demo", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class Ssd1331Demo implements Callable<Integer> {

    @Option(names = {"-s", "--spi"}, description = "SPI device, ${DEFAULT-VALUE}")
    private String spiDevice = "/dev/spidev1.0";

    @Option(names = {"-m", "--mode"}, description = "SPI mode, ${DEFAULT-VALUE}")
    private int mode = 3; // SSD1331 typically uses Mode 3

    @Option(names = {"-speed"}, description = "SPI speed, ${DEFAULT-VALUE}")
    private int speed = 10000000;

    @Option(names = {"-g", "--gpio"}, description = "GPIO chip, ${DEFAULT-VALUE}")
    private String gpioDevice = "/dev/gpiochip0";

    @Option(names = {"-dc"}, description = "DC pin, ${DEFAULT-VALUE}")
    private int dcPin = 199;

    @Option(names = {"-rst"}, description = "RST pin, ${DEFAULT-VALUE}")
    private int rstPin = 198;

    @Override
    public Integer call() {
        log.info("Starting SSD1331 Demo (DC: {}, RST: {})", dcPin, rstPin);

        try (final var rst = new Gpio(gpioDevice, rstPin, Gpio.GPIO_DIR_OUT);
             final var oled = new Ssd1331(spiDevice, mode, speed, gpioDevice, dcPin)) {

            // Hardware Reset
            Gpio.gpioWrite(rst.getHandle(), false);
            TimeUnit.MILLISECONDS.sleep(100);
            Gpio.gpioWrite(rst.getHandle(), true);
            TimeUnit.MILLISECONDS.sleep(100);

            // Create image
            final var image = new BufferedImage(96, 64, BufferedImage.TYPE_USHORT_565_RGB);
            final var g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, 96, 64);
            g2d.setColor(Color.ORANGE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString("Java UIO!", 15, 35);
            g2d.dispose();

            // Prepare pixel data
            final var data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
            final var byteBuf = ByteBuffer.allocate(data.length * 2).order(ByteOrder.BIG_ENDIAN);
            for (final var val : data) {
                byteBuf.putShort(val);
            }

            oled.drawBuffer(byteBuf.array());
            TimeUnit.SECONDS.sleep(5);

        } catch (Exception e) {
            log.error("Demo failed: {}", e.getMessage());
            return 1;
        }
        return 0;
    }

    public static void main(final String... args) {
        System.exit(new CommandLine(new Ssd1331Demo()).execute(args));
    }
}
