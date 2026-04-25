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
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="待审批" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
            <el-option label="已出库" value="OUTBOUND" />
            <el-option label="已归还" value="RETURNED" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前阶段">
          <el-select v-model="queryParams.currentStage" placeholder="请选择" clearable>
            <el-option label="部门审批" value="STAGE1" />
            <el-option label="分管审批" value="STAGE2" />
            <el-option label="馆长审批" value="STAGE3" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已归还" value="RETURNED" />
          </el-select>
        </el-form-item>
        <el-form-item label="出库日期">
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
          <el-button v-permission="PERMISSIONS.OUTBOUND_ADD" type="primary" @click="handleAdd">新增出库</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="outboundDate" label="出库日期" width="120" />
        <el-table-column prop="targetExhibition" label="目标展览" />
        <el-table-column prop="targetLocation" label="目标位置" width="150" />
        <el-table-column prop="currentStage" label="审批阶段" width="120">
          <template #default="{ row }">
            <el-tag :type="getOutboundStageType(row.currentStage)">
              {{ getOutboundStageLabel(row.currentStage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOutboundStatusType(row.status)">
              {{ getOutboundStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedReturnDate" label="预计归还" width="120" />
        <el-table-column prop="actualReturnDate" label="实际归还" width="120" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'PENDING'" v-permission="PERMISSIONS.OUTBOUND_EDIT" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 'PENDING'" v-permission="PERMISSIONS.OUTBOUND_APPROVE" link type="success" @click="handleApprove(row)">审批</el-button>
            <el-button
              v-if="row.status === 'APPROVED' || row.status === 'OUTBOUND'"
              link
              type="warning"
              v-permission="PERMISSIONS.OUTBOUND_RETURN"
              @click="handleReturn(row)"
            >
              回库确认
            </el-button>
            <el-button v-if="row.status === 'PENDING'" v-permission="PERMISSIONS.OUTBOUND_DELETE" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="detailVisible" title="出库详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="文物编号">{{ currentRow?.artifactCode }}</el-descriptions-item>
        <el-descriptions-item label="文物名称">{{ currentRow?.artifactName }}</el-descriptions-item>
        <el-descriptions-item label="出库日期">{{ currentRow?.outboundDate }}</el-descriptions-item>
        <el-descriptions-item label="预计归还">{{ currentRow?.expectedReturnDate }}</el-descriptions-item>
        <el-descriptions-item label="实际归还">{{ currentRow?.actualReturnDate }}</el-descriptions-item>
        <el-descriptions-item label="目标展览">{{ currentRow?.targetExhibition }}</el-descriptions-item>
        <el-descriptions-item label="目标位置">{{ currentRow?.targetLocation }}</el-descriptions-item>
        <el-descriptions-item label="目标联系人">{{ currentRow?.targetContact }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow?.targetPhone }}</el-descriptions-item>
        <el-descriptions-item label="出库事由" :span="2">{{ currentRow?.outboundReason }}</el-descriptions-item>
        <el-descriptions-item label="当前阶段">
          <el-tag :type="getOutboundStageType(currentRow?.currentStage)">
            {{ getOutboundStageLabel(currentRow?.currentStage) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getOutboundStatusType(currentRow?.status)">
            {{ getOutboundStatusLabel(currentRow?.status) }}
          </el-tag>
        </el-descriptions-item>
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
            <el-option label="部门审批" value="STAGE1" />
            <el-option label="分管审批" value="STAGE2" />
            <el-option label="馆长审批" value="STAGE3" />
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

    <el-dialog v-model="returnVisible" title="回库确认" width="500px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="归还图片">
          <el-input v-model="returnForm.returnImage" placeholder="请输入图片地址或上传结果" />
        </el-form-item>
        <el-form-item label="归还备注">
          <el-input
            v-model="returnForm.returnRemarks"
            type="textarea"
            :rows="4"
            placeholder="请输入回库备注，如检查情况、人员签字等"
          />
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
import { outboundApi } from '@/api/outbound'
import { useFlowDialogs } from '@/composables/useFlowDialogs'
import { usePageQuery } from '@/composables/usePageQuery'
import { PERMISSIONS } from '@/constants/permissions'
import {
  OUTBOUND_STAGE_LABELS,
  OUTBOUND_STAGE_TYPES,
  OUTBOUND_STATUS_LABELS,
  OUTBOUND_STATUS_TYPES,
  getStatusLabel,
  getStatusType
} from '@/utils/status'

const router = useRouter()
const queryParams = reactive({
  artifactCode: '',
  artifactName: '',
  status: '',
  currentStage: '',
  startDate: '',
  endDate: '',
  current: 1,
  size: 10
})

const approveForm = reactive({
  outboundId: null,
  stage: 'STAGE1',
  approverName: '',
  approverRole: '',
  opinion: '',
  status: 'APPROVED'
})

const returnForm = reactive({
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
  loadHistory: (row) => outboundApi.getFlowHistory(row.id),
  initApprove: (row) => {
    approveForm.outboundId = row.id
    approveForm.stage = row.currentStage || 'STAGE1'
    approveForm.approverName = ''
    approveForm.approverRole = ''
    approveForm.opinion = ''
    approveForm.status = 'APPROVED'
  },
  initReturn: () => {
    returnForm.returnImage = ''
    returnForm.returnRemarks = ''
  }
})

const getOutboundStageLabel = (value) => getStatusLabel(OUTBOUND_STAGE_LABELS, value)
const getOutboundStageType = (value) => getStatusType(OUTBOUND_STAGE_TYPES, value)
const getOutboundStatusLabel = (value) => getStatusLabel(OUTBOUND_STATUS_LABELS, value)
const getOutboundStatusType = (value) => getStatusType(OUTBOUND_STATUS_TYPES, value)

const createInitialQueryParams = () => ({
  artifactCode: '',
  artifactName: '',
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
  fetcher: outboundApi.getPage,
  onError: (error) => {
    ElMessage.error(error?.response?.data?.message || '加载出库数据失败')
  }
})

const handleAdd = () => {
  router.push('/outbound/add')
}

const handleEdit = (row) => {
  router.push(`/outbound/edit?id=${row.id}`)
}

const handleView = (row) => openDetail(row)

const handleApprove = (row) => openApprove(row)

const submitApprove = async () => {
  try {
    await outboundApi.approve(approveForm)
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
    await outboundApi.returnArtifact({
      outboundId: currentRow.value.id,
      returnImage: returnForm.returnImage,
      returnRemarks: returnForm.returnRemarks
    })
    ElMessage.success('回库确认成功')
    closeReturn()
    getList()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '回库确认失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除文物“${row.artifactName}”的出库记录吗？`, '提示', { type: 'warning' })
    await outboundApi.delete(row.id)
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
