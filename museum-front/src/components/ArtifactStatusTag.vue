<template>
  <el-tag :type="type" effect="dark">{{ label }}</el-tag>
</template>

<script setup>
import { computed } from 'vue'
import { getArtifactStateLabel, getArtifactStateType } from '@/utils/status'

const props = defineProps({
  status: { type: String, default: '' }
})

const label = computed(() => getArtifactStateLabel(props.status) || '未知')
const type = computed(() => getArtifactStateType(props.status) || 'info')
</script>

<style scoped>
.el-tag {
  border-radius: 999px;
  padding: 0 12px;
  font-size: 12px;
}
</style>
