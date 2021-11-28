/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.I2c.I2C_ERROR_ARG;
import static com.codeferm.periphery.I2c.I2C_ERROR_CLOSE;
import static com.codeferm.periphery.I2c.I2C_ERROR_NOT_SUPPORTED;
import static com.codeferm.periphery.I2c.I2C_ERROR_OPEN;
import static com.codeferm.periphery.I2c.I2C_ERROR_QUERY;
import static com.codeferm.periphery.I2c.I2C_ERROR_TRANSFER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Test I2C constants.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class I2cTest {

    /**
     * Test constants.
     */
    @Test
    public void constants() {
        // i2c_error_code
        assertEquals(-1, I2C_ERROR_ARG);
        assertEquals(-2, I2C_ERROR_OPEN);
        assertEquals(-3, I2C_ERROR_QUERY);
        assertEquals(-4, I2C_ERROR_NOT_SUPPORTED);
        assertEquals(-5, I2C_ERROR_TRANSFER);
        assertEquals(-6, I2C_ERROR_CLOSE);
    }
}
