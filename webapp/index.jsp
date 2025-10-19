<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome</title>
    <!-- Include Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
</head>
<body class="bg-light">

<div class="container text-center mt-5">

    <h1 class="mb-4">Welcome to the Website</h1>

    <div class="row">
        <div class="col-md-6">
            <form action="Customer/CustomerPage.jsp" class="mb-3">
                <button type="submit" class="btn btn-success btn-lg btn-block">Customer</button>
            </form>
        </div>
        <div class="col-md-6">
            <form action="Admin/AdminPage.jsp" class="mb-3">
                <button type="submit" class="btn btn-primary btn-lg btn-block">Admin</button>
            </form>
        </div>
    </div>

</div>

<!-- Include Bootstrap JS and Popper.js -->
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

</body>
</html>
