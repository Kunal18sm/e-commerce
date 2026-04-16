@REM ----------------------------------------------------------------------------
@REM ShopZone - Maven Wrapper
@REM Uses the local Maven installation in tools/ directory
@REM ----------------------------------------------------------------------------
@echo off
@setlocal

set "MVN_HOME=%~dp0tools\apache-maven-3.9.6"
set "PATH=%MVN_HOME%\bin;%PATH%"

if not exist "%MVN_HOME%\bin\mvn.cmd" (
    echo ERROR: Maven not found at %MVN_HOME%
    echo Please run: powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip'; Expand-Archive -Path maven.zip -DestinationPath tools -Force"
    exit /b 1
)

call "%MVN_HOME%\bin\mvn.cmd" %*
