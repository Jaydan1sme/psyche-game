#!/bin/bash

# 进入脚本所在目录
cd "$(dirname "$0")/.."

echo "正在启动 Psyche Game 本地服务..."

# 启动Redis
echo "启动Redis服务..."
./env/redis/redis-server ./env/redis/redis.conf > ./logs/redis.log 2>&1 &
sleep 2

# 启动Nginx
echo "启动Nginx服务..."
./env/nginx/nginx -c ./env/nginx/conf/nginx.conf > ./logs/nginx.log 2>&1 &
sleep 2

# 启动后端服务
echo "启动后端服务..."
./env/jdk/bin/java -jar ./backend/psyche-game-backend-1.0.0.jar --spring.profiles.active=local > ./logs/backend.log 2>&1 &
sleep 5

echo "所有服务启动完成！"
echo "前端访问地址：http://localhost"
echo "后端API地址：http://localhost:8080"
echo "Swagger文档：http://localhost:8080/doc.html"
echo "H2数据库控制台：http://localhost:8080/h2-console"