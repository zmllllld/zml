1.Spring Cloud

#### 1.什么是微服务

单体架构：所有业务模块全部打包到一个工程

单体应用各个业务模块耦合度太高，一个出问题，所有都无法使用，如何解决?微服务应运而生

微服务：将一个单体应用拆分成若干个小型的服务，协同完成系统功能的一种架构模式

#### Spring Cloud

实现微服务架构的框架，Spring全家桶的一部分，基于SpringBoot

SpringBoot快速构建工程的框架，SpringCloud快速构建微服务工程的框架

服务治理、配置中心、消息总线、负载均衡、熔断器、数据监控

Spring Cloud Alibaba

Spring Boot-->Spring Cloud--> Spring Cloud Alibaba

Spring Cloud Alibaba 2.2.1

Spring Cloud Hoxton.SR3

Spring Boot 2.3.0   

#### 搭建SpringCloud

​	1、创建 Spring Boot 工程，选择常用的 Lombok，Spring Cloud Alibaba 还没有完全集成到 Spring Boot Initialzr 中，我们需要手动添加。

Spring Boot ---》Spring Cloud ---》Spring Cloud Alibaba

**Spring Boot 版本修改为 2.3.0，因为高版本有 bug。**

pom.xml 中添加。

```xml
<dependencyManagement>
    <dependencies>
        <!-- Spring Cloud Hoxton -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Hoxton.SR3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!-- Spring Cloud Alibaba -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2.2.1.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```



### 2.Nacos 服务注册

服务注册，这里我们使用 Nacos 的服务注册，下载对应版本的 Nacos：https://github.com/alibaba/nacos/releases

解压，启动服务。

启动 nacos-server 

双击 bin 中的 startup.cmd 文件 

访问 http://localhost:8848/nacos/ 

使用默认的 nacos/nacos 进行登录 

Nacos 搭建成功，接下来注册服务。

在父工程路径下创建子工程，让子工程继承父工程的环境依赖

在子工程的<parent>中换为父工程的

```java
<parent>
    <groupId>com.ishang</groupId>
    <artifactId>myspringcloud_001</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
```

在子工程pom.xml 中添加 nacos 发现组件。

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

在子工程application.yml 中配置

```yaml
spring:
  cloud:
    nacos:
      discovery:
        # 指定nacos server地址
        server-addr: localhost:8848
  application:
    name: provider
```

需要引入 spring-web 依赖才能实现 Nacos 注册

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.3.0.RELEASE</version>
</dependency>
```

![image-20220329175151428](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220329175151428.png)

![image-20220329175206439](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220329175206439.png)

修改端口号即可运行多个实例

![image-20220329175233575](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220329175233575.png)

#### 2.1Nacos服务发现和调用

同样的方式先将服务注册进nacos

1.创建Module-----consumer

![image-20220329182334852](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220329182334852.png)

2.导入nacos依赖

```java
<!--nacos组件，服务注册与发现-->
<dependency>
   <groupId>com.alibaba.cloud</groupId>
   <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

将<parent>改为父工程

```java
<parent>
   <groupId>com.ishang</groupId>
   <artifactId>myspringcloud_001</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <relativePath/> <!-- lookup parent from repository -->
</parent>
```

3.配置application.yml

```java
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
  application:
    name: consumer
```

4.从consumer服务中调用provider服务

```java
import java.util.List;

@RestController
public class FindProviderController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/find")
    public List<ServiceInstance> find(){
        List<ServiceInstance> test = this.discoveryClient.getInstances("test");
        return  test;
    }
}
```

![image-20220329183421858](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220329183421858.png)





#### 2.2Nacos服务治理

包括

- 服务注册 如上图步骤
- 服务发现

服务发现利用DiscoveryClient

通过 discoveryClient 发现注册到 nacos 中的 provider 服务。

再通过Restemplate进行调用

服务提供者（被调用）：provider

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Value("${server.port}")
    private  String port;

    @RequestMapping("/find")
    public  String index(){
        return "该端口号是"+this.port;
    }

}
```



服务提供者：consumer

1.创建配置类RestemplateConfiguration

将RestTemplate注入到IOC容器中

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ResttemplateConfiguration {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate ();
    }
}
```

2.将ioc容器中的Restemplate注入，调用者在nacos中找到提供者的url，利用resttemplate实现调用

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class FindProviderController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/list")
    public List<ServiceInstance> list() {
        List<ServiceInstance> list = this.discoveryClient.getInstances("provider");


        for (ServiceInstance serviceInstance : list) {
            String host = serviceInstance.getHost();
            int port = serviceInstance.getPort();
            String url = "http://" + host + ":" + port+ "/find";
//            将调用的路径传进去，返回什么样的数据 xxx.class
            String result = this.restTemplate.getForObject(url, String.class);
//            在控制台输出被调用服务接口的内容
            System.out.println(result);
        }

        return list;
    }
}
```

![image-20220331144635771](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220331144635771.png)

![image-20220331144655225](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220331144655225.png)

![image-20220331153427636](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220331153427636.png)

### 3.Ribbon负载均衡

#### 3.1轮询策略

Ribbon 负载均衡算法默认是轮询，交替访问。

1.在Resttemplate中添加@LoadBalanced

```java
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ResttemplateConfiguration {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate ();
    }
}
```

2.不需要再获取实例，直接将服务者的名字传入，自动解析

```java
@RestController
public class FindProviderController {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;
    @RequestMapping("/list")
    public List<Account> list() {
        return this.restTemplate.getForObject("http://provider/finAll", List.class);
    }

