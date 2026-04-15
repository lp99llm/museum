<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="文物名称">
          <el-input v-model="queryParams.name" placeholder="文物名称" clearable />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="queryParams.category" placeholder="请选择" clearable>
            <el-option label="陶瓷" value="陶瓷" />
            <el-option label="书画" value="书画" />
            <el-option label="玉器" value="玉器" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.currentState" placeholder="请选择" clearable>
            <el-option label="在库" value="IN_STORAGE" />
            <el-option label="展览中" value="IN_EXHIBITION" />
            <el-option label="修复中" value="IN_RESTORATION" />
            <el-option label="外借中" value="ON_LOAN" />
          </el-select>
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
        <el-table-column prop="code" label="文物编号" width="150" />
        <el-table-column prop="name" label="名称" width="200" />
        <el-table-column prop="category" label="类别" width="100" />
        <el-table-column prop="era" label="年代" width="100" />
        <el-table-column prop="currentState" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStateTagType(row.currentState)">
              {{ getStateLabel(row.currentState) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="存放位置" />
        <el-table-column label="操作" width="250" fixed="right">
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
import { artifactApi } from '@/api/artifact'   // 改为导入 artifactApi

const router = useRouter()

const queryParams = ref({
  name: '',
  category: '',
  currentState: '',
  current: 1,
  size: 10
})

const tableData = ref([])
const total = ref(0)

// 获取列表（适配 artifactApi.getList 的参数格式）
const getList = async () => {
  try {
    // 将前端的查询参数映射为后端接口期望的参数
    const params = {
      keyword: queryParams.value.name,        // 文物名称 -> keyword
      category: queryParams.value.category,
      currentState: queryParams.value.currentState,
      page: queryParams.value.current,
      pageSize: queryParams.value.size
    }
    const res = await artifactApi.getList(params)
    // 根据后端实际返回结构调整
    const data = res.data
    tableData.value = data.records || data || []
    total.value = data.total || tableData.value.length
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
    name: '',
    category: '',
    currentState: '',
    current: 1,
    size: 10
  }
  getList()
}

const handleAdd = () => {
  router.push('/artifacts/create')   // 确保路由路径正确
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
    'IN_STORAGE': 'success',
    'IN_EXHIBITION': 'primary',
    'IN_RESTORATION': 'warning',
    'ON_LOAN': 'info',
    'DISPOSED': 'danger'
  }
  return map[state] || ''
}

onMounted(() => {
  getList()
})
</script>