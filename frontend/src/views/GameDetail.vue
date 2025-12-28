<template>
  <div class="game-detail-container">
    <el-card class="game-card">
      <div class="game-header">
        <div class="game-info">
          <h1 class="game-title">{{ game.name }}</h1>
          <p class="game-description">{{ game.description }}</p>
          <div class="game-stats">
            <span class="play-count"><el-icon><View /></el-icon> 游玩次数: {{ game.playCount }}</span>
            <span class="avg-score"><el-icon><Star /></el-icon> 平均分数: {{ game.averageScore }}</span>
          </div>
        </div>
        <div class="game-action">
          <el-button type="primary" size="large" @click="handleStartGame" :icon="'Cpu'" class="start-game-btn">
            开始游戏
          </el-button>
        </div>
      </div>
      
      <!-- 游戏规则 -->
      <div class="game-rules-section">
        <h2 class="section-title">游戏规则</h2>
        <div class="game-rules">
          {{ game.rules }}
        </div>
      </div>
      
      <!-- 游戏区域 -->
      <div class="game-play-section">
        <h2 class="section-title">游戏</h2>
        <div v-if="isGameStarted" class="game-container fade-in">
          <!-- 根据游戏类型动态加载游戏组件 -->
          <sweeper-game v-if="game.type === 'sweeper'" class="fade-in" />
          <snake-game v-else-if="game.type === 'snake'" class="fade-in" />
          <airplane-game v-else-if="game.type === 'airplane'" class="fade-in" />
          <game2048 v-else-if="game.type === '2048'" class="fade-in" />
        </div>
        <div v-else class="game-placeholder">
          <el-empty description="点击上方按钮开始游戏" />
        </div>
      </div>
      
      <!-- 游戏排行榜 -->
      <div class="game-ranking-section">
        <h2 class="section-title">排行榜</h2>
        <el-table :data="rankingList" class="ranking-table">
          <el-table-column prop="rank" label="排名" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="score" label="分数" width="100" />
          <el-table-column prop="time" label="时间" width="150" />
          <el-table-column prop="date" label="日期" width="180" />
        </el-table>
      </div>
      
      <!-- 我的游戏记录 -->
      <div class="my-records-section">
        <h2 class="section-title">我的游戏记录</h2>
        <div v-if="myRecords.length > 0">
          <el-table :data="myRecords" class="records-table">
            <el-table-column prop="score" label="分数" width="100" />
            <el-table-column prop="time" label="时间" width="150" />
            <el-table-column prop="date" label="日期" width="180" />
          </el-table>
        </div>
        <div v-else class="no-records">
          <el-empty description="暂无游戏记录" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import SweeperGame from '@/components/business/game/SweeperGame.vue'
import SnakeGame from '@/components/business/game/SnakeGame.vue'
import AirplaneGame from '@/components/business/game/AirplaneGame.vue'
import Game2048 from '@/components/business/game/Game2048.vue'

const route = useRoute()

// 响应式数据
const game = ref({
  name: '加载中...',
  description: '',
  playCount: 0,
  averageScore: 0,
  rules: '',
  type: ''
})

const rankingList = ref([
  { rank: 1, username: '玩家1', score: 999, time: '00:01:30', date: '2025-01-15 14:30:00' },
  { rank: 2, username: '玩家2', score: 888, time: '00:02:15', date: '2025-01-14 10:15:00' },
  { rank: 3, username: '玩家3', score: 777, time: '00:03:00', date: '2025-01-13 16:45:00' }
])

const myRecords = ref([
  { score: 500, time: '00:05:30', date: '2025-01-15 10:20:00' },
  { score: 350, time: '00:07:15', date: '2025-01-14 15:45:00' }
])

const isGameStarted = ref(false)

// 方法
const handleStartGame = () => {
  console.log('开始游戏:', game.value.type)
  isGameStarted.value = true
}

