/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.mmio;

import com.codeferm.periphery.Gpio;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT;
import com.codeferm.periphery.Mmio;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import picocli.CommandLine;

/**
 * GPIO performance using MMIO.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@CommandLine.Command(name = "Perf", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Show performance of MMIO based GPIO")
public class Perf implements Callable<Integer> {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Perf.class);
    /**
     * Input file.
     */
    @CommandLine.Option(names = {"-i", "--in"}, description = "Input property file name, ${DEFAULT-VALUE} by default.")
    private String inFileName = "duo-map.properties";
    /**
     * Device option.
     */
    @CommandLine.Option(names = {"-d", "--device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private int device = 0;
    /**
     * Line option.
     */
    @CommandLine.Option(names = {"-l", "--line"}, description = "GPIO line, ${DEFAULT-VALUE} by default.")
    private int line = 203;

    /**
     * Read pin value.
     *
     * @param pin Pin.
     * @return True = on, false = off.
     */
    public boolean read(final Pin pin) {
        final var value = new int[1];
        Mmio.mmioRead32(pin.getMmioHadle(), pin.getDataInOn().getOffset(), value);
        boolean ret;
        if ((value[0] & pin.getDataInOn().getMask()) == 0) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    /**
     * Write pin value.
     *
     * @param pin Pin.
     * @param value True = on, false = off.
     */
    public void write(final Pin pin, final boolean value) {
        final var reg = new int[1];
        final var dataOutOnOffset = pin.getDataOutOn().getOffset();
        final var dataOutOffOffset = pin.getDataOutOff().getOffset();
        if (!value) {
            // Get current register value
            Mmio.mmioRead32(pin.getMmioHadle(), dataOutOffOffset, reg);
            // If on and off registers are the same use AND
            if (dataOutOffOffset.equals(dataOutOnOffset)) {
                Mmio.mmioWrite32(pin.getMmioHadle(), dataOutOffOffset, reg[0] & pin.getDataOutOff().getMask());
            } else {
                // If on and off registers are different use OR like Raspberry Pi
                Mmio.mmioWrite32(pin.getMmioHadle(), dataOutOffOffset, reg[0] | pin.getDataOutOff().getMask());
            }
        } else {
            // Get current register value
            Mmio.mmioRead32(pin.getMmioHadle(), dataOutOnOffset, reg);
            Mmio.mmioWrite32(pin.getMmioHadle(), dataOutOnOffset, reg[0] | pin.getDataOutOn().getMask());
        }
    }

    /**
     * Performance test using GPIOD.
     *
     * @param pin Pin number.
     * @param samples How many samples to run.
     */
    public void perfGpiod(final Pin pin, final long samples) {
        try (final var gpio = new Gpio(String.format("/dev/gpiochip%d", pin.getKey().getChip()), pin.getKey().getPin(), GPIO_DIR_OUT)) {
            var handle = gpio.getHandle();
            logger.info(String.format("Running GPIOD write test with %d samples", samples));
            final var start = Instant.now();
            // Turn pin on and off, so we can see on a scope
            for (var i = 0; i < samples; i++) {
                Gpio.gpioWrite(handle, true);
                Gpio.gpioWrite(handle, false);
            }
            final var finish = Instant.now();
            // Elapsed milliseconds
            final var timeElapsed = Duration.between(start, finish).toMillis();
            logger.info(String.format("%.2f KHz", ((double) samples / (double) timeElapsed)));
        }
    }

    /**
     * Performance test using MMIO write method.
     *
     * @param pin Pin number.
     * @param samples How many samples to run.
     */
    public void perfGood(final Pin pin, final long samples) {
        try (final var gpio = new Gpio(String.format("/dev/gpiochip%d", pin.getKey().getChip()), pin.getKey().getPin(), GPIO_DIR_OUT)) {
            logger.info(String.format("Running good MMIO write test with %d samples", samples));
            final var start = Instant.now();
            // Turn pin on and off, so we can see on a scope
            for (var i = 0; i < samples; i++) {
                write(pin, true);
                write(pin, false);
            }
            final var finish = Instant.now();
            // Elapsed milliseconds
            final var timeElapsed = Duration.between(start, finish).toMillis();
            logger.info(String.format("%.2f KHz", ((double) samples / (double) timeElapsed)));
        }
    }

    /**
     * Performance test using raw MMIO and only reading register once before writes.
     *
     * @param pin Pin number.
     * @param samples How many samples to run.
     */
    public void perfBest(final Pin pin, final long samples) {
        try (final var gpio = new Gpio(String.format("/dev/gpiochip%d", pin.getKey().getChip()), pin.getKey().getPin(), GPIO_DIR_OUT)) {
            final var handle = pin.getMmioHadle();
            final var regOn = new int[1];
            final var dataOutOnOffset = pin.getDataOutOn().getOffset();
            // Only do read one time to get current value
            Mmio.mmioRead32(handle, dataOutOnOffset, regOn);
            final var regOff = new int[1];
            final var dataOutOffOffset = pin.getDataOutOff().getOffset();
            // Only do read one time to get current value
            Mmio.mmioRead32(handle, dataOutOffOffset, regOff);
            logger.info(String.format("Running best MMIO write test with %d samples", samples));
            final var start = Instant.now();
            // If on and off registers are the same use AND
            if (dataOutOffOffset.equals(dataOutOnOffset)) {
                final var on = regOff[0] | pin.getDataOutOn().getMask();
                final var off = regOff[0] & (pin.getDataOutOff().getMask());
                for (var i = 0; i < samples; i++) {
                    Mmio.mmioWrite32(handle, dataOutOnOffset, on);
                    Mmio.mmioWrite32(handle, dataOutOffOffset, off);
                }
            } else {
                // If on and off registers are different use OR like Raspberry Pi
                final var on = regOn[0] | pin.getDataOutOn().getMask();
                final var off = regOn[0] | pin.getDataOutOff().getMask();
                for (var i = 0; i < samples; i++) {
                    Mmio.mmioWrite32(handle, dataOutOnOffset, on);
                    Mmio.mmioWrite32(handle, dataOutOffOffset, off);
                }
            }
            final var finish = Instant.now();
            // Elapsed milliseconds
            final var timeElapsed = Duration.between(start, finish).toMillis();
            logger.info(String.format("%.2f KHz", ((double) samples / (double) timeElapsed)));
        }
    }

    /**
     * Read pin map properties and run performance test.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        final var file = new File();
        // Build pin Map
        final Map<PinKey, Pin> pinMap = file.loadPinMap(inFileName);
        // Make sure we have pins loaded
        if (!pinMap.isEmpty()) {
            // MMIO handle map based on GPIO dev key
            final Map<Integer, Long> mmioHandle = new HashMap<>();
            // Open MMIO for each chip
            for (int i = 0; i < file.getChips().size(); i++) {
                final var mmio = new Mmio(file.getChips().get(i), file.getMmioSize().get(i), file.getMemPath());
                mmioHandle.put(file.getGpioDev().get(i), mmio.getHandle());
            }
            // Set MMIO handle for each pin
            pinMap.entrySet().forEach((entry) -> {
                entry.getValue().setMmioHadle(mmioHandle.get(entry.getKey().getChip()));
            });
            final var pin = pinMap.get(new PinKey(device, line));
            perfGpiod(pin, 10000000);
            perfGood(pin, 10000000);
            perfBest(pin, 10000000);
            // Close all MMIO handles
            mmioHandle.entrySet().forEach((entry) -> {
                Mmio.mmioClose(entry.getValue());
            });
        } else {
            logger.error("Pin map empty. Make sure you have a valid property file.");
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
        System.exit(new CommandLine(new Perf()).execute(args));
    }
}
