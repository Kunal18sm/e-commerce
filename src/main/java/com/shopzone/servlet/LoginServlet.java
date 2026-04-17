package com.shopzone.servlet;

import com.shopzone.dao.UserDAO;
import com.shopzone.model.User;
import com.shopzone.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles user login authentication.
 * GET: Display login page
 * POST: Authenticate credentials and redirect by role
 */
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already logged in, redirect to appropriate dashboard
        if (SessionUtil.isLoggedIn(request)) {
            redirectByRole(request, response);
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate input
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter both username and password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        username = username.trim();
        password = password.trim();

        // Authenticate
        User user = userDAO.login(username, password);

        if (user == null) {
            String loginError = userDAO.consumeLastLoginError();
            if (UserDAO.LOGIN_ERROR_DB.equals(loginError)) {
                request.setAttribute("error",
                        "Database connection failed. Please check .env DB settings and ensure MySQL is reachable.");
            } else {
                request.setAttribute("error", "Invalid username or password.");
            }
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Check if account is enabled
        if (!user.isEnabled()) {
            request.setAttribute("error", "Your account is disabled. Please contact the administrator.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Set session and redirect
        SessionUtil.setUser(request, user);
        redirectByRole(request, response);
    }

    /**
     * Redirect user to their role-specific dashboard.
     */
    private void redirectByRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = SessionUtil.getUser(request);
        if (user == null) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/login"));
            return;
        }

        String ctx = request.getContextPath();
        switch (user.getUtype()) {
            case "admin":
                response.sendRedirect(response.encodeRedirectURL(ctx + "/admin/dashboard"));
                break;
            case "mod":
                response.sendRedirect(response.encodeRedirectURL(ctx + "/mod/dashboard"));
                break;
            default:
                response.sendRedirect(response.encodeRedirectURL(ctx + "/products"));
                break;
        }
    }
}

