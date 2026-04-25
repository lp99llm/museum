<template>
  <el-container class="layout-main">
    <el-aside width="260px" class="aside-panel">
      <div class="brand-panel">
        <div class="brand-logo">博</div>
        <div class="brand-text">
          <div class="brand-title">博物馆文物管理系统</div>
          <div class="brand-subtitle">馆藏 · 流程 · 保护</div>
        </div>
      </div>
      <SidebarMenu :menu="menuItems" />
    </el-aside>

    <el-container>
      <TopHeader />
      <el-main class="page-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import SidebarMenu from '@/components/SidebarMenu.vue'
import TopHeader from '@/components/TopHeader.vue'

const menuItems = [
  { path: '/dashboard', label: '首页仪表盘', icon: 'Collection' },
  { path: '/artifacts', label: '文物信息管理', icon: 'Document' },
  { path: '/process', label: '文物流程管理', icon: 'Ticket' },
  { path: '/exhibitions', label: '展览管理', icon: 'Calendar' },
  { path: '/restoration', label: '修复管理', icon: 'Tools' },
  { path: '/statistics', label: '数据统计分析', icon: 'DataLine' },
  { path: '/users', label: '用户与角色权限', icon: 'User' },
  { path: '/settings', label: '系统设置', icon: 'Setting' }
]
</script>

<style scoped>
.layout-main {
  min-height: 100vh;
  background: linear-gradient(180deg, #11110d 0%, #1c1a16 100%);
}

.aside-panel {
  background: #1c1913;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  padding: 24px 16px 16px;
}

.brand-panel {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
  padding: 14px;
  border-radius: 16px;
  background: rgba(255, 248, 235, 0.05);
}

.brand-logo {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: rgba(181, 133, 76, 0.16);
  color: #f8e7c2;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  font-weight: 700;
}

.brand-text {
  color: #f5ecd8;
}

.brand-title {
  font-size: 16px;
  font-weight: 700;
}

.brand-subtitle {
  margin-top: 6px;
  color: #c9bea7;
  font-size: 12px;
}

.page-content {
  min-height: 100vh;
  padding: 24px 24px 32px;
  background: linear-gradient(180deg, rgba(250, 246, 238, 0.08), rgba(19, 18, 15, 0.08));
}
</style>
