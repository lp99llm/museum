import { permissionApi, roleApi } from '@/api/role'
import { PERMISSIONS } from '@/constants/permissions'

const ROLE_FALLBACK_PERMISSION_MAP = {
  ROLE_ADMIN: [PERMISSIONS.ALL],
  ROLE_ARTIFACT_ADMIN: [
    PERMISSIONS.ARTIFACT_VIEW,
    PERMISSIONS.ARTIFACT_ADD,
    PERMISSIONS.ARTIFACT_EDIT,
    PERMISSIONS.ARTIFACT_DELETE,
    PERMISSIONS.STATISTICS_VIEW,
    PERMISSIONS.REPORT_EXPORT,
    PERMISSIONS.ACQUISITION_VIEW,
    PERMISSIONS.ACQUISITION_ADD,
    PERMISSIONS.ACQUISITION_EDIT,
    PERMISSIONS.APPRAISAL_VIEW,
    PERMISSIONS.APPRAISAL_ADD,
    PERMISSIONS.APPRAISAL_EDIT,
    PERMISSIONS.WAREHOUSING_VIEW,
    PERMISSIONS.WAREHOUSING_ADD,
    PERMISSIONS.WAREHOUSING_EDIT,
    PERMISSIONS.OUTBOUND_VIEW
  ],
  ROLE_EXHIBITION_ADMIN: [
    PERMISSIONS.EXHIBITION_VIEW,
    PERMISSIONS.EXHIBITION_ADD,
    PERMISSIONS.EXHIBITION_EDIT,
    PERMISSIONS.EXHIBITION_DELETE,
    PERMISSIONS.EXHIBITION_EVALUATION_VIEW,
    PERMISSIONS.EXHIBITION_EVALUATION_ADD,
    PERMISSIONS.EXHIBITION_EVALUATION_EDIT,
    PERMISSIONS.EXHIBITION_EVALUATION_DELETE,
    PERMISSIONS.VISITOR_APPOINTMENT_VIEW,
    PERMISSIONS.VISITOR_APPOINTMENT_ADD,
    PERMISSIONS.VISITOR_APPOINTMENT_EDIT,
    PERMISSIONS.VISITOR_APPOINTMENT_APPROVE,
    PERMISSIONS.VISITOR_APPOINTMENT_DELETE,
    PERMISSIONS.STATISTICS_VIEW,
    PERMISSIONS.REPORT_EXPORT,
    PERMISSIONS.OUTBOUND_VIEW,
    PERMISSIONS.OUTBOUND_ADD,
    PERMISSIONS.OUTBOUND_EDIT,
    PERMISSIONS.OUTBOUND_APPROVE,
    PERMISSIONS.OUTBOUND_RETURN,
    PERMISSIONS.OUTBOUND_DELETE
  ],
  ROLE_RESTORATION_STAFF: [
    PERMISSIONS.RESTORATION_VIEW,
    PERMISSIONS.RESTORATION_ADD,
    PERMISSIONS.RESTORATION_EDIT,
    PERMISSIONS.RESTORATION_APPROVE,
    PERMISSIONS.RESTORATION_DELETE,
    PERMISSIONS.STATISTICS_VIEW,
    PERMISSIONS.LOAN_VIEW
  ],
  ROLE_PROCESS_ADMIN: [
    PERMISSIONS.STATISTICS_VIEW,
    PERMISSIONS.REPORT_EXPORT,
    PERMISSIONS.ACQUISITION_VIEW,
    PERMISSIONS.APPRAISAL_VIEW,
    PERMISSIONS.WAREHOUSING_VIEW,
    PERMISSIONS.OUTBOUND_VIEW,
    PERMISSIONS.OUTBOUND_ADD,
    PERMISSIONS.OUTBOUND_EDIT,
    PERMISSIONS.OUTBOUND_APPROVE,
    PERMISSIONS.OUTBOUND_RETURN,
    PERMISSIONS.OUTBOUND_DELETE,
    PERMISSIONS.RESTORATION_VIEW,
    PERMISSIONS.RESTORATION_ADD,
    PERMISSIONS.RESTORATION_EDIT,
    PERMISSIONS.RESTORATION_APPROVE,
    PERMISSIONS.RESTORATION_DELETE,
    PERMISSIONS.LOAN_VIEW,
    PERMISSIONS.LOAN_ADD,
    PERMISSIONS.LOAN_EDIT,
    PERMISSIONS.LOAN_APPROVE,
    PERMISSIONS.LOAN_RETURN,
    PERMISSIONS.LOAN_DELETE,
    PERMISSIONS.DISPOSAL_VIEW,
    PERMISSIONS.DISPOSAL_ADD,
    PERMISSIONS.DISPOSAL_EDIT,
    PERMISSIONS.DISPOSAL_APPROVE,
    PERMISSIONS.DISPOSAL_DELETE,
    PERMISSIONS.VISITOR_APPOINTMENT_VIEW,
    PERMISSIONS.VISITOR_APPOINTMENT_APPROVE,
    PERMISSIONS.EXHIBITION_EVALUATION_VIEW
  ],
  ROLE_SYSTEM_ADMIN: [
    PERMISSIONS.USER_VIEW,
    PERMISSIONS.USER_ADD,
    PERMISSIONS.USER_EDIT,
    PERMISSIONS.USER_DELETE,
    PERMISSIONS.USER_ASSIGN,
    PERMISSIONS.ROLE_VIEW,
    PERMISSIONS.ROLE_ADD,
    PERMISSIONS.ROLE_EDIT,
    PERMISSIONS.ROLE_DELETE,
    PERMISSIONS.ROLE_ASSIGN,
    PERMISSIONS.PERMISSION_VIEW,
    PERMISSIONS.PERMISSION_EDIT,
    PERMISSIONS.STATISTICS_VIEW,
    PERMISSIONS.REPORT_EXPORT,
    PERMISSIONS.OPERATION_LOG_VIEW,
    PERMISSIONS.SECURITY_VIEW
  ]
}

