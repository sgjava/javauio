/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.all.demo;

import static com.codeferm.periphery.Common.cString;
import com.codeferm.periphery.Common;
import com.codeferm.periphery.Gpio;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT;
import static com.codeferm.periphery.Gpio.GPIO_DRIVE_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_NONE;
import static com.codeferm.periphery.Gpio.GPIO_EVENT_CLOCK_REALTIME;
import com.codeferm.u8g2.U8g2;
import com.codeferm.u8g2.demo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Use mono display and GPIO to show animated LED status with dynamic positioning.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "LedDisplay", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Blink LED and show animated sprite on mono display")
public class LedDisplay extends Base {

    private static final Logger logger = LoggerFactory.getLogger(LedDisplay.class);
    /**
     * GPIO device.
     */
    @CommandLine.Option(names = {"-d", "--device"}, description = "GPIO device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/gpiochip0";
    /**
     * GPIO line.
     */
    @CommandLine.Option(names = {"-l", "--line"}, description = "GPIO line, ${DEFAULT-VALUE} by default.")
    private int line = 203;
    /**
     * Realistic LED Off: Outline with a small internal reflection highlight.
     */
    private static final byte[] LED_OFF = {
        (byte) 0x07, (byte) 0xe0, (byte) 0x08, (byte) 0x10, (byte) 0x13, (byte) 0x08, (byte) 0x12, (byte) 0x08,
        (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08, (byte) 0x10, (byte) 0x08,
        (byte) 0x10, (byte) 0x08, (byte) 0x08, (byte) 0x10, (byte) 0x04, (byte) 0x20, (byte) 0x03, (byte) 0xc0,
        (byte) 0x02, (byte) 0x40, (byte) 0x02, (byte) 0x40, (byte) 0x03, (byte) 0xc0, (byte) 0x00, (byte) 0x00
    };
    /**
     * Realistic LED On: Solid core with a "glow" halo around the top.
     */
    private static final byte[] LED_ON = {
        (byte) 0x07, (byte) 0xe0, (byte) 0x1f, (byte) 0xf8, (byte) 0x3f, (byte) 0xfc, (byte) 0x7f, (byte) 0xfe,
        (byte) 0x7f, (byte) 0xfe, (byte) 0x7f, (byte) 0xfe, (byte) 0x7f, (byte) 0xfe, (byte) 0x7f, (byte) 0xfe,
        (byte) 0x7f, (byte) 0xfe, (byte) 0x3f, (byte) 0xfc, (byte) 0x1f, (byte) 0xf8, (byte) 0x03, (byte) 0xc0,
        (byte) 0x02, (byte) 0x40, (byte) 0x02, (byte) 0x40, (byte) 0x03, (byte) 0xc0, (byte) 0x00, (byte) 0x00
    };

    /**
     * Updates display with centered sprite and status text.
     *
     * @param on True for ON sprite, false for OFF.
     * @param text Status string.
     */
    private void drawLedStatus(final boolean on, final String text) {
        var u8 = getU8g2();
        var sprite = on ? LED_ON : LED_OFF;
        var spritePtr = Common.malloc(sprite.length);
        // Calculate dynamic center positions using base class width and height
        var centerX = (getWidth() - 16) / 2;
        // Lift sprite slightly for text space
        var centerY = (getHeight() / 2) - 12;
        try {
            Common.moveJavaToNative(spritePtr, sprite, sprite.length);
            U8g2.clearBuffer(u8);
            // Render sprite centered
            U8g2.drawBitmap(u8, centerX, centerY, 2, 16, spritePtr);
            U8g2.sendBuffer(u8);
        } finally {
            Common.free(spritePtr);
        }
    }

    @Override
    public Integer call() throws InterruptedException {
        // Setup display through base class
        var exitCode = super.call();
        setSleep(1000);
        try (final var gpio = new Gpio(device, line, Gpio.GpioConfig.builder()
                .bias(GPIO_BIAS_DEFAULT).direction(GPIO_DIR_OUT)
                .drive(GPIO_DRIVE_DEFAULT).edge(GPIO_EDGE_NONE)
                .inverted(false).label(cString(LedDisplay.class.getSimpleName()))
                .event_clock(GPIO_EVENT_CLOCK_REALTIME).debounce_us(0).build())) {
            logger.info("Blinking LED with dynamic animation");
            var i = 0;
            while (i < 10) {
                // LED ON
                Gpio.gpioWrite(gpio.getHandle(), true);
                drawLedStatus(true, "LED ON ");
                getDisplay().sleep(500);
                // LED OFF
                Gpio.gpioWrite(gpio.getHandle(), false);
                drawLedStatus(false, "LED OFF");
                getDisplay().sleep(500);
                i++;
            }
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            exitCode = 1;
        }
        done();
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new LedDisplay()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
