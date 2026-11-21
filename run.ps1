
# PowerShell Runner for Student Record Management System
# PCC-CS503 | Academy of Technology

$ErrorActionPreference = "Stop"

$base = $PSScriptRoot
Set-Location $base

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Student Record Management System" -ForegroundColor Cyan
Write-Host "  Academy of Technology | Department of CSE" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# Step 1: Detect Java
$java = Get-Command java.exe -ErrorAction SilentlyContinue

if (-not $java) {
    $javaPath = "C:\Program Files\Java\jdk-21\bin\java.exe"

    if (Test-Path $javaPath) {
        $java = $javaPath
    }
    else {
        Write-Host "[ERROR] Java JDK not found!" -ForegroundColor Red
        Write-Host "Please install Java JDK 21 and configure PATH."
        Read-Host "Press Enter to exit"
        exit 1
    }
}
else {
    $java = $java.Source
}

Write-Host "[OK] Java detected: $java" -ForegroundColor Green

# Step 2: Check JAR
$jarPath = Join-Path $base "target\student-record-management-1.0.0.jar"

if (-not (Test-Path $jarPath)) {
    Write-Host "[INFO] JAR not found. Building project..." -ForegroundColor Yellow

    $buildScript = Join-Path $base "build.ps1"

    if (Test-Path $buildScript) {
        & $buildScript

        if ($LASTEXITCODE -ne 0 -and $LASTEXITCODE -ne $null) {
            Write-Host "[ERROR] Build failed!" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }
    }
    else {
        Write-Host "[ERROR] build.ps1 not found!" -ForegroundColor Red
        exit 1
    }
}

# Step 3: Verify JAR
if (-not (Test-Path $jarPath)) {
    Write-Host "[ERROR] Application JAR was not generated!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "[OK] Application JAR found." -ForegroundColor Green

# Step 4: Launch Application
Write-Host "[INFO] Starting application..." -ForegroundColor Cyan

& $java -jar $jarPath

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Application exited with an error." -ForegroundColor Red
    Read-Host "Press Enter to exit"
}
else {
    Write-Host "[OK] Application closed successfully." -ForegroundColor Green
}
