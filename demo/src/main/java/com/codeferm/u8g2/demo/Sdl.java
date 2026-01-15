/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

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
public class Sdl extends Base {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Sdl.class);

    /**
     * Run demo.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */    
    @Override
    public Integer call() throws InterruptedException {
        int exitCode = 1;
        // This will setup display
        if (getType().equals(DisplayType.SDL)) {
            exitCode = super.call();
            showText("SDL test");
            done();
        } else {
            logger.error("Only SDL display type supported");
        }
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new Sdl()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
