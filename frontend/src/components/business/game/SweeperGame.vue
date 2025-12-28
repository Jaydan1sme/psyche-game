<template>
  <div class="sweeper-game-container">
    <div class="game-header">
      <div class="game-stats">
        <div class="stat-item">
          <span class="stat-label">地雷数:</span>
          <span class="stat-value">{{ minesCount }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">时间:</span>
          <span class="stat-value">{{ gameTime }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">分数:</span>
          <span class="stat-value">{{ score }}</span>
        </div>
      </div>
      <div class="game-controls">
        <el-button type="primary" @click="resetGame">重新开始</el-button>
        <el-select v-model="difficulty" @change="handleDifficultyChange" size="small">
          <el-option label="简单" value="easy" />
          <el-option label="中等" value="medium" />
          <el-option label="困难" value="hard" />
        </el-select>
      </div>
    </div>
    <div class="game-board" ref="gameBoardRef" @click="handleBoardClick" @contextmenu.prevent="handleBoardRightClick">
      <div 
        v-for="(row, rowIndex) in gameBoard" 
        :key="rowIndex" 
        class="game-row"
      >
        <div 
          v-for="(cell, colIndex) in row" 
          :key="colIndex" 
          class="game-cell"
          :class="getCellClass(cell)"
          @click.stop="handleCellClick(rowIndex, colIndex)"
          @contextmenu.stop.prevent="handleCellRightClick(rowIndex, colIndex)"
        >
          <span v-if="cell.revealed && cell.value > 0" :class="`cell-value value-${cell.value}`">{{ cell.value }}</span>
          <el-icon v-else-if="cell.revealed && cell.isMine" class="cell-mine">
            <StarFilled />
          </el-icon>
          <el-icon v-else-if="cell.flagged" class="cell-flag">
            <CircleCheckFilled />
          </el-icon>
        </div>
      </div>
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { StarFilled, CircleCheckFilled } from '@element-plus/icons-vue'

// 游戏难度配置
const difficultyConfig = {
  easy: { rows: 9, cols: 9, mines: 10 },
  medium: { rows: 16, cols: 16, mines: 40 },
  hard: { rows: 16, cols: 30, mines: 99 }
}

// 响应式数据
const gameBoardRef = ref(null)
const difficulty = ref('easy')
const gameBoard = ref([])
const minesCount = ref(0)
const gameTime = ref(0)
const score = ref(0)
const gameOver = ref(false)
const isWin = ref(false)
let timer = null

// 初始化游戏
const initGame = () => {
  const config = difficultyConfig[difficulty.value]
  const { rows, cols, mines } = config
  
  // 重置游戏状态
  gameOver.value = false
  isWin.value = false
  gameTime.value = 0
  score.value = 0
  minesCount.value = mines
  
  // 清空计时器
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  
  // 初始化游戏板
  const board = []
  for (let i = 0; i < rows; i++) {
    const row = []
    for (let j = 0; j < cols; j++) {
      row.push({
        isMine: false,
        revealed: false,
        flagged: false,
        value: 0
      })
    }
    board.push(row)
  }
  
  // 随机放置地雷
  let placedMines = 0
  while (placedMines < mines) {
    const row = Math.floor(Math.random() * rows)
    const col = Math.floor(Math.random() * cols)
    if (!board[row][col].isMine) {
      board[row][col].isMine = true
      placedMines++
    }
  }
  
  // 计算每个单元格周围的地雷数量
  for (let i = 0; i < rows; i++) {
    for (let j = 0; j < cols; j++) {
      if (!board[i][j].isMine) {
        let count = 0
        for (let di = -1; di <= 1; di++) {
          for (let dj = -1; dj <= 1; dj++) {
            const ni = i + di
            const nj = j + dj
            if (ni >= 0 && ni < rows && nj >= 0 && nj < cols && board[ni][nj].isMine) {
              count++
            }
          }
        }
        board[i][j].value = count
      }
    }
  }
  
  gameBoard.value = board
}

// 获取单元格样式类
const getCellClass = (cell) => {
  const classes = []
  if (cell.revealed) {
    classes.push('revealed')
    if (cell.isMine) {
      classes.push('mine')
    }
  } else if (cell.flagged) {
    classes.push('flagged')
  }
  return classes
}

// 处理单元格点击
const handleCellClick = (row, col) => {
  if (gameOver.value) return
  
  const cell = gameBoard.value[row][col]
  if (cell.revealed || cell.flagged) return
  
  // 开始计时
  if (!timer) {
    timer = setInterval(() => {
      gameTime.value++
    }, 1000)
  }
  
  // 揭示单元格
  revealCell(row, col)
  
  // 检查游戏是否结束
  checkGameStatus()
}

// 处理单元格右键点击
const handleCellRightClick = (row, col) => {
  if (gameOver.value) return
  
  const cell = gameBoard.value[row][col]
  if (cell.revealed) return
  
  // 切换标记状态
  cell.flagged = !cell.flagged
  
  // 更新地雷计数
  minesCount.value += cell.flagged ? -1 : 1
  
  // 检查游戏是否结束
  checkGameStatus()
}

// 揭示单元格
const revealCell = (row, col) => {
  const cell = gameBoard.value[row][col]
  if (cell.revealed || cell.flagged) return
  
  cell.revealed = true
  score.value++
  
  // 如果是地雷，游戏结束
  if (cell.isMine) {
    gameOver.value = true
    isWin.value = false
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    // 揭示所有地雷
    revealAllMines()
    return
  }
  
  // 如果是空白单元格，递归揭示周围单元格
  if (cell.value === 0) {
    for (let di = -1; di <= 1; di++) {
      for (let dj = -1; dj <= 1; dj++) {
        const ni = row + di
        const nj = col + dj
        if (ni >= 0 && ni < gameBoard.value.length && nj >= 0 && nj < gameBoard.value[0].length) {
          revealCell(ni, nj)
        }
      }
    }
  }
}

// 揭示所有地雷
const revealAllMines = () => {
  for (let i = 0; i < gameBoard.value.length; i++) {
    for (let j = 0; j < gameBoard.value[i].length; j++) {
      if (gameBoard.value[i][j].isMine) {
        gameBoard.value[i][j].revealed = true
      }
    }
  }
}

// 检查游戏是否结束
const checkGameStatus = () => {
  if (gameOver.value) return
  
  const rows = gameBoard.value.length
  const cols = gameBoard.value[0].length
  let revealedCount = 0
  let correctFlags = 0
  let totalMines = 0
  
  for (let i = 0; i < rows; i++) {
    for (let j = 0; j < cols; j++) {
      const cell = gameBoard.value[i][j]
      if (cell.isMine) {
        totalMines++
        if (cell.flagged) {
          correctFlags++
        }
      } else if (cell.revealed) {
        revealedCount++
      }
    }
  }
  
  // 检查是否获胜
  const totalCells = rows * cols
  if (revealedCount === totalCells - totalMines || correctFlags === totalMines) {
    gameOver.value = true
    isWin.value = true
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }
}

// 重置游戏
const resetGame = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  initGame()
}

// 处理难度变化
const handleDifficultyChange = () => {
  resetGame()
}

// 生命周期钩子
onMounted(() => {
  initGame()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped>
.sweeper-game-container {
  width: 100%;
  max-width: 800px;
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

.game-board {
  display: flex;
  flex-direction: column;
  gap: 2px;
  background: var(--bg-secondary);
  padding: 10px;
  border-radius: 8px;
  max-height: 600px;
  overflow: auto;
}

.game-row {
  display: flex;
  gap: 2px;
}

.game-cell {
  width: 30px;
  height: 30px;
  background: var(--primary-bg);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
  color: var(--text-primary);
}

.game-cell:hover {
  background: var(--bg-secondary);
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.game-cell.revealed {
  background: var(--primary-bg);
  cursor: default;
  border-color: var(--border-color);
}

.game-cell.revealed:hover {
  transform: none;
  box-shadow: none;
}

.game-cell.mine {
  background: rgba(244, 67, 54, 0.1);
  border-color: var(--error-color);
  animation: mineExplosion 0.5s ease-out;
}

@keyframes mineExplosion {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.5);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.game-cell.flagged {
  background: rgba(76, 175, 80, 0.1);
  border-color: var(--success-color);
}

.cell-value {
  font-weight: 700;
}

.value-1 {
  color: #3B82F6;
}

.value-2 {
  color: #10B981;
}

.value-3 {
  color: #EF4444;
}

.value-4 {
  color: #8B5CF6;
}

.value-5 {
  color: #F59E0B;
}

.value-6 {
  color: #EC4899;
}

.value-7 {
  color: #14B8A6;
}

.value-8 {
  color: #6B7280;
}

.cell-mine {
  color: var(--error-color);
  font-size: 18px;
}

.cell-flag {
  color: var(--success-color);
  font-size: 18px;
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
  .game-cell {
    width: 25px;
    height: 25px;
    font-size: 14px;
  }
  
  .game-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .game-stats {
    justify-content: center;
  }
}
</style>