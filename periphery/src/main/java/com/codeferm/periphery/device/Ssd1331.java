/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Gpio;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT;
import com.codeferm.periphery.Spi;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SSD1331 96x64 RGB OLED driver using optimized memory access.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class Ssd1331 implements AutoCloseable {

    /**
     * Set column start and end address.
     */
    public static final byte SET_COLUMN_ADDRESS = (byte) 0x15;
    /**
     * Set row start and end address.
     */
    public static final byte SET_ROW_ADDRESS = (byte) 0x75;
    /**
     * Set contrast for color A.
     */
    public static final byte SET_CONTRAST_A = (byte) 0x81;
    /**
     * Set contrast for color B.
     */
    public static final byte SET_CONTRAST_B = (byte) 0x82;
    /**
     * Set contrast for color C.
     */
    public static final byte SET_CONTRAST_C = (byte) 0x83;
    /**
     * Master current control (0-15).
     */
    public static final byte MASTER_CURRENT_CONTROL = (byte) 0x87;
    /**
     * Set pre-charge speed for color A.
     */
    public static final byte SET_PRECHARGE_SPEED_A = (byte) 0x8A;
    /**
     * Set pre-charge speed for color B.
     */
    public static final byte SET_PRECHARGE_SPEED_B = (byte) 0x8B;
    /**
     * Set pre-charge speed for color C.
     */
    public static final byte SET_PRECHARGE_SPEED_C = (byte) 0x8C;
    /**
     * Set re-map and color depth.
     */
    public static final byte SET_REMAP = (byte) 0xA0;
    /**
     * Set display start line.
     */
    public static final byte SET_DISPLAY_START_LINE = (byte) 0xA1;
    /**
     * Set display offset.
     */
    public static final byte SET_DISPLAY_OFFSET = (byte) 0xA2;
    /**
     * Normal display mode.
     */
    public static final byte NORMAL_DISPLAY = (byte) 0xA4;
    /**
     * Force entire display on.
     */
    public static final byte ENTIRE_DISPLAY_ON = (byte) 0xA5;
    /**
     * Force entire display off.
     */
    public static final byte ENTIRE_DISPLAY_OFF = (byte) 0xA6;
    /**
     * Inverse display mode.
     */
    public static final byte INVERSE_DISPLAY = (byte) 0xA7;
    /**
     * Set multiplex ratio.
     */
    public static final byte SET_MULTIPLEX_RATIO = (byte) 0xA8;
    /**
     * Dim mode setting.
     */
    public static final byte DIM_MODE_SETTING = (byte) 0xAB;
    /**
     * Master configuration.
     */
    public static final byte MASTER_CONFIGURATION = (byte) 0xAD;
    /**
     * Set display on dim.
     */
    public static final byte SET_DISPLAY_ON_DIM = (byte) 0xAC;
    /**
     * Power off display.
     */
    public static final byte DISPLAY_OFF = (byte) 0xAE;
    /**
     * Power on display.
     */
    public static final byte DISPLAY_ON = (byte) 0xAF;
    /**
     * Power save mode.
     */
    public static final byte POWER_SAVE_MODE = (byte) 0xB0;
    /**
     * Phase 1 and 2 period adjustment.
     */
    public static final byte PHASE_1_2_PERIOD = (byte) 0xB1;
    /**
     * Set display clock divide ratio/oscillator frequency.
     */
    public static final byte DISPLAY_CLOCK_DIV = (byte) 0xB3;
    /**
     * Set gray scale table.
     */
    public static final byte SET_GRAY_SCALE_TABLE = (byte) 0xB8;
    /**
     * Enable gray scale table.
     */
    public static final byte ENABLE_GRAY_SCALE_TABLE = (byte) 0xB9;
    /**
     * Set pre-charge voltage.
     */
    public static final byte PRECHARGE_VOLTAGE = (byte) 0xBB;
    /**
     * Set Vcomh voltage.
     */
    public static final byte SET_VCOMH_VOLTAGE = (byte) 0xBE;
    /**
     * No operation command.
     */
    public static final byte NO_OP = (byte) 0xBC;
    /**
     * Draw line.
     */
    public static final byte DRAW_LINE = (byte) 0x21;
    /**
     * Draw rectangle.
     */
    public static final byte DRAW_RECTANGLE = (byte) 0x22;
    /**
     * Copy window.
     */
    public static final byte COPY_WINDOW = (byte) 0x23;
    /**
     * Dim window.
     */
    public static final byte DIM_WINDOW = (byte) 0x24;
    /**
     * Clear window.
     */
    public static final byte CLEAR_WINDOW = (byte) 0x25;
    /**
     * Fill window.
     */
    public static final byte FILL_WINDOW = (byte) 0x26;
    /**
     * Continuous scrolling setup.
     */
    public static final byte CONTINUOUS_SCROLLING_SETUP = (byte) 0x27;
    /**
     * Deactivate scrolling.
     */
    public static final byte DEACTIVATE_SCROLLING = (byte) 0x2E;
    /**
     * Activate scrolling.
     */
    public static final byte ACTIVATE_SCROLLING = (byte) 0x2F;
    /**
     * SPI wrapper.
     */
    private final Spi spi;
    /**
     * Data/command control line.
     */
    private final Gpio dcGpio;
    /**
     * Reset line.
     */
    private final Gpio resGpio;
    /**
     * SPI handle.
     */
    private final long spiHandle;
    /**
     * DC handle.
     */
    private final long dcHandle;
    /**
     * Reset handle.
     */
    private final long resHandle;
    /**
     * Display width.
     */
    @Getter
    private final int width = 96;
    /**
     * Display height.
     */
    @Getter
    private final int height = 64;

    /**
     * Initialize hardware with SPI and GPIO handles.
     *
     * @param device SPI device path.
     * @param mode SPI mode.
     * @param speed SPI speed in Hz.
     * @param gpioDevice GPIO chip path.
     * @param dcPin Data/Command pin.
     * @param resPin Reset pin.
     */
    public Ssd1331(final String device, final int mode, final int speed,
            final String gpioDevice, final int dcPin, final int resPin) {
        this.spi = new Spi(device, mode, speed);
        this.spiHandle = spi.getHandle();
        this.dcGpio = new Gpio(gpioDevice, dcPin, GPIO_DIR_OUT);
        this.dcHandle = dcGpio.getHandle();
        this.resGpio = new Gpio(gpioDevice, resPin, GPIO_DIR_OUT);
        this.resHandle = resGpio.getHandle();
    }

    /**
     * Sends command bytes with DC pin LOW.
     *
     * @param data Command byte array.
     */
    public final void writeCommand(final byte[] data) {
        // DC pin LOW for command mode
        Gpio.gpioWrite(dcHandle, false);
        final var rc = Spi.spiTransfer(spiHandle, data, new byte[data.length], data.length);
        // Embedded error check
        if (rc < 0) {
            throw new RuntimeException(String.format("SPI Command failed: %d", rc));
        }
    }

    /**
     * Sends data bytes with DC pin HIGH.
     *
     * @param data Data byte array.
     */
    public final void writeData(final byte[] data) {
        // DC pin HIGH for data mode
        Gpio.gpioWrite(dcHandle, true);
        final var rc = Spi.spiTransfer(spiHandle, data, new byte[data.length], data.length);
        // Embedded error check
        if (rc < 0) {
            throw new RuntimeException(String.format("SPI Data failed: %d", rc));
        }
    }

    /**
     * Hardware initialization sequence.
     */
    public final void setup() {
        try {
            // Initial reset state
            Gpio.gpioWrite(dcHandle, false);
            Gpio.gpioWrite(resHandle, true);
            // Wakeup sync pulse
            Spi.spiTransfer(spiHandle, new byte[]{0}, new byte[1], 1);
            TimeUnit.MILLISECONDS.sleep(100);
            // Perform hardware reset
            Gpio.gpioWrite(resHandle, false);
            TimeUnit.MILLISECONDS.sleep(500);
            Gpio.gpioWrite(resHandle, true);
            TimeUnit.MILLISECONDS.sleep(500);
            // Display settings
            writeCommand(new byte[]{DISPLAY_OFF});
            // Set re-map and color depth
            writeCommand(new byte[]{SET_REMAP, (byte) 0x72});
            // Set display start line
            writeCommand(new byte[]{SET_DISPLAY_START_LINE, (byte) 0x00});
            // Set display offset
            writeCommand(new byte[]{SET_DISPLAY_OFFSET, (byte) 0x00});
            // Normal display mode
            writeCommand(new byte[]{NORMAL_DISPLAY});
            // Set multiplex ratio
            writeCommand(new byte[]{SET_MULTIPLEX_RATIO, (byte) 0x3F});
            // Master configuration
            writeCommand(new byte[]{MASTER_CONFIGURATION, (byte) 0x8E});
            // Power save mode
            writeCommand(new byte[]{POWER_SAVE_MODE, (byte) 0x0B});
            // Phase 1 and 2 period adjustment
            writeCommand(new byte[]{PHASE_1_2_PERIOD, (byte) 0x74});
            // Set display clock divide ratio/oscillator frequency
            writeCommand(new byte[]{DISPLAY_CLOCK_DIV, (byte) 0xD0});
            // Set pre-charge speeds
            writeCommand(new byte[]{SET_PRECHARGE_SPEED_A, (byte) 0x80});
            writeCommand(new byte[]{SET_PRECHARGE_SPEED_B, (byte) 0x80});
            writeCommand(new byte[]{SET_PRECHARGE_SPEED_C, (byte) 0x80});
            // Set pre-charge and Vcomh voltage
            writeCommand(new byte[]{PRECHARGE_VOLTAGE, (byte) 0x3E});
            writeCommand(new byte[]{SET_VCOMH_VOLTAGE, (byte) 0x3E});
            // Master current control
            writeCommand(new byte[]{MASTER_CURRENT_CONTROL, (byte) 0x0F});
            // Set contrast for RGB channels
            writeCommand(new byte[]{SET_CONTRAST_A, (byte) 0xFF});
            writeCommand(new byte[]{SET_CONTRAST_B, (byte) 0xFF});
            writeCommand(new byte[]{SET_CONTRAST_C, (byte) 0xFF});
            // Disable hardware scrolling to prevent pixel artifacts
            writeCommand(new byte[]{DEACTIVATE_SCROLLING});
            // Final display ON
            writeCommand(new byte[]{DISPLAY_ON});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Clears the display window using hardware acceleration.
     */
    public final void clear() {
        // Graphic acceleration command uses 0 to (dimension - 1)
        writeCommand(new byte[]{CLEAR_WINDOW, (byte) 0, (byte) 0, (byte) (width - 1), (byte) (height - 1)});
    }

    /**
     * Renders a Java2D BufferedImage using optimized bit-packing.
     *
     * @param image BufferedImage (TYPE_INT_RGB) to draw.
     */
    public final void drawImage(final BufferedImage image) {
        // Set column and row address to full screen
        writeCommand(new byte[]{SET_COLUMN_ADDRESS, (byte) 0, (byte) (width - 1)});
        writeCommand(new byte[]{SET_ROW_ADDRESS, (byte) 0, (byte) (height - 1)});
        // Fast manual bit-packing from internal int array
        final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        final byte[] output = new byte[pixels.length * 2];
        // Manual loop handles RGB888 -> RGB565 conversion and Endianness
        for (int i = 0, j = 0; i < pixels.length; i++) {
            final int p = pixels[i];
            // Extract & pack RGB 565
            final int r = (p >> 19) & 0x1F;
            final int g = (p >> 10) & 0x3F;
            final int b = (p >> 3) & 0x1F;
            final int packed = (r << 11) | (g << 5) | b;
            // Big-Endian packing for SPI
            output[j++] = (byte) (packed >> 8);
            output[j++] = (byte) (packed & 0xFF);
        }
        // Send full frame buffer in one transfer
        writeData(output);
        // Lock bus by pulling DC LOW with a NO_OP command
        writeCommand(new byte[]{NO_OP});
    }

    /**
     * Safely close all hardware resources using try-with-resources.
     */
    @Override
    public final void close() {
        // Reverse order of initialization: res, dc, then spi
        try (resGpio; dcGpio; spi) {
            // Power down sequence
            writeCommand(new byte[]{DISPLAY_OFF});
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                // Hardware reset pull-down
                Gpio.gpioWrite(resHandle, false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
