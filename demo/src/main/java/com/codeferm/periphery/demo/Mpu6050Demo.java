/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Mpu6050;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Six-Axis (Gyro + Accelerometer) MEMS MotionTracking test.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Command(name = "Mpu6050Test", mixinStandardHelpOptions = true, version = "1.0.0",
        description = "Six-Axis (Gyro + Accelerometer) MEMS MotionTracking demo")
public class Mpu6050Demo implements Callable<Integer> {

    @Option(names = {"--device"}, description = "I2C device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/i2c-0";
    @Option(names = {"--address"}, description = "Address, ${DEFAULT-VALUE} by default.")
    private short address = Mpu6050.DEFAULT_MPU6050_ADDRESS;

    /**
     * Display MPU6050 data in the original format.
     *
     * @return Exit code.
     * @throws InterruptedException Possible exception.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        log.info("Starting on {} address 0x{}", device, Integer.toHexString(address));
        try (var mpu = new Mpu6050(device, address)) {
            // Calibration and startup
            mpu.calibrateSensors();
            mpu.startUpdatingThread();
            log.info("Reading sensor data for 30 seconds...");
            for (int i = 0; i < 10; i++) {
                // Get a single consistent snapshot of all values
                var data = mpu.getSnapshot();
                log.info("Accelerometer:");
                log.info(Mpu6050.xyzValuesToString(
                        Mpu6050.angleToString(data.accelAngleX()),
                        Mpu6050.angleToString(data.accelAngleY()),
                        Mpu6050.angleToString(data.accelAngleZ())));
                log.info("Accelerations:");
                log.info(Mpu6050.xyzValuesToString(
                        Mpu6050.accelToString(data.accelX()),
                        Mpu6050.accelToString(data.accelY()),
                        Mpu6050.accelToString(data.accelZ())));
                log.info("Gyroscope:");
                log.info(Mpu6050.xyzValuesToString(
                        Mpu6050.angleToString(data.gyroAngleX()),
                        Mpu6050.angleToString(data.gyroAngleY()),
                        Mpu6050.angleToString(data.gyroAngleZ())));
                log.info(Mpu6050.xyzValuesToString(
                        Mpu6050.angularSpeedToString(data.gyroSpeedX()),
                        Mpu6050.angularSpeedToString(data.gyroSpeedY()),
                        Mpu6050.angularSpeedToString(data.gyroSpeedZ())));
                log.info("Filtered angles:");
                log.info(Mpu6050.xyzValuesToString(
                        Mpu6050.angleToString(data.filteredX()),
                        Mpu6050.angleToString(data.filteredY()),
                        Mpu6050.angleToString(data.filteredZ())));
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (RuntimeException e) {
            log.error("Hardware error: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Main entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new Mpu6050Demo())
                .registerConverter(Short.class, Short::decode)
                .registerConverter(Short.TYPE, Short::decode)
                .execute(args));
    }
}
