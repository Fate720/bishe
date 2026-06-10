@echo off
chcp 65001 >nul
title Library System - Start

echo.
echo ========================================
echo     Library System - Start
echo ========================================
echo.

cd /d "%~dp0"

echo Stopping any existing services...
echo.

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080.*LISTENING"') do (
    echo Killing backend process PID %%a ...
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":3000.*LISTENING"') do (
    echo Killing frontend process PID %%a ...
    taskkill /F /PID %%a >nul 2>&1
)

taskkill /F /IM node.exe >nul 2>&1
taskkill /F /IM java.exe >nul 2>&1

timeout /t 2 /nobreak >nul

echo.
echo [1/2] Starting backend...
cd "%~dp0library_server"
start "Lib Backend" cmd /k "title Lib Backend && java -jar target\Library_server-0.0.1-SNAPSHOT.jar"
cd ..\..
timeout /t 5 /nobreak >nul

echo [2/2] Starting frontend...
cd "%~dp0library-frontend"
start "Lib Frontend" cmd /k "title Lib Frontend && npm run dev"

echo.
echo ========================================
echo   Backend:   http://localhost:8080
echo   Frontend:  http://localhost:3000
echo   Swagger:   http://localhost:8080/doc.html
echo ========================================
echo.
echo Startup complete!
echo To stop: double-click stop.bat
echo.
pause >nul