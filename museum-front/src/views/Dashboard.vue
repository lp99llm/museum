<template>
  <div v-loading="loading" class="dashboard-page">
    <section class="hero-grid">
      <article class="welcome-card page-card">
        <div class="welcome-copy">
          <div class="welcome-eyebrow">Museum Operations Overview</div>
          <h2>欢迎回来，{{ currentUser.username }}</h2>
          <p class="welcome-subtitle">
            当前角色：{{ currentUser.role }}，上次登录时间：{{ lastLoginText }}
          </p>
          <div class="welcome-metrics">
            <div class="welcome-metric">
              <span>今日待处理任务</span>
              <strong>{{ summary.pendingTasks }}</strong>
            </div>
            <div class="welcome-metric">
              <span>待处理预约</span>
              <strong>{{ formatNumber(dashboard.pendingAppointments) }}</strong>
            </div>
            <div class="welcome-metric">
              <span>流程异常提醒</span>
              <strong>{{ anomalyAlerts.length }} 项</strong>
            </div>
          </div>
        </div>
        <div class="welcome-silhouette">
          <div class="silhouette-ring" />
          <div class="silhouette-badge">守护文明 | 智管传承</div>
        </div>
      </article>

      <article class="ai-card page-card">
        <div class="ai-card-header">
          <div>
            <div class="section-kicker">AI 洞察</div>
            <h3>{{ aiTitle }}</h3>
          </div>
          <el-tag effect="dark" type="warning">智能分析</el-tag>
        </div>
        <div class="ai-insights">
          <div v-for="insight in aiInsights" :key="insight.title" class="ai-item">
            <div class="ai-item-title">{{ insight.title }}</div>
            <div class="ai-item-desc">{{ insight.desc }}</div>
          </div>
        </div>
      </article>
    </section>

    <section class="kpi-grid">
      <article v-for="card in kpiCards" :key="card.title" class="kpi-card page-card">
        <div class="kpi-head">
          <div class="kpi-icon" :style="{ color: card.accent, background: `${card.accent}18` }">
            <component :is="card.icon" />
          </div>
          <span class="kpi-trend" :class="{ warning: card.warning }">{{ card.delta }}</span>
        </div>
        <div class="kpi-title">{{ card.title }}</div>
        <div class="kpi-value">{{ card.value }}</div>
        <div class="kpi-foot">{{ card.foot }}</div>
      </article>
    </section>

    <section class="alert-strip">
      <button
        v-for="alert in anomalyAlerts"
        :key="alert.title"
        type="button"
        class="alert-card"
        @click="goToAlert(alert)"
      >
        <div class="alert-badge">{{ alert.level }}</div>
        <div class="alert-body">
          <div class="alert-title">{{ alert.title }}</div>
          <div class="alert-desc">{{ alert.desc }}</div>
        </div>
      </button>
    </section>

    <section class="chart-grid">
      <article class="chart-panel page-card">
        <div class="panel-header">
          <div>
            <h3>文物类别分布</h3>
            <p>基于真实文物统计接口返回的类别结构，可快速定位馆藏集中类别</p>
          </div>
          <span class="panel-meta">馆藏画像</span>
        </div>
        <div ref="categoryChartRef" class="chart-box" />
      </article>

      <article class="chart-panel page-card">
        <div class="panel-header">
          <div>
            <h3>文物状态分布</h3>
            <p>展示当前文物在库、在展与修复等状态结构，反映流转压力</p>
          </div>
          <span class="panel-meta">状态结构</span>
        </div>
        <div ref="statusChartRef" class="chart-box" />
      </article>

      <article class="chart-panel page-card">
        <div class="panel-header">
          <div>
            <h3>近 12 个月访客趋势</h3>
            <p>使用已通过预约的真实访客数据绘制趋势曲线，辅助判断展览热度变化</p>
          </div>
          <span class="panel-meta">客流趋势</span>
        </div>
        <div ref="visitorChartRef" class="chart-box" />
      </article>
    </section>

    <section class="workspace-grid">
      <article class="todo-panel page-card">
        <div class="panel-header">
          <div>
            <h3>待办任务</h3>
            <p>根据预约、流程耗时、风险统计和展览表现自动汇总当前应优先处理事项</p>
          </div>
          <el-badge :value="todoItems.length" class="todo-badge" />
        </div>

        <div class="todo-timeline">
          <div v-for="item in todoItems" :key="item.title" class="todo-item">
            <div class="todo-line">
              <span class="todo-dot" :class="item.status" />
            </div>
            <div class="todo-content">
              <div class="todo-title">{{ item.title }}</div>
              <div class="todo-desc">{{ item.description }}</div>
              <div class="todo-meta">{{ item.time }}</div>
            </div>
          </div>
        </div>
      </article>

      <article class="quick-panel page-card">
        <div class="panel-header">
          <div>
            <h3>快捷操作</h3>
            <p>高频业务操作的快速入口</p>
          </div>
        </div>

        <div class="quick-grid">
          <button
            v-for="action in quickActions"
            :key="action.title"
            type="button"
            class="quick-card"
            @click="router.push(action.path)"
          >
            <div class="quick-icon" :style="{ color: action.accent, background: `${action.accent}14` }">
              <component :is="action.icon" />
            </div>
            <div class="quick-title">{{ action.title }}</div>
            <div class="quick-desc">{{ action.desc }}</div>
          </button>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
