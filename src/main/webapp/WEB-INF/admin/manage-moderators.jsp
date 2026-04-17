<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Manage Moderators" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-section">
            <div class="sidebar-label">Admin Panel</div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/admin/dashboard"><span class="nav-icon"><i class="fa-solid fa-chart-column" aria-hidden="true"></i></span> Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/products"><span class="nav-icon"><i class="fa-solid fa-box-open" aria-hidden="true"></i></span> Products</a>
                <a href="${pageContext.request.contextPath}/admin/users"><span class="nav-icon"><i class="fa-solid fa-users" aria-hidden="true"></i></span> Users</a>
                <a href="${pageContext.request.contextPath}/admin/moderators" class="active"><span class="nav-icon"><i class="fa-solid fa-user-shield" aria-hidden="true"></i></span> Moderators</a>
                <a href="${pageContext.request.contextPath}/admin/orders"><span class="nav-icon"><i class="fa-solid fa-cart-shopping" aria-hidden="true"></i></span> Orders</a>
            </nav>
        </div>
    </aside>

    <div class="dashboard-content">
        <div class="flex-between mb-3">
            <div>
                <h1><i class="fa-solid fa-user-shield" aria-hidden="true"></i> Manage Moderators</h1>
                <p class="text-muted">Add, search, and manage moderator accounts</p>
            </div>
            <button class="btn btn-primary" onclick="ModalManager.open('addModModal')" id="add-mod-btn">
                <i class="fa-solid fa-plus" aria-hidden="true"></i> Add Moderator
            </button>
        </div>

        <!-- Search -->
        <form action="${pageContext.request.contextPath}/admin/moderators" method="GET" class="search-bar mb-3" style="max-width:400px;">
            <span class="search-icon"><i class="fa-solid fa-magnifying-glass" aria-hidden="true"></i></span>
            <input type="text" name="q" placeholder="Search moderators..." value="${searchQuery}">
            <button type="submit">→</button>
        </form>

        <!-- Moderators Table -->
        <div class="table-wrapper">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Moderator</th>
                        <th>Email</th>
                        <th>Mobile</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty moderators}">
                            <tr><td colspan="6" class="text-center text-muted" style="padding:40px;">No moderators found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="m" items="${moderators}">
                                <tr>
                                    <td><strong>#${m.uid}</strong></td>
                                    <td>
                                        <div style="display:flex; align-items:center; gap:10px;">
                                            <div style="width:36px; height:36px; border-radius:50%; background:linear-gradient(135deg, var(--secondary), var(--secondary-light)); color:white; display:flex; align-items:center; justify-content:center; font-weight:700; font-size:0.8rem;">
                                                ${m.name != null && m.name.length() > 0 ? m.name.substring(0,1).toUpperCase() : '?'}
                                            </div>
                                            <div>
                                                <div style="font-weight:600;">${m.name}</div>
                                                <div class="text-small text-muted">@${m.uname}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>${m.email}</td>
                                    <td>${m.mobile}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${m.enabled}">
                                                <span class="badge badge-success">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-danger">Blocked</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="table-actions">
                                            <form action="${pageContext.request.contextPath}/admin/moderators" method="POST" style="display:inline;">
                                                <input type="hidden" name="action" value="toggleModerator">
                                                <input type="hidden" name="id" value="${m.uid}">
                                                <button type="submit" class="btn btn-sm ${m.enabled ? 'btn-warning' : 'btn-success'}">
                                                    <c:choose>
                                                        <c:when test="${m.enabled}">
                                                            <i class="fa-solid fa-lock" aria-hidden="true"></i> Block
                                                        </c:when>
                                                        <c:otherwise>
                                                            <i class="fa-solid fa-lock-open" aria-hidden="true"></i> Unblock
                                                        </c:otherwise>
                                                    </c:choose>
                                                </button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/moderators" method="POST"
                                                  id="delete-mod-${m.uid}" style="display:inline;">
                                                <input type="hidden" name="action" value="deleteModerator">
                                                <input type="hidden" name="id" value="${m.uid}">
                                                <button type="button" class="btn btn-sm btn-danger"
                                                        onclick="confirmDelete('Delete moderator: ${m.name}?', 'delete-mod-${m.uid}')">
                                                    <i class="fa-solid fa-trash" aria-hidden="true"></i>
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

<!-- Add Moderator Modal -->
<div class="modal-overlay" id="addModModal">
    <div class="modal">
        <div class="modal-header">
            <h3><i class="fa-solid fa-user-shield" aria-hidden="true"></i> Add New Moderator</h3>
            <button class="modal-close" onclick="ModalManager.close('addModModal')"><i class="fa-solid fa-xmark" aria-hidden="true"></i></button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/moderators" method="POST">
            <div class="modal-body">
                <input type="hidden" name="action" value="addModerator">
                <div class="form-row">
                    <div class="form-group">
                        <label>Username *</label>
                        <input type="text" name="username" class="form-control" required placeholder="Username" minlength="3">
                    </div>
                    <div class="form-group">
                        <label>Password *</label>
                        <input type="password" name="password" class="form-control" required placeholder="Password" minlength="4">
                    </div>
                </div>
                <div class="form-group">
                    <label>Full Name *</label>
                    <input type="text" name="name" class="form-control" required placeholder="Full Name">
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" class="form-control" placeholder="Email">
                    </div>
                    <div class="form-group">
                        <label>Mobile</label>
                        <input type="tel" name="mobile" class="form-control" placeholder="Mobile">
                    </div>
                </div>
                <div class="form-group">
                    <label>Address</label>
                    <textarea name="address" class="form-control" rows="2" placeholder="Address"></textarea>
                </div>
                <div class="form-group">
                    <label style="display:flex; align-items:center; gap:8px; cursor:pointer;">
                        <input type="checkbox" name="enabled" value="true" checked> Enable account immediately
                    </label>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="ModalManager.close('addModModal')">Cancel</button>
                <button type="submit" class="btn btn-primary"><i class="fa-solid fa-user-shield" aria-hidden="true"></i> Add Moderator</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
