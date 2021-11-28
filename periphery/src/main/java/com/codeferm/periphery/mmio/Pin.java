/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.mmio;

import java.util.Objects;

/**
 * GPIO pin based on using MMIO.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class Pin {

    /**
     * Pin key.
     */
    private PinKey key;
    /**
     * Pin group name (port, package, etc.)
     */
    private String groupName;
    /**
     * Pin name.
     */
    private String name;
    /**
     * Pin data input on register.
     */
    private Register dataInOn;
    /**
     * Pin data input off register.
     */
    private Register dataInOff;
    /**
     * Pin data output on register.
     */
    private Register dataOutOn;
    /**
     * Pin data output off register.
     */
    private Register dataOutOff;
    /**
     * MMIO handle.
     */
    private long mmioHadle;

    /**
     * Default constructor.
     */
    public Pin() {
    }

    /**
     * Pin key only constructor.
     *
     * @param key Pin key.
     */
    public Pin(final PinKey key) {
        this.key = key;
    }

    /**
     * Pin key and pin name constructor.
     *
     * @param key Pin key.
     * @param name Pin name.
     */
    public Pin(final PinKey key, final String name) {
        this.key = key;
        this.name = name;
    }

    /**
     * All fields constructor.
     *
     * @param key Pin key.
     * @param groupName Group name.
     * @param name Pin name.
     * @param dataInOn Data input on register.
     * @param dataInOff Data input off register.
     * @param dataOutOn Data output on register.
     * @param dataOutOff Data ouput off register.
     */
    public Pin(PinKey key, String groupName, String name, Register dataInOn, Register dataInOff, Register dataOutOn,
            Register dataOutOff) {
        this.key = key;
        this.groupName = groupName;
        this.name = name;
        this.dataInOn = dataInOn;
        this.dataInOff = dataInOff;
        this.dataOutOn = dataOutOn;
        this.dataOutOff = dataOutOff;
    }

    public PinKey getKey() {
        return key;
    }

    public Pin setKey(PinKey key) {
        this.key = key;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public Pin setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getName() {
        return name;
    }

    public Pin setName(String name) {
        this.name = name;
        return this;
    }

    public Register getDataInOn() {
        return dataInOn;
    }

    public Pin setDataInOn(Register dataInOn) {
        this.dataInOn = dataInOn;
        return this;
    }

    public Register getDataInOff() {
        return dataInOff;
    }

    public Pin setDataInOff(Register dataInOff) {
        this.dataInOff = dataInOff;
        return this;
    }

    public Register getDataOutOn() {
        return dataOutOn;
    }

    public Pin setDataOutOn(Register dataOutOn) {
        this.dataOutOn = dataOutOn;
        return this;
    }

    public Register getDataOutOff() {
        return dataOutOff;
    }

    public Pin setDataOutOff(Register dataOutOff) {
        this.dataOutOff = dataOutOff;
        return this;
    }

    public long getMmioHadle() {
        return mmioHadle;
    }

    public Pin setMmioHadle(long mmioHadle) {
        this.mmioHadle = mmioHadle;
        return this;
    }

    /**
     * Object hash code.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.key);
        hash = 83 * hash + Objects.hashCode(this.groupName);
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.dataInOn);
        hash = 83 * hash + Objects.hashCode(this.dataInOff);
        hash = 83 * hash + Objects.hashCode(this.dataOutOn);
        hash = 83 * hash + Objects.hashCode(this.dataOutOff);
        hash = 83 * hash + (int) (this.mmioHadle ^ (this.mmioHadle >>> 32));
        return hash;
    }

    /**
     * Object equals.
     *
     * @param obj Object to compare to.
     * @return True if equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pin other = (Pin) obj;
        if (this.mmioHadle != other.mmioHadle) {
            return false;
        }
        if (!Objects.equals(this.groupName, other.groupName)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.dataInOn, other.dataInOn)) {
            return false;
        }
        if (!Objects.equals(this.dataInOff, other.dataInOff)) {
            return false;
        }
        if (!Objects.equals(this.dataOutOn, other.dataOutOn)) {
            return false;
        }
        if (!Objects.equals(this.dataOutOff, other.dataOutOff)) {
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
        return "Pin{" + "key=" + key + ", groupName=" + groupName + ", name=" + name + ", dataInOn=" + dataInOn + ", dataInOff="
                + dataInOff + ", dataOutOn=" + dataOutOn + ", dataOutOff=" + dataOutOff + ", mmioHadle=" + mmioHadle + '}';
    }
}
