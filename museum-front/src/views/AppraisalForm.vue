<template>
  <div class="process-form-page">
    <section class="process-form-header">
      <div>
        <div class="eyebrow">Appraisal Process</div>
        <h1>{{ isEdit ? '编辑鉴定评估' : '鉴定评估' }}</h1>
        <p>录入专家意见、估值结果和附件信息，保持鉴定等级与数据库字段一致。</p>
      </div>
      <div class="process-form-kpis">
        <div class="process-form-kpi">
          <span>当前阶段</span>
          <strong>专家会签</strong>
        </div>
        <div class="process-form-kpi">
          <span>参与专家</span>
          <strong>{{ expertSigners.length }} 位</strong>
        </div>
        <div class="process-form-kpi">
          <span>报告状态</span>
          <strong>待生成</strong>
        </div>
      </div>
    </section>

    <div class="process-form-layout">
      <div class="process-form-main">
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>基本信息</h3>
                  <p>确认文物编号、文物名称和鉴定等级。</p>
                </div>
              </div>
            </template>
            <div class="process-form-grid">
              <el-form-item label="文物编号" prop="artifactCode">
                <el-input v-model="form.artifactCode" placeholder="请输入文物编号" />
              </el-form-item>
              <el-form-item label="文物名称" prop="artifactName">
                <el-input v-model="form.artifactName" placeholder="请输入文物名称" />
              </el-form-item>
              <el-form-item label="鉴定日期" prop="appraisalDate">
                <el-date-picker v-model="form.appraisalDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
              </el-form-item>
              <el-form-item label="鉴定等级" prop="appraisalLevel">
                <el-select v-model="form.appraisalLevel" placeholder="请选择鉴定等级">
                  <el-option v-for="item in appraisalLevelOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
              <el-form-item label="专家姓名" prop="expertName">
                <el-input v-model="form.expertName" placeholder="请输入主审专家姓名" />
              </el-form-item>
              <el-form-item label="专家职称" prop="expertTitle">
                <el-input v-model="form.expertTitle" placeholder="如：研究馆员 / 修复专家" />
              </el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>价值评估</h3>
                  <p>记录鉴定费用、估价金额和综合意见。</p>
                </div>
              </div>
            </template>
            <div class="process-form-grid">
              <el-form-item label="鉴定费用" prop="appraisalFee">
                <el-input-number v-model="form.appraisalFee" :min="0" :precision="2" :step="100" style="width: 100%" />
              </el-form-item>
              <el-form-item label="估价金额" prop="estimatedValue">
                <el-input-number v-model="form.estimatedValue" :min="0" :precision="2" :step="1000" style="width: 100%" />
              </el-form-item>
              <el-form-item label="鉴定结果" prop="appraisalResult" class="process-form-span-2">
                <el-input v-model="form.appraisalResult" type="textarea" :rows="3" placeholder="请输入鉴定结果" />
              </el-form-item>
              <el-form-item label="专家意见" prop="expertOpinion" class="process-form-span-2">
                <el-input v-model="form.expertOpinion" type="textarea" :rows="4" placeholder="请输入专家意见摘要" />
              </el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>多专家会签</h3>
                  <p>用于展示当前鉴定流程中的会签状态。</p>
                </div>
              </div>
            </template>
            <div class="process-chip-row">
              <el-tag v-for="expert in expertSigners" :key="expert.name" round effect="plain">{{ expert.name }} | {{ expert.status }}</el-tag>
            </div>
            <div class="process-inline-note">当前页面提交字段保持与后端接口一致，会签列表仅作为辅助展示。</div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>附件与提交</h3>
                  <p>支持报告草稿、影像文件和补充说明。</p>
                </div>
              </div>
            </template>
            <el-upload action="#" :auto-upload="false" :show-file-list="false" :on-change="handleUploadChange">
              <el-button type="primary" plain>添加报告附件</el-button>
            </el-upload>
            <div v-if="attachments.length" class="process-upload-list">
              <div v-for="file in attachments" :key="file.uid" class="process-upload-item">
                <span>{{ file.name }}</span>
                <el-button text type="danger" @click="removeAttachment(file.uid)">移除</el-button>
              </div>
            </div>
            <div v-else class="process-empty-note">暂无上传附件。</div>

            <div class="process-form-grid single" style="margin-top: 18px;">
              <el-form-item label="备注" prop="remarks">
                <el-input v-model="form.remarks" type="textarea" :rows="3" placeholder="请输入备注信息" />
              </el-form-item>
            </div>
            <div class="process-form-actions">
              <el-button type="primary" :loading="submitting" @click="handleSubmit">保存鉴定记录</el-button>
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
                <h3>流程提醒</h3>
                <p>确保鉴定等级、估值结果和附件材料保持一致。</p>
              </div>
            </div>
          </template>
          <div class="process-form-side-list">
            <div class="process-form-side-item">
              <h4>先确认等级</h4>
              <p>鉴定等级会直接影响保险、展示和修复优先级。</p>
            </div>
            <div class="process-form-side-item">
              <h4>会签闭环</h4>
              <p>高价值文物建议至少保留 3 位专家会签意见。</p>
            </div>
            <div class="process-form-side-item">
              <h4>附件归档</h4>
              <p>保存后可继续接入报告导出或扫描件管理流程。</p>
            </div>
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
import { appraisalApi } from '@/api/appraisal'
import { populateForm } from '@/utils/form'
import { normalizeAppraisalLevel } from '@/utils/status'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)
const attachments = ref([])

