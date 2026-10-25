package com.taskmaster;

import com.taskmaster.entity.Task;
import com.taskmaster.mapper.TaskMapper;
import com.taskmaster.service.impl.TaskServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;
import java.time.LocalDate;

/**
 * 验证声明式事务配置：
 * 1. Service 写方法确实带 @Transactional；
 * 2. TransactionTemplate 回滚后，Mapper 插入的数据不可见。
 */
public final class TransactionSmokeTest {
    private TransactionSmokeTest() {
    }

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context =
                     new ClassPathXmlApplicationContext("applicationContext.xml")) {
            Method createMethod = TaskServiceImpl.class.getMethod("create", Task.class);
            check(createMethod.isAnnotationPresent(Transactional.class),
                    "TaskServiceImpl#create should be annotated with @Transactional");

            PlatformTransactionManager manager =
                    context.getBean("transactionManager", PlatformTransactionManager.class);
            TaskMapper mapper = context.getBean("taskMapper", TaskMapper.class);
            TransactionTemplate template = new TransactionTemplate(manager);
            String title = "事务回滚验证-" + System.currentTimeMillis();
            Long id = template.execute(status -> {
                Task task = new Task();
                task.setUserId(1L);
                task.setTitle(title);
                task.setDescription("回滚后不应残留");
                task.setStatus(0);
                task.setPriority(2);
                task.setDueDate(LocalDate.now().plusDays(1));
                mapper.insert(task);
                status.setRollbackOnly();
                return task.getId();
            });
            check(id != null && id > 0, "transaction should assign an id before rollback");
            check(mapper.selectById(id, 1L) == null, "rolled-back row must not be visible");
            System.out.println("TRANSACTION_SMOKE_OK");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
