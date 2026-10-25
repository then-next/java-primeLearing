# 实验报告三：MyBatis Mapper 与动态代理

## 一、实验目的

用 Mapper 接口 + XML 替换 JDBC 六步法，理解“接口没有实现类也能运行”的 JDK 动态代理机制，并把 SQL 从 Java 实现类迁移到 XML。

## 二、实验环境

| 项目 | 环境 |
|---|---|
| MyBatis | 3.5.14 |
| MyBatis-Spring | 3.0.3 |
| Spring | 6.1.1 |
| 数据库 | MySQL 8.0.45，`taskmaster` |
| 构建 | Maven 3.9.16，本地离线构建 |

## 三、关键代码

Mapper 接口没有实现类：

```java
public interface TaskMapper {
    List<Task> selectByUser(@Param("userId") long userId,
                            @Param("keyword") String keyword,
                            @Param("status") Integer status);
    int insert(Task task);
    int update(Task task);
    int delete(@Param("id") long id, @Param("userId") long userId);
}
```

XML 通过 namespace 与接口方法绑定：

```xml
<mapper namespace="com.taskmaster.mapper.TaskMapper">
    <select id="selectByUser" resultMap="TaskResultMap">
        SELECT <include refid="TaskColumns"/>
        FROM t_task WHERE user_id = #{userId}
        <if test="keyword != null and keyword != ''">
            AND title LIKE CONCAT('%', #{keyword}, '%')
        </if>
    </select>
</mapper>
```

`applicationContext.xml` 扫描 Mapper 接口并创建代理：

```xml
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="com.taskmaster.mapper"/>
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
</bean>
```

## 四、运行结果

本地独立测试通过：

```text
MYBATIS_SMOKE_OK
```

测试验证了：

- `TaskMapper` 的类名由 JDK 动态代理生成；
- Spring 能创建 `TaskServiceImpl` 并注入 `TaskMapper` 代理；
- 插入、按 ID 查询、关键词查询、修改、状态变更、删除全部成功；
- 中文标题和描述正常写入、读取。

浏览器页面回归也通过：

```text
MYBATIS_LOGIN_OK=True
MYBATIS_ADD=True
COUNT1=2
COUNT2=1
MYBATIS_UPDATE=True
MYBATIS_STATUS=True
MYBATIS_DELETE=True
```

## 五、遇到的问题与排查

| 现象 | 原因 | 处理 |
|---|---|---|
| `mvn test` 离线失败 | 本地缺少 `surefire-junit3` 测试运行器 | 不下载插件，改成可执行 `main` 测试，用 `java -cp` 运行 |
| Mapper 接口无实现类是否会报错 | 误把 Java 接口当成需要手工实现的 DAO | 通过 `Proxy.isProxyClass` 验证 MyBatis 代理确实生成 |
| 首次部署后请求 404 | Tomcat 自动部署尚未完成就发起请求 | 等待部署日志完成，再重试请求 |
| 配置文件可能泄露密码 | 本地 `jdbc.properties` 包含连接密码 | 只提交 `jdbc.properties.example`，真实文件放在 `.runtime` 并加入忽略规则 |

## 六、结论

MyBatis 把 JDBC 连接、参数绑定、结果映射和资源关闭从 DAO 实现中移除。Java 只保留 Mapper 接口，SQL 集中到 XML。该课标签为 `stage2-class03`。
