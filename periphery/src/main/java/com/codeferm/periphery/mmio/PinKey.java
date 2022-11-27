/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.mmio;

import java.util.Comparator;

/**
 * GPIO pin key used for easy lookup and sorting.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
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
     * Default constructor.
     */
    public PinKey() {
    }

    /**
     * All fields constructor.
     *
     * @param chip Pin chip.
     * @param pin Pin number.
     */
    public PinKey(final int chip, final int pin) {
        this.chip = chip;
        this.pin = pin;
    }

    public int getChip() {
        return chip;
    }

    public PinKey setChip(final int chip) {
        this.chip = chip;
        return this;
    }

    public int getPin() {
        return pin;
    }

    public PinKey setPin(final int pin) {
        this.pin = pin;
        return this;
    }

    /**
     * Compare to used for sorting.
     *
     * @param key Pin key to compare.
     * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified
     * object.
     */
    @Override
    public int compareTo(final PinKey key) {
        return Comparator.comparing(PinKey::getChip).thenComparing(PinKey::getPin).compare(this, key);

    }

    /**
     * Object hash code.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.chip;
        hash = 41 * hash + this.pin;
        return hash;
    }

    /**
     * Object equals.
     *
     * @param obj Object to compare to.
     * @return True if equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PinKey other = (PinKey) obj;
        if (this.chip != other.chip) {
            return false;
        }
        if (this.pin != other.pin) {
            return false;
        }
        return true;
    }

    /**
     * String representation of Object.
     *
     * @return String of Object fields.
     */
    @Override
    public String toString() {
        return "PinKey{" + "chip=" + chip + ", pin=" + pin + '}';
    }
}
