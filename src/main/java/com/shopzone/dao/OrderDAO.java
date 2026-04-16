package com.shopzone.dao;

import com.shopzone.model.Order;
import com.shopzone.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Order and Cart operations.
 */
public class OrderDAO {

    // ==================== CART OPERATIONS ====================

    /**
     * Add item to cart. If already in cart, increment quantity.
     */
    public boolean addToCart(int userId, int productId, int quantity) {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setInt(4, quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Update cart item quantity.
     */
    public boolean updateCartQuantity(int userId, int productId, int quantity) {
        if (quantity <= 0) return removeFromCart(userId, productId);
        String sql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Remove item from cart.
     */
    public boolean removeFromCart(int userId, int productId) {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Get all cart items for a user with product details.
     */
    public List<Product> getCartItems(int userId) {
        List<Product> items = new ArrayList<>();
        String sql = "SELECT p.*, c.quantity AS cart_qty FROM cart c " +
                     "JOIN products p ON c.product_id = p.product_id " +
                     "WHERE c.user_id = ? ORDER BY c.cart_id";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setDescription(rs.getString("description"));
                p.setImage(rs.getString("image"));
                p.setCategory(rs.getString("category"));
                p.setCartQuantity(rs.getInt("cart_qty"));
                items.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return items;
    }

    /**
     * Get cart item count for a user.
     */
    public int getCartCount(int userId) {
        String sql = "SELECT COUNT(*) FROM cart WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return 0;
    }

    /**
     * Clear entire cart for a user.
     */
    public boolean clearCart(int userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    // ==================== ORDER OPERATIONS ====================

    /**
     * Place order from cart items (checkout).
     * Moves all cart items to orders table and clears the cart.
     */
    public boolean placeOrder(int userId, String address) {
        Connection conn = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCart = null;
        PreparedStatement psClear = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Get cart items
            String cartSql = "SELECT product_id, quantity FROM cart WHERE user_id = ?";
            psCart = conn.prepareStatement(cartSql);
            psCart.setInt(1, userId);
            rs = psCart.executeQuery();

            // Insert each cart item as an order
            String orderSql = "INSERT INTO orders (user_id, product_id, quantity, address, status) VALUES (?, ?, ?, ?, 'Pending')";
            psInsert = conn.prepareStatement(orderSql);

            boolean hasItems = false;
            while (rs.next()) {
                hasItems = true;
                psInsert.setInt(1, userId);
                psInsert.setInt(2, rs.getInt("product_id"));
                psInsert.setInt(3, rs.getInt("quantity"));
                psInsert.setString(4, address);
                psInsert.addBatch();
            }

            if (!hasItems) {
                conn.rollback();
                return false;
            }

            psInsert.executeBatch();

            // Clear cart
            String clearSql = "DELETE FROM cart WHERE user_id = ?";
            psClear = conn.prepareStatement(clearSql);
            psClear.setInt(1, userId);
            psClear.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, psClear, psInsert, psCart, conn);
        }
        return false;
    }

    /**
     * Get orders for a specific user.
     */
    public List<Order> getOrdersByUser(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, p.name AS product_name, p.price AS product_price, p.image AS product_image, " +
                     "d.name AS user_name " +
                     "FROM orders o " +
                     "JOIN products p ON o.product_id = p.product_id " +
                     "LEFT JOIN user_details d ON o.user_id = d.uid " +
                     "WHERE o.user_id = ? ORDER BY o.order_date DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) orders.add(mapOrder(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return orders;
    }

    /**
     * Get all orders (for admin/moderator).
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, p.name AS product_name, p.price AS product_price, p.image AS product_image, " +
                     "d.name AS user_name " +
                     "FROM orders o " +
                     "JOIN products p ON o.product_id = p.product_id " +
                     "LEFT JOIN user_details d ON o.user_id = d.uid " +
                     "ORDER BY o.order_date DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) orders.add(mapOrder(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return orders;
    }

    /**
     * Update order status.
     */
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Delete an order.
     */
    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Get total order count.
     */
    public int getOrderCount() {
        String sql = "SELECT COUNT(*) FROM orders";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return 0;
    }

    /**
     * Get total revenue.
     */
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(p.price * o.quantity), 0) AS revenue " +
                     "FROM orders o JOIN products p ON o.product_id = p.product_id " +
                     "WHERE o.status != 'Cancelled'";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("revenue");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return 0;
    }

    /**
     * Map ResultSet row to Order object.
     */
    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setProductId(rs.getInt("product_id"));
        o.setQuantity(rs.getInt("quantity"));
        o.setAddress(rs.getString("address"));
        o.setStatus(rs.getString("status"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setProductName(rs.getString("product_name"));
        o.setProductPrice(rs.getDouble("product_price"));
        o.setProductImage(rs.getString("product_image"));
        try {
            o.setUserName(rs.getString("user_name"));
        } catch (SQLException e) {
            // user_name might not be in all queries
        }
        return o;
    }
}
