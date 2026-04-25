<template>
  <el-card shadow="never" class="chart-card">
    <div class="chart-header">
      <div>
        <div class="title">{{ title }}</div>
        <div class="subtitle">{{ subtitle }}</div>
      </div>
      <div class="meta">{{ note }}</div>
    </div>
    <div class="chart-body">
      <slot />
    </div>
  </el-card>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
  subtitle: { type: String, default: '' },
  note: { type: String, default: '' }
})
</script>

<style scoped>
.chart-card {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}
.title {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
}
.subtitle {
  margin-top: 8px;
  color: #c9bea7;
  font-size: 13px;
}
.meta {
  color: #9f9370;
  font-size: 12px;
}
.chart-body {
  min-height: 280px;
}
</style>
