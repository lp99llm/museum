<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted } from 'vue'
import { initializePermissions } from '@/utils/permission'

const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')

const applyThemeMode = () => {
  const isDark = mediaQuery.matches
  document.documentElement.dataset.themeMode = isDark ? 'dark' : 'light'
  document.documentElement.style.colorScheme = isDark ? 'dark' : 'light'
}

onMounted(() => {
  document.documentElement.dataset.theme = 'bronze-modern'
  applyThemeMode()
  initializePermissions().catch(() => {})
  mediaQuery.addEventListener('change', applyThemeMode)
})

onBeforeUnmount(() => {
  mediaQuery.removeEventListener('change', applyThemeMode)
})
</script>
