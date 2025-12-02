@echo off
echo ============================================
echo   Pharmacy Management System
echo   JavaFX Application Launcher
echo ============================================
echo.

REM Set JavaFX path - UPDATE THIS PATH!
set JAVAFX_PATH=C:\path\to\javafx-sdk-21\lib

REM Set project paths
set PROJECT_DIR=%~dp0
set SRC_DIR=%PROJECT_DIR%src
set BIN_DIR=%PROJECT_DIR%bin
set LIB_DIR=%PROJECT_DIR%lib

echo Checking JavaFX path...
if not exist "%JAVAFX_PATH%" (
    echo ERROR: JavaFX SDK not found at: %JAVAFX_PATH%
    echo.
    echo Please update JAVAFX_PATH in this script to point to your JavaFX SDK lib folder
    echo Example: C:\javafx-sdk-21\lib
    echo.
    pause
    exit /b 1
)

echo.
echo Starting Pharmacy Management System...
echo.

REM Run the application
java --module-path "%JAVAFX_PATH%" ^
     --add-modules javafx.controls,javafx.fxml ^
     -cp "%BIN_DIR%;%LIB_DIR%\*" ^
     gui.PharmacyApp

if errorlevel 1 (
    echo.
    echo ============================================
    echo   Application failed to start!
    echo ============================================
    echo.
    echo Possible reasons:
    echo 1. JavaFX path is incorrect
    echo 2. Project not compiled yet
    echo 3. Database connection failed
    echo.
    echo Please check:
    echo - JavaFX SDK path in this script
    echo - Project is compiled (run from NetBeans first)
    echo - MySQL server is running
    echo - Database is created
    echo.
    pause
) else (
    echo.
    echo Application closed normally.
    echo.
)

pause
