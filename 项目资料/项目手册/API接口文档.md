# 买家端

## Product

### 商品列表

```
GET /buyer/product/list
```

参数

```
无
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": [
        {
            "name": "生鲜",
            "type": 1,
            "goods": [
                {
                    "id": "2",
                    "name": "生鲜大虾",
                    "price": 3.5,
                    "description": "很好吃",
                    "icon": "http://5b0988e595225.cdn.sohucs.com/images/20181101/225d20df2bc14cefb363686d90d47004.jpeg",
                    "quantity": 0,
                    "stock": 200
                }
            ]
        },
        {
            "name": "手机",
            "type": 2,
            "goods": [
                {
                    "id": "1",
                    "name": "华为",
                    "price": 3350,
                    "description": "好好好",
                    "icon": "http://n.sinaimg.cn/sinacn10/320/w640h480/20180827/5682-hifuvph4790369.jpg",
                    "quantity": 0,
                    "stock": 200
                }
            ]
        }
    ]
}
```

### 根据 ID 查询商品价格

```
GET /buyer/product/findPriceById
```

参数

```json
id: 1
```

返回

```json
100
```

### 通过 ID 查询商品

```
GET /buyer/product/findById
```

参数

```
id: 1
```

返回

```json
{
  "productId": 1,
  "productName": "肉夹馍",
  "productPrice": 16.5,
  "productStock": 89,
  "productDescription": "好吃好吃",
  "productIcon": "https://s1.st.meishij.net/rs/50/123/6030800/n6030800_152708155351112.jpg",
  "categoryType": 2,
  "createTime": "2020-01-07T23:45:33",
  "updateTime": "2021-04-19T16:17:52",
  "productStatus": 1
}
```

### 减库存

```
PUT /buyer/product/subStockById
```

参数

```
id: 1
quantity: 1
```

返回

```json
true
```



## Order

### 创建订单

**(创建的订单涉及主从表的存储，商品库存的修改，电话号码的校验，事务控制，计算价格)**

```
POST /buyer/order/create
```

参数

```json
{
  	"name": "张三"
   	"phone": "13678987656"
    "address": "科技路"
    "id": 11
    "items": [{
        "productId": 1,
        "productQuantity": 2
    }]
}
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
      "orderId": "f959ca203b237255ec76926b8c12c6c8" 
  }
}
```

### 订单列表

```
GET /buyer/order/list
```

参数

```
buyerId: 1
page: 1 
size: 10
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": [
    {
      "orderId": "161873371171128075",
      "buyerName": "张三",
      "buyerPhone": "18868877111",
      "buyerAddress": "科技路",
      "buyerOpenid": 1,
      "orderAmount": 0,
      "orderStatus": 0,
      "payStatus": 0,
      "createTime": "2021-06-06T15:01:27",
      "updateTime": "2021-06-06T15:01:27"
    },
    {
      "orderId": "161873371171128076",
      "buyerName": "张三",
      "buyerPhone": "18868877111",
      "buyerAddress": "科技路",
      "buyerOpenid": 1,
      "orderAmount": 0,
      "orderStatus": 0,
      "payStatus": 0,
      "createTime": "2021-06-06T15:01:27",
      "updateTime": "2021-06-06T15:01:27"
    }]
}
```

### 查询订单详情

```
GET /buyer/order/detail
```

参数

```
buyerId: 1
orderId: f959ca203b237255ec76926b8c12c6c8
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": {
          "orderId": "161899085773669363",
          "buyerName": "李四",
          "buyerPhone": "18868877111",
          "buyerAddress": "科技路",
          "buyerOpenid": 1,
          "orderAmount": 18,
          "orderStatus": 0,
          "payStatus": 0,
          "createTime": "2021-06-06T14:56:56",
          "updateTime": "2021-06-06T15:01:27",
          "orderDetailList": [
            {
                "detailId": "78bac05d2716c7535a953c42ec6416df",
                "orderId": "f959ca203b237255ec76926b8c12c6c8",
                "productId": 1,
                "productName": "生鲜大虾",
                "productPrice": 82.6,
                "productQuantity": 10,
                "productIcon": "https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/150370/24/6079/129012/5fa4bebaEb7aea500/0ceaa6b9e0ec073d.jpg!q80.dpg.webp"
            }
        ]
    }
}
```

### 取消订单

```
PUT /buyer/order/cancel
```

参数

