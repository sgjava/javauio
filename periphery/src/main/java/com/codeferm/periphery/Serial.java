/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Common.MAX_CHAR_ARRAY_LEN;
import static com.codeferm.periphery.Common.jString;
import static com.codeferm.periphery.Common.memMove;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;

/**
 * c-periphery Serial wrapper functions for Linux userspace termios tty devices.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@JniClass
public class Serial implements AutoCloseable {

    /**
     * Function was successful.
     */
    public static final int SERIAL_SUCCESS = 0;
    /**
     * periphery library.
     */
    private static final Library LIBRARY = new Library("periphery", Serial.class);
    /**
     * Serial handle.
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
    public static int SERIAL_ERROR_ARG;
    @JniField(flags = {CONSTANT})
    public static int SERIAL_ERROR_OPEN;
    @JniField(flags = {CONSTANT})
    public static int SERIAL_ERROR_QUERY;
    @JniField(flags = {CONSTANT})
    public static int SERIAL_ERROR_CONFIGURE;
    @JniField(flags = {CONSTANT})
    public static int SERIAL_ERROR_IO;
    @JniField(flags = {CONSTANT})
    public static int SERIAL_ERROR_CLOSE;
    /**
     * Parity constants.
     */
    @JniField(flags = {CONSTANT})
    public static int PARITY_NONE;
    @JniField(flags = {CONSTANT})
    public static int PARITY_ODD;
    @JniField(flags = {CONSTANT})
    public static int PARITY_EVEN;

    /**
     * Open the tty device at the specified path (e.g. "/dev/ttyUSB0"), with the specified baudrate, and the defaults of 8 data
     * bits, no parity, 1 stop bit, software flow control (xonxoff) off, hardware flow control (rtscts) off.
     *
     * @param path Serial device path.
     * @param baudrate Baud rate.
     */
    public Serial(final String path, final int baudrate) {
        // Allocate handle
        handle = serialNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open device
        if (serialOpen(handle, path, baudrate) != SERIAL_SUCCESS) {
            // Free handle before throwing exception
            serialFree(handle);
            throw new RuntimeException(serialErrMessage(handle));
        }
    }

    /**
     * Close and free handle.
     */
    @Override
    public void close() {
        serialClose(handle);
        serialFree(handle);
    }
   
    /**
     * Allocate a Serial handle. Returns a valid handle on success, or NULL on failure.
     *
     * @return A valid handle on success, or NULL on failure.
     */
    @JniMethod(accessor = "serial_new")
    public static final native long serialNew();

    /**
     * Open the tty device at the specified path (e.g. "/dev/ttyUSB0"), with the specified baudrate, and the defaults of 8 data
     * bits, no parity, 1 stop bit, software flow control (xonxoff) off, hardware flow control (rtscts) off.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param path Serial device path.
     * @param baudrate Baud rate.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_open")
    public static native int serialOpen(long serial, String path, int baudrate);

    /**
     * Open the tty device at the specified path (e.g. "/dev/ttyUSB0"), with the specified baudrate, data bits, parity, stop bits,
     * software flow control (xonxoff), and hardware flow control (rtscts) settings.
     *
     * serial should be a valid pointer to an allocated Serial handle structure. databits can be 5, 6, 7, or 8. parity can be
     * PARITY_NONE, PARITY_ODD, or PARITY_EVEN as defined above. stopbits can be 1 or 2.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param path Serial device path.
     * @param baudrate Baud rate.
     * @param databits Data bits.
     * @param parity Parity.
     * @param stopbits Stop bits.
     * @param xonXoff Software flow control.
     * @param rtsCts Hardware flow control.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_open_advanced")
    public static native int serialOpenAdvanced(long serial, String path, int baudrate, int databits, int parity, int stopbits,
            boolean xonXoff, boolean rtsCts);

    /**
     * Read up to len number of bytes from the serial port into the buf buffer with the specified millisecond timeout. A 0 timeout
     * can be specified for a non-blocking read. A negative timeout can be specified for a blocking read that will read until all of
     * the requested number of bytes are read. A positive timeout in milliseconds can be specified for a blocking read with timeout.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param buf Read buffer.
     * @param len Amount of data to read.
     * @param timeoutMs can be positive for a timeout in milliseconds, 0 for a non-blocking read, or a negative number for a
     * blocking read.
     * @return number of bytes read on success, 0 on timeout, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_read")
    public static native int serialRead(long serial, byte[] buf, int len, int timeoutMs);

    /**
     * Write len number of bytes from the buf buffer to the serial port.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param buf Write buffer.
     * @param len Amount of data to write.
     * @return Number of bytes written on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_write")
    public static native int serialWrite(long serial, byte[] buf, int len);

    /**
     * Flush the write buffer of the serial port (i.e. force its write immediately).
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_flush")
    public static native int serialFlush(long serial);

    /**
     * Get the number of bytes waiting to be read from the serial port.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param count Byte count.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_input_waiting")
    public static native int serialInputWaiting(long serial, int[] count);

    /**
     * Get the number of bytes waiting to be written to the serial port.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param count Byte count.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_output_waiting")
    public static native int serialOutputWaiting(long serial, int[] count);

    /**
     * Poll for data available for reading from the serial port.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param timeoutMs can be positive for a timeout in milliseconds, 0 for a non-blocking poll, or a negative number for a
     * blocking poll.
     * @return 1 on success (data available for reading), 0 on timeout, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_poll")
    public static native int serialPoll(long serial, int timeoutMs);

    /**
     * Close the tty device.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_close")
    public static native int serialClose(long serial);

    /**
     * Free a GPIO handle.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     */
    @JniMethod(accessor = "serial_free")
    public static native void serialFree(long serial);

    /**
     * Get baud rate.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param baudRate Baud rate.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_get_baudrate")
    public static native int serialGetBaudRate(long serial, int[] baudRate);

    /**
     * Get data bits.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param dataBits Data bits.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_get_databits")
    public static native int serialGetDataBits(long serial, int[] dataBits);

    /**
     * Get parity.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param parity Parity.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_get_parity")
    public static native int serialGetParity(long serial, int[] parity);

    /**
     * Get stop bits.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param stopBits Stop bits.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_get_stopbits")
    public static native int serialGetStopBits(long serial, int[] stopBits);

    /**
     * Get Xon/Xoff.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param xonXoff Xon/Xoff.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_get_stopbits")
    public static native int serialGetXonXoff(long serial, boolean[] xonXoff);

    /**
     * Get RTS/CTS.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param rtsCts RTS/CTS.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_get_rtscts")
    public static native int serialGetRtsCts(long serial, boolean[] rtsCts);

    /**
     * Set baud rate.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param baudRate Baud rate.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_set_baudrate")
    public static native int serialSetBaudRate(long serial, int baudRate);

    /**
     * Set data bits.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param dataBits Data bits.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_set_databits")
    public static native int serialSetDataBits(long serial, int dataBits);

    /**
     * Set parity.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param parity Parity.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_set_parity")
    public static native int serialSetParity(long serial, int parity);

    /**
     * Set stop bits.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param stopBits Stop bits.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_set_stopbits")
    public static native int serialSetStopBits(long serial, int stopBits);

    /**
     * Set Xon/Xoff.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param xonXoff Xon/Xoff.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_set_stopbits")
    public static native int serialSetXonXoff(long serial, boolean xonXoff);

    /**
     * Set RTS/CTS.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @param rtsCts RTS/CTS.
     * @return 0 on success, or a negative Serial error code on failure.
     */
    @JniMethod(accessor = "serial_set_rtscts")
    public static native int serialSetRtsCts(long serial, boolean rtsCts);

    /**
     * Return the file descriptor (for the underlying tty device) of the Serial handle.
     *
     * @param serial Valid pointer to an allocated Serial handle structure.
     * @return File descriptor (for the underlying tty device) of the Serial handle.
     */
    @JniMethod(accessor = "serial_fd")
    public static native int serialFd(long serial);

    /**
     * Return a string representation of the serial handle.
     *
     * @param serial Valid pointer to an allocated serial handle structure.
     * @param str String representation of the serial handle.
     * @param len Length of char array.
     * @return 0 on success, or a negative serial error code on failure.
     */
    @JniMethod(accessor = "serial_tostring")
    public static native int serialToString(long serial, byte[] str, long len);

    /**
     * Return a string representation of the serial handle. Wraps native method and simplifies.
     *
     * @param serial Valid pointer to an allocated serial handle structure.
     * @return Serial handle as String.
     */
    public static String serialToString(long serial) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (serialToString(serial, str, str.length) < 0) {
            throw new RuntimeException(serialErrMessage(serial));
        }
        return jString(str);
    }

    /**
     * Return the libc errno of the last failure that occurred.
     *
     * @param serial Valid pointer to an allocated serial handle structure.
     * @return libc errno.
     */
    @JniMethod(accessor = "serial_errno")
    public static native int serialErrNo(long serial);

    /**
     * Return a human readable error message pointer of the last failure that occurred.
     *
     * @param serial Valid pointer to an allocated serial handle structure.
     * @return Error message pointer.
     */
    @JniMethod(accessor = "serial_errmsg")
    public static native long serialErrMsg(long serial);

    /**
     * Return a human readable error message of the last failure that occurred. Converts const char * returned by serial_errmsg to a
     * Java String.
     *
     * @param serial Valid pointer to an allocated serial handle structure.
     * @return Error message.
     */
    public static String serialErrMessage(long serial) {
        var ptr = serialErrMsg(serial);
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        memMove(str, ptr, str.length);
        return jString(str);
    }
}
