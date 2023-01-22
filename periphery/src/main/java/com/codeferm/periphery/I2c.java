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
import org.fusesource.hawtjni.runtime.ClassFlag;
import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;

/**
 * c-periphery I2C wrapper functions for Linux userspace i2c-dev devices.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@JniClass
public class I2c implements AutoCloseable {

    /**
     * Function was successful.
     */
    public static final int I2C_SUCCESS = 0;
    /**
     * I2cMsg flags.
     */
    public static final short I2C_M_TEN = 0x0010;
    public static final short I2C_M_RD = 0x0001;
    public static final short I2C_M_STOP = (short) 0x8000;
    public static final short I2C_M_NOSTART = 0x4000;
    public static final short I2C_M_REV_DIR_ADDR = 0x2000;
    public static final short I2C_M_IGNORE_NAK = 0x1000;
    public static final short I2C_M_NO_RD_ACK = 0x0800;
    public static final short I2C_M_RECV_LEN = 0x0400;
    /**
     * periphery library.
     */
    private static final Library LIBRARY = new Library("periphery", I2c.class);
    /**
     * I2C handle.
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
    public static int I2C_ERROR_ARG;
    @JniField(flags = {CONSTANT})
    public static int I2C_ERROR_OPEN;
    @JniField(flags = {CONSTANT})
    public static int I2C_ERROR_QUERY;
    @JniField(flags = {CONSTANT})
    public static int I2C_ERROR_NOT_SUPPORTED;
    @JniField(flags = {CONSTANT})
    public static int I2C_ERROR_TRANSFER;
    @JniField(flags = {CONSTANT})
    public static int I2C_ERROR_CLOSE;

    /**
     * i2c_msg struct as Java object.
     */
    @JniClass(name = "i2c_msg", flags = {ClassFlag.STRUCT})
    public static class I2cMsg {

        static {
            LIBRARY.load();
            init();
        }

        @JniMethod(flags = {CONSTANT_INITIALIZER})
        private static native void init();
        @JniField(flags = {CONSTANT}, accessor = "sizeof(struct i2c_msg)")
        public static int SIZEOF;
        public short addr;
        public short flags;
        public short len;
        public long buf;
    }

    /**
     * Open the i2c-dev device at the specified path (e.g. "/dev/i2c-1").
     *
     * @param path Device path.
     */
    public I2c(final String path) {
        // Allocate handle
        handle = i2cNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open device
        if (i2cOpen(handle, path) != I2C_SUCCESS) {
            // Free handle before throwing exception
            i2cFree(handle);
            throw new RuntimeException(i2cErrMessage(handle));
        }
    }

    /**
     * Close and free handle.
     */
    @Override
    public void close() {
        i2cClose(handle);
        i2cFree(handle);
    }

    /**
     * Read array from i2c 8 bit address. Unlike i2cReadReg8 the bytes values are not "&amp; 0xff", thus the caller will need to do
     * this.
     *
     * In order to read a register, we first do a "dummy write" by writing 0 bytes to the register we want to read from. This is
     * similar to writing to a register except it's 1 byte rather than 2.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param addr Address.
     * @param reg Register.
     * @param buf Read buffer.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    public static int i2cReadReg8(final long i2c, final short addr, final short reg, final byte[] buf) {
        return i2cRead8(i2c, addr, reg, buf, buf.length);
    }

    /**
     * Read array from i2c 8 bit address.
     *
     * In order to read a register, we first do a "dummy write" by writing 0 bytes to the register we want to read from. This is
     * similar to writing to a register except it's 1 byte rather than 2.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param addr Address.
     * @param reg Register to return.
     * @param regVal Read buffer.
     * @return 0 on success, or a negative I2C error code on failure.
     *
     */
    public static int i2cReadReg8(final long i2c, final short addr, final short reg, final short regVal[]) {
        final var buf = new byte[1];
        final var error = i2cReadReg8(i2c, addr, reg, buf);
        regVal[0] = (short) (buf[0] & 0xff);
        return error;
    }

    /**
     * Read two consecutive i2c 8 bit addresses and combine them.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param addr Address.
     * @param reg Register.
     * @param regVal Read buffer.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    public static int i2cReadWord8(final long i2c, final short addr, final short reg, final int regVal[]) {
        final var highBuf = new short[1];
        var error = i2cReadReg8(i2c, addr, reg, highBuf);
        if (error == I2C_SUCCESS) {
            final var lowBuf = new short[1];
            // Increment register for next read
            error = i2cReadReg8(i2c, addr, (short) (reg + 1), lowBuf);
            final int value = (highBuf[0] << 8) + lowBuf[0];
            if (value >= 0x8000) {
                regVal[0] = -((65535 - value) + 1);
            } else {
                regVal[0] = value;
            }
        }
        return error;
    }

    /**
     * Write value to i2c 8 bit address.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param addr Address.
     * @param reg Register.
     * @param value Value to write.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    public static int i2cWriteReg8(final long i2c, final short addr, final short reg, final short value) {
        return i2cWrite8(i2c, addr, reg, value);
    }

    
    /**
     * Write value to i2c 16 bit address.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param addr Address.
     * @param reg Register.
     * @param value Value to write.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    public static int i2cWriteReg16(final long i2c, final short addr, final short reg, final short value) {
        return i2cWrite16(i2c, addr, reg, value);
    }
    
    /**
     * Allocate an I2C handle.
     *
     * @return A valid handle on success, or NULL on failure.
     */
    @JniMethod(accessor = "i2c_new")
    public static final native long i2cNew();

    /**
     * Open the i2c-dev device at the specified path (e.g. "/dev/i2c-1").
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param path Device path.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    @JniMethod(accessor = "i2c_open")
    public static native int i2cOpen(long i2c, String path);

    /**
     * Helper function to read from 8 bit address.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param addr Address.
     * @param reg Register.
     * @param buf Buffer.
     * @param len Buffer length.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    @JniMethod(accessor = "i2c_read8")
    public static native int i2cRead8(long i2c, short addr, short reg, byte[] buf, long len);

    /**
     * Helper function to read from 16 bit address.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param addr Address.
     * @param reg Register.
     * @param buf Buffer.
     * @param len Buffer length.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    @JniMethod(accessor = "i2c_read16")
    public static native int i2cRead16(long i2c, short addr, short reg, byte[] buf, long len);

    /**
     * Helper function to write byte to 8 bit address.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param addr Address.
     * @param reg Register.
     * @param value Value to write.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    @JniMethod(accessor = "i2c_write8")
    public static native int i2cWrite8(long i2c, short addr, short reg, short value);

    /**
     * Helper function to write byte to 16 bit address.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param addr Address.
     * @param reg Register.
     * @param value Value to write.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    @JniMethod(accessor = "i2c_write16")
    public static native int i2cWrite16(long i2c, short addr, short reg, short value);
    
    /**
     * Transfer count number of struct i2c_msg I2C messages.
     *
     * i2c should be a valid pointer to an I2C handle opened with i2c_open(). msgs should be a pointer to an array of struct i2c_msg
     * (defined in linux/i2c.h).
     *
     * Each I2C message structure (see above) specifies the transfer of a consecutive number of bytes to a slave address. The slave
     * address, message flags, buffer length, and pointer to a byte buffer should be specified in each message. The message flags
     * specify whether the message is a read (I2C_M_RD) or write (0) transaction, as well as additional options selected by the
     * bitwise OR of their bitmasks.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param msgs A pointer to an array of I2cMsg.
     * @param count Number of messages to transfer.
     * @return 0 on success, or a negative I2C error code on failure.
     *
     */
    @JniMethod(accessor = "i2c_transfer")
    public static native int i2cTransfer(long i2c, I2cMsg msgs, long count);

    /**
     * Close the I2C.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    @JniMethod(accessor = "i2c_close")
    public static native int i2cClose(long i2c);

    /**
     * Free an I2C handle.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     */
    @JniMethod(accessor = "i2c_free")
    public static native void i2cFree(long i2c);

    /**
     * Return a string representation of the I2C handle.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @param str String representation of the I2C handle.
     * @param len Length of char array.
     * @return 0 on success, or a negative I2C error code on failure.
     */
    @JniMethod(accessor = "i2c_tostring")
    public static native int i2cToString(long i2c, byte[] str, long len);

    /**
     * Return a string representation of the I2C handle. Wraps native method and simplifies.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @return I2C handle as String.
     */
    public static String i2cToString(long i2c) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (i2cToString(i2c, str, str.length) < 0) {
            throw new RuntimeException(i2cErrMessage(i2c));
        }
        return jString(str);
    }

    /**
     * Return the libc errno of the last failure that occurred.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @return libc errno.
     */
    @JniMethod(accessor = "i2c_errno")
    public static native int i2cErrNo(long i2c);

    /**
     * Return a human readable error message pointer of the last failure that occurred.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @return Error message pointer.
     */
    @JniMethod(accessor = "i2c_errmsg")
    public static native long i2cErrMsg(long i2c);

    /**
     * Return a human readable error message of the last failure that occurred. Converts const char * returned by i2c_errmsg to a
     * Java String.
     *
     * @param i2c Valid pointer to an allocated I2C handle structure.
     * @return Error message.
     */
    public static String i2cErrMessage(long i2c) {
        var ptr = i2cErrMsg(i2c);
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        memMove(str, ptr, str.length);
        return jString(str);
    }
}
