<template>
  <div ref="pageRef" class="analytics-page" :class="{ 'is-fullscreen': fullscreen }">
    <section class="analytics-hero page-card">
      <div>
        <div class="eyebrow">Analytics Center</div>
        <h1>数据统计与可视化中心</h1>
        <p>集中查看文物、流程、展览与访客统计，图表和概览数据均来自真实统计接口。</p>
      </div>
      <div class="hero-actions">
        <el-button @click="toggleFullscreen">{{ fullscreen ? '退出大屏' : '大屏模式' }}</el-button>
        <el-button v-permission="PERMISSIONS.REPORT_EXPORT" type="primary" @click="exportAs('Excel')">导出统计报表</el-button>
      </div>
    </section>

    <section class="filter-card page-card">
      <div class="filter-grid">
        <div class="quick-range">
          <span>时间范围</span>
          <div class="quick-range-buttons">
            <el-button
              v-for="item in quickRanges"
              :key="item.value"
              size="small"
              :type="filters.quickRange === item.value ? 'primary' : 'default'"
              @click="selectQuickRange(item.value)"
            >
              {{ item.label }}
            </el-button>
          </div>
        </div>

        <el-date-picker
          v-model="filters.range"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />

        <el-select v-model="filters.categories" multiple collapse-tags placeholder="文物类别">
          <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
        </el-select>

        <el-select v-model="filters.status" placeholder="文物状态" clearable>
          <el-option label="在库" value="在库" />
          <el-option label="在展" value="在展" />
          <el-option label="修复中" value="修复中" />
          <el-option label="外借中" value="外借中" />
        </el-select>

        <div class="toolbar-actions">
          <el-button type="primary" :loading="loading" @click="loadAnalytics">刷新数据</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </div>
    </section>

    <section class="metric-tabs">
      <button
        v-for="item in metricTabs"
        :key="item.value"
        type="button"
        class="metric-tab"
        :class="{ active: activeTab === item.value }"
        @click="activeTab = item.value"
      >
        <strong>{{ item.label }}</strong>
        <span>{{ item.desc }}</span>
      </button>
    </section>

    <section class="summary-grid">
      <article v-for="card in activeSummaryCards" :key="card.title" class="summary-card page-card">
        <div class="summary-top">
          <span>{{ card.title }}</span>
          <em :class="{ warning: card.warning }">{{ card.delta }}</em>
        </div>
        <strong>{{ card.value }}</strong>
        <p>{{ card.note }}</p>
      </article>
    </section>

    <section class="chart-grid">
      <article class="chart-card page-card">
        <div class="card-header">
          <div>
            <h3>{{ activeCharts.primary.title }}</h3>
            <p>{{ activeCharts.primary.desc }}</p>
          </div>
          <el-button text type="primary" @click="handleDrilldown(activeCharts.primary.target)">查看明细</el-button>
        </div>
        <div ref="primaryChartRef" class="chart-box" />
      </article>

      <article class="chart-card page-card">
        <div class="card-header">
          <div>
            <h3>{{ activeCharts.secondary.title }}</h3>
            <p>{{ activeCharts.secondary.desc }}</p>
          </div>
          <el-button text type="primary" @click="handleDrilldown(activeCharts.secondary.target)">查看明细</el-button>
        </div>
        <div ref="secondaryChartRef" class="chart-box" />
      </article>

      <article class="chart-card page-card">
        <div class="card-header">
          <div>
            <h3>{{ activeCharts.tertiary.title }}</h3>
            <p>{{ activeCharts.tertiary.desc }}</p>
          </div>
          <el-button text type="primary" @click="handleDrilldown(activeCharts.tertiary.target)">查看明细</el-button>
        </div>
        <div ref="tertiaryChartRef" class="chart-box" />
      </article>

      <article class="chart-card page-card">
        <div class="card-header">
          <div>
            <h3>空间利用热度</h3>
            <p>基于后端返回的文物位置与状态聚合结果，展示展厅与库房的空间利用热度。</p>
          </div>
          <el-button text type="primary" @click="handleDrilldown('/artifact/list')">定位文物明细</el-button>
        </div>
        <div ref="heatmapChartRef" class="chart-box" />
      </article>
    </section>

    <section class="insight-grid">
      <article class="insight-card page-card">
        <div class="card-header">
          <div>
            <h3>AI 智能分析</h3>
            <p>优先展示后端返回的 AI 分析结果；若暂无数据，则提供稳定的系统提示。</p>
          </div>
          <el-tag type="warning" effect="dark">大模型辅助</el-tag>
        </div>
        <div class="ai-list">
          <div v-for="item in aiInsights" :key="item.title" class="ai-item">
            <strong>{{ item.title }}</strong>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </article>

      <article class="insight-card page-card">
        <div class="card-header">
          <div>
            <h3>报表导出</h3>
            <p>支持导出 Excel / PDF 统计报表，便于日常复盘与管理汇报。</p>
          </div>
        </div>
        <div class="report-config">
          <el-form label-position="top">
            <el-form-item label="导出指标">
              <el-select v-model="reportConfig.metrics" multiple collapse-tags>
                <el-option label="文物统计" value="artifact" />
                <el-option label="展览统计" value="exhibition" />
              </el-select>
            </el-form-item>
            <el-form-item label="导出格式">
              <div class="report-actions">
                <el-button v-permission="PERMISSIONS.REPORT_EXPORT" @click="exportAs('Excel')">导出 Excel</el-button>
                <el-button v-permission="PERMISSIONS.REPORT_EXPORT" @click="exportAs('PDF')">导出 PDF</el-button>
              </div>
            </el-form-item>
            <el-form-item label="自动推送">
              <el-switch v-model="reportConfig.autoPush" disabled />
              <span class="inline-tip">自动推送依赖后端定时任务与邮件服务配置。</span>
            </el-form-item>
          </el-form>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { reportApi, statisticsApi } from '@/api/statistics'
