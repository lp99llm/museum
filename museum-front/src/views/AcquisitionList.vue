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
        <el-form-item label="征集类型">
          <el-select v-model="queryParams.acquisitionType" placeholder="请选择" clearable>
            <el-option label="捐赠" value="DONATION" />
            <el-option label="购买" value="PURCHASE" />
            <el-option label="移交" value="TRANSFER" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源机构">
          <el-input v-model="queryParams.sourceInstitution" placeholder="来源机构" clearable />
        </el-form-item>
        <el-form-item label="征集日期">
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
          <el-button type="primary" @click="handleAdd">新增征集</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="acquisitionType" label="征集类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.acquisitionType)">
              {{ getTypeLabel(row.acquisitionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="acquisitionDate" label="征集日期" width="120" />
        <el-table-column prop="sourceInstitution" label="来源机构" />
        <el-table-column prop="sourceContact" label="联系人" width="100" />
        <el-table-column prop="acquisitionCost" label="征集费用" width="120">
          <template #default="{ row }">
            {{ row.acquisitionCost ? `￥${row.acquisitionCost}` : '-' }}
          </template>
        </el-table-column>
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
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { acquisitionApi } from '@/api/acquisition'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])

const queryParams = reactive({
  artifactCode: '',
  artifactName: '',
  acquisitionType: '',
  sourceInstitution: '',
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
    const res = await acquisitionApi.getPage(queryParams)
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch {
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
    acquisitionType: '',
    sourceInstitution: '',
    startDate: '',
    endDate: '',
    current: 1,
    size: 10
  })
  getList()
}

const handleAdd = () => {
  router.push('/acquisition/add')
}

const handleEdit = (row) => {
  router.push(`/acquisition/edit?id=${row.id}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该征集记录吗？', '提示', {
      type: 'warning'
    })
    await acquisitionApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getTypeLabel = (type) => {
  const map = { DONATION: '捐赠', PURCHASE: '购买', TRANSFER: '移交' }
  return map[type] || type
}

const getTypeTagType = (type) => {
  const map = { DONATION: 'success', PURCHASE: 'primary', TRANSFER: 'warning' }
  return map[type] || ''
}

onMounted(() => {
  getList()
})
</script>
