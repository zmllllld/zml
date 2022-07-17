import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "@/views/Home";
import ProductManage from "@/views/ProductManage";
import AddProduct from "@/views/AddProduct";
import EditProduct from "@/views/EditProduct";
import OrderManage from "@/views/OrderManage";
import Bar from "@/views/Bar";
import BasicLine from "@/views/BasicLine";
import StackedLine from "@/views/StackedLine";
import Login from "@/views/Login";
import Error from "@/views/Error";
Vue.use(VueRouter)

const routes = [

  {
    path: '/',
    name: '商品模块',
    icon:'el-icon-goods',
    //是否展示
    show:true,
    component: Home,
    //默认展示某个页面
    redirect:'/productManage',
    //子菜单
    children:[
      {
        path: '/productManage',
        name: '商品管理',
        show:true,
        //图标
        icon:'el-icon-s-unfold',
        component: ProductManage,
      },{
        path: '/addProduct',
        name: '添加商品',
        show:true,
        icon:'el-icon-circle-plus',
        component: AddProduct
      },
      {
        path:'/editProduct',
        name:'修改商品',
        show:false,
        component:EditProduct
      }

    ]
  },
  {
    path: '/',
    name: '订单模块',
    component: Home,
    icon: 'el-icon-s-grid',
    show:true,
    redirect:'/productManage',
    children:[
      {
        path: '/orderManage',
        name: '订单管理',
        icon: 'el-icon-document-copy',
        show:true,
        component: OrderManage,
      }

    ]
  },
  {
    path: '/',
    name: '销量统计',
    component: Home,
    icon: 'el-icon-finished',
    redirect:'/productManage',
    show:true,
    children:[
      {
        path: '/bar',
        name: '总销量',
        icon: 'el-icon-s-data',
        show:true,
        component: Bar,
      },{
        path: '/basicLine',
        name: '日销量',
        icon: 'el-icon-s-marketing',
        show:true,
        component: BasicLine
      },
      {
        path: '/stackedLine',
        name: '销量明细',
        icon: 'el-icon-s-help',
        show:true,
        component: StackedLine
      }

    ]
  },
  {
    path:'/login',
    name:'登录',
    show:false,
    component:Login
  },
  {
    path:'/error',
    name:'登录',
    show:false,
    component:Error
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


//访问每个页面时都判断localstorage里面有没有登录对象，没有数据就没登陆,直接跳转到登录页面
//如果有数据就校验token的合法性

router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/login')) {
    window.localStorage.removeItem('access-admin')
    next()
  } else {
    let admin = JSON.parse(window.localStorage.getItem('access-admin'))
    if (!admin) {
      next({path: '/login'})
    } else {
      //校验token
      axios({
        url:'http://localhost:8686/account-service/admin/checkToken',
        method:'get',
        headers:{
          token:admin.token
        }
      }).then((response) => {
        //校验返回-1，则失败
        if(response.data.code == -1){
          console.log('校验失败')
          next({path: '/error'})
        }
      })
      next()
    }
  }
})

export default router
