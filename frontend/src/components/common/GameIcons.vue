<template>
  <div 
    class="game-icon" 
    :class="{ 'game-icon-hover': hoverable }"
    :style="{
      width: `${size}px`,
      height: `${size}px`,
      borderRadius: `${borderRadius}px`
    }"
  >
    <!-- 扫雷图标 -->
    <div v-if="type === 'sweeper'" class="icon-content">
      <svg width="100%" height="100%" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
        <!-- 地雷主体 -->
        <circle cx="40" cy="40" r="25" fill="none" stroke="#333647" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
        <!-- 地雷引线 -->
        <path 
          d="M40 15 Q55 20 60 35" 
          fill="none" 
          stroke="url(#sweeperGradient)" 
          stroke-width="3" 
          stroke-linecap="round" 
          stroke-linejoin="round"
          class="sweeper-fuse"
        />
        <!-- 地雷引信 -->
        <circle cx="60" cy="35" r="3" fill="url(#sweeperGradient)"/>
        <!-- 地雷花纹 -->
        <path d="M40 20 L40 60" fill="none" stroke="#333647" stroke-width="2"/>
        <path d="M20 40 L60 40" fill="none" stroke="#333647" stroke-width="2"/>
        <circle cx="40" cy="40" r="10" fill="none" stroke="#333647" stroke-width="2"/>
        <!-- 渐变定义 -->
        <defs>
          <linearGradient id="sweeperGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" :style="{ stopColor: '#7A86E8' }" />
            <stop offset="100%" :style="{ stopColor: '#82E2FF' }" />
          </linearGradient>
        </defs>
      </svg>
    </div>
    
    <!-- 贪吃蛇图标 -->
    <div v-else-if="type === 'snake'" class="icon-content">
      <svg width="100%" height="100%" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
        <!-- 蛇身 -->
        <g class="snake-body">
          <!-- 蛇头 -->
          <ellipse cx="60" cy="40" rx="8" ry="12" fill="none" stroke="#333647" stroke-width="3"/>
          <!-- 蛇眼 -->
          <circle cx="65" cy="37" r="2" fill="url(#snakeGradient)" class="snake-eye"/>
          <circle cx="65" cy="43" r="2" fill="url(#snakeGradient)" class="snake-eye"/>
          <!-- 蛇身段1 -->
          <ellipse cx="45" cy="40" rx="8" ry="12" fill="none" stroke="#333647" stroke-width="3" class="snake-segment"/>
          <!-- 蛇身段2 -->
          <ellipse cx="30" cy="40" rx="8" ry="12" fill="none" stroke="#333647" stroke-width="3" class="snake-segment"/>
          <!-- 蛇身段3 -->
          <ellipse cx="15" cy="40" rx="8" ry="12" fill="none" stroke="#333647" stroke-width="3" class="snake-segment"/>
        </g>
        <!-- 渐变定义 -->
        <defs>
          <linearGradient id="snakeGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" :style="{ stopColor: '#7A86E8' }" />
            <stop offset="100%" :style="{ stopColor: '#82E2FF' }" />
          </linearGradient>
        </defs>
      </svg>
    </div>
    
    <!-- 打飞机图标 -->
    <div v-else-if="type === 'airplane'" class="icon-content">
      <svg width="100%" height="100%" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
        <!-- 飞机主体 -->
        <g class="airplane-body">
          <!-- 机翼 -->
          <path 
            d="M30 40 L60 20 L60 60 Z" 
            fill="none" 
            stroke="url(#airplaneGradient)" 
            stroke-width="3" 
            stroke-linecap="round" 
            stroke-linejoin="round"
            class="airplane-wing"
          />
          <!-- 机身 -->
          <path 
            d="M20 40 L30 40" 
            fill="none" 
            stroke="#333647" 
            stroke-width="3" 
            stroke-linecap="round"
          />
          <!-- 机头 -->
          <circle cx="20" cy="40" r="5" fill="#333647"/>
          <!-- 尾翼 -->
          <path 
            d="M30 30 L25 40 L30 50" 
            fill="none" 
            stroke="#333647" 
            stroke-width="3" 
            stroke-linecap="round" 
            stroke-linejoin="round"
          />
          <!-- 尾部粒子效果 -->
          <g class="airplane-particles">
            <circle cx="15" cy="35" r="1" fill="url(#airplaneGradient)"/>
            <circle cx="12" cy="40" r="1" fill="url(#airplaneGradient)"/>
            <circle cx="15" cy="45" r="1" fill="url(#airplaneGradient)"/>
          </g>
        </g>
        <!-- 渐变定义 -->
        <defs>
          <linearGradient id="airplaneGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" :style="{ stopColor: '#7A86E8' }" />
            <stop offset="100%" :style="{ stopColor: '#82E2FF' }" />
          </linearGradient>
        </defs>
      </svg>
    </div>
    
    <!-- 2048图标 -->
    <div v-else-if="type === '2048'" class="icon-content">
      <svg width="100%" height="100%" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
        <!-- 2x2网格 -->
        <g class="grid-2048">
          <!-- 左上角方块 -->
          <rect x="20" y="20" width="15" height="15" fill="none" stroke="#333647" stroke-width="3" rx="3"/>
          <!-- 右上角方块 -->
          <rect x="45" y="20" width="15" height="15" fill="none" stroke="#333647" stroke-width="3" rx="3"/>
          <!-- 左下角方块 -->
          <rect x="20" y="45" width="15" height="15" fill="none" stroke="#333647" stroke-width="3" rx="3"/>
          <!-- 右下角方块（填充渐变） -->
          <rect 
            x="45" 
            y="45" 
            width="15" 
            height="15" 
            fill="url(#2048Gradient)" 
            stroke="#333647" 
            stroke-width="3" 
            rx="3"
            class="grid-cell"
          />
          <!-- 数字2048 -->
          <text 
            x="40" 
            y="55" 
            font-size="12" 
            font-weight="bold" 
            fill="#333647" 
            text-anchor="middle" 
            dominant-baseline="middle"
            class="number-2048"
          >
            2048
          </text>
        </g>
        <!-- 渐变定义 -->
        <defs>
          <linearGradient id="2048Gradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" :style="{ stopColor: '#7A86E8' }" />
            <stop offset="100%" :style="{ stopColor: '#82E2FF' }" />
          </linearGradient>
        </defs>
      </svg>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

