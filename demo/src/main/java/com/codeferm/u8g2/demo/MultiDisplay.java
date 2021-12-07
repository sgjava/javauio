/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.FontType;
import com.codeferm.u8g2.SetupType;
import com.codeferm.u8g2.U8g2;
import static com.codeferm.u8g2.U8x8.U8X8_PIN_NONE;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Simple text demo.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "MultiDisplay", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Multiple display demo")
public class MultiDisplay implements Callable<Integer> {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MultiDisplay.class);
    /**
     * Integer regex.
     */
    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    /**
     * Input file.
     */
    @CommandLine.Option(names = {"-f", "--file"}, description = "Input property file name, ${DEFAULT-VALUE} by default.")
    private String fileName = "display.properties";
    /**
     * Display helper.
     */
    private Display display = new Display();

    /**
     * Add display types here and in setup method.
     */
    public enum DisplayType {
        I2CHW,
        I2CSW,
        SPIHW,
        SPISW;
    }

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
            try (final var stream = MultiDisplay.class.getClassLoader().getResourceAsStream(propertyFile)) {
                props.load(stream);
                logger.debug("Properties loaded from class path {}", propertyFile);
            } catch (IOException e2) {
                throw new RuntimeException("No properties found", e2);
            }
        }
        return props;
    }

    /**
     * Configure display based on number in property file.
     *
     * @param displayNum Display number (suffix in property file).
     * @param properties Properties.
     * @return Pointer to u8g2 struct.
     */
    public long setup(final int displayNum, final Properties properties) {
        final var keys = properties.stringPropertyNames();
        final var intMap = new HashMap<String, Integer>();
        final var strMap = new HashMap<String, String>();
        // Built Integer and String maps of values
        keys.forEach(key -> {
            final var split = key.split("\\.");
            final int number = Integer.parseInt(split[split.length - 1]);
            // Filter only displayNum
            if (number == displayNum) {
                // See if int value
                if (pattern.matcher(properties.getProperty(key)).matches()) {
                    intMap.put(split[0], Integer.parseInt(properties.getProperty(key)));
                } else {
                    strMap.put(split[0], properties.getProperty(key));
                }
            }
        });
        long u8g2 = 0;
        switch (DisplayType.valueOf(strMap.get("type"))) {
            case I2CHW:
                u8g2 = display.initHwI2c(SetupType.valueOf(strMap.get("setup")), intMap.get("bus"), intMap.get("address"));
                break;
            case I2CSW:
                u8g2 = display.initSwI2c(SetupType.valueOf(strMap.get("setup")), intMap.get("gpio"), intMap.get("scl"), intMap.get(
                        "sda"), U8X8_PIN_NONE, intMap.get("delay"));
                break;
            case SPIHW:
                u8g2 = display.initHwSpi(SetupType.valueOf(strMap.get("setup")), intMap.get("gpio"), intMap.get("bus"), intMap.get(
                        "dc"), intMap.get("reset"), U8X8_PIN_NONE);
                break;
            case SPISW:
                u8g2 = display.initSwSpi(SetupType.valueOf(strMap.get("setup")), intMap.get("gpio"), intMap.get("dc"), intMap.get(
                        "reset"), intMap.get("mosi"), intMap.get("sck"), intMap.get("cs"), intMap.get("delay"));
                break;
            default:
                throw new RuntimeException(String.format("%s is not a valid type", strMap.get("setup")));
        }
        U8g2.setFont(u8g2, display.getFontPtr(FontType.valueOf(strMap.get("font"))));
        U8g2.clearBuffer(u8g2);
        U8g2.sendBuffer(u8g2);
        U8g2.setPowerSave(u8g2, 0);
        return u8g2;
    }

    /**
     * Return a Set of display numbers base on property key suffix.
     *
     * @param properties Properties.
     * @return Set of display numbers.
     */
    public Set<Integer> getDisplays(final Properties properties) {
        final var set = new HashSet<Integer>();
        final var keys = properties.stringPropertyNames();
        keys.stream().map(key -> key.split("\\.")).forEachOrdered(split -> {
            set.add(Integer.parseInt(split[split.length - 1]));
        });
        return set;
    }

    /**
     * Simple text display.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        final var properties = loadProperties(fileName);
        final var set = getDisplays(properties);
        final var map = new TreeMap<Integer, Long>();
        // Setup displays and store in Map.
        set.forEach(displayNum -> {
            map.put(displayNum, setup(displayNum, properties));
        });
        // Write string to each display
        map.entrySet().
                forEach(entry -> {
                    U8g2.drawStr(entry.getValue(), 1, U8g2.getMaxCharHeight(entry.getValue()), String.format("Display %d", entry.
                            getKey()));
                    U8g2.sendBuffer(entry.getValue());
                });
        display.sleep(5000);
        // Shut down displays
        map.entrySet().stream().map(entry -> {
            U8g2.setPowerSave(entry.getValue(), 1);
            return entry;
        }).forEachOrdered(entry -> {
            display.done(entry.getValue());
        });
        U8g2.doneI2c();
        U8g2.doneSpi();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new MultiDisplay()).registerConverter(Byte.class,
                Byte::decode).registerConverter(Byte.TYPE,
                        Byte::decode).registerConverter(Short.class,
                        Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class,
                        Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class,
                        Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
