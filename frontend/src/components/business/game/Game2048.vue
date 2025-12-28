<template>
  <div class="game2048-container" ref="gameContainer">
    <!-- 游戏标题 -->
    <div class="game-header">
      <h1 class="game-title">2048</h1>
      <div class="game-stats">
        <div class="score-box">
          <div class="score-label">分数</div>
          <div class="score-value" ref="scoreDisplay">{{ score }}</div>
        </div>
        <div class="best-box">
          <div class="best-label">最高分</div>
          <div class="best-value">{{ bestScore }}</div>
        </div>
      </div>
    </div>
    
    <!-- 游戏控制按钮 -->
    <div class="game-controls">
      <el-button @click="resetGame" :icon="'Refresh'">
        重新开始
      </el-button>
      <el-button @click="toggleGame" :icon="isPaused ? 'Play' : 'VideoPause'">
        {{ isPaused ? '继续' : '暂停' }}
      </el-button>
      <el-button @click="showGameRules" :icon="'Document'">
        游戏规则
      </el-button>
    </div>
    
    <!-- 游戏棋盘 -->
    <div class="game-board-container">
      <div class="game-board" ref="gameBoard">
        <div 
          v-for="(cell, index) in cells" 
          :key="index" 
          class="cell"
          :class="{ 'merged': cell.merged }"
        >
          <div 
            v-if="cell.value" 
            class="tile"
            :class="`tile-${cell.value}`"
            :data-value="cell.value"
            ref="tiles"
          >
            {{ cell.value }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- 游戏结束面板 -->
    <div v-if="gameOver" class="game-over-panel">
      <h2>{{ gameWin ? '恭喜胜利！' : '游戏结束！' }}</h2>
      <div class="final-score">最终分数: {{ score }}</div>
      <el-button type="primary" @click="resetGame" size="large">
        再来一局
      </el-button>
    </div>
    
    <!-- 游戏规则对话框 -->
    <el-dialog
      v-model="rulesVisible"
      title="2048游戏规则"
      width="500px"
    >
      <div class="game-rules-content">
        <p>2048是一款热门的数字合并游戏，玩家通过方向键控制方块移动，相同数字的方块会合并成一个，合并后的数字会相加。</p>
        <h3>游戏规则：</h3>
        <ul>
          <li>使用键盘方向键（↑↓←→）或WASD键控制所有方块移动</li>
          <li>相同数字的方块碰撞时会合并，合并后数值相加</li>
          <li>每次移动后会在空白位置随机生成一个2或4</li>
          <li>游戏目标是合并出2048或更高数字的方块</li>
          <li>当棋盘填满且无法移动时，游戏结束</li>
        </ul>
        <h3>计分规则：</h3>
        <p>每次合并方块时，合并后的数值会累加到总分数中。例如，合并两个2得到4，加4分；合并两个4得到8，加8分，以此类推。</p>
      </div>
      <template #footer>
        <el-button @click="rulesVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 提示文本 -->
    <div class="game-tip">
      <p>{{ gameTip }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'

const gameBoard = ref(null)
const gameContainer = ref(null)
const scoreDisplay = ref(null)
const tiles = ref([])
const rulesVisible = ref(false)

// 游戏状态
const gameStarted = ref(false)
const gameOver = ref(false)
const gameWin = ref(false)
const isPaused = ref(false)
const score = ref(0)
const bestScore = ref(parseInt(localStorage.getItem('2048-best-score')) || 0)
const gameTip = ref('使用键盘方向键或WASD控制方块移动')

// 游戏配置
const GRID_SIZE = 4
const CELL_SIZE = 100
const CELL_GAP = 15

// 游戏数据
const cells = ref([])
const grid = ref([])
const mergedCells = ref([])

// 键盘控制
const keys = ref({})

// 初始化游戏
const initGame = () => {
  // 初始化棋盘
  grid.value = Array(GRID_SIZE).fill().map(() => Array(GRID_SIZE).fill(0))
  cells.value = Array(GRID_SIZE * GRID_SIZE).fill().map(() => ({ value: 0, merged: false }))
  mergedCells.value = []
  
  // 重置游戏状态
  score.value = 0
  gameOver.value = false
  gameWin.value = false
  isPaused.value = false
  
  // 添加初始方块
  addRandomTile()
  addRandomTile()
  
  // 更新棋盘显示
  updateBoard()
  
  // 保存最高分
  if (bestScore.value === 0) {
    localStorage.setItem('2048-best-score', '0')
  }
}

// 重置游戏
const resetGame = () => {
  initGame()
}

// 暂停/继续游戏
const toggleGame = () => {
  isPaused.value = !isPaused.value
  gameTip.value = isPaused ? '游戏已暂停，点击继续按钮恢复游戏' : '使用键盘方向键或WASD控制方块移动'
}

// 添加随机方块
const addRandomTile = () => {
  // 寻找空位置
  const emptyCells = []
  for (let row = 0; row < GRID_SIZE; row++) {
    for (let col = 0; col < GRID_SIZE; col++) {
      if (grid.value[row][col] === 0) {
        emptyCells.push({ row, col })
      }
    }
  }
  
  if (emptyCells.length === 0) return false
  
  // 随机选择一个空位置
  const { row, col } = emptyCells[Math.floor(Math.random() * emptyCells.length)]
  
  // 随机生成2或4
  grid.value[row][col] = Math.random() < 0.9 ? 2 : 4
  
  // 创建方块出现动画
  setTimeout(() => {
    const tile = tiles.value[row * GRID_SIZE + col]
    if (tile) {
      tile.style.transform = 'scale(1)'
    }
  }, 10)
  
  return true
}



// 移动方块
const move = (direction) => {
  if (isPaused.value || gameOver.value) return false
  
  let moved = false
  
  // 根据方向调整遍历顺序
  const directions = {
    up: { rows: [0, 1, 2, 3], cols: [0, 1, 2, 3], rowDelta: -1, colDelta: 0, checkRow: (row, col, nextRow, nextCol) => nextRow >= 0 },
    down: { rows: [3, 2, 1, 0], cols: [0, 1, 2, 3], rowDelta: 1, colDelta: 0, checkRow: (row, col, nextRow, nextCol) => nextRow < GRID_SIZE },
    left: { rows: [0, 1, 2, 3], cols: [0, 1, 2, 3], rowDelta: 0, colDelta: -1, checkCol: (row, col, nextRow, nextCol) => nextCol >= 0 },
    right: { rows: [0, 1, 2, 3], cols: [3, 2, 1, 0], rowDelta: 0, colDelta: 1, checkCol: (row, col, nextRow, nextCol) => nextCol < GRID_SIZE }
  }
  
  const dir = directions[direction]
  if (!dir) return false
  
  // 重置合并标记
  mergedCells.value = []
  
  // 遍历棋盘
  for (const row of dir.rows) {
    for (const col of dir.cols) {
      if (grid.value[row][col] === 0) continue
      
      let nextRow = row
      let nextCol = col
      let finalRow = row
      let finalCol = col
      
      // 寻找最终位置
      while (true) {
        nextRow += dir.rowDelta
        nextCol += dir.colDelta
        
        // 检查边界
        const checkBoundary = dir.checkRow ? dir.checkRow(row, col, nextRow, nextCol) : dir.checkCol(row, col, nextRow, nextCol)
        if (!checkBoundary) break
        
        // 检查目标位置
        if (grid.value[nextRow][nextCol] === 0) {
          finalRow = nextRow
          finalCol = nextCol
        } else if (grid.value[nextRow][nextCol] === grid.value[row][col] && !mergedCells.value.includes(`${nextRow},${nextCol}`)) {
          // 可以合并
          finalRow = nextRow
          finalCol = nextCol
          mergedCells.value.push(`${finalRow},${finalCol}`)
          break
        } else {
          // 不能移动或合并
          break
        }
      }
      
      // 移动方块
      if (finalRow !== row || finalCol !== col) {
        moved = true
        
        if (mergedCells.value.includes(`${finalRow},${finalCol}`)) {
          // 合并方块
          grid.value[finalRow][finalCol] *= 2
          grid.value[row][col] = 0
          
          // 更新分数
          score.value += grid.value[finalRow][finalCol]
          
          // 更新最高分
          if (score.value > bestScore.value) {
            bestScore.value = score.value
            localStorage.setItem('2048-best-score', bestScore.value.toString())
          }
          
          // 检查胜利条件
          if (grid.value[finalRow][finalCol] === 2048 && !gameWin.value) {
            gameWin.value = true
            gameTip.value = '恭喜你合成了2048！继续挑战更高分数吧！'
          }
        } else {
          // 移动方块
          grid.value[finalRow][finalCol] = grid.value[row][col]
          grid.value[row][col] = 0
        }
      }
    }
  }
  
  // 如果有移动，添加新方块
  if (moved) {
    // 播放移动音效
    playMoveSound()
    
    // 更新棋盘显示
    updateBoard()
    
    // 添加新方块
    setTimeout(() => {
      addRandomTile()
      
      // 检查游戏结束
      if (!canMove()) {
        gameOver.value = true
        gameTip.value = '游戏结束！点击重新开始按钮再来一局吧！'
      }
    }, 100)
  }
  
  return moved
}

// 检查是否还能移动
const canMove = () => {
  // 检查是否有空位
  for (let row = 0; row < GRID_SIZE; row++) {
    for (let col = 0; col < GRID_SIZE; col++) {
      if (grid.value[row][col] === 0) return true
    }
  }
  
  // 检查是否有相邻相同数字
  for (let row = 0; row < GRID_SIZE; row++) {
    for (let col = 0; col < GRID_SIZE; col++) {
      const current = grid.value[row][col]
      
      // 检查右侧
      if (col < GRID_SIZE - 1 && grid.value[row][col + 1] === current) return true
      
      // 检查下方
      if (row < GRID_SIZE - 1 && grid.value[row + 1][col] === current) return true
    }
  }
  
  return false
}

// 键盘事件处理
const handleKeyDown = (e) => {
  if (isPaused.value || gameOver.value) return
  
  // 方向键映射
  const keyMap = {
    ArrowUp: 'up',
    ArrowDown: 'down',
    ArrowLeft: 'left',
    ArrowRight: 'right',
    w: 'up',
    s: 'down',
    a: 'left',
    d: 'right'
  }
  
  const direction = keyMap[e.key]
  if (direction) {
    e.preventDefault()
    move(direction)
  }
}

// 触摸事件处理
let touchStartX = 0
let touchStartY = 0

const handleTouchStart = (e) => {
  touchStartX = e.touches[0].clientX
  touchStartY = e.touches[0].clientY
}

const handleTouchEnd = (e) => {
  if (isPaused.value || gameOver.value) return
  
  const touchEndX = e.changedTouches[0].clientX
  const touchEndY = e.changedTouches[0].clientY
  
  const deltaX = touchEndX - touchStartX
  const deltaY = touchEndY - touchStartY
  
  // 确定滑动方向
  if (Math.abs(deltaX) > Math.abs(deltaY)) {
    // 水平滑动
    if (Math.abs(deltaX) > 50) {
      move(deltaX > 0 ? 'right' : 'left')
    }
  } else {
    // 垂直滑动
    if (Math.abs(deltaY) > 50) {
      move(deltaY > 0 ? 'down' : 'up')
    }
  }
}

// 播放移动音效（模拟）
const playMoveSound = () => {
  // 这里可以添加音效播放逻辑
}

// 更新棋盘显示
const updateBoard = () => {
  // 将grid转换为cells
  let index = 0
  for (let row = 0; row < GRID_SIZE; row++) {
    for (let col = 0; col < GRID_SIZE; col++) {
      cells.value[index].value = grid.value[row][col]
      cells.value[index].merged = mergedCells.value.includes(`${row},${col}`)
      index++
    }
  }
}

// 生命周期钩子
onMounted(() => {
  // 初始化游戏
  initGame()
  
  // 添加事件监听
  window.addEventListener('keydown', handleKeyDown)
  window.addEventListener('touchstart', handleTouchStart)
  window.addEventListener('touchend', handleTouchEnd)
})

onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('keydown', handleKeyDown)
  window.removeEventListener('touchstart', handleTouchStart)
  window.removeEventListener('touchend', handleTouchEnd)
})

