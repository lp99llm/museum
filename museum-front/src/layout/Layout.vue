<template>
  <el-container class="layout-main page-shell">
    <div class="route-progress" :style="{ transform: `scaleX(${progress / 100})` }" />

    <el-aside :width="isCollapsed ? '64px' : '256px'" class="aside-panel">
      <div class="brand-panel">
        <div class="brand-logo">博</div>
        <div v-if="!isCollapsed" class="brand-copy">
          <div class="brand-title">博物馆文物管理系统</div>
          <div class="brand-subtitle">Cultural Heritage Console</div>
        </div>
      </div>

      <div v-if="!isCollapsed" class="nav-section-label">核心功能</div>

      <el-menu
        :default-active="$route.path"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="transparent"
        text-color="var(--museum-text-inverse-secondary)"
        active-text-color="#ffffff"
        router
        unique-opened
      >
        <el-menu-item v-for="item in mainMenu" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.label }}</template>
        </el-menu-item>

        <el-sub-menu v-if="workflowMenu.length" index="workflow">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>流程管理</span>
          </template>
          <el-menu-item v-for="item in workflowMenu" :key="item.path" :index="item.path">
            {{ item.label }}
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="advancedMenu.length" index="more">
          <template #title>
            <el-icon><MoreFilled /></el-icon>
            <span>更多</span>
          </template>
          <el-menu-item v-for="item in advancedMenu" :key="item.path" :index="item.path">
            {{ item.label }}
          </el-menu-item>
        </el-sub-menu>
      </el-menu>

      <div class="sidebar-footer">
        <button class="collapse-button" type="button" @click="isCollapsed = !isCollapsed">
          <el-icon>
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
          <span v-if="!isCollapsed">收起导航</span>
        </button>

        <div class="user-card" :class="{ compact: isCollapsed }">
          <el-avatar class="user-avatar">馆</el-avatar>
          <div v-if="!isCollapsed" class="user-copy">
            <strong>{{ currentUser?.username || '管理员' }}</strong>
            <span>{{ currentUser?.role || '系统管理员' }}</span>
          </div>
        </div>
      </div>
    </el-aside>

    <el-container class="content-shell">
      <TopHeader
        :page-title="activeTab?.title || '工作台'"
        :is-dark="themeMode === 'dark'"
        @toggle-theme="toggleTheme"
      />

      <div class="workspace-tabs page-card">
        <div class="tabs-scroll">
          <button
            v-for="tab in tabs"
            :key="tab.path"
            type="button"
            class="workspace-tab"
            :class="{ active: tab.path === route.fullPath }"
            @click="goTab(tab.path)"
            @contextmenu.prevent="openTabContextMenu($event, tab)"
          >
            <span>{{ tab.title }}</span>
            <el-icon
              v-if="tab.closable"
              class="tab-close"
              @click.stop="closeTab(tab.path)"
            >
              <Close />
            </el-icon>
          </button>
        </div>
      </div>

      <el-main class="page-content">
        <div class="page-enter">
          <router-view />
        </div>
      </el-main>
    </el-container>

    <transition name="fade">
      <div
        v-if="contextMenu.visible"
        class="tab-context-menu"
        :style="{ left: `${contextMenu.x}px`, top: `${contextMenu.y}px` }"
      >
        <button type="button" @click="refreshCurrentTab">刷新页面</button>
        <button type="button" @click="closeTab(contextMenu.tab?.path)">关闭当前</button>
        <button type="button" @click="closeOtherTabs(contextMenu.tab?.path)">关闭其他</button>
      </div>
    </transition>

    <el-backtop :right="32" :bottom="32" />
  </el-container>
</template>

<script setup>
defineOptions({
  name: 'AppLayout'
})

import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TopHeader from '@/components/TopHeader.vue'
import { PERMISSIONS } from '@/constants/permissions'
import { hasPermission } from '@/utils/permission'
import {
  Close,
  Collection,
  DataAnalysis,
  Document,
  Expand,
  Fold,
  Histogram,
  MoreFilled,
  Picture,
  Setting,
  User
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const isCollapsed = ref(false)
const progress = ref(0)
const themeMode = ref(document.documentElement.dataset.themeMode || 'light')

const currentUser = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('user') || 'null')
  } catch {
    return null
  }
})

const filterByPermission = (items) => items.filter((item) => !item.permission || hasPermission(item.permission))

