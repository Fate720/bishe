@echo off
chcp 65001 >nul
title 图书馆管理系统 - 停止服务

echo.
echo ========================================
echo     停止图书馆管理系统所有服务
echo ========================================
echo.

echo 正在停止 Spring Boot 后端 (Java)...
taskkill /F /IM java.exe 2>nul && echo   后端已停止 || echo   后端未在运行

echo 正在停止前端服务 (Node.js)...
taskkill /F /IM node.exe 2>nul && echo   前端已停止 || echo   前端未在运行

echo.
echo 所有服务已停止！
echo 按任意键关闭此窗口...
pause >nul
