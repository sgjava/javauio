/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.FontType;
import static com.codeferm.u8g2.FontType.FONT_COURB10_TF;
import com.codeferm.u8g2.SetupType;
import static com.codeferm.u8g2.SetupType.SSD1306_I2C_128X64_NONAME;
import com.codeferm.u8g2.U8g2;
import static com.codeferm.u8g2.U8x8.U8X8_PIN_NONE;
import static com.codeferm.u8g2.demo.Base.DisplayType.I2CHW;
import static com.codeferm.u8g2.demo.Base.DisplayType.SDL;
import java.util.concurrent.Callable;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Option;

/**
 * Base CLI gives you a fully configured display with default font.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class Base implements Callable<Integer> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Base.class);

    /**
     * Add display types here and in setup method.
     */
    public enum DisplayType {
        I2CHW,
        I2CSW,
        SPIHW,
        SPISW,
        SDL;
    }
    /**
     * Type allows hardware and software I2C and SPI.
     */
    @Option(names = {"--setup"}, description
            = "Setup function to call, ${DEFAULT-VALUE} by default.")
    private SetupType setup = SSD1306_I2C_128X64_NONAME;
    /**
     * Font to use.
     */
    @Option(names = {"--font"}, description
            = "Font, ${DEFAULT-VALUE} by default.")
    private FontType font = FONT_COURB10_TF;
    /**
     * Type allows hardware and software I2C and SPI plus SDL.
     */
    @Option(names = {"--type"}, description = "Type of display, ${DEFAULT-VALUE} by default.")
    private DisplayType type = I2CHW;
    /**
     * GPIO chip number.
     */
    @Option(names = {"--gpio"}, description = "GPIO chip number, ${DEFAULT-VALUE} by default.")
    private int gpio = 0x0;
    /**
     * I2C or SPI bus number.
     */
    @Option(names = {"--bus"}, description = "I2C or SPI bus number , ${DEFAULT-VALUE} by default.")
    private int bus = 0x0;
    /**
     * I2C address.
     */
    @Option(names = {"--address"}, description = "I2C address, ${DEFAULT-VALUE} by default.")
    private int address = 0x3c;
    /**
     * I2C SCL.
     */
    @Option(names = {"--scl"}, description = "I2C software SCL pin, ${DEFAULT-VALUE} by default.")
    private int scl = 11;
    /**
     * I2C SDA.
     */
    @Option(names = {"--sda"}, description = "I2C software SDA pin, ${DEFAULT-VALUE} by default.")
    private int sda = 12;
    /**
     * DC pin for SPI.
     */
    @Option(names = {"--dc"}, description = "SPI DC pin, ${DEFAULT-VALUE} by default.")
    private int dc = 198;
    /**
     * RESET pin for SPI.
     */
    @Option(names = {"--reset"}, description = "I2C/SPI RESET pin, ${DEFAULT-VALUE} by default.")
    private int reset = 199;
    /**
     * MOSI pin for SPI.
     */
    @Option(names = {"--mosi"}, description = "SPI MOSI pin, ${DEFAULT-VALUE} by default.")
    private int mosi = 15;
    /**
     * SCK pin for SPI.
     */
    @Option(names = {"--sck"}, description = "SPI SCK pin, ${DEFAULT-VALUE} by default.")
    private int sck = 14;
    /**
     * CS pin for SPI.
     */
    @Option(names = {"--cs"}, description = "SPI CS pin, ${DEFAULT-VALUE} by default.")
    private int cs = 13;
    /**
     * Mode for SPI.
     */
    @Option(names = {"--mode"}, description = "SPI mode, ${DEFAULT-VALUE} by default.")
    private short mode = 0;
    /**
     * CS pin for SPI.
     */
    @Option(names = {"--speed"}, description = "SPI maximum speed, ${DEFAULT-VALUE} by default.")
    private long speed = 500000;
    /**
     * Nanosecond delay or 0 for none for software I2C and SPI.
     */
    @Option(names = {"--delay"}, description = "Nanosecond delay for software I2C and SPI, ${DEFAULT-VALUE} by default.")
    private long delay = 0;
    /**
     * Milliseconds to sleep for text and graphics.
     */
    @Option(names = {"--sleep"}, description = " Milliseconds to sleep for text and graphics, ${DEFAULT-VALUE} by default.")
    private long sleep = 5000;
    /**
     * Pointer to u8g2_t struct.
     */
    private long u8g2;
    /**
     * Display helper.
     */
    private Display display;
    /**
     * Display width.
     */
    private int width;
    /**
     * Display height.
     */
    private int height;
    /**
     * Show text with delay. Everything is calculated each time as font can differ between calls. String is wrapped if too long for
     * one line.
     *
     * @param text Text to show.
     */
    public void showText(final String text) {
        logger.debug(text);
        final var maxHeight = U8g2.getMaxCharHeight(u8g2);
        U8g2.clearBuffer(u8g2);
        // Does string fit display width?
        if (U8g2.getStrWidth(u8g2, text) < width) {
            U8g2.drawStr(u8g2, 1, maxHeight, text);
        } else {
            // String exceeds width, so let's wrap
            var y = maxHeight;
            var pos = 0;
            var str = "";
            while (y < height && pos < text.length()) {
                if (U8g2.getStrWidth(u8g2, str) < width) {
                    str += text.charAt(pos++);
                } else {
                    U8g2.drawStr(u8g2, 1, y, str);
                    str = "";
                    y += maxHeight;
                    pos--;
                    // Skip leading spaces on new line
                    while (text.charAt(pos) == ' ' && pos < text.length()) {
                        pos++;
                    }
                }
            }
            // Draw last line
            if (!str.isEmpty()) {
                U8g2.drawStr(u8g2, 1, y, str);
            }
        }
        U8g2.sendBuffer(u8g2);
        U8g2.clearBuffer(u8g2);
        display.sleep(sleep);
    }

    /**
     * Shut off display and free resources.
     */
    public void done() {
        U8g2.setPowerSave(u8g2, 1);
        // No need to free up hardware
        if (type == SDL) {
            U8g2.done(u8g2);
        } else {
            display.done(u8g2);
            U8g2.doneI2c();
            U8g2.doneSpi();
        }
    }

    /**
     * Sub class should call this to setup display.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        display = new Display();
        // Initialize user_data_struct based on display type
        logger.debug(String.format("Setup %s", setup));
        logger.debug(String.format("Type %s", type));
        logger.debug(String.format("Font %s", font));
        switch (type) {
            case I2CHW:
                u8g2 = display.initHwI2c(setup, bus, address);
                break;
            case I2CSW:
                u8g2 = display.initSwI2c(setup, gpio, scl, sda, U8X8_PIN_NONE, delay);
                break;
            case SPIHW:
                u8g2 = display.initHwSpi(setup, gpio, bus, dc, reset, U8X8_PIN_NONE, mode, speed);
                break;
            case SPISW:
                u8g2 = display.initSwSpi(setup, gpio, dc, reset, mosi, sck, cs, delay);
                break;
            case SDL:
                u8g2 = display.initSdl();
                break;
            default:
                throw new RuntimeException(String.format("%s is not a valid type", type));
        }
        width = U8g2.getDisplayWidth(u8g2);
        height = U8g2.getDisplayHeight(u8g2);
        U8g2.setFont(u8g2, display.getFontPtr(font));
        U8g2.clearBuffer(u8g2);
        U8g2.sendBuffer(u8g2);
        U8g2.setPowerSave(u8g2, 0);
        return exitCode;
    }
}
