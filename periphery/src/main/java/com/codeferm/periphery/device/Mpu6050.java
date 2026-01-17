/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.device;

import com.codeferm.periphery.I2c;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
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
 * The MPU-60X0 features three 16-bit analog-to-digital converters (ADCs) for digitizing the gyroscope outputs and three 16-bit ADCs
 * for digitizing the accelerometer outputs.</p>
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

    /**
     * Reentrant lock for thread-safe I2C access and state management.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * I2c device wrapper.
     */
    private final I2c i2c;

    /**
     * Handle to the I2c device.
     */
    private final long handle;

    /**
     * I2c address of the MPU6050.
     */
    private final short address;

    /*
     * -----------------------------------------------------------------------
     * DEFAULT VALUES
     * -----------------------------------------------------------------------
     */
    /** Default address of the MPU6050 device. */
    public static final int DEFAULT_MPU6050_ADDRESS = 0x68;
    /** Default value for the digital low pass filter (DLPF) setting. */
    public static final int DEFAULT_DLPF_CFG = 0x06;
    /** Default value for the sample rate divider. */
    public static final int DEFAULT_SMPLRT_DIV = 0x00;
    /** Coefficient to convert an angle value from radians to degrees. */
    public static final double RADIAN_TO_DEGREE = 180. / Math.PI;
    /** Default Z angle for accelerometer. */
    private static final double ACCEL_Z_ANGLE = 0;

    /*
     * -----------------------------------------------------------------------
     * REGISTERS ADDRESSES
     * -----------------------------------------------------------------------
     */
    /** <b>[datasheet 2 - p.11]</b> Sample Rate Divider. */
    public static final int MPU6050_REG_ADDR_SMPRT_DIV = 0x19;
    /** <b>[datasheet 2 - p.13]</b> Configuration. */
    public static final int MPU6050_REG_ADDR_CONFIG = 0x1A;
    /** <b>[datasheet 2 - p.14]</b> Gyroscope Configuration. */
    public static final int MPU6050_REG_ADDR_GYRO_CONFIG = 0x1B;
    /** <b>[datasheet 2 - p.15]</b> Accelerometer Configuration. */
    public static final int MPU6050_REG_ADDR_ACCEL_CONFIG = 0x1C;
    /** <b>[datasheet 2 - p.27]</b> Interrupt Enable. */
    public static final int MPU6050_REG_ADDR_INT_ENABLE = 0x38;
    /** <b>[datasheet 2 - p.40]</b> Power Management 1. */
    public static final int MPU6050_REG_ADDR_PWR_MGMT_1 = 0x6B;
    /** <b>[datasheet 2 - p.42]</b> Power Management 2. */
    public static final int MPU6050_REG_ADDR_PWR_MGMT_2 = 0x6C;
    /** <b>[datasheet 2 - p.29]</b> Accelerometer Measurements. */
    public static final int MPU6050_REG_ADDR_ACCEL_XOUT_H = 0x3B;
    /** <b>[datasheet 2 - p.31]</b> Gyroscope Measurements. */
    public static final int MPU6050_REG_ADDR_GYRO_XOUT_H = 0x43;

    /*
     * -----------------------------------------------------------------------
     * STATE VARIABLES
     * -----------------------------------------------------------------------
     */
    /** Current DLPF configuration. */
    private int dlpfCfg;
    /** Current Sample Rate Divider. */
    private int smplrtDiv;
    /** Accelerometer LSB sensitivity. */
    private double accelLSBSensitivity;
    /** Gyroscope LSB sensitivity. */
    private double gyroLSBSensitivity;
    /** Gyroscope X offset. */
    private double gyroAngularSpeedOffsetX;
    /** Gyroscope Y offset. */
    private double gyroAngularSpeedOffsetY;
    /** Gyroscope Z offset. */
    private double gyroAngularSpeedOffsetZ;

    /** Background update thread. */
    private Thread updatingThread = null;
    /** Volatile flag to stop the update thread. */
    private volatile boolean updatingThreadStopped = true;
    /** Last timestamp of update. */
    private long lastUpdateTime = 0;

    /**
     * Snapshot record of all processed MPU data.
     */
    public record MpuData(
            double accelX, double accelY, double accelZ,
            double accelAngleX, double accelAngleY, double accelAngleZ,
            double gyroSpeedX, double gyroSpeedY, double gyroSpeedZ,
            double gyroAngleX, double gyroAngleY, double gyroAngleZ,
            double filteredX, double filteredY, double filteredZ) {}

    /**
     * Thread-safe reference to the latest data snapshot.
     */
    private final AtomicReference<MpuData> dataSnapshot = new AtomicReference<>(new MpuData(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0));

    /**
     * Initialize MPU6050 on specified device and address.
     * @param device I2C device path.
     * @param address I2C address.
     */
    public Mpu6050(final String device, final short address) {
        this.i2c = new I2c(device);
        this.handle = i2c.getHandle();
        this.address = address;
        initializeHardware();
    }

    /*
     * -----------------------------------------------------------------------
     * PUBLIC API METHODS
     * -----------------------------------------------------------------------
     */

    /**
     * Returns the Sample Rate of the MPU6050.
     * @return the sample rate in Hz.
     */
    public int getSampleRate() {
        lock.lock();
        try {
            int gyroscopeOutputRate = (dlpfCfg == 0 || dlpfCfg == 7) ? 8000 : 1000;
            return gyroscopeOutputRate / (1 + smplrtDiv);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sets the value of the DLPF config.
     * @param dlpfConfig the new DLPF_CFG value (0-7).
     */
    public final void setDLPFConfig(int dlpfConfig) {
        if (dlpfConfig > 7 || dlpfConfig < 0) {
            throw new IllegalArgumentException("The DLPF config must be in the 0..7 range.");
        }
        lock.lock();
        try {
            this.dlpfCfg = dlpfConfig;
            updateRegisterValue(MPU6050_REG_ADDR_CONFIG, dlpfCfg);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Calibrate the accelerometer and gyroscope sensors by taking multiple readings.
     */
    public void calibrateSensors() {
        logger.info("Calibration starting in 5 seconds (don't move the sensor)");
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        lock.lock();
        try {
            var nbReadings = 50;
            gyroAngularSpeedOffsetX = 0.; gyroAngularSpeedOffsetY = 0.; gyroAngularSpeedOffsetZ = 0.;
            for (var i = 0; i < nbReadings; i++) {
                var speeds = readScaledGyroscopeValues();
                gyroAngularSpeedOffsetX += speeds[0];
                gyroAngularSpeedOffsetY += speeds[1];
                gyroAngularSpeedOffsetZ += speeds[2];
                TimeUnit.MILLISECONDS.sleep(100);
            }
            gyroAngularSpeedOffsetX /= nbReadings;
            gyroAngularSpeedOffsetY /= nbReadings;
            gyroAngularSpeedOffsetZ /= nbReadings;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        logger.info("Calibration ended");
    }

    /**
     * Starts the thread responsible to update MPU6050 values in background.
     */
    public void startUpdatingThread() {
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stops the update thread and joins.
     * @throws InterruptedException if interrupted while joining.
     */
    public void stopUpdatingThread() throws InterruptedException {
        updatingThreadStopped = true;
        if (updatingThread != null) {
            updatingThread.join();
            updatingThread = null;
        }
    }

    /**
     * Returns a thread-safe snapshot of all current MPU data.
     * @return the latest data snapshot.
     */
    public MpuData getSnapshot() {
        return dataSnapshot.get();
    }

    /**
     * This method updates the value of a specific register and checks success.
     * @param register register address.
     * @param value value to write.
     */
    public void updateRegisterValue(int register, int value) {
        lock.lock();
        try {
            I2c.i2cWriteReg8(handle, address, (short) register, (short) value);
            var regVal = new short[1];
            I2c.i2cReadReg8(handle, address, (short) register, regVal);
            if (regVal[0] != value) throw new RuntimeException("Error updating MPU6050 register");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reads the most recent accelerations in g.
     * @return array of [x, y, z] accelerations.
     */
    public double[] readScaledAccelerometerValues() {
        lock.lock();
        try {
            return new double[]{
                readWord2C(MPU6050_REG_ADDR_ACCEL_XOUT_H) / accelLSBSensitivity,
                readWord2C(MPU6050_REG_ADDR_ACCEL_XOUT_H + 2) / accelLSBSensitivity,
                -readWord2C(MPU6050_REG_ADDR_ACCEL_XOUT_H + 4) / accelLSBSensitivity
            };
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reads the most recent angular speeds in degrees/sec.
     * @return array of [x, y, z] angular speeds.
     */
    public double[] readScaledGyroscopeValues() {
        lock.lock();
        try {
            return new double[]{
                readWord2C(MPU6050_REG_ADDR_GYRO_XOUT_H) / gyroLSBSensitivity,
                readWord2C(MPU6050_REG_ADDR_GYRO_XOUT_H + 2) / gyroLSBSensitivity,
                readWord2C(MPU6050_REG_ADDR_GYRO_XOUT_H + 4) / gyroLSBSensitivity
            };
        } finally {
            lock.unlock();
        }
    }

    // Individual Accessors (Fallback for simple usage)
    /** @return Latest accelerometer values. */
    public double[] getAccelAccelerations() { var d = getSnapshot(); return new double[]{d.accelX(), d.accelY(), d.accelZ()}; }
    /** @return Latest calculated accelerometer angles. */
    public double[] getAccelAngles() { var d = getSnapshot(); return new double[]{d.accelAngleX(), d.accelAngleY(), d.accelAngleZ()}; }
    /** @return Latest gyroscope angular speeds. */
    public double[] getGyroAngularSpeeds() { var d = getSnapshot(); return new double[]{d.gyroSpeedX(), d.gyroSpeedY(), d.gyroSpeedZ()}; }
    /** @return Latest integrated gyroscope angles. */
    public double[] getGyroAngles() { var d = getSnapshot(); return new double[]{d.gyroAngleX(), d.gyroAngleY(), d.gyroAngleZ()}; }
    /** @return Latest filtered angles. */
    public double[] getFilteredAngles() { var d = getSnapshot(); return new double[]{d.filteredX(), d.filteredY(), d.filteredZ()}; }
    /** @return Gyroscope offsets used for calibration. */
    public double[] getGyroAngularSpeedsOffsets() { return new double[]{gyroAngularSpeedOffsetX, gyroAngularSpeedOffsetY, gyroAngularSpeedOffsetZ}; }

    // Static String formatters
    /** @param angle angle value. @return formatted string. */
    public static String angleToString(double angle) { return String.format("%.4f°", angle); }
    /** @param accel accel value. @return formatted string. */
    public static String accelToString(double accel) { return String.format("%.6fg", accel); }
    /** @param speed speed value. @return formatted string. */
    public static String angularSpeedToString(double speed) { return String.format("%.4f°/s", speed); }
    /** @param x X @param y Y @param z Z @return combined string. */
    public static String xyzValuesToString(String x, String y, String z) { return "x: " + x + "\ty: " + y + "\tz: " + z; }

    /*
     * -----------------------------------------------------------------------
     * LIFECYCLE METHOD
     * -----------------------------------------------------------------------
     */

    /**
     * Closes resources safely. Releases thread and hardware locks.
     */
    @Override
    public void close() {
        lock.lock();
        try {
            updatingThreadStopped = true;
            if (updatingThread != null) {
                try { updatingThread.join(500); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        } finally {
            try {
                i2c.close();
            } finally {
                lock.unlock();
            }
        }
    }

    /*
     * -----------------------------------------------------------------------
     * PRIVATE HELPER METHODS
     * -----------------------------------------------------------------------
     */

    /**
     * Configures initial power and sensitivity registers.
     */
    private void initializeHardware() {
        lock.lock();
        try {
            dlpfCfg = DEFAULT_DLPF_CFG;
            smplrtDiv = DEFAULT_SMPLRT_DIV;
            updateRegisterValue(MPU6050_REG_ADDR_PWR_MGMT_1, 0x00);
            updateRegisterValue(MPU6050_REG_ADDR_SMPRT_DIV, smplrtDiv);
            setDLPFConfig(dlpfCfg);
            gyroLSBSensitivity = 131.;
            updateRegisterValue(MPU6050_REG_ADDR_GYRO_CONFIG, 0x00);
            accelLSBSensitivity = 16384.;
            updateRegisterValue(MPU6050_REG_ADDR_ACCEL_CONFIG, 0x00);
            updateRegisterValue(MPU6050_REG_ADDR_INT_ENABLE, 0x00);
            updateRegisterValue(MPU6050_REG_ADDR_PWR_MGMT_2, 0x00);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Core update logic for background thread. Integrates gyro and filters accel.
     */
    private void updateValues() {
        lock.lock();
        try {
            double[] acc = readScaledAccelerometerValues();
            double[] gyro = readScaledGyroscopeValues();
            double dt = Math.abs(System.currentTimeMillis() - lastUpdateTime) / 1000.;
            lastUpdateTime = System.currentTimeMillis();

            double aX = acc[0], aY = acc[1], aZ = acc[2];
            double angAX = getAccelXAngle(aX, aY, aZ);
            double angAY = getAccelYAngle(aX, aY, aZ);

            double gSX = gyro[0] - gyroAngularSpeedOffsetX;
            double gSY = gyro[1] - gyroAngularSpeedOffsetY;
            double gSZ = gyro[2] - gyroAngularSpeedOffsetZ;

            MpuData prev = dataSnapshot.get();
            double alpha = 0.96;
            double fX = alpha * (prev.filteredX() + (gSX * dt)) + (1. - alpha) * angAX;
            double fY = alpha * (prev.filteredY() + (gSY * dt)) + (1. - alpha) * angAY;

            dataSnapshot.set(new MpuData(aX, aY, aZ, angAX, angAY, ACCEL_Z_ANGLE, 
                gSX, gSY, gSZ, 
                prev.gyroAngleX() + (gSX * dt), prev.gyroAngleY() + (gSY * dt), prev.gyroAngleZ() + (gSZ * dt),
                fX, fY, prev.filteredZ() + (gSZ * dt)));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reads a word (2 bytes) from I2C and converts from 2's complement.
     * @param register starting register.
     * @return signed integer value.
     */
    private int readWord2C(int register) {
        var regVal = new int[1];
        I2c.i2cReadWord8(handle, address, (short) register, regVal);
        return regVal[0];
    }

    /** Helper for magnitude calculation. 
     * @param a first component.
     * @param b second component.
     * @return distance. */
    private double distance(double a, double b) { return Math.sqrt(a * a + b * b); }

    /** Calculates X angle from accelerometer. 
     * @param x X accel. @param y Y accel. @param z Z accel.
     * @return calculated angle. */
    private double getAccelXAngle(double x, double y, double z) {
        double radians = Math.atan2(y, distance(x, z));
        double delta = (y >= 0) ? (z >= 0 ? 0 : 180) : (z <= 0 ? 180 : 360);
        if (y < 0 && z <= 0 || y >= 0 && z < 0) radians *= -1;
        return radians * RADIAN_TO_DEGREE + delta;
    }

    /** Calculates Y angle from accelerometer. 
     * @param x X accel. @param y Y accel. @param z Z accel.
     * @return calculated angle. */
    private double getAccelYAngle(double x, double y, double z) {
        double tan = -1 * x / distance(y, z);
        double delta = (x <= 0) ? (z >= 0 ? 0 : 180) : (z <= 0 ? 180 : 360);
        if (x > 0 && z <= 0 || x <= 0 && z < 0) tan *= -1;
        return Math.atan(tan) * RADIAN_TO_DEGREE + delta;
    }
}