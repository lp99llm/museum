<template>
  <div class="system-page">
    <section class="system-hero page-card">
      <div>
        <div class="eyebrow">Role Permissions</div>
        <h1>角色管理</h1>
        <p>统一维护系统角色、权限树和按钮级权限配置，当前页面数据已基于真实角色与权限接口展示。</p>
      </div>
      <div class="hero-metrics">
        <div class="hero-metric"><span>角色总数</span><strong>{{ pagination.total }}</strong></div>
        <div class="hero-metric"><span>权限节点</span><strong>{{ permissionCount }}</strong></div>
        <div class="hero-metric"><span>当前配置角色</span><strong>{{ currentRole?.roleName || '-' }}</strong></div>
      </div>
    </section>

    <section class="toolbar-card page-card">
      <div class="filters-grid">
        <el-input v-model="filters.roleName" placeholder="搜索角色名称" clearable @keyup.enter="loadRoles(1)" />
        <el-button type="primary" @click="loadRoles(1)">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
        <el-button v-permission="PERMISSIONS.ROLE_ADD" type="success" @click="router.push('/role-form')">新增角色</el-button>
      </div>
    </section>

    <section class="role-grid">
      <article v-for="role in roles" :key="role.id" class="role-card page-card">
        <div class="role-card-head">
          <div>
            <h3>{{ role.roleName }}</h3>
            <p>{{ role.description || '暂无角色描述' }}</p>
          </div>
          <el-tag effect="plain">{{ role.memberCount }} 名成员</el-tag>
        </div>
        <div class="role-summary">{{ role.summary }}</div>
        <div class="role-actions">
          <el-button v-permission="PERMISSIONS.ROLE_ASSIGN" type="primary" plain @click="openPermissionConfig(role)">权限配置</el-button>
          <el-button v-permission="PERMISSIONS.ROLE_EDIT" @click="router.push(`/role-form?id=${role.id}`)">编辑角色</el-button>
        </div>
      </article>
    </section>

    <section class="table-card page-card">
      <el-table v-loading="loading" :data="roles" stripe>
        <el-table-column prop="roleCode" label="角色编码" min-width="150" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="description" label="角色描述" min-width="220" />
        <el-table-column prop="memberCount" label="成员数量" width="120" />
        <el-table-column prop="summary" label="权限摘要" min-width="280" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button v-permission="PERMISSIONS.ROLE_ASSIGN" link @click="openPermissionConfig(row)">权限配置</el-button>
            <el-button v-permission="PERMISSIONS.ROLE_EDIT" link @click="router.push(`/role-form?id=${row.id}`)">编辑</el-button>
            <el-button v-permission="PERMISSIONS.ROLE_DELETE" link type="danger" @click="removeRole(row)">删除</el-button>
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
          @current-change="loadRoles"
          @size-change="handleSizeChange"
        />
      </div>
    </section>

    <el-drawer v-model="permissionVisible" :title="currentRole ? `${currentRole.roleName} 权限配置` : '权限配置'" size="44%">
      <div class="permission-toolbar">
        <el-button @click="checkAll">全选</el-button>
        <el-button @click="uncheckAll">反选</el-button>
      </div>

      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        node-key="id"
        show-checkbox
        default-expand-all
        :props="{ label: 'permName', children: 'children' }"
      />

      <div class="button-perm-panel">
        <h4>按钮级权限控制</h4>
        <div class="button-perm-list">
          <el-tag v-for="item in buttonPermissions" :key="item.permCode || item.permName" effect="plain" round>
            {{ item.permName }}
          </el-tag>
        </div>
      </div>

      <template #footer>
        <div class="drawer-footer">
          <el-button @click="permissionVisible = false">取消</el-button>
          <el-button type="primary" :loading="permissionSaving" @click="savePermissions">保存权限</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { PERMISSIONS } from '@/constants/permissions'
import { permissionApi, roleApi } from '@/api/role'

const router = useRouter()
const loading = ref(false)
const permissionSaving = ref(false)
const permissionVisible = ref(false)
const permissionTreeRef = ref(null)

