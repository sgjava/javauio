/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * ADXL345 3-Axis digital accelerometer device class.
 * This class provides a high-level interface to the ADXL345 accelerometer via I2C.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class Adxl345 implements AutoCloseable {

    /**
     * I2C bus device used for communication.
     */
    private final I2cBus i2cBus;
    /**
     * I2C address of the ADXL345 device.
     */
    private final short address;
    /**
     * Scaling factor used to convert raw register values to G-force.
     */
    private float scalingFactor;

    /**
     * Initialize ADXL345 with a thread-safe I2C bus and device address.
     *
     * @param i2cBus  Thread-safe I2C bus implementation.
     * @param address I2C address of the ADXL345.
     */
    public Adxl345(final I2cBus i2cBus, final short address) {
        this.i2cBus = i2cBus;
        this.address = address;
        log.atDebug().log("ADXL345 initialized at address 0x{}", Integer.toHexString(address));
    }

    /**
     * Enable the device and set default configuration.
     * Powers up the device by setting the Measure bit, sets range to +/- 2g, 
     * and sets the data rate to 100 Hz.
     */
    public void enable() {
        // Power-up device: set Measure bit in POWER_CTL register
        i2cBus.writeReg8(address, (short) 0x2d, (short) 0x08);
        // Default range +/- 2g
        setRange((short) 0x00);
        // Default 100 Hz data rate
        setDataRate((short) 0x0a);
        refreshScalingFactor();
    }

    /**
     * Gets the current measurement range from the DATA_FORMAT register.
     *
     * @return Short value representing the range (0x00: 2g, 0x01: 4g, 0x02: 8g, 0x03: 16g).
     */
    public short getRange() {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x31, buf);
        return (short) (buf[0] & 0x03);
    }

    /**
     * Sets the measurement range in the DATA_FORMAT register and refreshes the scaling factor.
     *
     * @param value Short value for the range (e.g., 0x00 for +/- 2g).
     */
    public void setRange(final short value) {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x31, buf);
        // 0x08 ensures FULL_RES bit is enabled for consistent scaling
        i2cBus.writeReg8(address, (short) 0x31, (short) (((buf[0] & ~0x0f) | value) | 0x08));
        refreshScalingFactor();
    }

    /**
     * Checks if the device is in Full Resolution mode.
     *
     * @return True if FULL_RES bit is set, false otherwise.
     */
    public boolean getFullResolution() {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x31, buf);
        return (short) (buf[0] & 0x08) == (short) 0x08;
    }

    /**
     * Gets the current data rate from the BW_RATE register.
     *
     * @return Short value representing the data rate.
     */
    public short getDataRate() {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x2c, buf);
        return (short) (buf[0] & 0x0f);
    }

    /**
     * Sets the data rate in the BW_RATE register.
     *
     * @param value Short value representing the desired data rate (e.g., 0x0a for 100 Hz).
     */
    public void setDataRate(final short value) {
        i2cBus.writeReg8(address, (short) 0x2c, (short) (value & 0x0f));
    }

    /**
     * Updates the internal scaling factor based on current range and resolution settings.
     * Recalculates based on the number of bits and the G-range.
     */
    public void refreshScalingFactor() {
        final var range = getRange();
        final var resolution = getFullResolution();
        final var bits = resolution ? 10 + range : 10;
        final var gRange = 4f * (float) Math.pow(2, range);
        final var bitRange = (float) Math.pow(2, bits);
        this.scalingFactor = gRange / bitRange;
    }

    /**
     * Internal helper to convert two bytes into a signed 10-bit integer.
     *
     * @param lowByte  Lower 8 bits of the axis data.
     * @param highByte Upper bits of the axis data.
     * @return Signed integer value of the axis.
     */
    private int bytesToInt(final byte lowByte, final byte highByte) {
        var value = ((highByte & 0x03) * 256 + (lowByte & 0xff));
        if (value > 511) {
            value -= 1024;
        }
        return value;
    }

    /**
     * Reads x, y, z axes and returns scaled acceleration in m/s^2.
     * Data is read starting from register DATAX0 (0x32).
     *
     * @return Map containing float values for "x", "y", and "z" axes in m/s^2.
     */
    public Map<String, Float> read() {
        final var data = new byte[6];
        // Read 6 bytes starting at DATAX0 (0x32)
        i2cBus.readReg8(address, (short) 0x32, data);
        final Map<String, Float> map = new HashMap<>();
        map.put("x", bytesToInt(data[0], data[1]) * scalingFactor * 9.8f);
        map.put("y", bytesToInt(data[2], data[3]) * scalingFactor * 9.8f);
        map.put("z", bytesToInt(data[4], data[5]) * scalingFactor * 9.8f);
        return map;
    }

    /**
     * Reads the device ID from the DEVID register (0x00).
     *
     * @return The fixed device ID (typically 0xE5).
     */
    public short getDeviceId() {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x00, buf);
        return buf[0];
    }

    /**
     * Closes the device instance.
     */
    @Override
    public void close() {
        log.atDebug().log("ADXL345 device instance closed");
    }
}
