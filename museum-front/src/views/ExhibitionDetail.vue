<template>
  <div class="exhibition-detail-page">
    <el-skeleton v-if="loading" :rows="12" animated />

    <template v-else-if="exhibition.id">
      <section class="hero-banner" :style="heroStyle">
        <div class="hero-overlay" />
        <div class="hero-content">
          <div class="hero-meta">
            <el-tag effect="dark" round type="info">展览详情</el-tag>
            <el-tag round :type="statusTagType">{{ exhibition.statusLabel }}</el-tag>
          </div>
          <h1>{{ exhibition.name }}</h1>
          <p>{{ exhibition.theme || exhibition.description || '当前展示的是该展览的基础信息与运营数据。' }}</p>
          <div class="hero-info">
            <span>策展人：{{ exhibition.curator || '未填写' }}</span>
            <span>{{ exhibition.dateRange }}</span>
            <span>{{ exhibition.venue || '未填写场地' }}</span>
          </div>
        </div>
      </section>

      <section class="detail-grid">
        <div class="main-column">
          <el-card shadow="never" class="detail-card">
            <template #header>
              <div class="section-header">
                <div>
                  <h3>展览信息</h3>
                  <p>主视觉、策展信息与展览简介。</p>
                </div>
                <div class="action-group">
                  <el-button v-if="canEdit" text @click="goEdit">编辑展览</el-button>
                  <el-button text @click="exportArtifactList">导出展品清单</el-button>
                  <el-button text @click="downloadEvaluationReport">评估报告</el-button>
                </div>
              </div>
            </template>

            <div class="intro-layout">
              <div class="intro-cover">
                <img :src="exhibition.cover" :alt="exhibition.name">
              </div>
              <div class="intro-body">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="展览名称">{{ exhibition.name }}</el-descriptions-item>
                  <el-descriptions-item label="策展人">{{ exhibition.curator || '未填写' }}</el-descriptions-item>
                  <el-descriptions-item label="展览时间">{{ exhibition.dateRange }}</el-descriptions-item>
                  <el-descriptions-item label="场地">{{ exhibition.venue || '未填写' }}</el-descriptions-item>
                  <el-descriptions-item label="参展文物">{{ exhibition.artifacts.length }} 件</el-descriptions-item>
                  <el-descriptions-item label="当前状态">{{ exhibition.statusLabel }}</el-descriptions-item>
                </el-descriptions>

                <div class="rich-text">
                  <h4>展览简介</h4>
                  <p>{{ exhibition.description || '暂无展览简介。' }}</p>
                </div>
              </div>
            </div>
          </el-card>

          <el-card shadow="never" class="detail-card">
            <template #header>
              <div class="section-header">
                <div>
                  <h3>参展文物</h3>
                  <p>从文物库中添加文物，或将当前展品移出展览。</p>
                </div>
                <div class="action-group">
                  <el-button v-if="canEdit" type="primary" plain @click="openAddDialog">添加文物</el-button>
                </div>
              </div>
            </template>

            <div class="artifact-grid">
              <article
                v-for="(artifact, index) in exhibition.artifacts"
                :key="artifact.artifactId"
                class="artifact-card"
              >
                <div class="artifact-order">#{{ String(index + 1).padStart(2, '0') }}</div>
                <img :src="artifact.image" :alt="artifact.name">
                <div class="artifact-content">
                  <h4>{{ artifact.name }}</h4>
                  <p>{{ artifact.code }}</p>
                  <div class="artifact-meta">
                    <span>{{ artifact.era || '年代未录入' }}</span>
                    <span>{{ artifact.position }}</span>
                  </div>
                </div>
                <div class="artifact-actions">
                  <el-button text @click="viewArtifact(artifact.artifactId)">查看详情</el-button>
                  <el-button v-if="canEdit" text type="danger" @click="removeArtifact(artifact.artifactId)">移出文物</el-button>
                </div>
              </article>
            </div>

            <el-empty v-if="!exhibition.artifacts.length" description="当前展览暂无关联文物" />
          </el-card>

          <el-card shadow="never" class="detail-card" id="materials">
            <template #header>
              <div class="section-header">
                <div>
                  <h3>宣传资料</h3>
                  <p>支持本地文件上传，也支持链接归档。</p>
                </div>
              </div>
            </template>

            <el-tabs v-model="materialMode">
              <el-tab-pane label="文件上传" name="file">
                <div class="material-upload page-subcard">
                  <div class="section-mini-title">上传本地文件</div>
                  <div class="material-form file-form">
                    <el-input v-model="fileMaterialForm.name" placeholder="资料名称，如展览海报" />
                    <el-select v-model="fileMaterialForm.type" placeholder="资料类型">
                      <el-option v-for="item in materialTypeOptions" :key="item" :label="item" :value="item" />
                    </el-select>
                    <el-upload
                      class="material-uploader"
                      :auto-upload="false"
                      :show-file-list="false"
                      :before-upload="beforeMaterialUpload"
                    >
                      <el-button plain>选择文件</el-button>
                    </el-upload>
                    <div class="picked-file">
                      {{ selectedFile ? selectedFile.name : '尚未选择文件' }}
                    </div>
                    <el-button type="primary" :loading="materialSaving" @click="submitMaterialFile">上传资料</el-button>
                  </div>
                </div>
              </el-tab-pane>

              <el-tab-pane label="链接归档" name="link">
                <div class="material-upload page-subcard">
                  <div class="section-mini-title">归档外部资料链接</div>
                  <div class="material-form">
                    <el-input v-model="linkMaterialForm.name" placeholder="资料名称，如新闻稿" />
                    <el-select v-model="linkMaterialForm.type" placeholder="资料类型">
                      <el-option v-for="item in materialTypeOptions" :key="item" :label="item" :value="item" />
                    </el-select>
                    <el-input v-model="linkMaterialForm.url" placeholder="资料链接 URL" />
                    <el-button type="primary" :loading="materialSaving" @click="submitMaterialLink">新增资料</el-button>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>

            <div v-if="materials.length" class="material-list">
              <article v-for="item in materials" :key="item.id" class="material-item page-subcard">
                <div>
                  <strong>{{ item.name }}</strong>
                  <p>
                    {{ item.type || '未分类' }}
                    ·
                    {{ item.storageType === 'FILE' ? '本地文件' : '链接归档' }}
                    ·
                    {{ formatDateTime(item.uploadedAt) }}
                  </p>
                  <small v-if="item.originalFileName" class="material-note">
                    原文件：{{ item.originalFileName }}
                  </small>
                  <small v-else-if="item.sourceUrl" class="material-note">
                    链接：{{ item.sourceUrl }}
                  </small>
                </div>
                <div class="material-actions">
                  <el-button text @click="downloadMaterial(item)">下载</el-button>
                  <el-button v-if="canEdit" text type="danger" @click="removeMaterial(item)">删除</el-button>
                </div>
              </article>
            </div>
            <el-empty v-else description="暂无宣传资料" />
          </el-card>

          <el-card shadow="never" class="detail-card" id="stats">
            <template #header>
              <div class="section-header">
                <div>
                  <h3>观众预约统计</h3>
                  <p>预约人数、实际参观人数与满场率趋势。</p>
                </div>
              </div>
            </template>

            <div class="stats-summary">
              <div v-for="item in statsCards" :key="item.label" class="stats-card">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }}</strong>
                <p>{{ item.note }}</p>
              </div>
            </div>

            <div ref="attendanceChartRef" class="attendance-chart" />
          </el-card>
        </div>

        <aside class="side-column">
          <el-card shadow="never" class="detail-card" id="timeline">
            <template #header>
              <div class="section-header">
                <div>
                  <h3>展览阶段时间轴</h3>
                  <p>根据展览时间生成策划、布展、开展与撤展阶段。</p>
                </div>
              </div>
            </template>

            <div class="gantt-board">
              <div class="gantt-header">
                <span>阶段</span>
                <div class="gantt-months">
                  <span v-for="month in ganttMonths" :key="month">{{ month }}</span>
                </div>
              </div>
              <div v-for="phase in phases" :key="phase.name" class="gantt-row">
                <span class="phase-name">{{ phase.name }}</span>
                <div class="gantt-track">
                  <div class="gantt-bar" :style="{ left: `${phase.start}%`, width: `${phase.duration}%` }">
                    {{ phase.range }}
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <el-card shadow="never" class="detail-card">
            <template #header>
              <div class="section-header">
                <div>
                  <h3>快捷操作</h3>
                  <p>展览管理的常用入口。</p>
                </div>
              </div>
            </template>

            <div class="quick-actions">
              <button class="quick-action" type="button" @click="scrollToAnchor('materials')">
                <span>宣传资料</span>
                <small>上传、归档、下载和删除展览资料。</small>
              </button>
              <button class="quick-action" type="button" @click="scrollToAnchor('stats')">
                <span>预约统计</span>
                <small>查看预约人数、到访人数与满场率趋势。</small>
              </button>
              <button v-if="canEdit" class="quick-action danger" type="button" @click="handleEndExhibition">
                <span>结束展览</span>
                <small>将展览状态更新为已结束。</small>
              </button>
            </div>
          </el-card>
        </aside>
      </section>

      <div class="floating-nav">
        <el-dropdown placement="top" trigger="click">
          <el-button type="primary" circle size="large">
            <el-icon><Operation /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="scrollToAnchor('timeline')">跳转到时间轴</el-dropdown-item>
              <el-dropdown-item @click="scrollToAnchor('materials')">跳转到宣传资料</el-dropdown-item>
              <el-dropdown-item @click="scrollToAnchor('stats')">跳转到预约统计</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <el-dialog v-model="addDialogVisible" title="从文物库添加文物" width="720px">
        <div class="picker-grid">
          <article
            v-for="artifact in availableArtifacts"
            :key="artifact.id"
            class="picker-card"
            :class="{ selected: selectedArtifactIds.includes(artifact.id) }"
            @click="toggleArtifactSelection(artifact.id)"
          >
            <img :src="artifact.image" :alt="artifact.name">
            <div>
              <h4>{{ artifact.name }}</h4>
              <p>{{ artifact.code }} · {{ artifact.era || '年代未录入' }}</p>
            </div>
          </article>
        </div>

        <el-empty v-if="!availableArtifacts.length" description="暂无可添加文物" />

        <template #footer>
          <span class="dialog-footer">
            <el-button @click="addDialogVisible = false">取消</el-button>
            <el-button type="primary" :loading="saving" @click="confirmAddArtifacts">添加选中文物</el-button>
          </span>
        </template>
      </el-dialog>
    </template>

    <el-empty v-else description="未找到展览详情" />
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Operation } from '@element-plus/icons-vue'
import { exhibitionApi } from '@/api/exhibition'
import { artifactApi } from '@/api/artifact'
import { downloadResponseBlob } from '@/utils/download'
import { formatDateTime } from '@/utils/format'
import { hasPermission } from '@/utils/permission'
import {
  EXHIBITION_STATUS_LABELS,
  EXHIBITION_STATUS_TYPES,
  getStatusLabel,
  getStatusType
} from '@/utils/status'

