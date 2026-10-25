package com.taskmaster.service;

/**
 * Service 层统一运行异常。控制器不再直接依赖 SQLException。
 */
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
