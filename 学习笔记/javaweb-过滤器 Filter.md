### 过滤器 Filter

中文乱码，在表单中输入中文信息，提交到后台，中文没有正确显示

```java
req.setCharacterEncoding("UTF-8");
String name = req.getParameter("name");
```

必须先设置编码格式，再取值，如果先取值，再设置编码格式，则不起作用

1.过滤器是服务器端的代码，Servlet 之前的组件（请求先进Filter，再进Servlet），用于拦截传入的请求响应，对请求进行统一处理（设置编码解决中文乱码问题）

2.监视、修改处理正在客户端和服务器之间传输的数据流

过滤器就是处于客户端和Servlet之间的一个组件，对客户端和Servlet之间的数据传输进行处理

 ### 1.2如何使用过滤器

和Servlet类似，Filter本身是个接口，创建过滤器，只需要创建一个类，实现Filter接口即可。

```java
@WebFilter("/student")
public class CharacterFilter  implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding("UTF-8");
       //过滤后一定要加filterChain释放，让请求继续走filterChain.doFilter(servletRequest,servletResponse);
    }
}
```

过滤多个   ![image-20220227153725763](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220227153725763.png)

过滤所有请求就只加个/

#### 1.3过滤器的应用

中文乱码的处理，敏感词替换、资源的访问权限（必须是登录用户才能下载资源）

敏感词替换

```java
  <h1>发表帖子</h1>
  <form action="/content" method="post">
    <textarea cols="30" rows="30" name="content"></textarea>
    <input type="submit" value="发布"/>
  </form>
  </body>
</html>
```

ContentFilter

```java
@WebFilter("/content")
public class ContentFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String content = servletRequest.getParameter("content");
//        去掉content中的包含的"敏感词"
      content = content.replaceAll("敏感词", "***");
      servletRequest.setAttribute("content",content);
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
```

ContentServlet

```java
@WebServlet("/content")
public class ContentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String content = (String)req.getAttribute("content");
        System.out.println(content);
    }
}
```

资源的访问权限

download.jsp页面必须是登录用户才能访问

download.jsp

```java
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

    <a href="资源一">资源一</a>
    <a href="资源二">资源二</a>
    <a href="资源三">资源三</a>


</body>
</html>
```

downloadFilter

```java
@WebFilter("/download.jsp")
public class DownloadFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("进入了过滤器");
        //        获取seesion，判断session中是否有值
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        String username = (String)session.getAttribute("username");
        if (username!=null){
            filterChain.doFilter(servletRequest,servletResponse);
        }else {
            HttpServletResponse response = (HttpServletResponse)servletResponse;
            response.sendRedirect("/login.jsp");
        }

    }
}
```

LoginServlet

```java
@WebServlet("/login")
public class LoginServlet  extends HttpServlet {
    private  static  final  String USERNAME = "admin";
    private static final  String PASSWORD ="123456";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username= req.getParameter("username");
        String password = req.getParameter("password");
        if (username.equals(USERNAME) && password.equals(PASSWORD)){
//           登录成功， 存入session中
            HttpSession session = req.getSession();
            session.setAttribute("username",username);
        }else {
            resp.sendRedirect("/login.jsp");
        }
    }
}
```