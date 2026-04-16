package com.shopzone.servlet;

import com.shopzone.dao.ProductDAO;
import com.shopzone.model.Product;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Handles product browsing - listing, search, and detail view.
 * GET /products          - List all products
 * GET /products?cat=X    - Filter by category
 * GET /products?q=X      - Search products
 * GET /products?id=X     - Product detail
 */
public class ProductServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productId = request.getParameter("id");

        // Product detail view
        if (productId != null && !productId.isEmpty()) {
            try {
                Product product = productDAO.getProductById(Integer.parseInt(productId));
                if (product != null) {
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/product-detail.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid ID, fall through to listing
            }
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/products"));
            return;
        }

        // Search
        String searchQuery = request.getParameter("q");
        String category = request.getParameter("cat");

        List<Product> products;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            products = productDAO.searchProducts(searchQuery.trim());
            request.setAttribute("searchQuery", searchQuery.trim());
        } else {
            products = productDAO.getAllProducts(category);
        }

        request.setAttribute("products", products);
        request.setAttribute("categories", productDAO.getCategories());
        request.setAttribute("selectedCategory", category);

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}