    @RequestMapping("/index")
    public String index(){
        return this.restTemplate.getForObject("http://provider/find",String.class);
    }
}
```

服务提供者

```java
@RestController
public class IndexController {

    @Value("${server.port}")
    private  String port;

    @Autowired
    private AccountMapper accountMapper;
    @RequestMapping("/find")
    public  String index(){
        return "该端口号是"+this.port;
    }

    @RequestMapping("/finAll")
    public List<Account> findAll(){
        System.out.println("当前调用的是"+this.port);
        return this.accountMapper.selectList(null);
    }

}
```

#### 3.2随机策略

在服务调用者的application.yml中添加服务提供者的

修改为随机，在 consumer 的 application.yml 添加配置随机规则即可。注意是谁去使用ribbon谁application.yml去加。这里是consumer调用provider，所以在consumer里面加

```java
#将服务提供者的名字传进来，设置随机调用
provider:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```

#### 3.3基于Nacos权重的负载均衡

1、创建 NacosWeightedRule 类定义基于权重的负载均衡

```java
package com.ishang.configuration;


import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class NacosWeightedRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        //读取配置文件
    }

    @Override
    public Server choose(Object o) {
        ILoadBalancer loadBalancer = this.getLoadBalancer();
        BaseLoadBalancer baseLoadBalancer = (BaseLoadBalancer) loadBalancer;
        //获取要请求的微服务名称
        String name = baseLoadBalancer.getName();
        //获取服务发现的相关API
        NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
        try {
            Instance instance = namingService.selectOneHealthyInstance(name);
            log.info("选择的实例是port={},instance={}",instance.getPort(),instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

2.在application.yml中配置

```java
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
  application:
    name: consumer

server:
  port: 8484

#将服务提供者的名字传进来
provider:
  ribbon:
    NFLoadBalancerRuleClassName: com.ishang.configuration.NacosWeightedRule
```

基于权重的策略可以在某台服务器并发量过高的时候，可以争对某个服务调整权重，减少对这台服务的请求量，保护系统，防止服务器挂机。

合理分配服务器对于请求的处理，充分利用资源

### 4.Feign 声明式接口调用

使用RestTemplate调用远程接口，将调用方法写在Controller层实际上代码可读性性差，维护性也比较差。（因为在开发的时候，controller主要是调用service层的接口方法，面向接口编程，而不是把实现写在controller层，应该是封装在接口里面，直接调用接口，才符合面向接口编程的规范）

![image-20220331165119383](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220331165119383.png)

因此可以通过Feign将RestTemplate封装在接口里面，直接调用Feign接口即可

Feign是基于Ribbon负载均衡实现的一种声明式接口调用机制，同时他更加灵活，使用起来也更加简单，取代Ribbon+RestTemplate的方式来实现基于接口调用的负载均衡；比Ribbon使用起来更加简便，只需要创建接口并添加相关注解配置，即可实现服务消费的负载均衡

![image-20220331170422822](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220331170422822.png)

1.pom.xml导入依赖

```java
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```

2.创建接口Feign，将provider服务中的方法进行映射

![image-20220331170034694](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220331170034694.png)

```java
package com.ishang.feign;

import com.ishang.entity.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@FeignClient("provider")
public interface Feign {

    @RequestMapping("/find")
    public  String index();

    @RequestMapping("/finAll")
    public List<Account> findAll();
}
```

3.在consumerApplication中添加@EnableFeignClients

```java
package com.ishang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ConsumerApplication {

   public static void main(String[] args) {
      SpringApplication.run(ConsumerApplication.class, args);
   }

}
```

4.在controller进行注入调用

```java
package com.ishang.controller;


import com.ishang.entity.Account;
import com.ishang.feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class FindProviderController {
    
    @Autowired
    private Feign feign;

    @RequestMapping("/list")
    public List<Account> list() {
        return  this.feign.findAll();
    }

    @RequestMapping("/index")
    public String index(){
        return this.feign.index();
    }
}
```

### 5.Sentinel 服务限流降级

 雪崩效应：

高并发系统中，因为一个小问题而引发的系统崩溃

比如B调用A，A服务挂机后，B调A的线程一直存在得不到处理，随着堆积越来越多，B服务的内存就会崩溃，随即调用C服务的一系列服务也会崩溃，造成雪崩效应

解决方案：

1.设置线程超时，当某个请求在特定的时间内无法调用，则直接释放线程（比如一直不买，就赶出去）

2.设置限流,某个微服务到达访问上限之后，其他请求就暂时无法访问该服务（比如只允许50个人进店，50个人没走，后面的人就没办法进店）

3.熔断器，这是目前微服务中比较主流的解决方案，如Hystrix，相当于家里的保险丝，如果电流过大，为了保护家电设备，字段稍短保险丝，断点，阻断电流

1.pom.xml导入依赖

```java
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

2.application配置 （在被调用者中使用 provider ）

```yml
management:
  endpoints:
    web:
      exposure:
        include: '*'
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8080
```

3、下载 Sentinel 控制台

https://github.com/alibaba/Sentinel/releases

4、解压，启动 sentinel-dashboard-1.7.2.jar，在所在包cmd后输入

命令：java -jar sentinel-dashboard-1.7.2.jar

![image-20220331180240034](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220331180240034.png)

浏览器访问 localhost:8080

用户名/密码都是sentinel

![image-20220331180657817](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220331180657817.png)

访问provider，实时监控进行捕获，QPS每秒钟请求数，每隔10s更新一次

![image-20220401171556601](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220401171556601.png)

#### 5.1流控规则

##### 直接限流

选择请求的簇点链路，对资源进行限流设置

![image-20220401172112393](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220401172112393.png)

![image-20220401172207830](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220401172207830.png)

![image-20220401172236243](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220401172236243.png)

##### 关联限流

关联限流指给某资源设置QPS后，若请求数超出设置的QPS，则会导致另外一个与它关联的资源不可访问。

```java
@RestController
public class IndexController {

    @Value("${server.port}")
    private  String port;

    @Autowired
    private AccountMapper accountMapper;
    @RequestMapping("/find")
    public  String index(){
        return "该端口号是"+this.port;
    }

    @RequestMapping("/finAll")
    public List<Account> findAll(){
        System.out.println("当前调用的是"+this.port);
        return this.accountMapper.selectList(null);
    }

}
```

![image-20220401172720685](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220401172720685.png)

测试方法：

```java
import org.springframework.web.client.RestTemplate;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 1000; i++) {
            restTemplate.getForObject("http://localhost:8181/find",String.class);
            //0.5秒访问一次， QPS为2
            Thread.sleep(500);
        }
    }
}
```

访问/finAll时，会报错

![image-20220401173955327](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220401173955327.png)

表示关联限流生效

##### 链路限流

是一种更细粒度的限流，对某个资源（Service）进行限流，则请求资源的接口无法访问，这里有个坑，高版本（1.6.3+）的Sentinel Web filter默认收敛所有URL的入口context，因此链路限流不生效

因此我们需要手动关闭收敛，1.7.0版本开始（对应SCA的2.1.1.RELEASE),官方在CommonFilter引入	参数,用于控制是否收敛context,将其配置为false即可根据不同的URL进行链路限流

解决如下：

1.pom.xml添加依赖

```java
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-core</artifactId>
    <version>1.7.1</version>