defineOptions({
  name: 'DashboardView'
})

import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Box,
  DataLine,
  DocumentChecked,
  Histogram,
  MagicStick,
  Promotion,
  Search
} from '@element-plus/icons-vue'
import { statisticsApi } from '@/api/statistics'

const router = useRouter()

const categoryChartRef = ref(null)
const statusChartRef = ref(null)
const visitorChartRef = ref(null)
const loading = ref(false)

const dashboard = reactive({
  totalArtifacts: 0,
  totalExhibitions: 0,
  totalVisitors: 0,
  pendingAppointments: 0,
  averageFeedbackScore: 0,
  safetyIncidentCount: 0,
  categoryDistribution: [],
  statusDistribution: [],
  visitorTrend: [],
  exhibitionEffects: []
})

const processStats = reactive({
  durationStats: [],
  monthlyTrend: [],
  riskStats: []
})

const aiAnalysis = reactive({
  title: '',
  summary: '',
  suggestions: [],
  cards: []
})

let categoryChart = null
let statusChart = null
let visitorChart = null
let echartsLib = null

const formatNumber = (value) => new Intl.NumberFormat('zh-CN').format(Number(value) || 0)
const formatScore = (value) => Number(value || 0).toFixed(1)
const formatPercent = (value) => `${Number(value || 0).toFixed(1)}%`
const toList = (value) => (Array.isArray(value) ? value : [])

const currentUser = computed(() => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || 'null')
    return {
      username: user?.username || '管理员',
      role: user?.role || '系统管理员'
    }
  } catch {
    return {
      username: '管理员',
      role: '系统管理员'
    }
  }
})

const previousLogin = localStorage.getItem('museum:last-login-at')
const lastLoginText = previousLogin || '首次登录'
localStorage.setItem('museum:last-login-at', new Date().toLocaleString('zh-CN'))

const totalRiskCount = computed(() =>
  toList(processStats.riskStats).reduce((sum, item) => sum + Number(item.riskValue || 0), 0)
)

const summary = computed(() => ({
  pendingTasks: dashboard.pendingAppointments + totalRiskCount.value
}))

const aiTitle = computed(() => aiAnalysis.title || '本周运营建议')

const kpiCards = computed(() => [
  {
    title: '文物总量',
    value: formatNumber(dashboard.totalArtifacts),
    delta: `${dashboard.categoryDistribution.length} 个类别`,
    foot: '来自文物基础统计接口',
    accent: '#2e5a5a',
    icon: Box
  },
  {
    title: '展览总数',
    value: formatNumber(dashboard.totalExhibitions),
    delta: `${dashboard.exhibitionEffects.length} 个已评估展览`,
    foot: '已进入展览成效统计',
    accent: '#d4a017',
    icon: Histogram
  },
  {
    title: '累计访客',
    value: formatNumber(dashboard.totalVisitors),
    delta: `${dashboard.visitorTrend.length} 个月趋势`,
    foot: '基于已通过预约累计',
    accent: '#1890ff',
    icon: DataLine
  },
  {
    title: '待处理预约',
    value: formatNumber(dashboard.pendingAppointments),
    delta: dashboard.pendingAppointments > 0 ? '需要尽快审批' : '当前无积压',
    foot: '首页同步预约审批压力',
    accent: '#faad14',
    icon: DocumentChecked,
    warning: dashboard.pendingAppointments > 0
  },
  {
    title: '平均反馈分',
    value: formatScore(dashboard.averageFeedbackScore),
    delta: dashboard.averageFeedbackScore >= 4 ? '展览口碑稳定' : '建议复盘优化',
    foot: '来自展览评估反馈均值',
    accent: '#52c41a',
    icon: MagicStick
  },
  {
    title: '安全事件数',
    value: formatNumber(dashboard.safetyIncidentCount),
    delta: totalRiskCount.value > 0 ? `${totalRiskCount.value} 项流程风险` : '流程风险可控',
    foot: '汇总展览安全事件与流程异常',
    accent: '#f5222d',
    icon: Promotion,
    warning: dashboard.safetyIncidentCount > 0 || totalRiskCount.value > 0
  }
])

