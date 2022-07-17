import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Cart from "@/views/Cart";
import Info from "@/views/Info";
import Login from "@/views/Login";
import OrderDetail from "@/views/OrderDetail";
import Pay from "@/views/Pay";
import Order from "@/views/Order";
import Mine from "@/views/Mine";
import Register from "@/views/Register";
import {Toast} from 'mint-ui'

Vue.use(VueRouter)

const routes = [
  {
    path:'/register',
    component:Register

  },
  {
    path:'/mine',
    component:Mine
  },
  {
    path: '/order',
    component:Order

  },
  {
    path: '/pay',
    component:Pay

  },
  {
    path:'/orderDetail',
    component:OrderDetail
  },
  {
    path:'/login',
    component:Login

  },
  {
    path:'/info',
    component:Info

  },
  {
    path:'/cart',
    component:Cart

  },

  {
    path: '/',
    component: Cart
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

//解决路由跳转错误
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}

//beforeEach每一个路由进入前都会调这个方法
router.beforeEach((to, from, next) => {
  //进入登录和注册页面就 放行
  if (to.path.startsWith('/login')) {
    window.localStorage.removeItem('access-user')
    next()
  } else if(to.path.startsWith('/register')){
    next()
  }
  else {
    let user = JSON.parse(window.localStorage.getItem('access-user'))
    if (!user) {
      next({path: '/login'})
    } else {
      // 校验token
      axios({
        url:'http://localhost:8686/account-service/user/checkToken',
        method:'get',
        headers:{
          token:user.token
        }
      }).then((response) => {
        if(response.data.code == -1){
          let instance = Toast('登录超时！请重新登录！');
          setTimeout(() => {
            instance.close();
          }, 2000)
          next({path: '/login'})
        }
      })
      next()
    }
  }
})

export default router
