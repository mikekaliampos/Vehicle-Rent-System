<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="webapp.database.DatabaseConnection" %>
<%@ page import="webapp.database.InitDatabase" %>
<%@ page import="static webapp.database.VehicleRentalSystem.recordVehicleRental" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Rent Vehicle</title>
    <!-- Include Bootstrap CSS -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container mt-5">
    <h1 class="mb-4">Rent Vehicle</h1>

    <%
        String errorMessage = null;

        // Check if the form is submitted
        if (request.getMethod().equals("POST")) {
            Connection connection = null;
            try {
                // Obtain a database connection
                connection = DatabaseConnection.getConnection();

                // Retrieve form parameters
                int customerId = Integer.parseInt(request.getParameter("customerId"));
                int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
                String rentalStartDate = request.getParameter("rentalStartDate");
                String rentalEndDate = request.getParameter("rentalEndDate");
                String driverLicense = request.getParameter("driverLicense");
                if (driverLicense == null || driverLicense.trim().isEmpty()) {
                    driverLicense = null;
                }
                boolean insurance = request.getParameter("insurance") != null;

                // Validate input data (add your validation logic here)

                // Call the recordVehicleRental method
               recordVehicleRental(connection, customerId, vehicleId,
                        rentalStartDate, rentalEndDate, driverLicense, insurance);

                out.println("Vehicle Rental Recorded Successfully");
            } catch (SQLException e) {
                errorMessage = "Error: " + e.getMessage();
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
    %>

    <form method="post" action="Rent.jsp">
        <% if (errorMessage != null) { %>
        <div class="alert alert-danger" role="alert">
            <%= errorMessage %>
        </div>
        <% } %>

        <div class="form-group">
            <label for="customerId">Customer ID:</label>
            <input type="number" class="form-control" name="customerId" required>
        </div>

        <div class="form-group">
            <label for="vehicleId">Vehicle ID:</label>
            <input type="number" class="form-control" name="vehicleId" required>
        </div>

        <div class="form-group">
            <label for="rentalStartDate">Rental Start Date:</label>
            <input type="text" class="form-control" name="rentalStartDate" placeholder="YYYY-MM-DD HH:MM:SS" required>
        </div>

        <div class="form-group">
            <label for="rentalEndDate">Rental End Date:</label>
            <input type="text" class="form-control" name="rentalEndDate" placeholder="YYYY-MM-DD HH:MM:SS" required>
        </div>

        <div class="form-group">
            <label for="driverLicense">Driver's License:</label>
            <input type="text" class="form-control" name="driverLicense">
        </div>

        <div class="form-check">
            <input type="checkbox" class="form-check-input" name="insurance">
            <label class="form-check-label" for="insurance">Insurance</label>
        </div>

        <button type="submit" class="btn btn-primary mt-3">Record Vehicle Rental</button>
    </form>
</div>

<!-- Include Bootstrap JS and Popper.js -->
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

</body>
</html>
