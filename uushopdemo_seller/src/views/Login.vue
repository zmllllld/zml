<template>
    <div class="login-container">
<!--        整个表单和rules的校验规则绑定 -->
        <el-form :model="ruleForm" :rules="rules"
                 status-icon
                 ref="ruleForm"
                 label-position="left"
                 label-width="0px"
                 class="demo-ruleForm login-page">
            <h3 class="title">系统登录</h3>、
<!--            prop="username"表示绑定username的校验规则-->
            <el-form-item prop="username">
<!--                v-model绑定的username 是结果-->
                <el-input type="text"
                          v-model="ruleForm.username"
                          auto-complete="off"
                          placeholder="用户名"
                ></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input type="password"
                          v-model="ruleForm.password"
                          auto-complete="off"
                          placeholder="密码"
                ></el-input>
            </el-form-item>
            <el-form-item style="width:100%;">
                <el-button type="primary" style="width:100%;" @click="handleSubmit" :loading="logining">登录</el-button>
            </el-form-item>
        </el-form>
    </div>
</template> 

<script>
    export default {
        name: "Login",
        data(){
            return{
                logining: false,
                fullscreenLoading: true,
                //ruleForm和视图绑定，ruleForm的数据自动回填到视图里面
                ruleForm: {
                    username: 'admin',
                    password: '123123'
                },
                //定义校验规则，trigger表示何时触发校验，blur：失去焦点 ，即但选中框的光标点在其他地方时触发校验规则
                rules: {
                    username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
                    password: [{required: true, message: '请输入密码', trigger: 'blur'}]
                }
            }
        },
        methods: {
            handleSubmit(){
                this.$refs.ruleForm.validate((valid) => {
                    if(valid){
                        this.logining = true
                        let _this = this
                        axios.get(this.$store.state.globalhost+'account-service/admin/login', {params:_this.ruleForm}).then(function (response) {
                            _this.logining = false
                            if(response.data.code == -1){
                                _this.$alert(response.data.data, '提示', {
                                    confirmButtonText: '确定'
                                })
                            }
                            if(response.data.code == 0){
                // 将数据存储到localstorage里面
                                localStorage.setItem('access-admin', JSON.stringify(response.data.data));
                               //跳转到首页
                                _this.$router.replace({path: '/'})
                            }
                        })
                    }else{
                        console.log('error submit!');
                        return false;
                    }
                })

            }
        }
    };
</script>

<style scoped>
    .login-container {
        width: 100%;
        height: 100%;
    }
    .login-page {
        -webkit-border-radius: 5px;
        border-radius: 5px;
        margin: 180px auto;
        width: 350px;
        padding: 35px 35px 15px;
        background: #fff;
        border: 1px solid #eaeaea;
        box-shadow: 0 0 25px #cac6c6;
    }
    label.el-checkbox.rememberme {
        margin: 0px 0px 15px;
        text-align: left;
    }
</style>