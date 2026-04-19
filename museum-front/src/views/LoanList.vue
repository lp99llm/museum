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
        <el-form-item label="借用单位">
          <el-input v-model="queryParams.borrowerInstitution" placeholder="借用单位" clearable />
        </el-form-item>
        <el-form-item label="当前阶段">
          <el-select v-model="queryParams.currentStage" placeholder="请选择" clearable>
            <el-option label="申请中" value="APPLY" />
            <el-option label="协议签订" value="AGREEMENT" />
            <el-option label="已借出" value="LOANED" />
            <el-option label="已归还" value="RETURNED" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="待审批" value="PENDING" />
            <el-option label="已借出" value="LOANED" />
            <el-option label="已归还" value="RETURNED" />
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
          <el-button type="primary" @click="handleAdd">新增外借</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="borrowerInstitution" label="借用单位" />
        <el-table-column prop="borrowerContact" label="联系人" width="100" />
        <el-table-column prop="applyDate" label="申请日期" width="120" />
        <el-table-column prop="loanDate" label="借出日期" width="120" />
        <el-table-column prop="expectedReturnDate" label="预计归还" width="120" />
        <el-table-column prop="actualReturnDate" label="实际归还" width="120" />
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
            <el-button link type="warning" @click="handleReturn(row)" v-if="row.status === 'LOANED'">归还</el-button>
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

    <el-dialog v-model="detailVisible" title="外借详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="文物编号">{{ currentRow?.artifactCode }}</el-descriptions-item>
        <el-descriptions-item label="文物名称">{{ currentRow?.artifactName }}</el-descriptions-item>
        <el-descriptions-item label="借用单位">{{ currentRow?.borrowerInstitution }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentRow?.borrowerContact }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow?.borrowerPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系地址">{{ currentRow?.borrowerAddress }}</el-descriptions-item>
        <el-descriptions-item label="申请日期">{{ currentRow?.applyDate }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow?.applicant }}</el-descriptions-item>
        <el-descriptions-item label="借出日期">{{ currentRow?.loanDate }}</el-descriptions-item>
        <el-descriptions-item label="预计归还">{{ currentRow?.expectedReturnDate }}</el-descriptions-item>
        <el-descriptions-item label="实际归还">{{ currentRow?.actualReturnDate }}</el-descriptions-item>
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
        <el-descriptions-item label="借用用途" :span="2">{{ currentRow?.loanPurpose }}</el-descriptions-item>
        <el-descriptions-item label="运输方式" :span="2">{{ currentRow?.transportMethod }}</el-descriptions-item>
        <el-descriptions-item label="安全措施" :span="2">{{ currentRow?.securityMeasures }}</el-descriptions-item>
        <el-descriptions-item label="借用地点" :span="2">{{ currentRow?.loanLocation }}</el-descriptions-item>
        <el-descriptions-item label="借用协议" :span="2">{{ currentRow?.loanAgreement }}</el-descriptions-item>
        <el-descriptions-item label="借用前状态" :span="2">{{ currentRow?.beforeCondition }}</el-descriptions-item>
        <el-descriptions-item label="归还后状态" :span="2">{{ currentRow?.afterCondition }}</el-descriptions-item>
        <el-descriptions-item label="归还图片" :span="2">{{ currentRow?.returnImage }}</el-descriptions-item>
        <el-descriptions-item label="归还备注" :span="2">{{ currentRow?.returnRemarks }}</el-descriptions-item>
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
            <el-option label="协议审批" value="AGREEMENT" />
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

    <el-dialog v-model="returnVisible" title="归还" width="500px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="实际归还日期" prop="actualReturnDate">
          <el-date-picker
            v-model="returnForm.actualReturnDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="归还图片">
          <el-input v-model="returnForm.returnImage" placeholder="请输入图片地址或上传" />
        </el-form-item>
        <el-form-item label="归还备注">
          <el-input v-model="returnForm.returnRemarks" type="textarea" :rows="3" placeholder="请输入归还备注，如检查情况、人员签字等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReturn">确认归还</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loanApi } from '@/api/loan'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])
const detailVisible = ref(false)
const approveVisible = ref(false)
const returnVisible = ref(false)
const currentRow = ref(null)
const flowHistory = ref([])

const queryParams = reactive({
  artifactCode: '',
  artifactName: '',
  borrowerInstitution: '',
  status: '',
  currentStage: '',
  startDate: '',
  endDate: '',
  current: 1,
  size: 10
})

const approveForm = reactive({
  loanId: null,
  stage: 'APPLY',
  approverName: '',
  approverRole: '',
  opinion: '',
  status: 'APPROVED'
})

const returnForm = reactive({
  loanId: null,
  actualReturnDate: '',
  returnImage: '',
  returnRemarks: ''
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
    const res = await loanApi.getPage(queryParams)
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
    borrowerInstitution: '',
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
  router.push('/loan/add')
}

const handleEdit = (row) => {
  router.push(`/loan/edit?id=${row.id}`)
}

const handleView = async (row) => {
  currentRow.value = row
  try {
    const res = await loanApi.getFlowHistory(row.id)
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
  approveForm.loanId = row.id
  approveForm.stage = row.currentStage
  approveForm.approverName = ''
  approveForm.approverRole = ''
  approveForm.opinion = ''
  approveForm.status = 'APPROVED'
  approveVisible.value = true
}

const submitApprove = async () => {
  try {
    await loanApi.approve(approveForm)
    ElMessage.success('审批提交成功')
    approveVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error('审批提交失败')
  }
}

const handleReturn = (row) => {
  currentRow.value = row
  returnForm.loanId = row.id
  returnForm.actualReturnDate = ''
  returnForm.returnImage = ''
  returnForm.returnRemarks = ''
  returnVisible.value = true
}

const submitReturn = async () => {
  try {
    await loanApi.returnLoan({
      loanId: returnForm.loanId,
      actualReturnDate: returnForm.actualReturnDate
    })
    ElMessage.success('归还成功')
    returnVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error('归还失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该外借记录吗？', '提示', { type: 'warning' })
    await loanApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStageLabel = (stage) => {
  const map = { APPLY: '申请中', AGREEMENT: '协议签订', LOANED: '已借出', RETURNED: '已归还' }
  return map[stage] || stage
}

const getStageTagType = (stage) => {
  const map = { APPLY: 'info', AGREEMENT: 'warning', LOANED: 'danger', RETURNED: 'success' }
  return map[stage] || ''
}

const getStatusLabel = (status) => {
  const map = { PENDING: '待审批', LOANED: '已借出', RETURNED: '已归还', REJECTED: '已拒绝' }
  return map[status] || status
}

const getStatusTagType = (status) => {
  const map = { PENDING: 'warning', LOANED: 'danger', RETURNED: 'success', REJECTED: 'danger' }
  return map[status] || ''
}

onMounted(() => {
  getList()
})
</script>
