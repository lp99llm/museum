<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="展览名称">
          <el-input v-model="queryParams.name" placeholder="展览名称" clearable />
        </el-form-item>
        <el-form-item label="展览状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="策划中" value="PLANNING" />
            <el-option label="进行中" value="ONGOING" />
            <el-option label="已结束" value="FINISHED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleAdd">新增展览</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="name" label="展览名称" width="200" show-overflow-tooltip />
        <el-table-column prop="theme" label="展览主题" width="150" show-overflow-tooltip />
        <el-table-column prop="venue" label="展览场地" width="120" />
        <el-table-column label="展览时间" width="220">
          <template #default="{ row }">
            {{ formatDate(row.startDate) }} 至 {{ formatDate(row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="curator" label="策展人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row.id)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleManageArtifacts(row)">管理展品</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
        style="margin-top: 20px;"
      />
    </el-card>

    <el-dialog v-model="artifactDialogVisible" title="管理展品" width="900px">
      <el-card>
        <el-form :inline="true" :model="artifactQuery" @submit.prevent>
          <el-form-item label="文物名称">
            <el-input v-model="artifactQuery.name" placeholder="文物名称" clearable />
          </el-form-item>
          <el-form-item label="类别">
            <el-select v-model="artifactQuery.category" placeholder="请选择" clearable style="width: 120px">
              <el-option label="陶瓷" value="陶瓷" />
              <el-option label="书画" value="书画" />
              <el-option label="玉器" value="玉器" />
              <el-option label="青铜器" value="青铜器" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="queryAvailableArtifacts">查询</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="availableArtifacts" border stripe size="small" style="margin-top: 10px;">
          <el-table-column prop="code" label="文物编号" width="120" />
          <el-table-column prop="name" label="名称" width="180" show-overflow-tooltip />
          <el-table-column prop="category" label="类别" width="80" />
          <el-table-column prop="era" label="年代" width="80" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="addArtifactToExhibition(row)">添加</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card style="margin-top: 15px;">
        <template #header>
          <span>已关联展品（{{ exhibitionArtifacts.length }}件）</span>
        </template>
        <el-table :data="exhibitionArtifacts" border stripe size="small">
          <el-table-column prop="displayOrder" label="排序" width="60" />
          <el-table-column prop="code" label="文物编号" width="120" />
          <el-table-column prop="name" label="名称" width="180" show-overflow-tooltip />
          <el-table-column prop="category" label="类别" width="80" />
          <el-table-column prop="era" label="年代" width="80" />
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="removeArtifactFromExhibition(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { exhibitionApi } from '@/api/exhibition'
import { artifactApi } from '@/api/artifact'

const router = useRouter()

const queryParams = ref({
  name: '',
  status: '',
  current: 1,
  size: 10
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const artifactDialogVisible = ref(false)
const currentExhibitionId = ref(null)
const artifactQuery = ref({
  name: '',
  category: ''
})
const availableArtifacts = ref([])
const exhibitionArtifacts = ref([])

const getList = async () => {
  loading.value = true
  try {
    const res = await exhibitionApi.getList(queryParams.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.value.current = 1
  getList()
}

const resetQuery = () => {
  queryParams.value = {
    name: '',
    status: '',
    current: 1,
    size: 10
  }
  getList()
}

const handleAdd = () => {
  router.push('/exhibitions/create')
}

const handleEdit = (id) => {
  router.push(`/exhibitions/${id}/edit`)
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该展览吗？', '提示', { type: 'warning' })
    .then(async () => {
      await exhibitionApi.delete(id)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

const getStatusLabel = (status) => {
  const map = {
    'PLANNING': '策划中',
    'ONGOING': '进行中',
    'FINISHED': '已结束'
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = {
    'PLANNING': 'info',
    'ONGOING': 'success',
    'FINISHED': 'warning'
  }
  return map[status] || ''
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

const handleManageArtifacts = async (row) => {
  currentExhibitionId.value = row.id
  artifactDialogVisible.value = true
  await loadExhibitionArtifacts()
  await queryAvailableArtifacts()
}

const loadExhibitionArtifacts = async () => {
  try {
    const res = await exhibitionApi.getArtifactDetails(currentExhibitionId.value)
    exhibitionArtifacts.value = res.data || []
  } catch (error) {
    ElMessage.error('加载展品失败')
  }
}

const queryAvailableArtifacts = async () => {
  try {
    const res = await artifactApi.getList(artifactQuery.value)
    availableArtifacts.value = res.data.records || []
  } catch (error) {
    ElMessage.error('查询可用展品失败')
  }
}

const addArtifactToExhibition = async (artifact) => {
  try {
    await exhibitionApi.addArtifact(currentExhibitionId.value, artifact.id, null, '')
    ElMessage.success('添加展品成功')
    await loadExhibitionArtifacts()
  } catch (error) {
    ElMessage.error('添加展品失败')
  }
}

const removeArtifactFromExhibition = async (row) => {
  try {
    await exhibitionApi.removeArtifact(currentExhibitionId.value, row.artifactId)
    ElMessage.success('移除展品成功')
    await loadExhibitionArtifacts()
  } catch (error) {
    ElMessage.error('移除展品失败')
  }
}

onMounted(() => {
  getList()
})
</script>
