<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="webapp.database.DatabaseConnection" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="static webapp.database.VehicleRentalSystem.fetchRentsByDate" %>



<%

    Connection connection = DatabaseConnection.getConnection();
    List<Map<String, String>> rents = fetchRentsByDate(connection);


%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Rental Information</title>
    <!-- Include Bootstrap CSS -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            padding: 20px;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Rental Information</h2>

    <table class="table table-bordered">
        <thead>
        <tr>
            <th>Date of Rent</th>
            <th>Date of Return</th>
            <th>Rent ID</th>
            <th>Customer ID</th>
            <th>Rent Duration</th>
            <th>Total Cost</th>
            <th>Insurance</th>
            <th>Driver's License</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <% for (Map<String, String> rent : rents) { %>
        <tr>
            <td><%= rent.get("date_of_rent") %>
            </td>
            <td><%= rent.get("date_of_return") %>
            </td>
            <td><%= rent.get("rent_id") %>
            </td>
            <td><%= rent.get("customer_id") %>
            </td>
            <td><%= rent.get("rent_duration") %>
            </td>
            <td><%= rent.get("total_cost") %>
            </td>
            <td><%= rent.get("insurance") %>
            </td>
            <td><%= rent.get("driver_license") %>
            </td>
            <td><%= rent.get("status") %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>

</div>
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

</body>
</html>
