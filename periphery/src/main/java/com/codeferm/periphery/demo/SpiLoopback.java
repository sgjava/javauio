/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.Spi;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * SPI loopback.
 *
 * Connect wire between MOSI and MISO pins.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "SpiLoopback", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Send data between MOSI and MISO pins.")
public class SpiLoopback implements Callable<Integer> {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(SpiLoopback.class);
    /**
     * Device option.
     */
    @Option(names = {"-d", "--device"}, description = "SPI device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/spidev1.0";

    /**
     * Send data via loopback.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        try (final var spi = new Spi(device, 0, 500000)) {
            final var txBuf = new byte[128];
            // Change some data at beginning and end.
            txBuf[0] = (byte) 0xff;
            txBuf[127] = (byte) 0x80;
            final var rxBuf = new byte[128];
            Spi.spiTransfer(spi.getHandle(), txBuf, rxBuf, txBuf.length);
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
        System.exit(new CommandLine(new SpiLoopback()).execute(args));
    }    
}
