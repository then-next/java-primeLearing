# TaskMaster · SSM 阶段

TaskMaster 是一个用于拆解原生开发痛点的任务管理系统。项目保留阶段一的 Servlet + JSP + JDBC 代码作为对照，再逐步引入 Spring Core、SpringMVC、MyBatis 和声明式事务，最终形成 Controller → Service → Mapper 的 SSM 三层架构。

## 技术栈

- **Spring Core 6.1.1**：IoC/DI，构造器注入与 Setter 注入；
- **SpringMVC 6.1.1**：`DispatcherServlet`、注解控制器、视图解析器、拦截器；
- **MyBatis 3.5.14 + MyBatis-Spring 3.0.3**：Mapper 接口、XML、JDK 动态代理；
- **Spring JDBC/Tx 6.1.1**：`PooledDataSource` 与 `DataSourceTransactionManager`；
- **Jakarta Servlet 6.0**：Tomcat 11；
- **MySQL 8.0.x**；
- **JDK 17+**、Maven 3.9.x。

## 已实现功能

- 登录/退出、Session 登录拦截；
- 任务列表、关键词搜索、状态筛选和统计；
- 新增、编辑、删除、开始/完成/重开；
- 中文编码、CSRF、HTML 转义、统一错误页；
- POST + Redirect 的 PRG 写操作；
- MyBatis Mapper XML 持久化；
- Service 写方法声明式事务；
- 用户数据隔离。

## 快速开始

1. 执行 `sql/schema.sql` 创建 `taskmaster`、`t_user`、`t_task` 和演示数据。
   已有旧版表时，按 `sql/migrate_existing_taskmaster.sql` 迁移，不要直接删表。
2. 复制 `src/main/resources/jdbc.properties.example` 为 `jdbc.properties`，填写本机数据库账号；真实文件已被 Git 忽略。
3. 执行：

   ```bash
   mvn clean package
   ```
4. 将 `target/taskmaster.war` 部署到 Tomcat 11 的 `webapps`。
5. 访问 `http://localhost:8080/taskmaster/`。
6. 演示账号：`admin / admin123`。

> 本地实际配置可使用 `.runtime/jdbc.properties`；不要把它加入版本库。没有配置时容器会明确提示缺少 `jdbc.properties`。

## 阶段与标签

| 标签 | 内容 |
|---|---|
| `stage1-class01` | 原生任务列表与 JDBC 查询 |
| `stage1-class02` | 原生登录、DAO、EncodingFilter、CRUD、PRG |
| `stage2-class01` | Spring Core DI |
| `stage2-class02` | SpringMVC + DispatcherServlet |
| `stage2-class03` | MyBatis Mapper + XML |
| `stage2-class04` | SSM 整合 + 声明式事务 |

## 三层架构

```text
Browser
  → DispatcherServlet
  → Controller
  → Service (@Transactional)
  → Mapper 代理
  → Mapper XML
  → MySQL
```

详细调用图、每层职责和三套配置分工：

- [三层架构调用图与职责说明](docs/三层架构调用图与职责说明.md)

## 文档

- [持久层重构收益对比表](docs/持久层重构收益对比表.md)
- [实验报告一：Spring Core](docs/实验报告1-SpringCore.md)
- [实验报告二：SpringMVC](docs/实验报告2-SpringMVC.md)
- [实验报告三：MyBatis](docs/实验报告3-MyBatis.md)
- [实验报告四：SSM 事务整合](docs/实验报告4-SSM事务整合.md)
- [阶段一实验报告](docs/实验报告.md)
- [原生开发六大痛点清单](docs/原生开发六大痛点清单.md)

五产出物代码评审报告单独放在 `D:\代码评审报告_TaskMaster_SSM.md`，不放进代码包。

## 目录说明

- `src/main/java/com/taskmaster/controller`：Web 控制器、全局异常/模型建议；
- `src/main/java/com/taskmaster/service`：业务接口与实现；
- `src/main/java/com/taskmaster/mapper`：MyBatis Mapper 接口，无实现类；
- `src/main/resources/mappers`：Mapper XML；
- `src/main/resources/applicationContext.xml`：根容器、DataSource、MyBatis、事务；
- `src/main/resources/spring-mvc.xml`：MVC 子容器、视图解析器、拦截器；
- `src/main/resources/mybatis-config.xml`：MyBatis 全局设置；
- `legacy/native-javax`：阶段一 Servlet/Filter 留档；
- `legacy/native-jdbc`：阶段一 JDBC DAO/DBUtil 留档。