const aiInsights = computed(() => {
  const summaryCard = aiAnalysis.summary
    ? [{ title: aiAnalysis.title || '智能分析摘要', desc: aiAnalysis.summary }]
    : []

  const suggestionCards = toList(aiAnalysis.suggestions).map((item, index) => ({
    title: `智能建议 ${index + 1}`,
    desc: item
  }))

  const metricCards = toList(aiAnalysis.cards)
    .slice(0, 2)
    .map((item) => ({
      title: item.title || '分析卡片',
      desc:
        item.percentage != null
          ? `当前占比 ${formatPercent(item.percentage)}，数量 ${formatNumber(item.value)}`
          : `当前值 ${formatNumber(item.value)}`
    }))

  return [...summaryCard, ...suggestionCards, ...metricCards].slice(0, 4)
})

const anomalyAlerts = computed(() => {
  const riskAlerts = toList(processStats.riskStats)
    .filter((item) => Number(item.riskValue || 0) > 0)
    .map((item) => ({
      level: Number(item.riskValue || 0) >= 3 ? '红色预警' : '橙色提醒',
      title: `${item.processName || '流程'}异常待处理`,
      desc: `当前累计 ${formatNumber(item.riskValue)} 项异常或逾期记录，建议进入流程中心核查。`,
      route: '/process/center'
    }))

  const exhibitionAlerts = toList(dashboard.exhibitionEffects)
    .filter((item) => Number(item.feedbackScore || 0) > 0 && Number(item.feedbackScore || 0) < 3.5)
    .slice(0, 1)
    .map((item) => ({
      level: '橙色提醒',
      title: '展览反馈偏低',
      desc: `${item.exhibitionName || '当前展览'} 的反馈分为 ${formatScore(item.feedbackScore)}，建议复盘讲解与动线。`,
      route: '/exhibitions'
    }))

  const appointmentAlerts = dashboard.pendingAppointments > 0
    ? [{
        level: '蓝色关注',
        title: '预约审批积压',
        desc: `当前仍有 ${formatNumber(dashboard.pendingAppointments)} 条预约待审批。`,
        route: '/visitor-appointments'
      }]
    : []

  return [...riskAlerts, ...exhibitionAlerts, ...appointmentAlerts].slice(0, 3)
})

const todoItems = computed(() => {
  const items = []

  if (dashboard.pendingAppointments > 0) {
    items.push({
      title: '处理预约审批',
      description: `还有 ${formatNumber(dashboard.pendingAppointments)} 条访客预约等待审核。`,
      time: '当前',
      status: 'warning'
    })
  }

  const longestDuration = [...toList(processStats.durationStats)]
    .sort((a, b) => Number(b.avgDays || 0) - Number(a.avgDays || 0))[0]
  if (longestDuration) {
    items.push({
      title: '关注流程耗时',
      description: `${longestDuration.processName || '流程'} 平均闭环时长 ${longestDuration.avgDays || 0} 天。`,
      time: '统计结果',
      status: Number(longestDuration.avgDays || 0) > 15 ? 'pending' : 'processing'
    })
  }

  const latestTrend = toList(dashboard.visitorTrend)[0]
  if (latestTrend) {
    items.push({
      title: '复盘最近客流',
      description: `${latestTrend.month} 累计访客 ${formatNumber(latestTrend.value)} 人。`,
      time: latestTrend.month,
      status: 'completed'
    })
  }

  const topExhibition = toList(dashboard.exhibitionEffects)[0]
  if (topExhibition) {
    items.push({
      title: '跟进重点展览',
      description: `${topExhibition.exhibitionName || '展览'} 当前访客 ${formatNumber(topExhibition.visitorCount)}，反馈 ${formatScore(topExhibition.feedbackScore)} 分。`,
      time: '本期',
      status: 'processing'
    })
  }

  const topRisk = [...toList(processStats.riskStats)]
    .sort((a, b) => Number(b.riskValue || 0) - Number(a.riskValue || 0))[0]
  if (topRisk && Number(topRisk.riskValue || 0) > 0) {
    items.push({
      title: '处理异常流转',
      description: `${topRisk.processName || '流程'} 当前有 ${formatNumber(topRisk.riskValue)} 项异常记录。`,
      time: '需优先',
      status: 'pending'
    })
  }

  if (!items.length) {
    items.push({
      title: '今日任务已清空',
      description: '当前首页未发现待审批预约和高风险流程，系统运行平稳。',
      time: '当前',
      status: 'completed'
    })
  }

  return items.slice(0, 5)
})

