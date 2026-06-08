@echo off
chcp 65001 >nul
title 图书馆管理系统 - 一键启动

echo.
echo ========================================
echo     图书馆管理系统 一键启动
echo ========================================
echo.

cd /d "%~dp0"

echo [1/2] 启动后端服务 (Spring Boot)...
start "图书馆-后端" /D "%~dp0library_server" cmd /k "mvnw.cmd spring-boot:run -s .mvn/settings.xml"
echo        后端正在启动，预计 30 秒...

timeout /t 5 /nobreak >nul

echo [2/2] 启动前端服务 (Vite)...
start "图书馆-前端" /D "%~dp0library-frontend" cmd /k "npm run dev"

echo.
echo ========================================
echo   后端: http://localhost:8080
echo   前端: http://localhost:3000
echo   文档: http://localhost:8080/doc.html
echo ========================================
echo.
echo 启动完成！请打开浏览器访问 http://localhost:3000
echo 按任意键关闭此窗口...
pause >nul
