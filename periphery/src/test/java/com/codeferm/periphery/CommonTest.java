/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test Common JNI operations using Java 25 syntax.
 *
 * @author Steven P. Goldsmith
 * @version 1.1.0
 * @since 1.0.0
 */
class CommonTest {

    @Test
    @DisplayName("Test native memory allocation and byte array movement")
    void testMallocAndMemMove() {
        var original = new byte[]{10, 20, 30, 40, 50};
        var size = original.length;

        // Allocate native memory
        var ptr = Common.malloc(size);
        assertNotEquals(0, ptr, "Memory pointer should not be null (0)");

        try {
            // Move Java byte array to C memory
            Common.moveJavaToNative(ptr, original, size);

            // Create a destination array to read back from C memory
            var result = new byte[size];
            Common.moveNativeToJava(result, ptr, size);

            // Verify the data survived the round trip
            assertArrayEquals(original, result, "Data read from native memory should match original");
        } finally {
            // Always free native memory
            Common.free(ptr);
        }
    }

    @Test
    @DisplayName("Test C-style string conversion utilities")
    void testStringConversions() {
        var originalStr = "Hello JNI";

        // Convert Java String to C pointer
        var cStrPtr = Common.cString(originalStr);
        assertNotEquals(0, cStrPtr, "C String pointer should not be null");

        try {
            // Include null terminator (+1)
            var buffer = new byte[originalStr.length() + 1];
            Common.moveNativeToJava(buffer, cStrPtr, buffer.length);

            // Convert back to Java String
            var resultStr = Common.jString(buffer);

            assertEquals(originalStr, resultStr, "String should match after round-trip conversion");
        } finally {
            Common.free(cStrPtr);
        }
    }

    @Test
    @DisplayName("Test jString with null terminator in the middle of a buffer")
    void testJStringNullTermination() {
        // Using var with explicit array initialization
        var buffer = new byte[10];
        buffer[0] = 'A';
        buffer[1] = 'B';
        buffer[2] = 0; // Null terminator
        buffer[3] = 'C';

        var result = Common.jString(buffer);
        assertEquals("AB", result, "jString should stop at the first null terminator");
    }
}