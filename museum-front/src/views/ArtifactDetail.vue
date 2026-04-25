<template>
  <div class="detail-page">
    <el-skeleton v-if="loading" :rows="10" animated />

    <template v-else-if="artifact">
      <section class="detail-layout">
        <div class="media-column page-card">
          <div class="main-media">
            <img :src="activeImage" :alt="artifact.name" class="main-image">
          </div>

          <div class="thumb-strip">
            <button
              v-for="(item, index) in mediaGallery"
              :key="`${item.label}-${index}`"
              type="button"
              class="thumb-card"
              :class="{ active: activeImage === item.src }"
              @click="activeImage = item.src"
            >
              <img :src="item.src" :alt="item.label">
              <span>{{ item.label }}</span>
            </button>
          </div>
        </div>

        <div class="info-column">
          <section class="summary-card page-card">
            <div class="summary-header">
              <div>
                <div class="section-kicker">Artifact Record</div>
                <h2>{{ artifact.name }}</h2>
                <p>{{ artifact.summary }}</p>
              </div>
              <div class="status-tag" :class="statusClass(artifact.preservationStatus)">
                {{ artifact.preservationStatus }}
              </div>
            </div>

            <div class="meta-grid">
              <div class="meta-item"><span>文物编号</span><strong>{{ artifact.code || '-' }}</strong></div>
              <div class="meta-item"><span>类别</span><strong>{{ artifact.category || '-' }}</strong></div>
              <div class="meta-item"><span>年代</span><strong>{{ artifact.era || '-' }}</strong></div>
              <div class="meta-item"><span>材质</span><strong>{{ artifact.material }}</strong></div>
              <div class="meta-item"><span>尺寸</span><strong>{{ artifact.size }}</strong></div>
              <div class="meta-item"><span>重量</span><strong>{{ artifact.weight }}</strong></div>
              <div class="meta-item"><span>来源</span><strong>{{ artifact.source }}</strong></div>
              <div class="meta-item"><span>鉴定等级</span><strong>{{ artifact.appraisalLevel }}</strong></div>
              <div class="meta-item"><span>存放位置</span><strong>{{ artifact.location }}</strong></div>
              <div class="meta-item"><span>当前状态</span><strong>{{ currentStateLabel(artifact.currentState) }}</strong></div>
            </div>

            <div class="insurance-panel">
              <div>
                <div class="section-title">保险信息</div>
                <p class="helper-text">信息来源于文物详情接口，仅在确认后展示敏感字段。</p>
              </div>
              <div class="insurance-values">
                <div class="insurance-item">
                  <span>保险机构</span>
                  <strong>{{ insuranceVerified ? insuranceInfo.company : '验证后查看' }}</strong>
                </div>
                <div class="insurance-item">
                  <span>保单编号</span>
                  <strong>{{ insuranceVerified ? insuranceInfo.policyNo : '验证后查看' }}</strong>
                </div>
                <div class="insurance-item">
                  <span>保额</span>
                  <strong>{{ insuranceVerified ? insuranceInfo.coverage : '验证后查看' }}</strong>
                </div>
              </div>
              <el-button @click="verifyInsurance">
                {{ insuranceVerified ? '已验证' : '验证后查看' }}
              </el-button>
            </div>

            <div class="action-group">
              <el-button type="primary" @click="goEdit">编辑信息</el-button>
              <el-button v-if="canJoinExhibition" @click="openJoinDialog">加入展览</el-button>
              <el-button @click="startOutbound">发起出库</el-button>
              <el-button @click="startRestoration">发起修复</el-button>
              <el-button @click="startLoan">发起外借</el-button>
              <el-button text @click="reportDialogVisible = true">档案报告</el-button>
              <el-button text @click="labelDialogVisible = true">打印标签</el-button>
            </div>

            <div class="export-notes">
              <div class="export-note">
                <span>档案报告</span>
                <small>导出当前文物档案与真实关联摘要，适合归档和跨部门流转。</small>
              </div>
              <div class="export-note">
                <span>标签打印</span>
                <small>导出当前文物标签数据，可用于展柜、库房和运输交接。</small>
              </div>
            </div>
          </section>

          <section id="environment" class="page-card section-card">
            <div class="section-title">保护环境要求</div>
            <div class="environment-grid">
              <div class="environment-item">
                <span>温度范围</span>
                <strong>{{ environmentInfo.temperature }}</strong>
              </div>
              <div class="environment-item">
                <span>湿度范围</span>
                <strong>{{ environmentInfo.humidity }}</strong>
              </div>
              <div class="environment-item">
                <span>光照限制</span>
                <strong>{{ environmentInfo.light }}</strong>
              </div>
              <div class="environment-item full">
                <span>特殊保护要求</span>
                <strong>{{ environmentInfo.notes }}</strong>
              </div>
            </div>
          </section>
        </div>
      </section>

      <section id="timeline" class="page-card section-card">
        <div class="section-title-row">
          <div class="section-title">档案事件</div>
          <el-button text type="primary" @click="goToProcessCenter">查看流程中心</el-button>
        </div>
        <div class="timeline-list">
          <div v-for="item in timelineEvents" :key="item.key" class="timeline-item">
            <div class="timeline-rail">
              <span class="timeline-dot" :class="item.status" />
            </div>
            <el-collapse class="timeline-collapse">
              <el-collapse-item :name="item.key">
                <template #title>
                  <div class="timeline-headline">
                    <div>
                      <div class="timeline-title">{{ item.title }}</div>
                      <div class="timeline-subtitle">{{ item.time }} · {{ item.operator }}</div>
                    </div>
                    <div class="timeline-tags">
                      <el-tag size="small" effect="plain">{{ item.approval }}</el-tag>
                    </div>
                  </div>
                </template>
                <div class="timeline-detail">{{ item.detail }}</div>
              </el-collapse-item>
            </el-collapse>
          </div>
          <el-empty v-if="!timelineEvents.length" description="暂无档案事件" />
        </div>
      </section>

      <section id="related" class="page-card section-card">
        <div class="section-title">关联信息</div>
        <el-tabs v-model="activeTab">
          <el-tab-pane
            v-for="group in relationTabs"
            :key="group.key"
            :label="group.label"
            :name="group.key"
          >
            <div v-if="group.records.length" class="relation-list">
              <article v-for="item in group.records" :key="item.id" class="relation-item">
                <strong>{{ item.title || item.name || '-' }}</strong>
                <span>{{ item.status || '-' }}</span>
                <p>{{ formatRange(item.startTime, item.endTime) }}</p>
              </article>
            </div>
            <el-empty v-else description="暂无关联记录" />
          </el-tab-pane>
        </el-tabs>
      </section>

      <div class="quick-nav page-card">
        <div class="quick-nav-title">快速导航</div>
        <button type="button" @click="scrollToSection('timeline')">档案事件</button>
        <button type="button" @click="scrollToSection('related')">关联记录</button>
        <button type="button" @click="scrollToSection('environment')">保护要求</button>
      </div>

      <el-dialog v-model="joinDialogVisible" title="加入展览" width="520px">
        <el-form label-position="top">
          <el-form-item label="选择展览">
            <el-select v-model="joinForm.exhibitionId" placeholder="请选择展览" style="width: 100%">
              <el-option
                v-for="item in joinableExhibitions"
                :key="item.id"
                :label="`${item.name}（${item.statusLabel}）`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="展示顺序">
            <el-input-number v-model="joinForm.displayOrder" :min="1" :step="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="joinForm.remark" type="textarea" :rows="3" placeholder="可填写展柜位置或备注信息" />
          </el-form-item>
        </el-form>
        <el-empty v-if="!joinableExhibitions.length" description="暂无可加入的展览" />
        <template #footer>
          <el-button @click="joinDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="joinSaving" @click="submitJoinExhibition">确认加入</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="reportDialogVisible" title="文物档案报告" width="520px">
        <div class="export-dialog">
          <div class="dialog-block">
            <div class="dialog-title">导出内容</div>
            <p>导出当前文物档案基础信息和真实关联摘要。</p>
          </div>
          <div class="dialog-block preview-grid">
            <div><span>文物编号</span><strong>{{ artifact.code }}</strong></div>
            <div><span>文物名称</span><strong>{{ artifact.name }}</strong></div>
            <div><span>当前状态</span><strong>{{ currentStateLabel(artifact.currentState) }}</strong></div>
            <div><span>导出格式</span><strong>TXT 报告</strong></div>
          </div>
        </div>
        <template #footer>
          <el-button @click="reportDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="reportDownloading" @click="exportReport">确认导出</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="labelDialogVisible" title="打印标签" width="520px">
        <div class="export-dialog">
          <div class="dialog-block">
            <div class="dialog-title">标签说明</div>
            <p>导出当前文物的标签数据文件。</p>
          </div>
          <div class="dialog-block preview-grid">
            <div><span>文物编号</span><strong>{{ artifact.code }}</strong></div>
            <div><span>文物名称</span><strong>{{ artifact.name }}</strong></div>
            <div><span>存放位置</span><strong>{{ artifact.location }}</strong></div>
            <div><span>导出格式</span><strong>TXT 标签数据</strong></div>
          </div>
        </div>
        <template #footer>
          <el-button @click="labelDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="labelDownloading" @click="exportLabel">确认导出</el-button>
        </template>
      </el-dialog>
    </template>

    <el-empty v-else description="未找到文物详情" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { artifactApi } from '@/api/artifact'