</dependency>

<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-web-servlet</artifactId>
    <version>1.7.1</version>
</dependency>
```

2.application.yml

```yml
spring:
	cloud:
        sentinel:
          filter:
            enabled: false
```

3.创建配置类

```java
package com.configuration;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean registrationBean(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CommonFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter(CommonFilter.WEB_CONTEXT_UNIFY,"false");
        registrationBean.setName("sentinelFilter");
        return registrationBean;
    }
}
```

4.service

```java
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    //添加该注解，表示对该链路进行限流
    @SentinelResource("hello")
    public void hello(){
        System.out.println("hello");
    }
}
```

5.controller

```java
@Autowired
private HelloService helloService;

@GetMapping("/test1")
public String test1(){
    this.helloService.test();
    return "test1";
}

@GetMapping("/test2")
public String test2(){
    this.helloService.test();
    return "test2";
}
```

6.设置链路限流，将service中的test()链路进行限流

![image-20220402151536715](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402151536715.png)

![image-20220402151604162](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402151604162.png)

入口 /test1调用test()方法，会进行链路限流。

入口/test2调用test()方法，不会进行链路限流

#### 5.2 流控效果

##### 快速失败

直接抛出异常

![image-20220402162628757](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402162628757.png)

##### Warm UP

给系统一个预热的时间，预热时间段内单机阈值较低，预热时间过后单机阈值增加，预热时间内当前的单机阈值是设置的阈值的三分之一，预热时间过后单机阈值恢复设置的值

![image-20220402162935844](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402162935844.png)

##### 排队等待

当请求调用失败后，不会立即抛出异常，等待下一次调用，时间范围是超时时间，在时间范围内如果请求成功则不抛出异常，如果请求不成功则抛出异常。（在规定时间内再给一次机会，如果没成功在抛出异常）

![image-20220402163635787](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402163635787.png)

![image-20220402163747491](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402163747491.png)

![image-20220402163804020](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402163804020.png)

![image-20220402163835694](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402163835694.png)

![image-20220402163846053](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402163846053.png)

改为500后成功

#### 5.3 热点规则

热点规则是流控规则的更细粒度操作，可以具体到对某个热点参数的限流，设置限流之后，如果带着限流参数的请求量超过阈值，则进行限流，时间为统计窗口的时长

高级设置是指可以给限流参数设置例外的值，同时设置对应的阈值，当参数的值为例外的值时，阈值采用对应的阈值，其他情况都是默认阈值

必须要添加@SentinelResource,即对资源进行流控

```java
@RequestMapping("/hot")
@SentinelResource("hot")
public String hot(
        @RequestParam(value = "num1",required = false) Integer num1,
        @RequestParam(value = "num2",required = false) Integer num2
        ){
    return num1+"-"+num2;

}
```

![image-20220402170730642](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402170730642.png)

![image-20220402171309112](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402171309112.png)

#### 5.4 授权规则

给指定的资源设置流控应用（追加参数），可以对流控应用进行访问权限的设置，具体就是添加白名单和黑名单

如果设置白名单，那么只有出现在白名单上的流控应用才能访问，其他应用不能访问。

如果设置黑名单，那么出现在黑名单上的流控应用不能访问，其他正常访问

如何给请求指定流控应用：通过实现RequestOriginParser接口来完成，

代码如下所示：

1.创建RequestOriginParserDefinition类

```java
package com.ishang.configuration;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestOriginParserDefinition implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        //设置的追加参数名
        String name = httpServletRequest.getParameter("name");
        if(StringUtils.isEmpty(name)){
            throw new RuntimeException("name is null");
        }
        return name;
    }
}
```

2.创建配置类

```java
package com.ishang.configuration;

