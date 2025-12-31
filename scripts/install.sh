#!/bin/bash
#
# Created on November 28, 2021
#
# @author: sgoldsmith
#
# Install system dependencies, UIO permissions, and build HawtJNI/JavaUIO.
# This script assumes install-java.sh has already been run.
#
# Steven P. Goldsmith
# sgjava@gmail.com
# 

# Get architecture
arch=$(uname -m)

# Temp dir for downloads
tmpdir="$HOME/temp"
mkdir -p "$tmpdir"

# Logfile setup
logfile="$PWD/install.log"
rm -f "$logfile"

log(){
    timestamp=$(date +"%m-%d-%Y %k:%M:%S")
    echo "$timestamp $1"
    echo "$timestamp $1" >> "$logfile" 2>&1
}

# 1. Source the environment set by install-java.sh
# This ensures $JAVA_HOME and $SDKMAN_DIR are available in the current subshell
if [ -f "/etc/environment" ]; then
    # We use a trick to export vars from /etc/environment
    export $(exclude_vars="PATH" grep -v '^PATH=' /etc/environment | xargs)
fi

# Ensure Maven and Java are in the current PATH for this script session
export SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]] && source "$SDKMAN_DIR/bin/sdkman-init.sh"

log "Using JAVA_HOME: $JAVA_HOME"
log "Using Maven: $(mvn -version | head -n 1)"

# 2. Install Build Dependencies
log "Installing dependencies..."
sudo apt-get update
sudo apt-get -y install build-essential autoconf automake libtool git libsdl2-dev >> "$logfile" 2>&1

# 3. UIO Permissions Service
log "Installing UIO Permissions Service..."
sudo usermod -a -G dialout "$USER" >> "$logfile" 2>&1
sudo groupadd -f uio >> "$logfile" 2>&1
sudo usermod -a -G uio "$USER" >> "$logfile" 2>&1

sudo cp uio-permissions.sh /usr/local/bin/. >> "$logfile" 2>&1
sudo chmod +x /usr/local/bin/uio-permissions.sh

sudo tee /etc/systemd/system/uio-permissions.service > /dev/null <<EOT
[Unit]
Description=UIO Permissions
 
[Service]
Type=oneshot
ExecStart=/usr/local/bin/uio-permissions.sh
RemainAfterExit=no

[Install]
WantedBy=multi-user.target
EOT

sudo systemctl daemon-reload
sudo systemctl enable uio-permissions >> "$logfile" 2>&1
log "Starting UIO Permissions..."
sudo systemctl start uio-permissions >> "$logfile" 2>&1

# 4. Copy udev rules 
sudo cp 98-sysfs.rules /etc/udev/rules.d/. >> "$logfile" 2>&1
sudo cp 99-pwm.rules /etc/udev/rules.d/. >> "$logfile" 2>&1

# 5. Build HawtJNI
cd "$HOME" || exit
log "Removing old hawtjni"
rm -rf hawtjni >> "$logfile" 2>&1
log "Cloning HawtJNI..."
git clone https://github.com/sgjava/hawtjni.git >> "$logfile" 2>&1
cd hawtjni || exit
log "Building HawtJNI..."
# Using the JAVA_HOME and Maven provided by SDKMAN
mvn clean install -DskipTests -pl '!hawtjni-example' >> "$logfile" 2>&1

# 6. Prepare native sources (c-periphery and U8g2)
cd "$HOME" || exit
log "Updating native sources..."
rm -rf c-periphery u8g2 >> "$logfile" 2>&1

git clone --depth 1 https://github.com/sgjava/c-periphery.git >> "$logfile" 2>&1
cp -a "$HOME/c-periphery/src/"*.c "$HOME/javauio/periphery/src/main/native-package/src/"
cp -a "$HOME/c-periphery/src/"*.h "$HOME/javauio/periphery/src/main/native-package/src/"

git clone --depth 1 --recurse-submodules https://github.com/sgjava/u8g2 >> "$logfile" 2>&1
# Sync U8g2 sources to JavaUIO native directory
cp -a "$HOME/u8g2/csrc/." "$HOME/javauio/u8g2/src/main/native-package/src/"
cp -a "$HOME/u8g2/cppsrc/." "$HOME/javauio/u8g2/src/main/native-package/src/"
cp -a "$HOME/u8g2/sys/sdl/common/." "$HOME/javauio/u8g2/src/main/native-package/src/"
cp -a "$HOME/u8g2/sys/arm-linux/c-periphery/src/." "$HOME/javauio/u8g2/src/main/native-package/src/"
cp -a "$HOME/u8g2/sys/arm-linux/port/." "$HOME/javauio/u8g2/src/main/native-package/src/"

# 7. Java UIO build
cd "$HOME/javauio" || exit
log "Building Java UIO..."
# Note: Source/Target set to 25 to match install-java.sh
mvn clean install -Dmaven.compiler.source=25 -Dmaven.compiler.target=25 >> "$logfile" 2>&1

log "Done"
