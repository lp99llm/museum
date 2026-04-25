<template>
  <div class="process-form-page">
    <section class="process-form-header">
      <div>
        <div class="eyebrow">Acquisition Process</div>
        <h1>{{ isEdit ? '编辑征集登记' : '征集登记' }}</h1>
        <p>标准化录入文物基本信息、来源渠道与征集附件，确保征集流程可审计、可追溯。</p>
      </div>
      <div class="process-form-kpis">
        <div class="process-form-kpi">
          <span>当前阶段</span>
          <strong>征集建档</strong>
        </div>
        <div class="process-form-kpi">
          <span>建议时效</span>
          <strong>2 天</strong>
        </div>
        <div class="process-form-kpi">
          <span>附件完整度</span>
          <strong>{{ proofFiles.length }}/3</strong>
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
                  <h3>文物基本信息</h3>
                  <p>名称、类别、年代、材质等标准化录入字段</p>
                </div>
              </div>
            </template>

            <div class="process-form-grid">
              <el-form-item label="文物编号" prop="artifactCode">
                <el-input v-model="form.artifactCode" placeholder="如 MUS-2026-001" />
              </el-form-item>
              <el-form-item label="文物名称" prop="artifactName">
                <el-input v-model="form.artifactName" placeholder="请输入文物名称" />
              </el-form-item>
              <el-form-item label="文物类别" prop="artifactCategory">
                <el-select v-model="form.artifactCategory" placeholder="请选择文物类别">
                  <el-option label="青铜器" value="青铜器" />
                  <el-option label="陶瓷" value="陶瓷" />
                  <el-option label="书画" value="书画" />
                  <el-option label="玉器" value="玉器" />
                  <el-option label="杂项" value="杂项" />
                </el-select>
              </el-form-item>
              <el-form-item label="年代" prop="artifactEra">
                <el-input v-model="form.artifactEra" placeholder="如 唐代 / 明代" />
              </el-form-item>
              <el-form-item label="材质" prop="artifactMaterial">
                <el-input v-model="form.artifactMaterial" placeholder="如 青铜 / 纸本设色" />
              </el-form-item>
              <el-form-item label="征集日期" prop="acquisitionDate">
                <el-date-picker v-model="form.acquisitionDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择征集日期" style="width: 100%" />
              </el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>来源信息</h3>
                  <p>记录征集人、来源渠道与证明材料</p>
                </div>
              </div>
            </template>

            <div class="process-form-grid">
              <el-form-item label="征集类型" prop="acquisitionType">
                <el-select v-model="form.acquisitionType" placeholder="请选择征集类型">
                  <el-option label="捐赠" value="DONATION" />
                  <el-option label="购买" value="PURCHASE" />
                  <el-option label="移交" value="TRANSFER" />
                </el-select>
              </el-form-item>
              <el-form-item label="来源渠道" prop="sourceChannel">
                <el-input v-model="form.sourceChannel" placeholder="如 社会捐赠 / 定向征集" />
              </el-form-item>
              <el-form-item label="来源机构/捐赠人" prop="sourceInstitution">
                <el-input v-model="form.sourceInstitution" placeholder="请输入机构或个人名称" />
              </el-form-item>
              <el-form-item label="联系人" prop="sourceContact">
                <el-input v-model="form.sourceContact" placeholder="请输入联系人姓名" />
              </el-form-item>
              <el-form-item label="联系电话" prop="sourcePhone">
                <el-input v-model="form.sourcePhone" placeholder="请输入联系电话" />
              </el-form-item>
              <el-form-item label="征集费用" prop="acquisitionCost">
                <el-input-number v-model="form.acquisitionCost" :min="0" :precision="2" :step="100" style="width: 100%" />
              </el-form-item>
              <el-form-item label="来源说明" prop="donorInfo" class="process-form-span-2">
                <el-input v-model="form.donorInfo" type="textarea" :rows="4" placeholder="补充记录捐赠、购买或移交背景说明" />
              </el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>附件上传</h3>
                  <p>支持文物照片、来源证明等材料预归档</p>
                </div>
              </div>
            </template>

            <el-upload action="#" :auto-upload="false" :show-file-list="false" :on-change="handleUploadChange">
              <el-button type="primary" plain>添加附件</el-button>
            </el-upload>
            <div class="process-inline-note">当前为前端演示列表，后续可直接接入对象存储或文件中心。</div>

            <div v-if="proofFiles.length" class="process-upload-list">
              <div v-for="file in proofFiles" :key="file.uid" class="process-upload-item">
                <span>{{ file.name }}</span>
                <el-button text type="danger" @click="removeFile(file.uid)">移除</el-button>
              </div>
            </div>
            <div v-else class="process-empty-note">暂无上传附件。</div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>备注与提交</h3>
                  <p>补充风险说明、交接备注和内部审阅意见</p>
                </div>
              </div>
            </template>

            <div class="process-form-grid single">
              <el-form-item label="备注" prop="remarks">
                <el-input v-model="form.remarks" type="textarea" :rows="4" placeholder="请输入备注信息" />
              </el-form-item>
            </div>

            <div class="process-form-actions">
              <el-button type="primary" :loading="submitting" @click="handleSubmit">保存征集登记</el-button>
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
                <h3>填写提醒</h3>
                <p>确保来源信息与附件链路完整</p>
              </div>
            </div>
          </template>
          <div class="process-form-side-list">
            <div class="process-form-side-item">
              <h4>来源合法性</h4>
              <p>优先补齐捐赠协议、征集审批单和来源证明，便于后续审计追踪。</p>
            </div>
            <div class="process-form-side-item">
              <h4>图像资料</h4>
              <p>建议至少上传正面照、细节照与来源凭证三类附件。</p>
            </div>
            <div class="process-form-side-item">
              <h4>提交后流转</h4>
              <p>保存后可进入鉴定评估流程，由专家组完成定级与价值判断。</p>
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
import { acquisitionApi } from '@/api/acquisition'
import { populateForm } from '@/utils/form'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)
const proofFiles = ref([])

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  id: null,
  artifactCode: '',
  artifactName: '',
  artifactCategory: '',
  artifactEra: '',
  artifactMaterial: '',
  acquisitionDate: '',
  acquisitionType: '',
  acquisitionCost: null,
  sourceChannel: '',
  sourceInstitution: '',
  sourceContact: '',
  sourcePhone: '',
  donorInfo: '',
  remarks: ''
})

const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  acquisitionType: [{ required: true, message: '请选择征集类型', trigger: 'change' }],
  acquisitionDate: [{ required: true, message: '请选择征集日期', trigger: 'change' }]
}

const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await acquisitionApi.getDetail(route.query.id)
    populateForm(form, res)
  } catch {
    ElMessage.error('加载征集信息失败')
  }
}

const handleUploadChange = (uploadFile) => {
  proofFiles.value = [...proofFiles.value, { uid: uploadFile.uid, name: uploadFile.name }]
}

const removeFile = (uid) => {
  proofFiles.value = proofFiles.value.filter((file) => file.uid !== uid)
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await acquisitionApi.update(form)
      ElMessage.success('征集登记已更新')
    } else {
      await acquisitionApi.create(form)
      ElMessage.success('征集登记已创建')
    }
    router.push('/acquisition/list')
  } catch {
    ElMessage.error(isEdit.value ? '更新征集登记失败' : '创建征集登记失败')
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
