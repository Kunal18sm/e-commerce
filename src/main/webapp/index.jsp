<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Shop" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="container page-wrapper">

    <!-- Hero Banner -->
    <div class="hero">
        <h1>Discover Premium Products</h1>
        <p>Shop the latest trends in electronics, fashion, sports gear, and more. Fast delivery right to your doorstep.</p>
        <a href="#products" class="btn" style="background:rgba(255,255,255,0.2); color:white; backdrop-filter:blur(10px); border:1px solid rgba(255,255,255,0.3);">
            <i class="fa-solid fa-cart-shopping" aria-hidden="true"></i> Start Shopping
        </a>
    </div>

    <!-- Search & Filter Bar -->
    <div class="flex-between mb-3" style="flex-wrap:wrap; gap:16px;">
        <form action="${pageContext.request.contextPath}/products" method="GET" class="search-bar" style="flex:1; min-width:280px;">
            <span class="search-icon"><i class="fa-solid fa-magnifying-glass" aria-hidden="true"></i></span>
            <input type="text" name="q" placeholder="Search products..."
                   value="${searchQuery}" id="search-input">
            <button type="submit">→</button>
        </form>
    </div>

    <!-- Category Pills -->
    <div class="category-pills">
        <a href="${pageContext.request.contextPath}/products" 
           class="category-pill ${empty selectedCategory ? 'active' : ''}">
            <i class="fa-solid fa-tags" aria-hidden="true"></i> All Products
        </a>
        <c:forEach var="cat" items="${categories}">
            <a href="${pageContext.request.contextPath}/products?cat=${cat}"
               class="category-pill ${selectedCategory == cat ? 'active' : ''}">
                <c:choose>
                    <c:when test="${cat == 'Electronics'}"><i class="fa-solid fa-laptop-code" aria-hidden="true"></i></c:when>
                    <c:when test="${cat == 'Clothing'}"><i class="fa-solid fa-shirt" aria-hidden="true"></i></c:when>
                    <c:when test="${cat == 'Sports'}"><i class="fa-solid fa-futbol" aria-hidden="true"></i></c:when>
                    <c:when test="${cat == 'Home & Kitchen'}"><i class="fa-solid fa-house" aria-hidden="true"></i></c:when>
                    <c:when test="${cat == 'Books'}"><i class="fa-solid fa-book-open" aria-hidden="true"></i></c:when>
                    <c:otherwise><i class="fa-solid fa-box-open" aria-hidden="true"></i></c:otherwise>
                </c:choose>
                ${cat}
            </a>
        </c:forEach>
    </div>

    <!-- Search Results Info -->
    <c:if test="${not empty searchQuery}">
        <div class="flex-between mb-2">
            <p class="text-muted">
                Showing results for "<strong>${searchQuery}</strong>" — ${products.size()} item(s) found
            </p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-sm btn-secondary"><i class="fa-solid fa-xmark" aria-hidden="true"></i> Clear Search</a>
        </div>
    </c:if>

    <!-- Product Grid -->
    <div id="products" class="product-grid">
        <c:choose>
            <c:when test="${empty products}">
                <div class="empty-state" style="grid-column: 1 / -1;">
                    <div class="empty-icon"><i class="fa-solid fa-magnifying-glass" aria-hidden="true"></i></div>
                    <h3>No Products Found</h3>
                    <p>Try a different search term or browse all categories.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">View All Products</a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="product" items="${products}">
                    <div class="product-card">
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
                        <a href="${pageContext.request.contextPath}/products?id=${product.productId}">
                            <c:choose>
                                <c:when test="${hasImage}">
                                    <div class="product-image">
                                        <img src="${imageSrc}" alt="${product.name}"
                                             onerror="this.parentElement.style.display='none'; this.parentElement.nextElementSibling.style.display='flex';">
                                    </div>
                                    <div class="product-image-placeholder cat-${product.category == 'Electronics' ? 'electronics' : 
                                        product.category == 'Clothing' ? 'clothing' : 
                                        product.category == 'Sports' ? 'sports' : 
                                        product.category == 'Home & Kitchen' ? 'home' : 
                                        product.category == 'Books' ? 'books' : 'general'}"
                                         style="display:none;">
                                        <c:choose>
                                            <c:when test="${product.category == 'Electronics'}"><i class="fa-solid fa-laptop-code" aria-hidden="true"></i></c:when>
                                            <c:when test="${product.category == 'Clothing'}"><i class="fa-solid fa-shirt" aria-hidden="true"></i></c:when>
                                            <c:when test="${product.category == 'Sports'}"><i class="fa-solid fa-futbol" aria-hidden="true"></i></c:when>
                                            <c:when test="${product.category == 'Home & Kitchen'}"><i class="fa-solid fa-house" aria-hidden="true"></i></c:when>
                                            <c:when test="${product.category == 'Books'}"><i class="fa-solid fa-book-open" aria-hidden="true"></i></c:when>
                                            <c:otherwise><i class="fa-solid fa-box-open" aria-hidden="true"></i></c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="product-image-placeholder cat-${product.category == 'Electronics' ? 'electronics' : 
                                        product.category == 'Clothing' ? 'clothing' : 
                                        product.category == 'Sports' ? 'sports' : 
                                        product.category == 'Home & Kitchen' ? 'home' : 
                                        product.category == 'Books' ? 'books' : 'general'}">
                                        <c:choose>
                                            <c:when test="${product.category == 'Electronics'}"><i class="fa-solid fa-laptop-code" aria-hidden="true"></i></c:when>
                                            <c:when test="${product.category == 'Clothing'}"><i class="fa-solid fa-shirt" aria-hidden="true"></i></c:when>
                                            <c:when test="${product.category == 'Sports'}"><i class="fa-solid fa-futbol" aria-hidden="true"></i></c:when>
                                            <c:when test="${product.category == 'Home & Kitchen'}"><i class="fa-solid fa-house" aria-hidden="true"></i></c:when>
                                            <c:when test="${product.category == 'Books'}"><i class="fa-solid fa-book-open" aria-hidden="true"></i></c:when>
                                            <c:otherwise><i class="fa-solid fa-box-open" aria-hidden="true"></i></c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </a>
                        <div class="product-info">
                            <span class="product-category">${product.category}</span>
                            <a href="${pageContext.request.contextPath}/products?id=${product.productId}" style="color:inherit;">
                                <h3 class="product-name">${product.name}</h3>
                            </a>
                            <p class="product-desc">${product.description}</p>
                            <div class="product-footer">
                                <span class="product-price">
                                    ₹<fmt:formatNumber value="${product.price}" pattern="#,##0.00"/>
                                </span>
                                <c:if test="${not empty loggedUser && loggedUser.utype == 'user'}">
                                    <button class="btn btn-primary btn-sm" onclick="addToCart(${product.productId})" id="add-cart-${product.productId}">
                                        <i class="fa-solid fa-cart-shopping" aria-hidden="true"></i> Add
                                    </button>
                                </c:if>
                                <c:if test="${empty loggedUser}">
                                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline btn-sm">
                                        <i class="fa-solid fa-key" aria-hidden="true"></i> Login to Buy
                                    </a>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

</div>

<jsp:include page="/includes/footer.jsp"/>
