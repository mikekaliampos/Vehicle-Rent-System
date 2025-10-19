<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="webapp.database.DatabaseConnection" %>
<%@ page import="webapp.database.InitDatabase" %>
<%@ page import="static webapp.database.VehicleRentalSystem.returnRentedVehicle" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Return Vehicle</title>
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

        input {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            background-color: #007bff;
            color: white;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<div class="container mt-5">
    <h1>Return Vehicle</h1>

    <%
        // Check if the form is submitted
        if (request.getMethod().equals("POST")) {
            // Get form parameters
            int rentalId = Integer.parseInt(request.getParameter("rentalId"));

            // Get the current time and format it
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String returnTime = currentDateTime.format(formatter);

            Connection connection = null;
            try {
                // Obtain a database connection
                connection = DatabaseConnection.getConnection();

                // Call the returnRentedVehicle method
               returnRentedVehicle(connection, rentalId, returnTime);

                out.println("Vehicle Returned Successfully");
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

    <form method="post" action="Return.jsp">
        <div class="form-group">
            <label for="rentalId">Rental ID:</label>
            <input type="number" class="form-control" name="rentalId" required>
        </div>

        <!-- Declare the formatter variable -->
        <% DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); %>

        <!-- Display the current time -->
        <p class="mb-3">Current Time: <%= LocalDateTime.now().format(formatter) %></p>

        <!-- Use a hidden input field to submit the current time along with the form -->
        <input type="hidden" name="returnTime" value="<%= LocalDateTime.now().format(formatter) %>">

        <button type="submit" class="btn btn-primary">Return Vehicle</button>
    </form>
</div>

<!-- Include Bootstrap JS and Popper.js -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

</body>
</html>
