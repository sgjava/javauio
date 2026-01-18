/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Spi;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe SPI bus wrapper for Linux spidev devices.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SpiBus implements AutoCloseable {

    /**
     * Reentrant lock for thread-safe SPI access.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Spi periphery wrapper.
     */
    private final Spi spi;

    /**
     * Handle to the SPI device.
     */
    private final long handle;

    /**
     * Initialize SPI bus with default settings.
     *
     * @param path spidev path.
     * @param mode SPI mode (0, 1, 2, or 3).
     * @param maxSpeed Max speed in hertz.
     */
    public SpiBus(final String path, final int mode, final int maxSpeed) {
        this.spi = new Spi(path, mode, maxSpeed);
        this.handle = spi.getHandle();
        log.atDebug().log("SPI bus {} initialized (Mode: {}, Speed: {})", path, mode, maxSpeed);
    }

    /**
     * Initialize SPI bus with advanced settings.
     */
    public SpiBus(final String path, final int mode, final int maxSpeed, final int bitOrder, 
                  final byte bitsPerWord, final byte extraFlags) {
        this.spi = new Spi(path, mode, maxSpeed, bitOrder, bitsPerWord, extraFlags);
        this.handle = spi.getHandle();
        log.atDebug().log("SPI bus {} initialized (Advanced)", path);
    }

    /**
     * Shift out txBuf while shifting in rxBuf.
     *
     * @param txBuf Transmit buffer.
     * @param rxBuf Receive buffer.
     * @param len Word count.
     * @return 0 on success.
     */
    public int transfer(final byte[] txBuf, final byte[] rxBuf, final long len) {
        lock.lock();
        try {
            return Spi.spiTransfer(handle, txBuf, rxBuf, len);
        } finally {
            lock.unlock();
        }
    }

    // --- Configuration Getters ---

    public int getMode() {
        lock.lock();
        try {
            final var mode = new int[1];
            Spi.spiGetMode(handle, mode);
            return mode[0];
        } finally {
            lock.unlock();
        }
    }

    public int getMaxSpeed() {
        lock.lock();
        try {
            final var speed = new int[1];
            Spi.spiGetMaxSpeed(handle, speed);
            return speed[0];
        } finally {
            lock.unlock();
        }
    }

    public int getBitOrder() {
        lock.lock();
        try {
            final var order = new int[1];
            Spi.spiGetBitOrder(handle, order);
            return order[0];
        } finally {
            lock.unlock();
        }
    }

    // --- Configuration Setters ---

    public void setMode(final int mode) {
        lock.lock();
        try {
            Spi.spiSetMode(handle, mode);
        } finally {
            lock.unlock();
        }
    }

    public void setMaxSpeed(final int maxSpeed) {
        lock.lock();
        try {
            Spi.spiSetMaxSpeed(handle, maxSpeed);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return Spi.spiToString(handle);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            spi.close();
        } finally {
            lock.unlock();
        }
    }
}
