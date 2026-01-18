/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * ADXL345 3-Axis digital accelerometer device class.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class Adxl345 implements AutoCloseable {

    /**
     * I2C bus device.
     */
    private final I2cBus i2cBus;
    /**
     * I2C address.
     */
    private final short address;
    /**
     * Scaling factor for Gs.
     */
    private float scalingFactor;

    /**
     * Initialize ADXL345 with I2cBus.
     *
     * @param i2cBus Thread-safe I2C bus.
     * @param address I2C address.
     */
    public Adxl345(final I2cBus i2cBus, final short address) {
        this.i2cBus = i2cBus;
        this.address = address;
        log.atDebug().log("ADXL345 initialized at address 0x{}", Integer.toHexString(address));
    }

    /**
     * Enable the device and set default configuration.
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

    public short getRange() {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x31, buf);
        return (short) (buf[0] & 0x03);
    }

    public void setRange(final short value) {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x31, buf);
        // 0x08 ensures FULL_RES bit is enabled for consistent scaling
        i2cBus.writeReg8(address, (short) 0x31, (short) (((buf[0] & ~0x0f) | value) | 0x08));
        refreshScalingFactor();
    }

    public boolean getFullResolution() {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x31, buf);
        return (short) (buf[0] & 0x08) == (short) 0x08;
    }

    public short getDataRate() {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x2c, buf);
        return (short) (buf[0] & 0x0f);
    }

    public void setDataRate(final short value) {
        i2cBus.writeReg8(address, (short) 0x2c, (short) (value & 0x0f));
    }

    /**
     * Updates the internal scaling factor based on current range and resolution.
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
     */
    private int bytesToInt(final byte lowByte, final byte highByte) {
        var value = ((highByte & 0x03) * 256 + (lowByte & 0xff));
        if (value > 511) {
            value -= 1024;
        }
        return value;
    }

    /**
     * Read x, y, z axes and return scaled m/s^2.
     *
     * @return Map of float values keyed by "x", "y", "z".
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

    public short getDeviceId() {
        final var buf = new short[1];
        i2cBus.readReg8(address, (short) 0x00, buf);
        return buf[0];
    }

    @Override
    public void close() {
        log.atDebug().log("ADXL345 device instance closed");
    }
}