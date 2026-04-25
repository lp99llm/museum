<template>
  <div class="exhibition-page">
    <div class="page-header">
      <div>
        <h2>展览信息管理</h2>
        <p>支持按状态和时间范围筛选展览，并提供卡片、列表、日历三种浏览方式。</p>
      </div>
      <div class="header-actions">
        <el-button v-if="canAdd" type="primary" @click="goCreate">新增展览</el-button>
        <div class="view-switch">
          <el-button :type="viewMode === 'card' ? 'primary' : 'default'" @click="viewMode = 'card'">卡片</el-button>
          <el-button :type="viewMode === 'table' ? 'primary' : 'default'" @click="viewMode = 'table'">列表</el-button>
          <el-button :type="viewMode === 'calendar' ? 'primary' : 'default'" @click="viewMode = 'calendar'">日历</el-button>
        </div>
      </div>
    </div>

    <el-card class="filter-card">
      <div class="filter-grid">
        <el-form-item label="展览名称">
          <el-input v-model="filters.keyword" placeholder="请输入展览名称" clearable />
        </el-form-item>
        <el-form-item label="展览状态">
          <el-select v-model="filters.status" placeholder="请选择状态" clearable>
            <el-option label="策划中" value="PLANNING" />
            <el-option label="进行中" value="ONGOING" />
            <el-option label="已结束" value="FINISHED" />
          </el-select>
        </el-form-item>
        <el-form-item label="展览时间范围">
          <el-date-picker
            v-model="filters.range"
            type="daterange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </div>

      <div class="filter-actions">
        <el-button type="primary" :loading="loading" @click="loadExhibitions(1)">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </div>
    </el-card>

    <section v-if="viewMode === 'card'" v-loading="loading" class="card-grid">
      <ExhibitionCard
        v-for="item in exhibitions"
        :key="item.id"
        :exhibition="item"
        @detail="goDetail"
        @edit="goEdit"
      />
      <el-empty v-if="!loading && !exhibitions.length" description="暂无展览数据" />
    </section>

    <el-card v-else-if="viewMode === 'table'" class="table-card">
      <el-table v-loading="loading" :data="exhibitions" stripe border>
        <el-table-column label="主视觉图" width="140">
          <template #default="{ row }">
            <img :src="row.cover" :alt="row.title" class="table-cover">
          </template>
        </el-table-column>
        <el-table-column prop="title" label="展览名称" min-width="180" />
        <el-table-column prop="time" label="展览时间" width="220" />
        <el-table-column prop="location" label="展览地点 / 展厅" width="180" />
        <el-table-column prop="artifactCount" label="参展文物数量" width="130" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link @click="goDetail(row.id)">详情</el-button>
            <el-button v-if="canEdit" link @click="goEdit(row.id)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-footer">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadExhibitions"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <section v-else class="calendar-view page-card">
      <div class="calendar-head">
        <h3>展览排期视图</h3>
        <p>按时间轴查看展览计划与排期分布。</p>
      </div>
      <div v-loading="loading" class="calendar-list">
        <div v-for="item in exhibitions" :key="item.id" class="calendar-item">
          <div class="calendar-date">
            <strong>{{ item.startDate }}</strong>
            <span>开始</span>
          </div>
          <div class="calendar-content">
            <div class="calendar-title-row">
              <h4>{{ item.title }}</h4>
              <el-tag :type="statusTagType(item.status)">{{ item.status }}</el-tag>
            </div>
            <p>{{ item.location }} | 共 {{ item.artifactCount }} 件文物</p>
            <div class="calendar-actions">
              <el-button link @click="goDetail(item.id)">详情</el-button>
              <el-button v-if="canEdit" link @click="goEdit(item.id)">编辑</el-button>
            </div>
          </div>
        </div>
        <el-empty v-if="!loading && !exhibitions.length" description="暂无展览数据" />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ExhibitionCard from '@/components/ExhibitionCard.vue'
import { exhibitionApi } from '@/api/exhibition'
import { hasPermission } from '@/utils/permission'

const router = useRouter()

