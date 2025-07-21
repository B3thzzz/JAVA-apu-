import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat; // Import SimpleDateFormat
import java.util.Date; // Import Date
import java.util.Calendar; // Import Calendar for month comparison

public class AdminGUI extends JFrame {
    private ArrayList<Tutor> tutors = new ArrayList<>();
    private ArrayList<Receptionist> receptionists = new ArrayList<>();
    private JTextField tutorUsernameField, tutorPasswordField, tutorNameField, tutorPhoneField;
    private JTextField receptionistIdField, receptionistPasswordField;
    private JTextArea reportArea; // This JTextArea will now display either report based on button clicked

    public AdminGUI() {
        setTitle("Admin Dashboard - Tuition Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JPanel tutorPanel = createTutorPanel();
        JPanel receptionistPanel = createReceptionistPanel();
        JPanel reportPanel = createReportPanel(); // This panel will now contain two buttons

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(tutorPanel);
        contentPanel.add(receptionistPanel);
        contentPanel.add(reportPanel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        mainPanel.add(logoutButton, BorderLayout.SOUTH);

        add(mainPanel);
        loadData();
    }

    private JPanel createTutorPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Tutor Management"));
        panel.setLayout(new GridLayout(5, 2, 5, 5));

        tutorUsernameField = new JTextField();
        tutorPasswordField = new JTextField();
        tutorNameField = new JTextField();
        tutorPhoneField = new JTextField();

        JButton registerTutorButton = new JButton("Register Tutor");
        registerTutorButton.addActionListener(e -> registerTutor());

        JButton deleteTutorButton = new JButton("Delete Tutor");
        deleteTutorButton.addActionListener(e -> deleteTutor());

        panel.add(new JLabel("Username:"));
        panel.add(tutorUsernameField);
        panel.add(new JLabel("Password:"));
        panel.add(tutorPasswordField);
        panel.add(new JLabel("Full Name:"));
        panel.add(tutorNameField);
        panel.add(new JLabel("Phone:"));
        panel.add(tutorPhoneField);
        panel.add(registerTutorButton);
        panel.add(deleteTutorButton);

        return panel;
    }

    private JPanel createReceptionistPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Receptionist Management"));
        panel.setLayout(new GridLayout(3, 2, 5, 5));

        receptionistIdField = new JTextField();
        receptionistPasswordField = new JTextField();

        JButton registerReceptionistButton = new JButton("Register Receptionist");
        registerReceptionistButton.addActionListener(e -> registerReceptionist());

        JButton updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(e -> updateProfile());

        panel.add(new JLabel("Receptionist ID:"));
        panel.add(receptionistIdField);
        panel.add(new JLabel("Password:"));
        panel.add(receptionistPasswordField);
        panel.add(registerReceptionistButton);
        panel.add(updateProfileButton);

        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reports")); // Changed title to be more general

        reportArea = new JTextArea(10, 50);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Container for report buttons
        JButton viewTutorReceptionistReportButton = new JButton("View Staff Report"); // Renamed for clarity
        viewTutorReceptionistReportButton.addActionListener(e -> viewStaffReport()); // Calls existing report

        JButton viewMonthlyIncomeReportButton = new JButton("View Monthly Income"); // New button
        viewMonthlyIncomeReportButton.addActionListener(e -> showMonthlyIncomeReport()); // Calls new income report

        buttonContainer.add(viewTutorReceptionistReportButton);
        buttonContainer.add(viewMonthlyIncomeReportButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonContainer, BorderLayout.SOUTH); // Add button container to the south

        return panel;
    }

