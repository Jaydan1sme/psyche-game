@echo off
chcp 65001 > nul

rem 进入脚本所在目录
cd /d %~dp0
cd ..

set LOG_FILE=logs\backend.log

if not exist "logs" mkdir logs

echo 正在启动 Psyche Game 本地服务...
echo.

rem 检查Java环境
echo 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未安装Java环境或Java未添加到系统路径
    echo 请安装JDK 17或更高版本并添加到系统PATH
    pause > nul
    exit /b 1
)

echo Java环境检查通过

rem 检查后端服务是否已在运行
echo 检查后端服务是否已在运行...
for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo 后端服务已在运行，PID: %%i
    set BACKEND_PID=%%i
)

if defined BACKEND_PID (
    echo 如需重启，请先终止现有服务
    echo 终止现有服务？(Y/N)
    set /p choice= 
    if /i "%choice%"=="Y" (
        taskkill /PID %BACKEND_PID% /F
        echo 现有服务已终止
    ) else (
        echo 服务启动已取消
        pause > nul
        exit /b 0
    )
)

rem 启动后端服务
echo 启动后端服务...
echo 日志文件：%LOG_FILE%
start "Backend" java -jar backend\psyche-game-backend-1.0.0.jar --spring.profiles.active=local > %LOG_FILE% 2>&1

rem 等待后端服务启动
echo 等待后端服务启动...
ping 127.0.0.1 -n 10 > nul

rem 检查后端服务是否启动成功
echo 检查后端服务启动状态...
for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    set BACKEND_PID=%%i
)

if defined BACKEND_PID (
    echo 后端服务启动成功，PID: %BACKEND_PID%
    echo.
    echo 服务已成功启动！
    echo 后端API地址：http://localhost:8080
    echo Swagger文档：http://localhost:8080/doc.html
    echo H2数据库控制台：http://localhost:8080/h2-console
    echo.
    echo 前端访问方式：
    echo 1. 开发模式：http://localhost:5173 （需手动启动前端服务）
    echo 2. 生产模式：http://localhost （需启动Nginx）
    echo.
    echo 如需启动前端开发服务，请执行：
    echo cd frontend && npm run dev
    echo.
) else (
    echo 后端服务启动失败！
    echo 请查看日志文件：%LOG_FILE%
    echo 按任意键查看日志...
    pause > nul
    type %LOG_FILE%
)

echo 按任意键关闭窗口...
pause > nul