import { PERMISSIONS } from '@/constants/permissions'
import { downloadResponseBlob } from '@/utils/download'

const router = useRouter()
const pageRef = ref(null)
const primaryChartRef = ref(null)
const secondaryChartRef = ref(null)
const tertiaryChartRef = ref(null)
const heatmapChartRef = ref(null)

const fullscreen = ref(false)
const loading = ref(false)
const activeTab = ref('artifact')

let primaryChart = null
let secondaryChart = null
let tertiaryChart = null
let heatmapChart = null
let echartsLib = null

const loadEcharts = async () => {
  if (!echartsLib) {
    const module = await import('@/utils/echarts')
    echartsLib = module.echarts
  }
  return echartsLib
}

const quickRanges = [
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本季度', value: 'quarter' },
  { label: '本年', value: 'year' }
]

const filters = reactive({
  quickRange: 'month',
  range: [],
  categories: [],
  status: ''
})

const reportConfig = reactive({
  metrics: ['artifact'],
  autoPush: false
})

const metricTabs = [
  { value: 'artifact', label: '文物基础数据', desc: '库存总量、类别分布与状态结构' },
  { value: 'process', label: '流程效率数据', desc: '处理时长、流转趋势与风险统计' },
  { value: 'exhibition', label: '展览成效数据', desc: '展览效果、客流表现与反馈质量' },
  { value: 'visitor', label: '访客预约数据', desc: '访客趋势与待处理预约概览' }
]

const dashboardData = ref(null)
const artifactStats = ref({ categoryDistribution: [], statusDistribution: [], totalCount: 0 })
const exhibitionStats = ref({ exhibitionEffects: [], totalExhibitions: 0, averageFeedback: 0, safetyIncidents: 0 })
const visitorStats = ref({ visitorTrend: [], totalVisitors: 0, pendingAppointments: 0 })
const processStats = ref({ durationStats: [], monthlyTrend: [], riskStats: [] })
const heatmapStats = ref({ xAxis: [], yAxis: [], matrix: [] })
const drilldownData = ref({ records: [], total: 0 })
const aiAnalysis = ref({
  queryType: '',
  title: '',
  summary: '',
  trend: '',
  suggestions: [],
  cards: []
})

