package com.shopzone.dao;

import com.shopzone.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product operations.
 */
public class ProductDAO {

    /**
     * Add a new product.
     * Returns the generated product ID, or -1 on failure.
     */
    public int addProduct(String name, double price, String description, String image, String category) {
        String sql = "INSERT INTO products (name, price, description, image, category) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, description);
            ps.setString(4, image);
            ps.setString(5, category);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return -1;
    }

    /**
     * Get product by ID.
     */
    public Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            rs = ps.executeQuery();
            if (rs.next()) return mapProduct(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return null;
    }

    /**
     * Get all products, optionally filtered by category.
     */
    public List<Product> getAllProducts(String category) {
        List<Product> products = new ArrayList<>();
        String sql;
        if (category != null && !category.isEmpty() && !category.equals("All")) {
            sql = "SELECT * FROM products WHERE category = ? ORDER BY created_at DESC";
        } else {
            sql = "SELECT * FROM products ORDER BY created_at DESC";
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            if (category != null && !category.isEmpty() && !category.equals("All")) {
                ps.setString(1, category);
            }
            rs = ps.executeQuery();
            while (rs.next()) products.add(mapProduct(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return products;
    }

    /**
     * Search products by name or description.
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ? OR category LIKE ? ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            rs = ps.executeQuery();
            while (rs.next()) products.add(mapProduct(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return products;
    }

    /**
     * Delete product by ID.
     */
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Get all distinct categories.
     */
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM products ORDER BY category";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) categories.add(rs.getString("category"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return categories;
    }

    /**
     * Get total product count.
     */
    public int getProductCount() {
        String sql = "SELECT COUNT(*) FROM products";
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
     * Map ResultSet row to Product object.
     */
    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setDescription(rs.getString("description"));
        p.setImage(rs.getString("image"));
        p.setCategory(rs.getString("category"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
