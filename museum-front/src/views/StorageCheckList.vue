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
        <el-form-item label="检查类型">
          <el-select v-model="queryParams.checkType" placeholder="请选择" clearable>
            <el-option label="日常检查" value="DAILY" />
            <el-option label="月度抽检" value="MONTHLY" />
            <el-option label="季度检查" value="QUARTERLY" />
            <el-option label="半年度清点" value="HALF_YEAR" />
            <el-option label="年度普查" value="YEARLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="检查结果">
          <el-select v-model="queryParams.checkResult" placeholder="请选择" clearable>
            <el-option label="正常" value="NORMAL" />
            <el-option label="异常" value="ABNORMAL" />
            <el-option label="需修复" value="NEED_REPAIR" />
          </el-select>
        </el-form-item>
        <el-form-item label="检查日期">
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
          <el-button type="primary" @click="handleAdd">新增检查</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="checkDate" label="检查日期" width="120" />
        <el-table-column prop="checkType" label="检查类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getCheckTypeTagType(row.checkType)">
              {{ getCheckTypeLabel(row.checkType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkResult" label="检查结果" width="100">
          <template #default="{ row }">
            <el-tag :type="getResultTagType(row.checkResult)">
              {{ getResultLabel(row.checkResult) }}
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
        <el-table-column prop="nextCheckDate" label="下次检查" width="120" />
        <el-table-column prop="checkerName" label="检查人" width="100" />
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
import { storageCheckApi } from '@/api/storageCheck'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])

const queryParams = reactive({
  artifactCode: '',
  artifactName: '',
  checkType: '',
  checkResult: '',
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
    const res = await storageCheckApi.getPage(queryParams)
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
    checkType: '',
    checkResult: '',
    startDate: '',
    endDate: '',
    current: 1,
    size: 10
  })
  getList()
}

const handleAdd = () => {
  router.push('/storage-check/add')
}

const handleEdit = (row) => {
  router.push(`/storage-check/edit?id=${row.id}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该检查记录吗？', '提示', {
      type: 'warning'
    })
    await storageCheckApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getCheckTypeLabel = (type) => {
  const map = { DAILY: '日常', MONTHLY: '月度', QUARTERLY: '季度', HALF_YEAR: '半年度', YEARLY: '年度' }
  return map[type] || type
}

const getCheckTypeTagType = (type) => {
  const map = { DAILY: 'info', MONTHLY: '', QUARTERLY: 'success', HALF_YEAR: 'warning', YEARLY: 'danger' }
  return map[type] || ''
}

const getResultLabel = (result) => {
  const map = { NORMAL: '正常', ABNORMAL: '异常', NEED_REPAIR: '需修复' }
  return map[result] || result
}

const getResultTagType = (result) => {
  const map = { NORMAL: 'success', ABNORMAL: 'danger', NEED_REPAIR: 'warning' }
  return map[result] || ''
}

onMounted(() => {
  getList()
})
</script>
