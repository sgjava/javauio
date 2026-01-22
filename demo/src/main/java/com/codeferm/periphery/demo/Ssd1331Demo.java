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
 * SSD1331 Hello World demo.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Ssd1331Demo", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Display Hello World on SSD1331 using Java 2D.")
public class Ssd1331Demo implements Callable<Integer> {

    @Option(names = {"-s", "--spi"}, description = "SPI device, ${DEFAULT-VALUE} by default.")
    private String spiDevice = "/dev/spidev1.0";

    @Option(names = {"-g", "--gpio"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String gpioDevice = "/dev/gpiochip0";

    @Option(names = {"-dc"}, description = "DC pin, ${DEFAULT-VALUE} by default.")
    private int dcPin = 199;

    @Option(names = {"-rst"}, description = "Reset pin, ${DEFAULT-VALUE} by default.")
    private int rstPin = 198;

    @Override
    public Integer call() {
        var exitCode = 0;
        log.info("Starting SSD1331 Demo (DC: {}, RST: {})", dcPin, rstPin);

        try (final var spi = new Spi(spiDevice, 3, 10000000);
             final var dc = new Gpio(gpioDevice, dcPin, Gpio.GPIO_DIR_OUT);
             final var rst = new Gpio(gpioDevice, rstPin, Gpio.GPIO_DIR_OUT);
             final var oled = new Ssd1331(spi, dc)) {

            // Hardware reset
            Gpio.gpioWrite(rst.getHandle(), false);
            TimeUnit.MILLISECONDS.sleep(100);
            Gpio.gpioWrite(rst.getHandle(), true);
            TimeUnit.MILLISECONDS.sleep(100);

            // AWT Drawing
            final var image = new BufferedImage(96, 64, BufferedImage.TYPE_USHORT_565_RGB);
            final var g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, 96, 64);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString("Hello World!", 10, 35);
            g2d.dispose();

            // Format buffer
            final var data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
            final var byteBuf = ByteBuffer.allocate(data.length * 2).order(ByteOrder.BIG_ENDIAN);
            for (final var val : data) {
                byteBuf.putShort(val);
            }

            oled.drawBuffer(byteBuf.array());
            TimeUnit.SECONDS.sleep(5);

        } catch (Exception e) {
            log.error("Demo failed: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    public static void main(final String[] args) {
        System.exit(new CommandLine(new Ssd1331Demo()).execute(args));
    }
}
