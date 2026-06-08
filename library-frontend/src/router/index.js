import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'book',
        name: 'Book',
        component: () => import('@/views/book/index.vue'),
        meta: { title: '图书管理', icon: 'Reading' }
      },
      {
        path: 'borrow',
        name: 'Borrow',
        component: () => import('@/views/borrow/BorrowList.vue'),
        meta: { title: '借阅管理', icon: 'Tickets' }
      },
      {
        path: 'book/:id',
        name: 'BookEdit',
        component: () => import('@/views/book/EditBook.vue'),
        meta: { title: '图书详情', hidden: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', icon: 'Avatar' }
      },
      {
        path: 'reader',
        name: 'Reader',
        component: () => import('@/views/reader/index.vue'),
        meta: { title: '读者管理', icon: 'User' }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/notice/index.vue'),
        meta: { title: '通知管理', icon: 'Bell' }
      },
      {
        path: 'system',
        name: 'System',
        component: () => import('@/views/system/index.vue'),
        meta: { title: '系统设置', icon: 'Setting' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token')
  const isAdmin = sessionStorage.getItem('isAdmin') === 'true'
  
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      // 检查系统设置页面的访问权限
      if (to.path === '/system' && !isAdmin) {
        next('/dashboard') // 非管理员重定向到首页
      } else if (to.path === '/reader' && !isAdmin) {
        next('/dashboard') // 非管理员重定向到首页
      } else {
        next()
      }
    } else {
      next('/login')
    }
  }
})

export default router
