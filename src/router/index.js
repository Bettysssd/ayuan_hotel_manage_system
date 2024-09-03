import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus';
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/supervisorLogin',
      name: 'supervisorLogin',
      component: () => import("@/components/supervisor/main.vue"),
      beforeEnter: (to, from, next) => {
        // 在路由跳转前显示消息
        ElMessage({
          message: '欢迎你，管理员',
          type: 'success',
        });
        next(); // 不要忘记调用 next() 来解析守卫
      }
    },
    {
      path: '/',
      name: 'Login',
      component: () => import("@/Login.vue")
    },
    {
      path: '/userLogin',
      name: 'userLogin',
      component: () => import("@/components/user/main.vue"),
      beforeEnter: (to, from, next) => {
        // 在路由跳转前显示消息
        ElMessage({
          message: '您好，尊敬的用户',
          type: 'success',
        });
        next(); 
      }
    },
  ]
});

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 检查目标路由是否需要登录
  const publicPages = ['/']; // 假设这些是公共页面，不需要登录
  const requiresAuth = !publicPages.includes(to.path);

  // 调用你的 isLoggedIn 方法来检查用户是否登录
  const isLoggedIn = sessionStorage.getItem('isLoggedIn') === 'true';

  if (requiresAuth && !isLoggedIn) {
    // 如果路由需要登录但用户未登录，则重定向到登录页面
    next('/');
  } else {
    // 如果不需要登录，或者用户已登录，则继续正常路由
    next();
  }
});



export default router
