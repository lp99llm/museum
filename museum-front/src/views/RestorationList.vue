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
        <el-form-item label="修复类型">
          <el-select v-model="queryParams.restorationType" placeholder="请选择" clearable>
            <el-option label="除尘清理" value="CLEANING" />
            <el-option label="加固" value="REINFORCEMENT" />
            <el-option label="补配" value="RESTORATION" />
            <el-option label="做旧" value="AGING" />
            <el-option label="防护处理" value="PROTECTION" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前阶段">
          <el-select v-model="queryParams.currentStage" placeholder="请选择" clearable>
            <el-option label="申请中" value="APPLY" />
            <el-option label="方案编制" value="PLAN" />
            <el-option label="修复执行" value="EXECUTE" />
            <el-option label="验收中" value="ACCEPTANCE" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="待审批" value="PENDING" />
            <el-option label="已完成" value="COMPLETED" />
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
          <el-button v-permission="PERMISSIONS.RESTORATION_ADD" type="primary" @click="handleAdd">新增修复</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="restorationType" label="修复类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getRestorationTypeLabel(row.restorationType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicant" label="申请人" width="100" />
        <el-table-column prop="applyDate" label="申请日期" width="120" />
        <el-table-column prop="currentStage" label="当前阶段" width="120">
          <template #default="{ row }">
            <el-tag :type="getRestorationStageType(row.currentStage)">
              {{ getRestorationStageLabel(row.currentStage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getRestorationStatusType(row.status)">
              {{ getRestorationStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="restorer" label="修复人" width="100" />
        <el-table-column prop="acceptanceDate" label="验收日期" width="120" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'PENDING'" v-permission="PERMISSIONS.RESTORATION_EDIT" link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 'PENDING'" v-permission="PERMISSIONS.RESTORATION_APPROVE" link type="success" @click="handleApprove(row)">审批</el-button>
            <el-button v-if="row.status === 'PENDING'" v-permission="PERMISSIONS.RESTORATION_DELETE" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="detailVisible" title="修复详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="文物编号">{{ currentRow?.artifactCode }}</el-descriptions-item>
        <el-descriptions-item label="文物名称">{{ currentRow?.artifactName }}</el-descriptions-item>
        <el-descriptions-item label="修复类型">{{ getRestorationTypeLabel(currentRow?.restorationType) }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow?.applicant }}</el-descriptions-item>
        <el-descriptions-item label="申请日期">{{ currentRow?.applyDate }}</el-descriptions-item>
        <el-descriptions-item label="修复人">{{ currentRow?.restorer }}</el-descriptions-item>
        <el-descriptions-item label="当前阶段">
          <el-tag :type="getRestorationStageType(currentRow?.currentStage)">
            {{ getRestorationStageLabel(currentRow?.currentStage) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getRestorationStatusType(currentRow?.status)">
            {{ getRestorationStatusLabel(currentRow?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="损伤情况" :span="2">{{ currentRow?.damageCondition || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="修复方案" :span="2">{{ currentRow?.planContent || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="修复材料" :span="2">{{ currentRow?.planMaterials || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="修复步骤" :span="2">{{ currentRow?.planSteps || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="修复前状态" :span="2">{{ currentRow?.beforeCondition || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="修复后状态" :span="2">{{ currentRow?.afterCondition || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="修复结果" :span="2">{{ currentRow?.restorationResult || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="验收意见" :span="2">{{ currentRow?.acceptanceOpinion || '暂无' }}</el-descriptions-item>
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
            <el-option label="方案审批" value="PLAN" />
            <el-option label="执行审批" value="EXECUTE" />
            <el-option label="验收审批" value="ACCEPTANCE" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批人">
          <el-input v-model="approveForm.approverName" placeholder="请输入审批人姓名" />
        </el-form-item>
        <el-form-item label="审批角色">
          <el-input v-model="approveForm.approverRole" placeholder="如：技术负责人" />
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
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { restorationApi } from '@/api/restoration'
import { useFlowDialogs } from '@/composables/useFlowDialogs'
import { usePageQuery } from '@/composables/usePageQuery'
import { PERMISSIONS } from '@/constants/permissions'
import {
  RESTORATION_STAGE_LABELS,
  RESTORATION_STAGE_TYPES,
  RESTORATION_STATUS_LABELS,
  RESTORATION_STATUS_TYPES,
  RESTORATION_TYPE_LABELS,
  getStatusLabel,
  getStatusType
} from '@/utils/status'

const router = useRouter()
const queryParams = reactive({
  artifactCode: '',
  artifactName: '',
  status: '',
  currentStage: '',
  restorationType: '',
  startDate: '',
  endDate: '',
  current: 1,
  size: 10
})

const approveForm = reactive({
  restorationId: null,
  stage: 'APPLY',
  approverName: '',
  approverRole: '',
  opinion: '',
  status: 'APPROVED'
})

const {
  currentRow,
  flowHistory,
  detailVisible,
  approveVisible,
  openDetail,
  openApprove,
  closeApprove
} = useFlowDialogs({
  loadHistory: (row) => restorationApi.getFlowHistory(row.id),
  initApprove: (row) => {
    approveForm.restorationId = row.id
    approveForm.stage = row.currentStage || 'APPLY'
    approveForm.approverName = ''
    approveForm.approverRole = ''
    approveForm.opinion = ''
    approveForm.status = 'APPROVED'
  }
})

const getRestorationTypeLabel = (value) => getStatusLabel(RESTORATION_TYPE_LABELS, value)
const getRestorationStageLabel = (value) => getStatusLabel(RESTORATION_STAGE_LABELS, value)
const getRestorationStageType = (value) => getStatusType(RESTORATION_STAGE_TYPES, value)
const getRestorationStatusLabel = (value) => getStatusLabel(RESTORATION_STATUS_LABELS, value)
const getRestorationStatusType = (value) => getStatusType(RESTORATION_STATUS_TYPES, value)

const createInitialQueryParams = () => ({
  artifactCode: '',
  artifactName: '',
  status: '',
  currentStage: '',
  restorationType: '',
  startDate: '',
  endDate: '',
  current: 1,
  size: 10
})

const { loading, tableData, total, dateRange, loadPage: getList, handleQuery, resetQuery: resetPageQuery } = usePageQuery({
  queryParams,
  createInitialQueryParams,
  fetcher: restorationApi.getPage,
  onError: (error) => {
    ElMessage.error(error?.response?.data?.message || '加载修复数据失败')
  }
})

const resetQuery = () => resetPageQuery()

const handleAdd = () => {
  router.push('/restoration/add')
}

const handleEdit = (row) => {
  router.push(`/restoration/edit?id=${row.id}`)
}

const handleView = (row) => openDetail(row)

const handleApprove = (row) => openApprove(row)

const submitApprove = async () => {
  try {
    await restorationApi.approve(approveForm)
    ElMessage.success('审批提交成功')
    closeApprove()
    getList()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '审批提交失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除文物“${row.artifactName}”的修复记录吗？`, '提示', { type: 'warning' })
    await restorationApi.delete(row.id)
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
