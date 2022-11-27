/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.Serial;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Command(name = "SerialLoopback", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Send data between RX and TX pins.")
public class SerialLoopback implements Callable<Integer> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SerialLoopback.class);
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
     * Send data via loopback.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        try (final var serial = new Serial(device, baud)) {
            final var txBuf = new byte[128];
            // Change some data at beginning and end.
            txBuf[0] = (byte) 0xff;
            txBuf[127] = (byte) 0x80;
            final var rxBuf = new byte[128];
            Serial.serialWrite(serial.getHandle(), txBuf, txBuf.length);
            Serial.serialRead(serial.getHandle(), rxBuf, rxBuf.length, 2000);
            logger.info(String.format("%02X, %02X", (short) rxBuf[0] & 0xff, (short) rxBuf[127] & 0xff));
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }
    
    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new SerialLoopback()).execute(args));
    }
}