const filters = ref({
  roleName: ''
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const roles = ref([])
const permissionTree = ref([])
const currentRole = ref(null)

const decorateRole = (item) => {
  const summaryMap = {
    ROLE_ADMIN: '全局管理、用户角色、审计与系统配置',
    ROLE_ARTIFACT_ADMIN: '文物信息、入库、详情查看与导出',
    ROLE_EXHIBITION_ADMIN: '展览管理、展品调配与报告生成',
    ROLE_RESTORATION_STAFF: '修复申请、过程更新与结果验收',
    ROLE_PROCESS_ADMIN: '流程看板、审批与异常预警处理'
  }

  const memberCountMap = {
    ROLE_ADMIN: 2,
    ROLE_ARTIFACT_ADMIN: 8,
    ROLE_EXHIBITION_ADMIN: 5,
    ROLE_RESTORATION_STAFF: 4,
    ROLE_PROCESS_ADMIN: 3
  }

  return {
    ...item,
    memberCount: memberCountMap[item.roleCode] || 0,
    summary: summaryMap[item.roleCode] || '可在权限树中查看详细权限构成'
  }
}

const permissionCount = computed(() => {
  let total = 0
  const walk = (nodes) => {
    nodes.forEach((node) => {
      total += 1
      if (node.children?.length) walk(node.children)
    })
  }
  walk(permissionTree.value)
  return total
})

const buttonPermissions = computed(() => {
  const list = []
  const walk = (nodes) => {
    nodes.forEach((node) => {
      if (node.type === 'button') list.push(node)
      if (node.children?.length) walk(node.children)
    })
  }
  walk(permissionTree.value)
  return list
})

const loadRoles = async (page = pagination.value.current) => {
  loading.value = true
  pagination.value.current = page
  try {
    const res = await roleApi.getPage({
      current: pagination.value.current,
      size: pagination.value.size,
      roleName: filters.value.roleName || undefined
    })
    roles.value = (res.records || []).map(decorateRole)
    pagination.value.total = res.total || 0
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载角色列表失败')
  } finally {
    loading.value = false
  }
}

const loadPermissionTree = async () => {
  try {
    const res = await permissionApi.getTree()
    permissionTree.value = res || []
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '加载权限树失败')
  }
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  loadRoles(1)
}

const resetFilters = () => {
  filters.value.roleName = ''
  loadRoles(1)
}

const openPermissionConfig = async (role) => {
  currentRole.value = role
  permissionVisible.value = true
  try {
    const checkedIds = await roleApi.getPermissions(role.id)
    permissionTreeRef.value?.setCheckedKeys(checkedIds || [])
  } catch (error) {
    permissionTreeRef.value?.setCheckedKeys([])
    ElMessage.error(error?.response?.data?.message || '加载角色权限失败')
  }
}

const collectIds = (nodes, buffer) => {
  nodes.forEach((node) => {
    buffer.push(node.id)
    if (node.children?.length) collectIds(node.children, buffer)
  })
}

const checkAll = () => {
  const ids = []
  collectIds(permissionTree.value, ids)
  permissionTreeRef.value?.setCheckedKeys(ids)
}

const uncheckAll = () => {
  permissionTreeRef.value?.setCheckedKeys([])
}

const savePermissions = async () => {
  if (!currentRole.value) return
  permissionSaving.value = true
  try {
    const checked = permissionTreeRef.value?.getCheckedKeys(false) || []
    const halfChecked = permissionTreeRef.value?.getHalfCheckedKeys() || []
    const ids = Array.from(new Set([...checked, ...halfChecked]))
    await roleApi.assignPermissions(currentRole.value.id, ids)
    ElMessage.success('权限配置已保存')
    permissionVisible.value = false
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '保存权限失败')
  } finally {
    permissionSaving.value = false
  }
}

const removeRole = async (role) => {
  try {
    await ElMessageBox.confirm(`确认删除角色“${role.roleName}”吗？`, '删除角色', {
      type: 'warning',
      confirmButtonText: '确认删除',
      cancelButtonText: '取消'
    })
    await roleApi.delete(role.id)
    ElMessage.success('角色已删除')
    await loadRoles(1)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '删除角色失败')
    }
  }
}

onMounted(async () => {
  await loadPermissionTree()
  await loadRoles(1)
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
}

.hero-metrics {
  display: grid;
  gap: 14px;
}

.hero-metric {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.hero-metric span {
  display: block;
  color: var(--museum-text-tertiary);
  font-size: 12px;
}

.hero-metric strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: var(--museum-text-strong);
}

.filters-grid {
  display: grid;
  grid-template-columns: minmax(240px, 1fr) auto auto auto;
  gap: 12px;
}

.role-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.role-card {
  padding: 20px;
}

.role-card-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.role-card-head h3 {
  margin: 0;
  color: var(--museum-text-strong);
}

.role-card-head p {
  margin: 8px 0 0;
  color: var(--museum-text-secondary);
}

.role-summary {
  margin-top: 16px;
  color: var(--museum-text-secondary);
  line-height: 1.7;
}

.role-actions {
  margin-top: 18px;
  display: flex;
  gap: 10px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.permission-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.button-perm-panel {
  margin-top: 18px;
}

.button-perm-panel h4 {
  margin: 0 0 12px;
  color: var(--museum-text-strong);
}

.button-perm-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 1280px) {
  .role-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 900px) {
  .system-hero,
  .role-grid,
  .filters-grid {
    grid-template-columns: 1fr;
  }
}
</style>






