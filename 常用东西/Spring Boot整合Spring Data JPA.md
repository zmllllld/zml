### Spring Boot整合Spring Data JPA

Spring Data JPA是Spring全家桶的一个持久层解决方案

1.pom.xml导入依赖



### 2.数据校验

给项目添加拦截器，拦截所有请求，并对请求进行各种校验

Spring Security，Shiro

认证，授权，防护，资源权限管理

将校验逻辑分发到不同的过滤器，一个过滤器负责处理一种认证方式，

UsernamePassword 过滤器验证用户和密码

1.pom.xml添加依赖

```java
<!--        spring security-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```

页面：index，admin

角色：ADMIN,USER

权限：ADMIN两个页面都可访问

user只能访问user