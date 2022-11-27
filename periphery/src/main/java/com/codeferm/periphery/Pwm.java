/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Common.MAX_CHAR_ARRAY_LEN;
import static com.codeferm.periphery.Common.jString;
import static com.codeferm.periphery.Common.memMove;
import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;

/**
 * c-periphery PWM wrapper functions for Linux userspace sysfs PWMs.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@JniClass
public class Pwm implements AutoCloseable {

    /**
     * Function was successful.
     */
    public static final int PWM_SUCCESS = 0;
    /**
     * periphery library.
     */
    private static final Library LIBRARY = new Library("periphery", Pwm.class);
    /**
     * PWM handle.
     */
    final private long handle;

    /**
     * Load library.
     */
    static {
        LIBRARY.load();
        init();
    }

    /**
     * Load constants.
     */
    @JniMethod(flags = {CONSTANT_INITIALIZER})
    private static native void init();
    /**
     * Error constants.
     */
    @JniField(flags = {CONSTANT})
    public static int PWM_ERROR_ARG;
    @JniField(flags = {CONSTANT})
    public static int PWM_ERROR_OPEN;
    @JniField(flags = {CONSTANT})
    public static int PWM_ERROR_QUERY;
    @JniField(flags = {CONSTANT})
    public static int PWM_ERROR_CONFIGURE;
    @JniField(flags = {CONSTANT})
    public static int PWM_ERROR_CLOSE;
    /**
     * Polarity constants.
     */
    @JniField(flags = {CONSTANT})
    public static int PWM_POLARITY_NORMAL;
    @JniField(flags = {CONSTANT})
    public static int PWM_POLARITY_INVERSED;

    /**
     * Open the sysfs PWM with the specified chip and channel.
     *
     * @param chip PWM chip.
     * @param channel PWM channel.
     */
    public Pwm(final int chip, final int channel) {
        // Allocate handle
        handle = pwmNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open device
        if (pwmOpen(handle, chip, channel) != PWM_SUCCESS) {
            // Free handle before throwing exception
            pwmFree(handle);
            throw new RuntimeException(pwmErrMessage(handle));
        }
    }

    /**
     * Close and free handle.
     */
    @Override
    public void close() {
        pwmClose(handle);
        pwmFree(handle);
    }
    
    /**
     * Handle accessor.
     *
     * @return Handle.
     */
    public long getHandle() {
        return handle;
    }    

    /**
     * Allocate an PWM handle.
     *
     * @return A valid handle on success, or NULL on failure.
     */
    @JniMethod(accessor = "pwm_new")
    public static final native long pwmNew();

    /**
     * Open the sysfs PWM with the specified chip and channel.
     *
     * @param pwm Valid pointer to an allocated PWM handle structure.
     * @param chip PWM chip.
     * @param channel PWM channel.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_open")
    public static native int pwmOpen(long pwm, int chip, int channel);

    /**
     * Enable the PWM output.
     *
     * @param pwm Valid pointer to an allocated PWM handle structure.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_enable")
    public static native int pwmEnable(long pwm);

    /**
     * Disable the PWM output.
     *
     * @param pwm Valid pointer to an allocated PWM handle structure.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_disable")
    public static native int pwmDisable(long pwm);

    /**
     * Close the PWM.
     *
     * @param pwm Valid pointer to an allocated PWM handle structure.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_close")
    public static native int pwmClose(long pwm);

    /**
     * Free an PWM handle.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     */
    @JniMethod(accessor = "pwm_free")
    public static native void pwmFree(long pwm);

    /**
     * Get the output state of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param enabled True if enabled, false if disabled.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_get_enabled")
    public static native int pwmGetEnabled(long pwm, boolean[] enabled);

    /**
     * Get the period in nanoseconds of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param periodNs Period in nanoseconds.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_get_period_ns")
    public static native int pwmGetPeriodNs(long pwm, long[] periodNs);

    /**
     * Get the duty cycle in nanoseconds of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param dutyCycleNs Duty cycle in nanoseconds of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_get_duty_cycle_ns")
    public static native int pwmGetDutyCycleNs(long pwm, long[] dutyCycleNs);

    /**
     * Get the period in seconds of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param period Period in seconds of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_get_period")
    public static native int pwmGetPeriod(long pwm, double[] period);

    /**
     * Get the duty cycle as a ratio between 0.0 to 1.0 of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param dutyCycle Duty cycle as a ratio between 0.0 to 1.0 of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_get_duty_cycle")
    public static native int pwmGetDutyCycle(long pwm, double[] dutyCycle);

    /**
     * Get the frequency in Hz of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param frequency Frequency in Hz of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_get_frequency")
    public static native int pwmGetFrequency(long pwm, double[] frequency);

    /**
     * Get the output polarity of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param polarity Polarity of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_get_polarity")
    public static native int pwmGetPolarity(long pwm, int[] polarity);

    /**
     * Set the output state of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param enabled True to enable or false to disable.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_set_enabled")
    public static native int pwmSetEnabled(long pwm, boolean enabled);

    /**
     * Set the period in nanoseconds of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param periodNs Period in nanoseconds of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_set_period_ns")
    public static native int pwmSetPeriodNs(long pwm, long periodNs);

    /**
     * Set the duty cycle in nanoseconds of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param dutyCycle Duty cycle in nanoseconds of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_set_duty_cycle_ns")
    public static native int pwmSetDutyCycleNs(long pwm, long dutyCycle);

    /**
     * Set the period in seconds of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param period period in seconds of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_set_period")
    public static native int pwmSetPeriod(long pwm, double period);

    /**
     * Set the duty cycle as a ratio between 0.0 to 1.0 of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param dutyCycle duty cycle as a ratio between 0.0 to 1.0 of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_set_duty_cycle")
    public static native int pwmSetDutyCycle(long pwm, double dutyCycle);

    /**
     * Set the frequency in Hz of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param frequency Frequency in Hz of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_set_frequency")
    public static native int pwmSetFrequency(long pwm, double frequency);

    /**
     * Set the output polarity of the PWM.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @param polarity Output polarity of the PWM.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_set_polarity")
    public static native int pwmSetPolarity(long pwm, int polarity);

    /**
     * Return the chip number of the PWM handle.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @return Chip number of the PWM handle.
     */
    @JniMethod(accessor = "pwm_chip")
    public static native int pwmChip(long pwm);

    /**
     * Return the channel number of the PWM handle.
     *
     * @param pwm Valid pointer to an allocated LED handle structure.
     * @return Channel number of the PWM handle.
     */
    @JniMethod(accessor = "pwm_channel")
    public static native int pwmChannel(long pwm);

    /**
     * Return a string representation of the PWM handle.
     *
     * @param pwm Valid pointer to an allocated PWM handle structure.
     * @param str String representation of the PWM handle.
     * @param len Length of char array.
     * @return 0 on success, or a negative PWM error code on failure.
     */
    @JniMethod(accessor = "pwm_tostring")
    public static native int pwmToString(long pwm, byte[] str, long len);

    /**
     * Return a string representation of the PWM handle. Wraps native method and simplifies.
     *
     * @param pwm Valid pointer to an allocated PWM handle structure.
     * @return PWM handle as String.
     */
    public static String pwmToString(long pwm) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (pwmToString(pwm, str, str.length) < 0) {
            throw new RuntimeException(pwmErrMessage(pwm));
        }
        return jString(str);
    }

    /**
     * Return the libc errno of the last failure that occurred.
     *
     * @param pwm Valid pointer to an allocated PWM handle structure.
     * @return libc errno.
     */
    @JniMethod(accessor = "pwm_errno")
    public static native int pwmErrNo(long pwm);

    /**
     * Return a human readable error message pointer of the last failure that occurred.
     *
     * @param pwm Valid pointer to an allocated PWM handle structure.
     * @return Error message pointer.
     */
    @JniMethod(accessor = "pwm_errmsg")
    public static native long pwmErrMsg(long pwm);

    /**
     * Return a human readable error message of the last failure that occurred. Converts const char * returned by pwm_errmsg to a
     * Java String.
     *
     * @param pwm Valid pointer to an allocated PWM handle structure.
     * @return Error message.
     */
    public static String pwmErrMessage(long pwm) {
        var ptr = pwmErrMsg(pwm);
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        memMove(str, ptr, str.length);
        return jString(str);
    }

}
