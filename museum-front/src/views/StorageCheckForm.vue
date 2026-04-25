<template>
  <div>
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑检查记录' : '新增检查记录' }}</span>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
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
            <el-form-item label="检查日期" prop="checkDate">
              <el-date-picker v-model="form.checkDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检查类型" prop="checkType">
              <el-select v-model="form.checkType" placeholder="请选择" style="width: 100%">
                <el-option label="日常检查" value="DAILY" />
                <el-option label="月度抽检" value="MONTHLY" />
                <el-option label="季度检查" value="QUARTERLY" />
                <el-option label="半年度清点" value="HALF_YEAR" />
                <el-option label="年度普查" value="YEARLY" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="检查结果" prop="checkResult">
              <el-select v-model="form.checkResult" placeholder="请选择" style="width: 100%">
                <el-option label="正常" value="NORMAL" />
                <el-option label="异常" value="ABNORMAL" />
                <el-option label="需修复" value="NEED_REPAIR" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检查人" prop="checkerName">
              <el-input v-model="form.checkerName" placeholder="请输入检查人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="温度(°C)" prop="temperature">
              <el-input v-model="form.temperature" placeholder="如：22" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="湿度(%)" prop="humidity">
              <el-input v-model="form.humidity" placeholder="如：45" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="下次检查日期" prop="nextCheckDate">
              <el-date-picker v-model="form.nextCheckDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">损伤检测</el-divider>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="锈蚀程度" prop="corrosionLevel">
              <el-select v-model="form.corrosionLevel" placeholder="请选择" style="width: 100%">
                <el-option label="无" value="NONE" />
                <el-option label="轻微" value="SLIGHT" />
                <el-option label="中等" value="MODERATE" />
                <el-option label="严重" value="SEVERE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="霉变程度" prop="mildewLevel">
              <el-select v-model="form.mildewLevel" placeholder="请选择" style="width: 100%">
                <el-option label="无" value="NONE" />
                <el-option label="轻微" value="SLIGHT" />
                <el-option label="中等" value="MODERATE" />
                <el-option label="严重" value="SEVERE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="脆裂程度" prop="brittlenessLevel">
              <el-select v-model="form.brittlenessLevel" placeholder="请选择" style="width: 100%">
                <el-option label="无" value="NONE" />
                <el-option label="轻微" value="SLIGHT" />
                <el-option label="中等" value="MODERATE" />
                <el-option label="严重" value="SEVERE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="光照损伤" prop="lightDamageLevel">
              <el-select v-model="form.lightDamageLevel" placeholder="请选择" style="width: 100%">
                <el-option label="无" value="NONE" />
                <el-option label="轻微" value="SLIGHT" />
                <el-option label="中等" value="MODERATE" />
                <el-option label="严重" value="SEVERE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="虫蛀程度" prop="pestDamageLevel">
              <el-select v-model="form.pestDamageLevel" placeholder="请选择" style="width: 100%">
                <el-option label="无" value="NONE" />
                <el-option label="轻微" value="SLIGHT" />
                <el-option label="中等" value="MODERATE" />
                <el-option label="严重" value="SEVERE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="状况描述" prop="conditionDesc">
              <el-input v-model="form.conditionDesc" type="textarea" :rows="3" placeholder="请输入文物当前状况描述" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="处理建议" prop="handlingSuggestion">
              <el-input v-model="form.handlingSuggestion" type="textarea" :rows="3" placeholder="请输入处理建议" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remarks">
              <el-input v-model="form.remarks" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
          <el-button @click="handleBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { storageCheckApi } from '@/api/storageCheck'
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
  checkDate: '',
  checkType: 'DAILY',
  checkResult: 'NORMAL',
  temperature: '',
  humidity: '',
  corrosionLevel: 'NONE',
  mildewLevel: 'NONE',
  brittlenessLevel: 'NONE',
  lightDamageLevel: 'NONE',
  pestDamageLevel: 'NONE',
  conditionDesc: '',
  handlingSuggestion: '',
  nextCheckDate: '',
  checkerName: '',
  remarks: ''
})

const rules = {
  artifactCode: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  artifactName: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  checkDate: [{ required: true, message: '请选择检查日期', trigger: 'change' }],
  checkType: [{ required: true, message: '请选择检查类型', trigger: 'change' }],
  checkResult: [{ required: true, message: '请选择检查结果', trigger: 'change' }]
}

const loadData = async () => {
  if (!route.query.id) return
  try {
    const res = await storageCheckApi.getDetail(route.query.id)
    populateForm(form, res)
  } catch {
    ElMessage.error('加载数据失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await storageCheckApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await storageCheckApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/storage-check/list')
  } catch {
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
