<template>
  <div class="airplane-game-container" ref="gameContainer">
    <!-- 游戏画布 -->
    <canvas ref="gameCanvas" class="game-canvas"></canvas>
    
    <!-- 游戏信息面板 -->
  <div class="game-info">
    <div class="difficulty-display">
      <span class="label">难度:</span>
      <span class="value difficulty-value">等级 {{ difficultyLevel }}</span>
    </div>
    <div class="score-display">
      <span class="label">分数:</span>
      <span class="value">{{ score }}</span>
    </div>
    <div class="level-display">
      <span class="label">关卡:</span>
      <span class="value">{{ level }}</span>
    </div>
    <div class="lives-display">
      <span class="label">生命:</span>
      <span class="hearts">
        <span v-for="i in lives" :key="i" class="heart">❤</span>
      </span>
    </div>
  </div>
    
    <!-- 游戏控制按钮 -->
    <div class="game-controls">
      <el-button type="primary" @click="toggleGame" :icon="isPaused ? 'Play' : 'VideoPause'">
        {{ isPaused ? '继续' : '暂停' }}
      </el-button>
      <el-button @click="resetGame" :icon="'Refresh'">
        重新开始
      </el-button>
    </div>
    
    <!-- 游戏结束面板 -->
    <div v-if="gameOver" class="game-over-panel">
      <h2>{{ gameWin ? '游戏胜利！' : '游戏结束！' }}</h2>
      <div class="final-score">最终分数: {{ score }}</div>
      <el-button type="primary" @click="resetGame" size="large">
        再来一局
      </el-button>
    </div>
    
    <!-- 游戏开始面板 -->
    <div v-if="!gameStarted" class="game-start-panel">
      <h2>打飞机</h2>
      <p>使用键盘方向键或WASD控制飞机移动，空格键发射子弹</p>
      <el-button type="primary" @click="startGame" size="large">
        开始游戏
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const gameCanvas = ref(null)
const gameContainer = ref(null)
const ctx = ref(null)

// 游戏状态
const gameStarted = ref(false)
const gameOver = ref(false)
const gameWin = ref(false)
const isPaused = ref(false)
const score = ref(0)
const level = ref(1)
const lives = ref(3)
const difficultyLevel = ref(1) // 难度等级
let difficultyTimer = null // 难度提升定时器

// 游戏元素
const player = ref({
  x: 0,
  y: 0,
  width: 50,
  height: 40,
  speed: 4, // 降低50%速度，原8 -> 4
  bullets: []
})

const enemies = ref([])
const enemyBullets = ref([])
const explosions = ref([])
const particles = ref([])

// 游戏配置
const CANVAS_WIDTH = ref(800)
const CANVAS_HEIGHT = ref(600)
const ENEMY_SPAWN_RATE = ref(0.02)
const ENEMY_SPEED = ref(2)
const BULLET_SPEED = ref(10)
const PLAYER_BULLET_COOLDOWN = ref(200)
let lastBulletTime = 0

// 键盘控制
const keys = ref({
  ArrowUp: false,
  ArrowDown: false,
  ArrowLeft: false,
  ArrowRight: false,
  w: false,
  s: false,
  a: false,
  d: false,
  ' ': false
})

// 初始化游戏
const initGame = () => {
  if (!gameCanvas.value) return
  
  // 设置画布大小
  const containerWidth = gameContainer.value.clientWidth
  const containerHeight = gameContainer.value.clientHeight
  CANVAS_WIDTH.value = Math.min(containerWidth, 800)
  CANVAS_HEIGHT.value = Math.min(containerHeight, 600)
  
  gameCanvas.value.width = CANVAS_WIDTH.value
  gameCanvas.value.height = CANVAS_HEIGHT.value
  ctx.value = gameCanvas.value.getContext('2d')
  
  // 初始化玩家位置
  player.value.x = CANVAS_WIDTH.value / 2 - player.value.width / 2
  player.value.y = CANVAS_HEIGHT.value - player.value.height - 20
  
  // 重置游戏状态
  resetGameState()
}

// 重置游戏状态
const resetGameState = () => {
  gameOver.value = false
  gameWin.value = false
  isPaused.value = false
  score.value = 0
  level.value = 1
  lives.value = 3
  difficultyLevel.value = 1 // 重置难度等级
  player.value.bullets = []
  enemies.value = []
  enemyBullets.value = []
  explosions.value = []
  particles.value = []
  
  ENEMY_SPAWN_RATE.value = 0.02
  ENEMY_SPEED.value = 2
  lastBulletTime = 0
  
  // 清除定时器
  if (difficultyTimer) {
    clearInterval(difficultyTimer)
    difficultyTimer = null
  }
}

