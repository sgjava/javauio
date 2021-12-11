![Title](images/title.png)

U8g2 is a high performance library based on [U8g2](https://github.com/olikraus/u8g2): Library for monochrome displays, version 2.
I used some custom code to generate the HawtJNI methods and font constants.
This makes life easier going forawrd as changes are simple to keep in sync.
* Java code follows C API, so if you used U8g2 in C, C++ or even NodeMcu with Lua it
will immediately be familar. No goofy Java wrapper with a totally different API.
* The [Display](https://github.com/sgjava/javauio/blob/main/u8g2/src/main/java/com/codeferm/u8g2/Display.java)
class allows for runtime setup and font selection, so you can make applications
that do not need to know the display or font type ahead of time.

I've been submitting PRs to U8g2 for
[arm-linux](https://github.com/olikraus/u8g2/tree/master/sys/arm-linux) port and
made it thread safe and multi-display capable. I also greatly improved
performance of the I2C and SPI software drivers.

Do it all yourself U8g2 style.

```
final var u8g2 = U8g2.initU8g2();
// Change this to your actual display
U8g2.setupSsd1306I2c128x64NonameF(u8g2, U8G2_R0, u8x8_byte_arm_linux_hw_i2c, u8x8_arm_linux_gpio_and_delay);
U8g2.initI2cHw(u8g2, 0);
U8g2.setI2CAddress(u8g2, 0x3c * 2);
U8g2.initDisplay(u8g2);
logger.debug(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2), U8g2.
        getDrawColor(u8g2)));
U8g2.setPowerSave(u8g2, 0);
U8g2.clearBuffer(u8g2);
U8g2.setFont(u8g2, u8g2_font_t0_15b_mf);
U8g2.drawStr(u8g2, 1, 18, "U8g2");
U8g2.sendBuffer(u8g2);
try {
    TimeUnit.SECONDS.sleep(5);
} catch (InterruptedException ie) {
    Thread.currentThread().interrupt();
}
U8g2.setPowerSave(u8g2, 1);
U8g2.doneUserData(u8g2);
U8g2.doneI2c();
```

Or look at the [Base](https://github.com/sgjava/javauio/blob/main/demo/src/main/java/com/codeferm/u8g2/demo/Base.java) class in Demo module.

```
var exitCode = super.call();
showText("Hello world.");
done();
```

<img src="images/u8g2.jpg" width="300"/>

SSD1306 sendBuffer performance based on JDK 17 and Nano Pi Duo.

|Setup                       |Type |Bus KHz | FPS  |
| -------------------------- | --- | ------ | ---- |
|SSD1306_I2C_128X64_NONAME   |HW   |     100|  8.85|
|SSD1306_I2C_128X64_NONAME   |HW   |     400| 30.30|
|SSD1306_I2C_128X64_NONAME   |SW   |     189| 14.49|
|SSD1306_I2C_128X32_UNIVISION|SW   |     189| 28.57|
|SSD1306_128X64_NONAME       |HW   |     500| 50.00|
|SSD1306_128X64_NONAME       |HW   |    1000|100.00|
|SSD1306_128X64_NONAME       |HW   |    2000|200.00|
|SSD1306_128X64_NONAME       |SW   |     189| 19.61|

## Use Java u8g2 in your own Maven projects
After bulding Java u8g2 simpily add the following artifact:
```
<groupId>com.codeferm</groupId>
<artifactId>u8g2</artifactId>
<version>1.0.0-SNAPSHOT</version>
```
