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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(Perf.class);
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
     * How many samples to run.
     */
    @CommandLine.Option(names = {"-s", "--samples"}, description = "Samples to run, ${DEFAULT-VALUE} by default.")
    private int samples = 10000000;
    /**
     * Run fast only.
     */
    @CommandLine.Option(names = {"-f", "--fast"}, description = "Fast test only, ${DEFAULT-VALUE} by default.")
    private boolean fast = true;

    /**
     * Read pin value.
     *
     * @param pin Pin.
     * @return True = on, false = off.
     */
    public boolean read(final Pin pin) {
        final var value = new int[1];
        Mmio.mmioRead32(pin.mmioHadle(), pin.dataInOn().offset(), value);
        boolean ret;
        if ((value[0] & pin.dataInOn().mask()) == 0) {
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
        final var dataOutOnOffset = pin.dataOutOn().offset();
        final var dataOutOffOffset = pin.dataOutOff().offset();
        if (!value) {
            // Get current register value
            Mmio.mmioRead32(pin.mmioHadle(), dataOutOffOffset, reg);
            // If on and off registers are the same use AND
            if (dataOutOffOffset.equals(dataOutOnOffset)) {
                Mmio.mmioWrite32(pin.mmioHadle(), dataOutOffOffset, reg[0] & pin.dataOutOff().mask());
            } else {
                // If on and off registers are different use OR like Raspberry Pi
                Mmio.mmioWrite32(pin.mmioHadle(), dataOutOffOffset, reg[0] | pin.dataOutOff().mask());
            }
        } else {
            // Get current register value
            Mmio.mmioRead32(pin.mmioHadle(), dataOutOnOffset, reg);
            Mmio.mmioWrite32(pin.mmioHadle(), dataOutOnOffset, reg[0] | pin.dataOutOn().mask());
        }
    }

    /**
     * Performance test using GPIOD.
     *
     * @param pin Pin number.
     * @param samples How many samples to run.
     */
    public void perfGpiod(final Pin pin, final long samples) {
        try (final var gpio = new Gpio(String.format("/dev/gpiochip%d", pin.key().chip()), pin.key().pin(), GPIO_DIR_OUT)) {
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
        try (final var gpio = new Gpio(String.format("/dev/gpiochip%d", pin.key().chip()), pin.key().pin(), GPIO_DIR_OUT)) {
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
        try (final var gpio = new Gpio(String.format("/dev/gpiochip%d", pin.key().chip()), pin.key().pin(), GPIO_DIR_OUT)) {
            final var handle = pin.mmioHadle();
            final var regOn = new int[1];
            final var dataOutOnOffset = pin.dataOutOn().offset();
            // Only do read one time to get current value
            Mmio.mmioRead32(handle, dataOutOnOffset, regOn);
            final var regOff = new int[1];
            final var dataOutOffOffset = pin.dataOutOff().offset();
            // Only do read one time to get current value
            Mmio.mmioRead32(handle, dataOutOffOffset, regOff);
            logger.info(String.format("Running best MMIO write test with %d samples", samples));
            final var start = Instant.now();
            // If on and off registers are the same use AND
            if (dataOutOffOffset.equals(dataOutOnOffset)) {
                final var on = regOff[0] | pin.dataOutOn().mask();
                final var off = regOff[0] & (pin.dataOutOff().mask());
                for (var i = 0; i < samples; i++) {
                    Mmio.mmioWrite32(handle, dataOutOnOffset, on);
                    Mmio.mmioWrite32(handle, dataOutOffOffset, off);
                }
            } else {
                // If on and off registers are different use OR like Raspberry Pi
                final var on = regOn[0] | pin.dataOutOn().mask();
                final var off = regOn[0] | pin.dataOutOff().mask();
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
            for (int i = 0; i < file.chips().size(); i++) {
                final var mmio = new Mmio(file.chips().get(i), file.mmioSize().get(i), file.memPath());
                mmioHandle.put(file.gpioDev().get(i), mmio.getHandle());
            }
            // Set MMIO handle for each pin
            pinMap.entrySet().forEach((entry) -> {
                entry.getValue().mmioHadle(mmioHandle.get(entry.getKey().chip()));
            });
            final var pin = pinMap.get(new PinKey(device, line));
            // Run slower tests?
            if (!fast) {
                perfGpiod(pin, samples);
                perfGood(pin, samples);
            }
            perfBest(pin, samples);
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
