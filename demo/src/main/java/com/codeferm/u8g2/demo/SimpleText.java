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
@Command(name = "SimpleText", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Simple text demo")
public class SimpleText extends Base {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SimpleText.class);

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
        showText("This is a test with a string too wide.");
        done();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new SimpleText()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