const categoryOptions = computed(() => {
  const fromStats = (artifactStats.value.categoryDistribution || [])
    .map((item) => item.category)
    .filter(Boolean)
  const selected = (filters.categories || []).filter(Boolean)
  return [...new Set([...fromStats, ...selected])]
})

const activeCharts = computed(() => ({
  artifact: {
    primary: { title: '文物类别分布', desc: '基于真实文物统计接口返回的类别分布。', target: '/artifact/list' },
    secondary: { title: '文物状态分布', desc: '基于真实文物统计接口返回的状态分布。', target: '/artifact/list' },
    tertiary: { title: '文物明细概览', desc: '结合下钻明细接口生成当前筛选条件下的概览。', target: '/artifact/list' }
  },
  process: {
    primary: { title: '流程处理耗时', desc: '展示不同流程的平均处理时长。', target: '/process/center' },
    secondary: { title: '流程创建与完成趋势', desc: '按月查看流程创建量与完成量。', target: '/process/center' },
    tertiary: { title: '流程风险雷达', desc: '根据风险统计接口展示流程风险强度。', target: '/process/center' }
  },
  exhibition: {
    primary: { title: '展览成效分布', desc: '展示展览客流与反馈等成效数据。', target: '/exhibition/list' },
    secondary: { title: '展览反馈与安全事件', desc: '综合查看反馈质量与安全事件情况。', target: '/exhibition/list' },
    tertiary: { title: '展览客流散点', desc: '用于观察展览客流与评分之间的关系。', target: '/exhibition/list' }
  },
  visitor: {
    primary: { title: '访客趋势', desc: '基于预约统计展示访客趋势。', target: '/visitor-appointments' },
    secondary: { title: '待处理预约', desc: '聚合当前待处理预约情况。', target: '/visitor-appointments' },
    tertiary: { title: '访客波动分布', desc: '帮助识别预约与到访的波动特征。', target: '/visitor-appointments' }
  }
}[activeTab.value]))

const buildStatisticsParams = () => ({
  startDate: filters.range?.[0] || undefined,
  endDate: filters.range?.[1] || undefined,
  category: filters.categories?.[0] || undefined,
  status: filters.status || undefined
})

const activeSummaryCards = computed(() => {
  if (activeTab.value === 'artifact') {
    const categories = artifactStats.value.categoryDistribution || []
    const statuses = artifactStats.value.statusDistribution || []
    const topCategory = categories[0]
    const firstStatus = statuses[0]
    return [
      { title: '文物总量', value: `${artifactStats.value.totalCount || 0}`, delta: '实时统计', note: '来自文物统计接口' },
      { title: '类别数量', value: `${categories.length}`, delta: '自动汇总', note: topCategory ? `当前最多类别为 ${topCategory.category}` : '暂无类别数据' },
      { title: '主要状态', value: `${firstStatus?.status || '-'}`, delta: '状态聚合', note: firstStatus ? `数量 ${firstStatus.count}` : '暂无状态数据' },
      { title: '下钻记录', value: `${drilldownData.value.total || 0}`, delta: '明细联动', note: '支持继续查看文物明细' }
    ]
  }

  if (activeTab.value === 'process') {
    const durationTop = (processStats.value.durationStats || []).slice().sort((a, b) => Number(b.avgDays || 0) - Number(a.avgDays || 0))[0]
    const riskTop = (processStats.value.riskStats || []).slice().sort((a, b) => Number(b.riskValue || 0) - Number(a.riskValue || 0))[0]
    return [
      { title: '流程类型', value: `${processStats.value.durationStats?.length || 0}`, delta: '真实统计', note: '基于流程业务表聚合得到' },
      { title: '最长环节', value: durationTop ? `${durationTop.processName}` : '-', delta: durationTop ? `${durationTop.avgDays} 天` : '暂无', note: '平均处理时长最高的流程' },
      { title: '趋势月份', value: `${processStats.value.monthlyTrend?.length || 0}`, delta: '最近 6 月', note: '用于观察创建与完成变化' },
      { title: '最高风险流程', value: riskTop ? `${riskTop.processName}` : '-', delta: riskTop ? `${riskTop.riskValue}` : '0', note: '根据风险统计接口生成', warning: true }
    ]
  }

  if (activeTab.value === 'exhibition') {
    const topExhibition = (exhibitionStats.value.exhibitionEffects || [])[0]
    return [
      { title: '展览总数', value: `${exhibitionStats.value.totalExhibitions || 0}`, delta: '真实统计', note: '来自展览统计接口' },
      { title: '平均反馈', value: `${exhibitionStats.value.averageFeedback || 0}`, delta: '反馈评分', note: '反映展览整体反馈质量' },
      { title: '安全事件', value: `${exhibitionStats.value.safetyIncidents || 0}`, delta: '风险提示', note: '用于展览运营安全复盘', warning: true },
      { title: '最高客流展览', value: topExhibition ? `${topExhibition.exhibitionName}` : '-', delta: '自动识别', note: topExhibition ? `客流 ${topExhibition.visitorCount}` : '暂无展览成效数据' }
    ]
  }

  return [
    { title: '访客总量', value: `${visitorStats.value.totalVisitors || 0}`, delta: '真实统计', note: '来自访客预约统计接口' },
    { title: '待处理预约', value: `${visitorStats.value.pendingAppointments || 0}`, delta: '当前待办', note: '需要人工处理的预约数量', warning: true },
    { title: '趋势月份', value: `${visitorStats.value.visitorTrend?.length || 0}`, delta: '趋势聚合', note: '可用于预约运营复盘' },
    { title: '最高月客流', value: `${Math.max(...((visitorStats.value.visitorTrend || []).map((item) => Number(item.value) || 0)), 0)}`, delta: '自动计算', note: '根据访客趋势自动识别' }
  ]
})

