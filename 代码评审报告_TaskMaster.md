# 个性化代码评审报告 · TaskMaster 实验二（原生 Servlet + JSP + JDBC）

| 项 | 内容 |
|---|---|
| 课程 | Java 高级编程 / 前端技术 |
| 项目 | 实验二：极简任务管理系统 TaskMaster |
| 姓名 | 陈雨晨 |
| 学号 | 3242508227 |
| 评审对象 | 自建 `TaskMaster` 项目（`stage1-class01` → `stage1-class02`） |
| 项目目录 | `D:\taskmaster` |
| 评审方法 | AI 辅助评审五步法：准备 → 扫描 → 分级 → 复核 → 归档 |
| 日期 | 2026-10-25 |

---

## 产出物①：准备（Prepare）—— 评审范围与个性化参数

### 1.1 评审目标

对 TaskMaster 阶段一代码做静态评审，识别原生 Servlet + JSP + JDBC 写法中的坏味道；在阶段二完成登录、DAO 分层、DBUtil、编码过滤器、任务 CRUD 和 PRG 重构，并保持任务管理的外部可观察行为不变。

验收基线：

- 登录验证、任务增删改查；
- 中文不乱码；
- 连续刷新不重复提交；
- 痛点清单不少于 8 条；
- 实验报告包含环境、关键代码、运行结果、问题与排查；
- Git 标签 `stage1-class01`、`stage1-class02`。

### 1.2 评审范围与边界

| 范围 | 方法 / 区域 | 是否可重构 | 说明 |
|---|---|---|---|
| 全工程 | `src/main/java`、JSP、`web.xml`、SQL、`pom.xml` | 是（整体评审） | 识别坏味道不受区域限制 |
| 行为契约 | 登录、任务 CRUD、中文编码、PRG、用户数据隔离 | **否** | 重构后必须保持可观察行为正确 |
| 可重构区域 | Servlet、DAO、Entity、Filter、Utils、JSP、配置 | **是** | 本次重构的主要落点 |
| 阶段一基线 | `stage1-class01` 标签 | 只读对照 | 保留重复、耦合、配置痛点的原始证据 |

### 1.3 个性化参数

本实验不使用 LegacyService 的“学号尾号推导 primary/secondary/minFix”公式；TaskMaster 任务书没有该派生规则。因此个性化输入只用于身份、项目命名和结果归档：

| 参数 | 值 |
|---|---|
| 学号 | `3242508227` |
| 姓名 | 陈雨晨 |
| 阶段标签 | `stage1-class01`、`stage1-class02` |
| 任务书痛点下限 | 不少于 8 条 |
| 本次实际识别 | 11 条 |
| 评审重点 | 原生 JDBC 的重复、Servlet 与 SQL/视图的耦合、部署与编码配置 |

### 1.4 评审依据

按质量五维判断：**正确性、可读性、可维护性、性能、安全性**。坏味道同时归类为任务书要求的 **重复 / 耦合 / 配置** 三类，避免只按严重度罗列而无法指导重构。

---

## 产出物②：扫描（Scan）—— 坏味道清单与坏代码快照

### 2.1 扫描方式

采用三层扫描：

1. 对 `stage1-class01` 源码逐文件静态阅读，定位重复 JDBC、SQL 拼接、ResultSet 映射和 JSP 逻辑；
2. 对 `stage1-class02` 代码做结构复核，确认 DAO、Filter、PRG、CSRF 和登录链路；
3. 通过真实 HTTP 请求回归登录、中文新增、刷新、搜索、编辑、状态、删除、未登录和 CSRF。

### 2.2 坏味道总表（识别总数 = 11）