import { exhibitionApi } from '@/api/exhibition'
import { downloadResponseBlob } from '@/utils/download'
import { formatDateRange } from '@/utils/format'
import { hasPermission } from '@/utils/permission'
import {
  getAppraisalLevelLabel,
  getArtifactStateLabel,
  getExhibitionStatusLabel,
  normalizeArtifactState,
  normalizePreservationStatus
} from '@/utils/status'

const route = useRoute()
const router = useRouter()

const transparentPixel =
  'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw=='

const relationConfigs = [
  { key: 'exhibitions', label: '展览记录' },
  { key: 'restorations', label: '修复记录' },
  { key: 'loans', label: '外借记录' },
  { key: 'acquisitions', label: '征集记录' },
  { key: 'appraisals', label: '鉴定记录' },
  { key: 'outbounds', label: '出库记录' },
  { key: 'disposals', label: '处置记录' },
  { key: 'storageChecks', label: '盘点记录' },
  { key: 'warehousingRecords', label: '入库记录' },
  { key: 'warehouseIns', label: '入馆记录' }
]

const activeTab = ref('exhibitions')
const insuranceVerified = ref(false)
const loading = ref(false)
const joinSaving = ref(false)
const reportDownloading = ref(false)
const labelDownloading = ref(false)
const reportDialogVisible = ref(false)
const labelDialogVisible = ref(false)
const artifact = ref(null)
const activeImage = ref(transparentPixel)
const joinDialogVisible = ref(false)
const exhibitionOptions = ref([])
const relations = ref(
  relationConfigs.reduce((acc, item) => {
    acc[item.key] = []
    return acc
  }, {})
)
const joinForm = ref({
  exhibitionId: null,
  displayOrder: 1,
  remark: ''
})

