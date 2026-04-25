<template>
  <div class="process-form-page">
    <section class="process-form-header">
      <div>
        <div class="eyebrow">Restoration Process</div>
        <h1>{{ isEdit ? '编辑修复管理' : '修复管理' }}</h1>
        <p>覆盖修复申请、方案审批、过程记录和验收结果，形成完整修复档案。</p>
      </div>
      <div class="process-form-kpis">
        <div class="process-form-kpi"><span>当前阶段</span><strong>修复申请</strong></div>
        <div class="process-form-kpi"><span>修复优先级</span><strong>{{ priorityText }}</strong></div>
        <div class="process-form-kpi"><span>对比影像</span><strong>{{ photoFiles.length }} 份</strong></div>
      </div>
    </section>

    <div class="process-form-layout">
      <div class="process-form-main">
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-card shadow="never" class="process-form-card">
            <template #header><div class="process-form-section-title"><div><h3>修复申请</h3><p>描述损伤情况、申请人和优先级</p></div></div></template>
            <div class="process-form-grid">
              <el-form-item label="文物编号" prop="artifactCode"><el-input v-model="form.artifactCode" /></el-form-item>
              <el-form-item label="文物名称" prop="artifactName"><el-input v-model="form.artifactName" /></el-form-item>
              <el-form-item label="申请人" prop="applicant"><el-input v-model="form.applicant" /></el-form-item>
              <el-form-item label="申请日期" prop="applyDate"><el-date-picker v-model="form.applyDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="修复类型" prop="restorationType">
                <el-select v-model="form.restorationType">
                  <el-option label="除尘清理" value="CLEANING" />
                  <el-option label="加固" value="REINFORCEMENT" />
                  <el-option label="补配" value="RESTORATION" />
                  <el-option label="做旧处理" value="AGING" />
                  <el-option label="防护处理" value="PROTECTION" />
                </el-select>
              </el-form-item>
              <el-form-item label="紧急程度" prop="restorationPriority">
                <el-select v-model="form.restorationPriority">
                  <el-option label="一般" value="NORMAL" />
                  <el-option label="紧急" value="URGENT" />
                  <el-option label="加急" value="CRITICAL" />
                </el-select>
              </el-form-item>
              <el-form-item label="损伤情况" prop="damageCondition" class="process-form-span-2"><el-input v-model="form.damageCondition" type="textarea" :rows="3" /></el-form-item>
              <el-form-item label="申请原因" prop="applyReason" class="process-form-span-2"><el-input v-model="form.applyReason" type="textarea" :rows="3" /></el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header><div class="process-form-section-title"><div><h3>修复方案与验收</h3><p>审批方案、修复执行和结果验收</p></div></div></template>
            <div class="process-form-grid">
              <el-form-item label="方案内容" prop="planContent" class="process-form-span-2"><el-input v-model="form.planContent" type="textarea" :rows="3" /></el-form-item>
              <el-form-item label="使用材料" prop="planMaterials"><el-input v-model="form.planMaterials" /></el-form-item>
              <el-form-item label="方案日期" prop="planDate"><el-date-picker v-model="form.planDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="方案审批人" prop="planApprover"><el-input v-model="form.planApprover" /></el-form-item>
              <el-form-item label="审批日期" prop="planApproveDate"><el-date-picker v-model="form.planApproveDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="修复前状态" prop="beforeCondition"><el-input v-model="form.beforeCondition" type="textarea" :rows="2" /></el-form-item>
              <el-form-item label="修复后状态" prop="afterCondition"><el-input v-model="form.afterCondition" type="textarea" :rows="2" /></el-form-item>
              <el-form-item label="修复内容" prop="restorationContent" class="process-form-span-2"><el-input v-model="form.restorationContent" type="textarea" :rows="3" /></el-form-item>
              <el-form-item label="验收结果" prop="acceptanceResult">
                <el-select v-model="form.acceptanceResult">
                  <el-option label="合格" value="QUALIFIED" />
                  <el-option label="不合格" value="UNQUALIFIED" />
                  <el-option label="待整改" value="PENDING" />
                </el-select>
              </el-form-item>
              <el-form-item label="验收日期" prop="acceptanceDate"><el-date-picker v-model="form.acceptanceDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header><div class="process-form-section-title"><div><h3>影像与提交</h3><p>上传修复前后对比图和过程资料</p></div></div></template>
            <el-upload action="#" :auto-upload="false" :show-file-list="false" :on-change="handleUploadChange">
              <el-button type="primary" plain>添加对比影像</el-button>
            </el-upload>
            <div v-if="photoFiles.length" class="process-upload-list">
              <div v-for="file in photoFiles" :key="file.uid" class="process-upload-item">
                <span>{{ file.name }}</span>
                <el-button text type="danger" @click="removeFile(file.uid)">移除</el-button>
              </div>
            </div>
            <div v-else class="process-empty-note">暂无上传修复前后对比图。</div>
            <div class="process-form-actions" style="margin-top:18px;">
              <el-button type="primary" :loading="submitting" @click="handleSubmit">保存修复记录</el-button>
              <el-button @click="handleBack">返回</el-button>
            </div>
          </el-card>
        </el-form>
      </div>
      <aside class="process-form-side">
        <el-card shadow="never" class="process-form-card">
          <template #header><div class="process-form-section-title"><div><h3>提醒</h3><p>修复流程建议保留完整对比证据</p></div></div></template>
          <div class="process-form-side-list">
            <div class="process-form-side-item"><h4>影像留档</h4><p>建议至少上传修复前、修复中、修复后的三类影像记录。</p></div>
            <div class="process-form-side-item"><h4>审批先行</h4><p>高风险修复项目应在方案审批通过后再执行实际修复。</p></div>
          </div>
        </el-card>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { restorationApi } from '@/api/restoration'
