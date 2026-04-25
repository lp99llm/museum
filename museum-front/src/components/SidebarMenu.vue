<template>
  <el-menu
    class="sidebar-menu"
    :default-active="active"
    router
    background-color="transparent"
    text-color="#d7c9a8"
    active-text-color="#fff"
    unique-opened
  >
    <el-menu-item
      v-for="item in menu"
      :key="item.path"
      :index="item.path"
    >
      <component :is="getIcon(item.icon)" class="menu-icon" />
      <span>{{ item.label }}</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  Collection,
  Document,
  Ticket,
  Calendar,
  Tools,
  DataLine,
  User,
  Setting
} from '@element-plus/icons-vue'

defineProps({
  menu: { type: Array, default: () => [] }
})

const route = useRoute()
const active = computed(() => route.path)

const icons = {
  Collection,
  Document,
  Ticket,
  Calendar,
  Tools,
  DataLine,
  User,
  Setting
}

const getIcon = (name) => icons[name] || Document
</script>

<style scoped>
.sidebar-menu {
  border-right: none;
  width: 100%;
  background: transparent;
}
.menu-icon {
  font-size: 18px;
  margin-right: 10px;
}
.el-menu-item {
  margin-bottom: 6px;
  border-radius: 14px;
}
.el-menu-item.is-active {
  background: rgba(214, 170, 81, 0.18);
}
.el-menu-item:hover {
  background: rgba(214, 170, 81, 0.1);
}
</style>
