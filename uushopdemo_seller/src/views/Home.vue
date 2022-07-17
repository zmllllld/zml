<template>
  <el-container class="home_container">
<!--    标题-->
    <el-header>
      <div class="home_title">社区优选平台后台管理系统</div>
      <div class="home_userinfoContainer">
        <el-avatar :src="admin.imgUrl"></el-avatar>
        <el-dropdown style="position: relative;top: -10px;left: 10px;">
                  <span class="el-dropdown-link home_userinfo">
                    {{admin.name}}<i class="el-icon-arrow-down el-icon--right home_userinfo"></i>
                  </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>

<!--     左侧菜单-->
      <el-aside width="200px" style="background-color: #a5e7f0">
        <el-menu router>
<!--          根据路由动态生成标签-->
<!--          index是给每个菜单设置一个下标,区分不同的菜单，点击时不会同时展开，选中哪一个展开哪一个 v-if为true则展示-->
<!--            class表示静态读取 :class表示动态读取-->
          <el-submenu v-for="(item,index) in $router.options.routes" v-if="item.show" :index="index+''">
<!--          class="item.icon"是菜单前的图标-->
            <template slot="title"><i :class="item.icon"></i>{{item.name}}</template>
<!--          index表示点击该菜单时跳转的页面--> 
            <el-menu-item v-for="item2 in item.children" v-if="item2.show" :index="item2.path"
                          :class="$route.path==item2.path?'is-active':''">
              <div style="position: relative;left: 20px;" >
                <i :class="item2.icon"></i>{{item2.name}}
              </div>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>

<!--      中间容器-->
      <el-container>
        <el-main>
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-text="this.$router.currentRoute.name"></el-breadcrumb-item>
          </el-breadcrumb>
<!--          视图窗口进行动态展示-->
          <router-view></router-view>
        </el-main>
        <el-footer>@V1.0-Spring Cloud Alibaba + Vue2.0</el-footer>
      </el-container>

    </el-container>

  </el-container>
</template>
<script>
  export default{
    methods: {
      logout(){
        let _this = this;
        this.$confirm('注销登录吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(function () {
          //  销毁admin
          localStorage.removeItem('access-admin')
          _this.$router.replace({path: '/login'})
        })
      }
    },
    mounted: function () {
      //  将用户从localstorage里面取出来还原成一个json对象
      let admin = JSON.parse(window.localStorage.getItem('access-admin'))
        //将从localstorage里面取出来的admin对象赋给当前页面的admin
        this.admin=admin
      // let _this = this
      // //首先需要校验token合法性
      // axios({
      //   url:this.$store.state.globalhost+'account-service/admin/checkToken',
      //   method:'get',
      //   headers:{
      //     token:admin.token
      //   }
      // }).then((response) => {
      //   if(response.data.code == 0){
      //     this.admin = admin
      //   }
      //   if(response.data.code == -1){
      //     _this.$alert(response.data.msg, '提示', {
      //       confirmButtonText: '确定'
      //     }).then((response) => {
      //       localStorage.removeItem('access-admin')
      //       _this.$router.replace({path: '/login'})
      //     })
      //   }
      // })
    },
    data(){
      return {
        admin:''
      }
    }
  }
</script>
<style>
  .home_container {
    height: 100%;
    position: absolute;
    top: 0px;
    left: 0px;
    width: 100%;
  }

  .el-header {
    background-color: #20a0ff;
    color: #333;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .el-aside {
    background-color: #ECECEC;
  }

  .el-main {
    background-color: #fff;
    color: #000;
    text-align: center;
  }

  .el-footer {
    background-color: #ea7e53;
    color: #fff;
    font-size: 22px;
    line-height: 60px;
  }

  .home_title {
    color: #fff;
    font-size: 22px;
    display: inline;
  }

  .home_userinfo {
    color: #fff;
    cursor: pointer;
  }

  .home_userinfoContainer {
    display: inline;
    margin-right: 20px;
  }
</style>
