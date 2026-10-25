package com.taskmaster;

import com.taskmaster.entity.Task;
import com.taskmaster.mapper.TaskMapper;
import com.taskmaster.service.TaskService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Proxy;
import java.time.LocalDate;

/**
 * 不启动 Tomcat 的持久层/容器联调入口：
 * 加载 applicationContext.xml，验证 Mapper 动态代理、依赖注入和 MyBatis CRUD。
 */
public final class MyBatisSmokeTest {
    private MyBatisSmokeTest() {
    }

    public static void main(String[] args) {
        long createdId = 0;
        try (ClassPathXmlApplicationContext context =
                     new ClassPathXmlApplicationContext("applicationContext.xml")) {
            TaskService taskService = context.getBean("taskService", TaskService.class);
            TaskMapper taskMapper = context.getBean("taskMapper", TaskMapper.class);
            check(Proxy.isProxyClass(taskMapper.getClass()), "Mapper should be a JDK proxy");

            String title = "MyBatis 联调任务-" + System.currentTimeMillis();
            Task task = new Task();
            task.setUserId(1L);
            task.setTitle(title);
            task.setDescription("验证 Mapper XML 与中文");
            task.setStatus(0);
            task.setPriority(2);
            task.setDueDate(LocalDate.now().plusDays(1));

            createdId = taskService.create(task);
            check(createdId > 0, "insert should generate an id");

            Task saved = taskService.findById(createdId, 1L);
            check(saved != null && title.equals(saved.getTitle()), "insert/select should round-trip");
            check(!taskService.list(1L, "MyBatis 联调", 0).isEmpty(), "search should find the task");

            saved.setTitle(title + "-更新");
            saved.setStatus(1);
            check(taskService.update(saved), "update should affect one row");
            check("MyBatis 联调任务-".equals(saved.getTitle().substring(0, "MyBatis 联调任务-".length())),
                    "updated title should keep the test prefix");

            check(taskService.updateStatus(createdId, 1L, 2), "status update should affect one row");
            check(taskService.findById(createdId, 1L).getStatus() == 2, "status should be completed");

            long deletedId = createdId;
            check(taskService.delete(deletedId, 1L), "delete should affect one row");
            createdId = 0;
            check(taskService.findById(deletedId, 1L) == null, "deleted task should not be found");
            System.out.println("MYBATIS_SMOKE_OK");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (createdId > 0) {
                // 失败时尽量清理测试数据；下一次运行仍使用唯一标题。
                try (ClassPathXmlApplicationContext cleanup =
                             new ClassPathXmlApplicationContext("applicationContext.xml")) {
                    cleanup.getBean("taskService", TaskService.class).delete(createdId, 1L);
                } catch (Exception ignored) {
                    // 保留原始失败。
                }
            }
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
