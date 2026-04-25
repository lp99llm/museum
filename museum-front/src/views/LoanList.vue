<template>
  <div class="process-list-page">
    <el-card class="toolbar-card">
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="文物编号">
          <el-input v-model="queryParams.artifactCode" placeholder="请输入文物编号" clearable />
        </el-form-item>
        <el-form-item label="文物名称">
          <el-input v-model="queryParams.artifactName" placeholder="请输入文物名称" clearable />
        </el-form-item>
        <el-form-item label="借用单位">
          <el-input v-model="queryParams.borrowerInstitution" placeholder="请输入借用单位" clearable />
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
            <el-option label="已驳回" value="REJECTED" />
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
          <el-button v-permission="PERMISSIONS.LOAN_ADD" type="primary" @click="handleAdd">新增外借</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
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
            <el-tag :type="getLoanStageType(row.currentStage)">
              {{ getLoanStageLabel(row.currentStage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getLoanStatusType(row.status)">
              {{ getLoanStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'PENDING'" v-permission="PERMISSIONS.LOAN_EDIT" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 'PENDING'" v-permission="PERMISSIONS.LOAN_APPROVE" link type="success" @click="handleApprove(row)">审批</el-button>
            <el-button v-if="row.status === 'LOANED'" v-permission="PERMISSIONS.LOAN_RETURN" link type="warning" @click="handleReturn(row)">归还</el-button>
            <el-button v-if="row.status === 'PENDING'" v-permission="PERMISSIONS.LOAN_DELETE" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
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
          <el-tag :type="getLoanStageType(currentRow?.currentStage)">
            {{ getLoanStageLabel(currentRow?.currentStage) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getLoanStatusType(currentRow?.status)">
            {{ getLoanStatusLabel(currentRow?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="借用用途" :span="2">{{ currentRow?.loanPurpose || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="运输方式" :span="2">{{ currentRow?.transportMethod || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="安全措施" :span="2">{{ currentRow?.securityMeasures || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="借用地点" :span="2">{{ currentRow?.loanLocation || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="借用协议" :span="2">{{ currentRow?.loanAgreement || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="借用前状态" :span="2">{{ currentRow?.beforeCondition || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="归还后状态" :span="2">{{ currentRow?.afterCondition || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="归还图片" :span="2">{{ currentRow?.returnImage || '未上传' }}</el-descriptions-item>
        <el-descriptions-item label="归还备注" :span="2">{{ currentRow?.returnRemarks || '暂无' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>审批历史</el-divider>
      <el-timeline v-if="flowHistory.length > 0">
        <el-timeline-item v-for="item in flowHistory" :key="item.id" :timestamp="item.approveTime" placement="top">
          <p><strong>{{ item.stageName }}</strong> - {{ item.approverName }}（{{ item.approverRole }}）</p>
          <p>意见：{{ item.approvalOpinion }}</p>
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
            <el-radio value="APPROVED">通过</el-radio>
            <el-radio value="REJECTED">拒绝</el-radio>
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
          <el-input v-model="returnForm.returnImage" placeholder="请输入图片地址或上传结果" />
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
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loanApi } from '@/api/loan'
import { useFlowDialogs } from '@/composables/useFlowDialogs'
import { usePageQuery } from '@/composables/usePageQuery'
import { PERMISSIONS } from '@/constants/permissions'
import {
  LOAN_STAGE_LABELS,
  LOAN_STAGE_TYPES,
  LOAN_STATUS_LABELS,
  LOAN_STATUS_TYPES,
  getStatusLabel,
  getStatusType
} from '@/utils/status'

const router = useRouter()
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

const {
  currentRow,
  flowHistory,
  detailVisible,
  approveVisible,
  returnVisible,
  openDetail,
  openApprove,
  openReturn,
  closeApprove,
  closeReturn
} = useFlowDialogs({
  loadHistory: (row) => loanApi.getFlowHistory(row.id),
  initApprove: (row) => {
    approveForm.loanId = row.id
    approveForm.stage = row.currentStage || 'APPLY'
    approveForm.approverName = ''
    approveForm.approverRole = ''
    approveForm.opinion = ''
    approveForm.status = 'APPROVED'
  },
  initReturn: (row) => {
    returnForm.loanId = row.id
    returnForm.actualReturnDate = ''
    returnForm.returnImage = ''
    returnForm.returnRemarks = ''
  }
})

const getLoanStageLabel = (value) => getStatusLabel(LOAN_STAGE_LABELS, value)
const getLoanStageType = (value) => getStatusType(LOAN_STAGE_TYPES, value)
const getLoanStatusLabel = (value) => getStatusLabel(LOAN_STATUS_LABELS, value)
const getLoanStatusType = (value) => getStatusType(LOAN_STATUS_TYPES, value)

const createInitialQueryParams = () => ({
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

const { loading, tableData, total, dateRange, loadPage: getList, handleQuery, resetQuery } = usePageQuery({
  queryParams,
  createInitialQueryParams,
  fetcher: loanApi.getPage,
  onError: (error) => {
    ElMessage.error(error?.response?.data?.message || '加载外借数据失败')
  }
})

const handleAdd = () => {
  router.push('/loan/add')
}

const handleEdit = (row) => {
  router.push(`/loan/edit?id=${row.id}`)
}

const handleView = (row) => openDetail(row)

const handleApprove = (row) => openApprove(row)

const submitApprove = async () => {
  try {
    await loanApi.approve(approveForm)
    ElMessage.success('审批提交成功')
    closeApprove()
    getList()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '审批提交失败')
  }
}

const handleReturn = (row) => openReturn(row)

const submitReturn = async () => {
  try {
    await loanApi.returnLoan({
      loanId: returnForm.loanId,
      actualReturnDate: returnForm.actualReturnDate
    })
    ElMessage.success('归还成功')
    closeReturn()
    getList()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '归还失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除文物“${row.artifactName}”的外借记录吗？`, '提示', { type: 'warning' })
    await loanApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '删除失败')
    }
  }
}
</script>

<style scoped>
.process-list-page {
  display: grid;
  gap: 16px;
}

.toolbar-card,
.table-card {
  padding: 18px;
}

.pagination {
  margin-top: 16px;
}
</style>
