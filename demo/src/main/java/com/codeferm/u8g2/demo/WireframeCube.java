/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.U8g2;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.Random;

/**
 * 3D Wireframe Cube with dynamic scaling, randomized movement, and FPS control.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "WireframeCube", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Bouncing 3D cube with dynamic scaling and coordinate clipping")
public class WireframeCube extends Base {

    /**
     * FPS.
     */
    @Option(names = {"--fps"}, defaultValue = "30", description = "Frames per second (default: ${DEFAULT-VALUE})")
    private int fps;
    /**
     * 3D vertices for a unit cube (coordinates from -1 to 1).
     */
    private final double[][] vertices = {
        {-1, -1, 1}, {1, -1, 1}, {1, 1, 1}, {-1, 1, 1},
        {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1}
    };
    /**
     * The 12 edges connecting the 8 vertices.
     */
    private final int[][] edges = {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7}
    };
    private final Random random = new Random();

    /**
     * Clips coordinates to display boundaries to prevent driver-level wrapping artifacts.
     */
    private void drawClippedLine(long u8, int x1, int y1, int x2, int y2, int w, int h) {
        var cx1 = Math.max(0, Math.min(x1, w - 1));
        var cy1 = Math.max(0, Math.min(y1, h - 1));
        var cx2 = Math.max(0, Math.min(x2, w - 1));
        var cy2 = Math.max(0, Math.min(y2, h - 1));
        U8g2.drawLine(u8, cx1, cy1, cx2, cy2);
    }

    /**
     * Main animation loop for the rotating and bouncing cube.
     */
    public void drawCube() {
        var u8 = getU8g2();
        var display = getDisplay();
        var screenW = getWidth();
        var screenH = getHeight();
        // Animation state
        var angleX = 0.0;
        var angleY = 0.0;
        var angleZ = 0.0;
        var posX = (double) screenW / 2.0;
        var posY = (double) screenH / 2.0;
        // Physics state
        final var targetSpeed = 1.3;
        var velX = 1.0;
        var velY = 0.7;
        var cameraDistance = 3.6;
        var projectionScale = screenH * 0.60;
        var frameDelay = 1000 / fps;
        while (true) {
            var startTime = System.currentTimeMillis();
            U8g2.clearBuffer(u8);

            var projected = new int[8][2];
            var minX = 1000;
            var maxX = -1000;
            var minY = 1000;
            var maxY = -1000;
            for (var i = 0; i < 8; i++) {
                var x = vertices[i][0];
                var y = vertices[i][1];
                var z = vertices[i][2];
                // --- 3D Rotation ---
                var cX = Math.cos(angleX);
                var sX = Math.sin(angleX);
                var y1 = y * cX - z * sX;
                var z1 = y * sX + z * cX;
                var cY = Math.cos(angleY);
                var sY = Math.sin(angleY);
                var x2 = x * cY + z1 * sY;
                var z2 = -x * sY + z1 * cY;
                var cZ = Math.cos(angleZ);
                var sZ = Math.sin(angleZ);
                var x3 = x2 * cZ - y1 * sZ;
                var y3 = x2 * sZ + y1 * cZ;
                // --- Perspective Projection ---
                var pZ = z2 + cameraDistance;
                var offX = (int) (x3 * projectionScale / pZ);
                var offY = (int) (y3 * projectionScale / pZ);
                // Update relative bounding box
                if (offX < minX) {
                    minX = offX;
                }
                if (offX > maxX) {
                    maxX = offX;
                }
                if (offY < minY) {
                    minY = offY;
                }
                if (offY > maxY) {
                    maxY = offY;
                }
                projected[i][0] = (int) (posX + offX);
                projected[i][1] = (int) (posY + offY);
            }
            for (var edge : edges) {
                drawClippedLine(u8, projected[edge[0]][0], projected[edge[0]][1],
                        projected[edge[1]][0], projected[edge[1]][1],
                        screenW, screenH);
            }
            U8g2.sendBuffer(u8);
            // Update physics
            angleX += 0.035;
            angleY += 0.05;
            angleZ += 0.025;
            posX += velX;
            posY += velY;
            // Bounce and Randomize
            var bounced = false;
            if (posX + minX <= 0 || posX + maxX >= screenW) {
                velX = -velX;
                posX = (posX + minX <= 0) ? -minX + 1 : screenW - maxX - 1;
                bounced = true;
            }
            if (posY + minY <= 0 || posY + maxY >= screenH) {
                velY = -velY;
                posY = (posY + minY <= 0) ? -minY + 1 : screenH - maxY - 1;
                bounced = true;
            }
            if (bounced) {
                var newAngle = Math.atan2(velY, velX) + (random.nextDouble() - 0.5) * 0.3;
                velX = Math.cos(newAngle) * targetSpeed;
                velY = Math.sin(newAngle) * targetSpeed;
            }
            // Sync FPS
            var diff = System.currentTimeMillis() - startTime;
            if (diff < frameDelay) {
                try {
                    display.sleep((int) (frameDelay - diff));
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public Integer call() throws InterruptedException {
        var exitCode = super.call();
        // Base call handles hardware init
        drawCube();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new WireframeCube()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
