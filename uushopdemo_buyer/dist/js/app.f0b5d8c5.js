(function(t){function e(e){for(var s,o,r=e[0],c=e[1],u=e[2],l=0,d=[];l<r.length;l++)o=r[l],Object.prototype.hasOwnProperty.call(a,o)&&a[o]&&d.push(a[o][0]),a[o]=0;for(s in c)Object.prototype.hasOwnProperty.call(c,s)&&(t[s]=c[s]);m&&m(e);while(d.length)d.shift()();return n.push.apply(n,u||[]),i()}function i(){for(var t,e=0;e<n.length;e++){for(var i=n[e],s=!0,o=1;o<i.length;o++){var c=i[o];0!==a[c]&&(s=!1)}s&&(n.splice(e--,1),t=r(r.s=i[0]))}return t}var s={},a={app:0},n=[];function o(t){return r.p+"js/"+({about:"about"}[t]||t)+"."+{about:"236ce1c3"}[t]+".js"}function r(e){if(s[e])return s[e].exports;var i=s[e]={i:e,l:!1,exports:{}};return t[e].call(i.exports,i,i.exports,r),i.l=!0,i.exports}r.e=function(t){var e=[],i=a[t];if(0!==i)if(i)e.push(i[2]);else{var s=new Promise((function(e,s){i=a[t]=[e,s]}));e.push(i[2]=s);var n,c=document.createElement("script");c.charset="utf-8",c.timeout=120,r.nc&&c.setAttribute("nonce",r.nc),c.src=o(t);var u=new Error;n=function(e){c.onerror=c.onload=null,clearTimeout(l);var i=a[t];if(0!==i){if(i){var s=e&&("load"===e.type?"missing":e.type),n=e&&e.target&&e.target.src;u.message="Loading chunk "+t+" failed.\n("+s+": "+n+")",u.name="ChunkLoadError",u.type=s,u.request=n,i[1](u)}a[t]=void 0}};var l=setTimeout((function(){n({type:"timeout",target:c})}),12e4);c.onerror=c.onload=n,document.head.appendChild(c)}return Promise.all(e)},r.m=t,r.c=s,r.d=function(t,e,i){r.o(t,e)||Object.defineProperty(t,e,{enumerable:!0,get:i})},r.r=function(t){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(t,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(t,"__esModule",{value:!0})},r.t=function(t,e){if(1&e&&(t=r(t)),8&e)return t;if(4&e&&"object"===typeof t&&t&&t.__esModule)return t;var i=Object.create(null);if(r.r(i),Object.defineProperty(i,"default",{enumerable:!0,value:t}),2&e&&"string"!=typeof t)for(var s in t)r.d(i,s,function(e){return t[e]}.bind(null,s));return i},r.n=function(t){var e=t&&t.__esModule?function(){return t["default"]}:function(){return t};return r.d(e,"a",e),e},r.o=function(t,e){return Object.prototype.hasOwnProperty.call(t,e)},r.p="/",r.oe=function(t){throw console.error(t),t};var c=window["webpackJsonp"]=window["webpackJsonp"]||[],u=c.push.bind(c);c.push=e,c=c.slice();for(var l=0;l<c.length;l++)e(c[l]);var m=u;n.push([0,"chunk-vendors"]),i()})({0:function(t,e,i){t.exports=i("56d7")},"034f":function(t,e,i){"use strict";i("85ec")},4e3:function(t,e,i){},"4c7b":function(t,e,i){"use strict";i("8e81")},"56d7":function(t,e,i){"use strict";i.r(e);i("e260"),i("e6cf"),i("cca6"),i("a79d");var s=i("2b0e"),a=(i("d3b7"),i("bc3a")),n=i.n(a),o={},r=n.a.create(o);r.interceptors.request.use((function(t){return t}),(function(t){return Promise.reject(t)})),r.interceptors.response.use((function(t){return t}),(function(t){return Promise.reject(t)})),Plugin.install=function(t,e){t.axios=r,window.axios=r,Object.defineProperties(t.prototype,{axios:{get:function(){return r}},$axios:{get:function(){return r}}})},s["default"].use(Plugin);Plugin;var c=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{attrs:{id:"app"}},[i("mt-header",{attrs:{title:"UU优选",id:"head"}},[i("mt-button",{attrs:{slot:"left",icon:"back"},on:{click:t.back},slot:"left"},[t._v("返回")])],1),i("router-view"),i("nav",{staticClass:"mui-bar mui-bar-tab",attrs:{id:"bar-tab"}},[i("div",{staticClass:"mui-tab-item",on:{click:function(e){t.$store.state.index=1,t.$router.push("/cart")}}},[i("span",{staticClass:"mui-icon mui-icon-compose",class:{"mui-active":1==t.$store.state.index}}),i("span",{staticClass:"mui-tab-label",class:{"mui-text-active":1==t.$store.state.index}},[t._v("购买")])]),i("div",{staticClass:"mui-tab-item",on:{click:function(e){t.$store.state.index=2,t.$router.push("/order")}}},[i("span",{staticClass:"mui-icon mui-icon-bars",class:{"mui-active":2==t.$store.state.index}}),i("span",{staticClass:"mui-tab-label",class:{"mui-text-active":2==t.$store.state.index}},[t._v("订单")])]),i("div",{staticClass:"mui-tab-item",on:{click:function(e){t.$store.state.index=3,t.$router.push("/mine")}}},[i("span",{staticClass:"mui-icon mui-icon-person",class:{"mui-active":3==t.$store.state.index}},[i("span",{staticClass:"mui-badge"},[t._v("1")])]),i("span",{staticClass:"mui-tab-label",class:{"mui-text-active":3==t.$store.state.index}},[t._v("我的")])])])],1)},u=[],l={name:"App",data:function(){return{isSelected:"1",isActive:!1,value:""}},methods:{back:function(){history.go(-1)}}},m=l,d=(i("034f"),i("2877")),p=Object(d["a"])(m,c,u,!1,null,null,null),f=p.exports,v=(i("3ca3"),i("ddb0"),i("8c4f")),b=function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"home"},[s("img",{attrs:{alt:"Vue logo",src:i("cf05")}}),s("HelloWorld",{attrs:{msg:"Welcome to Your Vue.js App"}})],1)},h=[],g=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"hello"},[i("h1",[t._v(t._s(t.msg))]),t._m(0),i("h3",[t._v("Installed CLI Plugins")]),t._m(1),i("h3",[t._v("Essential Links")]),t._m(2),i("h3",[t._v("Ecosystem")]),t._m(3)])},_=[function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("p",[t._v(" For a guide and recipes on how to configure / customize this project,"),i("br"),t._v(" check out the "),i("a",{attrs:{href:"https://cli.vuejs.org",target:"_blank",rel:"noopener"}},[t._v("vue-cli documentation")]),t._v(". ")])},function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("ul",[i("li",[i("a",{attrs:{href:"https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-babel",target:"_blank",rel:"noopener"}},[t._v("babel")])]),i("li",[i("a",{attrs:{href:"https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-router",target:"_blank",rel:"noopener"}},[t._v("router")])]),i("li",[i("a",{attrs:{href:"https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-vuex",target:"_blank",rel:"noopener"}},[t._v("vuex")])])])},function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("ul",[i("li",[i("a",{attrs:{href:"https://vuejs.org",target:"_blank",rel:"noopener"}},[t._v("Core Docs")])]),i("li",[i("a",{attrs:{href:"https://forum.vuejs.org",target:"_blank",rel:"noopener"}},[t._v("Forum")])]),i("li",[i("a",{attrs:{href:"https://chat.vuejs.org",target:"_blank",rel:"noopener"}},[t._v("Community Chat")])]),i("li",[i("a",{attrs:{href:"https://twitter.com/vuejs",target:"_blank",rel:"noopener"}},[t._v("Twitter")])]),i("li",[i("a",{attrs:{href:"https://news.vuejs.org",target:"_blank",rel:"noopener"}},[t._v("News")])])])},function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("ul",[i("li",[i("a",{attrs:{href:"https://router.vuejs.org",target:"_blank",rel:"noopener"}},[t._v("vue-router")])]),i("li",[i("a",{attrs:{href:"https://vuex.vuejs.org",target:"_blank",rel:"noopener"}},[t._v("vuex")])]),i("li",[i("a",{attrs:{href:"https://github.com/vuejs/vue-devtools#vue-devtools",target:"_blank",rel:"noopener"}},[t._v("vue-devtools")])]),i("li",[i("a",{attrs:{href:"https://vue-loader.vuejs.org",target:"_blank",rel:"noopener"}},[t._v("vue-loader")])]),i("li",[i("a",{attrs:{href:"https://github.com/vuejs/awesome-vue",target:"_blank",rel:"noopener"}},[t._v("awesome-vue")])])])}],y={name:"HelloWorld",props:{msg:String}},j=y,C=(i("7def"),Object(d["a"])(j,g,_,!1,null,"1935ec24",null)),x=C.exports,k={name:"HomeView",components:{HelloWorld:x}},$=k,w=Object(d["a"])($,b,h,!1,null,null,null),E=w.exports,q=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"mui-content"},[i("div",[i("img",{staticClass:"logo",attrs:{src:t.logo,alt:""}})]),i("div",{staticClass:"mui-row"},[i("div",{staticClass:"mui-col-xs-3",staticStyle:{"border-right":"1px solid #c8c7cc"}},[i("div",{staticClass:"mui-segmented-control mui-segmented-control-inverted mui-segmented-control-vertical",attrs:{id:"segmentedControls"}},[i("a",{staticClass:"mui-control-item",class:{"menu-active":1==t.menuIndex},on:{click:function(e){t.menuIndex=1}}},[t._v("生鲜")]),i("a",{staticClass:"mui-control-item",class:{"menu-active":2==t.menuIndex},on:{click:function(e){t.menuIndex=2}}},[t._v("手机")]),i("a",{staticClass:"mui-control-item",class:{"menu-active":3==t.menuIndex},on:{click:function(e){t.menuIndex=3}}},[t._v("食品")])])]),i("div",{staticClass:"mui-col-xs-9",attrs:{id:"segmentedControlContents"}},[i("div",[i("div",{staticClass:"itembox mui-row"},[t._m(0),i("div",{staticClass:"mui-col-xs-9"},[t._m(1),i("div",{staticClass:"operation"},[i("div",{staticClass:"operationPrice"},[t._v("￥82.60")]),i("div",{staticClass:"operationSelect"},[i("span",{staticClass:"mui-icon mui-icon-minus operationMinus",on:{click:function(e){return t.changeQuantity(t.item,"minus")}}}),i("span",[t._v("0")]),i("span",{staticClass:"mui-icon mui-icon-plus operationPlus",on:{click:function(e){return t.changeQuantity(t.item,"plus")}}})])])])]),i("div",{staticClass:"itembox mui-row"},[t._m(2),i("div",{staticClass:"mui-col-xs-9"},[t._m(3),i("div",{staticClass:"operation"},[i("div",{staticClass:"operationPrice"},[t._v("￥72.80")]),i("div",{staticClass:"operationSelect"},[i("span",{staticClass:"mui-icon mui-icon-minus operationMinus",on:{click:function(e){return t.changeQuantity(t.item,"minus")}}}),i("span",[t._v("0")]),i("span",{staticClass:"mui-icon mui-icon-plus operationPlus",on:{click:function(e){return t.changeQuantity(t.item,"plus")}}})])])])])]),i("div",{staticClass:"itembox mui-row"},[t._m(4),i("div",{staticClass:"mui-col-xs-9"},[t._m(5),i("div",{staticClass:"operation"},[i("div",{staticClass:"operationPrice"},[t._v("￥72.80")]),i("div",{staticClass:"operationSelect"},[i("span",{staticClass:"mui-icon mui-icon-minus operationMinus",on:{click:function(e){return t.changeQuantity(t.item,"minus")}}}),i("span",[t._v("0")]),i("span",{staticClass:"mui-icon mui-icon-plus operationPlus",on:{click:function(e){return t.changeQuantity(t.item,"plus")}}})])])])])])])])},P=[function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"mui-col-xs-3"},[i("img",{attrs:{src:"https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/150370/24/6079/129012/5fa4bebaEb7aea500/0ceaa6b9e0ec073d.jpg!q80.dpg.webp"}})])},function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"item"},[i("div",{staticClass:"itemmain"},[t._v(" 生鲜大虾 ")]),i("div",{staticClass:"itemsub"},[t._v(" 鲜活冷冻深海虾 ")])])},function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"mui-col-xs-3"},[i("img",{attrs:{src:"https://img14.360buyimg.com/n0/jfs/t1/145364/15/3380/155346/5f153f1dEeeaeb990/39984daed5369e91.jpg"}})])},function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"item"},[i("div",{staticClass:"itemmain"},[t._v(" 舟山海捕新鲜带鱼中段 ")]),i("div",{staticClass:"itemsub"},[t._v(" 五斤装特大段 ")])])},function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"mui-col-xs-3"},[i("img",{attrs:{src:"https://img14.360buyimg.com/n0/jfs/t1/145364/15/3380/155346/5f153f1dEeeaeb990/39984daed5369e91.jpg"}})])},function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"item"},[i("div",{staticClass:"itemmain"},[t._v(" 舟山海捕新鲜带鱼中段 ")]),i("div",{staticClass:"itemsub"},[t._v(" 五斤装特大段 ")])])}],O=(i("a434"),i("b0c0"),i("76a0")),A=i.n(O),Q={name:"Cart",data:function(){return{totalQuantity:0,totalPrice:0,menuIndex:0,logo:"../static/logo.jpg",data:[{name:"生鲜",type:1,goods:[{quantity:0,id:1,name:"生鲜大虾",price:82.6,stock:991,description:"鲜活冷冻深海虾",icon:"https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/150370/24/6079/129012/5fa4bebaEb7aea500/0ceaa6b9e0ec073d.jpg!q80.dpg.webp"},{quantity:0,id:2,name:"舟山海捕新鲜带鱼中段",price:72.8,stock:5,description:"五斤装特大段",icon:"https://img14.360buyimg.com/n0/jfs/t1/145364/15/3380/155346/5f153f1dEeeaeb990/39984daed5369e91.jpg"},{quantity:0,id:3,name:"帝王蟹",price:105,stock:197,description:"帝王蟹3.2～2.8斤·礼盒装",icon:"https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/162387/1/12528/235896/604e2f4eE2c6465f9/9726751899a4f735.jpg!q80.dpg.webp"},{quantity:0,id:4,name:"迷你小八爪鱼",price:67.2,stock:63,description:"新鲜海捕，三去工艺",icon:"https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/89654/16/18615/151102/5e96b4d5E0b6c7dc5/97436995c490dd20.jpg!q80.dpg.webp"},{quantity:0,id:5,name:"福建新鲜活冻大鲍鱼大",price:69,stock:1085,description:"火锅食材贝类 生鲜",icon:"https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/171918/6/4940/300074/607aa8ebEf9702ba0/6cff9c166521d87c.jpg!q80.dpg.webp"},{quantity:0,id:129,name:"生鲜大虾123",price:82.6,stock:8,description:"鲜活冷冻深海虾",icon:"https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/150370/24/6079/129012/5fa4bebaEb7aea500/0ceaa6b9e0ec073d.jpg!q80.dpg.webp"}]},{name:"手机",type:2,goods:[{quantity:0,id:6,name:"Redmi 9A 5000mAh大电量",price:599,stock:91,description:"游戏智能手机",icon:"https://img12.360buyimg.com/n1/s450x450_jfs/t1/118064/27/12885/59959/5f17b7efE453f688d/5b33ac76b2aaea9b.jpg"},{quantity:0,id:7,name:"Redmi Note 9 4G大",price:1099,stock:88,description:"游戏智能手机",icon:"https://img14.360buyimg.com/n0/jfs/t1/143051/26/15747/73035/5fbdd54cE80757a48/67c387e1d0dc5c83.jpg"},{quantity:0,id:10,name:"OPPO A52 8GB+128GB大",price:1349,stock:650,description:"拍照游戏智能手机",icon:"https://img14.360buyimg.com/n1/s450x450_jfs/t1/155704/14/2313/76251/5fe764dfE36d34fcd/85141b61f6a5196e.jpg"}]},{name:"食品",type:3,goods:[{quantity:0,id:12,name:"方便面干吃面大 ",price:5.1,stock:77,description:"严选工厂·专业质检",icon:"https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/134875/9/10819/349935/5f6c137aEfe86c034/5823bc25107b0d33.jpg!q80.dpg.webp"},{quantity:0,id:13,name:"酸辣无骨鸡爪",price:5.9,stock:95,description:"泡椒凤爪 酸辣柠檬",icon:"https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/167700/8/18748/219167/60799855E870b4a6a/ab6d827a71da2e21.jpg!q80.dpg.webp"},{quantity:0,id:17,name:"原味盐焗坚果干果",price:13.9,stock:105,description:"盐焗开心果1罐装",icon:"https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/103668/15/17528/180008/5e86f0d5E4388cccf/1cd7feb9bb083a06.jpg!q80.dpg.webp"}]}]}},created:function(){this.$store.state.index=1,this.$store.state.selectedArray=[];var t=this;axios.get(this.$store.state.globalhost+"product-service/buyer/product/list").then((function(e){t.data=e.data.data}))},methods:{submit:function(){if(0!=this.$store.state.selectedArray.length)this.$router.push("/info");else{var t=Object(O["Toast"])("请选择商品");setTimeout((function(){t.close()}),1e3)}},clear:function(){this.totalQuantity=0,this.totalPrice=0;for(var t=0;t<this.data.length;t++)for(var e=this.data[t],i=0;i<e.foods.length;i++)e.foods[i].quantity=0;this.$store.state.selectedArray=[]},changeQuantity:function(t,e){switch(e){case"minus":if(0==t.quantity)return;t.quantity--,this.totalQuantity--,this.totalPrice-=t.price;for(var i=0;i<this.$store.state.selectedArray.length;i++)if(t.id==this.$store.state.selectedArray[i].productId)return this.$store.state.selectedArray[i].productQuantity=t.quantity,void(0==t.quantity&&this.$store.state.selectedArray.splice(i,1));break;case"plus":if(t.quantity++,!(t.quantity<=t.stock)){t.quantity=t.stock;var s=Object(O["Toast"])(t.name+"已被你抢空！");return void setTimeout((function(){s.close()}),1e3)}this.totalQuantity++,this.totalPrice+=t.price;for(i=0;i<this.$store.state.selectedArray.length;i++)if(t.id==this.$store.state.selectedArray[i].productId)return void(this.$store.state.selectedArray[i].productQuantity=t.quantity);this.$store.state.selectedArray.push({productId:t.id,productQuantity:t.quantity});break}}}},I=Q,S=(i("4c7b"),Object(d["a"])(I,q,P,!1,null,"72385742",null)),T=S.exports;s["default"].use(v["a"]);var M=[{path:"/cart",component:T},{path:"/",name:"home",component:E},{path:"/about",name:"about",component:function(){return i.e("about").then(i.bind(null,"d7a9"))}}],H=new v["a"]({mode:"history",base:"/",routes:M}),L=H,W=i("2f62");s["default"].use(W["a"]);var G=new W["a"].Store({state:{index:2},getters:{},mutations:{},actions:{},modules:{}});i("aa35"),i("4000");s["default"].config.productionTip=!1,s["default"].use(A.a),new s["default"]({router:L,store:G,render:function(t){return t(f)}}).$mount("#app")},"7def":function(t,e,i){"use strict";i("8e33")},"85ec":function(t,e,i){},"8e33":function(t,e,i){},"8e81":function(t,e,i){},cf05:function(t,e,i){t.exports=i.p+"img/logo.82b9c7a5.png"}});
//# sourceMappingURL=app.f0b5d8c5.js.map