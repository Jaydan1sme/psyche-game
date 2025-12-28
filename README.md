# Psyche Game

一个基于Vue 3 + Spring Boot的游戏网站，包含多种经典游戏和现代化的UI设计。

## 功能特性

### 游戏模块
- **扫雷游戏**：经典的扫雷游戏，支持不同难度级别
- **贪吃蛇游戏**：经典的贪吃蛇游戏，支持键盘控制
- **2048游戏**：热门的2048数字游戏，支持触摸和键盘操作
- **打飞机游戏**：经典的射击游戏，支持动态难度调整

### 用户系统
- 用户注册和登录
- 个人中心
- 战绩记录

### 论坛系统
- 帖子列表
- 帖子详情

## 技术栈

### 前端
- **框架**：Vue 3
- **UI组件库**：Element Plus
- **路由**：Vue Router
- **状态管理**：Pinia
- **构建工具**：Vite
- **HTTP客户端**：Axios

### 后端
- **框架**：Spring Boot
- **ORM**：MyBatis
- **数据库**：MySQL
- **缓存**：Redis
- **认证授权**：JWT

## 项目结构

```
psyche-game/
├── backend/           # 后端代码
│   ├── src/           # Java源代码
│   └── pom.xml        # Maven配置
├── frontend/          # 前端代码
│   ├── src/           # Vue源代码
│   ├── package.json   # npm配置
│   └── vite.config.js # Vite配置
├── bin/               # 启动脚本
├── conf/              # 配置文件
└── env/               # 环境配置
```

## 快速开始

### 前端

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 后端

```bash
# 进入后端目录
cd backend

# 构建项目
mvn clean package

# 启动应用
java -jar target/psyche-game-backend-1.0.0.jar --spring.profiles.active=local
```

## 部署说明

### 生产环境部署

1. **构建前端**：
   ```bash
   cd frontend
   npm run build
   ```

2. **构建后端**：
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

3. **部署应用**：
   - 复制前端构建产物到Web服务器
   - 运行后端JAR文件

## 配置说明

### 前端配置
- **API地址**：通过`src/store/mode.js`配置API基础地址
- **主题配置**：通过`src/style.css`配置CSS变量

### 后端配置
- **数据库配置**：`src/main/resources/application.yml`
- **JWT配置**：`src/main/resources/application.yml`

## 开发指南

### 代码风格
- 前端：遵循ESLint和Prettier规范
- 后端：遵循Java Code Conventions

### 提交规范
- 提交信息使用中文描述
- 遵循Conventional Commits规范

## 许可证

MIT License

## 联系方式

- 项目地址：https://github.com/Jaydan1sme/psyche-game
- 作者：Psyche Game Team