import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SentinelConfiguration {

    @PostConstruct
    public void init(){
        WebCallbackManager.setRequestOriginParser(new RequestOriginParserDefinition());
    }
}
```

3.设置白名单/黑名单

![image-20220402173133067](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402173133067.png)

添加白名单时，只有白名单上面的参数才能访问

![image-20220402173322506](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402173322506.png)

![image-20220402173338263](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402173338263.png)

添加黑名单，即除了黑名单上的，都可以访问

![image-20220402173423482](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402173423482.png)

![image-20220402173431660](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402173431660.png)

#### 5.5 降级规则

##### 异常数

1分钟内的异常数超过阈值就进行降级处理，时间窗口的值要大于60s，否则刚结束熔断又进入下一次熔断

![image-20220402180723819](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402180723819.png)

##### RT

![image-20220402180915544](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402180915544.png)

单个请求的响应时间超过阈值，则进入准降级状态，接下来 1 S 内连续 5 个请求响应时间均超过阈值，就进行降级，持续时间为时间窗口的值。

当第一次请求响应时间超过 1 毫秒，就准降级，1 S 之内连续发送 5 个请求，响应时间均超过 1 毫秒则进入降级状态，降级状态持续时间为 8 s，8 s 之后恢复正常，进入下一轮循环。

##### 异常比例

每秒异常数量占通过量的比例大于阈值，就进行降级处理，持续时间为时间窗口的值。

![image-20220402181039111](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220402181039111.png)

##### 5.6自定义规则异常返回

默认情况下，发生限流、降级、授权拦截时，都会抛出异常到调用方。如果要自定义异常时的返回结果，需要实现UrlBlockHandler接口：

```java
package com.ishang.handler;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//创建异常处理类
public class ExceptionHandler implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {

        httpServletResponse.setContentType("text/html;charset=utf-8");
        String msg=null;
        int status =429;
        if(e instanceof FlowException){
            msg ="请求被限流了";
        }else if (e instanceof ParamFlowException){
            msg="请求被热点参数限流";
        }else if (e instanceof DegradeException){
            msg ="请求被降级了";
        }else if(e instanceof AuthorityException){
            msg = "没有权限访问";
            status =401;
        }
        httpServletResponse.setStatus(status);
        httpServletResponse.getWriter().println("{\"msg\": " + msg + ", \"status\": " + status + "}");
    }
}
```

2.创建SentinelConfiguration配置

```java
package com.ishang.configuration;

import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.ishang.handler.ExceptionHandler;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SentinelConfiguration {

    @PostConstruct
    public void init(){
//        WebCallbackManager.setRequestOriginParser(new RequestOriginParserDefinition());
        WebCallbackManager.setUrlBlockHandler(new ExceptionHandler());
    }
}
```

当请求被限流降级时会给客户端做出相应的响应。

#### 5.6Feign 整合 Sentinel

创建另外一个微服务，通过 Feign 调用 provider，pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    <version>2.2.1.RELEASE</version>
</dependency>

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    <version>2.2.1.RELEASE</version>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```

application.yml

```yaml
server:
  port: 8380
feign:
  sentinel:
    enabled: true
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8080
  application:
    name: feign
```

Feign

```java
package com.southwind.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("provider")
public interface ProviderFeign {
    
    @GetMapping("/index")
    public String index();
}
```

Controller

```java
package com.southwind.controller;

import com.southwind.feign.ProviderFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignController {

    @Autowired
    private ProviderFeign providerFeign;

    @GetMapping("/index")
    public String index(){
        return this.providerFeign.index();
    }
}
```

如果要自定义异常，创建 Fallback 类，实现接口。

```java
package com.southwind.fallback;

import com.southwind.feign.ProviderFeign;
import org.springframework.stereotype.Component;

@Component
public class ProviderFeignFallback implements ProviderFeign {
    @Override
    public String index() {
        return "index-fallback";
    }
}
```

