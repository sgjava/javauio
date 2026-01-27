/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.ssd1331.demo;

import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * SSD1331 Demo using BufferedImage. Refactored to extend Ssd1331Base.
 *
 * @author Steven P. Goldsmith
 * @version 1.1.1
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Ssd1331BufImage", mixinStandardHelpOptions = true)
public class Ssd1331BufImage extends Ssd1331Base {

    /**
     * Demo execution logic.
     * Uses var inference and final modifiers to follow established standards.
     *
     * @return Exit code.
     * @throws Exception Hardware or timing exception.
     */
    @Override
    public Integer call() throws Exception {
        // super.call() initializes the hardware and caches width/height
        super.call();
        log.info("Starting SSD1331 Demo");
        // Accessing cached dimensions from Base fields
        final var w = getWidth();
        final var h = getHeight();
        final var g = getG2d();
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        // Dynamic Border
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, w - 1, h - 1);
        // Text
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("Java 2D", 20, 35);
        log.info("Rendering {}x{} frame...", w, h);
        // Push the BufferedImage to the hardware via driver
        getOled().drawImage(getImage());
        // Wait based on sleep option
        TimeUnit.MILLISECONDS.sleep(getSleep());
        done();
        return 0;
    }

    /**
     * Main entry point using picocli.
     *
     * @param args Argument list.
     */
    public static void main(final String[] args) {
        System.exit(new CommandLine(new Ssd1331BufImage()).execute(args));
    }
}
