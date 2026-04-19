<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="用户名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleAssignRoles(row)">分配角色</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id">
          {{ role.roleName }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/user'
import { roleApi } from '@/api/role'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  current: 1,
  size: 10,
  username: ''
})

const roleDialogVisible = ref(false)
const allRoles = ref([])
const selectedRoleIds = ref([])
const currentUserId = ref(null)

const getList = async () => {
  loading.value = true
  try {
    const res = await userApi.getPage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const getAllRoles = async () => {
  try {
    const res = await roleApi.getPage({ current: 1, size: 100 })
    allRoles.value = res.data.records
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  }
}

const handleQuery = () => {
  queryParams.current = 1
  getList()
}

const resetQuery = () => {
  queryParams.username = ''
  queryParams.current = 1
  getList()
}

const handleAdd = () => {
  window.location.href = '/user-form'
}

const handleEdit = (row) => {
  window.location.href = `/user-form?id=${row.id}`
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await userApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleAssignRoles = async (row) => {
  currentUserId.value = row.id
  selectedRoleIds.value = []
  roleDialogVisible.value = true
  try {
    const res = await userApi.getUserRoles(row.id)
    selectedRoleIds.value = res.data || []
  } catch (error) {
    ElMessage.error('获取用户角色失败')
  }
}

const submitAssignRoles = async () => {
  try {
    await userApi.assignRoles(currentUserId.value, selectedRoleIds.value)
    ElMessage.success('分配角色成功')
    roleDialogVisible.value = false
  } catch (error) {
    ElMessage.error('分配角色失败')
  }
}

onMounted(() => {
  getList()
  getAllRoles()
})
</script>
