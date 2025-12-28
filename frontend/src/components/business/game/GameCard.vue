<template>
  <div class="game-card" @click="handlePlay">
    <div class="game-card-inner">
      <!-- 游戏图标 -->
      <div class="game-icon-container">
        <game-icons 
          :type="game.type" 
          :size="80" 
          :hoverable="true"
        />
        <div class="hot-badge" v-if="isHot">热门</div>
      </div>
      <!-- 游戏信息 -->
      <div class="game-info">
        <h3 class="game-name">{{ game.name }}</h3>
        <p class="game-description">{{ game.description }}</p>
      </div>
      <!-- 操作按钮 -->
      <div class="game-actions">
        <el-button type="primary" size="small" @click.stop="handlePlay">
          <el-icon><PlayArrow /></el-icon> 开始游戏
        </el-button>
        <el-button size="small" @click.stop="handleRules">
          <el-icon><Document /></el-icon> 游戏规则
        </el-button>
      </div>
    </div>
    
    <!-- 游戏规则弹窗 -->
    <el-dialog
      v-model="showRulesDialog"
      :title="game.name + ' - 游戏规则'"
      width="500px"
    >
      <div class="rules-content">
        <template v-if="game.type === 'sweeper'">
          <h4>扫雷游戏规则</h4>
          <ul>
            <li>点击方块来揭示它，数字表示周围8个方块中的地雷数量</li>
            <li>右键点击方块来标记或取消标记地雷</li>
            <li>揭示所有非地雷方块即可获胜</li>
            <li>点击到地雷则游戏结束</li>
          </ul>
        </template>
        <template v-else-if="game.type === 'snake'">
          <h4>贪吃蛇游戏规则</h4>
          <ul>
            <li>使用方向键控制蛇的移动方向</li>
            <li>吃到食物后蛇身会变长</li>
            <li>撞到墙壁或自己的身体则游戏结束</li>
            <li>分数根据吃到的食物数量计算</li>
          </ul>
        </template>
        <template v-else-if="game.type === 'airplane'">
          <h4>打飞机游戏规则</h4>
          <ul>
            <li>使用方向键控制飞机移动</li>
            <li>使用空格键发射子弹</li>
            <li>消灭敌机获得分数</li>
            <li>避开敌机和子弹，生命值耗尽则游戏结束</li>
          </ul>
        </template>
        <template v-else-if="game.type === '2048'">
          <h4>2048游戏规则</h4>
          <ul>
            <li>使用方向键滑动方块</li>
            <li>相同数字的方块碰撞后会合并</li>
            <li>每次滑动后会随机生成一个2或4</li>
            <li>合并出2048即可获胜</li>
            <li>无法移动方块则游戏结束</li>
          </ul>
        </template>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showRulesDialog = false">关闭</el-button>
          <el-button type="primary" @click="confirmPlay">开始游戏</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue'
import { useRouter } from 'vue-router'
import GameIcons from '@/components/common/GameIcons.vue'

const router = useRouter()

// 定义属性
const props = defineProps({
  game: {
    type: Object,
    required: true,
    default: () => ({})
  },
  isHot: {
    type: Boolean,
    default: false
  }
})

// 定义事件
const emit = defineEmits(['play', 'rules'])

// 响应式数据
const showRulesDialog = ref(false)

// 方法
const handlePlay = () => {
  emit('play', props.game.type)
}

const handleRules = () => {
  showRulesDialog.value = true
  emit('rules', props.game.type)
}

const confirmPlay = () => {
  showRulesDialog.value = false
  handlePlay()
}
</script>

<style scoped>
.game-card {
  perspective: 1000px;
}

.game-card-inner {
  background-color: var(--primary-bg);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-light);
  transition: all var(--transition-normal) ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transform-style: preserve-3d;
}

.game-card:hover .game-card-inner {
  transform: translateY(-8px) scale(1.02);
  box-shadow: var(--shadow-medium);
  border-color: var(--accent-color);
}

/* 游戏图标容器 */
.game-icon-container {
  position: relative;
  margin-bottom: var(--spacing-md);
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all var(--transition-normal) ease;
}

.game-card:hover .game-icon-container {
  transform: scale(1.05);
}

/* 热门徽章 */
.hot-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: rgba(255, 77, 79, 0.1);
  color: var(--error-color);
  font-size: 12px;
  font-weight: var(--font-weight-medium);
  padding: 4px 8px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.2);
  transform: rotate(15deg);
}

/* 游戏信息 */
.game-info {
  margin-bottom: var(--spacing-lg);
  text-align: center;
}

.game-name {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  line-height: 1.4;
}

.game-description {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 操作按钮 */
.game-actions {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: center;
}

/* 规则弹窗 */
.rules-content {
  padding: var(--spacing-sm) 0;
}

.rules-content h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.rules-content ul {
  padding-left: 20px;
  margin: 0;
}

.rules-content li {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.8;
  margin-bottom: var(--spacing-sm);
}

.rules-content li:last-child {
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .game-card-inner {
    padding: var(--spacing-md);
  }
  
  .game-info {
    margin-bottom: var(--spacing-md);
  }
  
  .game-name {
    font-size: var(--font-size-base);
  }
  
  .game-actions {
    flex-direction: column;
  }
}
</style>
