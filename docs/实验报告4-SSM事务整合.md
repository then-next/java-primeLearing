# 实验报告四：SSM 整合与声明式事务

## 一、实验目的

完成 Controller → Service → Mapper 的 SSM 闭环，补上任务修改、删除、状态流转，并用 Spring 声明式事务统一管理写操作。

## 二、实验环境

| 项目 | 环境 |
|---|---|
| Spring Core / MVC / JDBC / Tx | 6.1.1 |
| MyBatis / MyBatis-Spring | 3.5.14 / 3.0.3 |
| Servlet | Jakarta Servlet 6.0 |
| 容器 | Tomcat 11.0.26 |
| 数据库 | MySQL 8.0.45 |
| JDK / Maven | JDK 24.0.2（编译 Java 17）/ Maven 3.9.16 |

## 三、关键代码

事务管理器：

```xml
<bean id="transactionManager"
      class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>
<tx:annotation-driven transaction-manager="transactionManager"/>
```

Service 写方法声明事务：

```java
@Transactional
public long create(Task task) {
    taskMapper.insert(task);
    return task.getId() == null ? 0 : task.getId();
}

@Transactional
public boolean updateStatus(long id, long userId, int status) {
    return taskMapper.updateStatus(id, userId, status) == 1;
}
```

## 四、运行结果

本地事务测试：

```text
TRANSACTION_SMOKE_OK
```

测试先通过 `TransactionTemplate` 插入任务并执行 `setRollbackOnly()`，随后按 ID 查询为空，证明事务资源管理器生效；同时通过反射确认 `TaskServiceImpl#create` 带有 `@Transactional`。

最终页面回归：

```text
SSM_LOGIN_OK=True
SSM_LIST_OK=True
SSM_ADD=True COUNT1=2 COUNT2=1
SSM_UPDATE=True
SSM_STATUS=True
SSM_DELETE=True GONE=True
SSM_LOGOUT_STATUS=302 LOCATION=/taskmaster/login
```

## 五、遇到的问题与排查

| 现象 | 原因 | 处理 |
|---|---|---|
| 干净 WAR 没有 `jdbc.properties`，容器启动失败 | 真实密码不能提交 | 构建干净 WAR；本地验证时用测试 WAR 注入 `.runtime` 配置 |
| 热部署时出现 MySQL cleanup 线程警告 | Web 应用重新加载时旧驱动线程未及时停止 | 重新部署后等待上下文关闭，最终验证使用稳定实例 |
| 事务回滚后测试数据是否残留 | 需确认 Mapper 插入确实被事务管理器包裹 | 用 `TransactionTemplate` 插入后 rollback，再查询断言不存在 |
| 业务失败页状态码不稳定 | Controller 抛出的状态异常与统一异常处理器状态传播冲突 | Controller 直接设置响应状态并返回错误视图 |

## 六、结论

SSM 最终版本完成登录、任务列表、搜索筛选、新增、编辑、删除、开始/完成/重开，写操作通过 `@Transactional` 统一管理。三套配置分工明确：根容器管持久化与事务，MVC 子容器管控制器与视图，MyBatis 配置只管映射行为。

最终标签：`stage2-class01`、`stage2-class02`、`stage2-class03`、`stage2-class04`。
