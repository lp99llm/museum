<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="文物编号">
          <el-input v-model="queryParams.artifactCode" placeholder="文物编号" clearable />
        </el-form-item>
        <el-form-item label="文物名称">
          <el-input v-model="queryParams.artifactName" placeholder="文物名称" clearable />
        </el-form-item>
        <el-form-item label="存放库区">
          <el-input v-model="queryParams.storageArea" placeholder="存放库区" clearable />
        </el-form-item>
        <el-form-item label="入库状态">
          <el-select v-model="queryParams.storageStatus" placeholder="请选择" clearable>
            <el-option label="待确认" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增入库</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="storageDate" label="入库日期" width="120" />
        <el-table-column prop="storageArea" label="存放库区" width="100" />
        <el-table-column prop="storageShelf" label="货架号" width="100" />
        <el-table-column prop="storagePosition" label="存放位置" width="100" />
        <el-table-column prop="storageStatus" label="入库状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.storageStatus)">
              {{ getStatusLabel(row.storageStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="temperature" label="温度" width="80">
          <template #default="{ row }">
            {{ row.temperature ? row.temperature + '°C' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="humidity" label="湿度" width="80">
          <template #default="{ row }">
            {{ row.humidity ? row.humidity + '%' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="handler" label="操作人" width="100" />
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { warehousingApi } from '@/api/warehousing'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])

const queryParams = reactive({
  artifactCode: '',
  artifactName: '',
  storageArea: '',
  storageStatus: '',
  startDate: '',
  endDate: '',
  current: 1,
  size: 10
})

const getList = async () => {
  loading.value = true
  try {
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
    } else {
      queryParams.startDate = ''
      queryParams.endDate = ''
    }
    const res = await warehousingApi.getPage(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.current = 1
  getList()
}

const resetQuery = () => {
  dateRange.value = []
  Object.assign(queryParams, {
    artifactCode: '',
    artifactName: '',
    storageArea: '',
    storageStatus: '',
    startDate: '',
    endDate: '',
    current: 1,
    size: 10
  })
  getList()
}

const handleAdd = () => {
  router.push('/warehousing/add')
}

const handleEdit = (row) => {
  router.push(`/warehousing/edit?id=${row.id}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该入库记录吗？', '提示', {
      type: 'warning'
    })
    await warehousingApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStatusLabel = (status) => {
  const map = { PENDING: '待确认', CONFIRMED: '已确认', REJECTED: '已拒绝' }
  return map[status] || status
}

const getStatusTagType = (status) => {
  const map = { PENDING: 'warning', CONFIRMED: 'success', REJECTED: 'danger' }
  return map[status] || ''
}

onMounted(() => {
  getList()
})
</script>
