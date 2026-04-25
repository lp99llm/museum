<template>
  <div class="process-center-page">
    <section class="hero-panel page-card">
      <div class="hero-copy">
        <div class="eyebrow">Process Center</div>
        <h1>全流程业务管理中心</h1>
        <p>
          统一查看征集、鉴定、入库、出库、修复、外借与处置流程。
          页面只展示真实接口返回的记录、状态分布和异常提醒，不再使用前端推算的业务指标。
        </p>
      </div>
      <div class="hero-metrics">
        <div class="metric-item">
          <span>当前看板任务</span>
          <strong>{{ metrics.activeTasks }}</strong>
        </div>
        <div class="metric-item">
          <span>异常流转</span>
          <strong class="danger">{{ alerts.length }}</strong>
        </div>
        <div class="metric-item">
          <span>已归档流程</span>
          <strong>{{ metrics.completedTasks }}</strong>
        </div>
      </div>
    </section>

    <section class="entry-section">
      <div class="section-title">
        <div>
          <h3>流程入口</h3>
          <p>展示各业务模块的真实总量，以及可明确识别的待处理/待确认数量。</p>
        </div>
      </div>

      <div class="entry-grid">
        <button
          v-for="item in processEntries"
          :key="item.key"
          type="button"
          class="entry-card page-card"
          @click="router.push(item.path)"
        >
          <div class="entry-icon" :style="{ color: item.accent, background: `${item.accent}16` }">
            <component :is="item.icon" />
          </div>
          <div class="entry-content">
            <div class="entry-head">
              <h4>{{ item.title }}</h4>
              <el-tag :type="item.highlight > 5 ? 'danger' : 'warning'" effect="plain">
                {{ item.highlight }} {{ item.highlightLabel }}
              </el-tag>
            </div>
            <p>{{ item.desc }}</p>
            <div class="entry-meta">
              <span>入口路径 {{ item.routeLabel }}</span>
              <span>当前总量 {{ item.total }}</span>
            </div>
          </div>
        </button>
      </div>
    </section>

    <section class="board-section">
      <div class="section-title">
        <div>
          <h3>流程状态看板</h3>
          <p>仅展示具备明确状态字段的真实流程记录，按待处理、处理中、已完成、已驳回聚合。</p>
        </div>
        <div class="board-actions">
          <el-select v-model="selectedFlow" style="width: 220px">
            <el-option label="全部流程" value="all" />
            <el-option v-for="item in boardFlowOptions" :key="item.key" :label="item.title" :value="item.key" />
          </el-select>
        </div>
      </div>

      <div class="kanban-grid">
        <section
          v-for="column in boardColumns"
          :key="column.key"
          class="kanban-column page-card"
        >
          <header class="kanban-header">
            <div>
              <h4>{{ column.label }}</h4>
              <p>{{ column.desc }}</p>
            </div>
            <span class="kanban-count">{{ filteredBoard[column.key].length }}</span>
          </header>

          <div class="kanban-list">
            <article
              v-for="card in filteredBoard[column.key]"
              :key="card.id"
              class="kanban-card"
            >
              <div class="card-topline">
                <el-tag size="small" effect="plain">{{ card.flowLabel }}</el-tag>
                <span class="card-time">{{ card.updatedAt }}</span>
              </div>
              <h5>{{ card.title }}</h5>
              <p>{{ card.artifact }}</p>
              <div class="card-meta">
                <span>{{ card.owner }}</span>
                <span :class="{ danger: card.risk === 'high' }">{{ card.riskText }}</span>
              </div>
            </article>
            <el-empty v-if="!filteredBoard[column.key].length" description="暂无数据" />
          </div>
        </section>
      </div>
    </section>

    <section class="analysis-grid">
      <article class="efficiency-panel page-card">
        <div class="section-title compact">
          <div>
            <h3>流程状态概览</h3>
            <p>展示真实看板任务数量和状态分布，不再展示前端公式推导出的完成率、时长或异常占比。</p>
          </div>
        </div>

        <div class="efficiency-cards">
          <div v-for="metric in overviewCards" :key="metric.label" class="efficiency-card">
            <span>{{ metric.label }}</span>
            <strong>{{ metric.value }}</strong>
            <p>{{ metric.note }}</p>
          </div>
        </div>

        <div ref="efficiencyChartRef" class="chart-box" />
      </article>

      <article class="alerts-panel page-card">
        <div class="section-title compact">
          <div>
            <h3>流程异常提醒</h3>
            <p>基于真实流程记录的逾期待归还、待处理修复和已驳回任务进行提示。</p>
          </div>
        </div>

        <div class="alert-list">
          <button
            v-for="alert in alerts"
            :key="alert.id"
            type="button"
            class="alert-item"
            @click="goToArtifact(alert.artifactId)"
          >
            <div class="alert-topline">
              <span class="alert-level">{{ alert.level }}</span>
              <span class="alert-time">{{ alert.time }}</span>
            </div>
            <h4>{{ alert.title }}</h4>
            <p>{{ alert.desc }}</p>
          </button>
          <el-empty v-if="!alerts.length" description="暂无异常提醒" />
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Box,
  Checked,
  Document,
  Files,
  FirstAidKit,
  Goods,
  RemoveFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { acquisitionApi } from '@/api/acquisition'
