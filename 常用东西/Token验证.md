### Token验证

JwtUtil

```java
package com.ishang.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

public class JwtUtil {

//    定义令牌有效期时长 1000为毫秒=1s              1s   1min  1h   1天
    //    private static long tokenExpiration = 1000 * 60 * 60 * 24;

    private static long tokenExpiration = 1000 * 60;
    private static String tokenSignKey = "a1d23mi789n";

    public static String createToken(Integer id,String mobile){
        String token = Jwts.builder()
                //载荷：自定义信息
                .claim("id", id)
                .claim("mobile", mobile)
                //载荷：默认信息
                .setSubject("uushop-user") //令牌主题，可以自定义
                .setExpiration(new Date(System.currentTimeMillis()+tokenExpiration)) //过期时间
                .setId(UUID.randomUUID().toString())
                //签名哈希
                .signWith(SignatureAlgorithm.HS256, tokenSignKey)
                //组装jwt字符串
                .compact();
        return token;
    }

    public static boolean checkToken(String token){
        if(StringUtils.isEmpty(token)){
            return false;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}

```

controller：

生成token

```java
//        生成token
        String token = JwtUtil.createToken(one.getAdminId(), one.getUsername());
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(one,adminVO);
        adminVO.setToken(token);
        return ResultVOUtil.success(adminVO);
```

token验证：

```java
@ApiOperation("Token验证")
@GetMapping("/checkToken/{token}")
public ResultVO checkToken(@PathVariable("token") String token){
    boolean b = JwtUtil.checkToken(token);
    if(!b){
        throw new ShopException(ResponseEnum.TOKEN_ERRO.getMsg());
    }
    return ResultVOUtil.success(null);

}
```

没有前端对接的时候，这样写，以便接口测试

![image-20220516163134581](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220516163134581.png)

和前端对接后







前端router的index.js

```vue
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
```

AdminController

```java
//        生成token
        String token = JwtUtil.createToken(one.getAdminId(), one.getUsername());
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(one,adminVO);
        adminVO.setToken(token);
        return ResultVOUtil.success(adminVO);
    }

    @ApiOperation("Token验证")
    @GetMapping("/checkToken")
    public ResultVO checkToken(HttpServletRequest request){
        String token = request.getHeader("token");
        boolean result = JwtUtil.checkToken(token);
        if(!result){
            throw new ShopException(ResponseEnum.TOKEN_ERRO.getMsg());
        }
        return ResultVOUtil.success(null);

    }
```

