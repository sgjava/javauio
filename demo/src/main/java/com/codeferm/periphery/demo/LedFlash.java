/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.LedPwm;
import com.codeferm.periphery.Pwm;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Flash LED with PWM using high-level device abstraction.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "LedFlash", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Flash LED with PWM.")
public class LedFlash implements Callable<Integer> {

    /**
     * Chip option.
     */
    @Option(names = {"-d", "--device"}, description = "PWM device, ${DEFAULT-VALUE} by default.")
    private int chip = 0;
    /**
     * Line option.
     */
    @Option(names = {"-c", "--channel"}, description = "PWM pin, ${DEFAULT-VALUE} by default.")
    private int channel = 0;

    /**
     * Flash LED.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        // High-level LedPwm is AutoCloseable
        try (final var led = new LedPwm(chip, channel)) {
            log.info("Flash LED");
            led.enable();

            for (var i = 0; i < 10; i++) {
                // Fade in
                led.changeBrightness(1000, 0, 10, 100, 5000);
                // Fade out
                led.changeBrightness(1000, 1000, -10, 100, 5000);
            }

            // Clean up states before close
            Pwm.pwmSetPeriod(led.getHandle(), 0);
            led.disable();

        } catch (RuntimeException e) {
            log.error(e.getMessage());
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
        System.exit(new CommandLine(new LedFlash()).execute(args));
    }
}
