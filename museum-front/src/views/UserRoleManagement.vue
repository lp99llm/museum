<template>
  <div class="system-page">
    <section class="system-hero page-card">
      <div>
        <div class="eyebrow">System Users</div>
        <h1>用户管理</h1>
        <p>统一维护账号、角色、状态与登录审计信息，当前已接通用户详情、最后登录信息、导出和用户日志接口。</p>
      </div>
      <div class="hero-metrics">
        <div class="hero-metric"><span>用户总数</span><strong>{{ pagination.total }}</strong></div>
        <div class="hero-metric"><span>当前启用</span><strong>{{ enabledCount }}</strong></div>
        <div class="hero-metric"><span>角色类型</span><strong>{{ roleOptions.length }}</strong></div>
      </div>
    </section>

    <section class="toolbar-card page-card">
      <div class="filters-grid">
        <el-input v-model="filters.keyword" placeholder="搜索用户名 / 真实姓名" clearable @keyup.enter="loadUsers(1)" />
        <el-select v-model="filters.status" placeholder="状态筛选" clearable>
          <el-option label="启用" value="启用" />
          <el-option label="禁用" value="禁用" />
        </el-select>
        <el-button v-permission="PERMISSIONS.USER_ADD" type="primary" @click="openCreate">新增用户</el-button>
        <el-button v-permission="PERMISSIONS.REPORT_EXPORT" @click="exportUsers">导出列表</el-button>
      </div>
    </section>

    <section class="table-card page-card">
      <el-table v-loading="loading" :data="filteredUsers" stripe>
        <el-table-column prop="username" label="用户名" min-width="130" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="department" label="所属部门" min-width="120" />
        <el-table-column prop="roleName" label="角色" min-width="150" />
        <el-table-column prop="statusLabel" label="账号状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.statusLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTimeLabel" label="最后登录时间" width="170" />
        <el-table-column prop="lastLoginIp" label="最后登录 IP" width="140" />
        <el-table-column prop="createdTimeLabel" label="创建时间" width="180" />
        <el-table-column label="操作" width="420" fixed="right">
          <template #default="{ row }">
            <el-button link @click="openDetail(row)">详情</el-button>
            <el-button v-permission="PERMISSIONS.USER_EDIT" link @click="openEdit(row)">编辑</el-button>
            <el-button v-permission="PERMISSIONS.USER_ASSIGN" link @click="openRoleAssign(row)">分配角色</el-button>
            <el-button v-permission="PERMISSIONS.USER_RESET_PASSWORD" link @click="handleResetPassword(row)">重置密码</el-button>
            <el-button v-permission="PERMISSIONS.USER_EDIT" link @click="toggleStatus(row)">{{ row.enabled ? '禁用' : '启用' }}</el-button>
            <el-button v-permission="PERMISSIONS.USER_DELETE" link type="danger" @click="removeUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadUsers"
          @size-change="handleSizeChange"
        />
      </div>
    </section>

    <el-dialog v-model="userDialogVisible" :title="editingUserId ? '编辑用户' : '新增用户'" width="760px">
      <el-form ref="formRef" :model="userForm" :rules="rules" label-position="top">
        <div class="dialog-grid">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userForm.username" :disabled="Boolean(editingUserId)" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="userForm.realName" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="userForm.phone" />
          </el-form-item>
          <el-form-item v-if="!editingUserId" label="初始密码" prop="password">
            <el-input v-model="userForm.password" type="password" show-password />
          </el-form-item>
          <el-form-item label="账号状态" prop="enabled">
            <el-select v-model="userForm.enabled">
              <el-option :value="true" label="启用" />
              <el-option :value="false" label="禁用" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="520px">
      <template v-if="selectedUser">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户名">{{ selectedUser.username }}</el-descriptions-item>
          <el-descriptions-item label="当前角色">{{ selectedUser.roleName }}</el-descriptions-item>
        </el-descriptions>

        <el-form label-position="top" style="margin-top: 16px;">
          <el-form-item label="选择角色">
            <el-select v-model="selectedRoleIds" multiple placeholder="请选择角色" style="width: 100%">
              <el-option
                v-for="item in roleOptions"
                :key="item.id"
                :label="item.roleName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </template>

      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleSaving" @click="saveRoles">保存角色</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="用户详情" size="42%">
      <template v-if="currentUser">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ currentUser.realName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属角色">{{ currentUser.roleName }}</el-descriptions-item>
          <el-descriptions-item label="账号状态">{{ currentUser.statusLabel }}</el-descriptions-item>
          <el-descriptions-item label="最后登录时间">{{ currentUser.lastLoginTimeLabel }}</el-descriptions-item>
          <el-descriptions-item label="最后登录 IP">{{ currentUser.lastLoginIp || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentUser.createdTimeLabel }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentUser.updatedTimeLabel }}</el-descriptions-item>
        </el-descriptions>

        <div class="drawer-section">
          <div class="section-title-row">
            <h3>操作日志</h3>
            <el-tag type="success" effect="plain">已接通真实日志接口</el-tag>
          </div>
          <div class="log-list">
            <div v-for="(item, index) in currentUserLogs" :key="`${item.time}-${index}`" class="log-item">
              <strong>{{ item.type || '操作记录' }}</strong>
              <span>{{ formatDateTime(item.time) }} / {{ item.operator || '-' }} / {{ item.result || '-' }}</span>
              <p>{{ item.content || '-' }}</p>
            </div>
            <el-empty v-if="!currentUserLogs.length" description="该用户暂无日志记录" />
          </div>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/user'
