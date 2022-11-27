/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.Gpio;
import static com.codeferm.periphery.Gpio.GPIO_DIR_IN;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_BOTH;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_FALLING;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_RISING;
import static com.codeferm.periphery.Gpio.GPIO_POLL_EVENT;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Blocking event using button.
 *
 * Should work on any board with a button built in or wire one up. Just change device and line arguments as needed.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "ButtonWait", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Uses edge detection to wait for button press.")
public class ButtonWait implements Callable<Integer> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ButtonWait.class);
    /**
     * Device option.
     */
    @Option(names = {"-d", "--device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/gpiochip1";
    /**
     * Line option.
     */
    @Option(names = {"-l", "--line"}, description = "GPIO line, ${DEFAULT-VALUE} by default.")
    private int line = 3;

    /**
     * Use blocking edge detection.
     *
     * @return Exit code.
     */
    @Override
    public Integer call() {
        var exitCode = 0;
        try (final var gpio = new Gpio(device, line, GPIO_DIR_IN)) {
            final var edge = new int[1];
            final var timestamp = new long[1];
            Gpio.gpioSetEdge(gpio.getHandle(), GPIO_EDGE_BOTH);
            logger.info("Press button, stop pressing button for 10 seconds to exit");
            // Poll for event and timeout in 10 seconds if no event
            while (Gpio.gpioPoll(gpio.getHandle(), 10000) == GPIO_POLL_EVENT) {
                Gpio.gpioReadEvent(gpio.getHandle(), edge, timestamp);
                if (edge[0] == GPIO_EDGE_RISING) {
                    logger.info(String.format("Edge rising  [%8d.%9d]", timestamp[0] / 1000000000, timestamp[0] % 1000000000));
                } else if (edge[0] == GPIO_EDGE_FALLING) {
                    logger.info(String.format("Edge falling [%8d.%9d]", timestamp[0] / 1000000000, timestamp[0] % 1000000000));
                } else {
                    logger.info(String.format("Invalid edge %d, [%8d.%9d]", edge[0], timestamp[0] / 1000000000, timestamp[0]
                            % 1000000000));
                }
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
        System.exit(new CommandLine(new ButtonWait()).execute(args));
    }
}
