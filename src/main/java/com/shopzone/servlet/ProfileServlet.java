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
 * Handles user profile viewing and editing.
 * GET: Display profile
 * POST: Update profile
 */
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireLogin(request, response)) return;

        User user = SessionUtil.getUser(request);
        // Refresh from DB
        User freshUser = userDAO.getUserById(user.getUid());
        if (freshUser != null) {
            request.setAttribute("profileUser", freshUser);
        }

        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireLogin(request, response)) return;

        User user = SessionUtil.getUser(request);
        String action = request.getParameter("action");

        if ("updateProfile".equals(action)) {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String mobile = request.getParameter("mobile");
            String address = request.getParameter("address");

            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("error", "Name is required.");
                doGet(request, response);
                return;
            }

            boolean updated = userDAO.updateProfile(user.getUid(),
                    name.trim(),
                    email != null ? email.trim() : "",
                    mobile != null ? mobile.trim() : "",
                    address != null ? address.trim() : "");

            if (updated) {
                // Update session
                User freshUser = userDAO.getUserById(user.getUid());
                SessionUtil.setUser(request, freshUser);
                request.setAttribute("success", "Profile updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update profile.");
            }

        } else if ("changePassword".equals(action)) {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmNewPassword");

            if (currentPassword == null || newPassword == null || confirmPassword == null) {
                request.setAttribute("error", "All password fields are required.");
                doGet(request, response);
                return;
            }

            // Verify current password
            User verified = userDAO.login(user.getUname(), currentPassword.trim());
            if (verified == null) {
                request.setAttribute("error", "Current password is incorrect.");
                doGet(request, response);
                return;
            }

            if (!newPassword.trim().equals(confirmPassword.trim())) {
                request.setAttribute("error", "New passwords do not match.");
                doGet(request, response);
                return;
            }

            if (newPassword.trim().length() < 4) {
                request.setAttribute("error", "New password must be at least 4 characters.");
                doGet(request, response);
                return;
            }

            boolean changed = userDAO.updatePassword(user.getUid(), newPassword.trim());
            if (changed) {
                request.setAttribute("success", "Password changed successfully!");
            } else {
                request.setAttribute("error", "Failed to change password.");
            }
        }

        doGet(request, response);
    }
}