import { appraisalApi } from '@/api/appraisal'
import { warehousingApi } from '@/api/warehousing'
import { outboundApi } from '@/api/outbound'
import { restorationApi } from '@/api/restoration'
import { loanApi } from '@/api/loan'
import { disposalApi } from '@/api/disposal'

const router = useRouter()
const efficiencyChartRef = ref(null)
const selectedFlow = ref('all')
const loading = ref(false)
const processStats = ref({})
const boardData = ref({
  pending: [],
  processing: [],
  completed: [],
  rejected: []
})
const alerts = ref([])
let efficiencyChart = null
let echartsLib = null

const loadEcharts = async () => {
  if (!echartsLib) {
    const module = await import('@/utils/echarts')
    echartsLib = module.echarts
  }
  return echartsLib
}

const processConfig = [
  {
    key: 'acquisition',
    title: '征集登记',
    desc: '展示真实征集记录总量',
    path: '/acquisition/list',
    routeLabel: '/acquisition/list',
    accent: '#2e5a5a',
    icon: Document,
    highlightLabel: '记录',
    board: false
  },
  {
    key: 'appraisal',
    title: '鉴定评估',
    desc: '展示真实鉴定记录总量',
    path: '/appraisal/list',
    routeLabel: '/appraisal/list',
    accent: '#d4a017',
    icon: Checked,
    highlightLabel: '记录',
    board: false
  },
  {
    key: 'inbound',
    title: '入库管理',
    desc: '基于真实入库状态统计待确认记录',
    path: '/warehousing/list',
    routeLabel: '/warehousing/list',
    accent: '#1890ff',
    icon: Box,
    highlightLabel: '待确认',
    board: true
  },
  {
    key: 'outbound',
    title: '展览出库',
    desc: '基于真实出库状态统计待处理任务',
    path: '/outbound/list',
    routeLabel: '/outbound/list',
    accent: '#faad14',
    icon: Goods,
    highlightLabel: '待处理',
    board: true
  },
  {
    key: 'restoration',
    title: '修复管理',
    desc: '基于真实修复状态统计待处理任务',
    path: '/restoration/list',
    routeLabel: '/restoration/list',
    accent: '#f5222d',
    icon: FirstAidKit,
    highlightLabel: '待处理',
    board: true
  },
  {
    key: 'loan',
    title: '外借管理',
    desc: '基于真实外借状态统计待处理任务',
    path: '/loan/list',
    routeLabel: '/loan/list',
    accent: '#52c41a',
    icon: Files,
    highlightLabel: '待处理',
    board: true
  },
  {
    key: 'disposal',
    title: '出馆处置',
    desc: '基于真实处置状态统计待处理任务',
    path: '/disposal/list',
    routeLabel: '/disposal/list',
    accent: '#7a8c8c',
    icon: RemoveFilled,
    highlightLabel: '待处理',
    board: true
  }
]

