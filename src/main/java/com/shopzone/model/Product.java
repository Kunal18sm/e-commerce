package com.shopzone.model;

import java.sql.Timestamp;

/**
 * Product model representing an item in the catalog.
 */
public class Product implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int productId;
    private String name;
    private double price;
    private String description;
    private String image;
    private String category;
    private Timestamp createdAt;

    // Transient field for cart
    private int cartQuantity;

    public Product() {}

    public Product(int productId, String name, double price, String description, String image, String category) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.category = category;
    }

    // --- Getters and Setters ---

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getCartQuantity() { return cartQuantity; }
    public void setCartQuantity(int cartQuantity) { this.cartQuantity = cartQuantity; }

    /**
     * Get formatted price string.
     */
    public String getFormattedPrice() {
        return String.format("₹%.2f", price);
    }

    /**
     * Get the first letter of the product name for avatar display.
     */
    public String getInitial() {
        return (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase() : "?";
    }

    @Override
    public String toString() {
        return "Product{id=" + productId + ", name='" + name + "', price=" + price + ", category='" + category + "'}";
    }
}
