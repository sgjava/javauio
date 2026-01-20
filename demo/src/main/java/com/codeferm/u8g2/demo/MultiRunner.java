/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import com.codeferm.u8g2.FontType;
import com.codeferm.u8g2.SetupType;
import com.codeferm.u8g2.U8g2;
import static com.codeferm.u8g2.U8x8.U8X8_PIN_NONE;
import com.codeferm.u8g2.demo.Base.DisplayType;
import static com.codeferm.u8g2.demo.Base.DisplayType.SDL;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Orchestrates multiple U8g2 displays with randomized plugins and timed execution.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "MultiRunner", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class MultiRunner implements Callable<Integer> {

    /**
     * Regex pattern to identify numeric values in properties.
     */
    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    /**
     * Shared random generator for plugin selection.
     */
    private final Random random = new Random();

    /**
     * Path to the properties file provided via CLI.
     */
    @Option(names = {"-f", "--file"}, description = "Input property file name", defaultValue = "sdl.properties")
    private String fileName;

    /**
     * Default FPS for all displays if not specified in properties.
     */
    @Option(names = {"--fps"}, description = "Frames per second", defaultValue = "30")
    private int fps;

    /**
     * Default execution duration in milliseconds for all displays.
     */
    @Option(names = {"--runms"}, description = "Run duration in milliseconds (0 for infinite)", defaultValue = "0")
    private long runMs;

    /**
     * Display management utility.
     */
    private final Display display = new Display();

    /**
     * Map of display IDs to their connection types for cleanup.
     */
    private final Map<Integer, DisplayType> typeMap = new HashMap<>();

    /**
     * Loads application properties from disk or classpath.
     *
     * @param propertyFile File path to load.
     * @return Loaded properties object.
     */
    public Properties loadProperties(final String propertyFile) {
        final var props = new Properties();
        try (final var input = new FileInputStream(propertyFile)) {
            props.load(input);
        } catch (IOException e1) {
            try (final var stream = MultiRunner.class.getClassLoader().getResourceAsStream(propertyFile)) {
                if (stream != null) {
                    props.load(stream);
                }
            } catch (IOException e2) {
                log.error("Failed to load properties: {}", propertyFile);
            }
        }
        return props;
    }

    /**
     * Performs display-specific u8g2 initialization.
     *
     * @param displayNum The unique ID of the display.
     * @param properties Configuration properties.
     * @return Native handle to initialized display.
     */
    public long setup(final int displayNum, final Properties properties) {
        final var keys = properties.stringPropertyNames();
        final var intMap = new HashMap<String, Integer>();
        final var strMap = new HashMap<String, String>();

        keys.forEach(key -> {
            final var split = key.split("\\.");
            try {
                if (Integer.parseInt(split[split.length - 1]) == displayNum) {
                    final var val = properties.getProperty(key);
                    if (pattern.matcher(val).matches()) {
                        intMap.put(split[0], Integer.parseInt(val));
                    } else {
                        strMap.put(split[0], val);
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore keys that don't end in a numeric display ID
            }
        });

        final var dType = DisplayType.valueOf(strMap.get("type"));
        typeMap.put(displayNum, dType);
        final var setupType = SetupType.valueOf(strMap.get("setup"));

        final var u8g2 = switch (dType) {
            case I2CHW ->
                display.initHwI2c(setupType, intMap.get("bus"), intMap.get("address"));
            case I2CSW ->
                display.initSwI2c(setupType, intMap.get("gpio"), intMap.get("scl"), intMap.get("sda"), U8X8_PIN_NONE, intMap.get(
                "delay"));
            case SPIHW ->
                display.initHwSpi(setupType, intMap.get("gpio"), intMap.get("bus"), intMap.get("dc"), intMap.get("reset"),
                U8X8_PIN_NONE, intMap.get("mode").shortValue(), intMap.get("speed").longValue());
            case SPISW ->
                display.initSwSpi(setupType, intMap.get("gpio"), intMap.get("dc"), intMap.get("reset"), intMap.get("mosi"), intMap.
                get("sck"), intMap.get("cs"), intMap.get("delay"));
            case SDL ->
                display.initSdl(setupType);
        };

        U8g2.setFont(u8g2, display.getFontPtr(FontType.valueOf(strMap.get("font"))));
        U8g2.setPowerSave(u8g2, 0);
        return u8g2;
    }

    /**
     * Executes the runner logic, managing threads and cleanup.
     *
     * @return Execution status code.
     * @throws InterruptedException if thread pool wait is interrupted.
     */
    @Override
    public Integer call() throws InterruptedException {
        final var properties = loadProperties(fileName);
        final var set = new HashSet<Integer>();
        properties.stringPropertyNames().forEach(key -> {
            final var parts = key.split("\\.");
            try {
                set.add(Integer.valueOf(parts[parts.length - 1]));
            } catch (NumberFormatException e) {
                // Skip non-numeric suffixes
            }
        });

        final var map = new TreeMap<Integer, Long>();
        set.forEach(id -> map.put(id, setup(id, properties)));

        final var executor = Executors.newFixedThreadPool(set.size());

        for (final var entry : map.entrySet()) {
            final var id = entry.getKey();
            final var u8 = entry.getValue();

            final DemoPlugin plugin = switch (random.nextInt(3)) {
                case 0 ->
                    new RaycastPlugin();
                case 1 ->
                    new PlasmaPlugin();
                default ->
                    new StarfieldPlugin();
            };

            // Priority: Property file specific > Command line argument > Default (30)
            final var displayFps = Integer.parseInt(properties.getProperty("fps." + id, String.valueOf(this.fps)));

            // Priority: Property file specific > Command line argument > Default (0)
            final var displayDuration = Long.parseLong(properties.getProperty("run.ms." + id, String.valueOf(this.runMs)));

            executor.execute(() -> {
                log.info("Starting display {} with plugin {} (FPS: {}, Duration: {}ms)",
                        id, plugin.getClass().getSimpleName(), displayFps, displayDuration);
                plugin.run(u8, display, displayFps, displayDuration);
            });
        }

        try {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, NANOSECONDS);
        } finally {
            map.forEach((id, u8g2) -> {
                U8g2.setPowerSave(u8g2, 1);
                if (typeMap.get(id) == SDL) {
                    U8g2.done(u8g2);
                } else {
                    display.done(u8g2);
                }
            });
            U8g2.doneI2c();
            U8g2.doneSpi();
        }
        return 0;
    }

    /**
     * Main entry point for CLI.
     *
     * @param args command line arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new MultiRunner())
                .registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode)
                .registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode)
                .registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode)
                .execute(args));
    }
}
