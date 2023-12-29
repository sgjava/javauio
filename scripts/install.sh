#!/bin/sh
#
# Created on November 28, 2021
#
# @author: sgoldsmith
#
# Install dependencies, JDK 21, Maven and HawtJNI for Ubuntu/Debian.
# If JDK or Maven was already installed with this script then they will be replaced.
#
# Steven P. Goldsmith
# sgjava@gmail.com
# 

# Get architecture
arch=$(uname -m)

# Temp dir for downloads, etc.
tmpdir="$HOME/temp"

# stdout and stderr for commands logged
logfile="$PWD/install.log"
rm -f $logfile

# Simple logger
log(){
    timestamp=$(date +"%m-%d-%Y %k:%M:%S")
    echo "$timestamp $1"
    echo "$timestamp $1" >> $logfile 2>&1
}

log "Installing dependencies..."
# Install build/dev tools/libs
sudo apt-get -y install build-essential autoconf automake libtool git libsdl2-dev >> $logfile 2>&1

log "Installing UIO Permissions Service..."
# Install UIO Permissions service
sudo usermod -a -G dialout $LOGNAME >> $logfile 2>&1
sudo groupadd uio >> $logfile 2>&1
sudo usermod -a -G uio $LOGNAME >> $logfile 2>&1
# Copy permission script
sudo cp uio-permissions.sh /usr/local/bin/. >> $logfile 2>&1
sudo rm -f /etc/systemd/system/uio-permissions.service
sudo tee -a /etc/systemd/system/uio-permissions.service > /dev/null <<EOT
[Unit]
Description=UIO Permissions
 
[Service]
Type=oneshot
ExecStart=/usr/local/bin/uio-permissions.sh
RemainAfterExit=no

[Install]
WantedBy=multi-user.target
EOT

sudo -E systemctl enable uio-permissions >> $logfile 2>&1

# Start up UIO Permissions
log "Starting UIO Permissions..."
sudo -E service uio-permissions start >> $logfile 2>&1

# Copy udev rules 
sudo cp 98-sysfs.rules /etc/udev/rules.d/. >> $logfile 2>&1
sudo cp 99-pwm.rules /etc/udev/rules.d/. >> $logfile 2>&1

#Default JDK
javahome=/usr/lib/jvm/jdk21
jdk=21

# ARM 32
if [ "$arch" = "armv7l" ]; then
    # Liberica used for ARM32
    jdkurl="https://download.bell-sw.com/java/21.0.1+12/bellsoft-jdk21.0.1+12-linux-arm32-vfp-hflt.tar.gz"
# ARM 64
elif [ "$arch" = "aarch64" ]; then
	jdkurl="https://cdn.azul.com/zulu/bin/zulu21.30.15-ca-jdk21.0.1-linux_aarch64.tar.gz"
# X86_32
elif [ "$arch" = "i586" ] || [ "$arch" = "i686" ]; then
        # Liberica used for x86
	jdkurl="https://download.bell-sw.com/java/21.0.1+12/bellsoft-jdk21.0.1+12-linux-i586.tar.gz"
# X86_64	
elif [ "$arch" = "x86_64" ]; then
    jdkurl="https://cdn.azul.com/zulu/bin/zulu21.30.15-ca-jdk21.0.1-linux_x64.tar.gz"
fi
export javahome
# Just JDK archive name
jdkarchive=$(basename "$jdkurl")

# Install Zulu Java JDK
log "Downloading $jdkarchive to $tmpdir"
wget -q --directory-prefix=$tmpdir "$jdkurl" >> $logfile 2>&1
filename="jdk$jdk"
log "Extracting $jdkarchive to $tmpdir/$filename"
mkdir "$tmpdir/$filename" >> $logfile 2>&1
tar -xf "$tmpdir/$jdkarchive" -C "$tmpdir/$filename" --strip-components=1 >> $logfile 2>&1
log "Removing $javahome"
sudo -E rm -rf "$javahome" >> $logfile 2>&1
sudo mkdir -p /usr/lib/jvm >> $logfile 2>&1
log "Moving $tmpdir/$filename to $javahome"
sudo -E mv "$tmpdir/$filename" "$javahome" >> $logfile 2>&1
sudo -E update-alternatives --install "/usr/bin/java" "java" "$javahome/bin/java" 1 >> $logfile 2>&1
sudo -E update-alternatives --install "/usr/bin/javac" "javac" "$javahome/bin/javac" 1 >> $logfile 2>&1
sudo -E update-alternatives --install "/usr/bin/jar" "jar" "$javahome/bin/jar" 1 >> $logfile 2>&1
sudo -E update-alternatives --install "/usr/bin/javadoc" "javadoc" "$javahome/bin/javadoc" 1 >> $logfile 2>&1
# See if JAVA_HOME exists and if not add it to /etc/environment
if grep -q "JAVA_HOME" /etc/environment; then
    log "JAVA_HOME already exists, deleting"
    sudo sed -i '/JAVA_HOME/d' /etc/environment	
