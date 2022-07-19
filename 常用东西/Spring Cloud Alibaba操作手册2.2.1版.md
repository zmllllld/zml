创建父工程

Spring Cloud Alibaba 的环境在父工程中创建，微服务的各个组件作为子工程，继承父工程的环境。

1、创建 Spring Boot 工程，选择常用的 Lombok，Spring Cloud Alibaba 还没有完全集成到 Spring Boot Initialzr 中，我们需要手动添加。

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



# Nacos 服务注册

服务注册，这里我们使用 Nacos 的服务注册，下载对应版本的 Nacos：https://github.com/alibaba/nacos/releases

解压，启动服务。

Nacos 搭建成功，接下来注册服务。

在父工程路径下创建子工程，让子工程继承父工程的环境依赖，pom.xml 中添加 nacos 发现组件。

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

application.yml 中配置

```yaml
spring:
  cloud:
    nacos:
      discovery:
        # 指定nacos server地址
        server-addr: localhost:8848
  application:
    name: my-nacos
```

需要引入 spring-web 依赖才能实现 Nacos 注册



# Nacos 服务发现与调用

创建 consumer 工程，从 nacos 中发现服务，进行调用。

pom.xml 添加 discovery，完成服务发现。

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

通过 discoveryClient 发现注册到 nacos 中的 provider 服务。

```java
package coms.southwind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConsumerController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/instances")
    public List<ServiceInstance> instances(){
        List<ServiceInstance> provider = discoveryClient.getInstances("provider");
        return provider;
    }

}
```

resttemplate 首先需要注入 IoC 。

```java
@Configuration
public class ConsumerConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
```

首先通过 discoveryClient 发现 provider 服务实例，获取其 uri 即可，拼接 url，通过 resttemplate 调用，随机返回。

```java
package coms.southwind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class ConsumerController {

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/index")
    public String index(){
        List<ServiceInstance> provider = discoveryClient.getInstances("provider");
        int index = ThreadLocalRandom.current().nextInt(provider.size());
        String url = provider.get(index).getUri()+"/index";
        return "consumer随机远程调用provier："+this.restTemplate.getForObject(url, String.class);
    }

}
```



# Ribbon 负载均衡

1、RestTemplate 添加 @LoadBalanced 注解

```java
@Configuration
public class ConsumerConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
```

2、Controller 代码

```java
package coms.southwind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/index")
    public String index(){
        return "consumer远程调用provier："+this.restTemplate.getForObject("http://provider/index", String.class);
    }

}
```

> Ribbon 负载均衡算法默认是轮询，交替访问。

> 修改为随机，在 consumer 的 application.yml 添加配置随机规则即可。

```yaml
server:
  port: 8180
provider:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```

> 基于 Nacos 权重的负载均衡

1、创建 NacosWeightedRule 类定义基于权重的负载均衡

```java
package com.southwind.configuration;

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

2、修改配置文件负载均衡规则为 NacosWeightedRule 

```yaml
server:
  port: 8180
provider:
  ribbon:
    NFLoadBalancerRuleClassName: com.southwind.configuration.NacosWeightedRule
```



# Feign 声明式接口调用

使用 RestTemplate 调用远程接口，代码可读性较差且不方便维护，我们可以通过 Feign 声明式接口调用的方式来解决这一问题。

Feign 是基于 Ribbon 实现的一种声明式接口调用机制，同时它更加灵活，使用起来也更加简单，取代 Ribbon + RestTemplate 的方式来实现基于接口调用的负载均衡，比 Ribbon 使用起来要更加简便，只需要创建接口并添加相关注解配置，即可实现服务消费的负载均衡。

1、pom.xml 引入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```

2、创建 Feign 接口

```java
package coms.southwind.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("provider")
public interface Feign {
    
    @GetMapping("/index")
    public String index();
}
```

3、启动类添加注解

```java
package coms.southwind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

4、直接调用

```java
package coms.southwind.controller;

