/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Serial;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe Serial (UART) wrapper for Linux termios tty devices.
 * This class encapsulates the hardware handle to prevent subverting thread safety.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class Uart implements AutoCloseable {

    /**
     * Reentrant lock for thread-safe access to serial hardware.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Serial periphery wrapper.
     */
    private final Serial serial;

    /**
     * Open UART with default settings (8N1).
     *
     * @param path     Device path (e.g., "/dev/ttyUSB0").
     * @param baudrate Initial baud rate for the connection.
     */
    public Uart(final String path, final int baudrate) {
        this.serial = new Serial(path, baudrate);
        log.atDebug().log("UART {} opened at {} baud", path, baudrate);
    }

    /**
     * Read data from the serial port.
     *
     * @param buf       Read buffer to store incoming data.
     * @param timeoutMs Timeout in milliseconds (0 for non-blocking, negative for blocking).
     * @return Number of bytes actually read.
     */
    public int read(final byte[] buf, final int timeoutMs) {
        lock.lock();
        try {
            // Internal use of handle via serial.getHandle()
            return Serial.serialRead(serial.getHandle(), buf, buf.length, timeoutMs);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Write data to the serial port.
     *
     * @param buf Buffer containing data to be written.
     * @return Number of bytes actually written.
     */
    public int write(final byte[] buf) {
        lock.lock();
        try {
            return Serial.serialWrite(serial.getHandle(), buf, buf.length);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Flush the serial write buffer.
     */
    public void flush() {
        lock.lock();
        try {
            Serial.serialFlush(serial.getHandle());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves the current baud rate configuration.
     *
     * @return The baud rate as an integer.
     */
    public int getBaudRate() {
        lock.lock();
        try {
            final var val = new int[1];
            Serial.serialGetBaudRate(serial.getHandle(), val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sets a new baud rate for the serial connection.
     *
     * @param baudRate The desired baud rate.
     */
    public void setBaudRate(final int baudRate) {
        lock.lock();
        try {
            Serial.serialSetBaudRate(serial.getHandle(), baudRate);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves the current number of data bits configured.
     *
     * @return Number of data bits (typically 5, 6, 7, or 8).
     */
    public int getDataBits() {
        lock.lock();
        try {
            final var val = new int[1];
            Serial.serialGetDataBits(serial.getHandle(), val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sets the number of data bits.
     *
     * @param dataBits The number of bits per character.
     */
    public void setDataBits(final int dataBits) {
        lock.lock();
        try {
            Serial.serialSetDataBits(serial.getHandle(), dataBits);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves the current parity setting.
     *
     * @return Parity mode (0: None, 1: Odd, 2: Even).
     */
    public int getParity() {
        lock.lock();
        try {
            final var val = new int[1];
            Serial.serialGetParity(serial.getHandle(), val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sets the parity bit configuration.
     *
     * @param parity The parity mode to set.
     */
    public void setParity(final int parity) {
        lock.lock();
        try {
            Serial.serialSetParity(serial.getHandle(), parity);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks the number of bytes available to be read from the input buffer.
     *
     * @return Number of bytes waiting in the input queue.
     */
    public int getInputWaiting() {
        lock.lock();
        try {
            final var val = new int[1];
            Serial.serialInputWaiting(serial.getHandle(), val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a string representation of the UART device state.
     *
     * @return String detailing the serial port configuration.
     */
    @Override
    public String toString() {
        lock.lock();
        try {
            return Serial.serialToString(serial.getHandle());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Releases serial resources and closes the device handle.
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
