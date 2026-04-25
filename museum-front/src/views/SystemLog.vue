<template>
  <div class="system-page">
    <section class="system-hero page-card">
      <div>
        <div class="eyebrow">Audit Logs</div>
        <h1>操作日志审计</h1>
        <p>
          当前页面已接入真实操作日志分页接口，支持按时间范围、操作人和操作类型筛选，
          并查看请求参数、接口地址、耗时与错误信息。</p>
      </div>
      <div class="hero-metrics">
        <div class="hero-metric"><span>当前结果</span><strong>{{ pagination.total }}</strong></div>
        <div class="hero-metric"><span>成功记录</span><strong>{{ successCount }}</strong></div>
        <div class="hero-metric"><span>失败记录</span><strong>{{ failureCount }}</strong></div>
      </div>
    </section>

    <section class="toolbar-card page-card">
      <div class="filters-grid wide">
        <el-date-picker
          v-model="filters.range"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
        <el-input v-model="filters.operator" placeholder="操作人 / 用户名" clearable @keyup.enter="loadLogs(1)" />
        <el-select v-model="filters.type" placeholder="操作类型" clearable>
          <el-option label="新增" value="ADD" />
          <el-option label="编辑" value="UPDATE" />
          <el-option label="删除" value="DELETE" />
          <el-option label="登录" value="LOGIN" />
          <el-option label="导出" value="EXPORT" />
        </el-select>
        <div class="toolbar-actions">
          <el-button type="primary" :loading="loading" @click="loadLogs(1)">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
          <el-button v-permission="PERMISSIONS.REPORT_EXPORT" @click="exportLogs">导出日志</el-button>
        </div>
      </div>
    </section>

    <section class="table-card page-card">
      <el-table v-loading="loading" :data="logs" stripe>
        <el-table-column prop="time" label="操作时间" width="180" />
        <el-table-column prop="operatorName" label="操作人" width="140" />
        <el-table-column prop="ip" label="IP 地址" width="140" />
        <el-table-column prop="module" label="操作模块" width="140" />
        <el-table-column prop="typeLabel" label="操作类型" width="110" />
        <el-table-column prop="content" label="操作内容" min-width="260" />
        <el-table-column prop="resultLabel" label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === 'SUCCESS' ? 'success' : 'danger'">{{ row.resultLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="durationLabel" label="耗时" width="100" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link @click="openDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadLogs"
          @size-change="handleSizeChange"
        />
      </div>
    </section>

    <el-dialog v-model="detailVisible" title="日志详情" width="860px">
      <template v-if="currentLog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="操作人">{{ currentLog.operatorName }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentLog.username }}</el-descriptions-item>
          <el-descriptions-item label="操作模块">{{ currentLog.module || '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">{{ currentLog.typeLabel }}</el-descriptions-item>
          <el-descriptions-item label="请求地址">{{ currentLog.url || '-' }}</el-descriptions-item>
          <el-descriptions-item label="请求 IP">{{ currentLog.ip || '-' }}</el-descriptions-item>
          <el-descriptions-item label="执行耗时">{{ currentLog.durationLabel }}</el-descriptions-item>
          <el-descriptions-item label="执行结果">{{ currentLog.resultLabel }}</el-descriptions-item>
          <el-descriptions-item label="操作描述" :span="2">{{ currentLog.content || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="compare-grid">
          <div class="compare-card">
            <h4>请求参数</h4>
            <pre>{{ currentLog.requestParams }}</pre>
          </div>
          <div class="compare-card">
            <h4>异常信息 / 返回摘要</h4>
            <pre>{{ currentLog.errorMsg }}</pre>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { operationLogApi } from '@/api/system'
import { PERMISSIONS } from '@/constants/permissions'
import { downloadResponseBlob } from '@/utils/download'

const loading = ref(false)
const detailVisible = ref(false)
const currentLog = ref(null)
const logs = ref([])

const filters = reactive({
  range: [],
  operator: '',
  type: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const typeLabelMap = {
  ADD: '新增',
  UPDATE: '编辑',
  DELETE: '删除',
  LOGIN: '登录',
  EXPORT: '导出'
}

const resultLabelMap = {
  SUCCESS: '成功',
  FAIL: '失败'
}

const successCount = computed(() => logs.value.filter((item) => item.result === 'SUCCESS').length)
const failureCount = computed(() => logs.value.filter((item) => item.result !== 'SUCCESS').length)

const mapLog = (item) => ({
  ...item,
  typeLabel: typeLabelMap[item.type] || item.type || '-',
  resultLabel: resultLabelMap[item.result] || item.result || '-',
  durationLabel: item.duration ? `${item.duration} ms` : '-',
  requestParams: item.requestParams || '-',
  errorMsg: item.errorMsg || '-',
  content: item.content || '-',
  operatorName: item.operatorName || '-',
  username: item.username || '-'
})

const buildParams = () => ({
  current: pagination.current,
  size: pagination.size,
  operator: filters.operator || undefined,
  type: filters.type || undefined,
  startDate: filters.range?.[0] || undefined,
  endDate: filters.range?.[1] || undefined
})

const loadLogs = async (page = pagination.current) => {
  loading.value = true
  pagination.current = page
  try {
    const res = await operationLogApi.getPage(buildParams())
    logs.value = (res.records || []).map(mapLog)
    pagination.total = res.total || 0
  } catch (error) {
    logs.value = []
    pagination.total = 0
    ElMessage.error(error?.response?.data?.message || '加载操作日志失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadLogs(1)
}

const resetFilters = () => {
  filters.range = []
  filters.operator = ''
  filters.type = ''
  loadLogs(1)
}

const openDetail = async (row) => {
  try {
    const res = await operationLogApi.getDetail(row.id)
    currentLog.value = mapLog(res)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载日志详情失败')
  }
}

const exportLogs = async () => {
  try {
    const res = await operationLogApi.export({
      operator: filters.operator || undefined,
      type: filters.type || undefined,
      startDate: filters.range?.[0] || undefined,
      endDate: filters.range?.[1] || undefined
    })
    downloadResponseBlob(res, 'operation-log-export.csv')
    ElMessage.success('审计日志导出成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '审计日志导出失败')
  }
}

onMounted(() => {
  loadLogs(1)
})
</script>

<style scoped>
.system-page {
  display: grid;
  gap: 24px;
}

.system-hero,
.toolbar-card,
.table-card {
  padding: 22px;
}

.system-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(280px, 0.9fr);
  gap: 24px;
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

.system-hero h1 {
  margin: 12px 0 10px;
  color: var(--museum-text-strong);
}

.system-hero p {
  margin: 0;
  color: var(--museum-text-secondary);
  line-height: 1.8;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.hero-metric {
  padding: 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.76);
}

.hero-metric span {
  display: block;
  font-size: 12px;
  color: var(--museum-text-secondary);
}

.hero-metric strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: var(--museum-text-strong);
}

.filters-grid.wide {
  display: grid;
  grid-template-columns: minmax(300px, 1.1fr) 220px 180px auto;
  gap: 14px;
  align-items: start;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.compare-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.compare-card {
  padding: 16px;
  border-radius: 16px;
  background: var(--museum-bg-subtle);
}

.compare-card h4 {
  margin: 0 0 12px;
  color: var(--museum-text-strong);
}

.compare-card pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, monospace;
  color: var(--museum-text-secondary);
}

@media (max-width: 1100px) {
  .system-hero,
  .hero-metrics,
  .filters-grid.wide,
  .compare-grid {
    grid-template-columns: 1fr;
  }

  .toolbar-actions,
  .pagination-wrap {
    justify-content: flex-start;
  }
}
</style>


