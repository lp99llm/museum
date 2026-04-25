import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { PERMISSIONS } from '@/constants/permissions'
import { PROCESS_CENTER_PERMISSIONS } from '@/config/navigation'
import { hasPermission, initializePermissions } from '@/utils/permission'

const Layout = () => import('../layout/Layout.vue')
const Login = () => import('../views/Login.vue')
const Dashboard = () => import('../views/Dashboard.vue')
const ArtifactList = () => import('../views/ArtifactList.vue')
const ArtifactForm = () => import('../views/ArtifactForm.vue')
const ArtifactDetail = () => import('../views/ArtifactDetail.vue')
const AcquisitionList = () => import('../views/AcquisitionList.vue')
const AcquisitionForm = () => import('../views/AcquisitionForm.vue')
const AppraisalList = () => import('../views/AppraisalList.vue')
const AppraisalForm = () => import('../views/AppraisalForm.vue')
const WarehousingList = () => import('../views/WarehousingList.vue')
const WarehousingForm = () => import('../views/WarehousingForm.vue')
const StorageCheckList = () => import('../views/StorageCheckList.vue')
const StorageCheckForm = () => import('../views/StorageCheckForm.vue')
const OutboundList = () => import('../views/OutboundList.vue')
const OutboundForm = () => import('../views/OutboundForm.vue')
const LoanList = () => import('../views/LoanList.vue')
const LoanForm = () => import('../views/LoanForm.vue')
const DisposalList = () => import('../views/DisposalList.vue')
const DisposalForm = () => import('../views/DisposalForm.vue')
const RestorationList = () => import('../views/RestorationList.vue')
const RestorationForm = () => import('../views/RestorationForm.vue')
const RoleList = () => import('../views/RoleList.vue')
const RoleForm = () => import('../views/RoleForm.vue')
const VisitorAppointmentList = () => import('../views/VisitorAppointmentList.vue')
const VisitorAppointmentForm = () => import('../views/VisitorAppointmentForm.vue')
const ExhibitionEvaluationList = () => import('../views/ExhibitionEvaluationList.vue')
const ExhibitionEvaluationForm = () => import('../views/ExhibitionEvaluationForm.vue')
const ProcessManagement = () => import('../views/ProcessManagement.vue')
const ExhibitionManagement = () => import('../views/ExhibitionManagement.vue')
const ExhibitionDetail = () => import('../views/ExhibitionDetail.vue')
const ExhibitionForm = () => import('../views/ExhibitionForm.vue')
const StatisticsAnalysis = () => import('../views/StatisticsAnalysis.vue')
const UserRoleManagement = () => import('../views/UserRoleManagement.vue')
const SystemLog = () => import('../views/SystemLog.vue')
const SystemSettings = () => import('../views/SystemSettings.vue')

const withMeta = (title, permission) => ({ title, permission })

const createCrudRoutes = ({
  listPath,
  listComponent,
  listTitle,
  viewPermission,
  addPath,
  addComponent,
  addTitle,
  addPermission,
  editPath,
  editComponent,
  editTitle,
  editPermission,
  addAlias = [],
  editAlias = []
}) => [
  {
    path: listPath,
    component: listComponent,
    meta: withMeta(listTitle, viewPermission)
  },
  {
    path: addPath,
    component: addComponent,
    alias: addAlias,
    meta: withMeta(addTitle, addPermission)
  },
  {
    path: editPath,
    component: editComponent,
    alias: editAlias,
    meta: withMeta(editTitle, editPermission)
  }
]

const createDetailRoute = ({ path, component, title, permission, alias = [] }) => ({
  path,
  component,
  alias,
  meta: withMeta(title, permission)
})

