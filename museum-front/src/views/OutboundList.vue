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
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="待审批" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
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
          <el-button type="primary" @click="handleAdd">新增出库</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="artifactCode" label="文物编号" />
        <el-table-column prop="artifactName" label="文物名称" />
        <el-table-column prop="outboundDate" label="出库日期" width="120" />
        <el-table-column prop="targetExhibition" label="目标展览" />
        <el-table-column prop="targetLocation" label="目标位置" width="150" />
        <el-table-column prop="currentStage" label="审批阶段" width="120">
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
        <el-table-column prop="expectedReturnDate" label="预计归还" width="120" />
        <el-table-column prop="actualReturnDate" label="实际归还" width="120" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(row)" v-if="row.status === 'PENDING'">编辑</el-button>
            <el-button link type="success" @click="handleApprove(row)" v-if="row.status === 'PENDING'">审批</el-button>
            <el-button link type="warning" @click="handleReturn(row)" v-if="row.status === 'APPROVED' || row.status === 'OUTBOUND'">回库确认</el-button>
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
          <el-tag :type="getStageTagType(currentRow?.currentStage)">
            {{ getStageLabel(currentRow?.currentStage) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentRow?.status)">
            {{ getStatusLabel(currentRow?.status) }}
          </el-tag>
        </el-descriptions-item>
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

    <el-dialog v-model="returnVisible" title="回库确认" width="500px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="归还图片">
          <el-input v-model="returnForm.returnImage" placeholder="请输入图片地址或上传" />
        </el-form-item>
        <el-form-item label="归还备注">
          <el-input v-model="returnForm.returnRemarks" type="textarea" :rows="4" placeholder="请输入归还备注，如检查情况、人员签字等" />
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
import { outboundApi } from '@/api/outbound'

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
    const res = await outboundApi.getPage(queryParams)
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
  router.push('/outbound/add')
}

const handleEdit = (row) => {
  router.push(`/outbound/edit?id=${row.id}`)
}

const handleView = async (row) => {
  currentRow.value = row
  try {
    const res = await outboundApi.getFlowHistory(row.id)
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
  approveForm.outboundId = row.id
  approveForm.stage = row.currentStage
  approveForm.approverName = ''
  approveForm.approverRole = ''
  approveForm.opinion = ''
  approveForm.status = 'APPROVED'
  approveVisible.value = true
}

const submitApprove = async () => {
  try {
    await outboundApi.approve(approveForm)
    ElMessage.success('审批提交成功')
    approveVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error('审批提交失败')
  }
}

const handleReturn = (row) => {
  currentRow.value = row
  returnForm.returnImage = ''
  returnForm.returnRemarks = ''
  returnVisible.value = true
}

const submitReturn = async () => {
  try {
    await outboundApi.returnArtifact({
      outboundId: currentRow.value.id,
      returnImage: returnForm.returnImage,
      returnRemarks: returnForm.returnRemarks
    })
    ElMessage.success('回库确认成功')
    returnVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error('回库确认失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该出库记录吗？', '提示', { type: 'warning' })
    await outboundApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStageLabel = (stage) => {
  const map = { STAGE1: '部门审批', STAGE2: '分管审批', STAGE3: '馆长审批', APPROVED: '已通过', RETURNED: '已归还' }
  return map[stage] || stage
}

const getStageTagType = (stage) => {
  const map = { STAGE1: '', STAGE2: 'warning', STAGE3: 'danger', APPROVED: 'success', RETURNED: 'info' }
  return map[stage] || ''
}

const getStatusLabel = (status) => {
  const map = { PENDING: '待审批', APPROVED: '已通过', REJECTED: '已拒绝', OUTBOUND: '已出库', RETURNED: '已归还' }
  return map[status] || status
}

const getStatusTagType = (status) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', OUTBOUND: '', RETURNED: 'info' }
  return map[status] || ''
}

onMounted(() => {
  getList()
})
</script>
