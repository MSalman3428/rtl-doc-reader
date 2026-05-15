# Build and Deploy RTL Doc Reader to Mobile

This guide explains how to build and run the RTL Document Reader app on your Android mobile device.

---

## Prerequisites

### Option 1: Using Android Studio (Recommended)
- **Android Studio** (2023.1 or later) - [Download](https://developer.android.com/studio)
- **JDK 17** or later (usually included with Android Studio)
- **Android SDK** (API 21+)
- Android device or emulator

### Option 2: Using Command Line
- **Java 17 JDK** installed
- **Android SDK** with build tools (API 34)
- Gradle will auto-download via wrapper

---

## 🚀 Method 1: Build with Android Studio (Easiest)

### Step 1: Open Project
1. Open Android Studio
2. Select **File** → **Open...**
3. Navigate to `/home/sheikh/Documents/rtl-doc-reader`
4. Click **Open**

### Step 2: Wait for Gradle Sync
- Android Studio will automatically:
  - Download required dependencies
  - Configure the Kotlin compiler
  - Build the project index

**This may take 3-5 minutes on first build**

### Step 3: Build APK

**Debug Build (for testing):**
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

**Release Build (for production):**
```
Build → Generate Signed Bundle / APK
→ Select "APK"
→ Create new keystore or select existing
→ Fill in keystore details
→ Select "release" build variant
→ Click "Finish"
```

### Step 4: Install on Device

**Via USB (Recommended):**
1. Connect Android phone via USB
2. Enable **Developer Mode**:
   - Settings → About Phone → Tap "Build Number" 7 times
3. Enable **USB Debugging**:
   - Settings → Developer Options → USB Debugging → ON
4. In Android Studio: **Run** → **Run 'app'**
5. Select your device
6. Click **OK**

**Via Emulator:**
1. Open Android Virtual Device Manager
2. Create/select an emulator (API 21+)
3. Start the emulator
4. In Android Studio: **Run** → **Run 'app'**
5. Select the emulator
6. Click **OK**

---

## 🖥️ Method 2: Build via Command Line

### Step 1: Navigate to Project
```bash
cd /home/sheikh/Documents/rtl-doc-reader
```

### Step 2: Build Debug APK
```bash
./gradlew assembleDebug
```

**Output:** `app/build/outputs/apk/debug/app-debug.apk`

### Step 3: Build Release APK
```bash
./gradlew assembleRelease
```

**Output:** `app/build/outputs/apk/release/app-release.apk`

### Step 4: Install on Device

**Connect via USB and install:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Or directly install and run:**
```bash
./gradlew installDebug
```

---

## 📱 Using Emulator from Command Line

### Create Emulator
```bash
# List available API levels
sdkmanager --list_installed

# Create emulator (example: API 30, name "rtl-test")
avdmanager create avd -n rtl-test -k "system-images;android-30;google_apis;x86_64"
```

### Start Emulator
```bash
emulator -avd rtl-test
```

### Build and Install
```bash
# Build
./gradlew assembleDebug

# Wait for emulator to start, then install
./gradlew installDebug

# OR use adb directly
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔍 Verify Installation

### Check if app is installed
```bash
adb shell pm list packages | grep rtldocreader
```

### Launch app from command line
```bash
adb shell am start -n com.example.rtldocreader/.MainActivity
```

### View logs
```bash
adb logcat | grep "rtldocreader"
```

---

## 📋 Troubleshooting

### Build Fails: "Java 17 required"
**Solution:**
```bash
# Check Java version
java -version

# Set JAVA_HOME to JDK 17
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### Error: "Android SDK not found"
**Solution:**
1. In Android Studio: **File** → **Settings** → **SDK Manager**
2. Click "Edit" next to Android SDK location
3. Make sure you have:
   - API 21+ SDK Platform
   - Build Tools 34

### Gradle Build Hangs
**Solution:**
```bash
# Stop gradle daemon
./gradlew --stop

# Clear cache
rm -rf .gradle/

# Retry
./gradlew assembleDebug
```

### Device Not Recognized
```bash
# List connected devices
adb devices

# If device shows "unauthorized":
# 1. Unplug device
# 2. On device: Settings → Developer Options → Revoke USB debugging
# 3. Reconnect and grant permission
```

### App Crashes on Launch
**Check logs:**
```bash
adb logcat | grep FATAL
```

**Common causes:**
- Missing permissions (check AndroidManifest.xml)
- Database issue (clear app data: `adb shell pm clear com.example.rtldocreader`)
- Dependency not available

---

## 📊 Build Configuration Reference

### Debug vs Release

| Aspect | Debug | Release |
|--------|-------|---------|
| Size | ~45 MB | ~15 MB (with R8/ProGuard) |
| Speed | Slower (unoptimized) | Faster (optimized) |
| Debuggable | Yes | No |
| Signing | Debug key | Custom key |
| For | Testing | Production/PlayStore |

### Gradle Commands Reference

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease

# Install to device/emulator
./gradlew installDebug

# Run tests
./gradlew test

# Build and install debug
./gradlew installDebug

# View dependency tree
./gradlew dependencies

# Check lint issues
./gradlew lint
```

---

## 🔑 Creating a Release Key for Play Store

### Generate Keystore
```bash
keytool -genkey -v -keystore ~/rtl-doc-reader.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias rtl-key-alias
```

**Fill in when prompted:**
- Keystore password: (create one, save it!)
- Key password: (same as keystore or different)
- First name: Your Name
- Last name: Company/Personal
- Organization: RTL Doc Reader
- City: Any
- State: Any
- Country: Any

### Use Keystore for Release Build

**In Android Studio:**
1. **Build** → **Generate Signed Bundle / APK**
2. Choose **APK**
3. Click **Create new...**
4. Browse to `~/rtl-doc-reader.keystore`
5. Enter passwords
6. Click **Next** → **Release** → **Finish**

**Via command line:**
```bash
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=$HOME/rtl-doc-reader.keystore \
  -Pandroid.injected.signing.store.password=KEYSTORE_PASSWORD \
  -Pandroid.injected.signing.key.alias=rtl-key-alias \
  -Pandroid.injected.signing.key.password=KEY_PASSWORD
```

---

## 📤 Distribute APK

### Email or Messaging
1. Build release APK
2. Located at: `app/build/outputs/apk/release/app-release.apk`
3. Attach to email or upload to file sharing

### Sideload on Android
1. Copy APK to device
2. On device: Settings → Apps → Allow installation from unknown sources
3. Open file manager, tap APK
4. Tap "Install"

### Upload to Google Play Store
1. Create Google Play Developer account ($25 one-time)
2. Create app listing
3. Upload signed APK
4. Fill in store details
5. Submit for review

---

## 📝 Project Structure for Building

```
rtl-doc-reader/
├── gradlew              ← Use this on Linux/Mac
├── gradlew.bat          ← Use this on Windows
├── gradle/
│   └── wrapper/         ← Gradle auto-download files
├── app/
│   ├── build.gradle.kts ← App dependencies
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/        ← Source code
│   │   └── res/         ← Resources
│   └── build/           ← Generated APKs after build
└── build.gradle.kts     ← Project config
```

---

## 🧪 Testing the Build

### Test on Device/Emulator
1. **Install**: `./gradlew installDebug`
2. **Open App** from launcher
3. **Grant Permissions** when prompted
4. **Search Test**:
   - Type: "کتاب" (Urdu word)
   - Should show message: "No matches" (no docs indexed yet)

### Add Test Document
To test search functionality:
1. Copy a PDF/DOCX/PPTX to device storage
2. App should index on next launch
3. Search for text in that document

---

## ⚙️ Advanced Options

### Build Variants
```bash
# Debug variant
./gradlew assembleDebug

# Release variant  
./gradlew assembleRelease

# Specific build type
./gradlew assemble --variant=release
```

### Optimize Build Speed
```bash
# Parallel compilation
./gradlew assembleDebug -x test --parallel

# Daemon for faster incremental builds
./gradlew assembleDebug --daemon

# Skip tests
./gradlew assembleDebug -x test
```

### Verbose Output
```bash
./gradlew assembleDebug --info
```

---

## 📞 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "Gradle sync failed" | Click "Sync Now" in Android Studio or run `./gradlew sync` |
| APK too large | Use ProGuard/R8 in release build |
| Permission denied | Make gradlew executable: `chmod +x gradlew` |
| Memory error during build | Increase heap: `export GRADLE_OPTS="-Xmx4g"` |
| Device offline | Restart adb: `adb kill-server && adb start-server` |

---

## 📚 Next Steps

1. **Build the app** using one of the methods above
2. **Install on device**
3. **Grant storage permissions** when prompted
4. **Add PDF/DOCX/PPTX files** to device
5. **Search for RTL text** (Arabic/Urdu/Pashto)
6. **Verify search results** highlight matches

---

## 📖 Resources

- **Android Studio:** https://developer.android.com/studio
- **Gradle Documentation:** https://docs.gradle.org
- **Android Docs:** https://developer.android.com/docs
- **Kotlin Documentation:** https://kotlinlang.org/docs
- **Jetpack Compose:** https://developer.android.com/jetpack/compose

---

**Build Status:** ✅ **Ready to Build**

You can now build and deploy the RTL Doc Reader to any Android device (API 21+).

