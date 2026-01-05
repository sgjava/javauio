![Title](images/title.png)

Java UIO Tools provides CLI programs, so you do not have to compile code with
hard coded pins, ports, etc. Note that the native library jar has a suffix such
as linux32, so depending on your target platform it could be linux64.

## High performance GPIO using MMIO

NanoPi Duo (H2+) example:
* `sudo env "PATH=$PATH" java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.mmio.Gen -i duo.properties -o duo-map.properties`
* `sudo env "PATH=$PATH" java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.mmio.Perf -i duo-map.properties -d 0 -l 203`

NanoPi Neo Plus2 (H5) example:
* `sudo env "PATH=$PATH" java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux64.jar com.codeferm.periphery.mmio.Gen -i neoplus2.properties -o neoplus2-map.properties`
* `sudo env "PATH=$PATH" java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux64.jar com.codeferm.periphery.mmio.Perf -i neoplus2-map.properties -d 1 -l 203`

Lets try a real MemScan tool example with the Orange Pi PC Plus. For this exercise I'm using latest Armbian Focal.

Install [Java UIO](https://github.com/sgjava/javauio#download-project)
Compile with latest GPIO header file

* `sudo armbian-config` Software, Headers_install
* `grep -R -i "GPIOHANDLE_REQUEST_BIAS_DISABLE" /usr/src`
* `mkdir -p $HOME/include/linux`
* `cp /usr/src/linux-headers-5.10.60-sunxi/include/uapi/linux/gpio.h $HOME/include/linux/.` (use actual path)
* `cd ~/javauio/periphery`
* `mvn clean install "-DCFLAGS=-I$HOME/include"`
* Add `-Dmaven.compiler.source=11 -Dmaven.compiler.target=11` for ARM32
* Exit and login again, so permissions from install script take effect

Install gpiod

* `sudo apt install -y gpiod`

Find the GPIO chip addresses.
* `gpiodetect`
    * `gpiochip0 [1c20800.pinctrl] (224 lines)`
    * `gpiochip1 [1f02c00.pinctrl] (32 lines)`
    * Here we see the chips and addresses.

Have a look at the [pin names](https://linux-sunxi.org/Xunlong_Orange_Pi_PC#Expansion_Port)
* Start with PA0 with A being group name. So group A data register offset is 0x10 for the H3. You can calculate
the sysfs number (should be the same for GPIOD chip 0) using (position of letter in alphabet - 1) * 32 + pin number. Thus
PA0 should be at 1(A)-1 = 0 * 32 + 0 or 0. To test this use the following command:
* `sudo env "PATH=$PATH" java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.mmio.MemScan -a 0x1c20800 -w 0x10 -d 0 -l 0`
```
16:14:08.895 [main] DEBUG com.codeferm.periphery.mmio.MemScan - Memory address 0x01c20800 words 0x00000010
16:14:09.380 [main] INFO com.codeferm.periphery.mmio.MemScan - Mode difference found at offset 0x00000000 before 0x00200000 after 0x00200001 difference 0x00000001
16:14:09.384 [main] INFO com.codeferm.periphery.mmio.MemScan - Data difference found at offset 0x00000010 before 0x00000000 after 0x00000001 difference 0x00000001
16:14:09.387 [main] INFO com.codeferm.periphery.mmio.MemScan - Pull up difference found at offset 0x00000010 before 0x00000000 after 0x00000001 difference 0x00000001
16:14:09.389 [main] INFO com.codeferm.periphery.mmio.MemScan - Pull up difference found at offset 0x0000001c before 0x0000a6a8 after 0x0000a6a9 difference 0x00000001
16:14:09.391 [main] INFO com.codeferm.periphery.mmio.MemScan - Pull down difference found at offset 0x00000010 before 0x00000001 after 0x00000000 difference 0x00000001
16:14:09.393 [main] INFO com.codeferm.periphery.mmio.MemScan - Pull down difference found at offset 0x0000001c before 0x0000a6a9 after 0x0000a6aa difference 0x00000001
```
Now take PG9 with G being group name. So group G data register offset is 0xe8. PG9 should be at 7(G)-1 = 6 * 32 + 9 or 201. To test this use the following command:
* `sudo env "PATH=$PATH" java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.mmio.MemScan -a 0x1c208e8 -w 0x20 -d 0 -l 201`
* Notice we add pin group to address (0x1c208e8) and scan 0x20 words?
```
10:30:23.610 [main] DEBUG com.codeferm.periphery.mmio.MemScan - Memory address 0x01c208e8 words 0x00000020
10:30:24.095 [main] INFO com.codeferm.periphery.mmio.MemScan - Data difference found at offset 0x00000000 before 0x00000000 after 0x00000200 difference 0x00000200
10:30:24.099 [main] INFO com.codeferm.periphery.mmio.MemScan - Pull up difference found at offset 0x00000000 before 0x00000000 after 0x00000200 difference 0x00000200
10:30:24.101 [main] INFO com.codeferm.periphery.mmio.MemScan - Pull up difference found at offset 0x0000000c before 0x0002a555 after 0x0006a555 difference 0x00040000
10:30:24.103 [main] INFO com.codeferm.periphery.mmio.MemScan - Pull down difference found at offset 0x00000000 before 0x00000200 after 0x00000000 difference 0x00000200
10:30:24.105 [main] INFO com.codeferm.periphery.mmio.MemScan - Pull down difference found at offset 0x0000000c before 0x0006a555 after 0x000aa555 difference 0x00040000
```
I usually test every pin this way to make sure I'm on the right track. So now
lets copy our register properties to our test device and generate the map file.
* `sudo env "PATH=$PATH" java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.mmio.Gen -i opiplus.properties -o opiplus-map.properties`

And finally lets run the performance test
* `sudo env "PATH=$PATH" java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.mmio.Perf -i opiplus-map.properties -d 0 -l 0`
```
12:49:31.956 [main] DEBUG com.codeferm.periphery.mmio.File - Properties loaded from file opiplus-map.properties
12:49:32.570 [main] INFO com.codeferm.periphery.mmio.Perf - Running GPIOD write test with 10000000 samples
12:50:10.638 [main] INFO com.codeferm.periphery.mmio.Perf - 262.79 KHz
12:50:10.640 [main] INFO com.codeferm.periphery.mmio.Perf - Running good MMIO write test with 10000000 samples
12:50:48.089 [main] INFO com.codeferm.periphery.mmio.Perf - 267.04 KHz
12:50:48.090 [main] INFO com.codeferm.periphery.mmio.Perf - Running best MMIO write test with 10000000 samples
12:50:57.510 [main] INFO com.codeferm.periphery.mmio.Perf - 1061.68 KHz
```
So one thing to note is JDK 21 on ARM32 seems to be much slower than JDK 11. Lets switch over to JDK 11:
```
14:20:55.201 [main] DEBUG com.codeferm.periphery.mmio.File - Properties loaded from file opiplus-map.properties
14:20:55.884 [main] INFO com.codeferm.periphery.mmio.Perf - Running GPIOD write test with 10000000 samples
14:21:27.664 [main] INFO com.codeferm.periphery.mmio.Perf - 314.81 KHz
14:21:27.666 [main] INFO com.codeferm.periphery.mmio.Perf - Running good MMIO write test with 10000000 samples
14:21:57.749 [main] INFO com.codeferm.periphery.mmio.Perf - 332.44 KHz
14:21:57.751 [main] INFO com.codeferm.periphery.mmio.Perf - Running best MMIO write test with 10000000 samples
14:22:02.104 [main] INFO com.codeferm.periphery.mmio.Perf - 2298.85 KHz

```
I'll leave it up to you how important that performance delta is. In some cases
it's overkill and you'll want to stick with JDK 21. For now I'm keeping the code
compatible with JDK11, so it's really just a compile time issue. Using an oscilloscope
is the only way to know the true speed. In this case JDK 21 allowed around 900 KHz
GPIO toggle.

## Run u8g2 tools
This utility generates code for u8g2 library instead of manually dealing with updates all the time. 
* `java -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/u8g2/target/u8g2-1.0.0-SNAPSHOT-linux32.jar com.codeferm.u8g2.CodeGen --help`
