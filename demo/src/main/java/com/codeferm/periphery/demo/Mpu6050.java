/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.I2c;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This is based on https://github.com/Raspoid/raspoid/blob/master/src/main/com/raspoid/additionalcomponents/MPU6050.java by Julien
 * Louette &amp; Gaël Wittorski. The idea here is to show you with simple changes you can use existing code with something complex like
 * the MPU 6050 and easily convert it to Java Periphery.
 *
 * <b>Implementation of the MPU6050 component.</b>
 *
 * <p>
 * <b>[datasheet - p.7]</b> Product Overview</p>
 *
 * <p>
 * The MPU-60X0 is the world’s first integrated 6-axis MotionTracking device that combines a 3-axis gyroscope, 3-axis accelerometer,
 * and a Digital Motion Processor™ (DMP) all in a small 4x4x0.9mm package. With its dedicated I2C sensor bus, it directly accepts
 * inputs from an external 3-axis compass to provide a complete 9-axis MotionFusion™ output.</p>
 *
 * <p>
 * The MPU-60X0 features three 16-bit analog-to-digital converters (ADCs) for digitizing the gyroscope outputs and three 16-bit ADCs
 * for digitizing the accelerometer outputs. For precision tracking of both fast and slow motions, the parts feature a
 * user-programmable gyroscope full-scale range of ±250, ±500, ±1000, and ±2000°/sec (dps) and a user-programmable accelerometer
 * full-scale range of ±2g, ±4g, ±8g, and ±16g.</p>
 *
 * <p>
 * Communication with all registers of the device is performed using I2C at 400kHz.</p>
 *
 * <p>
 * For power supply flexibility, the MPU-60X0 operates from VDD power supply voltage range of 2.375V-3.46V. Additionally, the
 * MPU-6050 provides a VLOGIC reference pin (in addition to its analog supply pin: VDD), which sets the logic levels of its I2C
 * interface. The VLOGIC voltage may be 1.8V±5% or VDD.</p>
 *
 * <p>
 * <b>[datasheet - p.10-11]</b> Features
 * <ul>
 * <li>Gyroscope features (triple-axis MEMS gyroscope)</li>
 * <li>Digital-output X-, Y-, and Z-Axis angular rate sensors (gyroscopes) with a user-programmable fullscale range of ±250, ±500,
 * ±1000, and ±2000°/sec</li>
 * <li>Integrated 16-bit ADCs enable simultaneous sampling of gyros</li>
 * <li>Enhanced bias and sensitivity temperature stability reduces the need for user calibration</li>
 * <li>Improved low-frequency noise performance</li>
 * <li>Digitally-programmable low-pass filter</li>
 * <li>Gyroscope operating current: 3.6mA</li>
 * <li>Standby current: 5µA</li>
 * <li>Factory calibrated sensitivity scale factor</li>
 * </ul>
 * <ul>
 * <li>Accelerometer features (triple-axis MEMS accelerometer)</li>
 * <li>Digital-output triple-axis accelerometer with a programmable full scale
 * range of ±2g, ±4g, ±8g and ±16g</li>
 * <li>Integrated 16-bit ADCs enable simultaneous sampling of accelerometers while requiring no external multiplexer</li>
 * <li>Accelerometer normal operating current: 500µA</li>
 * <li>Low power accelerometer mode current: 10µA at 1.25Hz, 20µA at 5Hz, 60µA
 * at 20Hz, 110µA at 40Hz</li>
 * <li>Orientation detection and signaling</li>
 * <li>Tap detection</li>
 * <li>User-programmable interrupts</li>
 * <li>High-G interrupt</li>
 * </ul>
 * <ul>
 * <li>Additional features</li>
 * <li>9-Axis MotionFusion by the on-chip Digital Motion Processor (DMP)</li>
 * <li>Auxiliary master I2C bus for reading data from external sensors (e.g., magnetometer)</li>
 * <li>3.9mA operating current when all 6 motion sensing axes and the DMP are enabled</li>
 * <li>VDD supply voltage range of 2.375V-3.46V</li>
 * <li>1024 byte FIFO buffer reduces power consumption by allowing host processor to read the data in bursts and then go into a
 * low-power mode as the MPU collects more data</li>
 * <li>Digital-output temperature sensor</li>
 * <li>User-programmable digital filters for gyroscope, accelerometer, and temp sensor</li>
 * <li>10,000 g shock tolerant</li>
 * <li>400kHz Fast Mode I2C for communicating with all registers</li>
 * </ul>
 * <ul>
 * <li>MotionProcessing</li>
 * <li>Internal Digital Motion Processing™ (DMP™) engine supports 3D MotionProcessing and gesture recognition algorithms</li>
 * <li>The MPU-60X0 collects gyroscope and accelerometer data while synchronizing data sampling at a user defined rate. The total
 * dataset obtained by the MPU-60X0 includes 3-Axis gyroscope data, 3-Axis accelerometer data, and temperature data. The MPU’s
 * calculated output to the system processor can also include heading data
 * from a digital 3-axis third party magnetometer.</li>
 * <li>The FIFO buffers the complete data set, reducing timing requirements on the system processor by allowing the processor burst
 * read the FIFO data. After burst reading the FIFO data, the system processor can save power by entering a low-power sleep mode
 * while the MPU collects more data.</li>
 * <li>Programmable interrupt supports features such as gesture recognition, panning, zooming, scrolling, tap detection, and shake
 * detection.</li>
 * <li>Digitally-programmable low-pass filters</li>
 * <li>Low-power pedometer functionality allows the host processor to sleep while the DMP maintains the step count.</li>
 * </ul>
 *
 * <p>
 * Datasheet: <a href="http://raspoid.com/download/datasheet/MPU6050">MPU6050 (part 1)</a>,
 * <a href="http://raspoid.com/download/datasheet/MPU6050_2">MPU6050 (part 2 - registers map and descriptions)</a></p>
 *
 * <p>
 * Note: the datasheet information related to this sensor are about ~100 pages long. We don't implemented all the features in this
 * driver, but we provide you the updateRegisterValue(int registerAddress, int registerValue) method from which you can easily
 * update the content of a register, and the readRegisterValue(int registerAddress) method from which you can easily read the
 * content of a register of the component.</p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "Mpu6050", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Six-Axis (Gyro + Accelerometer) MEMS MotionTracking example.")
