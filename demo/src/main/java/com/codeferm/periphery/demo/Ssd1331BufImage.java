/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * SSD1331 Demo using BufferedImage.
 *
 * @author Steven P. Goldsmith
 * @version 1.1.1
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Ssd1331BufImage", mixinStandardHelpOptions = true)
public class Ssd1331BufImage extends Ssd1331Base {

    @Override
    public Integer call() throws Exception {
        super.call();
        log.info("Starting SSD1331 Demo");
        
        // Accessing width/height from Base fields, not the oled driver directly
        final int w = getWidth();
        final int h = getHeight();
        final var g = getG2d();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, w - 1, h - 1);
        
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("Java 2D", 20, 35);
        
        log.info("Rendering {}x{} frame...", w, h);
        getOled().drawImage(getImage());
        
        TimeUnit.MILLISECONDS.sleep(getSleep());
        
        done();
        return 0;
    }

    public static void main(final String[] args) {
        System.exit(new CommandLine(new Ssd1331BufImage()).execute(args));
    }
}