const canJoinExhibition = computed(() => hasPermission('artifact:edit'))

const safeParse = (value) => {
  if (!value) return null
  try {
    return JSON.parse(value)
  } catch {
    return null
  }
}

const asText = (value) => (value === null || value === undefined || value === '' ? '-' : String(value))

const parseInsuranceInfo = (value) => {
  const parsed = safeParse(value)
  if (parsed && typeof parsed === 'object') {
    return {
      company: asText(parsed.company || parsed.insurer),
      policyNo: asText(parsed.policyNo || parsed.policy),
      coverage: asText(parsed.coverage || parsed.amount)
    }
  }

  if (value) {
    return {
      company: asText(value),
      policyNo: '-',
      coverage: '-'
    }
  }

  return {
    company: '-',
    policyNo: '-',
    coverage: '-'
  }
}

const parseEnvironmentInfo = (value) => {
  const parsed = safeParse(value)
  if (parsed && typeof parsed === 'object') {
    return {
      temperature: asText(parsed.temperature),
      humidity: asText(parsed.humidity),
      light: asText(parsed.light),
      notes: asText(parsed.notes || parsed.remark)
    }
  }

  return {
    temperature: '-',
    humidity: '-',
    light: '-',
    notes: asText(value)
  }
}

const parseGallery = (item) =>
  (item.documents &&
    (() => {
      const docs = safeParse(item.documents)
      if (Array.isArray(docs)) {
        return docs
          .map((entry, index) =>
            typeof entry === 'string'
              ? { label: `资料 ${index + 1}`, src: entry }
              : entry?.url
                ? { label: entry.label || `资料 ${index + 1}`, src: entry.url }
                : null
          )
          .filter(Boolean)
      }
      return String(item.documents)
        .split(/[\n,]/)
        .map((part, index) => part.trim() && ({ label: `资料 ${index + 1}`, src: part.trim() }))
        .filter(Boolean)
    })()) || [{ label: '主图', src: item.imageUrl || transparentPixel }]

