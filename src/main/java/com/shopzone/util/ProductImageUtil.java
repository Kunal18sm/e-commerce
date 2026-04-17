package com.shopzone.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles product image resolution from text input or multipart upload.
 */
public final class ProductImageUtil {

    private static final Pattern JSON_SECURE_URL_PATTERN =
            Pattern.compile("\"secure_url\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern JSON_MESSAGE_PATTERN =
            Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"");

    private ProductImageUtil() {}

    public static String resolveImage(HttpServletRequest request, String imageTextField, String imagePartField)
            throws IOException, ServletException {
        String imageText = request.getParameter(imageTextField);
        String normalizedText = imageText != null ? imageText.trim() : "";

        Part imagePart = request.getPart(imagePartField);
        String uploadedFileName = saveUploadedImageIfPresent(request, imagePart);
        if (uploadedFileName != null) {
            return uploadedFileName;
        }

        if (!normalizedText.isEmpty()) {
            return normalizedText;
        }
        return "default.jpg";
    }

    private static String saveUploadedImageIfPresent(HttpServletRequest request, Part imagePart)
            throws IOException, ServletException {
        if (imagePart == null || imagePart.getSize() <= 0) {
            return null;
        }

        String submittedFileName = extractSubmittedFileName(imagePart);
        if (submittedFileName == null || submittedFileName.trim().isEmpty()) {
            return null;
        }

        String safeName = Paths.get(submittedFileName).getFileName().toString();
        int dotIndex = safeName.lastIndexOf('.');
        String extension = dotIndex >= 0 ? safeName.substring(dotIndex).toLowerCase(Locale.ROOT) : "";

        if (!isAllowedImageExtension(extension)) {
            throw new ServletException("Unsupported image type. Allowed: .jpg, .jpeg, .png, .webp, .gif");
        }

        CloudinaryConfig cloudinaryConfig = CloudinaryConfig.fromEnv();
        if (cloudinaryConfig.isEnabled()) {
            return uploadToCloudinary(imagePart, safeName, cloudinaryConfig);
        }

        return saveLocally(request, imagePart, extension);
    }

    private static String saveLocally(HttpServletRequest request, Part imagePart, String extension) throws IOException {
        String generatedName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path uploadDir = resolveUploadDir(request);
        Files.createDirectories(uploadDir);

        try (InputStream in = imagePart.getInputStream()) {
            Files.copy(in, uploadDir.resolve(generatedName), StandardCopyOption.REPLACE_EXISTING);
        }
        return generatedName;
    }

    private static String uploadToCloudinary(Part imagePart, String safeName, CloudinaryConfig config)
            throws IOException, ServletException {
        String boundary = "----ShopZoneBoundary" + UUID.randomUUID().toString().replace("-", "");
        String endpoint = "https://api.cloudinary.com/v1_1/" + urlEncode(config.cloudName) + "/image/upload";

        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        long timestamp = System.currentTimeMillis() / 1000L;

        try (OutputStream out = conn.getOutputStream()) {
            if (config.folder != null && !config.folder.isEmpty()) {
                writeTextPart(out, boundary, "folder", config.folder);
            }

            if (config.uploadPreset != null && !config.uploadPreset.isEmpty()) {
                writeTextPart(out, boundary, "upload_preset", config.uploadPreset);
            } else {
                writeTextPart(out, boundary, "timestamp", String.valueOf(timestamp));
                writeTextPart(out, boundary, "api_key", config.apiKey);
                writeTextPart(out, boundary, "signature", buildCloudinarySignature(timestamp, config.folder, config.apiSecret));
            }

            String contentType = imagePart.getContentType();
            if (contentType == null || contentType.trim().isEmpty()) {
                contentType = "application/octet-stream";
            }
            writeFilePart(out, boundary, "file", safeName, contentType, imagePart);
            out.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        }

        int statusCode = conn.getResponseCode();
        String responseBody;
        try (InputStream in = statusCode >= 200 && statusCode < 300 ? conn.getInputStream() : conn.getErrorStream()) {
            responseBody = in != null ? readFully(in) : "";
        } finally {
            conn.disconnect();
        }

        if (statusCode < 200 || statusCode >= 300) {
            String errorMessage = extractMessage(responseBody);
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Cloudinary upload failed with HTTP " + statusCode;
            }
            throw new ServletException(errorMessage);
        }

