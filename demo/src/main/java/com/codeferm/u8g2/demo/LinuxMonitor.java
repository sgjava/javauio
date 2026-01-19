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
 * A hardware monitor for Linux systems with dynamic layout scaling. This demo samples kernel data (procfs/sysfs) and renders a
 * graphical dashboard that automatically adjusts its layout based on the display resolution.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "LinuxMonitor", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class LinuxMonitor extends Base {

    /**
     * Update rate in frames per second.
     */
    @Option(names = {"-f", "--fps"}, description = "Update rate", defaultValue = "2")
    private int fps;

    /**
     * Maximum number of frames to render before exiting (0 for infinite).
     */
    @Option(names = {"-m", "--max-frames"}, description = "Exit after N frames (0 for infinite)", defaultValue = "0")
    private int maxFrames;

    /**
     * Target network interface for bandwidth monitoring.
     */
    @Option(names = {"-i", "--interface"}, description = "Network interface name", defaultValue = "eth0")
    private String netIf;

    /**
     * Current frame count.
     */
    private int frameCount = 0;

    /**
     * History buffer for CPU sparkline.
     */
    private final float[] cpuHistory = new float[40];

    /**
     * History buffer for Network sparkline.
     */
    private final float[] netHistory = new float[40];

    private long lastIdle = 0;
    private long lastTotal = 0;
    private long lastNetBytes = 0;
    private float currentCpuLoad = 0;
    private float currentNetKbps = 0;
    private float loadAvg = 0.0f;
    private float cpuTempF = -1.0f;
    private long memAvailableKb = 0;
    private long memTotalKb = 0;

    /**
     * Updates CPU utilization by parsing /proc/stat.
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
            log.error("CPU metric error: {}", e.getMessage());
        }
    }

    /**
     * Updates CPU temperature from sysfs and converts Celsius to Fahrenheit.
     */
    public void updateThermalMetric() {
        var thermalPath = Paths.get("/sys/class/thermal/thermal_zone0/temp");
        if (Files.exists(thermalPath)) {
            try {
                var content = Files.readString(thermalPath).trim();
                var cpuTempC = Float.parseFloat(content) / 1000.0f;
                // Convert to Fahrenheit
                cpuTempF = (cpuTempC * 9 / 5) + 32;
            } catch (IOException | NumberFormatException e) {
                cpuTempF = -1.0f;
            }
        }
    }

    /**
     * Updates 1-minute load average from /proc/loadavg.
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
     * Updates Memory statistics from /proc/meminfo.
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
            log.error("Memory metric error");
        }
    }

    /**
     * Updates network throughput for the specified interface.
     */
    public void updateNetMetric() {
        try (var lines = Files.lines(Paths.get("/proc/net/dev"))) {
            var bytesWrapper = lines.filter(l -> l.contains(netIf + ":")).findFirst();
            if (bytesWrapper.isPresent()) {
                var p = bytesWrapper.get().trim().split("\\s+");
                var totalBytes = Long.parseLong(p[1]) + Long.parseLong(p[9]);
                var deltaBytes = totalBytes - lastNetBytes;
                if (lastNetBytes > 0) {
                    currentNetKbps = (deltaBytes / 1024.0f) * fps * 8;
                }
                lastNetBytes = totalBytes;
            }
            System.arraycopy(netHistory, 1, netHistory, 0, netHistory.length - 1);
            netHistory[netHistory.length - 1] = Math.min(100, currentNetKbps / 10.0f);
        } catch (IOException | NumberFormatException e) {
            log.error("Network metric error");
        }
    }

    /**
     * Draws a sparkline graph that scales to the assigned bounding box.
     *
     * @param u8 Native U8g2 handle.
     * @param x X start coordinate.
     * @param y Y start coordinate.
     * @param w Width of the bounding box.
     * @param h Height of the bounding box.
     * @param data Array of float values (0-100 scale).
     * @param label Text label to display above graph.
     */
    private void drawDynamicGraph(final long u8, final int x, final int y, final int w, final int h,
            final float[] data, final String label) {
        U8g2.setFont(u8, getDisplay().getFontPtr(FontType.FONT_4X6_TF));
        if (!label.isEmpty()) {
            U8g2.drawStr(u8, x, y - 1, label);
        }
        U8g2.drawFrame(u8, x, y, w, h);
        for (var i = 0; i < data.length - 1 && i < (w - 2); i++) {
            var y1 = (y + h - 1) - (int) ((data[i] / 100.0f) * (h - 2));
            var y2 = (y + h - 1) - (int) ((data[i + 1] / 100.0f) * (h - 2));
            U8g2.drawLine(u8, x + 1 + i, y1, x + 2 + i, y2);
        }
    }

    /**
     * Renders the hardware dashboard. Coordinates are calculated based on the display's width and height via getters.
     */
    public void render() {
        var u8 = getU8g2();
        U8g2.clearBuffer(u8);

        final var displayWidth = getWidth();
        final var displayHeight = getHeight();

        var smallFont = getDisplay().getFontPtr(FontType.FONT_4X6_TF);
        var medFont = getDisplay().getFontPtr(displayHeight >= 64 ? FontType.FONT_5X8_TF : FontType.FONT_4X6_TF);

        U8g2.setFont(u8, smallFont);
        var headerY = U8g2.getMaxCharHeight(u8);

        // Header row with Fahrenheit temperature
        var status = String.format("CPU:%.0f%% L:%.2f T:%.0fF", currentCpuLoad, loadAvg, cpuTempF);
        U8g2.drawStr(u8, 1, headerY, status);

        var graphY = headerY + 2;
        var graphH = (displayHeight / 2) - 4;
        var graphW = (displayWidth / 3) - 2;

        drawDynamicGraph(u8, 1, graphY, graphW, graphH, cpuHistory, "");
        drawDynamicGraph(u8, graphW + 3, graphY, graphW, graphH, netHistory, "");

        var memX = (graphW * 2) + 6;
        var used = memTotalKb - memAvailableKb;
        var ratio = (float) used / Math.max(1, memTotalKb);
        U8g2.drawStr(u8, memX, graphY - 1, String.format("%dM", used / 1024));
        U8g2.drawFrame(u8, memX, graphY, displayWidth - memX - 1, graphH);
        U8g2.drawBox(u8, memX + 1, graphY + 1, (int) ((displayWidth - memX - 3) * ratio), graphH - 2);

        U8g2.setFont(u8, medFont);
        var footerText = String.format("IF:%s RAM:%d/%dMB", netIf, used / 1024, memTotalKb / 1024);
        U8g2.drawStr(u8, 1, displayHeight - 1, footerText);

        U8g2.sendBuffer(u8);
    }

    /**
     * Main execution loop.
     *
     * @return Exit code.
     * @throws InterruptedException If sleep is interrupted.
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
     * Entry point for the Linux Monitor demo.
     *
     * @param args CLI arguments.
     */
    public static void main(final String... args) {
        System.exit(new CommandLine(new LinuxMonitor())
                .registerConverter(Integer.class, Integer::decode)
                .registerConverter(Integer.TYPE, Integer::decode)
                .execute(args));
    }
}