const mainMenu = computed(() =>
  filterByPermission([
    { path: '/dashboard', label: '工作台', icon: DataAnalysis },
    { path: '/artifacts', label: '文物管理', icon: Collection, permission: PERMISSIONS.ARTIFACT_VIEW },
    { path: '/exhibitions', label: '展览管理', icon: Picture, permission: PERMISSIONS.EXHIBITION_VIEW },
    { path: '/analytics', label: '数据分析', icon: Histogram, permission: PERMISSIONS.STATISTICS_VIEW }
  ])
)

const workflowMenu = computed(() =>
  filterByPermission([
    { path: '/acquisition/list', label: '征集登记', permission: 'acquisition:view' },
    { path: '/appraisal/list', label: '鉴定评估', permission: 'appraisal:view' },
    { path: '/warehousing/list', label: '入库管理', permission: 'warehousing:view' },
    { path: '/storage-check/list', label: '在库保管', permission: PERMISSIONS.ARTIFACT_VIEW },
    { path: '/outbound/list', label: '展览出库', permission: 'outbound:view' },
    { path: '/restoration/list', label: '修复记录', permission: 'restoration:view' },
    { path: '/loan/list', label: '外借管理', permission: 'loan:view' },
    { path: '/disposal/list', label: '出馆处置', permission: 'disposal:view' }
  ])
)

const advancedMenu = computed(() =>
  filterByPermission([
    { path: '/system/user', label: '用户管理', icon: User, permission: PERMISSIONS.USER_VIEW },
    { path: '/system/role', label: '角色管理', icon: User, permission: PERMISSIONS.ROLE_VIEW },
    { path: '/system/log', label: '操作审计', icon: Setting, permission: PERMISSIONS.OPERATION_LOG_VIEW },
    { path: '/system/security', label: '安全中心', icon: Setting, permission: PERMISSIONS.SECURITY_VIEW }
  ])
)

const tabs = ref([])
const contextMenu = reactive({
  visible: false,
  x: 0,
  y: 0,
  tab: null
})

const activeTab = computed(() => tabs.value.find((item) => item.path === route.fullPath))

const syncTab = () => {
  if (route.path === '/login') return
  const title = route.meta?.title || '未命名页面'
  const existing = tabs.value.find((item) => item.path === route.fullPath)
  if (!existing) {
    tabs.value.push({
      path: route.fullPath,
      title,
      closable: route.fullPath !== '/dashboard'
    })
  }
}

const goTab = (path) => {
  hideContextMenu()
  router.push(path)
}

const closeTab = (path) => {
  hideContextMenu()
  const targetPath = path || route.fullPath
  const index = tabs.value.findIndex((item) => item.path === targetPath)
  if (index < 0) return

  const isActive = route.fullPath === targetPath
  tabs.value.splice(index, 1)

  if (!tabs.value.length) {
    tabs.value.push({ path: '/dashboard', title: '工作台', closable: false })
  }

  if (isActive) {
    const fallback = tabs.value[index - 1] || tabs.value[index] || tabs.value[0]
    if (fallback) router.push(fallback.path)
  }
}

const closeOtherTabs = (path) => {
  hideContextMenu()
  const keepPath = path || route.fullPath
  tabs.value = tabs.value.filter((item) => item.path === keepPath || item.path === '/dashboard')
  if (route.fullPath !== keepPath) {
    router.push(keepPath)
  }
}

const refreshCurrentTab = async () => {
  hideContextMenu()
  const current = route.fullPath
  await router.replace('/dashboard')
  await nextTick()
  router.replace(current)
}

const openTabContextMenu = (event, tab) => {
  contextMenu.visible = true
  contextMenu.x = event.clientX
  contextMenu.y = event.clientY
  contextMenu.tab = tab
}

const hideContextMenu = () => {
  contextMenu.visible = false
  contextMenu.tab = null
}

const startProgress = () => {
  progress.value = 18
  window.clearInterval(startProgress.timer)
  startProgress.timer = window.setInterval(() => {
    if (progress.value < 82) {
      progress.value += 9
    }
  }, 120)
}
startProgress.timer = null

const finishProgress = () => {
  window.clearInterval(startProgress.timer)
  progress.value = 100
  window.setTimeout(() => {
    progress.value = 0
  }, 220)
}

const applyTheme = (mode) => {
  themeMode.value = mode
  document.documentElement.dataset.themeMode = mode
  document.documentElement.style.colorScheme = mode
}

const toggleTheme = () => {
  applyTheme(themeMode.value === 'dark' ? 'light' : 'dark')
}

watch(() => route.fullPath, syncTab, { immediate: true })

