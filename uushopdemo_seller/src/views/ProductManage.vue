<template>
    <div style="margin-top: 60px;margin-left:80px;border: 0px solid red;" >
        <el-form label-width="100px" class="demo-ruleForm">
            <el-form-item label="关键字：" prop="keyWord">
                <el-input v-model="keyWord" placeholder="请输入商品关键字" style="width: 230px;float: left"></el-input>
                <el-button type="primary" icon="el-icon-search" style="float: left;position: relative;left: 10px;" @click="submitForm()">搜索</el-button>
                <span>分类检索：</span>
                <el-select v-model="selectedCategory" placeholder="请选择商品分类">
<!--                   value获取的是商品分类的数值 -->
                    <el-option v-for="item in category" :label="item.name" :value="item.type"></el-option>
                </el-select>
                <el-button type="primary" icon="el-icon-search" style="position: relative;left: 10px;" @click="categoryForm()">搜索</el-button>
                <el-button @click="exportExcel" type="success" icon="el-icon-download" style="float: right">导出Excel</el-button>
                <el-button @click="importExcel" type="success" icon="el-icon-upload2" style="float: right;margin-right: 5px;">导入Excel</el-button>
            </el-form-item>
        </el-form>

        <el-dialog title="导入文件" :visible.sync="dialogTableVisible" width="30%">
            <el-upload
                    class="upload-demo"
                    drag
                    action="http://localhost:8686/product-service/seller/product/import"
                    multiple
                    :on-success="importSuccess"
                    :on-error="importError"
                    accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                <div class="el-upload__tip" slot="tip">只能上传xls/xlsx</div>
            </el-upload>
        </el-dialog>

        <el-table
                :data="tableData"
                border
                stripe
                style="width: 100%">
            <el-table-column
                    fixed
                    prop="id"
                    label="编号"
                    width="80">
            </el-table-column>
            <el-table-column
                    prop="name"
                    label="商品名称"
                    width="230">
            </el-table-column>
            <el-table-column
                    prop="price"
                    label="商品价格"
                    width="80">
            </el-table-column>
            <el-table-column
                    prop="stock"
                    label="商品库存"
                    width="80">
            </el-table-column>
            <el-table-column
                    prop="description"
                    label="商品描述"
                    width="200">
            </el-table-column>
            <el-table-column label="商品图标" width="100">
                　　<template slot-scope="scope">
                　　　　<img :src="scope.row.icon" width="60"/>
                　　</template>
            </el-table-column>
            <el-table-column label="上架" width="150">

                <template slot-scope="scope">
<!--                    status true表示上架false下架-->
                    <el-switch
                            style="height: 20px"
                            v-model="scope.row.status"
                            active-color="#13ce66"
                            active-text="上架"
                            inactive-color="#ff4949"
                            inactive-text="下架"
                            @change="changeSwitch(scope.row)">
                    </el-switch>
                </template>

            </el-table-column>
            <el-table-column
                    prop="categoryName"
                    label="商品分类"
                    width="80">
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            @click="edit(scope.row)">编辑</el-button>
                    <el-button
                            size="mini"
                            type="danger"
                            @click="del(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination style="margin-top: 20px; float: right"
                       background
                       layout="prev, pager, next"
                       :page-size="pageSize"
                       :total="total"
                       :current-page.sync="currentPage"
                       @current-change="page">
        </el-pagination>

    </div>

</template>

