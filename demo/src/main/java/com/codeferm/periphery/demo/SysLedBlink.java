/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.SysLed;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * LED blink using high-level SysLed wrapper.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "SysLedBlink", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Turn LED on and off using SysLed device class.")
@Slf4j
public class SysLedBlink implements Callable<Integer> {

    /**
     * Device name option.
     */
    @Option(names = {"-n", "--name"}, description = "System LED name, ${DEFAULT-VALUE} by default.")
    private String name = "nanopi:green:pwr";

    /**
     * Blink system LED.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception from sleep.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        try (final var sysLed = new SysLed(name)) {
            final var originalValue = sysLed.read();
            log.info("Blinking LED: {}", name);
            var i = 0;
            while (i < 10) {
                sysLed.write(true);
                TimeUnit.SECONDS.sleep(1);
                sysLed.write(false);
                TimeUnit.SECONDS.sleep(1);
                i++;
            }
            // Using Fluent API for debug logging
            log.atDebug().log("Restoring LED to original state: {}", originalValue);
            sysLed.write(originalValue);
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
        System.exit(new CommandLine(new SysLedBlink()).execute(args));
    }
}
