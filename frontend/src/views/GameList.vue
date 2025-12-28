<template>
  <div class="game-list-container">
    <h2>游戏列表</h2>
    <div class="games-grid">
      <game-card
        v-for="game in games"
        :key="game.type"
        :game="game"
        :is-hot="game.isHot"
        @play="handlePlayGame"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import GameCard from '@/components/business/game/GameCard.vue'

const router = useRouter()

// 模拟游戏数据
const games = ref([
  { type: 'sweeper', name: '扫雷', icon: 'el-icon-help', isHot: true, description: '经典扫雷游戏，考验你的推理能力' },
  { type: 'snake', name: '贪吃蛇', icon: 'el-icon-s-operation', isHot: true, description: '经典贪吃蛇，挑战你的反应速度' },
  { type: 'airplane', name: '打飞机', icon: 'el-icon-monitor', isHot: false, description: '经典打飞机游戏，重温童年记忆' },
  { type: '2048', name: '2048', icon: 'el-icon-collection-tag', isHot: true, description: '热门数字游戏，合并数字挑战高分' }
])

// 方法
const handlePlayGame = (gameType) => {
  router.push(`/games/${gameType}`)
}
</script>

<style scoped>
.game-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.game-list-container h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 30px;
  position: relative;
  padding-bottom: 10px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-align: center;
}

.game-list-container h2::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  border-radius: 2px;
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}
</style>