FeignClient 指定 fallback

```java
package com.southwind.feign;

import com.southwind.fallback.ProviderFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "provider",fallback = ProviderFeignFallback.class)
public interface ProviderFeign {

    @GetMapping("/index")
    public String index();
}
```

### 6.Rocket MQ

异步解耦

![image-20220405173349352](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220405173349352.png)

Active MQ,RibbateMQ,RocketMQ,Kafaka

RocketMQ是阿里巴巴的MQ中间件

http://rocketmq.apache.org/release_notes/release-notes-4.7.1/

#### 6.1安装RocketMQ

1、

```
cd /usr/local
mkdir rokerMQ
```

2.传入 rokerMQ文件夹

3.进入rokerMQ文件夹后，解压缩

```
unzip rocketmq-all-4.7.1-bin-release.zip
```

4.进入解压后的文件夹，启动 NameServer

```
cd rocketmq-all-4.7.1-bin-release
```

```
nohup ./bin/mqnamesrv &
```

![image-20220403224757787](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220403224757787.png)

5.检查是否启动成功

```
netstat -an | grep 9876
```

![image-20220403224953324](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220403224953324.png)

6.启动Broker

启动之前需要编辑配置文件，修改JVM内存设置，默认给的内存4GB,超过我们的JVM了。

```
cd bin
vim runserver.sh
```

修改为一下数值

![image-20220403225414880](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220403225414880.png)

```
vim runbroker.sh
```

![image-20220403225639236](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220403225639236.png)

7.启动 Broker

```
nohup ./mqbroker -n localhost:9876 &
```

通过查看日志，检查是否成功启动

![image-20220403225930065](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220403225930065.png)

启动成功

8、测试 RocketMQ

消息发送

```
cd bin
export NAMESRV_ADDR=localhost:9876
./tools.sh org.apache.rocketmq.example.quickstart.Producer
```

消息接收

```
cd bin
export NAMESRV_ADDR=localhost:9876
./tools.sh org.apache.rocketmq.example.quickstart.Consumer
```

9、关闭 RocketMQ

```
cd bin
./mqshutdown broker
./mqshutdown namesrv
```

#### 6.2 安装RocketMQ控制台

1、下载

https://github.com/apache/rocketmq-externals/releases

2.解压缩，修改application.properties

![image-20220404150052614](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220404150052614.png)

3.进入控制台根路径，进行打包

```
mvn clean package -Dmaven.test.skip=true
```

4、进入 target 启动 jar

```
java -jar rocketmq-console-ng-1.0.0.jar 
```

第三步和第四步也有可以直接从idea运行

原因，JDK 9 以上版本缺失 jar，需要手动导入，打开项目 pom.xml 添加

```xml
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-impl</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-core</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
```

重新构建 maven

```
mvn clean install
```

5.开放端口

这是因为我们的 RocketMQ 安装在 Linux 中，控制台在 windows，Linux 需要开放端口才能访问，开放 10909 和 9876 ，10911端口

```
firewall-cmd --zone=public --add-port=10909/tcp --permanent
firewall-cmd --zone=public --add-port=10911/tcp --permanent
firewall-cmd --zone=public --add-port=9876/tcp --permanent
systemctl restart firewalld.service
firewall-cmd --reload
```

注意：这一步我用的轻量级应用服务器，所以需在阿里云的防火墙中添加端口

![image-20220405161937845](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220405161937845.png)

6.重新启动控制台

![image-20220405162023199](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220405162023199.png)

#### 6.3 java实现消息发送

1.provider的pom.xml中引入依赖

```java
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>
```

2.生产消息

```java
package com.ishang.controller;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.web.client.RestTemplate;

public class Test {
    public static void main(String[] args) throws InterruptedException, MQClientException, RemotingException, MQBrokerException {

//        创建消息生产者,分组
        DefaultMQProducer producer = new DefaultMQProducer("myproducer-group");
//        设置NameServer,指向linux上的rocketmq
        producer.setNamesrvAddr("47.103.92.13:9876");
//        启动生产者
        producer.start();
//        构建消息对象                   消息标题          标签           内容
        Message message = new Message("myTopic","myTag",("Test MQ").getBytes());
//        发送消息，超过超时消息这条消息将不再发送
        SendResult result = producer.send(message,10000);
        System.out.println(result);
//        关闭生产者
        producer.shutdown();

    }
}
```

3、直接运行，如果报错 sendDefaultImpl call timeout，可以手动关闭 Linux 防火墙

```shell
# 关闭防火墙
systemctl stop firewalld
# 查看防火墙状态
firewall-cmd --state
```

或者开放 10911 端口

```shell
firewall-cmd --zone=public --add-port=10911/tcp --permanent
systemctl restart firewalld.service
firewall-cmd --reload
```

打开 RocketMQ 控制台，可查看消息。

![image-20220405164233903](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220405164233903.png)

#### 6.4 java实现消息消费

1.导入依赖

```java
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>
```



