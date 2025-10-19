package webapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    // Για Docker environment
    private static final String host = "db";  // όνομα container από docker-compose
    private static final String databaseName = "EVOL";
    private static final int port = 3306;
    private static final String username = "root";
    private static final String password = "rootpassword";

    public static Connection getInitialConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
         String url = "jdbc:mysql://db:3306/?useSSL=false&allowPublicKeyRetrieval=true";
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
        return DriverManager.getConnection(url, username, password);
    }
}