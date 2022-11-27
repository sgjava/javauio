/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.Pwm;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Flash LED with PWM.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "LedFlash", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Flash LED with PWM.")
public class LedFlash implements Callable<Integer> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(LedFlash.class);
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
     * Gradually increase and decrease LED brightness.
     *
     * @param handle Valid pointer to an allocated LED handle structure.
     * @param period Set the period in seconds of the PWM.
     * @param startDc Starting duty cycle in nanoseconds.
     * @param dcInc Duty cycle increment in nanoseconds.
     * @param count Number of times to loop.
     * @param sleepTime Sleep time in microseconds.
     * @throws InterruptedException Possible exception.
     */
    public void changeBrightness(final long handle, final int period, final int startDc, final int dcInc, final int count,
            final int sleepTime) throws InterruptedException {
        Pwm.pwmSetPeriodNs(handle, period);
        var dutyCycle = startDc;
        var i = 0;
        while (i < count) {
            Pwm.pwmSetDutyCycleNs(handle, dutyCycle);
            TimeUnit.MICROSECONDS.sleep(sleepTime);
            dutyCycle += dcInc;
            i += 1;
        }
    }

    /**
     * Flash LED.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        try (final var pwm = new Pwm(chip, channel)) {
            logger.info("Flash LED");
            Pwm.pwmEnable(pwm.getHandle());
            for (var i = 0; i < 10; i++) {
                changeBrightness(pwm.getHandle(), 1000, 0, 10, 100, 5000);
                changeBrightness(pwm.getHandle(), 1000, 1000, -10, 100, 5000);
            }
            Pwm.pwmSetDutyCycleNs(pwm.getHandle(), 0);
            Pwm.pwmSetPeriod(pwm.getHandle(), 0);
            Pwm.pwmDisable(pwm.getHandle());
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
        System.exit(new CommandLine(new LedFlash()).execute(args));
    }    
}
