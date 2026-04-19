import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'
import Login from '../views/Login.vue'
import ArtifactList from '../views/ArtifactList.vue'
import ArtifactDetail from '../views/ArtifactDetail.vue'
import ArtifactForm from '../views/ArtifactForm.vue'
import ExhibitionList from '../views/ExhibitionList.vue'
import ExhibitionForm from '../views/ExhibitionForm.vue'
import VisitorAppointmentList from '../views/VisitorAppointmentList.vue'
import VisitorAppointmentForm from '../views/VisitorAppointmentForm.vue'
import ExhibitionEvaluationList from '../views/ExhibitionEvaluationList.vue'
import ExhibitionEvaluationForm from '../views/ExhibitionEvaluationForm.vue'
import UserList from '../views/UserList.vue'
import UserForm from '../views/UserForm.vue'
import RoleList from '../views/RoleList.vue'
import RoleForm from '../views/RoleForm.vue'
import Dashboard from '../views/Dashboard.vue'
import AcquisitionList from '../views/AcquisitionList.vue'
import AcquisitionForm from '../views/AcquisitionForm.vue'
import AppraisalList from '../views/AppraisalList.vue'
import AppraisalForm from '../views/AppraisalForm.vue'
import WarehousingList from '../views/WarehousingList.vue'
import WarehousingForm from '../views/WarehousingForm.vue'
import StorageCheckList from '../views/StorageCheckList.vue'
import StorageCheckForm from '../views/StorageCheckForm.vue'
import OutboundList from '../views/OutboundList.vue'
import OutboundForm from '../views/OutboundForm.vue'
import RestorationList from '../views/RestorationList.vue'
import RestorationForm from '../views/RestorationForm.vue'
import LoanList from '../views/LoanList.vue'
import LoanForm from '../views/LoanForm.vue'
import DisposalList from '../views/DisposalList.vue'
import DisposalForm from '../views/DisposalForm.vue'

const routes = [
  { path: '/login', component: Login, meta: { requiresAuth: false } },
  { path: '/', component: Layout, meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', component: Dashboard },
      { path: 'artifacts', component: ArtifactList },
      { path: 'artifacts/:id', component: ArtifactDetail, props: true },
      { path: 'artifacts/create', component: ArtifactForm },
      { path: 'artifacts/:id/edit', component: ArtifactForm, props: true },
      { path: 'exhibitions', component: ExhibitionList },
      { path: 'exhibitions/create', component: ExhibitionForm },
      { path: 'exhibitions/:id/edit', component: ExhibitionForm, props: true },
      { path: 'visitor-appointments', component: VisitorAppointmentList },
      { path: 'visitor-appointments/create', component: VisitorAppointmentForm },
      { path: 'visitor-appointments/:id/edit', component: VisitorAppointmentForm, props: true },
      { path: 'exhibition-evaluations', component: ExhibitionEvaluationList },
      { path: 'exhibition-evaluations/create', component: ExhibitionEvaluationForm },
      { path: 'exhibition-evaluations/:id/edit', component: ExhibitionEvaluationForm, props: true },
      { path: 'users', component: UserList },
      { path: 'user-form', component: UserForm },
      { path: 'roles', component: RoleList },
      { path: 'role-form', component: RoleForm },
      { path: 'acquisition/list', component: AcquisitionList },
      { path: 'acquisition/add', component: AcquisitionForm },
      { path: 'acquisition/edit', component: AcquisitionForm },
      { path: 'appraisal/list', component: AppraisalList },
      { path: 'appraisal/add', component: AppraisalForm },
      { path: 'appraisal/edit', component: AppraisalForm },
      { path: 'warehousing/list', component: WarehousingList },
      { path: 'warehousing/add', component: WarehousingForm },
      { path: 'warehousing/edit', component: WarehousingForm },
      { path: 'storage-check/list', component: StorageCheckList },
      { path: 'storage-check/add', component: StorageCheckForm },
      { path: 'storage-check/edit', component: StorageCheckForm },
      { path: 'outbound/list', component: OutboundList },
      { path: 'outbound/add', component: OutboundForm },
      { path: 'outbound/edit', component: OutboundForm },
      { path: 'restoration/list', component: RestorationList },
      { path: 'restoration/add', component: RestorationForm },
      { path: 'restoration/edit', component: RestorationForm },
      { path: 'loan/list', component: LoanList },
      { path: 'loan/add', component: LoanForm },
      { path: 'loan/edit', component: LoanForm },
      { path: 'disposal/list', component: DisposalList },
      { path: 'disposal/add', component: DisposalForm },
      { path: 'disposal/edit', component: DisposalForm }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return '/login'
  } else if (to.path === '/login' && token) {
    return '/dashboard'
  }
  return true
})

export default router
