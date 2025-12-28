@echo off
chcp 65001 > nul

echo 正在停止 Psyche Game 本地服务...
echo.

rem 停止后端服务
echo 停止后端服务...
for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /PID %%i /F > nul 2>&1
    echo 后端服务已停止，PID: %%i
)

rem 停止Redis服务（如果正在运行）
echo 停止Redis服务...
taskkill /f /im redis-server.exe > nul 2>&1
if %errorlevel% equ 0 (
    echo Redis服务已停止
) else (
    echo Redis服务未运行或已停止
)

rem 停止Nginx服务（如果正在运行）
echo 停止Nginx服务...
taskkill /f /im nginx.exe > nul 2>&1
if %errorlevel% equ 0 (
    echo Nginx服务已停止
) else (
    echo Nginx服务未运行或已停止
)

echo.
echo 所有服务停止操作已完成！
echo 按任意键关闭窗口...
pause > nul