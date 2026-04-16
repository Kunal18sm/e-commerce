-- =============================================
-- ShopZone Online Shopping System
-- Database Schema & Seed Data
-- (Configured for Aiven MySQL - uses defaultdb)
-- =============================================

-- Use the Aiven default database
-- (CREATE DATABASE not needed on Aiven free tier)
USE defaultdb;

-- =============================================
-- TABLE: login
-- Stores authentication credentials and roles
-- =============================================
CREATE TABLE IF NOT EXISTS login (
    uid INT AUTO_INCREMENT PRIMARY KEY,
    uname VARCHAR(50) NOT NULL UNIQUE,
    upass VARCHAR(100) NOT NULL,
    utype ENUM('admin', 'mod', 'user') NOT NULL DEFAULT 'user',
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =============================================
-- TABLE: user_details
-- Stores user profile information
-- =============================================
CREATE TABLE IF NOT EXISTS user_details (
    uid INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    mobile VARCHAR(20),
    address TEXT,
    FOREIGN KEY (uid) REFERENCES login(uid) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =============================================
-- TABLE: products
-- Stores product catalog
-- =============================================
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    image VARCHAR(255) DEFAULT 'default.jpg',
    category VARCHAR(100) DEFAULT 'General',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =============================================
-- TABLE: cart
-- Stores shopping cart items per user
-- =============================================
CREATE TABLE IF NOT EXISTS cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES login(uid) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_item (user_id, product_id)
) ENGINE=InnoDB;

-- =============================================
-- TABLE: orders
-- Stores placed orders
-- =============================================
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    address TEXT NOT NULL,
    status ENUM('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES login(uid) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =============================================
-- INDEXES for performance
-- =============================================
CREATE INDEX idx_login_uname ON login(uname);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_cart_user ON cart(user_id);

-- =============================================
-- SEED DATA
-- =============================================

-- Default Admin Account
-- Username: admin | Password: admin123
INSERT INTO login (uname, upass, utype, enabled) VALUES
    ('admin', SHA1('admin123'), 'admin', TRUE);

INSERT INTO user_details (uid, name, email, mobile, address) VALUES
    (1, 'System Administrator', 'admin@shopzone.com', '9876543210', 'ShopZone HQ, Tech Park, Bangalore');

-- Sample Moderator
-- Username: moderator | Password: mod123
INSERT INTO login (uname, upass, utype, enabled) VALUES
    ('moderator', SHA1('mod123'), 'mod', TRUE);

INSERT INTO user_details (uid, name, email, mobile, address) VALUES
    (2, 'Store Manager', 'mod@shopzone.com', '9876543211', 'ShopZone Store, Mall Road, Delhi');

-- Sample User
-- Username: user1 | Password: user123
INSERT INTO login (uname, upass, utype, enabled) VALUES
    ('user1', SHA1('user123'), 'user', TRUE);

INSERT INTO user_details (uid, name, email, mobile, address) VALUES
    (3, 'Rahul Sharma', 'rahul@gmail.com', '9876543212', '42, MG Road, Mumbai 400001');

-- =============================================
-- Sample Products
-- =============================================
INSERT INTO products (name, price, description, image, category) VALUES
('Wireless Bluetooth Headphones', 2499.00,
 'Premium wireless headphones with active noise cancellation, 30-hour battery life, and crystal-clear HD audio. Features comfortable over-ear cushions and foldable design for portability.',
 'headphones.jpg', 'Electronics'),

('Smart Fitness Watch Pro', 3999.00,
 'Advanced fitness tracker with heart rate monitoring, SpO2 sensor, GPS tracking, sleep analysis, and 7-day battery life. Water resistant up to 50 meters with a vibrant AMOLED display.',
 'smartwatch.jpg', 'Electronics'),

('Premium Cotton Casual T-Shirt', 599.00,
 'Ultra-soft 100% organic cotton t-shirt with a relaxed fit. Available in multiple colors. Pre-shrunk fabric, double-stitched hems for durability. Perfect for everyday wear.',
 'tshirt.jpg', 'Clothing'),

('Running Shoes UltraBoost', 4299.00,
 'Lightweight performance running shoes with responsive cushioning, breathable mesh upper, and durable rubber outsole. Engineered for comfort during long runs.',
 'shoes.jpg', 'Sports'),

('Insulated Stainless Steel Bottle', 799.00,
 'Double-wall vacuum insulated water bottle. Keeps drinks cold for 24 hours or hot for 12 hours. BPA-free, leak-proof cap. 750ml capacity with matte finish.',
 'bottle.jpg', 'Home & Kitchen'),

('Laptop Backpack Elite', 1499.00,
 'Premium laptop backpack with USB charging port, anti-theft hidden zipper, and water-resistant fabric. Fits 15.6 inch laptops. Multiple organized compartments.',
 'backpack.jpg', 'Electronics'),

('Organic Green Tea Collection', 349.00,
 'Premium organic green tea sourced from high-altitude gardens. Rich in natural antioxidants. Pack of 50 individually wrapped tea bags. USDA Organic certified.',
 'greentea.jpg', 'Home & Kitchen'),

('Professional Yoga Mat', 1299.00,
 'Non-slip premium yoga mat with alignment guide lines. 6mm thick TPE foam for comfort and joint protection. Comes with carrying strap. Eco-friendly material.',
 'yogamat.jpg', 'Sports'),

('Classic Slim-Fit Jeans', 1899.00,
 'Modern slim-fit denim jeans with 2% stretch for all-day comfort. Classic indigo wash with five-pocket styling. Reinforced stitching for long-lasting wear.',
 'jeans.jpg', 'Clothing'),

('Complete Programming Bundle', 2999.00,
 'Comprehensive programming book collection covering Java, Python, JavaScript, and Web Development. Includes hands-on projects and exercises. Over 2000 pages of content.',
 'books.jpg', 'Books'),

('Ergonomic Wireless Mouse', 699.00,
 'Silent-click wireless mouse with adjustable DPI (800/1200/1600). Ergonomic sculpted design reduces wrist strain. 2.4GHz wireless with nano receiver. 18-month battery life.',
 'mouse.jpg', 'Electronics'),

('Artisan Ceramic Mug Set', 899.00,
 'Beautiful set of 4 handcrafted ceramic coffee mugs. Each mug holds 350ml. Microwave and dishwasher safe. Unique reactive glaze finish makes each piece one-of-a-kind.',
 'mugs.jpg', 'Home & Kitchen');
