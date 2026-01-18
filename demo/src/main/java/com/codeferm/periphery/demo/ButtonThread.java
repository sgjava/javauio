/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.BlockingButton;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Blocking event using button. A thread is used, so other processing can occur.
 *
 * This version uses the encapsulated BlockingButton device to handle edge detection and thread-safe hardware access.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "ButtonThread", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Uses edge detection to wait for button press while other processing occurs.")
public class ButtonThread implements Callable<Integer> {

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
     * Wait for edge thread.
     *
     * @param executor Executor service.
     */
    public void executeWaitForEdge(final ExecutorService executor) {
        // Execute lambda using BlockingButton device
        executor.execute(() -> {
            try (final var button = new BlockingButton(device, line)) {
                log.info("Press button, stop pressing button for 10 seconds to exit");
                BlockingButton.ButtonEvent event;
                // Use the device class poll/read abstraction with a 10s timeout
                while ((event = button.waitForEvent(10000)) != null) {
                    final var edgeStr = BlockingButton.edgeToString(event.edge());
                    final var timestampStr = BlockingButton.formatTimestamp(event.timestamp());
                    if (edgeStr.equals("Rising")) {
                        log.info(String.format("Edge rising  [%s]", timestampStr));
                    } else if (edgeStr.equals("Falling")) {
                        log.info(String.format("Edge falling [%s]", timestampStr));
                    } else {
                        log.info(String.format("Invalid edge %d, [%s]", event.edge(), timestampStr));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Use blocking edge detection in a separate thread.
     *
     * @return Exit code.
     */
    @Override
    public Integer call() {
        var exitCode = 0;
        final var executor = Executors.newSingleThreadExecutor();
        executeWaitForEdge(executor);
        try {
            // Initiate shutdown so the main loop can monitor completion
            executor.shutdown();
            int count = 0;
            while (count <= 30 && !executor.isTerminated()) {
                log.info("Main program doing stuff, press button");
                TimeUnit.SECONDS.sleep(1);
                count++;
            }
            // Wait for thread to finish if it hasn't timed out yet
            if (!executor.isTerminated()) {
                log.info("Waiting for thread to finish");
                executor.awaitTermination(Long.MAX_VALUE, NANOSECONDS);
            }
        } catch (InterruptedException e) {
            log.error("Tasks interrupted");
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdownNow();
        }
        return exitCode;
    }

    /**
     * Main entry point.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new ButtonThread()).execute(args));
    }
}
