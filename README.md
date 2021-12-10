![Title](images/title.png)

Java UIO provides high performance Java interfaces for Linux Userspace IO. Java
UIO was built from the ground up to use modern kernel APIs, libraries and code
generation techniques to provide a best of breed cross platform approach. It
does not make sense to recreate the wheel like so many other IO libraries. JDK
17 LTS is supported out of the box.

<img src="periphery/images/periphery.png" width="100"/><img src="u8g2/images/u8g2.jpg" width="100"/>

* [Periphery](https://github.com/sgjava/javauio/tree/main/periphery) API for
GPIO, LED, PWM, SPI, I2C, MMIO and Serial peripheral I/O interface access. Based
on [c-periphery](https://github.com/vsergeev/c-periphery) API which also covers
C, C++, Python, Lua and Dart.
* [U8g2](https://github.com/sgjava/javauio/tree/main/u8g2) API for monochrome
displays. Based on [U8g2](https://github.com/olikraus/u8g2): Library for
monochrome displays, version 2. I added ability to use multiple displays in a
thread safe way and dramatically improved software driven I2C and SPI performance
at the C level.
* [Tools](https://github.com/sgjava/javauio/tree/main/tools) provides tools
for mapping MMIO GPIO register mapping, code generation, etc.
* [Demo](https://github.com/sgjava/javauio/tree/main/demo) provides CLI based
demos instead of using mocks or hard coded pins, busses, etc.

* An install script gives you a complete install of [JDK](https://www.azul.com/products/core),
[Maven](https://maven.apache.org), [HawtJNI](https://github.com/fusesource/hawtjni)
fork and any required projects, so there is no guessing. This can be run on the target platform
such as [Armbian](https://www.armbian.com) using Ubuntu. All testing has been done on
Armbian and Ubuntu targets while development is done on x86 and Ubuntu.
* HawtJNI generates JNI code to reduce errors and time hand coding. Updating code
is also much easier.
    * Generates JNI source code.
    * Generates an autoconf and msbuild source project to build the native library.
This gets attached to the Maven project as as the native source zip file.
    * Builds the native source tar for the current platform.
    * Built native library is stored in a platform specific jar. This gets attached
to the Maven project as a platform specific jar file.
* Why Linux userspace? This is really the only way to get cross platform
libraries to work since most SBCs have different chip sets. The trade off is
performance compared to native C written to specific chip sets. However, since
I'm wrapping C with JNI it guarantees the fastest userspace experience for Java.
* Why Armbian? Because Armbian supports many SBCs and the idea is to be truly
SBC cross platform. See [downloads](https://www.armbian.com/download).
* Why Java 17? Because Java 17 is the current LTS version of Java. Java 11 the
previous LTS release is end of life September 2023. I'm only moving forward
with Java. You can always create a fork and make a Java 8 or Java 11 version of
Java UIO.
* Why Zulu OpenJDK? Because it's easy to download without all the crap Oracle
puts you through. You can always use another JDK 17 vendor, but you will have to
do that manually. [Liberica Full JDK](https://bell-sw.com/pages/downloads/?version=java-17-lts)
offers an ARM32 version of JDK 17, so that is used instead.

## SBC configuration
* If you are using Armbian then use `armbian-config` or edit `/boot/armbianEnv.txt`
to configure various devices. Userspace devices are exposed through /dev or
/sys. Verify the device is showing up prior to trying demo apps.
    * `sudo apt install armbian-config`
* If you are not using Armbian then you will need to know how to configure
devices to be exposed to userspace for your Linux distribution and SBC model.
Check each log in scripts directory to be sure there were no errors after running
install.sh.
* Since linux 4.8 the GPIO sysfs interface is [deprecated](https://www.kernel.org/doc/html/latest/admin-guide/gpio/sysfs.html).
Userspace should use the character device instead.
* I have tested 32 bit and 64 bit boards using the latest Armbian release or in
the case of the Raspberry Pi Ubuntu Server. The ability to switch seamlessly
between boards gives you a wide range of SBC choices.

## Non-root access
Non-root access is provided by a systemd service called [uio-permissions](https://github.com/sgjava/javauio/blob/6ea3ef5155f3158d92eb16b5f428372ec8adda3d/scripts/install.sh#L44) that
applies permissions for GPIO, I2C, SPI, serial and system LEDs. GPIO
sysfs (deprecated) and PWM permissions are provided by udev rules. Tweak [uio-permissions.sh](https://github.com/sgjava/javauio/blob/main/scripts/uio-permissions.sh)
[98-sysfs.rules](https://github.com/sgjava/javauio/blob/main/scripts/98-sysfs.rules) and
[99-pwm.rules](https://github.com/sgjava/javauio/blob/main/scripts/99-pwm.rules)
as needed.

## Download project
* `sudo apt install git`
* `cd ~/`
* `git clone --depth 1 https://github.com/sgjava/javauio.git`

## Install script
The install script assumes a clean OS install. If you would like to install on
a OS with your own version of Java 17, etc. then you can look at what install.sh
does and do it manually. What does the script do?
* Install build dependencies
* Install UIO Permissions Service and udev rules
* Installs Zulu JDK 17 (Liberica JDK for ARM 32) to /usr/lib/jvm
* Installs Maven to /opt
* Build HawtJNI (using my fork that works with JDK 17)
* Build Java UIO

### Run script
* `cd ~/javauio/scripts`
* `./install.sh`
* Check various log files if you have issues running the demo code. Something
could have gone wrong during the build/bindings generation processes.
* If you need to recompile use
    * `cd ~/javauio`
    * `mvn clean install`
