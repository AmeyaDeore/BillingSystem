@echo off
setlocal enabledelayedexpansion
title Electricity Billing System - Build & Run
color 0B

echo ================================================
echo   Electricity Billing System
echo   Build & Run (single launcher)
echo ================================================
echo.

REM MySQL JDBC driver (adjust only if you rename the jar)
set "DRIVER_JAR=libs\mysql-connector-j-9.5.0.jar"

REM 1) Clean previous build to avoid running old classes
if exist bin (
  echo Cleaning previous build...
  rmdir /S /Q bin >nul 2>&1
)
mkdir bin >nul 2>&1

REM 2) Collect sources and compile
echo Compiling sources...
dir /B /S src\*.java > sources.txt
javac -d bin -cp "%DRIVER_JAR%" @sources.txt
set "RC=%ERRORLEVEL%"
del /Q sources.txt >nul 2>&1

if not "%RC%"=="0" (
  echo.
  echo Compilation failed. Please review errors above.
  pause
  exit /b %RC%
)

echo.
echo Compilation successful.
echo Launching application...
echo.

REM 3) Run latest compiled classes
java -cp "bin;%DRIVER_JAR%" com.billing.main.Main

if %ERRORLEVEL% NEQ 0 (
  echo.
  echo Application closed or an error occurred.
)

endlocal
