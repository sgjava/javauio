/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery;

import static com.codeferm.periphery.Pwm.PWM_ERROR_ARG;
import static com.codeferm.periphery.Pwm.PWM_ERROR_CLOSE;
import static com.codeferm.periphery.Pwm.PWM_ERROR_CONFIGURE;
import static com.codeferm.periphery.Pwm.PWM_ERROR_OPEN;
import static com.codeferm.periphery.Pwm.PWM_ERROR_QUERY;
import static com.codeferm.periphery.Pwm.PWM_POLARITY_INVERSED;
import static com.codeferm.periphery.Pwm.PWM_POLARITY_NORMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Test PWM constants.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class PwmTest {

    /**
     * Test constants.
     */
    @Test
    public void constants() {
        // pwm_error_code
        assertEquals(-1, PWM_ERROR_ARG);
        assertEquals(-2, PWM_ERROR_OPEN);
        assertEquals(-3, PWM_ERROR_QUERY);
        assertEquals(-4, PWM_ERROR_CONFIGURE);
        assertEquals(-5, PWM_ERROR_CLOSE);
        // enum pwm_polarity
        assertEquals(0, PWM_POLARITY_NORMAL);
        assertEquals(1, PWM_POLARITY_INVERSED);
    }
}
