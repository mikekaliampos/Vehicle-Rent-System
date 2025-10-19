package webapp.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;



public class InitDatabase {
    public static void main(String[] args) throws Exception {
        System.out.println("🔍 Checking database status...");
        
        try {
            Connection connection =  DatabaseConnection.getConnection();
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, "Vehicle", null);
            
            if (tables.next()) {
                System.out.println("✅ Database is already initialized. Nothing to do.");
                tables.close();
                connection.close();
                return;
            }
            tables.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("⚠️ Database not accessible: " + e.getMessage());
        }
        
        System.out.println("🚀 Starting fresh database initialization...");
        InitDatabase init = new InitDatabase();
        init.createDatabase();
        init.initDatabase();
        System.out.println("✅ Database initialization completed!");
    }

    private void createDatabase() throws Exception {
        Connection connection = DatabaseConnection.getInitialConnection();
        Statement statement = connection.createStatement();
        statement.execute("CREATE DATABASE IF NOT EXISTS EVOL");
        statement.close();
    }

    private void initDatabase() throws Exception {
        Connection connection = DatabaseConnection.getConnection();
        Statement statement = connection.createStatement();
        createTables(connection);
        insertSampleCustomers(connection);
        insertSampleVehicles(connection);
        statement.close();
        connection.close();
        System.out.println("Database initialization completed!");
    }

    public static void createTables(Connection connection) {
        try {
            Statement statement = connection.createStatement();

            // Creating Vehicle table
            String createVehicleTable = "CREATE TABLE IF NOT EXISTS Vehicle (" +
                    "vehicle_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "brand VARCHAR(255) NOT NULL," +
                    "model VARCHAR(255) NOT NULL," +
                    "color VARCHAR(255) NOT NULL," +
                    "range_in_km INT NOT NULL," +
                    "registration_number VARCHAR(255) UNIQUE," +
                    "category VARCHAR(50) NOT NULL," +
                    "status ENUM('Available', 'Rented', 'Damaged','Maintenance')" +
                    ")";
            statement.executeUpdate(createVehicleTable);

            // Creating Car table
            String createCarTable = "CREATE TABLE IF NOT EXISTS Car (" +
                    "vehicle_id INT PRIMARY KEY," +
                    "type VARCHAR(255) NOT NULL," +
                    "passenger_number INT NOT NULL," +
                    "daily_rental_cost DECIMAL(10, 2)," +
                    "insurance_cost DECIMAL(10, 2)," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createCarTable);

            // Creating Motorcycle table
            String createMotorcycleTable = "CREATE TABLE IF NOT EXISTS Motorcycle (" +
                    "vehicle_id INT PRIMARY KEY," +
                    "daily_rental_cost DECIMAL(10, 2)," +
                    "insurance_cost DECIMAL(10, 2)," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createMotorcycleTable);

            // Creating Bike table
            String createBikeTable = "CREATE TABLE IF NOT EXISTS Bike (" +
                    "vehicle_id INT PRIMARY KEY," +
                    "daily_rental_cost DECIMAL(10, 2)," +
                    "insurance_cost DECIMAL(10, 2)," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createBikeTable);

            // Creating Scooter table
            String createScooterTable = "CREATE TABLE IF NOT EXISTS Scooter (" +
                    "vehicle_id INT PRIMARY KEY," +
                    "daily_rental_cost DECIMAL(10, 2)," +
                    "insurance_cost DECIMAL(10, 2)," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createScooterTable);

            String createCustomerTable = "CREATE TABLE IF NOT EXISTS Customer (" +
                    "customer_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "address VARCHAR(255) NOT NULL," +
                    "date_of_birth DATE NOT NULL," +
                    "driver_license VARCHAR(255) UNIQUE," +
                    "card_details VARCHAR(100)" +
                    ")";
            statement.executeUpdate(createCustomerTable);

            String createRentTable = "CREATE TABLE IF NOT EXISTS Rent (" +
                    "rent_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "customer_id INT NOT NULL," +
                    "vehicle_id INT NOT NULL," +
                    "date_of_rent DATETIME NOT NULL," +
                    "date_of_return DATETIME NOT NULL," +
                    "rent_duration INT NOT NULL," +
                    "total_cost DECIMAL(10,2) NOT NULL," +
                    "insurance BOOLEAN NOT NULL," +
                    "driver_license VARCHAR(255)," +
                    "status ENUM('Active', 'Completed', 'Cancelled')," +
                    "FOREIGN KEY(customer_id) REFERENCES Customer(customer_id)," +
                    "FOREIGN KEY(vehicle_id) REFERENCES Vehicle(vehicle_id) " +
                    ")";
            statement.executeUpdate(createRentTable);

            String createRepairTable = "CREATE TABLE IF NOT EXISTS Repair (" +
                    "repair_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "vehicle_id INT NOT NULL," +
                    "date_of_enter DATETIME NOT NULL," +
                    "date_of_exit DATETIME NOT NULL," +
                    "cost DECIMAL(10,2) NOT NULL," +
                    "status ENUM( 'Damaged', 'Being Repaired', 'Maintenance')," +
                    "FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)" +
                    ")";
            statement.executeUpdate(createRepairTable);

            System.out.println("Tables created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void RegisterRepair(int vehicle_id, String date_of_enter, String date_of_exit, int cost, String status, Connection connection) {
        String sql = "INSERT INTO Repair (repair_id, vehicle_id, date_of_enter, date_of_exit, cost, status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // Set values for the placeholders
            preparedStatement.setInt(1, vehicle_id);
            preparedStatement.setString(2, date_of_enter);
            preparedStatement.setString(3, date_of_exit);
            preparedStatement.setInt(4, cost);
            preparedStatement.setString(5, status);

            // Execute the update
            preparedStatement.executeUpdate();
            System.out.println("Repair Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void RegisterCustomer(String name, String address, String date_of_birth, String driver_license, String card_details, Connection connection) {
        String sql = "INSERT INTO Customer (customer_id, name, address, date_of_birth, driver_license, card_details) " +
                "VALUES (NULL, ?, ?, ?, ?, ?)";


        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // Set values for the placeholders
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, date_of_birth);
            preparedStatement.setString(4, driver_license);
            preparedStatement.setString(5, card_details);

            // Execute the update
            preparedStatement.executeUpdate();
            System.out.println("User Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void RegisterCar(String brand, String model, String color, int range_in_km, String registration_number, String type, int passenger_number, int daily_rental_cost, int insurance_cost, Connection connection) {
        String sql = "INSERT INTO Vehicle (vehicle_id, brand, model, color, range_in_km, registration_number,category,status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?,?,?)";
        String sql2 = "INSERT INTO Car (vehicle_id, type, passenger_number,daily_rental_cost, insurance_cost) " +
                "VALUES (LAST_INSERT_ID(), ?, ?,?,?)";

        try {
            PreparedStatement VehicleStatement = connection.prepareStatement(sql);
            PreparedStatement CarStatement = connection.prepareStatement(sql2);
            // Set values for the placeholders
            VehicleStatement.setString(1, brand);
            VehicleStatement.setString(2, model);
            VehicleStatement.setString(3, color);
            VehicleStatement.setInt(4, range_in_km);
            VehicleStatement.setString(5, registration_number);
            VehicleStatement.setString(6, "Car");
            VehicleStatement.setString(7, "Available");

            CarStatement.setString(1, type);
            CarStatement.setInt(2, passenger_number);
            CarStatement.setInt(3, daily_rental_cost);
            CarStatement.setInt(4, insurance_cost);

            // Execute the update
            VehicleStatement.executeUpdate();
            CarStatement.executeUpdate();
            System.out.println("Car Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void RegisterMotorcycle(String brand, String model, String color, int range_in_km, String registration_number, int daily_rental_cost, int insurance_cost, Connection connection) {
        String sql = "INSERT INTO Vehicle (vehicle_id, brand, model, color, range_in_km, registration_number,category,status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?,?,?)";
        String sql2 = "INSERT INTO Motorcycle (vehicle_id,daily_rental_cost, insurance_cost) " +
                "VALUES (LAST_INSERT_ID(), ?, ?)";

        try {
            PreparedStatement VehicleStatement = connection.prepareStatement(sql);
            PreparedStatement MotorcycleStatement = connection.prepareStatement(sql2);
            // Set values for the placeholders
            VehicleStatement.setString(1, brand);
            VehicleStatement.setString(2, model);
            VehicleStatement.setString(3, color);
            VehicleStatement.setInt(4, range_in_km);
            VehicleStatement.setString(5, registration_number);
            VehicleStatement.setString(6, "Motorcycle");
            VehicleStatement.setString(7, "Available");


            MotorcycleStatement.setInt(1, daily_rental_cost);
            MotorcycleStatement.setInt(2, insurance_cost);


            // Execute the update
            VehicleStatement.executeUpdate();
            MotorcycleStatement.executeUpdate();
            System.out.println("Motorcycle Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void RegisterBike(String brand, String model, String color, int range_in_km, String registration_number, int daily_rental_cost, int insurance_cost, Connection connection) {
        String sql = "INSERT INTO Vehicle (vehicle_id, brand, model, color, range_in_km, registration_number,category,status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO Bike (vehicle_id,daily_rental_cost, insurance_cost) " +
                "VALUES (LAST_INSERT_ID(), ?, ?)";

        try {
            PreparedStatement VehicleStatement = connection.prepareStatement(sql);
            PreparedStatement BikeStatement = connection.prepareStatement(sql2);
            // Set values for the placeholders
            VehicleStatement.setString(1, brand);
            VehicleStatement.setString(2, model);
            VehicleStatement.setString(3, color);
            VehicleStatement.setInt(4, range_in_km);
            VehicleStatement.setString(5, registration_number);
            VehicleStatement.setString(6, "Bike");
            VehicleStatement.setString(7, "Available");


            BikeStatement.setInt(1, daily_rental_cost);
            BikeStatement.setInt(2, insurance_cost);

            // Execute the update
            VehicleStatement.executeUpdate();
            BikeStatement.executeUpdate();
            System.out.println("Bike Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void RegisterScooter(String brand, String model, String color, int range_in_km, String registration_number, int daily_rental_cost, int insurance_cost, Connection connection) {
        String sql = "INSERT INTO Vehicle (vehicle_id, brand, model, color, range_in_km, registration_number,category,status) " +
                "VALUES (NULL, ?, ?, ?, ?, ?,?,?)";
        String sql2 = "INSERT INTO Scooter (vehicle_id,daily_rental_cost, insurance_cost) " +
                "VALUES (LAST_INSERT_ID(), ?, ?)";

        try {
            PreparedStatement VehicleStatement = connection.prepareStatement(sql);
            PreparedStatement ScooterStatement = connection.prepareStatement(sql2);
            // Set values for the placeholders
            VehicleStatement.setString(1, brand);
            VehicleStatement.setString(2, model);
            VehicleStatement.setString(3, color);
            VehicleStatement.setInt(4, range_in_km);
            VehicleStatement.setString(5, registration_number);
            VehicleStatement.setString(6, "Scooter");
            VehicleStatement.setString(7, "Available");


            ScooterStatement.setInt(1, daily_rental_cost);
            ScooterStatement.setInt(2, insurance_cost);

            // Execute the update
            VehicleStatement.executeUpdate();
            ScooterStatement.executeUpdate();
            System.out.println("Scooter Registered Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertSampleCustomers(Connection connection) {
        RegisterCustomer("Orestis", "Ntilinta", "2000-05-15", "DL1", "1234-5678-9012-3456", connection);
        RegisterCustomer("John Doe", "123 Main St", "1990-05-15", "DL2", "1234-5678-9012-3456", connection);
        RegisterCustomer("Alice Smith", "456 Elm St", "1985-09-20", "DL3", "9876-5432-1098-7654", connection);
        RegisterCustomer("Bob Smith", "789 Maple St", "2015-01-01", null, "1234-5678-9012-3456", connection);
        RegisterCustomer("Jane Doe", "321 Oak St", "1995-12-31", null, "1234-5678-9012-3456", connection);

    }

    public static void insertSampleVehicles(Connection connection) {

        RegisterCar("Toyota", "Prius", "Blue", 100, "ABC1", "Sedan", 5, 50, 10, connection);
        RegisterCar("Land Rover", "Range Rover", "Black", 100, "ABC6", "SUV", 5, 50, 10, connection);
        RegisterCar("Mercedes", "S Class", "Black", 100, "ABC7", "Sedan", 5, 50, 10, connection);
        RegisterCar("BMW", "X5", "Black", 100, "ABC8", "SUV", 5, 50, 10, connection);
        RegisterCar("Audi", "A8", "Black", 100, "ABC9", "Sedan", 5, 50, 10, connection);

        RegisterBike("BMX", "MBX", "Red", 100, "ABC2", 1, 50, connection);
        RegisterBike("Giant", "Giant", "Red", 100, "ABC10", 1, 50, connection);
        RegisterBike("Scott", "Scott", "Red", 100, "ABC11", 1, 50, connection);
        RegisterBike("Trek", "Trek", "Red", 100, "ABC12", 1, 50, connection);
        RegisterBike("Cannondale", "Cannondale", "Red", 100, "ABC13", 1, 50, connection);

        RegisterMotorcycle("Honda", "CBR", "Red", 100, "ABC3", 1, 50, connection);
        RegisterMotorcycle("Yamaha", "R1", "Red", 100, "ABC14", 1, 50, connection);
        RegisterMotorcycle("Suzuki", "GSX", "Red", 100, "ABC15", 1, 50, connection);
        RegisterMotorcycle("Kawasaki", "Ninja", "Red", 100, "ABC16", 1, 50, connection);
        RegisterMotorcycle("Ducati", "Panigale", "Red", 100, "ABC17", 1, 50, connection);

        RegisterScooter("Kymco", "Agility", "Red", 100, "ABC4", 1, 50, connection);
        RegisterScooter("Piaggio", "Liberty", "Red", 100, "ABC18", 1, 50, connection);
        RegisterScooter("Vespa", "Primavera", "Red", 100, "ABC19", 1, 50, connection);
        RegisterScooter("Yamaha", "NMAX", "Red", 100, "ABC20", 1, 50, connection);
        RegisterScooter("Honda", "SH", "Red", 100, "ABC21", 1, 50, connection);


        System.out.println("Sample vehicles inserted successfully!");
    }
}