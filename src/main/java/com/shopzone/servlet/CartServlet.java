package com.shopzone.servlet;

import com.shopzone.dao.OrderDAO;
import com.shopzone.model.Product;
import com.shopzone.model.User;
import com.shopzone.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Handles shopping cart operations.
 * GET: Display cart contents
 * POST: Add/remove/update cart items, or checkout
 */
public class CartServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireLogin(request, response)) return;

        User user = SessionUtil.getUser(request);
        List<Product> cartItems = orderDAO.getCartItems(user.getUid());

        double cartTotal = 0;
        for (Product p : cartItems) {
            cartTotal += p.getPrice() * p.getCartQuantity();
        }

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", cartTotal);
        request.setAttribute("cartCount", cartItems.size());

        request.getRequestDispatcher("/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireLogin(request, response)) return;

        User user = SessionUtil.getUser(request);
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/cart"));
            return;
        }

        switch (action) {
            case "add":
                handleAdd(request, response, user);
                break;
            case "remove":
                handleRemove(request, response, user);
                break;
            case "update":
                handleUpdate(request, response, user);
                break;
            case "checkout":
                handleCheckout(request, response, user);
                break;
            default:
                response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/cart"));
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity = 1;
            String qtyParam = request.getParameter("quantity");
            if (qtyParam != null && !qtyParam.isEmpty()) {
                quantity = Integer.parseInt(qtyParam);
            }
            orderDAO.addToCart(user.getUid(), productId, quantity);
        } catch (NumberFormatException e) {
            // Invalid input, ignore
        }

        // Redirect back to referring page or cart
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("/cart")) {
            response.sendRedirect(response.encodeRedirectURL(referer));
        } else {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/cart"));
        }
    }

    private void handleRemove(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            orderDAO.removeFromCart(user.getUid(), productId);
        } catch (NumberFormatException e) {
            // Invalid input, ignore
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/cart"));
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            orderDAO.updateCartQuantity(user.getUid(), productId, quantity);
        } catch (NumberFormatException e) {
            // Invalid input, ignore
        }
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/cart"));
    }

    private void handleCheckout(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException, ServletException {
        String address = request.getParameter("address");
        if (address == null || address.trim().isEmpty()) {
            // Use user's default address
            address = user.getAddress();
        }
        if (address == null || address.trim().isEmpty()) {
            request.setAttribute("error", "Please provide a delivery address.");
            doGet(request, response);
            return;
        }

        boolean success = orderDAO.placeOrder(user.getUid(), address.trim());
        if (success) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/orders?msg=placed"));
        } else {
            request.setAttribute("error", "Failed to place order. Your cart may be empty.");
            doGet(request, response);
        }
    }
}

