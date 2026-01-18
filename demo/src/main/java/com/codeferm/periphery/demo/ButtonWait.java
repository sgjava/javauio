/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.BlockingButton;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Blocking event using button device class.
 *
 * This version uses the encapsulated BlockingButton device to handle edge detection and thread-safe hardware access.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "ButtonWait", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Uses edge detection to wait for button press.")
public class ButtonWait implements Callable<Integer> {

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
        log.info("Starting ButtonWait on {} line {}", device, line);
        try (final var button = new BlockingButton(device, line)) {
            log.info("Press button, stop pressing button for 10 seconds to exit");
            BlockingButton.ButtonEvent event;
            // Poll for event and timeout in 10 seconds if no event
            while ((event = button.waitForEvent(10000)) != null) {
                final var edgeStr = BlockingButton.edgeToString(event.edge());
                final var timestampStr = BlockingButton.formatTimestamp(event.timestamp());
                // Format matches the original logging requirements
                switch (edgeStr) {
                    case "Rising" ->
                        log.info(String.format("Edge rising  [%s]", timestampStr));
                    case "Falling" ->
                        log.info(String.format("Edge falling [%s]", timestampStr));
                    default ->
                        log.info(String.format("Invalid edge %d, [%s]", event.edge(), timestampStr));
                }
            }
        } catch (RuntimeException e) {
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
    public static void main(String... args) {
        System.exit(new CommandLine(new ButtonWait()).execute(args));
    }
}
