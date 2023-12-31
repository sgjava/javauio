/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.mmio;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse property files containing board specific information.
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
public class File {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(File.class);

    /**
     * File description.
     */
    private String description;
    /**
     * GPIO device number.
     */
    private List<Integer> gpioDev;
    /**
     * GPIO chips sometimes called banks
     */
    private List<Long> chips;
    /**
     * MMIO size for each chip
     */
    private List<Long> mmioSize;
    /**
     * GPIOD pin numbers, no weirdo wiringPi or BCM numbers
     */
    private List<Integer> pins;
    /**
     * GPIO CHIP used by pin
     */
    private List<Integer> pinChip;
    /**
     * Pin name
     */
    private List<String> pinName;
    /**
     * Chip group os on
     */
    private List<Integer> groupChip;
    /**
     * Name of group
     */
    private List<String> groupName;
    /**
     * Data in register on offset inside chip
     */
    private List<Integer> dataInOnOffset;
    /**
     * Data in register off offset inside chip
     */
    private List<Integer> dataInOffOffset;
    /**
     * Data out register on offset inside chip
     */
    private List<Integer> dataOutOnOffset;
    /**
     * Data out register off offset inside chip
     */
    private List<Integer> dataOutOffOffset;
    /**
     * Use input data register to detect changes.
     */
    private boolean useInputDataReg;
    /**
     * MMIO path.
     */
    private String memPath;

    /**
     * Load properties file from file path or fail back to class path.
     *
     * @param propertyFile Name of property file.
     * @return Properties.
     */
    public Properties loadProperties(final String propertyFile) {
        Properties props = new Properties();
        try {
            // Get properties from file
            props.load(new FileInputStream(propertyFile));
            logger.debug("Properties loaded from file {}", propertyFile);
        } catch (IOException e1) {
            logger.warn("Properties file not found {}", propertyFile);
            // Get properties from classpath
            try (final var stream = File.class.getClassLoader().getResourceAsStream(propertyFile)) {
                props.load(stream);
                logger.debug("Properties loaded from class path {}", propertyFile);
            } catch (IOException e2) {
                throw new RuntimeException("No properties found", e2);
            }
        }
        return props;
    }

    /**
     * Return List of Long from comma delimited string. Spaces are stripped out.
     *
     * @param str Comma delimited hex string.
     * @return List of Long.
     */
    public List<Long> hexToLongList(final String str) {
        return Arrays.stream(str.replace(" ", "").split(",")).map(Long::decode).collect(Collectors.toList());
    }

    /**
     * Return List of Long from comma delimited string. Spaces are stripped out.
     *
     * @param str Comma delimited decimal string.
     * @return List of Long.
     */
    public List<Long> decToLongList(final String str) {
        return Arrays.stream(str.replace(" ", "").split(",")).map(Long::parseLong).collect(Collectors.toList());
    }

    /**
     * Return List of Integer from comma delimited string. Spaces are stripped
     * out.
     *
     * @param str Comma delimited hex string.
     * @return List of Integer.
     */
    public List<Integer> hexToIntList(final String str) {
        return Arrays.stream(str.replace(" ", "").split(",")).map(Integer::decode).collect(Collectors.toList());
    }

