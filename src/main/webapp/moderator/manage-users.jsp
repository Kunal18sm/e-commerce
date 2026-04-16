<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Manage Users" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-section">
            <div class="sidebar-label">Moderator Panel</div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/mod/dashboard"><span class="nav-icon">📊</span> Dashboard</a>
                <a href="${pageContext.request.contextPath}/mod/products"><span class="nav-icon">📦</span> Products</a>
                <a href="${pageContext.request.contextPath}/mod/users" class="active"><span class="nav-icon">👥</span> Users</a>
                <a href="${pageContext.request.contextPath}/mod/orders"><span class="nav-icon">🛒</span> Orders</a>
            </nav>
        </div>
    </aside>

    <div class="dashboard-content">
        <div class="page-header">
            <h1>👥 Manage Users</h1>
            <p class="text-muted">View and manage customer accounts</p>
        </div>

        <form action="${pageContext.request.contextPath}/mod/users" method="GET" class="search-bar mb-3" style="max-width:400px;">
            <span class="search-icon">🔍</span>
            <input type="text" name="q" placeholder="Search users..." value="${searchQuery}">
            <button type="submit">→</button>
        </form>

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
                                            <form action="${pageContext.request.contextPath}/mod/users" method="POST" style="display:inline;">
                                                <input type="hidden" name="action" value="toggleUser">
                                                <input type="hidden" name="id" value="${u.uid}">
                                                <button type="submit" class="btn btn-sm ${u.enabled ? 'btn-warning' : 'btn-success'}">
                                                    ${u.enabled ? '🔒 Block' : '🔓 Unblock'}
                                                </button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/mod/users" method="POST"
                                                  id="mod-del-user-${u.uid}" style="display:inline;">
                                                <input type="hidden" name="action" value="deleteUser">
                                                <input type="hidden" name="id" value="${u.uid}">
                                                <button type="button" class="btn btn-sm btn-danger"
                                                        onclick="confirmDelete('Delete user: ${u.name}?', 'mod-del-user-${u.uid}')">
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
