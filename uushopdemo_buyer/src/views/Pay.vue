<template>
    <div class="mui-content">
        <div class="mui-card">
            <ul class="mui-table-view">
                <li class="mui-table-view-cell">付款</li>
                <li class="mui-table-view-cell">支付方式<span class="mui-icon mui-icon-weixin spanright"></span></li>
            </ul>
            <form class="mui-input-group">
                <div class="mui-input-row">
                    <label>金额</label>
<!--                    只读-->
                    <input type="text" v-model="orderAmount" readonly/>
                </div>
                <button type="button" class="mui-btn mui-btn-block mui-btn-success" @click="pay()">付款</button>
            </form>
        </div>
    </div>
</template>
 
<script>
    import { Toast } from 'mint-ui';
    export default {
        name: "Wallet",
        data(){
            return{
                orderAmount: ''
            }
        },
        created(){
            this.orderAmount = this.$route.query.orderAmount
        },
        methods:{
            pay(){

                const _this = this
                axios.put(this.$store.state.globalhost+'order-service/buyer/order/pay/'+JSON.parse(window.localStorage.getItem('access-user')).userId+'/'+this.$route.query.orderId).then(function (resp) {
                    let instance = Toast('支付成功');
                    setTimeout(() => {
                        instance.close();
                        _this.$router.push('/orderDetail?orderId='+_this.$route.query.orderId)
                    }, 500);
                })
            }
        }
    }
</script>

<style scoped>
    .spanright{
        display: inline-block;
        float: right;
    }
</style>
