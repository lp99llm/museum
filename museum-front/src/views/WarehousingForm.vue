<template>
  <div class="process-form-page">
    <section class="process-form-header">
      <div>
        <div class="eyebrow">Inbound Process</div>
        <h1>{{ isEdit ? '编辑入库管理' : '入库管理' }}</h1>
        <p>完成库位分配、环境参数确认和标签打印准备，建立文物正式入库记录。</p>
      </div>
      <div class="process-form-kpis">
        <div class="process-form-kpi">
          <span>当前阶段</span>
          <strong>库位确认</strong>
        </div>
        <div class="process-form-kpi">
          <span>标签状态</span>
          <strong>待打印</strong>
        </div>
        <div class="process-form-kpi">
          <span>环境基线</span>
          <strong>已配置</strong>
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
                  <h3>入库信息</h3>
                  <p>确认文物与入库状态</p>
                </div>
              </div>
            </template>
            <div class="process-form-grid">
              <el-form-item label="文物编号" prop="artifactCode"><el-input v-model="form.artifactCode" /></el-form-item>
              <el-form-item label="文物名称" prop="artifactName"><el-input v-model="form.artifactName" /></el-form-item>
              <el-form-item label="入库日期" prop="storageDate"><el-date-picker v-model="form.storageDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
              <el-form-item label="入库状态" prop="storageStatus">
                <el-select v-model="form.storageStatus">
                  <el-option label="待确认" value="PENDING" />
                  <el-option label="已确认" value="CONFIRMED" />
                  <el-option label="已驳回" value="REJECTED" />
                </el-select>
              </el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>库位与环境</h3>
                  <p>分配库房、货架编号和保存参数</p>
                </div>
              </div>
            </template>
            <div class="process-form-grid">
              <el-form-item label="存放库区" prop="storageArea"><el-input v-model="form.storageArea" placeholder="如 A库房" /></el-form-item>
              <el-form-item label="货架号" prop="storageShelf"><el-input v-model="form.storageShelf" placeholder="如 A-03" /></el-form-item>
              <el-form-item label="存放位置" prop="storagePosition"><el-input v-model="form.storagePosition" placeholder="如 第2层第4格" /></el-form-item>
              <el-form-item label="经办人" prop="handler"><el-input v-model="form.handler" /></el-form-item>
              <el-form-item label="温度(°C)" prop="temperature"><el-input-number v-model="form.temperature" :min="-20" :max="50" :precision="1" style="width:100%" /></el-form-item>
              <el-form-item label="湿度(%)" prop="humidity"><el-input-number v-model="form.humidity" :min="0" :max="100" style="width:100%" /></el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>标签打印与备注</h3>
                  <p>条码、二维码与入库补充说明</p>
                </div>
              </div>
            </template>
            <div class="process-chip-row">
              <el-tag round effect="plain">条形码标签</el-tag>
              <el-tag round effect="plain">二维码标签</el-tag>
              <el-tag round effect="plain">库位标签</el-tag>
            </div>
            <div class="process-form-grid single" style="margin-top: 18px;">
              <el-form-item label="备注" prop="remarks"><el-input v-model="form.remarks" type="textarea" :rows="4" /></el-form-item>
            </div>
            <div class="process-form-actions">
              <el-button type="primary" :loading="submitting" @click="handleSubmit">保存入库记录</el-button>
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
                <h3>业务提醒</h3>
                <p>入库确认前建议完成核验</p>
              </div>
            </div>
          </template>
          <div class="process-form-side-list">
            <div class="process-form-side-item">
              <h4>库位编号规范</h4>
              <p>建议采用“库区-货架-层位”统一格式，便于后续盘点与追溯。</p>
            </div>
            <div class="process-form-side-item">
              <h4>环境参数记录</h4>
              <p>温湿度将用于后续异常预警与保护环境要求同步。</p>
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
import { warehousingApi } from '@/api/warehousing'
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
    populateForm(form, res)
  } catch {
    ElMessage.error('加载入库信息失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await warehousingApi.update(form)
      ElMessage.success('入库记录已更新')
    } else {
      await warehousingApi.create(form)
      ElMessage.success('入库记录已创建')
    }
    router.push('/warehousing/list')
  } catch {
    ElMessage.error(isEdit.value ? '更新入库记录失败' : '创建入库记录失败')
  } finally {
    submitting.value = false
  }
}

const handleBack = () => router.back()

onMounted(loadData)
</script>
