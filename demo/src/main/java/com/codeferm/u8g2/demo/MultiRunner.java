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
    private final Map<Integer, DisplayType> typeMap = new HashMap<>();

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
        properties.stringPropertyNames().forEach(key -> {
            final var split = key.split("\\.");
            set.add(Integer.valueOf(split[split.length - 1]));
        });
        return set;
    }

    public long setup(final int displayNum, final Properties properties) {
        final var intMap = new HashMap<String, Integer>();
        final var strMap = new HashMap<String, String>();
        properties.stringPropertyNames().forEach(key -> {
            final var split = key.split("\\.");
            if (Integer.parseInt(split[split.length - 1]) == displayNum) {
                final var val = properties.getProperty(key);
                if (pattern.matcher(val).matches()) {
                    intMap.put(split[0], Integer.parseInt(val));
                } else {
                    strMap.put(split[0], val);
                }
            }
        });

        final var dType = DisplayType.valueOf(strMap.get("type"));
        typeMap.put(displayNum, dType);
        final var setupType = SetupType.valueOf(strMap.get("setup"));

        long u8g2 = switch (dType) {
            case I2CHW -> display.initHwI2c(setupType, (short) intMap.get("bus").intValue(), (short) intMap.get("address").intValue());
            case SPIHW -> display.initHwSpi(setupType, (short) 1, (short) intMap.get("bus").intValue(), (short) intMap.get("dc").intValue(), (short) intMap.get("reset").intValue(), (short) U8X8_PIN_NONE, (short) 0, 1000000L);
            default -> throw new RuntimeException("Hardware only supported");
        };

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

        for (Integer id : set) {
            map.put(id, setup(id, properties));
            pluginNameMap.put(id, properties.getProperty("plugin." + id, "Plasma"));
            display.sleep(1000);
        }

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
            map.values().forEach(u8 -> {
                U8g2.setPowerSave(u8, 1);
                display.done(u8);
            });
            U8g2.doneI2c();
            U8g2.doneSpi();
        }
        return 0;
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new MultiRunner())
                .registerConverter(Integer.class, Integer::decode)
                .registerConverter(Integer.TYPE, Integer::decode)
                .execute(args));
    }
}
