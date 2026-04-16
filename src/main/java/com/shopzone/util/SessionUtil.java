package com.shopzone.util;

import com.shopzone.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Session management utility for role-based access control.
 */
public class SessionUtil {

    private static final String USER_KEY = "loggedInUser";

    /**
     * Store user in session after successful login.
     */
    public static void setUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_KEY, user);
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
    }

    /**
     * Get the logged-in user from session.
     */
    public static User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute(USER_KEY);
    }

    /**
     * Check if user is logged in.
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getUser(request) != null;
    }

    /**
     * Check if logged-in user has the specified role.
     */
    public static boolean hasRole(HttpServletRequest request, String role) {
        User user = getUser(request);
        return user != null && role.equals(user.getUtype());
    }

    /**
     * Require user to be logged in. Redirects to login page if not.
     * Returns true if user IS logged in, false if redirect was sent.
     */
    public static boolean requireLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/login"));
            return false;
        }
        return true;
    }

    /**
     * Require user to have a specific role. Redirects to login if not authorized.
     * Returns true if user has the role.
     */
    public static boolean requireRole(HttpServletRequest request, HttpServletResponse response, String... roles) throws IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/login"));
            return false;
        }
        User user = getUser(request);
        for (String role : roles) {
            if (role.equals(user.getUtype())) return true;
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/login?error=unauthorized"));
        return false;
    }

    /**
     * Invalidate the current session (logout).
     */
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}

