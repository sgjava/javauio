/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.SerialDevice;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Serial loopback.
 *
 * Connect wire between RX and TX pins.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "SerialLoopback", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Send data between RX and TX pins.")
public class SerialLoopback implements Callable<Integer> {

    /**
     * Device option.
     */
    @Option(names = {"-d", "--device"}, description = "Serial device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/ttyS1";

    /**
     * Baud rate.
     */
    @Option(names = {"-b", "--baud"}, description = "Baud rate, ${DEFAULT-VALUE} by default.")
    private int baud = 115200;

    /**
     * Send data via loopback using SerialDevice.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        try (final var serialDevice = new SerialDevice(device, baud)) {
            // Log initialization at debug level using parameterized logging
            log.debug("Serial loopback initialized on {} at {} baud", device, baud);
            final var txBuf = new byte[128];
            // Change some data at beginning and end.
            txBuf[0] = (byte) 0xff;
            txBuf[127] = (byte) 0x80;         
            final var rxBuf = new byte[128];       
            // Perform loopback write and read
            serialDevice.write(txBuf);
            final var bytesRead = serialDevice.read(rxBuf, 2000);           
            if (bytesRead > 0) {
                log.info(String.format("%s, %s", 
                        SerialDevice.toHex(rxBuf[0]), 
                        SerialDevice.toHex(rxBuf[127])));
            } else {
                log.warn("No data read during loopback. Is the wire connected between RX and TX?");
            }
        } catch (final RuntimeException e) {
            log.error(e.getMessage());
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
        System.exit(new CommandLine(new SerialLoopback()).execute(args));
    }
}