import { roleApi } from '@/api/role'
import { PERMISSIONS } from '@/constants/permissions'
import { downloadResponseBlob } from '@/utils/download'
import { formatDateTime } from '@/utils/format'

const formRef = ref(null)
const loading = ref(false)
const saving = ref(false)
const roleSaving = ref(false)

const userDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const detailVisible = ref(false)
const editingUserId = ref(null)
const selectedUser = ref(null)
const currentUser = ref(null)
const currentUserLogs = ref([])
const selectedRoleIds = ref([])

const filters = reactive({
  keyword: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const userList = ref([])
const roleOptions = ref([])

const userForm = reactive({
  id: null,
  username: '',
  realName: '',
  email: '',
  phone: '',
  password: '',
  enabled: true
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }]
}

const roleIdNameMap = computed(() => {
  const map = {}
  roleOptions.value.forEach((item) => {
    map[item.id] = item.roleName
  })
  return map
})

const decorateUser = (item) => {
  const roleName = item.role || '未分配角色'
  return {
    ...item,
    department: item.department || '-',
    roleName,
    statusLabel: item.enabled ? '启用' : '禁用',
    lastLoginTimeLabel: formatDateTime(item.lastLoginTime),
    lastLoginIp: item.lastLoginIp || '-',
    createdTimeLabel: formatDateTime(item.createdTime),
    updatedTimeLabel: formatDateTime(item.updatedTime)
  }
}

const filteredUsers = computed(() => userList.value.filter((item) => (filters.status ? item.statusLabel === filters.status : true)))
const enabledCount = computed(() => filteredUsers.value.filter((item) => item.enabled).length)

const resetForm = () => {
  editingUserId.value = null
  userForm.id = null
  userForm.username = ''
  userForm.realName = ''
  userForm.email = ''
  userForm.phone = ''
  userForm.password = ''
  userForm.enabled = true
}

const loadRoles = async () => {
  try {
    const res = await roleApi.getPage({ current: 1, size: 100 })
    roleOptions.value = res.records || []
  } catch (error) {
    ElMessage.error(error?.message || '加载角色列表失败')
  }
}

const loadUsers = async (page = pagination.current) => {
  loading.value = true
  pagination.current = page
  try {
    const res = await userApi.getPage({
      current: pagination.current,
      size: pagination.size,
      username: filters.keyword || undefined
    })
    userList.value = (res.records || []).map(decorateUser)
    pagination.total = res.total || 0
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadUsers(1)
}

const exportUsers = async () => {
  try {
    const res = await userApi.export({
      username: filters.keyword || undefined
    })
    downloadResponseBlob(res, 'users-export.csv')
    ElMessage.success('用户列表导出成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '导出用户列表失败')
  }
}

const openCreate = () => {
  resetForm()
  userDialogVisible.value = true
}

const openEdit = (row) => {
  editingUserId.value = row.id
  userForm.id = row.id
  userForm.username = row.username
  userForm.realName = row.realName || ''
  userForm.email = row.email || ''
  userForm.phone = row.phone || ''
  userForm.password = ''
  userForm.enabled = row.enabled
  userDialogVisible.value = true
}

const saveUser = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    if (editingUserId.value) {
      await userApi.update({
        id: userForm.id,
        username: userForm.username,
        realName: userForm.realName,
        email: userForm.email,
        phone: userForm.phone,
        enabled: userForm.enabled
      })
      ElMessage.success('用户信息已更新')
    } else {
      await userApi.create({
        username: userForm.username,
        realName: userForm.realName,
        email: userForm.email,
        phone: userForm.phone,
        password: userForm.password,
        enabled: userForm.enabled
      })
      ElMessage.success('用户已创建')
    }
    userDialogVisible.value = false
    resetForm()
    await loadUsers(editingUserId.value ? pagination.current : 1)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '保存用户失败')
  } finally {
    saving.value = false
  }
}