const quickActions = [
  { title: '快速入库登记', desc: '发起文物入库流程', icon: Box, accent: '#2e5a5a', path: '/process/inbound' },
  { title: '快速查询文物', desc: '进入馆藏检索页面', icon: Search, accent: '#1890ff', path: '/artifacts' },
  { title: '发起出库申请', desc: '创建展览出库单', icon: Promotion, accent: '#d4a017', path: '/process/outbound' },
  { title: '进入展览管理', desc: '查看展览与成效数据', icon: MagicStick, accent: '#52c41a', path: '/exhibitions' }
]

const loadEcharts = async () => {
  if (!echartsLib) {
    echartsLib = await import('@/utils/echarts')
  }
  return echartsLib.echarts
}

const ensureCharts = async () => {
  const echarts = await loadEcharts()

  if (categoryChartRef.value && !categoryChart) {
    categoryChart = echarts.init(categoryChartRef.value)
    categoryChart.on('click', () => router.push('/artifacts'))
  }

  if (statusChartRef.value && !statusChart) {
    statusChart = echarts.init(statusChartRef.value)
    statusChart.on('click', () => router.push('/artifacts'))
  }

  if (visitorChartRef.value && !visitorChart) {
    visitorChart = echarts.init(visitorChartRef.value)
    visitorChart.on('click', () => router.push('/visitor-appointments'))
  }
}

const updateCategoryChart = async () => {
  if (!categoryChart) return

  categoryChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, textStyle: { color: '#6b7280' } },
    series: [{
      type: 'pie',
      radius: ['48%', '72%'],
      center: ['50%', '44%'],
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { formatter: '{b}\n{c} 件', color: '#374151' },
      data: toList(dashboard.categoryDistribution).map((item) => ({
        value: Number(item.count || 0),
        name: item.category || '未分类'
      })),
      color: ['#2e5a5a', '#d4a017', '#6c8b8b', '#8aa2a2', '#cbb27b']
    }]
  })
}

const updateStatusChart = async () => {
  if (!statusChart) return
  const echarts = await loadEcharts()

  statusChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 42, right: 12, top: 20, bottom: 36 },
    xAxis: {
      type: 'category',
      data: toList(dashboard.statusDistribution).map((item) => item.status || '未设置'),
      axisLabel: { color: '#6b7280' },
      axisLine: { lineStyle: { color: '#d1d5db' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#6b7280' },
      splitLine: { lineStyle: { color: '#eef2f7' } }
    },
    series: [{
      type: 'bar',
      barWidth: 26,
      data: toList(dashboard.statusDistribution).map((item) => Number(item.count || 0)),
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#2e5a5a' },
          { offset: 1, color: '#7ea0a0' }
        ])
      }
    }]
  })
}

const updateVisitorChart = async () => {
  if (!visitorChart) return

  const visitorTrend = [...toList(dashboard.visitorTrend)].reverse()

  visitorChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 18, top: 24, bottom: 28 },
    xAxis: {
      type: 'category',
      data: visitorTrend.map((item) => item.month || '-'),
      axisLabel: { color: '#6b7280' },
      axisLine: { lineStyle: { color: '#d1d5db' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#6b7280' },
      splitLine: { lineStyle: { color: '#eef2f7' } }
    },
    series: [
      {
        name: '访客人数',
        type: 'line',
        smooth: true,
        data: visitorTrend.map((item) => Number(item.value || 0)),
        itemStyle: { color: '#d4a017' },
        areaStyle: { color: 'rgba(212, 160, 23, 0.12)' }
      }
    ]
  })
}

