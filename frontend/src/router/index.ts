import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes: RouteRecordRaw[] = [
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
  // ---------- 后台管理 ----------
  { path: '/admin/login', name: 'admin-login', component: () => import('../views/admin/AdminLogin.vue') },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    children: [
      { path: '', redirect: '/admin/articles' },
      {
        path: 'articles',
        name: 'admin-articles',
        component: () => import('../views/admin/AdminArticles.vue'),
      },
      {
        path: 'articles/new',
        name: 'admin-article-new',
        component: () => import('../views/admin/AdminArticleEdit.vue'),
      },
      {
        path: 'articles/:id/edit',
        name: 'admin-article-edit',
        component: () => import('../views/admin/AdminArticleEdit.vue'),
      },
      {
        path: 'categories',
        name: 'admin-categories',
        component: () => import('../views/admin/AdminCategories.vue'),
      },
    ],
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

// 登录守卫：后台页面需持有 token
router.beforeEach((to) => {
  if (to.path.startsWith('/admin') && to.path !== '/admin/login') {
    if (!localStorage.getItem('blog_token')) {
      return '/admin/login'
    }
  }
  return true
})

export default router
