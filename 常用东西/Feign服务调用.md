### Feign服务调用

- **当以上功能的实现需要调用其他模块的接口时，可以用feign进行调用**

1.导入依赖

```
<!--        feign 服务调用-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <version>2.2.2.RELEASE</version>
        </dependency>
```

2.创建feign接口，添加注解@FeignClient("被调用的服务名")

3.将需要调用的接口方法名和映射路径添加进来,路径为全路径

![image-20220417130453230](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220417130453230.png)

4.在启动类添加注解@EnableFeignClients