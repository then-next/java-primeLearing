package com.taskmaster.listener;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 第一课：由 Listener 管理 Spring Core 容器生命周期。
 * 第二课会改用 Spring Web 的 ContextLoaderListener + DispatcherServlet。
 */
public class SpringContextListener implements ServletContextListener {
    public static final String CONTEXT_ATTRIBUTE = "taskmasterSpringContext";

    private ClassPathXmlApplicationContext context;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        ServletContext servletContext = event.getServletContext();
        servletContext.setAttribute(CONTEXT_ATTRIBUTE, context);
        servletContext.log("TaskMaster Spring Core container started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (context != null) {
            context.close();
        }
    }
}
