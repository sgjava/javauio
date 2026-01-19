/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.Common;
import com.codeferm.u8g2.U8g2;
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
 * Video demo with resolution-aware centering and clipping.
 * <p>
 * This class plays raw bitmap video files. It dynamically calculates offsets to center the video if the display is larger than the
 * video dimensions, or clips the video if the display is smaller.
 * </p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Video", mixinStandardHelpOptions = true, version = "1.1.0-SNAPSHOT",
        description = "Video demo with resolution-independent centering")
public class Video extends Base {

    /**
     * Input file path for the raw video data.
     */
    @Option(names = {"-f", "--file"}, description = "Input video file name, ${DEFAULT-VALUE} by default.")
    private String fileName = "src/main/resources/video.raw";
    /**
     * Target frames per second for playback pacing.
     */
    @Option(names = {"-fps", "--fps"}, description = "Target FPS, ${DEFAULT-VALUE} by default.", defaultValue = "24")
    private int fps;
    /**
     * The width of the source video in pixels.
     */
    @Option(names = {"--vw"}, description = "Video width, ${DEFAULT-VALUE} by default.", defaultValue = "128")
    private int videoWidth;
    /**
     * The height of the source video in pixels.
     */
    @Option(names = {"--vh"}, description = "Video height, ${DEFAULT-VALUE} by default.", defaultValue = "64")
    private int videoHeight;

    /**
     * Reads frames from the file channel and renders them to the display.
     * <p>
     * Calculates offsets to center the video on the screen. U8g2 handles clipping automatically if the offsets or dimensions exceed
     * the physical display boundaries.
     * </p>
     */
    public void displayFrames() {
        final var u8g2 = getU8g2();
        final var displayW = getWidth();
        final var displayH = getHeight();
        // Calculate frame size in bytes (1 bit per pixel)
        final int frameSize = (videoHeight * videoWidth) / 8;
        final long frameDurationNs = TimeUnit.SECONDS.toNanos(1) / fps;
        // Determine offsets to center the video on any resolution
        final int offsetX = (displayW - videoWidth) / 2;
        final int offsetY = (displayH - videoHeight) / 2;
        final var buffer = ByteBuffer.allocateDirect(frameSize);
        final var frameArray = new byte[frameSize];
        final var image = Common.malloc(frameSize);
        log.info("Display: {}x{}, Video: {}x{}. Centering at offset: {},{}",
                displayW, displayH, videoWidth, videoHeight, offsetX, offsetY);
        try (var channel = FileChannel.open(Path.of(fileName), StandardOpenOption.READ)) {
            while (channel.read(buffer) != -1) {
                final long startTime = System.nanoTime();
                buffer.flip();
                if (buffer.remaining() == frameSize) {
                    buffer.get(frameArray);
                    Common.moveJavaToNative(image, frameArray, frameArray.length);

                    // Clear the buffer to prevent tiling/artifacts on larger screens
                    U8g2.clearBuffer(u8g2);

                    // Draw centered (or clipped) bitmap
                    U8g2.drawBitmap(u8g2, offsetX, offsetY, videoWidth / 8, videoHeight, image);
                    U8g2.sendBuffer(u8g2);

                    // Maintain target FPS
                    final long elapsedTime = System.nanoTime() - startTime;
                    final long sleepTimeNs = frameDurationNs - elapsedTime;
                    if (sleepTimeNs > 0) {
                        TimeUnit.NANOSECONDS.sleep(sleepTimeNs);
                    }
                } else {
                    log.warn("Partial frame read: {} bytes", buffer.remaining());
                }
                buffer.clear();
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error during video playback: {}", e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } finally {
            Common.free(image);
        }
    }

    /**
     * Initializes the display and starts the video playback loop.
     *
     * @return Exit code.
     * @throws InterruptedException If the thread is interrupted during sleep.
     */
    @Override
    public Integer call() throws InterruptedException {
        // Base.call() initializes the u8g2 handle and retrieves display dimensions
        final var exitCode = super.call();
        displayFrames();
        done();
        return exitCode;
    }

    /**
     * Main entry point for the Video demo.
     *
     * @param args Command line arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new Video())
                .registerConverter(Integer.class, Integer::decode)
                .registerConverter(Integer.TYPE, Integer::decode)
                .execute(args));
    }
}
