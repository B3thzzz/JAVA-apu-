import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Initialize data directory and required files
        FileManager.createDataDirectory();
        FileManager.initializeTutorFile();

        // Create and show the login window
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}

class FileManager {
    private static final String DATA_DIR = "data/";

    public static void createDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void initializeTutorFile() {
        File tutorFile = new File(DATA_DIR + "tutors.txt");
        if (!tutorFile.exists()) {
            try (PrintWriter writer = new PrintWriter(tutorFile)) {
                writer.println("tutor1;tutor123;Default Tutor;123-456-7890");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error creating tutors.txt: " + e.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static List<String> readAllLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(DATA_DIR + filename);

        if (!file.exists()) {
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error reading " + filename + ": " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return lines;
    }

    public static void writeAllLines(String filename, List<String> lines) {
        createDataDirectory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error writing " + filename + ": " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void appendLine(String filename, String line) {
        createDataDirectory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + filename, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error appending to " + filename + ": " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}


class LoginWindow extends JFrame {
    public LoginWindow() {
        setTitle("Tuition Management Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // User credentials (username, password, role)
        String[][] users = {
                {"student1", "student123", "student"},
                {"tutor1", "tutor123", "tutor"},
                {"reception1", "reception123", "receptionist"},
                {"admin1", "admin123", "admin"}
        };

        // UI Components
        JLabel titleLabel = new JLabel("TUITION MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passText = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        panel.add(userText, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        panel.add(passText, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        gbc.gridy = 4;
        panel.add(statusLabel, gbc);

        // Login action
        loginButton.addActionListener(e -> {
            String username = userText.getText().trim();
            String password = new String(passText.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Username and password required!");
                return;
            }

            for (String[] user : users) {
                if (user[0].equals(username) && user[1].equals(password)) {
                    dispose();
                    openDashboard(username, user[2]);
                    return;
                }
            }
            statusLabel.setText("Invalid username or password!");
        });

        // Enter key login
        passText.addActionListener(e -> loginButton.doClick());

        add(panel);
    }

    private void openDashboard(String username, String role) {
        switch (role.toLowerCase()) {
            case "student":
                new StudentGUI().setVisible(true);
                break;
            case "tutor":
                new TutorGUI(username).setVisible(true);
                break;
            case "receptionist":
                new ReceptionistGUI().setVisible(true);
                break;
            case "admin":
                new AdminGUI().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        "Unknown role: " + role,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                new LoginWindow().setVisible(true);
        }
    }
}