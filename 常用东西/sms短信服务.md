### sms短信服务

sms短信服务是系统随机生成的验证码和手机号，系统将验证码和手机号传入到sms短信接口,短信接口接收到参数后，将验证码发送到指定的手机号，并返回状态码，给系统响应是否成功发送，随后系统将验证码再存入到redis中一份，当用户输入验证码后，与redis中的验证码进行检验是否一致。sms短信服务的作用就是：给某手机号发某个验证码。

1.导入sms的jar包

![image-20220422141528753](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220422141528753.png)

```
在idea控制台运行：
D:\JavaStudyResource\StudyVip\Springboot_demo01>mvn install:install-file -DgroupId=com.jdwx -Dartifact
Id=my_sms -Dversion=1.0 -Dpackaging=jar -Dfile=D:\JavaStudyResource\Software\SpringCloud_Software\sms-
1.0.jar(jar包存放路径)

```

```
   <dependencies>
<!--        短信接口-->
        <dependency>
            <groupId>com.jdwx</groupId>
            <artifactId>my_sms</artifactId>
            <version>1.0</version>
        </dependency>


<!--将数据转换为Map格式-->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.8.6</version>
        </dependency>
<!--        common service-->
        <dependency>
            <groupId>com.ishang</groupId>
            <artifactId>common-service</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>

<!--        Redis-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

    </dependencies>
```

1.访问京东万象网站API短信,可以挑选短信接口

![image-20220422134956785](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220422134956785.png)

2.将API接口请求事例中的参数配置到application.yml中

![image-20220422135153540](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220422135153540.png)

![image-20220422135257574](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220422135257574.png)

2.创建一个工具类读取配置文件里的信息，并且可以通过类名进行调用

```java
package com.ishang.util;


import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Setter
@Component
@ConfigurationProperties(prefix = "jdwx")
public class SmsUtil implements InitializingBean {
//    sms将application.yml的配置信息读取到
    private String url;
    private String appkey;
    private String sign;

//    读取到的信息转为静态变量，通过类即可调用
    public static String Url;
    public static String Appkey;
    public static String Sign;


//    将读取到的配置信息赋给静态变量
    @Override
    public void afterPropertiesSet() throws Exception {
        Url = this.url;
        Appkey = this.appkey;
        Sign = this.sign;

    }
}
```

3.创建RandomUtil工具类，随机生成验证码

```java
package com.ishang.util;

import java.text.DecimalFormat;
import java.util.Random;

public class RandomUtil {

    private static final Random random = new Random();

    private static final DecimalFormat fourdf = new DecimalFormat("0000");

    private static final DecimalFormat sixdf = new DecimalFormat("000000");

    //生成四位的随机数字
    public static String getFourBitRandom() {
        return fourdf.format(random.nextInt(10000));
    }

    //生成六位的随机数字
    public static String getSixBitRandom() {
        return sixdf.format(random.nextInt(1000000));
    }

}
```

4.SmsController

```java
package com.ishang.controller;


import com.ishang.exception.ShopException;
import com.ishang.result.ResponseEnum;
import com.ishang.service.SmsService;
import com.ishang.util.RandomUtil;
import com.ishang.util.RegexValidateUtil;
import com.ishang.util.ResultVOUtil;
import com.ishang.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/send/{mobile}")
    public ResultVO send(@PathVariable("mobile") String mobile){
//        验证手机号是否合格
        boolean b = RegexValidateUtil.checkMobile(mobile);
        if (!b){
            throw new ShopException(ResponseEnum.MOBILE_ERROR.getMsg());
        }
// 生成验证码
        String code = RandomUtil.getSixBitRandom();
//        调用service层的jdwx发送验证码
        boolean send = this.smsService.send(mobile,code);
        if(send){
//返回true时，将验证码给redis存一份
            this.redisTemplate.opsForValue().set("uushop-sms-code"+mobile,code);
            return ResultVOUtil.success("短信发送成功");
        }
        return ResultVOUtil.fail("短信发送失败");
    }
}
```

![image-20220422141744632](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220422141744632.png)

2.SmsServiceImpl

```java
package com.ishang.service.impl;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ishang.exception.ShopException;
import com.ishang.result.ResponseEnum;
import com.ishang.service.SmsService;
import com.ishang.util.SmsUtil;
import com.wxapi.WxApiCall.WxApiCall;
import com.wxapi.model.RequestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Override
    public boolean send(String mobile, String code) {
        RequestModel model = new RequestModel();
        model.setGwUrl(SmsUtil.Url);
        model.setAppkey(SmsUtil.Appkey);
        Map queryMap = new HashMap();
        queryMap.put("sign",SmsUtil.Sign); //访问参数
        queryMap.put("mobile",mobile); //访问参数
        queryMap.put("content","您本次的验证码是："+code); //访问参数
        model.setQueryParams(queryMap);
        try {
            WxApiCall call = new WxApiCall();
            call.setModel(model);
            call.request();
            String request = call.request();
            Gson gson = new Gson();
            Map<String,String> map = gson.fromJson(request,
                    new TypeToken<Map<String,String>>(){}.getType());
            System.out.println(map);
//            如果获取状态码为10010，则返回true
            if(map.get("code").equals("10010"))return true;
        } catch (JsonSyntaxException e) {
            log.error("短信调用失败！");
            throw new ShopException(ResponseEnum.SMS_SEND_ERROR.getMsg());
        }
        return false;
    }
}
```







验证验证码

![image-20220422142752471](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220422142752471.png)