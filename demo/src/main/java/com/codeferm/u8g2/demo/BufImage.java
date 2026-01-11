package com.codeferm.u8g2.demo;

import static com.codeferm.u8g2.Common.moveJavaToNative;
import com.codeferm.u8g2.U8g2;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import picocli.CommandLine;

/**
 * This version uses pre-allocated buffers and direct raster bit-scraping.
 *
 * @author Steven P. Goldsmith
 * @version 1.1.0
 */
@CommandLine.Command(name = "BufImage", mixinStandardHelpOptions = true, version = "1.1.0",
        description = "Optimized BufferedImage demo")
public class BufImage extends Base {

    // Pre-allocated drawing objects to prevent GC pressure
    private BufferedImage canvas;
    private Graphics2D g2d;
    private byte[] localBuffer;
    private byte[] canvasPixels; // Direct access to canvas bits
    private int bufferSize;
    private long nativeBufferPtr;

    /**
     * Initialize the buffers once. This is key for performance.
     */
    private void initBuffers() {
        final var u8g2 = getU8g2();
        final int w = getWidth();
        final int h = getHeight();
        // Setup the Java Canvas (1-bit monochrome)
        this.canvas = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        this.g2d = canvas.createGraphics();
        // Extract the underlying byte array from the BufferedImage
        this.canvasPixels = ((DataBufferByte) canvas.getRaster().getDataBuffer()).getData();
        // Setup the transfer buffer for JNI
        this.bufferSize = U8g2.getBufferSize(u8g2);
        this.localBuffer = new byte[bufferSize];
        this.nativeBufferPtr = U8g2.getBufferPtr(u8g2);
        // Default Graphics settings
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
    }

    /**
     * High-speed translation of BufferedImage bits to U8g2 memory layout.
     */
    public void sendBufferedImage() {
        final int h = getHeight();
        final int w = getWidth();
        // Clear the transfer buffer
        Arrays.fill(localBuffer, (byte) 0);
        // Java Raster: Horizontal rows, 8 pixels per byte, MSB first.
        // U8g2 Raster: Vertical pages (8px high), LSB first.                
        for (int y = 0; y < h; y++) {
            int rowOffset = y * ((w + 7) / 8); // Java row start
            int pageOffset = (y / 8) * w;    // U8g2 page start
            int u8g2Bit = (1 << (y % 8));    // Bit position in U8g2 byte
            for (int x = 0; x < w; x++) {
                // Access Java bit: (x / 8) is byte, (7 - (x % 8)) is bit pos
                int javaByte = canvasPixels[rowOffset + (x / 8)] & 0xFF;
                if ((javaByte & (1 << (7 - (x % 8)))) != 0) {
                    localBuffer[pageOffset + x] |= u8g2Bit;
                }
            }
        }
        // Send to native C memory and then to hardware
        moveJavaToNative(nativeBufferPtr, localBuffer, bufferSize);
        U8g2.sendBuffer(getU8g2());
    }

    /**
     * Renders a frame using standard Java2D calls.
     * 
     * @param message String to display. 
     */
    public void renderFrame(final String message) {
        // Clear Java canvas (Black)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        // Draw Content (White)
        g2d.setColor(Color.WHITE);
        final var fm = g2d.getFontMetrics();
        final int x = (getWidth() - fm.stringWidth(message)) / 2;
        final int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(message, x, y);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        // Push to hardware
        sendBufferedImage();
    }

    /**
     * Show BufferedImage image.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        // super.call() initializes the hardware/u8g2 pointer
        var exitCode = super.call();
        initBuffers();
        renderFrame("Java 2D");
        getDisplay().sleep(getSleep());
        done();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new BufImage()).registerConverter(Byte.class, Byte::decode).registerConverter(Integer.class,
                Integer::decode).execute(args));
    }
}
