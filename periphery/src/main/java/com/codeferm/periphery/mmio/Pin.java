/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.mmio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * GPIO pin based on using MMIO.
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
     * All fields except mmioHandle constructor.
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
}
