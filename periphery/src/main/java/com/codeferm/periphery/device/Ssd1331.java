/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Gpio;
import com.codeferm.periphery.Spi;
import java.util.Objects;

/**
 * SSD1331 96x64 RGB OLED display driver.
 *
 * This driver utilizes a hardware SPI bus for data transfer and a GPIO line for the Data/Command (D/C) signal. It is
 * designed to work with 16-bit RGB565 buffers, making it compatible with Java 2D BufferedImage outputs.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class Ssd1331 implements AutoCloseable {

    /**
     * SSD1331 Command set constants.
     */
    public static final byte SET_COLUMN_ADDRESS = (byte) 0x15;
    public static final byte SET_ROW_ADDRESS = (byte) 0x75;
    public static final byte SET_REMAP = (byte) 0xA0;
    public static final byte DISPLAY_OFF = (byte) 0xAE;
    public static final byte DISPLAY_ON = (byte) 0xAF;

    /**
     * Display dimensions.
     */
    private final int width = 96;
    private final int height = 64;

    /**
     * SPI handle for the display bus.
     */
    private final Spi spi;
    /**
     * GPIO handle for the Data/Command select line.
     */
    private final Gpio dcPin;

    /**
     * Initialize the SSD1331 device.
     *
     * @param spi Device instance of the SPI bus.
     * @param dcPin Device instance of the GPIO used for D/C.
     */
    public Ssd1331(final Spi spi, final Gpio dcPin) {
        this.spi = Objects.requireNonNull(spi, "SPI handle cannot be null");
        this.dcPin = Objects.requireNonNull(dcPin, "D/C GPIO handle cannot be null");
        init();
    }

    /**
     * Send a command byte to the controller.
     *
     * @param cmd The command byte.
     */
    public void writeCommand(final byte cmd) {
        // Set D/C low for command mode
        dcPin.gpioWrite(dcPin.getHandle(), false);
        final var buf = new byte[]{cmd};
        spi.spiTransfer(spi.getHandle(), buf, new byte[1], 1);
    }

    /**
     * Send a data buffer to the controller.
     *
     * @param data The byte array of data/pixels.
     */
    public void writeData(final byte[] data) {
        // Set D/C high for data mode
        dcPin.gpioWrite(dcPin.getHandle(), true);
        spi.spiTransfer(spi.getHandle(), data, new byte[data.length], data.length);
    }

    /**
     * Standard initialization sequence for SSD1331.
     */
    private void init() {
        writeCommand(DISPLAY_OFF);
        writeCommand(SET_REMAP);
        writeCommand((byte) 0x72); // RGB565, horizontal address increment
        writeCommand((byte) 0xA1); // Start Line
        writeCommand((byte) 0x00);
        writeCommand((byte) 0xA2); // Offset
        writeCommand((byte) 0x00);
        writeCommand((byte) 0xA4); // Normal Display
        writeCommand((byte) 0xAD); // Master Config
        writeCommand((byte) 0x8E);
        writeCommand((byte) 0x81); // Contrast A
        writeCommand((byte) 0x91);
        writeCommand((byte) 0x82); // Contrast B
        writeCommand((byte) 0x50);
        writeCommand((byte) 0x83); // Contrast C
        writeCommand((byte) 0x7D);
        writeCommand(DISPLAY_ON);
    }

    /**
     * Define the rectangular area in GDDRAM for subsequent data writes.
     *
     * @param x1 Start column (0-95).
     * @param y1 Start row (0-63).
     * @param x2 End column.
     * @param y2 End row.
     */
    public void setWindow(final int x1, final int y1, final int x2, final int y2) {
        writeCommand(SET_COLUMN_ADDRESS);
        writeCommand((byte) x1);
        writeCommand((byte) x2);
        writeCommand(SET_ROW_ADDRESS);
        writeCommand((byte) y1);
        writeCommand((byte) y2);
    }

    /**
     * Clear the entire display by filling RAM with zeros.
     */
    public void clear() {
        setWindow(0, 0, width - 1, height - 1);
        final var black = new byte[width * height * 2];
        writeData(black);
    }

    /**
     * Transmit a full frame buffer to the display.
     *
     * @param buffer The 16-bit RGB565 byte array.
     */
    public void drawBuffer(final byte[] buffer) {
        setWindow(0, 0, width - 1, height - 1);
        writeData(buffer);
    }

    /**
     * Close resources. Note: This does not close the SPI/GPIO handles as they are passed in.
     */
    @Override
    public void close() {
        // Implementation can be expanded if device-specific cleanup is needed
    }
}
