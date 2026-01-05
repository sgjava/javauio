/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.all.demo;

import static com.codeferm.periphery.Common.cString;
import com.codeferm.periphery.Gpio;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT;
import static com.codeferm.periphery.Gpio.GPIO_DRIVE_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_NONE;
import static com.codeferm.periphery.Gpio.GPIO_EVENT_CLOCK_REALTIME;
import com.codeferm.u8g2.demo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Use mono display and GPIO.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "LedDisplay", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Blink LED and show status on mono display")
public class LedDisplay extends Base {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(LedDisplay.class);

    /**
     * Device option.
     */
    @CommandLine.Option(names = {"-d", "--device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/gpiochip0";
    /**
     * Line option.
     */
    @CommandLine.Option(names = {"-l", "--line"}, description = "GPIO line, ${DEFAULT-VALUE} by default.")
    private int line = 203;

    /**
     * Simple text display.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        // This will setup display
        var exitCode = super.call();
        // 1 second delay after screen draw
        setSleep(1000);
        try (final var gpio = new Gpio(device, line, Gpio.GpioConfig.builder().bias(GPIO_BIAS_DEFAULT).direction(GPIO_DIR_OUT).
                drive(GPIO_DRIVE_DEFAULT).edge(GPIO_EDGE_NONE).inverted(false).label(cString(LedDisplay.class.getSimpleName())).
                event_clock(GPIO_EVENT_CLOCK_REALTIME).debounce_us(0).build())) {
            logger.info("Blinking LED");
            var i = 0;
            while (i < 10) {
                Gpio.gpioWrite(gpio.getHandle(), true);
                showText("LED on");
                Gpio.gpioWrite(gpio.getHandle(), false);
                showText("LED off");
                i++;
            }
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            exitCode = 1;
        }
        done();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new LedDisplay()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
