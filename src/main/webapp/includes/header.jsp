<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Get logged-in user from session
    com.shopzone.model.User loggedUser = (com.shopzone.model.User) session.getAttribute("loggedInUser");
    String ctxPath = request.getContextPath();

    // Get cart count for badge
    int cartCount = 0;
    if (loggedUser != null) {
        try {
            com.shopzone.dao.OrderDAO cartDAO = new com.shopzone.dao.OrderDAO();
            cartCount = cartDAO.getCartCount(loggedUser.getUid());
        } catch (Exception e) { /* ignore */ }
    }
    request.setAttribute("loggedUser", loggedUser);
    pageContext.setAttribute("cartCount", cartCount);
    pageContext.setAttribute("ctxPath", ctxPath);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="ShopZone - Your premium online shopping destination for electronics, clothing, sports gear, and more.">
    <title>${pageTitle != null ? pageTitle : 'ShopZone'} | Online Shopping</title>
    <link rel="stylesheet" href="${ctxPath}/css/style.css">
</head>
<body>

<!-- Navigation Bar -->
<nav class="navbar">
    <div class="container">
        <a href="${ctxPath}/products" class="navbar-brand">
            <span class="brand-icon">🛍️</span>
            ShopZone
        </a>

        <div class="navbar-nav">
            <a href="${ctxPath}/products" id="nav-home">🏠 Home</a>

            <c:choose>
                <c:when test="${loggedUser != null}">
                    <c:if test="${loggedUser.utype == 'user'}">
                        <a href="${ctxPath}/cart" id="nav-cart" class="nav-cart-badge">
                            🛒 Cart
                            <c:if test="${cartCount > 0}">
                                <span class="badge-count">${cartCount}</span>
                            </c:if>
                        </a>
                        <a href="${ctxPath}/orders" id="nav-orders">📦 Orders</a>
                    </c:if>
                    <c:if test="${loggedUser.utype == 'admin'}">
                        <a href="${ctxPath}/admin/dashboard" id="nav-admin">⚙️ Admin</a>
                    </c:if>
                    <c:if test="${loggedUser.utype == 'mod'}">
                        <a href="${ctxPath}/mod/dashboard" id="nav-mod">🛠️ Panel</a>
                    </c:if>
                    <a href="${ctxPath}/profile" id="nav-profile">
                        <span class="nav-user">
                            <span class="user-avatar">
                                ${loggedUser.name != null && loggedUser.name.length() > 0 ? loggedUser.name.substring(0,1).toUpperCase() : '?'}
                            </span>
                            <span class="user-info">
                                <span class="user-name">${loggedUser.displayName}</span>
                                <span class="user-role">${loggedUser.utype}</span>
                            </span>
                        </span>
                    </a>
                    <a href="${ctxPath}/logout" id="nav-logout">↩️ Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="${ctxPath}/login" id="nav-login">🔑 Login</a>
                    <a href="${ctxPath}/register" id="nav-register" class="btn btn-primary btn-sm" style="color:white;">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<!-- Toast Container -->
<div id="toast-container" class="toast-container"></div>

<!-- Main Content -->
<main>
