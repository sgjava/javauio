/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.I2c;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe I2C bus wrapper for Linux i2c-dev character devices.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class I2cBus implements AutoCloseable {

    /**
     * Reentrant lock for thread-safe I2C access.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * I2c periphery wrapper.
     */
    private final I2c i2c;

    /**
     * Handle to the I2C device.
     */
    private final long handle;

    /**
     * Initialize I2C bus.
     *
     * @param device I2C device path (e.g., "/dev/i2c-0").
     */
    public I2cBus(final String device) {
        this.i2c = new I2c(device);
        this.handle = i2c.getHandle();
        log.atDebug().log("I2C bus {} initialized", device);
    }

    /**
     * Read array from i2c 8 bit address.
     *
     * @param addr Peripheral address.
     * @param reg Register address.
     * @param buf Read buffer.
     * @return 0 on success.
     */
    public int readReg8(final short addr, final short reg, final byte[] buf) {
        lock.lock();
        try {
            return I2c.i2cReadReg8(handle, addr, reg, buf);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Read array from i2c 8 bit address into short array.
     *
     * @param addr Peripheral address.
     * @param reg Register address.
     * @param regVal Read buffer.
     * @return 0 on success.
     */
    public int readReg8(final short addr, final short reg, final short[] regVal) {
        lock.lock();
        try {
            return I2c.i2cReadReg8(handle, addr, reg, regVal);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Read two consecutive i2c 8 bit addresses and combine them.
     *
     * @param addr Peripheral address.
     * @param reg Register address.
     * @param regVal Read buffer.
     * @return 0 on success.
     */
    public int readWord8(final short addr, final short reg, final int[] regVal) {
        lock.lock();
        try {
            return I2c.i2cReadWord8(handle, addr, reg, regVal);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Write value to i2c 8 bit address.
     *
     * @param addr Peripheral address.
     * @param reg Register address.
     * @param value Value to write.
     * @return 0 on success.
     */
    public int writeReg8(final short addr, final short reg, final short value) {
        lock.lock();
        try {
            return I2c.i2cWriteReg8(handle, addr, reg, value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Write value to i2c 16 bit address.
     *
     * @param addr Peripheral address.
     * @param reg Register address.
     * @param value Value to write.
     * @return 0 on success.
     */
    public int writeReg16(final short addr, final short reg, final short value) {
        lock.lock();
        try {
            return I2c.i2cWriteReg16(handle, addr, reg, value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Return a string representation of the I2C handle.
     * * @return I2C handle as String.
     */
    @Override
    public String toString() {
        lock.lock();
        try {
            return I2c.i2cToString(handle);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Closes the I2C resource safely.
     */
    @Override
    public void close() {
        lock.lock();
        try {
            i2c.close();
        } finally {
            lock.unlock();
        }
    }
}
