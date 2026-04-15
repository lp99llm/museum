<template>
  <div>
    <el-card>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleAssignRoles(row.id)">分配角色</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="current"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="dialogVisible" title="分配角色" width="30%">
      <el-select v-model="selectedRoles" multiple placeholder="请选择角色">
        <el-option v-for="role in roleList" :key="role.id" :label="role.roleName" :value="role.id" />
      </el-select>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserPage, getUserRoles, assignRoles } from '@/api/user'
import { getRolePage } from '@/api/role'

const current = ref(1)
const size = ref(10)
const total = ref(0)
const tableData = ref([])
const dialogVisible = ref(false)
const selectedRoles = ref([])
const currentUserId = ref(null)
const roleList = ref([])

const getList = async () => {
  const res = await getUserPage({ current: current.value, size: size.value })
  tableData.value = res.records
  total.value = res.total
}

const handleAssignRoles = async (userId) => {
  currentUserId.value = userId
  // 获取用户已有角色
  const userRoleIds = await getUserRoles(userId)
  selectedRoles.value = userRoleIds
  // 获取所有角色列表
  const roleRes = await getRolePage({ current: 1, size: 100 })
  roleList.value = roleRes.records
  dialogVisible.value = true
}

const saveRoles = async () => {
  await assignRoles(currentUserId.value, selectedRoles.value)
  ElMessage.success('分配成功')
  dialogVisible.value = false
}

onMounted(() => {
  getList()
})
</script>