const boardColumns = [
  { key: 'pending', label: '待处理', desc: '等待审核或确认' },
  { key: 'processing', label: '处理中', desc: '已进入执行阶段' },
  { key: 'completed', label: '已完成', desc: '已完成并归档' },
  { key: 'rejected', label: '已驳回', desc: '待补充或终止' }
]

const boardFlowOptions = processConfig.filter((item) => item.board)

const processEntries = computed(() =>
  processConfig.map((item) => {
    const stat = processStats.value[item.key] || { total: 0, highlight: 0 }
    return {
      ...item,
      total: stat.total,
      highlight: stat.highlight
    }
  })
)

const filteredBoard = computed(() => {
  if (selectedFlow.value === 'all') {
    return boardData.value
  }

  return Object.fromEntries(
    Object.entries(boardData.value).map(([key, cards]) => [
      key,
      cards.filter((card) => card.flow === selectedFlow.value)
    ])
  )
})

const metrics = computed(() => ({
  activeTasks: boardData.value.pending.length + boardData.value.processing.length,
  completedTasks: boardData.value.completed.length
}))

const overviewCards = computed(() => [
  {
    label: '当前在办',
    value: metrics.value.activeTasks,
    note: '来自待处理和处理中看板任务'
  },
  {
    label: '已完成',
    value: boardData.value.completed.length,
    note: '来自具备明确完成状态的真实流程记录'
  },
  {
    label: '已驳回',
    value: boardData.value.rejected.length,
    note: '来自后端返回的驳回状态记录'
  }
])

const unwrapResult = (res) => {
  if (!res) return { total: 0, records: [] }
  return {
    total: Number(res.total || 0),
    records: Array.isArray(res.records) ? res.records : []
  }
}

const mapBoardColumn = (status) => {
  if (['PENDING', 'APPLY'].includes(status)) return 'pending'
  if (['REJECTED'].includes(status)) return 'rejected'
  if (['RETURNED', 'COMPLETED', 'ARCHIVED', 'CONFIRMED'].includes(status)) return 'completed'
  return 'processing'
}

const formatTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

const buildCard = (flow, flowLabel, item, owner, riskText, risk = 'medium') => ({
  id: `${flow}-${item.id}`,
  flow,
  flowLabel,
  title: `${flowLabel}任务`,
  artifact: `${item.artifactName || '未命名文物'} / ${item.artifactCode || '-'}`,
  owner: owner || item.handler || item.applicant || item.expertName || '未分配',
  updatedAt: formatTime(item.updatedTime || item.createdTime),
  risk,
  riskText
})

const appendCards = (cards) => {
  cards.forEach((card) => {
    boardData.value[card.column].push(card.item)
  })
}

const isPast = (dateValue) => {
  if (!dateValue) return false
  const today = new Date()
  const current = new Date(String(dateValue))
  if (Number.isNaN(current.getTime())) return false
  today.setHours(0, 0, 0, 0)
  current.setHours(0, 0, 0, 0)
  return current < today
}

