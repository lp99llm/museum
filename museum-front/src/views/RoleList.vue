<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="角色名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleAdd">新增角色</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 16px">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleAssignPerms(row)">分配权限</el-button>
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

    <el-dialog v-model="permDialogVisible" title="分配权限" width="600px">
      <el-tree
        ref="permTreeRef"
        :data="permissionTree"
        :props="{ label: 'permName', children: 'children' }"
        node-key="id"
        :default-expand-all="true"
        show-checkbox
        :check-strictly="false"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignPerms">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { roleApi, permissionApi } from '@/api/role'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  current: 1,
  size: 10,
  roleName: ''
})

const permDialogVisible = ref(false)
const permissionTree = ref([])
const selectedPermIds = ref([])
const permTreeRef = ref(null)
const currentRoleId = ref(null)

const getList = async () => {
  loading.value = true
  try {
    const res = await roleApi.getPage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

const getPermissionTree = async () => {
  try {
    const res = await permissionApi.getTree()
    permissionTree.value = res.data || []
  } catch (error) {
    ElMessage.error('获取权限树失败')
  }
}

const handleQuery = () => {
  queryParams.current = 1
  getList()
}

const resetQuery = () => {
  queryParams.roleName = ''
  queryParams.current = 1
  getList()
}

const handleAdd = () => {
  window.location.href = '/role-form'
}

const handleEdit = (row) => {
  window.location.href = `/role-form?id=${row.id}`
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      type: 'warning'
    })
    await roleApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleAssignPerms = async (row) => {
  currentRoleId.value = row.id
  permDialogVisible.value = true
  selectedPermIds.value = []
  try {
    const res = await roleApi.getPermissions(row.id)
    selectedPermIds.value = res.data || []
    if (permTreeRef.value) {
      permTreeRef.value.setCheckedKeys(selectedPermIds.value, false)
    }
  } catch (error) {
    ElMessage.error('获取角色权限失败')
  }
}

const submitAssignPerms = async () => {
  const checkedKeys = permTreeRef.value.getCheckedKeys(false)
  try {
    await roleApi.assignPermissions(currentRoleId.value, checkedKeys)
    ElMessage.success('分配权限成功')
    permDialogVisible.value = false
  } catch (error) {
    ElMessage.error('分配权限失败')
  }
}

onMounted(() => {
  getList()
  getPermissionTree()
})
</script>
