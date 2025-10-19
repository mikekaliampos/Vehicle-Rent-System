<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="webapp.database.DatabaseConnection" %>
<%@ page import="webapp.database.InitDatabase" %>
<%@ page import="java.util.Vector" %>
<%@ page import="static webapp.database.VehicleRentalSystem.reportDamageAndRepair" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Report Damage/Maintenance</title>
    <!-- Include Bootstrap CSS -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 20px;
        }

        form {
            max-width: 400px;
            margin: 0 auto;
            background-color: #ffffff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        label {
            display: block;
            margin-bottom: 8px;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            background-color: #dc3545;
            color: white;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #c82333;
        }
    </style>
</head>
<body>

<div class="container mt-5">
    <h1>Report Damage/Maintenance</h1>

    <%
        // Check if the form is submitted
        if (request.getMethod().equals("POST")) {
            // Get form parameters
            int rentId = Integer.parseInt(request.getParameter("rentId"));
            String flag = request.getParameter("flag");

            Connection connection = null;
            try {
                // Obtain a database connection
                connection = DatabaseConnection.getConnection();

                // Call the reportDamageAndRepair method
             reportDamageAndRepair(connection, rentId, flag);

                out.println("Damage/Maintenance Reported Successfully");
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
    %>

    <form method="post" action="Damage.jsp">
        <div class="form-group">
            <label for="rentId">Rental ID:</label>
            <input type="number" class="form-control" name="rentId" required>
        </div>

        <div class="form-group">
            <label for="flag">Report Type:</label>
            <select class="form-control" name="flag" required>
                <option value="Damaged">Damage</option>
                <option value="Maintenance">Maintenance</option>
            </select>
        </div>

        <button type="submit" class="btn btn-danger">Report Damage/Maintenance</button>
    </form>
</div>

<!-- Include Bootstrap JS and Popper.js -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

</body>
</html>
