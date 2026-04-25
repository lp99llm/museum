<template>
  <div class="process-form-page">
    <section class="process-form-header">
      <div>
        <div class="eyebrow">Outbound Process</div>
        <h1>{{ isEdit ? '编辑展览出库' : '展览出库' }}</h1>
        <p>发起出库申请、设置回库日期，并对展中跟踪和回库核验做好预设。</p>
      </div>
      <div class="process-form-kpis">
        <div class="process-form-kpi"><span>当前阶段</span><strong>出库申请</strong></div>
        <div class="process-form-kpi"><span>审批路径</span><strong>3 节点</strong></div>
        <div class="process-form-kpi"><span>回库校验</span><strong>已启用</strong></div>
      </div>
    </section>

    <div class="process-form-layout">
      <div class="process-form-main">
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-card shadow="never" class="process-form-card">
            <template #header><div class="process-form-section-title"><div><h3>文物与出库信息</h3><p>选择文物、出库事由与预计回库时间</p></div></div></template>
            <div class="process-form-grid">
              <el-form-item label="文物编号" prop="artifactCode"><el-input v-model="form.artifactCode" /></el-form-item>
              <el-form-item label="文物名称" prop="artifactName"><el-input v-model="form.artifactName" /></el-form-item>
              <el-form-item label="出库事由" prop="outboundReason">
                <el-select v-model="form.outboundReason">
                  <el-option label="展览" value="EXHIBITION" />
                  <el-option label="研究" value="RESEARCH" />
                  <el-option label="修复" value="RESTORATION" />
                  <el-option label="借展" value="LOAN" />
                  <el-option label="鉴定" value="APPRAISAL" />
                  <el-option label="其他" value="OTHER" />
                </el-select>
              </el-form-item>
              <el-form-item label="预计回库日期" prop="expectedReturnDate"><el-date-picker v-model="form.expectedReturnDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header><div class="process-form-section-title"><div><h3>目标展览与跟踪</h3><p>展厅信息、联系人和展中状态报告安排</p></div></div></template>
            <div class="process-form-grid">
              <el-form-item label="展览名称" prop="targetExhibition"><el-input v-model="form.targetExhibition" placeholder="请输入展览名称" /></el-form-item>
              <el-form-item label="目标位置" prop="targetLocation"><el-input v-model="form.targetLocation" placeholder="如 展厅A-展柜3" /></el-form-item>
              <el-form-item label="联系人" prop="targetContact"><el-input v-model="form.targetContact" /></el-form-item>
              <el-form-item label="联系电话" prop="targetPhone"><el-input v-model="form.targetPhone" /></el-form-item>
              <el-form-item label="经办人" prop="handler"><el-input v-model="form.handler" /></el-form-item>
            </div>
            <div class="process-inline-note">展中跟踪建议每周提交一次状态报告，回库时需再次核验文物状态。</div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header><div class="process-form-section-title"><div><h3>提交</h3><p>确认后进入出库审批流</p></div></div></template>
            <div class="process-form-actions">
              <el-button type="primary" :loading="submitting" @click="handleSubmit">提交出库申请</el-button>
              <el-button @click="handleBack">返回</el-button>
            </div>
          </el-card>
        </el-form>
      </div>
      <aside class="process-form-side">
        <el-card shadow="never" class="process-form-card">
          <template #header><div class="process-form-section-title"><div><h3>审批提示</h3><p>出库流程需要重点关注回库节点</p></div></div></template>
          <div class="process-form-side-list">
            <div class="process-form-side-item"><h4>回库日期必填</h4><p>系统会基于预计回库日期触发超期未归预警。</p></div>
            <div class="process-form-side-item"><h4>展中跟踪</h4><p>建议同步要求保管人定期记录状态报告，避免状态流转缺失。</p></div>
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
import { outboundApi } from '@/api/outbound'
import { populateForm } from '@/utils/form'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)
const isEdit = computed(() => !!route.query.id)
const form = reactive({
  id: null,
  artifactCode: '',
  artifactName: '',
  outboundDate: '',
  outboundReason: 'EXHIBITION',
  expectedReturnDate: '',
  targetExhibition: '',
  targetLocation: '',
  targetContact: '',
  targetPhone: '',
  handler: ''
})
const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  outboundReason: [{ required: true, message: '请选择出库事由', trigger: 'change' }],
  expectedReturnDate: [{ required: true, message: '请选择预计回库日期', trigger: 'change' }]
}
const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await outboundApi.getDetail(route.query.id)
    populateForm(form, res)
  } catch {
    ElMessage.error('加载出库信息失败')
  }
}
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await outboundApi.update(form)
      ElMessage.success('出库记录已更新')
    } else {
      await outboundApi.create(form)
      ElMessage.success('出库申请已提交')
    }
    router.push('/outbound/list')
  } catch {
    ElMessage.error(isEdit.value ? '更新出库记录失败' : '提交出库申请失败')
  } finally {
    submitting.value = false
  }
}
const handleBack = () => router.back()
onMounted(loadData)
</script>