```java
package com.ishang.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;
@Slf4j
public class ConsumerTest {

    public static void main(String[] args) throws MQClientException {

//        创建消息消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("myconsumer-group");
//        设置NameServer
        consumer.setNamesrvAddr("47.103.92.13:9876");
//        指定订阅的主题和标签
        consumer.subscribe("myTopic","*");
//        回调函数，一旦读到myTopic的消息就读取
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                log.info("Message=>{}",list);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        //启动消费者
        consumer.start();
    }
}
```

#### 6.5SpringBoot整合RocketMQ

provider：

1.导入依赖

```java
<!--        rocketmq-->
        <dependency>
            <groupId>org.apache.rocketmq</groupId>
            <artifactId>rocketmq-spring-boot-starter</artifactId>
            <version>2.1.0</version>
        </dependency>
<!--        rocketmq client-->
        <dependency>
            <groupId>org.apache.rocketmq</groupId>
            <artifactId>rocketmq-client</artifactId>
            <version>4.7.0</version>
        </dependency>
```

2.在application.yml中进行配置

```java
rocketmq:
  name-server: 47.103.92.13:9876
  producer:
    group: myprovider
```

3.实体类order

```java
package com.ishang.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Order {
    private Integer id;
    private String name;
    private String address;
    private Date createDate;
}
```

4.Controller

```java
@Autowired
private RocketMQTemplate rocketMQTemplate;

@RequestMapping("/create")
public Order create(){
    Order order = new Order(
            1,
            "张三",
            "攀枝花",
            new Date()
    );

    this.rocketMQTemplate.convertAndSend("myOrder",order);
    return order;
}
```

consumer

1.引入依赖

```pom
<!--       rocketmq-->
      <dependency>
         <groupId>org.apache.rocketmq</groupId>
         <artifactId>rocketmq-spring-boot-starter</artifactId>
         <version>2.1.0</version>
      </dependency>
      <!--        rocketmq client-->
      <dependency>
         <groupId>org.apache.rocketmq</groupId>
         <artifactId>rocketmq-client</artifactId>
         <version>4.7.0</version>
      </dependency>
```

2.同步provider的实体类

```java
package com.ishang.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Order {
    private Integer id;
    private String name;
    private String address;
    private Date createDate;
}
```

3.service（因为读取自动读取，不需要手动访问controller）

```java
package com.ishang.service;

import com.ishang.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RocketMQMessageListener(consumerGroup = "myConsumer",topic = "myOrder")
public class SmsService implements RocketMQListener<Order> {
    @Override
    public void onMessage(Order order) {
        log.info("新订单{},发消息",order);
    }
}
```

访问/create

![image-20220405173957293](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220405173957293.png)

读取出来

![image-20220405174013248](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220405174013248.png)

![image-20220405174118822](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220405174118822.png)

### 7.服务网关

给不同的服务提供统一的入口，通过统一的网关端口号+不同服务名即可，不用记每个服务的ip+端口号

 ![image-20220406173323820](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220406173323820.png)

#### 7.1什么是Spring Cloud Gateway

微服务第二代网关，第一代是Netiflix的组件Zuul，Gateway的性能更加强大。

SpringCloud Gateway是spring cloud官方提供的新一代网关，spring cloud 阿里巴巴没有提供网关组件，直接用的是Spring Cloud Gateway

spring cloud gateway是基于Netty、WebFlux开发的，与Servlet不兼容，使用时不能引入Spring MVC依赖，不能发布war包，只能发布jar包

![image-20220407170122396](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220407170122396.png)

#### 快速上手

![image-20220407174021459](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220407174021459.png)

1.创建gateway模块

2.创建依赖pom.xml

```java
<!--        gateway-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
            <version>2.2.3.RELEASE</version>
        </dependency>
```

注意：由于geteway模块需要引入父模块的依赖，如果父模块中有spring-boot-start-web需要将父模块中的这个依赖分别放入子模块的pom.xml中，否则网关不生效

2.配置application.yml

```java
server:
  port: 8010
spring:
  application:
    name: gateway
#    开启网关
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
#          路由
      routes:
        - id: provider_route #自己任意取
          uri: http://localhost:8181 #真实路径
          predicates:
            - Path=/provider/** #映射名
          filters:
            - StripPrefix=1
        - id: consumer_route #自己任意取
          uri: http://localhost:8484 #真实路径
          predicates:
            - Path=/consumer/** #映射名
          filters:
            - StripPrefix=1

```

这是访问就可用：http://localhost:8010/provider/find访问就接口

#### 7.2 nacos整合gateway

上面这种做法其实没有用到 nacos ，现在我们让 gateway 直接去 nacos 中发现服务，配置更加简单了。

1.在gateway模块的pom.xml导入依赖

```java
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    <version>2.2.1.RELEASE</version>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    <version>2.2.1.RELEASE</version>
</dependency>
```

2.application.yml

```java
server:
  port: 8010
spring:
  application:
    name: gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
```

![image-20220409125812004](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409125812004.png)

![image-20220409125858314](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409125858314.png)

### 8.配置中心

Nacos 提供了配置中心的服务，即可以将微服务的配置文件直接在 Nacos 中进行配置管理。使配置文件可以实现动态更新，环境隔离

1.新建config工程,引入父依赖

1.pom.xml

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

