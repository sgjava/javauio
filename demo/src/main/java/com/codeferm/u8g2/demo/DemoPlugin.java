/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;

/**
 * Generic interface for U8g2 demo plugins.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DemoPlugin {

    /**
     * Executes the plugin rendering loop.
     *
     * @param u8g2 Native display handle used to query dimensions and draw.
     * @param display Display helper for sleep operations.
     * @param fps Target frames per second.
     * @param duration Duration to run in milliseconds (0 for infinite).
     */
    void run(final long u8g2, final Display display, final int fps, final long duration);
}
