<template>
  <div class="artifact-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑文物' : '新增文物' }}</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="文物编号" prop="code">
              <el-input v-model="form.code" placeholder="请输入文物编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文物名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入文物名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类别" prop="category">
              <el-select v-model="form.category" placeholder="请选择类别" style="width: 100%">
                <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年代" prop="era">
              <el-input v-model="form.era" placeholder="例如：汉代、清代" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="材质" prop="material">
              <el-input v-model="form.material" placeholder="请输入材质" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="尺寸" prop="size">
              <el-input v-model="form.size" placeholder="例如：12×36×28cm" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="重量" prop="weight">
              <el-input-number v-model="form.weight" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源" prop="source">
              <el-input v-model="form.source" placeholder="请输入来源信息" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保存状态" prop="preservationStatus">
              <el-select v-model="form.preservationStatus" placeholder="请选择保存状态" style="width: 100%">
                <el-option v-for="item in preservationOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="鉴定等级" prop="appraisalLevel">
              <el-select v-model="form.appraisalLevel" placeholder="请选择鉴定等级" style="width: 100%">
                <el-option v-for="item in appraisalOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前状态" prop="currentState">
              <el-select v-model="form.currentState" placeholder="请选择当前状态" style="width: 100%">
                <el-option label="在库" value="IN_STORAGE" />
                <el-option label="展出中" value="IN_EXHIBITION" />
                <el-option label="修复中" value="IN_RESTORATION" />
                <el-option label="外借中" value="ON_LOAN" />
                <el-option label="出库中" value="OUTBOUND" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存放位置" prop="location">
              <el-input v-model="form.location" placeholder="请输入当前存放位置" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="主图地址" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入图片 URL" />
        </el-form-item>

        <el-form-item label="保险信息" prop="insuranceInfo">
          <el-input
            v-model="form.insuranceInfo"
            type="textarea"
            :rows="3"
            placeholder='支持纯文本或 JSON，例如：{"company":"保险机构","policyNo":"保单号","coverage":"保额"}'
          />
        </el-form-item>

        <el-form-item label="环境要求" prop="environmentRequirements">
          <el-input
            v-model="form.environmentRequirements"
            type="textarea"
            :rows="3"
            placeholder='支持纯文本或 JSON，例如：{"temperature":"18-22°C","humidity":"45%-55%","light":"≤50 lux","notes":"特殊要求"}'
          />
        </el-form-item>

        <el-form-item label="资料附件" prop="documents">
          <el-input
            v-model="form.documents"
            type="textarea"
            :rows="3"
            placeholder="支持填写逗号分隔的资料 URL，或 JSON 数组"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { artifactApi } from '@/api/artifact'
import {
  normalizeAppraisalLevel,
  normalizeArtifactState,
  normalizePreservationStatus
} from '@/utils/status'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const saving = ref(false)

const categoryOptions = ['青铜器', '陶瓷', '书画', '玉器', '杂项', '金银器', '织绣']
const preservationOptions = ['完好', '轻微损伤', '待修复', '修复中']
const appraisalOptions = ['一级', '二级', '三级', '四级', '一般文物']

const isEdit = computed(() => Boolean(route.params.id))

const form = reactive({
  id: null,
  code: '',
  name: '',
  category: '',
  era: '',
  material: '',
  size: '',
  weight: null,
  source: '',
  preservationStatus: '',
  appraisalLevel: '',
  currentState: 'IN_STORAGE',
  location: '',
  imageUrl: '',
  insuranceInfo: '',
  environmentRequirements: '',
  documents: ''
})

const rules = {
  code: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择类别', trigger: 'change' }]
}

const loadDetail = async () => {
  if (!isEdit.value) return
  try {
    const data = await artifactApi.getDetail(route.params.id)
    Object.assign(form, {
      id: data.id,
      code: data.code || '',
      name: data.name || '',
      category: data.category || '',
      era: data.era || '',
      material: data.material || '',
      size: data.size || '',
      weight: data.weight ?? null,
      source: data.source || '',
      preservationStatus: normalizePreservationStatus(data.preservationStatus),
      appraisalLevel: normalizeAppraisalLevel(data.appraisalLevel),
      currentState: normalizeArtifactState(data.currentState) || 'IN_STORAGE',
      location: data.location || data.currentLocation || '',
      imageUrl: data.imageUrl || '',
      insuranceInfo: data.insuranceInfo || '',
      environmentRequirements: data.environmentRequirements || '',
      documents: data.documents || ''
    })
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '获取文物信息失败')
  }
}

const submitForm = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const payload = {
      ...form,
      preservationStatus: normalizePreservationStatus(form.preservationStatus),
      appraisalLevel: normalizeAppraisalLevel(form.appraisalLevel),
      currentState: normalizeArtifactState(form.currentState) || 'IN_STORAGE',
      weight: form.weight === null || form.weight === '' ? null : Number(form.weight)
    }

    if (isEdit.value) {
      await artifactApi.update(payload)
      ElMessage.success('文物信息已更新')
    } else {
      await artifactApi.create(payload)
      ElMessage.success('文物信息已创建')
    }
    router.push('/artifacts')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || (isEdit.value ? '更新失败' : '创建失败'))
  } finally {
    saving.value = false
  }
}

const goBack = () => {
  router.push('/artifacts')
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.artifact-form {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
