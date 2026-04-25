<template>
  <article class="artifact-card page-card" @click="emit('view', artifact.id)">
    <div class="artifact-media">
      <img :src="artifact.thumb" :alt="artifact.name" class="artifact-image">
      <div class="artifact-overlay">
        <button class="more-button" type="button" @click.stop="toggleActions">
          <el-icon><MoreFilled /></el-icon>
        </button>
      </div>

      <transition name="fade">
        <div v-if="actionsVisible" class="quick-actions" @click.stop>
          <button type="button" @click="handleAction('edit')">快速编辑</button>
        </div>
      </transition>
    </div>

    <div class="artifact-body">
      <div class="artifact-header">
        <div>
          <h3>{{ artifact.name }}</h3>
          <p class="artifact-code">{{ artifact.code }}</p>
        </div>
        <div class="status-dot" :class="statusClass">
          <span />
          <span>{{ preservationStatusLabel }}</span>
        </div>
      </div>

      <div class="artifact-meta">
        <span>{{ artifact.era }}</span>
        <span>·</span>
        <span>{{ artifact.category }}</span>
      </div>

      <div class="artifact-tags">
        <el-tag size="small" effect="plain">{{ artifact.location }}</el-tag>
        <el-tag size="small" effect="plain" type="info">{{ appraisalLevelLabel }}</el-tag>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed, ref } from 'vue'
import { MoreFilled } from '@element-plus/icons-vue'
import {
  getAppraisalLevelLabel,
  normalizePreservationStatus
} from '@/utils/status'

const props = defineProps({
  artifact: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['view', 'edit'])

const actionsVisible = ref(false)

const preservationStatus = computed(() => normalizePreservationStatus(props.artifact.preservationStatus))
const preservationStatusLabel = computed(() => preservationStatus.value || props.artifact.preservationStatus || '未知')
const appraisalLevelLabel = computed(() => getAppraisalLevelLabel(props.artifact.appraisalLevel))

const statusClass = computed(() => {
  if (preservationStatus.value === '完好') return 'is-good'
  if (preservationStatus.value === '轻微损伤') return 'is-warning'
  return 'is-danger'
})

const toggleActions = () => {
  actionsVisible.value = !actionsVisible.value
}

const handleAction = (action) => {
  actionsVisible.value = false
  if (action === 'edit') emit('edit', props.artifact.id)
}
</script>

<style scoped>
.artifact-card {
  position: relative;
  overflow: hidden;
  cursor: pointer;
  border-radius: var(--museum-radius-card);
}

.artifact-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.14);
}

.artifact-media {
  position: relative;
  aspect-ratio: 4 / 3;
  overflow: hidden;
  background: var(--museum-bg-subtle);
}

.artifact-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.artifact-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  padding: var(--museum-space-12);
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.18), transparent 35%);
}

.more-button {
  width: 32px;
  height: 32px;
  border: 0;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.88);
  color: var(--museum-color-primary);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.quick-actions {
  position: absolute;
  top: 52px;
  right: 12px;
  display: grid;
  gap: 4px;
  padding: 8px;
  min-width: 136px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--museum-border-light);
  box-shadow: var(--museum-shadow-popover);
}

.quick-actions button {
  border: 0;
  background: transparent;
  text-align: left;
  padding: 8px 10px;
  border-radius: 8px;
  color: var(--museum-text-primary);
  cursor: pointer;
}

.quick-actions button:hover {
  background: var(--museum-bg-subtle);
}

.artifact-body {
  padding: var(--museum-space-16);
}

.artifact-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--museum-space-12);
}

.artifact-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--museum-text-strong);
}

.artifact-code {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--museum-text-tertiary);
}

.artifact-meta {
  margin-top: 10px;
  color: var(--museum-text-secondary);
  font-size: 13px;
}

.artifact-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 12px;
}

.status-dot {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--museum-text-secondary);
  white-space: nowrap;
}

.status-dot span:first-child {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
}

.status-dot.is-good {
  color: var(--museum-color-success);
}

.status-dot.is-warning {
  color: var(--museum-color-warning);
}

.status-dot.is-danger {
  color: var(--museum-color-error);
}
</style>
