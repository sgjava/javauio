/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2.demo;

import com.codeferm.u8g2.FontType;
import com.codeferm.u8g2.U8g2;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * A hardware monitor for Linux systems. Samples kernel data to drive a high-density graphical dashboard.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "LinuxMonitor", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class LinuxMonitor extends Base {

    /**
     * FPS.
     */
    @Option(names = {"-f", "--fps"}, description = "Update rate", defaultValue = "2")
    public int fps;
    /**
     * Maximum frames.
     */
    @Option(names = {"-m", "--max-frames"}, description = "Exit after N frames (0 for infinite)", defaultValue = "0")
    public int maxFrames;
    /**
     * Network interface.
     */
    @Option(names = {"-i", "--interface"}, description = "Network interface name", defaultValue = "eth0")
    public String netIf;

    private int frameCount = 0;
    private final float[] cpuHistory = new float[40];
    private final float[] netHistory = new float[40];
    private long lastIdle = 0;
    private long lastTotal = 0;
    private long lastNetBytes = 0;
    private float currentCpuLoad = 0;
    private float currentNetKbps = 0;
    private float loadAvg = 0.0f;
    private float cpuTempC = -1.0f;
    private long memAvailableKb = 0;
    private long memTotalKb = 0;
    private boolean interfaceFound = false;

    /**
     * Samples CPU usage by calculating the delta between idle and total jiffies.
     */
    public void updateCpuMetric() {
        try (var lines = Files.lines(Paths.get("/proc/stat"))) {
            var firstLine = lines.findFirst().orElse("");
            var parts = firstLine.split("\\s+");
            var idle = Long.parseLong(parts[4]);
            var total = 0L;
            for (var i = 1; i < parts.length; i++) {
                total += Long.parseLong(parts[i]);
            }
            var totalDelta = total - lastTotal;
            var idleDelta = idle - lastIdle;
            if (totalDelta > 0) {
                currentCpuLoad = 100.0f * (1.0f - ((float) idleDelta / totalDelta));
            }
            lastTotal = total;
            lastIdle = idle;
            System.arraycopy(cpuHistory, 1, cpuHistory, 0, cpuHistory.length - 1);
            cpuHistory[cpuHistory.length - 1] = currentCpuLoad;
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attempts to read thermal data; defaults to N/A if sensor is unavailable.
     */
    public void updateThermalMetric() {
        var thermalPath = Paths.get("/sys/class/thermal/thermal_zone0/temp");
        if (Files.exists(thermalPath)) {
            try {
                var content = Files.readString(thermalPath).trim();
                cpuTempC = Float.parseFloat(content) / 1000.0f;
            } catch (IOException | NumberFormatException e) {
                cpuTempC = -1.0f;
            }
        } else {
            cpuTempC = -1.0f;
        }
    }

    /**
     * Reads the 1-minute load average from the kernel.
     */
    public void updateLoadAvg() {
        try {
            var content = Files.readString(Paths.get("/proc/loadavg"));
            loadAvg = Float.parseFloat(content.split("\\s+")[0]);
        } catch (IOException | NumberFormatException e) {
            loadAvg = 0.0f;
        }
    }

    /**
     * Parses memory metrics to determine utilization.
     */
    public void updateMemMetric() {
        try (var lines = Files.lines(Paths.get("/proc/meminfo"))) {
            lines.forEach(line -> {
                if (line.startsWith("MemTotal:")) {
                    memTotalKb = Long.parseLong(line.replaceAll("[^0-9]", ""));
                } else if (line.startsWith("MemAvailable:")) {
                    memAvailableKb = Long.parseLong(line.replaceAll("[^0-9]", ""));
                }
            });
        } catch (IOException e) {
            // memory stats unavailable
        }
    }

    /**
     * Tracks network throughput for the configured interface.
     */
    public void updateNetMetric() {
        try (var lines = Files.lines(Paths.get("/proc/net/dev"))) {
            var bytesWrapper = lines.filter(l -> l.contains(netIf + ":")).findFirst();
            if (bytesWrapper.isPresent()) {
                interfaceFound = true;
                var p = bytesWrapper.get().trim().split("\\s+");
                var totalBytes = Long.parseLong(p[1]) + Long.parseLong(p[9]);
                var deltaBytes = totalBytes - lastNetBytes;
                if (lastNetBytes > 0) {
                    currentNetKbps = (deltaBytes / 1024.0f) * fps * 8;
                }
                lastNetBytes = totalBytes;
            } else {
                interfaceFound = false;
                currentNetKbps = 0;
            }
            System.arraycopy(netHistory, 1, netHistory, 0, netHistory.length - 1);
            netHistory[netHistory.length - 1] = Math.min(100, currentNetKbps / 10.0f);
        } catch (IOException | NumberFormatException e) {
            // net stats unavailable
        }
    }

    /**
     * Renders a small pulsing heartbeat indicator to show application activity.
     *
     * * @param u8 The U8g2 handle
     */
    private void drawHeartbeat(long u8) {
        var x = 123;
        var y = 2;
        // Toggle between solid and hollow box for better visibility
        if (frameCount % 2 == 0) {
            U8g2.drawBox(u8, x, y, 4, 4);
        } else {
            U8g2.drawFrame(u8, x, y, 4, 4);
        }
    }

    /**
     * Draws a labeled sparkline graph.
     *
     * * @param u8 The U8g2 handle.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param data history array.
     * @param label Section name.
     */
    private void drawGraph(long u8, int x, int y, float[] data, String label) {
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_4X6_TF));
        U8g2.drawStr(u8, x, y - 2, label);
        U8g2.drawFrame(u8, x, y, 42, 18);
        for (var i = 0; i < data.length - 1; i++) {
            var y1 = (y + 17) - (int) ((data[i] / 100.0f) * 16);
            var y2 = (y + 17) - (int) ((data[i + 1] / 100.0f) * 16);
            U8g2.drawLine(u8, x + 1 + i, Math.max(y + 1, y1), x + 2 + i, Math.max(y + 1, y2));
        }
    }

    /**
     * Main rendering routine.
     */
    public void render() {
        var u8 = getU8g2();
        U8g2.clearBuffer(u8);
        // Header Section
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_5X7_TF));
        U8g2.drawStr(u8, 2, 8, "LINUX MONITOR");
        drawHeartbeat(u8);
        U8g2.drawLine(u8, 0, 10, 127, 10);
        // Trend Graphs
        drawGraph(u8, 2, 22, cpuHistory, String.format("CPU:%.1f%%", currentCpuLoad));
        var netLabel = interfaceFound ? "NET:LIVE" : "NET:ERR";
        drawGraph(u8, 46, 22, netHistory, netLabel);
        // Memory usage gauge
        var used = memTotalKb - memAvailableKb;
        var ratio = (float) used / Math.max(1, memTotalKb);
        U8g2.drawFrame(u8, 92, 22, 34, 18);
        U8g2.drawBox(u8, 94, 38 - (int) (14 * ratio), 30, (int) (14 * ratio));
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_4X6_TF));
        U8g2.drawStr(u8, 92, 20, "MEMORY");
        // Footer Section
        var tempStr = (cpuTempC < 0) ? "TEMP: N/A" : String.format("TEMP: %.1f C", cpuTempC);
        U8g2.drawStr(u8, 2, 48, tempStr);
        U8g2.drawStr(u8, 2, 56, String.format("NET:  %.2f Kbps", currentNetKbps));
        U8g2.drawStr(u8, 2, 64, String.format("LOAD: %.2f  MEM: %dMB", loadAvg, used / 1024));
        U8g2.sendBuffer(u8);
    }

    /**
     * Update info.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        super.call();
        while (maxFrames == 0 || frameCount < maxFrames) {
            updateCpuMetric();
            updateMemMetric();
            updateNetMetric();
            updateLoadAvg();
            updateThermalMetric();
            render();
            getDisplay().sleep(1000 / fps);
            frameCount++;
        }
        done();
        return 0;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new LinuxMonitor()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
