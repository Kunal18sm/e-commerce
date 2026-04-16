<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="${product.name}" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="container page-wrapper">

    <div class="mb-2">
        <a href="${pageContext.request.contextPath}/products" style="color:var(--text-secondary); font-size:0.9rem;">
            ← Back to Products
        </a>
    </div>

    <c:if test="${not empty product}">
        <c:set var="imageValue" value="${product.image}" />
        <c:set var="hasImage" value="${not empty imageValue}" />
        <c:if test="${hasImage}">
            <c:choose>
                <c:when test="${fn:startsWith(imageValue, 'http://') || fn:startsWith(imageValue, 'https://') || fn:startsWith(imageValue, '/')}">
                    <c:set var="imageSrc" value="${imageValue}" />
                </c:when>
                <c:otherwise>
                    <c:set var="imageSrc" value="${pageContext.request.contextPath}/uploads/${imageValue}" />
                </c:otherwise>
            </c:choose>
        </c:if>
        <div class="product-detail-layout animate-fadeIn">
            <!-- Product Image -->
            <c:choose>
                <c:when test="${hasImage}">
                    <div class="product-detail-image">
                        <img src="${imageSrc}" alt="${product.name}"
                             onerror="this.parentElement.style.display='none'; this.parentElement.nextElementSibling.style.display='flex';">
                    </div>
                    <div class="product-detail-image-placeholder cat-${product.category == 'Electronics' ? 'electronics' : 
                        product.category == 'Clothing' ? 'clothing' : 
                        product.category == 'Sports' ? 'sports' : 
                        product.category == 'Home & Kitchen' ? 'home' : 
                        product.category == 'Books' ? 'books' : 'general'}"
                         style="display:none;">
                        <c:choose>
                            <c:when test="${product.category == 'Electronics'}">💻</c:when>
                            <c:when test="${product.category == 'Clothing'}">👕</c:when>
                            <c:when test="${product.category == 'Sports'}">⚽</c:when>
                            <c:when test="${product.category == 'Home & Kitchen'}">🏠</c:when>
                            <c:when test="${product.category == 'Books'}">📚</c:when>
                            <c:otherwise>📦</c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="product-detail-image-placeholder cat-${product.category == 'Electronics' ? 'electronics' : 
                        product.category == 'Clothing' ? 'clothing' : 
                        product.category == 'Sports' ? 'sports' : 
                        product.category == 'Home & Kitchen' ? 'home' : 
                        product.category == 'Books' ? 'books' : 'general'}">
                        <c:choose>
                            <c:when test="${product.category == 'Electronics'}">💻</c:when>
                            <c:when test="${product.category == 'Clothing'}">👕</c:when>
                            <c:when test="${product.category == 'Sports'}">⚽</c:when>
                            <c:when test="${product.category == 'Home & Kitchen'}">🏠</c:when>
                            <c:when test="${product.category == 'Books'}">📚</c:when>
                            <c:otherwise>📦</c:otherwise>
                        </c:choose>
                    </div>
                </c:otherwise>
            </c:choose>

            <!-- Product Info -->
            <div class="product-detail-info">
                <span class="detail-category">${product.category}</span>
                <h1>${product.name}</h1>
                <div class="detail-price">
                    ₹<fmt:formatNumber value="${product.price}" pattern="#,##0.00"/>
                </div>

                <p class="detail-desc">${product.description}</p>

                <c:if test="${not empty loggedUser && loggedUser.utype == 'user'}">
                    <form action="${pageContext.request.contextPath}/cart" method="POST" style="display:flex; align-items:center; gap:16px; flex-wrap:wrap;">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="productId" value="${product.productId}">
                        <div class="form-group" style="margin-bottom:0;">
                            <label for="quantity" style="font-size:0.85rem;">Quantity</label>
                            <select name="quantity" id="quantity" class="form-control" style="width:auto; min-width:80px;">
                                <c:forEach begin="1" end="10" var="i">
                                    <option value="${i}">${i}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary btn-lg" id="add-to-cart-btn" style="margin-top:18px;">
                            🛒 Add to Cart
                        </button>
                    </form>
                </c:if>
                <c:if test="${empty loggedUser}">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">
                        🔑 Login to Purchase
                    </a>
                </c:if>

                <!-- Product Details Card -->
                <div class="card mt-3">
                    <div class="card-header">📋 Product Details</div>
                    <div class="card-body">
                        <table style="width:100%;">
                            <tr>
                                <td style="padding:8px 0; color:var(--text-secondary); width:140px;">Product ID</td>
                                <td style="padding:8px 0; font-weight:600;">#${product.productId}</td>
                            </tr>
                            <tr>
                                <td style="padding:8px 0; color:var(--text-secondary);">Category</td>
                                <td style="padding:8px 0; font-weight:600;">${product.category}</td>
                            </tr>
                            <tr>
                                <td style="padding:8px 0; color:var(--text-secondary);">Price</td>
                                <td style="padding:8px 0; font-weight:600;">₹<fmt:formatNumber value="${product.price}" pattern="#,##0.00"/></td>
                            </tr>
                            <tr>
                                <td style="padding:8px 0; color:var(--text-secondary);">Availability</td>
                                <td style="padding:8px 0;"><span class="badge badge-success">In Stock</span></td>
                            </tr>
                            <tr>
                                <td style="padding:8px 0; color:var(--text-secondary);">Delivery</td>
                                <td style="padding:8px 0; font-weight:600;">🚚 Home Delivery Available</td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <c:if test="${empty product}">
        <div class="empty-state">
            <div class="empty-icon">😕</div>
            <h3>Product Not Found</h3>
            <p>The product you're looking for doesn't exist or has been removed.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Browse Products</a>
        </div>
    </c:if>
</div>

<jsp:include page="/includes/footer.jsp"/>