const route = useRoute()
const router = useRouter()

const attendanceChartRef = ref(null)
const addDialogVisible = ref(false)
const selectedArtifactIds = ref([])
const availableArtifacts = ref([])
const exhibition = ref({})
const materials = ref([])
const loading = ref(false)
const saving = ref(false)
const materialSaving = ref(false)
const materialMode = ref('file')
const selectedFile = ref(null)
const visitorStats = ref({
  reservedCount: 0,
  visitedCount: 0,
  occupancyRate: '0%',
  trendData: []
})

const fileMaterialForm = ref({
  name: '',
  type: ''
})

const linkMaterialForm = ref({
  name: '',
  type: '',
  url: ''
})

const materialTypeOptions = ['海报', '导览手册', '新闻稿', '图片素材', '其他']
const canEdit = computed(() => hasPermission('exhibition:edit'))

let attendanceChart = null
let echartsLib = null

const transparentPixel = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw=='

const heroStyle = computed(() => ({
  backgroundImage: exhibition.value.cover
    ? `linear-gradient(120deg, rgba(15, 31, 31, 0.28), rgba(15, 31, 31, 0.72)), url(${exhibition.value.cover})`
    : 'linear-gradient(120deg, rgba(15, 31, 31, 0.72), rgba(15, 31, 31, 0.42))'
}))