2.创建bootstrap.yml

```java
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        file-extension: yaml   //后缀名
  application:
    name: config
  profiles:
    active: dev
        //根据 config-dev.yaml查找nacos中的配置信息
```

3.在nacos中添加配置，Data ID 的写法必须和 bootstrap.yml 保持一致

![image-20220409134120699](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409134120699.png)

3.创建controller测试

```java
package com.ishang.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    @Value("${your.configuration}")
    private String configuration;
    @GetMapping("/config")
    public String config(){
        return this.configuration;
    }
}
```

![image-20220409134203556](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409134203556.png)

#### 8.1动态刷新

在conreoller添加@RefreshScop实现动态刷新

```java
package com.ishang.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigController {

    @Value("${your.configuration}")
    private String configuration;
    @GetMapping("/config")
    public String config(){
        return this.configuration;
    }
}
```

在nacos中修改配置即可自动刷新，不用再重启服务

![image-20220409135457763](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409135457763.png)

![image-20220409135503823](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409135503823.png)

### 9.Zipkin服务追踪

zipkin将请求记录下来，当前请求了哪些服务，成功还是失败，请求了服务的哪些接口全部记录下来，方便我们去进行排查。如果遇到问题，尤其是服务调用比较复杂，多个服务互相调用时，出现问题，解决需要知道这个请求经给哪些服务，zipkin记录下来，帮助我们排查问题。

1.下载zipkin应用

2.启动

![image-20220409140301679](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409140301679.png)

创建zipkin项目,导入父依赖

1.pom.xml

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

2.application.yml

```java
server:
  port: 8282
spring:
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
      # 抽样率，默认 0.1，90%数据都会丢弃
      probability: 1.0
  application:
    name: zipkin
```

3.controller

```java
package com.ishang.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class indexcontroller {

    private String name="你好";

    @RequestMapping("/index")
    public String named(){
        return this.name;
    }
}
```

4.启动应用并访问

zipkin即可监控到

![image-20220409142323247](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409142323247.png)

### 10.分布式事务

在分布式项目中，两个数据库实现的是同一个业务，事务统一进行回滚

#### 10.1模拟分布式事务异常

1.创建pay和order模块，导入父依赖，两个模块中加入依赖（这里模拟用jdbc，也可用mybatis...）

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

2.创建两个数据库pay、order

![image-20220409151415470](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409151415470.png)

![image-20220409151436854](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409151436854.png)

3.配置数据源

```java
server:
  port: 8686
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/pay?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  application:
    name: pay
```

```java
server:
  port: 8585
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/order?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shjavaanghai
    username: root
    password: 123456
  application:
    name: order
```

4.目的是当订单表中存入客户信息时，支付表中也存入

创建service

pay：

```java
package com.ishang.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
public class payservice {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(){
        this.jdbcTemplate.update("insert  into pay(username) values ('张三')");
    }
}
```

order

```java
package com.ishang.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class orderservice {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    public void save(){
        this.jdbcTemplate.update("insert into orders(username) values('张三'java)");
    }
}
```

5.创建controller，利用RestTemplate，连接两个服务，在启动类中添加

```java
package com.ishang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();

    }
}
```

controller：

paycontroller：

```java
package com.ishang.controller;
import com.ishang.service.payservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class paycontroller {

    @Autowired
    private payservice service;

    @RequestMapping("/test")
    public String test(){
        this.service.save();
        return "success";
    }
}
```

ordercontroller

```java
package com.ishang.controller;


import com.ishang.service.orderservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class orderController {
        @Autowired
        private orderservice orderservice;

        @Autowired
        private RestTemplate restTemplate;

        @RequestMapping("/test")
        public String test(){
            this.orderservice.save();
            int i=10/0;
            this.restTemplate.getForObject("http://localhost:8686/test",String.class);
            return "success";
        }
}
```

此时调用接口，会报错

![image-20220409151927366](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409151927366.png)

导致pay表信息存不进去

![image-20220409152020188](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409152020188.png)

![image-20220409152034701](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409152034701.png)

分布式异常模拟结束，Order 存储完成之后，出现异常，会导致 Pay 无法存储，但是 Order 数据库不会进行回滚。



#### 10.2 Seata解决

1、下载 Seate 0.9.0

https://github.com/seata/seata/releases

2.解压，修改两个文件

![image-20220409152459196](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409152459196.png)

regisry.conf：修改一下配置

![image-20220410141606125](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220410141606125.png)

修改config.txt

![image-20220410142354072](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220410142354072.png)

![image-20220410142429671](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220410142429671.png)

3.将config.txt和nacos-config.sh放在一下位置

![image-20220410142608099](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220410142608099.png)

**config.txt 和nacos-config.sh下载地址：**