const processRoutes = [
  ...createCrudRoutes({
    listPath: 'acquisition/list',
    listComponent: AcquisitionList,
    listTitle: '征集登记',
    viewPermission: PERMISSIONS.ACQUISITION_VIEW,
    addPath: 'acquisition/add',
    addComponent: AcquisitionForm,
    addTitle: '新增征集',
    addPermission: PERMISSIONS.ACQUISITION_ADD,
    addAlias: ['process/acquisition'],
    editPath: 'acquisition/edit',
    editComponent: AcquisitionForm,
    editTitle: '编辑征集',
    editPermission: PERMISSIONS.ACQUISITION_EDIT
  }),
  ...createCrudRoutes({
    listPath: 'appraisal/list',
    listComponent: AppraisalList,
    listTitle: '鉴定评估',
    viewPermission: PERMISSIONS.APPRAISAL_VIEW,
    addPath: 'appraisal/add',
    addComponent: AppraisalForm,
    addTitle: '新增鉴定',
    addPermission: PERMISSIONS.APPRAISAL_ADD,
    addAlias: ['process/appraisal'],
    editPath: 'appraisal/edit',
    editComponent: AppraisalForm,
    editTitle: '编辑鉴定',
    editPermission: PERMISSIONS.APPRAISAL_EDIT
  }),
  ...createCrudRoutes({
    listPath: 'warehousing/list',
    listComponent: WarehousingList,
    listTitle: '入库管理',
    viewPermission: PERMISSIONS.WAREHOUSING_VIEW,
    addPath: 'warehousing/add',
    addComponent: WarehousingForm,
    addTitle: '新增入库',
    addPermission: PERMISSIONS.WAREHOUSING_ADD,
    addAlias: ['process/inbound'],
    editPath: 'warehousing/edit',
    editComponent: WarehousingForm,
    editTitle: '编辑入库',
    editPermission: PERMISSIONS.WAREHOUSING_EDIT
  }),
  ...createCrudRoutes({
    listPath: 'outbound/list',
    listComponent: OutboundList,
    listTitle: '展览出库',
    viewPermission: PERMISSIONS.OUTBOUND_VIEW,
    addPath: 'outbound/add',
    addComponent: OutboundForm,
    addTitle: '新增出库',
    addPermission: PERMISSIONS.OUTBOUND_ADD,
    addAlias: ['process/outbound'],
    editPath: 'outbound/edit',
    editComponent: OutboundForm,
    editTitle: '编辑出库',
    editPermission: PERMISSIONS.OUTBOUND_EDIT
  }),
  ...createCrudRoutes({
    listPath: 'loan/list',
    listComponent: LoanList,
    listTitle: '外借管理',
    viewPermission: PERMISSIONS.LOAN_VIEW,
    addPath: 'loan/add',
    addComponent: LoanForm,
    addTitle: '新增外借',
    addPermission: PERMISSIONS.LOAN_ADD,
    addAlias: ['process/loan'],
    editPath: 'loan/edit',
    editComponent: LoanForm,
    editTitle: '编辑外借',
    editPermission: PERMISSIONS.LOAN_EDIT
  }),
  ...createCrudRoutes({
    listPath: 'disposal/list',
    listComponent: DisposalList,
    listTitle: '出馆处置',
    viewPermission: PERMISSIONS.DISPOSAL_VIEW,
    addPath: 'disposal/add',
    addComponent: DisposalForm,
    addTitle: '新增处置',
    addPermission: PERMISSIONS.DISPOSAL_ADD,
    addAlias: ['process/disposal'],
    editPath: 'disposal/edit',
    editComponent: DisposalForm,
    editTitle: '编辑处置',
    editPermission: PERMISSIONS.DISPOSAL_EDIT
  }),
  ...createCrudRoutes({
    listPath: 'restoration/list',
    listComponent: RestorationList,
    listTitle: '修复记录',
    viewPermission: PERMISSIONS.RESTORATION_VIEW,
    addPath: 'restoration/add',
    addComponent: RestorationForm,
    addTitle: '新增修复',
    addPermission: PERMISSIONS.RESTORATION_ADD,
    addAlias: ['process/restoration'],
    editPath: 'restoration/edit',
    editComponent: RestorationForm,
    editTitle: '编辑修复',
    editPermission: PERMISSIONS.RESTORATION_EDIT
  }),
  {
    path: 'storage-check/list',
    component: StorageCheckList,
    meta: withMeta('在库保管', PERMISSIONS.ARTIFACT_VIEW)
  },
  {
    path: 'storage-check/add',
    component: StorageCheckForm,
    meta: withMeta('新增在库保管', PERMISSIONS.ARTIFACT_EDIT)
  },
  {
    path: 'storage-check/edit',
    component: StorageCheckForm,
    meta: withMeta('编辑在库保管', PERMISSIONS.ARTIFACT_EDIT)
  },
  {
    path: 'process',
    component: ProcessManagement,
    alias: ['process/center'],
    meta: withMeta('文物流程管理', PROCESS_CENTER_PERMISSIONS)
  }
]

const artifactRoutes = [
  ...createCrudRoutes({
    listPath: 'artifacts',
    listComponent: ArtifactList,
    listTitle: '文物信息管理',
    viewPermission: PERMISSIONS.ARTIFACT_VIEW,
    addPath: 'artifacts/add',
    addComponent: ArtifactForm,
    addTitle: '新增文物',
    addPermission: PERMISSIONS.ARTIFACT_ADD,
    editPath: 'artifacts/:id/edit',
    editComponent: ArtifactForm,
    editTitle: '编辑文物',
    editPermission: PERMISSIONS.ARTIFACT_EDIT
  }),
  createDetailRoute({
    path: 'artifacts/:id',
    component: ArtifactDetail,
    alias: ['artifact/detail/:id'],
    title: '文物详情',
    permission: PERMISSIONS.ARTIFACT_VIEW
  })
]

