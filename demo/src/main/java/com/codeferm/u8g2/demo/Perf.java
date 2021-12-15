/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.U8g2;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * sendBuffer performance.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "Perf", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "U8g2 buffer write performance demo")
public class Perf extends Base {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Perf.class);
    /**
     * Line option.
     */
    @CommandLine.Option(names = {"--samples"}, description = "Samples, ${DEFAULT-VALUE} by default.")
    private int samples = 1000;

    /**
     * Calculate sendBuffer FPS.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        // This will setup display
        var exitCode = super.call();
        final var u8g2 = getU8g2();
        final var start = Instant.now();
        logger.info(String.format("Timing %d sendBuffer", samples));
        for (var i = 0; i < samples; i++) {
            U8g2.sendBuffer(u8g2);
        }
        final var finish = Instant.now();
        // Elapsed milliseconds
        final var timeElapsed = Duration.between(start, finish).toSeconds();
        logger.info(String.format("%.2f sendBuffer per second", ((double) samples / (double) timeElapsed)));
        done();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new Perf()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
