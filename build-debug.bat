@echo off
REM RTL Doc Reader - Quick Build Script for Windows
REM This script builds the debug APK for mobile deployment

setlocal enabledelayedexpansion

set PROJECT_ROOT=%~dp0
cd /d "%PROJECT_ROOT%"

echo.
echo ============================================================
echo      RTL Doc Reader - Build Script (Windows)
echo      Building Debug APK for Mobile
echo ============================================================
echo.

REM Check if gradlew.bat exists
if not exist "%PROJECT_ROOT%gradlew.bat" (
    echo.
    echo Error: gradlew.bat not found. Please run from project root.
    exit /b 1
)

echo Cleaning previous build artifacts...
call "%PROJECT_ROOT%gradlew.bat" clean -q
echo OK
echo.

echo Building debug APK (this may take 2-5 minutes)...
call "%PROJECT_ROOT%gradlew.bat" assembleDebug

REM Check if build succeeded
if exist "%PROJECT_ROOT%app\build\outputs\apk\debug\app-debug.apk" (
    set APK_PATH=%PROJECT_ROOT%app\build\outputs\apk\debug\app-debug.apk
    
    echo.
    echo ============================================================
    echo           BUILD SUCCESSFUL!
    echo ============================================================
    echo.
    echo APK Ready:
    echo    Location: %APK_PATH%
    echo.
    echo Next Steps:
    echo.
    echo   Option 1: Install on USB-connected device
    echo   ^> adb install "%APK_PATH%"
    echo.
    echo   Option 2: Install and run
    echo   ^> gradlew installDebug
    echo.
    echo   Option 3: Share APK file
    echo   ^> Copy APK to USB or file sharing
    echo.
) else (
    echo.
    echo Build failed. Check error messages above.
    exit /b 1
)
