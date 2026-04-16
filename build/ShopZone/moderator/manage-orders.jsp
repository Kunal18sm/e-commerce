<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Manage Orders" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-section">
            <div class="sidebar-label">Moderator Panel</div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/mod/dashboard"><span class="nav-icon">📊</span> Dashboard</a>
                <a href="${pageContext.request.contextPath}/mod/products"><span class="nav-icon">📦</span> Products</a>
                <a href="${pageContext.request.contextPath}/mod/users"><span class="nav-icon">👥</span> Users</a>
                <a href="${pageContext.request.contextPath}/mod/orders" class="active"><span class="nav-icon">🛒</span> Orders</a>
            </nav>
        </div>
    </aside>

    <div class="dashboard-content">
        <div class="page-header">
            <h1>🛒 Manage Orders</h1>
            <p class="text-muted">View and update order statuses</p>
        </div>

        <div class="table-wrapper">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Customer</th>
                        <th>Product</th>
                        <th>Qty</th>
                        <th>Total</th>
                        <th>Address</th>
                        <th>Status</th>
                        <th>Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty orders}">
                            <tr><td colspan="9" class="text-center text-muted" style="padding:40px;">No orders found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="o" items="${orders}">
                                <tr>
                                    <td><strong>#${o.orderId}</strong></td>
                                    <td>${o.userName}</td>
                                    <td style="max-width:160px;">
                                        <div style="font-weight:600; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${o.productName}</div>
                                    </td>
                                    <td>${o.quantity}</td>
                                    <td style="font-weight:600;">₹<fmt:formatNumber value="${o.totalPrice}" pattern="#,##0.00"/></td>
                                    <td style="max-width:140px;">
                                        <div style="font-size:0.8rem; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;" title="${o.address}">${o.address}</div>
                                    </td>
                                    <td><span class="badge ${o.statusClass}">${o.status}</span></td>
                                    <td class="text-small text-muted">${o.formattedDate}</td>
                                    <td>
                                        <div class="table-actions" style="flex-wrap:wrap;">
                                            <form action="${pageContext.request.contextPath}/mod/orders" method="POST" style="display:inline;">
                                                <input type="hidden" name="action" value="updateOrderStatus">
                                                <input type="hidden" name="id" value="${o.orderId}">
                                                <select name="status" class="form-control" style="padding:4px 8px; font-size:0.8rem; width:auto; min-width:100px;"
                                                        onchange="this.form.submit()">
                                                    <option value="Pending" ${o.status == 'Pending' ? 'selected' : ''}>Pending</option>
                                                    <option value="Processing" ${o.status == 'Processing' ? 'selected' : ''}>Processing</option>
                                                    <option value="Shipped" ${o.status == 'Shipped' ? 'selected' : ''}>Shipped</option>
                                                    <option value="Delivered" ${o.status == 'Delivered' ? 'selected' : ''}>Delivered</option>
                                                    <option value="Cancelled" ${o.status == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                                                </select>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/mod/orders" method="POST"
                                                  id="mod-del-order-${o.orderId}" style="display:inline;">
                                                <input type="hidden" name="action" value="deleteOrder">
                                                <input type="hidden" name="id" value="${o.orderId}">
                                                <button type="button" class="btn btn-sm btn-danger"
                                                        onclick="confirmDelete('Delete order #${o.orderId}?', 'mod-del-order-${o.orderId}')">
                                                    🗑️
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
