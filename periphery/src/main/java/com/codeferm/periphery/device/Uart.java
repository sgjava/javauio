/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Serial;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe Serial (UART) wrapper for Linux termios tty devices.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class Uart implements AutoCloseable {

    /**
     * Reentrant lock for thread-safe access.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Serial periphery wrapper.
     */
    private final Serial serial;

    /**
     * Handle to the Serial device.
     */
    private final long handle;

    /**
     * Open UART with default settings (8N1).
     *
     * @param path Device path (e.g., "/dev/ttyUSB0").
     * @param baudrate Baud rate.
     */
    public Uart(final String path, final int baudrate) {
        this.serial = new Serial(path, baudrate);
        this.handle = serial.getHandle();
        log.atDebug().log("UART {} opened at {} baud", path, baudrate);
    }

    /**
     * Read data from the serial port.
     *
     * @param buf Read buffer.
     * @param timeoutMs Timeout in milliseconds (0 for non-blocking, negative for blocking).
     * @return Number of bytes read.
     */
    public int read(final byte[] buf, final int timeoutMs) {
        lock.lock();
        try {
            return Serial.serialRead(handle, buf, buf.length, timeoutMs);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Write data to the serial port.
     *
     * @param buf Write buffer.
     * @return Number of bytes written.
     */
    public int write(final byte[] buf) {
        lock.lock();
        try {
            return Serial.serialWrite(handle, buf, buf.length);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Flush the write buffer.
     */
    public void flush() {
        lock.lock();
        try {
            Serial.serialFlush(handle);
        } finally {
            lock.unlock();
        }
    }

    // --- Configuration Wrappers ---

    public int getBaudRate() {
        lock.lock();
        try {
            final var val = new int[1];
            Serial.serialGetBaudRate(handle, val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    public void setBaudRate(final int baudRate) {
        lock.lock();
        try {
            Serial.serialSetBaudRate(handle, baudRate);
        } finally {
            lock.unlock();
        }
    }

    public int getDataBits() {
        lock.lock();
        try {
            final var val = new int[1];
            Serial.serialGetDataBits(handle, val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    public void setDataBits(final int dataBits) {
        lock.lock();
        try {
            Serial.serialSetDataBits(handle, dataBits);
        } finally {
            lock.unlock();
        }
    }

    public int getParity() {
        lock.lock();
        try {
            final var val = new int[1];
            Serial.serialGetParity(handle, val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    public void setParity(final int parity) {
        lock.lock();
        try {
            Serial.serialSetParity(handle, parity);
        } finally {
            lock.unlock();
        }
    }

    public int getInputWaiting() {
        lock.lock();
        try {
            final var val = new int[1];
            Serial.serialInputWaiting(handle, val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return Serial.serialToString(handle);
        } finally {
            lock.unlock();
        }
    }

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
