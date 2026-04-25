<template>
  <div class="artifact-page">
    <div class="page-header">
      <div>
        <h2>文物信息管理</h2>
        <p>文物列表已接入真实分页、筛选、导出、删除、加入展览、批量导入和批量修改存放位置能力。</p>
      </div>
      <div class="header-actions">
        <el-button v-permission="PERMISSIONS.ARTIFACT_ADD" type="primary" @click="handleAdd">新增文物</el-button>
        <el-button v-permission="PERMISSIONS.ARTIFACT_ADD" @click="importDialogVisible = true">批量导入</el-button>
        <el-button v-permission="PERMISSIONS.ARTIFACT_EXPORT" @click="exportCurrent">导出当前结果</el-button>
        <div class="view-switch">
          <el-button :type="viewMode === 'table' ? 'primary' : 'default'" @click="viewMode = 'table'">列表</el-button>
          <el-button :type="viewMode === 'grid' ? 'primary' : 'default'" @click="viewMode = 'grid'">网格</el-button>
        </div>
      </div>
    </div>

    <el-card class="filter-card">
      <div class="filter-head">
        <div class="filter-title">筛选条件</div>
        <el-button text @click="advancedVisible = !advancedVisible">
          {{ advancedVisible ? '收起筛选' : '更多筛选' }}
        </el-button>
      </div>

      <el-form :model="query" label-position="top" class="filter-form">
        <div class="filter-grid">
          <el-form-item label="文物名称 / 编号">
            <el-input v-model="query.keyword" placeholder="请输入名称或编号" clearable />
          </el-form-item>
          <el-form-item label="文物类别">
            <el-select v-model="query.category" placeholder="请选择类别" clearable>
              <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="保存状态">
            <el-select v-model="query.preservationStatus" placeholder="请选择状态" clearable>
              <el-option v-for="item in preservationOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
        </div>

        <transition name="fade">
          <div v-if="advancedVisible" class="filter-grid advanced-grid">
            <el-form-item label="年代区间">
              <div class="era-range">
                <el-select v-model="query.eraStart" placeholder="起始年代" clearable>
                  <el-option v-for="item in eraOptions" :key="item" :label="item" :value="item" />
                </el-select>
                <span class="range-separator">至</span>
                <el-select v-model="query.eraEnd" placeholder="结束年代" clearable>
                  <el-option v-for="item in eraOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </div>
            </el-form-item>
            <el-form-item label="存放位置">
              <el-select v-model="query.location" placeholder="请选择位置" clearable>
                <el-option v-for="item in locationOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="鉴定等级">
              <el-select v-model="query.appraisalLevel" placeholder="请选择等级" clearable>
                <el-option v-for="item in appraisalOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </div>
        </transition>

        <div class="filter-actions">
          <el-button type="primary" :loading="loading" @click="loadArtifacts(1)">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <transition name="fade">
      <el-card v-if="selectedRows.length" class="batch-card">
        <div class="batch-bar">
          <div class="batch-meta">已选择 {{ selectedRows.length }} 条文物记录</div>
          <div class="batch-actions">
            <el-button v-permission="PERMISSIONS.ARTIFACT_EXPORT" @click="exportSelected">批量导出</el-button>
            <el-button v-permission="PERMISSIONS.ARTIFACT_EDIT" @click="openJoinDialog()">批量加入展览</el-button>
            <el-button type="primary" @click="locationDialogVisible = true">批量修改存放位置</el-button>
          </div>
        </div>
      </el-card>
    </transition>

    <el-card class="content-card">
      <div class="content-toolbar">
        <div class="result-meta">共 {{ pagination.total }} 件文物</div>
        <div class="toolbar-right">
          <div class="export-helper">导出和批量导出会直接下载当前筛选结果对应的数据文件。</div>
          <el-popover placement="bottom-end" width="220" trigger="click">
            <div class="column-panel">
              <div class="column-title">自定义列显示</div>
              <el-checkbox-group v-model="visibleColumns">
                <el-checkbox v-for="item in columnOptions" :key="item.key" :label="item.key">
                  {{ item.label }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
            <template #reference>
              <el-button>列设置</el-button>
            </template>
          </el-popover>
        </div>
      </div>

      <div v-if="viewMode === 'table'">
        <el-table
          v-loading="loading"
          :data="tableData"
          row-key="id"
          stripe
          border
          max-height="620"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="52" fixed="left" />
          <el-table-column v-if="showColumn('thumb')" label="缩略图" width="100" fixed="left">
            <template #default="{ row }">
              <el-image :src="row.thumb" fit="cover" class="thumb-image">
                <template #error>
                  <div class="thumb-placeholder">无图</div>
                </template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column v-if="showColumn('code')" prop="code" label="文物编号" width="140" sortable />
          <el-table-column v-if="showColumn('name')" prop="name" label="名称" width="180" sortable />
          <el-table-column v-if="showColumn('category')" prop="category" label="类别" width="120" sortable />
          <el-table-column v-if="showColumn('era')" prop="era" label="年代" width="120" sortable />
          <el-table-column v-if="showColumn('material')" prop="material" label="材质" width="120" />
          <el-table-column v-if="showColumn('size')" prop="size" label="尺寸" width="150" />
          <el-table-column v-if="showColumn('preservationStatus')" label="保存状态" width="130" sortable>
            <template #default="{ row }">
              <div class="status-cell" :class="statusClass(row.preservationStatus)">
                <span class="status-point" />
                <span>{{ row.preservationStatus || '-' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column v-if="showColumn('location')" prop="location" label="存放位置" width="150" />
          <el-table-column v-if="showColumn('appraisalLevel')" prop="appraisalLevel" label="鉴定等级" width="110" sortable />
          <el-table-column label="操作" width="300" fixed="right">
            <template #default="{ row }">
              <el-button link @click="viewDetail(row.id)">详情</el-button>
              <el-button v-permission="PERMISSIONS.ARTIFACT_EDIT" link @click="handleQuickEdit(row.id)">编辑</el-button>
              <el-button v-permission="PERMISSIONS.ARTIFACT_EDIT" link @click="openJoinDialog(row)">加入展览</el-button>
              <el-button v-permission="PERMISSIONS.ARTIFACT_DELETE" link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-else v-loading="loading" class="artifact-grid">
        <ArtifactCard
          v-for="item in tableData"
          :key="item.id"
          :artifact="item"
          @view="viewDetail"
          @edit="handleQuickEdit"
        />
      </div>

      <el-empty v-if="!loading && !tableData.length" description="暂无文物数据" />

      <div class="pagination-footer">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadArtifacts"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="locationDialogVisible" title="批量修改存放位置" width="420px">
      <el-form label-position="top">
        <el-form-item label="新的存放位置">
          <el-select v-model="batchLocation" placeholder="请选择位置" style="width: 100%">
            <el-option v-for="item in locationOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="locationDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchSaving" @click="submitBatchLocation">确认修改</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="joinDialogVisible" :title="joinSingleRow ? '加入展览' : '批量加入展览'" width="520px">
      <el-form label-position="top">
        <el-form-item label="选择展览">
          <el-select v-model="joinForm.exhibitionId" placeholder="请选择展览" style="width: 100%">
            <el-option
              v-for="item in exhibitionOptions"
              :key="item.id"
              :label="`${item.name}（${item.statusLabel}）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="展示顺序起始值">
          <el-input-number v-model="joinForm.displayOrder" :min="1" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="joinForm.remark" type="textarea" :rows="3" placeholder="可填写展柜位置或备注信息" />
        </el-form-item>
      </el-form>
      <el-empty v-if="!exhibitionOptions.length" description="暂无可加入的展览" />
      <template #footer>
        <el-button @click="joinDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="joinSaving" @click="submitJoinExhibition">确认加入</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="批量导入文物" width="760px">
      <div class="import-panel">
        <div class="import-tips page-subcard">
          <h4>导入说明</h4>
          <p>请粘贴 JSON 数组，每条至少包含 `code` 和 `name`。其余字段如 `category`、`era`、`material`、`location` 可选。</p>
        </div>

        <el-input
          v-model="importText"
          type="textarea"
          :rows="14"
          placeholder="请输入包含 code 和 name 字段的 JSON 数组"
        />

        <div v-if="importPreview.length" class="import-preview page-subcard">
          <div class="preview-head">
            <strong>预览 {{ importPreview.length }} 条记录</strong>
            <span>会自动过滤缺少 `code` 或 `name` 的无效记录</span>
          </div>
          <el-table :data="importPreview.slice(0, 5)" size="small" stripe>
            <el-table-column prop="code" label="编号" min-width="140" />
            <el-table-column prop="name" label="名称" min-width="160" />
            <el-table-column prop="category" label="类别" min-width="100" />
            <el-table-column prop="era" label="年代" min-width="100" />
          </el-table>
          <div v-if="importPreview.length > 5" class="preview-more">仅展示前 5 条，提交时会导入全部有效记录。</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button @click="parseImportText">解析预览</el-button>
        <el-button type="primary" :loading="importSaving" @click="submitImport">确认导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ArtifactCard from '@/components/ArtifactCard.vue'
import { artifactApi } from '@/api/artifact'
import { exhibitionApi } from '@/api/exhibition'
import { PERMISSIONS } from '@/constants/permissions'
import { downloadResponseBlob } from '@/utils/download'

const router = useRouter()
const transparentPixel = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw=='

const eraOptions = ['先秦', '汉', '唐', '宋', '元', '明', '清', '近现代']
const eraOrderMap = Object.fromEntries(eraOptions.map((item, index) => [item, index + 1]))

const query = reactive({
  keyword: '',
  category: '',
  eraStart: '',
  eraEnd: '',
  preservationStatus: '',
  location: '',
  appraisalLevel: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const advancedVisible = ref(false)
const viewMode = ref('table')
const loading = ref(false)
const batchSaving = ref(false)
const joinSaving = ref(false)
const importSaving = ref(false)
const selectedRows = ref([])
const locationDialogVisible = ref(false)
const joinDialogVisible = ref(false)
const importDialogVisible = ref(false)
const joinSingleRow = ref(null)
const batchLocation = ref('')
const tableData = ref([])
const exhibitionOptions = ref([])
const importText = ref('')
const importPreview = ref([])

const dynamicOptions = reactive({
  category: [],
  preservationStatus: [],
  location: [],
  appraisalLevel: []
})

const joinForm = reactive({
  exhibitionId: null,
  displayOrder: 1,
  remark: ''
})

const columnOptions = [
  { key: 'thumb', label: '缩略图' },
  { key: 'code', label: '文物编号' },
  { key: 'name', label: '名称' },
  { key: 'category', label: '类别' },
  { key: 'era', label: '年代' },
  { key: 'material', label: '材质' },
  { key: 'size', label: '尺寸' },
  { key: 'preservationStatus', label: '保存状态' },
  { key: 'location', label: '存放位置' },
  { key: 'appraisalLevel', label: '鉴定等级' }
]

const visibleColumns = ref(columnOptions.map((item) => item.key))

const categoryOptions = computed(() => dynamicOptions.category)
const preservationOptions = computed(() => dynamicOptions.preservationStatus)
const locationOptions = computed(() => dynamicOptions.location)
const appraisalOptions = computed(() => dynamicOptions.appraisalLevel)

const mapArtifact = (item) => ({
  ...item,
  thumb: item.imageUrl || transparentPixel,
  preservationStatus: item.preservationStatus || '-',
  appraisalLevel: item.appraisalLevel || '-',
  location: item.location || item.currentLocation || '-',
  material: item.material || '-',
  size: item.size || '-',
  era: item.era || '-',
  category: item.category || '-'
})

const mergeOptionValues = (current, extra = []) => {
  const merged = [...current, ...extra]
    .map((item) => (item == null ? '' : String(item).trim()))
    .filter((item) => item && item !== '-')
  return [...new Set(merged)]
}

const syncDynamicOptions = (records) => {
  dynamicOptions.category = mergeOptionValues(
    records.map((item) => item.category),
    [query.category]
  )
  dynamicOptions.preservationStatus = mergeOptionValues(
    records.map((item) => item.preservationStatus),
    [query.preservationStatus]
  )
  dynamicOptions.location = mergeOptionValues(
    records.map((item) => item.location),
    [query.location, batchLocation.value]
  )
  dynamicOptions.appraisalLevel = mergeOptionValues(
    records.map((item) => item.appraisalLevel),
    [query.appraisalLevel]
  )
}

const getKeywordPayload = (keyword) => {
  const value = keyword.trim()
  if (!value) return {}
  const looksLikeCode = /[A-Za-z]/.test(value) || /[-_/]/.test(value)
  return looksLikeCode ? { code: value } : { name: value }
}

const buildPayload = () => {
  const startOrder = query.eraStart ? eraOrderMap[query.eraStart] : 0
  const endOrder = query.eraEnd ? eraOrderMap[query.eraEnd] : Number.MAX_SAFE_INTEGER
  const useSingleEra = query.eraStart && query.eraEnd && startOrder === endOrder

  return {
    current: pagination.current,
    size: pagination.size,
    ...getKeywordPayload(query.keyword),
    category: query.category || undefined,
    era: useSingleEra ? query.eraStart : undefined,
    preservationStatus: query.preservationStatus || undefined,
    appraisalLevel: query.appraisalLevel || undefined,
    location: query.location || undefined
  }
}

const buildExportPayload = (ids) => {
  const startOrder = query.eraStart ? eraOrderMap[query.eraStart] : 0
  const endOrder = query.eraEnd ? eraOrderMap[query.eraEnd] : Number.MAX_SAFE_INTEGER
  const useSingleEra = query.eraStart && query.eraEnd && startOrder === endOrder
  const payload = {
    ...getKeywordPayload(query.keyword),
    category: query.category || undefined,
    era: useSingleEra ? query.eraStart : undefined,
    preservationStatus: query.preservationStatus || undefined,
    appraisalLevel: query.appraisalLevel || undefined,
    location: query.location || undefined
  }
  if (ids?.length) payload.ids = ids
  return payload
}

const applyClientFilters = (records) => {
  const startOrder = query.eraStart ? eraOrderMap[query.eraStart] : 0
  const endOrder = query.eraEnd ? eraOrderMap[query.eraEnd] : Number.MAX_SAFE_INTEGER

  return records.filter((item) => {
    const currentEraOrder = eraOrderMap[item.era] || 0
    return currentEraOrder >= startOrder && currentEraOrder <= endOrder
  })
}

const loadExhibitions = async () => {
  try {
    const res = await exhibitionApi.getList({ current: 1, size: 100 })
    exhibitionOptions.value = (res.records || [])
      .filter((item) => item.status !== 'FINISHED')
      .map((item) => ({
        ...item,
        statusLabel:
          item.status === 'PLANNING'
            ? '策划中'
            : item.status === 'ONGOING'
              ? '进行中'
              : item.status === 'FINISHED'
                ? '已结束'
                : item.status || '未设置'
      }))
  } catch {
    exhibitionOptions.value = []
  }
}

const loadArtifacts = async (page = pagination.current) => {
  loading.value = true
  pagination.current = page
  try {
    const res = await artifactApi.getList(buildPayload())
    const records = (res.records || []).map(mapArtifact)
    tableData.value = applyClientFilters(records)
    pagination.total = res.total || 0
    selectedRows.value = []
    syncDynamicOptions(records)
  } catch (error) {
    tableData.value = []
    pagination.total = 0
    ElMessage.error(error?.response?.data?.message || '加载文物列表失败')
  } finally {
    loading.value = false
  }
}

const exportCurrent = async () => {
  try {
    const res = await artifactApi.export(buildExportPayload())
    downloadResponseBlob(res, 'artifacts-export.csv')
    ElMessage.success('文物列表导出成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '导出文物列表失败')
  }
}

const exportSelected = async () => {
  if (!selectedRows.value.length) {
    ElMessage.warning('请先选择要导出的文物')
    return
  }
  try {
    const res = await artifactApi.exportBatch(buildExportPayload(selectedRows.value.map((item) => item.id)))
    downloadResponseBlob(res, 'artifacts-selected-export.csv')
    ElMessage.success('批量导出成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '批量导出失败')
  }
}

const openJoinDialog = async (row = null) => {
  await loadExhibitions()
  joinSingleRow.value = row
  joinForm.exhibitionId = exhibitionOptions.value[0]?.id || null
  joinForm.displayOrder = 1
  joinForm.remark = ''
  joinDialogVisible.value = true
}

const submitJoinExhibition = async () => {
  if (!joinForm.exhibitionId) {
    ElMessage.warning('请选择要加入的展览')
    return
  }

  const targets = joinSingleRow.value ? [joinSingleRow.value] : selectedRows.value
  if (!targets.length) {
    ElMessage.warning('请先选择文物')
    return
  }

  joinSaving.value = true
  try {
    await Promise.all(
      targets.map((item, index) =>
        artifactApi.joinExhibition(item.id, {
          exhibitionId: joinForm.exhibitionId,
          displayOrder: (joinForm.displayOrder || 1) + index,
          remark: joinForm.remark || undefined
        })
      )
    )
    ElMessage.success(joinSingleRow.value ? '文物已加入展览' : '选中文物已批量加入展览')
    joinDialogVisible.value = false
    joinSingleRow.value = null
    await loadArtifacts(pagination.current)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加入展览失败')
  } finally {
    joinSaving.value = false
  }
}

const normalizeImportArtifacts = (records) =>
  records
    .filter((item) => item && typeof item === 'object')
    .map((item) => ({
      code: item.code?.trim?.() || '',
      name: item.name?.trim?.() || '',
      category: item.category || '',
      era: item.era || '',
      material: item.material || '',
      size: item.size || '',
      source: item.source || '',
      preservationStatus: item.preservationStatus || '',
      appraisalLevel: item.appraisalLevel || '',
      location: item.location || ''
    }))
    .filter((item) => item.code && item.name)

const parseImportText = () => {
  try {
    const parsed = JSON.parse(importText.value || '[]')
    if (!Array.isArray(parsed)) {
      ElMessage.warning('请输入 JSON 数组格式的数据')
      importPreview.value = []
      return
    }
    importPreview.value = normalizeImportArtifacts(parsed)
    if (!importPreview.value.length) {
      ElMessage.warning('未解析到有效文物记录，请至少提供 code 和 name')
      return
    }
    ElMessage.success(`已解析 ${importPreview.value.length} 条有效记录`)
  } catch {
    importPreview.value = []
    ElMessage.error('导入内容不是合法的 JSON 数组')
  }
}

const submitImport = async () => {
  if (!importPreview.value.length) {
    parseImportText()
    if (!importPreview.value.length) return
  }

  importSaving.value = true
  try {
    const result = await artifactApi.importArtifacts({ artifacts: importPreview.value })
    ElMessage.success(`导入完成：成功 ${result.importedCount || 0} 条`)
    if (Array.isArray(result.skippedCodes) && result.skippedCodes.length) {
      ElMessage.warning(`以下编号因重复被跳过：${result.skippedCodes.join('、')}`)
    }
    importDialogVisible.value = false
    importText.value = ''
    importPreview.value = []
    await loadArtifacts(1)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '批量导入失败')
  } finally {
    importSaving.value = false
  }
}

const showColumn = (key) => visibleColumns.value.includes(key)

const statusClass = (status) => {
  if (status === '完好') return 'is-good'
  if (status === '轻微损伤') return 'is-warning'
  return 'is-danger'
}

const reset = () => {
  query.keyword = ''
  query.category = ''
  query.eraStart = ''
  query.eraEnd = ''
  query.preservationStatus = ''
  query.location = ''
  query.appraisalLevel = ''
  pagination.current = 1
  loadArtifacts(1)
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadArtifacts(1)
}

const handleAdd = () => {
  router.push('/artifacts/add')
}

const viewDetail = (id) => {
  router.push('/artifacts/' + id)
}

const handleQuickEdit = (id) => {
  router.push('/artifacts/' + id + '/edit')
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除文物“${row.name}”吗？`, '二次确认', {
      type: 'warning',
      confirmButtonText: '确认删除',
      cancelButtonText: '取消'
    })
    await artifactApi.delete(row.id)
    ElMessage.success('删除成功')
    await loadArtifacts(1)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '删除文物失败')
    }
  }
}

const submitBatchLocation = async () => {
  if (!batchLocation.value) {
    ElMessage.warning('请选择新的存放位置')
    return
  }

  batchSaving.value = true
  try {
    await Promise.all(
      selectedRows.value.map((item) =>
        artifactApi.update({
          ...item,
          imageUrl: item.imageUrl || undefined,
          location: batchLocation.value
        })
      )
    )
    ElMessage.success('批量修改存放位置成功')
    locationDialogVisible.value = false
    batchLocation.value = ''
    await loadArtifacts(pagination.current)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '批量修改存放位置失败')
  } finally {
    batchSaving.value = false
  }
}

onMounted(async () => {
  await loadArtifacts(1)
  await loadExhibitions()
})
</script>

<style scoped>
.artifact-page {
  display: grid;
  gap: 20px;
}

.page-header,
.filter-card,
.content-card,
.batch-card {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.page-header h2 {
  margin: 0 0 6px;
  font-size: 28px;
  color: var(--museum-text-strong);
}

.page-header p {
  margin: 0;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.header-actions,
.view-switch,
.filter-actions,
.batch-bar,
.batch-actions,
.content-toolbar,
.preview-head,
.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.header-actions {
  flex-wrap: wrap;
  justify-content: flex-end;
}

.filter-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}

.filter-title,
.column-title {
  font-weight: 600;
  color: var(--museum-text-strong);
}

.filter-form,
.column-panel,
.import-panel {
  display: grid;
  gap: 12px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.advanced-grid {
  padding-top: 4px;
}

.era-range {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 10px;
  align-items: center;
}

.range-separator {
  color: var(--museum-text-secondary);
}

.filter-actions,
.batch-bar,
.content-toolbar,
.pagination-footer,
.preview-head {
  justify-content: space-between;
}

.batch-meta,
.result-meta,
.preview-head span,
.preview-more,
.export-helper {
  color: var(--museum-text-secondary);
}

.export-helper {
  font-size: 12px;
}

.artifact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.thumb-image {
  width: 60px;
  height: 48px;
  border-radius: 8px;
}

.thumb-placeholder {
  width: 60px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: var(--museum-bg-subtle);
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

.status-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--museum-text-secondary);
}

.status-point {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
}

.status-cell.is-good {
  color: var(--museum-color-success);
}

.status-cell.is-warning {
  color: var(--museum-color-warning);
}

.status-cell.is-danger {
  color: var(--museum-color-error);
}

.pagination-footer {
  margin-top: 20px;
  display: flex;
}

.page-subcard {
  padding: 16px 18px;
  border-radius: 16px;
  background: var(--museum-bg-subtle);
}

.import-tips h4 {
  margin: 0 0 8px;
  color: var(--museum-text-primary);
}

.import-tips p {
  margin: 0;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.preview-more {
  margin-top: 10px;
  font-size: 12px;
}

@media (max-width: 900px) {
  .page-header,
  .content-toolbar,
  .toolbar-right,
  .batch-bar {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