| 序号 | 位置（阶段一或当前代码） | 类型 | 归类 | 严重度 | 处置 |
|---|---|---|---|---|---|
| ① | `TaskListServlet.java:31-37` | 数据库 URL、用户名、密码来源写在 Servlet | 配置 | P1 | 已修复：`DBUtil` + 环境变量/`db.properties` |
| ② | `TaskListServlet.java:41-42` | 驱动加载和连接获取重复 | 重复 | P1 | 已修复：连接创建收口到 `DBUtil` |
| ③ | `TaskListServlet.java:48-62` | ResultSet 字段映射重复 | 重复 | P1 | 已修复：`TaskDaoImpl#map` |
| ④ | `TaskListServlet.java:44-47` | SQL 与 Web 控制器耦合 | 耦合 | P1 | 已修复：DAO 接口/实现 |
| ⑤ | `list.jsp:4-10, 44-69` | JSP 脚本片段承担数据判断和格式化 | 耦合 | P2 | 部分修复：展示属性下沉，但仍保留原生脚本片段 |
| ⑥ | `TaskListServlet.java:46, 69-70` | 固定 `user_id=1` 和“任务管理员” | 耦合 | P0 | 已修复：登录 Session + `AuthFilter` + DAO 用户条件 |
| ⑦ | `list.jsp:1` 与阶段一 `web.xml` | 编码靠每个 JSP 单独声明，无统一 Filter | 配置 | P1 | 已修复：`EncodingFilter` 映射 `/*` |
| ⑧ | `stage1-class01` `web.xml:9-16` | Servlet/Filter/错误页映射手工增加 | 配置 | P2 | 部分修复：补齐统一错误页和 Filter，仍保留显式映射 |
| ⑨ | `Task.java`、DAO 排序、JSP 判断 | 状态/优先级魔法数字重复 | 重复 | P2 | 部分修复：展示文案集中，数字枚举化仍可继续 |
| ⑩ | `list.jsp:56, 59` | 标题、描述直接输出 | 耦合 | P0 | 已修复：`WebUtil.escapeHtml` 输出 |
| ⑪ | `DBUtil#getConnection` | 每个请求新建物理连接 | 配置 | P2 | 遗留：本阶段保留原生 JDBC，未引入连接池 |

### 2.3 扫描得到的坏代码快照

**连接参数和 JDBC 直接写在 Servlet：**

```java
String url = System.getenv().getOrDefault(
        "TASKMASTER_DB_URL",
        "jdbc:mysql://127.0.0.1:3306/taskmaster?...");
String username = System.getenv().getOrDefault("TASKMASTER_DB_USER", "root");
String password = System.getenv().getOrDefault("TASKMASTER_DB_PASSWORD", "");

Class.forName("com.mysql.cj.jdbc.Driver");
Connection connection = DriverManager.getConnection(url, username, password);
```

**ResultSet 映射与 Servlet 生命周期绑定：**

```java
while (rs.next()) {
    Task task = new Task();
    task.setId(rs.getLong("id"));
    task.setTitle(rs.getString("title"));
    task.setStatus(rs.getInt("status"));
    task.setPriority(rs.getInt("priority"));
    tasks.add(task);
}
```

**JSP 直接输出未转义内容：**

```jsp
<h3><%= task.getTitle() %></h3>
<p><%= task.getDescription() == null ? "暂无描述" : task.getDescription() %></p>
```

---

## 产出物③：分级（Grade）—— 分级与修复计划

### 3.1 分级维度

- **严重度**：P0（登录/安全/数据隔离）＞ P1（功能正确性与主要可维护性）＞ P2（可读性、配置和性能）。
- **处置**：本次修复、阶段二部分修复、后续演进。
- **行为约束**：不能为了消除坏味道而破坏登录、CRUD、中文编码或 PRG。

### 3.2 分级结果表

| 序号 | 类型 | 严重度 | 处置 |
|---|---|---|---|
| ⑥ | 固定用户身份、无登录隔离 | P0 | **本次修复**：登录、Session、AuthFilter |
| ⑩ | 未转义输出，存在 XSS 风险 | P0 | **本次修复**：HTML 转义 |
| ① ② ③ ④ ⑦ | 配置分散、重复 JDBC、SQL 耦合、编码分散 | P1 | **本次修复**：DBUtil、DAO、EncodingFilter |
| ⑤ ⑧ ⑨ | JSP 脚本、显式映射、魔法数字 | P2 | **部分修复**，保留原生技术形态 |
| ⑪ | 每请求新建物理连接 | P2 | **遗留**，后续引入 DataSource/连接池 |

