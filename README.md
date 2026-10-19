# TaskMaster

TaskMaster 是一个用于“数痛点”的极简任务管理系统。项目刻意采用 Servlet + JSP + JDBC 的原生组合，不使用 Spring、MyBatis 或前端框架，以便把请求转发、参数接收、JDBC 映射、页面渲染和部署配置中的摩擦完整暴露出来。

## 当前阶段

- `stage1-class01`：环境检查、建库建表、任务列表 Servlet 与 `list.jsp`，跑通浏览器到 MySQL 的查询通路。
- `stage1-class02`：Task 实体、DBUtil、DAO 接口与实现、EncodingFilter、登录验证，以及新增/编辑/删除/状态变更和 PRG 跳转。

## 环境

- JDK 11（源码按 Java 11 编译；本机可用 JDK 24 执行 `--release 11` 构建）
- Tomcat 9.0.x（源码使用 `javax.servlet`）
- MySQL 8.0.x
- Maven 3.9.x
- VS Code + Java/Extension Pack（可选）

## 快速开始

1. 执行 `sql/schema.sql` 创建 `taskmaster` 库、`t_user`、`t_task` 和演示数据。
2. 复制 `src/main/resources/db.properties.example` 为 `db.properties`，填写本机数据库账号。也可用环境变量 `TASKMASTER_DB_URL`、`TASKMASTER_DB_USER`、`TASKMASTER_DB_PASSWORD` 覆盖。
3. 执行 `mvn clean package`，得到 `target/taskmaster.war`。
4. 将 WAR 放入 Tomcat 9 的 `webapps`，启动后访问 `http://localhost:8080/taskmaster/`。
5. 演示账号：`admin` / `admin123`。

更完整的环境、运行结果与排查记录见 `docs/实验报告.md`，痛点归纳见 `docs/原生开发六大痛点清单.md`。
