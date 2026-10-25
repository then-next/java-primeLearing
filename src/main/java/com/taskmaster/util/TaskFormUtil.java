package com.taskmaster.util;

import com.taskmaster.entity.Task;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 新增与编辑共用的表单绑定/校验，避免在两个 Servlet 中复制同一套规则。
 */
public final class TaskFormUtil {
    private TaskFormUtil() {
    }

    public static Map<String, String> bind(HttpServletRequest request, Task task) {
        Map<String, String> errors = new LinkedHashMap<>();

        String title = WebUtil.trimToNull(request.getParameter("title"));
        if (title == null) {
            errors.put("title", "任务标题不能为空");
        } else if (title.length() > 100) {
            errors.put("title", "任务标题不能超过 100 个字符");
        }
        task.setTitle(title == null ? "" : title);

        String description = WebUtil.trimToNull(request.getParameter("description"));
        if (description != null && description.length() > 1000) {
            errors.put("description", "任务描述不能超过 1000 个字符");
        }
        task.setDescription(description);

        Integer statusValue = WebUtil.parseInteger(request.getParameter("status"));
        int status = statusValue == null ? 0 : statusValue;
        if (status < 0 || status > 2) {
            errors.put("status", "任务状态不合法");
            status = 0;
        }
        task.setStatus(status);

        Integer priorityValue = WebUtil.parseInteger(request.getParameter("priority"));
        int priority = priorityValue == null ? 2 : priorityValue;
        if (priority < 1 || priority > 3) {
            errors.put("priority", "优先级不合法");
            priority = 2;
        }
        task.setPriority(priority);

        String dueDateValue = WebUtil.trimToNull(request.getParameter("dueDate"));
        if (dueDateValue == null) {
            task.setDueDate(null);
        } else {
            try {
                task.setDueDate(LocalDate.parse(dueDateValue));
            } catch (DateTimeParseException e) {
                errors.put("dueDate", "截止日期格式应为 yyyy-MM-dd");
                task.setDueDate(null);
            }
        }
        return errors;
    }
}
