/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.GpioLed;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Blink LED using high-level device abstraction.
 *
 * Using the NanoPi Duo connect a 220Ω resistor to ground, then the resistor to the cathode (the short pin) of the LED. Connect the
 * anode (the long pin) of the LED to line 203 (IOG11).
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "LedBlink", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Blink LED using com.codeferm.periphery.device.Led")
public class LedBlink implements Callable<Integer> {

    /**
     * Device option.
     */
    @Option(names = {"-d", "--device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/gpiochip0";

    /**
     * Line option.
     */
    @Option(names = {"-l", "--line"}, description = "GPIO line, ${DEFAULT-VALUE} by default.")
    private int line = 203;

    /**
     * Blink LED using high-level GpioLed device class.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        // Using var with try-with-resources for the high-level GpioLed device
        try (var led = new GpioLed(device, line)) {
            log.info("Blinking LED on {} line {}", device, line);

            for (var i = 0; i < 10; i++) {
                log.debug("Cycle {}: LED ON", i);
                led.on();
                TimeUnit.SECONDS.sleep(1);

                log.debug("Cycle {}: LED OFF", i);
                led.off();
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            log.error("Failed to operate LED: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Main entry point using picocli.
     *
     * @param args Argument list.
     */
    public static void main(String[] args) {
        var cmd = new CommandLine(new LedBlink());
        System.exit(cmd.execute(args));
    }
}
