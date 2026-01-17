/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Gpio;
import static com.codeferm.periphery.Gpio.GPIO_DIR_IN;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_BOTH;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_FALLING;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_RISING;
import static com.codeferm.periphery.Gpio.GPIO_POLL_EVENT;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Blocking button device using GPIO edge detection.
 *
 * Provides a thread-safe abstraction for waiting on button events (rising, falling, or both edges) with timeout support.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class BlockingButton implements AutoCloseable {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(BlockingButton.class);

    /**
     * Reentrant lock for thread-safe GPIO access and lifecycle management.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Gpio device wrapper.
     */
    private final Gpio gpio;

    /**
     * Handle to the Gpio device.
     */
    private final long handle;

    /**
     * Data record for button events.
     *
     * @param edge The type of edge detected (RISING, FALLING).
     * @param timestamp The event timestamp in nanoseconds.
     */
    public record ButtonEvent(int edge, long timestamp) {

    }

    /**
     * Initialize button on specified GPIO device and line.
     *
     * @param device GPIO device path (e.g., "/dev/gpiochip1").
     * @param line GPIO line number.
     */
    public BlockingButton(final String device, final int line) {
        this.gpio = new Gpio(device, line, GPIO_DIR_IN);
        this.handle = gpio.getHandle();
        // Initialize with default edge detection
        setEdgeDetection(GPIO_EDGE_BOTH);
    }

    /**
     * Configures the edge detection type for the button.
     *
     * @param edge Type of edge to detect (GPIO_EDGE_NONE, GPIO_EDGE_RISING, GPIO_EDGE_FALLING, or GPIO_EDGE_BOTH).
     */
    public final void setEdgeDetection(int edge) {
        lock.lock();
        try {
            Gpio.gpioSetEdge(handle, edge);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Wait for a button event within the specified timeout.
     *
     * @param timeoutMillis Maximum time to wait in milliseconds.
     * @return ButtonEvent containing edge and timestamp, or null if a timeout occurred.
     */
    public ButtonEvent waitForEvent(int timeoutMillis) {
        lock.lock();
        try {
            // Poll for event and timeout if no event occurs
            if (Gpio.gpioPoll(handle, timeoutMillis) == GPIO_POLL_EVENT) {
                final var edge = new int[1];
                final var timestamp = new long[1];
                Gpio.gpioReadEvent(handle, edge, timestamp);
                return new ButtonEvent(edge[0], timestamp[0]);
            }
        } finally {
            lock.unlock();
        }
        return null; // Timeout
    }

    /**
     * Formats a timestamp into a human-readable string (seconds.nanoseconds).
     *
     * @param timestamp Nanosecond timestamp.
     * @return Formatted string.
     */
    public static String formatTimestamp(long timestamp) {
        return String.format("%8d.%9d", timestamp / 1000000000, timestamp % 1000000000);
    }

    /**
     * Returns the string representation of an edge type. Uses if-else logic to avoid constant expression requirements of switch
     * statements.
     *
     * @param edge The edge constant.
     * @return "Rising", "Falling", or "Invalid".
     */
    public static String edgeToString(int edge) {
        if (edge == GPIO_EDGE_RISING) {
            return "Rising";
        } else if (edge == GPIO_EDGE_FALLING) {
            return "Falling";
        } else {
            return "Invalid";
        }
    }

    /**
     * Closes the GPIO resource safely. Acquires the lock to ensure no concurrent operations happen during shutdown.
     */
    @Override
    public void close() {
        lock.lock();
        try {
            gpio.close();
        } finally {
            lock.unlock();
        }
    }
}
