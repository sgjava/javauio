/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Gpio;
import com.codeferm.periphery.Spi;

/**
 * SSD1331 96x64 RGB OLED driver using static JNI wrapper calls.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class Ssd1331 implements AutoCloseable {

    /**
     * SSD1331 Command set.
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
     * SPI handle.
     */
    private final long spiHandle;
    /**
     * GPIO handle for Data/Command selection.
     */
    private final long dcHandle;

    /**
     * Initialize SSD1331 with existing native handles.
     *
     * @param spiHandle Native SPI handle.
     * @param dcHandle Native GPIO handle for D/C.
     */
    public Ssd1331(final long spiHandle, final long dcHandle) {
        this.spiHandle = spiHandle;
        this.dcHandle = dcHandle;
        init();
    }

    /**
     * Send command byte using static SPI and GPIO calls.
     *
     * @param cmd Command byte.
     */
    public void writeCommand(final byte cmd) {
        // D/C low for command
        Gpio.gpioWrite(dcHandle, false);
        final var buf = new byte[]{cmd};
        Spi.spiTransfer(spiHandle, buf, new byte[1], 1);
    }

    /**
     * Send data array using static SPI and GPIO calls.
     *
     * @param data Data array.
     */
    public void writeData(final byte[] data) {
        // D/C high for data
        Gpio.gpioWrite(dcHandle, true);
        Spi.spiTransfer(spiHandle, data, new byte[data.length], data.length);
    }

    /**
     * Hardware initialization sequence.
     */
    private void init() {
        writeCommand(DISPLAY_OFF);
        writeCommand(SET_REMAP);
        writeCommand((byte) 0x72); // RGB565 format
        writeCommand((byte) 0xA1); // Start Line
        writeCommand((byte) 0x00);
        writeCommand((byte) 0xA2); // Offset
        writeCommand((byte) 0x00);
        writeCommand((byte) 0xA4); // Normal Display
        writeCommand((byte) 0xAD); // Master Config
        writeCommand((byte) 0x8E);
        writeCommand(DISPLAY_ON);
    }

    /**
     * Set the RAM window for drawing.
     *
     * @param x1 Start column.
     * @param y1 Start row.
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
     * Clear the screen.
     */
    public void clear() {
        setWindow(0, 0, width - 1, height - 1);
        final var black = new byte[width * height * 2];
        writeData(black);
    }

    /**
     * Transmit buffer to display.
     *
     * @param buffer Byte array of pixel data.
     */
    public void drawBuffer(final byte[] buffer) {
        setWindow(0, 0, width - 1, height - 1);
        writeData(buffer);
    }

    @Override
    public void close() {
        // Handle closing is managed by the Demo/Caller
    }
}