const updateCharts = async () => {
  await ensureCharts()
  await Promise.all([updateCategoryChart(), updateStatusChart(), updateVisitorChart()])
}

const loadDashboard = async () => {
  loading.value = true
  try {
    const [dashboardRes, processRes, aiRes] = await Promise.all([
      statisticsApi.getDashboard(),
      statisticsApi.getProcessStats({}),
      statisticsApi.getAIAnalysis('inventory_optimization')
    ])

    dashboard.totalArtifacts = Number(dashboardRes?.totalArtifacts || 0)
    dashboard.totalExhibitions = Number(dashboardRes?.totalExhibitions || 0)
    dashboard.totalVisitors = Number(dashboardRes?.totalVisitors || 0)
    dashboard.pendingAppointments = Number(dashboardRes?.pendingAppointments || 0)
    dashboard.averageFeedbackScore = Number(dashboardRes?.averageFeedbackScore || 0)
    dashboard.safetyIncidentCount = Number(dashboardRes?.safetyIncidentCount || 0)
    dashboard.categoryDistribution = toList(dashboardRes?.categoryDistribution)
    dashboard.statusDistribution = toList(dashboardRes?.statusDistribution)
    dashboard.visitorTrend = toList(dashboardRes?.visitorTrend)
    dashboard.exhibitionEffects = toList(dashboardRes?.exhibitionEffects)

    processStats.durationStats = toList(processRes?.durationStats)
    processStats.monthlyTrend = toList(processRes?.monthlyTrend)
    processStats.riskStats = toList(processRes?.riskStats)

    aiAnalysis.title = aiRes?.title || ''
    aiAnalysis.summary = aiRes?.summary || ''
    aiAnalysis.suggestions = toList(aiRes?.suggestions)
    aiAnalysis.cards = toList(aiRes?.cards)

    await nextTick()
    await updateCharts()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载首页统计数据失败')
  } finally {
    loading.value = false
  }
}

const goToAlert = (alert) => {
  router.push(alert.route)
}

const resizeCharts = () => {
  categoryChart?.resize()
  statusChart?.resize()
  visitorChart?.resize()
}

onMounted(async () => {
  await loadDashboard()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  categoryChart?.dispose()
  statusChart?.dispose()
  visitorChart?.dispose()
})
</script>

<style scoped>
.dashboard-page {
  display: grid;
  gap: 24px;
}

.hero-grid {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(320px, 1fr);
  gap: 24px;
}

.welcome-card {
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) 220px;
  gap: 16px;
  padding: 28px;
  background:
    radial-gradient(circle at top right, rgba(212, 160, 23, 0.22), transparent 28%),
    linear-gradient(135deg, rgba(46, 90, 90, 0.14), rgba(46, 90, 90, 0.04));
}

.welcome-eyebrow,
.section-kicker {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--museum-color-primary);
}

.welcome-copy h2 {
  margin: 14px 0 8px;
  font-size: 30px;
  line-height: 1.3;
  color: var(--museum-text-strong);
}

.welcome-subtitle {
  margin: 0;
  color: var(--museum-text-secondary);
}

.welcome-metrics {
  display: flex;
  gap: 14px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.welcome-metric {
  min-width: 138px;
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.68);
  border: 1px solid rgba(255, 255, 255, 0.7);
}

.welcome-metric span {
  display: block;
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

.welcome-metric strong {
  display: block;
  margin-top: 6px;
  color: var(--museum-text-strong);
  font-size: 24px;
  font-weight: 600;
}

.welcome-silhouette {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.silhouette-ring {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  background:
    radial-gradient(circle at center, rgba(212, 160, 23, 0.28), transparent 54%),
    linear-gradient(180deg, rgba(46, 90, 90, 0.16), rgba(46, 90, 90, 0.04));
  mask: radial-gradient(circle at center, transparent 36%, #000 36%);
}

.silhouette-badge {
  position: absolute;
  bottom: 30px;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.88);
  color: var(--museum-color-primary);
  font-size: 12px;
  font-weight: 500;
}

.ai-card {
  padding: 24px;
}

.ai-card-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.ai-card-header h3 {
  margin: 10px 0 0;
  font-size: 20px;
  color: var(--museum-text-strong);
}

.ai-insights {
  display: grid;
  gap: 12px;
}

.ai-item {
  padding: 14px 16px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(46, 90, 90, 0.06), rgba(212, 160, 23, 0.08));
  border: 1px solid rgba(46, 90, 90, 0.08);
}

.ai-item-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--museum-text-strong);
}

