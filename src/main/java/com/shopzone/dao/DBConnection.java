package com.shopzone.dao;

import com.shopzone.util.EnvLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection factory.
 * Reads credentials from .env file via EnvLoader.
 */
public class DBConnection {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("[ShopZone] MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Get a new database connection using .env configuration.
     */
    public static Connection getConnection() throws SQLException {
        String host = EnvLoader.get("DB_HOST", "localhost");
        String port = EnvLoader.get("DB_PORT", "3306");
        String dbName = EnvLoader.get("DB_NAME", "shopzone");
        String user = EnvLoader.get("DB_USER", "root");
        String pass = EnvLoader.get("DB_PASS", "root");

        String ssl = EnvLoader.get("DB_SSL", "false");
        String sslParams;
        if ("true".equalsIgnoreCase(ssl)) {
            sslParams = "useSSL=true&requireSSL=true&verifyServerCertificate=false";
        } else {
            sslParams = "useSSL=false";
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                   + "?" + sslParams + "&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Close a connection silently.
     */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try { res.close(); } catch (Exception ignored) {}
            }
        }
    }
}
