<template>
  <div>
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑修复记录' : '新增修复记录' }}</span>
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

        <el-divider content-position="left">修复申请</el-divider>
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
          <el-col :span="12">
            <el-form-item label="修复类型" prop="restorationType">
              <el-select v-model="form.restorationType" placeholder="请选择" style="width: 100%">
                <el-option label="除尘清理" value="CLEANING" />
                <el-option label="加固" value="REINFORCEMENT" />
                <el-option label="补配" value="RESTORATION" />
                <el-option label="做旧" value="AGING" />
                <el-option label="防护处理" value="PROTECTION" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急程度" prop="restorationPriority">
              <el-select v-model="form.restorationPriority" placeholder="请选择" style="width: 100%">
                <el-option label="一般" value="NORMAL" />
                <el-option label="紧急" value="URGENT" />
                <el-option label="加急" value="CRITICAL" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="损伤情况" prop="damageCondition">
              <el-input v-model="form.damageCondition" type="textarea" :rows="3" placeholder="请描述文物损伤情况" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="申请原因" prop="applyReason">
              <el-input v-model="form.applyReason" type="textarea" :rows="2" placeholder="请输入修复申请原因" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">修复方案</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="方案内容" prop="planContent">
              <el-input v-model="form.planContent" type="textarea" :rows="4" placeholder="请输入修复方案内容" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="使用材料" prop="planMaterials">
              <el-input v-model="form.planMaterials" placeholder="请输入修复材料" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="方案日期" prop="planDate">
              <el-date-picker
                v-model="form.planDate"
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
            <el-form-item label="修复步骤" prop="planSteps">
              <el-input v-model="form.planSteps" type="textarea" :rows="3" placeholder="请输入修复步骤" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="方案审批人" prop="planApprover">
              <el-input v-model="form.planApprover" placeholder="请输入审批人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审批日期" prop="planApproveDate">
              <el-date-picker
                v-model="form.planApproveDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">修复执行</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预计开始日期" prop="estimatedStartDate">
              <el-date-picker
                v-model="form.estimatedStartDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计结束日期" prop="estimatedEndDate">
              <el-date-picker
                v-model="form.estimatedEndDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="实际开始日期" prop="actualStartDate">
              <el-date-picker
                v-model="form.actualStartDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实际结束日期" prop="actualEndDate">
              <el-date-picker
                v-model="form.actualEndDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="修复人" prop="restorer">
              <el-input v-model="form.restorer" placeholder="请输入修复人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="修复资质" prop="restorerQualification">
              <el-input v-model="form.restorerQualification" placeholder="请输入修复资质" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="监督人" prop="supervisor">
              <el-input v-model="form.supervisor" placeholder="请输入监督人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="修复前状态" prop="beforeCondition">
              <el-input v-model="form.beforeCondition" type="textarea" :rows="2" placeholder="修复前状态" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="修复后状态" prop="afterCondition">
              <el-input v-model="form.afterCondition" type="textarea" :rows="2" placeholder="修复后状态" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="修复内容" prop="restorationContent">
              <el-input v-model="form.restorationContent" type="textarea" :rows="3" placeholder="请输入修复内容" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="修复结果" prop="restorationResult">
              <el-input v-model="form.restorationResult" type="textarea" :rows="2" placeholder="请输入修复结果" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">验收信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="验收意见" prop="acceptanceOpinion">
              <el-input v-model="form.acceptanceOpinion" type="textarea" :rows="2" placeholder="请输入验收意见" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="验收结果" prop="acceptanceResult">
              <el-select v-model="form.acceptanceResult" placeholder="请选择" style="width: 100%">
                <el-option label="合格" value="QUALIFIED" />
                <el-option label="不合格" value="UNQUALIFIED" />
                <el-option label="待整改" value="PENDING" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="验收日期" prop="acceptanceDate">
              <el-date-picker
                v-model="form.acceptanceDate"
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
          <el-col :span="12">
            <el-form-item label="经办人" prop="handler">
              <el-input v-model="form.handler" placeholder="请输入经办人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remarks">
              <el-input v-model="form.remarks" type="textarea" :rows="2" placeholder="备注信息" />
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
import { restorationApi } from '@/api/restoration'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  id: null,
  artifactCode: '',
  artifactName: '',
  restorationType: '',
  restorationPriority: 'NORMAL',
  applyDate: '',
  applicant: '',
  applyReason: '',
  damageCondition: '',
  planContent: '',
  planMaterials: '',
  planSteps: '',
  planDate: '',
  planApprover: '',
  planApproveDate: '',
  estimatedStartDate: '',
  estimatedEndDate: '',
  actualStartDate: '',
  actualEndDate: '',
  restorer: '',
  restorerQualification: '',
  supervisor: '',
  beforeCondition: '',
  afterCondition: '',
  restorationContent: '',
  restorationResult: '',
  acceptanceOpinion: '',
  acceptanceResult: '',
  acceptanceDate: '',
  handler: '',
  remarks: ''
})

const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  restorationType: [{ required: true, message: '请选择修复类型', trigger: 'change' }]
}

const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await restorationApi.getDetail(route.query.id)
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
      await restorationApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await restorationApi.create(form)
      ElMessage.success('保存成功')
    }
    router.push('/restoration/list')
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
