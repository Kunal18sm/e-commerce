package com.shopzone.servlet;

import com.shopzone.dao.OrderDAO;
import com.shopzone.dao.ProductDAO;
import com.shopzone.dao.UserDAO;
import com.shopzone.util.ProductImageUtil;
import com.shopzone.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Admin servlet handling all admin panel operations.
 * URL pattern: /admin/*
 *
 * Paths:
 *   /admin/dashboard        - Dashboard with stats
 *   /admin/products         - Manage products
 *   /admin/users            - Manage users
 *   /admin/moderators       - Manage moderators
 *   /admin/orders           - Manage orders
 */
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
public class AdminServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, "admin")) return;

        String path = request.getPathInfo();
        if (path == null) path = "/dashboard";
        int sessionParamIdx = path.indexOf(';');
        if (sessionParamIdx >= 0) {
            path = path.substring(0, sessionParamIdx);
        }

        switch (path) {
            case "/dashboard":
                showDashboard(request, response);
                break;
            case "/products":
                showProducts(request, response);
                break;
            case "/users":
                showUsers(request, response);
                break;
            case "/moderators":
                showModerators(request, response);
                break;
            case "/orders":
                showOrders(request, response);
                break;
            default:
                response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/dashboard"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, "admin")) return;

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/dashboard"));
            return;
        }

        switch (action) {
            case "addProduct":
                addProduct(request, response);
                break;
            case "deleteProduct":
                deleteProduct(request, response);
                break;
            case "addUser":
                addUser(request, response, "user");
                break;
            case "deleteUser":
                deleteUser(request, response, "users");
                break;
            case "toggleUser":
                toggleUser(request, response, "users");
                break;
            case "addModerator":
                addUser(request, response, "mod");
                break;
            case "deleteModerator":
                deleteUser(request, response, "moderators");
                break;
            case "toggleModerator":
                toggleUser(request, response, "moderators");
                break;
            case "updateOrderStatus":
                updateOrderStatus(request, response);
                break;
            case "deleteOrder":
                deleteOrder(request, response);
                break;
            default:
                response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/dashboard"));
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("totalUsers", userDAO.getCountByType("user"));
        request.setAttribute("totalMods", userDAO.getCountByType("mod"));
        request.setAttribute("totalProducts", productDAO.getProductCount());
        request.setAttribute("totalOrders", orderDAO.getOrderCount());
        request.setAttribute("totalRevenue", orderDAO.getTotalRevenue());
        request.setAttribute("recentOrders", orderDAO.getAllOrders());

        request.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(request, response);
    }

    private void showProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("q");
        if (search != null && !search.trim().isEmpty()) {
            request.setAttribute("products", productDAO.searchProducts(search.trim()));
            request.setAttribute("searchQuery", search.trim());
        } else {
            request.setAttribute("products", productDAO.getAllProducts(null));
        }
        request.setAttribute("categories", productDAO.getCategories());
        request.getRequestDispatcher("/WEB-INF/admin/manage-products.jsp").forward(request, response);
    }

    private void showUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("q");
        if (search != null && !search.trim().isEmpty()) {
            request.setAttribute("users", userDAO.searchUsers(search.trim(), "user"));
            request.setAttribute("searchQuery", search.trim());
        } else {
            request.setAttribute("users", userDAO.getUsersByType("user"));
        }
        request.getRequestDispatcher("/WEB-INF/admin/manage-users.jsp").forward(request, response);
    }

    private void showModerators(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("q");
        if (search != null && !search.trim().isEmpty()) {
            request.setAttribute("moderators", userDAO.searchUsers(search.trim(), "mod"));
            request.setAttribute("searchQuery", search.trim());
        } else {
            request.setAttribute("moderators", userDAO.getUsersByType("mod"));
        }
        request.getRequestDispatcher("/WEB-INF/admin/manage-moderators.jsp").forward(request, response);
    }

    private void showOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("orders", orderDAO.getAllOrders());
        request.getRequestDispatcher("/WEB-INF/admin/manage-orders.jsp").forward(request, response);
    }

    private void addProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");
        String description = request.getParameter("description");
        String category = request.getParameter("category");

        if (name == null || name.trim().isEmpty() || priceStr == null) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/products?error=invalid"));
            return;
        }

        try {
            double price = Double.parseDouble(priceStr.trim());
            String image = ProductImageUtil.resolveImage(request, "image", "imageFile");
            if (category == null || category.trim().isEmpty()) category = "General";

            productDAO.addProduct(name.trim(), price, description != null ? description.trim() : "",
                                  image.trim(), category.trim());
        } catch (ServletException e) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/products?error=invalidimage"));
            return;
        } catch (NumberFormatException e) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/products?error=invalid"));
            return;
        }

        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/products?msg=added"));
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            productDAO.deleteProduct(id);
        } catch (NumberFormatException e) {
            // Invalid ID
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/products?msg=deleted"));
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response, String type) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String address = request.getParameter("address");

        if (username != null && password != null && name != null) {
            boolean enabled = "true".equals(request.getParameter("enabled"));
            userDAO.register(username.trim(), password.trim(), type, enabled,
                           name.trim(),
                           email != null ? email.trim() : "",
                           mobile != null ? mobile.trim() : "",
                           address != null ? address.trim() : "");
        }

        String redirect = "mod".equals(type) ? "/admin/moderators" : "/admin/users";
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + redirect + "?msg=added"));
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response, String page) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            userDAO.deleteUser(id);
        } catch (NumberFormatException e) {
            // Invalid ID
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/" + page + "?msg=deleted"));
    }

    private void toggleUser(HttpServletRequest request, HttpServletResponse response, String page) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            userDAO.toggleEnabled(id);
        } catch (NumberFormatException e) {
            // Invalid ID
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/" + page + "?msg=updated"));
    }

    private void updateOrderStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status");
            if (status != null) orderDAO.updateOrderStatus(id, status);
        } catch (NumberFormatException e) {
            // Invalid ID
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/orders?msg=updated"));
    }

    private void deleteOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            orderDAO.deleteOrder(id);
        } catch (NumberFormatException e) {
            // Invalid ID
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/orders?msg=deleted"));
    }
}
