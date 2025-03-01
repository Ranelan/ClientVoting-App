
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

public class ClientSide extends JFrame implements ActionListener {

    private JComboBox<String> vehicleComboBox;
    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    private JButton voteButton;
    private JButton viewButton;
    private JButton exitButton;
    private JButton addCarButton;
    private JButton leaderboardButton;
    

    public ClientSide() {
        super("Client Voting System");

        // Hardcoded vehicle names
        String[] vehicleNames = {
            "Select Vehicle",
            "Lamborghini Urus Performante",
            "Lamborghini SVJ",
            "Porsche GT3 RS",
            "BMW M5 Competition",
            "BMW M8",
            "Mercedes Benz C63s"
        };

        vehicleComboBox = new JComboBox<>(vehicleNames);
        voteButton = new JButton("Vote");
        viewButton = new JButton("View");
        leaderboardButton = new JButton("Leaderboard");
        addCarButton = new JButton("Add car");
        exitButton = new JButton("Exit");
        

        // Set up the table with a default table model
        tableModel = new DefaultTableModel(new String[]{"Vehicle", "Votes"}, 0);
        vehicleTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(vehicleTable);

        // Panel for the dropdown and vote button
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Vehicle:"));
        topPanel.add(vehicleComboBox);
        topPanel.add(voteButton);

        // Panel for the View and Exit buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(viewButton);
        bottomPanel.add(leaderboardButton);
        bottomPanel.add(addCarButton);
        bottomPanel.add(exitButton);
        

        // Layout the components
        add(topPanel, "North");
        add(scrollPane, "Center");
        add(bottomPanel, "South");

        voteButton.addActionListener(this);
        viewButton.addActionListener(this);
        leaderboardButton.addActionListener(this);
        addCarButton.addActionListener(this);
        exitButton.addActionListener(this);
       

        // Set up the window
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Establish the connection to the server
        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 4444);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error connecting to server: " + e.getMessage());
        }
    }

    private void loadVehiclesFromServer() {
        try {
            // Request the list of topVehicles from the server
            out.writeObject("GET_VEHICLE_LIST");
            out.flush();

            // Receive and update the table with topVehicles and their votes
            List<String> vehicles = (List<String>) in.readObject();
            updateTable(vehicles);  // Only update the table when "View" is clicked
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading vehicles for table: " + e.getMessage());
        }
    }

    private void sendVote() {
        String selectedVehicle = (String) vehicleComboBox.getSelectedItem();  // Get selected vehicle
        try {
            out.writeObject(selectedVehicle);  // Send selected vehicle to the server
            out.flush();
            JOptionPane.showMessageDialog(null, "Vote Added");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error sending vote: " + e.getMessage());
        }
    }

    private void updateTable(List<String> vehicles) {
        tableModel.setRowCount(0);  // Clear the table
        for (String vehicle : vehicles) {
            String[] parts = vehicle.split(" - Votes: ");
            tableModel.addRow(new Object[]{parts[0], parts[1]});  // Add vehicle name and votes to the table
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == voteButton) {
            sendVote();  // Send vote when Vote button is clicked
        } else if (e.getSource() == viewButton) {
            loadVehiclesFromServer();  // Load topVehicles from server when View button is clicked
        } else if (e.getSource() == exitButton) {
            System.exit(0);  // Exit the application
        } else if (e.getSource() == leaderboardButton) {  // Handle leaderboard button click
            Leaderboard();
        } else if (e.getSource() == addCarButton) {
            addNewCar();// add new vwhicles
        }
    }

    private void Leaderboard() {
        try {
            out.writeObject("GET_LEADERBOARD");  // Send request to server
            out.flush();

            // Receive and update the table with top voted topVehicles
            List<String> topVehicles = (List<String>) in.readObject();
            updateTable(topVehicles);  // Reuse updateTable to display leaderboard
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading leaderboard: " + e.getMessage());
        }
    }

    private void addNewCar() {
        String newCar = JOptionPane.showInputDialog(this, "Enter the car name:");
        if (newCar != null && !newCar.trim().isEmpty()) {
            try {
                out.writeObject("ADD_CAR");
                out.writeObject(newCar);
                out.flush();
                JOptionPane.showMessageDialog(null, "Car added successfully!");
                reloadVehicleComboBox();
            } catch (IOException e ) {
                JOptionPane.showMessageDialog(null, "Error adding car: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a valid  car name.");
        }
    }

    private void reloadVehicleComboBox() {
        try {
            // Request the updated list of topVehicles from the server
            out.writeObject("GET_VEHICLE_LIST");
            out.flush();

            // Receive the updated list of topVehicles
            List<String> vehicles = (List<String>) in.readObject();
            vehicleComboBox.removeAllItems();
            vehicleComboBox.addItem("Select Vehicle");

            // Add the new topVehicles to the combo box
            for (String vehicle : vehicles) {
                String vehicleName = vehicle.split(" - Votes: ")[0];  // Extract just the vehicle name
                vehicleComboBox.addItem(vehicleName);  // Add each vehicle to the combo box
            }

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error reloading vehicle list: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ClientSide();
    }
}
