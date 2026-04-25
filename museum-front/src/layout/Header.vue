<template>
  <div class="header-right">
    <el-dropdown @command="handleCommand">
      <span class="user-info">
        {{ userStore.user?.realName || userStore.user?.username }}
        <el-icon><ArrowDown /></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
defineOptions({
  name: 'AppHeader'
})

import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.header-right {
  flex: 1;
  text-align: right;
}

.user-info {
  cursor: pointer;
}
</style>
