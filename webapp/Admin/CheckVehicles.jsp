<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="webapp.database.DatabaseConnection" %>
<%@ page import="webapp.database.InitDatabase" %>

<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="static webapp.database.VehicleRentalSystem.fetchVehiclesByCategory" %>

<%

   Connection connection = null;
    List<Map<String, String>> vehicles = new java.util.ArrayList<>();
    
    try {
        connection = DatabaseConnection.getConnection();
        vehicles = fetchVehiclesByCategory(connection);
    } catch (Exception e) {
        e.printStackTrace();
        // Fallback - empty list will be used
    } finally {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Rent Display</title>
    <!-- Include Bootstrap CSS -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            padding: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        th, td {
            border: 1px solid #dee2e6;
            padding: 8px;
            text-align: left;
        }

        th {
            background-color: #007bff;
            color: #ffffff;
        }

        tbody tr:nth-child(odd) {
            background-color: #f8f9fa;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Rent Display</h2>

    <table class="table">
        <thead class="thead-light">
        <tr>
            <th>Category</th>
            <th>Status</th>
            <th>Vehicle ID</th>
            <th>Model</th>
            <th>Color</th>
            <th>Range in Kilometres</th>
            <th>Registration Number</th>
        </tr>
        </thead>
        <tbody>
        <%
            for (Map<String, String> vehicle : vehicles) {
        %>
        <tr>
            <td><%= vehicle.get("category") %></td>
            <td><%= vehicle.get("status") %></td>
            <td><%= vehicle.get("vehicle_id") %></td>
            <td><%= vehicle.get("model") %></td>
            <td><%= vehicle.get("color") %></td>
            <td><%= vehicle.get("range_in_km") %></td>
            <td><%= vehicle.get("registration_number") %></td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

<!-- Include Bootstrap JS and Popper.js -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

</body>
</html>