    /**
     * Return List of Integer from comma delimited string. Spaces are stripped
     * out.
     *
     * @param str Comma delimited decimal string.
     * @return List of Integer.
     */
    public List<Integer> decToIntList(final String str) {
        return Arrays.stream(str.replace(" ", "").split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    /**
     * Return List of String from comma delimited string. Spaces are stripped
     * out.
     *
     * @param str Comma delimited string.
     * @return List of String.
     */
    public List<String> strToStrList(final String str) {
        return Arrays.asList(str.replace(" ", "").split(","));
    }

    /**
     * Parse input property file.
     *
     * @param fileName Property file name.
     * @return Pin Map.
     */
    public Map<PinKey, Pin> parseInput(final String fileName) {
        final Map<PinKey, Pin> pinMap = new TreeMap<>();
        final var properties = loadProperties(fileName);
        // Make sure properties loaded
        if (!properties.isEmpty()) {
            description = properties.getProperty("description");
            gpioDev = decToIntList(properties.getProperty("gpio.dev"));
            chips = hexToLongList(properties.getProperty("chips"));
            mmioSize = decToLongList(properties.getProperty("chip.size"));
            pins = decToIntList(properties.getProperty("pins"));
            pinChip = decToIntList(properties.getProperty("pin.chip"));
            pinName = strToStrList(properties.getProperty("pin.name"));
            groupChip = decToIntList(properties.getProperty("group.chip"));
            groupName = strToStrList(properties.getProperty("group.name"));
            dataInOnOffset = hexToIntList(properties.getProperty("data.in.on.offset"));
            dataInOffOffset = hexToIntList(properties.getProperty("data.in.off.offset"));
            dataOutOnOffset = hexToIntList(properties.getProperty("data.out.on.offset"));
            dataOutOffOffset = hexToIntList(properties.getProperty("data.out.off.offset"));
            useInputDataReg = Boolean.parseBoolean(properties.getProperty("use.input.data.reg"));
            memPath = properties.getProperty("mem.path");
            // Create minimal pin Map with chip, pin and pin name
            for (int i = 0; i < pins.size(); i++) {
                PinKey key = new PinKey(gpioDev.get(pinChip.get(i)), pins.get(i));
                pinMap.put(key, new Pin(key, pinName.get(i)));
            }
        }
        return pinMap;
    }

    /**
     * Generate output property file from pin Map.
     *
     * @param pinMap Pin Map.
     * @param inFileName Input property file.
     * @param outFileName Output property file.
     */
    public void genProperties(final Map<PinKey, Pin> pinMap, final String inFileName, final String outFileName) {
        logger.debug("Generating output file {}", outFileName);
        final var properties = loadProperties(inFileName);
        try (final var writer = new BufferedWriter(new FileWriter(outFileName))) {
            // Write header comment
            writer.write(String.format(
                    "#\n# Generated by %s on %s\n#\n# Format: pin.chip.number = group name, pin name, data in on name, "
                    + "data in on offset, data in on mask, data in off name, data in off offset, data in off mask, data out on name, "
                    + "data out on offset, data out on mask, data out off name, data out off offset, data out off mask\n#\n\n",
                    this.getClass().getCanonicalName(), DateTimeFormatter.ISO_INSTANT.format(Instant.now())));
            // Write individual properties from input file
            writer.write(String.format("description = %s\nchips = %s\nchip.size = %s\ngpio.dev = %s\nmem.path = %s\n", properties.
                    getProperty("description"), properties.getProperty("chips"), properties.getProperty("chip.size"), properties.
                    getProperty("gpio.dev"), properties.getProperty("mem.path")));
            // Write entry for each pin
            for (final var entry : pinMap.entrySet()) {
                PinKey key = entry.getKey();
                Pin value = entry.getValue();
                // Make sure detect worked by making sure there's a group name
                if (value.groupName() != null) {
                    writer.write(String.format(
                            "pin.%d.%d = %s, %s, %s, 0x%02x, 0x%08x, %s, 0x%02x, 0x%08x, %s, 0x%02x, 0x%08x, %s, 0x%02x, 0x%08x\n",
                            key.chip(), key.pin(), value.groupName(), value.name(),
                            value.dataInOn().name(), value.dataInOn().offset(), value.dataInOn().mask(), value.
                            dataInOff().name(), value.dataInOff().offset(), value.dataInOff().mask(), value.
                            dataOutOn().name(), value.dataOutOn().offset(), value.dataOutOn().mask(), value.
                            dataOutOff().name(), value.dataOutOff().offset(), value.dataOutOff().mask()));
                } else {
                    logger.warn(String.format("Chip %d pin %d detection failed, so skipping", key.chip(), key.pin()));
                }
            }
        } catch (IOException e) {
            logger.error(String.format("Error %s", e.getMessage()));
        }
    }

    /**
     * Convert hex string to int.
     *
     * @param str Hex string.
     * @return int or -1 for null/"null";
     */
    public int hexToInt(final String str) {
        var i = -1;
        if (str != null && !str.toLowerCase().contains("null")) {
            // Trim and remove 0x before converting to int (handles values >= 0x80000000 too)
            i = (int) Long.parseLong(str.trim().substring(2), 16);
        }
        return i;
    }

    /**
     * Convert string to string handling "null".
     *
     * @param str String.
     * @return String null for null/"null";
     */
    public String strToStr(final String str) {
        String s = null;
        if (str != null && !str.toLowerCase().contains("null")) {
            s = str.trim();
        }
        return s;
    }

    /**
     * Parse property file into Map.
     *
     * @param inFileName Property file name.
     * @return Pin Map.
     */
    public Map<PinKey, Pin> loadPinMap(final String inFileName) {
        final var properties = loadProperties(inFileName);
        chips = hexToLongList(properties.getProperty("chips"));
        mmioSize = decToLongList(properties.getProperty("chip.size"));
        gpioDev = decToIntList(properties.getProperty("gpio.dev"));
        memPath = properties.getProperty("mem.path");
        final Map<PinKey, Pin> pinMap = new TreeMap<>();
        // Process all properties
        properties.entrySet().forEach((entry) -> {
            final var key = ((String) entry.getKey()).split(Pattern.quote("."));
            // Only process pins
            if (key[0].contains("pin")) {
                final var value = ((String) entry.getValue()).split(",");
                final var pinKey = new PinKey(Integer.parseInt(key[1]), Integer.parseInt(key[2]));
                final var dataInOn = new Register(strToStr(value[2]), hexToInt(value[3]), hexToInt(value[4]));
                final var dataInOff = new Register(strToStr(value[5]), hexToInt(value[6]), hexToInt(value[7]));
                final var dataOutOn = new Register(strToStr(value[8]), hexToInt(value[9]), hexToInt(value[10]));
                final var dataOutOff = new Register(strToStr(value[11]), hexToInt(value[12]), hexToInt(value[13]));
                final var pin = new Pin(pinKey, strToStr(value[0]), strToStr(value[1]), dataInOn, dataInOff, dataOutOn, dataOutOff);
                pinMap.put(pinKey, pin);
            }
        });
        return pinMap;
    }
}