const aiInsights = computed(() => {
  const suggestions = Array.isArray(aiAnalysis.value?.suggestions) ? aiAnalysis.value.suggestions : []
  if (suggestions.length) {
    return suggestions.slice(0, 3).map((item, index) => ({
      title: aiAnalysis.value.title || `AI 洞察 ${index + 1}`,
      desc: item
    }))
  }

  if (aiAnalysis.value?.summary) {
    return [
      {
        title: aiAnalysis.value.title || 'AI 分析',
        desc: aiAnalysis.value.summary
      }
    ]
  }

  return [
    { title: 'AI 洞察暂不可用', desc: '当前 AI 分析接口未返回内容，系统已回退为稳定提示模式。' }
  ]
})

const resolveAnalysisType = () => {
  if (activeTab.value === 'exhibition' || activeTab.value === 'visitor') {
    return 'exhibition_trend'
  }
  return 'inventory_optimization'
}

const selectQuickRange = (value) => {
  filters.quickRange = value
  const today = new Date()
  const end = today.toISOString().slice(0, 10)
  const start = new Date(today)

  if (value === 'week') start.setDate(today.getDate() - 6)
  if (value === 'month') start.setDate(today.getDate() - 29)
  if (value === 'quarter') start.setDate(today.getDate() - 89)
  if (value === 'year') start.setDate(today.getDate() - 364)

  filters.range = [start.toISOString().slice(0, 10), end]
}

const resetFilters = () => {
  filters.quickRange = 'month'
  filters.range = []
  filters.categories = []
  filters.status = ''
  loadAnalytics()
}

const handleDrilldown = (path) => {
  ElMessage.info('正在打开对应明细页面')
  router.push(path)
}

const exportAs = async (format) => {
  try {
    const params = buildStatisticsParams()
    if (format === 'Excel') {
      if (reportConfig.metrics.includes('exhibition')) {
        const res = await reportApi.exportExhibitionStatsExcel(params)
        downloadResponseBlob(res, 'exhibition-stats.xlsx')
      } else {
        const res = await reportApi.exportArtifactStatsExcel(params)
        downloadResponseBlob(res, 'artifact-stats.xlsx')
      }
    } else {
      const res = await reportApi.exportArtifactPdf(params)
      downloadResponseBlob(res, 'artifact-drilldown.pdf')
    }
    ElMessage.success(`${format} 报表导出成功`)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || `${format} 报表导出失败`)
  }
}

