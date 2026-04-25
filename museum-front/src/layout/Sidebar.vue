<template>
  <el-menu
    :default-active="$route.path"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409EFF"
    router
  >
    <template v-for="item in visibleNavItems" :key="item.key">
      <el-sub-menu v-if="item.children?.length" :index="item.index">
        <template #title>
          <el-icon><component :is="iconMap[item.icon]" /></el-icon>
          <span>{{ item.title }}</span>
        </template>
        <el-menu-item
          v-for="child in item.children"
          :key="child.key"
          :index="child.index"
        >
          {{ child.title }}
        </el-menu-item>
      </el-sub-menu>

      <el-menu-item v-else :index="item.index">
        <el-icon><component :is="iconMap[item.icon]" /></el-icon>
        <span>{{ item.title }}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
defineOptions({
  name: 'AppSidebar'
})

import { computed } from 'vue'
import { Box, Calendar, Collection, DataAnalysis, Document, List, Operation, User } from '@element-plus/icons-vue'
import { SIDEBAR_NAV_ITEMS } from '@/config/navigation'
import { hasPermission } from '@/utils/permission'

const iconMap = {
  DataAnalysis,
  Collection,
  Operation,
  Box,
  Calendar,
  Document,
  User,
  List
}

const canAccess = (item) => hasPermission(item.permission)

const visibleNavItems = computed(() =>
  SIDEBAR_NAV_ITEMS
    .map((item) => {
      if (!item.children?.length) {
        return canAccess(item) ? item : null
      }

      const children = item.children.filter((child) => canAccess(child))
      return canAccess(item) && children.length ? { ...item, children } : null
    })
    .filter(Boolean)
)
</script>