import { populateForm } from '@/utils/form'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)
const photoFiles = ref([])
const isEdit = computed(() => !!route.query.id)
const form = reactive({
  id: null, artifactCode: '', artifactName: '', restorationType: '', restorationPriority: 'NORMAL',
  applyDate: '', applicant: '', applyReason: '', damageCondition: '', planContent: '', planMaterials: '',
  planSteps: '', planDate: '', planApprover: '', planApproveDate: '', estimatedStartDate: '', estimatedEndDate: '',
  actualStartDate: '', actualEndDate: '', restorer: '', restorerQualification: '', supervisor: '',
  beforeCondition: '', afterCondition: '', restorationContent: '', restorationResult: '', acceptanceOpinion: '',
  acceptanceResult: '', acceptanceDate: '', handler: '', remarks: ''
})
const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  restorationType: [{ required: true, message: '请选择修复类型', trigger: 'change' }]
}
const priorityText = computed(() => ({ NORMAL: '一般', URGENT: '紧急', CRITICAL: '加急' }[form.restorationPriority] || '一般'))
const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await restorationApi.getDetail(route.query.id)
    populateForm(form, res)
  } catch {
    ElMessage.error('加载修复信息失败')
  }
}
const handleUploadChange = (uploadFile) => {
  photoFiles.value = [...photoFiles.value, { uid: uploadFile.uid, name: uploadFile.name }]
}
const removeFile = (uid) => {
  photoFiles.value = photoFiles.value.filter((file) => file.uid !== uid)
}
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await restorationApi.update(form)
      ElMessage.success('修复记录已更新')
    } else {
      await restorationApi.create(form)
      ElMessage.success('修复记录已创建')
    }
    router.push('/restoration/list')
  } catch {
    ElMessage.error(isEdit.value ? '更新修复记录失败' : '创建修复记录失败')
  } finally {
    submitting.value = false
  }
}
const handleBack = () => router.back()
onMounted(loadData)
</script>
