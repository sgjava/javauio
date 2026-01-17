/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.Pwm;
import java.util.concurrent.TimeUnit;

/**
 * High-level LED device abstraction using hardware PWM.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class LedPwm implements AutoCloseable {

    /**
     * PWM wrapper.
     */
    private final Pwm pwm;

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
     * * @return PWM handle.
     */
    public long getHandle() {
        return pwm.getHandle();
    }

    /**
     * Enable PWM.
     */
    public void enable() {
        Pwm.pwmEnable(pwm.getHandle());
    }

    /**
     * Disable PWM.
     */
    public void disable() {
        Pwm.pwmDisable(pwm.getHandle());
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
        Pwm.pwmSetPeriodNs(pwm.getHandle(), period);
        var dutyCycle = startDc;
        var i = 0;
        while (i < count) {
            Pwm.pwmSetDutyCycleNs(pwm.getHandle(), dutyCycle);
            TimeUnit.MICROSECONDS.sleep(sleepTime);
            dutyCycle += dcInc;
            i += 1;
        }
    }

    /**
     * Set duty cycle to 0 and close PWM handle.
     */
    @Override
    public void close() {
        Pwm.pwmSetDutyCycleNs(pwm.getHandle(), 0);
        pwm.close();
    }
}
