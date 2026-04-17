package com.shopzone.dao;

import com.shopzone.model.User;
import com.shopzone.util.SHA1Util;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations.
 */
public class UserDAO {
    private final ThreadLocal<String> lastLoginError = new ThreadLocal<String>();

    public static final String LOGIN_ERROR_DB = "db";

    /**
     * Authenticate user by username and password.
     * Returns User object if credentials are valid, null otherwise.
     */
    public User login(String username, String password) {
        clearLastLoginError();
        String sql = "SELECT l.uid, l.uname, l.utype, l.enabled, l.created_at, " +
                     "d.name, d.email, d.mobile, d.address " +
                     "FROM login l LEFT JOIN user_details d ON l.uid = d.uid " +
                     "WHERE l.uname = ? AND l.upass = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, SHA1Util.hash(password));
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            lastLoginError.set(LOGIN_ERROR_DB);
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return null;
    }

    public String consumeLastLoginError() {
        String error = lastLoginError.get();
        lastLoginError.remove();
        return error;
    }

    private void clearLastLoginError() {
        lastLoginError.remove();
    }

    /**
     * Register a new user.
     * Returns the generated user ID, or -1 on failure.
     */
    public int register(String username, String password, String utype, boolean enabled,
                        String name, String email, String mobile, String address) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert into login table
            String sql1 = "INSERT INTO login (uname, upass, utype, enabled) VALUES (?, ?, ?, ?)";
            ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, username);
            ps1.setString(2, SHA1Util.hash(password));
            ps1.setString(3, utype);
            ps1.setBoolean(4, enabled);
            ps1.executeUpdate();

            rs = ps1.getGeneratedKeys();
            if (!rs.next()) {
                conn.rollback();
                return -1;
            }
            int uid = rs.getInt(1);

            // Insert into user_details table
            String sql2 = "INSERT INTO user_details (uid, name, email, mobile, address) VALUES (?, ?, ?, ?, ?)";
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, uid);
            ps2.setString(2, name);
            ps2.setString(3, email);
            ps2.setString(4, mobile);
            ps2.setString(5, address);
            ps2.executeUpdate();

            conn.commit();
            return uid;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return -1;
        } finally {
            DBConnection.close(rs, ps2, ps1, conn);
        }
    }

    /**
     * Get user by ID.
     */
    public User getUserById(int uid) {
        String sql = "SELECT l.uid, l.uname, l.utype, l.enabled, l.created_at, " +
                     "d.name, d.email, d.mobile, d.address " +
                     "FROM login l LEFT JOIN user_details d ON l.uid = d.uid " +
                     "WHERE l.uid = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return null;
    }

    /**
     * Get all users of a specific type.
     */
    public List<User> getUsersByType(String utype) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT l.uid, l.uname, l.utype, l.enabled, l.created_at, " +
                     "d.name, d.email, d.mobile, d.address " +
                     "FROM login l LEFT JOIN user_details d ON l.uid = d.uid " +
                     "WHERE l.utype = ? ORDER BY l.uid DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, utype);
            rs = ps.executeQuery();
            while (rs.next()) users.add(mapUser(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return users;
    }

    /**
     * Get all users (all types).
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT l.uid, l.uname, l.utype, l.enabled, l.created_at, " +
                     "d.name, d.email, d.mobile, d.address " +
                     "FROM login l LEFT JOIN user_details d ON l.uid = d.uid " +
                     "ORDER BY l.uid DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) users.add(mapUser(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return users;
    }

    /**
     * Search users by name or username.
     */
    public List<User> searchUsers(String keyword, String utype) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT l.uid, l.uname, l.utype, l.enabled, l.created_at, " +
                     "d.name, d.email, d.mobile, d.address " +
                     "FROM login l LEFT JOIN user_details d ON l.uid = d.uid " +
                     "WHERE l.utype = ? AND (l.uname LIKE ? OR d.name LIKE ? OR d.email LIKE ?) " +
                     "ORDER BY l.uid DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            String pattern = "%" + keyword + "%";
            ps.setString(1, utype);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            rs = ps.executeQuery();
            while (rs.next()) users.add(mapUser(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return users;
    }

    /**
     * Update user profile details.
     */
    public boolean updateProfile(int uid, String name, String email, String mobile, String address) {
        String sql = "UPDATE user_details SET name = ?, email = ?, mobile = ?, address = ? WHERE uid = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, mobile);
            ps.setString(4, address);
            ps.setInt(5, uid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Update user password.
     */
    public boolean updatePassword(int uid, String newPassword) {
        String sql = "UPDATE login SET upass = ? WHERE uid = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, SHA1Util.hash(newPassword));
            ps.setInt(2, uid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Delete a user by ID.
     */
    public boolean deleteUser(int uid) {
        String sql = "DELETE FROM login WHERE uid = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Toggle user enabled/disabled status.
     */
    public boolean toggleEnabled(int uid) {
        String sql = "UPDATE login SET enabled = NOT enabled WHERE uid = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    /**
     * Check if username already exists.
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM login WHERE uname = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return false;
    }

    /**
     * Get counts for dashboard statistics.
     */
    public int getCountByType(String utype) {
        String sql = "SELECT COUNT(*) FROM login WHERE utype = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, utype);
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
     * Map ResultSet row to User object.
     */
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUid(rs.getInt("uid"));
        user.setUname(rs.getString("uname"));
        user.setUtype(rs.getString("utype"));
        user.setEnabled(rs.getBoolean("enabled"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setMobile(rs.getString("mobile"));
        user.setAddress(rs.getString("address"));
        return user;
    }
}
