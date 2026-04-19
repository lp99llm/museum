<template>
  <div>
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑入库记录' : '新增入库记录' }}</span>
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
            <el-form-item label="入库日期" prop="storageDate">
              <el-date-picker
                v-model="form.storageDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入库状态" prop="storageStatus">
              <el-select v-model="form.storageStatus" placeholder="请选择" style="width: 100%">
                <el-option label="待确认" value="PENDING" />
                <el-option label="已确认" value="CONFIRMED" />
                <el-option label="已拒绝" value="REJECTED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="存放库区" prop="storageArea">
              <el-input v-model="form.storageArea" placeholder="请输入库区" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="货架号" prop="storageShelf">
              <el-input v-model="form.storageShelf" placeholder="请输入货架号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="存放位置" prop="storagePosition">
              <el-input v-model="form.storagePosition" placeholder="请输入位置" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="温度(°C)" prop="temperature">
              <el-input-number
                v-model="form.temperature"
                :precision="1"
                :step="1"
                :min="-20"
                :max="50"
                placeholder="请输入"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="湿度(%)" prop="humidity">
              <el-input-number
                v-model="form.humidity"
                :precision="0"
                :step="1"
                :min="0"
                :max="100"
                placeholder="请输入"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="操作人" prop="handler">
              <el-input v-model="form.handler" placeholder="请输入操作人" />
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
import { warehousingApi } from '@/api/warehousing'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  id: null,
  artifactCode: '',
  artifactName: '',
  storageDate: '',
  storageStatus: 'PENDING',
  storageArea: '',
  storageShelf: '',
  storagePosition: '',
  temperature: null,
  humidity: null,
  handler: '',
  remarks: ''
})

const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  storageDate: [{ required: true, message: '请选择入库日期', trigger: 'change' }],
  storageStatus: [{ required: true, message: '请选择入库状态', trigger: 'change' }],
  storageArea: [{ required: true, message: '请输入存放库区', trigger: 'blur' }]
}

const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await warehousingApi.getDetail(route.query.id)
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
      await warehousingApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await warehousingApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/warehousing/list')
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
