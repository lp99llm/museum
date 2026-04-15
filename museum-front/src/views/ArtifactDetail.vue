<template>
  <div v-loading="loading" class="artifact-detail">
    <el-card v-if="detail">
      <template #header>
        <div class="card-header">
          <span>文物详情 - {{ detail.name }}</span>
          <div>
            <el-button type="primary" @click="edit">编辑</el-button>
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="文物编号">{{ detail.code || '-' }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="类别">{{ detail.category }}</el-descriptions-item>
        <el-descriptions-item label="年代">{{ detail.dynasty }}</el-descriptions-item>
        <el-descriptions-item label="出土地点">{{ detail.location }}</el-descriptions-item>
        <el-descriptions-item label="现藏地点">{{ detail.currentLocation || detail.location }}</el-descriptions-item>
        <el-descriptions-item label="文物简介" :span="2">{{ detail.description || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { artifactApi } from '@/api/artifact'

const route = useRoute()
const router = useRouter()
const detail = ref(null)
const loading = ref(false)

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await artifactApi.getDetail(route.params.id)
    detail.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const edit = () => router.push(`/artifacts/${route.params.id}/edit`)
const goBack = () => router.push('/artifacts')

onMounted(() => fetchDetail())
</script>

<style scoped>
.artifact-detail { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>