const toggleFullscreen = async () => {
  if (!document.fullscreenElement) {
    await pageRef.value?.requestFullscreen?.()
    fullscreen.value = true
  } else {
    await document.exitFullscreen?.()
    fullscreen.value = false
  }
  resizeCharts()
}

const renderPie = async (elRef, chartRef, data, nameKey, valueKey) => {
  if (!elRef.value) return chartRef
  const echarts = await loadEcharts()
  if (!chartRef) chartRef = echarts.init(elRef.value)
  chartRef.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['40%', '68%'],
        data: data.map((item) => ({ name: item[nameKey], value: Number(item[valueKey] || 0) }))
      }
    ]
  })
  return chartRef
}

const renderBar = async (elRef, chartRef, labels, series) => {
  if (!elRef.value) return chartRef
  const echarts = await loadEcharts()
  if (!chartRef) chartRef = echarts.init(elRef.value)
  chartRef.setOption({
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    grid: { left: 24, right: 24, top: 24, bottom: 54, containLabel: true },
    xAxis: { type: 'category', data: labels },
    yAxis: { type: 'value' },
    series
  })
  return chartRef
}

const renderRadar = async (elRef, chartRef, data) => {
  if (!elRef.value) return chartRef
  const echarts = await loadEcharts()
  if (!chartRef) chartRef = echarts.init(elRef.value)
  const indicators = data.map((item) => ({ name: item.processName || item.name, max: Math.max(...data.map((v) => Number(v.riskValue || 0)), 1) }))
  chartRef.setOption({
    tooltip: {},
    radar: { indicator: indicators },
    series: [{ type: 'radar', data: [{ value: data.map((item) => Number(item.riskValue || 0)), name: '流程风险' }] }]
  })
  return chartRef
}

const renderScatter = async (elRef, chartRef, data) => {
  if (!elRef.value) return chartRef
  const echarts = await loadEcharts()
  if (!chartRef) chartRef = echarts.init(elRef.value)
  chartRef.setOption({
    tooltip: { trigger: 'item' },
    grid: { left: 24, right: 24, top: 24, bottom: 40, containLabel: true },
    xAxis: { type: 'value', name: '反馈评分' },
    yAxis: { type: 'value', name: '客流' },
    series: [
      {
        type: 'scatter',
        symbolSize: 18,
        data: data.map((item) => [Number(item.feedbackScore || 0), Number(item.visitorCount || 0), item.exhibitionName])
      }
    ]
  })
  return chartRef
}

const renderHeatmap = async () => {
  if (!heatmapChartRef.value) return
  const echarts = await loadEcharts()
  if (!heatmapChart) heatmapChart = echarts.init(heatmapChartRef.value)
  const xAxis = heatmapStats.value.xAxis || []
  const yAxis = heatmapStats.value.yAxis || []
  const data = heatmapStats.value.matrix || []
  heatmapChart.setOption({
    tooltip: { position: 'top' },
    grid: { top: 30, bottom: 30, left: 80, right: 20 },
    xAxis: { type: 'category', data: xAxis, splitArea: { show: true } },
    yAxis: { type: 'category', data: yAxis, splitArea: { show: true } },
    visualMap: {
      min: 0,
      max: Math.max(...data.map((item) => Number(item[2] || 0)), 1),
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: 0
    },
    series: [
      {
        type: 'heatmap',
        data,
        label: { show: true }
      }
    ]
  })
}