// 开始游戏
const startGame = () => {
  gameStarted.value = true
  gameLoop()
  
  // 启动难度提升定时器（每30秒）
  difficultyTimer = setInterval(() => {
    increaseDifficulty()
  }, 30000)
}

// 增加难度
const increaseDifficulty = () => {
  difficultyLevel.value++
  
  // 敌人数量增加通过提高生成率实现
  ENEMY_SPAWN_RATE.value += 0.015 // 增加敌人生成率
  ENEMY_SPEED.value *= 1.1 // 敌人移动速度提升10%
  
  // 屏幕震动效果
  const container = gameContainer.value
  if (container) {
    container.style.animation = 'shake 0.5s ease-in-out'
    setTimeout(() => {
      container.style.animation = ''
    }, 500)
  }
}

// 暂停/继续游戏
const toggleGame = () => {
  isPaused.value = !isPaused.value
  if (!isPaused.value) {
    gameLoop()
    // 恢复难度定时器
    if (!difficultyTimer) {
      difficultyTimer = setInterval(() => {
        increaseDifficulty()
      }, 30000)
    }
  } else {
    // 暂停时清除定时器
    if (difficultyTimer) {
      clearInterval(difficultyTimer)
      difficultyTimer = null
    }
  }
}

// 重置游戏
const resetGame = () => {
  resetGameState()
  initGame()
  gameStarted.value = false
}



// 游戏主循环
const gameLoop = () => {
  if (!ctx.value || gameOver.value || isPaused.value) return
  
  // 清除画布 - 浅色主题
  ctx.value.fillStyle = '#FFFFFF'
  ctx.value.fillRect(0, 0, CANVAS_WIDTH.value, CANVAS_HEIGHT.value)
  
  // 更新游戏元素
  update()
  
  // 绘制游戏元素
  draw()
  
  // 继续下一次循环
  requestAnimationFrame(gameLoop)
}

// 更新游戏元素
const update = () => {
  // 更新玩家位置
  updatePlayer()
  
  // 生成敌人
  spawnEnemies()
  
  // 更新敌人
  updateEnemies()
  
  // 更新敌人子弹
  updateEnemyBullets()
  
  // 更新玩家子弹
  updatePlayerBullets()
  
  // 更新爆炸效果
  updateExplosions()
  
  // 更新粒子效果
  updateParticles()
  
  // 检测碰撞
  checkCollisions()
  
  // 检查游戏胜利条件
  checkWinCondition()
}

// 更新玩家位置
const updatePlayer = () => {
  if (keys.value.ArrowUp || keys.value.w) {
    player.value.y = Math.max(0, player.value.y - player.value.speed)
  }
  if (keys.value.ArrowDown || keys.value.s) {
    player.value.y = Math.min(CANVAS_HEIGHT.value - player.value.height, player.value.y + player.value.speed)
  }
  if (keys.value.ArrowLeft || keys.value.a) {
    player.value.x = Math.max(0, player.value.x - player.value.speed)
  }
  if (keys.value.ArrowRight || keys.value.d) {
    player.value.x = Math.min(CANVAS_WIDTH.value - player.value.width, player.value.x + player.value.speed)
  }
  
  // 发射子弹
  if (keys.value[' ']) {
    const currentTime = Date.now()
    if (currentTime - lastBulletTime > PLAYER_BULLET_COOLDOWN.value) {
      fireBullet()
      lastBulletTime = currentTime
    }
  }
}

// 发射子弹
const fireBullet = () => {
  player.value.bullets.push({
    x: player.value.x + player.value.width / 2 - 2,
    y: player.value.y,
    width: 4,
    height: 12,
    speed: BULLET_SPEED.value
  })
  
  // 添加发射粒子效果
  createParticleBurst(player.value.x + player.value.width / 2, player.value.y, '#00ffff', 5)
}

// 生成敌人
const spawnEnemies = () => {
  if (Math.random() < ENEMY_SPAWN_RATE.value) {
    const x = Math.random() * (CANVAS_WIDTH.value - 40)
    enemies.value.push({
      x: x,
      y: -50,
      width: 40,
      height: 30,
      speed: ENEMY_SPEED.value + Math.random() * 1,
      health: level.value * 2,
      maxHealth: level.value * 2,
      lastShot: 0
    })
  }
}