<script>
    export default {
        data() {
            return {
                tableData:null,
                pageSize:5,
                total:null,
                keyWord:'',
                currentPage:1,
                dialogTableVisible:false,
                category:'',
                selectedCategory:''
            }
        },
        methods:{
            importSuccess(data){
                if(data.code == 0){
                    this.$alert('数据导入成功！', '', {
                        confirmButtonText: '确定',
                        callback: action => {
                            this.dialogTableVisible=false
                            location.reload()
                        }
                    });
                }
                if(data.code == -1){
                    this.$alert('数据导入失败！', '', {
                        confirmButtonText: '确定',
                        callback: action => {
                            this.dialogTableVisible=false
                            location.reload()
                        }
                    });
                }
            },
            importError(){
                this.$alert('数据导入失败', '', {
                    confirmButtonText: '确定',
                    callback: action => {
                        this.dialogTableVisible=false
                        location.reload()
                    }
                });
            },
            exportExcel(){
                window.location.href=this.$store.state.globalhost+'product-service/seller/product/export'
            },
            importExcel(){
                this.dialogTableVisible=true
            },
            submitForm() {
                const _this = this
                //让翻页复原
                _this.currentPage=1
                _this.selectedCategory=''
                if(_this.keyWord == ''){
                    axios.get(this.$store.state.globalhost+'product-service/seller/product/list/1/'+_this.pageSize).then(function (resp) {
                        _this.tableData = resp.data.data.content
                        _this.pageSize = resp.data.data.size
                        _this.total = resp.data.data.total
                    })
                }else{
                    axios.get(this.$store.state.globalhost+'product-service/seller/product/like/'+_this.keyWord+'/1/'+_this.pageSize).then(function (resp) {
                        _this.tableData = resp.data.data.content
                        _this.pageSize = resp.data.data.size
                        _this.total = resp.data.data.total
                    })
                }
            },
            categoryForm() {
                const _this = this
                //让翻页复原
                _this.currentPage=1
                _this.keyWord=''
                if(this.selectedCategory == '') {
                    _this.$message({
                        showClose: true,
                        message: '请选择商品分类',
                        type: 'error'
                    })
                }
                axios.get(this.$store.state.globalhost+'product-service/seller/product/findByCategory/'+_this.selectedCategory+'/1/'+_this.pageSize).then(function (resp) {
                    // console.log(resp.data.data)
                    _this.tableData = resp.data.data.content
                    _this.pageSize = resp.data.data.size
                    _this.total = resp.data.data.total
                })
            },
            page(currentPage){
                const _this = this
                //这是以关键字查询分页的情况
                if(_this.keyWord != ''){
                    axios.get(this.$store.state.globalhost+'product-service/seller/product/like/'+_this.keyWord+'/'+currentPage+'/'+_this.pageSize).then(function (resp) {
                        _this.tableData = resp.data.data.content
                        _this.pageSize = resp.data.data.size
                        _this.total = resp.data.data.total
                    })
                //    这个else if是代表选择的商品分类不为空是就还需要把商品分类传过去的情况
                }else if(_this.selectedCategory != ''){
                    axios.get(this.$store.state.globalhost+'product-service/seller/product/findByCategory/'+_this.selectedCategory+'/'+currentPage+'/'+_this.pageSize).then(function (resp) {
                        _this.tableData = resp.data.data.content
                        _this.pageSize = resp.data.data.size
                        _this.total = resp.data.data.total
                    })
                //    这个else是选择的商品分类为空的情况
                }else {
                    axios.get(this.$store.state.globalhost+'product-service/seller/product/list/'+currentPage+'/'+_this.pageSize).then(function (resp) {
                        _this.tableData = resp.data.data.content
                        _this.pageSize = resp.data.data.size
                        _this.total = resp.data.data.total
                    })
                }
            },
            edit(row){
                this.$router.push('/editProduct?id='+row.id)
            },
            del(row) {
                const _this = this
                this.$confirm('确认删除【'+row.name+'】吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    axios.delete(this.$store.state.globalhost+'product-service/seller/product/delete/'+row.id).then(function (resp) {
                        if(resp.data.code==0){
                            _this.$alert('【'+row.name+'】已删除', '', {
                                confirmButtonText: '确定',
                                callback: action => {
                                    location.reload()
                                }
                            });
                        }
                    });
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            changeSwitch (data) {
                const _this = this
                axios.put(this.$store.state.globalhost+'product-service/seller/product/updateStatus/'+data.id+'/'+data.status).then(function (resp) {
                    console.log(resp.data.data)
                    if(data.status == true){
                        _this.$message({
                            showClose: true,
                            message: '【'+data.name+'】已上架',
                            type: 'success'
                        })
                    }else{
                        _this.$message({
                            showClose: true,
                            message: '【'+data.name+'】已下架',
                            type: 'error'
                        })
                    }
                })
            }
        },

        //created方法表示初始化页面
        created() {
            const _this = this
            axios.get(this.$store.state.globalhost+'product-service/seller/product/list/1/'+_this.pageSize).then(function (resp) {
                _this.tableData = resp.data.data.content
                _this.pageSize = resp.data.data.size
                _this.total = resp.data.data.total
            })
            axios.get(this.$store.state.globalhost+'product-service/seller/product/findAllProductCategory').then(function (resp) {
                _this.category = resp.data.data.content
            })
        }
    }
</script>