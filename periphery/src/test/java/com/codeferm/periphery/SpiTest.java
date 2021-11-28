/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Spi.LSB_FIRST;
import static com.codeferm.periphery.Spi.MSB_FIRST;
import static com.codeferm.periphery.Spi.SPI_ERROR_ARG;
import static com.codeferm.periphery.Spi.SPI_ERROR_CLOSE;
import static com.codeferm.periphery.Spi.SPI_ERROR_CONFIGURE;
import static com.codeferm.periphery.Spi.SPI_ERROR_OPEN;
import static com.codeferm.periphery.Spi.SPI_ERROR_QUERY;
import static com.codeferm.periphery.Spi.SPI_ERROR_TRANSFER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Test SPI constants.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class SpiTest {

    /**
     * Test constants.
     */
    @Test
    public void constants() {
        // spi_error_code
        assertEquals(-1, SPI_ERROR_ARG);
        assertEquals(-2, SPI_ERROR_OPEN);
        assertEquals(-3, SPI_ERROR_QUERY);
        assertEquals(-4, SPI_ERROR_CONFIGURE);
        assertEquals(-5, SPI_ERROR_TRANSFER);
        assertEquals(-6, SPI_ERROR_CLOSE);
        // enum spi_bit_order
        assertEquals(0, MSB_FIRST);
        assertEquals(1, LSB_FIRST);
    }
}
