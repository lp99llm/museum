<template>
  <div class="empty-state">
    <div class="icon">文</div>
    <div class="title">暂无数据</div>
    <div class="description">当前区域尚未配置内容，建议调整筛选条件或稍后再试。</div>
  </div>
</template>

<script setup></script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 280px;
  color: #c9bea7;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 20px;
}
.icon {
  width: 72px;
  height: 72px;
  border-radius: 18px;
  background: rgba(181, 139, 76, 0.17);
  color: #f7e8c2;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-bottom: 18px;
}
.title {
  font-size: 18px;
  color: #fff;
  font-weight: 700;
}
.description {
  margin-top: 10px;
  font-size: 13px;
  color: #b8a985;
}
</style>