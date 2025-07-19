import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class AdminGUI extends JFrame {
    private ArrayList<Tutor> tutors = new ArrayList<>();
    private ArrayList<Receptionist> receptionists = new ArrayList<>();
    private JTextField tutorNameField, subjectField, levelField, receptionistIdField, receptionistPasswordField;
    private JTextArea reportArea;

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
        JPanel reportPanel = createReportPanel();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(tutorPanel);
        contentPanel.add(receptionistPanel);
        contentPanel.add(reportPanel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(_ -> {
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
        panel.setLayout(new GridLayout(4, 2));

        tutorNameField = new JTextField();
        subjectField = new JTextField();
        levelField = new JTextField();

        JButton registerTutorButton = new JButton("Register Tutor");
        registerTutorButton.addActionListener(_ -> registerTutor());

        JButton deleteTutorButton = new JButton("Delete Tutor");
        deleteTutorButton.addActionListener(_ -> deleteTutor());

        panel.add(new JLabel("Tutor Name:"));
        panel.add(tutorNameField);
        panel.add(new JLabel("Subject:"));
        panel.add(subjectField);
        panel.add(new JLabel("Level (1-5):"));
        panel.add(levelField);
        panel.add(registerTutorButton);
        panel.add(deleteTutorButton);

        return panel;
    }

    private JPanel createReceptionistPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Receptionist Management"));
        panel.setLayout(new GridLayout(3, 2));

        receptionistIdField = new JTextField();
        receptionistPasswordField = new JTextField();

        JButton registerReceptionistButton = new JButton("Register Receptionist");
        registerReceptionistButton.addActionListener(_ -> registerReceptionist());

        JButton updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(_ -> updateProfile());

        panel.add(new JLabel("Receptionist ID:"));
        panel.add(receptionistIdField);
        panel.add(new JLabel("Password:"));
        panel.add(receptionistPasswordField);
        panel.add(registerReceptionistButton);
        panel.add(updateProfileButton);

        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Monthly Income Report"));
        reportArea = new JTextArea(5, 40);
        reportArea.setEditable(false);
        JButton viewReportButton = new JButton("View Report");
        viewReportButton.addActionListener(_ -> viewReport());

        panel.add(new JScrollPane(reportArea));
        panel.add(viewReportButton);

        return panel;
    }

    private void registerTutor() {
        String name = tutorNameField.getText();
        String subject = subjectField.getText();
        int level = Integer.parseInt(levelField.getText());
        tutors.add(new Tutor(name, subject, level));
        saveTutors();
        JOptionPane.showMessageDialog(this, "Tutor Registered!");
    }

    private void deleteTutor() {
        String name = tutorNameField.getText();
        tutors.removeIf(tutor -> tutor.name.equals(name));
        saveTutors();
        JOptionPane.showMessageDialog(this, "Tutor Deleted!");
    }

    private void registerReceptionist() {
        String id = receptionistIdField.getText();
        String password = receptionistPasswordField.getText();
        receptionists.add(new Receptionist(id, password));
        saveReceptionists();
        JOptionPane.showMessageDialog(this, "Receptionist Registered!");
    }

    private void updateProfile() {
        String id = receptionistIdField.getText();
        String newPassword = receptionistPasswordField.getText();
        for (Receptionist receptionist : receptionists) {
            if (receptionist.id.equals(id)) {
                receptionist.password = newPassword;
                saveReceptionists();
                JOptionPane.showMessageDialog(this, "Profile Updated!");
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Receptionist Not Found!");
    }

    private void viewReport() {
        reportArea.setText("Monthly Income Report:\n");
        reportArea.append("Total Tutors: " + tutors.size() + "\n");
    }

    private void loadData() {
        loadTutors();
        loadReceptionists();
    }

    private void loadTutors() {
        try (BufferedReader br = new BufferedReader(new FileReader("tutors.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                String subject = parts[1];
                int level = Integer.parseInt(parts[2]);
                tutors.add(new Tutor(name, subject, level));
            }
        } catch (IOException e) {
            System.out.println("No existing tutor data found.");
        }
    }

    private void loadReceptionists() {
        try (BufferedReader br = new BufferedReader(new FileReader("receptionists.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                String password = parts[1];
                receptionists.add(new Receptionist(id, password));
            }
        } catch (IOException e) {
            System.out.println("No existing receptionist data found.");
        }
    }

    private void saveTutors() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("tutors.txt"))) {
            for (Tutor tutor : tutors) {
                bw.write(tutor.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveReceptionists() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("receptionists.txt"))) {
            for (Receptionist receptionist : receptionists) {
                bw.write(receptionist.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Receptionist {
    String id;
    String password;

    Receptionist(String id, String password) {
        this.id = id;
        this.password = password;
    }

    @Override
    public String toString() {
        return id + "," + password;
    }
} 