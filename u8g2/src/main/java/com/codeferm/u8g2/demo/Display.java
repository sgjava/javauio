/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.U8g2;
import static com.codeferm.u8g2.U8g2.U8G2_R0;
import static com.codeferm.u8g2.U8g2.u8x8_arm_linux_gpio_and_delay;
import static com.codeferm.u8g2.U8g2.u8x8_byte_4wire_sw_spi;
import static com.codeferm.u8g2.U8g2.u8x8_byte_arm_linux_hw_i2c;
import static com.codeferm.u8g2.U8g2.u8x8_byte_arm_linux_hw_spi;
import static com.codeferm.u8g2.U8g2.u8x8_byte_sw_i2c;
import static com.codeferm.u8g2.demo.Display.SetupType.SSD1306NONAME;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;

/**
 * Dynamically allocate u8g2_t structure and call display setup. Change the setupXX call to match your display.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class Display {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Display.class);

    /**
     * Add display types here and in setup method.
     */
    public enum SetupType {
        SSD1306NONAME,
        SSD1306UNIVISION;
    }

    private SetupType setupType;

    /**
     * Default constructor.
     */
    public Display() {
        setupType = SSD1306NONAME;
    }

    /**
     * Constructor to set setup type.
     *
     * @param setupType Setup type.
     */
    public Display(final SetupType setupType) {
        this.setupType = setupType;
    }

    /**
     * Centralize I2C setup.
     *
     * @param u8g2 Pointer to u8g2_t structure.
     * @param rotation Display rotation.
     * @param byteCb Byte callback pointer.
     * @param gpioAndDelayCb GPIO callback pointer.
     */
    public void setupI2c(final long u8g2, final long rotation, final long byteCb,
            final long gpioAndDelayCb) {
        switch (setupType) {
            case SSD1306NONAME:
                U8g2.setupSsd1306I2c128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306UNIVISION:
                U8g2.setupSsd1306I2c128x32UnivisionF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            default:
                throw new RuntimeException(String.format("Setup type %s not supported for I2C", setupType));
        }
    }

    /**
     * Centralize SPI setup.
     *
     * @param u8g2 Pointer to u8g2_t structure.
     * @param rotation Display rotation.
     * @param byteCb Byte callback pointer.
     * @param gpioAndDelayCb GPIO callback pointer.
     */
    public void setupSpi(final long u8g2, final long rotation, final long byteCb,
            final long gpioAndDelayCb) {
        switch (setupType) {
            case SSD1306NONAME:
                U8g2.setupSsd1306128x64NonameF(u8g2, U8G2_R0, u8x8_byte_arm_linux_hw_spi, u8x8_arm_linux_gpio_and_delay);
                break;
            default:
                throw new RuntimeException(String.format("Setup type %s not supported for SPI", setupType));
        }
    }

    /**
     * Initialize I2C hardware driven display and return pointer to u8g2_t structure.
     *
     * @param bus I2C bus number.
     * @param address I2C address.
     * @return Pointer to u8g2_t structure.
     */
    public long initHwI2c(final int bus, final int address) {
        final var u8g2 = U8g2.initU8g2();
        setupI2c(u8g2, U8G2_R0, u8x8_byte_arm_linux_hw_i2c, u8x8_arm_linux_gpio_and_delay);
        U8g2.initI2cHw(u8g2, bus);
        U8g2.setI2CAddress(u8g2, address * 2);
        U8g2.initDisplay(u8g2);
        logger.debug(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2), U8g2.
                getDrawColor(u8g2)));
        logger.debug(String.format("Bus 0x%02x, Address %02x", bus, address));
        return u8g2;
    }

    /**
     * Initialize I2C software driven display and return pointer to u8g2_t structure.
     *
     * @param gpio GPIO chip number.
     * @param scl SCL.
     * @param sda SDA.
     * @param res RESET pin.
     * @param delay Nanosecond delay or 0 for none.
     * @return Pointer to u8g2_t structure.
     */
    public long initSwI2c(final int gpio, final int scl, final int sda, final int res, final long delay) {
        final var u8g2 = U8g2.initU8g2();
        setupI2c(u8g2, U8G2_R0, u8x8_byte_sw_i2c, u8x8_arm_linux_gpio_and_delay);
        U8g2.initI2cSw(u8g2, gpio, scl, sda, res, delay);
        U8g2.initDisplay(u8g2);
        logger.debug(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2), U8g2.
                getDrawColor(u8g2)));
        logger.debug(String.format("GPIO chip %d, SCL %d, SDA %d, RES %d, Delay %d", gpio, scl, sda, res, delay));
        return u8g2;
    }

    /**
     * Initialize SPI display and return pointer to u8g2_t structure.
     *
     * @param gpio GPIO chip number.
     * @param bus SPI bus number.
     * @param dc DC pin.
     * @param res RESET pin.
     * @param cs CS pin.
     * @return Pointer to u8g2_t structure.
     */
    public long initHwSpi(final int gpio, final int bus, final int dc, final int res, final int cs) {
        final var u8g2 = U8g2.initU8g2();
        setupSpi(u8g2, U8G2_R0, u8x8_byte_arm_linux_hw_spi, u8x8_arm_linux_gpio_and_delay);
        U8g2.initSpiHw(u8g2, gpio, bus, dc, res, cs);
        U8g2.initDisplay(u8g2);
        logger.debug(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2), U8g2.
                getDrawColor(u8g2)));
        logger.debug(String.format("GPIO chip %d, bus 0x%02x, DC %d, RES %d, CS %d", gpio, bus, dc, res, cs));
        return u8g2;
    }

    /**
     * Initialize SPI display and return pointer to u8g2_t structure.
     *
     * @param gpio GPIO chip number.
     * @param dc DC pin.
     * @param res RESET pin.
     * @param mosi MOSI pin.
     * @param sck SCK pin.
     * @param cs CS pin.
     * @param delay Nanosecond delay or 0 for none.
     * @return Pointer to u8g2_t structure.
     */
    public long initSwSpi(final int gpio, final int dc, final int res, final int mosi, final int sck, final int cs,
            final long delay) {
        final var u8g2 = U8g2.initU8g2();
        setupSpi(u8g2, U8G2_R0, u8x8_byte_4wire_sw_spi, u8x8_arm_linux_gpio_and_delay);
        U8g2.initSpiSw(u8g2, gpio, dc, res, mosi, sck, cs, delay);
        U8g2.initDisplay(u8g2);
        logger.debug(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2), U8g2.
                getDrawColor(u8g2)));
        logger.debug(String.format("GPIO chip %d, DC %d, RES %d, MOSI %d, SCK %d, CS %d, Delay %d", gpio, dc, res, mosi, sck, cs,
                delay));
        return u8g2;
    }

    /**
     * Sleep for desired milliseconds.
     *
     * @param milliseconds Milliseconds to sleep.
     */
    public void sleep(final long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Free u8g2_t structure from memory, close down GPIO, I2C and SPI.
     *
     * @param u8g2 Pointer to u8g2_t structure.
     */
    public void done(final long u8g2) {
        logger.debug("Done");
        U8g2.doneUserData(u8g2);
        U8g2.done(u8g2);
        U8g2.doneI2c();
        U8g2.doneSpi();
    }
}