const filters = ref({
  keyword: '',
  status: '',
  range: []
})

const viewMode = ref('card')
const loading = ref(false)
const exhibitions = ref([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const transparentPixel = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw=='
const canAdd = computed(() => hasPermission('exhibition:add'))
const canEdit = computed(() => hasPermission('exhibition:edit'))

const statusLabelMap = {
  PLANNING: '策划中',
  ONGOING: '进行中',
  FINISHED: '已结束'
}

const statusTagType = (status) => {
  if (status === '进行中') return 'success'
  if (status === '策划中') return 'warning'
  return 'info'
}

const normalizeExhibition = async (item) => {
  const artifactDetails = await exhibitionApi.getArtifactDetails(item.id).catch(() => [])

  const previewArtifacts = artifactDetails.slice(0, 4).map((artifact) => ({
    name: artifact.name || artifact.code || '未命名文物',
    thumb: artifact.imageUrl || transparentPixel
  }))

  return {
    id: item.id,
    title: item.name || '未命名展览',
    location: item.venue || '未填写场地',
    time: `${item.startDate || '-'} - ${item.endDate || '-'}`,
    startDate: item.startDate || '-',
    endDate: item.endDate || '-',
    artifactCount: artifactDetails.length,
    cover: item.posterUrl || transparentPixel,
    status: statusLabelMap[item.status] || item.status || '未设置',
    previewArtifacts
  }
}

const loadExhibitions = async (page = pagination.value.current) => {
  loading.value = true
  pagination.value.current = page
  try {
    const res = await exhibitionApi.getList({
      current: pagination.value.current,
      size: pagination.value.size,
      name: filters.value.keyword || undefined,
      status: filters.value.status || undefined,
      startDate: filters.value.range?.[0] || undefined,
      endDate: filters.value.range?.[1] || undefined
    })
    const mapped = await Promise.all((res.records || []).map(normalizeExhibition))
    exhibitions.value = mapped
    pagination.value.total = res.total || 0
  } catch (error) {
    exhibitions.value = []
    pagination.value.total = 0
    ElMessage.error(error?.response?.data?.message || '加载展览列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  loadExhibitions(1)
}

const resetFilters = () => {
  filters.value = {
    keyword: '',
    status: '',
    range: []
  }
  loadExhibitions(1)
}

const goDetail = (id) => {
  router.push(`/exhibitions/${id}`)
}

const goCreate = () => {
  router.push('/exhibitions/add')
}

const goEdit = (id) => {
  router.push(`/exhibitions/${id}/edit`)
}

onMounted(() => {
  loadExhibitions(1)
})
</script>

<style scoped>
.exhibition-page {
  display: grid;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.page-header h2 {
  margin: 0;
  color: var(--museum-text-strong);
}

.page-header p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-card,
.table-card,
.calendar-view {
  padding: 18px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.filter-actions {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 16px;
  margin-top: 16px;
}

.view-switch {
  display: inline-flex;
  gap: 8px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.table-cover {
  width: 96px;
  height: 68px;
  border-radius: 8px;
  object-fit: cover;
  display: block;
}

.pagination-footer {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
}

.calendar-head h3 {
  margin: 0;
  color: var(--museum-text-strong);
}

.calendar-head p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
}

.calendar-list {
  display: grid;
  gap: 16px;
  margin-top: 18px;
}

.calendar-item {
  display: grid;
  grid-template-columns: 160px minmax(0, 1fr);
  gap: 18px;
  padding: 18px;
  border-radius: 18px;
  background: var(--museum-bg-subtle);
}

.calendar-date strong {
  display: block;
  color: var(--museum-text-strong);
}

.calendar-date span,
.calendar-content p {
  color: var(--museum-text-secondary);
}

.calendar-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.calendar-title-row h4 {
  margin: 0;
  color: var(--museum-text-strong);
}

.calendar-actions {
  margin-top: 12px;
}

@media (max-width: 1100px) {
  .filter-grid,
  .card-grid {
    grid-template-columns: 1fr;
  }

  .calendar-item {
    grid-template-columns: 1fr;
  }
}
</style>
