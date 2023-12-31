/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.mmio;

import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * GPIO pin key used for easy lookup and sorting.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(fluent = true)
public class PinKey implements Comparable<PinKey> {

    /**
     * Pin chip.
     */
    private int chip;
    /**
     * Pin number.
     */
    private int pin;

    /**
     * Compare to used for sorting.
     *
     * @param key Pin key to compare.
     * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified
     * object.
     */
    @Override
    public int compareTo(PinKey key) {
        final int chipCompare = Integer.compare(this.chip, key.chip);
        if (chipCompare != 0) {
            return chipCompare;
        }
        return Integer.compare(this.pin, key.pin);
    }
}
