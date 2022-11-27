/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.mmio;

import java.util.Objects;

/**
 * GPIO register.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class Register {

    /**
     * Name of register.
     */
    private String name;
    /**
     * Register offset from chip address.
     */
    private Integer offset;
    /**
     * Bit mask used to control operation.
     */
    private Integer mask;

    /**
     * Default constructor.
     */
    public Register() {
    }

    /**
     * All fields constructor.
     *
     * @param name Register name.
     * @param offset Register offset.
     * @param mask Register mask.
     */
    public Register(final String name, final Integer offset, final Integer mask) {
        this.name = name;
        this.offset = offset;
        this.mask = mask;
    }

    public String getName() {
        return name;
    }

    public Register setName(final String name) {
        this.name = name;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public Register setOffset(final Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getMask() {
        return mask;
    }

    public Register setMask(final Integer mask) {
        this.mask = mask;
        return this;
    }

    /**
     * Object hash code.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.offset);
        hash = 97 * hash + Objects.hashCode(this.mask);
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
        final Register other = (Register) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.offset, other.offset)) {
            return false;
        }
        if (!Objects.equals(this.mask, other.mask)) {
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
        return "Register{" + "name=" + name + ", offset=" + offset + ", mask=" + mask + '}';
    }
}
