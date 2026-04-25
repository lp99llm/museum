<template>
  <div class="system-page">
    <section class="system-hero page-card">
      <div>
        <div class="eyebrow">Security Center</div>
        <h1>数据安全中心</h1>
        <p>集中查看备份状态、敏感数据加密状态和安全报告摘要，页面中的操作入口均会发起真实后端请求。</p>
      </div>
      <div class="hero-metrics">
        <div class="hero-metric"><span>备份接口</span><strong>{{ overview.backupInterfaceStatus || '-' }}</strong></div>
        <div class="hero-metric"><span>加密状态</span><strong>{{ overview.encryptionStatus || '-' }}</strong></div>
        <div class="hero-metric"><span>扫描报告</span><strong>{{ overview.scanReportStatus || '-' }}</strong></div>
      </div>
    </section>

    <section class="security-grid" v-loading="loading">
      <article class="security-card page-card">
        <div class="card-header">
          <div>
            <h3>数据备份</h3>
            <p>可手动触发备份和恢复流程，并查看当前备份能力与条目概览。</p>
          </div>
          <el-button v-permission="PERMISSIONS.SECURITY_VIEW" type="primary" @click="backupNow">立即备份</el-button>
        </div>
        <div class="backup-switch">
          <span>自动备份</span>
          <el-switch :model-value="Boolean(overview.autoBackupEnabled)" disabled />
        </div>
        <el-table :data="overview.backupItems || []" stripe>
          <el-table-column prop="name" label="项目" min-width="160" />
          <el-table-column prop="status" label="当前状态" width="140" />
          <el-table-column prop="desc" label="说明" min-width="260" />
          <el-table-column label="操作" width="140">
            <template #default="{ row }">
              <el-button link @click="restoreBackup(row)">恢复</el-button>
            </template>
          </el-table-column>
        </el-table>
      </article>

      <article class="security-card page-card">
        <div class="card-header">
          <div>
            <h3>敏感数据加密状态</h3>
            <p>展示保险信息、来源详情等敏感字段的当前治理状态。</p>
          </div>
        </div>
        <div class="encrypt-list">
          <div v-for="item in overview.encryptionFields || []" :key="item.field" class="encrypt-item">
            <div>
              <strong>{{ item.field }}</strong>
              <p>{{ item.desc }}</p>
            </div>
            <el-tag :type="item.statusType || 'warning'">{{ item.status }}</el-tag>
          </div>
        </div>
      </article>

      <article class="security-card page-card full">
        <div class="card-header">
          <div>
            <h3>安全扫描报告</h3>
            <p>通过后端统一提供报告入口和摘要，可直接查看当前扫描状态与下一步建议。</p>
          </div>
          <el-button v-permission="PERMISSIONS.SECURITY_VIEW" @click="openSecurityReport">查看报告</el-button>
        </div>
        <div class="report-panel">
          <div class="report-item">
            <span>接口状态</span>
            <strong>{{ overview.securityReport?.interfaceStatus || '-' }}</strong>
          </div>
          <div class="report-item">
            <span>下一步建议</span>
            <strong>{{ overview.securityReport?.nextStep || '-' }}</strong>
          </div>
          <div class="report-item">
            <span>当前结论</span>
            <strong>{{ overview.securityReport?.conclusion || '-' }}</strong>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { securityApi } from '@/api/system'
import { PERMISSIONS } from '@/constants/permissions'

const loading = ref(false)
const overview = ref({
  backupInterfaceStatus: '',
  encryptionStatus: '',
  scanReportStatus: '',
  autoBackupEnabled: false,
  backupItems: [],
  encryptionFields: [],
  securityReport: null
})

const loadOverview = async () => {
  loading.value = true
  try {
    overview.value = await securityApi.getOverview()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载安全中心状态失败')
  } finally {
    loading.value = false
  }
}

const backupNow = async () => {
  try {
    const result = await securityApi.triggerManualBackup()
    ElMessage.success(result?.result || '手动备份请求已提交')
    await loadOverview()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '触发手动备份失败')
  }
}

const restoreBackup = async (item) => {
  try {
    const targetFileName = item?.name
    if (!targetFileName) {
      ElMessage.warning('当前没有可用的备份文件')
      return
    }
    const result = await securityApi.triggerRestore({ fileName: targetFileName })
    ElMessage.success(result?.result || '恢复请求已提交')
    await loadOverview()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '触发恢复流程失败')
  }
}

const openSecurityReport = async () => {
  try {
    const result = await securityApi.getSecurityReportEntry()
    ElMessage.info(result?.result || result?.nextStep || '安全扫描报告入口已打开')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '获取安全报告入口失败')
  }
}

onMounted(() => {
  loadOverview()
})
</script>

<style scoped>
.system-page {
  display: grid;
  gap: 24px;
}

.system-hero {
  padding: 22px;
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

.security-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.security-card {
  padding: 20px;
}

.security-card.full {
  grid-column: 1 / -1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  color: var(--museum-text-strong);
}

.card-header p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
}

.backup-switch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 14px 16px;
  border-radius: 14px;
  background: var(--museum-bg-subtle);
}

.encrypt-list {
  display: grid;
  gap: 12px;
}

.encrypt-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 14px;
  padding: 16px;
  border-radius: 14px;
  background: var(--museum-bg-subtle);
}

.encrypt-item strong {
  color: var(--museum-text-strong);
}

.encrypt-item p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
}

.report-panel {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.report-item {
  padding: 16px;
  border-radius: 14px;
  background: var(--museum-bg-subtle);
}

.report-item span {
  display: block;
  font-size: 12px;
  color: var(--museum-text-secondary);
}

.report-item strong {
  display: block;
  margin-top: 10px;
  color: var(--museum-text-strong);
  line-height: 1.6;
}

@media (max-width: 1100px) {
  .system-hero,
  .hero-metrics,
  .security-grid,
  .report-panel {
    grid-template-columns: 1fr;
  }
}
</style>


