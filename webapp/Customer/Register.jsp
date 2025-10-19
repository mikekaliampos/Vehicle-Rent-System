<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="webapp.database.DatabaseConnection" %>
<%@ page import="webapp.database.InitDatabase" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Customer Registration</title>
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

        .error-message {
            color: red;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>

<%
    String errorMessage = null;

    if (request.getMethod().equals("POST")) {
        Connection connection = null;
        try {
            // Obtain a database connection
            connection = DatabaseConnection.getConnection();

            // Retrieve form parameters
            String name = request.getParameter("name");
            String address = request.getParameter("address");
            String dateOfBirth = request.getParameter("date_of_birth");
            String driverLicense = request.getParameter("driver_license");
            String cardDetails = request.getParameter("card_details");

            // Call the RegisterCustomer method
            InitDatabase.RegisterCustomer(name, address, dateOfBirth, driverLicense, cardDetails, connection);

            out.println("User Registered Successfully");
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

<div class="container mt-5">
    <form method="post" action="Register.jsp">
        <% if (errorMessage != null) { %>
        <div class="alert alert-danger" role="alert">
            <%= errorMessage %>
        </div>
        <% } %>
        <div class="form-group">
            <label for="name">Name:</label>
            <input type="text" class="form-control" name="name" required>
        </div>

        <div class="form-group">
            <label for="address">Address:</label>
            <input type="text" class="form-control" name="address" required>
        </div>

        <div class="form-group">
            <label for="date_of_birth">Date of Birth:</label>
            <input type="text" class="form-control" name="date_of_birth" required>
        </div>

        <div class="form-group">
            <label for="driver_license">Driver's License:</label>
            <input type="text" class="form-control" name="driver_license" required>
        </div>

        <div class="form-group">
            <label for="card_details">Card Details:</label>
            <input type="text" class="form-control" name="card_details" required>
        </div>

        <button type="submit" class="btn btn-primary">Register</button>
    </form>
</div>

<!-- Include Bootstrap JS and Popper.js -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

</body>
</html>
