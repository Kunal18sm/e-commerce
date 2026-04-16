<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="My Orders" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="container page-wrapper">
    <div class="page-header">
        <h1>📦 My Orders</h1>
        <p>Track your order history and delivery status</p>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-state">
                <div class="empty-icon">📦</div>
                <h3>No Orders Yet</h3>
                <p>You haven't placed any orders yet. Start shopping to see your orders here!</p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Start Shopping</a>
            </div>
        </c:when>
        <c:otherwise>
            <c:forEach var="order" items="${orders}">
                <div class="order-card animate-fadeIn">
                    <div class="order-card-header">
                        <div>
                            <span style="font-weight:700; color:var(--text-primary);">Order #${order.orderId}</span>
                            <span class="text-muted text-small" style="margin-left:12px;">${order.formattedDate}</span>
                        </div>
                        <span class="badge ${order.statusClass}">${order.status}</span>
                    </div>
                    <div class="order-card-body">
                        <div class="order-product-thumb cat-${order.productName != null && order.productName.contains('Headphone') ? 'electronics' : 'general'}"
                             style="background:linear-gradient(135deg, var(--primary), var(--primary-light));">
                            📦
                        </div>
                        <div style="flex:1;">
                            <div style="font-weight:600; margin-bottom:4px;">${order.productName}</div>
                            <div class="text-small text-muted">Qty: ${order.quantity} × ₹<fmt:formatNumber value="${order.productPrice}" pattern="#,##0.00"/></div>
                            <div class="text-small text-muted">📍 ${order.address}</div>
                        </div>
                        <div style="text-align:right;">
                            <div style="font-weight:800; font-size:1.1rem; color:var(--primary-dark);">
                                ₹<fmt:formatNumber value="${order.totalPrice}" pattern="#,##0.00"/>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/includes/footer.jsp"/>
