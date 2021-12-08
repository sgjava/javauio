/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.U8g2;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * XBM demo.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "Draw", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Draw demo")
public class Draw extends Base {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Draw.class);

    /**
     * Line examples.
     */
    public void lines() {
        final var u8g2 = getU8g2();
        final var display = getDisplay();
        final var height = getHeight();
        final var width = getWidth();
        final var sleep = getSleep();
        showText("drawHLine");
        for (int y = 0; y < height; y += 4) {
            U8g2.drawHLine(u8g2, y, y, width - (y * 2));
            U8g2.sendBuffer(u8g2);
        }
        display.sleep(sleep);
        showText("drawVLine");
        for (int x = 0; x < width; x += 4) {
            U8g2.drawVLine(u8g2, x, 0, height - x / (width / height));
            U8g2.sendBuffer(u8g2);
        }
        display.sleep(sleep);
        showText("drawLine");
        for (int y = 0; y < height; y += 4) {
            U8g2.drawLine(u8g2, 0, 0, width, y);
            U8g2.sendBuffer(u8g2);
        }
        display.sleep(sleep);
    }

    /**
     * Ellipse and circle examples.
     */
    public void ellipses() {
        final var u8g2 = getU8g2();
        final var display = getDisplay();
        final var height = getHeight();
        final var width = getWidth();
        final var sleep = getSleep();
        showText("drawCircle");
        for (int r = 4; r < height / 2; r += 4) {
            U8g2.drawCircle(u8g2, width / 2, height / 2, r, U8g2.U8G2_DRAW_ALL);
            U8g2.sendBuffer(u8g2);
        }
        display.sleep(sleep);
        showText("drawDisc");
        for (int r = 4; r < height; r += 4) {
            U8g2.drawDisc(u8g2, width / 2, height / 2, r / 2, U8g2.U8G2_DRAW_ALL);
            U8g2.sendBuffer(u8g2);
        }
        display.sleep(sleep);
        showText("drawEllipse");
        for (int r = 4; r < height / 2; r += 4) {
            U8g2.drawEllipse(u8g2, width / 2, height / 2, r * 2, r, U8g2.U8G2_DRAW_ALL);
            U8g2.sendBuffer(u8g2);
        }
        display.sleep(sleep);
        showText("drawFilledEllipse");
        for (int r = 4; r < height; r += 4) {
            U8g2.drawFilledEllipse(u8g2, width / 2, height / 2, r, r / 2, U8g2.U8G2_DRAW_ALL);
            U8g2.sendBuffer(u8g2);
        }
        display.sleep(sleep);
    }

    /**
     * Pixel example.
     */
    public void pixels() {
        final var u8g2 = getU8g2();
        final var height = getHeight();
        final var width = getWidth();
        final var random = new Random();
        showText("drawPixel");
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                U8g2.drawPixel(u8g2, random.nextInt(width), random.nextInt(height));
            }
            U8g2.sendBuffer(u8g2);
            U8g2.clearBuffer(u8g2);
        }
    }

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
        lines();
        ellipses();
        pixels();
        done();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new Draw()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
