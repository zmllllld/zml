### Spring Boot整合Spring Data JPA

Spring Data JPA是Spring全家桶的一个持久层解决方案

1.pom.xml导入依赖

```x'm'l
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

2、实体类和数据表进行映射

```java
@Data
@Entity
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column
  private String name;
  @Column
  private Integer age;
}
```

3、创建接口

```java
package com.southwind.repository;

import com.southwind.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Integer> {
}
```

4.application.xml

```java
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/dbname
    username: root
    password: root
  jpa:
    show-sql: true
```

5.controller

```java
package com.southwind.controller;


import com.southwind.entity.Account;
import com.southwind.repository.AccountRepository;
import com.southwind.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2022-03-19
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/list2")
    public List<Account> list2(){
        return this.accountRepository.findAll();
    }

}
```

PQL Java 持久层查询语言，类似 SQL，区别在于不查询数据库，而是查询数据库对应的实体类。

```java
package com.southwind.repository;

import com.southwind.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Integer> {
    public Account getByAge(Integer age);
    public Account queryByName(String name);
    @Query("select a from Account a where a.name like %?1%")
    public List<Account> findByNameLike(String name);
}
```



