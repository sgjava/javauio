/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Common;
import com.codeferm.u8g2.U8g2;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes a PNG sprite sheet into native memory and renders it in a tiled grid.
 * <p>
 * This class converts a monochrome-ready PNG into an MSB-first bitmap format compatible with U8g2.drawBitmap, storing the result in
 * a single contiguous native memory block.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "SpriteSheetProcessor", mixinStandardHelpOptions = true, version = "2.9.0")
public class SpriteSheet extends Base {

    /**
     * File to process.
     */
    @Option(names = {"-f", "--file"}, description = "PNG file path")
    private String filePath = "src/main/resources/1bit_sciencefiction_pyairvander.png";
    /**
     * Sprite Width.
     */
    @Option(names = {"--sw"}, defaultValue = "16", description = "Sprite Width")
    private int spriteW;
    /**
     * Sprite Height.
     */
    @Option(names = {"--sh"}, defaultValue = "16", description = "Sprite Height")
    private int spriteH;
    /**
     * Columns in sheet.
     */
    @Option(names = {"--cols"}, defaultValue = "20", description = "Columns in sheet")
    private int cols;
    /**
     * Rows in sheet.
     */
    @Option(names = {"--rows"}, defaultValue = "8", description = "Rows in sheet")
    private int rows;
    /**
     * Invert black/white pixels.
     */
    @Option(names = {"--invert"}, description = "Invert black/white pixels", defaultValue = "false")
    private boolean invert;

    /**
     * Pointer to the start of the sprite sheet in native memory.
     */
    private long nativeSheetPtr;

    /**
     * Number of bytes occupied by a single sprite in memory.
     */
    private int bytesPerSprite;

    /**
     * Reads the PNG file, performs binarization, and packs bits into MSB-first format. The resulting linear byte array is moved to
     * native memory for high-performance access.
     */
    private void processPngToNative() {
        var path = Path.of(filePath);
        if (!Files.exists(path)) {
            path = Path.of(filePath + ".png");
            if (!Files.exists(path)) {
                throw new RuntimeException(String.format("File not found %s", filePath));
            }
        }
        try {
            var img = ImageIO.read(path.toFile());
            if (img == null) {
                throw new RuntimeException(String.format("Could not decode image at %s", path));
            }
            bytesPerSprite = (spriteW / 8) * spriteH;
            var totalSprites = cols * rows;
            var linearSheet = new byte[totalSprites * bytesPerSprite];
            for (var r = 0; r < rows; r++) {
                for (var c = 0; c < cols; c++) {
                    var spriteIdx = (r * cols) + c;
                    var baseByteOffset = spriteIdx * bytesPerSprite;
                    for (var y = 0; y < spriteH; y++) {
                        for (var xByte = 0; xByte < (spriteW / 8); xByte++) {
                            var b = 0;
                            for (var bit = 0; bit < 8; bit++) {
                                var px = (c * spriteW) + (xByte * 8) + bit;
                                var py = (r * spriteH) + y;
                                if (px < img.getWidth() && py < img.getHeight()) {
                                    var rgb = img.getRGB(px, py);
                                    // Luminance formula to get grayscale value
                                    var gray = (int) (0.299 * ((rgb >> 16) & 0xff) + 0.587 * ((rgb >> 8) & 0xff) + 0.114 * (rgb & 0xff));
                                    // Pack bit: MSB-first (bit 7 is leftmost pixel)
                                    if (invert ? (gray < 128) : (gray > 128)) {
                                        b |= (1 << (7 - bit));
                                    }
                                }
                            }
                            linearSheet[baseByteOffset + (y * (spriteW / 8)) + xByte] = (byte) b;
                        }
                    }
                }
            }
            // Move entire contiguous sheet to native memory once
            nativeSheetPtr = Common.malloc(linearSheet.length);
            Common.moveJavaToNative(nativeSheetPtr, linearSheet, linearSheet.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the rendering loop, tiling sprites onto the display based on screen width/height.
     *
     * @return 0 upon successful completion of the sprite sequence.
     * @throws InterruptedException if the display sleep is interrupted.
     */
    @Override
    public Integer call() throws InterruptedException {
        super.call();
        processPngToNative();
        var u8 = getU8g2();
        var display = getDisplay();
        var screenW = getWidth();
        var screenH = getHeight();
        var x = 0;
        var y = 0;
        var totalSprites = cols * rows;
        U8g2.clearBuffer(u8);
        for (var i = 0; i < totalSprites; i++) {
            // Calculate pointer to the specific sprite using linear offset
            var spritePtr = nativeSheetPtr + ((long) i * bytesPerSprite);
            // Draw sprite from native memory
            U8g2.drawBitmap(u8, x, y, spriteW / 8, spriteH, spritePtr);
            U8g2.sendBuffer(u8);
            // Calculate next tiling position
            x += spriteW;
            // Wrap to next row if width exceeded
            if (x + spriteW > screenW) {
                x = 0;
                y += spriteH;
            }
            // Clear and restart at (0,0) if screen is full
            if (y + spriteH > screenH) {
                display.sleep(2000);
                U8g2.clearBuffer(u8);
                x = 0;
                y = 0;
            }
        }
        log.info("All sprites displayed. Closing...");
        display.sleep(3000);
        done();
        return 0;
    }

    /**
     * Main entry point for the SpriteSheetProcessor application.
     *
     * * @param args command line arguments.
     */
    public static void main(String[] args) {
        System.exit(new CommandLine(new SpriteSheet()).execute(args));
    }
}
