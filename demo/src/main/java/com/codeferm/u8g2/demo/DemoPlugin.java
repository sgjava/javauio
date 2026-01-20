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
     * @param u8g2 Native display handle.
     * @param width Display width.
     * @param height Display height.
     * @param display Display helper for timing/sleep.
     */
    void run(final long u8g2, final int width, final int height, final Display display);
}
