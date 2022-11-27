/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Display;
import static com.codeferm.u8g2.Fonts.u8g2_font_t0_15b_mf;
import com.codeferm.u8g2.U8g2;
import static com.codeferm.u8g2.demo.Base.DisplayType.SDL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Simple text demo.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "SDL", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "SDL demo")
public class Sdl implements Callable<Integer> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Sdl.class);
    /**
     * Type allows hardware and software I2C and SPI.
     */
    @CommandLine.Option(names = {"--type"}, description = "Type of display, ${DEFAULT-VALUE} by default.")
    private Base.DisplayType type = SDL;

    public long setup(final Display display) {
        final var display1 = display.initSdl();
        display.sleep(2000);
        U8g2.clearBuffer(display1);
        U8g2.setFont(display1, u8g2_font_t0_15b_mf);
        return display1;
    }

    /**
     * Simple text display.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        final var display = new Display();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    final var display1 = setup(display);
                    U8g2.drawStr(display1, 1, 18, Long.toString(display1));
                    U8g2.sendBuffer(display1);
                    display.sleep(5000);
                    U8g2.setPowerSave(display1, 0);
                }
            });
        }
        try {
            // Initiate shutdown
            executorService.shutdown();
            // Wait for threads to finish
            if (!executorService.isTerminated()) {
                logger.info("Waiting for threads to finish");
                executorService.awaitTermination(Long.MAX_VALUE, NANOSECONDS);
            }
        } catch (InterruptedException e) {
            logger.error("Tasks interrupted");
        } finally {
            executorService.shutdownNow();
        }
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new Sdl()).registerConverter(Byte.class,
                Byte::decode).registerConverter(Byte.TYPE,
                        Byte::decode).registerConverter(Short.class,
                        Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class,
                        Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class,
                        Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
