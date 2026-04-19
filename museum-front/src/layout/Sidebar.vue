<template>
  <el-menu
    :default-active="$route.path"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409EFF"
    router
  >
    <el-menu-item index="/dashboard">
      <el-icon><DataAnalysis /></el-icon>
      <span>数据统计</span>
    </el-menu-item>
    <el-menu-item index="/artifacts">
      <el-icon><Collection /></el-icon>
      <span>文物管理</span>
    </el-menu-item>
    <el-sub-menu index="workflow">
      <template #title>
        <el-icon><Operation /></el-icon>
        <span>流程管理</span>
      </template>
      <el-menu-item index="/acquisition/list">征集登记</el-menu-item>
      <el-menu-item index="/appraisal/list">鉴定评估</el-menu-item>
      <el-menu-item index="/warehousing/list">入库管理</el-menu-item>
      <el-menu-item index="/storage-check/list">在库保管</el-menu-item>
      <el-menu-item index="/outbound/list">展览出库</el-menu-item>
      <el-menu-item index="/restoration/list">修复记录</el-menu-item>
      <el-menu-item index="/loan/list">外借管理</el-menu-item>
      <el-menu-item index="/disposal/list">出馆处置</el-menu-item>
    </el-sub-menu>
    <el-menu-item index="/exhibitions">
      <el-icon><Box /></el-icon>
      <span>展览管理</span>
    </el-menu-item>
    <el-menu-item index="/visitor-appointments">
      <el-icon><Calendar /></el-icon>
      <span>预约管理</span>
    </el-menu-item>
    <el-menu-item index="/exhibition-evaluations">
      <el-icon><Document /></el-icon>
      <span>评估报告</span>
    </el-menu-item>
    <el-menu-item index="/users">
      <el-icon><User /></el-icon>
      <span>用户管理</span>
    </el-menu-item>
    <el-menu-item index="/roles">
      <el-icon><List /></el-icon>
      <span>角色管理</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { DataAnalysis, Collection, Box, Calendar, Document, User, List, Operation } from '@element-plus/icons-vue'
</script>