```
buyerId: 1
orderId: f959ca203b237255ec76926b8c12c6c8
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```

### 完结订单

```
PUT /buyer/order/finish
```

参数

```
orderId: f959ca203b237255ec76926b8c12c6c8
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```

### 支付订单

```
PUT /buyer/order/pay
```

参数

```
buyerId: 1
orderId: f959ca203b237255ec76926b8c12c6c8
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```



## User

### 注册用户

```
POST /user/register
```

参数

```
{
  "code": "333666",
  "mobile": "13312345678",
  "password": "123456"
}
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```

### 用户登录

```
GET /user/login
```

参数

```
{
  "mobile": "13312345678",
  "password": "123456"
}
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "userId": 12,
    "mobile": "13312345678",
    "password": "c1df1b61022d11d41829f63a08ff5824427f40af83919878",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTIsIm1vYmlsZSI6IjEzMzMzMzMzMzMyIiwic3ViIjoidXVzaG9wLXVzZXIiLCJleHAiOjE2MjA1NTEzNzgsImp0aSI6ImU1OGU5NTkxLWU4OTMtNDI4MC04MDFlLTUxN2FkZDc2ODgwNCJ9.3U4_3wWlw1g5P4YmyatBDJ1cRY2ecPdcRT7oYIIXvjk"
  }
}
```

### Token验证

```
GET /user/checkToken
```

参数

```
无
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```



## SMS

### 发送短信验证码

```
GET /sms/send
```

参数

```
mobile: 13312345678
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": "短信发送成功！"
}
```

```
D:\JavaStudyResource\StudyVip\Springboot_demo01>mvn install:install-file -DgroupId=com.jdwx -Dartifact
Id=my_sms -Dversion=1.0 -Dpackaging=jar -Dfile=D:\JavaStudyResource\Software\SpringCloud_Software\sms-
1.0.jar

```



# 卖家端

## Product

### 查询所有商品分类

```
GET /seller/product/findAllProductCategory
```

参数

```
无
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "content":[
      {
        "name": "热销榜",
        "type": 2,
        "goods": null
      },
      {
        "name": "夜宵",
        "type": 3,
        "goods": null
      },
      {
        "name": "午餐",
        "type": 10,
        "goods": null
      },
      {
        "name": "小吃",
        "type": 11,
        "goods": null
      },
      {
        "name": "早餐",
        "type": 1,
        "goods": null
      }
    ]
  }
```

### 添加商品

```
POST /seller/product/add
```

参数

```json
{
  "categoryType": 2,
  "productDescription": "好吃好吃",
  "productIcon": "https://timgsa.baidu.com/timg",
  "productName": "牛肉汉堡",
  "productPrice": 22.5,
  "productStatus": 0,
  "productStock": 100
}
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```

### 查询商品

```
GET /seller/product/list
```

参数

```
page: 1
size: 10
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "content": [
      {
        "status": true,
        "id": 1,
        "name": "肉夹馍",
        "price": 16,
        "stock": 107,
        "description": "好吃好吃",
        "icon": "https://s1.st.meishij.net/rs/50/123/6030800/n6030800_152708155351112.jpg",
        "categoryName": "热销榜"
      }
    ],
    "size": 5,
	"total": 11
  }
}
```

### 商品模糊查询

```
GET /seller/product/like
```

参数

```
keyWord: '大'
page: 1
size: 10
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "content": [
      {
        "status": true,
        "id": 1,
        "name": "肉夹馍",
        "price": 16,
        "stock": 107,
        "description": "好吃好吃",
        "icon": "https://s1.st.meishij.net/rs/50/123/6030800/n6030800_152708155351112.jpg",
        "categoryName": "热销榜"
      }
    ],
    "size": 5,
	"total": 11
  }
}
```

### 通过分类查询商品

```
GET /seller/product/findByCategory
```

参数

```
categoryType: 1
page: 1
size: 10
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "content": [
      {
        "status": true,
        "id": 1,
        "name": "肉夹馍",
        "price": 16,
        "stock": 107,
        "description": "好吃好吃",
        "icon": "https://s1.st.meishij.net/rs/50/123/6030800/n6030800_152708155351112.jpg",
        "categoryName": "热销榜"
      }
    ],
    "size": 5,
	"total": 11
  }
}
```

### 通过 ID 查询商品

```
GET /seller/product/findById
```

参数

