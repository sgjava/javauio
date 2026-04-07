# Java UIO 2 (FFM)

![Title](images/title.png)

[![JDK 25 LTS](https://img.shields.io/badge/JDK-25_LTS-orange.svg)](https://openjdk.java.net/projects/jdk/25/)
[![Ubuntu 24.04](https://img.shields.io/badge/Ubuntu-24.04_Noble-blue.svg)](https://ubuntu.com/blog/whats-new-in-security-for-ubuntu-24-04-lts)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![API: FFM/Panama](https://img.shields.io/badge/API-FFM%2FPanama-red.svg)](https://openjdk.org/projects/panama/)

**Java UIO 2** is the next-generation evolution of the Java UIO project, rebuilt from the ground up to leverage the **Foreign Function & Memory API (FFM)**. By moving beyond traditional JNI, Java UIO 2 achieves unprecedented performance and hardware-level accuracy for Linux Userspace IO. Engineered for **JDK 25**, it provides a cutting-edge cross-platform solution for modern embedded systems.

## 🚀 The New Standard
This project adheres to rigorous development standards to ensure production-grade reliability and performance:
* **Modern Java:** Full utilization of `var`, `final`, and the latest FFM features.
* **Zero Allocation:** Frame-based operations (like SSD1331 rendering) use pre-allocated buffers to eliminate GC pressure during high-speed I/O.
* **Clean Architecture:** **No native loaders in device classes.** Native loading is handled at the application level, keeping core logic decoupled and lean.
* **Complete Documentation:** 100% Javadoc coverage for all public APIs.

---

## 🏗️ Architectural Contrast: Why Java UIO 2?

| Feature | Pi4J (v4.0+) | diozero (v1.4+) | **Java UIO 2** |
| :--- | :--- | :--- | :--- |
| **Model** | **Provider-Centric.** Relies on a plugin architecture to map hardware. | **Device-Centric.** Uses a "Factory" abstraction to wrap pins in objects. | **Kernel-Direct.** Treats the Linux Kernel ABI as the only provider. |
| **FFM Integration** | **Plugin Level.** FFM is an optional provider module. | **JNI Core.** Primarily utilizes JNI/JNA for native access. | **Native FFM.** Built specifically for Project Panama as the core engine. |
| **Board Support** | Board-specific definitions/configs often required. | Broad, but requires factory logic for each SoC. | **Universal.** If it runs a standard Linux kernel, it works instantly. |
| **Graphics** | Community-ported Java drivers. | High-level/basic shape support. | **Deep u8g2 Binding.** Full C-performance for **SSD1331** OLEDs. |
| **Portability** | Heavyweight (Core + Provider + Config). | Lightweight core, but SoC-specific factories. | **Ultra-Lightweight.** Zero-dependency bridge to Linux interfaces. |

### 1. Universal Kernel-Standardized I/O
Most libraries require a "Provider" or specific plugin for every new board. **Java UIO 2** bypasses this middleman. By targeting the **Standard Linux Kernel ABI** (Character Devices and UIO), any board running a modern kernel is supported immediately. The Kernel is the only "Provider" you need.

### 2. Deep Graphics with u8g2 & SSD1331
While other libraries stop at "Blinky," Java UIO 2 provides a professional-grade graphics stack. By binding the industry-standard **u8g2** library via FFM, you gain access to hundreds of fonts and optimized drawing routines at near-native speeds.

### 3. Hardware-Accurate Native Sizing
A major weakness in traditional libraries is their reliance on manual JNI headers. Java UIO 2 uses a **Native Sizer** during the build process (via QEMU emulation). This guarantees that `MemoryLayout` offsets are byte-perfect for the target CPU architecture (ARM64, ARM32, or x86_64), preventing alignment traps and crashes.

---

## 🛠️ Performance Benchmark (Pine64 ARM64)
In raw performance testing on the Pine A64 (Cortex-A53), the FFM implementation demonstrated a massive leap over established JNI methods:

| Operation | HawtJNI (Legacy) | **Java UIO 2 (FFM)** | **Improvement** |
| :--- | :--- | :--- | :--- |
| **GPIO Writes** | ~292k ops/sec | **~561k ops/sec** | **+91.5%** |
| **GPIO Reads** | ~400k ops/sec | **~582k ops/sec** | **+45.5%** |

*Note: Benchmarks were performed on single-core execution. With FFM, the Java-to-Native bridge is no longer the bottleneck; the performance limit is now defined by the Linux Kernel's ioctl latency.*

---

## 🌍 Architecture Support Matrix (JDK 25)

| Architecture | JNI (Java UIO) | **FFM (Java UIO 2)** |
| :--- | :---: | :---: |
| **ARM32 (v7)** | ✅ Supported | ⏳ Pending Linker |
| **ARM64 (v8)** | ✅ Supported | ✅ **Recommended** |
| **X86_64** | ✅ Supported | ✅ **Recommended** |

---

## 📦 Project Depth
This repository contains more than just a library; it includes exhaustive demos demonstrating real-world complexity:
* **SSD1331 OLED:** Full-color graphics, custom fonts, and buffer rotations.
* **Game Logic:** Atari-style Centipede clones showcasing memory-efficient movement controllers.
* **Native Demos:** Direct ports of JNI demos to the modern FFM era.

### Download and Build
```bash
# Download project
sudo apt install git
cd ~/
git clone --depth 1 [https://github.com/sgjava/javauio2.git](https://github.com/sgjava/javauio2.git)

# Setup and Install
cd ~/javauio2/scripts
./install-java.sh
./setup-permissions.sh # ARM only
sudo reboot

# Build with Maven
cd ~/javauio2
mvn clean install # X86_64 default
# Use -P arm64 (aarch64) or -P arm32 (armhf) for profiles