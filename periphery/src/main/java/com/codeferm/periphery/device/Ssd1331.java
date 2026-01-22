/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Gpio;
import com.codeferm.periphery.Spi;
import com.codeferm.periphery.device.SpiBus;

/**
 * SSD1331 96x64 RGB OLED driver using SpiBus and Gpio.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class Ssd1331 implements AutoCloseable {

    // SSD1331 Commands from Seeed/Adafruit C++ source
    public static final byte CMD_SET_COLUMN_ADDRESS = (byte) 0x15;
    public static final byte CMD_SET_ROW_ADDRESS = (byte) 0x75;
    public static final byte CMD_SET_REMAP = (byte) 0xA0;
    public static final byte CMD_DISPLAY_OFF = (byte) 0xAE;
    public static final byte CMD_DISPLAY_ON = (byte) 0xAF;
    public static final byte CMD_SET_MASTER_CONFIGURE = (byte) 0xAD;

    private final int width = 96;
    private final int height = 64;

    private final SpiBus spiBus;
    private final Gpio dc;

    /**
     * Initialize SSD1331 with SpiBus and Gpio for DC.
     * Note: CS is handled by the SPI driver via the device path.
     */
    public Ssd1331(final String spiDevice, final int mode, final int speed, 
                   final String gpioDevice, final int dcPin) {
        this.spiBus = new SpiBus(spiDevice, mode, speed);
        this.dc = new Gpio(gpioDevice, dcPin, Gpio.GPIO_DIR_OUT);
        init();
    }

    private void sendCmd(final byte c) {
        // Set DC low for Command
        Gpio.gpioWrite(dc.getHandle(), false);
        final var buf = new byte[]{c};
        spiBus.transfer(buf, new byte[1], 1);
    }

    private void sendData(final byte[] data) {
        // Set DC high for Data
        Gpio.gpioWrite(dc.getHandle(), true);
        spiBus.transfer(data, new byte[data.length], data.length);
    }

    /**
     * Exact initialization sequence from Seeed Studio C++ library.
     */
    private void init() {
        sendCmd(CMD_DISPLAY_OFF);          // 0xAE
        sendCmd((byte) 0x81);              // Set contrast A
        sendCmd((byte) 0x91);
        sendCmd((byte) 0x82);              // Set contrast B
        sendCmd((byte) 0x50);
        sendCmd((byte) 0x83);              // Set contrast C
        sendCmd((byte) 0x7D);
        sendCmd((byte) 0x87);              // Master current control
        sendCmd((byte) 0x06);              // Value from C++ source
        sendCmd((byte) 0x8A);              // Precharge speed A
        sendCmd((byte) 0x64);
        sendCmd((byte) 0x8B);              // Precharge speed B
        sendCmd((byte) 0x78);
        sendCmd((byte) 0x8C);              // Precharge speed C
        sendCmd((byte) 0x64);
        sendCmd(CMD_SET_REMAP);            // 0xA0
        sendCmd((byte) 0x72);              // RGB565 mode
        sendCmd((byte) 0xA1);              // Start line
        sendCmd((byte) 0x00);
        sendCmd((byte) 0xA2);              // Display offset
        sendCmd((byte) 0x00);
        sendCmd((byte) 0xA4);              // Normal display
        sendCmd((byte) 0xA8);              // Multiplex ratio
        sendCmd((byte) 0x3F);
        sendCmd(CMD_SET_MASTER_CONFIGURE); // 0xAD
        sendCmd((byte) 0x8E);              // External VCC
        sendCmd((byte) 0xB0);              // Power save mode
        sendCmd((byte) 0x00);
        sendCmd((byte) 0xB1);              // Phase period adjustment
        sendCmd((byte) 0x31);
        sendCmd((byte) 0xB3);              // Display clock div
        sendCmd((byte) 0xF0);
        sendCmd((byte) 0xBB);              // Precharge voltage
        sendCmd((byte) 0x3A);
        sendCmd((byte) 0xBE);              // VcomH voltage
        sendCmd((byte) 0x3E);
        sendCmd((byte) 0x2E);              // Deactivate scrolling
        
        clear();
        sendCmd(CMD_DISPLAY_ON);           // 0xAF
    }

    public void drawBuffer(final byte[] buffer) {
        sendCmd(CMD_SET_COLUMN_ADDRESS);
        sendCmd((byte) 0);
        sendCmd((byte) (width - 1));
        sendCmd(CMD_SET_ROW_ADDRESS);
        sendCmd((byte) 0);
        sendCmd((byte) (height - 1));
        sendData(buffer);
    }

    public void clear() {
        drawBuffer(new byte[width * height * 2]);
    }

    @Override
    public void close() {
        clear();
        sendCmd(CMD_DISPLAY_OFF);
        // SpiBus and Gpio are AutoCloseable
        spiBus.close();
        dc.close();
    }
}