fi
# Add JAVA_HOME to /etc/environment
log "Adding JAVA_HOME to /etc/environment"
sudo -E sh -c 'echo "JAVA_HOME=$javahome" >> /etc/environment'
. /etc/environment
log "JAVA_HOME = $JAVA_HOME"

# Apache Maven
mavenurl="https://downloads.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz"
mavenarchive=$(basename "$mavenurl")
mavenver="apache-maven-3.9.6"
mavenhome="/opt/maven"
export mavenhome
mavenbin="/opt/maven/bin"

# Install latest Maven
log "Installing Maven $mavenver..."
log "Downloading $mavenurl to $tmpdir     "
wget -q --directory-prefix=$tmpdir "$mavenurl" >> $logfile 2>&1
log "Extracting $tmpdir/$mavenarchive to $tmpdir"
tar -xf "$tmpdir/$mavenarchive" -C "$tmpdir" >> $logfile 2>&1
log "Removing $mavenhome"
sudo -E rm -rf "$mavenhome" >> $logfile 2>&1
# In case /opt doesn't exist
sudo mkdir -p /opt >> $logfile 2>&1
log "Moving $tmpdir/$mavenver to $mavenhome"
sudo -E mv "$tmpdir/$mavenver" "$mavenhome" >> $logfile 2>&1
# See if M2_HOME exists and if not add it to /etc/environment
if grep -q "M2_HOME" /etc/environment; then
    log "M2_HOME already exists"
else
    # OS will not find Maven by M2_HOME, so create link to where it's looking
    sudo -E ln -sf "$mavenbin/mvn" /usr/bin/mvn >> $logfile 2>&1
    # Add M2_HOME to /etc/environment
    log "Adding M2_HOME to /etc/environment"
    sudo -E sh -c 'echo "M2_HOME=$mavenhome" >> /etc/environment'
    . /etc/environment
    log "M2_HOME = $M2_HOME"
fi

log "PATH = $PATH"

# Clean up
log "Removing $tmpdir"
rm -rf "$tmpdir" >> $logfile 2>&1

# HawtJNI install (using my JDK 21 fork)
export JAVA_HOME=$javahome
cd >> $logfile 2>&1
log "Removing hawtjni"
rm -rf hawtjni >> $logfile 2>&1
log "Cloning HawtJNI..."
# My fork that works with JDK 21
git clone https://github.com/sgjava/hawtjni.git >> $logfile 2>&1
cd hawtjni >> $logfile 2>&1
log "Building HawtJNI..."
# Callback assert fails on 32 bit ARM, so skipping tests until I can fix
# hawtjni-example fails to build on armv7l
mvn clean install -Dmaven.compiler.source=$jdk -Dmaven.compiler.target=$jdk -DskipTests -pl '!hawtjni-example' --log-file="../javauio/scripts/hawtjni.log" >> $logfile 2>&1

# Copy c-periphery source
cd >> $logfile 2>&1
log "Removing c-periphery"
rm -rf c-periphery >> $logfile 2>&1
log "Cloning c-periphery..."
# My fork, so changes don't break build
git clone --depth 1 https://github.com/sgjava/c-periphery.git >> $logfile 2>&1
log "Copying files into periphery..."
cp -a "$HOME/c-periphery/src/"*.c "$HOME/javauio/periphery/src/main/native-package/src/"
cp -a "$HOME/c-periphery/src/"*.h "$HOME/javauio/periphery/src/main/native-package/src/"

# Copy U8g2 source
cd >> $logfile 2>&1
log "Removing u8g2"
rm -rf u8g2 >> $logfile 2>&1
log "Cloning U8g2..."
# My fork, so changes don't break build
git clone --depth 1 --recurse-submodules https://github.com/sgjava/u8g2 >> $logfile 2>&1
log "Copying files into u8g2..."
# Order is important here because some files will be overwritten
cp -a "$HOME/u8g2/csrc/." "$HOME/javauio/u8g2/src/main/native-package/src/"
cp -a "$HOME/u8g2/cppsrc/." "$HOME/javauio/u8g2/src/main/native-package/src/"
cp -a "$HOME/u8g2/sys/sdl/common/." "$HOME/javauio/u8g2/src/main/native-package/src/"
cp -a "$HOME/u8g2/sys/arm-linux/c-periphery/src/." "$HOME/javauio/u8g2/src/main/native-package/src/"
cp -a "$HOME/u8g2/sys/arm-linux/port/." "$HOME/javauio/u8g2/src/main/native-package/src/"

# Java UIO build
cd >> $logfile 2>&1
cd javauio >> $logfile 2>&1
log "Building Java UIO..."
mvn clean install -Dmaven.compiler.source=$jdk -Dmaven.compiler.target=$jdk --log-file="scripts/javauio.log" >> $logfile 2>&1
log "Done"
