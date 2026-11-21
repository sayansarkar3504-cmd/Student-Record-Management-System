# Build script for Student Record Management System
# PCC-CS503 Object Oriented Programming | Academy of Technology

$base = $PSScriptRoot
Set-Location $base

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Building Student Record Management System (PCC-CS503)" -ForegroundColor Cyan
Write-Host "  Academy of Technology | Department of CSE" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

$jdkBin = "C:\Program Files\Java\jdk-21\bin"
$javac = if (Test-Path "$jdkBin\javac.exe") { "$jdkBin\javac.exe" } else { "javac" }
$jar = if (Test-Path "$jdkBin\jar.exe") { "$jdkBin\jar.exe" } else { "jar" }

$classesDir = Join-Path $base "target\classes"
$stagingDir = Join-Path $base "target\staging"
$outJar = Join-Path $base "target\student-record-management-1.0.0.jar"

New-Item -ItemType Directory -Path $classesDir -Force | Out-Null
if (Test-Path $stagingDir) { Remove-Item -Path $stagingDir -Recurse -Force }
New-Item -ItemType Directory -Path $stagingDir -Force | Out-Null

$m2 = "$HOME\.m2\repository"
$cp = "$m2\com\formdev\flatlaf\3.4\flatlaf-3.4.jar;$m2\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar"

Write-Host "[1/3] Compiling Java source files..." -ForegroundColor Yellow
$sources = Get-ChildItem -Path "$base\src\main\java" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
& $javac -cp $cp -d $classesDir $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit 1
}

Write-Host "[2/3] Assembling Fat Executable JAR..." -ForegroundColor Yellow
Copy-Item -Path "$classesDir\*" -Destination $stagingDir -Recurse -Force
Push-Location $stagingDir
& $jar -xf "$m2\com\formdev\flatlaf\3.4\flatlaf-3.4.jar"
& $jar -xf "$m2\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar"

Remove-Item -Path "$stagingDir\META-INF\*.SF", "$stagingDir\META-INF\*.DSA", "$stagingDir\META-INF\*.RSA" -ErrorAction SilentlyContinue

$manifest = "Manifest-Version: 1.0`nMain-Class: com.aot.studentmanagement.Main`nCreated-By: Academy of Technology (PCC-CS503)`n"
Set-Content -Path "$stagingDir\manifest.txt" -Value $manifest -Encoding Ascii

& $jar -cfm $outJar "$stagingDir\manifest.txt" -C $stagingDir .
Pop-Location

Write-Host "[3/3] ✓ Build successful!" -ForegroundColor Green
Write-Host "  Output JAR: $outJar" -ForegroundColor Green
$mb = [math]::Round((Get-Item $outJar).Length / 1MB, 2)
Write-Host "  File Size:  $mb MB" -ForegroundColor Green