public class Mpu6050 implements Callable<Integer> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Mpu6050.class);
    /**
     * Device option.
     */
    @Option(names = {"--device"}, description = "I2C device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/i2c-0";
    /**
     * Address option.
     */
    @Option(names = {"--address"}, description = "Address, ${DEFAULT-VALUE} by default.")
    private short address = 0x68;
    /**
     * I2C file handle.
     */
    private long handle;

    /*
     * -----------------------------------------------------------------------
     * DEFAULT VALUES
     * -----------------------------------------------------------------------
     */
    /**
     * Default address of the MPU6050 device.
     */
    public static final int DEFAULT_MPU6050_ADDRESS = 0x68;

    /**
     * Default value for the digital low pass filter (DLPF) setting for both gyroscope and accelerometer.
     */
    public static final int DEFAULT_DLPF_CFG = 0x06;

    /**
     * Default value for the sample rate divider.
     */
    public static final int DEFAULT_SMPLRT_DIV = 0x00;

    /**
     * Coefficient to convert an angle value from radians to degrees.
     */
    public static final double RADIAN_TO_DEGREE = 180. / Math.PI;

    /**
     * It is impossible to calculate an angle for the z axis from the accelerometer.
     */
    private static final double ACCEL_Z_ANGLE = 0;

    /*
     * -----------------------------------------------------------------------
     * REGISTERS ADDRESSES
     * -----------------------------------------------------------------------
     */
    /**
     * <b>[datasheet 2 - p.11]</b> Sample Rate Divider
     * <p>
     * This register specifies the divider from the gyroscope output rate used to generate
     * the Sample Rate for the MPU-60X0.</p>
     */
    public static final int MPU6050_REG_ADDR_SMPRT_DIV = 0x19; // 25

    /**
     * <b>[datasheet 2 - p.13]</b> Configuration
     * <p>
     * This register configures the external Frame Synchronization (FSYNC) pin sampling and
     * the Digital Low Pass Filter (DLPF) setting for both the gyroscopes and accelerometers.</p>
     */
    public static final int MPU6050_REG_ADDR_CONFIG = 0x1A; // 26

    /**
     * <b>[datasheet 2 - p.14]</b> Gyroscope Configuration
     * <p>
     * This register is used to trigger gyroscope self-test and configure the gyroscopes’ full
     * scale range</p>
     */
    public static final int MPU6050_REG_ADDR_GYRO_CONFIG = 0x1B; // 27

    /**
     * <b>[datasheet 2 - p.15]</b> Accelerometer Configuration
     * <p>
     * This register is used to trigger accelerometer self test and configure the accelerometer
     * full scale range. This register also configures the Digital High Pass Filter (DHPF).</p>
     */
    public static final int MPU6050_REG_ADDR_ACCEL_CONFIG = 0x1C; // 28

    /**
     * <b>[datasheet 2 - p.27]</b> Interrupt Enable
     * <p>
     * This register enables interrupt generation by interrupt sources.</p>
     */
    public static final int MPU6050_REG_ADDR_INT_ENABLE = 0x1A; // 56

    /**
     * <b>[datasheet 2 - p.40]</b> Power Management 1
     * <p>
     * This register allows the user to configure the power mode and clock source. It also provides
     * a bit for resetting the entire device, and a bit for disabling the temperature sensor.</p>
     */
    public static final int MPU6050_REG_ADDR_PWR_MGMT_1 = 0x6B; // 107

    /**
     * <b>[datasheet 2 - p.42]</b> Power Management 2
     * <p>
     * This register allows the user to configure the frequency of wake-ups in Accelerometer Only Low
     * Power Mode. This register also allows the user to put individual axes of the accelerometer and
     * gyroscope into standby mode.</p>
     */
    public static final int MPU6050_REG_ADDR_PWR_MGMT_2 = 0x6C; // 108

    /**
     * <b>[datasheet 2 - p.29]</b> Accelerometer Measurements
     * <p>
     * These registers store the most recent accelerometer measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_ACCEL_XOUT_H = 0x3B; // 59

    /**
     * <b>[datasheet 2 - p.29]</b> Accelerometer Measurements
     * <p>
     * These registers store the most recent accelerometer measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_ACCEL_XOUT_L = 0x3C; // 60

    /**
     * <b>[datasheet 2 - p.29]</b> Accelerometer Measurements
     * <p>
     * These registers store the most recent accelerometer measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_ACCEL_YOUT_H = 0x3D; // 61

    /**
     * <b>[datasheet 2 - p.29]</b> Accelerometer Measurements
     * <p>
     * These registers store the most recent accelerometer measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_ACCEL_YOUT_L = 0x3E; // 62

    /**
     * <b>[datasheet 2 - p.29]</b> Accelerometer Measurements
     * <p>
     * These registers store the most recent accelerometer measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_ACCEL_ZOUT_H = 0x3F; // 63

    /**
     * <b>[datasheet 2 - p.29]</b> Accelerometer Measurements
     * <p>
     * These registers store the most recent accelerometer measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_XOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_H
     * @see #MPU6050_REG_ADDR_ACCEL_YOUT_L
     * @see #MPU6050_REG_ADDR_ACCEL_ZOUT_H
     */
    public static final int MPU6050_REG_ADDR_ACCEL_ZOUT_L = 0x40; // 64

    /**
     * <b>[datasheet 2 - p.30]</b> Temperature Measurement
     * <p>
     * These registers store the most recent temperature sensor measurement.</p>
     *
     * @see #MPU6050_REG_ADDR_TEMP_OUT_L
     */
    public static final int MPU6050_REG_ADDR_TEMP_OUT_H = 0x41; // 65

    /**
     * <b>[datasheet 2 - p.30]</b> Temperature Measurement
     * <p>
     * These registers store the most recent temperature sensor measurement.</p>
     *
     * @see #MPU6050_REG_ADDR_TEMP_OUT_H
     */
    public static final int MPU6050_REG_ADDR_TEMP_OUT_L = 0x42; // 66

    /**
     * <b>[datasheet 2 - p.31]</b> Gyroscope Measurements
     * <p>
     * These registers store the most recent gyroscope measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_GYRO_XOUT_H = 0x43; // 67

    /**
     * <b>[datasheet 2 - p.31]</b> Gyroscope Measurements
     * <p>
     * These registers store the most recent gyroscope measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_GYRO_XOUT_L = 0x44; // 68

    /**
     * <b>[datasheet 2 - p.31]</b> Gyroscope Measurements
     * <p>
     * These registers store the most recent gyroscope measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_GYRO_YOUT_H = 0x45; // 69

    /**
     * <b>[datasheet 2 - p.31]</b> Gyroscope Measurements
     * <p>
     * These registers store the most recent gyroscope measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_GYRO_YOUT_L = 0x46; // 70

    /**
     * <b>[datasheet 2 - p.31]</b> Gyroscope Measurements
     * <p>
     * These registers store the most recent gyroscope measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_L
     */
    public static final int MPU6050_REG_ADDR_GYRO_ZOUT_H = 0x47; // 71

    /**
     * <b>[datasheet 2 - p.31]</b> Gyroscope Measurements
     * <p>
     * These registers store the most recent gyroscope measurements.</p>
     *
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_XOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_H
     * @see #MPU6050_REG_ADDR_GYRO_YOUT_L
     * @see #MPU6050_REG_ADDR_GYRO_ZOUT_H
     */
    public static final int MPU6050_REG_ADDR_GYRO_ZOUT_L = 0x48; // 72

    /*
     * -----------------------------------------------------------------------
     * VARIABLES
     * -----------------------------------------------------------------------
     */
    /**
     * Value used for the DLPF config.
     */
    private int dlpfCfg;

    /**
     * Value used for the sample rate divider.
     */
    private int smplrtDiv;

    /**
     * Sensitivity of the measures from the accelerometer.
     * Used to convert accelerometer values.
     */
    private double accelLSBSensitivity;

    /**
     * Sensitivity of the measures from the gyroscope.
     * Used to convert gyroscope values to degrees/sec.
     */
    private double gyroLSBSensitivity;

    private Thread updatingThread = null;
    private boolean updatingThreadStopped = true;
    private long lastUpdateTime = 0;

    // ACCELEROMETER
    /**
     * Last acceleration value, in g, retrieved from the accelerometer, for the x axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double accelAccelerationX = 0.;

    /**
     * Last acceleration value, in g, retrieved from the accelerometer, for the y axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double accelAccelerationY = 0.;

    /**
     * Last acceleration value, in g, retrieved from the accelerometer, for the z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double accelAccelerationZ = 0.;

    /**
     * Last angle value, in °, retrieved from the accelerometer, for the x axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double accelAngleX = 0.;

    /**
     * Last angle value, in °, retrieved from the accelerometer, for the y axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double accelAngleY = 0.;

    /**
     * Last angle value, in °, retrieved from the accelerometer, for the z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double accelAngleZ = 0.;

    // GYROSCOPE
    /**
     * Last angular speed value, in °/sec, retrieved from the gyroscope, for the x axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double gyroAngularSpeedX = 0.;

    /**
     * Last angular speed value, in °/sec, retrieved from the gyroscope, for the y axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double gyroAngularSpeedY = 0.;

    /**
     * Last angular speed value, in °/sec, retrieved from the gyroscope, for the z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double gyroAngularSpeedZ = 0.;

    /**
     * Last angle value, in °, calculated from the gyroscope, for the x axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double gyroAngleX = 0.;

    /**
     * Last angle value, in °, calculated from the gyroscope, for the y axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double gyroAngleY = 0.;

    /**
     * Last angle value, in °, calculated from the gyroscope, for the z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double gyroAngleZ = 0.;

    /**
     * Calculated offset for the angular speed from the gyroscope, for the x axis.
     */
    private double gyroAngularSpeedOffsetX = 0.;

    /**
     * Calculated offset for the angular speed from the gyroscope, for the y axis.
     */
    private double gyroAngularSpeedOffsetY = 0.;

    /**
     * Calculated offset for the angular speed from the gyroscope, for the z axis.
     */
    private double gyroAngularSpeedOffsetZ = 0.;

    // FILTERED
    /**
     * Last angle value, in °, calculated from the accelerometer and the gyroscope,
     * for the x axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double filteredAngleX = 0.;

    /**
     * Last angle value, in °, calculated from the accelerometer and the gyroscope,
     * for the y axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double filteredAngleY = 0.;

    /**
     * Last angle value, in °, calculated from the accelerometer and the gyroscope,
     * for the z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     */
    private double filteredAngleZ = 0.;

    /*
     * -----------------------------------------------------------------------
     * METHODS
     * -----------------------------------------------------------------------
     */
    /**
     * Returns the Sample Rate of the MPU6050.
     *
     * [datasheet 2 - p.12] The sensor output, FIFO output, and DMP sampling are
     * all based on the Sample Rate ('Fs' in the datasheet).
     *
     * The Sample Rate is generated by dividing the gyroscope output rate
     * by SMPLRT_DIV:
     * Sample Rate = Gyroscope Output Rate / (1 + SMPLRT_DIV)
     * where Gyroscope Output Rate = 8kHz when the DLPF is disabled (DLPF_CFG = 0 or 7),
     * and 1kHz when the DLPF is enabled (see Register 26)
     *
     * Note: The accelerometer output rate is 1kHz (accelerometer and not gyroscope !).
     * This means that for a Sample Rate greater than 1kHz, the same accelerometer
     * sample may be output to the FIFO, DMP, and sensor registers more than once.
     *
     * @return the sample rate, in Hz.
     */
    public int getSampleRate() {
        int gyroscopeOutputRate = dlpfCfg == 0 || dlpfCfg == 7 ? 8000 : 1000; // 8kHz if DLPG disabled, and 1kHz if enabled.
        return gyroscopeOutputRate / (1 + smplrtDiv);
    }

    /**
     * Sets the value of the DLPF config, according to the datasheet informations.
     *
     * The accelerometer and gyroscope are filtered according to the value of
     * DLPF_CFG as shown in the table [datasheet 2 - p.13].
     *
     * @param dlpfConfig the new DLPF_CFG value. Must be in the [0; 7] range,
     * where 0 and 7 are used to disable the DLPF.
     */
    public void setDLPFConfig(int dlpfConfig) {
        if (dlpfConfig > 7 || dlpfConfig < 0) {
            throw new IllegalArgumentException("The DLPF config must be in the 0..7 range.");
        }
        dlpfCfg = dlpfConfig;
        updateRegisterValue(MPU6050_REG_ADDR_CONFIG, dlpfCfg);
    }

    /**
     * Reads the most recent accelerometer values on MPU6050 for X, Y and Z axis,
     * and calculates the corresponding accelerations in g, according to the
     * selected AFS_SEL mode.
     *
     * @return [ACCEL_X, ACCEL_Y, ACCEL_Z], the accelerations in g for the x, y and z axis.
     */
    public double[] readScaledAccelerometerValues() {
        double accelX = readWord2C(MPU6050_REG_ADDR_ACCEL_XOUT_H);
        accelX /= accelLSBSensitivity;
        double accelY = readWord2C(MPU6050_REG_ADDR_ACCEL_YOUT_H);
        accelY /= accelLSBSensitivity;
        double accelZ = readWord2C(MPU6050_REG_ADDR_ACCEL_ZOUT_H);
        accelZ /= accelLSBSensitivity;

        return new double[]{accelX, accelY, -accelZ};
    }

    /**
     * Reads the most recent gyroscope values on the MPU6050 for X, Y and Z axis,
     * and calculates the corresponding angular speeds in degrees/sec,
     * according to the selected FS_SEL mode.
     *
     * @return [GYRO_X, GYRO_Y, GYRO_Z], the angular velocities in degrees/sec for the x, y and z axis.
     */
    public double[] readScaledGyroscopeValues() {
        double gyroX = readWord2C(MPU6050_REG_ADDR_GYRO_XOUT_H);
        gyroX /= gyroLSBSensitivity;
        double gyroY = readWord2C(MPU6050_REG_ADDR_GYRO_YOUT_H);
        gyroY /= gyroLSBSensitivity;
        double gyroZ = readWord2C(MPU6050_REG_ADDR_GYRO_ZOUT_H);
        gyroZ /= gyroLSBSensitivity;

        return new double[]{gyroX, gyroY, gyroZ};
    }

    /**
     * Callibrate the accelerometer and gyroscope sensors.
     */
    private void calibrateSensors() {
        logger.info("Calibration starting in 5 seconds (don't move the sensor)");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Calibration started (~5s) (don't move the sensor)");

        int nbReadings = 50;

        // Gyroscope offsets
        gyroAngularSpeedOffsetX = 0.;
        gyroAngularSpeedOffsetY = 0.;
        gyroAngularSpeedOffsetZ = 0.;
        for (int i = 0; i < nbReadings; i++) {
            double[] angularSpeeds = readScaledGyroscopeValues();
            gyroAngularSpeedOffsetX += angularSpeeds[0];
            gyroAngularSpeedOffsetY += angularSpeeds[1];
            gyroAngularSpeedOffsetZ += angularSpeeds[2];
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        gyroAngularSpeedOffsetX /= nbReadings;
        gyroAngularSpeedOffsetY /= nbReadings;
        gyroAngularSpeedOffsetZ /= nbReadings;

        logger.info("Calibration ended");
    }

    /**
     * Starts the thread responsible to update MPU6050 values in background.
     */
    public void startUpdatingThread() {
        if (updatingThread == null || !updatingThread.isAlive()) {
            updatingThreadStopped = false;
            lastUpdateTime = System.currentTimeMillis();
            updatingThread = new Thread(() -> {
                while (!updatingThreadStopped) {
                    updateValues();
                }
            });
            updatingThread.start();
        } else {
            logger.debug("Updating thread of the MPU6050 is already started.");
        }
    }

    /**
     * Stops the thread responsible to update MPU6050 values in background.
     *
     * @throws InterruptedException if any thread has interrupted the current thread.
     * The interrupted status of the current thread is cleared when this exception is thrown.
     */
    public void stopUpdatingThread() throws InterruptedException {
        updatingThreadStopped = true;
        try {
            updatingThread.join();
        } catch (InterruptedException e) {
            logger.info("Exception when joining the updating thread.");
            throw e;
        }
        updatingThread = null;
    }

    /**
     * Update values for the accelerometer angles, gyroscope angles and filtered angles values.
     * <p>
     * <i>This method is used with the updating thread.</i></p>
     */
    private void updateValues() {
        // Accelerometer
        double[] accelerations = readScaledAccelerometerValues();
        accelAccelerationX = accelerations[0];
        accelAccelerationY = accelerations[1];
        accelAccelerationZ = accelerations[2];
        accelAngleX = getAccelXAngle(accelAccelerationX, accelAccelerationY, accelAccelerationZ);
        accelAngleY = getAccelYAngle(accelAccelerationX, accelAccelerationY, accelAccelerationZ);
        accelAngleZ = getAccelZAngle();

        // Gyroscope
        double[] angularSpeeds = readScaledGyroscopeValues();
        gyroAngularSpeedX = angularSpeeds[0] - gyroAngularSpeedOffsetX;
        gyroAngularSpeedY = angularSpeeds[1] - gyroAngularSpeedOffsetY;
        gyroAngularSpeedZ = angularSpeeds[2] - gyroAngularSpeedOffsetZ;
        // angular speed * time = angle
        double dt = Math.abs(System.currentTimeMillis() - lastUpdateTime) / 1000.; // s
        double deltaGyroAngleX = gyroAngularSpeedX * dt;
        double deltaGyroAngleY = gyroAngularSpeedY * dt;
        double deltaGyroAngleZ = gyroAngularSpeedZ * dt;
        lastUpdateTime = System.currentTimeMillis();

        gyroAngleX += deltaGyroAngleX;
        gyroAngleY += deltaGyroAngleY;
        gyroAngleZ += deltaGyroAngleZ;

        // Complementary Filter
        double alpha = 0.96;
        filteredAngleX = alpha * (filteredAngleX + deltaGyroAngleX) + (1. - alpha) * accelAngleX;
        filteredAngleY = alpha * (filteredAngleY + deltaGyroAngleY) + (1. - alpha) * accelAngleY;
        filteredAngleZ = filteredAngleZ + deltaGyroAngleZ;
    }

    /**
     * Get the last acceleration values, in g, retrieved from the accelerometer,
     * for the x, y and z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     *
     * @return the accelerations for the x, y and z axis. [-1, -1, -1] if the updating thread isn't running.
     */
    public double[] getAccelAccelerations() {
        if (updatingThreadStopped) {
            return new double[]{-1., -1., -1.};
        }
        return new double[]{accelAccelerationX, accelAccelerationY, accelAccelerationZ};
    }

    /**
     * Get the last angle values, in °, retrieved from the accelerometer,
     * for the x, y and z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     *
     * @return the angle values for the x, y and z axis. [-1, -1, -1] if the updating thread isn't running.
     */
    public double[] getAccelAngles() {
        if (updatingThreadStopped) {
            return new double[]{-1., -1., -1.};
        }
        return new double[]{accelAngleX, accelAngleY, accelAngleZ};
    }

    /**
     * Get the last angular speed values, in °/sec, retrieved from the gyroscope,
     * for the x, y and z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     *
     * @return the angular speed values for the x, y and z axis. [-1, -1, -1] if the updating thread isn't running.
     */
    public double[] getGyroAngularSpeeds() {
        if (updatingThreadStopped) {
            return new double[]{-1., -1., -1.};
        }
        return new double[]{gyroAngularSpeedX, gyroAngularSpeedY, gyroAngularSpeedZ};
    }

    /**
     * Get the last angles values, in °, retrieved from the gyroscope,
     * for the x, y and z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     *
     * @return the angles values from the gyroscope for the x, y and z axis. [-1, -1, -1] if the updating thread isn't running.
     */
    public double[] getGyroAngles() {
        if (updatingThreadStopped) {
            return new double[]{-1., -1., -1.};
        }
        return new double[]{gyroAngleX, gyroAngleY, gyroAngleZ};
    }

    /**
     * Get the calculated offsets for the angular speeds from the gyroscope,
     * for the x, y and z axis.
     * <p>
     * <i>(calculated with the calibration function)</i></p>
     *
     * @return the offsets for the angular speeds from the gyroscope.
     */
    public double[] getGyroAngularSpeedsOffsets() {
        return new double[]{gyroAngularSpeedOffsetX, gyroAngularSpeedOffsetY, gyroAngularSpeedOffsetZ};
    }

    /**
     * Last angle value, in °, calculated from the accelerometer and the gyroscope,
     * for the x, y and z axis.
     * <p>
     * <i>(using the updating thread)</i></p>
     *
     * @return the angles values, in °, filtered with values from the accelerometer and the gyroscope.
     */
    public double[] getFilteredAngles() {
        if (updatingThreadStopped) {
            return new double[]{-1., -1., -1.};
        }
        return new double[]{filteredAngleX, filteredAngleY, filteredAngleZ};
    }

    /*
     * -----------------------------------------------------------------------
     * UTILS
     * -----------------------------------------------------------------------
     */
    /**
     * This method updates the value of a specific register with a specific value.
     * The method also checks that the update was successful.
     *
     * @param register The register to update.
     * @param value The value to set in the register.
     */
    public void updateRegisterValue(int register, int value) {
        I2c.i2cWriteReg8(handle, address, (short) register, (short) value);
        // we check that the value of the register has been updated
        final var regVal = new short[1];
        I2c.i2cReadReg8(handle, address, (short) register, regVal);
        if (regVal[0] != value) {
            throw new RuntimeException(String.format("Error when updating the MPU6050 register value (register: %d, value: %d",
                    register, value));
        }
    }

    /**
     * Reads the content of two consecutive registers, starting at registerAddress,
     * and returns the int representation of the combination of those registers,
     * with a two's complement representation.
     *
     * @param register the address of the first register to read.
     * @return the int representation of the combination of the two consecutive
     * registers, with a two's complement representation.
     */
    private int readWord2C(int register) {
        final var regVal = new int[1];
        I2c.i2cReadWord8(handle, address, (short) register, regVal);
        return regVal[0];
    }

    /**
     * Get the distance between two points.
     *
     * @param a the first point.
     * @param b the second point.
     * @return the distance between a and b.
     */
    private double distance(double a, double b) {
        return Math.sqrt(a * a + b * b);
    }

    private double getAccelXAngle(double x, double y, double z) {
        // v1 - 360
        double radians = Math.atan2(y, distance(x, z));
        double delta = 0.;
        if (y >= 0) {
            if (z >= 0) {
                // pass
            } else {
                radians *= -1;
                delta = 180.;
            }
        } else {
            if (z <= 0) {
                radians *= -1;
                delta = 180.;
            } else {
                delta = 360.;
            }
        }
        return radians * RADIAN_TO_DEGREE + delta;
    }

    private double getAccelYAngle(double x, double y, double z) {
        // v2
        double tan = -1 * x / distance(y, z);
        double delta = 0.;
        if (x <= 0) {
            if (z >= 0) {
                // q1
                // nothing to do
            } else {
                // q2
                tan *= -1;
                delta = 180.;
            }
        } else {
            if (z <= 0) {
                // q3
                tan *= -1;
                delta = 180.;
            } else {
                // q4
                delta = 360.;
            }
        }

        return Math.atan(tan) * RADIAN_TO_DEGREE + delta;
    }

    private double getAccelZAngle() {
        return ACCEL_Z_ANGLE;
    }

    /**
     * Returns the String representation of an angle, in the "x.xxxx°" format.
     *
     * @param angle the angle to convert.
     * @return the String representation of an angle, in the "x.xxxx°" format.
     */
    public static String angleToString(double angle) {
        return String.format("%.4f", angle) + "°";
    }

    /**
     * Returns the String representation of an acceleration value, in the "x.xxxxxxg" format.
     *
     * @param accel the acceleration to convert.
     * @return the String representation of an acceleration value, in the "x.xxxxxxg" format.
     */
    public static String accelToString(double accel) {
        return String.format("%.6f", accel) + "g";
    }

    /**
     * Returns the String representation of an angular speed value, in the "x.xxxx°/s" format.
     *
     * @param angularSpeed the angular speed to convert.
     * @return the String representation of an angular speed value, in the "x.xxxx°/s" format.
     */
    public static String angularSpeedToString(double angularSpeed) {
        return String.format("%.4f", angularSpeed) + "°/s";
    }

    /**
     * Returns a String representation of a triplet of values, in the "x: X\t y: Y\t z: Z" format.
     *
     * @param x the first value of the triplet.
     * @param y the second value of the triplet.
     * @param z the thirs value of the triplet.
     * @return a String representation of a triplet of values, in the "x: X\t y: Y\t z: Z" format.
     */
    public static String xyzValuesToString(String x, String y, String z) {
        return "x: " + x + "\ty: " + y + "\tz: " + z;
    }

    /**
     * Run thread.
     */
    public void run() {
        startUpdatingThread();
        for (int i = 0; i < 10; i++) {
            // Accelerometer
            logger.info("Accelerometer:");
            double[] accelAngles = getAccelAngles();
            logger.info(xyzValuesToString(angleToString(accelAngles[0]), angleToString(accelAngles[1]), angleToString(
                    accelAngles[2])));
            logger.info("Accelerations:");
            double[] accelAccelerations = getAccelAccelerations();
            logger.info(xyzValuesToString(accelToString(accelAccelerations[0]), accelToString(accelAccelerations[1]), accelToString(
                    accelAccelerations[2])));
            // Gyroscope
            logger.info("Gyroscope:");
            double[] gyroAngles = getGyroAngles();
            logger.info(xyzValuesToString(angleToString(gyroAngles[0]), angleToString(gyroAngles[1]), angleToString(gyroAngles[2])));
            double[] gyroAngularSpeeds = getGyroAngularSpeeds();
            logger.info(xyzValuesToString(angularSpeedToString(gyroAngularSpeeds[0]), angularSpeedToString(gyroAngularSpeeds[1]),
                    angularSpeedToString(gyroAngularSpeeds[2])));
            // Filtered angles
            logger.info("Filtered angles:");
            double[] filteredAngles = getFilteredAngles();
            logger.info(xyzValuesToString(angleToString(filteredAngles[0]),
                    angleToString(filteredAngles[1]), angleToString(filteredAngles[2])));
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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
            // Assign I2C file handle
            handle = i2c.getHandle();
            dlpfCfg = DEFAULT_DLPF_CFG;
            smplrtDiv = DEFAULT_SMPLRT_DIV;

            // 1. waking up the MPU6050 (0x00 = 0000 0000) as it starts in sleep mode.
            updateRegisterValue(MPU6050_REG_ADDR_PWR_MGMT_1, 0x00);

            // 2. sample rate divider
            // The sensor register output, FIFO output, and DMP sampling are all based on the Sample Rate.
            // The Sample Rate is generated by dividing the gyroscope output rate by SMPLRT_DIV:
            //      Sample Rate = Gyroscope Output Rate / (1 + SMPLRT_DIV)
            // where Gyroscope Output Rate = 8kHz when the DLPF is disabled (DLPF_CFG = 0 or 7),
            // and 1kHz when the DLPF is enabled (see register 26).
            // SMPLRT_DIV set the rate to the default value : Sample Rate = Gyroscope Rate.
            updateRegisterValue(MPU6050_REG_ADDR_SMPRT_DIV, smplrtDiv);

            // 3. This register configures the external Frame Synchronization (FSYNC) 
            // pin sampling and the Digital Low Pass Filter (DLPF) setting for both 
            // the gyroscopes and accelerometers.
            setDLPFConfig(dlpfCfg);

            // 4. Gyroscope configuration
            // FS_SEL selects the full scale range of the gyroscope outputs.
            byte fsSel = 0 << 3; // FS_SEL +- 250 °/s
            gyroLSBSensitivity = 131.; // cfr [datasheet 2 - p.31]
            updateRegisterValue(MPU6050_REG_ADDR_GYRO_CONFIG, fsSel);

            // 5. Accelerometer configuration [datasheet 2 - p.29]
            byte afsSel = 0; // AFS_SEL full scale range: ± 2g. LSB sensitivity : 16384 LSB/g
            accelLSBSensitivity = 16384.; // LSB Sensitivity corresponding to AFS_SEL 0
            updateRegisterValue(MPU6050_REG_ADDR_ACCEL_CONFIG, afsSel);

            // 6. Disable interrupts
            updateRegisterValue(MPU6050_REG_ADDR_INT_ENABLE, 0x00);

            // 7. Disable standby mode
            updateRegisterValue(MPU6050_REG_ADDR_PWR_MGMT_2, 0x00);

            calibrateSensors();
            run();
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
        System.exit(new CommandLine(new Mpu6050()).registerConverter(Byte.class, Byte::decode).registerConverter(Byte.TYPE,
                Byte::decode).registerConverter(Short.class, Short::decode).registerConverter(Short.TYPE, Short::decode).
                registerConverter(Integer.class, Integer::decode).registerConverter(Integer.TYPE, Integer::decode).
                registerConverter(Long.class, Long::decode).registerConverter(Long.TYPE, Long::decode).execute(args));
    }
}
