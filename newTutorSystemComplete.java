import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

// Student class
class Student {
    private String id;
    private String name;
    private String contact;

    public Student(String id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() {
        return id + "," + name + "," + contact;
    }
}

// FileManager class
class FileManager {
    public static List<String> readAllLines(String filename) {
        try {
            // Use absolute path for all .txt files in the project directory
            if (filename.equals("tutors.txt") || filename.equals("classes.txt") || filename.equals("students.txt") || filename.equals("enrollments.txt")) {
                filename = "C:/Users/User/TutorCentreSystem/JAVA-apu-/" + filename;
            }
            return Files.readAllLines(Paths.get(filename));
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
            return new ArrayList<>();
        }
    }

    public static void writeAllLines(String filename, List<String> lines) {
        try {
            if (filename.equals("tutors.txt") || filename.equals("classes.txt") || filename.equals("students.txt") || filename.equals("enrollments.txt")) {
                filename = "C:/Users/User/TutorCentreSystem/JAVA-apu-/" + filename;
            }
            Files.write(Paths.get(filename), lines);
        } catch (IOException e) {
            System.out.println("Error writing file: " + filename);
        }
    }

    public static void appendLine(String filename, String line) {
        try {
            if (filename.equals("tutors.txt") || filename.equals("classes.txt") || filename.equals("students.txt") || filename.equals("enrollments.txt")) {
                filename = "C:/Users/User/TutorCentreSystem/JAVA-apu-/" + filename;
            }
            Files.write(Paths.get(filename), Arrays.asList(line), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error appending to file: " + filename);
        }
    }
}

// Tutor class
class Tutor {
    private String username;
    private String password;
    private String name;
    private String contact;

    public Tutor(String username, String password, String name, String contact) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.contact = contact;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    // Login method (static) for GUI
    public static Tutor login(String username, String password) {
        List<String> lines = FileManager.readAllLines("tutors.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                String fileUsername = parts[0].trim();
                String filePassword = parts[1].trim();
                if (fileUsername.equals(username.trim()) && filePassword.equals(password.trim())) {
                    return new Tutor(fileUsername, filePassword, parts[2].trim(), parts[3].trim());
                }
            }
        }
        return null;
    }
}

// TutorSystemGUI class
class TutorSystemGUI {
    private JFrame loginFrame, dashboardFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private int loginAttempts = 0;
    private final int MAX_ATTEMPTS = 3;
    private Tutor loggedInTutor = null;
    private javax.swing.Timer lockoutTimer;
    private int lockoutSecondsLeft = 0;
    
    // Color scheme for comfortable viewing
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219); // Soft blue
    private static final Color SECONDARY_COLOR = new Color(236, 240, 241); // Light gray
    private static final Color ACCENT_COLOR = new Color(46, 204, 113); // Soft green
    private static final Color ERROR_COLOR = new Color(231, 76, 60); // Soft red
    private static final Color TEXT_COLOR = new Color(20, 20, 20); // Very dark gray, almost black
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Very light gray