import coms.southwind.feign.ProviderFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Autowired
    private ProviderFeign providerFeign;

    @GetMapping("/index")
    public String index(){
        return "consumer远程调用provier："+this.providerFeign.index();
    }

}
```



# Sentinel 服务限流降级

> 雪崩效应

高并发系统中，由一个小问题而引发的系统崩溃。

> 解决方案

1、设置线程超时，当某个请求在特定的时间内无法调通，则直接释放该线程。

2、设置限流，某个微服务到达访问上限之后，其他请求就暂时无法访问该服务。

3、熔断器，这是目前微服务中比较主流的解决方案，如 Hystrix，相当于家里的保险丝，如果电流过大，为了保护家电设备，自动烧断保险丝，断电，阻断电流。

1、pom.xml 引入依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

2、application 配置

```yaml
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

4、解压，启动 sentinel-dashboard-1.7.2.jar，浏览器访问 localhost:8080

5、启动服务，访问接口，再刷新 Sentinel 控制台页面，可以看到监控数据。

## 流控规则

> 直接限流

选择 簇点链路，对资源进行限流设置。

> 关联限流

```java
package com.southwind.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentinelController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/index")
    public String index(){
        return this.port;
    }

    @GetMapping("/list")
    public String list(){
        return "list";
    }
}
```

```java
package com.southwind;

import org.springframework.web.client.RestTemplate;

public class Test {
    public static void main(String[] args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 1000; i++) {
            restTemplate.getForObject("http://localhost:8280/list", String.class);
            Thread.sleep(500);
        }
    }
}
```

> 链路限流

是一种更细粒度的限流，对某个资源（Service）进行限流，则请求该资源的接口无法访问，这里有个坑，高版本（1.6.3+）的 Sentinel Web filter 默认收敛所有 URL 的入口 context，因此链路限流不生效。

因此我们需要手动关闭收敛，1.7.0 版本开始（对应 SCA 的 2.1.1.RELEASE)，官方在 CommonFilter 引入了 WEB_CONTEXT_UNIFY 参数，用于控制是否收敛 context，将其配置为 false 即可根据不同的URL 进行链路限流。

1、pom.xml 添加依赖

```xml
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

2、application.yml

```yml
spring:
	cloud:
        sentinel:
          filter:
            enabled: false
```

3、写配置类

```java
package com.southwind.configuration;

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

4、Service

```java
@Service
public class HelloService {

    @SentinelResource("test")
    public void test(){
        System.out.println("test");
    }
}
```

5、Controller

```java
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



## 流控效果

> 快速失败

直接抛出异常

> Warm UP

给系统一个预热的时间，预热时间段内单机阈值较低，预热时间过后单机阈值增加，预热时间内当前的单机阈值是设置的阈值的三分之一，预热时间过后单机阈值恢复设置的值。

> 排队等待

当请求调用失败之后，不会立即抛出异常，等待下一次调用，时间范围是超时时间，在时间范围内如果能请求成功则不抛出异常，如果请求则抛出异常。

![image-20200814091225606](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200814091225606.png)

```java
package com.southwind;

import org.springframework.web.client.RestTemplate;

public class Test {
    public static void main(String[] args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 1000; i++) {
            restTemplate.getForObject("http://localhost:8280/index", String.class);
            Thread.sleep(500);
        }
    }
}
```



## 降级规则

> RT

单个请求的响应时间超过阈值，则进入准降级状态，接下来 1 S 内连续 5 个请求响应时间均超过阈值，就进行降级，持续时间为时间窗口的值。

![image-20200814092115114](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200814092115114.png)

当第一次请求响应时间超过 1 毫秒，就准降级，1 S 之内连续发送 5 个请求，响应时间均超过 1 毫秒则进入降级状态，降级状态持续时间为 8 s，8 s 之后恢复正常，进入下一轮循环。

> 异常比例

每秒异常数量占通过量的比例大于阈值，就进行降级处理，持续时间为时间窗口的值。

> 异常数

1 分钟内的异常数超过阈值就进行降级处理，时间窗口的值要大于 60S，否则刚结束熔断又进入下一次熔断了。



## 热点规则

热点规则是流控规则的更细粒度操作，可以具体到对某个热点参数的限流，设置限流之后，如果带着限流参数的请求量超过阈值，则进行限流，时间为统计窗口时长。

高级设置是指可以给限流参数设置例外的值，同时设置对应的阈值，当参数的值为例外值时，阈值采用对应的阈值，其他情况都是默认阈值。

必须要添加 @SentinelResource，即对资源进行流控。

```java
@GetMapping("/hot")
@SentinelResource("hot")
public String hot(
        @RequestParam(value = "num1",required = false) Integer num1,
        @RequestParam(value = "num2",required = false) Integer num2){
    return num1+"-"+num2;
}
```



## 授权规则

给指定的资源设置流控应用（追加参数），可以对流控应用进行访问权限的设置，具体就是添加白名单和黑名单。

如果设置白名单，那么只有出现在白名单上的流控应用才可以访问，其他不能访问。

如果设置黑名单，那么出现在黑名单上的流控应用不能访问，其他正常访问。

如何给请求指定流控应用，通过实现 RequestOriginParser 接口来完成，代码如下所示。

```java
package com.southwind.configuration;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestOriginParserDefinition implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        String name = httpServletRequest.getParameter("name");
        if(StringUtils.isEmpty(name)){
            throw new RuntimeException("name is null");
        }
        return name;
    }
}
```

要让 RequestOriginParserDefinition 生效，需要在配置类中进行配置。

```java
package com.southwind.configuration;

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