[https://github.com/seata/seata/tree/develop/script/config-center](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fseata%2Fseata%2Ftree%2Fdevelop%2Fscript%2Fconfig-center)
https://github.com/seata/seata/tree/develop/script/config-center/nacos

4.创建seata数据库，运行一下脚本

```mysql
-- -------------------------------- The script used when storeMode is 'db' --------------------------------
-- the table to store GlobalSession data
CREATE TABLE IF NOT EXISTS `global_table`
(
    `xid`                       VARCHAR(128) NOT NULL,
    `transaction_id`            BIGINT,
    `status`                    TINYINT      NOT NULL,
    `application_id`            VARCHAR(32),
    `transaction_service_group` VARCHAR(32),
    `transaction_name`          VARCHAR(128),
    `timeout`                   INT,
    `begin_time`                BIGINT,
    `application_data`          VARCHAR(2000),
    `gmt_create`                DATETIME,
    `gmt_modified`              DATETIME,
    PRIMARY KEY (`xid`),
    KEY `idx_status_gmt_modified` (`status` , `gmt_modified`),
    KEY `idx_transaction_id` (`transaction_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store BranchSession data
CREATE TABLE IF NOT EXISTS `branch_table`
(
    `branch_id`         BIGINT       NOT NULL,
    `xid`               VARCHAR(128) NOT NULL,
    `transaction_id`    BIGINT,
    `resource_group_id` VARCHAR(32),
    `resource_id`       VARCHAR(256),
    `branch_type`       VARCHAR(8),
    `status`            TINYINT,
    `client_id`         VARCHAR(64),
    `application_data`  VARCHAR(2000),
    `gmt_create`        DATETIME(6),
    `gmt_modified`      DATETIME(6),
    PRIMARY KEY (`branch_id`),
    KEY `idx_xid` (`xid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store lock data
CREATE TABLE IF NOT EXISTS `lock_table`
(
    `row_key`        VARCHAR(128) NOT NULL,
    `xid`            VARCHAR(128),
    `transaction_id` BIGINT,
    `branch_id`      BIGINT       NOT NULL,
    `resource_id`    VARCHAR(256),
    `table_name`     VARCHAR(32),
    `pk`             VARCHAR(36),
    `status`         TINYINT      NOT NULL DEFAULT '0' COMMENT '0:locked ,1:rollbacking',
    `gmt_create`     DATETIME,
    `gmt_modified`   DATETIME,
    PRIMARY KEY (`row_key`),
    KEY `idx_status` (`status`),
    KEY `idx_branch_id` (`branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `distributed_lock`
(
    `lock_key`       CHAR(20) NOT NULL,
    `lock_value`     VARCHAR(20) NOT NULL,
    `expire`         BIGINT,
    primary key (`lock_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('HandleAllSession', ' ', 0);
```

![image-20220410142851654](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220410142851654.png)

5.在pay和order服务中添加undo_log表

```mysql
CREATE TABLE undo_log (
id bigint(20) NOT NULL AUTO_INCREMENT,
branch_id bigint(20) NOT NULL,
xid varchar(100) NOT NULL,
context varchar(128) NOT NULL,
rollback_info longblob NOT NULL,
log_status int(11) NOT NULL,
log_created datetime NOT NULL,
log_modified datetime NOT NULL,
ext varchar(100) DEFAULT NULL,
PRIMARY KEY (id),
UNIQUE KEY ux_undo_log (xid,branch_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

```

![image-20220409154109095](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409154109095.png)

6.启动 Nacos，运行 nacos-config.sh 将 Seata 配置导入 Nacos

进入 conf，右键 Git Bash Here

![image-20220410143052617](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220410143052617.png)

执行成功，刷新 Nacos，配置加入

![image-20220409153350516](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409153350516.png)

7、启动 Seata Server，  **JDK 8 以上环境无法启动**

进入seata 的bin路径java

```java
cd bin
seata-server.bat -p 8090 -m file
```

![image-20220409153552849](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409153552849.png)

启动成功，Nacos 注册成功。

![image-20220409153701669](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409153701669.png)

**8.整合到微服务**

8.1两个工程的 pom.xml 添加 Seata 组件和 Nacos Config 组件。

```java
<dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
</dependency>

<!--nacos-->
<dependency>
       <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

8.2、将 registry.conf 复制到两个工程的 resources 下。

![image-20220409154748436](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409154748436.png)

![image-20220409154805000](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220409154805000.png)

8.3、给两个工程添加 application.yml 读取 Nacos 配置。

```yml
server:
  port: 8686
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/pay?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  application:
    name: pay
  cloud:
    alibaba:
      seata:
        tx-service-group: my_test_tx_group
```

```yml
server:
  port: 8585
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/order?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  application:
    name: order
  cloud:
    alibaba:
      seata:
        tx-service-group: my_test_tx_group

```



8.4、在 Order 调用 Pay 处添加注解 @GlobalTransactional

```java
package com.ishang.controller;


import com.ishang.service.orderservice;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class orderController {
        @Autowired
        private orderservice orderservice;

        @Autowired
        private RestTemplate restTemplate;

        @RequestMapping("/test")
        @GlobalTransactional
        public String test(){
            this.orderservice.save();
            int i=10/0;
            this.restTemplate.getForObject("http://localhost:8686/test",String.class);
            return "success";
        }
}
```

8.5运行

![image-20220410143548671](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220410143548671.png)

![image-20220410143559861](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220410143559861.png)

![image-20220410143605829](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220410143605829.png)

数据表进行回滚
