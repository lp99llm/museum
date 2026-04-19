<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="展览名称">
          <el-input v-model="queryParams.exhibitionId" placeholder="展览ID" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleAdd">新增评估</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="exhibitionName" label="展览名称" width="200" show-overflow-tooltip />
        <el-table-column prop="visitorCount" label="参观人数" width="100" />
        <el-table-column prop="feedbackScore" label="观众评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.feedbackScore" disabled show-score text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="safetyIncidents" label="安全事故" width="80" />
        <el-table-column prop="artifactDamageCount" label="文物损坏" width="80" />
        <el-table-column prop="budgetActual" label="实际支出" width="100">
          <template #default="{ row }">
            ¥{{ row.budgetActual }}
          </template>
        </el-table-column>
        <el-table-column prop="revenue" label="收入" width="100">
          <template #default="{ row }">
            ¥{{ row.revenue }}
          </template>
        </el-table-column>
        <el-table-column prop="evaluator" label="评估人" width="100" />
        <el-table-column prop="evaluationDate" label="评估日期" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row.id)">编辑</el-button>
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
import { exhibitionEvaluationApi } from '@/api/exhibitionEvaluation'

const router = useRouter()

const queryParams = ref({
  exhibitionId: null,
  current: 1,
  size: 10
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const getList = async () => {
  loading.value = true
  try {
    const res = await exhibitionEvaluationApi.getList(queryParams.value)
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
    exhibitionId: null,
    current: 1,
    size: 10
  }
  getList()
}

const handleAdd = () => {
  router.push('/exhibition-evaluations/create')
}

const handleEdit = (id) => {
  router.push(`/exhibition-evaluations/${id}/edit`)
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该评估报告吗？', '提示', { type: 'warning' })
    .then(async () => {
      await exhibitionEvaluationApi.delete(id)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

onMounted(() => {
  getList()
})
</script>