const buildAlerts = (groups) => {
  const nextAlerts = []

  groups.outbound.records
    .filter((item) => item.status !== 'RETURNED' && isPast(item.expectedReturnDate))
    .slice(0, 3)
    .forEach((item) => {
      nextAlerts.push({
        id: `outbound-${item.id}`,
        artifactId: item.artifactId,
        level: '高优先级',
        time: formatTime(item.updatedTime || item.createdTime),
        title: '出库文物逾期待归还',
        desc: `${item.artifactName || '文物'} 的出库流程尚未归档，请尽快确认回库状态。`
      })
    })

  groups.loan.records
    .filter((item) => item.status !== 'RETURNED' && isPast(item.expectedReturnDate))
    .slice(0, 3)
    .forEach((item) => {
      nextAlerts.push({
        id: `loan-${item.id}`,
        artifactId: item.artifactId,
        level: '高优先级',
        time: formatTime(item.updatedTime || item.createdTime),
        title: '外借文物逾期待归还',
        desc: `${item.artifactName || '文物'} 的外借记录已到预期归还时间，请核对归还与入库。`
      })
    })

  groups.restoration.records
    .filter((item) => ['PENDING', 'REJECTED'].includes(item.status))
    .slice(0, 3)
    .forEach((item) => {
      nextAlerts.push({
        id: `restoration-${item.id}`,
        artifactId: item.artifactId,
        level: item.status === 'REJECTED' ? '待跟进' : '待处理',
        time: formatTime(item.updatedTime || item.createdTime),
        title: item.status === 'REJECTED' ? '修复申请已驳回' : '修复申请待处理',
        desc: `${item.artifactName || '文物'} 当前状态为 ${item.status || '-'}，需要业务人员继续处理。`
      })
    })

  alerts.value = nextAlerts
}

const renderChart = async () => {
  if (!efficiencyChartRef.value) return
  const echarts = await loadEcharts()

  if (!efficiencyChart) {
    efficiencyChart = echarts.init(efficiencyChartRef.value)
  }

  efficiencyChart.setOption({
    color: ['#2e5a5a'],
    tooltip: { trigger: 'axis' },
    grid: { left: 24, right: 24, top: 24, bottom: 30, containLabel: true },
    xAxis: {
      type: 'category',
      data: ['待处理', '处理中', '已完成', '已驳回'],
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisLabel: { color: '#606266' }
    },
    yAxis: [{ type: 'value', name: '数量', splitLine: { lineStyle: { color: '#f0f2f5' } } }],
    series: [
      {
        name: '任务数量',
        type: 'bar',
        barMaxWidth: 28,
        data: [
          boardData.value.pending.length,
          boardData.value.processing.length,
          boardData.value.completed.length,
          boardData.value.rejected.length
        ],
        itemStyle: { borderRadius: [8, 8, 0, 0] }
      }
    ]
  })
}

const resizeChart = () => {
  efficiencyChart?.resize()
}

const fetchTotal = (api, params = {}) =>
  api.getPage({ current: 1, size: 1, ...params }).then((res) => Number(res?.total || 0)).catch(() => 0)

