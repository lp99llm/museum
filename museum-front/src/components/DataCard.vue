<template>
  <el-card shadow="never" class="data-card">
    <div class="card-head">
      <div class="icon-box" :style="iconStyle">
        <component :is="icon" class="card-icon" />
      </div>
      <div class="title-group">
        <div class="title">{{ title }}</div>
        <div class="subtitle">{{ subtitle }}</div>
      </div>
    </div>
    <div class="card-body">
      <div class="value">{{ value }}</div>
      <div class="foot">{{ foot }}</div>
    </div>
  </el-card>
</template>

<script setup>
const props = defineProps({
  title: { type: String, default: '' },
  subtitle: { type: String, default: '' },
  value: { type: [String, Number], default: '--' },
  foot: { type: String, default: '' },
  icon: { type: Object, required: true },
  accent: { type: String, default: '#b58b4c' }
})

const iconStyle = {
  background: `rgba(181, 139, 76, 0.16)`,
  color: props.accent
}
</script>

<style scoped>
.data-card {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
}
.card-head {
  display: flex;
  align-items: center;
  gap: 14px;
}
.icon-box {
  width: 54px;
  height: 54px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.card-icon {
  font-size: 24px;
}
.title-group {
  display: flex;
  flex-direction: column;
}
.title {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}
.subtitle {
  margin-top: 6px;
  color: #c9bea7;
  font-size: 12px;
}
.card-body {
  margin-top: 18px;
}
.value {
  color: #fff;
  font-size: 32px;
  font-weight: 700;
}
.foot {
  margin-top: 8px;
  color: #b9ab8f;
  font-size: 12px;
}
</style>