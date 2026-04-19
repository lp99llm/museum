<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon artifact-icon">
            <el-icon :size="40"><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboardData.totalArtifacts || 0 }}</div>
            <div class="stat-label">文物总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon exhibition-icon">
            <el-icon :size="40"><Box /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboardData.totalExhibitions || 0 }}</div>
            <div class="stat-label">展览总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon visitor-icon">
            <el-icon :size="40"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboardData.totalVisitors || 0 }}</div>
            <div class="stat-label">参观人数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon appointment-icon">
            <el-icon :size="40"><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboardData.pendingAppointments || 0 }}</div>
            <div class="stat-label">待审核预约</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>文物类别分布</span>
              <el-button type="primary" size="small" @click="handleDrillDown('category')">数据钻取</el-button>
            </div>
          </template>
          <div ref="categoryChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>文物状态分布</span>
              <el-button type="primary" size="small" @click="handleDrillDown('status')">数据钻取</el-button>
            </div>
          </template>
          <div ref="statusChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>月度参观趋势</span>
              <el-button type="primary" size="small" @click="exportExcel('visitor')">导出Excel</el-button>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>展览成效TOP5</span>
          </template>
          <div ref="exhibitionChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>AI智能分析</span>
              <el-space>
                <el-select v-model="aiQueryType" placeholder="选择分析类型" style="width: 200px">
                  <el-option label="展览热度趋势预测" value="exhibition_trend" />
                  <el-option label="库存结构优化建议" value="inventory_optimization" />
                </el-select>
                <el-button type="primary" @click="getAIAnalysis">生成分析</el-button>
              </el-space>
            </div>
          </template>
          <div v-loading="aiLoading">
            <pre v-if="aiAnalysisResult" class="ai-result">{{ aiAnalysisResult }}</pre>
            <el-empty v-else description="点击上方按钮生成AI智能分析" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>数据筛选</span>
              <el-button @click="resetQuery">重置</el-button>
            </div>
          </template>
          <el-form :inline="true" :model="queryParams">
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="类别">
              <el-select v-model="queryParams.category" placeholder="请选择类别" clearable>
                <el-option
                  v-for="item in categoryOptions"
                  :key="item.category"
                  :label="item.category"
                  :value="item.category"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                <el-option label="在库" value="IN_STORAGE" />
                <el-option label="展示中" value="ON_DISPLAY" />
                <el-option label="修复中" value="UNDER_REPAIR" />
                <el-option label="外借中" value="LENT_OUT" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery">查询</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="drillDownVisible" title="数据钻取" width="90%">
      <el-table :data="drillDownData.records" border height="400">
        <el-table-column
          v-for="header in drillDownData.headers"
          :key="header"
          :prop="header"
          :label="header"
        />
      </el-table>
      <template #footer>
        <el-button @click="drillDownVisible = false">关闭</el-button>
        <el-button type="primary" @click="exportDrillDownData">导出数据</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi, reportApi } from '@/api/statistics'
import { ElMessage } from 'element-plus'
import { Collection, Box, User, Calendar } from '@element-plus/icons-vue'

const categoryChartRef = ref(null)
const statusChartRef = ref(null)
const trendChartRef = ref(null)
const exhibitionChartRef = ref(null)

const dashboardData = ref({})
const queryParams = reactive({
  category: '',
  status: ''
})
const dateRange = ref([])
const categoryOptions = ref([])

const aiQueryType = ref('exhibition_trend')
const aiLoading = ref(false)
const aiAnalysisResult = ref('')

const drillDownVisible = ref(false)
const drillDownData = ref({ headers: [], records: [], total: 0 })

let categoryChart = null
let statusChart = null
let trendChart = null
let exhibitionChart = null

const loadDashboard = async () => {
  try {
    const res = await statisticsApi.getDashboard()
    if (res.code === 200) {
      dashboardData.value = res.data || {}
      categoryOptions.value = res.data.categoryDistribution || []
      nextTick(() => {
        initCategoryChart()
        initStatusChart()
        initTrendChart()
        initExhibitionChart()
      })
    }
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

const initCategoryChart = () => {
  if (!categoryChartRef.value) return
  categoryChart = echarts.init(categoryChartRef.value)

  const data = (dashboardData.value.categoryDistribution || []).map(item => ({
    name: item.category,
    value: item.count
  }))

  categoryChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: '60%',
      data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })
}

const initStatusChart = () => {
  if (!statusChartRef.value) return
  statusChart = echarts.init(statusChartRef.value)

  const data = (dashboardData.value.statusDistribution || []).map(item => ({
    name: item.status,
    value: item.count
  }))

  statusChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })
}

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)

  const data = dashboardData.value.visitorTrend || []
  const months = data.map(item => item.month)
  const values = data.map(item => item.value)

  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value' },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      areaStyle: { opacity: 0.3 },
      lineStyle: { width: 3 },
      itemStyle: { color: '#409EFF' }
    }]
  })
}

const initExhibitionChart = () => {
  if (!exhibitionChartRef.value) return
  exhibitionChart = echarts.init(exhibitionChartRef.value)

  const data = (dashboardData.value.exhibitionEffects || []).slice(0, 5)

  exhibitionChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: data.map(item => item.exhibitionName) },
    series: [{
      type: 'bar',
      data: data.map(item => item.visitorCount),
      itemStyle: { color: '#67C23A' }
    }]
  })
}

const handleQuery = () => {
  loadDashboard()
}

const resetQuery = () => {
  dateRange.value = []
  queryParams.category = ''
  queryParams.status = ''
  loadDashboard()
}

const handleDrillDown = async (type) => {
  try {
    const params = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await statisticsApi.drillDownArtifacts(params)
    if (res.code === 200) {
      drillDownData.value = res.data || { headers: [], records: [], total: 0 }
      drillDownVisible.value = true
    }
  } catch (error) {
    ElMessage.error('数据钻取失败')
  }
}

const getAIAnalysis = async () => {
  if (!aiQueryType.value) {
    ElMessage.warning('请选择分析类型')
    return
  }
  aiLoading.value = true
  try {
    const res = await statisticsApi.getAIAnalysis(aiQueryType.value)
    if (res.code === 200) {
      aiAnalysisResult.value = res.data
    }
  } catch (error) {
    ElMessage.error('AI分析生成失败')
  } finally {
    aiLoading.value = false
  }
}

const exportExcel = async (type) => {
  try {
    const params = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await reportApi.exportArtifactStatsExcel(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '文物统计报表.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const exportDrillDownData = async () => {
  try {
    const params = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await reportApi.exportArtifactExcel(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '文物明细报表.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  loadDashboard()
  window.addEventListener('resize', () => {
    categoryChart?.resize()
    statusChart?.resize()
    trendChart?.resize()
    exhibitionChart?.resize()
  })
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: #fff;
}

.artifact-icon { background: linear-gradient(135deg, #409EFF, #66B1FF); }
.exhibition-icon { background: linear-gradient(135deg, #67C23A, #85CE61); }
.visitor-icon { background: linear-gradient(135deg, #E6A23C, #F56C6C); }
.appointment-icon { background: linear-gradient(135deg, #909399, #C0C4CC); }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ai-result {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  white-space: pre-wrap;
  line-height: 1.8;
}
</style>
