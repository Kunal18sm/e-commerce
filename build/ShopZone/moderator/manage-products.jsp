<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Manage Products" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-section">
            <div class="sidebar-label">Moderator Panel</div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/mod/dashboard"><span class="nav-icon">📊</span> Dashboard</a>
                <a href="${pageContext.request.contextPath}/mod/products" class="active"><span class="nav-icon">📦</span> Products</a>
                <a href="${pageContext.request.contextPath}/mod/users"><span class="nav-icon">👥</span> Users</a>
                <a href="${pageContext.request.contextPath}/mod/orders"><span class="nav-icon">🛒</span> Orders</a>
            </nav>
        </div>
    </aside>

    <div class="dashboard-content">
        <div class="flex-between mb-3">
            <div>
                <h1>📦 Manage Products</h1>
                <p class="text-muted">Add, search, and manage products</p>
            </div>
            <button class="btn btn-primary" onclick="ModalManager.open('addProductModal')">➕ Add Product</button>
        </div>

        <form action="${pageContext.request.contextPath}/mod/products" method="GET" class="search-bar mb-3" style="max-width:400px;">
            <span class="search-icon">🔍</span>
            <input type="text" name="q" placeholder="Search products..." value="${searchQuery}">
            <button type="submit">→</button>
        </form>

        <div class="table-wrapper">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Product</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Added</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty products}">
                            <tr><td colspan="6" class="text-center text-muted" style="padding:40px;">No products found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td><strong>#${p.productId}</strong></td>
                                    <td>
                                        <div style="display:flex; align-items:center; gap:10px;">
                                            <div style="width:40px; height:40px; border-radius:8px; display:flex; align-items:center; justify-content:center; font-size:1.2rem; color:white;"
                                                 class="cat-${p.category == 'Electronics' ? 'electronics' : p.category == 'Clothing' ? 'clothing' : p.category == 'Sports' ? 'sports' : p.category == 'Home & Kitchen' ? 'home' : p.category == 'Books' ? 'books' : 'general'}">
                                                ${p.initial}
                                            </div>
                                            <div style="font-weight:600;">${p.name}</div>
                                        </div>
                                    </td>
                                    <td><span class="badge badge-primary">${p.category}</span></td>
                                    <td style="font-weight:600;">₹<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></td>
                                    <td class="text-small text-muted"><fmt:formatDate value="${p.createdAt}" pattern="dd MMM yyyy"/></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/mod/products" method="POST"
                                              id="mod-del-product-${p.productId}" style="display:inline;">
                                            <input type="hidden" name="action" value="deleteProduct">
                                            <input type="hidden" name="id" value="${p.productId}">
                                            <button type="button" class="btn btn-sm btn-danger"
                                                    onclick="confirmDelete('Delete product: ${p.name}?', 'mod-del-product-${p.productId}')">
                                                🗑️ Delete
                                            </button>
                                        </form>
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

<!-- Add Product Modal -->
<div class="modal-overlay" id="addProductModal">
    <div class="modal">
        <div class="modal-header">
            <h3>➕ Add New Product</h3>
            <button class="modal-close" onclick="ModalManager.close('addProductModal')">✕</button>
        </div>
        <form action="${pageContext.request.contextPath}/mod/products" method="POST">
            <div class="modal-body">
                <input type="hidden" name="action" value="addProduct">
                <div class="form-group">
                    <label>Product Name *</label>
                    <input type="text" name="name" class="form-control" required placeholder="Product name">
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Price (₹) *</label>
                        <input type="number" name="price" class="form-control" required step="0.01" min="0" placeholder="0.00">
                    </div>
                    <div class="form-group">
                        <label>Category</label>
                        <select name="category" class="form-control">
                            <option value="Electronics">Electronics</option>
                            <option value="Clothing">Clothing</option>
                            <option value="Sports">Sports</option>
                            <option value="Home & Kitchen">Home & Kitchen</option>
                            <option value="Books">Books</option>
                            <option value="General">General</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label>Image Filename</label>
                    <input type="text" name="image" class="form-control" placeholder="e.g. product.jpg">
                </div>
                <div class="form-group">
                    <label>Description</label>
                    <textarea name="description" class="form-control" rows="3" placeholder="Description..."></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="ModalManager.close('addProductModal')">Cancel</button>
                <button type="submit" class="btn btn-primary">➕ Add Product</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
