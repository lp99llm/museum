<template>
  <div class="process-form-page">
    <section class="process-form-header">
      <div>
        <div class="eyebrow">Loan Process</div>
        <h1>{{ isEdit ? '编辑外借管理' : '外借管理' }}</h1>
        <p>记录借入方信息、借期、外借协议和归还验收，形成完整在借跟踪链路。</p>
      </div>
      <div class="process-form-kpis">
        <div class="process-form-kpi"><span>当前阶段</span><strong>外借申请</strong></div>
        <div class="process-form-kpi"><span>归还提醒</span><strong>已启用</strong></div>
        <div class="process-form-kpi"><span>协议状态</span><strong>待上传</strong></div>
      </div>
    </section>

    <div class="process-form-layout">
      <div class="process-form-main">
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-card shadow="never" class="process-form-card">
            <template #header><div class="process-form-section-title"><div><h3>申请与借入方</h3><p>填写借入方信息和借期</p></div></div></template>
            <div class="process-form-grid">
              <el-form-item label="文物编号" prop="artifactCode"><el-input v-model="form.artifactCode" /></el-form-item>
              <el-form-item label="文物名称" prop="artifactName"><el-input v-model="form.artifactName" /></el-form-item>
              <el-form-item label="申请人" prop="applicant"><el-input v-model="form.applicant" /></el-form-item>
              <el-form-item label="申请日期" prop="applyDate"><el-date-picker v-model="form.applyDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="借入单位" prop="borrowerInstitution"><el-input v-model="form.borrowerInstitution" /></el-form-item>
              <el-form-item label="联系人" prop="borrowerContact"><el-input v-model="form.borrowerContact" /></el-form-item>
              <el-form-item label="联系电话" prop="borrowerPhone"><el-input v-model="form.borrowerPhone" /></el-form-item>
              <el-form-item label="联系地址" prop="borrowerAddress"><el-input v-model="form.borrowerAddress" /></el-form-item>
              <el-form-item label="外借日期" prop="loanDate"><el-date-picker v-model="form.loanDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="预计归还日期" prop="expectedReturnDate"><el-date-picker v-model="form.expectedReturnDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header><div class="process-form-section-title"><div><h3>协议与在借跟踪</h3><p>协议、运输方式和状态核验信息</p></div></div></template>
            <div class="process-form-grid">
              <el-form-item label="借用用途" prop="loanPurpose">
                <el-select v-model="form.loanPurpose">
                  <el-option label="展览" value="EXHIBITION" />
                  <el-option label="研究" value="RESEARCH" />
                  <el-option label="教育" value="EDUCATION" />
                  <el-option label="其他" value="OTHER" />
                </el-select>
              </el-form-item>
              <el-form-item label="运输方式" prop="transportMethod">
                <el-select v-model="form.transportMethod">
                  <el-option label="专车运输" value="SPECIAL_CAR" />
                  <el-option label="快递" value="EXPRESS" />
                  <el-option label="自取" value="SELF_PICKUP" />
                  <el-option label="其他" value="OTHER" />
                </el-select>
              </el-form-item>
              <el-form-item label="安全措施" prop="securityMeasures"><el-input v-model="form.securityMeasures" /></el-form-item>
              <el-form-item label="借用地点" prop="loanLocation"><el-input v-model="form.loanLocation" /></el-form-item>
              <el-form-item label="外借协议" prop="loanAgreement" class="process-form-span-2"><el-input v-model="form.loanAgreement" type="textarea" :rows="3" /></el-form-item>
              <el-form-item label="借出前状态" prop="beforeCondition"><el-input v-model="form.beforeCondition" type="textarea" :rows="2" /></el-form-item>
              <el-form-item label="归还后状态" prop="afterCondition"><el-input v-model="form.afterCondition" type="textarea" :rows="2" /></el-form-item>
            </div>
            <div class="process-form-actions">
              <el-button type="primary" :loading="submitting" @click="handleSubmit">保存外借记录</el-button>
              <el-button @click="handleBack">返回</el-button>
            </div>
          </el-card>
        </el-form>
      </div>
      <aside class="process-form-side">
        <el-card shadow="never" class="process-form-card">
          <template #header><div class="process-form-section-title"><div><h3>注意事项</h3><p>关注归还节点和状态核验</p></div></div></template>
          <div class="process-form-side-list">
            <div class="process-form-side-item"><h4>归还验收</h4><p>归还时建议重新核验状态并补充影像资料，形成闭环。</p></div>
            <div class="process-form-side-item"><h4>超期提醒</h4><p>系统可基于预计归还日期触发自动预警。</p></div>
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
import { loanApi } from '@/api/loan'
import { populateForm } from '@/utils/form'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)
const isEdit = computed(() => !!route.query.id)
const form = reactive({
  id: null, artifactCode: '', artifactName: '', applicant: '', applyDate: '', applyReason: '',
  borrowerInstitution: '', borrowerContact: '', borrowerPhone: '', borrowerAddress: '',
  loanDate: '', expectedReturnDate: '', actualReturnDate: '', loanPurpose: '', transportMethod: '',
  securityMeasures: '', loanLocation: '', loanSupervisor: '', loanAgreement: '', beforeCondition: '',
  afterCondition: '', returnImage: '', returnRemarks: '', approvalOpinion: '', handler: ''
})
const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  borrowerInstitution: [{ required: true, message: '请输入借入单位', trigger: 'blur' }],
  borrowerContact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  expectedReturnDate: [{ required: true, message: '请选择预计归还日期', trigger: 'change' }]
}
const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await loanApi.getDetail(route.query.id)
    populateForm(form, res)
  } catch {
    ElMessage.error('加载外借信息失败')
  }
}
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await loanApi.update(form)
      ElMessage.success('外借记录已更新')
    } else {
      await loanApi.create(form)
      ElMessage.success('外借记录已创建')
    }
    router.push('/loan/list')
  } catch {
    ElMessage.error(isEdit.value ? '更新外借记录失败' : '创建外借记录失败')
  } finally {
    submitting.value = false
  }
}
const handleBack = () => router.back()
onMounted(loadData)
</script>
