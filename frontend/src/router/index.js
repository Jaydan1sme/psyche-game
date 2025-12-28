import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/atomic/Layout.vue'
import Home from '@/views/Home.vue'
import GameList from '@/views/GameList.vue'
import ForumList from '@/views/ForumList.vue'
import Profile from '@/views/Profile.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import LocalDeploy from '@/views/LocalDeploy.vue'
import ArticleDetail from '@/views/ArticleDetail.vue'
import GameDetail from '@/views/GameDetail.vue'
import { useModeStore } from '@/store/mode'

const routes = [
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: false },
    children: [
      { path: '', component: Home, name: 'Home', meta: { title: '首页' } },
      { path: 'games', component: GameList, name: 'GameList', meta: { title: '游戏列表' } },
      { path: 'games/:gameType', component: GameDetail, name: 'GameDetail', meta: { title: '游戏详情' } },
      { path: 'forum', component: ForumList, name: 'ForumList', meta: { title: '论坛' } },
      { path: 'forum/article/:id', component: ArticleDetail, name: 'ArticleDetail', meta: { title: '文章详情' } },
      { path: 'profile', component: Profile, name: 'Profile', meta: { title: '个人中心', requiresAuth: true } },
      { path: 'local-deploy', component: LocalDeploy, name: 'LocalDeploy', meta: { title: '本地部署配置' } }
    ]
  },
  {
    path: '/login',
    component: Login,
    name: 'Login',
    meta: { title: '登录' }
  },
  {
    path: '/register',
    component: Register,
    name: 'Register',
    meta: { title: '注册' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const modeStore = useModeStore()
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - Psyche Game`
  }
  
  // 登录验证
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
