import { PERMISSIONS } from '@/constants/permissions'

export const PROCESS_CENTER_PERMISSIONS = [
  PERMISSIONS.ACQUISITION_VIEW,
  PERMISSIONS.APPRAISAL_VIEW,
  PERMISSIONS.WAREHOUSING_VIEW,
  PERMISSIONS.OUTBOUND_VIEW,
  PERMISSIONS.RESTORATION_VIEW,
  PERMISSIONS.LOAN_VIEW,
  PERMISSIONS.DISPOSAL_VIEW
]

export const SIDEBAR_NAV_ITEMS = [
  {
    key: 'dashboard',
    index: '/dashboard',
    title: '数据统计',
    icon: 'DataAnalysis'
  },
  {
    key: 'artifacts',
    index: '/artifacts',
    title: '文物管理',
    icon: 'Collection',
    permission: PERMISSIONS.ARTIFACT_VIEW
  },
  {
    key: 'workflow',
    index: 'workflow',
    title: '流程管理',
    icon: 'Operation',
    permission: PROCESS_CENTER_PERMISSIONS,
    children: [
      { key: 'acquisition', index: '/acquisition/list', title: '征集登记', permission: PERMISSIONS.ACQUISITION_VIEW },
      { key: 'appraisal', index: '/appraisal/list', title: '鉴定评估', permission: PERMISSIONS.APPRAISAL_VIEW },
      { key: 'warehousing', index: '/warehousing/list', title: '入库管理', permission: PERMISSIONS.WAREHOUSING_VIEW },
      { key: 'storage-check', index: '/storage-check/list', title: '在库保管', permission: PERMISSIONS.ARTIFACT_VIEW },
      { key: 'outbound', index: '/outbound/list', title: '展览出库', permission: PERMISSIONS.OUTBOUND_VIEW },
      { key: 'restoration', index: '/restoration/list', title: '修复记录', permission: PERMISSIONS.RESTORATION_VIEW },
      { key: 'loan', index: '/loan/list', title: '外借管理', permission: PERMISSIONS.LOAN_VIEW },
      { key: 'disposal', index: '/disposal/list', title: '出馆处置', permission: PERMISSIONS.DISPOSAL_VIEW }
    ]
  },
  {
    key: 'exhibitions',
    index: '/exhibitions',
    title: '展览管理',
    icon: 'Box',
    permission: PERMISSIONS.EXHIBITION_VIEW
  },
  {
    key: 'visitor-appointments',
    index: '/visitor-appointments',
    title: '预约管理',
    icon: 'Calendar',
    permission: PERMISSIONS.VISITOR_APPOINTMENT_VIEW
  },
  {
    key: 'exhibition-evaluations',
    index: '/exhibition-evaluations',
    title: '评估报告',
    icon: 'Document',
    permission: PERMISSIONS.EXHIBITION_EVALUATION_VIEW
  },
  {
    key: 'users',
    index: '/users',
    title: '用户管理',
    icon: 'User',
    permission: PERMISSIONS.USER_VIEW
  },
  {
    key: 'roles',
    index: '/roles',
    title: '角色管理',
    icon: 'List',
    permission: PERMISSIONS.ROLE_VIEW
  }
]
