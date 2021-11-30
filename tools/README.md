![Title](images/title.png)

Java UIO Tools provides CLI programs, so you do not have to compile code with
hard coded pins, ports, etc. Note that the native library jar has a suffix such
as linux32, so depending on your target platform it could be linux64.

## High performance GPIO using MMIO

NanoPi Duo (H2+) example:
* `sudo java -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.mmio.Gen -i duo.properties -o duo-map.properties`
* `sudo java -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.mmio.Perf -i duo-map.properties -d 0 -l 203`

NanoPi Neo Plus2 (H5) example:
* `sudo java -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux64.jar com.codeferm.periphery.mmio.Gen -i neoplus2.properties -o neoplus2-map.properties`
* `sudo java -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux64.jar com.codeferm.periphery.mmio.Perf -i neoplus2-map.properties -d 1 -l 203`

MemScan tool to help map registers:

For example on the ODROID C2 lets look at chip 0 and line 9
* `sudo java -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.mmio.MemScan -a 0xc8100024 -w 0x03 -d 0 -l 9`

Output:

```11:55:39.342 [main] DEBUG MemScan - Memory address 0xc8100024 words 0x00000003
11:55:39.538 [main] INFO  MemScan - Mode difference found at offset 0x00000000 before 0xa0003ef7 after 0xa0003cf7 difference 0x00000200
11:55:39.540 [main] INFO  MemScan - Mode difference found at offset 0x00000004 before 0x80003ef7 after 0x80003cf7 difference 0x00000200
11:55:39.543 [main] INFO  MemScan - Data difference found at offset 0x00000000 before 0xa0003cf7 after 0xa2003cf7 difference 0x02000000
11:55:39.545 [main] INFO  MemScan - Data difference found at offset 0x00000004 before 0x80003cf7 after 0x80003ef7 difference 0x00000200
11:55:39.548 [main] ERROR MemScan - Device 0 line 9 Error Kernel version does not support configuring GPIO line bias
```

Note the bias error is due to no compiling with latest gpio.h header.

## Run u8g2 tools
* `java -cp $HOME/javauio/tools/target/tools-1.0.0-SNAPSHOT.jar:$HOME/javauio/u8g2/target/u8g2-1.0.0-SNAPSHOT-linux32.jar com.codeferm.u8g2.CodeGen --help`