const visitorAppointmentRoutes = [
  ...createCrudRoutes({
    listPath: 'visitor-appointments',
    listComponent: VisitorAppointmentList,
    listTitle: '预约管理',
    viewPermission: PERMISSIONS.VISITOR_APPOINTMENT_VIEW,
    addPath: 'visitor-appointments/create',
    addComponent: VisitorAppointmentForm,
    addTitle: '新增预约',
    addPermission: PERMISSIONS.VISITOR_APPOINTMENT_ADD,
    editPath: 'visitor-appointments/:id/edit',
    editComponent: VisitorAppointmentForm,
    editTitle: '编辑预约',
    editPermission: PERMISSIONS.VISITOR_APPOINTMENT_EDIT
  })
]

const exhibitionEvaluationRoutes = [
  ...createCrudRoutes({
    listPath: 'exhibition-evaluations',
    listComponent: ExhibitionEvaluationList,
    listTitle: '评估报告',
    viewPermission: PERMISSIONS.EXHIBITION_EVALUATION_VIEW,
    addPath: 'exhibition-evaluations/create',
    addComponent: ExhibitionEvaluationForm,
    addTitle: '新增评估',
    addPermission: PERMISSIONS.EXHIBITION_EVALUATION_ADD,
    editPath: 'exhibition-evaluations/:id/edit',
    editComponent: ExhibitionEvaluationForm,
    editTitle: '编辑评估',
    editPermission: PERMISSIONS.EXHIBITION_EVALUATION_EDIT
  })
]

const exhibitionRoutes = [
  ...createCrudRoutes({
    listPath: 'exhibitions',
    listComponent: ExhibitionManagement,
    listTitle: '展览管理',
    viewPermission: PERMISSIONS.EXHIBITION_VIEW,
    addPath: 'exhibitions/add',
    addComponent: ExhibitionForm,
    addTitle: '新增展览',
    addPermission: PERMISSIONS.EXHIBITION_ADD,
    addAlias: ['exhibition/add'],
    editPath: 'exhibitions/:id/edit',
    editComponent: ExhibitionForm,
    editTitle: '编辑展览',
    editPermission: PERMISSIONS.EXHIBITION_EDIT,
    editAlias: ['exhibition/:id/edit']
  }).map((route, index) =>
    index === 0 ? { ...route, alias: ['exhibition', 'exhibition/list'] } : route
  ),
  createDetailRoute({
    path: 'exhibitions/:id',
    component: ExhibitionDetail,
    alias: ['exhibition/detail/:id'],
    title: '展览详情',
    permission: PERMISSIONS.EXHIBITION_VIEW
  })
]

const routes = [
  {
    path: '/login',
    component: Login,
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', component: Dashboard, meta: { title: '首页仪表盘' } },
      ...artifactRoutes.map((route, index) =>
        index === 0 ? { ...route, alias: ['artifact', 'artifact/list'] } : route
      ),
      ...processRoutes,
      ...visitorAppointmentRoutes,
      ...exhibitionEvaluationRoutes,
      ...exhibitionRoutes,
      {
        path: 'roles',
        component: RoleList,
        alias: ['system/role'],
        meta: withMeta('角色管理', PERMISSIONS.ROLE_VIEW)
      },
      {
        path: 'role-form',
        component: RoleForm,
        meta: withMeta('角色表单', [PERMISSIONS.ROLE_ADD, PERMISSIONS.ROLE_EDIT])
      },
      {
        path: 'statistics',
        component: StatisticsAnalysis,
        alias: ['analytics'],
        meta: withMeta('数据统计分析', PERMISSIONS.STATISTICS_VIEW)
      },
      {
        path: 'users',
        component: UserRoleManagement,
        alias: ['system/user'],
        meta: withMeta('用户管理', PERMISSIONS.USER_VIEW)
      },
      {
        path: 'system/log',
        component: SystemLog,
        meta: withMeta('操作日志审计', PERMISSIONS.OPERATION_LOG_VIEW)
      },
      {
        path: 'settings',
        component: SystemSettings,
        alias: ['system/security', 'system'],
        meta: withMeta('数据安全中心', PERMISSIONS.SECURITY_VIEW)
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from) => {
  window.dispatchEvent(new CustomEvent('museum:navigation:start', { detail: { to: to.fullPath, from: from.fullPath } }))
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    return '/login'
  }

  if (to.path === '/login' && token) {
    return '/dashboard'
  }

  if (token) {
    await initializePermissions()
    if (to.meta.permission && !hasPermission(to.meta.permission)) {
      ElMessage.warning('当前账号没有访问该页面的权限')
      return from.path && from.path !== to.path ? from.fullPath : '/dashboard'
    }
  }

  return true
})

router.afterEach((to, from) => {
  window.dispatchEvent(new CustomEvent('museum:navigation:end', { detail: { to: to.fullPath, from: from.fullPath } }))
})

export default router
