<template>
    <div id="myChart" :style="{width: '1000px', height: '500px'}"></div>
</template>

<script>
    export default {
        name: "Line",
        mounted(){
            let _this = this
            axios.get(this.$store.state.globalhost+'order-service/seller/order/basicLineSale').then(function (response) {
                _this.drawLine(response.data.data);
            })
        },
        methods: {
            drawLine(data){
                // 基于准备好的dom，初始化echarts实例
                let myChart = this.$echarts.init(document.getElementById('myChart'))
                // 绘制图表
                myChart.setOption({
                    color:'#ea7e53',
                    xAxis: {
                        type: 'category',
                        data: data.names
                    },
                    tooltip: {},
                    yAxis: {
                        type: 'value'
                    },
                    series: [{
                        data: data.values,
                        type: 'line'
                    }]
                });
            }
        }
    }
</script>

<style scoped>

</style>