## 自定义规则异常返回

创建异常处理类

```java
package com.southwind.handler;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionHandler implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
        httpServletResponse.setContentType("text/html;charset=utf-8");
        String msg = null;
        if(e instanceof FlowException){
            msg = "限流";
        }else if(e instanceof DegradeException){
            msg = "降级";
        }
        httpServletResponse.getWriter().write(msg);
    }
}
```

进行配置。

```java
@Configuration
public class SentinelConfiguration {

    @PostConstruct
    public void init(){
        WebCallbackManager.setUrlBlockHandler(new ExceptionHandler());
    }
}
```



## Feign 整合 Sentinel 

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



# 整合 RocketMQ

## 安装 RocketMQ 

RocketMQ 是阿里巴巴的 MQ 中间件

http://rocketmq.apache.org/release_notes/release-notes-4.7.1/

1、传入 Linux 服务器

2、解压缩

```
unzip rocketmq-all-4.7.1-bin-release.zip
```

3、启动 NameServer

```
nohup ./bin/mqnamesrv &
```

4、检查是否启动成功

```
netstat -an | grep 9876
```

![image-20200612162924121](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612162924121.png)

5、启动 Broker

启动之前需要编辑配置文件，修改 JVM 内存设置，默认给的内存 4 GB，超过我们的 JVM 了。

```
cd bin
vim runserver.sh
```

![image-20200612163432947](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612163432947.png)

```
vim runbroker.sh
```

![image-20200612163711475](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612163711475.png)

启动 Broker

```
nohup ./mqbroker -n localhost:9876 &
```

可以查看日志

```
tail -f ~/logs/rocketmqlogs/broker.log
```

![image-20200612164059688](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612164059688.png)

启动成功

6、测试 RocketMQ

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

7、关闭 RocketMQ

```
cd bin
./mqshutdown broker
./mqshutdown namesrv
```



## 安装 RocketMQ 控制台

1、下载

https://github.com/apache/rocketmq-externals/releases

2、解压缩，修改配置，打包

![image-20200612190405331](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612190405331.png)

```
mvn clean package -Dmaven.test.skip=true
```

![image-20200612190454926](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612190454926.png)

4、进入 target 启动 jar

```
java -jar rocketmq-console-ng-1.0.0.jar 
```

如果报错

![image-20200612190636120](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612190636120.png)

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

TEST 错误可以忽略

重新打包，启动 jar，成功！

![image-20200612191058068](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612191058068.png)

打开浏览器访问 localhost:9877，如果报错

![image-20200612191202652](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612191202652.png)

这是因为我们的 RocketMQ 安装在 Linux 中，控制台在 windows，Linux 需要开放端口才能访问，开放 10909 和 9876 端口

```shell
firewall-cmd --zone=public --add-port=10909/tcp --permanent
firewall-cmd --zone=public --add-port=9876/tcp --permanent
systemctl restart firewalld.service
firewall-cmd --reload
```

重新启动控制台项目

![image-20200612191417412](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200612191417412.png)



## Java 实现消息发送

1、pom.xml 中引入依赖

```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>
```

2、生产消息