const loadProcessCenter = async () => {
  loading.value = true
  try {
    const [
      acquisitionRes,
      appraisalRes,
      warehousingRes,
      outboundRes,
      restorationRes,
      loanRes,
      disposalRes,
      outboundPending,
      restorationPending,
      loanPending,
      disposalPending,
      warehousingConfirmed
    ] = await Promise.all([
      acquisitionApi.getPage({ current: 1, size: 12 }),
      appraisalApi.getPage({ current: 1, size: 12 }),
      warehousingApi.getPage({ current: 1, size: 12 }),
      outboundApi.getPage({ current: 1, size: 12 }),
      restorationApi.getPage({ current: 1, size: 12 }),
      loanApi.getPage({ current: 1, size: 12 }),
      disposalApi.getPage({ current: 1, size: 12 }),
      fetchTotal(outboundApi, { status: 'PENDING' }),
      fetchTotal(restorationApi, { status: 'PENDING' }),
      fetchTotal(loanApi, { status: 'PENDING' }),
      fetchTotal(disposalApi, { status: 'PENDING' }),
      fetchTotal(warehousingApi, { storageStatus: 'CONFIRMED' })
    ])

    const groups = {
      acquisition: unwrapResult(acquisitionRes),
      appraisal: unwrapResult(appraisalRes),
      inbound: unwrapResult(warehousingRes),
      outbound: unwrapResult(outboundRes),
      restoration: unwrapResult(restorationRes),
      loan: unwrapResult(loanRes),
      disposal: unwrapResult(disposalRes)
    }

    processStats.value = {
      acquisition: {
        total: groups.acquisition.total,
        highlight: groups.acquisition.total
      },
      appraisal: {
        total: groups.appraisal.total,
        highlight: groups.appraisal.total
      },
      inbound: {
        total: groups.inbound.total,
        highlight: Math.max(groups.inbound.total - warehousingConfirmed, 0)
      },
      outbound: {
        total: groups.outbound.total,
        highlight: outboundPending
      },
      restoration: {
        total: groups.restoration.total,
        highlight: restorationPending
      },
      loan: {
        total: groups.loan.total,
        highlight: loanPending
      },
      disposal: {
        total: groups.disposal.total,
        highlight: disposalPending
      }
    }

    boardData.value = { pending: [], processing: [], completed: [], rejected: [] }

    appendCards([
      ...(groups.inbound.records || []).map((item) => ({
        column: item.storageStatus === 'CONFIRMED' ? 'completed' : 'processing',
        item: buildCard(
          'inbound',
          '入库管理',
          item,
          item.handler || '库房管理员',
          item.storageStatus || '待确认',
          item.storageStatus === 'CONFIRMED' ? 'low' : 'medium'
        )
      })),
      ...(groups.outbound.records || []).map((item) => ({
        column: mapBoardColumn(item.status),
        item: buildCard(
          'outbound',
          '展览出库',
          item,
          item.handler || '流程负责人',
          item.currentStage || item.status || '-',
          item.status === 'PENDING' ? 'high' : 'medium'
        )
      })),
      ...(groups.restoration.records || []).map((item) => ({
        column: mapBoardColumn(item.status),
        item: buildCard(
          'restoration',
          '修复管理',
          item,
          item.restorer || item.applicant || '待分配',
          item.currentStage || item.status || '-',
          ['PENDING', 'REJECTED'].includes(item.status) ? 'high' : 'medium'
        )
      })),
      ...(groups.loan.records || []).map((item) => ({
        column: mapBoardColumn(item.status),
        item: buildCard(
          'loan',
          '外借管理',
          item,
          item.borrowerInstitution || item.applicant || '待确认',
          item.currentStage || item.status || '-',
          item.status === 'PENDING' ? 'high' : 'medium'
        )
      })),
      ...(groups.disposal.records || []).map((item) => ({
        column: mapBoardColumn(item.status),
        item: buildCard(
          'disposal',
          '出馆处置',
          item,
          item.applicant || '待确认',
          item.currentStage || item.status || '-',
          item.status === 'PENDING' ? 'high' : 'medium'
        )
      }))
    ])

    buildAlerts(groups)
    await nextTick()
    await renderChart()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载流程中心失败')
  } finally {
    loading.value = false
  }
}

const goToArtifact = (id) => {
  if (!id) {
    router.push('/process/center')
    return
  }
  router.push(`/artifact/detail/${id}`)
}

onMounted(async () => {
  await loadProcessCenter()
  window.addEventListener('resize', resizeChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  efficiencyChart?.dispose()
  efficiencyChart = null
})
</script>

<style scoped>
.process-center-page {
  display: grid;
  gap: 24px;
}

.hero-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(280px, 0.9fr);
  gap: 24px;
  padding: 28px;
  background:
    radial-gradient(circle at top right, rgba(212, 160, 23, 0.2), transparent 28%),
    linear-gradient(135deg, rgba(46, 90, 90, 0.12), rgba(46, 90, 90, 0.04));
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--museum-color-primary);
  font-weight: 600;
}

.hero-copy h1 {
  margin: 14px 0 10px;
  font-size: 30px;
  color: var(--museum-text-strong);
}

