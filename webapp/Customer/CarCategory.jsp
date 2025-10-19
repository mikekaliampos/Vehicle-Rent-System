<%@ page import="java.sql.Connection" %>
<%@ page import="webapp.database.DatabaseConnection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="static webapp.database.VehicleRentalSystem.*" %>

<%
   Connection connection = null;
    List<Map<String, String>> vehicles = new java.util.ArrayList<>();
    
    try {
        connection = DatabaseConnection.getConnection();
        vehicles = fetchAvailableCars(connection);
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
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>

<div class="container mt-5">
    <table class="table">
        <thead>
        <tr>
            <th>Category</th>
            <th>Status</th>
            <th>Vehicle id</th>
            <th>Model</th>
            <th>Color</th>
            <th>Range in kilometres</th>
            <th>Registration</th>
        </tr>
        </thead>
        <tbody>
        <%
            for (Map<String, String> vehicle : vehicles) {
        %>
        <tr>
            <td><%= vehicle.get("category") %>
            </td>
            <td><%= vehicle.get("status") %>
            </td>
            <td><%= vehicle.get("vehicle_id") %>
            </td>
            <td><%= vehicle.get("model") %>
            </td>
            <td><%= vehicle.get("color") %>
            </td>
            <td><%= vehicle.get("range_in_km") %>
            </td>
            <td><%= vehicle.get("registration_number") %>
            </td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

<!-- Include Bootstrap JS and Popper.js -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

</body>
</html>
