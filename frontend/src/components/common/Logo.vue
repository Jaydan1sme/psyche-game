<template>
  <div class="logo-container">
    <div class="logo">
      <svg class="logo-icon" viewBox="0 0 24 24" width="24" height="24">
        <path d="M3 6h18v12H3V6z" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M7 10h2v4H7zm10 0h2v4h-2z" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M9 12h6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M12 9v6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span class="logo-text">Psyche</span>
    </div>
  </div>
</template>

<script setup>
// Logo组件
</script>

<style scoped>
.logo-container {
  width: 80px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logo-icon {
  color: var(--gradient-start);
  transition: all 0.3s ease;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--gradient-start);
  background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: all 0.3s ease;
}

.logo:hover .logo-text {
  animation: logoGlow 2s ease-in-out infinite;
}

@keyframes logoGlow {
  0%, 100% {
    background: linear-gradient(120deg, var(--gradient-start), var(--gradient-end));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  50% {
    background: linear-gradient(120deg, var(--gradient-end), var(--gradient-start));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    text-shadow: 0 0 10px rgba(122, 134, 232, 0.5);
  }
}
</style>