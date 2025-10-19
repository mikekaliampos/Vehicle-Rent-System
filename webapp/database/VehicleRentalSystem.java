
package webapp.database;


import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VehicleRentalSystem {


    private static boolean isDuplicateRent(Connection connection, int vehicleId, String driverLicense)
            throws SQLException {
        String query = "SELECT COUNT(*) FROM Rent WHERE status = 'Active' AND (vehicle_id = ? OR driver_license = ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, vehicleId);
            preparedStatement.setString(2, driverLicense);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    public static void recordVehicleRental(Connection connection, int customerId, int vehicleId,
                                           String rentalStartDate, String rentalEndDate,
                                           String DriverLicense, boolean insurance) throws SQLException {

        Map<String, String> vehicleDetails = fetchVehicleDetails(connection, vehicleId);
        Map<String, String> customerDetails = fetchCustomerDetails(connection, customerId);
        boolean ageAndLicenseCheck;
        if (DriverLicense == null) {
            ageAndLicenseCheck = checkAgeAndLicenseRequirements(vehicleDetails.get("category"), customerDetails.get("date_of_birth"), customerDetails.get("driver_license"));
            DriverLicense = customerDetails.get("driver_license");
        } else
            ageAndLicenseCheck = checkAgeAndLicenseRequirements(vehicleDetails.get("category"), customerDetails.get("date_of_birth"), DriverLicense);

        if (isDuplicateRent(connection, vehicleId, DriverLicense)) {
            System.out.println("Cannot insert duplicate vehicle_id with status Active");
            return;
        }
        if (ageAndLicenseCheck) {
            String insertRentQuery = "INSERT INTO Rent (customer_id, vehicle_id, date_of_rent, date_of_return,rent_duration,insurance, total_cost, driver_license, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active')";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertRentQuery)) {

                // Calculate rent duration in days
                int rentDuration = calculateRentDuration(rentalStartDate, rentalEndDate);
                // Fetch daily_rental_cost from the respective vehicle type table based on vehicle_id
                double rentCost = fetchDailyRentalCost(connection, vehicleId);
                double totalCost;
                if (insurance)
                    totalCost = (rentCost * rentDuration) + fetchInsuranceCost(connection, vehicleId);
                else
                    totalCost = rentCost * rentDuration;

                // Insert the rental record into the Rent table
                preparedStatement.setInt(1, customerId);
                preparedStatement.setInt(2, vehicleId);
                preparedStatement.setString(3, rentalStartDate);
                preparedStatement.setString(4, rentalEndDate);
                preparedStatement.setInt(5, rentDuration);
                preparedStatement.setBoolean(6, insurance);
                preparedStatement.setDouble(7, totalCost);
                preparedStatement.setString(8, DriverLicense);

                preparedStatement.executeUpdate();

                // Update the status of the rented vehicle in the Vehicle table to 'Rented'
                updateVehicleStatus(connection, vehicleId, "Rented");
            }
        } else {
            System.out.println("Customer does not meet age and/or driver's license requirements for the selected vehicle");
        }
    }

    private static Map<String, String> fetchVehicleDetails(Connection connection, int vehicleId) throws SQLException {
        // Fetch vehicle details including type from the Vehicle table
        String selectVehicleQuery = "SELECT * FROM Vehicle WHERE vehicle_id = ?";
        return getStringStringMap(connection, vehicleId, selectVehicleQuery);
    }

    public static Map<String, String> fetchCustomerDetails(Connection connection, int customerId) throws SQLException {
        // Fetch all customer details from the customer_id
        String selectCustomerQuery = "SELECT * FROM Customer WHERE customer_id = ?";

        return getStringStringMap(connection, customerId, selectCustomerQuery);
    }

    private static Map<String, String> getStringStringMap(Connection connection, int customerId, String selectCustomerQuery) throws SQLException {
        Map<String, String> map = new HashMap<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectCustomerQuery)) {
            preparedStatement.setInt(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();

                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = resultSet.getString(i);
                        map.put(columnName, columnValue);
                    }
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        System.out.println("Column Name: " + entry.getKey() + ", Column Value: " + entry.getValue());
                    }
                }
                return map;

            }
        }
    }

    private static boolean checkAgeAndLicenseRequirements(String vehicleDetails, String customerAge, String
            driverLicense) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateOfBirth = LocalDate.parse(customerAge, formatter);

        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(dateOfBirth, currentDate);

        if ("Car".equals(vehicleDetails) || "Motorcycle".equals(vehicleDetails)) {
            return age.getYears() >= 18 && driverLicense != null;
        } else if ("Bike".equals(vehicleDetails) || "Scooter".equals(vehicleDetails)) {
            return age.getYears() >= 16;
        } else {
            return false; // Handle other vehicle types as needed
        }
    }

    private static double fetchDailyRentalCost(Connection connection, int vehicleId) throws SQLException {
        String selectDailyRentalCostQuery =
                "SELECT " +
                        "CASE " +
                        "WHEN v.category = 'Car' THEN c.daily_rental_cost " +
                        "WHEN v.category = 'Bike' THEN b.daily_rental_cost " +
                        "WHEN v.category = 'Scooter' THEN s.daily_rental_cost " +
                        "WHEN v.category = 'Motorcycle' THEN m.daily_rental_cost " +
                        "END AS daily_rental_cost " +
                        "FROM Vehicle v " +
                        "LEFT JOIN Car c ON v.vehicle_id = c.vehicle_id " +
                        "LEFT JOIN Bike b ON v.vehicle_id = b.vehicle_id " +
                        "LEFT JOIN Scooter s ON v.vehicle_id = s.vehicle_id " +
                        "LEFT JOIN Motorcycle m ON v.vehicle_id = m.vehicle_id " +
                        "WHERE v.vehicle_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectDailyRentalCostQuery)) {
            preparedStatement.setInt(1, vehicleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("daily_rental_cost");
                } else {
                    throw new SQLException("Vehicle not found with ID: " + vehicleId);
                }
            }
        }
    }

    private static double fetchInsuranceCost(Connection connection, int vehicleId) throws SQLException {
        String selectInsuranceCostQuery =
                "SELECT " +
                        "CASE " +
                        "WHEN v.category = 'Car' THEN c.insurance_cost " +
                        "WHEN v.category = 'Bike' THEN b.insurance_cost " +
                        "WHEN v.category = 'Scooter' THEN s.insurance_cost " +
                        "WHEN v.category = 'Motorcycle' THEN m.insurance_cost " +
                        "END AS insurance_cost " +
                        "FROM Vehicle v " +
                        "LEFT JOIN Car c ON v.vehicle_id = c.vehicle_id " +
                        "LEFT JOIN Bike b ON v.vehicle_id = b.vehicle_id " +
                        "LEFT JOIN Scooter s ON v.vehicle_id = s.vehicle_id " +
                        "LEFT JOIN Motorcycle m ON v.vehicle_id = m.vehicle_id " +
                        "WHERE v.vehicle_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectInsuranceCostQuery)) {
            preparedStatement.setInt(1, vehicleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("insurance_cost");
                } else {
                    throw new SQLException("Vehicle not found with ID: " + vehicleId);
                }
            }
        }
    }


    private static int calculateRentDuration(String rentalStartDate, String rentalEndDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date startDate = dateFormat.parse(rentalStartDate);
            java.util.Date endDate = dateFormat.parse(rentalEndDate);

            // Calculate duration in days
            long durationInMillis = endDate.getTime() - startDate.getTime();
            return (int) (durationInMillis / (24 * 60 * 60 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 in case of error
        }
    }

    public static void returnRentedVehicle(Connection connection, int rentalId, String returnTime) throws SQLException {
        // Using a prepared statement to avoid SQL injection
        System.out.println("Return Of vehicle");
        String updateRentQuery = "UPDATE Rent SET total_cost =?,date_of_return = ?,  status = ? WHERE rent_id = ?";
        String selectRentQuery = "SELECT rent_duration,vehicle_id, date_of_rent,date_of_return, total_cost FROM Rent WHERE rent_id = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectRentQuery);
             PreparedStatement updateStatement = connection.prepareStatement(updateRentQuery)) {

            // Fetch rent details
            selectStatement.setInt(1, rentalId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {

                    String rentalStartDate = resultSet.getString("date_of_rent");
                    String rentalEndDate = resultSet.getString("date_of_return");
                    double totalCost = resultSet.getDouble("total_cost");

                    // Calculate additional charges for late return
                    double additionalCharges = calculateAdditionalCharges(rentalStartDate, rentalEndDate, returnTime);

                    // Update return time, status, and total cost in Rent table
                    updateStatement.setDouble(1, totalCost + additionalCharges);
                    updateStatement.setString(2, returnTime);
                    updateStatement.setString(3, "Completed");
                    updateStatement.setInt(4, rentalId);

                    updateStatement.executeUpdate();
                    updateVehicleStatus(connection, resultSet.getInt("vehicle_id"), "Available");

                    System.out.println("Vehicle returned successfully!");
                    System.out.println("Additional charges: $" + additionalCharges);

                } else {
                    System.out.println("Rental not found with ID: " + rentalId);
                }
            }
        }
    }

    private static double calculateAdditionalCharges(String rentalStartDate, String rentalEndDate, String returnTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date startDate = dateFormat.parse(rentalStartDate);
            java.util.Date endTime = dateFormat.parse(rentalEndDate);
            java.util.Date returnDate = dateFormat.parse(returnTime);

            // Calculate the duration in hours
            long durationInMillis = endTime.getTime() - startDate.getTime();
            int durationInHours = (int) (durationInMillis / (60 * 60 * 1000));

            long returnInMillis = returnDate.getTime() - startDate.getTime();
            int returnInHours = (int) (returnInMillis / (60 * 60 * 1000));

            System.out.println("durationInHours: " + durationInHours);
            System.out.println("returnInHours: " + returnInHours);

            // Calculate additional charges for each hour beyond the rental duration
            int lateHours = Math.max(0, returnInHours - durationInHours);
            double hourlyLateFee = 10.0; // Replace with your actual late fee per hour

            return lateHours * hourlyLateFee;

        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 in case of error
        }
    }


    private static void updateVehicleStatus(Connection connection, int vehicleId, String newStatus) throws
            SQLException {
        String updateVehicleStatusQuery = "UPDATE Vehicle SET status = ? WHERE vehicle_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateVehicleStatusQuery)) {
            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, vehicleId);

            preparedStatement.executeUpdate();
        }
    }

    public static void reportDamageAndRepair(Connection connection, int rentId, String flag) {

        // Check if the rent exists and retrieve the vehicle details
        String checkRentQuery = "SELECT Rent.vehicle_id, Rent.insurance,Rent.total_cost ,Vehicle.category FROM Rent JOIN Vehicle ON Rent.vehicle_id = Vehicle.vehicle_id WHERE rent_id = ?";

        try (PreparedStatement checkRentStatement = connection.prepareStatement(checkRentQuery)) {
            checkRentStatement.setInt(1, rentId);

            // Execute the query and retrieve the result
            try (ResultSet resultSet = checkRentStatement.executeQuery()) {
                // Check if the result set is not empty
                if (resultSet.next()) {
                    // Extract vehicle_id and category from the result
                    int damagedVehicleId = resultSet.getInt("vehicle_id");
                    String category = resultSet.getString("category");
                    boolean insurance = resultSet.getBoolean("insurance");
                    double totalCost = resultSet.getDouble("total_cost");

                    System.out.println("insurance: " + insurance);
                    System.out.println("flag: " + flag);
                    System.out.println("rentId: " + rentId);

                    if (flag.equals("Maintenance") || (flag.equals("Damaged") && insurance)) {
                        System.out.println("Insurance Brother.");
                        int newVehicleId = getFirstAvailableVehicleWithCategory(category, connection);

                        // Assign a new vehicle of the same category to the rent
                        if (newVehicleId != -1) {
                            String assignNewVehicleQuery = "UPDATE Rent SET vehicle_id = ? WHERE rent_id = ?";
                            try (PreparedStatement assignNewVehicleStatement = connection.prepareStatement(assignNewVehicleQuery)) {
                                assignNewVehicleStatement.setInt(1, newVehicleId);
                                assignNewVehicleStatement.setInt(2, rentId);
                                updateVehicleStatus(connection, newVehicleId, "Rented");

                                // Execute the query to assign the new vehicle
                                int rowsAffected = assignNewVehicleStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    // Successfully assigned new vehicle, now register for repair
                                    registerVehicleForRepair(damagedVehicleId, category, flag, connection);
                                } else {
                                    // Handle the case where no rows were affected (e.g., invalid rentId)
                                    System.out.println("No rows affected. Invalid rentId?");
                                }
                            }
                        } else {
                            String assignNewVehicleQuery = "UPDATE Rent SET status = ? WHERE rent_id = ?";

                            try (PreparedStatement assignNewVehicleStatement = connection.prepareStatement(assignNewVehicleQuery)) {
                                assignNewVehicleStatement.setString(1, "Completed");
                                assignNewVehicleStatement.setInt(2, rentId);

                                // Execute the query to assign the new vehicle
                                int rowsAffected = assignNewVehicleStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    // Successfully assigned new vehicle, now register for repair
                                    registerVehicleForRepair(damagedVehicleId, category, flag, connection);
                                } else {
                                    // Handle the case where no rows were affected (e.g., invalid rentId)
                                    System.out.println("No rows affected. Invalid rentId?");
                                }
                            }
                            System.out.println("No available vehicle with the same category found.");
                        }
                    } else {
                        System.out.println("No Insurance Brother.");
                        String updateTotalCostQuery = "UPDATE Rent SET total_cost = ?, status = 'Completed' WHERE rent_id = ?";
                        updateVehicleStatus(connection, damagedVehicleId, "Damaged");
                        try (PreparedStatement updateTotalCostStatement = connection.prepareStatement(updateTotalCostQuery)) {
                            updateTotalCostStatement.setDouble(1, totalCost * 3);
                            updateTotalCostStatement.setInt(2, rentId);

                            updateTotalCostStatement.executeUpdate();

                            registerVehicleForRepair(damagedVehicleId, category, flag, connection);
                        }
                    }
                } else {
                    // Handle the case where the rentId does not exist or the result set is empty
                    System.out.println("Rent not found or empty result set.");
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle the exception appropriately in a real-world scenario
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static int getFirstAvailableVehicleWithCategory(String category, Connection connection) throws SQLException {
        String query = "SELECT vehicle_id FROM Vehicle WHERE status = 'Available' AND category = ? ORDER BY vehicle_id LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("vehicle_id");
                }
            }
        }

        // Return a default value or handle the case when no available vehicle with the same category is found
        return -1;
    }

    // Method to register a vehicle for repair
    private static void registerVehicleForRepair(int vehicleId, String category, String flag, Connection connection) throws
            SQLException {
        System.out.println(vehicleId + " " + category + " " + flag);
        // Assuming you have a method to insert repair details into the Repair table
        String registerRepairQuery;

        if (flag.equals("Maintenance")) {
            registerRepairQuery = "INSERT INTO Repair (vehicle_id, date_of_enter,date_of_exit, cost, status) VALUES (?, NOW(),DATE_ADD(NOW(), INTERVAL 1 DAY), ?, 'Maintenance')";
        } else {
            registerRepairQuery = "INSERT INTO Repair (vehicle_id, date_of_enter,date_of_exit, cost, status) VALUES (?, NOW(),DATE_ADD(NOW(), INTERVAL 3 DAY), ?, 'Damaged')";
        }
        try (PreparedStatement registerRepairStatement = connection.prepareStatement(registerRepairQuery)) {
            registerRepairStatement.setInt(1, vehicleId);
            // You may set the cost based on your logic
            registerRepairStatement.setInt(2, 10);

            updateVehicleStatus(connection, vehicleId, "Damaged");

            // Execute the query to register the vehicle for repair
            // (Handle the result and implement error checking based on your needs)
            registerRepairStatement.executeUpdate();
        }
    }

    public static List<Map<String, String>> fetchRentsByDate(Connection connection) throws SQLException {
        String query = "SELECT *\n" +
                "FROM Rent\n" +
                "ORDER BY date_of_rent;\n";
        return getStringRents(connection, query);
    }

    public static List<Map<String, String>> getStringRents(Connection connection, String query) throws SQLException {
        List<Map<String, String>> resultList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Create a new map for each row
                    Map<String, String> rowMap = new HashMap<>();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = resultSet.getString(i);

                        // Store column metadata in the row map
                        rowMap.put(columnName, columnValue);
                    }

                    // Add the row map to the result list
                    resultList.add(rowMap);
                }
            }
            return resultList;

        }
    }

    public static List<Map<String, String>> fetchRentsDuration(Connection connection) throws SQLException {
        String query = "SELECT " +
                "v.category, " +
                "MIN(r.rent_duration) AS min_duration, " +
                "MAX(r.rent_duration) AS max_duration, " +
                "AVG(r.rent_duration) AS avg_duration " +
                "FROM " +
                "Vehicle v " +
                "JOIN " +
                "Rent r ON v.vehicle_id = r.vehicle_id " +
                "GROUP BY " +
                "v.category;";


        return getStringRents(connection, query);
    }

    public static List<Map<String, String>> fetchPopularVehicle(Connection connection) throws SQLException {
        String query = "SELECT " +
                "v.category, " +
                "v.vehicle_id, " +
                "COUNT(*) AS rental_count " +
                "FROM " +
                "Vehicle v " +
                "JOIN " +
                "Rent r ON v.vehicle_id = r.vehicle_id " +
                "GROUP BY " +
                "v.category, v.vehicle_id " +
                "HAVING " +
                "COUNT(*) = (" +
                "SELECT " +
                "MAX(rental_count) " +
                "FROM " +
                "(SELECT " +
                "v.category, " +
                "v.vehicle_id, " +
                "COUNT(*) AS rental_count " +
                "FROM " +
                "Vehicle v " +
                "JOIN " +
                "Rent r ON v.vehicle_id = r.vehicle_id " +
                "GROUP BY " +
                "v.category, v.vehicle_id) AS category_counts " +
                "WHERE " +
                "category_counts.category = v.category" +
                ");";


        return getStringRents(connection, query);
    }

    public static List<Map<String, String>> fetchCostOfRepair(Connection connection) throws SQLException {
        String query = "SELECT " +
                "DATE_FORMAT(r.date_of_enter, '%Y-%m') AS repair_month, " +
                "SUM(r.cost) AS total_repair_cost " +
                "FROM " +
                "Repair r " +
                "GROUP BY " +
                "repair_month " +
                "ORDER BY " +
                "repair_month;";

        return getStringRents(connection, query);
    }


    public static List<Map<String, String>> fetchEarning(Connection connection) throws SQLException {
        String query = "SELECT " +
                "DATE_FORMAT(r.date_of_rent, '%Y-%m') AS rent_month, " +
                "SUM(r.total_cost) AS total_earning " +
                "FROM " +
                "Rent r " +
                "GROUP BY " +
                "rent_month " +
                "ORDER BY " +
                "rent_month;";


        return getStringRents(connection, query);
    }

    public static List<Map<String, String>> fetchVehiclesByCategory(Connection connection) throws SQLException {
        String Query = "SELECT *\n" +
                "FROM Vehicle\n" +
                "WHERE status IN ('available', 'rented')\n" +
                "ORDER BY\n" +
                "  CASE category\n" +
                "    WHEN 'CAR' THEN 1\n" +
                "    WHEN 'MOTORCYCLE' THEN 2\n" +
                "    WHEN 'BIKE' THEN 3\n" +
                "    -- Add more categories as needed\n" +
                "    ELSE 4 -- Default order for unknown categories\n" +
                "  END;\n";
        return getStringRents(connection, Query);

    }

    public static List<Map<String, String>> fetchAvailableVehiclesByCategory(Connection connection) throws SQLException {
        String Query = "SELECT * \n" +
                "FROM Vehicle\n" +
                "WHERE status IN ('available')\n" +
                "ORDER BY\n" +
                "  CASE category\n" +
                "    WHEN 'CAR' THEN 1\n" +
                "    WHEN 'MOTORCYCLE' THEN 2\n" +
                "    WHEN 'BIKE' THEN 3\n" +
                "    -- Add more categories as needed\n" +
                "    ELSE 4 -- Default order for unknown categories\n" +
                "  END;\n";
        return getStringRents(connection, Query);

    }

    public static List<Map<String, String>> fetchAvailableCars(Connection connection) throws SQLException {
        String Query = "SELECT * FROM Vehicle\n" +
                "WHERE category = 'Car' AND status = 'available';";
        return getStringRents(connection, Query);

    }

    public static List<Map<String, String>> fetchAvailableMotorcycles(Connection connection) throws SQLException {
        String Query = "SELECT * FROM Vehicle\n" +
                "WHERE category = 'Motorcycle' AND status = 'available';";
        return getStringRents(connection, Query);

    }

    public static List<Map<String, String>> fetchAvailableBikes(Connection connection) throws SQLException {
        String Query = "SELECT * FROM Vehicle\n" +
                "WHERE category = 'Bike' AND status = 'available';";
        return getStringRents(connection, Query);

    }

    public static List<Map<String, String>> fetchAvailableScooters(Connection connection) throws SQLException {
        String Query = "SELECT * FROM Vehicle\n" +
                "WHERE category = 'Scooter' AND status = 'available';";
        return getStringRents(connection, Query);

    }
}