// 更新敌人
const updateEnemies = () => {
  enemies.value = enemies.value.filter(enemy => {
    enemy.y += enemy.speed
    
    // 敌人射击
    const currentTime = Date.now()
    if (currentTime - enemy.lastShot > 1000 + Math.random() * 2000) {
      enemyBullets.value.push({
        x: enemy.x + enemy.width / 2 - 2,
        y: enemy.y + enemy.height,
        width: 4,
        height: 8,
        speed: 5
      })
      enemy.lastShot = currentTime
    }
    
    // 移除超出屏幕的敌人
    return enemy.y < CANVAS_HEIGHT.value
  })
}

// 更新敌人子弹
const updateEnemyBullets = () => {
  enemyBullets.value = enemyBullets.value.filter(bullet => {
    bullet.y += bullet.speed
    return bullet.y < CANVAS_HEIGHT.value
  })
}

// 更新玩家子弹
const updatePlayerBullets = () => {
  player.value.bullets = player.value.bullets.filter(bullet => {
    bullet.y -= bullet.speed
    return bullet.y > 0
  })
}

// 更新爆炸效果
const updateExplosions = () => {
  explosions.value = explosions.value.filter(explosion => {
    explosion.life--
    return explosion.life > 0
  })
}

// 更新粒子效果
const updateParticles = () => {
  particles.value = particles.value.filter(particle => {
    particle.x += particle.vx
    particle.y += particle.vy
    particle.vy += particle.gravity
    particle.alpha *= 0.98
    particle.size *= 0.98
    return particle.alpha > 0.1 && particle.size > 0.1
  })
}

// 检测碰撞
const checkCollisions = () => {
  // 玩家子弹与敌人碰撞
  player.value.bullets.forEach((bullet, bulletIndex) => {
    enemies.value.forEach((enemy, enemyIndex) => {
      if (checkCollision(bullet, enemy)) {
        // 敌人受伤
        enemy.health--
        
        // 添加击中粒子效果
        createParticleBurst(bullet.x + bullet.width / 2, bullet.y + bullet.height / 2, '#ff6b35', 3)
        
        // 移除子弹
        player.value.bullets.splice(bulletIndex, 1)
        
        // 敌人死亡
        if (enemy.health <= 0) {
          // 添加爆炸效果
          createExplosion(enemy.x + enemy.width / 2, enemy.y + enemy.height / 2)
          createParticleBurst(enemy.x + enemy.width / 2, enemy.y + enemy.height / 2, '#ff0000', 15)
          
          // 增加分数
          score.value += 100 * level.value
          
          // 移除敌人
          enemies.value.splice(enemyIndex, 1)
        }
      }
    })
  })
  
  // 敌人子弹与玩家碰撞
  enemyBullets.value.forEach((bullet, bulletIndex) => {
    if (checkCollision(bullet, player.value)) {
      // 玩家受伤
      lives.value--
      
      // 添加爆炸效果
      createExplosion(player.value.x + player.value.width / 2, player.value.y + player.value.height / 2)
      createParticleBurst(player.value.x + player.value.width / 2, player.value.y + player.value.height / 2, '#ffffff', 10)
      
      // 移除子弹
      enemyBullets.value.splice(bulletIndex, 1)
      
      // 游戏结束
      if (lives.value <= 0) {
        gameOver.value = true
      }
    }
  })
  
  // 敌人与玩家碰撞
  enemies.value.forEach((enemy, enemyIndex) => {
    if (checkCollision(enemy, player.value)) {
      // 添加爆炸效果
      createExplosion(player.value.x + player.value.width / 2, player.value.y + player.value.height / 2)
      createParticleBurst(player.value.x + player.value.width / 2, player.value.y + player.value.height / 2, '#ffffff', 15)
      
      // 移除敌人
      enemies.value.splice(enemyIndex, 1)
      
      // 玩家死亡
      lives.value = 0
      gameOver.value = true
    }
  })
}

// 检查碰撞
const checkCollision = (rect1, rect2) => {
  return (
    rect1.x < rect2.x + rect2.width &&
    rect1.x + rect1.width > rect2.x &&
    rect1.y < rect2.y + rect2.height &&
    rect1.y + rect1.height > rect2.y
  )
}

// 检查胜利条件
const checkWinCondition = () => {
  // 每1000分升级一次关卡
  if (score.value >= level.value * 1000) {
    level.value++
    
    // 最高等级胜利
    if (level.value > 10) {
      gameWin.value = true
      gameOver.value = true
    }
  }
}

// 创建爆炸效果
const createExplosion = (x, y) => {
  explosions.value.push({
    x: x,
    y: y,
    life: 30,
    size: 50
  })
}