    private void registerTutor() {
        String username = tutorUsernameField.getText().trim();
        String password = tutorPasswordField.getText().trim();
        String name = tutorNameField.getText().trim();
        String phone = tutorPhoneField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Tutor tutor : tutors) {
            if (tutor.getUsername().equals(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        tutors.add(new Tutor(username, password, name, phone));
        saveTutors();
        clearTutorFields();
        JOptionPane.showMessageDialog(this, "Tutor registered successfully!");
    }

    private void deleteTutor() {
        String username = tutorUsernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean removed = tutors.removeIf(tutor -> tutor.getUsername().equals(username));
        if (removed) {
            saveTutors();
            clearTutorFields();
            JOptionPane.showMessageDialog(this, "Tutor deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Tutor not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerReceptionist() {
        String id = receptionistIdField.getText().trim();
        String password = receptionistPasswordField.getText().trim();

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Receptionist receptionist : receptionists) {
            if (receptionist.getId().equals(id)) {
                JOptionPane.showMessageDialog(this, "ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        receptionists.add(new Receptionist(id, password));
        saveReceptionists();
        clearReceptionistFields();
        JOptionPane.showMessageDialog(this, "Receptionist registered successfully!");
    }

    private void updateProfile() {
        String id = receptionistIdField.getText().trim();
        String newPassword = receptionistPasswordField.getText().trim();

        if (id.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Receptionist receptionist : receptionists) {
            if (receptionist.getId().equals(id)) {
                receptionist.setPassword(newPassword);
                saveReceptionists();
                clearReceptionistFields();
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Receptionist not found!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Existing method for staff report (renamed for clarity)
    private void viewStaffReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== STAFF REPORT ===\n\n"); // Changed title
        report.append("Total Tutors: ").append(tutors.size()).append("\n");
        report.append("Total Receptionists: ").append(receptionists.size()).append("\n");
        report.append("\nTutor List:\n");

        for (Tutor tutor : tutors) {
            report.append("- ").append(tutor.getName()).append(" (").append(tutor.getUsername()).append(")\n");
        }
        report.append("\nReceptionist List:\n"); // Added receptionist list
        for (Receptionist receptionist : receptionists) {
            report.append("- ").append(receptionist.getId()).append("\n");
        }

        reportArea.setText(report.toString());
    }

    // New method for monthly income report
    private void showMonthlyIncomeReport() {
        double totalIncome = 0.0;
        // Get current month and year for comparison
        Calendar currentCal = Calendar.getInstance();
        int currentMonth = currentCal.get(Calendar.MONTH);
        int currentYear = currentCal.get(Calendar.YEAR);

        // Format for displaying the current month
        String monthYearDisplay = new SimpleDateFormat("MMMM yyyy").format(currentCal.getTime());

        try (BufferedReader reader = new BufferedReader(new FileReader("data/all_receipts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Date:")) {
                    try {
                        // Parse the date from the receipt line
                        // Example date format: Sun Jul 20 17:02:55 GMT+08:00 2025
                        SimpleDateFormat receiptDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                        Date receiptDate = receiptDateFormat.parse(line.substring(6).trim());

                        Calendar receiptCal = Calendar.getInstance();
                        receiptCal.setTime(receiptDate);

                        // Check if the receipt date is in the current month and year
                        if (receiptCal.get(Calendar.MONTH) == currentMonth &&
                                receiptCal.get(Calendar.YEAR) == currentYear) {

                            // Read next lines to find "Amount Paid:"
                            String amountLine = reader.readLine(); // Student line
                            amountLine = reader.readLine(); // Amount Paid line

                            if (amountLine != null && amountLine.startsWith("Amount Paid:")) {
                                String amountStr = amountLine.replace("Amount Paid:", "").replace("RM", "").trim();
                                // Remove any commas if present (e.g., "1,200.00")
                                amountStr = amountStr.replace(",", "");
                                totalIncome += Double.parseDouble(amountStr);
                            }
                        }
                    } catch (java.text.ParseException e) {
                        System.err.println("Error parsing date in receipt: " + line + " - " + e.getMessage());
                        // Continue to next line if date parsing fails for one receipt
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Receipts file not found at data/all_receipts.txt. Please ensure it exists.", "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading receipts file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error parsing amount in receipts file: " + ex.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Display the monthly income report
        StringBuilder report = new StringBuilder();
        report.append("=== MONTHLY INCOME REPORT FOR ").append(monthYearDisplay.toUpperCase()).append(" ===\n\n");
        report.append("Total Income: RM ").append(String.format("%.2f", totalIncome)).append("\n");

        reportArea.setText(report.toString());
    }


    private void loadData() {
        loadTutors();
        loadReceptionists();
    }

    private void loadTutors() {
        tutors.clear();
        List<String> lines = FileHelper.readAllLines("tutors.txt");

        for (String line : lines) {
            String[] parts = line.split(";");
            if (parts.length == 4) {
                tutors.add(new Tutor(parts[0], parts[1], parts[2], parts[3]));
            }
        }
    }

    private void loadReceptionists() {
        receptionists.clear();
        List<String> lines = FileHelper.readAllLines("receptionists.txt");

        for (String line : lines) {
            String[] parts = line.split(";");
            if (parts.length == 2) {
                receptionists.add(new Receptionist(parts[0], parts[1]));
            }
        }
    }

    private void saveTutors() {
        List<String> lines = new ArrayList<>();
        for (Tutor tutor : tutors) {
            lines.add(tutor.getUsername() + ";" + tutor.getPassword() + ";" + tutor.getName() + ";" + tutor.getPhone());
        }
        FileHelper.writeAllLines("tutors.txt", lines);
    }

    private void saveReceptionists() {
        List<String> lines = new ArrayList<>();
        for (Receptionist receptionist : receptionists) {
            lines.add(receptionist.getId() + ";" + receptionist.getPassword());
        }
        FileHelper.writeAllLines("receptionists.txt", lines);
    }

    private void clearTutorFields() {
        tutorUsernameField.setText("");
        tutorPasswordField.setText("");
        tutorNameField.setText("");
        tutorPhoneField.setText("");
    }

    private void clearReceptionistFields() {
        receptionistIdField.setText("");
        receptionistPasswordField.setText("");
    }

    class Tutor {
        private String username;
        private String password;
        private String name;
        private String phone;

        public Tutor(String username, String password, String name, String phone) {
            this.username = username;
            this.password = password;
            this.name = name;
            this.phone = phone;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
    }

    class Receptionist {
        private String id;
        private String password;

        public Receptionist(String id, String password) {
            this.id = id;
            this.password = password;
        }

        public String getId() { return id; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
