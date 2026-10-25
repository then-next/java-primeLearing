package com.taskmaster.controller;

import com.taskmaster.entity.Task;
import com.taskmaster.entity.User;
import com.taskmaster.service.TaskService;
import com.taskmaster.util.TaskFormUtil;
import com.taskmaster.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * 原 TaskListServlet / TaskAddServlet / ... 统一由 DispatcherServlet 调度。
 * Controller 只负责请求参数和视图，查询/写入交给 Service。
 */
@Controller
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public String list(@SessionAttribute(name = WebUtil.SESSION_USER) User user,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "status", required = false) String rawStatus,
                       Model model) {
        String normalizedKeyword = WebUtil.trimToNull(keyword);
        if (normalizedKeyword != null && normalizedKeyword.length() > 100) {
            normalizedKeyword = normalizedKeyword.substring(0, 100);
        }
        Integer status = parseStatus(rawStatus);
        List<Task> tasks = taskService.list(user.getId(), normalizedKeyword, status);
        model.addAttribute("tasks", tasks);
        model.addAttribute("totalCount", taskService.count(user.getId(), null));
        model.addAttribute("todoCount", taskService.count(user.getId(), 0));
        model.addAttribute("doingCount", taskService.count(user.getId(), 1));
        model.addAttribute("doneCount", taskService.count(user.getId(), 2));
        model.addAttribute("overdueCount", taskService.countOverdue(user.getId()));
        model.addAttribute("keyword", normalizedKeyword == null ? "" : normalizedKeyword);
        model.addAttribute("statusFilter", status);
        return "list";
    }

    @GetMapping("/tasks/add")
    public String addForm(Model model) {
        Task task = new Task();
        task.setStatus(0);
        task.setPriority(2);
        model.addAttribute("task", task);
        model.addAttribute("formMode", "add");
        return "add";
    }

    @PostMapping("/tasks/add")
    public String add(@SessionAttribute(name = WebUtil.SESSION_USER) User user,
                      HttpServletRequest request,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        Task task = new Task();
        task.setUserId(user.getId());
        Map<String, String> errors = TaskFormUtil.bind(request, task);
        if (!errors.isEmpty()) {
            model.addAttribute("task", task);
            model.addAttribute("errors", errors);
            model.addAttribute("formMode", "add");
            return "add";
        }
        taskService.create(task);
        redirectAttributes.addFlashAttribute("flashType", "success");
        redirectAttributes.addFlashAttribute("flashMessage", "任务“" + task.getTitle() + "”已创建");
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/edit")
    public String editForm(@SessionAttribute(name = WebUtil.SESSION_USER) User user,
                           @RequestParam(value = "id", required = false) String id,
                           Model model,
                           HttpServletResponse response) {
        Long taskId = WebUtil.parseLong(id);
        if (taskId == null || taskId <= 0) {
            return renderError(response, model, 400, "任务 ID 不合法");
        }
        Task task = taskService.findById(taskId, user.getId());
        if (task == null) {
            return renderError(response, model, 404, "任务不存在");
        }
        model.addAttribute("task", task);
        model.addAttribute("formMode", "edit");
        return "edit";
    }

    @PostMapping("/tasks/update")
    public String update(@SessionAttribute(name = WebUtil.SESSION_USER) User user,
                         @RequestParam(value = "id", required = false) String id,
                         HttpServletRequest request,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         HttpServletResponse response) {
        Long taskId = WebUtil.parseLong(id);
        if (taskId == null || taskId <= 0) {
            return renderError(response, model, 400, "任务 ID 不合法");
        }
        Task task = taskService.findById(taskId, user.getId());
        if (task == null) {
            return renderError(response, model, 404, "任务不存在");
        }
        Map<String, String> errors = TaskFormUtil.bind(request, task);
        if (!errors.isEmpty()) {
            model.addAttribute("task", task);
            model.addAttribute("errors", errors);
            model.addAttribute("formMode", "edit");
            return "edit";
        }
        if (!taskService.update(task)) {
            return renderError(response, model, 404, "任务不存在或无权修改");
        }
        redirectAttributes.addFlashAttribute("flashType", "success");
        redirectAttributes.addFlashAttribute("flashMessage", "任务“" + task.getTitle() + "”已更新");
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/delete")
    public String delete(@SessionAttribute(name = WebUtil.SESSION_USER) User user,
                         @RequestParam(value = "id", required = false) String id,
                         RedirectAttributes redirectAttributes,
                         Model model,
                         HttpServletResponse response) {
        Long taskId = WebUtil.parseLong(id);
        if (taskId == null || taskId <= 0) {
            return renderError(response, model, 400, "任务 ID 不合法");
        }
        if (!taskService.delete(taskId, user.getId())) {
            return renderError(response, model, 404, "任务不存在或无权删除");
        }
        redirectAttributes.addFlashAttribute("flashType", "success");
        redirectAttributes.addFlashAttribute("flashMessage", "任务已删除");
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/status")
    public String status(@SessionAttribute(name = WebUtil.SESSION_USER) User user,
                         @RequestParam(value = "id", required = false) String id,
                         @RequestParam(value = "status", required = false) String rawStatus,
                         RedirectAttributes redirectAttributes,
                         Model model,
                         HttpServletResponse response) {
        Long taskId = WebUtil.parseLong(id);
        if (taskId == null || taskId <= 0) {
            return renderError(response, model, 400, "任务 ID 不合法");
        }
        Integer status = parseStatus(rawStatus);
        if (status == null) {
            return renderError(response, model, 400, "任务状态不合法");
        }
        if (!taskService.updateStatus(taskId, user.getId(), status)) {
            return renderError(response, model, 404, "任务不存在或无权修改");
        }
        String[] messages = {"任务已移回待办", "任务已开始进行", "任务已完成"};
        redirectAttributes.addFlashAttribute("flashType", "success");
        redirectAttributes.addFlashAttribute("flashMessage", messages[status]);
        return "redirect:/tasks";
    }

    private static Integer parseStatus(String rawStatus) {
        Integer status = WebUtil.parseInteger(rawStatus);
        return status != null && status >= 0 && status <= 2 ? status : null;
    }

    private static String renderError(HttpServletResponse response, Model model,
                                      int statusCode, String message) {
        response.setStatus(statusCode);
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("message", message);
        return "error";
    }
}
