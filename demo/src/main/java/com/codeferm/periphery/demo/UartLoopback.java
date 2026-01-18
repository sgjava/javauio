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
 * UART loopback demo using the high-level Uart device class. This demo requires a physical jumper connecting RX to TX on the
 * specified serial device. It demonstrates thread-safe writing and reading using the picocli framework for command-line
 * configuration.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "UartLoopback", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "UART loopback test (requires RX to TX jumper).")
public class UartLoopback implements Callable<Integer> {

    /**
     * Serial device path option.
     */
    @Option(names = {"-d", "--device"}, description = "Serial device, ${DEFAULT-VALUE} by default.",
            defaultValue = "/dev/ttyS1")
    private String device;
    /**
     * Baud rate option.
     */
    @Option(names = {"-b", "--baud"}, description = "Baud rate, ${DEFAULT-VALUE} by default.",
            defaultValue = "115200")
    private int baud;

    /**
     * Executes the loopback test logic. Initializes the UART device, sends a test string, and attempts to read it back within a
     * 1-second timeout.
     *
     * @return Exit code (0 for success, 1 for failure or data mismatch).
     */
    @Override
    public Integer call() {
        var exitCode = 0;
        // The Uart class is thread-safe and encapsulates the hardware handle
        try (final var uart = new Uart(device, baud)) {
            final var testStr = "Hello Periphery!";
            final var tx = testStr.getBytes(StandardCharsets.UTF_8);
            final var rx = new byte[tx.length];
            log.info("Sending: '{}' to {}", testStr, device);
            // Thread-safe write operation
            uart.write(tx);
            // Wait up to 1 second for loopback data to arrive
            // Thread-safe read operation
            final var bytesRead = uart.read(rx, 1000);
            if (bytesRead > 0) {
                final var result = new String(rx, 0, bytesRead, StandardCharsets.UTF_8);
                log.info("Received: '{}'", result);

                if (testStr.equals(result)) {
                    log.info("Loopback successful!");
                } else {
                    log.warn("Data corruption detected (mismatch).");
                    exitCode = 1;
                }
            } else {
                log.error("No data received. Is the jumper connected between RX and TX?");
                exitCode = 1;
            }
        } catch (final RuntimeException e) {
            log.error("UART error during demo: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Main entry point for the UART loopback application. Uses picocli CommandLine to parse arguments and handle help/version
     * requests.
     *
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new UartLoopback()).execute(args));
    }
}
