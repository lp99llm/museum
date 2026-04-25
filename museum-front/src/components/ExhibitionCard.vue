<template>
  <article class="exhibition-card page-card">
    <div class="card-cover" :style="coverStyle">
      <div class="cover-mask" />
      <div class="cover-content">
        <div class="status-tag" :class="statusClass">{{ exhibition.status }}</div>
        <div class="cover-title-group">
          <h3>{{ exhibition.title }}</h3>
          <p>{{ exhibition.time }}</p>
        </div>
      </div>

      <transition name="fade">
        <div v-if="exhibition.previewArtifacts?.length" class="artifact-preview">
          <div class="preview-title">参展文物预览</div>
          <div class="preview-list">
            <div v-for="item in exhibition.previewArtifacts" :key="item.name" class="preview-item">
              <img :src="item.thumb" :alt="item.name">
              <span>{{ item.name }}</span>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <div class="card-body">
      <div class="meta-row">
        <span>展览地点</span>
        <strong>{{ exhibition.location }}</strong>
      </div>
      <div class="meta-row">
        <span>参展文物</span>
        <strong>{{ exhibition.artifactCount }} 件</strong>
      </div>
      <div class="meta-row">
        <span>状态</span>
        <strong>{{ exhibition.status }}</strong>
      </div>
    </div>

    <div class="card-footer">
      <el-button link @click="$emit('detail', exhibition.id)">详情</el-button>
      <el-button v-if="canEdit" link @click="$emit('edit', exhibition.id)">编辑</el-button>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { hasPermission } from '@/utils/permission'

const props = defineProps({
  exhibition: {
    type: Object,
    required: true
  }
})

defineEmits(['detail', 'edit'])

const coverStyle = computed(() => ({
  background: `linear-gradient(180deg, rgba(0,0,0,.1), rgba(0,0,0,.54)), url(${props.exhibition.cover}) center / cover no-repeat`
}))

const statusClass = computed(() => {
  if (props.exhibition.status === '进行中') return 'is-active'
  if (props.exhibition.status === '策划中') return 'is-planning'
  return 'is-ended'
})

const canEdit = computed(() => hasPermission('exhibition:edit'))
</script>

<style scoped>
.exhibition-card {
  overflow: hidden;
  border-radius: var(--museum-radius-card);
}

.card-cover {
  position: relative;
  min-height: 240px;
  overflow: hidden;
}

.cover-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.08), rgba(0, 0, 0, 0.68));
}

.cover-content {
  position: absolute;
  inset: 0;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 18px;
}

.status-tag {
  width: fit-content;
  padding: 7px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.status-tag.is-active {
  color: #fff;
  background: rgba(82, 196, 26, 0.82);
}

.status-tag.is-planning {
  color: #fff;
  background: rgba(250, 173, 20, 0.86);
}

.status-tag.is-ended {
  color: #fff;
  background: rgba(0, 0, 0, 0.45);
}

.cover-title-group h3 {
  margin: 0;
  color: #fff;
  font-size: 22px;
  line-height: 1.3;
}

.cover-title-group p {
  margin: 8px 0 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
}

.artifact-preview {
  position: absolute;
  inset: auto 16px 16px 16px;
  z-index: 2;
  padding: 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  box-shadow: var(--museum-shadow-popover);
}

.preview-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--museum-text-strong);
}

.preview-list {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-top: 12px;
}

.preview-item img {
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 10px;
  object-fit: cover;
  display: block;
}

.preview-item span {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: var(--museum-text-secondary);
}

.card-body {
  padding: 18px;
  display: grid;
  gap: 10px;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.meta-row span {
  color: var(--museum-text-tertiary);
}

.meta-row strong {
  color: var(--museum-text-strong);
}

.card-footer {
  display: flex;
  gap: 8px;
  padding: 0 18px 18px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 180ms ease, transform 180ms ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>
