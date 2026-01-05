/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Common.MAX_CHAR_ARRAY_LEN;
import static com.codeferm.periphery.Common.jString;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;
import static com.codeferm.periphery.Common.moveNativeToJava;

/**
 * c-periphery LED wrapper functions for Linux userspace sysfs LEDs.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@JniClass
public class Led implements AutoCloseable {

    /**
     * Function was successful.
     */
    public static final int LED_SUCCESS = 0;
    /**
     * periphery library.
     */
    private static final Library LIBRARY = new Library("periphery", Led.class);
    /**
     * LED handle.
     */
    @Setter(AccessLevel.NONE)
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
    public static int LED_ERROR_ARG;
    @JniField(flags = {CONSTANT})
    public static int LED_ERROR_OPEN;
    @JniField(flags = {CONSTANT})
    public static int LED_ERROR_QUERY;
    @JniField(flags = {CONSTANT})
    public static int LED_ERROR_IO;
    @JniField(flags = {CONSTANT})
    public static int LED_ERROR_CLOSE;

    /**
     * Open the sysfs LED with the specified name.
     *
     * @param name Led name.
     */
    public Led(final String name) {
        // Allocate handle
        handle = ledNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open device
        if (ledOpen(handle, name) != LED_SUCCESS) {
            // Free handle before throwing exception
            ledFree(handle);
            throw new RuntimeException(ledErrMessage(handle));
        }
    }

    /**
     * Close and free handle.
     */
    @Override
    public void close() {
        ledClose(handle);
        ledFree(handle);
    }
    
    /**
     * Allocate an LED handle.
     *
     * @return A valid handle on success, or NULL on failure.
     */
    @JniMethod(accessor = "led_new")
    public static final native long ledNew();

    /**
     * Open the sysfs LED with the specified name.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @param name Led name.
     * @return 0 on success, or a negative LED error code on failure.
     */
    @JniMethod(accessor = "led_open")
    public static native int ledOpen(long led, String name);

    /**
     * Read the state of the LED into value, where true is non-zero brightness, and false is zero brightness.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @param value Should be a pointer to an allocated bool.
     * @return 0 on success, or a negative LED error code on failure.
     */
    @JniMethod(accessor = "led_read")
    public static native int ledRead(long led, boolean[] value);

    /**
     * Write the state of the LED to value, where true is max brightness, and false is zero brightness.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @param value True is max brightness, and false is zero brightness.
     * @return 0 on success, or a negative LED error code on failure.
     */
    @JniMethod(accessor = "led_write")
    public static native int ledWrite(long led, boolean value);

    /**
     * Close the LED.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @return 0 on success, or a negative LED error code on failure.
     */
    @JniMethod(accessor = "led_close")
    public static native int ledClose(long led);

    /**
     * Free an LED handle.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     */
    @JniMethod(accessor = "led_free")
    public static native void ledFree(long led);

    /**
     * Get the brightness of the LED.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @param brightness Brightness value.
     * @return 0 on success, or a negative LED error code on failure.
     */
    @JniMethod(accessor = "led_get_brightness")
    public static native int ledGetBrightness(long led, int[] brightness);

    /**
     * Get the max brightness of the LED.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @param maxBrightness Max brightness to set.
     * @return 0 on success, or a negative LED error code on failure.
     */
    @JniMethod(accessor = "led_get_max_brightness")
    public static native int ledGetMaxBrightness(long led, int[] maxBrightness);

    /**
     * Set the brightness of the LED.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @param brightness Brightness to set.
     * @return 0 on success, or a negative LED error code on failure.
     */
    @JniMethod(accessor = "led_set_brightness")
    public static native int ledSetBrightness(long led, int brightness);

    /**
     * Return the name of the sysfs LED.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @param str LED name.
     * @param len Length of char array.
     * @return 0 on success, or a negative LED error code on failure.
     */
    @JniMethod(accessor = "led_name")
    public static native int ledName(long led, byte[] str, long len);

    /**
     * Return the name of the sysfs LED. Wraps native method and simplifies.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @return
     */
    public static String ledName(long led) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (ledName(led, str, str.length) < 0) {
            throw new RuntimeException(ledErrMessage(led));
        }
        return jString(str);
    }

    /**
     * Return a string representation of the LED handle.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @param str String representation of the LED handle.
     * @param len Length of char array.
     * @return 0 on success, or a negative LED error code on failure.
     */
    @JniMethod(accessor = "led_tostring")
    public static native int ledToString(long led, byte[] str, long len);

    /**
     * Return a string representation of the LED handle. Wraps native method and simplifies.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @return LED handle as String.
     */
    public static String ledToString(long led) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (ledToString(led, str, str.length) < 0) {
            throw new RuntimeException(ledErrMessage(led));
        }
        return jString(str);
    }

    /**
     * Return the libc errno of the last failure that occurred.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @return libc errno.
     */
    @JniMethod(accessor = "led_errno")
    public static native int ledErrNo(long led);

    /**
     * Return a human readable error message pointer of the last failure that occurred.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @return Error message pointer.
     */
    @JniMethod(accessor = "led_errmsg")
    public static native long ledErrMsg(long led);

    /**
     * Return a human readable error message of the last failure that occurred. Converts const char * returned by led_errmsg to a
     * Java String.
     *
     * @param led Valid pointer to an allocated LED handle structure.
     * @return Error message.
     */
    public static String ledErrMessage(long led) {
        var ptr = ledErrMsg(led);
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        moveNativeToJava(str, ptr, str.length);
        return jString(str);
    }
}