const renderCharts = async () => {
  if (activeTab.value === 'artifact') {
    primaryChart = await renderPie(primaryChartRef, primaryChart, artifactStats.value.categoryDistribution || [], 'category', 'count')
    secondaryChart = await renderPie(secondaryChartRef, secondaryChart, artifactStats.value.statusDistribution || [], 'status', 'count')
    tertiaryChart = await renderBar(
      tertiaryChartRef,
      tertiaryChart,
      (drilldownData.value.records || []).slice(0, 8).map((item) => item.name || item.code || '文物'),
      [{ name: '记录数', type: 'bar', data: (drilldownData.value.records || []).slice(0, 8).map(() => 1) }]
    )
  } else if (activeTab.value === 'process') {
    primaryChart = await renderBar(
      primaryChartRef,
      primaryChart,
      (processStats.value.durationStats || []).map((item) => item.processName),
      [{ name: '平均耗时', type: 'bar', data: (processStats.value.durationStats || []).map((item) => Number(item.avgDays || 0)) }]
    )
    secondaryChart = await renderBar(
      secondaryChartRef,
      secondaryChart,
      (processStats.value.monthlyTrend || []).map((item) => item.month),
      [
        { name: '创建量', type: 'line', smooth: true, data: (processStats.value.monthlyTrend || []).map((item) => Number(item.createdCount || 0)) },
        { name: '完成量', type: 'line', smooth: true, data: (processStats.value.monthlyTrend || []).map((item) => Number(item.completedCount || 0)) }
      ]
    )
    tertiaryChart = await renderRadar(tertiaryChartRef, tertiaryChart, processStats.value.riskStats || [])
  } else if (activeTab.value === 'exhibition') {
    primaryChart = await renderBar(
      primaryChartRef,
      primaryChart,
      (exhibitionStats.value.exhibitionEffects || []).map((item) => item.exhibitionName),
      [{ name: '客流', type: 'bar', data: (exhibitionStats.value.exhibitionEffects || []).map((item) => Number(item.visitorCount || 0)) }]
    )
    secondaryChart = await renderBar(
      secondaryChartRef,
      secondaryChart,
      (exhibitionStats.value.exhibitionEffects || []).map((item) => item.exhibitionName),
      [
        { name: '反馈评分', type: 'bar', data: (exhibitionStats.value.exhibitionEffects || []).map((item) => Number(item.feedbackScore || 0)) },
        { name: '安全事件', type: 'bar', data: (exhibitionStats.value.exhibitionEffects || []).map((item) => Number(item.safetyIncidents || 0)) }
      ]
    )
    tertiaryChart = await renderScatter(tertiaryChartRef, tertiaryChart, exhibitionStats.value.exhibitionEffects || [])
  } else {
    primaryChart = await renderBar(
      primaryChartRef,
      primaryChart,
      (visitorStats.value.visitorTrend || []).map((item) => item.month || item.date),
      [{ name: '访客数量', type: 'line', smooth: true, data: (visitorStats.value.visitorTrend || []).map((item) => Number(item.value || 0)) }]
    )
    secondaryChart = await renderBar(
      secondaryChartRef,
      secondaryChart,
      ['待处理预约', '总访客'],
      [{ name: '数量', type: 'bar', data: [Number(visitorStats.value.pendingAppointments || 0), Number(visitorStats.value.totalVisitors || 0)] }]
    )
    tertiaryChart = await renderBar(
      tertiaryChartRef,
      tertiaryChart,
      (visitorStats.value.visitorTrend || []).map((item) => item.month || item.date),
      [{ name: '访客波动', type: 'bar', data: (visitorStats.value.visitorTrend || []).map((item) => Number(item.value || 0)) }]
    )
  }

  await renderHeatmap()
}

const resizeCharts = () => {
  primaryChart?.resize()
  secondaryChart?.resize()
  tertiaryChart?.resize()
  heatmapChart?.resize()
}

const loadAnalytics = async () => {
  loading.value = true
  try {
    const params = buildStatisticsParams()
    const [dashboard, artifact, exhibition, visitor, process, heatmap, drilldown, ai] = await Promise.all([
      statisticsApi.getDashboard().catch(() => ({})),
      statisticsApi.getArtifactStats(params).catch(() => ({ categoryDistribution: [], statusDistribution: [], totalCount: 0 })),
      statisticsApi.getExhibitionStats(params).catch(() => ({ exhibitionEffects: [], totalExhibitions: 0, averageFeedback: 0, safetyIncidents: 0 })),
      statisticsApi.getVisitorStats(params).catch(() => ({ visitorTrend: [], totalVisitors: 0, pendingAppointments: 0 })),
      statisticsApi.getProcessStats(params).catch(() => ({ durationStats: [], monthlyTrend: [], riskStats: [] })),
      statisticsApi.getSpaceHeatmapStats().catch(() => ({ xAxis: [], yAxis: [], matrix: [] })),
      statisticsApi.drillDownArtifacts(params).catch(() => ({ records: [], total: 0 })),
      statisticsApi.getAIAnalysis(resolveAnalysisType()).catch(() => ({
        queryType: '',
        title: '',
        summary: '',
        trend: '',
        suggestions: [],
        cards: []
      }))
    ])

    dashboardData.value = dashboard
    artifactStats.value = artifact
    exhibitionStats.value = exhibition
    visitorStats.value = visitor
    processStats.value = process
    heatmapStats.value = heatmap
    drilldownData.value = drilldown
    aiAnalysis.value = ai || {
      queryType: '',
      title: '',
      summary: '',
      trend: '',
      suggestions: [],
      cards: []
    }

    await nextTick()
    await renderCharts()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载统计数据失败')
  } finally {
    loading.value = false
  }
}

