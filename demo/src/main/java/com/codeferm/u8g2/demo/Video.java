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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Video demo. Video is in raw bitmap format the same size as the display. You can create files from ffmpeg using:
 *
 * ffmpeg -i input.mkv -vf "format=gray,scale=128:64,format=monob" -f rawvideo video.raw
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "Video", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Video demo")
public class Video extends Base {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Video.class);
    /**
     * Input file.
     */
    @CommandLine.Option(names = {"-f", "--file"}, description = "Input video file name, ${DEFAULT-VALUE} by default.")
    private String fileName = "src/main/resources/video.raw";
    /**
     * Target frames per second.
     */
    @CommandLine.Option(names = {"-fps", "--fps"}, description = "Target FPS, ${DEFAULT-VALUE} by default.")
    private int fps = 24;

    /**
     * Display each frame from raw video file with FPS pacing.
     */
    public void displayFrames() {
        final var u8g2 = getU8g2();
        final var height = getHeight();
        final var width = getWidth();
        final int frameSize = (height * width) / 8;
        // Target duration per frame in nanoseconds
        final long frameDurationNs = TimeUnit.SECONDS.toNanos(1) / fps;
        // Use Direct ByteBuffer for high-performance I/O
        final ByteBuffer buffer = ByteBuffer.allocateDirect(frameSize);
        final byte[] frameArray = new byte[frameSize];
        final var image = Common.malloc(frameSize);
        logger.info("Reading {} at {} FPS using NIO FileChannel", fileName, fps);
        try (var channel = FileChannel.open(Path.of(fileName), StandardOpenOption.READ)) {
            while (channel.read(buffer) != -1) {
                long startTime = System.nanoTime();
                buffer.flip(); // Prepare to read from buffer                
                if (buffer.remaining() == frameSize) {
                    buffer.get(frameArray); // Copy to array for native move
                    Common.moveJavaToNative(image, frameArray, frameArray.length);
                    U8g2.drawBitmap(u8g2, 0, 0, width / 8, height, image);
                    U8g2.sendBuffer(u8g2);
                    // Pacing logic
                    long elapsedTime = System.nanoTime() - startTime;
                    long sleepTimeNs = frameDurationNs - elapsedTime;
                    if (sleepTimeNs > 0) {
                        TimeUnit.NANOSECONDS.sleep(sleepTimeNs);
                    }
                } else {
                    throw new RuntimeException(String.format("Partial frame read: %d bytes", buffer.remaining()));
                }

                buffer.clear(); // Prepare for next channel read
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            Common.free(image);
        }
    }

    /**
     * Display video.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        // This will setup display
        var exitCode = super.call();
        displayFrames();
        done();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new Video()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