// 监听分数变化，添加动画效果
watch(score, (newScore, oldScore) => {
  if (newScore > oldScore && scoreDisplay.value) {
    // 分数增长动画
    scoreDisplay.value.classList.add('score-grow')
    setTimeout(() => {
      scoreDisplay.value.classList.remove('score-grow')
    }, 300)
  }
})
</script>

<style scoped>
.game2048-container {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background: var(--primary-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: var(--shadow-medium);
  position: relative;
  overflow: hidden;
  color: var(--text-primary);
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: var(--bg-secondary);
  padding: 16px;
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
}

.game-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.game-stats {
  display: flex;
  gap: 15px;
}

.score-box, .best-box {
  background: var(--primary-bg);
  border-radius: 12px;
  padding: 12px 20px;
  text-align: center;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-light);
}

.score-label, .best-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-tertiary);
  margin-bottom: 4px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.score-value, .best-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.score-grow {
  animation: scoreGrow 0.3s ease-out;
}

@keyframes scoreGrow {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

.game-controls {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.game-board-container {
  background: var(--bg-secondary);
  border-radius: 16px;
  padding: 15px;
  margin-bottom: 20px;
  border: 1px solid var(--border-color);
}

.game-board {
  display: grid;
  grid-template-columns: repeat(4, 100px);
  grid-template-rows: repeat(4, 100px);
  gap: 15px;
  width: fit-content;
  margin: 0 auto;
}

.cell {
  width: 100px;
  height: 100px;
  background: var(--primary-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-light);
}

.cell.merged {
  animation: merge 0.3s ease-out;
}

@keyframes merge {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.tile {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #eee4da;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 40px;
  font-weight: 700;
  color: #776e65;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.5);
}

/* 不同数值的方块样式 */
.tile-2 { background: linear-gradient(135deg, #eee4da, #ede0c8); }
.tile-4 { background: linear-gradient(135deg, #ede0c8, #f2b179); }
.tile-8 { background: linear-gradient(135deg, #f2b179, #f59563); color: #f9f6f2; }
.tile-16 { background: linear-gradient(135deg, #f59563, #f67c5f); color: #f9f6f2; }
.tile-32 { background: linear-gradient(135deg, #f67c5f, #f65e3b); color: #f9f6f2; }
.tile-64 { background: linear-gradient(135deg, #f65e3b, #edcf72); color: #f9f6f2; }
.tile-128 { background: linear-gradient(135deg, #edcf72, #edcc61); color: #f9f6f2; box-shadow: 0 0 20px rgba(237, 204, 97, 0.5); }
.tile-256 { background: linear-gradient(135deg, #edcc61, #edc850); color: #f9f6f2; box-shadow: 0 0 20px rgba(237, 200, 80, 0.6); }
.tile-512 { background: linear-gradient(135deg, #edc850, #edc53f); color: #f9f6f2; box-shadow: 0 0 20px rgba(237, 197, 63, 0.7); }
.tile-1024 { background: linear-gradient(135deg, #edc53f, #edc22e); color: #f9f6f2; box-shadow: 0 0 30px rgba(237, 194, 46, 0.8); font-size: 30px; }
.tile-2048 { background: linear-gradient(135deg, #edc22e, #ffed4e); color: #f9f6f2; box-shadow: 0 0 40px rgba(237, 194, 46, 1); font-size: 30px; }
.tile-4096 { background: linear-gradient(135deg, #ffed4e, #ffc107); color: #f9f6f2; box-shadow: 0 0 50px rgba(255, 205, 7, 0.9); font-size: 30px; }
.tile-8192 { background: linear-gradient(135deg, #ffc107, #ff9800); color: #f9f6f2; box-shadow: 0 0 60px rgba(255, 152, 0, 1); font-size: 30px; }

/* 方块出现动画 */
.tile { animation: tileAppear 0.3s ease-out; }

@keyframes tileAppear {
  0% { transform: scale(0); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

/* 游戏结束面板 */
.game-over-panel {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  box-shadow: var(--shadow-medium);
  border: 1px solid var(--border-color);
  z-index: 10;
  animation: panelAppear 0.3s ease-out;
  color: var(--text-primary);
}

@keyframes panelAppear {
  0% { transform: translate(-50%, -50%) scale(0.8); opacity: 0; }
  100% { transform: translate(-50%, -50%) scale(1); opacity: 1; }
}

.game-over-panel h2 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 20px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.final-score {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 30px;
}

.game-tip {
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 10px;
  background: var(--bg-secondary);
  padding: 12px;
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
}

.game-tip p {
  margin: 0;
}

.game-rules-content {
  line-height: 1.6;
}

.game-rules-content h3 {
  margin-top: 20px;
  margin-bottom: 10px;
  color: #667eea;
}

.game-rules-content ul {
  padding-left: 20px;
}

.game-rules-content li {
  margin-bottom: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .game2048-container {
    padding: 15px;
    margin: 10px;
  }
  
  .game-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .game-title {
    font-size: 36px;
  }
  
  .game-stats {
    justify-content: center;
  }
  
  .game-controls {
    justify-content: center;
  }
  
  .game-board {
    grid-template-columns: repeat(4, 70px);
    grid-template-rows: repeat(4, 70px);
    gap: 10px;
  }
  
  .cell {
    width: 70px;
    height: 70px;
  }
  
  .tile {
    font-size: 30px;
  }
  
  .tile-1024, .tile-2048, .tile-4096, .tile-8192 {
    font-size: 20px;
  }
  
  .game-board-container {
    padding: 10px;
  }
}
</style>