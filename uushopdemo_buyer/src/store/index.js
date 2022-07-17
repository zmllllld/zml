import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    //选中的底部菜单
    index:2,
    //设置全局访问参数
    globalhost:'http://localhost:8686/',
    //当前选中的商品集合
    selectedArray: []
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
  }
})
