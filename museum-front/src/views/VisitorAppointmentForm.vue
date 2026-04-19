<template>
  <div class="appointment-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑预约' : '新增预约' }}</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="参观者姓名" prop="visitorName">
              <el-input v-model="form.visitorName" placeholder="请输入参观者姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="visitorPhone">
              <el-input v-model="form.visitorPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="visitorIdCard">
              <el-input v-model="form.visitorIdCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约展览" prop="exhibitionId">
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
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约日期" prop="appointmentDate">
              <el-date-picker
                v-model="form.appointmentDate"
                type="date"
                placeholder="选择预约日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约时段" prop="appointmentTimeSlot">
              <el-select v-model="form.appointmentTimeSlot" placeholder="请选择时段" style="width: 100%">
                <el-option label="上午 (9:00-12:00)" value="上午 9:00-12:00" />
                <el-option label="下午 (14:00-17:00)" value="下午 14:00-17:00" />
                <el-option label="晚上 (18:00-21:00)" value="晚上 18:00-21:00" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="参观人数" prop="visitorCount">
              <el-input-number v-model="form.visitorCount" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注" rows="3" />
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
import { visitorAppointmentApi } from '@/api/visitorAppointment'
import { exhibitionApi } from '@/api/exhibition'

const route = useRoute()
const router = useRouter()
const formRef = ref()

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  visitorName: '',
  visitorPhone: '',
  visitorIdCard: '',
  exhibitionId: null,
  appointmentDate: '',
  appointmentTimeSlot: '',
  visitorCount: 1,
  remark: ''
})

const exhibitions = ref([])

const rules = {
  visitorName: [{ required: true, message: '请输入参观者姓名', trigger: 'blur' }],
  visitorPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  exhibitionId: [{ required: true, message: '请选择预约展览', trigger: 'change' }],
  appointmentDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }]
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
      const res = await visitorAppointmentApi.getDetail(route.params.id)
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
      await visitorAppointmentApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await visitorAppointmentApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/visitor-appointments')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const goBack = () => {
  router.push('/visitor-appointments')
}

onMounted(() => {
  loadExhibitions()
  loadData()
})
</script>

<style scoped>
.appointment-form {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
