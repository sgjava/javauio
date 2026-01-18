/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static org.fusesource.hawtjni.runtime.ArgFlag.CRITICAL;
import static org.fusesource.hawtjni.runtime.ArgFlag.NO_IN;
import static org.fusesource.hawtjni.runtime.ArgFlag.NO_OUT;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;

/**
 * Java Periphery common code.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@JniClass
public class Common {

    /**
     * Used for C reference character array arguments.
     */
    public static final int MAX_CHAR_ARRAY_LEN = 256;
    /**
     * periphery library.
     */
    private static final Library LIBRARY = new Library("periphery", Common.class);

    /**
     * Load library.
     */
    static {
        LIBRARY.load();
    }

    /**
     * Allocate native memory.
     *
     * @param size Amount of memory to allocate.
     * @return Pointer to memory.
     */
    @JniMethod(cast = "void *")
    public static final native long malloc(@JniArg(cast = "size_t") long size);

    /**
     * Free native memory.
     *
     * @param ptr Pointer to memory.
     */
    public static final native void free(@JniArg(cast = "void *") long ptr);

    /**
     * Move C memory to byte array.
     *
     * @param dest Java byte array.
     * @param src Pointer to C memory.
     * @param size Amount of bytes to move.
     */
    @JniMethod(accessor = "memmove")
    public static final native void moveNativeToJava(@JniArg(cast = "void *", flags = {NO_IN, CRITICAL}) byte[] dest, @JniArg(cast
            = "const void *") long src, @JniArg(cast = "size_t") long size);

    /**
     * Move byte array to C memory.
     *
     * @param dest Pointer to C memory.
     * @param src Java byte array.
     * @param size Amount of bytes to move.
     */
    @JniMethod(accessor = "memmove")
    public static final native void moveJavaToNative(
            @JniArg(cast = "void *") long dest, @JniArg(cast = "const void *", flags = {NO_OUT, CRITICAL}) byte[] src, @JniArg(cast
                    = "size_t") long size);

    /**
     * Convert C style string to Java String.
     *
     * @param str 0 terminated character array.
     * @return Java String.
     */
    public static String jString(final byte[] str) {
        var i = 0;
        // Find 0 terminator
        while (i < str.length && str[i] != 0) {
            i++;
        }
        return new String(str, 0, i);
    }

    /**
     * Convert Java String to C style string. Remember to free native memory with free.
     *
     * @param str Java String.
     * @return Pointer to C style string.
     */
    public static long cString(final String str) {
        // Add null terminator
        final var cStr = str + "\0";
        // Allocate native memory
        final var strPtr = malloc(cStr.length());
        // Copy Java String to native memory
        moveJavaToNative(strPtr, cStr.getBytes(), cStr.length());
        return strPtr;
    }
}
