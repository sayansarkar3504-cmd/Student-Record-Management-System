@REM Maven Wrapper startup batch script
@REM -------------------------------------------------------------------
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir

@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_CMD_LINE_ARGS=%*

if exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
    "%JAVA_HOME%\bin\java.exe" ^
        -cp "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" ^
        org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%
) else (
    echo Maven wrapper JAR not found. Downloading...
    powershell -Command "& { Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar' }"
    if exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
        "%JAVA_HOME%\bin\java.exe" ^
            -cp "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" ^
            org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%
    ) else (
        echo Failed to download Maven wrapper. Please install Maven manually.
        echo Download from: https://maven.apache.org/download.cgi
        exit /b 1
    )
)

