/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.mmio;

import static com.codeferm.periphery.Common.cString;
import com.codeferm.periphery.Gpio;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_DISABLE;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_PULL_DOWN;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_PULL_UP;
import static com.codeferm.periphery.Gpio.GPIO_DIR_IN;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT;
import static com.codeferm.periphery.Gpio.GPIO_DRIVE_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_NONE;
import com.codeferm.periphery.Mmio;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import picocli.CommandLine;

/**
 * Scan memory for changes based on start address, range and GPIO chip and line.
 *
 * Make sure you disable all hardware in armbian-config System, Hardware and remove console=serial from /boot/armbianEnv.txt. You
 * want multi-function pins to act as GPIO pins.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@CommandLine.Command(name = "memscan", mixinStandardHelpOptions = true, version = "memscan 1.0.0",
        description = "Use GPIO device to detect memory changes")
public class MemScan implements Callable<Integer> {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MemScan.class);
    /**
     * MMIO path.
     */
    @CommandLine.Option(names = {"-p", "--path"}, description = "Path defaults to /dev/mem")
    private String path = "/dev/mem";
    /**
     * Memory address.
     */
    @CommandLine.Option(names = {"-a", "--address"}, description = "Memorry address defaults to 0x00")
    private long address = 0x00;
    /**
     * Memory size to scan.
     */
    @CommandLine.Option(names = {"-w", "--words"}, description = "32 bit words to read defaults to 0x01")
    private long words = 0x01;
    /**
     * Device option.
     */
    @CommandLine.Option(names = {"-d", "--device"}, description = "GPIO device defaults to 0")
    private int device = 0;
    /**
     * Line option.
     */
    @CommandLine.Option(names = {"-l", "--line"}, description = "GPIO line defaults to 1")
    private int line = 1;

    /**
     * Return values from all registers.
     *
     * @param mmioHandle MMIO handle.
     * @return List of register values.
     */
    public List<Integer> getRegValues(final Long mmioHandle) {
        final var list = new ArrayList<Integer>();
        final var value = new int[1];
        for (long i = 0; i < words; i++) {
            Mmio.mmioRead32(mmioHandle, i * 4, value);
            list.add(value[0]);
        }
        return list;
    }

    /**
     * Compare list values and log difference.
     *
     * @param list1 First list.
     * @param list2 Second list.
     * @param text Description text.
     */
    public void listDiff(final List<Integer> list1, final List<Integer> list2, final String text) {
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                int diff;
                if (list1.get(i) > list2.get(i)) {
                    diff = list1.get(i) - list2.get(i);
                } else {
                    diff = list2.get(i) - list1.get(i);
                }
                logger.info(String.format("%s difference found at offset 0x%08x before 0x%08x after 0x%08x difference 0x%08x", text,
                        i * 4, list1.get(i), list2.get(i), diff));
            }
        }
    }

    /**
     * Use GPIO device to detect configuration changes.
     *
     * @param mmioHandle MMIO handle.
     */
    public void detectMode(final long mmioHandle) {
        final var dev = String.format("/dev/gpiochip%d", device);
        // Set pin for input, output and look for delta
        try (final var gpio = new Gpio(dev, line, new Gpio.GpioConfig().setBias(GPIO_BIAS_DEFAULT).setDirection(GPIO_DIR_IN).setDrive(
                GPIO_DRIVE_DEFAULT).setEdge(GPIO_EDGE_NONE).setInverted(false).setLabel(cString(
                MemScan.class.getSimpleName())))) {
            final var list1 = getRegValues(mmioHandle);
            Gpio.gpioSetDirection(gpio.getHandle(), GPIO_DIR_OUT);
            final var list2 = getRegValues(mmioHandle);
            // Show the register delta
            listDiff(list1, list2, "Mode");
        } catch (RuntimeException e) {
            logger.error(String.format("Device %d line %d Error %s", device, line, e.getMessage()));
        }
    }

    /**
     * Use GPIO device to detect data changes.
     *
     * @param mmioHandle MMIO handle.
     */
    public void detectData(final long mmioHandle) {
        final var dev = String.format("/dev/gpiochip%d", device);
        // Set pin for input, output and look for delta
        try (final var gpio = new Gpio(dev, line, new Gpio.GpioConfig().setBias(GPIO_BIAS_DEFAULT).setDirection(GPIO_DIR_OUT).setDrive(
                GPIO_DRIVE_DEFAULT).setEdge(GPIO_EDGE_NONE).setInverted(false).setLabel(cString(
                MemScan.class.getSimpleName())))) {
            Gpio.gpioWrite(gpio.getHandle(), false);
            final var list1 = getRegValues(mmioHandle);
            Gpio.gpioWrite(gpio.getHandle(), true);
            final var list2 = getRegValues(mmioHandle);
            // Show the register delta
            listDiff(list1, list2, "Data");
        } catch (RuntimeException e) {
            logger.error(String.format("Device %d line %d Error %s", device, line, e.getMessage()));
        }
    }

    /**
     * Use GPIO device to detect pull changes.
     *
     * @param mmioHandle MMIO handle.
     */
    public void detectPull(final long mmioHandle) {
        final var dev = String.format("/dev/gpiochip%d", device);
        // Set pin for input, output and look for delta
        try (final var gpio = new Gpio(dev, line, new Gpio.GpioConfig().setBias(GPIO_BIAS_DISABLE).setDirection(GPIO_DIR_IN).setDrive(
                GPIO_DRIVE_DEFAULT).setEdge(GPIO_EDGE_NONE).setInverted(false).setLabel(cString(
                MemScan.class.getSimpleName())))) {
            var list1 = getRegValues(mmioHandle);
            Gpio.gpioSetBias(gpio.getHandle(), GPIO_BIAS_PULL_UP);
            var list2 = getRegValues(mmioHandle);
            // Show the register delta
            listDiff(list1, list2, "Pull up");
            list1 = getRegValues(mmioHandle);
            Gpio.gpioSetBias(gpio.getHandle(), GPIO_BIAS_PULL_DOWN);
            list2 = getRegValues(mmioHandle);
            // Show the register delta
            listDiff(list1, list2, "Pull down");
        } catch (RuntimeException e) {
            logger.error(String.format("Device %d line %d Error %s", device, line, e.getMessage()));
        }
    }

    /**
     * Detect changes made by GPIO at register level.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        logger.debug(String.format("Memory address 0x%08x words 0x%08x", address, words));
        try (final var mmio = new Mmio(address, words * 4, path)) {
            detectMode(mmio.getHandle());
            detectData(mmio.getHandle());
            detectPull(mmio.getHandle());
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Main parsing, error handling and handling user requests for usage help or version help are done with one line of code.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new MemScan()).registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE,
                Long::decode).execute(args));
    }
}
