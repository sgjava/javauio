![Title](images/title.png)

Java UIO provides high performance Java interfaces for Linux Userspace IO. Java
UIO was built from the ground up to use modern kernel APIs, libraries and code
generation techniques to provide a best of breed cross platform approach. It
does not make sense to recreate the wheel like so many other IO libraries. JDK
17 LTS is supported out of the box (JDK 11 for ARM 32 until Zulu JDK 17 released).

* [Periphery](https://github.com/sgjava/javauio/tree/master/periphery) API for
GPIO, LED, PWM, SPI, I2C, MMIO and Serial peripheral I/O interface access. Based
on [c-periphery](https://github.com/vsergeev/c-periphery) API which also covers
C, C++, Python, Lua and Dart.
* [U8g2](https://github.com/sgjava/javauio/tree/master/u8g2) API for monochrome
displays. Based on [U8g2](https://github.com/olikraus/u8g2): Library for
monochrome displays, version 2.
* [Tools](https://github.com/sgjava/javauio/tree/master/tools) provides tools
for mapping MMIO GPIO register mapping, code generation, etc.
* [Demo](https://github.com/sgjava/javauio/tree/master/demo) provides CLI based
demos instead of using mocks or hard coded pins, busses, etc.

* An install script gives you a complete install of [JDK](https://www.azul.com/products/core),
[Maven](https://maven.apache.org), [HawtJNI](https://github.com/fusesource/hawtjni)
fork and any required projects. This can be run on the target platform such as
[Armbian](https://www.armbian.com) using Ubuntu. All testing has been done on
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
Periphery.
* Why Zulu OpenJDK? Because it's easy to download without all the crap Oracle
puts you through. You can always use another JDK 17 vendor, but you will have to
do that manually. [Liberica JDK](https://bell-sw.com/pages/downloads/?version=java-17-lts)
offers an ARM32 version of JDK 17, but it's 50% slower than Zulu JDK 11. At yhis
point in time there are few JDK 17 choices for ARM32. I will update the project
once a decent JDK 17 is available for ARM32.

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
If you want to access devices without root do the following (you can try udev
rules instead if you wish):
* `sudo usermod -a -G dialout username` (Use a non-root username)
* `sudo groupadd uio`
* `sudo usermod -a -G uio username` (Use a non-root username)
* `sudo nano /etc/rc.local`
<pre><code>chown -R root:uio /dev/gpiochip* #/dev/gpiomem for sandbox
chmod -R ug+rw /dev/gpiochip* #/dev/gpiomem for sandbox
chown -R root:uio /dev/i2c*
chmod -R ug+rw /dev/i2c*
chown -R root:uio /dev/spidev*
chmod -R ug+rw /dev/spidev*
chown -R root:uio /sys/devices/platform/leds/leds
chmod -R ug+rw /sys/devices/platform/leds/leds</code></pre>
* PWM udev rules
    * You need kernel 4.16 or greater to use non-root access for PWM.
    * `sudo nano /etc/udev/rules.d/99-pwm.rules`
    <pre><code>SUBSYSTEM=="pwm*", PROGRAM="/bin/sh -c '\
  chown -R root:uio /sys/class/pwm && chmod -R 770 /sys/class/pwm;\
  chown -R root:uio /sys/devices/platform/soc/*.pwm/pwm/pwmchip* && chmod -R 770 /sys/devices/platform/soc/*.pwm/pwm/pwmchip*\
  '"</code></pre>

## Download project
* `sudo apt install git`
* `cd ~/`
* `git clone --depth 1 https://github.com/sgjava/javauio.git`

## Install script
The install script assumes a clean OS install. If you would like to install on
a OS with your own version of Java 17, etc. then you can look at what install.sh
does and do it manually. What does the script do?
* Install build dependencies for HawtJNI 
* Installs Zulu JDK 17 (JDK 11 for ARM 32) to /usr/lib/jvm
* Installs Maven to /opt
* Build HawtJNI (using my fork that works with JDK 17)
* Build Java UIO

### Run script
* `cd ~/javauio/scripts`
* `./install.sh`
* Check various log files if you have issues running the demo code. Something
could have gone wrong during the build/bindings generation processes.
* If you need to recompile use
    * `mvn clean install`
    * Add `-Dmaven.compiler.source=11 -Dmaven.compiler.target=11` for ARM32
