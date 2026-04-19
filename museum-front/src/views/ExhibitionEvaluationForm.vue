<template>
  <div class="evaluation-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑评估报告' : '新增评估报告' }}</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="130px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="展览名称" prop="exhibitionId">
              <el-select v-model="form.exhibitionId" placeholder="请选择展览" style="width: 100%">
                <el-option
                  v-for="exhibition in exhibitions"
                  :key="exhibition.id"
                  :label="exhibition.name"
                  :value="exhibition.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评估日期" prop="evaluationDate">
              <el-date-picker
                v-model="form.evaluationDate"
                type="date"
                placeholder="选择评估日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="参观人数" prop="visitorCount">
              <el-input-number v-model="form.visitorCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="观众评分" prop="feedbackScore">
              <el-rate v-model="form.feedbackScore" show-score style="margin-top: 6px;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="安全事故数" prop="safetyIncidents">
              <el-input-number v-model="form.safetyIncidents" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文物损坏数" prop="artifactDamageCount">
              <el-input-number v-model="form.artifactDamageCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="实际支出" prop="budgetActual">
              <el-input-number v-model="form.budgetActual" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收入" prop="revenue">
              <el-input-number v-model="form.revenue" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="媒体曝光度" prop="mediaCoverage">
              <el-input v-model="form.mediaCoverage" placeholder="如：高度关注/一般/较少" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="社交媒体提及" prop="socialMediaMentions">
              <el-input-number v-model="form.socialMediaMentions" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="评估人" prop="evaluator">
          <el-input v-model="form.evaluator" placeholder="请输入评估人姓名" style="width: 50%" />
        </el-form-item>

        <el-form-item label="主要亮点" prop="majorHighlights">
          <el-input type="textarea" v-model="form.majorHighlights" placeholder="请输入本次展览的主要亮点" rows="3" />
        </el-form-item>

        <el-form-item label="存在的问题" prop="problems">
          <el-input type="textarea" v-model="form.problems" placeholder="请输入存在的问题" rows="3" />
        </el-form-item>

        <el-form-item label="改进建议" prop="improvementSuggestions">
          <el-input type="textarea" v-model="form.improvementSuggestions" placeholder="请输入改进建议" rows="3" />
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
import { exhibitionEvaluationApi } from '@/api/exhibitionEvaluation'
import { exhibitionApi } from '@/api/exhibition'

const route = useRoute()
const router = useRouter()
const formRef = ref()

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  exhibitionId: null,
  visitorCount: 0,
  feedbackScore: 5,
  safetyIncidents: 0,
  artifactDamageCount: 0,
  budgetActual: 0,
  revenue: 0,
  mediaCoverage: '',
  socialMediaMentions: 0,
  majorHighlights: '',
  problems: '',
  improvementSuggestions: '',
  evaluator: '',
  evaluationDate: ''
})

const exhibitions = ref([])

const rules = {
  exhibitionId: [{ required: true, message: '请选择展览', trigger: 'change' }],
  evaluationDate: [{ required: true, message: '请选择评估日期', trigger: 'change' }]
}

const loadExhibitions = async () => {
  try {
    const res = await exhibitionApi.getList({ current: 1, size: 100 })
    exhibitions.value = res.data.records || []
  } catch (error) {
    ElMessage.error('加载展览列表失败')
  }
}

const loadData = async () => {
  if (isEdit.value) {
    try {
      const res = await exhibitionEvaluationApi.getDetail(route.params.id)
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
      await exhibitionEvaluationApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await exhibitionEvaluationApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/exhibition-evaluations')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const goBack = () => {
  router.push('/exhibition-evaluations')
}

onMounted(() => {
  loadExhibitions()
  loadData()
})
</script>

<style scoped>
.evaluation-form {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
