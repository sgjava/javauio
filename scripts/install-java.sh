#!/bin/bash
#
# Created on December 30, 2025
#
# @author: sgoldsmith
#
# Install dependencies, JDK 25 for all but x86_32.
#
# Steven P. Goldsmith
# sgjava@gmail.com
#

# 1. Detect Architecture
ARCH=$(uname -m)
echo "--------------------------------------------------"
echo "Detected Architecture: $ARCH"
echo "--------------------------------------------------"

SHIPILEV_ARM32_URL="https://builds.shipilev.net/openjdk-jdk25/openjdk-jdk25-linux-arm32-hflt-server.tar.xz"
JAVA_TMP="$HOME/.java_tmp"

# 2. System Prep & Tmp Dir
sudo apt update && sudo apt install -y curl zip unzip wget xz-utils
mkdir -p "$JAVA_TMP"
chmod 777 "$JAVA_TMP"

# 3. Install/Init SDKMAN
export SDKMAN_DIR="$HOME/.sdkman"
if [[ ! -d "$SDKMAN_DIR" ]]; then
    echo "Installing SDKMAN..."
    curl -s "https://get.sdkman.io" | bash || true
fi

# Idempotent .bashrc patch
if ! grep -q "sdkman-init.sh" "$HOME/.bashrc"; then
    cat << 'EOF' >> "$HOME/.bashrc"
export SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"
EOF
fi

# Disable native for ARM32/niche archs to avoid 404s
[[ -f "$SDKMAN_DIR/etc/config" ]] && sed -i 's/sdkman_native_enable=true/sdkman_native_enable=false/' "$SDKMAN_DIR/etc/config"
source "$SDKMAN_DIR/bin/sdkman-init.sh"

# 4. Install Java based on Arch
case $ARCH in
    armv7l|armv8l)
        JDK_DIR="$SDKMAN_DIR/candidates/java/25-arm32-local"
        if [ ! -d "$JDK_DIR" ]; then
            echo "Downloading ARM32 Shipilev JDK 25..."
            wget -q -O /tmp/jdk25_arm32.tar.xz "$SHIPILEV_ARM32_URL"
            mkdir -p "$JDK_DIR"
            tar -xJf /tmp/jdk25_arm32.tar.xz -C "$JDK_DIR" --strip-components=1
            
            # --- CRITICAL PERMISSION FIX FOR LOCAL JDK ---
            echo "Applying execution bits to Local JDK..."
            chmod -R +rX "$JDK_DIR"
            # Ensure the specific spawn helper is executable
            find "$JDK_DIR" -name "jspawnhelper" -exec chmod +x {} +
            # ---------------------------------------------

            sdk install java 25-arm32-local "$JDK_DIR"
        fi
        sdk default java 25-arm32-local
        ;;
    *)
        sdk install java 25-zulu
        sdk default java 25-zulu
        ;;
esac

# 5. Install Maven and Ant
sdk install maven
sdk install ant

# 6. Global Environment Setup (Idempotent)
echo "Updating /etc/environment..."

update_env_var() {
    local var_name=$1
    local var_value=$2
    if grep -q "^${var_name}=" /etc/environment; then
        sudo sed -i "s|^${var_name}=.*|${var_name}=\"${var_value}\"|" /etc/environment
    else
        echo "${var_name}=\"${var_value}\"" | sudo tee -a /etc/environment
    fi
}

JAVA_PATH="$SDKMAN_DIR/candidates/java/current"
MAVEN_PATH="$SDKMAN_DIR/candidates/maven/current"
ANT_PATH="$SDKMAN_DIR/candidates/ant/current"
BASE_PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

update_env_var "PATH" "$JAVA_PATH/bin:$MAVEN_PATH/bin:$ANT_PATH/bin:$BASE_PATH"
update_env_var "JAVA_HOME" "$JAVA_PATH"
update_env_var "JAVA_OPTS" "-Djava.io.tmpdir=$JAVA_TMP"
update_env_var "MAVEN_OPTS" "-Djava.io.tmpdir=$JAVA_TMP"

# 7. Final Global Permissions Fix
echo "Finalizing global permissions..."
chmod +x $HOME $SDKMAN_DIR $SDKMAN_DIR/candidates
find $SDKMAN_DIR/candidates/ -type d -exec chmod 755 {} +
find $SDKMAN_DIR/candidates/ -path "*/bin/*" -exec chmod 755 {} +

echo "--------------------------------------------------"
echo "Setup Complete!"
echo "Please run: source /etc/environment"
echo "Then verify with: mvn -version"
echo "--------------------------------------------------"