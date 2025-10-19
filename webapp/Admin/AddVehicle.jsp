<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="webapp.database.DatabaseConnection" %>
<%@ page import="webapp.database.InitDatabase" %>
<%@ page import="java.sql.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // Check if the form is submitted
    if (request.getMethod().equals("POST")) {
        String vehicleType = request.getParameter("vehicleType");

        if (request.getMethod().equals("POST")) {
            Connection connection = null;
            try {
                // Obtain a database connection
                connection = DatabaseConnection.getConnection();

                // Call the appropriate function based on the selected vehicleType
                switch (vehicleType) {
                    case "Car":
                        InitDatabase.RegisterCar(request.getParameter("brand"), request.getParameter("model"), request.getParameter("color"),
                                Integer.valueOf(request.getParameter("range_in_km")), request.getParameter("registration_number"),
                                request.getParameter("carType"), Integer.valueOf(request.getParameter("passenger_number")), Integer.valueOf(request.getParameter("daily_rental_cost")),
                                Integer.valueOf(request.getParameter("insurance_cost")), connection);
                        break;
                    case "Bike":
                        InitDatabase.RegisterBike(request.getParameter("brand"), request.getParameter("model"), request.getParameter("color"),
                                Integer.valueOf(request.getParameter("range_in_km")), request.getParameter("registration_number"),
                                Integer.valueOf(request.getParameter("daily_rental_cost")),
                                Integer.valueOf(request.getParameter("insurance_cost")), connection);
                        break;
                    case "Motorcycle":
                        InitDatabase.RegisterMotorcycle(request.getParameter("brand"), request.getParameter("model"), request.getParameter("color"),
                                Integer.valueOf(request.getParameter("range_in_km")), request.getParameter("registration_number"),
                                Integer.valueOf(request.getParameter("daily_rental_cost")),
                                Integer.valueOf(request.getParameter("insurance_cost")), connection);
                        break;
                    case "Scooter":
                        InitDatabase.RegisterScooter(request.getParameter("brand"), request.getParameter("model"), request.getParameter("color"),
                                Integer.valueOf(request.getParameter("range_in_km")), request.getParameter("registration_number"),
                                Integer.valueOf(request.getParameter("daily_rental_cost")),
                                Integer.valueOf(request.getParameter("insurance_cost")), connection);
                        break;
                }

                out.println("Vehicle Registered Successfully");
            } catch (SQLException e) {
                out.println("Error: " + e.getMessage());
            } finally {
                // Close the database connection
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Register Vehicle</title>
    <!-- Include Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
</head>
<body class="bg-light">

<div class="container mt-5">

    <h1 class="mb-4">Register Vehicle</h1>

    <form method="post" action="AddVehicle.jsp">
        <div class="form-group">
            <label for="vehicleType">Select Vehicle Type:</label>
            <select class="form-control" name="vehicleType" required>
                <option value="Motorcycle">Motorcycle</option>
                <option value="Bike">Bike</option>
                <option value="Car">Car</option>
                <option value="Scooter">Scooter</option>
            </select>
        </div>

        <!-- Common vehicle fields -->
        <div class="form-group">
            <label for="brand">Brand:</label>
            <input type="text" class="form-control" name="brand" required>
        </div>
        <div class="form-group">
            <label for="model">Model:</label>
            <input type="text" class="form-control" name="model" required>
        </div>
        <div class="form-group">
            <label for="color">Color:</label>
            <input type="text" class="form-control" name="color" required>
        </div>
        <div class="form-group">
            <label for="range_in_km">Range:</label>
            <input type="text" class="form-control" name="range_in_km" required>
        </div>
        <div class="form-group">
            <label for="registration_number">Registration Number:</label>
            <input type="text" class="form-control" name="registration_number" required>
        </div>


        <!-- Vehicle type-specific fields -->
        <div id="carFields" style="display: none;">
            <div class="form-group">
                <label for="carType">Car Type:</label>
                <input type="text" class="form-control" name="carType">
            </div>
            <div class="form-group">
                <label for="passenger_number">Passenger Number:</label>
                <input type="text" class="form-control" name="passenger_number">
            </div>
        </div>

        <div class="form-group">
            <label for="daily_rental_cost">Daily Rental Cost:</label>
            <input type="text" class="form-control" name="daily_rental_cost">
        </div>

        <div class="form-group">
            <label for="insurance_cost">Insurance Cost:</label>
            <input type="text" class="form-control" name="insurance_cost">
        </div>

        <input type="submit" class="btn btn-primary" value="Register Vehicle">
    </form>

</div>

<!-- Include Bootstrap JS and Popper.js -->
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

<script>
    document.querySelector('select[name="vehicleType"]').addEventListener('change', function () {
        let selectedType = this.value;

        // Hide all type-specific fields
        document.querySelectorAll('[id$="Fields"]').forEach(function (element) {
            element.style.display = 'none';
        });

        // Show fields specific to the selected type
        document.getElementById(selectedType.toLowerCase() + 'Fields').style.display = 'block';
    });
</script>

</body>
</html>


