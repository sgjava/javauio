/*
 * c-periphery helper functions for Java wrapper.
 *
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */

#ifdef __cplusplus
extern "C" {
#endif

#include "i2c.h"

int i2c_read8(i2c_t *i2c, uint16_t addr, uint16_t reg, uint8_t *buf, size_t len);
int i2c_read16(i2c_t *i2c, uint16_t addr, uint16_t reg, uint8_t *buf, size_t len);
int i2c_write8(i2c_t *i2c, uint16_t addr, uint16_t reg, uint16_t value);
int i2c_write16(i2c_t *i2c, uint16_t addr, uint16_t reg, uint16_t value);

#ifdef __cplusplus
}
#endif