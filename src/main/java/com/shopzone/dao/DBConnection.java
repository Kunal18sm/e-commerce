package com.shopzone.dao;

import com.shopzone.util.EnvLoader;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        DbConfig cfg = loadConfig();
        return DriverManager.getConnection(cfg.jdbcUrl, cfg.user, cfg.pass);
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

    private static DbConfig loadConfig() {
        String directJdbcUrl = trimToNull(EnvLoader.get("DB_URL"));
        String user = trimToNull(EnvLoader.get("DB_USER"));
        String pass = trimToNull(EnvLoader.get("DB_PASS"));
        if (directJdbcUrl != null) {
            return new DbConfig(directJdbcUrl, defaultIfNull(user, "root"), defaultIfNull(pass, "root"));
        }

        String serviceUri = firstNonEmpty(
                EnvLoader.get("DB_SERVICE_URI"),
                EnvLoader.get("AIVEN_SERVICE_URI"),
                EnvLoader.get("SERVICE_URI")
        );

        String hostValue = trimToNull(EnvLoader.get("DB_HOST", "localhost"));
        if (serviceUri == null && hostValue != null && hostValue.startsWith("mysql://")) {
            serviceUri = hostValue;
        }

        ParsedMysqlUri parsedUri = serviceUri != null ? parseMysqlUri(serviceUri) : null;
        if (parsedUri != null) {
            String host = defaultIfNull(parsedUri.host, "localhost");
            String port = defaultIfNull(parsedUri.port, "3306");
            String dbName = defaultIfNull(parsedUri.database, "shopzone");

            if (user == null) user = defaultIfNull(parsedUri.user, "root");
            if (pass == null) pass = defaultIfNull(parsedUri.password, "root");

            String sslSetting = firstNonEmpty(EnvLoader.get("DB_SSL"), parsedUri.sslMode);
            String sslParams = buildSslParams(sslSetting);

            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                    + "?" + sslParams + "&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
            return new DbConfig(url, defaultIfNull(user, "root"), defaultIfNull(pass, "root"));
        }

        String host = defaultIfNull(hostValue, "localhost");
        String port = EnvLoader.get("DB_PORT", "3306");
        String dbName = EnvLoader.get("DB_NAME", "shopzone");

        String sslParams = buildSslParams(EnvLoader.get("DB_SSL", "false"));

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?" + sslParams + "&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

        return new DbConfig(url, defaultIfNull(user, "root"), defaultIfNull(pass, "root"));
    }

    private static String buildSslParams(String sslValue) {
        String normalized = sslValue != null ? sslValue.trim().toLowerCase(Locale.ROOT) : "false";
        boolean sslEnabled = "true".equals(normalized)
                || "required".equals(normalized)
                || "require".equals(normalized);
        if (sslEnabled) {
            return "useSSL=true&requireSSL=true&verifyServerCertificate=false";
        }
        return "useSSL=false";
    }

    private static ParsedMysqlUri parseMysqlUri(String uriText) {
        try {
            URI uri = URI.create(uriText.trim());
            if (!"mysql".equalsIgnoreCase(uri.getScheme())) {
                return null;
            }

            ParsedMysqlUri parsed = new ParsedMysqlUri();
            parsed.host = trimToNull(uri.getHost());
            if (uri.getPort() > 0) {
                parsed.port = String.valueOf(uri.getPort());
            }

            String rawPath = trimToNull(uri.getPath());
            if (rawPath != null) {
                while (rawPath.startsWith("/")) rawPath = rawPath.substring(1);
                if (!rawPath.isEmpty()) parsed.database = rawPath;
            }

            String userInfo = trimToNull(uri.getRawUserInfo());
            if (userInfo != null) {
                int colon = userInfo.indexOf(':');
                if (colon >= 0) {
                    parsed.user = decode(userInfo.substring(0, colon));
                    parsed.password = decode(userInfo.substring(colon + 1));
                } else {
                    parsed.user = decode(userInfo);
                }
            }

            Map<String, String> queryParams = parseQuery(uri.getRawQuery());
            String sslMode = queryParams.get("ssl-mode");
            if (sslMode == null) sslMode = queryParams.get("sslmode");
            parsed.sslMode = trimToNull(sslMode);

            return parsed;
        } catch (Exception e) {
            return null;
        }
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> map = new HashMap<String, String>();
        if (rawQuery == null || rawQuery.trim().isEmpty()) return map;

        String[] parts = rawQuery.split("&");
        for (String part : parts) {
            if (part == null || part.isEmpty()) continue;
            int idx = part.indexOf('=');
            if (idx <= 0) continue;
            String key = decode(part.substring(0, idx)).toLowerCase(Locale.ROOT);
            String value = decode(part.substring(idx + 1));
            map.put(key, value);
        }
        return map;
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return value;
        }
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String defaultIfNull(String value, String fallback) {
        return value != null ? value : fallback;
    }

    private static String firstNonEmpty(String... values) {
        if (values == null) return null;
        for (String value : values) {
            String normalized = trimToNull(value);
            if (normalized != null) return normalized;
        }
        return null;
    }

    private static class DbConfig {
        private final String jdbcUrl;
        private final String user;
        private final String pass;

        private DbConfig(String jdbcUrl, String user, String pass) {
            this.jdbcUrl = jdbcUrl;
            this.user = user;
            this.pass = pass;
        }
    }

    private static class ParsedMysqlUri {
        private String host;
        private String port;
        private String database;
        private String user;
        private String password;
        private String sslMode;
    }
}
