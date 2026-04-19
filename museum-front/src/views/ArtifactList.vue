<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="文物编号">
          <el-input v-model="queryParams.code" placeholder="文物编号" clearable />
        </el-form-item>
        <el-form-item label="文物名称">
          <el-input v-model="queryParams.name" placeholder="文物名称" clearable />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="queryParams.category" placeholder="请选择" clearable style="width: 120px">
            <el-option label="陶瓷" value="陶瓷" />
            <el-option label="书画" value="书画" />
            <el-option label="玉器" value="玉器" />
            <el-option label="青铜器" value="青铜器" />
            <el-option label="金银器" value="金银器" />
            <el-option label="铁器" value="铁器" />
            <el-option label="漆器" value="漆器" />
            <el-option label="织绣" value="织绣" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="年代">
          <el-input v-model="queryParams.era" placeholder="年代" clearable />
        </el-form-item>
        <el-form-item label="材质">
          <el-input v-model="queryParams.material" placeholder="材质" clearable />
        </el-form-item>
        <el-form-item label="保存状况">
          <el-select v-model="queryParams.preservationStatus" placeholder="请选择" clearable style="width: 120px">
            <el-option label="完好" value="完好" />
            <el-option label="轻度破损" value="轻度破损" />
            <el-option label="中度破损" value="中度破损" />
            <el-option label="重度破损" value="重度破损" />
          </el-select>
        </el-form-item>
        <el-form-item label="鉴定等级">
          <el-select v-model="queryParams.appraisalLevel" placeholder="请选择" clearable style="width: 120px">
            <el-option label="一级文物" value="一级" />
            <el-option label="二级文物" value="二级" />
            <el-option label="三级文物" value="三级" />
            <el-option label="一般文物" value="一般" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.currentState" placeholder="请选择" clearable style="width: 120px">
            <el-option label="在库" value="IN_STORAGE" />
            <el-option label="展览中" value="IN_EXHIBITION" />
            <el-option label="修复中" value="IN_RESTORATION" />
            <el-option label="外借中" value="ON_LOAN" />
            <el-option label="征集入库" value="ACQUIRED" />
            <el-option label="已处置" value="DISPOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="存放位置">
          <el-input v-model="queryParams.location" placeholder="存放位置" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <el-table :data="tableData" border stripe>
        <el-table-column prop="code" label="文物编号" width="120" />
        <el-table-column prop="name" label="名称" width="180" show-overflow-tooltip />
        <el-table-column prop="category" label="类别" width="80" />
        <el-table-column prop="era" label="年代" width="80" />
        <el-table-column prop="material" label="材质" width="80" show-overflow-tooltip />
        <el-table-column prop="appraisalLevel" label="鉴定等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getAppraisalLevelType(row.appraisalLevel)">
              {{ row.appraisalLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="preservationStatus" label="保存状况" width="100" />
        <el-table-column prop="currentState" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStateTagType(row.currentState)">
              {{ getStateLabel(row.currentState) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="存放位置" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row.id)">编辑</el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { artifactApi } from '@/api/artifact'

const router = useRouter()

const queryParams = ref({
  code: '',
  name: '',
  category: '',
  era: '',
  material: '',
  source: '',
  preservationStatus: '',
  appraisalLevel: '',
  currentState: '',
  location: '',
  current: 1,
  size: 10
})

const tableData = ref([])
const total = ref(0)

const getList = async () => {
  try {
    const res = await artifactApi.getList(queryParams.value)
    const data = res.data
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('获取列表失败')
  }
}

const handleQuery = () => {
  queryParams.value.current = 1
  getList()
}

const resetQuery = () => {
  queryParams.value = {
    code: '',
    name: '',
    category: '',
    era: '',
    material: '',
    source: '',
    preservationStatus: '',
    appraisalLevel: '',
    currentState: '',
    location: '',
    current: 1,
    size: 10
  }
  getList()
}

const handleAdd = () => {
  router.push('/artifacts/create')
}

const handleEdit = (id) => {
  router.push(`/artifacts/${id}/edit`)
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该文物吗？', '提示', { type: 'warning' })
    .then(async () => {
      await artifactApi.delete(id)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

const getStateLabel = (state) => {
  const map = {
    'ACQUIRED': '征集入库',
    'IN_STORAGE': '在库',
    'IN_EXHIBITION': '展览中',
    'IN_RESTORATION': '修复中',
    'ON_LOAN': '外借中',
    'DISPOSED': '已处置'
  }
  return map[state] || state
}

const getStateTagType = (state) => {
  const map = {
    'ACQUIRED': 'info',
    'IN_STORAGE': 'success',
    'IN_EXHIBITION': 'primary',
    'IN_RESTORATION': 'warning',
    'ON_LOAN': 'info',
    'DISPOSED': 'danger'
  }
  return map[state] || ''
}

const getAppraisalLevelType = (level) => {
  const map = {
    '一级': 'danger',
    '二级': 'warning',
    '三级': 'info',
    '一般': 'success'
  }
  return map[level] || ''
}

onMounted(() => {
  getList()
})
</script>
