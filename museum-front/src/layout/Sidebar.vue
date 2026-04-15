<template>
  <el-menu
    :default-active="$route.path"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409EFF"
    router
  >
    <el-menu-item index="/artifact/list">
      <el-icon><Collection /></el-icon>
      <span>文物管理</span>
    </el-menu-item>
    <el-menu-item index="/user/list">
      <el-icon><User /></el-icon>
      <span>用户管理</span>
    </el-menu-item>
    <el-menu-item index="/role/list">
      <el-icon><List /></el-icon>
      <span>角色管理</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { Collection, User, List } from '@element-plus/icons-vue'
</script>