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

    private static long tokenExpiration = 1000 * 60*60;
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
