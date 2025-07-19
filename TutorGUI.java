import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class TutorGUI extends JFrame {
    private TutorDashboard tutor;
    private final Color PRIMARY = new Color(52, 152, 219);
    private final Color SECONDARY = new Color(236, 240, 241);
    private final Color ACCENT = new Color(46, 204, 113);
    private final Color ERROR = new Color(231, 76, 60);
    private final Color BACKGROUND = new Color(248, 249, 250);

    public TutorGUI(String username) {
        initializeTutor(username);
        createUI();
    }

    private void initializeTutor(String username) {
        List<String> tutors = FileManager.readAllLines("tutors.txt");
        for (String line : tutors) {
            String[] data = line.split(";");
            if (data.length >= 4 && data[0].equals(username)) {
                tutor = new TutorDashboard(data[0], data[1], data[2], data[3]);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Tutor not found", "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    private void createUI() {
        setTitle("Tutor Dashboard - Tuition Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("TUTOR DASHBOARD - " + tutor.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBackground(BACKGROUND);

        buttonPanel.add(createButton("Add Class", PRIMARY, e -> showAddClassDialog()));
        buttonPanel.add(createButton("Update Class", PRIMARY, e -> showUpdateClassDialog()));
        buttonPanel.add(createButton("Delete Class", ERROR, e -> showDeleteClassDialog()));
        buttonPanel.add(createButton("View Students", ACCENT, e -> showStudentsDialog()));
        buttonPanel.add(createButton("Update Profile", PRIMARY, e -> showProfileDialog()));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(logoutButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createButton(String text, Color bgColor, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }

    private void showAddClassDialog() {
        JDialog dialog = new JDialog(this, "Add New Class", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField subjectField = new JTextField(20);
        JTextField chargesField = new JTextField(20);

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        JComboBox<String> dayBox = new JComboBox<>(days);

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

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Subject Name:"), gbc);
        gbc.gridx = 1;
        panel.add(subjectField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Charges:"), gbc);
        gbc.gridx = 1;
        panel.add(chargesField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1;
        panel.add(dayBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startTimePanel.add(startHour);
        startTimePanel.add(new JLabel(":"));
        startTimePanel.add(startMinute);
        startTimePanel.add(startAMPM);
        panel.add(startTimePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        endTimePanel.add(endHour);
        endTimePanel.add(new JLabel(":"));
        endTimePanel.add(endMinute);
        endTimePanel.add(endAMPM);
        panel.add(endTimePanel, gbc);

        JButton saveBtn = createButton("Save", ACCENT, e -> {
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

            List<String> classLines = FileManager.readAllLines("classes.txt");
            int maxId = 0;
            for (String line : classLines) {
                if (line.startsWith("C")) {
                    try {
                        int id = Integer.parseInt(line.substring(1, line.indexOf(";")));
                        if (id > maxId) maxId = id;
                    } catch (Exception ignored) {}
                }
            }

            String newId = "C" + String.format("%03d", maxId + 1);
            ClassInfo newClass = new ClassInfo(newId, subject, charges, schedule, tutor.getUsername());
            FileManager.appendLine("classes.txt", newClass.toString());

            JOptionPane.showMessageDialog(dialog, "Class added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        JButton cancelBtn = createButton("Cancel", SECONDARY, e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showUpdateClassDialog() {
        List<String> classLines = FileManager.readAllLines("classes.txt");
        List<ClassInfo> myClasses = new ArrayList<>();
        for (String line : classLines) {
            String[] parts = line.split(";");
            if (parts.length >= 5 && parts[4].equals(tutor.getUsername())) {
                myClasses.add(new ClassInfo(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3], parts[4]));
            }
        }

        if (myClasses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no classes to update.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] classOptions = myClasses.stream()
                .map(c -> c.getId() + " - " + c.getSubject())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a class to update:", "Update Class",
                JOptionPane.PLAIN_MESSAGE, null, classOptions, classOptions[0]);

        if (selected == null) return;

        String selectedId = selected.split(" - ")[0];
        ClassInfo selectedClass = myClasses.stream()
                .filter(c -> c.getId().equals(selectedId))
                .findFirst()
                .orElse(null);

        if (selectedClass == null) return;

        JDialog dialog = new JDialog(this, "Update Class", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField subjectField = new JTextField(selectedClass.getSubject(), 20);
        JTextField chargesField = new JTextField(String.valueOf(selectedClass.getCharges()), 20);

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        JComboBox<String> dayBox = new JComboBox<>(days);

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

        String currentSchedule = selectedClass.getSchedule();
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

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Subject Name:"), gbc);
        gbc.gridx = 1;
        panel.add(subjectField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Charges:"), gbc);
        gbc.gridx = 1;
        panel.add(chargesField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1;
        panel.add(dayBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startTimePanel.add(startHour);
        startTimePanel.add(new JLabel(":"));
        startTimePanel.add(startMinute);
        startTimePanel.add(startAMPM);
        panel.add(startTimePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        endTimePanel.add(endHour);
        endTimePanel.add(new JLabel(":"));
        endTimePanel.add(endMinute);
        endTimePanel.add(endAMPM);
        panel.add(endTimePanel, gbc);

        JButton updateBtn = createButton("Update", ACCENT, e -> {
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

            selectedClass.setSubject(subject);
            selectedClass.setCharges(charges);
            selectedClass.setSchedule(newSchedule);

            List<String> updatedLines = new ArrayList<>();
            for (String line : classLines) {
                if (line.startsWith(selectedClass.getId())) {
                    updatedLines.add(selectedClass.toString());
                } else {
                    updatedLines.add(line);
                }
            }

            FileManager.writeAllLines("classes.txt", updatedLines);
            JOptionPane.showMessageDialog(dialog, "Class updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        JButton cancelBtn = createButton("Cancel", SECONDARY, e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showDeleteClassDialog() {
        List<String> classLines = FileManager.readAllLines("classes.txt");
        List<String> myClasses = new ArrayList<>();
        for (String line : classLines) {
            String[] parts = line.split(";");
            if (parts.length >= 5 && parts[4].equals(tutor.getUsername())) {
                myClasses.add(line);
            }
        }

        if (myClasses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no classes to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] classOptions = myClasses.stream()
                .map(line -> {
                    String[] parts = line.split(";");
                    return parts[0] + " - " + parts[1];
                })
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a class to delete:", "Delete Class",
                JOptionPane.PLAIN_MESSAGE, null, classOptions, classOptions[0]);

        if (selected == null) return;

        String selectedId = selected.split(" - ")[0];
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this class?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            List<String> updatedClasses = new ArrayList<>();
            for (String line : classLines) {
                if (!line.startsWith(selectedId)) {
                    updatedClasses.add(line);
                }
            }
            FileManager.writeAllLines("classes.txt", updatedClasses);

            List<String> enrollments = FileManager.readAllLines("enrollments.txt");
            List<String> updatedEnrollments = new ArrayList<>();
            for (String enroll : enrollments) {
                if (!enroll.startsWith(selectedId + ";")) {
                    updatedEnrollments.add(enroll);
                }
            }
            FileManager.writeAllLines("enrollments.txt", updatedEnrollments);

            JOptionPane.showMessageDialog(this, "Class deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showStudentsDialog() {
        List<String> classLines = FileManager.readAllLines("classes.txt");
        List<ClassInfo> myClasses = new ArrayList<>();
        for (String line : classLines) {
            String[] parts = line.split(";");
            if (parts.length >= 5 && parts[4].equals(tutor.getUsername())) {
                myClasses.add(new ClassInfo(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3], parts[4]));
            }
        }

        if (myClasses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no classes with enrolled students.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] classOptions = myClasses.stream()
                .map(c -> c.getId() + " - " + c.getSubject())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a class to view students:", "View Students",
                JOptionPane.PLAIN_MESSAGE, null, classOptions, classOptions[0]);

        if (selected == null) return;

        String selectedId = selected.split(" - ")[0];

        List<String> enrollments = FileManager.readAllLines("enrollments.txt");
        Set<String> studentIds = new HashSet<>();
        for (String enroll : enrollments) {
            String[] parts = enroll.split(";");
            if (parts.length >= 2 && parts[0].equals(selectedId)) {
                studentIds.add(parts[1]);
            }
        }

        if (studentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students enrolled in this class.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {"Student ID", "Name", "Contact"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<String> students = FileManager.readAllLines("students.txt");
        for (String studentLine : students) {
            String[] parts = studentLine.split(";");
            if (parts.length >= 3 && studentIds.contains(parts[0])) {
                model.addRow(new Object[]{parts[0], parts[1], parts[2]});
            }
        }

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Enrolled Students in " + selected,
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void showProfileDialog() {
        JDialog dialog = new JDialog(this, "Update Profile", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(tutor.getName(), 20);
        JTextField contactField = new JTextField(tutor.getContact(), 20);
        JPasswordField passwordField = new JPasswordField(tutor.getPassword(), 20);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton saveBtn = createButton("Save", ACCENT, e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || contact.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tutor.setName(name);
            tutor.setContact(contact);
            tutor.setPassword(password);

            List<String> tutors = FileManager.readAllLines("tutors.txt");
            List<String> updatedTutors = new ArrayList<>();
            for (String line : tutors) {
                String[] parts = line.split(";");
                if (parts.length >= 4 && parts[0].equals(tutor.getUsername())) {
                    updatedTutors.add(tutor.toFileString());
                } else {
                    updatedTutors.add(line);
                }
            }

            FileManager.writeAllLines("tutors.txt", updatedTutors);
            JOptionPane.showMessageDialog(dialog, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        JButton cancelBtn = createButton("Cancel", SECONDARY, e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}

class TutorDashboard {
    private String username;
    private String password;
    private String name;
    private String contact;

    public TutorDashboard(String username, String password, String name, String contact) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.contact = contact;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }

    public String toFileString() {
        return username + ";" + password + ";" + name + ";" + contact;
    }
}

class ClassInfo {
    private String id;
    private String subject;
    private double charges;
    private String schedule;
    private String tutorUsername;

    public ClassInfo(String id, String subject, double charges, String schedule, String tutorUsername) {
        this.id = id;
        this.subject = subject;
        this.charges = charges;
        this.schedule = schedule;
        this.tutorUsername = tutorUsername;
    }

    public String getId() { return id; }
    public String getSubject() { return subject; }
    public double getCharges() { return charges; }
    public String getSchedule() { return schedule; }
    public String getTutorUsername() { return tutorUsername; }

    public void setSubject(String subject) { this.subject = subject; }
    public void setCharges(double charges) { this.charges = charges; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    @Override
    public String toString() {
        return id + ";" + subject + ";" + charges + ";" + schedule + ";" + tutorUsername;
    }
}

class FileManager {
    private static final String DATA_DIR = "data/";

    public static void initializeTutorFile() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File tutorFile = new File(DATA_DIR + "tutors.txt");
        if (!tutorFile.exists()) {
            try (PrintWriter writer = new PrintWriter(tutorFile)) {
                // Add default tutor
                writer.println("tutor1;tutor123;John Tutor;555-1234");
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
            JOptionPane.showMessageDialog(null, "Error reading " + filename + ": " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Error writing " + filename + ": " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void appendLine(String filename, String line) {
        createDataDirectory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + filename, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error appending to " + filename + ": " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}