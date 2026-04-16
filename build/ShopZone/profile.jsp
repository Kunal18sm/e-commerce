<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Profile" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="container page-wrapper">
    <div class="page-header">
        <h1>👤 My Profile</h1>
        <p>Manage your account settings and personal information</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">⚠️ ${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="alert alert-success">✅ ${success}</div>
    </c:if>

    <c:set var="u" value="${profileUser != null ? profileUser : loggedUser}"/>

    <div class="profile-layout">
        <!-- Profile Sidebar -->
        <div class="profile-sidebar">
            <div class="profile-avatar">
                ${u.name != null && u.name.length() > 0 ? u.name.substring(0,1).toUpperCase() : '?'}
            </div>
            <h3>${u.displayName}</h3>
            <span class="badge badge-primary" style="margin:8px 0;">${u.utype}</span>
            <p class="text-small text-muted">@${u.uname}</p>
            <hr style="margin:16px 0; border:none; border-top:1px solid var(--border-color);">
            <p class="text-small text-muted">
                📧 ${not empty u.email ? u.email : 'Not set'}<br>
                📱 ${not empty u.mobile ? u.mobile : 'Not set'}
            </p>
        </div>

        <!-- Profile Forms -->
        <div>
            <!-- Update Profile -->
            <div class="card mb-3">
                <div class="card-header">✏️ Edit Profile</div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/profile" method="POST" id="profileForm">
                        <input type="hidden" name="action" value="updateProfile">

                        <div class="form-group">
                            <label for="profile-name">Full Name *</label>
                            <input type="text" id="profile-name" name="name" class="form-control"
                                   value="${u.name}" required>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="profile-email">Email</label>
                                <input type="email" id="profile-email" name="email" class="form-control"
                                       value="${u.email}">
                            </div>
                            <div class="form-group">
                                <label for="profile-mobile">Mobile</label>
                                <input type="tel" id="profile-mobile" name="mobile" class="form-control"
                                       value="${u.mobile}">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="profile-address">Delivery Address</label>
                            <textarea id="profile-address" name="address" class="form-control" rows="3">${u.address}</textarea>
                        </div>

                        <button type="submit" class="btn btn-primary" id="update-profile-btn">💾 Save Changes</button>
                    </form>
                </div>
            </div>

            <!-- Change Password -->
            <div class="card">
                <div class="card-header">🔒 Change Password</div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/profile" method="POST" id="passwordForm">
                        <input type="hidden" name="action" value="changePassword">

                        <div class="form-group">
                            <label for="currentPassword">Current Password</label>
                            <input type="password" id="currentPassword" name="currentPassword" class="form-control"
                                   required placeholder="Enter current password">
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="newPassword">New Password</label>
                                <input type="password" id="newPassword" name="newPassword" class="form-control"
                                       required placeholder="Min 4 characters" minlength="4">
                            </div>
                            <div class="form-group">
                                <label for="confirmNewPassword">Confirm New Password</label>
                                <input type="password" id="confirmNewPassword" name="confirmNewPassword" class="form-control"
                                       required placeholder="Re-enter new password">
                            </div>
                        </div>

                        <button type="submit" class="btn btn-warning" id="change-password-btn">🔑 Change Password</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.getElementById('passwordForm').addEventListener('submit', function(e) {
        var np = document.getElementById('newPassword').value;
        var cnp = document.getElementById('confirmNewPassword').value;
        if (np !== cnp) {
            e.preventDefault();
            alert('New passwords do not match!');
        }
    });
</script>

<jsp:include page="/includes/footer.jsp"/>
