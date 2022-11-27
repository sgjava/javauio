/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Gpio.GPIO_BIAS_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_DISABLE;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_PULL_DOWN;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_PULL_UP;
import static com.codeferm.periphery.Gpio.GPIO_DIR_IN;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT_HIGH;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT_LOW;
import static com.codeferm.periphery.Gpio.GPIO_DRIVE_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_DRIVE_OPEN_DRAIN;
import static com.codeferm.periphery.Gpio.GPIO_DRIVE_OPEN_SOURCE;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_BOTH;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_FALLING;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_NONE;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_RISING;
import static com.codeferm.periphery.Gpio.GPIO_ERROR_ARG;
import static com.codeferm.periphery.Gpio.GPIO_ERROR_CLOSE;
import static com.codeferm.periphery.Gpio.GPIO_ERROR_CONFIGURE;
import static com.codeferm.periphery.Gpio.GPIO_ERROR_INVALID_OPERATION;
import static com.codeferm.periphery.Gpio.GPIO_ERROR_IO;
import static com.codeferm.periphery.Gpio.GPIO_ERROR_NOT_FOUND;
import static com.codeferm.periphery.Gpio.GPIO_ERROR_OPEN;
import static com.codeferm.periphery.Gpio.GPIO_ERROR_QUERY;
import static com.codeferm.periphery.Gpio.GPIO_ERROR_UNSUPPORTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Test GPIO constants.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class GpioTest {

    /**
     * Test constants.
     */
    @Test
    public void constants() {
        // gpio_error_code
        assertEquals(-1, GPIO_ERROR_ARG);
        assertEquals(-2, GPIO_ERROR_OPEN);
        assertEquals(-3, GPIO_ERROR_NOT_FOUND);
        assertEquals(-4, GPIO_ERROR_QUERY);
        assertEquals(-5, GPIO_ERROR_CONFIGURE);
        assertEquals(-6, GPIO_ERROR_UNSUPPORTED);
        assertEquals(-7, GPIO_ERROR_INVALID_OPERATION);
        assertEquals(-8, GPIO_ERROR_IO);
        assertEquals(-9, GPIO_ERROR_CLOSE);
        // enum gpio_direction
        assertEquals(0, GPIO_DIR_IN);
        assertEquals(1, GPIO_DIR_OUT);
        assertEquals(2, GPIO_DIR_OUT_LOW);
        assertEquals(3, GPIO_DIR_OUT_HIGH);
        // enum gpio_edge
        assertEquals(0, GPIO_EDGE_NONE);
        assertEquals(1, GPIO_EDGE_RISING);
        assertEquals(2, GPIO_EDGE_FALLING);
        assertEquals(3, GPIO_EDGE_BOTH);
        // enum gpio_bias
        assertEquals(0, GPIO_BIAS_DEFAULT);
        assertEquals(1, GPIO_BIAS_PULL_UP);
        assertEquals(2, GPIO_BIAS_PULL_DOWN);
        assertEquals(3, GPIO_BIAS_DISABLE);
        // enum gpio_drive
        assertEquals(0, GPIO_DRIVE_DEFAULT);
        assertEquals(1, GPIO_DRIVE_OPEN_DRAIN);
        assertEquals(2, GPIO_DRIVE_OPEN_SOURCE);
    }
}
