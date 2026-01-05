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
 * Test Common JNI operations.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
class CommonTest {

    @Test
    @DisplayName("Test native memory allocation and byte array movement")
    void testMallocAndMemMove() {
        byte[] original = {10, 20, 30, 40, 50};
        int size = original.length;

        // Allocate native memory
        long ptr = Common.malloc(size);
        assertNotEquals(0, ptr, "Memory pointer should not be null (0)");

        try {
            // Move Java byte array to C memory
            Common.moveJavaToNative(ptr, original, size);

            // Create a destination array to read back from C memory
            byte[] result = new byte[size];
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
        String originalStr = "Hello JNI";

        // Convert Java String to C pointer
        long cStrPtr = Common.cString(originalStr);
        assertNotEquals(0, cStrPtr, "C String pointer should not be null");

        try {
            // To test jString, we first read the native memory back into a buffer
            // We include the null terminator (+1)
            byte[] buffer = new byte[originalStr.length() + 1];
            Common.moveNativeToJava(buffer, cStrPtr, buffer.length);

            // Convert back to Java String
            String resultStr = Common.jString(buffer);

            assertEquals(originalStr, resultStr, "String should match after round-trip conversion");
        } finally {
            Common.free(cStrPtr);
        }
    }

    @Test
    @DisplayName("Test jString with null terminator in the middle of a buffer")
    void testJStringNullTermination() {
        // Buffer is larger than the actual string content
        byte[] buffer = new byte[10];
        buffer[0] = 'A';
        buffer[1] = 'B';
        buffer[2] = 0; // Null terminator
        buffer[3] = 'C';

        String result = Common.jString(buffer);
        assertEquals("AB", result, "jString should stop at the first null terminator");
    }
}
