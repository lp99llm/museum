<template>
  <div class="role-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑角色' : '新增角色' }}</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码，如: ADMIN" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
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
import { roleApi } from '@/api/role'

const router = useRouter()
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
    const res = await roleApi.getDetail(id)
    Object.assign(form, res.data)
  } catch (error) {
    ElMessage.error('获取角色信息失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (isEdit.value) {
      await roleApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await roleApi.create(form)
      ElMessage.success('创建成功')
    }
    router.push('/roles')
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const handleBack = () => {
  router.push('/roles')
}

onMounted(() => {
  const id = router.currentRoute.value.query.id
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
