#!/bin/bash

# RTL Doc Reader - Quick Build Script for Linux/Mac
# This script builds the debug APK and prepares it for mobile deployment

set -e

PROJECT_ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "$PROJECT_ROOT"

echo "╔════════════════════════════════════════════════════╗"
echo "║     RTL Doc Reader - Build Script                  ║"
echo "║     Building Debug APK for Mobile                  ║"
echo "╚════════════════════════════════════════════════════╝"
echo ""

# Check if gradlew exists
if [ ! -f "$PROJECT_ROOT/gradlew" ]; then
    echo "❌ Error: gradlew not found. Please ensure you're in the project root."
    exit 1
fi

# Make gradlew executable
chmod +x "$PROJECT_ROOT/gradlew"
echo "✅ Gradle wrapper ready"
echo ""

# Clean previous builds
echo "🧹 Cleaning previous build artifacts..."
./gradlew clean --quiet
echo ""

# Build debug APK
echo "🔨 Building debug APK (this may take 2-5 minutes)..."
./gradlew assembleDebug

# Check if build succeeded
if [ -f "$PROJECT_ROOT/app/build/outputs/apk/debug/app-debug.apk" ]; then
    APK_PATH="$PROJECT_ROOT/app/build/outputs/apk/debug/app-debug.apk"
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    
    echo ""
    echo "╔════════════════════════════════════════════════════╗"
    echo "║          ✅ BUILD SUCCESSFUL!                      ║"
    echo "╚════════════════════════════════════════════════════╝"
    echo ""
    echo "📦 APK Ready:"
    echo "   Location: $APK_PATH"
    echo "   Size: $APK_SIZE"
    echo ""
    echo "📱 Next Steps:"
    echo ""
    echo "   Option 1: Install on USB-connected device"
    echo "   $ adb install $APK_PATH"
    echo ""
    echo "   Option 2: Install and run"
    echo "   $ ./gradlew installDebug"
    echo ""
    echo "   Option 3: Share APK file"
    echo "   $ cp $APK_PATH ~/Desktop/"
    echo ""
else
    echo ""
    echo "❌ Build failed. Check error messages above."
    exit 1
fi
