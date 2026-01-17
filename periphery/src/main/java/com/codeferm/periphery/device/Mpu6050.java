/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.I2c;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * The MPU-60X0 features six 16-bit analog-to-digital converters (ADCs) for digitizing the gyroscope and accelerometer outputs. For
 * precision tracking of both fast and slow motions, the parts feature a user-programmable gyroscope full-scale range of ±250, ±500,
 * ±1000, and ±2000°/sec (dps) and a user-programmable accelerometer full-scale range of ±2g, ±4g, ±8g, and ±16g.</p>
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class Mpu6050 implements AutoCloseable {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Mpu6050.class);

    /*
     * -----------------------------------------------------------------------
     * DEFAULT VALUES
     * -----------------------------------------------------------------------
     */
    public static final int DEFAULT_MPU6050_ADDRESS = 0x68;
    public static final int DEFAULT_DLPF_CFG = 0x06;
    public static final int DEFAULT_SMPLRT_DIV = 0x00;

    /**
     * Constant used to convert radians to degrees.
     */
    public static final double RADIAN_TO_DEGREE = 180. / Math.PI;

    /**
     * The angle of the z-axis (perpendicular to the earth's surface).
     */
    private static final double ACCEL_Z_ANGLE = 0;

    /*
     * -----------------------------------------------------------------------
     * REGISTERS ADDRESSES
     * -----------------------------------------------------------------------
     */
    /**
     * <b>[datasheet 2 - p.11]</b> Sample Rate Divider
     */
    public static final int MPU6050_REG_ADDR_SMPRT_DIV = 0x19; // 25

    /**
     * <b>[datasheet 2 - p.13]</b> Configuration
     */
    public static final int MPU6050_REG_ADDR_CONFIG = 0x1A; // 26

    /**
     * <b>[datasheet 2 - p.14]</b> Gyroscope Configuration
     */
    public static final int MPU6050_REG_ADDR_GYRO_CONFIG = 0x1B; // 27

    /**
     * <b>[datasheet 2 - p.15]</b> Accelerometer Configuration
     */
    public static final int MPU6050_REG_ADDR_ACCEL_CONFIG = 0x1C; // 28

    /**
     * <b>[datasheet 2 - p.27]</b> Interrupt Enable
     */
    public static final int MPU6050_REG_ADDR_INT_ENABLE = 0x38; // 56

    /**
     * <b>[datasheet 2 - p.40]</b> Power Management 1
     */
    public static final int MPU6050_REG_ADDR_PWR_MGMT_1 = 0x6B; // 107

    /**
     * <b>[datasheet 2 - p.42]</b> Power Management 2
     */
    public static final int MPU6050_REG_ADDR_PWR_MGMT_2 = 0x6C; // 108

    /**
     * <b>[datasheet 2 - p.29]</b> Accelerometer Measurements. X-axis high byte.
     */
    public static final int MPU6050_REG_ADDR_ACCEL_XOUT_H = 0x3B; // 59

    /**
     * <b>[datasheet 2 - p.31]</b> Gyroscope Measurements. X-axis high byte.
     */
    public static final int MPU6050_REG_ADDR_GYRO_XOUT_H = 0x43; // 67

    private final I2c i2c;
    private final long handle;
    private final short address;

    private int dlpfCfg = DEFAULT_DLPF_CFG;
    private int smplrtDiv = DEFAULT_SMPLRT_DIV;

    private double accelLSBSensitivity;
    private double gyroLSBSensitivity;

    private Thread updatingThread = null;
    private volatile boolean updatingThreadStopped = true;
    private long lastUpdateTime = 0;

    // ACCELEROMETER
    private double accelAccelerationX, accelAccelerationY, accelAccelerationZ = 0.;
    private double accelAngleX, accelAngleY, accelAngleZ = 0.;

    // GYROSCOPE
    private double gyroAngularSpeedX, gyroAngularSpeedY, gyroAngularSpeedZ = 0.;
    private double gyroAngleX, gyroAngleY, gyroAngleZ = 0.;
    private double gyroAngularSpeedOffsetX, gyroAngularSpeedOffsetY, gyroAngularSpeedOffsetZ = 0.;

    // FILTERED
    private double filteredAngleX, filteredAngleY, filteredAngleZ = 0.;

    /**
     * Constructor for Mpu6050.
     *
     * @param device I2C device path.
     * @param address I2C address.
     */
    public Mpu6050(final String device, final short address) {
        this.i2c = new I2c(device);
        this.handle = i2c.getHandle();
        this.address = address;
        initialize();
    }

    /**
     * Hardware initialization sequence.
     */
    private void initialize() {
        // 1. waking up the MPU6050 (0x00 = 0000 0000) as it starts in sleep mode.
        updateRegisterValue(MPU6050_REG_ADDR_PWR_MGMT_1, 0x00);

        // 2. sample rate divider
        updateRegisterValue(MPU6050_REG_ADDR_SMPRT_DIV, smplrtDiv);

        // 3. Digital Low Pass Filter (DLPF) setting
        setDLPFConfig(dlpfCfg);

        // 4. Gyroscope configuration [datasheet 2 - p.14]
        byte fsSel = 0; // FS_SEL full scale range: +- 250 °/s. LSB sensitivity : 131 LSB/°/s
        gyroLSBSensitivity = 131.; // LSB Sensitivity corresponding to FS_SEL 0
        updateRegisterValue(MPU6050_REG_ADDR_GYRO_CONFIG, fsSel);

        // 5. Accelerometer configuration [datasheet 2 - p.29]
        byte afsSel = 0; // AFS_SEL full scale range: ± 2g. LSB sensitivity : 16384 LSB/g
        accelLSBSensitivity = 16384.; // LSB Sensitivity corresponding to AFS_SEL 0
        updateRegisterValue(MPU6050_REG_ADDR_ACCEL_CONFIG, afsSel);

        // 6. Disable interrupts
        updateRegisterValue(MPU6050_REG_ADDR_INT_ENABLE, 0x00);

        // 7. Disable standby mode
        updateRegisterValue(MPU6050_REG_ADDR_PWR_MGMT_2, 0x00);
    }

    /**
     * Sets the value of the DLPF config, according to the datasheet informations.
     *
     * @param dlpfConfig the new value for the DLPF config.
     */
    public final void setDLPFConfig(int dlpfConfig) {
        if (dlpfConfig > 7 || dlpfConfig < 0) {
            throw new IllegalArgumentException("The DLPF config must be in the 0..7 range.");
        }
        dlpfCfg = dlpfConfig;
        updateRegisterValue(MPU6050_REG_ADDR_CONFIG, dlpfCfg);
    }

    /**
     * Reads the most recent accelerometer values and calculates accelerations in g.
     *
     * @return an array of three doubles containing the accelerations in g (x, y, z).
     */
    public double[] readScaledAccelerometerValues() {
        var accelX = readWord2C(MPU6050_REG_ADDR_ACCEL_XOUT_H) / accelLSBSensitivity;
        var accelY = readWord2C(MPU6050_REG_ADDR_ACCEL_XOUT_H + 2) / accelLSBSensitivity;
        var accelZ = readWord2C(MPU6050_REG_ADDR_ACCEL_XOUT_H + 4) / accelLSBSensitivity;
        return new double[]{accelX, accelY, -accelZ};
    }

    /**
     * Reads the most recent gyroscope values and calculates angular speeds in degrees/sec.
     *
     * @return an array of three doubles containing the angular speeds in degrees/sec (x, y, z).
     */
    public double[] readScaledGyroscopeValues() {
        var gyroX = readWord2C(MPU6050_REG_ADDR_GYRO_XOUT_H) / gyroLSBSensitivity;
        var gyroY = readWord2C(MPU6050_REG_ADDR_GYRO_XOUT_H + 2) / gyroLSBSensitivity;
        var gyroZ = readWord2C(MPU6050_REG_ADDR_GYRO_XOUT_H + 4) / gyroLSBSensitivity;
        return new double[]{gyroX, gyroY, gyroZ};
    }

    /**
     * Calibrate the accelerometer and gyroscope sensors.
     *
     * <p>
     * <b>NB: The sensor must be remained stationary during this process.</b></p>
     */
    public void calibrateSensors() {
        logger.info("Calibration starting in 5 seconds (don't move the sensor)");
        try {
            TimeUnit.SECONDS.sleep(5);
            logger.info("Calibration started (~5s) (don't move the sensor)");
            var nbReadings = 50;
            gyroAngularSpeedOffsetX = 0.;
            gyroAngularSpeedOffsetY = 0.;
            gyroAngularSpeedOffsetZ = 0.;
            for (var i = 0; i < nbReadings; i++) {
                var angularSpeeds = readScaledGyroscopeValues();
                gyroAngularSpeedOffsetX += angularSpeeds[0];
                gyroAngularSpeedOffsetY += angularSpeeds[1];
                gyroAngularSpeedOffsetZ += angularSpeeds[2];
                TimeUnit.MILLISECONDS.sleep(100);
            }
            gyroAngularSpeedOffsetX /= nbReadings;
            gyroAngularSpeedOffsetY /= nbReadings;
            gyroAngularSpeedOffsetZ /= nbReadings;
            logger.info("Calibration ended");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Start the updating thread.
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
        }
    }

    /**
     * Stop the updating thread.
     */
    public void stopUpdatingThread() {
        updatingThreadStopped = true;
        if (updatingThread != null) {
            try {
                updatingThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Update values for the accelerometer angles, gyroscope angles and filtered angles values.
     */
    private void updateValues() {
        var accelerations = readScaledAccelerometerValues();
        accelAccelerationX = accelerations[0];
        accelAccelerationY = accelerations[1];
        accelAccelerationZ = accelerations[2];
        accelAngleX = getAccelXAngle(accelAccelerationX, accelAccelerationY, accelAccelerationZ);
        accelAngleY = getAccelYAngle(accelAccelerationX, accelAccelerationY, accelAccelerationZ);

        var angularSpeeds = readScaledGyroscopeValues();
        gyroAngularSpeedX = angularSpeeds[0] - gyroAngularSpeedOffsetX;
        gyroAngularSpeedY = angularSpeeds[1] - gyroAngularSpeedOffsetY;
        gyroAngularSpeedZ = angularSpeeds[2] - gyroAngularSpeedOffsetZ;

        var dt = Math.abs(System.currentTimeMillis() - lastUpdateTime) / 1000.;
        lastUpdateTime = System.currentTimeMillis();

        gyroAngleX += gyroAngularSpeedX * dt;
        gyroAngleY += gyroAngularSpeedY * dt;
        gyroAngleZ += gyroAngularSpeedZ * dt;

        // Complementary Filter
        var alpha = 0.96;
        filteredAngleX = alpha * (filteredAngleX + gyroAngularSpeedX * dt) + (1. - alpha) * accelAngleX;
        filteredAngleY = alpha * (filteredAngleY + gyroAngularSpeedY * dt) + (1. - alpha) * accelAngleY;
        filteredAngleZ = filteredAngleZ + gyroAngularSpeedZ * dt;
    }

    /**
     * Get the accelerometer angles.
     *
     * @return an array of three doubles containing the accelerometer angles (x, y, z).
     */
    public double[] getAccelAngles() {
        return new double[]{accelAngleX, accelAngleY, accelAngleZ};
    }

    /**
     * Get the gyroscope angles.
     *
     * @return an array of three doubles containing the gyroscope angles (x, y, z).
     */
    public double[] getGyroAngles() {
        return new double[]{gyroAngleX, gyroAngleY, gyroAngleZ};
    }

    /**
     * Get the filtered angles (accelerometer + gyroscope).
     *
     * @return an array of three doubles containing the filtered angles (x, y, z).
     */
    public double[] getFilteredAngles() {
        return new double[]{filteredAngleX, filteredAngleY, filteredAngleZ};
    }

    /**
     * Simple register write and read-back verification.
     *
     * @param register register address.
     * @param value value to write.
     */
    public void updateRegisterValue(int register, int value) {
        I2c.i2cWriteReg8(handle, address, (short) register, (short) value);
        final var regVal = new short[1];
        I2c.i2cReadReg8(handle, address, (short) register, regVal);
        if (regVal[0] != value) {
            throw new RuntimeException(String.format("Error updating register %d", register));
        }
    }

    /**
     * Read 2's complement word.
     *
     * @param register register address.
     * @return signed integer value.
     */
    private int readWord2C(int register) {
        final var regVal = new int[1];
        I2c.i2cReadWord8(handle, address, (short) register, regVal);
        return regVal[0];
    }

    private double getAccelXAngle(double x, double y, double z) {
        var radians = Math.atan2(y, distance(x, z));
        return radians * RADIAN_TO_DEGREE;
    }

    private double getAccelYAngle(double x, double y, double z) {
        var radians = Math.atan2(x, distance(y, z));
        return -radians * RADIAN_TO_DEGREE;
    }

    private double distance(double a, double b) {
        return Math.sqrt(a * a + b * b);
    }

    @Override
    public void close() {
        stopUpdatingThread();
        i2c.close();
    }
}