package com.shopzone.servlet;

import com.shopzone.dao.UserDAO;
import com.shopzone.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles new user registration.
 * GET: Display registration form
 * POST: Process registration
 */
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/products"));
            return;
        }
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String address = request.getParameter("address");

        // Validate required fields
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            name == null || name.trim().isEmpty()) {
            request.setAttribute("error", "Username, password, and name are required.");
            preserveFormData(request, username, name, email, mobile, address);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        username = username.trim();
        password = password.trim();
        name = name.trim();

        // Validate password match
        if (confirmPassword == null || !password.equals(confirmPassword.trim())) {
            request.setAttribute("error", "Passwords do not match.");
            preserveFormData(request, username, name, email, mobile, address);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Validate password length
        if (password.length() < 4) {
            request.setAttribute("error", "Password must be at least 4 characters long.");
            preserveFormData(request, username, name, email, mobile, address);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Validate username length
        if (username.length() < 3) {
            request.setAttribute("error", "Username must be at least 3 characters long.");
            preserveFormData(request, username, name, email, mobile, address);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Check if username exists
        if (userDAO.usernameExists(username)) {
            request.setAttribute("error", "Username '" + username + "' is already taken.");
            preserveFormData(request, username, name, email, mobile, address);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Register user (enabled=false, must be approved by admin)
        int uid = userDAO.register(username, password, "user", false,
                                   name,
                                   email != null ? email.trim() : "",
                                   mobile != null ? mobile.trim() : "",
                                   address != null ? address.trim() : "");

        if (uid > 0) {
            request.setAttribute("success", "Registration successful! Your account will be activated after admin approval.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            preserveFormData(request, username, name, email, mobile, address);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    private void preserveFormData(HttpServletRequest request, String username, String name,
                                  String email, String mobile, String address) {
        request.setAttribute("username", username);
        request.setAttribute("name", name);
        request.setAttribute("email", email);
        request.setAttribute("mobile", mobile);
        request.setAttribute("address", address);
    }
}

