/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.I2c;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * ADXL345 3-Axis, ±2 g/±4 g/±8 g/±16 g digital accelerometer example. I'm using I2C to communicate with the ADXL345 although SPI is
 * supported as well.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "Adxl345", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "ADXL345 3-Axis, ±2 g/±4 g/±8 g/±16 g digital accelerometer example.")
public class Adxl345 implements Callable<Integer> {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Adxl345.class);
    /**
     * Device option.
     */
    @Option(names = {"-d", "--device"}, description = "I2C device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/i2c-0";
    /**
     * Address option.
     */
    @Option(names = {"-a", "--address"}, description = "Address, ${DEFAULT-VALUE} by default.")
    private short address = 0x53;

    /**
     * Get data range setting.
     *
     * Register 0x31 -- DATA_FORMAT (Read/Write)
     *
     * These bits set the g range as described below.
     *
     * <pre>
     * Range Setting
     * | Setting |  g Range | Value |
     * |    00   | +/-  2 g |   0   |
     * |    01   | +/-  4 g |   1   |
     * |    10   | +/-  8 g |   2   |
     * |    11   | +/- 16 g |   3   |
     * </pre>
     *
     * @param handle I2C file handle.
     * @param addr Address.
     * @return Range.
     */
    public short getRange(final long handle, final short addr) {
        final var buf = new short[1];
        I2c.i2cReadReg8(handle, addr, (short) 0x31, buf);
        return (short) (buf[0] & 0x03);
    }

    /**
     * Set data range setting. Read the data format register to preserve bits. Update the data rate, make sure that the FULL-RES bit
     * is enabled for range scaling.
     *
     * Register 0x31 -- DATA_FORMAT (Read/Write)
     *
     * These bits set the g range as described below.
     *
     * <pre>
     * g Range Setting
     * | Setting |  g Range | Value |
     * |    00   | +/-  2 g |   0   |
     * |    01   | +/-  4 g |   1   |
     * |    10   | +/-  8 g |   2   |
     * |    11   | +/- 16 g |   3   |
     * </pre>
     *
     * @param handle I2C file handle.
     * @param addr Address.
     * @param value Range.
     */
    public void setRange(final long handle, final short addr, final short value) {
        // 0x08 sets fill resolution bit to enabled
        final var buf = new short[1];
        I2c.i2cReadReg8(handle, addr, (short) 0x31, buf);
        I2c.i2cWriteReg8(handle, addr, (short) 0x31, (short) (((buf[0] & ~0x0f) | value) | 0x08));
    }

    /**
     * Read full resolution mode setting.
     *
     * Register 0x31 -- DATA_FORMAT (Read/Write)
     *
     * When this bit is set to a value of 1, the device is in full resolution mode, where the output resolution increases with the g
     * range set by the range bits to maintain a 4 mg/LSB scale factor. When the FULL_RES bit is set to 0, the device is in 10-bit
     * mode, and the range bits determine the maximum g range and scale factor.
     *
     * @param handle I2C file handle.
     * @param addr Address.
     * @return Full resolution enabled setting
     */
    public boolean getFullResolution(final long handle, final short addr) {
        final var buf = new short[1];
        I2c.i2cReadReg8(handle, addr, (short) 0x31, buf);
        return (short) (buf[0] & 0x08) == (short) 0x08;
    }

    /**
     * Get the device bandwidth rate.
     *
     * Register 0x2C -- BW_RATE (Read/Write)
     *
     * These bits select the device bandwidth and output data rate. The default value is 0x0A, which translates to a 100 Hz output
     * data rate. An output data rate should be selected that is appropriate for the communication protocol and frequency selected.
     * Selecting too high of an output data rate with a low communication speed results in samples being discarded.
     *
     * <pre>
     * Typical Current Consumption vs. Data Rate
     * Output Data Rate (Hz) | Bandwidth (Hz) | Rate Code |   Value   | Idd (uA)
     *          3200         |      1600      |    1111   | 0xF or 15 |   140
     *          1600         |       800      |    1110   | 0xE or 14 |    90
     *           800         |       400      |    1101   | 0xD or 13 |   140
     *           400         |       200      |    1100   | 0xC or 12 |   140
     *           200         |       100      |    1011   | 0xB or 11 |   140
     *           100         |        50      |    1010   | 0xA or 10 |   140
     *            50         |        25      |    1001   | 0x9 or 9  |    90
     *            25         |      12.5      |    1000   | 0x8 or 8  |    60
     *          12.5         |      6.25      |    0111   | 0x7 or 7  |    50
     *          6.25         |      3.13      |    0110   | 0x6 or 6  |    45
     *          3.13         |      1.56      |    0101   | 0x5 or 5  |    40
     *          1.56         |      0.78      |    0100   | 0x4 or 4  |    34
     *          0.78         |      0.39      |    0011   | 0x3 or 3  |    23
     *          0.39         |      0.20      |    0010   | 0x2 or 2  |    23
     *          0.20         |      0.10      |    0001   | 0x1 or 1  |    23
     *          0.10         |      0.05      |    0000   | 0x0 or 0  |    23
     * </pre>
     *
     * @param handle I2C file handle.
     * @param addr Address.
     * @return Range.
     */
    public short getDataRate(final long handle, final short addr) {
        final var buf = new short[1];
        I2c.i2cReadReg8(handle, addr, (short) 0x2c, buf);
        return (short) (buf[0] & 0x0f);
    }

    /**
     * Set the device bandwidth rate.
     *
     * Register 0x2C -- BW_RATE (Read/Write)
     *
     * These bits select the device bandwidth and output data rate. The default value is 0x0A, which translates to a 100 Hz output
     * data rate. An output data rate should be selected that is appropriate for the communication protocol and frequency selected.
     * Selecting too high of an output data rate with a low communication speed results in samples being discarded. Note: The
     * LOW_POWER bits are currently ignored, we always keep the device in 'normal' mode.
     *
     * <pre>
     * Typical Current Consumption vs. Data Rate
     * Output Data Rate (Hz) | Bandwidth (Hz) | Rate Code |   Value   | Idd (uA)
     *          3200         |      1600      |    1111   | 0xF or 15 |   140
     *          1600         |       800      |    1110   | 0xE or 14 |    90
     *           800         |       400      |    1101   | 0xD or 13 |   140
     *           400         |       200      |    1100   | 0xC or 12 |   140
     *           200         |       100      |    1011   | 0xB or 11 |   140
     *           100         |        50      |    1010   | 0xA or 10 |   140
     *            50         |        25      |    1001   | 0x9 or 9  |    90
     *            25         |      12.5      |    1000   | 0x8 or 8  |    60
     *          12.5         |      6.25      |    0111   | 0x7 or 7  |    50
     *          6.25         |      3.13      |    0110   | 0x6 or 6  |    45
     *          3.13         |      1.56      |    0101   | 0x5 or 5  |    40
     *          1.56         |      0.78      |    0100   | 0x4 or 4  |    34
     *          0.78         |      0.39      |    0011   | 0x3 or 3  |    23
     *          0.39         |      0.20      |    0010   | 0x2 or 2  |    23
     *          0.20         |      0.10      |    0001   | 0x1 or 1  |    23
     *          0.10         |      0.05      |    0000   | 0x0 or 0  |    23
     * </pre>
     *
     * @param handle I2C file handle.
     * @param addr Address.
     * @param value Data rate.
     */
    public void setDataRate(final long handle, final short addr, final short value) {
        I2c.i2cWriteReg8(handle, addr, (short) 0x2c, (short) (value & 0x0f));
    }

    /**
     * Convert low and high bytes to 10-bit integer.
     *
     * @param lowByte Low byte from register.
     * @param highByte High byte from register.
     * @return Integer value composed of low and high bytes.
     */
    public int bytesToInt(final byte lowByte, final byte highByte) {
        // Convert the data to 10-bits
        var value = ((highByte & 0x03) * 256 + (lowByte & 0xff));
        if (value > 511) {
            value -= 1024;
        }
        return value;
    }

    /**
     * Retrieve x, y, z 10 bit data in 6 bytes.
     *
     * @param handle I2C file handle.
     * @param addr Address.
     * @return Map of Integers keyed by x, y, z.
     */
    public Map<String, Integer> read(final long handle, final short addr) {
        // Read all 6 registers at once
        final var data = new byte[6];
        I2c.i2cReadReg8(handle, addr, (short) 0x32, data);
        final Map<String, Integer> map = new HashMap<>();
        map.put("x", bytesToInt(data[0], data[1]));
        map.put("y", bytesToInt(data[2], data[3]));
        map.put("z", bytesToInt(data[4], data[5]));
        return map;
    }

    /**
     * Determines the scaling factor of raw values to obtain Gs.
     *
     * The scale factor changes dependent on other device settings, so be sure to get the scaling factor after writing desired
     * settings.
     *
     * @param range Use getRange.
     * @param resolution Use getFullResolution.
     * @return Raw value scaling factor.
     */
    public float getScalingFactor(final short range, final boolean resolution) {
        final var bits = resolution ? 10 + range : 10;
        final var gRange = 4f * (float) Math.pow(2, range);
        final var bitRange = (float) Math.pow(2, bits);
        return gRange / bitRange;
    }

    /**
     * Calculate scaling value.
     *
     * @param value raw value.
     * @param scalingFactor Use getScalingFactor.
     * @return Scaled value.
     */
    public float scaling(final int value, final float scalingFactor) {
        return value * scalingFactor * 9.8f;
    }

    /**
     * Main program.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        try (final var i2c = new I2c(device)) {
            // Check device ID
            final var buf = new short[1];
            I2c.i2cReadReg8(i2c.getHandle(), address, (short) 0x00, buf);
            if (buf[0] == 0xe5) {
                // Enable the accelerometer
                I2c.i2cWriteReg8(i2c.getHandle(), address, (short) 0x2d, (short) 0x08);
                // +/- 2g
                setRange(i2c.getHandle(), address, (short) 0x00);
                // 100 Hz
                setDataRate(i2c.getHandle(), address, (short) 0x0a);
                // Save off range and data rate
                final var range = getRange(i2c.getHandle(), address);
                final var dataRate = getDataRate(i2c.getHandle(), address);
                final var scalingFactor = getScalingFactor(range, getFullResolution(i2c.getHandle(), address));
                logger.info(String.format("Range = %d, data rate = %d, scaling factor = %f", range, dataRate, scalingFactor));
                for (var i = 0; i < 100; i++) {
                    final var data = read(i2c.getHandle(), address);
                    logger.info(String.format("x: %+5.2f, y: %+5.2f, z: %+5.2f", scaling(data.get("x"), scalingFactor), scaling(
                            data.get("y"), scalingFactor), scaling(data.get("z"), scalingFactor)));
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            } else {
                logger.error("Not ADXL345?");
            }
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
        System.exit(new CommandLine(new Adxl345()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
