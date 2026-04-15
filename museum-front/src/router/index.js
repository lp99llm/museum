import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import ArtifactList from '../views/ArtifactList.vue'
import ArtifactDetail from '../views/ArtifactDetail.vue'
import ArtifactForm from '../views/ArtifactForm.vue'

const routes = [
  { path: '/login', component: Login, meta: { requiresAuth: false } },
  { path: '/', redirect: '/artifacts' },
  { path: '/artifacts', component: ArtifactList, meta: { requiresAuth: true } },
  { path: '/artifacts/:id', component: ArtifactDetail, props: true, meta: { requiresAuth: true } },
  { path: '/artifacts/create', component: ArtifactForm, meta: { requiresAuth: true } },
  { path: '/artifacts/:id/edit', component: ArtifactForm, props: true, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/artifacts')
  } else {
    next()
  }
})

export default router