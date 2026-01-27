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
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Color video demo for SSD1331 using raw RGB565BE data. To make video use:
 * <p>
 * ffmpeg -i input.mp4 -f rawvideo -pix_fmt rgb565be -s 96x64 video.raw
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Video", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Plays raw RGB565BE video on SSD1331")
public class Ssd1331Video extends Ssd1331Base {

    /**
     * Input file path for the raw video data.
     */
    @Option(names = {"--file"}, description = "Input RGB565BE file, ${DEFAULT-VALUE} by default.")
    private String fileName = "src/main/resources/color.raw";

    /**
     * Reads raw RGB565 frames and sends them directly to the display via SPI.
     * <p>
     * Uses var type inference and final modifiers. The buffer is cleared after each read to reset the position for the next frame.
     * </p>
     *
     * @param oled SSD1331 driver instance.
     */
    public void playVideo(final Ssd1331 oled) {
        // Access cached dimensions from Base class
        final var w = getWidth();
        final var h = getHeight();
        // RGB565 is 2 bytes per pixel
        final var frameSize = w * h * 2;
        final var frameDurationNs = TimeUnit.SECONDS.toNanos(1) / getFps();
        final var buffer = ByteBuffer.allocateDirect(frameSize);
        final var frameArray = new byte[frameSize];
        log.info("Playing {}x{} color video from {} at {} FPS", w, h, fileName, getFps());
        try (var channel = FileChannel.open(Path.of(fileName), StandardOpenOption.READ)) {
            // Set display address once to fill the screen
            oled.writeCommand(new byte[]{Ssd1331.SET_COLUMN_ADDRESS, (byte) 0, (byte) (w - 1)});
            oled.writeCommand(new byte[]{Ssd1331.SET_ROW_ADDRESS, (byte) 0, (byte) (h - 1)});
            while (channel.read(buffer) != -1) {
                final var startTime = System.nanoTime();
                buffer.flip();
                if (buffer.remaining() == frameSize) {
                    buffer.get(frameArray);
                    // Direct push of raw bytes to SPI
                    oled.writeData(frameArray);
                    // Maintain target FPS timing
                    final var elapsedTime = System.nanoTime() - startTime;
                    final var sleepTimeNs = frameDurationNs - elapsedTime;
                    if (sleepTimeNs > 0) {
                        TimeUnit.NANOSECONDS.sleep(sleepTimeNs);
                    }
                }
                // Prepare buffer for the next frame read
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
     * Execution logic for video playback.
     *
     * @return Exit code.
     * @throws Exception Possible hardware exception.
     */
    @Override
    public Integer call() throws Exception {
        // super.call() initializes the hardware and caches dimensions in Base
        super.call();
        playVideo(getOled());
        done();
        return 0;
    }

    /**
     * Main entry point using picocli.
     *
     * @param args Argument list.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Ssd1331Video()).execute(args));
    }
}
