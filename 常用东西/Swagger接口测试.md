Swagger接口测试

1.导入依赖

```java
<!--        swagger-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
<!--        swagger-ui-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
```

2.创建配置类SwaggerConfiguration

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
//注意更改这里的父包                .apis(RequestHandlerSelectors.basePackage("com.southwind"))
                .build().apiInfo(new ApiInfoBuilder()
                        .title("UU优选系统测试接口")
                        .description("Product商品管理")
                        .version("V1.0")
                        .build());
    }
}
```

3.启动项目

http://localhost:8081/swagger-ui.html

![image-20220411183059382](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220411183059382.png)