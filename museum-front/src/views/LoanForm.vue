<template>
  <div>
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑外借记录' : '新增外借记录' }}</span>
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

        <el-divider content-position="left">申请信息</el-divider>
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
          <el-col :span="24">
            <el-form-item label="申请原因" prop="applyReason">
              <el-input v-model="form.applyReason" type="textarea" :rows="2" placeholder="请输入申请原因" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">借用单位信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="借用单位" prop="borrowerInstitution">
              <el-input v-model="form.borrowerInstitution" placeholder="请输入借用单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人" prop="borrowerContact">
              <el-input v-model="form.borrowerContact" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="borrowerPhone">
              <el-input v-model="form.borrowerPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系地址" prop="borrowerAddress">
              <el-input v-model="form.borrowerAddress" placeholder="请输入联系地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">借用详情</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预计借出日期" prop="loanDate">
              <el-date-picker
                v-model="form.loanDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
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

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="借用用途" prop="loanPurpose">
              <el-select v-model="form.loanPurpose" placeholder="请选择" style="width: 100%">
                <el-option label="展览" value="EXHIBITION" />
                <el-option label="研究" value="RESEARCH" />
                <el-option label="教育" value="EDUCATION" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="运输方式" prop="transportMethod">
              <el-select v-model="form.transportMethod" placeholder="请选择" style="width: 100%">
                <el-option label="专车运输" value="SPECIAL_CAR" />
                <el-option label="快递" value="EXPRESS" />
                <el-option label="自取" value="SELF_PICKUP" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="安全措施" prop="securityMeasures">
              <el-input v-model="form.securityMeasures" placeholder="请输入安全措施" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="借用地点" prop="loanLocation">
              <el-input v-model="form.loanLocation" placeholder="请输入借用地点" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="借用监督人" prop="loanSupervisor">
              <el-input v-model="form.loanSupervisor" placeholder="请输入监督人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">协议信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="借用协议" prop="loanAgreement">
              <el-input v-model="form.loanAgreement" type="textarea" :rows="3" placeholder="请输入借用协议内容" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">状态信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="借用前状态" prop="beforeCondition">
              <el-input v-model="form.beforeCondition" type="textarea" :rows="2" placeholder="请描述借用前状态" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归还后状态" prop="afterCondition">
              <el-input v-model="form.afterCondition" type="textarea" :rows="2" placeholder="请描述归还后状态" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="归还图片" prop="returnImage">
              <el-input v-model="form.returnImage" placeholder="请输入图片地址或上传" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="归还备注" prop="returnRemarks">
              <el-input v-model="form.returnRemarks" type="textarea" :rows="2" placeholder="请输入归还备注" />
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
            <el-form-item label="备注" prop="approvalOpinion">
              <el-input v-model="form.approvalOpinion" type="textarea" :rows="2" placeholder="备注信息" />
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
import { loanApi } from '@/api/loan'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  id: null,
  artifactCode: '',
  artifactName: '',
  applicant: '',
  applyDate: '',
  applyReason: '',
  borrowerInstitution: '',
  borrowerContact: '',
  borrowerPhone: '',
  borrowerAddress: '',
  loanDate: '',
  expectedReturnDate: '',
  actualReturnDate: '',
  loanPurpose: '',
  transportMethod: '',
  securityMeasures: '',
  loanLocation: '',
  loanSupervisor: '',
  loanAgreement: '',
  beforeCondition: '',
  afterCondition: '',
  returnImage: '',
  returnRemarks: '',
  approvalOpinion: '',
  handler: ''
})

const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  borrowerInstitution: [{ required: true, message: '请输入借用单位', trigger: 'blur' }],
  borrowerContact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  expectedReturnDate: [{ required: true, message: '请选择预计归还日期', trigger: 'change' }]
}

const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await loanApi.getDetail(route.query.id)
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
      await loanApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await loanApi.create(form)
      ElMessage.success('保存成功')
    }
    router.push('/loan/list')
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
