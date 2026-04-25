<template>
  <div class="role-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑角色' : '新增角色' }}</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码，例如 ROLE_ADMIN" :disabled="isEdit" />
        </el-form-item>

        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入角色描述" />
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
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { roleApi } from '@/api/role'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const isEdit = computed(() => !!form.id)

const form = reactive({
  id: null,
  roleCode: '',
  roleName: '',
  description: ''
})

const rules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

const loadData = async (id) => {
  try {
    const data = await roleApi.getDetail(id)
    Object.assign(form, data)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '获取角色信息失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (isEdit.value) {
      await roleApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await roleApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/system/role')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || (isEdit.value ? '更新失败' : '创建失败'))
  }
}

const handleBack = () => {
  router.push('/system/role')
}

onMounted(() => {
  const id = route.query.id
  if (id) {
    loadData(id)
  }
})
</script>

<style scoped>
.role-form {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}
</style>