const appraisalLevelOptions = ['一级', '二级', '三级', '四级', '一般文物']

const expertSigners = [
  { name: '李文岚', status: '已签署' },
  { name: '周若衡', status: '已签署' },
  { name: '沈知远', status: '待签署' }
]

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  id: null,
  artifactCode: '',
  artifactName: '',
  appraisalDate: '',
  appraisalLevel: '',
  expertName: '',
  expertTitle: '',
  expertOpinion: '',
  appraisalResult: '',
  appraisalFee: null,
  estimatedValue: null,
  attachment: '',
  remarks: ''
})

const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  expertName: [{ required: true, message: '请输入专家姓名', trigger: 'blur' }],
  appraisalLevel: [{ required: true, message: '请选择鉴定等级', trigger: 'change' }]
}

const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await appraisalApi.getDetail(route.query.id)
    populateForm(form, {
      ...res,
      appraisalLevel: normalizeAppraisalLevel(res.appraisalLevel)
    })
  } catch {
    ElMessage.error('加载鉴定信息失败')
  }
}

const syncAttachmentField = () => {
  form.attachment = attachments.value.map((item) => item.name).join('，')
}

const handleUploadChange = (uploadFile) => {
  attachments.value = [...attachments.value, { uid: uploadFile.uid, name: uploadFile.name }]
  syncAttachmentField()
}

const removeAttachment = (uid) => {
  attachments.value = attachments.value.filter((file) => file.uid !== uid)
  syncAttachmentField()
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = {
      ...form,
      appraisalLevel: normalizeAppraisalLevel(form.appraisalLevel)
    }
    if (isEdit.value) {
      await appraisalApi.update(payload)
      ElMessage.success('鉴定记录已更新')
    } else {
      await appraisalApi.create(payload)
      ElMessage.success('鉴定记录已创建')
    }
    router.push('/appraisal/list')
  } catch {
    ElMessage.error(isEdit.value ? '更新鉴定记录失败' : '创建鉴定记录失败')
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

<style scoped>
.process-form-page {
  display: grid;
  gap: 24px;
}

.process-form-header,
.process-form-layout {
  display: grid;
  gap: 20px;
}

.process-form-header {
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: start;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--museum-color-primary);
}

.process-form-header h1,
.process-form-section-title h3,
.process-form-side-item h4 {
  margin: 0;
}

.process-form-header p,
.process-form-section-title p,
.process-form-side-item p,
.process-inline-note,
.process-empty-note {
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.process-form-kpis {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.process-form-kpi,
.process-form-side-item {
  padding: 14px 16px;
  border-radius: 16px;
  background: var(--museum-bg-subtle);
}

.process-form-kpi span {
  display: block;
  font-size: 12px;
  color: var(--museum-text-secondary);
}

.process-form-layout {
  grid-template-columns: minmax(0, 1.6fr) minmax(280px, 0.8fr);
}

.process-form-main,
.process-form-side {
  display: grid;
  gap: 20px;
}

.process-form-card {
  border-radius: 20px;
}

.process-form-section-title {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: start;
}

.process-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.process-form-span-2,
.single {
  grid-column: 1 / -1;
}

.process-chip-row,
.process-upload-list,
.process-form-side-list {
  display: grid;
  gap: 12px;
}

.process-upload-item,
.process-form-actions {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

@media (max-width: 960px) {
  .process-form-header,
  .process-form-layout,
  .process-form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
