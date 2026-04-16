<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Manage Users" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-section">
            <div class="sidebar-label">Admin Panel</div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/admin/dashboard"><span class="nav-icon">📊</span> Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/products"><span class="nav-icon">📦</span> Products</a>
                <a href="${pageContext.request.contextPath}/admin/users" class="active"><span class="nav-icon">👥</span> Users</a>
                <a href="${pageContext.request.contextPath}/admin/moderators"><span class="nav-icon">🛡️</span> Moderators</a>
                <a href="${pageContext.request.contextPath}/admin/orders"><span class="nav-icon">🛒</span> Orders</a>
            </nav>
        </div>
    </aside>

    <div class="dashboard-content">
        <div class="flex-between mb-3">
            <div>
                <h1>👥 Manage Users</h1>
                <p class="text-muted">View, add, and manage customer accounts</p>
            </div>
            <button class="btn btn-primary" onclick="ModalManager.open('addUserModal')" id="add-user-btn">
                ➕ Add User
            </button>
        </div>

        <!-- Search -->
        <form action="${pageContext.request.contextPath}/admin/users" method="GET" class="search-bar mb-3" style="max-width:400px;">
            <span class="search-icon">🔍</span>
            <input type="text" name="q" placeholder="Search users..." value="${searchQuery}">
            <button type="submit">→</button>
        </form>

        <!-- Users Table -->
        <div class="table-wrapper">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>User</th>
                        <th>Email</th>
                        <th>Mobile</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty users}">
                            <tr><td colspan="6" class="text-center text-muted" style="padding:40px;">No users found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="u" items="${users}">
                                <tr>
                                    <td><strong>#${u.uid}</strong></td>
                                    <td>
                                        <div style="display:flex; align-items:center; gap:10px;">
                                            <div style="width:36px; height:36px; border-radius:50%; background:linear-gradient(135deg, var(--primary), var(--primary-light)); color:white; display:flex; align-items:center; justify-content:center; font-weight:700; font-size:0.8rem;">
                                                ${u.name != null && u.name.length() > 0 ? u.name.substring(0,1).toUpperCase() : '?'}
                                            </div>
                                            <div>
                                                <div style="font-weight:600;">${u.name}</div>
                                                <div class="text-small text-muted">@${u.uname}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>${u.email}</td>
                                    <td>${u.mobile}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${u.enabled}">
                                                <span class="badge badge-success">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-danger">Disabled</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="table-actions">
                                            <form action="${pageContext.request.contextPath}/admin/users" method="POST" style="display:inline;">
                                                <input type="hidden" name="action" value="toggleUser">
                                                <input type="hidden" name="id" value="${u.uid}">
                                                <button type="submit" class="btn btn-sm ${u.enabled ? 'btn-warning' : 'btn-success'}">
                                                    ${u.enabled ? '🔒 Block' : '🔓 Unblock'}
                                                </button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/users" method="POST"
                                                  id="delete-user-${u.uid}" style="display:inline;">
                                                <input type="hidden" name="action" value="deleteUser">
                                                <input type="hidden" name="id" value="${u.uid}">
                                                <button type="button" class="btn btn-sm btn-danger"
                                                        onclick="confirmDelete('Delete user: ${u.name}?', 'delete-user-${u.uid}')">
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

<!-- Add User Modal -->
<div class="modal-overlay" id="addUserModal">
    <div class="modal">
        <div class="modal-header">
            <h3>➕ Add New User</h3>
            <button class="modal-close" onclick="ModalManager.close('addUserModal')">✕</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/users" method="POST">
            <div class="modal-body">
                <input type="hidden" name="action" value="addUser">
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
                <button type="button" class="btn btn-secondary" onclick="ModalManager.close('addUserModal')">Cancel</button>
                <button type="submit" class="btn btn-primary">➕ Add User</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