```java
package com.southwind;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Test {
    public static void main(String[] args) throws Exception {
        //创建消息生产者
        DefaultMQProducer producer = new DefaultMQProducer("myproducer-group");
        //设置NameServer
        producer.setNamesrvAddr("192.168.248.129:9876");
        //启动生产者
        producer.start();
        //构建消息对象
        Message message = new Message("myTopic","myTag",("Test MQ").getBytes());
        //发送消息
        SendResult result = producer.send(message, 1000);
        System.out.println(result);
        //关闭生产者
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

## Java 实现消息消费

```java
package com.southwind.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

@Slf4j
public class ConsumerTest {
    public static void main(String[] args) throws MQClientException {
        //创建消息消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("myconsumer-group");
        //设置NameServer
        consumer.setNamesrvAddr("192.168.248.129:9876");
        //指定订阅的主题和标签
        consumer.subscribe("myTopic","*");
        //回调函数
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



## Spring Boot 整合 RocketMQ

![image-20200814142552347](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200814142552347.png)

> provider

1、pom.xml

```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>4.7.0</version>
</dependency>
```

2、application.yml

```yaml
rocketmq:
  name-server: 192.168.248.129:9876
  producer:
    group: myprovider
```

3、Order

```java
package com.southwind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Integer id;
    private String buyerName;
    private String buyerTel;
    private String address;
    private Date createDate;
}
```

4、Controller

```java
@Autowired
private RocketMQTemplate rocketMQTemplate;

@GetMapping("/create")
public Order create(){
    Order order = new Order(
        1,
        "张三",
        "123123",
        "软件园",
        new Date()
    );
    this.rocketMQTemplate.convertAndSend("myTopic",order);
    return order;
}
```

> consumer

1、pom.xml

```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>4.7.0</version>
</dependency>
```

2、application.yml

```yaml
rocketmq:
  name-server: 192.168.248.129:9876
```

3、Service

```java
@Slf4j
@Service
@RocketMQMessageListener(consumerGroup = "myConsumer",topic = "myTopic")
public class SmsService implements RocketMQListener<Order> {
    @Override
    public void onMessage(Order order) {
        log.info("新订单{},发短信",order);
    }
}
```



# 服务网关

## 什么是 Spring Cloud Gateway？

微服务第二代网关，第一代是 Netiflix 的组件 Zuul，Gateway 的性能更加强大。

Spring Cloud Gateway 是 Spring Cloud 官方提供的新一代网关，Spring Cloud Alibaba 没有提供网关组件，直接用的是 Spring Cloud Gateway。

Spring Cloud Gateway 是基于 Netty、WebFlux 开发的，与 Servlet 不兼容，使用时不能引入 Spring MVC 依赖，不同发布 war 包，只能发布 jar。

## 快速上手

1、pom.xml

注意，一定不能出现 spring web 的依赖，因为 Gateway 与 Servlet 不兼容。

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

2、application.yml

```yaml
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
      routes: 
        - id: provider_route   
          uri: http://localhost:8081 
          predicates: 
            - Path=/provider/** 
          filters:
            - StripPrefix=1
        - id: consumer_route
          uri: http://localhost:8181
          predicates:
            - Path=/consumer/**
          filters:
          	- StripPrefix=1
```

上面这种做法其实没有用到 nacos ，现在我们让 gateway 直接去 nacos 中发现服务，配置更加简单了。

1、pom.xml 引入 nacos

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

2、application.yml

```yaml
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



## Gateway 限流

基于路由限流

1、pom.xml

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-spring-cloud-gateway-adapter</artifactId>
</dependency>
```

2、配置类

```java
package com.southwind.configuration;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

@Configuration
public class GatewayConfiguration {
    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;


    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    //配置限流的异常处理
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    //配置初始化的限流参数
    @PostConstruct
    public void initGatewayRules(){
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(
                new GatewayFlowRule("provider_route")
                .setCount(1)
                .setIntervalSec(1)
        );
        GatewayRuleManager.loadRules(rules);
    }

    //初始化限流过滤器
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    //自定义限流异常页面
    @PostConstruct
    public void initBlockHandlers(){
        BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                Map map = new HashMap();
                map.put("code",0);
                map.put("msg","被限流了");
                return ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(map));
            }
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}
```

3、application.yml

```yaml
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
      routes:
        - id: provider_route
          uri: http://localhost:8081
          order: 1
          predicates:
            - Path=/provider/**
          filters:
            - StripPrefix=1
        - id: consumer_route
          uri: http://localhost:8181
          order: 1
          predicates:
            - Path=/consumer/**
          filters:
```

基于 API 分组限流

1、修改配置类，添加基于 API 分组限流的方法，修改初始化的限流参数

```java
package com.southwind.configuration;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

@Configuration
public class GatewayConfiguration {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;


    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    //配置限流的异常处理
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    //配置初始化的限流参数
    @PostConstruct
    public void initGatewayRules(){
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule("provider_api1").setCount(1).setIntervalSec(1));
        rules.add(new GatewayFlowRule("provider_api2").setCount(1).setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);
    }

    //初始化限流过滤器
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    //自定义限流异常页面
    @PostConstruct
    public void initBlockHandlers(){
        BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                Map map = new HashMap();
                map.put("code",0);
                map.put("msg","被限流了");
                return ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(map));
            }
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }

    //自定义API分组
    @PostConstruct
    private void initCustomizedApis(){
        Set<ApiDefinition> definitions = new HashSet<>();
        ApiDefinition api1 = new ApiDefinition("provider_api1")
                .setPredicateItems(new HashSet<ApiPredicateItem>(){{
                    add(new ApiPathPredicateItem().setPattern("/provider/api1/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        ApiDefinition api2 = new ApiDefinition("provider_api2")
                .setPredicateItems(new HashSet<ApiPredicateItem>(){{
                    add(new ApiPathPredicateItem().setPattern("/provider/api2/demo1"));
                }});
        definitions.add(api1);
        definitions.add(api2);
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }
}
```

2、Controller 添加方法

```java
@GetMapping("/api1/demo1")
public String demo1(){
    return "demo";
}

@GetMapping("/api1/demo2")
public String demo2(){
    return "demo";
}

@GetMapping("/api2/demo1")
public String demo3(){
    return "demo";
}

@GetMapping("/api2/demo2")
public String demo4(){
    return "demo";
}
```

也可以基于 Nacos 服务发现组件进行限流

```yaml
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

API 分组代码修改，改为 discovery 中的服务名。

```java
ApiDefinition api2 = new ApiDefinition("provider_api2")
        .setPredicateItems(new HashSet<ApiPredicateItem>(){{
            add(new ApiPathPredicateItem().setPattern("/p1/api2/demo1"));
        }});
```

![image-20200617182640546](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200617182640546.png)



# 配置中心

## 快速上手

Nacos 提供了配置中心的服务，即可以将微服务的配置文件直接在 Nacos 中进行配置管理。

新建 config 工程。

1、pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

2、创建 bootstrap.yml

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        file-extension: yaml
  application:
    name: config
  profiles:
    active: dev
```

3、在 nacos 中添加配置，Data ID 的写法必须和 bootstrap.yml 保持一致

4、完成 Controller

```java
package com.southwind.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloHandler {

    @Value("${your.configuration}")
    private String configuration;

    @GetMapping("/config")
    public String config(){
        return this.configuration;
    }
}
```



## 动态刷新

1、在 Controller 添加 @RefreshScope

```java
package com.southwind.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class HelloHandler {

    @Value("${your.configuration}")
    private String configuration;

    @GetMapping("/config")
    public String config(){
        return this.configuration;
    }
}
```



# 服务追踪

下载 Zipkin 应用

https://search.maven.org/remote_content?g=io.zipkin.java&a=zipkin-server&v=LATEST&c=exec

通过 CMD 直接启动

![image-20200619170203537](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200619170203537.png)

创建 Zipkin 项目

1、pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

2、application.yml

```yaml
server:
  port: 8181
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

3、启动应用并访问



# 分布式事务

## 模拟分布式事务异常

1、创建两个工程 order、pay，pom.xml

```xml
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

2、建两个数据库 order、pay，两个微服务分别访问。

3、分别写两个服务的 application.yml

```yaml
server:
  port: 8010
spring:
  application:
    name: order
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
    url: jdbc:mysql://localhost:3306/order
```

```yaml
server:
  port: 8020
spring:
  application:
    name: pay
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
    url: jdbc:mysql://localhost:3306/pay
```

4、分别写两个 Service

```java
package com.southwind.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(){
        this.jdbcTemplate.update("insert into orders(username) values ('张三')");
    }
}
```

```java
package com.southwind.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PayService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(){
        this.jdbcTemplate.update("insert into pay(username) values ('张三')");
    }
}
```

5、控制器 Order 通过 RestTemplate 调用 Pay 的服务

```java
package com.southwind.controller;

import com.southwind.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/save")
    public String save(){
        //订单
        this.orderService.save();
        int i = 10/0;
        //支付
        this.restTemplate.getForObject("http://localhost:8020/save",String.class);
        return "success";
    }
}
```

```java
package com.southwind.controller;

import com.southwind.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayController {
    @Autowired
    private PayService payService;

    @GetMapping("/save")
    public String save(){
        this.payService.save();
        return "success";
    }
}
```

6、启动类

```java
package com.southwind;

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

```java
package com.southwind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }

}
```

分布式异常模拟结束，Order 存储完成之后，出现异常，会导致 Pay 无法存储，但是 Order 数据库不会进行回滚。



## Seata 解决

1、下载 Seate 0.9.0

https://github.com/seata/seata/releases

2、解压，修改两个文件

![image-20200624165841578](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200624165841578.png)

regisry.conf

```conf
registry {
  type = "nacos"
  nacos {
    serverAddr = "localhost"
    namespace = "public"
    cluster = "default"
  }
}

