<template>
  <div class="artifact-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑文物' : '新增文物' }}</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="文物名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="文物编号" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="年代" prop="era">
          <el-input v-model="form.era" />
        </el-form-item>
        <el-form-item label="出土地点" prop="location">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="现藏地点" prop="currentLocation">
          <el-input v-model="form.currentLocation" />
        </el-form-item>
        <el-form-item label="文物简介" prop="description">
          <el-input type="textarea" v-model="form.description" rows="4" />
        </el-form-item>
        <el-form-item label="状态" prop="currentState">
          <el-select v-model="form.currentState" placeholder="请选择状态">
            <el-option label="在库" value="IN_STORAGE" />
            <el-option label="展览中" value="IN_EXHIBITION" />
            <el-option label="修复中" value="IN_RESTORATION" />
            <el-option label="外借中" value="ON_LOAN" />
          </el-select>
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
import { artifactApi } from '@/api/artifact'   // 改为导入 artifactApi

const route = useRoute()
const router = useRouter()
const formRef = ref()

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  name: '',
  code: '',
  category: '',
  era: '',
  location: '',
  currentLocation: '',
  description: '',
  currentState: 'IN_STORAGE'
})

const rules = {
  name: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  category: [{ required: true, message: '请输入类别', trigger: 'blur' }]
}

// 如果是编辑模式，加载现有数据
const loadData = async () => {
  if (isEdit.value) {
    try {
      const res = await artifactApi.getDetail(route.params.id)
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
      await artifactApi.update(route.params.id, form)
      ElMessage.success('更新成功')
    } else {
      await artifactApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/artifacts')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const goBack = () => {
  router.push('/artifacts')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.artifact-form {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
</style>