/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Adxl345;
import com.codeferm.periphery.device.I2cBus;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * ADXL345 demo using high-level device class and thread-safe bus.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Adxl345Demo", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Read ADXL345 data using Adxl345 device class.")
public class Adxl345Demo implements Callable<Integer> {

    /**
     * I2C device path.
     */
    @Option(names = {"-d", "--device"}, description = "I2C device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/i2c-0";
    /**
     * I2C address of ADXL345.
     */
    @Option(names = {"-a", "--address"}, description = "Address, ${DEFAULT-VALUE} by default.")
    private short address = 0x53;

    /**
     * Execution logic for the demo.
     *
     * @return Exit code.
     * @throws InterruptedException If sleep is interrupted.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        // I2cBus is AutoCloseable, Adxl345 is AutoCloseable
        try (final var i2cBus = new I2cBus(device); final var adxl = new Adxl345(i2cBus, address)) {
            log.atDebug().log("Checking device ID at address 0x{}", Integer.toHexString(address));
            // Validate device is present
            if (adxl.getDeviceId() == 0xe5) {
                log.info("ADXL345 detected. Initializing...");
                // Set power mode and defaults
                adxl.enable();
                log.info("Range: {}, Data Rate: {}", adxl.getRange(), adxl.getDataRate());
                log.info("Starting 100 sample collection (0.5s intervals)...");
                for (var i = 0; i < 100; i++) {
                    // Read scaled data (m/s^2)
                    final var data = adxl.read();
                    log.info(String.format("Sample %3d - x: %+5.2f, y: %+5.2f, z: %+5.2f",
                            i, data.get("x"), data.get("y"), data.get("z")));
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            } else {
                log.error("Device ID mismatch. Expected 0xE5, got 0x{}",
                        Integer.toHexString(adxl.getDeviceId()));
            }
        } catch (final RuntimeException e) {
            log.error("Runtime error: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Main entry point.
     *
     * @param args Argument list.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Adxl345Demo())
                .registerConverter(Short.class, Short::decode)
                .registerConverter(Short.TYPE, Short::decode)
                .execute(args));
    }
}