```
id: 1
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "status": true,
    "id": 1,
    "name": "肉夹馍",
    "price": 16,
    "stock": 107,
    "description": "好吃好吃",
    "icon": "https://s1.st.meishij.net/rs/50/123/6030800/n6030800_152708155351112.jpg",
    "category": {
        "categoryType": 2
    }
  }
}
```

### 通过 ID 删除商品

```
DELETE /seller/product/delete
```

参数

```
id: 1
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```

### 修改商品状态

```
PUT /seller/product/updateStatus
```

参数

```json
id: 1
status: true
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": true
}
```

### 修改商品

```
PUT /seller/product/update
```

参数

```json
{
    "status": true,
    "id": 1,
    "name": "肉夹馍",
    "price": 16,
    "stock": 107,
    "description": "好吃好吃",
    "icon": "https://s1.st.meishij.net/rs/50/123/6030800/n6030800_152708155351112.jpg",
    "category": {
        "categoryId": 1,
        "categoryName": "热销榜",
        "categoryType": 2
    }
}
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```

### 导出Excel

```
PUT /seller/product/export
```

参数

```json
无
```

返回

```json
HttpServletResponse连接客户端下载工具
```

### 导入Excel

```
PUT /seller/product/import
```

参数

```json
form表单数据
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```



## Order

### 查询订单

```
GET /seller/order/list
```

参数

```
page: 1
size: 10
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "content": [
      {
        "orderId": "1579074660492865989",
        "buyerName": "小溪",
        "buyerPhone": "13567676767",
        "buyerAddress": "小寨",
        "buyerOpenid": 123,
        "orderAmount": 88,
        "orderStatus": 2,
        "payStatus": 0,
        "createTime": "2020-01-15T07:51:00.000+0000",
        "updateTime": "2020-01-25T15:49:09.000+0000"
      }
    ],
    "size": 5,
	"total": 11
  }
}
```

### 取消订单

```
PUT /seller/order/cancel
```

参数

```
orderId:"1579074660492865989"
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```

### 完结订单

```
PUT /seller/order/finish
```

参数

```
orderId:"1579074660492865989"
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```

### 柱状图

```
PUT /seller/order/barSale
```

参数

```
无
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "names": [
      "舟山海捕新鲜带鱼中段",
      "帝王蟹"
    ],
    "values": [
      {
        "value": 16,
        "itemStyle": {
          "color": "#a0a7e6"
        }
      },
      {
        "value": 5,
        "itemStyle": {
          "color": "#c4ebad"
        }
      }
    ]
  }
}
```

### 基础折线图

```
PUT /seller/order/basicLineSale
```

参数

```
无
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "names": [
      "2021-05-01",
      "2021-05-03"
    ],
    "values": [
      23,
      33
    ]
  }
}
```

### 折线图堆叠

```
PUT /seller/order/stackedLineSale
```

参数

```
无
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "names": [
      "生鲜大虾",
      "舟山海捕新鲜带鱼中段"
    ],
    "dates": [
      "2021-05-01",
      "2021-05-03"
    ],
    "datas": [
      {
        "name": "生鲜大虾",
        "type": "line",
        "stack": "销量",
        "data": [
          1,
          2
        ]
      },
      {
        "name": "舟山海捕新鲜带鱼中段",
        "type": "line",
        "stack": "销量",
        "data": [
          0,
          16
        ]
      }
    ]
  }
}
```

## Admin

### 用户登录

```
GET /admin/login
```

参数

```
{
  "username": "admin",
  "password": "123123"
}
```

返回

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "adminId": 1,
    "username": "admin",
    "password": "123123",
    "imgUrl": "https://img1.baidu.com/it/u=1026177128,203298668&fm=26&fmt=auto&gp=0.jpg",
    "name": "超级玛丽",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwibW9iaWxlIjoiYWRtaW4iLCJzdWIiOiJ1dXNob3AtdXNlciIsImV4cCI6MTYyMDU1MDc5MSwianRpIjoiMDIwMjkyNzgtYTZlZC00NDJkLWI4ZDYtMTg3N2M2MGE1NDliIn0.AO3SHTaNlz9QWMHiO51GEpr0EpR6ZfCwp02f9H97B0g"
  }
}
```

### Token验证

```
GET /admin/checkToken
```

参数

```
无
```

返回

```json
{
    "code": 0,
    "msg": "成功",
    "data": null
}
```

