/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.BlockingButton;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Blocking event using button device class.
 *
 * This version uses the encapsulated BlockingButton device to handle edge detection
 * and thread-safe hardware access.
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
        logger.info("Starting ButtonWait on {} line {}", device, line);

        try (final var button = new BlockingButton(device, line)) {
            logger.info("Press button, stop pressing button for 10 seconds to exit");
            
            BlockingButton.ButtonEvent event;
            // Poll for event and timeout in 10 seconds if no event
            while ((event = button.waitForEvent(10000)) != null) {
                final var edgeStr = BlockingButton.edgeToString(event.edge());
                final var timestampStr = BlockingButton.formatTimestamp(event.timestamp());
                
                // Format matches the original logging requirements
                if (edgeStr.equals("Rising")) {
                    logger.info(String.format("Edge rising  [%s]", timestampStr));
                } else if (edgeStr.equals("Falling")) {
                    logger.info(String.format("Edge falling [%s]", timestampStr));
                } else {
                    logger.info(String.format("Invalid edge %d, [%s]", event.edge(), timestampStr));
                }
            }
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Main entry point.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new ButtonWait()).execute(args));
    }
}