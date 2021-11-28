/*
 * c-periphery helper functions for Java wrapper.
 *
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */

#include "helper.h"

/*
 * Read I2C 8 bit address.
 */
int i2c_read8(i2c_t *i2c, uint16_t addr, uint16_t reg, uint8_t *buf, size_t len) {
        // 8 bit register
	uint8_t msg_addr[1] = { reg & 0xff };
	struct i2c_msg msgs[2] = {
	// Write 8-bit address
	{ .addr = addr, .flags = 0, .len = 1, .buf = msg_addr },
	// Read 8-bit data
	{ .addr = addr, .flags = I2C_M_RD, .len = len, .buf = buf }, };
	// Transfer a transaction with two I2C messages
	return i2c_transfer(i2c, msgs, 2);
}

/*
 * Read I2C 16 bit address.
 */
int i2c_read16(i2c_t *i2c, uint16_t addr, uint16_t reg, uint8_t *buf, size_t len) {
        // 16 bit register high and low byte
	uint8_t msg_addr[2] = { reg >> 8, reg & 0xff };
	struct i2c_msg msgs[2] = {
	// Write 16-bit address
	{ .addr = addr, .flags = 0, .len = 2, .buf = msg_addr },
	// Read 8-bit data
	{ .addr = addr, .flags = I2C_M_RD, .len = len, .buf = buf }, };
	// Transfer a transaction with two I2C messages
	return i2c_transfer(i2c, msgs, 2);
}

/*
 * Write I2C 8 bit address.
 */
int i2c_write8(i2c_t *i2c, uint16_t addr, uint16_t reg, uint16_t value) {
        // 8 bit register
	uint8_t msg_addr[2] = { reg, value};
	struct i2c_msg msgs[1] = {
	// Write 8-bit address
	{ .addr = addr, .flags = 0, .len = 2, .buf = msg_addr }, };
	// Transfer a transaction with two I2C messages
	return i2c_transfer(i2c, msgs, 1);
}

/*
 * Write I2C 16 bit address.
 */
int i2c_write16(i2c_t *i2c, uint16_t addr, uint16_t reg, uint16_t value) {
        // 16 bit value low/high byte
	uint8_t msg_addr[3] = { reg, value & 0xff, value >> 8};
	struct i2c_msg msgs[1] = {
	// Write 16-bit address
	{ .addr = addr, .flags = 0, .len = 3, .buf = msg_addr }, };
	// Transfer a transaction with three I2C messages
	return i2c_transfer(i2c, msgs, 1);
}
