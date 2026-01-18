/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.PwmLed;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Flash LED with PWM using the high-level PwmLed class. This demo manages the brightness ramp loops at the application level.
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
     * PWM chip/device option.
     */
    @Option(names = {"-d", "--device"}, description = "PWM device, ${DEFAULT-VALUE} by default.", defaultValue = "0")
    private int chip;
    /**
     * PWM channel/pin option.
     */
    @Option(names = {"-c", "--channel"}, description = "PWM pin, ${DEFAULT-VALUE} by default.", defaultValue = "0")
    private int channel;

    /**
     * Gradually increase or decrease LED brightness by iterating through duty cycle steps.
     *
     * @param pwmLed The thread-safe PwmLed instance.
     * @param period The PWM period in nanoseconds.
     * @param startDc The starting duty cycle in nanoseconds.
     * @param dcInc The amount to increment/decrement the duty cycle per step.
     * @param count The number of steps in the ramp.
     * @param sleepTime The time to wait between steps in microseconds.
     * @throws InterruptedException If the sleep is interrupted.
     */
    public void changeBrightness(final PwmLed pwmLed, final int period, final int startDc, final int dcInc,
            final int count, final int sleepTime) throws InterruptedException {
        var dutyCycle = startDc;
        for (int i = 0; i < count; i++) {
            // Update hardware via high-level PwmLed method
            pwmLed.changeBrightness(period, dutyCycle);
            TimeUnit.MICROSECONDS.sleep(sleepTime);
            dutyCycle += dcInc;
        }
    }

    /**
     * Main execution logic for the LED flash demo.
     *
     * @return Exit code (0 for success, 1 for failure).
     */
    @Override
    public Integer call() {
        var exitCode = 0;
        // Use PwmLed high-level class for thread-safe hardware access
        try (final var pwmLed = new PwmLed(chip, channel)) {
            log.info("Starting LED Flash Demo on chip {}, channel {}", chip, channel);
            pwmLed.enable();

            // Repeat the fade up/down sequence 10 times
            for (var i = 0; i < 10; i++) {
                // Fade up: start at 0, increment by 10, 100 steps
                changeBrightness(pwmLed, 1000, 0, 10, 100, 5000);
                // Fade down: start at 1000, decrement by 10, 100 steps
                changeBrightness(pwmLed, 1000, 1000, -10, 100, 5000);
            }
            pwmLed.disable();
            log.info("LED Flash Demo completed successfully.");
        } catch (Exception e) {
            log.error("PWM Demo failed: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Main entry point for the application.
     *
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        final int exitCode = new CommandLine(new LedFlash()).execute(args);
        System.exit(exitCode);
    }
}
