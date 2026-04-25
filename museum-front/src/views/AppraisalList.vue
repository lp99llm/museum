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
        <el-form-item label="鉴定等级">
          <el-select v-model="queryParams.appraisalLevel" placeholder="请选择" clearable>
            <el-option v-for="item in appraisalLevelOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="专家姓名">
          <el-input v-model="queryParams.expertName" placeholder="专家姓名" clearable />
        </el-form-item>
        <el-form-item label="鉴定日期">
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
          <el-button type="primary" @click="handleAdd">新增鉴定</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <el-table v-loading="loading" :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="appraisalDate" label="鉴定日期" width="120" />
        <el-table-column prop="expertName" label="专家姓名" width="100" />
        <el-table-column prop="expertTitle" label="专家职称" width="120" />
        <el-table-column prop="appraisalLevel" label="鉴定等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getAppraisalLevelType(row.appraisalLevel)">
              {{ getAppraisalLevelLabel(row.appraisalLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="estimatedValue" label="估价金额" width="140">
          <template #default="{ row }">
            {{ row.estimatedValue ? `￥${row.estimatedValue}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="appraisalResult" label="鉴定结果" show-overflow-tooltip />
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
import { appraisalApi } from '@/api/appraisal'
import { getAppraisalLevelLabel, getAppraisalLevelType, normalizeAppraisalLevel } from '@/utils/status'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])

const appraisalLevelOptions = ['一级', '二级', '三级', '四级', '一般文物']

const queryParams = reactive({
  artifactCode: '',
  artifactName: '',
  appraisalLevel: '',
  expertName: '',
  startDate: '',
  endDate: '',
  current: 1,
  size: 10
})

const normalizeRow = (row) => ({
  ...row,
  appraisalLevel: normalizeAppraisalLevel(row.appraisalLevel)
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
    const payload = {
      ...queryParams,
      appraisalLevel: normalizeAppraisalLevel(queryParams.appraisalLevel) || ''
    }
    const res = await appraisalApi.getPage(payload)
    tableData.value = (res.records || []).map(normalizeRow)
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
    appraisalLevel: '',
    expertName: '',
    startDate: '',
    endDate: '',
    current: 1,
    size: 10
  })
  getList()
}

const handleAdd = () => {
  router.push('/appraisal/add')
}

const handleEdit = (row) => {
  router.push(`/appraisal/edit?id=${row.id}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该鉴定记录吗？', '提示', {
      type: 'warning'
    })
    await appraisalApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  getList()
})
</script>