// 生命周期钩子
onMounted(() => {
  // 根据路由参数获取游戏详情
  const gameType = route.params.gameType
  console.log('获取游戏详情:', gameType)
  
  // 模拟游戏数据
  setTimeout(() => {
    // 根据游戏类型返回不同的游戏数据
    const gameDataMap = {
      sweeper: {
        name: '扫雷',
        description: '经典扫雷游戏，考验你的推理能力',
        playCount: 12345,
        averageScore: 850,
        rules: '扫雷是一款经典的单人益智游戏，考验玩家的推理能力和判断速度。游戏界面由许多方格组成，点击方格可以揭示其内容。如果方格中是数字，则表示周围8个方格中地雷的数量；如果是地雷，则游戏结束。你的目标是找出所有地雷，避免触雷。',
        type: 'sweeper'
      },
      snake: {
        name: '贪吃蛇',
        description: '经典贪吃蛇，挑战你的反应速度',
        playCount: 23456,
        averageScore: 1200,
        rules: '贪吃蛇是一款经典的休闲游戏，玩家通过方向键控制蛇的移动，吃掉食物使蛇变长。游戏目标是让蛇变得尽可能长，同时避免撞到墙壁或自己的身体。随着蛇的变长，游戏难度会逐渐增加。',
        type: 'snake'
      },
      airplane: {
        name: '打飞机',
        description: '经典打飞机游戏，重温童年记忆',
        playCount: 9876,
        averageScore: 15000,
        rules: '打飞机是一款经典的射击游戏，玩家控制一架飞机，通过射击消灭敌机，躲避敌机的攻击。游戏目标是尽可能多地消灭敌机，获得更高的分数。随着游戏进行，敌机的数量和速度会逐渐增加。',
        type: 'airplane'
      },
      '2048': {
        name: '2048',
        description: '热门数字游戏，合并数字挑战高分',
        playCount: 34567,
        averageScore: 2048,
        rules: '2048是一款热门的数字合并游戏，玩家通过方向键移动方块，相同数字的方块会合并成一个，合并后的数字会相加。游戏目标是合并出2048或更高数字的方块，获得更高的分数。随着数字的增大，游戏难度会逐渐增加。',
        type: '2048'
      }
    }
    
    game.value = gameDataMap[gameType] || gameDataMap.sweeper
  }, 500)
})
</script>

<style scoped>
.game-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-lg);
  background-color: var(--secondary-bg);
  min-height: 100vh;
}

.game-card {
  background-color: var(--primary-bg);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-light);
  transition: all var(--transition-normal) ease;
  overflow: hidden;
}

.game-card:hover {
  box-shadow: var(--shadow-medium);
}

/* 开始游戏按钮动画 */
.start-game-btn {
  transition: all var(--transition-normal) ease;
  font-weight: var(--font-weight-medium);
  padding: 0.8em 1.6em;
  font-size: var(--font-size-base);
  border: none;
  border-radius: var(--border-radius);
  background: var(--gradient-primary);
  color: white;
  box-shadow: var(--shadow-medium);
}

.start-game-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover), 0 0 15px rgba(122, 134, 232, 0.3);
}

.start-game-btn:active {
  transform: translateY(0);
}

/* 游戏容器加载动画 */
.game-container {
  animation: gameContainerAppear 0.5s ease-out;
  position: relative;
  overflow: hidden;
  background-color: var(--primary-bg);
  border-radius: var(--border-radius);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-light);
  min-height: 500px;
  display: flex;
  justify-content: center;
  align-items: center;
}

@keyframes gameContainerAppear {
  0% {
    opacity: 0;
    transform: scale(0.95);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

/* 游戏加载过渡效果 */
.game-placeholder {
  transition: all 0.3s ease;
  padding: var(--spacing-xl);
  text-align: center;
}

/* 游戏切换溶解效果 */
.fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

@media (max-width: 768px) {
  .game-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
}

.game-info {
  flex: 1;
}

.game-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: var(--spacing-sm);
}

.game-description {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-md);
  line-height: 1.6;
}

.game-stats {
  display: flex;
  gap: var(--spacing-lg);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.game-stats span {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.game-action {
  flex-shrink: 0;
}

/* 游戏规则 */
.game-rules-section {
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

.section-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  position: relative;
  padding-left: var(--spacing-md);
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: var(--gradient-primary);
  border-radius: 2px;
}

.game-rules {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.8;
  background-color: var(--secondary-bg);
  padding: var(--spacing-md);
  border-radius: var(--border-radius);
  white-space: pre-wrap;
}

/* 排行榜 */
.game-ranking-section {
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

.ranking-table {
  width: 100%;
  background-color: var(--primary-bg);
  border-radius: var(--border-radius);
  overflow: hidden;
}

/* 游戏区域 */
.game-play-section {
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

/* 我的游戏记录 */
.my-records-section {
  margin-bottom: var(--spacing-md);
}

.records-table {
  width: 100%;
  background-color: var(--primary-bg);
  border-radius: var(--border-radius);
  overflow: hidden;
}

.no-records {
  text-align: center;
  padding: var(--spacing-xl);
}
</style>