### 3.3 修复计划

TaskMaster 不使用 LegacyService 的 `minFix` 数值公式。按任务书“不少于 8 条”的要求，本次识别 11 条，完成 **7 条完全修复、3 条部分修复、1 条遗留记录**。

重构顺序：

1. 先补登录与用户隔离，避免后续 CRUD 继续依赖固定 `user_id=1`；
2. 抽 `DBUtil`，再建 DAO 接口/实现，移动 SQL 与映射；
3. 增加 `EncodingFilter` 和 `AuthFilter`，统一横切逻辑；
4. 用 `WebUtil` 统一 HTML 转义、CSRF 和一次性提示；
5. 新增/编辑/删除/状态变更统一 POST + Redirect；
6. 用真实 HTTP 请求做回归，最后补痛点清单、实验报告和 Git 标签。

---

## 产出物④：复核（Review）—— 复核记录与误报剔除

### 4.1 逐条复核（AI 扫描 → 人工确认）

| 序号 | 复核结论 | 说明 |
|---|---|---|
| ① | 真坏味道 | 连接参数进入 Servlet 会在新增/修改/删除时重复扩散 |
| ② | 真坏味道 | 驱动加载和连接获取可以直接被 DAO/DBUtil 吸收 |
| ③ | 真坏味道 | 字段映射重复且列名变更时容易漏改 |
| ④ | 真坏味道 | Servlet 同时做 Web 和持久化，职责过宽 |
| ⑤ | 真坏味道（部分保留） | 当前仍有脚本片段，但数据逻辑已减少；继续用 JSTL/EL 可进一步收敛 |
| ⑥ | 真坏味道 | 固定用户导致未登录也能读写同一批任务 |
| ⑦ | 真坏味道 | Filter 之前必须在每个 JSP/请求重复编码设置 |
| ⑧ | 部分真坏味道 | 显式映射便于教学观察，但新增用例确实增加配置成本 |
| ⑨ | 真坏味道 | 数字状态在实体、DAO、JSP 中多处分叉 |
| ⑩ | 真坏味道 | 标题和描述来自数据库，直接输出可形成存储型 XSS |
| ⑪ | 真问题，但有阶段取舍 | 原生 JDBC 教学阶段未引入连接池，作为性能遗留而非功能缺陷 |

### 4.2 误报剔除

没有把“显式 `web.xml` 映射”直接判为错误：Servlet 3.0 注解可以替代一部分映射，但显式配置在教学中更利于观察 Filter/Servlet 的装配关系。因此第 ⑧ 条降为 P2、部分修复。

同样没有把“未使用 Spring”判为缺陷：本实验要求原生 Servlet + JSP + JDBC，Spring 是参考项目中另一项作业的技术栈，不在本次范围内。

### 4.3 行为与功能复核

实际 HTTP 回归结果：

| 验证项 | 实际结果 |
|---|---|
| 登录成功 | `LOGIN_OK=True` |
| 错误密码 | 显示“用户名或密码不正确” |
| 未登录访问 `/tasks` | `302` → `/taskmaster/login` |
| 中文新增 | 列表出现中文标题，无乱码 |
| 新增后连续刷新 | 第一次含提示计数 2，第二次计数 1；数据库只有一条 |
| 搜索 | 中文关键词 + 待办状态命中 |
| 编辑 | 旧标题消失，新标题出现一次 |
| 状态变更 | 变为“已完成”，提示“任务已完成” |
| 删除 | 提示“任务已删除”，列表不再出现 |
| 缺少 CSRF | HTTP `403` |
| XSS 标题 | 输出为 `&lt;script&gt;...`，原始 script 未出现 |
| 退出登录 | Session 失效，回到登录页 |

### 4.4 构建与部署复核

