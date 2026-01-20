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

@Slf4j
@Command(name = "MultiRunner", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class MultiRunner implements Callable<Integer> {

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    @Option(names = {"-f", "--file"}, description = "Property file", defaultValue = "display.properties")
    private String fileName;

    private final Display display = new Display();
    private final HashMap<Integer, DisplayType> typeMap = new HashMap<>();

    public Properties loadProperties(final String propertyFile) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(propertyFile));
        } catch (IOException e1) {
            try (final var stream = MultiRunner.class.getClassLoader().getResourceAsStream(propertyFile)) {
                props.load(stream);
            } catch (IOException e2) {
                throw new RuntimeException("No properties found", e2);
            }
        }
        return props;
    }

    public Set<Integer> getDisplays(final Properties properties) {
        final var set = new HashSet<Integer>();
        properties.stringPropertyNames().stream().map(key -> key.split("\\.")).forEachOrdered(split -> {
            set.add(Integer.valueOf(split[split.length - 1]));
        });
        return set;
    }

    public long setup(final int displayNum, final Properties properties) {
        final var keys = properties.stringPropertyNames();
        final var intMap = new HashMap<String, Integer>();
        final var strMap = new HashMap<String, String>();
        keys.forEach(key -> {
            final var split = key.split("\\.");
            if (Integer.parseInt(split[split.length - 1]) == displayNum) {
                if (pattern.matcher(properties.getProperty(key)).matches()) {
                    intMap.put(split[0], Integer.parseInt(properties.getProperty(key)));
                } else {
                    strMap.put(split[0], properties.getProperty(key));
                }
            }
        });

        typeMap.put(displayNum, DisplayType.valueOf(strMap.get("type")));
        long u8g2 = 0;
        final var setupType = SetupType.valueOf(strMap.get("setup"));
        
        switch (typeMap.get(displayNum)) {
            case I2CHW -> u8g2 = display.initHwI2c(setupType, intMap.get("bus"), intMap.get("address"));
            case I2CSW -> u8g2 = display.initSwI2c(setupType, intMap.get("gpio"), intMap.get("scl"), intMap.get("sda"), U8X8_PIN_NONE, intMap.get("delay"));
            case SPIHW -> u8g2 = display.initHwSpi(setupType, intMap.get("gpio"), intMap.get("bus"), intMap.get("dc"), intMap.get("reset"), U8X8_PIN_NONE, intMap.get("mode").shortValue(), intMap.get("speed").longValue());
            case SPISW -> u8g2 = display.initSwSpi(setupType, intMap.get("gpio"), intMap.get("dc"), intMap.get("reset"), intMap.get("mosi"), intMap.get("sck"), intMap.get("cs"), intMap.get("delay"));
            case SDL -> u8g2 = display.initSdl(setupType);
            default -> throw new RuntimeException(String.format("%s is not a valid type", strMap.get("setup")));
        }

        U8g2.setFont(u8g2, display.getFontPtr(FontType.valueOf(strMap.get("font"))));
        U8g2.setPowerSave(u8g2, 0);
        return u8g2;
    }

    @Override
    public Integer call() throws InterruptedException {
        final var properties = loadProperties(fileName);
        final var set = getDisplays(properties);
        final var map = new TreeMap<Integer, Long>();
        final var pluginNameMap = new HashMap<Integer, String>();

        set.forEach(displayNum -> {
            map.put(displayNum, setup(displayNum, properties));
            // Fixed the property lookup key for the plugin name
            pluginNameMap.put(displayNum, properties.getProperty("plugin." + displayNum, "Plasma"));
            display.sleep(2000);
        });

        final var executor = Executors.newFixedThreadPool(set.size());
        for (Map.Entry<Integer, Long> entry : map.entrySet()) {
            final var id = entry.getKey();
            final var u8 = entry.getValue();
            final DemoPlugin plugin = switch (pluginNameMap.get(id)) {
                case "Raycast" -> new RaycastPlugin();
                default -> new PlasmaPlugin();
            };
            executor.execute(() -> plugin.run(u8, U8g2.getDisplayWidth(u8), U8g2.getDisplayHeight(u8), display));
        }

        try {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, NANOSECONDS);
        } finally {
            executor.shutdownNow();
            map.forEach((displayNum, u8g2) -> {
                U8g2.setPowerSave(u8g2, 1);
                if (typeMap.get(displayNum) == SDL) {
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

    public static void main(String... args) {
        System.exit(new CommandLine(new MultiRunner())
                .registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode)
                .registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode)
                .registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode)
                .execute(args));
    }
}