config {
  type = "nacos"
  nacos {
    serverAddr = "localhost"
    namespace = "public"
    cluster = "default"
  }
}
```

nacos-config.txt

![image-20200624170027580](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200624170027580.png)

3、启动 Nacos，运行 nacos-config.sh 将 Seata 配置导入 Nacos

进入 conf，右键 Git Bash Here

```
cd conf
sh nacos-config.sh 127.0.0.1
```

执行成功，刷新 Nacos，配置加入

![image-20200624170411851](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200624170411851.png)

nacos-config.txt 配置已生效

![image-20200624170446667](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200624170446667.png)

4、启动 Seata Server，  **JDK 8 以上环境无法启动**

```
cd bin
seata-server.bat -p 8090 -m file
```

![image-20200624170701118](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200624170701118.png)

启动成功，Nacos 注册成功。

![image-20200624171016755](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200624171016755.png)

Seata 服务环境搭建完毕，接下来去应用中添加。

1、初始化数据库，在两个数据库中添加事务日志记录表，SQL Seata已经提供。

![image-20200624171211591](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200624171211591.png)

2、直接在两个数据库运行脚本。

```sql
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

3、两个工程的 pom.xml 添加 Seata 组件和 Nacos Config 组件。

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
    <version>2.1.1.RELEASE</version>
