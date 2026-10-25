# 实验报告二：SpringMVC 与 DispatcherServlet

## 一、实验目的

使用 `DispatcherServlet` 作为中央控制器，把原生 Servlet 的路径分派改造成 `@Controller`、`@RequestMapping`、`@GetMapping`、`@PostMapping`，并配置视图解析器。

## 二、实验环境

| 项目 | 环境 |
|---|---|
| Spring MVC | 6.1.1 |
| Servlet | Jakarta Servlet 6.0 |
| 容器 | Tomcat 11.0.26 |
| JDK | JDK 24.0.2，编译目标 17 |
| 本地依赖 | 全部来自本地 Maven 缓存，未下载 |

## 三、关键代码

`web.xml` 注册 Spring 根容器和 DispatcherServlet：

```xml
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring-mvc.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

`TaskController` 用注解替代 Servlet：

```java
@Controller
public class TaskController {
    @GetMapping("/tasks")
    public String list(...) { ... return "list"; }

    @PostMapping("/tasks/add")
    public String add(...) { ... return "redirect:/tasks"; }
}
```

`spring-mvc.xml` 配置视图解析器和拦截器：

```xml
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/views/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```

## 四、运行结果

```text
MVC_LOGIN_OK=True
MVC_LIST_OK=True
MVC_ADD=True
COUNT1=2
COUNT2=1
MVC_UPDATE=True
MVC_STATUS=True
MVC_DELETE=True
MVC_UNAUTH=302 LOCATION=/taskmaster/login
MVC_CSRF_STATUS=403
MVC_404_STATUS=404
```

`COUNT1=2` 是第一次 GET 同时包含一次性提示和任务卡片；再次 GET 后 `COUNT2=1`，证明 PRG 生效。

## 五、遇到的问题与排查

| 现象 | 原因 | 处理 |
|---|---|---|
| `/tasks` 不经过登录拦截 | 只配置了 `/tasks/**`，没有匹配无尾斜杠的 `/tasks` | 同时增加 `/tasks` 和 `/tasks/**` 映射 |
| 不存在的任务最初返回 500 | 异常处理链的状态码没有按预期传播 | Controller 直接设置 404 并返回 `error` 视图 |
| 旧 Servlet 与 Jakarta 控制器混在一起 | Servlet/JSP 规范从 javax 迁移到 Jakarta | 把原生 javax 实现移到 `legacy/native-javax`，不参与 SSM 编译 |

## 六、结论

DispatcherServlet 接管了 URL 分派、控制器调用和视图选择；登录、CRUD、状态流转、PRG、错误页均通过真实 HTTP 回归。该课标签为 `stage2-class02`。
