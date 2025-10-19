<%@ page import="java.sql.Connection" %>
<%@ page import="webapp.database.DatabaseConnection" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>

<%@ page import="static webapp.database.VehicleRentalSystem.*" %>

<%
    Connection connection = DatabaseConnection.getConnection();
    List<Map<String, String>> rents = fetchRentsDuration(connection);
%>

<!DOCTYPE html>
<html>
<head>
    <title>Rent Duration Display</title>
    <!-- Include Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
    <style>
        body {
            background-color: #f8f9fa;
            padding: 20px;
        }

        table {
            width: 100%;
            margin-bottom: 1rem;
            color: #212529;
        }

        th,
        td {
            padding: 1rem;
            vertical-align: top;
            border-top: 1px solid #dee2e6;
        }

        th {
            background-color: #007bff;
            color: #fff;
        }

        tbody tr:nth-of-type(odd) {
            background-color: rgba(0, 0, 0, 0.05);
        }
    </style>
</head>
<body>

<div class="container mt-3">

    <h1>Rent Duration Display</h1>

    <table class="table">
        <thead>
        <tr>
            <th>Category</th>
            <th>Min Duration</th>
            <th>Max Duration</th>
            <th>Average Duration</th>
        </tr>
        </thead>
        <tbody>
        <%
            // Iterate through the list and display the information in a table
            for (Map<String, String> rent : rents) {
        %>
        <tr>
            <td><%= rent.get("category") %>
            </td>
            <td><%= rent.get("min_duration") %>
            </td>
            <td><%= rent.get("max_duration") %>
            </td>
            <td><%= rent.get("avg_duration") %>
            </td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>

</div>
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

</body>
</html>
