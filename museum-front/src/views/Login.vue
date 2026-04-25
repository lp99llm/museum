<template>
  <div class="login-page">
    <div class="pattern-overlay" />

    <section class="brand-panel">
      <div class="brand-carousel">
        <div
          v-for="(slide, index) in slides"
          :key="slide.title"
          class="brand-slide"
          :class="{ active: index === activeSlide }"
          :style="{ backgroundImage: slide.background }"
        />
      </div>

      <div class="brand-mask" />

      <div class="brand-content">
        <div class="brand-badge">Museum Heritage Console</div>
        <h1>博物馆文物管理系统</h1>
        <p class="brand-slogan">守护文明 | 智管传承</p>
        <p class="brand-description">
          面向馆藏管理、展览流转、修复保护与审计追踪的一体化业务平台。
        </p>

        <div class="brand-highlights">
          <div class="highlight-card">
            <span class="highlight-label">今日馆务</span>
            <strong>18 项待处理</strong>
          </div>
          <div class="highlight-card">
            <span class="highlight-label">安全审计</span>
            <strong>全流程可追溯</strong>
          </div>
        </div>

        <div class="carousel-dots">
          <button
            v-for="(slide, index) in slides"
            :key="`${slide.title}-${index}`"
            type="button"
            class="dot"
            :class="{ active: index === activeSlide }"
            @click="activeSlide = index"
          />
        </div>
      </div>
    </section>

    <section class="form-panel">
      <div class="form-shell">
        <div class="form-header">
          <div class="eyebrow">账号登录</div>
          <h2>欢迎进入馆藏工作台</h2>
          <p>请输入账号信息完成身份验证。</p>
        </div>

        <el-alert
          title="首次登录请使用管理员分配的初始账号，并在登录后立即修改密码。"
          type="info"
          :closable="false"
          show-icon
          class="guide-alert"
        />

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input v-model="form.username" size="large" placeholder="请输入账号">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              size="large"
              type="password"
              placeholder="请输入密码"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="captcha">
            <div class="captcha-row">
              <el-input v-model="form.captcha" size="large" placeholder="请输入图形验证码">
                <template #prefix>
                  <el-icon><Key /></el-icon>
                </template>
              </el-input>
              <button type="button" class="captcha-box" @click="refreshCaptcha" aria-label="刷新验证码">
                <img :src="captchaImage" alt="图形验证码" class="captcha-image">
              </button>
            </div>
          </el-form-item>

          <div class="form-meta">
            <el-checkbox v-model="rememberMe">记住账号</el-checkbox>
            <el-link type="primary" @click="forgetDialogVisible = true">忘记密码？</el-link>
          </div>

          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            登录系统
          </el-button>
        </el-form>

        <div class="entry-footer">
          <div class="entry-tip">
            首次接入或没有账号？
            <button type="button" class="inline-action" @click="registerDialogVisible = true">
              申请开通
            </button>
          </div>
          <div class="entry-security">登录过程已启用安全校验与操作留痕。</div>
        </div>
      </div>
    </section>

    <el-dialog v-model="registerDialogVisible" title="注册新账号" width="420px">
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱，用于找回密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="forgetDialogVisible" title="找回密码" width="420px">
      <el-steps :active="forgetStep" finish-status="success" simple>
        <el-step title="验证身份" />
        <el-step title="重置密码" />
      </el-steps>

      <div v-if="forgetStep === 0" class="dialog-body">
        <el-form ref="forgetFormRef" :model="forgetForm" :rules="forgetRules">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="forgetForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="forgetForm.email" placeholder="请输入注册邮箱" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="sendCodeLoading" @click="sendVerifyCode">
              发送验证码
            </el-button>
          </el-form-item>
          <el-form-item label="验证码" prop="code">
            <el-input v-model="forgetForm.code" placeholder="请输入邮箱中的验证码" />
          </el-form-item>
        </el-form>
      </div>

      <div v-else class="dialog-body">
        <el-form ref="resetFormRef" :model="resetForm" :rules="resetRules">
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="resetForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="resetForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="forgetDialogVisible = false">取消</el-button>
        <el-button
          v-if="forgetStep === 0"
          type="primary"
          :loading="verifyLoading"
          @click="verifyCodeAndNext"
        >
          下一步
        </el-button>
        <el-button
          v-else
          type="primary"
          :loading="resetLoading"
          @click="resetPassword"
        >
          完成重置
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
defineOptions({
  name: 'LoginView'
})

