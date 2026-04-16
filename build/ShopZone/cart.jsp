<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Shopping Cart" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="container page-wrapper">
    <div class="page-header">
        <h1>🛒 Shopping Cart</h1>
        <p>Review your items before checkout</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">⚠️ ${error}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty cartItems}">
            <div class="empty-state">
                <div class="empty-icon">🛒</div>
                <h3>Your Cart is Empty</h3>
                <p>Looks like you haven't added any items to your cart yet.</p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Start Shopping</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="cart-container">
                <!-- Cart Items -->
                <div>
                    <div class="card">
                        <div class="card-header">
                            <span>Cart Items (${cartCount})</span>
                        </div>
                        <c:forEach var="item" items="${cartItems}">
                            <div class="cart-item">
                                <div class="item-image-placeholder cat-${item.category == 'Electronics' ? 'electronics' : 
                                    item.category == 'Clothing' ? 'clothing' : 
                                    item.category == 'Sports' ? 'sports' : 
                                    item.category == 'Home & Kitchen' ? 'home' : 
                                    item.category == 'Books' ? 'books' : 'general'}">
                                    <c:choose>
                                        <c:when test="${item.category == 'Electronics'}">💻</c:when>
                                        <c:when test="${item.category == 'Clothing'}">👕</c:when>
                                        <c:when test="${item.category == 'Sports'}">⚽</c:when>
                                        <c:when test="${item.category == 'Home & Kitchen'}">🏠</c:when>
                                        <c:when test="${item.category == 'Books'}">📚</c:when>
                                        <c:otherwise>📦</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="item-details">
                                    <div class="item-name">${item.name}</div>
                                    <div class="item-category text-muted text-small">${item.category}</div>
                                    <div class="item-price">₹<fmt:formatNumber value="${item.price}" pattern="#,##0.00"/></div>
                                </div>
                                <div class="quantity-control">
                                    <button onclick="updateQuantity(${item.productId}, ${item.cartQuantity - 1})" title="Decrease">−</button>
                                    <span class="qty-value">${item.cartQuantity}</span>
                                    <button onclick="updateQuantity(${item.productId}, ${item.cartQuantity + 1})" title="Increase">+</button>
                                </div>
                                <div style="text-align:right; min-width:100px;">
                                    <div style="font-weight:700; font-size:1.05rem; color:var(--primary-dark);">
                                        ₹<fmt:formatNumber value="${item.price * item.cartQuantity}" pattern="#,##0.00"/>
                                    </div>
                                    <button class="btn btn-sm" style="color:var(--danger); background:none; padding:4px 8px; font-size:0.8rem;"
                                            onclick="removeFromCart(${item.productId})">
                                        🗑️ Remove
                                    </button>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <!-- Cart Summary -->
                <div class="cart-summary">
                    <h3>Order Summary</h3>

                    <div class="summary-row">
                        <span class="text-muted">Subtotal (${cartCount} items)</span>
                        <span>₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/></span>
                    </div>
                    <div class="summary-row">
                        <span class="text-muted">Delivery</span>
                        <span style="color:var(--success); font-weight:600;">FREE</span>
                    </div>
                    <div class="summary-row total">
                        <span>Total</span>
                        <span>₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/></span>
                    </div>

                    <!-- Checkout Form -->
                    <form action="${pageContext.request.contextPath}/cart" method="POST" class="mt-2">
                        <input type="hidden" name="action" value="checkout">
                        <div class="form-group">
                            <label for="checkout-address">Delivery Address</label>
                            <textarea id="checkout-address" name="address" class="form-control" rows="3"
                                      placeholder="Enter delivery address" required>${loggedUser.address}</textarea>
                        </div>
                        <button type="submit" class="btn btn-success btn-block btn-lg" id="checkout-btn">
                            ✅ Place Order
                        </button>
                    </form>

                    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary btn-block mt-1" style="text-align:center;">
                        ← Continue Shopping
                    </a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/includes/footer.jsp"/>