        String secureUrl = extractSecureUrl(responseBody);
        if (secureUrl == null || secureUrl.isEmpty()) {
            throw new ServletException("Cloudinary response missing secure_url");
        }
        return secureUrl;
    }

    private static boolean isAllowedImageExtension(String extension) {
        return ".jpg".equals(extension)
                || ".jpeg".equals(extension)
                || ".png".equals(extension)
                || ".webp".equals(extension)
                || ".gif".equals(extension);
    }

    private static Path resolveUploadDir(HttpServletRequest request) {
        String realPath = request.getServletContext().getRealPath("/uploads");
        if (realPath != null && !realPath.trim().isEmpty()) {
            return Paths.get(realPath);
        }

        String appRoot = request.getServletContext().getRealPath("/");
        if (appRoot != null && !appRoot.trim().isEmpty()) {
            return Paths.get(appRoot, "uploads");
        }

        return Paths.get(System.getProperty("java.io.tmpdir"), "shopzone-uploads");
    }

    private static String extractSubmittedFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }

        String[] parts = contentDisposition.split(";");
        for (String token : parts) {
            String trimmed = token.trim();
            if (trimmed.startsWith("filename=")) {
                String fileName = trimmed.substring("filename=".length()).trim();
                if (fileName.startsWith("\"") && fileName.endsWith("\"") && fileName.length() >= 2) {
                    fileName = fileName.substring(1, fileName.length() - 1);
                }
                return fileName;
            }
        }
        return null;
    }

    private static void writeTextPart(OutputStream out, String boundary, String fieldName, String value) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"\r\n\r\n");
        sb.append(value).append("\r\n");
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void writeFilePart(OutputStream out, String boundary, String fieldName, String fileName,
                                      String contentType, Part imagePart) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"").append(fieldName)
          .append("\"; filename=\"").append(fileName).append("\"\r\n");
        sb.append("Content-Type: ").append(contentType).append("\r\n\r\n");
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));

        byte[] buffer = new byte[8192];
        int bytesRead;
        try (InputStream fileIn = imagePart.getInputStream()) {
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private static String buildCloudinarySignature(long timestamp, String folder, String apiSecret)
            throws ServletException {
        StringBuilder toSign = new StringBuilder();
        if (folder != null && !folder.isEmpty()) {
            toSign.append("folder=").append(folder).append("&");
        }
        toSign.append("timestamp=").append(timestamp).append(apiSecret);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(toSign.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException("Unable to generate Cloudinary signature", e);
        }
    }

    private static String readFully(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            baos.write(buffer, 0, read);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    private static String extractSecureUrl(String json) {
        Matcher matcher = JSON_SECURE_URL_PATTERN.matcher(json);
        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }
        return null;
    }

    private static String extractMessage(String json) {
        Matcher matcher = JSON_MESSAGE_PATTERN.matcher(json);
        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }
        return null;
    }

    private static String unescapeJson(String value) {
        return value.replace("\\/", "/").replace("\\\"", "\"");
    }

    private static String urlEncode(String value) throws IOException {
        return URLEncoder.encode(value, "UTF-8");
    }

    private static class CloudinaryConfig {
        private final String cloudName;
        private final String apiKey;
        private final String apiSecret;
        private final String uploadPreset;
        private final String folder;

        private CloudinaryConfig(String cloudName, String apiKey, String apiSecret,
                                 String uploadPreset, String folder) {
            this.cloudName = cloudName;
            this.apiKey = apiKey;
            this.apiSecret = apiSecret;
            this.uploadPreset = uploadPreset;
            this.folder = folder;
        }

        static CloudinaryConfig fromEnv() {
            return new CloudinaryConfig(
                    normalize(firstNonBlank(
                            EnvLoader.get("CLOUDINARY_CLOUD_NAME"),
                            EnvLoader.get("PERMANENT_CLOUD_NAME"))),
                    normalize(firstNonBlank(
                            EnvLoader.get("CLOUDINARY_API_KEY"),
                            EnvLoader.get("PERMANENT_CLOUD_API_KEY"))),
                    normalize(firstNonBlank(
                            EnvLoader.get("CLOUDINARY_API_SECRET"),
                            EnvLoader.get("PERMANENT_CLOUD_API_SECRET"))),
                    normalize(EnvLoader.get("CLOUDINARY_UPLOAD_PRESET")),
                    normalize(EnvLoader.get("CLOUDINARY_FOLDER", "shopzone"))
            );
        }

        boolean isEnabled() {
            boolean hasUnsigned = cloudName != null && uploadPreset != null;
            boolean hasSigned = cloudName != null && apiKey != null && apiSecret != null;
            return hasUnsigned || hasSigned;
        }

        private static String firstNonBlank(String first, String second) {
            String normalizedFirst = normalize(first);
            if (normalizedFirst != null) {
                return normalizedFirst;
            }
            return normalize(second);
        }

        private static String normalize(String value) {
            if (value == null) return null;
            String trimmed = value.trim();
            return trimmed.isEmpty() ? null : trimmed;
        }
    }
}
