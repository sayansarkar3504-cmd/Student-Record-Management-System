@echo off
setlocal enabledelayedexpansion
title Student Record Management System - Build (PCC-CS503)

echo ============================================================
echo   Student Record Management System - Build Script
echo   Academy of Technology - Department of CSE (PCC-CS503)
echo ============================================================
echo.

set "PROJECT_DIR=%~dp0"
cd /d "%PROJECT_DIR%"

:: Check Java Compiler
set "JAVAC_CMD="
if exist "C:\Program Files\Java\jdk-21\bin\javac.exe" (
    set "JAVAC_CMD=C:\Program Files\Java\jdk-21\bin\javac.exe"
    set "JAR_CMD=C:\Program Files\Java\jdk-21\bin\jar.exe"
) else (
    where javac >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        set "JAVAC_CMD=javac"
        set "JAR_CMD=jar"
    )
)

if "%JAVAC_CMD%"=="" (
    echo [ERROR] JDK (Java Development Kit) not found!
    echo Please install JDK 21 or set JAVA_HOME.
    pause
    exit /b 1
)

echo [1/3] Compiling Java source files...
if not exist "target\classes" mkdir "target\classes"
if not exist "target\staging" mkdir "target\staging"

set "CP=%USERPROFILE%\.m2\repository\com\formdev\flatlaf\3.4\flatlaf-3.4.jar;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar"

powershell -NoProfile -Command "$srcs = Get-ChildItem -Path 'src\main\java' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }; & '%JAVAC_CMD%' -cp '%CP%' -d 'target\classes' $srcs"
if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [2/3] Assembling Standalone Executable Fat JAR...
if exist "target\staging" rmdir /s /q "target\staging"
mkdir "target\staging"

xcopy /s /e /q /y "target\classes\*" "target\staging\" >nul
cd /d "target\staging"
"%JAR_CMD%" -xf "%USERPROFILE%\.m2\repository\com\formdev\flatlaf\3.4\flatlaf-3.4.jar"
"%JAR_CMD%" -xf "%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar"

if exist "META-INF\*.SF" del /q "META-INF\*.SF"
if exist "META-INF\*.DSA" del /q "META-INF\*.DSA"
if exist "META-INF\*.RSA" del /q "META-INF\*.RSA"

(
echo Manifest-Version: 1.0
echo Main-Class: com.aot.studentmanagement.Main
echo Created-By: Academy of Technology (PCC-CS503)
) > manifest.txt

"%JAR_CMD%" -cfm "..\student-record-management-1.0.0.jar" manifest.txt . >nul
cd /d "%PROJECT_DIR%"

echo [3/3] Build completed successfully!
echo Executable JAR generated at: target\student-record-management-1.0.0.jar
echo.
exit /b 0