import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Key, Lock, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const registerFormRef = ref()
const forgetFormRef = ref()
const resetFormRef = ref()

const loading = ref(false)
const registerLoading = ref(false)
const sendCodeLoading = ref(false)
const verifyLoading = ref(false)
const resetLoading = ref(false)

const rememberMe = ref(false)
const registerDialogVisible = ref(false)
const forgetDialogVisible = ref(false)
const forgetStep = ref(0)

const activeSlide = ref(0)
let slideTimer = null

const slides = [
  {
    title: 'Museum Exterior',
    background:
      'radial-gradient(circle at top left, rgba(212, 160, 23, 0.34), transparent 28%), linear-gradient(135deg, rgba(14, 35, 35, 0.88), rgba(34, 77, 77, 0.72))'
  },
  {
    title: 'Bronze Artifact',
    background:
      'radial-gradient(circle at 75% 20%, rgba(201, 130, 52, 0.36), transparent 24%), linear-gradient(135deg, rgba(17, 44, 44, 0.9), rgba(73, 54, 25, 0.78))'
  },
  {
    title: 'Cultural Detail',
    background:
      'radial-gradient(circle at bottom right, rgba(94, 133, 119, 0.34), transparent 30%), linear-gradient(135deg, rgba(18, 38, 38, 0.88), rgba(24, 58, 79, 0.74))'
  }
]

const form = reactive({
  username: '',
  password: '',
  captcha: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const forgetForm = reactive({
  username: '',
  email: '',
  code: ''
})

const resetForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

const captchaCode = ref('')
const captchaImage = ref('')

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入图形验证码', trigger: 'blur' }]
}

const validateRegisterPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const validateResetPassword = (rule, value, callback) => {
  if (value !== resetForm.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
    return
  }
  callback()
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateRegisterPassword, trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const forgetRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const resetRules = {
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateResetPassword, trigger: 'blur' }
  ]
}

