package za.ac.cput.T6Project.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ranelani Engel
 */
public class DatabaseConnector {

    private static final String DB_URL = "jdbc:derby://localhost:1527/VotingSystem";
    private static final String USER = "administrator";
    private static final String PASSWORD = "admin";

    public static Connection derbyConnection() throws SQLException {

        System.out.println("About to connect...");
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}//End of class