const statusTagType = computed(() => {
  return getStatusType(EXHIBITION_STATUS_TYPES, exhibition.value.status) || 'info'
})

const statsCards = computed(() => [
  { label: '预约人数', value: visitorStats.value.reservedCount ?? 0, note: '来自预约记录汇总' },
  { label: '实际参观人数', value: visitorStats.value.visitedCount ?? 0, note: '根据审批通过的预约统计' },
  { label: '满场率', value: visitorStats.value.occupancyRate || '0%', note: '按预约与到访人数计算' }
])

const ganttMonths = computed(() => {
  const start = exhibition.value.startDate?.slice(0, 7) || ''
  if (!start) return []
  const [, month] = start.split('-').map(Number)
  return Array.from({ length: 6 }, (_, index) => `${((month - 1 + index) % 12) + 1}月`)
})

const phases = computed(() => {
  if (!exhibition.value.startDate || !exhibition.value.endDate) return []
  return [
    { name: '展览周期', start: 0, duration: 96, range: exhibition.value.dateRange || '-' }
  ]
})

const loadEcharts = async () => {
  if (!echartsLib) {
    const module = await import('@/utils/echarts')
    echartsLib = module.echarts
  }
  return echartsLib
}

const normalizeExhibition = (detail, artifacts) => {
  const startDate = detail.startDate || ''
  const endDate = detail.endDate || ''
  return {
    ...detail,
    cover: detail.posterUrl || detail.cover || '',
    curator: detail.curator || detail.curatorName || '',
    venue: detail.location || detail.venue || '',
    statusLabel: getStatusLabel(EXHIBITION_STATUS_LABELS, detail.status) || '未设置',
    dateRange: `${startDate || '-'} - ${endDate || '-'}`,
    startDate,
    endDate,
    artifacts: (artifacts || []).map((item) => ({
      ...item,
      artifactId: item.artifactId || item.id,
      image: item.imageUrl || item.image || transparentPixel,
      position: item.position || item.remark || '-'
    }))
  }
}

