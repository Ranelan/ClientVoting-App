package za.accput.t6project.doa;

import za.ac.cput.T6Project.connection.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ranelani Engel
 */
public class VehicleDoa {

    private static final String[] vehicle_names = {// array to store vehicle names
        "Lamborghini Urus Performante", "Lamborghini SVJ", "Porsche GT3 RS", "BMW M5 Competition", "BMW M8", "Mercedes Benz C63s"}; 

    public void addVehicles() throws SQLException { //adding vehicles in the database
        String insert = "INSERT INTO vehicles (vehicle_name, Number_Of_Votes) VALUES (?, 0)";

        try (Connection conn = DatabaseConnector.derbyConnection();//Database connection
                PreparedStatement pstmt = conn.prepareStatement(insert)) {

            for (String vehicleName : vehicle_names) {
                pstmt.setString(1, vehicleName);
                try {
                    pstmt.executeUpdate();  // updating the list of vehicles in the database
                } catch (SQLException e) { //catch an exception if the vehicle already exists 
                    System.out.println("Vehicle already exists:  " + vehicleName + " - " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error inserting vehicles: " + e.getMessage());
        }
    }
    

    public void updateVotes(String vehicleName) throws SQLException {  //update votes for a vehicle
        String update = "UPDATE vehicles SET Number_Of_Votes = Number_Of_Votes + 1 WHERE vehicle_name = ?";

        try (Connection conn = DatabaseConnector.derbyConnection();
                PreparedStatement pstmt = conn.prepareStatement(update)) {
            pstmt.setString(1, vehicleName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Vote updated for: " + vehicleName);
            } 
        }
    }

    public List<String> getAllVehicles() throws SQLException {  //using arrayList to retrieve all vehicles and their votes
        String selectSQL = "SELECT vehicle_name, Number_Of_Votes FROM vehicles";
        List<String> vehicleList = new ArrayList<>();

        try (Connection conn = DatabaseConnector.derbyConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                String vehicle = rs.getString("vehicle_name");
                int votes = rs.getInt("Number_Of_Votes");
                vehicleList.add(vehicle + " - Votes: " + votes);
            }
        }

        return vehicleList;
    }
    public List<String> getTopVotedVehicles() throws SQLException { // Method for retrieving the vehicles according to their number of votes (Etra feature).
    String selectSQL = "SELECT vehicle_name, Number_Of_Votes FROM vehicles ORDER BY Number_Of_Votes DESC";
    List<String> vehicleList = new ArrayList<>();

    try (Connection conn = DatabaseConnector.derbyConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(selectSQL)) {

        while (rs.next()) {
            String vehicle = rs.getString("vehicle_name");
            int votes = rs.getInt("Number_Of_Votes");
            vehicleList.add(vehicle + " - Votes: " + votes);
        }
    }
    return vehicleList;
}
   public void addCar(String carName) throws SQLException {
        String insertSQL = "INSERT INTO vehicles (vehicle_name, Number_Of_Votes) VALUES (?, 0)";

        try (Connection conn = DatabaseConnector.derbyConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, carName);
            pstmt.executeUpdate();  // Insert the new vehicle with votes set to 0
            System.out.println("New vehicle added: " + carName);
        } catch (SQLException e) {
            System.out.println("Error adding new vehicle: " + e.getMessage());
        }
    }

}
