/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Led;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe LED wrapper for Linux sysfs LEDs.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SysLed implements AutoCloseable {

    /**
     * Reentrant lock for thread-safe access.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Led periphery wrapper.
     */
    private final Led led;

    /**
     * Handle to the LED device.
     */
    private final long handle;

    /**
     * Initialize LED.
     *
     * @param name LED name (e.g., "led0").
     */
    public SysLed(final String name) {
        this.led = new Led(name);
        // Access handle via Lombok getter (ensure Lombok is working)
        this.handle = led.getHandle();
        log.atDebug().log("LED {} initialized", name);
    }

    /**
     * Read LED brightness as boolean.
     *
     * @return true if brightness > 0, false otherwise.
     */
    public boolean read() {
        lock.lock();
        try {
            final var val = new boolean[1];
            Led.ledRead(handle, val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    /**
     * Set LED brightness to max or off.
     *
     * @param value true for max brightness, false for off.
     */
    public void write(final boolean value) {
        lock.lock();
        try {
            Led.ledWrite(handle, value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get LED brightness as integer.
     *
     * @return Brightness value.
     */
    public int getBrightness() {
        lock.lock();
        try {
            final var val = new int[1];
            Led.ledGetBrightness(handle, val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    /**
     * Set LED brightness as integer.
     *
     * @param brightness Brightness value.
     */
    public void setBrightness(final int brightness) {
        lock.lock();
        try {
            Led.ledSetBrightness(handle, brightness);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get maximum brightness supported by this LED.
     *
     * @return Max brightness value.
     */
    public int getMaxBrightness() {
        lock.lock();
        try {
            final var val = new int[1];
            Led.ledGetMaxBrightness(handle, val);
            return val[0];
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the kernel name of the LED using the provided wrapper method.
     *
     * @return LED name.
     */
    public String getName() {
        lock.lock();
        try {
            // Use the static Java wrapper provided in your Led class
            return Led.ledName(handle);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a string representation of the LED handle properties.
     *
     * @return LED properties as String.
     */
    @Override
    public String toString() {
        lock.lock();
        try {
            // Use the static Java wrapper provided in your Led class
            return Led.ledToString(handle);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Closes the LED resource safely.
     */
    @Override
    public void close() {
        lock.lock();
        try {
            led.close();
        } finally {
            lock.unlock();
        }
    }
}
