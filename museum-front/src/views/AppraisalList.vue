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
            <el-option label="一级" value="LEVEL1" />
            <el-option label="二级" value="LEVEL2" />
            <el-option label="三级" value="LEVEL3" />
            <el-option label="四级" value="LEVEL4" />
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
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="appraisalDate" label="鉴定日期" width="120" />
        <el-table-column prop="expertName" label="专家姓名" width="100" />
        <el-table-column prop="expertTitle" label="专家职称" width="100" />
        <el-table-column prop="appraisalLevel" label="鉴定等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.appraisalLevel)">
              {{ getLevelLabel(row.appraisalLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="estimatedValue" label="估值金额" width="120">
          <template #default="{ row }">
            {{ row.estimatedValue ? '¥' + row.estimatedValue : '-' }}
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { appraisalApi } from '@/api/appraisal'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])

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
    const res = await appraisalApi.getPage(queryParams)
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

const getLevelLabel = (level) => {
  const map = { LEVEL1: '一级', LEVEL2: '二级', LEVEL3: '三级', LEVEL4: '四级' }
  return map[level] || level
}

const getLevelTagType = (level) => {
  const map = { LEVEL1: 'danger', LEVEL2: 'warning', LEVEL3: 'success', LEVEL4: 'info' }
  return map[level] || ''
}

onMounted(() => {
  getList()
})
</script>