const loadAvailableArtifacts = async (excludedIds = []) => {
  try {
    const res = await artifactApi.getList({ current: 1, size: 100 })
    const records = res.records || []
    availableArtifacts.value = records
      .filter((item) => !excludedIds.includes(item.id))
      .map((item) => ({
        ...item,
        image: item.imageUrl || transparentPixel
      }))
  } catch {
    availableArtifacts.value = []
  }
}

const renderAttendanceChart = async () => {
  if (!attendanceChartRef.value) return
  const echarts = await loadEcharts()

  if (!attendanceChart) {
    attendanceChart = echarts.init(attendanceChartRef.value)
  }

  const trendData = visitorStats.value.trendData || []
  attendanceChart.setOption({
    color: ['#2e5a5a', '#d4a017', '#7a8c8c'],
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    grid: { left: 24, right: 24, top: 24, bottom: 54, containLabel: true },
    xAxis: {
      type: 'category',
      data: trendData.map((item) => item.date)
    },
    yAxis: [
      { type: 'value', name: '人数' },
      { type: 'value', name: '满场率', min: 0, max: 100, axisLabel: { formatter: '{value}%' } }
    ],
    series: [
      {
        name: '预约人数',
        type: 'bar',
        data: trendData.map((item) => item.reservedCount || 0),
        barMaxWidth: 24
      },
      {
        name: '实际参观人数',
        type: 'bar',
        data: trendData.map((item) => item.visitedCount || 0),
        barMaxWidth: 24
      },
      {
        name: '满场率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: trendData.map((item) => item.occupancyRate ?? 0)
      }
    ]
  })
}

const handleResize = () => {
  attendanceChart?.resize()
}

const resetMaterialForms = () => {
  fileMaterialForm.value = { name: '', type: '' }
  linkMaterialForm.value = { name: '', type: '', url: '' }
  selectedFile.value = null
}

