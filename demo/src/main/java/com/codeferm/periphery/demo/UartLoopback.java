/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Uart;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * UART loopback demo using Uart device class.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "UartLoopback", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "UART loopback test (requires RX to TX jumper).")
@Slf4j
public class UartLoopback implements Callable<Integer> {

    @Option(names = {"-d", "--device"}, description = "Serial device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/ttyUSB0";

    @Option(names = {"-b", "--baud"}, description = "Baud rate, ${DEFAULT-VALUE} by default.")
    private int baud = 115200;

    @Override
    public Integer call() {
        var exitCode = 0;
        try (final var uart = new Uart(device, baud)) {
            final var testStr = "Hello Periphery!";
            final var tx = testStr.getBytes(StandardCharsets.UTF_8);
            final var rx = new byte[tx.length];

            log.info("Sending: '{}' to {}", testStr, device);
            uart.write(tx);
            
            // Wait up to 1 second for loopback data
            final var bytesRead = uart.read(rx, 1000);

            if (bytesRead > 0) {
                final var result = new String(rx, 0, bytesRead, StandardCharsets.UTF_8);
                log.info("Received: '{}'", result);
                
                if (testStr.equals(result)) {
                    log.info("Loopback successful!");
                } else {
                    log.warn("Data corruption detected.");
                }
            } else {
                log.error("No data received. Is the jumper connected?");
                exitCode = 1;
            }
        } catch (final RuntimeException e) {
            log.error("UART error: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    public static void main(final String... args) {
        System.exit(new CommandLine(new UartLoopback()).execute(args));
    }
}
