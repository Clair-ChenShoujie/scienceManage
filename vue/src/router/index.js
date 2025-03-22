import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/manager/home' },
    {
      path: '/manager',
      component: () => import('@/views/Manager.vue'),
      meta: {requiresAuth:true},
      children: [
        { path: 'home', meta: { name: '系统首页', requiresAuth: true }, component: () => import('@/views/manager/Home.vue') },
        { path: 'admin', meta: { name: '管理员信息', requiresAuth: true }, component: () => import('@/views/manager/Admin.vue') },
        { path: 'notice', meta: { name: '系统公告', requiresAuth: true }, component: () => import('@/views/manager/Notice.vue') },
        { path: 'person', meta: { name: '个人资料', requiresAuth: true }, component: () => import('@/views/manager/Person.vue') },
        { path: 'password', meta: { name: '修改密码', requiresAuth: true }, component: () => import('@/views/manager/Password.vue') },
        { path: 'teacher', meta: { name: '教师信息', requiresAuth: true }, component: () => import('@/views/manager/Teacher.vue') },
        { path: 'project', meta: { name: '科研项目', requiresAuth: true }, component: () => import('@/views/manager/Project.vue') },
        { path: 'process', meta: { name: '科研过程', requiresAuth: true }, component: () => import('@/views/manager/Process.vue') },
        { path: 'type', meta: { name: '成果分类', requiresAuth: true }, component: () => import('@/views/manager/Type.vue') },
      ]
    },
    {
      path: '/front',
      component: () => import('@/views/Front.vue'),
      meta: {requiresAuth:true},
      children: [
        { path: 'home', meta: { requiresAuth: true }, component: () => import('@/views/front/Home.vue') },
        { path: 'person', meta: { requiresAuth: true }, component: () => import('@/views/front/Person.vue') },
      ]
    },
    { path: '/login', meta: { requiresAuth: false }, component: () => import('@/views/Login.vue') },
    { path: '/register', meta: { requiresAuth: false }, component: () => import('@/views/Register.vue') },
    { path: '/404', meta: { requiresAuth: false }, component: () => import('@/views/404.vue') },
    { path: '/:pathMatch(.*)', redirect: '/404' },
  ],
});


export default router