.ai-item-desc {
  margin-top: 6px;
  color: var(--museum-text-secondary);
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
}

.kpi-card,
.chart-panel,
.todo-panel,
.quick-panel {
  padding: 18px;
}

.kpi-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.kpi-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.kpi-trend {
  font-size: 12px;
  font-weight: 600;
  color: var(--museum-color-success);
}

.kpi-trend.warning {
  color: var(--museum-color-warning);
}

.kpi-title {
  margin-top: 14px;
  color: var(--museum-text-secondary);
  font-size: 13px;
}

.kpi-value {
  margin-top: 8px;
  color: var(--museum-text-strong);
  font-size: 30px;
  font-weight: 600;
}

.kpi-foot {
  margin-top: 6px;
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

.alert-strip {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.alert-card {
  display: grid;
  grid-template-columns: 92px minmax(0, 1fr);
  gap: 14px;
  align-items: center;
  padding: 18px;
  border-radius: 18px;
  border: 1px solid rgba(245, 34, 45, 0.16);
  background: linear-gradient(135deg, rgba(245, 34, 45, 0.08), rgba(250, 173, 20, 0.05));
  text-align: left;
  cursor: pointer;
  transition: transform 200ms ease, box-shadow 200ms ease;
}

.alert-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 28px rgba(245, 34, 45, 0.08);
}

.alert-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 56px;
  padding: 0 10px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.78);
  color: var(--museum-color-error);
  font-size: 13px;
  font-weight: 700;
}

.alert-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--museum-text-strong);
}

.alert-desc {
  margin-top: 6px;
  color: var(--museum-text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.panel-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--museum-text-strong);
}

.panel-header p {
  margin: 6px 0 0;
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

.panel-meta {
  color: var(--museum-color-primary);
  font-size: 12px;
  font-weight: 600;
}

.chart-box {
  min-height: 320px;
}

.workspace-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(360px, 1fr);
  gap: 16px;
}

.todo-timeline {
  display: grid;
  gap: 18px;
}

.todo-item {
  display: grid;
  grid-template-columns: 18px minmax(0, 1fr);
  gap: 12px;
}

.todo-line {
  display: flex;
  justify-content: center;
}

.todo-dot {
  position: relative;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-top: 6px;
}

.todo-dot::after {
  content: '';
  position: absolute;
  top: 14px;
  left: 50%;
  width: 1px;
  height: 42px;
  transform: translateX(-50%);
  background: var(--museum-border-default);
}

.todo-item:last-child .todo-dot::after {
  display: none;
}

.todo-dot.pending {
  background: var(--museum-color-error);
}

.todo-dot.processing {
  background: var(--museum-color-info);
}

.todo-dot.warning {
  background: var(--museum-color-warning);
}

.todo-dot.completed {
  background: var(--museum-color-success);
}

.todo-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--museum-text-strong);
}

.todo-desc {
  margin-top: 4px;
  color: var(--museum-text-secondary);
}

.todo-meta {
  margin-top: 6px;
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.quick-card {
  border: 1px solid var(--museum-border-light);
  border-radius: 16px;
  background: var(--museum-bg-card);
  padding: 18px;
  text-align: left;
  cursor: pointer;
  transition: transform 200ms ease, box-shadow 200ms ease, border-color 200ms ease;
}

.quick-card:hover {
  transform: translateY(-2px);
  border-color: rgba(46, 90, 90, 0.18);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.08);
}

.quick-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.quick-title {
  margin-top: 14px;
  font-size: 15px;
  font-weight: 600;
  color: var(--museum-text-strong);
}

.quick-desc {
  margin-top: 6px;
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

@media (max-width: 1440px) {
  .kpi-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .chart-grid,
  .alert-strip {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1024px) {
  .hero-grid,
  .workspace-grid {
    grid-template-columns: 1fr;
  }

  .welcome-card {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .kpi-grid,
  .quick-grid {
    grid-template-columns: 1fr;
  }

  .welcome-metrics {
    flex-direction: column;
  }

  .alert-card {
    grid-template-columns: 1fr;
  }
}
</style>
