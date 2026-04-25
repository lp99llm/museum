<template>
  <div class="timeline-panel">
    <div class="timeline-head">
      <span>{{ title }}</span>
      <span class="more" v-if="action">{{ action }}</span>
    </div>
    <div class="timeline-list">
      <div v-for="item in events" :key="item.time" class="timeline-item">
        <div class="timeline-dot" :class="item.status" />
        <div class="timeline-content">
          <div class="timeline-title">{{ item.title }}</div>
          <div class="timeline-desc">{{ item.description }}</div>
          <div class="timeline-meta">{{ item.time }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '时间线' },
  events: { type: Array, default: () => [] },
  action: { type: String, default: '' }
})
</script>

<style scoped>
.timeline-panel {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 22px;
}
.timeline-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  color: #f7eedb;
  font-weight: 600;
}
.more {
  color: #c9bea7;
  font-size: 12px;
}
.timeline-list {
  display: grid;
  gap: 18px;
}
.timeline-item {
  display: flex;
  gap: 14px;
}
.timeline-dot {
  width: 12px;
  min-width: 12px;
  height: 12px;
  border-radius: 999px;
  margin-top: 6px;
  background: #b58b4c;
}
.timeline-dot.completed { background: #4caf50; }
.timeline-dot.processing { background: #ff9800; }
.timeline-dot.pending { background: #f56c6c; }
.timeline-content {
  flex: 1;
}
.timeline-title {
  color: #fff;
  font-weight: 600;
}
.timeline-desc {
  margin-top: 6px;
  color: #c9bea7;
  font-size: 13px;
}
.timeline-meta {
  margin-top: 8px;
  color: #9f9370;
  font-size: 12px;
}
</style>
