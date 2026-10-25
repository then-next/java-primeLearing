package com.taskmaster.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 页面输出与 Web 层小工具，集中处理 HTML 转义、CSRF 令牌和一次性提示。
 */
public final class WebUtil {
    public static final String SESSION_USER = "loginUser";
    public static final String SESSION_CSRF = "csrfToken";

    private static final SecureRandom RANDOM = new SecureRandom();

    private WebUtil() {
    }

    public static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String ensureCsrfToken(HttpSession session) {
        Object existing = session.getAttribute(SESSION_CSRF);
        if (existing instanceof String && !((String) existing).isEmpty()) {
            return (String) existing;
        }
        String token = newToken();
        session.setAttribute(SESSION_CSRF, token);
        return token;
    }

    public static String csrfToken(HttpServletRequest request) {
        return ensureCsrfToken(request.getSession());
    }

    public static String rotateCsrfToken(HttpSession session) {
        String token = newToken();
        session.setAttribute(SESSION_CSRF, token);
        return token;
    }

    public static boolean verifyCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(SESSION_CSRF);
        String actual = request.getParameter("_csrf");
        if (!(expected instanceof String) || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                ((String) expected).getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    public static void flash(HttpServletRequest request, String type, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }

    public static void moveFlashToRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object message = session.getAttribute("flashMessage");
        Object type = session.getAttribute("flashType");
        if (message != null) {
            request.setAttribute("flashMessage", message);
            request.setAttribute("flashType", type == null ? "success" : type);
            session.removeAttribute("flashMessage");
            session.removeAttribute("flashType");
        }
    }

    public static Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String newToken() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
