<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Page</title>
    <!-- Include Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
</head>
<body class="bg-light">

<div class="container text-center mt-5">

    <h1>Welcome, Admin!</h1>

    <div class="row mt-5">
        <div class="col-md-4 mb-3">
            <form action="AddVehicle.jsp">
                <button type="submit" class="btn btn-primary btn-lg btn-block">Add Vehicle</button>
            </form>
        </div>
        <div class="col-md-4 mb-3">
            <form action="CheckVehicles.jsp">
                <button type="submit" class="btn btn-success btn-lg btn-block">Check Vehicles</button>
            </form>
        </div>
        <div class="col-md-4 mb-3">
            <form action="RentStatus.jsp">
                <button type="submit" class="btn btn-success btn-lg btn-block">Rent Status</button>
            </form>
        </div>
        <div class="col-md-4 mb-3">
            <form action="RentDurations.jsp">
                <button type="submit" class="btn btn-primary  btn-lg btn-block">Rent Duration</button>
            </form>
        </div>
    </div>

    <div class="row">
        <div class="col-md-4 mb-3">
            <form action="PopularVehicles.jsp">
                <button type="submit" class="btn btn-warning btn-lg btn-block">Popular Vehicles</button>
            </form>
        </div>
        <div class="col-md-4 mb-3">
            <form action="RepairCost.jsp">
                <button type="submit" class="btn btn-danger btn-lg btn-block">Repair Cost</button>
            </form>
        </div>
        <div class="col-md-4 mb-3">
            <form action="Earnings.jsp">
                <button type="submit" class="btn btn-secondary btn-lg btn-block">Earnings</button>
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
