/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.SpiBus;
import java.util.Arrays;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * SPI loopback demo using SpiBus.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "SpiLoopback", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "SPI loopback test (requires MISO to MOSI jumper).")
public class SpiLoopback implements Callable<Integer> {

    @Option(names = {"-d", "--device"}, description = "SPI device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/spidev1.0";

    @Option(names = {"-m", "--mode"}, description = "SPI mode, ${DEFAULT-VALUE} by default.")
    private int mode = 0;

    @Option(names = {"-s", "--speed"}, description = "Max speed in Hz, ${DEFAULT-VALUE} by default.")
    private int speed = 500000;

    @Override
    public Integer call() {
        var exitCode = 0;
        try (final var spiBus = new SpiBus(device, mode, speed)) {
            final var tx = new byte[]{0x01, 0x02, 0x03, 0x04, (byte) 0xff};
            final var rx = new byte[tx.length];

            log.info("Starting SPI loopback on {}...", device);
            log.atDebug().log("Bus status: {}", spiBus.toString());
            // Perform hardware transfer
            spiBus.transfer(tx, rx, tx.length);
            log.info("TX: {}", Arrays.toString(tx));
            log.info("RX: {}", Arrays.toString(rx));
            if (Arrays.equals(tx, rx)) {
                log.info("Loopback check passed!");
            } else {
                log.warn("Loopback check failed! Jumper MISO and MOSI.");
            }
        } catch (final RuntimeException e) {
            log.error("SPI demo failed: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    public static void main(final String... args) {
        System.exit(new CommandLine(new SpiLoopback()).execute(args));
    }
}