```text
mvn -o -q clean package
BUILD SUCCESS
target/taskmaster.war
```

源码使用 `javax.servlet` 并按 Java 11 编译。由于本机没有 JDK 11 和 Tomcat 9，本次运行验证使用 Tomcat 11 + `jakartaee-migration` 临时适配包；Tomcat 9 实机验证作为环境遗留项记录，不能把它写成已经完成。

---

## 产出物⑤：归档（Archive）—— 归档记录与遗留

### 5.1 运行/归档输出

```text
TASKMASTER|11|7|3|1
BUILD_SUCCESS
LOGIN_OK=True
ADD_FLASH=True COUNT_WITH_FLASH=2 COUNT_AFTER_REFRESH=1
SEARCH_HIT=True
UPDATE_FLASH=True OLD_GONE=True NEW_COUNT=1
STATUS_FLASH=True DONE_TEXT=True
DELETE_FLASH=True DELETED_GONE=True
UNAUTH_STATUS=302 LOCATION=/taskmaster/login
WRONG_PASSWORD_MSG=True
CSRF_MISSING_STATUS=403
XSS_ESCAPED=True RAW_SCRIPT_PRESENT=False
```

- 识别总数 = **11**
- 完全修复 = **7**（① ② ③ ④ ⑥ ⑦ ⑩）
- 部分修复 = **3**（⑤ ⑧ ⑨）
- 遗留 = **1**（⑪ 连接池）

### 5.2 重构落点

| 重构点 | 文件 | 作用 |
|---|---|---|
| DBUtil | `src/main/java/com/taskmaster/util/DBUtil.java` | 连接参数、驱动和连接创建统一 |
| DAO 接口/实现 | `TaskDao.java`、`TaskDaoImpl.java` | SQL、参数绑定、ResultSet 映射下沉 |
| JavaBean | `Task.java`、`User.java` | 任务与用户数据承载 |
| 编码过滤器 | `EncodingFilter.java` | 请求/响应 UTF-8 |
| 登录拦截 | `LoginServlet.java`、`AuthFilter.java` | Session 登录与访问控制 |
| PRG 写操作 | `TaskAddServlet`、`TaskUpdateServlet`、`TaskDeleteServlet`、`TaskStatusServlet` | 写成功后 302 回列表 |
| CSRF/XSS | `WebUtil.java` | 令牌校验与 HTML 转义 |
| 视图 | `login.jsp`、`list.jsp`、`add.jsp`、`edit.jsp`、`error.jsp` | 登录、列表、表单和错误页 |

### 5.3 遗留问题（如实记录）

1. 本机未安装 JDK 11/Tomcat 9，实际验证使用 Tomcat 11 迁移包；严格环境仍需复测。
2. `DBUtil` 仍是每次请求新建物理连接，未引入 DBCP2/HikariCP/JNDI DataSource。
3. JSP 仍保留脚本片段，未使用 JSTL/EL 或自定义标签。
4. 状态/优先级数字仍可在枚举化后进一步统一。
5. 密码摘要使用教学用加盐 SHA-256，生产应替换为 BCrypt/Argon2。

### 5.4 检查点对照

| 检查点 | 内容 | 状态 |
|---|---|---|
| CP1 | 阶段一列表与 MySQL 通路 | ✅ |
| CP2 | 登录、Session、用户隔离 | ✅ |
| CP3 | DAO、DBUtil、EncodingFilter | ✅ |
| CP4 | 新增/编辑/删除/状态与 PRG | ✅ |
| CP5 | 中文、CSRF、XSS、错误页 | ✅ |
| CP6 | 痛点清单、实验报告、Git 标签 | ✅ |
| CP7 | JDK 11 + Tomcat 9 实机复测 | ⚠️ 环境遗留 |

---

*评审方法：AI 辅助静态扫描 + 人工复核 + 真实 HTTP 回归。项目保持原生 Servlet/JSP/JDBC 技术栈，未把参考项目的 Spring 方案混入本次实现。*
