<template>
  <div class="process-form-page">
    <section class="process-form-header">
      <div>
        <div class="eyebrow">Exhibition Form</div>
        <h1>{{ isEdit ? '编辑展览' : '新增展览' }}</h1>
        <p>维护展览主视觉、时间地点、策展信息与简介内容，保存后即可进入展览详情继续维护展品与资料。</p>
      </div>
      <div class="process-form-kpis">
        <div class="process-form-kpi">
          <span>当前阶段</span>
          <strong>{{ isEdit ? '信息更新' : '展览建档' }}</strong>
        </div>
        <div class="process-form-kpi">
          <span>默认状态</span>
          <strong>{{ statusLabel }}</strong>
        </div>
        <div class="process-form-kpi">
          <span>主视觉</span>
          <strong>{{ form.posterUrl ? '已设置' : '待补充' }}</strong>
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
                  <h3>展览基本信息</h3>
                  <p>名称、主题、策展人、状态与主视觉图</p>
                </div>
              </div>
            </template>

            <div class="process-form-grid">
              <el-form-item label="展览名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入展览名称" />
              </el-form-item>
              <el-form-item label="展览主题" prop="theme">
                <el-input v-model="form.theme" placeholder="请输入展览主题" />
              </el-form-item>
              <el-form-item label="策展人" prop="curator">
                <el-input v-model="form.curator" placeholder="请输入策展人姓名" />
              </el-form-item>
              <el-form-item label="展览状态" prop="status">
                <el-select v-model="form.status" placeholder="请选择展览状态">
                  <el-option label="策划中" value="PLANNING" />
                  <el-option label="进行中" value="ONGOING" />
                  <el-option label="已结束" value="FINISHED" />
                </el-select>
              </el-form-item>
              <el-form-item label="主视觉图链接" prop="posterUrl" class="process-form-span-2">
                <el-input v-model="form.posterUrl" placeholder="请输入封面图片 URL" />
              </el-form-item>
            </div>

            <div v-if="form.posterUrl" class="poster-preview">
              <img :src="form.posterUrl" alt="展览主视觉预览">
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>时间与场地</h3>
                  <p>配置开展周期、场地与展厅位置</p>
                </div>
              </div>
            </template>

            <div class="process-form-grid">
              <el-form-item label="开始日期" prop="startDate">
                <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
              </el-form-item>
              <el-form-item label="结束日期" prop="endDate">
                <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
              </el-form-item>
              <el-form-item label="展览场地" prop="venue" class="process-form-span-2">
                <el-input v-model="form.venue" placeholder="如：主展厅 A / 临展厅 2F" />
              </el-form-item>
            </div>
          </el-card>

          <el-card shadow="never" class="process-form-card">
            <template #header>
              <div class="process-form-section-title">
                <div>
                  <h3>展览简介</h3>
                  <p>用于列表摘要、详情页简介与运营传播文案</p>
                </div>
              </div>
            </template>

            <div class="process-form-grid single">
              <el-form-item label="简介内容" prop="description">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="8"
                  placeholder="请输入展览简介"
                />
              </el-form-item>
            </div>

            <div class="process-form-actions">
              <el-button type="primary" :loading="submitting" @click="handleSubmit">
                {{ isEdit ? '保存展览修改' : '创建展览' }}
              </el-button>
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
                <h3>填写提醒</h3>
                <p>确保展览主信息足够支撑后续展品和资料维护</p>
              </div>
            </div>
          </template>
          <div class="process-form-side-list">
            <div class="process-form-side-item">
              <h4>时间闭环</h4>
              <p>开始日期和结束日期将直接影响排期视图、状态判断和统计口径。</p>
            </div>
            <div class="process-form-side-item">
              <h4>主视觉建议</h4>
              <p>优先使用横幅比例主图，详情页与列表卡片的展示效果会更稳定。</p>
            </div>
            <div class="process-form-side-item">
              <h4>保存后下一步</h4>
              <p>创建成功后可进入详情页继续添加参展文物、宣传资料和查看预约统计。</p>
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
import { exhibitionApi } from '@/api/exhibition'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => Boolean(route.params.id))

const form = reactive({
  id: null,
  name: '',
  theme: '',
  curator: '',
  venue: '',
  startDate: '',
  endDate: '',
  status: 'PLANNING',
  description: '',
  posterUrl: ''
})

const rules = {
  name: [{ required: true, message: '请输入展览名称', trigger: 'blur' }],
  curator: [{ required: true, message: '请输入策展人', trigger: 'blur' }],
  venue: [{ required: true, message: '请输入展览场地', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择展览状态', trigger: 'change' }]
}

const statusLabel = computed(() => {
  const map = {
    PLANNING: '策划中',
    ONGOING: '进行中',
    FINISHED: '已结束'
  }
  return map[form.status] || form.status
})

const loadDetail = async () => {
  if (!isEdit.value) return
  try {
    const detail = await exhibitionApi.getDetail(route.params.id)
    Object.assign(form, {
      id: detail.id,
      name: detail.name || '',
      theme: detail.theme || '',
      curator: detail.curator || '',
      venue: detail.venue || '',
      startDate: detail.startDate || '',
      endDate: detail.endDate || '',
      status: detail.status || 'PLANNING',
      description: detail.description || '',
      posterUrl: detail.posterUrl || ''
    })
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载展览信息失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = { ...form }
    if (isEdit.value) {
      await exhibitionApi.update(payload)
      ElMessage.success('展览已更新')
      router.push(`/exhibitions/${form.id}`)
    } else {
      const res = await exhibitionApi.create(payload)
      const newId = res?.id || res?.data?.id || form.id
      ElMessage.success('展览已创建')
      router.push(newId ? `/exhibitions/${newId}` : '/exhibitions')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || (isEdit.value ? '更新展览失败' : '创建展览失败'))
  } finally {
    submitting.value = false
  }
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.poster-preview {
  margin-top: 18px;
}

.poster-preview img {
  width: 100%;
  max-height: 320px;
  object-fit: cover;
  display: block;
  border-radius: 18px;
  border: 1px solid var(--museum-border-light);
}
</style>
