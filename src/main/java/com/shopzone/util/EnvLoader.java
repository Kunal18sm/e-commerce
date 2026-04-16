package com.shopzone.util;

import java.io.*;
import java.util.Properties;

/**
 * Loads environment variables from a .env file.
 * Supports reading from project root or a custom path via system property.
 */
public class EnvLoader {

    private static final Properties props = new Properties();
    private static boolean loaded = false;

    static {
        load();
    }

    private static void load() {
        if (loaded) return;

        String[] searchPaths = {
            System.getProperty("shopzone.env", ""),
            ".env",
            "../.env",
            "../../.env"
        };

        for (String path : searchPaths) {
            if (path == null || path.isEmpty()) continue;
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        // Skip empty lines and comments
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int eqIndex = line.indexOf('=');
                        if (eqIndex > 0) {
                            String key = line.substring(0, eqIndex).trim();
                            String value = line.substring(eqIndex + 1).trim();
                            // Remove surrounding quotes if present
                            if (value.length() >= 2 &&
                                ((value.startsWith("\"") && value.endsWith("\"")) ||
                                 (value.startsWith("'") && value.endsWith("'")))) {
                                value = value.substring(1, value.length() - 1);
                            }
                            props.setProperty(key, value);
                        }
                    }
                    loaded = true;
                    System.out.println("[ShopZone] Loaded .env from: " + file.getAbsolutePath());
                    break;
                } catch (IOException e) {
                    System.err.println("[ShopZone] Error reading .env file: " + e.getMessage());
                }
            }
        }

        if (!loaded) {
            System.err.println("[ShopZone] WARNING: No .env file found. Using default database settings.");
        }
    }

    /**
     * Get an environment variable value.
     */
    public static String get(String key) {
        // Check system env first, then .env file
        String sysEnv = System.getenv(key);
        if (sysEnv != null && !sysEnv.isEmpty()) return sysEnv;
        return props.getProperty(key);
    }

    /**
     * Get an environment variable value with a default fallback.
     */
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