const handleStart = () => startProgress()
const handleEnd = () => finishProgress()
const handleClick = () => hideContextMenu()
const handleKeydown = (event) => {
  if (event.key === 'Escape') hideContextMenu()
}

onMounted(() => {
  window.addEventListener('museum:navigation:start', handleStart)
  window.addEventListener('museum:navigation:end', handleEnd)
  window.addEventListener('click', handleClick)
  window.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('museum:navigation:start', handleStart)
  window.removeEventListener('museum:navigation:end', handleEnd)
  window.removeEventListener('click', handleClick)
  window.removeEventListener('keydown', handleKeydown)
  window.clearInterval(startProgress.timer)
})
</script>

<style scoped>
.layout-main {
  min-height: 100vh;
  background: transparent;
}

.route-progress {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 70;
  width: 100%;
  height: 3px;
  transform-origin: left center;
  background: linear-gradient(90deg, var(--museum-color-accent), var(--museum-color-primary));
  box-shadow: 0 0 18px rgba(212, 160, 23, 0.32);
  transition: transform 180ms ease;
}

.aside-panel {
  width: var(--museum-layout-sidebar-width);
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #173131 0%, #102626 100%);
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  padding: var(--museum-space-24) var(--museum-space-12) var(--museum-space-16);
  overflow: hidden;
}

.brand-panel {
  display: flex;
  align-items: center;
  gap: var(--museum-space-12);
  margin-bottom: var(--museum-space-24);
  padding: var(--museum-space-12);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-logo {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff7df;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, rgba(212, 160, 23, 0.92), rgba(156, 117, 16, 0.92));
}

.brand-title {
  color: #fff;
  font-size: 15px;
  font-weight: 600;
}

.brand-subtitle {
  color: rgba(255, 255, 255, 0.58);
  font-size: 12px;
  margin-top: 2px;
}

.nav-section-label {
  margin: 0 0 var(--museum-space-12);
  padding: 0 var(--museum-space-12);
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.42);
}

:deep(.el-menu) {
  flex: 1;
  border-inline-end: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 44px;
  line-height: 44px;
  margin-bottom: var(--museum-space-8);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(212, 160, 23, 0.24), rgba(212, 160, 23, 0.06)) !important;
  box-shadow: inset 0 0 0 1px rgba(212, 160, 23, 0.24);
}

.sidebar-footer {
  display: grid;
  gap: var(--museum-space-12);
  margin-top: var(--museum-space-16);
}

.collapse-button {
  height: 40px;
  border: 0;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.78);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.user-card.compact {
  justify-content: center;
}

.user-avatar {
  background: linear-gradient(135deg, var(--museum-color-accent), #9d7510);
  color: #fff;
}

.user-copy {
  min-width: 0;
  display: grid;
}

.user-copy strong {
  color: #fff;
  font-size: 14px;
}

.user-copy span {
  color: rgba(255, 255, 255, 0.58);
  font-size: 12px;
}

.content-shell {
  min-width: 0;
}

.workspace-tabs {
  margin: 0 var(--museum-space-24);
  padding: 8px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.86);
}

:global(:root[data-theme-mode='dark']) .workspace-tabs {
  background: rgba(31, 31, 31, 0.9);
}

.tabs-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
}

.workspace-tab {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 36px;
  padding: 0 14px;
  border: 1px solid transparent;
  border-radius: 10px;
  background: transparent;
  color: var(--museum-text-secondary);
  cursor: pointer;
  white-space: nowrap;
}

.workspace-tab.active {
  color: var(--museum-text-strong);
  background: var(--museum-bg-subtle);
  border-color: var(--museum-border-light);
}

.tab-close {
  font-size: 12px;
}

.page-content {
  min-height: calc(100vh - var(--museum-layout-header-height) - 54px);
  padding: var(--museum-space-24);
}

.tab-context-menu {
  position: fixed;
  z-index: 80;
  min-width: 140px;
  display: grid;
  gap: 4px;
  padding: 8px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--museum-border-light);
  box-shadow: var(--museum-shadow-popover);
}

.tab-context-menu button {
  border: 0;
  background: transparent;
  text-align: left;
  padding: 8px 10px;
  border-radius: 8px;
  color: var(--museum-text-primary);
  cursor: pointer;
}

.tab-context-menu button:hover {
  background: var(--museum-bg-subtle);
}

@media (max-width: 960px) {
  .workspace-tabs {
    margin: 0 var(--museum-space-16);
  }

  .page-content {
    padding: var(--museum-space-16);
  }
}
</style>
