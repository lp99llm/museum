<template>
  <div class="user-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑用户' : '新增用户' }}</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" :required="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          <span v-if="isEdit" style="color: #999; font-size: 12px; margin-left: 8px">留空则不修改密码</span>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="handleBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user'

const router = useRouter()
const formRef = ref(null)
const isEdit = computed(() => !!form.id)

const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  enabled: true
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const loadData = async (id) => {
  try {
    const res = await userApi.getDetail(id)
    Object.assign(form, res.data)
    form.password = ''
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (isEdit.value) {
      await userApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await userApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/users')
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const handleBack = () => {
  router.push('/users')
}

onMounted(() => {
  const id = router.currentRoute.value.query.id
  if (id) {
    loadData(id)
  }
})
</script>

<style scoped>
.user-form {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}
</style>
