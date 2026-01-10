package com.codeferm.u8g2.demo;

import static com.codeferm.u8g2.Common.moveJavaToNative;
import com.codeferm.u8g2.U8g2;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import picocli.CommandLine;

/**
 * BufferedImage demo.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@CommandLine.Command(name = "BufImage", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "BufferedImage demo")
public class BufImage extends Base {

    /**
     * Draws a BufferedImage to the U8g2 internal buffer. Assumes the image size matches the display size.
     *
     * @param image BufferedImage to render.
     */
    public void sendBufferedImage(final BufferedImage image) {
        final var u8g2 = getU8g2();
        final var display = getDisplay();
        final var height = getHeight();
        final var width = getWidth();
        final var sleep = getSleep();
        final var bufferSize = U8g2.getBufferSize(u8g2);
        // Get the native pointer to the U8g2 internal buffer
        final var nativeBufferPtr = U8g2.getBufferPtr(u8g2);
        // Create a Java byte array to hold the translated pixel data
        byte[] localBuffer = new byte[bufferSize];
        // Map pixels: U8g2 uses a vertical bit-mapping (8 pixels per byte)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                // Simple binary threshold (consider any color as 'on')
                boolean isPixelOn = (rgb & 0xFFFFFF) != 0;
                if (isPixelOn) {
                    // U8g2 memory layout: (y / 8) * width + x
                    int byteIdx = (y / 8) * width + x;
                    int bitIdx = y % 8;
                    localBuffer[byteIdx] |= (1 << bitIdx);
                }
            }
        }
        // Move the Java byte array to the C memory address
        moveJavaToNative(nativeBufferPtr, localBuffer, bufferSize);
        // Tell the hardware to display what's in the native buffer
        U8g2.sendBuffer(u8g2);
        getDisplay().sleep(getSleep());
    }

    /**
     * Creates and draws the frame.
     *
     * @param message String to display.
     */
    public void renderFrame(final String message) {
        final var height = getHeight();
        final var width = getWidth();
        // Create a 1-bit (Monochrome) image
        final var canvas = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        final var g2d = canvas.createGraphics();
        // Fill background with black
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        // Setup Font
        g2d.setColor(Color.WHITE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        // Draw Text centered
        final var fm = g2d.getFontMetrics();
        final var x = (width - fm.stringWidth(message)) / 2;
        final var y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(message, x, y);
        // Draw a decorative border
        g2d.drawRect(0, 0, width - 1, height - 1);
        g2d.dispose();
        // Send to display
        sendBufferedImage(canvas);
    }

    /**
     * Show XBM image.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        // This will setup display
        var exitCode = super.call();
        renderFrame("Test");
        done();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new BufImage()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
