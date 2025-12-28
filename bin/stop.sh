#!/bin/bash

# 进入脚本所在目录
cd "$(dirname "$0")/.."

echo "正在停止 Psyche Game 本地服务..."

# 停止后端服务
echo "停止后端服务..."
pkill -f "psyche-game-backend"

# 停止Nginx服务
echo "停止Nginx服务..."
pkill -f nginx

# 停止Redis服务
echo "停止Redis服务..."
pkill -f redis-server

echo "所有服务已停止！"