watch(activeTab, async () => {
  await loadAnalytics()
})

onMounted(async () => {
  selectQuickRange('month')
  await loadAnalytics()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  primaryChart?.dispose()
  secondaryChart?.dispose()
  tertiaryChart?.dispose()
  heatmapChart?.dispose()
})
</script>

<style scoped>
.analytics-page {
  display: grid;
  gap: 20px;
}

.analytics-page.is-fullscreen {
  padding: 20px;
  background: #0f172a;
}

.analytics-page.is-fullscreen .page-card {
  background: rgba(255, 255, 255, 0.96);
}

.analytics-hero,
.filter-card {
  padding: 22px;
}

.analytics-hero {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--museum-color-primary);
  font-weight: 600;
}

.analytics-hero h1 {
  margin: 12px 0 10px;
  color: var(--museum-text-strong);
}

.analytics-hero p {
  margin: 0;
  color: var(--museum-text-secondary);
  line-height: 1.8;
  max-width: 780px;
}

.hero-actions,
.quick-range-buttons,
.toolbar-actions,
.report-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-grid {
  display: grid;
  grid-template-columns: 1.2fr 1fr 1fr 200px auto;
  gap: 14px;
  align-items: center;
}

.quick-range {
  display: grid;
  gap: 8px;
}

.quick-range > span,
.inline-tip {
  color: var(--museum-text-secondary);
  font-size: 12px;
}

.metric-tabs,
.summary-grid,
.chart-grid,
.insight-grid {
  display: grid;
  gap: 16px;
}

.metric-tabs {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.metric-tab {
  border: 1px solid var(--museum-border-light);
  border-radius: 18px;
  background: #fff;
  padding: 16px;
  text-align: left;
  cursor: pointer;
}

.metric-tab.active {
  border-color: rgba(46, 90, 90, 0.3);
  box-shadow: 0 0 0 1px rgba(46, 90, 90, 0.15);
}

.metric-tab strong {
  display: block;
  color: var(--museum-text-primary);
}

.metric-tab span {
  display: block;
  margin-top: 8px;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.summary-card,
.chart-card,
.insight-card {
  padding: 20px;
}

.summary-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  color: var(--museum-text-secondary);
  font-size: 13px;
}

.summary-top em {
  font-style: normal;
  color: var(--museum-color-primary);
}

.summary-top em.warning {
  color: var(--museum-color-error);
}

.summary-card strong {
  display: block;
  margin-top: 14px;
  font-size: 30px;
  color: var(--museum-text-primary);
}

.summary-card p {
  margin: 10px 0 0;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.chart-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.card-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 14px;
}

.card-header h3 {
  margin: 0;
  color: var(--museum-text-primary);
}

.card-header p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.chart-box {
  min-height: 320px;
}

.insight-grid {
  grid-template-columns: 1.2fr 1fr;
}

.ai-list {
  display: grid;
  gap: 12px;
}

.ai-item {
  padding: 14px 16px;
  border-radius: 14px;
  background: var(--museum-bg-subtle);
}

.ai-item strong {
  color: var(--museum-text-primary);
}

.ai-item p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.report-config {
  display: grid;
  gap: 12px;
}

@media (max-width: 1200px) {
  .filter-grid,
  .metric-tabs,
  .summary-grid,
  .chart-grid,
  .insight-grid {
    grid-template-columns: 1fr;
  }
}
</style>