// 创建粒子爆炸
const createParticleBurst = (x, y, color, count) => {
  for (let i = 0; i < count; i++) {
    const angle = Math.random() * Math.PI * 2
    const speed = Math.random() * 5 + 2
    particles.value.push({
      x: x,
      y: y,
      vx: Math.cos(angle) * speed,
      vy: Math.sin(angle) * speed,
      gravity: 0.2,
      size: Math.random() * 5 + 2,
      color: color,
      alpha: 1
    })
  }
}

// 绘制游戏元素
const draw = () => {
  // 绘制背景星星
  drawBackgroundStars()
  
  // 绘制玩家
  drawPlayer()
  
  // 绘制玩家子弹
  drawPlayerBullets()
  
  // 绘制敌人
  drawEnemies()
  
  // 绘制敌人子弹
  drawEnemyBullets()
  
  // 绘制爆炸效果
  drawExplosions()
  
  // 绘制粒子效果
  drawParticles()
}

// 绘制背景星星
const drawBackgroundStars = () => {
  ctx.value.fillStyle = '#7A86E8'
  for (let i = 0; i < 100; i++) {
    const x = (i * 137.5) % CANVAS_WIDTH.value
    const y = (i * 277.3 + Date.now() * 0.01) % CANVAS_HEIGHT.value
    const size = Math.random() * 2 + 1
    ctx.value.fillRect(x, y, size, size)
  }
}

// 绘制玩家
const drawPlayer = () => {
  // 玩家主体
  const gradient = ctx.value.createLinearGradient(
    player.value.x, player.value.y,
    player.value.x, player.value.y + player.value.height
  )
  gradient.addColorStop(0, '#00ffff')
  gradient.addColorStop(1, '#0088ff')
  
  ctx.value.fillStyle = gradient
  ctx.value.beginPath()
  ctx.value.moveTo(player.value.x + player.value.width / 2, player.value.y)
  ctx.value.lineTo(player.value.x, player.value.y + player.value.height)
  ctx.value.lineTo(player.value.x + player.value.width, player.value.y + player.value.height)
  ctx.value.closePath()
  ctx.value.fill()
  
  // 玩家边框
  ctx.value.strokeStyle = '#ffffff'
  ctx.value.lineWidth = 2
  ctx.value.stroke()
  
  // 玩家发光效果
  ctx.value.shadowColor = '#00ffff'
  ctx.value.shadowBlur = 20
  ctx.value.stroke()
  ctx.value.shadowBlur = 0
}

// 绘制玩家子弹
const drawPlayerBullets = () => {
  player.value.bullets.forEach(bullet => {
    const gradient = ctx.value.createLinearGradient(
      bullet.x, bullet.y,
      bullet.x, bullet.y + bullet.height
    )
    gradient.addColorStop(0, '#ffffff')
    gradient.addColorStop(1, '#00ffff')
    
    ctx.value.fillStyle = gradient
    ctx.value.fillRect(bullet.x, bullet.y, bullet.width, bullet.height)
    
    // 子弹发光效果
    ctx.value.shadowColor = '#00ffff'
    ctx.value.shadowBlur = 10
    ctx.value.fill()
    ctx.value.shadowBlur = 0
  })
}

// 绘制敌人
const drawEnemies = () => {
  enemies.value.forEach(enemy => {
    // 敌人主体
    const gradient = ctx.value.createLinearGradient(
      enemy.x, enemy.y,
      enemy.x, enemy.y + enemy.height
    )
    gradient.addColorStop(0, '#ff6b35')
    gradient.addColorStop(1, '#c9184a')
    
    ctx.value.fillStyle = gradient
    ctx.value.fillRect(enemy.x, enemy.y, enemy.width, enemy.height)
    
    // 敌人边框
    ctx.value.strokeStyle = '#ffffff'
    ctx.value.lineWidth = 2
    ctx.value.strokeRect(enemy.x, enemy.y, enemy.width, enemy.height)
    
    // 敌人生命值条
    const healthBarWidth = enemy.width * (enemy.health / enemy.maxHealth)
    ctx.value.fillStyle = '#ff0000'
    ctx.value.fillRect(enemy.x, enemy.y - 5, enemy.width, 3)
    ctx.value.fillStyle = '#00ff00'
    ctx.value.fillRect(enemy.x, enemy.y - 5, healthBarWidth, 3)
    
    // 敌人发光效果
    ctx.value.shadowColor = '#ff6b35'
    ctx.value.shadowBlur = 15
    ctx.value.fill()
    ctx.value.shadowBlur = 0
  })
}

