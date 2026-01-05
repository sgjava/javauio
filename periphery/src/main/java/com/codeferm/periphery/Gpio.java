/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Common.MAX_CHAR_ARRAY_LEN;
import static com.codeferm.periphery.Common.free;
import static com.codeferm.periphery.Common.jString;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.fusesource.hawtjni.runtime.ClassFlag;
import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;
import static com.codeferm.periphery.Common.moveNativeToJava;
import static com.codeferm.periphery.Common.moveJavaToNative;

/**
 * c-periphery GPIO wrapper methods for Linux userspace character device gpio-cdev and sysfs GPIOs.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@JniClass
public class Gpio implements AutoCloseable {

    /**
     * gpioPoll returned event.
     */
    public static final int GPIO_POLL_EVENT = 1;
    /**
     * gpioPoll timeout.
     */
    public static final int GPIO_POLL_TIMEOUT = 0;
    /**
     * Function was successful.
     */
    public static final int GPIO_SUCCESS = 0;
    /**
     * periphery library.
     */
    private static final Library LIBRARY = new Library("periphery", Gpio.class);
    /**
     * GPIO handle.
     */
    @Setter(AccessLevel.NONE)
    final private long handle;
    /**
     * Gpio config struct.
     */
    @Setter(AccessLevel.NONE)
    final private GpioConfig config;

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
    public static int GPIO_ERROR_ARG;
    @JniField(flags = {CONSTANT})
    public static int GPIO_ERROR_OPEN;
    @JniField(flags = {CONSTANT})
    public static int GPIO_ERROR_NOT_FOUND;
    @JniField(flags = {CONSTANT})
    public static int GPIO_ERROR_QUERY;
    @JniField(flags = {CONSTANT})
    public static int GPIO_ERROR_CONFIGURE;
    @JniField(flags = {CONSTANT})
    public static int GPIO_ERROR_UNSUPPORTED;
    @JniField(flags = {CONSTANT})
    public static int GPIO_ERROR_INVALID_OPERATION;
    @JniField(flags = {CONSTANT})
    public static int GPIO_ERROR_IO;
    @JniField(flags = {CONSTANT})
    public static int GPIO_ERROR_CLOSE;
    /**
     * Direction constants.
     */
    @JniField(flags = {CONSTANT})
    public static int GPIO_DIR_IN;
    @JniField(flags = {CONSTANT})
    public static int GPIO_DIR_OUT;
    @JniField(flags = {CONSTANT})
    public static int GPIO_DIR_OUT_LOW;
    @JniField(flags = {CONSTANT})
    public static int GPIO_DIR_OUT_HIGH;
    /**
     * Edge constants.
     */
    @JniField(flags = {CONSTANT})
    public static int GPIO_EDGE_NONE;
    @JniField(flags = {CONSTANT})
    public static int GPIO_EDGE_RISING;
    @JniField(flags = {CONSTANT})
    public static int GPIO_EDGE_FALLING;
    @JniField(flags = {CONSTANT})
    public static int GPIO_EDGE_BOTH;
    /**
     * Bias constants.
     */
    @JniField(flags = {CONSTANT})
    public static int GPIO_BIAS_DEFAULT;
    @JniField(flags = {CONSTANT})
    public static int GPIO_BIAS_PULL_UP;
    @JniField(flags = {CONSTANT})
    public static int GPIO_BIAS_PULL_DOWN;
    @JniField(flags = {CONSTANT})
    public static int GPIO_BIAS_DISABLE;
    /**
     * Drive constants.
     */
    @JniField(flags = {CONSTANT})
    public static int GPIO_DRIVE_DEFAULT;
    @JniField(flags = {CONSTANT})
    public static int GPIO_DRIVE_OPEN_DRAIN;
    @JniField(flags = {CONSTANT})
    public static int GPIO_DRIVE_OPEN_SOURCE;

    /**
     * gpio_config struct as Java object.
     */
    @Data
    @Builder
    @Accessors(fluent = true)
    @JniClass(name = "gpio_config_t", flags = {ClassFlag.STRUCT, ClassFlag.TYPEDEF})
    public static class GpioConfig {

        static {
            LIBRARY.load();
            init();
        }

        @JniMethod(flags = {CONSTANT_INITIALIZER})
        private static native void init();
        @JniField(flags = {CONSTANT}, accessor = "sizeof(gpio_config_t)")
        @Getter
        @Setter
        private static int SIZEOF; // @Getter and @Setter required for static
        private int direction;
        private int edge;
        private int bias;
        private int drive;
        private boolean inverted;
        private long label;
    }

    /**
     * Open the character device GPIO with the specified GPIO line and direction at the specified character device GPIO chip path.
     *
     * @param path GPIO chip character device path.
     * @param line GPIO line number.
     * @param direction One of the direction values.
     */
    public Gpio(final String path, final int line, final int direction) {
        // Config not used
        config = null;
        // Allocate handle
        handle = gpioNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open line
        if (gpioOpen(handle, path, line, direction) != GPIO_SUCCESS) {
            // Free handle before throwing exception
            gpioFree(handle);
            throw new RuntimeException(gpioErrMessage(handle));
        }
    }

    /**
     * Open the character device GPIO with the specified GPIO name and direction at the specified character device GPIO chip path
     * (e.g. /dev/gpiochip0).
     *
     * @param path GPIO chip character device path.
     * @param name GPIO line name.
     * @param direction One of the direction values.
     */
    public Gpio(final String path, final String name, final int direction) {
        // Config not used
        config = null;
        // Allocate handle
        handle = gpioNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open line
        if (gpioOpenName(handle, path, name, direction) != GPIO_SUCCESS) {
            // Free handle before throwing exception
            gpioFree(handle);
            throw new RuntimeException(gpioErrMessage(handle));
        }
    }

    /**
     * Open the character device GPIO with the specified GPIO line and configuration at the specified character device GPIO chip
     * path (e.g. /dev/gpiochip0).
     *
     * @param path GPIO chip character device path.
     * @param line GPIO line number.
     * @param config Configuration struct.
     */
    public Gpio(final String path, final int line, final GpioConfig config) {
        this.config = config;
        // Allocate handle
        handle = gpioNew();
        if (handle == 0) {
            // Deallocate label before throwing exception
            if (config.label() != 0) {
                free(config.label());
            }
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open line
        if (gpioOpenAdvanced(handle, path, line, config) != GPIO_SUCCESS) {
            // Free handle before throwing exception
            gpioFree(handle);
            // Deallocate label before throwing exception
            if (config.label() != 0) {
                free(config.label());
            }
            throw new RuntimeException(gpioErrMessage(handle));
        }
    }

    /**
     * Open the character device GPIO with the specified GPIO name and configuration at the specified character device GPIO chip
     * path (e.g. /dev/gpiochip0).
     *
     * @param path GPIO chip character device path.
     * @param name GPIO line name.
     * @param config Configuration struct.
     */
    public Gpio(final String path, final String name, final GpioConfig config) {
        this.config = config;
        // Allocate handle
        handle = gpioNew();
        if (handle == 0) {
            // Deallocate label before throwing exception
            if (config.label() != 0) {
                free(config.label());
            }
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open line
        if (gpioOpenNameAdvanced(handle, path, name, config) != GPIO_SUCCESS) {
            // Free handle before throwing exception
            gpioFree(handle);
            // Deallocate label before throwing exception
            if (config.label() != 0) {
                free(config.label());
            }
            throw new RuntimeException(gpioErrMessage(handle));
        }
    }

    /**
     * Open the sysfs GPIO with the specified line and direction.
     *
     * @param line GPIO line number.
     * @param direction One of the direction values.
     */
    public Gpio(final int line, final int direction) {
        // Config not used
        config = null;
        // Allocate handle
        handle = gpioNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open line
        if (gpioOpenSysfs(handle, line, direction) != GPIO_SUCCESS) {
            // Free handle before throwing exception
            gpioFree(handle);
            throw new RuntimeException(gpioErrMessage(handle));
        }
    }

    /**
     * Close handle, free handle and free line label if allocated.
     */
    @Override
    public void close() {
        // Close handle
        gpioClose(handle);
        // Free handle
        gpioFree(handle);
        // Deallocate label
        if (config != null && config.label() != 0) {
            free(config.label());
        }
    }

    /**
     * Allocate a GPIO handle. Returns a valid handle on success, or NULL on failure.
     *
     * @return A valid handle on success, or NULL on failure.
     */
    @JniMethod(accessor = "gpio_new")
    public static final native long gpioNew();

    /**
     * Open the character device GPIO with the specified GPIO line and direction at the specified character device GPIO chip path.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param path GPIO chip character device path.
     * @param line GPIO line number.
     * @param direction One of the direction values.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_open")
    public static native int gpioOpen(long gpio, String path, int line, int direction);

    /**
     * Open the character device GPIO with the specified GPIO name and direction at the specified character device GPIO chip path
     * (e.g. /dev/gpiochip0).
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param path GPIO chip character device path.
     * @param name GPIO line name.
     * @param direction One of the direction values.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_open_name")
    public static native int gpioOpenName(long gpio, String path, String name, int direction);

    /**
     * Open the character device GPIO with the specified GPIO line and configuration at the specified character device GPIO chip
     * path (e.g. /dev/gpiochip0).
     *
     * gpio should be a valid pointer to an allocated GPIO handle structure. path is the GPIO chip character device path. line is
     * the GPIO line number. config should be a valid pointer to a gpio_config_t structure with valid values. label can be NULL for
     * a default consumer label.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param path GPIO chip character device path.
     * @param line GPIO line number.
     * @param config Configuration struct.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_open_advanced")
    public static native int gpioOpenAdvanced(long gpio, String path, int line, GpioConfig config);

    /**
     * Open the character device GPIO with the specified GPIO name and configuration at the specified character device GPIO chip
     * path (e.g. /dev/gpiochip0).
     *
     * gpio should be a valid pointer to an allocated GPIO handle structure. path is the GPIO chip character device path. name is
     * the GPIO line name. config should be a valid pointer to a gpio_config_t structure with valid values. label can be NULL for a
     * default consumer label.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param path GPIO chip character device path.
     * @param name GPIO line name.
     * @param config Configuration struct.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_open_name_advanced")
    public static native int gpioOpenNameAdvanced(long gpio, String path, String name, GpioConfig config);

    /**
     * Open the sysfs GPIO with the specified line and direction.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param line GPIO line number.
     * @param direction One of the direction values.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_open_sysfs")
    public static native int gpioOpenSysfs(long gpio, int line, int direction);

    /**
     * Read the state of the GPIO into value.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param value Pointer to an allocated bool.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_read")
    public static native int gpioRead(long gpio, boolean[] value);

    /**
     * Set the state of the GPIO to value.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param value True of false.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_write")
    public static native int gpioWrite(long gpio, boolean value);

    /**
     * Poll a GPIO for the edge event configured with gpio_set_edge(). For character device GPIOs, the edge event should be consumed
     * with gpio_read_event(). For sysfs GPIOs, the edge event should be consumed with gpio_read().
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param timeoutMs Positive number for a timeout in milliseconds, 0 for a non-blocking poll, or a negative number for a
     * blocking poll.
     * @return 1 on success (an edge event occurred), 0 on timeout, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_poll")
    public static native int gpioPoll(long gpio, int timeoutMs);

    /**
     * Close the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_close")
    public static native int gpioClose(long gpio);

    /**
     * Free a GPIO handle.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     */
    @JniMethod(accessor = "gpio_free")
    public static native void gpioFree(long gpio);

    /**
     * Read the edge event that occurred with the GPIO. This method is intended for use with character device GPIOs and is
     * unsupported by sysfs GPIOs.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param edge Pointer to an allocated int.
     * @param timestamp Pointer to an allocated long.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_read_event")
    public static native int gpioReadEvent(long gpio, int[] edge, long[] timestamp);

    /**
     * Poll multiple GPIOs for an edge event configured with gpio_set_edge(). For character device GPIOs, the edge event should be
     * consumed with gpio_read_event(). For sysfs GPIOs, the edge event should be consumed with gpio_read().
     *
     * @param gpios A valid pointer to a size count array of GPIO handles opened with one of the gpio_open*() functions.
     * @param count Number of gpio pointers.
     * @param timeoutMs Positive number for a timeout in milliseconds, 0 for a non-blocking poll, or a negative number for a
     * blocking poll.
     * @param gpiosReady is an optional pointer to a size count array of bool that will be populated with true for the corresponding
     * GPIO in the gpios array if an edge event occurred, or false if none occurred.
     * @return Number of GPIOs for which an edge event occurred, 0 on timeout, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_poll_multiple")
    public static native int gpioPollMultiple(long[] gpios, int count, int timeoutMs, boolean[] gpiosReady);

    /**
     * Get the configured direction of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param direction Pointer to an allocated int.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_get_direction")
    public static native int gpioGetDirection(long gpio, int[] direction);

    /**
     * Get the configured interrupt edge of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param edge Pointer to an allocated int.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_get_edge")
    public static native int gpioGetEdge(long gpio, int[] edge);

    /**
     * Get the configured line bias of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param bias
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_get_bias")
    public static native int gpioGetBias(long gpio, int[] bias);

    /**
     * Get the configured line drive of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param drive
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_get_drive")
    public static native int gpioGetDrive(long gpio, int[] drive);

    /**
     * Get the configured line inverted of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param inverted Active low.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_get_inverted")
    public static native int gpioGetInverted(long gpio, boolean[] inverted);

    /**
     * Set the direction of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param direction One of the direction values.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_set_direction")
    public static native int gpioSetDirection(long gpio, int direction);

    /**
     * Set the interrupt edge of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param edge One of the edge values.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_set_edge")
    public static native int gpioSetEdge(long gpio, int edge);

    /**
     * Set the bias of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param bias One of the bias values.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_set_bias")
    public static native int gpioSetBias(long gpio, int bias);

    /**
     * Set the drive of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param drive One of the drive values.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_set_drive")
    public static native int gpioSetDrive(long gpio, int drive);

    /**
     * Set the inverted value of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param inverted Is line inverted (active low)?
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_set_inverted")
    public static native int gpioSetInverted(long gpio, boolean inverted);

    /**
     * Return the line the GPIO handle was opened with.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return Line the GPIO handle was opened with.
     */
    @JniMethod(accessor = "gpio_line")
    public static native int gpioLine(long gpio);

    /**
     * Return the line file descriptor of the GPIO handle.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return Line file descriptor.
     */
    @JniMethod(accessor = "gpio_fd")
    public static native int gpioFd(long gpio);

    /**
     * Return the line name of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param str Line name.
     * @param len Length of char array.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_name")
    public static native int gpioName(long gpio, byte[] str, long len);

    /**
     * Return the line name of the GPIO. Wraps native method and simplifies.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return Line name.
     */
    public static String gpioName(long gpio) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (gpioName(gpio, str, str.length) < 0) {
            throw new RuntimeException(gpioErrMessage(gpio));
        }
        return jString(str);
    }

    /**
     * Return the line label of the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param str Line label.
     * @param len Length of char array.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_label")
    public static native int gpioLabel(long gpio, byte[] str, long len);

    /**
     * Return the line lable of the GPIO. Wraps native method and simplifies.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return Line label.
     */
    public static String gpioLabel(long gpio) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (gpioLabel(gpio, str, str.length) < 0) {
            throw new RuntimeException(gpioErrMessage(gpio));
        }
        return jString(str);
    }

    /**
     * Return the GPIO chip file descriptor of the GPIO handle.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return GPIO chip file descriptor.
     */
    @JniMethod(accessor = "gpio_chip_fd")
    public static native int gpioChipFd(long gpio);

    /**
     * Return the name of the GPIO chip associated with the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param str Name of the GPIO chip.
     * @param len Length of char array.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_chip_name")
    public static native int gpioChipName(long gpio, byte[] str, long len);

    /**
     * Return the name of the GPIO chip associated with the GPIO. Wraps native method and simplifies.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return
     */
    public static String gpioChipName(long gpio) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (gpioChipName(gpio, str, str.length) < 0) {
            throw new RuntimeException(gpioErrMessage(gpio));
        }
        return jString(str);
    }

    /**
     * Return the label of the GPIO chip associated with the GPIO.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param str Label of the GPIO chip.
     * @param len Length of char array.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_chip_label")
    public static native int gpioChipLabel(long gpio, byte[] str, long len);

    /**
     * Return the label of the GPIO chip associated with the GPIO. Wraps native method and simplifies.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return GPIO label.
     */
    public static String gpioChipLabel(long gpio) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (gpioChipLabel(gpio, str, str.length) < 0) {
            throw new RuntimeException(gpioErrMessage(gpio));
        }
        return jString(str);
    }

    /**
     *
     * Return a string representation of the GPIO handle.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @param str String representation of the GPIO handle.
     * @param len Length of char array.
     * @return 0 on success, or a negative GPIO error code on failure.
     */
    @JniMethod(accessor = "gpio_tostring")
    public static native int gpioToString(long gpio, byte[] str, long len);

    /**
     * Return a string representation of the GPIO handle. Wraps native method and simplifies.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return GPIO handle as String.
     */
    public static String gpioToString(long gpio) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (gpioToString(gpio, str, str.length) < 0) {
            throw new RuntimeException(gpioErrMessage(gpio));
        }
        return jString(str);
    }

    /**
     * Return the libc errno of the last failure that occurred.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return libc errno.
     */
    @JniMethod(accessor = "gpio_errno")
    public static native int gpioErrNo(long gpio);

    /**
     * Return a human readable error message pointer of the last failure that occurred.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return Error message pointer.
     */
    @JniMethod(accessor = "gpio_errmsg")
    public static native long gpioErrMsg(long gpio);

    /**
     * Return a human readable error message of the last failure that occurred. Converts const char * returned by gpio_errmsg to a
     * Java String.
     *
     * @param gpio Valid pointer to an allocated GPIO handle structure.
     * @return Error message.
     */
    public static String gpioErrMessage(long gpio) {
        var ptr = gpioErrMsg(gpio);
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        moveNativeToJava(str, ptr, str.length);
        return jString(str);
    }
}