</dependency>

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

4、给 JDBCTemplate 添加代理数据源

```java
package com.southwind;

import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(new DataSourceProxy(dataSource));
    }
}
```

```java
package com.southwind;

import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(new DataSourceProxy(dataSource));
    }

}
```

5、将 registry.conf 复制到两个工程的 resources 下。

6、给两个工程添加 bootstrap.yml 读取 Nacos 配置。

```yaml
spring:
  application:
    name: order
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        namespace: public
        group: SEATA_GROUP
    alibaba:
      seata:
        tx-service-group: ${spring.application.name}
```

```yaml
spring:
  application:
    name: pay
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        namespace: public
        group: SEATA_GROUP
    alibaba:
      seata:
        tx-service-group: ${spring.application.name}
```

tx-service-group 需要和 Nacos 配置中的名称一致。

![image-20200624172215657](C:\Users\ningn\AppData\Roaming\Typora\typora-user-images\image-20200624172215657.png)

7、在 Order 调用 Pay 处添加注解 @GlobalTransactional

```java
package com.southwind.controller;

import com.southwind.service.OrderService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/save")
    @GlobalTransactional
    public String save(){
        //订单
        this.orderService.save();
        int i = 10/0;
        //支付
        this.restTemplate.getForObject("http://localhost:8020/save",String.class);
        return "success";
    }
}
```