// 绘制敌人子弹
const drawEnemyBullets = () => {
  enemyBullets.value.forEach(bullet => {
    ctx.value.fillStyle = '#ff4444'
    ctx.value.fillRect(bullet.x, bullet.y, bullet.width, bullet.height)
    
    // 子弹发光效果
    ctx.value.shadowColor = '#ff4444'
    ctx.value.shadowBlur = 8
    ctx.value.fill()
    ctx.value.shadowBlur = 0
  })
}

// 绘制爆炸效果
const drawExplosions = () => {
  explosions.value.forEach(explosion => {
    const alpha = explosion.life / 30
    const size = explosion.size * (alpha + 0.5)
    
    const gradient = ctx.value.createRadialGradient(
      explosion.x, explosion.y, 0,
      explosion.x, explosion.y, size
    )
    gradient.addColorStop(0, `rgba(255, 255, 255, ${alpha})`)
    gradient.addColorStop(0.5, `rgba(255, 107, 53, ${alpha * 0.8})`)
    gradient.addColorStop(1, `rgba(201, 24, 74, ${alpha * 0.5})`)
    
    ctx.value.fillStyle = gradient
    ctx.value.beginPath()
    ctx.value.arc(explosion.x, explosion.y, size, 0, Math.PI * 2)
    ctx.value.fill()
  })
}

// 绘制粒子效果
const drawParticles = () => {
  particles.value.forEach(particle => {
    ctx.value.save()
    ctx.value.globalAlpha = particle.alpha
    ctx.value.fillStyle = particle.color
    ctx.value.beginPath()
    ctx.value.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2)
    ctx.value.fill()
    ctx.value.restore()
  })
}

// 键盘事件处理
const handleKeyDown = (e) => {
  if (keys.value.hasOwnProperty(e.key)) {
    keys.value[e.key] = true
    e.preventDefault()
  }
}

const handleKeyUp = (e) => {
  if (keys.value.hasOwnProperty(e.key)) {
    keys.value[e.key] = false
    e.preventDefault()
  }
}

// 生命周期钩子
onMounted(() => {
  // 初始化游戏
  initGame()
  
  // 添加事件监听
  window.addEventListener('keydown', handleKeyDown)
  window.addEventListener('keyup', handleKeyUp)
  window.addEventListener('resize', initGame)
})

onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('keydown', handleKeyDown)
  window.removeEventListener('keyup', handleKeyUp)
  window.removeEventListener('resize', initGame)
})
</script>

<style scoped>
.airplane-game-container {
  width: 100%;
  height: 100%;
  position: relative;
  background: linear-gradient(to bottom, #F5F7FA, #FFFFFF);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.game-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.game-info {
  position: absolute;
  top: 20px;
  left: 20px;
  right: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #333647;
  z-index: 10;
  pointer-events: none;
}

.score-display, .level-display, .lives-display {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  text-shadow: none;
}

.value {
  color: #7A86E8;
  font-size: 24px;
  font-weight: 700;
}

.hearts {
  color: #7A86E8;
  display: flex;
  gap: 5px;
}

.heart {
  font-size: 20px;
  text-shadow: none;
}

.difficulty-value {
  background: linear-gradient(135deg, #7A86E8, #9FA8DA);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 震动动画 */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.game-controls {
  position: absolute;
  bottom: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
  z-index: 10;
}

.game-over-panel {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid #7A86E8;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  z-index: 20;
  backdrop-filter: blur(10px);
}

.game-over-panel h2 {
  color: #333647;
  font-size: 36px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #7A86E8, #9FA8DA);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: none;
}

.final-score {
  color: #7A86E8;
  font-size: 24px;
  margin-bottom: 30px;
  font-weight: 600;
}

.game-start-panel {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid #7A86E8;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  z-index: 20;
  backdrop-filter: blur(10px);
  max-width: 500px;
}

.game-start-panel h2 {
  color: #333647;
  font-size: 48px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #7A86E8, #9FA8DA);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: none;
}

.game-start-panel p {
  color: #333647;
  font-size: 16px;
  margin-bottom: 30px;
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .game-info {
    flex-direction: column;
    gap: 10px;
    left: 50%;
    transform: translateX(-50%);
  }
  
  .game-controls {
    bottom: 10px;
    right: 10px;
    left: 10px;
    justify-content: center;
  }
  
  .game-over-panel, .game-start-panel {
    padding: 30px 20px;
    margin: 0 20px;
    width: calc(100% - 40px);
  }
  
  .game-over-panel h2, .game-start-panel h2 {
    font-size: 28px;
  }
}
</style>