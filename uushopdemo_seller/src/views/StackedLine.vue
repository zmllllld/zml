<template>
    <div id="myChart" :style="{width: '1000px', height: '550px',marginTop:'30px'}"></div>
</template>

<script>
    export default {
        name: "StackedLine",
        mounted(){
            let _this = this
            axios.get(this.$store.state.globalhost+'order-service/seller/order/stackedLineSale').then(function (response) {
                _this.drawLine(response.data.data);
            })
        },
        methods: {
            drawLine(data){
                // 基于准备好的dom，初始化echarts实例
                let myChart = this.$echarts.init(document.getElementById('myChart'))
                // 绘制图表
                myChart.setOption({
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: data.names
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    toolbox: {
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: data.dates
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: data.datas
                });
            }
        }
    }
</script>

<style scoped>

</style>