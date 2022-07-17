### 1.会话

#### 1.1什么是会话

服务器和客户端之间进行数据通信的一种技术，用来识别不同的客户端

客户端和服务器之间发生的一系列连续的请求和响应的过程，打开浏览器进行操作到关闭浏览器的过程，就是一次会话

会话状态是服务器和浏览器在会话过程中产生的状态信息，借助于会话状态，服务器能够把属于同一会话的一系列请求和响应过程关联起来

 实现会话有两种方式：

Session

Cookie

Session：

session常用方法:

String getId()

void setAttribute(String key,Object value)

void getAttribute(String key )

void removeAttribute（String key）

void invalidate()设置session失效

```java
${sessionScope.username},欢迎回来<a href="/logout">退出登录</a>
```

LogoutServlet

```Java
@WebServlet("/logout")
public class LogoutServlet  extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.invalidate();
        resp.sendRedirect("download.jsp");
    }
}
```

#### 1.2Cookie

Cookie是浏览器内置得一个文本文件，用来存储数据，功能和Session类似，但是Session是服务器提供的，Cookie是浏览器提供的

一旦浏览器保存了某个Cookie之后，每次访问服务器，都会将这个Cookie带到服务器，服务器在做响应得时候，又会将Cookie再传回到浏览器中，Cookie会随着请求和响应在服务器和浏览器之间来回传递

```java
//        创建Cookie
        Cookie cookie = new Cookie("name","tom");
        resp.addCookie(cookie);
```

```java
Cookie[] cookies = request.getCookies();
for (Cookie cookie : cookies) {
    String name = cookie.getName();
    String value = cookie.getValue();
    int maxAge = cookie.getMaxAge();
    out.write(name+"-"+value+"-"+maxAge+"<br/>");
}
```

```JAVA
cookie.setMaxAge(10);//设置有效期
```

setMaxAge(int time) 给Cookie设置有效期，单位为秒，一旦设置有效期之后，Cookie就开始，与是否关闭浏览器无关。





### 1.3JSp内置对象作用域

request、session、pageContext、application都可以用来存储数据

它们有什么区别？

存储数据的作用域不同：

pageContext<request<session<application

pageContext:只在当前页面有效

request：在同一次i请求 11z中有效（跳转不同页面有效）

session:在同一次会话中有效(不关浏览器就一样)

application：整个WEB应用中有效（在tomcat里）

EL可以作用于pageContext、request、session、application

优先级：

pageCOntext>request>session>application

全部取出来用xxxxScope

![image-20220228164519061](C:\Users\DELL\AppData\Roaming\Typora\typora-user-images\image-20220228164519061.png)

