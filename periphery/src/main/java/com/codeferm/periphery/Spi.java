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
 * c-periphery SPI wrapper functions for Linux userspace spidev devices.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@JniClass
public class Spi implements AutoCloseable {

    /**
     * Function was successful.
     */
    public static final int SPI_SUCCESS = 0;
    /**
     * periphery library.
     */
    private static final Library LIBRARY = new Library("periphery", Spi.class);
    /**
     * SPI handle.
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
    public static int SPI_ERROR_ARG;
    @JniField(flags = {CONSTANT})
    public static int SPI_ERROR_OPEN;
    @JniField(flags = {CONSTANT})
    public static int SPI_ERROR_QUERY;
    @JniField(flags = {CONSTANT})
    public static int SPI_ERROR_CONFIGURE;
    @JniField(flags = {CONSTANT})
    public static int SPI_ERROR_TRANSFER;
    @JniField(flags = {CONSTANT})
    public static int SPI_ERROR_CLOSE;
    /**
     * Bit order constants.
     */
    @JniField(flags = {CONSTANT})
    public static int MSB_FIRST;
    @JniField(flags = {CONSTANT})
    public static int LSB_FIRST;

    /**
     * Open the spidev device at the specified path (e.g. "/dev/spidev1.0"), with the specified SPI mode, specified max speed in
     * hertz, and the defaults of MSB_FIRST bit order, and 8 bits per word.
     *
     * @param path spidev path.
     * @param mode Mode can be 0, 1, 2, or 3.
     * @param maxSpeed Max speed in hertz.
     */
    public Spi(final String path, final int mode, final int maxSpeed) {
        // Allocate handle
        handle = spiNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open device
        if (spiOpen(handle, path, mode, maxSpeed) != SPI_SUCCESS) {
            // Free handle before throwing exception
            spiFree(handle);
            throw new RuntimeException(spiErrMessage(handle));
        }
    }

    /**
     * Open the spidev device at the specified path, with the specified SPI mode, max speed in hertz, bit order, bits per word, and
     * extra flags.
     *
     * SPI mode can be 0, 1, 2, or 3. Bit order can be MSB_FIRST or LSB_FIRST, as defined above. Bits per word specifies the
     * transfer word size. Extra flags specified additional flags bitwise-ORed with the SPI mode.
     *
     * @param path spidev path.
     * @param mode Mode can be 0, 1, 2, or 3.
     * @param maxSpeed Max speed in hertz.
     * @param bitOrder Bit order can be MSB_FIRST or LSB_FIRST.
     * @param bitsPerWord Transfer word size.
     * @param extraFlags Additional flags bitwise-ORed with the SPI mode.
     */
    public Spi(final String path, final int mode, final int maxSpeed, final int bitOrder, final byte bitsPerWord,
            final byte extraFlags) {
        // Allocate handle
        handle = spiNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open device
        if (spiOpenAdvanced(handle, path, mode, maxSpeed, bitOrder, bitsPerWord, extraFlags) != SPI_SUCCESS) {
            // Free handle before throwing exception
            spiFree(handle);
            throw new RuntimeException(spiErrMessage(handle));
        }
    }

    /**
     * Close and free handle.
     */
    @Override
    public void close() {
        spiClose(handle);
        spiFree(handle);
    }

    /**
     * Allocate an SPI handle.
     *
     * @return A valid handle on success, or NULL on failure.
     */
    @JniMethod(accessor = "spi_new")
    public static final native long spiNew();

    /**
     * Open the spidev device at the specified path (e.g. "/dev/spidev1.0"), with the specified SPI mode, specified max speed in
     * hertz, and the defaults of MSB_FIRST bit order, and 8 bits per word.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param path spidev path.
     * @param mode Mode can be 0, 1, 2, or 3.
     * @param maxSpeed Max speed in hertz.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_open")
    public static native int spiOpen(long spi, String path, int mode, int maxSpeed);

    /**
     * Open the spidev device at the specified path, with the specified SPI mode, max speed in hertz, bit order, bits per word, and
     * extra flags.
     *
     * spi should be a valid pointer to an allocated SPI handle structure. SPI mode can be 0, 1, 2, or 3. Bit order can be MSB_FIRST
     * or LSB_FIRST, as defined above. Bits per word specifies the transfer word size. Extra flags specified additional flags
     * bitwise-ORed with the SPI mode.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param path spidev path.
     * @param mode Mode can be 0, 1, 2, or 3.
     * @param maxSpeed Max speed in hertz.
     * @param bitOrder Bit order can be MSB_FIRST or LSB_FIRST.
     * @param bitsPerWord Transfer word size.
     * @param extraFlags Additional flags bitwise-ORed with the SPI mode.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_open_advanced")
    public static native int spiOpenAdvanced(long spi, String path, int mode, int maxSpeed, int bitOrder, byte bitsPerWord,
            byte extraFlags);

    /**
     * Open the spidev device at the specified path, with the specified SPI mode, max speed in hertz, bit order, bits per word, and
     * extra flags. This open function is the same as spi_open_advanced(), except that extra_flags can be 32-bits.
     *
     * spi should be a valid pointer to an allocated SPI handle structure. SPI mode can be 0, 1, 2, or 3. Bit order can be MSB_FIRST
     * or LSB_FIRST, as defined above. Bits per word specifies the transfer word size. Extra flags specified additional flags
     * bitwise-ORed with the SPI mode.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param path spidev path.
     * @param mode Mode can be 0, 1, 2, or 3.
     * @param maxSpeed Max speed in hertz.
     * @param bitOrder Bit order can be MSB_FIRST or LSB_FIRST.
     * @param bitsPerWord Transfer word size.
     * @param extraFlags Additional flags bitwise-ORed with the SPI mode.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_open_advanced2")
    public static native int spiOpenAdvanced2(long spi, String path, int mode, int maxSpeed, int bitOrder, byte bitsPerWord,
            int extraFlags);

    /**
     * Shift out len word counts of the txbuf buffer, while shifting in len word counts to the rxbuf buffer.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param txBuf Transmit buffer.
     * @param rxBuf Receive buffer.
     * @param len Word count.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_transfer")
    public static native int spiTransfer(long spi, byte[] txBuf, byte[] rxBuf, long len);

    /**
     * Close the spidev device.
     *
     * @param spi Valid pointer to an allocated SPI handle structure.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_close")
    public static native int spiClose(long spi);

    /**
     * Free an SPI handle.
     *
     * @param spi Valid pointer to an allocated SPI handle structure.
     */
    @JniMethod(accessor = "spi_free")
    public static native void spiFree(long spi);

