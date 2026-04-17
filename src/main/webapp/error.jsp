<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error | ShopZone</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" referrerpolicy="no-referrer">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="error-page">
    <div>
        <div class="error-code">${pageContext.errorData.statusCode != 0 ? pageContext.errorData.statusCode : '404'}</div>
        <h2 style="margin: 16px 0 8px;">Oops! Something went wrong</h2>
        <p class="text-muted" style="margin-bottom:24px;">
            The page you're looking for might have been removed or is temporarily unavailable.
        </p>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary"><i class="fa-solid fa-house" aria-hidden="true"></i> Go Home</a>
        <a href="javascript:history.back()" class="btn btn-secondary">← Go Back</a>
    </div>
</div>

</body>
</html>
