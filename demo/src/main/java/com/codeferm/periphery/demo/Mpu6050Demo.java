/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.periphery.demo;

import com.codeferm.periphery.device.Mpu6050;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Six-Axis (Gyro + Accelerometer) MEMS MotionTracking test. * This demo utilizes the Mpu6050 device class to perform sensor
 * calibration and retrieve filtered motion data.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Command(name = "Mpu6050Test", mixinStandardHelpOptions = true, version = "1.0.0-SNAPSHOT",
        description = "Six-Axis (Gyro + Accelerometer) MEMS MotionTracking test.")
public class Mpu6050Demo implements Callable<Integer> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Mpu6050Demo.class);

    /**
     * I2C device option.
     */
    @Option(names = {"-d", "--device"}, description = "I2C device, ${DEFAULT-VALUE} by default.")
    private String device = "/dev/i2c-0";

    /**
     * I2C address option.
     */
    @Option(names = {"-a", "--address"}, description = "I2C address, ${DEFAULT-VALUE} by default.")
    private short address = Mpu6050.DEFAULT_MPU6050_ADDRESS;

    /**
     * Run the MPU6050 test logic.
     *
     * @return Exit code.
     * @throws InterruptedException if the sleep is interrupted.
     */
    @Override
    public Integer call() throws InterruptedException {
        var exitCode = 0;
        // Use try-with-resources for automatic hardware cleanup via AutoCloseable
        try (final var mpu = new Mpu6050(device, address)) {
            // Calibrate sensors (requires 5 seconds of stationary position)
            mpu.calibrateSensors();

            // Start background thread for sensor fusion calculations
            mpu.startUpdatingThread();

            logger.info("Displaying filtered sensor data for 30 seconds...");
            for (var i = 0; i < 10; i++) {
                // Access processed data without needing to know register addresses
                var angles = mpu.getFilteredAngles();

                logger.info(String.format("Iteration %d - Filtered Angles: X: %.4f° | Y: %.4f° | Z: %.4f°",
                        i + 1, angles[0], angles[1], angles[2]));

                TimeUnit.SECONDS.sleep(3);
            }
        } catch (RuntimeException e) {
            logger.error("Hardware error: {}", e.getMessage());
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Main entry point using picocli for command line parsing.
     *
     * @param args Argument list.
     */
    public static void main(String... args) {
        System.exit(new CommandLine(new Mpu6050Demo())
                .registerConverter(Short.class, Short::decode)
                .registerConverter(Short.TYPE, Short::decode)
                .execute(args));
    }
}
