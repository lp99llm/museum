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
        <el-form-item label="申请人">
          <el-input v-model="queryParams.applicant" placeholder="申请人" clearable />
        </el-form-item>
        <el-form-item label="处置类型">
          <el-select v-model="queryParams.disposalType" placeholder="请选择" clearable>
            <el-option label="移交" value="TRANSFER" />
            <el-option label="销毁" value="DESTROY" />
            <el-option label="退藏" value="REMOVE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前阶段">
          <el-select v-model="queryParams.currentStage" placeholder="请选择" clearable>
            <el-option label="申请中" value="APPLY" />
            <el-option label="专家评估" value="EVALUATION" />
            <el-option label="公示" value="PUBLIC" />
            <el-option label="备案" value="RECORD" />
            <el-option label="执行" value="EXECUTION" />
            <el-option label="归档" value="ARCHIVE" />
            <el-option label="完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="待审批" value="PENDING" />
            <el-option label="已批准" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请日期">
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
          <el-button type="primary" @click="handleAdd">新增处置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="applicant" label="申请人" width="100" />
        <el-table-column prop="disposalType" label="处置类型" width="100">
          <template #default="{ row }">
            {{ getDisposalTypeLabel(row.disposalType) }}
          </template>
        </el-table-column>
        <el-table-column prop="applyDate" label="申请日期" width="120" />
        <el-table-column prop="currentStage" label="当前阶段" width="120">
          <template #default="{ row }">
            <el-tag :type="getStageTagType(row.currentStage)">
              {{ getStageLabel(row.currentStage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(row)" v-if="row.status === 'PENDING'">编辑</el-button>
            <el-button link type="success" @click="handleApprove(row)" v-if="row.status === 'PENDING'">审批</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.status === 'PENDING'">删除</el-button>
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

    <el-dialog v-model="detailVisible" title="出馆处置详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="文物编号">{{ currentRow?.artifactCode }}</el-descriptions-item>
        <el-descriptions-item label="文物名称">{{ currentRow?.artifactName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow?.applicant }}</el-descriptions-item>
        <el-descriptions-item label="申请日期">{{ currentRow?.applyDate }}</el-descriptions-item>
        <el-descriptions-item label="处置类型">{{ getDisposalTypeLabel(currentRow?.disposalType) }}</el-descriptions-item>
        <el-descriptions-item label="申请原因">{{ currentRow?.applyReason }}</el-descriptions-item>
        <el-descriptions-item label="当前阶段">
          <el-tag :type="getStageTagType(currentRow?.currentStage)">
            {{ getStageLabel(currentRow?.currentStage) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentRow?.status)">
            {{ getStatusLabel(currentRow?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="评估报告" :span="2">{{ currentRow?.evaluationReport }}</el-descriptions-item>
        <el-descriptions-item label="专家意见" :span="2">{{ currentRow?.expertOpinion }}</el-descriptions-item>
        <el-descriptions-item label="公示开始" :span="2">{{ currentRow?.publicStartTime }}</el-descriptions-item>
        <el-descriptions-item label="公示结束" :span="2">{{ currentRow?.publicEndTime }}</el-descriptions-item>
        <el-descriptions-item label="公示结果" :span="2">{{ currentRow?.publicResult }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ currentRow?.approvalOpinion }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ currentRow?.approver }}</el-descriptions-item>
        <el-descriptions-item label="审批日期">{{ currentRow?.approvalDate }}</el-descriptions-item>
        <el-descriptions-item label="备案状态">{{ currentRow?.recordStatus }}</el-descriptions-item>
        <el-descriptions-item label="备案日期">{{ currentRow?.recordDate }}</el-descriptions-item>
        <el-descriptions-item label="备案机构" :span="2">{{ currentRow?.recordOrganization }}</el-descriptions-item>
        <el-descriptions-item label="处置日期">{{ currentRow?.disposalDate }}</el-descriptions-item>
        <el-descriptions-item label="执行人">{{ currentRow?.operator }}</el-descriptions-item>
        <el-descriptions-item label="执行结果" :span="2">{{ currentRow?.executionResult }}</el-descriptions-item>
        <el-descriptions-item label="档案编号">{{ currentRow?.archiveNo }}</el-descriptions-item>
        <el-descriptions-item label="归档日期">{{ currentRow?.archiveDate }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow?.remarks }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>审批历史</el-divider>
      <el-timeline v-if="flowHistory.length > 0">
        <el-timeline-item v-for="item in flowHistory" :key="item.id" :timestamp="item.approveTime" placement="top">
          <p><strong>{{ item.stageName }}</strong> - {{ item.approverName }} ({{ item.approverRole }})</p>
          <p>意见: {{ item.approvalOpinion }}</p>
          <el-tag :type="item.status === 'APPROVED' ? 'success' : 'danger'" size="small">
            {{ item.status === 'APPROVED' ? '通过' : '拒绝' }}
          </el-tag>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无审批记录" />

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="approveVisible" title="审批" width="500px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="审批阶段">
          <el-select v-model="approveForm.stage" style="width: 100%">
            <el-option label="申请审批" value="APPLY" />
            <el-option label="评估审批" value="EVALUATION" />
            <el-option label="公示审批" value="PUBLIC" />
            <el-option label="备案审批" value="RECORD" />
            <el-option label="执行审批" value="EXECUTION" />
            <el-option label="归档审批" value="ARCHIVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批人">
          <el-input v-model="approveForm.approverName" placeholder="请输入审批人姓名" />
        </el-form-item>
        <el-form-item label="审批角色">
          <el-input v-model="approveForm.approverRole" placeholder="如：部门负责人" />
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="approveForm.opinion" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
        <el-form-item label="审批结果">
          <el-radio-group v-model="approveForm.status">
            <el-radio label="APPROVED">通过</el-radio>
            <el-radio label="REJECTED">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApprove">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { disposalApi } from '@/api/disposal'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])
const detailVisible = ref(false)
const approveVisible = ref(false)
const currentRow = ref(null)
const flowHistory = ref([])

const queryParams = reactive({
  artifactCode: '',
  artifactName: '',
  applicant: '',
  disposalType: '',
  status: '',
  currentStage: '',
  startDate: '',
  endDate: '',
  current: 1,
  size: 10
})

const approveForm = reactive({
  disposalId: null,
  stage: 'APPLY',
  approverName: '',
  approverRole: '',
  opinion: '',
  status: 'APPROVED'
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
    const res = await disposalApi.getPage(queryParams)
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
    applicant: '',
    disposalType: '',
    status: '',
    currentStage: '',
    startDate: '',
    endDate: '',
    current: 1,
    size: 10
  })
  getList()
}

const handleAdd = () => {
  router.push('/disposal/add')
}

const handleEdit = (row) => {
  router.push(`/disposal/edit?id=${row.id}`)
}

const handleView = async (row) => {
  currentRow.value = row
  try {
    const res = await disposalApi.getFlowHistory(row.id)
    if (res.code === 200) {
      flowHistory.value = res.data || []
    }
  } catch (error) {
    flowHistory.value = []
  }
  detailVisible.value = true
}

const handleApprove = (row) => {
  currentRow.value = row
  approveForm.disposalId = row.id
  approveForm.stage = row.currentStage
  approveForm.approverName = ''
  approveForm.approverRole = ''
  approveForm.opinion = ''
  approveForm.status = 'APPROVED'
  approveVisible.value = true
}

const submitApprove = async () => {
  try {
    await disposalApi.approve(approveForm)
    ElMessage.success('审批提交成功')
    approveVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error('审批提交失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该出馆处置记录吗？', '提示', { type: 'warning' })
    await disposalApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getDisposalTypeLabel = (type) => {
  const map = { TRANSFER: '移交', DESTROY: '销毁', REMOVE: '退藏', OTHER: '其他' }
  return map[type] || type
}

const getStageLabel = (stage) => {
  const map = { 
    APPLY: '申请中', 
    EVALUATION: '专家评估', 
    PUBLIC: '公示', 
    RECORD: '备案', 
    EXECUTION: '执行', 
    ARCHIVE: '归档', 
    COMPLETED: '完成' 
  }
  return map[stage] || stage
}

const getStageTagType = (stage) => {
  const map = { 
    APPLY: 'info', 
    EVALUATION: 'warning', 
    PUBLIC: 'primary', 
    RECORD: 'success', 
    EXECUTION: 'danger', 
    ARCHIVE: 'success', 
    COMPLETED: 'success' 
  }
  return map[stage] || ''
}

const getStatusLabel = (status) => {
  const map = { PENDING: '待审批', APPROVED: '已批准', REJECTED: '已拒绝' }
  return map[status] || status
}

const getStatusTagType = (status) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || ''
}

onMounted(() => {
  getList()
})
</script>
