#!/bin/sh
#
# Created on November 30, 2021
#
# @author: sgoldsmith
#
# Set permissions to uio group, so root is not required.
#
# Steven P. Goldsmith
# sgjava@gmail.com
#

chown -R root:uio /dev/gpiochip* #/dev/gpiomem for sandbox
chmod -R ug+rw /dev/gpiochip* #/dev/gpiomem for sandbox
chown -R root:uio /dev/i2c*
chmod -R ug+rw /dev/i2c*
chown -R root:uio /dev/spidev*
chmod -R ug+rw /dev/spidev*
chown -R root:uio /sys/devices/platform/leds/leds
chmod -R ug+rw /sys/devices/platform/leds/leds
exit 0