.hero-copy p {
  margin: 0;
  max-width: 760px;
  line-height: 1.8;
  color: var(--museum-text-secondary);
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.metric-item {
  padding: 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.metric-item span {
  display: block;
  color: var(--museum-text-secondary);
  font-size: 12px;
}

.metric-item strong {
  display: block;
  margin-top: 10px;
  font-size: 28px;
  color: var(--museum-text-strong);
}

.metric-item strong.danger {
  color: var(--museum-color-error);
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.section-title.compact {
  margin-bottom: 18px;
}

.section-title h3 {
  margin: 0;
  font-size: 18px;
  color: var(--museum-text-strong);
}

.section-title p {
  margin: 6px 0 0;
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

.entry-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.entry-card {
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr);
  gap: 16px;
  padding: 20px;
  text-align: left;
  border: 1px solid var(--museum-border-light);
  cursor: pointer;
  transition: transform 200ms ease, border-color 200ms ease, box-shadow 200ms ease;
}

.entry-card:hover {
  transform: translateY(-3px);
  border-color: rgba(46, 90, 90, 0.18);
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.08);
}

.entry-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.entry-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.entry-head h4 {
  margin: 0;
  font-size: 16px;
  color: var(--museum-text-strong);
}

.entry-content p {
  margin: 8px 0 12px;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.entry-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: var(--museum-text-tertiary);
}

.kanban-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.kanban-column {
  min-height: 320px;
  padding: 18px;
  border: 1px solid var(--museum-border-light);
}

.kanban-header {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 14px;
}

.kanban-header h4 {
  margin: 0;
  font-size: 15px;
  color: var(--museum-text-strong);
}

.kanban-header p {
  margin: 6px 0 0;
  font-size: 12px;
  color: var(--museum-text-tertiary);
}

.kanban-count {
  min-width: 32px;
  height: 32px;
  border-radius: 12px;
  background: var(--museum-bg-subtle);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--museum-text-secondary);
  font-weight: 600;
}

.kanban-list {
  display: grid;
  gap: 12px;
}

.kanban-card {
  padding: 14px;
  border-radius: 14px;
  background: var(--museum-bg-card);
  border: 1px solid var(--museum-border-light);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.04);
}

.card-topline,
.card-meta {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
}

.card-time,
.card-meta {
  font-size: 12px;
  color: var(--museum-text-tertiary);
}

.kanban-card h5 {
  margin: 12px 0 6px;
  font-size: 15px;
  color: var(--museum-text-strong);
}

.kanban-card p {
  margin: 0 0 12px;
  color: var(--museum-text-secondary);
}

.card-meta .danger {
  color: var(--museum-color-error);
}

.analysis-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(320px, 0.9fr);
  gap: 16px;
}

.efficiency-panel,
.alerts-panel {
  padding: 20px;
}

.efficiency-cards {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.efficiency-card {
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(46, 90, 90, 0.06), rgba(212, 160, 23, 0.08));
  border: 1px solid rgba(46, 90, 90, 0.08);
}

.efficiency-card span {
  display: block;
  color: var(--museum-text-secondary);
  font-size: 12px;
}

.efficiency-card strong {
  display: block;
  margin-top: 8px;
  color: var(--museum-text-strong);
  font-size: 26px;
}

.efficiency-card p {
  margin: 8px 0 0;
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

.chart-box {
  min-height: 320px;
}

.alert-list {
  display: grid;
  gap: 12px;
}

.alert-item {
  padding: 16px;
  text-align: left;
  border-radius: 14px;
  border: 1px solid rgba(245, 34, 45, 0.14);
  background: linear-gradient(135deg, rgba(245, 34, 45, 0.06), rgba(250, 173, 20, 0.05));
  cursor: pointer;
  transition: transform 200ms ease, box-shadow 200ms ease;
}

.alert-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 28px rgba(245, 34, 45, 0.08);
}

.alert-topline {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: var(--museum-color-error);
  font-size: 12px;
}

.alert-item h4 {
  margin: 10px 0 6px;
  font-size: 15px;
  color: var(--museum-text-strong);
}

.alert-item p {
  margin: 0;
  line-height: 1.7;
  color: var(--museum-text-secondary);
}

@media (max-width: 1440px) {
  .kanban-grid,
  .entry-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1200px) {
  .analysis-grid,
  .hero-panel {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .kanban-grid,
  .entry-grid,
  .efficiency-cards,
  .hero-metrics {
    grid-template-columns: 1fr;
  }
}
</style>