const createCaptchaSvg = (code) => {
  const chars = code.split('')
  const noise = Array.from({ length: 6 }, (_, index) => {
    const x1 = 12 + index * 22
    const y1 = 10 + ((index * 7) % 26)
    const x2 = 110 + index * 18
    const y2 = 14 + ((index * 11) % 22)
    return `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" stroke="rgba(46,90,90,0.18)" stroke-width="1.2" />`
  }).join('')

  const circles = Array.from({ length: 10 }, (_, index) => {
    const cx = 12 + index * 16
    const cy = 8 + ((index * 9) % 36)
    const radius = 1.6 + (index % 3)
    return `<circle cx="${cx}" cy="${cy}" r="${radius}" fill="rgba(212,160,23,0.16)" />`
  }).join('')

  const text = chars.map((char, index) => {
    const rotate = [-12, 8, -6, 10][index % 4]
    const x = 24 + index * 34
    const y = 35 + (index % 2) * 4
    return `<text x="${x}" y="${y}" font-size="24" font-weight="700" fill="#2E5A5A" transform="rotate(${rotate} ${x} ${y})">${char}</text>`
  }).join('')

  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(`
    <svg xmlns="http://www.w3.org/2000/svg" width="156" height="52" viewBox="0 0 156 52">
      <rect width="156" height="52" rx="12" fill="#F7FAFA" />
      <rect x="1" y="1" width="154" height="50" rx="11" fill="none" stroke="rgba(46,90,90,0.14)" />
      ${noise}
      ${circles}
      ${text}
    </svg>
  `)}`
}

const refreshCaptcha = () => {
  const chars = '23456789ABCDEFGHJKLMNPQRSTUVWXYZ'
  captchaCode.value = Array.from({ length: 4 }, () => chars[Math.floor(Math.random() * chars.length)]).join('')
  captchaImage.value = createCaptchaSvg(captchaCode.value)
  form.captcha = ''
}

const startCarousel = () => {
  slideTimer = window.setInterval(() => {
    activeSlide.value = (activeSlide.value + 1) % slides.length
  }, 4200)
}

const handleLogin = async () => {
  await formRef.value.validate()

  if (form.captcha.trim().toUpperCase() !== captchaCode.value) {
    ElMessage.error('图形验证码不正确')
    refreshCaptcha()
    return
  }

  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    if (rememberMe.value) {
      localStorage.setItem('savedUsername', form.username)
      localStorage.setItem('rememberMe', 'true')
    } else {
      localStorage.removeItem('savedUsername')
      localStorage.setItem('rememberMe', 'false')
    }
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败')
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  await registerFormRef.value.validate()
  registerLoading.value = true
  try {
    await authApi.register({
      username: registerForm.username,
      password: registerForm.password,
      email: registerForm.email
    })
    ElMessage.success('注册成功，请登录')
    registerDialogVisible.value = false
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

const sendVerifyCode = async () => {
  await forgetFormRef.value.validate(['username', 'email'])
  sendCodeLoading.value = true
  try {
    await authApi.sendResetCode({
      username: forgetForm.username,
      email: forgetForm.email
    })
    ElMessage.success('验证码已发送至你的邮箱，请注意查收')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发送失败')
  } finally {
    sendCodeLoading.value = false
  }
}

const verifyCodeAndNext = async () => {
  await forgetFormRef.value.validate()
  verifyLoading.value = true
  try {
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

const resetPassword = async () => {
  await resetFormRef.value.validate()
  resetLoading.value = true
  try {
    await authApi.resetPassword({
      username: forgetForm.username,
      email: forgetForm.email,
      code: forgetForm.code,
      newPassword: resetForm.newPassword
    })
    ElMessage.success('密码重置成功，请重新登录')
    forgetDialogVisible.value = false
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

onMounted(() => {
  if (localStorage.getItem('rememberMe') === 'true') {
    const savedUsername = localStorage.getItem('savedUsername')
    if (savedUsername) {
      form.username = savedUsername
    }
    rememberMe.value = true
  }

  refreshCaptcha()
  startCarousel()
})

onBeforeUnmount(() => {
  if (slideTimer) {
    window.clearInterval(slideTimer)
  }
})
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: grid;
  grid-template-columns: 2fr 3fr;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.66), rgba(255, 255, 255, 0.8)),
    radial-gradient(circle at top left, rgba(212, 160, 23, 0.08), transparent 30%),
    var(--museum-bg-page);
  overflow: hidden;
}

.pattern-overlay {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at 12% 14%, rgba(212, 160, 23, 0.08), transparent 18%),
    radial-gradient(circle at 84% 16%, rgba(46, 90, 90, 0.08), transparent 22%),
    repeating-linear-gradient(
      135deg,
      transparent,
      transparent 26px,
      rgba(46, 90, 90, 0.02) 26px,
      rgba(46, 90, 90, 0.02) 28px
    );
}

.brand-panel,
.form-panel {
  position: relative;
  min-height: 100vh;
}

.brand-panel {
  overflow: hidden;
}

.brand-carousel,
.brand-mask {
  position: absolute;
  inset: 0;
}

.brand-slide {
  position: absolute;
  inset: 0;
  background-position: center;
  background-size: cover;
  opacity: 0;
  transform: scale(1.04);
  transition:
    opacity 600ms ease,
    transform 4200ms ease;
}

.brand-slide.active {
  opacity: 1;
  transform: scale(1);
}

.brand-mask {
  background:
    linear-gradient(180deg, rgba(11, 24, 24, 0.26), rgba(11, 24, 24, 0.76)),
    radial-gradient(circle at top left, rgba(212, 160, 23, 0.18), transparent 26%);
}

.brand-content {
  position: relative;
  z-index: 1;
  height: 100%;
  padding: 64px 56px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  color: #fff;
}

.brand-badge {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.18);
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.brand-content h1 {
  margin: 18px 0 10px;
  font-size: 40px;
  line-height: 1.2;
  color: #fff;
}

.brand-slogan {
  margin: 0;
  font-size: 20px;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.86);
}

.brand-description {
  max-width: 460px;
  margin: 16px 0 0;
  color: rgba(255, 255, 255, 0.72);
}

.brand-highlights {
  display: flex;
  gap: 16px;
  margin-top: 28px;
}

.highlight-card {
  min-width: 168px;
  padding: 16px 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(12px);
}

.highlight-label {
  display: block;
  color: rgba(255, 255, 255, 0.64);
  font-size: 12px;
}

.highlight-card strong {
  display: block;
  margin-top: 8px;
  font-size: 18px;
  font-weight: 600;
}

.carousel-dots {
  display: flex;
  gap: 8px;
  margin-top: 28px;
}

.dot {
  width: 10px;
  height: 10px;
  padding: 0;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.32);
  cursor: pointer;
}

.dot.active {
  width: 28px;
  border-radius: 999px;
  background: var(--museum-color-accent);
}

.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.form-shell {
  width: min(460px, 100%);
  padding: 40px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.76);
  box-shadow: 0 24px 56px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(22px);
}

:global(:root[data-theme-mode='dark']) .form-shell {
  background: rgba(31, 31, 31, 0.92);
  border-color: rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.32);
}

.form-header {
  margin-bottom: 24px;
}

.eyebrow {
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--museum-color-primary);
}

.form-header h2 {
  margin: 0;
  font-size: 28px;
  line-height: 1.3;
  color: var(--museum-text-strong);
}

.form-header p {
  margin: 10px 0 0;
  color: var(--museum-text-secondary);
}

.guide-alert {
  margin-bottom: 20px;
  border-radius: 14px;
}

.login-form {
  margin-top: 8px;
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 156px;
  gap: 12px;
  width: 100%;
}

.captcha-box {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: 1px solid var(--museum-border-default);
  border-radius: 14px;
  background: var(--museum-bg-card);
  cursor: pointer;
  overflow: hidden;
}

.captcha-box:hover {
  border-color: rgba(46, 90, 90, 0.28);
}

.captcha-image {
  width: 100%;
  height: 52px;
  display: block;
}

.form-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin: 4px 0 20px;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 15px;
}

.entry-footer {
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid var(--museum-border-light);
}

.entry-tip {
  color: var(--museum-text-secondary);
}

.inline-action {
  padding: 0;
  border: 0;
  background: transparent;
  color: var(--museum-color-primary);
  cursor: pointer;
  font: inherit;
}

.entry-security {
  margin-top: 8px;
  font-size: 12px;
  color: var(--museum-text-tertiary);
}

.dialog-body {
  margin-top: 16px;
}

@media (max-width: 1024px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .brand-panel {
    min-height: 42vh;
  }

  .brand-content {
    padding: 40px 32px;
  }

  .form-panel {
    min-height: auto;
    padding: 24px 20px 40px;
  }
}

@media (max-width: 768px) {
  .brand-content h1 {
    font-size: 30px;
  }

  .brand-slogan {
    font-size: 16px;
  }

  .brand-highlights {
    flex-direction: column;
  }

  .form-shell {
    padding: 28px 20px;
    border-radius: 20px;
  }

  .captcha-row {
    grid-template-columns: 1fr;
  }
}
</style>
