package com.taskmaster.mapper;

import com.taskmaster.entity.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis Mapper 接口：没有实现类。
 * MyBatis 通过 JDK 动态代理生成代理对象，代理在调用时按 namespace + method
 * 找到 XML 中的 SQL，并完成参数绑定和 ResultSet 映射。
 */
public interface TaskMapper {
    List<Task> selectByUser(@Param("userId") long userId,
                            @Param("keyword") String keyword,
                            @Param("status") Integer status);

    Task selectById(@Param("id") long id, @Param("userId") long userId);

    int insert(Task task);

    int update(Task task);

    int delete(@Param("id") long id, @Param("userId") long userId);

    int updateStatus(@Param("id") long id,
                     @Param("userId") long userId,
                     @Param("status") int status);

    int countByUser(@Param("userId") long userId, @Param("status") Integer status);

    int countOverdue(@Param("userId") long userId);
}
