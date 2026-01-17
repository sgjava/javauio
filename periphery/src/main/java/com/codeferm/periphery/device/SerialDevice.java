/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Serial;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe Serial device wrapper for high-level UART operations.
 *
 * Provides a synchronized interface for reading and writing data over a serial port.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SerialDevice implements AutoCloseable {

    /**
     * Reentrant lock for thread-safe Serial access.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Serial device wrapper.
     */
    private final Serial serial;

    /**
     * Handle to the Serial device.
     */
    private final long handle;

    /**
     * Initialize serial device.
     *
     * @param device Serial device path (e.g., "/dev/ttyS1").
     * @param baud Baud rate (e.g., 115200).
     */
    public SerialDevice(final String device, final int baud) {
        this.serial = new Serial(device, baud);
        this.handle = serial.getHandle();
    }

    /**
     * Write a byte array to the serial port.
     *
     * @param data Data to write.
     * @return Number of bytes written.
     */
    public int write(final byte[] data) {
        lock.lock();
        try {
            return Serial.serialWrite(handle, data, data.length);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Read data into a buffer with a specific timeout.
     *
     * @param buffer Buffer to read into.
     * @param timeout Timeout in milliseconds.
     * @return Number of bytes read.
     */
    public int read(final byte[] buffer, final int timeout) {
        lock.lock();
        try {
            return Serial.serialRead(handle, buffer, buffer.length, timeout);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Helper to format bytes as Hex for logging.
     *
     * @param b Byte to format.
     * @return 2-digit hex string.
     */
    public static String toHex(final byte b) {
        return String.format("%02X", (short) b & 0xff);
    }

    /**
     * Closes the serial resource safely.
     */
    @Override
    public void close() {
        lock.lock();
        try {
            serial.close();
        } finally {
            lock.unlock();
        }
    }
}