<template>
  <div>
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑出库记录' : '新增出库记录' }}</span>
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

        <el-divider content-position="left">出库信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出库事由" prop="outboundReason">
              <el-select v-model="form.outboundReason" placeholder="请选择" style="width: 100%">
                <el-option label="展览" value="EXHIBITION" />
                <el-option label="研究" value="RESEARCH" />
                <el-option label="修复" value="RESTORATION" />
                <el-option label="借展" value="LOAN" />
                <el-option label="鉴定" value="APPRAISAL" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计归还日期" prop="expectedReturnDate">
              <el-date-picker
                v-model="form.expectedReturnDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">目标信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标展览" prop="targetExhibition">
              <el-input v-model="form.targetExhibition" placeholder="请输入目标展览名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标位置" prop="targetLocation">
              <el-input v-model="form.targetLocation" placeholder="展位号/存放位置" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="targetContact">
              <el-input v-model="form.targetContact" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="targetPhone">
              <el-input v-model="form.targetPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">操作信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经办人" prop="handler">
              <el-input v-model="form.handler" placeholder="请输入经办人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交申请</el-button>
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
import { outboundApi } from '@/api/outbound'

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
  expectedReturnDate: [{ required: true, message: '请选择预计归还日期', trigger: 'change' }]
}

const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await outboundApi.getDetail(route.query.id)
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
      await outboundApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await outboundApi.create(form)
      ElMessage.success('提交成功')
    }
    router.push('/outbound/list')
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '提交失败')
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