const currentStateLabel = (state) => getArtifactStateLabel(state) || '-'

const normalizeArtifact = (item) => ({
  ...item,
  material: asText(item.material),
  size: asText(item.size),
  weight: item.weight ? `${item.weight} kg` : '-',
  source: asText(item.source),
  appraisalLevel: getAppraisalLevelLabel(item.appraisalLevel) || '-',
  location: asText(item.location || item.currentLocation),
  summary: asText(item.description),
  preservationStatus: normalizePreservationStatus(item.preservationStatus) || '-',
  currentState: normalizeArtifactState(item.currentState) || ''
})

const mediaGallery = computed(() => (artifact.value ? parseGallery(artifact.value) : []))
const insuranceInfo = computed(() => parseInsuranceInfo(artifact.value?.insuranceInfo))
const environmentInfo = computed(() => parseEnvironmentInfo(artifact.value?.environmentRequirements))

const relationTabs = computed(() =>
  relationConfigs.map((config) => ({
    ...config,
    records: Array.isArray(relations.value?.[config.key]) ? relations.value[config.key] : []
  }))
)

const timelineStatus = (status) => {
  if (['REJECTED'].includes(status)) return 'processing'
  if (['RETURNED', 'COMPLETED', 'ARCHIVED', 'CONFIRMED', 'FINISHED'].includes(status)) return 'completed'
  return 'processing'
}

const normalizeEventTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

const timelineEvents = computed(() => {
  if (!artifact.value) return []

  const events = []
  const createdTime = artifact.value.createdTime
  const updatedTime = artifact.value.updatedTime || artifact.value.updateTime

  if (createdTime) {
    events.push({
      key: `created-${createdTime}`,
      title: '档案创建',
      time: normalizeEventTime(createdTime),
      operator: '系统记录',
      approval: '已创建',
      status: 'completed',
      detail: '文物基础档案已建立。'
    })
  }

  if (updatedTime && updatedTime !== createdTime) {
    events.push({
      key: `updated-${updatedTime}`,
      title: '档案更新',
      time: normalizeEventTime(updatedTime),
      operator: '系统记录',
      approval: '已更新',
      status: 'completed',
      detail: '当前页面展示的数据来自真实文物详情接口。'
    })
  }

  relationTabs.value.forEach((group) => {
    group.records.forEach((item) => {
      const time = formatRange(item.startTime, item.endTime)
      events.push({
        key: `${group.key}-${item.id}`,
        title: group.label,
        time,
        operator: '关联业务',
        approval: item.status || '-',
        status: timelineStatus(item.status),
        detail: `${item.title || item.name || '-'}`
      })
    })
  })

  return events.sort((left, right) => {
    const leftTime = new Date(String(left.time).split(' · ')[0]).getTime()
    const rightTime = new Date(String(right.time).split(' · ')[0]).getTime()
    return (Number.isNaN(rightTime) ? 0 : rightTime) - (Number.isNaN(leftTime) ? 0 : leftTime)
  })
})

