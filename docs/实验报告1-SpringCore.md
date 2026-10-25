# 实验报告一：Spring Core IoC 与 DI

## 一、实验目的

把原生代码中由 Servlet 自己 `new TaskDaoImpl()`、`new UserDaoImpl()` 的对象创建权交给 Spring 容器，并对比构造器注入与 Setter 注入。

## 二、实验环境

| 项目 | 环境 |
|---|---|
| JDK | JDK 24.0.2，源码编译目标 Java 17（Spring 6.1.1 要求） |
| Maven | 3.9.16，本地离线构建 |
| 容器 | Tomcat 11.0.26 验证 |
| 数据库 | MySQL 8.0.45，`taskmaster` |
| Spring | 6.1.1，本地 Maven 缓存已有，未下载 |

## 三、关键代码

`applicationContext.xml` 明确声明 DAO/Service，并让 Spring 创建对象：

```xml
<bean id="taskService" class="com.taskmaster.service.impl.TaskServiceImpl">
    <constructor-arg ref="taskDao"/>
</bean>

<bean id="userService" class="com.taskmaster.service.impl.UserServiceImpl">
    <property name="userDao" ref="userDao"/>
</bean>
```

构造器注入采用不可变字段：

```java
private final TaskDao taskDao;

public TaskServiceImpl(TaskDao taskDao) {
    this.taskDao = taskDao;
}
```

Setter 注入用于对照：

```java
private UserDao userDao;
public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
}
```

## 四、运行结果

```text
mvn -o -q clean package
BUILD SUCCESS
SPRING_CORE_LIST=200
HAS_USER=True
HAS_TASK=True
```

登录和列表均能通过 Spring 注入的 Service 完成；Servlet 中不再 `new TaskDaoImpl()`。

## 五、遇到的问题与排查

| 现象 | 原因 | 处理 |
|---|---|---|
| Spring 6 编译目标不能继续使用 Java 11 | Spring 6 需要 Java 17+ | 将源码编译目标改为 17，使用本机 JDK 24 编译 |
| 容器启动时 Bean 找不到 | XML 中 Bean id 与 Servlet 查询名不一致 | 统一使用 `taskService`、`userService` 并复核 `getBean` |
| 旧 `SpringContextListener` 的容器生命周期不便于后续 MVC 分层 | 根容器应由 Spring Web 管理 | 第二课改为 `ContextLoaderListener` + DispatcherServlet |

## 六、结论

Spring Core 把 Service、DAO 的创建和依赖装配收口到容器。构造器注入保证依赖完整，Setter 注入便于替换；这一课的标签为 `stage2-class01`。
