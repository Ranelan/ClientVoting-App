
import za.accput.t6project.doa.VehicleDoa;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;

public class ServerSide extends JFrame {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private JTextArea logArea;
    private VehicleDoa vehicleDoa;

    public ServerSide() {
        super("Server Log");
        logArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        vehicleDoa = new VehicleDoa();
        try {
            vehicleDoa.addVehicles();  // Add initial vehicles
            serverSocket = new ServerSocket(4444);
            logArea.append("Server started...\n");
            listenForClient();
        } catch (IOException | SQLException e) {
            logArea.append("Error starting server: " + e.getMessage() + "\n");
        }

    }

    public void listenForClient() {
        try {
            clientSocket = serverSocket.accept();
            logArea.append("Client connected...\n");
            processClient();
        } catch (IOException e) {
            logArea.append("Error accepting client: " + e.getMessage() + "\n");
        }
    }

    public void processClient() {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;

        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            while (true) {

                String request = (String) in.readObject(); // Assign the input to the request variable

                if (request.equals("GET_VEHICLE_LIST")) {
                    List<String> vehicles = vehicleDoa.getAllVehicles();
                    out.writeObject(vehicles);
                    out.flush();
                } else if (request.equals("GET_LEADERBOARD")) {  // Handle leaderboard request
                    List<String> topVehicles = vehicleDoa.getTopVotedVehicles();
                    out.writeObject(topVehicles);
                    out.flush();
                } else if (request.equals("ADD_CAR")) {
                    String newCar = (String) in.readObject();
                    vehicleDoa.addCar(newCar);

                } else {
                    // Handle vote
                    vehicleDoa.updateVotes(request);
                    List<String> vehicles = vehicleDoa.getAllVehicles();
                    out.writeObject(vehicles);
                    out.flush();
                }

            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            logArea.append("Error processing client: " + e.getMessage() + "\n");
        } finally {

            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                logArea.append("Error closing resources: " + e.getMessage() + "\n");
            }
        }
    }

    public static void main(String[] args) {
        new ServerSide();
    }
}
