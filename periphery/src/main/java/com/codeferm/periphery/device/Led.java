/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Gpio;
import com.codeferm.periphery.Gpio.GpioConfig;
import static com.codeferm.periphery.Common.cString;
import static com.codeferm.periphery.Gpio.*;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * LED device mapped to a Linux character device GPIO pin. This class is thread-safe.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class Led implements AutoCloseable {

    /**
     * GPIO handle.
     */
    private final Gpio gpio;
    /**
     * Lock for thread-safe access to the GPIO handle.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructor to initialize the LED using character device GPIO.
     *
     * @param device GPIO device path (e.g., "/dev/gpiochip0").
     * @param line GPIO line number.
     */
    public Led(final String device, final int line) {
        // Initialize GPIO character device for output using fluent GpioConfig builder
        this.gpio = new Gpio(device, line, GpioConfig.builder()
                .direction(GPIO_DIR_OUT)
                .bias(GPIO_BIAS_DEFAULT)
                .drive(GPIO_DRIVE_DEFAULT)
                .edge(GPIO_EDGE_NONE)
                .label(cString("LedDevice"))
                .build());
        log.atDebug().log("LED initialized using character device {} line {}", device, line);
    }

    /**
     * Turn the LED on.
     */
    public void on() {
        setState(true);
    }

    /**
     * Turn the LED off.
     */
    public void off() {
        setState(false);
    }

    /**
     * Set the LED state.
     *
     * @param value True for on, false for off.
     */
    public void setState(final boolean value) {
        lock.lock();
        try {
            Gpio.gpioWrite(gpio.getHandle(), value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the current state of the LED pin. Uses the JNI pattern: passing a boolean array to the native layer.
     *
     * @return True if on, false if off.
     */
    public boolean getState() {
        lock.lock();
        try {
            var value = new boolean[1];
            if (Gpio.gpioRead(gpio.getHandle(), value) < 0) {
                throw new RuntimeException(String.format("Error %d: %s",
                        Gpio.gpioErrNo(gpio.getHandle()),
                        Gpio.gpioErrMessage(gpio.getHandle())));
            }
            return value[0];
        } finally {
            lock.unlock();
        }
    }

    /**
     * Toggle the LED state.
     */
    public void toggle() {
        lock.lock();
        try {
            setState(!getState());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Close the GPIO character device resource.
     */
    @Override
    public void close() {
        lock.lock();
        try {
            if (gpio != null) {
                gpio.close();
                log.atDebug().log("LED GPIO character device closed");
            }
        } finally {
            lock.unlock();
        }
    }
}
