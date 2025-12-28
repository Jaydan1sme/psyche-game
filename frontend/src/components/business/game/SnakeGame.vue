<template>
  <div class="snake-game-container">
    <div class="game-header">
      <div class="game-stats">
        <div class="stat-item">
          <span class="stat-label">分数:</span>
          <span class="stat-value">{{ score }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">长度:</span>
          <span class="stat-value">{{ snake.length }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">时间:</span>
          <span class="stat-value">{{ gameTime }}</span>
        </div>
      </div>
      <div class="game-controls">
        <el-button type="primary" @click="resetGame">重新开始</el-button>
        <el-button @click="togglePause">
          {{ isPaused ? '继续' : '暂停' }}
        </el-button>
      </div>
    </div>
    <div class="game-board-container">
      <canvas 
        ref="gameCanvas" 
        :width="canvasWidth" 
        :height="canvasHeight" 
        class="game-canvas"
      ></canvas>
    </div>
    <div class="game-instructions">
      <p>使用方向键控制蛇的移动，吃到食物使蛇变长，避免撞到墙壁或自己的身体</p>
    </div>
    <div v-if="gameOver" class="game-over-overlay">
      <div class="game-over-content">
        <h2>{{ isWin ? '恭喜你获胜了！' : '游戏结束！' }}</h2>
        <p>最终分数: {{ score }}</p>
        <el-button type="primary" @click="resetGame">再来一局</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

// 游戏配置
const gridSize = 20
const canvasWidth = 600
const canvasHeight = 400
const gridWidth = canvasWidth / gridSize
const gridHeight = canvasHeight / gridSize
const initialSpeed = 150

// 游戏状态
const gameCanvas = ref(null)
const ctx = ref(null)
const snake = ref([{ x: 10, y: 10 }])
const direction = ref({ x: 1, y: 0 })
const food = ref({})
const score = ref(0)
const gameTime = ref(0)
const isGameOver = ref(false)
const isPaused = ref(false)
const gameOver = ref(false)
const isWin = ref(false)
const speed = ref(initialSpeed)
let gameLoop = null
let timer = null

// 初始化游戏
const initGame = () => {
  // 重置游戏状态
  snake.value = [{ x: 10, y: 10 }]
  direction.value = { x: 1, y: 0 }
  score.value = 0
  gameTime.value = 0
  isGameOver.value = false
  isPaused.value = false
  gameOver.value = false
  isWin.value = false
  speed.value = initialSpeed
  
  // 生成初始食物
  generateFood()
  
  // 清除之前的游戏循环
  if (gameLoop) {
    clearInterval(gameLoop)
    gameLoop = null
  }
  
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  
  // 开始计时
  timer = setInterval(() => {
    gameTime.value++
  }, 1000)
  
  // 开始游戏循环
  gameLoop = setInterval(updateGame, speed.value)
}

// 生成食物
const generateFood = () => {
  const newFood = {
    x: Math.floor(Math.random() * gridWidth),
    y: Math.floor(Math.random() * gridHeight)
  }
  
  // 确保食物不在蛇身上
  const isOnSnake = snake.value.some(segment => segment.x === newFood.x && segment.y === newFood.y)
  if (isOnSnake) {
    generateFood()
    return
  }
  
  food.value = newFood
}

// 更新游戏
const updateGame = () => {
  if (isGameOver.value || isPaused.value) return
  
  // 计算新的蛇头位置
  const head = { ...snake.value[0] }
  head.x += direction.value.x
  head.y += direction.value.y
  
  // 检查碰撞
  if (checkCollision(head)) {
    gameOver.value = true
    isGameOver.value = true
    isWin.value = false
    clearInterval(gameLoop)
    gameLoop = null
    return
  }
  
  // 将新头添加到蛇的前面
  snake.value.unshift(head)
  
  // 检查是否吃到食物
  if (head.x === food.value.x && head.y === food.value.y) {
    // 增加分数
    score.value += 10
    
    // 增加速度
    speed.value = Math.max(50, speed.value - 5)
    
    // 重新生成食物
    generateFood()
    
    // 检查是否获胜
    if (snake.value.length === gridWidth * gridHeight) {
      gameOver.value = true
      isGameOver.value = true
      isWin.value = true
      clearInterval(gameLoop)
      gameLoop = null
      return
    }
    
    // 重新设置游戏循环，使用新的速度
    clearInterval(gameLoop)
    gameLoop = setInterval(updateGame, speed.value)
  } else {
    // 移除蛇尾
    snake.value.pop()
  }
  
  // 绘制游戏
  drawGame()
}

// 检查碰撞
const checkCollision = (head) => {
  // 检查墙壁碰撞
  if (head.x < 0 || head.x >= gridWidth || head.y < 0 || head.y >= gridHeight) {
    return true
  }
  
  // 检查自身碰撞
  for (let i = 1; i < snake.value.length; i++) {
    if (head.x === snake.value[i].x && head.y === snake.value[i].y) {
      return true
    }
  }
  
  return false
}

// 绘制游戏
const drawGame = () => {
  if (!ctx.value) return
  
  // 清空画布
  ctx.value.fillStyle = '#FFFFFF'
  ctx.value.fillRect(0, 0, canvasWidth, canvasHeight)
  
  // 绘制网格
  ctx.value.strokeStyle = '#E0E0E0'
  ctx.value.lineWidth = 1
  for (let x = 0; x <= canvasWidth; x += gridSize) {
    ctx.value.beginPath()
    ctx.value.moveTo(x, 0)
    ctx.value.lineTo(x, canvasHeight)
    ctx.value.stroke()
  }
  for (let y = 0; y <= canvasHeight; y += gridSize) {
    ctx.value.beginPath()
    ctx.value.moveTo(0, y)
    ctx.value.lineTo(canvasWidth, y)
    ctx.value.stroke()
  }
  
  // 绘制食物
  ctx.value.fillStyle = '#F44336'
  ctx.value.fillRect(
    food.value.x * gridSize + 2,
    food.value.y * gridSize + 2,
    gridSize - 4,
    gridSize - 4
  )
  
  // 绘制蛇
  snake.value.forEach((segment, index) => {
    // 为蛇身添加渐变效果
    const gradient = ctx.value.createLinearGradient(
      segment.x * gridSize,
      segment.y * gridSize,
      (segment.x + 1) * gridSize,
      (segment.y + 1) * gridSize
    )
    
    if (index === 0) {
      // 蛇头
      gradient.addColorStop(0, '#7A86E8')
      gradient.addColorStop(1, '#82E2FF')
    } else {
      // 蛇身，根据位置调整颜色
      const ratio = index / snake.value.length
      gradient.addColorStop(0, `rgba(122, 134, 232, ${1 - ratio * 0.5})`)
      gradient.addColorStop(1, `rgba(130, 226, 255, ${1 - ratio * 0.5})`)
    }
    
    ctx.value.fillStyle = gradient
    ctx.value.fillRect(
      segment.x * gridSize + 1,
      segment.y * gridSize + 1,
      gridSize - 2,
      gridSize - 2
    )
    
    // 添加蛇身发光效果
    ctx.value.shadowColor = '#7A86E8'
    ctx.value.shadowBlur = 10
    ctx.value.strokeStyle = 'rgba(122, 134, 232, 0.5)'
    ctx.value.lineWidth = 2
    ctx.value.strokeRect(
      segment.x * gridSize + 1,
      segment.y * gridSize + 1,
      gridSize - 2,
      gridSize - 2
    )
    ctx.value.shadowBlur = 0
  })
}

// 处理键盘输入
const handleKeyPress = (event) => {
  switch (event.key) {
    case 'ArrowUp':
      if (direction.value.y === 0) {
        direction.value = { x: 0, y: -1 }
      }
      break
    case 'ArrowDown':
      if (direction.value.y === 0) {
        direction.value = { x: 0, y: 1 }
      }
      break
    case 'ArrowLeft':
      if (direction.value.x === 0) {
        direction.value = { x: -1, y: 0 }
      }
      break
    case 'ArrowRight':
      if (direction.value.x === 0) {
        direction.value = { x: 1, y: 0 }
      }
      break
    case ' ':
      togglePause()
      break
  }
}

// 切换暂停状态
const togglePause = () => {
  if (isGameOver.value) return
  isPaused.value = !isPaused.value
}

// 重置游戏
const resetGame = () => {
  initGame()
  drawGame()
}

// 生命周期钩子
onMounted(() => {
  // 获取画布上下文
  ctx.value = gameCanvas.value.getContext('2d')
  
  // 初始化游戏
  initGame()
  
  // 绘制初始游戏
  drawGame()
  
  // 添加键盘事件监听
  window.addEventListener('keydown', handleKeyPress)
})

onUnmounted(() => {
  // 清除游戏循环
  if (gameLoop) {
    clearInterval(gameLoop)
    gameLoop = null
  }
  
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  
  // 移除键盘事件监听
  window.removeEventListener('keydown', handleKeyPress)
})
</script>

<style scoped>
.snake-game-container {
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
  padding: 20px;
  background: var(--primary-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: var(--shadow-medium);
  color: var(--text-primary);
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-secondary);
  padding: 16px;
  border-radius: var(--border-radius);
}

.game-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: 14px;
  color: var(--text-tertiary);
  margin-bottom: 4px;
  font-weight: 400;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.game-controls {
  display: flex;
  gap: 10px;
}

.game-board-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  background: var(--bg-secondary);
  border-radius: 8px;
  padding: 10px;
  border: 1px solid var(--border-color);
}

.game-canvas {
  border: 2px solid var(--border-color);
  border-radius: 4px;
  box-shadow: var(--shadow-light);
  background: var(--primary-bg);
}

.game-instructions {
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 20px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
}

.game-over-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.game-over-content {
  background: rgba(255, 255, 255, 0.95);
  padding: 40px;
  border-radius: 16px;
  text-align: center;
  box-shadow: var(--shadow-medium);
  border: 1px solid var(--border-color);
  animation: slideUp 0.5s ease;
  color: var(--text-primary);
}

@keyframes slideUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.game-over-content h2 {
  font-size: 32px;
  margin-bottom: 20px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.game-over-content p {
  font-size: 18px;
  margin-bottom: 30px;
  color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .game-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .game-stats {
    justify-content: center;
  }
  
  .game-canvas {
    max-width: 100%;
    height: auto;
  }
}
</style>