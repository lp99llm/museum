<template>
  <div class="artifact-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑文物' : '新增文物' }}</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="文物编号" prop="code">
              <el-input v-model="form.code" placeholder="请输入文物编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文物名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入文物名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类别" prop="category">
              <el-select v-model="form.category" placeholder="请选择类别" style="width: 100%">
                <el-option label="陶瓷" value="陶瓷" />
                <el-option label="书画" value="书画" />
                <el-option label="玉器" value="玉器" />
                <el-option label="青铜器" value="青铜器" />
                <el-option label="金银器" value="金银器" />
                <el-option label="铁器" value="铁器" />
                <el-option label="漆器" value="漆器" />
                <el-option label="织绣" value="织绣" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年代" prop="era">
              <el-input v-model="form.era" placeholder="如：唐代、清代" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="材质" prop="material">
              <el-input v-model="form.material" placeholder="请输入材质" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="尺寸" prop="size">
              <el-input v-model="form.size" placeholder="如：30cm×20cm×15cm" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="重量" prop="weight">
              <el-input v-model="form.weight" placeholder="请输入重量（克）" type="number" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源" prop="source">
              <el-input v-model="form.source" placeholder="请输入文物来源" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保存状况" prop="preservationStatus">
              <el-select v-model="form.preservationStatus" placeholder="请选择保存状况" style="width: 100%">
                <el-option label="完好" value="完好" />
                <el-option label="轻度破损" value="轻度破损" />
                <el-option label="中度破损" value="中度破损" />
                <el-option label="重度破损" value="重度破损" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="鉴定等级" prop="appraisalLevel">
              <el-select v-model="form.appraisalLevel" placeholder="请选择鉴定等级" style="width: 100%">
                <el-option label="一级文物" value="一级" />
                <el-option label="二级文物" value="二级" />
                <el-option label="三级文物" value="三级" />
                <el-option label="一般文物" value="一般" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前状态" prop="currentState">
              <el-select v-model="form.currentState" placeholder="请选择当前状态" style="width: 100%">
                <el-option label="在库" value="IN_STORAGE" />
                <el-option label="展览中" value="IN_EXHIBITION" />
                <el-option label="修复中" value="IN_RESTORATION" />
                <el-option label="外借中" value="ON_LOAN" />
                <el-option label="征集入库" value="ACQUIRED" />
                <el-option label="已处置" value="DISPOSED" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出土地点" prop="location">
              <el-input v-model="form.location" placeholder="请输入出土地点" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="现藏地点" prop="currentLocation">
              <el-input v-model="form.currentLocation" placeholder="请输入现藏地点" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保存环境要求" prop="environmentRequirements">
              <el-input v-model="form.environmentRequirements" placeholder="如：温度18-22℃，湿度45-55%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="文物图片" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入文物图片URL" />
        </el-form-item>

        <el-form-item label="保险信息" prop="insuranceInfo">
          <el-input v-model="form.insuranceInfo" placeholder="请输入保险信息" />
        </el-form-item>

        <el-form-item label="相关文献资料" prop="documents">
          <el-input type="textarea" v-model="form.documents" placeholder="请输入相关文献资料" rows="3" />
        </el-form-item>

        <el-form-item label="文物简介" prop="description">
          <el-input type="textarea" v-model="form.description" placeholder="请输入文物简介" rows="4" />
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
import { artifactApi } from '@/api/artifact'

const route = useRoute()
const router = useRouter()
const formRef = ref()

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  code: '',
  name: '',
  category: '',
  era: '',
  material: '',
  size: '',
  weight: null,
  source: '',
  preservationStatus: '',
  appraisalLevel: '',
  currentState: 'IN_STORAGE',
  imageUrl: '',
  documents: '',
  environmentRequirements: '',
  insuranceInfo: '',
  location: '',
  currentLocation: '',
  description: ''
})

const rules = {
  code: [{ required: true, message: '请输入文物编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入文物名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择类别', trigger: 'change' }]
}

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
      await artifactApi.update(form)
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
  max-width: 1200px;
  margin: 0 auto;
}
</style>
