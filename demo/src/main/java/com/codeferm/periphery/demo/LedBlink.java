/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import static com.codeferm.periphery.Common.cString;
import com.codeferm.periphery.Gpio;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT;
import static com.codeferm.periphery.Gpio.GPIO_DRIVE_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_NONE;
import com.codeferm.periphery.Gpio.GpioConfig;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Blink LED.
 *
 * Using the NanoPi Duo connect a 220Ω resistor to ground, then the resistor to the cathode (the short pin) of the LED. Connect the
 * anode (the long pin) of the LED to line 203 (IOG11).
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "LedBlink", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Turn LED on and off.")
public class LedBlink implements Callable<Integer> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(LedBlink.class);
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
     * Blink LED.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        try (final var gpio = new Gpio(device, line, new GpioConfig().setBias(GPIO_BIAS_DEFAULT).setDirection(GPIO_DIR_OUT).
                setDrive(GPIO_DRIVE_DEFAULT).setEdge(GPIO_EDGE_NONE).setInverted(false).setLabel(cString(LedBlink.class.
                getSimpleName())))) {
            logger.info("Blinking LED");
            var i = 0;
            while (i < 10) {
                Gpio.gpioWrite(gpio.getHandle(), true);
                TimeUnit.SECONDS.sleep(1);
                Gpio.gpioWrite(gpio.getHandle(), false);
                TimeUnit.SECONDS.sleep(1);
                i++;
            }
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
        System.exit(new CommandLine(new LedBlink()).execute(args));
    }
}
