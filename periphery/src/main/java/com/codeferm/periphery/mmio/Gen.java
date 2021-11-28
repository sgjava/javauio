/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.mmio;

import static com.codeferm.periphery.Common.cString;
import com.codeferm.periphery.Gpio;
import static com.codeferm.periphery.Gpio.GPIO_BIAS_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_DIR_OUT;
import static com.codeferm.periphery.Gpio.GPIO_DRIVE_DEFAULT;
import static com.codeferm.periphery.Gpio.GPIO_EDGE_NONE;
import com.codeferm.periphery.Mmio;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import picocli.CommandLine;

/**
 * GPIO data register offset and mask generator using GPIO device and MMIO.
 *
 * Make sure you disable all hardware in armbian-config System, Hardware and remove console=serial from /boot/armbianEnv.txt. You
 * want multi-function pins to act as GPIO pins. The idea here is to generate data register offsets and masks, so you can build MMIO
 * based GPIO code without doing it manually from the datasheet. The only thing you need the datasheet for is building the property
 * file and validation.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class Gen implements Callable<Integer> {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Gen.class);
    /**
     * Input file.
     */
    @CommandLine.Option(names = {"-i", "--in"}, description = "Input property file name")
    private String inFileName = "duo.properties";
    /**
     * Ouptut file.
     */
    @CommandLine.Option(names = {"-o", "--out"}, description = "Output property file name")
    private String outFileName = "out.properties";

    /**
     * Return values from all registers.
     *
     * @param mmioHandle MMIO handles.
     * @param groupChip Chip group is on.
     * @param dataOffset Data register offsets in chip.
     * @return List of register values.
     */
    public List<Integer> getRegValues(final List<Long> mmioHandle, final List<Integer> groupChip, final List<Integer> dataOffset) {
        final var list = new ArrayList<Integer>();
        final var value = new int[1];
        // Read all groups
        for (int chip = 0; chip < groupChip.size(); chip++) {
            Mmio.mmioRead32(mmioHandle.get(groupChip.get(chip)), dataOffset.get(chip), value);
            list.add(value[0]);
        }
        return list;
    }

    /**
     * Compare list values and return index where difference is found. Filter is used to select on desired register name.
     *
     * @param list1 First list.
     * @param list2 Second list.
     * @return Index of difference.
     */
    public int listDiff(final List<Integer> list1, final List<Integer> list2) {
        var i = 0;
        // Look for difference based on filter and exit on first instance
        while (i < list1.size() && list1.get(i).equals(list2.get(i))) {
            i++;
        }
        // No difference
        if (i == list1.size()) {
            i = -1;
        }
        return i;
    }

    /**
     * Return positive difference between 2 values for bit mask.
     *
     * @param value1 First value.
     * @param value2 Second value.
     * @return Diff value.
     */
    public int valueDiff(final int value1, final int value2) {
        int diff;
        if (value1 > value2) {
            diff = value1 - value2;
        } else {
            diff = value2 - value1;
        }
        return diff;
    }

    /**
     * Set data register info in pin DTO.
     *
     * @param pin Pin DTO.
     * @param mmioHandle MMIO handles.
     * @param groupChip Chips ports are on.
     * @param groupName Pin group names.
     * @param dataInOnOffset Data register in on offsets.
     * @param dataInOffOffset Data register in 0ff offsets.
     * @param dataOutOnOffset Data register out on offsets.
     * @param dataOutOffOffset Data register out off offsets.
     * @param useInputDataReg Use input register instead of output register.
     */
    public void setDataReg(final Pin pin, final List<Long> mmioHandle, final List<Integer> groupChip, final List<String> groupName,
            final List<Integer> dataInOnOffset, final List<Integer> dataInOffOffset, final List<Integer> dataOutOnOffset,
            final List<Integer> dataOutOffOffset, final boolean useInputDataReg) {
        final List<Integer> dataOffset;
        // Use input data register to check for changes
        if (useInputDataReg) {
            dataOffset = dataInOnOffset;
        } else {
            dataOffset = dataOutOnOffset;
        }
        final var dev = String.format("/dev/gpiochip%d", pin.getKey().getChip());
        // Set pin for input, output and look for delta
        try (final var gpio = new Gpio(dev, pin.getKey().getPin(), new Gpio.GpioConfig().setBias(GPIO_BIAS_DEFAULT).
                setDirection(GPIO_DIR_OUT).setDrive(GPIO_DRIVE_DEFAULT).setEdge(GPIO_EDGE_NONE).setInverted(false).setLabel(cString(
                Gen.class.getSimpleName())))) {
            Gpio.gpioWrite(gpio.getHandle(), false);
            final var list1 = getRegValues(mmioHandle, groupChip, dataOffset);
            Gpio.gpioWrite(gpio.getHandle(), true);
            final var list2 = getRegValues(mmioHandle, groupChip, dataOffset);
            // Find the register delta
            final var reg = listDiff(list1, list2);
            // Make sure a delta is detected
            if (reg >= 0) {
                pin.setGroupName(groupName.get(reg)).setDataInOn(new Register("IN_ON", dataInOnOffset.get(reg % dataInOnOffset.
                        size()), valueDiff(list1.get(reg), list2.get(reg)))).setDataInOff(new Register("IN_OFF", dataInOffOffset.
                        get(reg % dataInOffOffset.size()), valueDiff(list1.get(reg), list2.get(reg)))).setDataOutOn(new Register(
                        "OUT_ON", dataOutOnOffset.get(reg % dataOutOnOffset.size()), valueDiff(list1.get(reg), list2.get(reg)))).
                        setDataOutOff(new Register("OUT_OFF", dataOutOffOffset.get(reg % dataOutOffOffset.size()), valueDiff(list1.
                                get(reg), list2.get(reg))));
                // If data out uses same register for on/off then generate AND mask for off.
                if (pin.getDataOutOn().getOffset().equals(pin.getDataOutOff().getOffset())) {
                    pin.getDataOutOff().setMask(pin.getDataOutOff().getMask() ^ 0xffffffff);
                }
            } else {
                logger.warn(String.format("Chip %d Pin %d data register change not detected", pin.getKey().getChip(),
                        pin.getKey().getPin()));
            }
        } catch (RuntimeException e) {
            logger.error(String.format("Chip %d Pin %d Error %s", pin.getKey().getChip(), pin.getKey().getPin(), e.getMessage()));
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
        final var file = new File();
        final Map<PinKey, Pin> pinMap = file.parseInput(inFileName);
        // Make sure we have pins loaded
        if (!pinMap.isEmpty()) {
            final List<Long> mmioHandle = new ArrayList<>();
            // Open MMIO for each chip
            for (int i = 0; i < file.getChips().size(); i++) {
                final var mmio = new Mmio(file.getChips().get(i), file.getMmioSize().get(i), file.getMemPath());
                mmioHandle.add(mmio.getHandle());
            }
            // Set register offset and mask for each pin
            pinMap.entrySet().stream().map((entry) -> entry.getValue()).forEachOrdered((value) -> {
                setDataReg(value, mmioHandle, file.getGroupChip(), file.getGroupName(), file.getDataInOnOffset(), file.
                        getDataInOffOffset(), file.getDataOutOnOffset(), file.getDataOutOffOffset(), file.isUseInputDataReg());
            });
            // Generate properties file
            file.genProperties(pinMap, inFileName, outFileName);
            // Close MMIO for each handle
            mmioHandle.forEach((handle) -> {
                Mmio.mmioClose(handle);
            });
        } else {
            logger.error("Pin map empty. Make sure you have a valid property file.");
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
        System.exit(new CommandLine(new Gen()).execute(args));
    }
}
