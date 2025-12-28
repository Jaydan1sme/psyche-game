@echo off
setlocal enabledelayedexpansion

REM Change to script directory
cd /d %~dp0
cd ..

set LOG_FILE=logs\backend.log

if not exist "logs" mkdir logs

echo Starting Psyche Game Local Service...
echo.

REM Check Java environment
echo Checking Java environment...
java -version >nul 2>&1
if !errorlevel! neq 0 (
    echo ERROR: Java not found in PATH
    echo Please install JDK 17+ and add to system PATH
    pause
    exit /b 1
)

echo Java environment check passed

REM Check if backend service is already running
echo Checking if backend service is running...
set "BACKEND_PID="
for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    set "BACKEND_PID=%%i"
)

if defined BACKEND_PID (
    echo Backend service is already running, PID: !BACKEND_PID!
    echo Restart? (Y/N)
    set /p "choice="
    if /i "!choice!"=="Y" (
        taskkill /PID !BACKEND_PID! /F >nul 2>&1
        echo Existing service terminated
    ) else (
        echo Service start cancelled
        pause
        exit /b 0
    )
)

REM Start backend service
echo Starting backend service...
echo Log file: %LOG_FILE%
start "Backend" java -jar backend\psyche-game-backend-1.0.0.jar --spring.profiles.active=local > %LOG_FILE% 2>&1

REM Wait for backend to start
echo Waiting for backend to start...
timeout /t 10 /nobreak >nul

REM Check if backend started successfully
echo Checking backend status...
set "BACKEND_PID="
for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    set "BACKEND_PID=%%i"
)

if defined BACKEND_PID (
    echo Backend service started successfully, PID: !BACKEND_PID!
    echo.
    echo Service started successfully!
    echo Backend API: http://localhost:8080
    echo Swagger Docs: http://localhost:8080/doc.html
    echo H2 Console: http://localhost:8080/h2-console
    echo.
    echo Frontend access methods:
    echo 1. Dev mode: http://localhost:5173 (Need to start frontend manually)
    echo 2. Prod mode: http://localhost (Need to start Nginx)
    echo.
    echo To start frontend dev server, run:
    echo cd frontend && npm run dev
    echo.
) else (
    echo Backend service failed to start!
    echo Please check log file: %LOG_FILE%
    echo Press any key to view log...
    pause >nul
    type %LOG_FILE%
)

echo Press any key to exit...
pause