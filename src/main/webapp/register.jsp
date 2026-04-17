<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Create your ShopZone account and start shopping.">
    <title>Register | ShopZone</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" referrerpolicy="no-referrer">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="auth-wrapper">
    <div class="auth-card animate-scaleIn" style="max-width:520px;">
        <div class="auth-header">
            <div class="auth-logo"><i class="fa-solid fa-sparkles" aria-hidden="true"></i></div>
            <h2>Create Account</h2>
            <p>Join ShopZone and start shopping today</p>
        </div>

        <div class="auth-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger"><i class="fa-solid fa-triangle-exclamation" aria-hidden="true"></i> ${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="POST" id="registerForm">
                <div class="form-group">
                    <label for="name">Full Name *</label>
                    <input type="text" id="name" name="name" class="form-control"
                           placeholder="Enter your full name" required value="${name}">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="username">Username *</label>
                        <input type="text" id="username" name="username" class="form-control"
                               placeholder="Choose a username" required value="${username}"
                               minlength="3" autocomplete="username">
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" class="form-control"
                               placeholder="your@email.com" value="${email}">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="password">Password *</label>
                        <input type="password" id="password" name="password" class="form-control"
                               placeholder="Min 4 characters" required minlength="4"
                               autocomplete="new-password">
                    </div>
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password *</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                               placeholder="Re-enter password" required autocomplete="new-password">
                    </div>
                </div>

                <div class="form-group">
                    <label for="mobile">Mobile Number</label>
                    <input type="tel" id="mobile" name="mobile" class="form-control"
                           placeholder="Enter your mobile number" value="${mobile}">
                </div>

                <div class="form-group">
                    <label for="address">Delivery Address</label>
                    <textarea id="address" name="address" class="form-control"
                              placeholder="Enter your delivery address" rows="3">${address}</textarea>
                </div>

                <button type="submit" class="btn btn-primary btn-block btn-lg" id="registerBtn">
                    <i class="fa-solid fa-sparkles" aria-hidden="true"></i> Create Account
                </button>

                <p class="text-center text-small text-muted mt-2">
                    Note: Your account will be activated after admin approval.
                </p>
            </form>
        </div>

        <div class="auth-footer">
            Already have an account?
            <a href="${pageContext.request.contextPath}/login" style="font-weight:600;">Sign In</a>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        var pwd = document.getElementById('password').value;
        var cpwd = document.getElementById('confirmPassword').value;
        if (pwd !== cpwd) {
            e.preventDefault();
            alert('Passwords do not match!');
        }
    });
</script>
</body>
</html>
