application.yml

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/pay?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
  
  security:
    user:
      name: admin
      password: 123123
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  mapper-locations: com/southwind/mapper/*.xml
server:
  servlet:
    context-path: /springboot  //设置启动路径
```



### 取配置文件的值

![image-20220324173703224](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220324173703224.png)

通过@Value("${k值}")可以取出，

例如

```
@Value("{server.name}")
private String servername;
```

@Value("{server.name}")

![image-20220324173853672](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220324173853672.png)