package com.shopzone.model;

import java.sql.Timestamp;

/**
 * Order model representing a placed order.
 */
public class Order implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int orderId;
    private int userId;
    private int productId;
    private int quantity;
    private String address;
    private String status;
    private Timestamp orderDate;

    // Joined fields for display
    private String userName;
    private String productName;
    private double productPrice;
    private String productImage;

    public Order() {}

    // --- Getters and Setters ---

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    /**
     * Calculate total price for this order line.
     */
    public double getTotalPrice() {
        return productPrice * quantity;
    }

    /**
     * Get formatted total price.
     */
    public String getFormattedTotal() {
        return String.format("₹%.2f", getTotalPrice());
    }

    /**
     * Get formatted order date.
     */
    public String getFormattedDate() {
        if (orderDate == null) return "";
        return new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(orderDate);
    }

    /**
     * Get CSS class for status badge.
     */
    public String getStatusClass() {
        if (status == null) return "badge-secondary";
        switch (status) {
            case "Pending": return "badge-warning";
            case "Processing": return "badge-info";
            case "Shipped": return "badge-primary";
            case "Delivered": return "badge-success";
            case "Cancelled": return "badge-danger";
            default: return "badge-secondary";
        }
    }

    @Override
    public String toString() {
        return "Order{id=" + orderId + ", userId=" + userId + ", productId=" + productId +
               ", qty=" + quantity + ", status='" + status + "'}";
    }
}
