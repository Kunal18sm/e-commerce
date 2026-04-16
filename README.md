# 🛍️ ShopZone — Online Shopping System

A complete production-ready **Online Shopping System** built with Java (JSP/Servlet), MySQL, and Android WebView.

---

## 🏗️ Architecture

```
Client (Browser/Android)  →  HTTP  →  JSP/Servlet (Tomcat)  →  JDBC  →  MySQL
```

| Layer | Technology |
|-------|-----------|
| Frontend | HTML5, CSS3, JavaScript, JSP |
| Backend | Java Servlets (MVC Controllers) |
| Database | MySQL 8.0 via JDBC |
| Server | Embedded Apache Tomcat (via Maven) |
| Android | WebView-based app |
| Security | SHA-1 password hashing, Session management |

---

## 👥 User Roles

| Role | Capabilities |
|------|-------------|
| **Admin** | Full control: manage products, users, moderators, orders |
| **Moderator** | Manage products, users, orders (cannot manage moderators) |
| **User** | Browse, cart, place orders, edit profile |

---

## 🚀 Setup & Run Instructions

### Prerequisites
- **Java JDK 8+** — [Download](https://adoptium.net/)
- **Maven 3.6+** — [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/)

### Step 1: Configure Database

1. Start MySQL server
2. Run the SQL schema:
   ```bash
   mysql -u root -p < database/shopzone.sql
   ```
3. Update `.env` with your MySQL credentials:
   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=shopzone
   DB_USER=root
   DB_PASS=your_password
   ```
4. (Optional) Enable Cloudinary image upload by adding:
   ```
   CLOUDINARY_CLOUD_NAME=your_cloud_name
   CLOUDINARY_UPLOAD_PRESET=your_unsigned_preset
   CLOUDINARY_FOLDER=shopzone
   ```
   Or use signed uploads:
   ```
   CLOUDINARY_CLOUD_NAME=your_cloud_name
   CLOUDINARY_API_KEY=your_api_key
   CLOUDINARY_API_SECRET=your_api_secret
   CLOUDINARY_FOLDER=shopzone
   ```

### Step 2: Run the Application

```bash
# Navigate to project root
cd ecom2

# Build and run with embedded Tomcat
.\mvnw.cmd tomcat7:run
```

The application will start at: **http://localhost:8080/ShopZone/**

### Step 3: Login

| Account | Username | Password |
|---------|----------|----------|
| Admin | `admin` | `admin123` |
| Moderator | `moderator` | `mod123` |
| User | `user1` | `user123` |

---

## 📁 Project Structure

```
ecom2/
├── .env                          # Database credentials
├── pom.xml                       # Maven config + embedded Tomcat
├── database/
│   └── shopzone.sql              # SQL schema + seed data
├── src/main/
│   ├── java/com/shopzone/
│   │   ├── model/                # POJOs (User, Product, Order)
│   │   ├── dao/                  # Data Access (DB, User, Product, Order)
│   │   ├── servlet/              # Controllers (Login, Cart, Admin, etc.)
│   │   └── util/                 # Utilities (SHA1, Env, Session)
│   └── webapp/
│       ├── WEB-INF/web.xml       # Deployment descriptor
│       ├── css/style.css         # Design system
│       ├── js/app.js             # Client-side logic
│       ├── includes/             # Header & footer
│       ├── admin/                # Admin panel pages
│       ├── moderator/            # Moderator panel pages
│       └── *.jsp                 # User-facing pages
└── ShopZoneApp/                  # Android WebView app
```

---

## 🗄️ Database Schema

### Tables
| Table | Description |
|-------|-------------|
| `login` | Authentication (uid, username, SHA1 password, role, enabled) |
| `user_details` | Profile info (name, email, mobile, address) |
| `products` | Product catalog (name, price, description, image, category) |
| `cart` | Shopping cart per user |
| `orders` | Placed orders with status tracking |

### ER Relationships
- `login` → `user_details` (1:1)
- `login` → `cart` (1:N)
- `login` → `orders` (1:N)
- `products` → `cart` (1:N)
- `products` → `orders` (1:N)

---

## 🔄 Application Flow

### User Shopping Flow
```
Register → Admin Approves → Login → Browse Products → Add to Cart → Checkout → Order Placed
```

### Admin Flow
```
Login → Dashboard (Stats) → Manage Products/Users/Moderators/Orders
```

### Data Flow (DFD)
```
User → [Login] → Session Created → [Browse] → Product List
                                  → [Add to Cart] → Cart DB
                                  → [Checkout] → Orders DB → Admin Notified
```

---

## 📱 Android App

The Android app uses WebView to load the web application:

1. Open `ShopZoneApp/` in Android Studio
2. Update `SHOP_URL` in `MainActivity.java`:
   - **Emulator**: `http://10.0.2.2:8080/ShopZone/products`
   - **Physical device**: `http://YOUR_PC_IP:8080/ShopZone/products`
3. Build and run

---

## ⚙️ Key Features

- ✅ Role-based access control (Admin/Mod/User)
- ✅ SHA-1 password encryption
- ✅ Session management with 30-min timeout
- ✅ Shopping cart with quantity management
- ✅ Order placement and status tracking
- ✅ Responsive design (mobile + desktop)
- ✅ Product categories and search
- ✅ User approval system (admin enables accounts)
- ✅ Block/unblock users and moderators
- ✅ Order status progression (Pending → Processing → Shipped → Delivered)
- ✅ Revenue tracking dashboard
- ✅ CSRF-safe form submissions
- ✅ Beautiful light-theme UI with micro-animations

---

## 📊 API / URL Routes

| URL | Method | Description |
|-----|--------|-------------|
| `/login` | GET/POST | Login page |
| `/register` | GET/POST | Registration |
| `/logout` | GET | Logout |
| `/products` | GET | Product listing (+ ?q= search, ?cat= filter, ?id= detail) |
| `/cart` | GET/POST | Cart operations |
| `/orders` | GET | User's order history |
| `/profile` | GET/POST | Profile management |
| `/admin/*` | GET/POST | Admin panel |
| `/mod/*` | GET/POST | Moderator panel |

---

## 🎨 UI Design

- **Theme**: Premium light theme
- **Primary**: Indigo (#4F46E5)
- **Font**: Inter (Google Fonts)
- **Components**: Cards, badges, modals, toasts, data tables
- **Responsive**: Mobile-first with breakpoints at 480px, 768px, 1024px

---

© 2026 ShopZone. Built with ❤️