const getStoredUser = () => {
  try {
    return JSON.parse(localStorage.getItem('user') || 'null')
  } catch {
    return null
  }
}

const setStoredUser = (user) => {
  localStorage.setItem('user', JSON.stringify(user))
}

const getRoleCodes = (user = getStoredUser()) =>
  String(user?.roleCode || '')
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)

const buildFallbackPermissions = (roleCodes) => {
  const permissions = new Set()
  roleCodes.forEach((roleCode) => {
    ;(ROLE_FALLBACK_PERMISSION_MAP[roleCode] || []).forEach((permission) => {
      permissions.add(permission)
    })
  })
  return Array.from(permissions)
}

export const getStoredPermissions = () => {
  const user = getStoredUser()
  return Array.isArray(user?.permissions) ? user.permissions : []
}

export const hasPermission = (required, mode = 'some') => {
  if (!required) return true

  const permissions = getStoredPermissions()
  if (permissions.includes(PERMISSIONS.ALL)) return true

  const requiredList = Array.isArray(required) ? required : [required]
  if (!requiredList.length) return true

  const matcher = (permission) =>
    permissions.includes(permission) ||
    permissions.some((owned) => owned.endsWith(':*') && permission.startsWith(owned.replace('*', '')))

  return mode === 'every' ? requiredList.every(matcher) : requiredList.some(matcher)
}

export const initializePermissions = async (force = false) => {
  const user = getStoredUser()
  if (!user) return []

  if (!force && Array.isArray(user.permissions) && user.permissions.length) {
    return user.permissions
  }

  const roleCodes = getRoleCodes(user)
  let permissions = []

  try {
    if (roleCodes.includes('ROLE_ADMIN')) {
      permissions = [PERMISSIONS.ALL]
    } else {
      const [rolePage, permissionList] = await Promise.all([
        roleApi.getPage({ current: 1, size: 200 }),
        permissionApi.getList()
      ])

      const roles = (rolePage.records || []).filter((item) => roleCodes.includes(item.roleCode))
      const permissionMap = new Map((permissionList || []).map((item) => [item.id, item.permCode]))
      const permissionIdGroups = await Promise.all(
        roles.map((role) => roleApi.getPermissions(role.id).catch(() => []))
      )

      permissions = Array.from(
        new Set(permissionIdGroups.flat().map((id) => permissionMap.get(id)).filter(Boolean))
      )
    }
  } catch {
    // Fall back to role-based defaults below when remote permission loading fails.
  }

  if (!permissions.length) {
    permissions = buildFallbackPermissions(roleCodes)
  }

  setStoredUser({ ...user, permissions })
  return permissions
}

export const clearPermissions = () => {
  const user = getStoredUser()
  if (!user) return
  const nextUser = { ...user }
  delete nextUser.permissions
  setStoredUser(nextUser)
}
