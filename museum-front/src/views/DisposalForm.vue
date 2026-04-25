<template>
  <div class="process-form-page">
    <section class="process-form-header">
      <div>
        <div class="eyebrow">Disposal Process</div>
        <h1>{{ isEdit ? '编辑出馆处置' : '出馆处置' }}</h1>
        <p>管理处置申请、审批、公示和归档，确保出馆处置过程合规透明。</p>
      </div>
      <div class="process-form-kpis">
        <div class="process-form-kpi"><span>当前阶段</span><strong>处置申请</strong></div>
        <div class="process-form-kpi"><span>公示状态</span><strong>待启动</strong></div>
        <div class="process-form-kpi"><span>归档要求</span><strong>必须完成</strong></div>
      </div>
    </section>

    <div class="process-form-layout">
      <div class="process-form-main">
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>申请与处置方式</h3>
                  <p>填写原因、方式与专家意见</p>
                </div>
              </div>
            </template>
            <div class="process-form-grid">
              <el-form-item label="文物编号" prop="artifactCode"><el-input v-model="form.artifactCode" /></el-form-item>
              <el-form-item label="文物名称" prop="artifactName"><el-input v-model="form.artifactName" /></el-form-item>
              <el-form-item label="申请人" prop="applicant"><el-input v-model="form.applicant" /></el-form-item>
              <el-form-item label="申请日期" prop="applyDate"><el-date-picker v-model="form.applyDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="处置类型" prop="disposalType">
                <el-select v-model="form.disposalType">
                  <el-option label="移交" value="TRANSFER" />
                  <el-option label="销毁" value="DESTROY" />
                  <el-option label="退藏" value="REMOVE" />
                  <el-option label="其他" value="OTHER" />
                </el-select>
              </el-form-item>
              <el-form-item label="申请原因" prop="applyReason" class="process-form-span-2"><el-input v-model="form.applyReason" type="textarea" :rows="3" /></el-form-item>
              <el-form-item label="评估报告" prop="evaluationReport" class="process-form-span-2"><el-input v-model="form.evaluationReport" type="textarea" :rows="3" /></el-form-item>
              <el-form-item label="专家意见" prop="expertOpinion" class="process-form-span-2"><el-input v-model="form.expertOpinion" type="textarea" :rows="3" /></el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>审批、公示与归档</h3>
                  <p>流程闭环信息统一登记</p>
                </div>
              </div>
            </template>
            <div class="process-form-grid">
              <el-form-item label="审批人" prop="approver"><el-input v-model="form.approver" /></el-form-item>
              <el-form-item label="审批日期" prop="approvalDate"><el-date-picker v-model="form.approvalDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="审批意见" prop="approvalOpinion" class="process-form-span-2"><el-input v-model="form.approvalOpinion" type="textarea" :rows="2" /></el-form-item>
              <el-form-item label="公示开始" prop="publicStartTime"><el-date-picker v-model="form.publicStartTime" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="公示结束" prop="publicEndTime"><el-date-picker v-model="form.publicEndTime" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="公示结果" prop="publicResult" class="process-form-span-2"><el-input v-model="form.publicResult" type="textarea" :rows="2" /></el-form-item>
              <el-form-item label="归档状态" prop="recordStatus">
                <el-select v-model="form.recordStatus">
                  <el-option label="已备案" value="RECORD" />
                  <el-option label="未备案" value="NOT_RECORD" />
                </el-select>
              </el-form-item>
              <el-form-item label="归档日期" prop="recordDate"><el-date-picker v-model="form.recordDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
            </div>
            <div class="process-form-actions">
              <el-button type="primary" :loading="submitting" @click="handleSubmit">保存处置记录</el-button>
              <el-button @click="handleBack">返回</el-button>
            </div>
          </el-card>
        </el-form>
      </div>
      <aside class="process-form-side">
        <el-card shadow="never" class="process-form-card">
          <template #header>
            <div class="process-form-section-title">
              <div>
                <h3>合规提示</h3>
                <p>处置流程必须保留完整审批链</p>
              </div>
            </div>
          </template>
          <div class="process-form-side-list">
            <div class="process-form-side-item"><h4>审批与公示</h4><p>涉及处置的文物应保留审批意见与公示结果，确保透明可查。</p></div>
            <div class="process-form-side-item"><h4>最终归档</h4><p>处置完成后必须同步归档信息，确保状态闭环。</p></div>
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
import { disposalApi } from '@/api/disposal'
import { populateForm } from '@/utils/form'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)
const isEdit = computed(() => !!route.query.id)
const form = reactive({
  id: null, artifactCode: '', artifactName: '', applicant: '', applyDate: '', applyReason: '', disposalType: '',
  evaluationReport: '', expertOpinion: '', publicStartTime: '', publicEndTime: '', publicResult: '',
  approvalOpinion: '', approver: '', approvalDate: '', recordStatus: '', recordDate: '', recordOrganization: '',
  disposalDate: '', operator: '', executionResult: '', archiveNo: '', archiveDate: '', remarks: ''
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
    populateForm(form, res)
  } catch {
    ElMessage.error('加载处置信息失败')
  }
}
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await disposalApi.update(form)
      ElMessage.success('处置记录已更新')
    } else {
      await disposalApi.create(form)
      ElMessage.success('处置记录已创建')
    }
    router.push('/disposal/list')
  } catch {
    ElMessage.error(isEdit.value ? '更新处置记录失败' : '创建处置记录失败')
  } finally {
    submitting.value = false
  }
}
const handleBack = () => router.back()
onMounted(loadData)
</script>