const loadDetail = async () => {
  loading.value = true
  try {
    const [detail, artifacts, materialRes, statsRes] = await Promise.all([
      exhibitionApi.getDetail(route.params.id),
      exhibitionApi.getArtifactDetails(route.params.id),
      exhibitionApi.getMaterials(route.params.id).catch(() => []),
      exhibitionApi.getVisitorStats(route.params.id).catch(() => ({
        reservedCount: 0,
        visitedCount: 0,
        occupancyRate: '0%',
        trendData: []
      }))
    ])

    exhibition.value = normalizeExhibition(detail, artifacts)
    materials.value = materialRes || []
    visitorStats.value = statsRes || {
      reservedCount: 0,
      visitedCount: 0,
      occupancyRate: '0%',
      trendData: []
    }
    await loadAvailableArtifacts(exhibition.value.artifacts.map((item) => item.artifactId))
    await nextTick()
    await renderAttendanceChart()
  } catch (error) {
    exhibition.value = {}
    materials.value = []
    ElMessage.error(error?.response?.data?.message || '加载展览详情失败')
  } finally {
    loading.value = false
  }
}

const scrollToAnchor = (id) => {
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const exportArtifactList = async () => {
  try {
    const res = await exhibitionApi.exportArtifacts(exhibition.value.id)
    downloadResponseBlob(res, `exhibition-artifacts-${exhibition.value.id}.csv`)
    ElMessage.success('展品清单导出成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '导出展品清单失败')
  }
}

const downloadEvaluationReport = async () => {
  try {
    const res = await exhibitionApi.getEvaluationReport(exhibition.value.id)
    downloadResponseBlob(res, `exhibition-evaluation-${exhibition.value.id}.txt`)
    ElMessage.success('评估报告下载成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '下载评估报告失败')
  }
}

const beforeMaterialUpload = (file) => {
  selectedFile.value = file
  if (!fileMaterialForm.value.name) {
    const filename = file.name.replace(/\.[^.]+$/, '')
    fileMaterialForm.value.name = filename
  }
  return false
}

const submitMaterialFile = async () => {
  if (!fileMaterialForm.value.name || !fileMaterialForm.value.type || !selectedFile.value) {
    ElMessage.warning('请填写资料名称、资料类型，并选择要上传的文件')
    return
  }

  const formData = new FormData()
  formData.append('name', fileMaterialForm.value.name)
  formData.append('type', fileMaterialForm.value.type)
  formData.append('file', selectedFile.value)

  materialSaving.value = true
  try {
    const res = await exhibitionApi.uploadMaterialFile(exhibition.value.id, formData)
    materials.value = res || []
    resetMaterialForms()
    materialMode.value = 'file'
    ElMessage.success('资料文件上传成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '上传资料文件失败')
  } finally {
    materialSaving.value = false
  }
}

const submitMaterialLink = async () => {
  if (!linkMaterialForm.value.name || !linkMaterialForm.value.type || !linkMaterialForm.value.url) {
    ElMessage.warning('请完整填写资料名称、资料类型和资料链接')
    return
  }

  materialSaving.value = true
  try {
    const res = await exhibitionApi.uploadMaterial(exhibition.value.id, linkMaterialForm.value)
    materials.value = res || []
    resetMaterialForms()
    materialMode.value = 'link'
    ElMessage.success('资料已归档')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '新增资料失败')
  } finally {
    materialSaving.value = false
  }
}

const downloadMaterial = async (item) => {
  try {
    const res = await exhibitionApi.downloadMaterial(exhibition.value.id, item.id)
    downloadResponseBlob(res, item.originalFileName || `${item.name || 'material'}.txt`)
    ElMessage.success('资料下载成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '资料下载失败')
  }
}

const removeMaterial = async (item) => {
  try {
    await ElMessageBox.confirm(`确认删除资料“${item.name}”吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '确认删除',
      cancelButtonText: '取消'
    })
    await exhibitionApi.deleteMaterial(exhibition.value.id, item.id)
    materials.value = materials.value.filter((entry) => entry.id !== item.id)
    ElMessage.success('资料已删除')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '删除资料失败')
    }
  }
}

const handleEndExhibition = async () => {
  try {
    await ElMessageBox.confirm(`确认结束“${exhibition.value.name}”吗？`, '结束展览确认', {
      type: 'warning',
      confirmButtonText: '确认结束',
      cancelButtonText: '取消'
    })
    await exhibitionApi.update({
      ...exhibition.value,
      posterUrl: exhibition.value.posterUrl,
      status: 'FINISHED'
    })
    ElMessage.success('展览已结束')
    await loadDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '结束展览失败')
    }
  }
}

const openAddDialog = async () => {
  selectedArtifactIds.value = []
  await loadAvailableArtifacts(exhibition.value.artifacts.map((item) => item.artifactId))
  addDialogVisible.value = true
}

const viewArtifact = (id) => {
  router.push(`/artifacts/${id}`)
}

const goEdit = () => {
  router.push(`/exhibitions/${exhibition.value.id}/edit`)
}

const removeArtifact = async (artifactId) => {
  const target = exhibition.value.artifacts.find((item) => item.artifactId === artifactId)
  if (!target) return

  try {
    await ElMessageBox.confirm(`确认将“${target.name}”移出当前展览吗？`, '移出文物确认', {
      type: 'warning',
      confirmButtonText: '确认移出',
      cancelButtonText: '取消'
    })
    await exhibitionApi.removeArtifact(exhibition.value.id, artifactId)
    ElMessage.success('文物已移出展览')
    await loadDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '移出文物失败')
    }
  }
}

const toggleArtifactSelection = (id) => {
  if (selectedArtifactIds.value.includes(id)) {
    selectedArtifactIds.value = selectedArtifactIds.value.filter((item) => item !== id)
    return
  }
  selectedArtifactIds.value = [...selectedArtifactIds.value, id]
}

const confirmAddArtifacts = async () => {
  if (!selectedArtifactIds.value.length) {
    ElMessage.warning('请先选择要添加的文物')
    return
  }

  saving.value = true
  try {
    await Promise.all(
      selectedArtifactIds.value.map((artifactId, index) =>
        exhibitionApi.addArtifact(exhibition.value.id, artifactId, exhibition.value.artifacts.length + index + 1, '')
      )
    )
    ElMessage.success('选中文物已添加到展览')
    addDialogVisible.value = false
    selectedArtifactIds.value = []
    await loadDetail()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '添加文物失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await loadDetail()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  attendanceChart?.dispose()
  attendanceChart = null
})
</script>

<style scoped>
.exhibition-detail-page {
  display: grid;
  gap: 24px;
}

.hero-banner {
  position: relative;
  min-height: 280px;
  border-radius: 24px;
  overflow: hidden;
  background-size: cover;
  background-position: center;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, rgba(15, 23, 42, 0.2), rgba(15, 23, 42, 0.66));
}

.hero-content {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 14px;
  padding: 36px;
  color: #fff;
}

.hero-meta,
.hero-info,
.action-group,
.artifact-actions,
.material-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.hero-content h1 {
  margin: 0;
  font-size: 32px;
}

.hero-content p {
  margin: 0;
  line-height: 1.8;
  max-width: 780px;
}

.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(320px, 0.85fr);
  gap: 20px;
}

.main-column,
.side-column {
  display: grid;
  gap: 20px;
}

.detail-card {
  border-radius: 22px;
}

.page-subcard {
  padding: 16px 18px;
  border-radius: 16px;
  background: var(--museum-bg-subtle);
}

.section-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.section-header h3 {
  margin: 0;
  color: var(--museum-text-primary);
}

.section-header p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.intro-layout {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 20px;
}

.intro-cover img,
.artifact-card img,
.picker-card img {
  width: 100%;
  display: block;
  object-fit: cover;
}

.intro-cover img {
  aspect-ratio: 4 / 5;
  border-radius: 18px;
}

.rich-text {
  margin-top: 18px;
}

.rich-text h4 {
  margin: 0 0 10px;
  color: var(--museum-text-primary);
}

.rich-text p {
  margin: 0;
  color: var(--museum-text-secondary);
  line-height: 1.8;
}

.artifact-grid,
.picker-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.artifact-card,
.picker-card,
.material-item {
  border-radius: 18px;
  overflow: hidden;
  background: var(--museum-bg-subtle);
}

.artifact-card,
.picker-card {
  position: relative;
  cursor: pointer;
}

.artifact-card img,
.picker-card img {
  aspect-ratio: 4 / 3;
}

.artifact-order {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 1;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(20, 20, 20, 0.6);
  color: #fff;
  font-size: 12px;
}

.artifact-content {
  padding: 14px 14px 8px;
}

.artifact-content h4,
.picker-card h4,
.material-item strong {
  margin: 0;
  color: var(--museum-text-primary);
}

.artifact-content p,
.picker-card p,
.material-item p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
}

.artifact-meta {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  font-size: 12px;
}

.artifact-actions {
  padding: 0 14px 12px;
  justify-content: space-between;
}

.section-mini-title {
  font-weight: 600;
  color: var(--museum-text-primary);
  margin-bottom: 12px;
}

.material-form {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 180px minmax(0, 1.1fr) auto;
  gap: 12px;
}

.file-form {
  grid-template-columns: minmax(0, 1fr) 180px auto minmax(0, 1fr) auto;
}

.material-uploader {
  display: flex;
}

.picked-file,
.material-note {
  color: var(--museum-text-secondary);
}

.picked-file {
  display: flex;
  align-items: center;
  min-height: 32px;
}

.material-upload {
  margin-bottom: 16px;
}

.material-list {
  display: grid;
  gap: 14px;
}

.material-item {
  padding: 16px 18px;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.stats-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.stats-card {
  padding: 18px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(46, 90, 90, 0.08), rgba(212, 160, 23, 0.08));
  border: 1px solid rgba(46, 90, 90, 0.12);
}

.stats-card span {
  display: block;
  color: var(--museum-text-secondary);
  font-size: 13px;
}

.stats-card strong {
  display: block;
  margin-top: 10px;
  font-size: 28px;
  color: var(--museum-text-primary);
}

.attendance-chart {
  min-height: 320px;
}

.gantt-header,
.gantt-row {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr);
  gap: 14px;
  align-items: center;
}

.gantt-header {
  color: var(--museum-text-secondary);
  font-size: 12px;
}

.gantt-months,
.gantt-track {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 8px;
}

.gantt-track {
  position: relative;
  min-height: 38px;
  padding: 6px 0;
}

.gantt-track::before {
  content: '';
  position: absolute;
  inset: 10px 0;
  border-radius: 999px;
  background:
    repeating-linear-gradient(
      to right,
      rgba(240, 242, 245, 0.95) 0,
      rgba(240, 242, 245, 0.95) calc(16.66% - 4px),
      transparent calc(16.66% - 4px),
      transparent 16.66%
    );
}

.gantt-bar {
  position: absolute;
  top: 6px;
  height: 26px;
  border-radius: 999px;
  background: linear-gradient(90deg, var(--museum-color-primary), #d4a017);
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
}

.quick-actions {
  display: grid;
  gap: 12px;
}

.quick-action {
  border: 1px solid var(--museum-border-light);
  background: #fff;
  border-radius: 16px;
  padding: 14px 16px;
  text-align: left;
  cursor: pointer;
}

.quick-action span {
  display: block;
  font-weight: 600;
  color: var(--museum-text-primary);
}

.quick-action small {
  display: block;
  margin-top: 8px;
  color: var(--museum-text-secondary);
  line-height: 1.6;
}

.quick-action.danger {
  border-color: rgba(245, 34, 45, 0.16);
  background: rgba(245, 34, 45, 0.04);
}

.floating-nav {
  position: fixed;
  right: 28px;
  bottom: 28px;
  z-index: 20;
}

.picker-card {
  border: 1px solid transparent;
}

.picker-card.selected {
  border-color: rgba(46, 90, 90, 0.28);
  box-shadow: 0 0 0 1px rgba(46, 90, 90, 0.12);
}

@media (max-width: 1200px) {
  .detail-grid,
  .intro-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .stats-summary,
  .material-form,
  .file-form {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .hero-content,
  .detail-grid {
    gap: 16px;
  }

  .hero-content {
    padding: 24px;
  }

  .hero-content h1 {
    font-size: 26px;
  }

  .section-header,
  .hero-info,
  .action-group,
  .artifact-actions,
  .material-actions {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
