<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="参观者姓名">
          <el-input v-model="queryParams.visitorName" placeholder="参观者姓名" clearable />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="queryParams.visitorPhone" placeholder="联系电话" clearable />
        </el-form-item>
        <el-form-item label="预约状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleAdd">新增预约</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="visitorName" label="参观者姓名" width="120" />
        <el-table-column prop="visitorPhone" label="联系电话" width="140" />
        <el-table-column prop="visitorIdCard" label="身份证号" width="180" show-overflow-tooltip />
        <el-table-column prop="exhibitionName" label="预约展览" width="180" show-overflow-tooltip />
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="appointmentTimeSlot" label="预约时段" width="140" />
        <el-table-column prop="visitorCount" label="人数" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAppointmentStatusType(row.status)">
              {{ getAppointmentStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row.id)">编辑</el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="success"
              @click="handleApprove(row.id)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="danger"
              @click="handleReject(row.id)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px;"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" rows="3" placeholder="请输入拒绝原因" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { visitorAppointmentApi } from '@/api/visitorAppointment'
import {
  VISITOR_APPOINTMENT_STATUS_LABELS,
  VISITOR_APPOINTMENT_STATUS_TYPES,
  getStatusLabel,
  getStatusType
} from '@/utils/status'

const router = useRouter()

const queryParams = ref({
  visitorName: '',
  visitorPhone: '',
  exhibitionId: null,
  status: '',
  startDate: null,
  endDate: null,
  current: 1,
  size: 10
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const rejectDialogVisible = ref(false)
const rejectId = ref(null)
const rejectReason = ref('')

const getAppointmentStatusLabel = (status) => getStatusLabel(VISITOR_APPOINTMENT_STATUS_LABELS, status)
const getAppointmentStatusType = (status) => getStatusType(VISITOR_APPOINTMENT_STATUS_TYPES, status)

const getList = async () => {
  loading.value = true
  try {
    const res = await visitorAppointmentApi.getList(queryParams.value)
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch {
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
    visitorName: '',
    visitorPhone: '',
    exhibitionId: null,
    status: '',
    startDate: null,
    endDate: null,
    current: 1,
    size: 10
  }
  getList()
}

const handleAdd = () => {
  router.push('/visitor-appointments/create')
}

const handleEdit = (id) => {
  router.push(`/visitor-appointments/${id}/edit`)
}

const handleApprove = async (id) => {
  await visitorAppointmentApi.approve(id)
  ElMessage.success('审核通过')
  getList()
}

const handleReject = (id) => {
  rejectId.value = id
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  await visitorAppointmentApi.reject(rejectId.value, rejectReason.value)
  ElMessage.success('已拒绝')
  rejectDialogVisible.value = false
  getList()
}

onMounted(() => {
  getList()
})
</script>
