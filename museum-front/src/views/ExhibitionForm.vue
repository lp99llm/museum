<template>
  <div class="exhibition-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑展览' : '新增展览' }}</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="展览名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入展览名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="展览主题" prop="theme">
              <el-input v-model="form.theme" placeholder="请输入展览主题" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="form.endDate"
                type="date"
                placeholder="选择结束日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="展览场地" prop="venue">
              <el-input v-model="form.venue" placeholder="请输入展览场地" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="策展人" prop="curator">
              <el-input v-model="form.curator" placeholder="请输入策展人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="展览状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="策划中" value="PLANNING" />
                <el-option label="进行中" value="ONGOING" />
                <el-option label="已结束" value="FINISHED" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="宣传海报" prop="posterUrl">
              <el-input v-model="form.posterUrl" placeholder="请输入海报URL" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="展览简介" prop="description">
          <el-input type="textarea" v-model="form.description" placeholder="请输入展览简介" rows="4" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { exhibitionApi } from '@/api/exhibition'

const route = useRoute()
const router = useRouter()
const formRef = ref()

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  name: '',
  theme: '',
  startDate: '',
  endDate: '',
  venue: '',
  curator: '',
  description: '',
  posterUrl: '',
  status: 'PLANNING'
})

const rules = {
  name: [{ required: true, message: '请输入展览名称', trigger: 'blur' }],
  theme: [{ required: true, message: '请输入展览主题', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  venue: [{ required: true, message: '请输入展览场地', trigger: 'blur' }]
}

const loadData = async () => {
  if (isEdit.value) {
    try {
      const res = await exhibitionApi.getDetail(route.params.id)
      Object.assign(form, res.data)
    } catch (error) {
      ElMessage.error('加载数据失败')
    }
  }
}

const submitForm = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await exhibitionApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await exhibitionApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/exhibitions')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const goBack = () => {
  router.push('/exhibitions')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.exhibition-form {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