const joinableExhibitions = computed(() => {
  const currentIds = new Set((relations.value.exhibitions || []).map((item) => Number(item.id)))
  return exhibitionOptions.value.filter((item) => !currentIds.has(Number(item.id)) && item.status !== 'FINISHED')
})

watch(mediaGallery, (value) => {
  activeImage.value = value[0]?.src || transparentPixel
}, { immediate: true })

watch(relationTabs, (groups) => {
  const current = groups.find((item) => item.key === activeTab.value)
  if (current) return
  activeTab.value = groups[0]?.key || 'exhibitions'
}, { immediate: true })

const formatRange = (start, end) => formatDateRange(start, end)

const loadExhibitions = async () => {
  try {
    const res = await exhibitionApi.getList({ current: 1, size: 100 })
    exhibitionOptions.value = (res.records || []).map((item) => ({
      ...item,
      statusLabel: getExhibitionStatusLabel(item.status) || '-'
    }))
  } catch {
    exhibitionOptions.value = []
  }
}

const emptyRelations = () =>
  relationConfigs.reduce((acc, item) => {
    acc[item.key] = []
    return acc
  }, {})

const loadDetail = async () => {
  loading.value = true
  try {
    const [res, relationRes] = await Promise.all([
      artifactApi.getDetail(route.params.id),
      artifactApi.getRelations(route.params.id).catch(() => emptyRelations())
    ])
    artifact.value = normalizeArtifact(res)
    relations.value = {
      ...emptyRelations(),
      ...relationRes
    }
    insuranceVerified.value = false
  } catch (error) {
    artifact.value = null
    relations.value = emptyRelations()
    ElMessage.error(error?.response?.data?.message || '加载文物详情失败')
  } finally {
    loading.value = false
  }
}

const statusClass = (status) => {
  const normalized = normalizePreservationStatus(status)
  if (normalized === '完好') return 'is-good'
  if (normalized === '轻微损伤') return 'is-warning'
  return 'is-danger'
}

const verifyInsurance = async () => {
  if (insuranceVerified.value) {
    ElMessage.success('保险信息已验证')
    return
  }

  try {
    await ElMessageBox.confirm('是否确认进行二次权限验证并查看保险信息？', '权限验证', {
      type: 'warning',
      confirmButtonText: '确认验证',
      cancelButtonText: '取消'
    })
    insuranceVerified.value = true
    ElMessage.success('验证成功，已显示保险信息')
  } catch {
    // canceled
  }
}

