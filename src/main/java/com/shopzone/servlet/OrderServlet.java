package com.shopzone.servlet;

import com.shopzone.dao.OrderDAO;
import com.shopzone.model.Order;
import com.shopzone.model.User;
import com.shopzone.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Handles order viewing for regular users.
 * GET: Display user's order history
 */
public class OrderServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireLogin(request, response)) return;

        User user = SessionUtil.getUser(request);
        List<Order> orders = orderDAO.getOrdersByUser(user.getUid());

        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/orders.jsp").forward(request, response);
    }
}
