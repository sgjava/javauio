/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Pwm;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * High-level LED device abstraction using hardware PWM. 
 * This class is thread-safe; the raw handle is not exposed to prevent 
 * subverting thread safety.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class PwmLed implements AutoCloseable {

    /**
     * PWM wrapper.
     */
    private final Pwm pwm;

    /**
     * Reentrant lock for thread-safe PWM access.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Initialize LED on specified PWM chip and channel.
     *
     * @param chip PWM chip number.
     * @param channel PWM channel number.
     */
    public PwmLed(final int chip, final int channel) {
        this.pwm = new Pwm(chip, channel);
    }

    /**
     * Enable PWM output.
     */
    public void enable() {
        lock.lock();
        try {
            Pwm.pwmEnable(pwm.getHandle());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Disable PWM output.
     */
    public void disable() {
        lock.lock();
        try {
            Pwm.pwmDisable(pwm.getHandle());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Set the brightness for a single step in a sequence. 
     * This updates both period and duty cycle atomically under a lock.
     *
     * @param periodNs  The period in nanoseconds.
     * @param dutyCycle Duty cycle in nanoseconds.
     */
    public void changeBrightness(final long periodNs, final long dutyCycle) {
        lock.lock();
        try {
            Pwm.pwmSetPeriodNs(pwm.getHandle(), periodNs);
            Pwm.pwmSetDutyCycleNs(pwm.getHandle(), dutyCycle);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Close PWM handle and ensure duty cycle is 0.
     */
    @Override
    public void close() {
        lock.lock();
        try {
            Pwm.pwmSetDutyCycleNs(pwm.getHandle(), 0);
            pwm.close();
        } finally {
            lock.unlock();
        }
    }
}