### 1.Servlet

Servlet是一个接口，描述某一个功能，类实现该接口之后，就具备了这个功能。

使类具备接收客户端请求以及给客户端请求做出响应的功能

类具备ServerSocket的功能，开发者不需要自己手动写ServerSocket代码，直接使用即可

![image-20220115201246893](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220115201246893.png)

init 初始化Servlet，获取初始化参数

getServletConfig 获取Servlet信息

service 实现业务功能，核心方法

getServletInfo 获取Servlet信息

destroy Tomcat服务关闭的时候，关闭某些资源

需要用的只有Service

每一个Servlet，都需要实现这5个方法，但是只用1个，4个都是无意义的实现，很浪费资源

HTTP请求分为GET.POST

GET请求和post请求所做的业务不同

servlet体系：

第一层：servelet接口

第二层：GenericServlet，节省资源，只实现service方法

第三次：HttpServlet ，对get和post方法进行重写

使用时只需要继承HttpServlet即可，进行简化的开发

### 2.JSP

Servlet返回视图层资源非常麻烦，需要手动拼接HTML代码，很容易出错，效率极低。

JSP就是解决这一问题 的

JSP全称：java server page Java服务页面

提供了一套模板，在模板中可以直接开发html代码，并且将Java代码进行结合，可以在html中加入Java代码

为什么可以整合呢？因为jsp底层就是一个Servlet，每一个jsp最终都会转换成servlet，将代码逐行读出来，用write的方式返回

jsp和servlet的关系？底层机制？解析方式？

底层运行机制：jsp是一个模板，可以将html代码和Java代码混合起来，然后通过jsp引擎，把jsp整个转换为servlet，转换为servlet之后将jsp中的内容全部取出来，一行一行的将代码解析出来往外面写，呈现出来

![image-20220115220316574](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220115220316574.png)

  

Servlet和jsp分工：

Servlet和jsp本质是一样的，都是Servlet，Servlet用来处理业务逻辑，生成业务数据，jsp负责视图层数据展示。

Servlet到jsp的跳转：

1.转发 req.getRequestDispatcher("test.jsp").forward(req,resp);

request一定要先赋值setAttribute,再进行跳转，否则jsp无法接收到数据

```java
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//       业务数据
        List<Student> list = Arrays.asList(
                new Student(1,"张三",22),
                new Student(2,"小米",22),
                new Student(3,"王五",25)
        );

        //      将业务数据传给jsp
        req.setAttribute("list",list);
        //请求来到jsp
        req.getRequestDispatcher("test.jsp").forward(req,resp);

    }

}
```

2.重定向 resp.sendRedirect("test.jsp")

#### 2.1JSTL标签库

JSP提供的一套标签，使用这套标签可以简化代码的开发

使用步骤：

1.导入jar包

在WEB-INF路径下创建lib文件夹，将jar复制进来，在工程中进行导入配置

2.jsp页面导入标签进行使用

![image-20220226163915617](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220226163915617.png)

EL表达式，简化JSP代码

${list} = request.getAttribute("list")

#### 2.2JSP内置对象

JSP默认存在的java对象，不需要开发者手动创建，直接使用即可

1.request：HttpServletRequest的一个对象，接收客户端请求

2.response：HttpServletResponse的一个对象，给客户端做出响应

3.pageContext：PageContext的一个对象，页面上下文，获取页面信息

4.session:HttpSession的一个对象，表示会话，存储用户登 录信息

5.application:ServletContext的一个对象，表示当前web应用，全局对象，存储所有用户信息

6.config：JSP转换之后的Servlet对象，获取当前Servlet的信息

7.out：JspWriter对象，向客户端进行输出

8.page：当前jsp对应的servlet对象

9.exception：当前JSP发生的异常对象

JSP内置对象是在jsp生成的Servlet中进行初始化的，因为jsp代码最终会复制到Servlet中，所以预先在Servlet中创建的对象，可以直接在JSP中使用，因为最终代码是一个整体

JSP内置对象是Servlet提供的Java对象

Cookie是浏览器的一个组件，Java对该组件进行了封装，Cookie对象表示浏览器组件