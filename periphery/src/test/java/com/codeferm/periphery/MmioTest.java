/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Mmio.MMIO_ERROR_ARG;
import static com.codeferm.periphery.Mmio.MMIO_ERROR_CLOSE;
import static com.codeferm.periphery.Mmio.MMIO_ERROR_OPEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Test MMIO constants.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class MmioTest {

    /**
     * Test constants.
     */
    @Test
    public void constants() {
        // mmio_error_code
        assertEquals(-1, MMIO_ERROR_ARG);
        assertEquals(-2, MMIO_ERROR_OPEN);
        assertEquals(-3, MMIO_ERROR_CLOSE);
    }
}
