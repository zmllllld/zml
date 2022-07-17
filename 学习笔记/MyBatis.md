### 1.持久层框架

持久层/持久化  对数据库的操作

操作数据库的框架

MyBatis、 MyBatis Plus、Hibernate、Spring Data JPA

对JDBC的封装

#### 1.1MyBatis

ORM/ORMapping

Object Relationship Mapping 对象关系映射

把面向对象的编程语言和关系型数据库进行映射

又名iBatis	

特点：

1.简化了jdbc代码的开发

2.非常灵活

3.需要开发者手动定义SQL，SQL放置在xml文件中，降低耦合度

4.支持动态SQL，根据具体业务灵活实现需求

使用MyBatis

1.安装Maven环境

Maven：用来快速构建java工程的

分布式项目：多个子项目构成一个大项目

快速给项目导入jar包

Maven是一个服务，跟Tomcat类似，下载到电脑，解压，配置环境变量

配置阿里云镜像文件

2.创建Maven工程

pom.xml project object model 工程对象模型

pom.xml 添加代码

```java
       <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.25</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.7</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.22</version>
        </dependency>
```

3.创建数据表对应的实体类

导入lombok依赖后，用注解@Data可以即可自动生成getset方法

```java
package com.southwind.entity;

import lombok.Data;

@Data
public class Book {
    private Integer bookid;
    private String name;
    private Double price;
}
```

4.创建MyBatis配置文件 config.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!-- MyBatis环境 -->
    <environments default="dev">
        <environment id="dev">
            <transactionManager type="JDBC"></transactionManager>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/test12"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>

</configuration>
```

5、Mapper代理实现自定义接口

自定义一个接口，由MyBatis去动态实现该接口，返回实例化对象，直接操作该对象即可

```java
package com.southwind.mapper;

import com.southwind.entity.Book;

import java.util.List;

public interface BookMapper {
    public List<Book> list();
}
```

创建接口对应的Mapper.xml,定义接口方法对应的SQL语句

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.southwind.mapper.BookMapper">

    <select id="list" resultType="com.southwind.entity.Book">
        select * from book
    </select>

</mapper>
```

6.在config.xml中注册BookMapper.xml

```xml
<mappers>
    <mapper resource="com/southwind/mapper/BookMapper.xml"/>
</mappers>
```

pom.xml添加resource配置，允许读取java路径下的xml文件

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
            </includes>
        </resource>
    </resources>
</build>
```

测试类

```java
package com.southwind;

import com.southwind.entity.Book;
import com.southwind.mapper.BookMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        //加载MyBatis配置文件
        InputStream resourceAsStream = Test.class.getClassLoader().getResourceAsStream("config.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(resourceAsStream);
        SqlSession sqlSession = factory.openSession();
        BookMapper mapper = sqlSession.getMapper(BookMapper.class);
        List<Book> list = mapper.list();
        for (Book book : list) {
            System.out.println(book);
        }
    }
}
```

1.读取config.xml，获取数据源信息从而建立数据库连接池

2.读取Mapper接口+Mapper.xml 接口中定义了方法，Mapper.xml定义了实现方法的SQL语句，通过这些动态生成代理对象

3.操作该对象，调用方法，会自动执行方法对应的SQL语句，并完成对结果集的封装

除了查询方法，增删改方法都需要进行提交

```java
package com.southwind.mapper;

import com.southwind.entity.Book;

import java.util.List;

public interface BookMapper {
    public List<Book> list();
    public Book findById(Integer id);
    public void add(Book book);
    public void update(Book book);
    public void delete(Integer id);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.southwind.mapper.BookMapper">

    <insert id="add">
        insert into book(name,price) values(#{name},#{price})
    </insert>

    <update id="update">
        update book set name = #{name},price = #{price} where bookid = #{bookid}
    </update>

    <delete id="delete">
        delete from book where bookid = #{id}
    </delete>

    <select id="list" resultType="com.southwind.entity.Book">
        select * from book
    </select>

    <select id="findById" resultType="com.southwind.entity.Book">
        select * from book where bookid = #{id}
    </select>

</mapper>
```

```java、
public class Test {
    public static void main(String[] args) {
        //加载MyBatis配置文件
        InputStream resourceAsStream = Test.class.getClassLoader().getResourceAsStream("config.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(resourceAsStream);
        SqlSession sqlSession = factory.openSession();
        BookMapper mapper = sqlSession.getMapper(BookMapper.class);
//        List<Book> list = mapper.list();
//        for (Book book : list) {
//            System.out.println(book);
//        }
//        System.out.println(mapper.findById(1));
//        Book book = new Book();
//        book.setName("MyBatis快速入门");
//        book.setPrice(100.0);
//        mapper.add(book);
//        Book book = mapper.findById(10);
//        book.setName("MyBatis进阶");
//        book.setPrice(200.0);
//        mapper.update(book);
//        mapper.delete(10);
//        sqlSession.commit();
        for (Goods goods : mapper.findGoodsList()) {
            System.out.println(goods);
        }
    }
}
```

### 2.MyBatis底层实现

MyBatis最核心的功能就是动态代理机制

MyBatis使用：

1.自定义接口

2.创建接口对应的SQL

3.加载MyBatis，获取动态代理对象，进行操作

具体实现步骤：

1.读取xml文件，加载数据源

2.执行SQL，根据开发者自定义的接口，生成动态代理对象

3.创建一个类，实现InvocationHandler接口，该类就具备了创建动态代理对象的功能



