/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Procedural RGB Plasma Effect for SSD1331.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Plasma", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class Ssd1331Plasma implements Callable<Integer> {

    /**
     * SPI device option.
     */
    @Option(names = {"-d", "--device"}, description = "SPI device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/spidev1.0";
    /**
     * SPI mode option.
     */
    @Option(names = {"-m", "--mode"}, description = "SPI mode, ${DEFAULT-VALUE} by default.")
    private int mode = 3;
    /**
     * SPI Hz speed option.
     */
    @Option(names = {"-s", "--speed"}, description = "Max speed in Hz, ${DEFAULT-VALUE} by default.")
    private int speed = 8000000;
    /**
     * GPIO device option.
     */
    @Option(names = {"-g", "--gpio-device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String gpioDevice = "/dev/gpiochip0";
    /**
     * DC line option.
     */
    @Option(names = {"-dc", "--dc-line"}, description = "DC line, ${DEFAULT-VALUE} by default.")
    private int dc = 199;
    /**
     * RES line option.
     */
    @Option(names = {"-res", "--res-line"}, description = "RES line, ${DEFAULT-VALUE} by default.")
    private int res = 198;
    /**
     * FPS option.
     */
    @Option(names = {"-f", "--fps"}, description = "Target frames per second", defaultValue = "30")
    public int fps;

    /**
     * Executes the plasma animation logic.
     *
     * @param oled SSD1331 driver instance.
     * @throws Exception On SPI or timing errors.
     */
    public void demo(final Ssd1331 oled) throws Exception {
        // Use Lombok getters from driver
        final var w = oled.getWidth();
        final var h = oled.getHeight();
        final var maxFrames = fps * 60;
        var frameCount = 0;
        var time = 0.0f;

        // Optimized for Java 25 bit-packing in Ssd1331 class
        final var image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        final int[] pixelData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        log.info("Starting RGB Plasma (60s run)...");
        while (frameCount < maxFrames) {
            for (var y = 0; y < h; y++) {
                // Pre-calculate Y components
                final var vY = Math.sin(y / 8.0 + time / 2.0);
                final var cyBase = y + 8.0 * Math.cos(time / 2.0);

                for (var x = 0; x < w; x++) {
                    // Combine interference patterns
                    var v = Math.sin(x / 16.0 + time) + vY;
                    v += Math.sin((x + y + time) / 16.0);
                    final var cx = x + 8.0 * Math.sin(time / 3.0);
                    v += Math.sin(Math.sqrt(cx * cx + cyBase * cyBase + 1.0) / 8.0);

                    // Generate different color blobs based on wave value 'v'
                    final int r = (int) (128.0 + 127.0 * Math.sin(v * Math.PI + time));
                    final int g = (int) (128.0 + 127.0 * Math.sin(v * Math.PI + time / 2.0 + Math.PI / 2.0));
                    final int b = (int) (128.0 + 127.0 * Math.sin(v * Math.PI + time / 4.0 + Math.PI));

                    // Direct write to buffer for maximum performance
                    pixelData[y * w + x] = (r << 16) | (g << 8) | b;
                }
            }

            // Push to display using optimized manual bit-packing
            oled.drawImage(image);

            time += 0.08f;
            frameCount++;

            // Maintain FPS
            TimeUnit.MILLISECONDS.sleep(1000L / fps);
        }
        log.info("Demo complete.");
    }

    @Override
    public Integer call() throws Exception {
        try (final var oled = new Ssd1331(device, mode, speed, gpioDevice, dc, res)) {
            oled.setup();
            oled.clear();
            demo(oled);
        }
        return 0;
    }

    public static void main(final String[] args) {
        System.exit(new CommandLine(new Ssd1331Plasma()).execute(args));
    }
}
