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
 * PWM LED Flash and Fade Demo using picocli.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "LedFlash", mixinStandardHelpOptions = true, version = "1.0.0",
        description = "Flash or fade an LED using hardware PWM.")
public class LedFlash implements Callable<Integer> {

    @Option(names = {"-c", "--chip"}, description = "PWM chip number", defaultValue = "0")
    private int chip;

    @Option(names = {"-n", "--channel"}, description = "PWM channel number", defaultValue = "0")
    private int channel;

    @Option(names = {"-p", "--period"}, description = "PWM period in ns", defaultValue = "1000")
    private int period;

    @Option(names = {"-s", "--startDc"}, description = "Starting duty cycle in ns", defaultValue = "0")
    private int startDc;

    @Option(names = {"-i", "--dcInc"}, description = "Duty cycle increment in ns", defaultValue = "10")
    private int dcInc;

    @Option(names = {"-t", "--count"}, description = "Number of times to loop", defaultValue = "100")
    private int count;

    @Option(names = {"-w", "--sleepTime"}, description = "Sleep time in microseconds", defaultValue = "5000")
    private int sleepTime;

    /**
     * Execution logic for the demo.
     *
     * @return Exit code.
     * @throws Exception on error.
     */
    @Override
    public Integer call() throws Exception {
        log.info("Starting PWM LED Demo on chip {}, channel {}", chip, channel);
        try (PwmLed pwmLed = new PwmLed(chip, channel)) {
            pwmLed.enable();

            var dutyCycle = startDc;
            // Looping logic handled at the application (demo) level
            for (int i = 0; i < count; i++) {
                // Update hardware via thread-safe call
                pwmLed.changeBrightness(period, dutyCycle);
                
                TimeUnit.MICROSECONDS.sleep(sleepTime);
                dutyCycle += dcInc;
            }
            pwmLed.disable();
            log.info("Demo finished successfully.");
        } catch (Exception e) {
            log.error("PWM Demo failed: {}", e.getMessage());
            return 1;
        }
        return 0;
    }

    /**
     * Main entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        final int exitCode = new CommandLine(new LedFlash()).execute(args);
        System.exit(exitCode);
    }
}