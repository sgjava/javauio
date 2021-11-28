/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Led.LED_ERROR_ARG;
import static com.codeferm.periphery.Led.LED_ERROR_CLOSE;
import static com.codeferm.periphery.Led.LED_ERROR_IO;
import static com.codeferm.periphery.Led.LED_ERROR_OPEN;
import static com.codeferm.periphery.Led.LED_ERROR_QUERY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Test LED constants.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class LedTest {

    /**
     * Test constants.
     */
    @Test
    public void constants() {
        // gpio_error_code
        assertEquals(-1, LED_ERROR_ARG);
        assertEquals(-2, LED_ERROR_OPEN);
        assertEquals(-3, LED_ERROR_QUERY);
        assertEquals(-4, LED_ERROR_IO);
        assertEquals(-5, LED_ERROR_CLOSE);
    }
}
