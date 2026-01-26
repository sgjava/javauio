/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Color video demo for SSD1331 using raw RGB565BE data. Ro make video use:
 *
 * ffmpeg -i input.mp4 -f rawvideo -pix_fmt rgb565be -s 96x64 video.raw
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Video", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Plays raw RGB565BE video on SSD1331")
public class Ssd1331Video implements Callable<Integer> {

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
     * Input file path for the raw video data.
     */
    @Option(names = {"--file"}, description = "Input RGB565BE file, ${DEFAULT-VALUE} by default.")
    private String fileName = "src/main/resources/color.raw";

    /**
     * Reads raw RGB565 frames and sends them directly to the display.
     *
     * @param oled SSD1331 driver instance.
     */
    public void playVideo(final Ssd1331 oled) {
        // Use Lombok getters from your driver
        final int width = oled.getWidth();
        final int height = oled.getHeight();
        // RGB565 is 2 bytes per pixel
        final int frameSize = width * height * 2;
        final long frameDurationNs = TimeUnit.SECONDS.toNanos(1) / fps;
        final var buffer = ByteBuffer.allocateDirect(frameSize);
        final var frameArray = new byte[frameSize];
        log.info("Playing {}x{} color video from {} at {} FPS", width, height, fileName, fps);
        try (var channel = FileChannel.open(Path.of(fileName), StandardOpenOption.READ)) {
            // Set display address once to fill the screen
            oled.writeCommand(new byte[]{Ssd1331.SET_COLUMN_ADDRESS, (byte) 0, (byte) (width - 1)});
            oled.writeCommand(new byte[]{Ssd1331.SET_ROW_ADDRESS, (byte) 0, (byte) (height - 1)});
            while (channel.read(buffer) != -1) {
                final long startTime = System.nanoTime();
                buffer.flip();
                if (buffer.remaining() == frameSize) {
                    buffer.get(frameArray);
                    // Direct push of raw bytes to SPI
                    oled.writeData(frameArray);
                    // Maintain target FPS timing
                    final long elapsedTime = System.nanoTime() - startTime;
                    final long sleepTimeNs = frameDurationNs - elapsedTime;
                    if (sleepTimeNs > 0) {
                        TimeUnit.NANOSECONDS.sleep(sleepTimeNs);
                    }
                }
                buffer.clear();
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error during video playback: {}", e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Play video.
     *
     * @return Exit code.
     * @throws Exception Possible hardware exception.
     */
    @Override
    public Integer call() throws Exception {
        try (final var oled = new Ssd1331(device, mode, speed, gpioDevice, dc, res)) {
            oled.setup();
            oled.clear();
            playVideo(oled);
        }
        return 0;
    }

    /**
     * Main entry point.
     *
     * @param args Argument list.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Ssd1331Video()).execute(args));
    }
}