const openDetail = async (row) => {
  try {
    const [detail, logs] = await Promise.all([
      userApi.getDetail(row.id),
      userApi.getLogs(row.id).catch(() => [])
    ])
    currentUser.value = decorateUser(detail)
    currentUserLogs.value = logs || []
    detailVisible.value = true
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载用户详情失败')
  }
}

const openRoleAssign = async (row) => {
  selectedUser.value = row
  roleDialogVisible.value = true
  try {
    const roleIds = await userApi.getUserRoles(row.id)
    selectedRoleIds.value = roleIds || []
  } catch (error) {
    selectedRoleIds.value = []
    ElMessage.error(error?.response?.data?.message || '加载用户角色失败')
  }
}

const saveRoles = async () => {
  if (!selectedUser.value) return
  roleSaving.value = true
  try {
    await userApi.assignRoles(selectedUser.value.id, selectedRoleIds.value)
    const roleNames = selectedRoleIds.value
      .map((id) => roleIdNameMap.value[id])
      .filter(Boolean)
      .join('、') || '未分配角色'

    const target = userList.value.find((item) => item.id === selectedUser.value.id)
    if (target) {
      target.roleName = roleNames
      target.role = roleNames
    }

    ElMessage.success('角色分配已保存')
    roleDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '保存角色失败')
  } finally {
    roleSaving.value = false
  }
}

const handleResetPassword = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt(`请输入 ${row.username} 的新密码`, '重置密码', {
      confirmButtonText: '确认重置',
      cancelButtonText: '取消',
      inputType: 'password',
      inputPattern: /^.{6,}$/,
      inputErrorMessage: '密码至少 6 位'
    })
    await userApi.resetPassword(row.id, { newPassword: value })
    ElMessage.success('密码已重置')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '重置密码失败')
    }
  }
}

const toggleStatus = async (row) => {
  try {
    await userApi.update({
      id: row.id,
      username: row.username,
      realName: row.realName,
      email: row.email,
      phone: row.phone,
      enabled: !row.enabled
    })
    row.enabled = !row.enabled
    row.statusLabel = row.enabled ? '启用' : '禁用'
    ElMessage.success(`账号已${row.enabled ? '启用' : '禁用'}`)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '更新账号状态失败')
  }
}

const removeUser = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除用户“${row.username}”吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '确认删除',
      cancelButtonText: '取消'
    })
    await userApi.delete(row.id)
    ElMessage.success('用户已删除')
    await loadUsers(1)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '删除用户失败')
    }
  }
}

onMounted(async () => {
  await loadRoles()
  await loadUsers(1)
})
</script>

<style scoped>
.system-page {
  display: grid;
  gap: 24px;
}

.system-hero,
.toolbar-card,
.table-card {
  padding: 22px;
}

.system-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(280px, 0.9fr);
  gap: 24px;
  background:
    radial-gradient(circle at top right, rgba(212, 160, 23, 0.2), transparent 28%),
    linear-gradient(135deg, rgba(46, 90, 90, 0.12), rgba(46, 90, 90, 0.04));
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--museum-color-primary);
  font-weight: 600;
}

.system-hero h1 {
  margin: 12px 0 10px;
  color: var(--museum-text-strong);
}

.system-hero p {
  margin: 0;
  color: var(--museum-text-secondary);
  line-height: 1.8;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.hero-metric {
  padding: 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.76);
}

.hero-metric span {
  display: block;
  font-size: 12px;
  color: var(--museum-text-secondary);
}

.hero-metric strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: var(--museum-text-strong);
}

.filters-grid {
  display: grid;
  grid-template-columns: minmax(260px, 1fr) 180px auto auto;
  gap: 14px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.dialog-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.drawer-section {
  margin-top: 24px;
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.section-title-row h3 {
  margin: 0;
  font-size: 16px;
  color: var(--museum-text-strong);
}

.log-list {
  display: grid;
  gap: 12px;
}

.log-item {
  padding: 14px;
  border-radius: 14px;
  background: var(--museum-bg-subtle);
}

.log-item strong {
  color: var(--museum-text-strong);
}

.log-item span {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: var(--museum-text-tertiary);
}

.log-item p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

@media (max-width: 1100px) {
  .system-hero,
  .filters-grid,
  .dialog-grid,
  .hero-metrics {
    grid-template-columns: 1fr;
  }

  .pagination-wrap {
    justify-content: flex-start;
  }
}
</style>


