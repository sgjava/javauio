/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Pwm;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * High-level LED device abstraction using hardware PWM. This class is thread-safe using a ReentrantLock for hardware state
 * management.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class LedPwm implements AutoCloseable {

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
    public LedPwm(final int chip, final int channel) {
        this.pwm = new Pwm(chip, channel);
    }

    /**
     * Get PWM handle.
     *
     * @return PWM handle.
     */
    public long getHandle() {
        return pwm.getHandle();
    }

    /**
     * Enable PWM.
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
     * Disable PWM.
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
     * Gradually increase and decrease LED brightness.
     *
     * @param period Set the period in seconds of the PWM.
     * @param startDc Starting duty cycle in nanoseconds.
     * @param dcInc Duty cycle increment in nanoseconds.
     * @param count Number of times to loop.
     * @param sleepTime Sleep time in microseconds.
     * @throws InterruptedException Possible exception.
     */
    public void changeBrightness(final int period, final int startDc, final int dcInc, final int count,
            final int sleepTime) throws InterruptedException {
        lock.lock();
        try {
            Pwm.pwmSetPeriodNs(pwm.getHandle(), period);
            var dutyCycle = startDc;
            var i = 0;
            while (i < count) {
                Pwm.pwmSetDutyCycleNs(pwm.getHandle(), dutyCycle);
                // Unlock during sleep to allow other threads access if needed, 
                // though usually brightness ramps are exclusive.
                lock.unlock();
                try {
                    TimeUnit.MICROSECONDS.sleep(sleepTime);
                } finally {
                    lock.lock();
                }
                dutyCycle += dcInc;
                i += 1;
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * Set duty cycle to 0 and close PWM handle. Uses try-with-resources on the autocloseable Pwm field to ensure OS handles are
     * released.
     */
    @Override
    public void close() {
        lock.lock();
        try (pwm) {
            Pwm.pwmSetDutyCycleNs(pwm.getHandle(), 0);
        } finally {
            lock.unlock();
        }
    }
}
