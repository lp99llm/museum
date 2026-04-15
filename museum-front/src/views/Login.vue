<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>博物馆文物管理系统</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
          <el-link type="primary" @click="forgetDialogVisible = true" style="float: right;">忘记密码？</el-link>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="text" @click="registerDialogVisible = true">还没有账号？立即注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 注册弹窗 -->
    <el-dialog title="注册新账号" v-model="registerDialogVisible" width="400px">
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="registerForm.password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱（用于找回密码）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" :loading="registerLoading">注册</el-button>
      </template>
    </el-dialog>

    <!-- 忘记密码弹窗 -->
    <el-dialog title="找回密码" v-model="forgetDialogVisible" width="400px">
      <el-steps :active="forgetStep" finish-status="success" simple>
        <el-step title="验证身份" />
        <el-step title="重置密码" />
      </el-steps>
      <div v-if="forgetStep === 0">
        <el-form :model="forgetForm" :rules="forgetRules" ref="forgetFormRef">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="forgetForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="forgetForm.email" placeholder="请输入注册时使用的邮箱" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="sendVerifyCode" :loading="sendCodeLoading">发送验证码</el-button>
          </el-form-item>
          <el-form-item label="验证码" prop="code">
            <el-input v-model="forgetForm.code" placeholder="请输入邮箱中的验证码" />
          </el-form-item>
        </el-form>
      </div>
      <div v-if="forgetStep === 1">
        <el-form :model="resetForm" :rules="resetRules" ref="resetFormRef">
          <el-form-item label="新密码" prop="newPassword">
            <el-input type="password" v-model="resetForm.newPassword" placeholder="请输入新密码" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input type="password" v-model="resetForm.confirmPassword" placeholder="请再次输入新密码" show-password />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="forgetDialogVisible = false">取消</el-button>
        <el-button v-if="forgetStep === 0" type="primary" @click="verifyCodeAndNext" :loading="verifyLoading">下一步</el-button>
        <el-button v-if="forgetStep === 1" type="primary" @click="resetPassword" :loading="resetLoading">完成重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

// 登录表单
const form = reactive({
  username: '',
  password: ''
})

const rememberMe = ref(false)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 注册相关
const registerDialogVisible = ref(false)
const registerLoading = ref(false)
const registerFormRef = ref()
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }, { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }]
}

// 忘记密码相关
const forgetDialogVisible = ref(false)
const forgetStep = ref(0)
const forgetFormRef = ref()
const resetFormRef = ref()
const sendCodeLoading = ref(false)
const verifyLoading = ref(false)
const resetLoading = ref(false)

const forgetForm = reactive({
  username: '',
  email: '',
  code: ''
})

const resetForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

const forgetRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }, { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const resetRules = {
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 登录逻辑（保留原有，增加记住密码）
const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    if (rememberMe.value) {
      // 记住密码：存储用户名和密码（注意：生产环境应加密存储，这里仅为演示）
      localStorage.setItem('savedUsername', form.username)
      localStorage.setItem('savedPassword', form.password)
      localStorage.setItem('rememberMe', 'true')
    } else {
      localStorage.removeItem('savedUsername')
      localStorage.removeItem('savedPassword')
      localStorage.setItem('rememberMe', 'false')
    }
    ElMessage.success('登录成功')
    router.push('/artifacts')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 注册
const handleRegister = async () => {
  await registerFormRef.value.validate()
  registerLoading.value = true
  try {
    // 调用后端注册接口
    await authApi.register({
      username: registerForm.username,
      password: registerForm.password,
      email: registerForm.email
    })
    ElMessage.success('注册成功，请登录')
    registerDialogVisible.value = false
    // 自动填充用户名
    form.username = registerForm.username
    registerForm.username = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
    registerForm.email = ''
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  } finally {
    registerLoading.value = false
  }
}

// 发送验证码（忘记密码第一步）
const sendVerifyCode = async () => {
  await forgetFormRef.value.validate(['username', 'email'])
  sendCodeLoading.value = true
  try {
    await authApi.sendResetCode({
      username: forgetForm.username,
      email: forgetForm.email
    })
    ElMessage.success('验证码已发送至您的邮箱，请注意查收')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发送失败')
  } finally {
    sendCodeLoading.value = false
  }
}

// 验证验证码并进入下一步
const verifyCodeAndNext = async () => {
  await forgetFormRef.value.validate()
  verifyLoading.value = true
  try {
    // 验证验证码是否正确（后端接口）
    await authApi.verifyResetCode({
      username: forgetForm.username,
      email: forgetForm.email,
      code: forgetForm.code
    })
    forgetStep.value = 1
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '验证码错误')
  } finally {
    verifyLoading.value = false
  }
}

// 重置密码
const resetPassword = async () => {
  await resetFormRef.value.validate()
  resetLoading.value = true
  try {
    await authApi.resetPassword({
      username: forgetForm.username,
      email: forgetForm.email,
      newPassword: resetForm.newPassword
    })
    ElMessage.success('密码重置成功，请登录')
    forgetDialogVisible.value = false
    // 重置步骤
    forgetStep.value = 0
    forgetForm.username = ''
    forgetForm.email = ''
    forgetForm.code = ''
    resetForm.newPassword = ''
    resetForm.confirmPassword = ''
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '重置失败')
  } finally {
    resetLoading.value = false
  }
}

// 加载记住的密码
onMounted(() => {
  const remember = localStorage.getItem('rememberMe')
  if (remember === 'true') {
    const savedUsername = localStorage.getItem('savedUsername')
    const savedPassword = localStorage.getItem('savedPassword')
    if (savedUsername) form.username = savedUsername
    if (savedPassword) form.password = savedPassword
    rememberMe.value = true
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.login-card {
  width: 420px;
}
</style>