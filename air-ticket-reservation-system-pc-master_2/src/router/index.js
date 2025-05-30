import Vue from 'vue'
import VueRouter from 'vue-router'
import IndexView from '../views/index.vue'
import {Message} from 'element-ui';
import login from '../views/login.vue';
import statistics from '../views/charts/statistics.vue';
Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: '',
    component: login,
    hidden: true,
    meta: {
      requireAuth: false
    }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/register.vue'),
    meta: {
      requireAuth: false
    }
  },
  {
    path: '/index',
    name: '首页',
    component: IndexView,
    iconCls: 'el-icon-tickets',
    children: [
      {
      path: '/charts/statistics',
      name: '数据可视化',
      component: statistics,
      meta: {
          requireAuth: true,
          adminOnly: true
      }
      },
      // {
      // path: '/person/employee',
      // name: 'employee',
      //   component: () => import('../views/person/employee.vue'),
      //   meta: {
      //     requireAuth: true,
      //     adminOnly: true
      //   }
      // },
      {
      path: '/person/user',
      name: 'user',
        component: () => import('../views/person/user.vue'),
        meta: {
          requireAuth: true,
          adminOnly: true
        }
      },
    // 会员管理功能已移除
    
      /*
      {
      path: '/fly/airport',
      name: 'airport',
        component: () => import('../views/fly/airport.vue'),
        meta: {
          requireAuth: true,
          adminOnly: true
        }
      },
      */
      {
      path: '/fly/flight',
      name: 'flight',
        component: () => import('../views/fly/flight.vue'),
        meta: {
          requireAuth: true,
          adminOnly: true
        }
      },
      {
      path: '/order/order',
      name: 'order',
        component: () => import('../views/order/order.vue'),
        meta: {
          requireAuth: true,
          adminOnly: true
        }
      },
      // 机型管理功能已移除
      
      // 行李托运功能已移除
      
      // 公告管理功能已移除

      {
        path: '/user/flights',
        name: '航班查询',
        component: () => import('../views/user/flights.vue'),
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/user/home',
        name: '用户首页',
        component: () => import('../views/user/home.vue'),
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/user/orders',
        name: '我的订单',
        component: () => import('../views/user/orders.vue'),
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/user/passengers',
        name: '乘客管理',
        component: () => import('../views/user/passengers.vue'),
        meta: {
          requireAuth: true
        }
      },
      // 座位管理功能已移除
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})
router.beforeEach((to, from, next) => {
  // 开发模式下，暂时取消登录验证
  const devMode = false; // 开发阶段设为true，上线时改为false
  
  if (devMode) {
    // 如果是开发模式，直接放行所有路由
    return next()
  }
  
  // 以下是原有的验证逻辑，生产环境使用
  if (to.path === '/' || to.path === '/login' || to.path === '/register') {
    return next()
  } else {
    let token = sessionStorage.getItem('token')
    let role = sessionStorage.getItem('role')
    
    if (!token) {
      Message.error('用户未登录')
      return next('/')
    }

    // 处理登录后的首页重定向
    if (to.path === '/index') {
      if (role === '1') {
        return next('/charts/statistics') // 管理员跳转到数据统计
      } else {
        return next('/user/home') // 普通用户跳转到用户首页
      }
    }
    
    if (to.meta.adminOnly && role !== '1') {
      Message.error('无访问权限')
      return next('/user/home') // 普通用户无权限时跳转到用户首页
    }
    
    return next()
  }
})
export default router
