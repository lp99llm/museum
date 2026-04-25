<template>
  <div class="top-header">
    <div class="header-left">
      <div class="page-title">{{ pageTitle }}</div>
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item @click="goDashboard">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-for="item in crumbs" :key="item.path">
          {{ item.label }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="header-right">
      <el-input
        v-model="search"
        placeholder="搜索文物、流程、展览或操作记录"
        class="header-search"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-tooltip content="切换主题" placement="bottom">
        <el-button circle @click="$emit('toggle-theme')">
          <el-icon>
            <Moon v-if="!isDark" />
            <Sunny v-else />
          </el-icon>
        </el-button>
      </el-tooltip>

      <el-popover trigger="click" placement="bottom-end" width="340" v-model:visible="notificationsVisible">
        <div class="notification-panel">
          <div class="panel-header">
            <span>消息通知</span>
            <el-button text size="small" @click="markAllRead">全部标为已读</el-button>
          </div>

          <div class="notification-list">
            <div
              v-for="item in notifications"
              :key="item.id"
              class="notification-item"
              :class="{ unread: !item.read }"
              @click="openNotification(item)"
            >
              <div class="notification-title">{{ item.title }}</div>
              <div class="notification-time">{{ item.time }}</div>
              <div class="notification-desc">{{ item.content }}</div>
            </div>
            <div v-if="notifications.length === 0" class="empty-state">暂无消息</div>
          </div>
        </div>

        <template #reference>
          <el-badge :value="unreadCount" class="message-badge">
            <el-button circle>
              <el-icon><Bell /></el-icon>
            </el-button>
          </el-badge>
        </template>
      </el-popover>

      <el-dropdown class="user-dropdown" trigger="click">
        <span class="dropdown-link">
          <el-avatar size="small" class="user-avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <span class="user-name">管理员</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>个人中心</el-dropdown-item>
            <el-dropdown-item>系统设置</el-dropdown-item>
            <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>

  <el-dialog v-model="notificationDialogVisible" title="消息详情" width="420px">
    <div v-if="selectedNotification">
      <p><strong>{{ selectedNotification.title }}</strong></p>
      <p class="dialog-time">{{ selectedNotification.time }}</p>
      <p class="dialog-content">{{ selectedNotification.content }}</p>
    </div>
    <template #footer>
      <el-button type="primary" @click="notificationDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Moon, Search, Sunny, User } from '@element-plus/icons-vue'

defineProps({
  pageTitle: {
    type: String,
    default: '工作台'
  },
  isDark: {
    type: Boolean,
    default: false
  }
})

defineEmits(['toggle-theme'])

const route = useRoute()
const router = useRouter()

const search = ref('')
const notificationsVisible = ref(false)
const notificationDialogVisible = ref(false)
const selectedNotification = ref(null)

const notifications = ref([
  { id: 1, title: '修复申请已提交', content: '宋代书画修复申请已提交，等待审批。', time: '刚刚', read: false },
  { id: 2, title: '展览布展完成', content: '“敦煌秘境”展览布展已完成，可以开始预展。', time: '1 小时前', read: false },
  { id: 3, title: '外借申请已通过', content: '“清代瓷器”外借申请已通过，请确认出库安排。', time: '昨天', read: true }
])

const crumbs = computed(() => {
  return route.matched
    .slice(1)
    .map((item) => ({ path: item.path, label: item.meta?.title || item.name }))
})

const unreadCount = computed(() => notifications.value.filter((item) => !item.read).length)

const goDashboard = () => {
  router.push('/dashboard')
}

const openNotification = (item) => {
  item.read = true
  selectedNotification.value = item
  notificationDialogVisible.value = true
}

const markAllRead = () => {
  notifications.value.forEach((item) => {
    item.read = true
  })
}

const logout = () => {
  localStorage.removeItem('token')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--museum-space-24);
  min-height: var(--museum-layout-header-height);
  padding: var(--museum-space-16) var(--museum-space-24);
  background: rgba(255, 255, 255, 0.74);
  border-bottom: 1px solid var(--museum-border-light);
  backdrop-filter: blur(20px);
}

:global(:root[data-theme-mode='dark']) .top-header {
  background: rgba(20, 20, 20, 0.86);
}

.header-left {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.page-title {
  font-size: 24px;
  line-height: 1.4;
  font-weight: 600;
  color: var(--museum-text-strong);
}

.breadcrumb {
  color: var(--museum-text-secondary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--museum-space-12);
}

.header-search {
  width: 360px;
}

.dropdown-link {
  display: inline-flex;
  align-items: center;
  gap: var(--museum-space-8);
  cursor: pointer;
  color: var(--museum-text-primary);
}

.user-avatar {
  background: linear-gradient(135deg, var(--museum-color-primary), var(--museum-color-primary-hover));
  color: #fff;
}

.notification-panel {
  display: grid;
  gap: 12px;
  padding: 8px 0;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
}

.notification-list {
  display: grid;
  gap: 8px;
  max-height: 280px;
  overflow-y: auto;
  padding: 0 16px;
}

.notification-item {
  padding: 12px;
  border-radius: var(--museum-radius-card);
  background: var(--museum-bg-subtle);
  cursor: pointer;
}

.notification-item.unread {
  border: 1px solid rgba(212, 160, 23, 0.3);
}

.notification-title {
  color: var(--museum-text-primary);
  font-weight: 500;
}

.notification-time,
.dialog-time {
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

.notification-desc,
.dialog-content {
  margin-top: 6px;
  color: var(--museum-text-secondary);
  line-height: 1.6;
}

.empty-state {
  color: var(--museum-text-tertiary);
  text-align: center;
  padding: 20px 0;
}
</style>