// 定义组件props
const props = defineProps({
  // 图标类型：sweeper, snake, airplane, 2048
  type: {
    type: String,
    required: true,
    validator: (value) => {
      return ['sweeper', 'snake', 'airplane', '2048'].includes(value)
    }
  },
  // 图标尺寸（默认80px）
  size: {
    type: Number,
    default: 80
  },
  // 是否可悬停（默认true）
  hoverable: {
    type: Boolean,
    default: true
  },
  // 圆角大小（默认20px）
  borderRadius: {
    type: Number,
    default: 20
  }
})
</script>

<style scoped>
.game-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--secondary-bg);
  border-radius: 20px;
  transition: all var(--transition-normal) ease;
  position: relative;
  overflow: hidden;
}

.game-icon-hover:hover {
  transform: scale(1.05);
  box-shadow: 0 0 0 1px var(--gradient-primary), 0 4px 12px rgba(0, 0, 0, 0.08);
}

.icon-content {
  width: 60%;
  height: 60%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

/* 扫雷图标动效 */
.sweeper-fuse {
  animation: sweeperFuse 2s infinite linear;
  transform-origin: center;
}

@keyframes sweeperFuse {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.game-icon-hover:hover .sweeper-fuse {
  animation-duration: 1s;
}

/* 贪吃蛇图标动效 */
.snake-body {
  animation: snakeMove 3s infinite ease-in-out;
}

@keyframes snakeMove {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(-5px);
  }
}

.snake-segment {
  animation: snakeSegment 3s infinite ease-in-out;
}

.snake-segment:nth-child(3) {
  animation-delay: 0.2s;
}

.snake-segment:nth-child(4) {
  animation-delay: 0.4s;
}

.snake-segment:nth-child(5) {
  animation-delay: 0.6s;
}

@keyframes snakeSegment {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.snake-eye {
  animation: snakeEye 1s infinite ease-in-out;
}

@keyframes snakeEye {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

/* 打飞机图标动效 */
.airplane-wing {
  animation: airplaneWing 1s infinite ease-in-out;
  transform-origin: left center;
}

@keyframes airplaneWing {
  0%, 100% {
    transform: rotate(0deg);
  }
  50% {
    transform: rotate(5deg);
  }
}

.airplane-particles {
  animation: airplaneParticles 1s infinite ease-in-out;
}

@keyframes airplaneParticles {
  0% {
    opacity: 0;
    transform: translateX(0);
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
    transform: translateX(-10px);
  }
}

/* 2048图标动效 */
.grid-cell {
  animation: gridCellPulse 2s infinite ease-in-out;
}

@keyframes gridCellPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.number-2048 {
  opacity: 0;
  animation: numberAppear 3s infinite ease-in-out;
}

@keyframes numberAppear {
  0%, 60% {
    opacity: 0;
    transform: scale(0.8);
  }
  80%, 100% {
    opacity: 1;
    transform: scale(1);
  }
}

/* 通用SVG样式 */
svg {
  transition: all var(--transition-normal) ease;
}

.game-icon-hover:hover svg {
  filter: drop-shadow(0 0 4px rgba(122, 134, 232, 0.3));
}
</style>