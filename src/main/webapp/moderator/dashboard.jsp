<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Moderator Dashboard" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-section">
            <div class="sidebar-label">Moderator Panel</div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/mod/dashboard" class="active">
                    <span class="nav-icon">📊</span> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/mod/products">
                    <span class="nav-icon">📦</span> Products
                </a>
                <a href="${pageContext.request.contextPath}/mod/users">
                    <span class="nav-icon">👥</span> Users
                </a>
                <a href="${pageContext.request.contextPath}/mod/orders">
                    <span class="nav-icon">🛒</span> Orders
                </a>
            </nav>
        </div>
        <div class="sidebar-section">
            <div class="sidebar-label">Quick Links</div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/products">
                    <span class="nav-icon">🏠</span> View Store
                </a>
                <a href="${pageContext.request.contextPath}/profile">
                    <span class="nav-icon">👤</span> My Profile
                </a>
            </nav>
        </div>
    </aside>

    <div class="dashboard-content">
        <div class="page-header">
            <h1>📊 Moderator Dashboard</h1>
            <p>Welcome, ${loggedUser.displayName}! Here's your overview.</p>
        </div>

        <div class="stat-grid">
            <div class="stat-card stat-primary">
                <div class="stat-icon">👥</div>
                <div class="stat-value">${totalUsers}</div>
                <div class="stat-label">Total Users</div>
            </div>
            <div class="stat-card stat-warning">
                <div class="stat-icon">📦</div>
                <div class="stat-value">${totalProducts}</div>
                <div class="stat-label">Products</div>
            </div>
            <div class="stat-card stat-success">
                <div class="stat-icon">🛒</div>
                <div class="stat-value">${totalOrders}</div>
                <div class="stat-label">Total Orders</div>
            </div>
            <div class="stat-card stat-danger">
                <div class="stat-icon">💰</div>
                <div class="stat-value">₹<fmt:formatNumber value="${totalRevenue}" pattern="#,##0"/></div>
                <div class="stat-label">Revenue</div>
            </div>
        </div>

        <!-- Recent Orders -->
        <div class="card">
            <div class="card-header">
                <span>📋 Recent Orders</span>
                <a href="${pageContext.request.contextPath}/mod/orders" class="btn btn-sm btn-secondary">View All →</a>
            </div>
            <c:choose>
                <c:when test="${empty recentOrders}">
                    <div class="card-body">
                        <div class="empty-state" style="padding:32px;">
                            <div class="empty-icon" style="font-size:2rem;">📋</div>
                            <p class="text-muted">No orders yet.</p>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrapper">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Order ID</th>
                                    <th>Customer</th>
                                    <th>Product</th>
                                    <th>Total</th>
                                    <th>Status</th>
                                    <th>Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="order" items="${recentOrders}" end="9">
                                    <tr>
                                        <td><strong>#${order.orderId}</strong></td>
                                        <td>${order.userName}</td>
                                        <td>${order.productName}</td>
                                        <td style="font-weight:600;">₹<fmt:formatNumber value="${order.totalPrice}" pattern="#,##0.00"/></td>
                                        <td><span class="badge ${order.statusClass}">${order.status}</span></td>
                                        <td class="text-small text-muted">${order.formattedDate}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
