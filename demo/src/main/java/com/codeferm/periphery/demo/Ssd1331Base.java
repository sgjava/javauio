/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Option;

/**
 * Base CLI for SSD1331 provides hardware initialization and a Graphics2D canvas.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Slf4j
public class Ssd1331Base implements Callable<Integer> {

    @Option(names = {"-d", "--device"}, description = "SPI device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/spidev1.0";

    @Option(names = {"-m", "--mode"}, description = "SPI mode, ${DEFAULT-VALUE} by default.")
    private int mode = 3;

    @Option(names = {"-s", "--speed"}, description = "Max speed in Hz, ${DEFAULT-VALUE} by default.")
    private int speed = 1000000;

    @Option(names = {"-g", "--gpio-device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String gpioDevice = "/dev/gpiochip0";

    @Option(names = {"-dc", "--dc-line"}, description = "DC line, ${DEFAULT-VALUE} by default.")
    private int dc = 199;

    @Option(names = {"-res", "--res-line"}, description = "RES line, ${DEFAULT-VALUE} by default.")
    private int res = 198;

    @Option(names = {"-f", "--fps"}, description = "Target frames per second", defaultValue = "60")
    private int fps;

    @Option(names = {"--sleep"}, description = "Milliseconds to sleep for text and graphics, ${DEFAULT-VALUE} by default.")
    private long sleep = 5000;

    private Ssd1331 oled;
    private BufferedImage image;
    private Graphics2D g2d;
    
    /**
     * Display width cached from driver.
     */
    private int width;
    
    /**
     * Display height cached from driver.
     */
    private int height;

    /**
     * Initializes hardware and populates width/height from the driver.
     *
     * @return Exit code.
     * @throws Exception Possible hardware exception.
     */
    @Override
    public Integer call() throws Exception {
        oled = new Ssd1331(device, mode, speed, gpioDevice, dc, res);
        oled.setup();
        oled.clear();
        
        // Cache dimensions locally just like u8g2 Base
        width = oled.getWidth();
        height = oled.getHeight();
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = image.createGraphics();
        return 0;
    }

    public void done() {
        if (g2d != null) {
            g2d.dispose();
        }
        if (oled != null) {
            oled.close();
        }
    }
}
