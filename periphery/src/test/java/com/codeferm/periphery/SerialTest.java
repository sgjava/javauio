/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Serial.PARITY_EVEN;
import static com.codeferm.periphery.Serial.PARITY_NONE;
import static com.codeferm.periphery.Serial.PARITY_ODD;
import static com.codeferm.periphery.Serial.SERIAL_ERROR_ARG;
import static com.codeferm.periphery.Serial.SERIAL_ERROR_CLOSE;
import static com.codeferm.periphery.Serial.SERIAL_ERROR_CONFIGURE;
import static com.codeferm.periphery.Serial.SERIAL_ERROR_IO;
import static com.codeferm.periphery.Serial.SERIAL_ERROR_OPEN;
import static com.codeferm.periphery.Serial.SERIAL_ERROR_QUERY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Test serial constants.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class SerialTest {

    /**
     * Test constants.
     */
    @Test
    public void constants() {
        // serial_error_code
        assertEquals(-1, SERIAL_ERROR_ARG);
        assertEquals(-2, SERIAL_ERROR_OPEN);
        assertEquals(-3, SERIAL_ERROR_QUERY);
        assertEquals(-4, SERIAL_ERROR_CONFIGURE);
        assertEquals(-5, SERIAL_ERROR_IO);
        assertEquals(-6, SERIAL_ERROR_CLOSE);
        // enum serial_parity
        assertEquals(0, PARITY_NONE);
        assertEquals(1, PARITY_ODD);        
        assertEquals(2, PARITY_EVEN);        
    }
}
