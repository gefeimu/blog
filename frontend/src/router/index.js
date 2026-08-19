import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  { path: '/', name: 'home', component: HomeView },
  {
    path: '/category/:id',
    name: 'category',
    component: () => import('../views/CategoryView.vue'),
  },
  {
    path: '/article/:id',
    name: 'article',
    component: () => import('../views/ArticleView.vue'),
  },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

export default router
