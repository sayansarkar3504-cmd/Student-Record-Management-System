@echo off
setlocal
title Student Record Management System - Academy of Technology

echo ============================================================
echo   Student Record Management System (PCC-CS503)
echo   Academy of Technology - Department of CSE
echo   Faculty Evaluator: Dr. Krishna Bhowal
echo ============================================================
echo.

set "PROJECT_DIR=%~dp0"
cd /d "%PROJECT_DIR%"

set "JAVA_CMD="
if exist "C:\Program Files\Java\jdk-21\bin\javaw.exe" (
    set "JAVA_CMD=C:\Program Files\Java\jdk-21\bin\javaw.exe"
) else (
    where javaw >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        set "JAVA_CMD=javaw"
    ) else (
        where java >nul 2>&1
        if %ERRORLEVEL% equ 0 set "JAVA_CMD=java"
    )
)

if "%JAVA_CMD%"=="" (
    echo [ERROR] Java Runtime not detected!
    echo Please ensure Java 21 or Java 17 is installed.
    pause
    exit /b 1
)

set "JAR_PATH=%PROJECT_DIR%target\student-record-management-1.0.0.jar"

if not exist "%JAR_PATH%" (
    echo [INFO] Executable JAR not found. Building project now...
    call "%PROJECT_DIR%build.bat"
    if %ERRORLEVEL% neq 0 (
        echo [ERROR] Build failed. Cannot launch application.
        pause
        exit /b 1
    )
)

echo Starting Student Record Management System...
start "" "%JAVA_CMD%" -jar "%JAR_PATH%"
echo Application launched successfully.
