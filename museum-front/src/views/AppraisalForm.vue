<template>
  <div>
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑鉴定记录' : '新增鉴定记录' }}</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="140px">
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

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="鉴定日期" prop="appraisalDate">
              <el-date-picker
                v-model="form.appraisalDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="鉴定等级" prop="appraisalLevel">
              <el-select v-model="form.appraisalLevel" placeholder="请选择" style="width: 100%">
                <el-option label="一级" value="LEVEL1" />
                <el-option label="二级" value="LEVEL2" />
                <el-option label="三级" value="LEVEL3" />
                <el-option label="四级" value="LEVEL4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专家姓名" prop="expertName">
              <el-input v-model="form.expertName" placeholder="请输入专家姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专家职称" prop="expertTitle">
              <el-input v-model="form.expertTitle" placeholder="请输入专家职称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="鉴定费用" prop="appraisalFee">
              <el-input-number
                v-model="form.appraisalFee"
                :precision="2"
                :step="100"
                :min="0"
                placeholder="请输入"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="估值金额" prop="estimatedValue">
              <el-input-number
                v-model="form.estimatedValue"
                :precision="2"
                :step="1000"
                :min="0"
                placeholder="请输入"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="鉴定结果" prop="appraisalResult">
              <el-input v-model="form.appraisalResult" type="textarea" :rows="3" placeholder="请输入鉴定结果" />
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

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="附件" prop="attachment">
              <el-input v-model="form.attachment" placeholder="请输入附件地址或上传" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remarks">
              <el-input v-model="form.remarks" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { appraisalApi } from '@/api/appraisal'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)

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
      await appraisalApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await appraisalApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/appraisal/list')
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
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
