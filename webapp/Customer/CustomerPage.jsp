<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Customer Page</title>
    <!-- Include Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
</head>
<body class="bg-light">

<div class="container text-center mt-5">

    <h1>Welcome, Customer!</h1>

    <div class="row mt-4">
        <div class="col-md-4 mb-3">
            <form action="Register.jsp">
                <button type="submit" class="btn btn-primary btn-lg btn-block">Register</button>
            </form>
        </div>
        <div class="col-md-4 mb-3">
            <form action="Rent.jsp">
                <button type="submit" class="btn btn-success btn-lg btn-block">Rent</button>
            </form>
        </div>
        <div class="col-md-4 mb-3">
            <form action="Search.jsp">
                <button type="submit" class="btn btn-info btn-lg btn-block">Search</button>
            </form>
        </div>
    </div>

    <div class="row">
        <div class="col-md-6 mb-3">
            <form action="Return.jsp">
                <button type="submit" class="btn btn-warning btn-lg btn-block">Return</button>
            </form>
        </div>
        <div class="col-md-6 mb-3">
            <form action="Damage.jsp">
                <button type="submit" class="btn btn-danger btn-lg btn-block">Damage</button>
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
