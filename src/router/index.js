import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus';
const router = createRouter({
  history: createWebHistory(),
  routes: [

    {
      path: '/',
      name: 'login',
      component: () => import("@/login.vue")
    },
    {
      path: '/supervisor',
      name: 'supervisor',
      component: () => import("@/components/supervisor/main.vue"),
      beforeEnter: (to, from, next) => {
        // 在路由跳转前显示消息
        ElMessage({
          message: '欢迎你，管理员',
          type: 'success',
        });
         next(); // 不要忘记调用 next() 来解析守卫
      },
      // 一定要设置这个，不然路由跳转就不能很好的展示在特定的区域中
      children: [
        {
          path: '/supervisor/variableGraphics',
          name:"variableGraphics",
          component: () => import("@/components/supervisor/graphics/variableGraphics.vue")
        },
        {
          path: '/supervisor/manageUser',
          name:"manageUser",
          component: () => import("@/components/supervisor/user/manageUser.vue")
        },
        {
          path: '/supervisor/manageRoom',
          name:"manageRoom",
          component: () => import("@/components/supervisor/room/manageRoom.vue")
        },
        {
          path: '/supervisor/catering',
          name:"cateringDepartment",
          component: () => import("@/components/supervisor/waiters/cateringDepartment.vue")
        },
        {
          path: '/supervisor/deliver',
          name:"deliverDepartment",
          component: () => import("@/components/supervisor/waiters/deliverDepartment.vue")
        },
        {
          path: '/supervisor/roomServe',
          name:"roomDepartment",
          component: () => import("@/components/supervisor/waiters/roomDepartment.vue")
        },
        {
          path: '/supervisor/concierge',
          name:"conciergeDepartment",
          component: () => import("@/components/supervisor/waiters/conciergeDepartment.vue")
        },
        {
          path: '/supervisor/reservation',
          name:"reservationDepartment",
          component: () => import("@/components/supervisor/waiters/reservationDepartment.vue")
        },
        {
          path: '/supervisor/parking',
          name:"parkingDepartment",
          component: () => import("@/components/supervisor/waiters/parkingDepartment.vue")
        },
        {
          path: '/supervisor/comunication',
          name:"comunicationDepartment",
          component: () => import("@/components/supervisor/waiters/comunicationDepartment.vue")
        },
        {
          path: '/supervisor/manageParking',
          name:"manageParking",
          component: () => import("@/components/supervisor/parking/manageParking.vue")
        },
        {
          path: '/supervisor/manageCanteen',
          name:"manageCanteen",
          component: () => import("@/components/supervisor/canteen/manageCanteen.vue")
        },
        {
          path: '/supervisor/manageGoods',
          name:"manageGoods",
          component: () => import("@/components/supervisor/goods/manageGoods.vue")
        }
      ]
    },
    

    {
      path: '/user',
      name: 'user',
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
  const userToken = localStorage.getItem("userToken");
  const supervisorToken = localStorage.getItem("supervisorToken");  
  if (requiresAuth) {
    // 如果路由需要登录
    if (userToken && supervisorToken) {
      // 如果用户和管理员都登录，允许访问
      next();
    } else if (userToken) {
      // 如果只有用户登录，只允许访问用户的路由
      if (to.path.startsWith('/user')) {
        next();
      } else {
        next('/user'); // 否则重定向到用户的路由
      }
    } else if (supervisorToken) {
      // 如果只有管理员登录，只允许访问管理员的路由
      if (to.path.startsWith('/supervisor')) {
        next();
      } else {
        next('/supervisor'); // 否则重定向到管理员的路由
      }
    } else {
      // 如果两者都没有登录，只允许访问公共页面
      next('/');
    }
  } else {
    // 如果路由不需要登录，允许访问
    next();
  }
});



export default router
