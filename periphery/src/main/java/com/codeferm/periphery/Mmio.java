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
 * c-periphery MMIO wrapper functions for the Linux userspace /dev/mem device.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@JniClass
public class Mmio implements AutoCloseable {

    /**
     * Function was successful.
     */
    public static final int MMIO_SUCCESS = 0;
    /**
     * periphery library.
     */
    private static final Library LIBRARY = new Library("periphery", Mmio.class);
    /**
     * MMIO handle.
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
    public static int MMIO_ERROR_ARG;
    @JniField(flags = {CONSTANT})
    public static int MMIO_ERROR_OPEN;
    @JniField(flags = {CONSTANT})
    public static int MMIO_ERROR_CLOSE;

    /**
     * Map the region of physical memory at the specified base address with the specified size.
     *
     * @param base Doesn't need be aligned to a page boundary.
     * @param size Doesn't need be aligned to a page boundary.
     */
    public Mmio(final long base, final long size) {
        // Allocate handle
        handle = mmioNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open device
        if (mmioOpen(handle, base, size) != MMIO_SUCCESS) {
            // Free handle before throwing exception
            mmioFree(handle);
            throw new RuntimeException(mmioErrMessage(handle));
        }
    }

    /**
     * Map the region of physical memory at the specified base address and size, using the specified memory character device. This
     * open function can be used with sandboxed memory character devices, e.g. /dev/gpiomem.
     *
     * @param base Doesn't need be aligned to a page boundary.
     * @param size Doesn't need be aligned to a page boundary.
     * @param path MMIO path /dev/mem, /dev/gpiomem, etc.
     */
    public Mmio(final long base, final long size, final String path) {
        // Allocate handle
        handle = mmioNew();
        if (handle == 0) {
            throw new RuntimeException("Handle cannot be NULL");
        }
        // Open device
        if (mmioOpenAdvanced(handle, base, size, path) != MMIO_SUCCESS) {
            // Free handle before throwing exception
            mmioFree(handle);
            throw new RuntimeException(mmioErrMessage(handle));
        }
    }

    /**
     * Close and free handle.
     */
    @Override
    public void close() {
        mmioClose(handle);
        mmioFree(handle);
    }

    /**
     * Allocate a MMIO handle. Returns a valid handle on success, or NULL on failure.
     *
     * @return A valid handle on success, or NULL on failure.
     */
    @JniMethod(accessor = "mmio_new")
    public static final native long mmioNew();

    /**
     * Map the region of physical memory at the specified base address with the specified size.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param base Doesn't need be aligned to a page boundary.
     * @param size Doesn't need be aligned to a page boundary.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_open")
    public static native int mmioOpen(long mmio, long base, long size);

    /**
     * Map the region of physical memory at the specified base address with the specified size.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param base Doesn't need be aligned to a page boundary.
     * @param size Doesn't need be aligned to a page boundary.
     * @param path MMIO path /dev/mem, /dev/gpiomem, etc.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_open_advanced")
    public static native int mmioOpenAdvanced(long mmio, long base, long size, final String path);

    /**
     * Return the pointer to the mapped physical memory.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @return Pointer to the mapped physical memory.
     */
    @JniMethod(accessor = "mmio_ptr")
    public static final native long mmioPtr(long mmio);

    /**
     * Read 32-bits from mapped physical memory, starting at the specified byte offset, relative to the base address the MMIO handle
     * was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param offset Starting offset.
     * @param value Read memory.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_read32")
    public static final native int mmioRead32(long mmio, long offset, int[] value);

    /**
     * Read 16-bits from mapped physical memory, starting at the specified byte offset, relative to the base address the MMIO handle
     * was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param offset Starting offset.
     * @param value Read memory.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_read16")
    public static final native int mmioRead16(long mmio, long offset, short[] value);

    /**
     * Read 8-bits from mapped physical memory, starting at the specified byte offset, relative to the base address the MMIO handle
     * was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param offset Starting offset.
     * @param value Read memory.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_read8")
    public static final native int mmioRead8(long mmio, long offset, byte[] value);

    /**
     * Read array of bytes from mapped physical memory, starting at the specified byte offset, relative to the base address the MMIO
     * handle was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param offset Starting offset.
     * @param buf Array of bytes read.
     * @param len Amount to read.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_read")
    public static final native int mmioRead(long mmio, long offset, byte[] buf, long len);

    /**
     * Write 32-bits to mapped physical memory, starting at the specified byte offset, relative to the base address the MMIO handle
     * was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param offset Starting offset.
     * @param value Value to write.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_write32")
    public static final native int mmioWrite32(long mmio, long offset, int value);

    /**
     * Write 16-bits to mapped physical memory, starting at the specified byte offset, relative to the base address the MMIO handle
     * was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param offset Starting offset.
     * @param value Value to write.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_write16")
    public static final native int mmioWrite16(long mmio, long offset, short value);

    /**
     * Write 8-bits to mapped physical memory, starting at the specified byte offset, relative to the base address the MMIO handle
     * was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param offset Starting offset.
     * @param value Value to write.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_write8")
    public static final native int mmioWrite8(long mmio, long offset, byte value);

    /**
     * Write array of bytes to mapped physical memory, starting at the specified byte offset, relative to the base address the MMIO
     * handle was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param offset Starting offset.
     * @param buf Array of bytes write.
     * @param len Amount to write.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_write")
    public static final native int mmioWrite(long mmio, long offset, byte[] buf, long len);

    /**
     * Unmap mapped physical memory.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_close")
    public static native int mmioClose(long mmio);

    /**
     * Free a MMIO handle.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     */
    @JniMethod(accessor = "mmio_free")
    public static native void mmioFree(long mmio);

    /**
     * Return the base address the MMIO handle was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @return Base address.
     */
    @JniMethod(accessor = "mmio_base")
    public static final native long mmioBase(long mmio);

    /**
     * Return the base address the MMIO handle was opened with.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @return Base address.
     */
    @JniMethod(accessor = "mmio_size")
    public static final native long mmioSize(long mmio);

    /**
     *
     * Return a string representation of the MMIO handle.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @param str String representation of the MMIO handle.
     * @param len Length of char array.
     * @return 0 on success, or a negative MMIO error code on failure.
     */
    @JniMethod(accessor = "mmio_tostring")
    public static native int mmioToString(long mmio, byte[] str, long len);

    /**
     * Return a string representation of the MMIO handle. Wraps native method and simplifies.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @return MMIO handle as String.
     */
    public static String mmioToString(long mmio) {
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        if (mmioToString(mmio, str, str.length) < 0) {
            throw new RuntimeException(mmioErrMessage(mmio));
        }
        return jString(str);
    }

    /**
     * Return the libc errno of the last failure that occurred.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @return libc errno.
     */
    @JniMethod(accessor = "mmio_errno")
    public static native int mmioErrNo(long mmio);

    /**
     * Return a human readable error message pointer of the last failure that occurred.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @return Error message pointer.
     */
    @JniMethod(accessor = "mmio_errmsg")
    public static native long mmioErrMsg(long mmio);

    /**
     * Return a human readable error message of the last failure that occurred. Converts const char * returned by mmio_errmsg to a
     * Java String.
     *
     * @param mmio Valid pointer to an allocated MMIO handle structure.
     * @return Error message.
     */
    public static String mmioErrMessage(long mmio) {
        var ptr = mmioErrMsg(mmio);
        var str = new byte[MAX_CHAR_ARRAY_LEN];
        memMove(str, ptr, str.length);
        return jString(str);
    }

}
