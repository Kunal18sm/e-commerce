<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Login to ShopZone - Your premium online shopping destination.">
    <title>Login | ShopZone</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" referrerpolicy="no-referrer">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="auth-wrapper">
    <div class="auth-card animate-scaleIn">
        <div class="auth-header">
            <div class="auth-logo"><i class="fa-solid fa-bag-shopping" aria-hidden="true"></i></div>
            <h2>Welcome Back</h2>
            <p>Sign in to your ShopZone account</p>
        </div>

        <div class="auth-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger"><i class="fa-solid fa-triangle-exclamation" aria-hidden="true"></i> ${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success"><i class="fa-solid fa-circle-check" aria-hidden="true"></i> ${success}</div>
            </c:if>
            <c:if test="${param.msg == 'loggedout'}">
                <div class="alert alert-info"><i class="fa-solid fa-circle-info" aria-hidden="true"></i> You have been logged out successfully.</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="POST" id="loginForm">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" class="form-control"
                           placeholder="Enter your username" required
                           value="${username}" autocomplete="username">
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="Enter your password" required autocomplete="current-password">
                </div>

                <button type="submit" class="btn btn-primary btn-block btn-lg" id="loginBtn">
                    <i class="fa-solid fa-key" aria-hidden="true"></i> Sign In
                </button>
            </form>
        </div>

        <div class="auth-footer">
            Don't have an account?
            <a href="${pageContext.request.contextPath}/register" style="font-weight:600;">Create Account</a>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