    /**
     * Get the mode.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param mode Mode can be 0, 1, 2, or 3.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_get_mode")
    public static native int spiGetMode(long spi, int[] mode);

    /**
     * Get the max speed.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param maxSpeed Max speed in hertz.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_get_max_speed")
    public static native int spiGetMaxSpeed(long spi, int[] maxSpeed);

    /**
     * Get the bit order.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param bit_order Bit order can be MSB_FIRST or LSB_FIRST.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_get_bit_order")
    public static native int spiGetBitOrder(long spi, int[] bit_order);

    /**
     * Get the bits per word.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param bitsPerWord Transfer word size.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_get_bits_per_word")
    public static native int spiGetBitsPerWord(long spi, byte[] bitsPerWord);

    /**
     * Get extra flags.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param extraFlags Additional flags bitwise-ORed with the SPI mode.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_get_extra_flags")
    public static native int spiGetExtraFlags(long spi, byte[] extraFlags);

    /**
     * Get extra flags.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param extraFlags Additional flags bitwise-ORed with the SPI mode.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_get_extra_flags32")
    public static native int spiGetExtraFlags32(long spi, int[] extraFlags);

    /**
     * Set the mode.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param mode Mode can be 0, 1, 2, or 3.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_set_mode")
    public static native int spiSetMode(long spi, int mode);

    /**
     * Set the max speed.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param maxSpeed Max speed in hertz.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_set_max_speed")
    public static native int spiSetMaxSpeed(long spi, int maxSpeed);

    /**
     * Set the bit order.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param bit_order Bit order can be MSB_FIRST or LSB_FIRST.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_set_bit_order")
    public static native int spiSetBitOrder(long spi, int bit_order);

    /**
     * Set the bits per word.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param bitsPerWord Transfer word size.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_set_bits_per_word")
    public static native int spiSetBitsPerWord(long spi, byte bitsPerWord);

    /**
     * Set extra flags.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param extraFlags Additional flags bitwise-ORed with the SPI mode.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_set_extra_flags")
    public static native int spiSetExtraFlags(long spi, byte extraFlags);

    /**
     * Set extra flags.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @param extraFlags Additional flags bitwise-ORed with the SPI mode.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_set_extra_flags32")
    public static native int spiSetExtraFlags32(long spi, int extraFlags);

    /**
     * Return the file descriptor (for the underlying spidev device) of the SPI handle.
     *
     * @param spi A valid pointer to an allocated SPI handle structure.
     * @return File descriptor (for the underlying spidev device) of the SPI handle.
     */
    @JniMethod(accessor = "spi_fd")
    public static native int spiFd(long spi);

    /**
     * Return a string representation of the SPI handle.
     *
     * @param spi Valid pointer to an allocated SPI handle structure.
     * @param str String representation of the SPI handle.
     * @param len Length of char array.
     * @return 0 on success, or a negative SPI error code on failure.
     */
    @JniMethod(accessor = "spi_tostring")
    public static native int spiToString(long spi, byte[] str, long len);

    /**
     * Return a string representation of the SPI handle. Wraps native method and simplifies.
     *
     * @param spi Valid pointer to an allocated SPI handle structure.
     * @return SPI handle as String.
     */
    public static String spiToString(long spi) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (spiToString(spi, str, str.length) < 0) {
            throw new RuntimeException(spiErrMessage(spi));
        }
        return jString(str);
    }

    /**
     * Return the libc errno of the last failure that occurred.
     *
     * @param spi Valid pointer to an allocated SPI handle structure.
     * @return libc errno.
     */
    @JniMethod(accessor = "spi_errno")
    public static native int spiErrNo(long spi);

    /**
     * Return a human readable error message pointer of the last failure that occurred.
     *
     * @param spi Valid pointer to an allocated SPI handle structure.
     * @return Error message pointer.
     */
    @JniMethod(accessor = "spi_errmsg")
    public static native long spiErrMsg(long spi);

    /**
     * Return a human readable error message of the last failure that occurred. Converts const char * returned by spi_errmsg to a
     * Java String.
     *
     * @param spi Valid pointer to an allocated SPI handle structure.
     * @return Error message.
     */
    public static String spiErrMessage(long spi) {
        var ptr = spiErrMsg(spi);
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        moveNativeToJava(str, ptr, str.length);
        return jString(str);
    }
}
