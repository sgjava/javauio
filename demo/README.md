![Title](images/title.png)

Java UIO Demo provides CLI programs, so you do not have to compile code with
hard coded pins, ports, etc. Note that the native library jar has a suffix such
as linux32, so depending on your target platform it could be different.
 
## Run Periphery demos
 To see a list of demos 
[browse](https://github.com/sgjava/javauio/tree/main/demo/src/main/java/com/codeferm/periphery/demo)
code. Just pass in --help to get list of command line arguments.

* `java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/demo/target/demo-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar com.codeferm.periphery.demo.LedBlink --help`

## Run U8g2 demos
To see a list of demos 
[browse](https://github.com/sgjava/javauio/tree/main/demo/src/main/java/com/codeferm/u8g2/demo)
code. Just pass in --help to get list of command line arguments.

* `java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/demo/target/demo-1.0.0-SNAPSHOT.jar:$HOME/javauio/u8g2/target/u8g2-1.0.0-SNAPSHOT-linux32.jar com.codeferm.u8g2.demo.SimpleText --help`

## Run U8g2 plus Periphery demos
To see a list of demos 
[browse](https://github.com/sgjava/javauio/tree/main/demo/src/main/java/com/codeferm/all/demo)
code. Just pass in --help to get list of command line arguments.

* `java --enable-native-access=ALL-UNNAMED -cp $HOME/javauio/demo/target/demo-1.0.0-SNAPSHOT.jar:$HOME/javauio/periphery/target/periphery-1.0.0-SNAPSHOT-linux32.jar:$HOME/javauio/u8g2/target/u8g2-1.0.0-SNAPSHOT-linux32.jar com.codeferm.all.demo.LedDisplay --help`