const exportReport = async () => {
  reportDownloading.value = true
  try {
    const res = await artifactApi.exportReport(route.params.id)
    downloadResponseBlob(res, `artifact-report-${route.params.id}.txt`)
    reportDialogVisible.value = false
    ElMessage.success('文物档案报告导出成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '导出文物档案报告失败')
  } finally {
    reportDownloading.value = false
  }
}

const exportLabel = async () => {
  labelDownloading.value = true
  try {
    const res = await artifactApi.exportLabel(route.params.id)
    downloadResponseBlob(res, `artifact-label-${route.params.id}.txt`)
    labelDialogVisible.value = false
    ElMessage.success('文物标签导出成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '导出文物标签失败')
  } finally {
    labelDownloading.value = false
  }
}

const openJoinDialog = async () => {
  await loadExhibitions()
  joinForm.value = {
    exhibitionId: joinableExhibitions.value[0]?.id || null,
    displayOrder: (relations.value.exhibitions?.length || 0) + 1,
    remark: ''
  }
  joinDialogVisible.value = true
}

const submitJoinExhibition = async () => {
  if (!joinForm.value.exhibitionId) {
    ElMessage.warning('请选择要加入的展览')
    return
  }

  joinSaving.value = true
  try {
    await artifactApi.joinExhibition(route.params.id, {
      exhibitionId: joinForm.value.exhibitionId,
      displayOrder: joinForm.value.displayOrder || undefined,
      remark: joinForm.value.remark || undefined
    })
    ElMessage.success('文物已加入展览')
    joinDialogVisible.value = false
    await loadDetail()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加入展览失败')
  } finally {
    joinSaving.value = false
  }
}

const goEdit = () => {
  router.push(`/artifacts/${artifact.value.id}/edit`)
}

const startOutbound = () => {
  router.push('/outbound/list')
}

const startRestoration = () => {
  router.push('/restoration/list')
}

const startLoan = () => {
  router.push('/loan/list')
}

const goToProcessCenter = () => {
  router.push('/process/center')
}

const scrollToSection = (id) => {
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

onMounted(async () => {
  await loadDetail()
  await loadExhibitions()
})
</script>

<style scoped>
.detail-page {
  display: grid;
  gap: 22px;
}

.detail-layout {
  display: grid;
  grid-template-columns: minmax(340px, 0.9fr) minmax(0, 1.35fr);
  gap: 22px;
}

.media-column,
.summary-card,
.section-card {
  padding: 22px;
}

.media-column {
  align-self: start;
}

.main-media {
  overflow: hidden;
  border-radius: 18px;
  background: var(--museum-bg-subtle);
}

.main-image {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  display: block;
}

.thumb-strip {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.thumb-card {
  border: 1px solid var(--museum-border-light);
  border-radius: 12px;
  background: var(--museum-bg-card);
  padding: 8px;
  cursor: pointer;
  text-align: left;
}

.thumb-card.active {
  border-color: rgba(46, 90, 90, 0.28);
  box-shadow: 0 0 0 1px rgba(46, 90, 90, 0.16);
}

.thumb-card img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  border-radius: 10px;
}

.thumb-card span {
  display: block;
  margin-top: 8px;
  font-size: 12px;
}

.info-column {
  display: grid;
  gap: 22px;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.section-kicker {
  color: var(--museum-color-primary);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.summary-header h2 {
  margin: 8px 0 10px;
  font-size: 28px;
  color: var(--museum-text-strong);
}

.summary-header p {
  margin: 0;
  color: var(--museum-text-secondary);
  line-height: 1.8;
}

.status-tag {
  padding: 10px 14px;
  border-radius: 999px;
  color: #fff;
  font-size: 13px;
  white-space: nowrap;
}

.status-tag.is-good {
  background: var(--museum-color-success);
}

.status-tag.is-warning {
  background: var(--museum-color-warning);
}

.status-tag.is-danger {
  background: var(--museum-color-error);
}

.meta-grid,
.environment-grid,
.insurance-values,
.preview-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 18px;
}

.meta-item,
.environment-item,
.insurance-item,
.preview-grid div {
  display: grid;
  gap: 6px;
  padding: 14px 16px;
  border-radius: 14px;
  background: var(--museum-bg-subtle);
}

.meta-item span,
.environment-item span,
.insurance-item span,
.preview-grid span {
  color: var(--museum-text-secondary);
  font-size: 12px;
}

.meta-item strong,
.environment-item strong,
.insurance-item strong,
.preview-grid strong {
  color: var(--museum-text-strong);
  font-size: 15px;
}

.environment-item.full {
  grid-column: 1 / -1;
}

.insurance-panel {
  display: grid;
  gap: 14px;
  margin-top: 22px;
  padding: 18px;
  border-radius: 18px;
  background: rgba(212, 160, 23, 0.08);
}

.helper-text {
  margin: 4px 0 0;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.action-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 22px;
}

.export-notes {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 18px;
}

.export-note {
  display: grid;
  gap: 6px;
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(46, 90, 90, 0.06);
}

.export-note span {
  color: var(--museum-text-strong);
  font-weight: 600;
}

.export-note small {
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.section-title,
.dialog-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--museum-text-strong);
}

.alert-list {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}

.alert-item {
  border: 1px solid rgba(245, 34, 45, 0.12);
  border-radius: 14px;
  padding: 14px 16px;
  text-align: left;
  background: rgba(245, 34, 45, 0.04);
  cursor: pointer;
}

.alert-item-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: var(--museum-color-error);
  font-size: 12px;
  margin-bottom: 6px;
}

.alert-item strong {
  display: block;
  color: var(--museum-text-strong);
}

.alert-item p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.timeline-list {
  display: grid;
  gap: 14px;
  margin-top: 18px;
}

.timeline-item {
  display: grid;
  grid-template-columns: 24px minmax(0, 1fr);
  gap: 12px;
}

.timeline-rail {
  position: relative;
  display: flex;
  justify-content: center;
}

.timeline-rail::after {
  content: '';
  position: absolute;
  top: 16px;
  bottom: -24px;
  width: 2px;
  background: var(--museum-border-light);
}

.timeline-item:last-child .timeline-rail::after {
  display: none;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-top: 6px;
  background: var(--museum-text-tertiary);
}

.timeline-dot.completed {
  background: var(--museum-color-success);
}

.timeline-dot.processing {
  background: var(--museum-color-warning);
}

.timeline-collapse {
  border: 0;
}

.timeline-headline {
  width: 100%;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.timeline-title {
  font-weight: 600;
  color: var(--museum-text-strong);
}

.timeline-subtitle {
  font-size: 12px;
  color: var(--museum-text-secondary);
  margin-top: 4px;
}

.timeline-tags {
  display: flex;
  gap: 8px;
  align-items: center;
}

.timeline-detail {
  color: var(--museum-text-secondary);
  line-height: 1.8;
}

.relation-list {
  display: grid;
  gap: 12px;
  margin-top: 12px;
}

.relation-item {
  display: grid;
  gap: 6px;
  padding: 14px 16px;
  border-radius: 14px;
  background: var(--museum-bg-subtle);
}

.relation-item strong {
  color: var(--museum-text-strong);
}

.relation-item span,
.relation-item p {
  color: var(--museum-text-secondary);
  margin: 0;
}

.quick-nav {
  position: fixed;
  right: 28px;
  bottom: 28px;
  display: grid;
  gap: 10px;
  width: 180px;
  padding: 16px;
  z-index: 10;
}

.quick-nav-title {
  font-weight: 600;
  color: var(--museum-text-strong);
}

.quick-nav button {
  border: 1px solid var(--museum-border-light);
  background: #fff;
  border-radius: 10px;
  padding: 10px 12px;
  text-align: left;
  cursor: pointer;
  color: var(--museum-text-primary);
}

.export-dialog {
  display: grid;
  gap: 14px;
}

.dialog-block {
  display: grid;
  gap: 8px;
}

.dialog-block p {
  margin: 0;
  color: var(--museum-text-secondary);
  line-height: 1.8;
}

@media (max-width: 1100px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }

  .quick-nav {
    position: static;
    width: auto;
  }
}

@media (max-width: 640px) {
  .meta-grid,
  .environment-grid,
  .insurance-values,
  .preview-grid,
  .export-notes {
    grid-template-columns: 1fr;
  }

  .thumb-strip {
    grid-template-columns: 1fr 1fr;
  }

  .summary-header,
  .section-title-row,
  .timeline-headline {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
