<template>
  <div>
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑出馆处置' : '新增出馆处置' }}</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="140px">
        <el-divider content-position="left">文物信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="文物编号" prop="artifactCode">
              <el-input v-model="form.artifactCode" placeholder="请输入文物编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文物名称" prop="artifactName">
              <el-input v-model="form.artifactName" placeholder="请输入文物名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">申请信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicant">
              <el-input v-model="form.applicant" placeholder="请输入申请人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请日期" prop="applyDate">
              <el-date-picker
                v-model="form.applyDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="申请原因" prop="applyReason">
              <el-input v-model="form.applyReason" type="textarea" :rows="2" placeholder="请输入申请原因" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">处置信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="处置类型" prop="disposalType">
              <el-select v-model="form.disposalType" placeholder="请选择" style="width: 100%">
                <el-option label="移交" value="TRANSFER" />
                <el-option label="销毁" value="DESTROY" />
                <el-option label="退藏" value="REMOVE" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">评估信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="评估报告" prop="evaluationReport">
              <el-input v-model="form.evaluationReport" type="textarea" :rows="3" placeholder="请输入评估报告内容" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="专家意见" prop="expertOpinion">
              <el-input v-model="form.expertOpinion" type="textarea" :rows="3" placeholder="请输入专家意见" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">公示信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公示开始日期" prop="publicStartTime">
              <el-date-picker
                v-model="form.publicStartTime"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公示结束日期" prop="publicEndTime">
              <el-date-picker
                v-model="form.publicEndTime"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="公示结果" prop="publicResult">
              <el-input v-model="form.publicResult" type="textarea" :rows="2" placeholder="请输入公示结果" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">审批信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="审批人" prop="approver">
              <el-input v-model="form.approver" placeholder="请输入审批人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审批日期" prop="approvalDate">
              <el-date-picker
                v-model="form.approvalDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="审批意见" prop="approvalOpinion">
              <el-input v-model="form.approvalOpinion" type="textarea" :rows="3" placeholder="请输入审批意见" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">备案信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="备案状态" prop="recordStatus">
              <el-select v-model="form.recordStatus" placeholder="请选择" style="width: 100%">
                <el-option label="已备案" value="RECORD" />
                <el-option label="未备案" value="NOT_RECORD" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备案日期" prop="recordDate">
              <el-date-picker
                v-model="form.recordDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备案机构" prop="recordOrganization">
              <el-input v-model="form.recordOrganization" placeholder="请输入备案机构" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">执行信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="处置日期" prop="disposalDate">
              <el-date-picker
                v-model="form.disposalDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行人" prop="operator">
              <el-input v-model="form.operator" placeholder="请输入执行人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="执行结果" prop="executionResult">
              <el-input v-model="form.executionResult" type="textarea" :rows="2" placeholder="请输入执行结果" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">归档信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="档案编号" prop="archiveNo">
              <el-input v-model="form.archiveNo" placeholder="请输入档案编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归档日期" prop="archiveDate">
              <el-date-picker
                v-model="form.archiveDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">备注信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remarks">
              <el-input v-model="form.remarks" type="textarea" :rows="2" placeholder="请输入备注信息" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
          <el-button @click="handleBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { disposalApi } from '@/api/disposal'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  id: null,
  artifactCode: '',
  artifactName: '',
  applicant: '',
  applyDate: '',
  applyReason: '',
  disposalType: '',
  evaluationReport: '',
  expertOpinion: '',
  publicStartTime: '',
  publicEndTime: '',
  publicResult: '',
  approvalOpinion: '',
  approver: '',
  approvalDate: '',
  recordStatus: '',
  recordDate: '',
  recordOrganization: '',
  disposalDate: '',
  operator: '',
  executionResult: '',
  archiveNo: '',
  archiveDate: '',
  remarks: ''
})

const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  applicant: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  applyReason: [{ required: true, message: '请输入申请原因', trigger: 'blur' }],
  disposalType: [{ required: true, message: '请选择处置类型', trigger: 'change' }]
}

const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await disposalApi.getDetail(route.query.id)
    if (res.code === 200 && res.data) {
      Object.assign(form, res.data)
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await disposalApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await disposalApi.create(form)
      ElMessage.success('保存成功')
    }
    router.push('/disposal/list')
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '保存失败')
  } finally {
    submitting.value = false
  }
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  loadData()
})
</script>
