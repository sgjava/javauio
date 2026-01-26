/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Ssd1331;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Color Space Invaders demo for SSD1331 with AI-driven player.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Ssd1331Invaders", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT")
public class Ssd1331SpaceInvaders implements Callable<Integer> {

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

    private final Random random = new Random();
    private boolean running = true;
    private int score = 0;
    private int playerX;
    private int aiTargetX;
    private final List<Invader> invaders = new ArrayList<>();
    private final List<Projectile> missiles = new ArrayList<>();
    private Projectile playerShot = null;
    private int rackX, rackY, rackDir = 1;

    // Sprite Colors (RGB888)
    private static final int C_INV_TOP = 0xFF00FF;    // Magenta
    private static final int C_INV_MID = 0x00FFFF;    // Cyan
    private static final int C_INV_BOT = 0x00FF00;    // Green
    private static final int C_PLAYER  = 0xFFFF00;    // Yellow
    private static final int C_SAUCER  = 0xFF0000;    // Red

    public static record Projectile(int x, int y) {}
    public static class Invader {
        public int x, y, type;
        public boolean active = true;
        public Invader(int x, int y, int type) { this.x = x; this.y = y; this.type = type; }
    }

    /**
     * Initializes the game world with a colorful grid.
     */
    private void init(int w, int h) {
        playerX = w / 2;
        aiTargetX = playerX;
        invaders.clear();
        missiles.clear();
        rackX = 10;
        rackY = 10;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 8; c++) {
                invaders.add(new Invader(c * 10, r * 8, r / 2));
            }
        }
    }

    private void update(int w, int h) {
        // AI Logic: Track the lowest invader or dodge missiles
        if (random.nextInt(10) > 2) {
            Invader target = invaders.stream().filter(i -> i.active).findFirst().orElse(null);
            if (target != null) aiTargetX = target.x + rackX;
        }
        if (playerX < aiTargetX) playerX++; else if (playerX > aiTargetX) playerX--;

        // Move Rack
        rackX += rackDir;
        if (rackX > w - 85 || rackX < 2) {
            rackDir *= -1;
            rackY += 2;
        }

        // Combat
        if (playerShot == null) playerShot = new Projectile(playerX + 4, h - 10);
        else {
            playerShot = new Projectile(playerShot.x, playerShot.y - 3);
            if (playerShot.y < 0) playerShot = null;
            else {
                for (var inv : invaders) {
                    if (inv.active && playerShot.x >= inv.x + rackX && playerShot.x <= inv.x + rackX + 8 
                        && playerShot.y >= inv.y + rackY && playerShot.y <= inv.y + rackY + 6) {
                        inv.active = false;
                        playerShot = null;
                        score += 10;
                        break;
                    }
                }
            }
        }
    }

    private void render(Ssd1331 oled, BufferedImage img, Graphics2D g) {
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, oled.getWidth(), oled.getHeight());

        // Draw Player
        g.setColor(new Color(C_PLAYER));
        g.fillRect(playerX, oled.getHeight() - 8, 9, 5);
        g.fillRect(playerX + 3, oled.getHeight() - 10, 3, 2);

        // Draw Invaders with specific color types
        for (var inv : invaders) {
            if (!inv.active) continue;
            g.setColor(new Color(inv.type == 0 ? C_INV_TOP : (inv.type == 1 ? C_INV_MID : C_INV_BOT)));
            g.fillRect(inv.x + rackX, inv.y + rackY, 7, 5); // Simplistic colored blocks
        }

        // Draw Projectile
        if (playerShot != null) {
            g.setColor(Color.WHITE);
            g.drawLine(playerShot.x, playerShot.y, playerShot.x, playerShot.y - 2);
        }

        oled.drawImage(img);
    }

    @Override
    public Integer call() throws Exception {
        try (final var oled = new Ssd1331(device, mode, speed, gpioDevice, dc, res)) {
            oled.setup();
            oled.clear();
            
            final var image = new BufferedImage(oled.getWidth(), oled.getHeight(), BufferedImage.TYPE_INT_RGB);
            final var g2d = image.createGraphics();
            
            init(oled.getWidth(), oled.getHeight());
            log.info("Starting Color Space Invaders AI Demo...");

            while (running) {
                update(oled.getWidth(), oled.getHeight());
                render(oled, image, g2d);
                TimeUnit.MILLISECONDS.sleep(1000 / fps);
            }
            g2d.dispose();
        }
        return 0;
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new Ssd1331SpaceInvaders()).execute(args));
    }
}