    public TutorSystemGUI() {
        setupLookAndFeel();
        showLoginWindow();
    }
    
    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
    }

    private void showLoginWindow() {
        loginFrame = new JFrame("Advanced Tuition Centre - Tutor Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Tutor Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field
        JLabel userLabel = createStyledLabel("Username:");
        usernameField = createStyledTextField();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(usernameField, gbc);
        
        // Password field
        JLabel passLabel = createStyledLabel("Password:");
        passwordField = createStyledPasswordField();
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(passwordField, gbc);
        
        // Login button
        JButton loginButton = createStyledButton("Login", ACCENT_COLOR);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 5, 10, 5);
        formPanel.add(loginButton, gbc);
        
        // Message label
        JLabel messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(ERROR_COLOR);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 5, 5, 5);
        formPanel.add(messageLabel, gbc);
        
        // Add components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        loginFrame.add(mainPanel);
        
        // Login button action
        loginButton.addActionListener(_ -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            Tutor t = Tutor.login(username, password);
            if (t != null) {
                loggedInTutor = t;
                loginFrame.dispose();
                showDashboard();
            } else {
                loginAttempts++;
                if (loginAttempts >= MAX_ATTEMPTS) {
                    lockoutSecondsLeft = 5 * 60;
                    loginButton.setEnabled(false);
                    messageLabel.setText("Maximum login attempts reached. Please wait 5:00 to try again.");
                    lockoutTimer = new javax.swing.Timer(1000, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            lockoutSecondsLeft--;
                            int min = lockoutSecondsLeft / 60;
                            int sec = lockoutSecondsLeft % 60;
                            messageLabel.setText(String.format("Locked out. Please wait %d:%02d to try again.", min, sec));
                            if (lockoutSecondsLeft <= 0) {
                                lockoutTimer.stop();
                                loginAttempts = 0;
                                loginButton.setEnabled(true);
                                messageLabel.setText("");
                            }
                        }
                    });
                    lockoutTimer.start();
                } else {
                    messageLabel.setText("Invalid credentials. Attempts left: " + (MAX_ATTEMPTS - loginAttempts));
                }
            }
        });
        
        // Enter key support
        loginFrame.getRootPane().setDefaultButton(loginButton);
        
        loginFrame.setVisible(true);
    }

    private void showDashboard() {
        dashboardFrame = new JFrame("Advanced Tuition Centre - Tutor Dashboard");
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setSize(500, 600);
        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(BACKGROUND_COLOR);
        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInTutor.getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomePanel.add(welcomeLabel);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(6, 1, 15, 15));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        JButton addClassBtn = createStyledButton("➕ Add New Class", PRIMARY_COLOR);
        JButton updateClassBtn = createStyledButton("✏️ Update Class", PRIMARY_COLOR);
        JButton deleteClassBtn = createStyledButton("🗑️ Delete Class", ERROR_COLOR);
        JButton viewStudentsBtn = createStyledButton("👥 View Enrolled Students", ACCENT_COLOR);
        JButton updateProfileBtn = createStyledButton("👤 Update Profile", PRIMARY_COLOR);
        JButton logoutBtn = createStyledButton("🚪 Logout", SECONDARY_COLOR);
        
        buttonsPanel.add(addClassBtn);
        buttonsPanel.add(updateClassBtn);
        buttonsPanel.add(deleteClassBtn);
        buttonsPanel.add(viewStudentsBtn);
        buttonsPanel.add(updateProfileBtn);
        buttonsPanel.add(logoutBtn);
        
        // Add action listeners
        addClassBtn.addActionListener(_ -> showAddClassDialog());
        updateClassBtn.addActionListener(_ -> showUpdateClassDialog());
        deleteClassBtn.addActionListener(_ -> showDeleteClassDialog());
        viewStudentsBtn.addActionListener(_ -> showViewStudentsDialog());
        updateProfileBtn.addActionListener(_ -> showUpdateProfileDialog());
        logoutBtn.addActionListener(_ -> {
            dashboardFrame.dispose();
            loggedInTutor = null;
            loginAttempts = 0;
            showLoginWindow();
        });
        
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        dashboardFrame.add(mainPanel);
        dashboardFrame.setVisible(true);
    }
    
    // Helper methods for styled components
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(new Color(20, 20, 20)); // Very dark text for better readability
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    // --- Dialog methods ---
    private void showAddClassDialog() {
        JDialog dialog = new JDialog(dashboardFrame, "Add New Class", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(dashboardFrame);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField subjectField = createStyledTextField();
        JTextField chargesField = createStyledTextField();

        // Day of week
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        JComboBox<String> dayBox = new JComboBox<>(days);

        // Time selectors
        String[] hours = new String[12];
        for (int i = 0; i < 12; i++) hours[i] = String.valueOf(i == 0 ? 12 : i);
        String[] minutes = {"00", "15", "30", "45"};
        String[] ampm = {"am", "pm"};
        JComboBox<String> startHour = new JComboBox<>(hours);
        JComboBox<String> startMinute = new JComboBox<>(minutes);
        JComboBox<String> startAMPM = new JComboBox<>(ampm);
        JComboBox<String> endHour = new JComboBox<>(hours);
        JComboBox<String> endMinute = new JComboBox<>(minutes);
        JComboBox<String> endAMPM = new JComboBox<>(ampm);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createStyledLabel("Subject Name:"), gbc);
        gbc.gridx = 1;
        panel.add(subjectField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStyledLabel("Charges:"), gbc);
        gbc.gridx = 1;
        panel.add(chargesField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createStyledLabel("Day:"), gbc);
        gbc.gridx = 1;
        panel.add(dayBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createStyledLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startTimePanel.setBackground(BACKGROUND_COLOR);
        startTimePanel.add(startHour); startTimePanel.add(new JLabel(":")); startTimePanel.add(startMinute); startTimePanel.add(startAMPM);
        panel.add(startTimePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createStyledLabel("End Time:"), gbc);
        gbc.gridx = 1;
        JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        endTimePanel.setBackground(BACKGROUND_COLOR);
        endTimePanel.add(endHour); endTimePanel.add(new JLabel(":")); endTimePanel.add(endMinute); endTimePanel.add(endAMPM);
        panel.add(endTimePanel, gbc);

        JButton saveBtn = createStyledButton("Save", ACCENT_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", SECONDARY_COLOR);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveBtn.addActionListener(_ -> {
            String subject = subjectField.getText().trim();
            String chargesStr = chargesField.getText().trim();
            String day = (String) dayBox.getSelectedItem();
            String start = startHour.getSelectedItem() + ":" + startMinute.getSelectedItem() + startAMPM.getSelectedItem();
            String end = endHour.getSelectedItem() + ":" + endMinute.getSelectedItem() + endAMPM.getSelectedItem();
            String schedule = day + " " + start + "-" + end;
            if (subject.isEmpty() || chargesStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double charges;
            try {
                charges = Double.parseDouble(chargesStr);
                if (charges < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Charges must be a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<String> lines = FileManager.readAllLines("classes.txt");
            int maxId = 0;
            for (String line : lines) {
                if (line.startsWith("C")) {
                    try {
                        int id = Integer.parseInt(line.substring(1, line.indexOf(",")));
                        if (id > maxId) maxId = id;
                    } catch (Exception ignored) {}
                }
            }
            String newId = "C" + String.format("%03d", maxId + 1);
            String newClassLine = newId + "," + subject + "," + charges + "," + schedule + "," + loggedInTutor.getUsername();
            FileManager.appendLine("classes.txt", newClassLine);
            JOptionPane.showMessageDialog(dialog, "Class added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        cancelBtn.addActionListener(_ -> dialog.dispose());
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showUpdateClassDialog() {
        List<String> lines = FileManager.readAllLines("classes.txt");
        List<String> myClasses = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[4].equals(loggedInTutor.getUsername())) {
                myClasses.add(line);
            }
        }
        if (myClasses.isEmpty()) {
            JOptionPane.showMessageDialog(dashboardFrame, "You have no classes to update.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] classArr = myClasses.toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(dashboardFrame, "Select a class to update:", "Update Class", JOptionPane.PLAIN_MESSAGE, null, classArr, classArr[0]);
        if (selected == null) return;
        String[] parts = selected.split(",");
        JDialog dialog = new JDialog(dashboardFrame, "Update Class", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(dashboardFrame);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField subjectField = createStyledTextField();
        JTextField chargesField = createStyledTextField();
        // Day of week
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        JComboBox<String> dayBox = new JComboBox<>(days);
        // Time selectors
        String[] hours = new String[12];
        for (int i = 0; i < 12; i++) hours[i] = String.valueOf(i == 0 ? 12 : i);
        String[] minutes = {"00", "15", "30", "45"};
        String[] ampm = {"am", "pm"};
        JComboBox<String> startHour = new JComboBox<>(hours);
        JComboBox<String> startMinute = new JComboBox<>(minutes);
        JComboBox<String> startAMPM = new JComboBox<>(ampm);
        JComboBox<String> endHour = new JComboBox<>(hours);
        JComboBox<String> endMinute = new JComboBox<>(minutes);
        JComboBox<String> endAMPM = new JComboBox<>(ampm);
        // Set current values
        subjectField.setText(parts[1]);
        chargesField.setText(parts[2]);
        String currentSchedule = parts[3];
        if (currentSchedule.contains(" ")) {
            String[] scheduleParts = currentSchedule.split(" ");
            if (scheduleParts.length >= 2) {
                String dayStr = scheduleParts[0];
                for (int i = 0; i < days.length; i++) {
                    if (days[i].equals(dayStr)) {
                        dayBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createStyledLabel("Subject Name:"), gbc);
        gbc.gridx = 1;
        panel.add(subjectField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStyledLabel("Charges:"), gbc);
        gbc.gridx = 1;
        panel.add(chargesField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createStyledLabel("Day:"), gbc);
        gbc.gridx = 1;
        panel.add(dayBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createStyledLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startTimePanel.setBackground(BACKGROUND_COLOR);
        startTimePanel.add(startHour); startTimePanel.add(new JLabel(":")); startTimePanel.add(startMinute); startTimePanel.add(startAMPM);
        panel.add(startTimePanel, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createStyledLabel("End Time:"), gbc);
        gbc.gridx = 1;
        JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        endTimePanel.setBackground(BACKGROUND_COLOR);
        endTimePanel.add(endHour); endTimePanel.add(new JLabel(":")); endTimePanel.add(endMinute); endTimePanel.add(endAMPM);
        panel.add(endTimePanel, gbc);
        JButton saveBtn = createStyledButton("Update", ACCENT_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", SECONDARY_COLOR);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        saveBtn.addActionListener(_ -> {
            String subject = subjectField.getText().trim();
            String chargesStr = chargesField.getText().trim();
            String day = (String) dayBox.getSelectedItem();
            String start = startHour.getSelectedItem() + ":" + startMinute.getSelectedItem() + startAMPM.getSelectedItem();
            String end = endHour.getSelectedItem() + ":" + endMinute.getSelectedItem() + endAMPM.getSelectedItem();
            String newSchedule = day + " " + start + "-" + end;
            if (subject.isEmpty() || chargesStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double charges;
            try {
                charges = Double.parseDouble(chargesStr);
                if (charges < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Charges must be a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String updatedLine = parts[0] + "," + subject + "," + charges + "," + newSchedule + "," + loggedInTutor.getUsername();
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).equals(selected)) {
                    lines.set(i, updatedLine);
                    break;
                }
            }
            FileManager.writeAllLines("classes.txt", lines);
            JOptionPane.showMessageDialog(dialog, "Class updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        cancelBtn.addActionListener(_ -> dialog.dispose());
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showDeleteClassDialog() {
        List<String> lines = FileManager.readAllLines("classes.txt");
        List<String> myClasses = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[4].equals(loggedInTutor.getUsername())) {
                myClasses.add(line);
            }
        }
        if (myClasses.isEmpty()) {
            JOptionPane.showMessageDialog(dashboardFrame, "You have no classes to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] classArr = myClasses.toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(dashboardFrame, "Select a class to delete:", "Delete Class", JOptionPane.PLAIN_MESSAGE, null, classArr, classArr[0]);
        if (selected == null) return;
        int confirm = JOptionPane.showConfirmDialog(dashboardFrame, "Are you sure you want to delete this class?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Remove from classes.txt
            lines.remove(selected);
            FileManager.writeAllLines("classes.txt", lines);
            // Remove related enrollments
            String classId = selected.split(",")[0];
            List<String> enrollments = FileManager.readAllLines("enrollments.txt");
            List<String> updatedEnrollments = new ArrayList<>();
            for (String enroll : enrollments) {
                if (!enroll.startsWith(classId + ",")) {
                    updatedEnrollments.add(enroll);
                }
            }
            FileManager.writeAllLines("enrollments.txt", updatedEnrollments);
            JOptionPane.showMessageDialog(dashboardFrame, "Class deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showViewStudentsDialog() {
        List<String> classLines = FileManager.readAllLines("classes.txt");
        List<String> myClasses = new ArrayList<>();
        for (String line : classLines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[4].equals(loggedInTutor.getUsername())) {
                myClasses.add(line);
            }
        }
        if (myClasses.isEmpty()) {
            JOptionPane.showMessageDialog(dashboardFrame, "You have no classes.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] classArr = myClasses.toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(dashboardFrame, "Select a class:", "View Enrolled Students", JOptionPane.PLAIN_MESSAGE, null, classArr, classArr[0]);
        if (selected == null) return;
        String classId = selected.split(",")[0];
        List<String> enrollments = FileManager.readAllLines("enrollments.txt");
        List<String> studentIds = new ArrayList<>();
        for (String enroll : enrollments) {
            String[] parts = enroll.split(",");
            if (parts.length == 2 && parts[0].equals(classId)) {
                studentIds.add(parts[1]);
            }
        }
        if (studentIds.isEmpty()) {
            JOptionPane.showMessageDialog(dashboardFrame, "No students enrolled in this class.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<String> students = FileManager.readAllLines("students.txt");
        String[] columnNames = {"ID", "Name", "Contact"};
        List<String[]> data = new ArrayList<>();
        for (String studentLine : students) {
            String[] parts = studentLine.split(",");
            if (parts.length >= 3 && studentIds.contains(parts[0])) {
                data.add(new String[]{parts[0], parts[1], parts[2]});
            }
        }
        String[][] tableData = data.toArray(new String[0][]);
        JTable table = new JTable(tableData, columnNames);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(dashboardFrame, scrollPane, "Enrolled Students", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUpdateProfileDialog() {
        JDialog dialog = new JDialog(dashboardFrame, "Update Profile", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(dashboardFrame);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField nameField = createStyledTextField();
        JTextField contactField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        
        nameField.setText(loggedInTutor.getName());
        contactField.setText(loggedInTutor.getContact());
        passwordField.setText(loggedInTutor.getPassword());
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createStyledLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStyledLabel("Contact:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        JButton saveBtn = createStyledButton("Update", ACCENT_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", SECONDARY_COLOR);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        saveBtn.addActionListener(_ -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (name.isEmpty() || contact.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Update tutors.txt
            List<String> lines = FileManager.readAllLines("tutors.txt");
            String updatedLine = loggedInTutor.getUsername() + "," + password + "," + name + "," + contact;
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 4 && parts[0].equals(loggedInTutor.getUsername())) {
                    lines.set(i, updatedLine);
                    break;
                }
            }
            FileManager.writeAllLines("tutors.txt", lines);
            loggedInTutor.setName(name);
            loggedInTutor.setContact(contact);
            loggedInTutor.setPassword(password);
            JOptionPane.showMessageDialog(dialog, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(_ -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}

// Main class - the only public class
public class TutorSystemComplete {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TutorSystemGUI::new);
    }
} 