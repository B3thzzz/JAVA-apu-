import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.table.*;

public class UnifiedTuitionSystem {
    // --- FileManager Utility ---
    static class FileManager {
        public static List<String> readAllLines(String filename) {
            try {
                File file = new File(filename);
                if (!file.exists()) return new ArrayList<>();
                BufferedReader br = new BufferedReader(new FileReader(file));
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) lines.add(line.trim());
                }
                br.close();
                return lines;
            } catch (IOException e) {
                return new ArrayList<>();
            }
        }
        public static void writeAllLines(String filename, List<String> lines) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
                for (String line : lines) {
                    bw.write(line + "\n");
                }
                bw.close();
            } catch (IOException e) {}
        }
        public static void appendLine(String filename, String line) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true));
                bw.write(line + "\n");
                bw.close();
            } catch (IOException e) {}
        }
        public static void ensureFile(String filename, String headerOrSample) {
            File file = new File(filename);
            if (!file.exists() || file.length() == 0) {
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    if (headerOrSample != null && !headerOrSample.isEmpty()) {
                        bw.write(headerOrSample + "\n");
                    }
                    bw.close();
                } catch (IOException e) {}
            }
        }
    }

    // --- Utility classes for JTable button rendering and editing ---
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;
        private ActionListener action;
        public ButtonEditor(JCheckBox checkBox, ActionListenerWithRow action) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            this.action = e -> action.actionPerformed(row);
            button.addActionListener(this.action);
        }
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        public Object getCellEditorValue() { isPushed = false; return label; }
        public boolean stopCellEditing() { isPushed = false; return super.stopCellEditing(); }
        protected void fireEditingStopped() { super.fireEditingStopped(); }
    }
    interface ActionListenerWithRow { void actionPerformed(int row); }

    // --- Admin Dashboard ---
    static class AdminDashboard extends JFrame {
        String adminUsername;
        JLabel welcomeLabel;
        JTabbedPane tabbedPane;

        public AdminDashboard(String username) {
            this.adminUsername = username;
            setTitle("Admin Dashboard - ATC Tuition Centre");
            setSize(700, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            welcomeLabel = new JLabel("Welcome, " + adminUsername + "!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(welcomeLabel, BorderLayout.NORTH);

            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Tutors", createTutorsPanel());
            tabbedPane.addTab("Receptionists", createReceptionistsPanel());
            tabbedPane.addTab("Income Report", createIncomePanel());
            tabbedPane.addTab("Profile", createProfilePanel());

            add(tabbedPane, BorderLayout.CENTER);
        }

        // Panel for managing tutors
        private JPanel createTutorsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Manage Tutors", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            String[] columns = {"Username", "Name", "Contact", "Delete"};
            java.util.List<String[]> tutorData = new ArrayList<>();
            for (String line : FileManager.readAllLines("tutors.txt")) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    tutorData.add(new String[]{parts[0], parts[2], parts[3], "Delete"});
                }
            }
            String[][] data = tutorData.toArray(new String[0][]);
            DefaultTableModel model = new DefaultTableModel(data, columns) {
                public boolean isCellEditable(int row, int col) { return col == 3; }
            };
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);

            table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
            table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), (row) -> {
                String username = (String) model.getValueAt(row, 0);
                List<String> lines = FileManager.readAllLines("tutors.txt");
                lines.removeIf(l -> l.startsWith(username + ","));
                FileManager.writeAllLines("tutors.txt", lines);
                model.removeRow(row);
            }));

            JPanel formPanel = new JPanel(new FlowLayout());
            JTextField userField = new JTextField(8);
            JTextField passField = new JTextField(8);
            JTextField nameField = new JTextField(10);
            JTextField contactField = new JTextField(10);
            JButton addBtn = new JButton("Add Tutor");
            formPanel.add(new JLabel("Username:"));
            formPanel.add(userField);
            formPanel.add(new JLabel("Password:"));
            formPanel.add(passField);
            formPanel.add(new JLabel("Name:"));
            formPanel.add(nameField);
            formPanel.add(new JLabel("Contact:"));
            formPanel.add(contactField);
            formPanel.add(addBtn);
            panel.add(formPanel, BorderLayout.SOUTH);

            addBtn.addActionListener(e -> {
                String u = userField.getText().trim();
                String p = passField.getText().trim();
                String n = nameField.getText().trim();
                String c = contactField.getText().trim();
                if (u.isEmpty() || p.isEmpty() || n.isEmpty() || c.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                FileManager.appendLine("tutors.txt", u + "," + p + "," + n + "," + c);
                model.addRow(new Object[]{u, n, c, "Delete"});
                userField.setText(""); passField.setText(""); nameField.setText(""); contactField.setText("");
            });

            return panel;
        }

        // Panel for managing receptionists
        private JPanel createReceptionistsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Manage Receptionists", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            String[] columns = {"Username", "Name", "Email", "Delete"};
            java.util.List<String[]> recData = new ArrayList<>();
            for (String line : FileManager.readAllLines("receptionist.txt")) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    recData.add(new String[]{parts[0], parts[2], parts[3], "Delete"});
                }
            }
            String[][] data = recData.toArray(new String[0][]);
            DefaultTableModel model = new DefaultTableModel(data, columns) {
                public boolean isCellEditable(int row, int col) { return col == 3; }
            };
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);

            table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
            table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), (row) -> {
                String username = (String) model.getValueAt(row, 0);
                List<String> lines = FileManager.readAllLines("receptionist.txt");
                lines.removeIf(l -> l.startsWith(username + ","));
                FileManager.writeAllLines("receptionist.txt", lines);
                model.removeRow(row);
            }));

            JPanel formPanel = new JPanel(new FlowLayout());
            JTextField userField = new JTextField(8);
            JTextField passField = new JTextField(8);
            JTextField nameField = new JTextField(10);
            JTextField emailField = new JTextField(12);
            JButton addBtn = new JButton("Add Receptionist");
            formPanel.add(new JLabel("Username:"));
            formPanel.add(userField);
            formPanel.add(new JLabel("Password:"));
            formPanel.add(passField);
            formPanel.add(new JLabel("Name:"));
            formPanel.add(nameField);
            formPanel.add(new JLabel("Email:"));
            formPanel.add(emailField);
            formPanel.add(addBtn);
            panel.add(formPanel, BorderLayout.SOUTH);

            addBtn.addActionListener(e -> {
                String u = userField.getText().trim();
                String p = passField.getText().trim();
                String n = nameField.getText().trim();
                String eaddr = emailField.getText().trim();
                if (u.isEmpty() || p.isEmpty() || n.isEmpty() || eaddr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                FileManager.appendLine("receptionist.txt", u + "," + p + "," + n + "," + eaddr);
                model.addRow(new Object[]{u, n, eaddr, "Delete"});
                userField.setText(""); passField.setText(""); nameField.setText(""); emailField.setText("");
            });

            return panel;
        }

        // Panel for viewing monthly income report
        private JPanel createIncomePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Monthly Income Report", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            JTextArea reportArea = new JTextArea(18, 50);
            reportArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(reportArea);
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton refreshBtn = new JButton("Refresh Report");
            panel.add(refreshBtn, BorderLayout.SOUTH);

            refreshBtn.addActionListener(e -> {
                reportArea.setText(generateIncomeReport());
            });

            reportArea.setText(generateIncomeReport());

            return panel;
        }

        private String generateIncomeReport() {
            Map<String, Double> incomeByLevel = new HashMap<>();
            Map<String, Double> incomeBySubject = new HashMap<>();
            for (String line : FileManager.readAllLines("students.txt")) {
                String[] parts = line.split(";");
                if (parts.length >= 8) {
                    String level = parts[5];
                    String[] subjects = parts[6].split(",");
                    double paid = 0.0;
                    try { paid = Double.parseDouble(parts[7]); } catch (Exception e) {}
                    incomeByLevel.put(level, incomeByLevel.getOrDefault(level, 0.0) + paid);
                    for (String subj : subjects) {
                        subj = subj.trim();
                        if (!subj.isEmpty())
                            incomeBySubject.put(subj, incomeBySubject.getOrDefault(subj, 0.0) + paid / subjects.length);
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Income by Level:\n");
            for (String lvl : incomeByLevel.keySet()) {
                sb.append("  ").append(lvl).append(": RM ").append(String.format("%.2f", incomeByLevel.get(lvl))).append("\n");
            }
            sb.append("\nIncome by Subject:\n");
            for (String subj : incomeBySubject.keySet()) {
                sb.append("  ").append(subj).append(": RM ").append(String.format("%.2f", incomeBySubject.get(subj))).append("\n");
            }
            return sb.toString();
        }

        // Panel for updating admin profile
        private JPanel createProfilePanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel nameLabel = new JLabel("Name:");
            JTextField nameField = new JTextField(20);
            JLabel emailLabel = new JLabel("Email:");
            JTextField emailField = new JTextField(20);
            JButton updateBtn = new JButton("Update Profile");

            String[] profile = null;
            for (String line : FileManager.readAllLines("admin.txt")) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(adminUsername)) {
                    profile = parts;
                    break;
                }
            }
            if (profile != null) {
                nameField.setText(profile[2]);
                emailField.setText(profile[3]);
            }

            gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(emailLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; panel.add(emailField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(updateBtn, gbc);

            updateBtn.addActionListener(e -> {
                String newName = nameField.getText().trim();
                String newEmail = emailField.getText().trim();
                if (newName.isEmpty() || newEmail.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                List<String> lines = FileManager.readAllLines("admin.txt");
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",");
                    if (parts.length >= 4 && parts[0].equals(adminUsername)) {
                        parts[2] = newName;
                        parts[3] = newEmail;
                        lines.set(i, String.join(",", parts));
                        break;
                    }
                }
                FileManager.writeAllLines("admin.txt", lines);
                JOptionPane.showMessageDialog(this, "Profile updated!");
            });

            return panel;
        }
    }

    // --- Receptionist Dashboard ---
    static class ReceptionistDashboard extends JFrame {
        String receptionistUsername;
        JLabel welcomeLabel;
        JTabbedPane tabbedPane;

        public ReceptionistDashboard(String username) {
            this.receptionistUsername = username;
            setTitle("Receptionist Dashboard - ATC Tuition Centre");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            welcomeLabel = new JLabel("Welcome, " + receptionistUsername + "!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(welcomeLabel, BorderLayout.NORTH);

            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Register Student", createRegisterStudentPanel());
            tabbedPane.addTab("Update Enrollment", createUpdateEnrollmentPanel());
            tabbedPane.addTab("Accept Payment", createPaymentPanel());
            tabbedPane.addTab("Delete Student", createDeleteStudentPanel());
            tabbedPane.addTab("Profile", createProfilePanel());

            add(tabbedPane, BorderLayout.CENTER);
        }

        // Panel for registering a new student
        private JPanel createRegisterStudentPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel nameLabel = new JLabel("Full Name:");
            JTextField nameField = new JTextField(18);
            JLabel icLabel = new JLabel("IC/Passport:");
            JTextField icField = new JTextField(18);
            JLabel contactLabel = new JLabel("Contact Number:");
            JTextField contactField = new JTextField(18);
            JLabel addressLabel = new JLabel("Address:");
            JTextField addressField = new JTextField(18);
            JLabel levelLabel = new JLabel("Level:");
            JComboBox<String> levelCombo = new JComboBox<>(new String[]{"Form 1", "Form 2", "Form 3", "Form 4", "Form 5"});
            JLabel subjectsLabel = new JLabel("Subjects (comma separated, max 3):");
            JTextField subjectsField = new JTextField(18);
            JLabel monthLabel = new JLabel("Month of Enrollment:");
            JTextField monthField = new JTextField(10);
            JButton registerBtn = new JButton("Register Student");

            gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(icLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; panel.add(icField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; panel.add(contactLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 2; panel.add(contactField, gbc);
            gbc.gridx = 0; gbc.gridy = 3; panel.add(addressLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 3; panel.add(addressField, gbc);
            gbc.gridx = 0; gbc.gridy = 4; panel.add(levelLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 4; panel.add(levelCombo, gbc);
            gbc.gridx = 0; gbc.gridy = 5; panel.add(subjectsLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 5; panel.add(subjectsField, gbc);
            gbc.gridx = 0; gbc.gridy = 6; panel.add(monthLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 6; panel.add(monthField, gbc);
            gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; panel.add(registerBtn, gbc);

            registerBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String ic = icField.getText().trim();
                String contact = contactField.getText().trim();
                String address = addressField.getText().trim();
                String level = (String) levelCombo.getSelectedItem();
                String subjects = subjectsField.getText().trim();
                String month = monthField.getText().trim();
                if (name.isEmpty() || ic.isEmpty() || contact.isEmpty() || address.isEmpty() ||
                        level.isEmpty() || subjects.isEmpty() || month.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                String[] subjectArr = subjects.split(",");
                if (subjectArr.length > 3) {
                    JOptionPane.showMessageDialog(this, "Maximum 3 subjects allowed.");
                    return;
                }
                String studentId = "S" + (System.currentTimeMillis() % 100000);
                String balance = "0.0";
                String record = String.join(";", name, studentId, ic, contact, address, level, subjects, balance, month);
                FileManager.appendLine("students.txt", record);
                JOptionPane.showMessageDialog(this, "Student registered! ID: " + studentId);
                nameField.setText(""); icField.setText(""); contactField.setText(""); addressField.setText("");
                subjectsField.setText(""); monthField.setText("");
            });

            return panel;
        }

        // Panel for updating subject enrollment of a student
        private JPanel createUpdateEnrollmentPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Update Student Enrollment", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new FlowLayout());
            JTextField studentIdField = new JTextField(10);
            JTextField newSubjectsField = new JTextField(18);
            JButton updateBtn = new JButton("Update Enrollment");
            formPanel.add(new JLabel("Student ID:"));
            formPanel.add(studentIdField);
            formPanel.add(new JLabel("New Subjects (comma separated, max 3):"));
            formPanel.add(newSubjectsField);
            formPanel.add(updateBtn);
            panel.add(formPanel, BorderLayout.CENTER);

            updateBtn.addActionListener(e -> {
                String studentId = studentIdField.getText().trim();
                String newSubjects = newSubjectsField.getText().trim();
                if (studentId.isEmpty() || newSubjects.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                String[] subjectArr = newSubjects.split(",");
                if (subjectArr.length > 3) {
                    JOptionPane.showMessageDialog(this, "Maximum 3 subjects allowed.");
                    return;
                }
                List<String> lines = FileManager.readAllLines("students.txt");
                boolean found = false;
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(";");
                    if (parts.length >= 8 && parts[1].equals(studentId)) {
                        parts[6] = newSubjects;
                        lines.set(i, String.join(";", parts));
                        found = true;
                        break;
                    }
                }
                if (found) {
                    FileManager.writeAllLines("students.txt", lines);
                    JOptionPane.showMessageDialog(this, "Enrollment updated!");
                } else {
                    JOptionPane.showMessageDialog(this, "Student ID not found.");
                }
                studentIdField.setText(""); newSubjectsField.setText("");
            });

            return panel;
        }

        // Panel for accepting payment from students and generating receipts
        private JPanel createPaymentPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel studentIdLabel = new JLabel("Student ID:");
            JTextField studentIdField = new JTextField(12);
            JLabel amountLabel = new JLabel("Amount Paid (RM):");
            JTextField amountField = new JTextField(10);
            JButton payBtn = new JButton("Accept Payment");

            gbc.gridx = 0; gbc.gridy = 0; panel.add(studentIdLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; panel.add(studentIdField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(amountLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; panel.add(amountField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(payBtn, gbc);

            payBtn.addActionListener(e -> {
                String studentId = studentIdField.getText().trim();
                String amountStr = amountField.getText().trim();
                if (studentId.isEmpty() || amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount.");
                    return;
                }
                List<String> lines = FileManager.readAllLines("students.txt");
                boolean found = false;
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(";");
                    if (parts.length >= 8 && parts[1].equals(studentId)) {
                        double oldBalance = 0.0;
                        try { oldBalance = Double.parseDouble(parts[7]); } catch (Exception ex) {}
                        double newBalance = Math.max(0.0, oldBalance - amount);
                        parts[7] = String.valueOf(newBalance);
                        lines.set(i, String.join(";", parts));
                        found = true;
                        String receipt = "----------------------------------\n" +
                                "Date: " + new Date() + "\n" +
                                "Student: " + parts[0] + " (" + studentId + ")\n" +
                                "Amount Paid: RM " + String.format("%.2f", amount) + "\n" +
                                "New Balance: RM " + String.format("%.2f", newBalance) + "\n" +
                                "Processed By: " + receptionistUsername + "\n" +
                                "----------------------------------\n";
                        FileManager.appendLine("all_receipts.txt", receipt);
                        break;
                    }
                }
                if (found) {
                    FileManager.writeAllLines("students.txt", lines);
                    JOptionPane.showMessageDialog(this, "Payment accepted and receipt generated!");
                } else {
                    JOptionPane.showMessageDialog(this, "Student ID not found.");
                }
                studentIdField.setText(""); amountField.setText("");
            });

            return panel;
        }

        // Panel for deleting students who have completed their studies
        private JPanel createDeleteStudentPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Delete Student Record", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new FlowLayout());
            JTextField studentIdField = new JTextField(12);
            JButton deleteBtn = new JButton("Delete Student");
            formPanel.add(new JLabel("Student ID:"));
            formPanel.add(studentIdField);
            formPanel.add(deleteBtn);
            panel.add(formPanel, BorderLayout.CENTER);

            deleteBtn.addActionListener(e -> {
                String studentId = studentIdField.getText().trim();
                if (studentId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Student ID required.");
                    return;
                }
                List<String> lines = FileManager.readAllLines("students.txt");
                boolean found = false;
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(";");
                    if (parts.length >= 2 && parts[1].equals(studentId)) {
                        lines.remove(i);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    FileManager.writeAllLines("students.txt", lines);
                    JOptionPane.showMessageDialog(this, "Student deleted.");
                } else {
                    JOptionPane.showMessageDialog(this, "Student ID not found.");
                }
                studentIdField.setText("");
            });

            return panel;
        }

        // Panel for updating receptionist profile
        private JPanel createProfilePanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel nameLabel = new JLabel("Name:");
            JTextField nameField = new JTextField(20);
            JLabel emailLabel = new JLabel("Email:");
            JTextField emailField = new JTextField(20);
            JButton updateBtn = new JButton("Update Profile");

            String[] profile = null;
            for (String line : FileManager.readAllLines("receptionist.txt")) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(receptionistUsername)) {
                    profile = parts;
                    break;
                }
            }
            if (profile != null) {
                nameField.setText(profile[2]);
                emailField.setText(profile[3]);
            }

            gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(emailLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; panel.add(emailField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(updateBtn, gbc);

            updateBtn.addActionListener(e -> {
                String newName = nameField.getText().trim();
                String newEmail = emailField.getText().trim();
                if (newName.isEmpty() || newEmail.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                List<String> lines = FileManager.readAllLines("receptionist.txt");
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",");
                    if (parts.length >= 4 && parts[0].equals(receptionistUsername)) {
                        parts[2] = newName;
                        parts[3] = newEmail;
                        lines.set(i, String.join(",", parts));
                        break;
                    }
                }
                FileManager.writeAllLines("receptionist.txt", lines);
                JOptionPane.showMessageDialog(this, "Profile updated!");
            });

            return panel;
        }
    }

    // --- Tutor Dashboard ---
    static class TutorDashboard extends JFrame {
        String tutorUsername;
        JLabel welcomeLabel;
        JTabbedPane tabbedPane;

        public TutorDashboard(String username) {
            this.tutorUsername = username;
            setTitle("Tutor Dashboard - ATC Tuition Centre");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            welcomeLabel = new JLabel("Welcome, " + tutorUsername + "!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(welcomeLabel, BorderLayout.NORTH);

            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Add Class", createAddClassPanel());
            tabbedPane.addTab("Update Class", createUpdateClassPanel());
            tabbedPane.addTab("Delete Class", createDeleteClassPanel());
            tabbedPane.addTab("View Enrolled Students", createViewStudentsPanel());
            tabbedPane.addTab("Profile", createProfilePanel());

            add(tabbedPane, BorderLayout.CENTER);
        }

        // Panel for adding class information
        private JPanel createAddClassPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel classIdLabel = new JLabel("Class ID:");
            JTextField classIdField = new JTextField(10);
            JLabel subjectLabel = new JLabel("Subject:");
            JTextField subjectField = new JTextField(12);
            JLabel priceLabel = new JLabel("Charges (RM):");
            JTextField priceField = new JTextField(8);
            JLabel scheduleLabel = new JLabel("Schedule:");
            JTextField scheduleField = new JTextField(12);
            JButton addBtn = new JButton("Add Class");

            gbc.gridx = 0; gbc.gridy = 0; panel.add(classIdLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; panel.add(classIdField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(subjectLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; panel.add(subjectField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; panel.add(priceLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 2; panel.add(priceField, gbc);
            gbc.gridx = 0; gbc.gridy = 3; panel.add(scheduleLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 3; panel.add(scheduleField, gbc);
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panel.add(addBtn, gbc);

            addBtn.addActionListener(e -> {
                String classId = classIdField.getText().trim();
                String subject = subjectField.getText().trim();
                String price = priceField.getText().trim();
                String schedule = scheduleField.getText().trim();
                if (classId.isEmpty() || subject.isEmpty() || price.isEmpty() || schedule.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                FileManager.appendLine("classes.txt", classId + "," + subject + "," + price + "," + schedule + "," + tutorUsername);
                JOptionPane.showMessageDialog(this, "Class added!");
                classIdField.setText(""); subjectField.setText(""); priceField.setText(""); scheduleField.setText("");
            });

            return panel;
        }

        // Panel for updating class information
        private JPanel createUpdateClassPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Update Class Information", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new FlowLayout());
            JTextField classIdField = new JTextField(10);
            JTextField newSubjectField = new JTextField(12);
            JTextField newPriceField = new JTextField(8);
            JTextField newScheduleField = new JTextField(12);
            JButton updateBtn = new JButton("Update Class");
            formPanel.add(new JLabel("Class ID:"));
            formPanel.add(classIdField);
            formPanel.add(new JLabel("New Subject:"));
            formPanel.add(newSubjectField);
            formPanel.add(new JLabel("New Price:"));
            formPanel.add(newPriceField);
            formPanel.add(new JLabel("New Schedule:"));
            formPanel.add(newScheduleField);
            formPanel.add(updateBtn);
            panel.add(formPanel, BorderLayout.CENTER);

            updateBtn.addActionListener(e -> {
                String classId = classIdField.getText().trim();
                String newSubject = newSubjectField.getText().trim();
                String newPrice = newPriceField.getText().trim();
                String newSchedule = newScheduleField.getText().trim();
                if (classId.isEmpty() || newSubject.isEmpty() || newPrice.isEmpty() || newSchedule.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                List<String> lines = FileManager.readAllLines("classes.txt");
                boolean found = false;
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",");
                    if (parts.length >= 5 && parts[0].equals(classId) && parts[4].equals(tutorUsername)) {
                        parts[1] = newSubject;
                        parts[2] = newPrice;
                        parts[3] = newSchedule;
                        lines.set(i, String.join(",", parts));
                        found = true;
                        break;
                    }
                }
                if (found) {
                    FileManager.writeAllLines("classes.txt", lines);
                    JOptionPane.showMessageDialog(this, "Class updated!");
                } else {
                    JOptionPane.showMessageDialog(this, "Class ID not found or not owned by you.");
                }
                classIdField.setText(""); newSubjectField.setText(""); newPriceField.setText(""); newScheduleField.setText("");
            });

            return panel;
        }

        // Panel for deleting class information
        private JPanel createDeleteClassPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Delete Class", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new FlowLayout());
            JTextField classIdField = new JTextField(10);
            JButton deleteBtn = new JButton("Delete Class");
            formPanel.add(new JLabel("Class ID:"));
            formPanel.add(classIdField);
            formPanel.add(deleteBtn);
            panel.add(formPanel, BorderLayout.CENTER);

            deleteBtn.addActionListener(e -> {
                String classId = classIdField.getText().trim();
                if (classId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Class ID required.");
                    return;
                }
                List<String> lines = FileManager.readAllLines("classes.txt");
                boolean found = false;
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",");
                    if (parts.length >= 5 && parts[0].equals(classId) && parts[4].equals(tutorUsername)) {
                        lines.remove(i);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    FileManager.writeAllLines("classes.txt", lines);
                    JOptionPane.showMessageDialog(this, "Class deleted.");
                } else {
                    JOptionPane.showMessageDialog(this, "Class ID not found or not owned by you.");
                }
                classIdField.setText("");
            });

            return panel;
        }

        // Panel for viewing list of students enrolled in tutor's subjects
        private JPanel createViewStudentsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Enrolled Students", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            JTextArea studentsArea = new JTextArea(20, 60);
            studentsArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(studentsArea);
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton refreshBtn = new JButton("Refresh List");
            panel.add(refreshBtn, BorderLayout.SOUTH);

            refreshBtn.addActionListener(e -> {
                studentsArea.setText(getEnrolledStudentsList());
            });

            studentsArea.setText(getEnrolledStudentsList());

            return panel;
        }

        private String getEnrolledStudentsList() {
            Set<String> mySubjects = new HashSet<>();
            for (String line : FileManager.readAllLines("classes.txt")) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[4].equals(tutorUsername)) {
                    mySubjects.add(parts[1]);
                }
            }
            StringBuilder sb = new StringBuilder();
            for (String line : FileManager.readAllLines("students.txt")) {
                String[] parts = line.split(";");
                if (parts.length >= 7) {
                    String name = parts[0];
                    String id = parts[1];
                    String level = parts[5];
                    String[] subjects = parts[6].split(",");
                    for (String subj : subjects) {
                        if (mySubjects.contains(subj.trim())) {
                            sb.append("Student: ").append(name)
                              .append(" (").append(id).append("), Level: ").append(level)
                              .append(", Subject: ").append(subj.trim()).append("\n");
                        }
                    }
                }
            }
            if (sb.length() == 0) {
                sb.append("No students enrolled in your subjects.");
            }
            return sb.toString();
        }

        // Panel for updating tutor profile
        private JPanel createProfilePanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel nameLabel = new JLabel("Name:");
            JTextField nameField = new JTextField(20);
            JLabel contactLabel = new JLabel("Contact:");
            JTextField contactField = new JTextField(20);
            JButton updateBtn = new JButton("Update Profile");

            String[] profile = null;
            for (String line : FileManager.readAllLines("tutors.txt")) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(tutorUsername)) {
                    profile = parts;
                    break;
                }
            }
            if (profile != null) {
                nameField.setText(profile[2]);
                contactField.setText(profile[3]);
            }

            gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(contactLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; panel.add(contactField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(updateBtn, gbc);

            updateBtn.addActionListener(e -> {
                String newName = nameField.getText().trim();
                String newContact = contactField.getText().trim();
                if (newName.isEmpty() || newContact.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                List<String> lines = FileManager.readAllLines("tutors.txt");
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",");
                    if (parts.length >= 4 && parts[0].equals(tutorUsername)) {
                        parts[2] = newName;
                        parts[3] = newContact;
                        lines.set(i, String.join(",", parts));
                        break;
                    }
                }
                FileManager.writeAllLines("tutors.txt", lines);
                JOptionPane.showMessageDialog(this, "Profile updated!");
            });

            return panel;
        }
    }

    // --- Student Dashboard ---
    static class StudentDashboard extends JFrame {
        String studentId;
        String studentName;
        JLabel welcomeLabel;
        JTabbedPane tabbedPane;

        public StudentDashboard(String username) {
            // Find student by username (could be name or ID)
            String[] student = null;
            for (String line : FileManager.readAllLines("students.txt")) {
                String[] parts = line.split(";");
                if (parts.length >= 3 && (parts[0].equals(username) || parts[1].equals(username))) {
                    student = parts;
                    break;
                }
            }
            if (student == null) {
                JOptionPane.showMessageDialog(null, "Student not found.");
                dispose();
                return;
            }
            this.studentId = student[1];
            this.studentName = student[0];

            setTitle("Student Dashboard - ATC Tuition Centre");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            welcomeLabel = new JLabel("Welcome, " + studentName + " (" + studentId + ")!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(welcomeLabel, BorderLayout.NORTH);

            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Class Schedule", createSchedulePanel());
            tabbedPane.addTab("Subject Change Request", createRequestPanel());
            tabbedPane.addTab("Payment Status", createPaymentPanel());
            tabbedPane.addTab("Profile", createProfilePanel());

            add(tabbedPane, BorderLayout.CENTER);
        }

        // Panel for viewing class schedule
        private JPanel createSchedulePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Class Schedule", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            JTextArea scheduleArea = new JTextArea(20, 60);
            scheduleArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(scheduleArea);
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton refreshBtn = new JButton("Refresh Schedule");
            panel.add(refreshBtn, BorderLayout.SOUTH);

            refreshBtn.addActionListener(e -> {
                scheduleArea.setText(getSchedule());
            });

            scheduleArea.setText(getSchedule());

            return panel;
        }

        private String getSchedule() {
            StringBuilder sb = new StringBuilder();
            String[] student = null;
            for (String line : FileManager.readAllLines("students.txt")) {
                String[] parts = line.split(";");
                if (parts.length >= 3 && (parts[0].equals(studentName) || parts[1].equals(studentId))) {
                    student = parts;
                    break;
                }
            }
            if (student == null) return "No schedule found.";
            String[] subjects = student[6].split(",");
            for (String subj : subjects) {
                for (String cline : FileManager.readAllLines("classes.txt")) {
                    String[] cparts = cline.split(",");
                    if (cparts.length >= 5 && cparts[1].equals(subj.trim())) {
                        sb.append("Subject: ").append(cparts[1])
                          .append(", Schedule: ").append(cparts[3])
                          .append(", Tutor: ").append(cparts[4])
                          .append("\n");
                    }
                }
            }
            if (sb.length() == 0) sb.append("No classes scheduled for your subjects.");
            return sb.toString();
        }

        // Panel for sending and deleting subject change requests
        private JPanel createRequestPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Subject Change Request", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new FlowLayout());
            JTextField dropField = new JTextField(10);
            JTextField addField = new JTextField(10);
            JButton sendBtn = new JButton("Send Request");
            formPanel.add(new JLabel("Drop Subject:"));
            formPanel.add(dropField);
            formPanel.add(new JLabel("Add Subject:"));
            formPanel.add(addField);
            formPanel.add(sendBtn);
            panel.add(formPanel, BorderLayout.NORTH);

            DefaultListModel<String> requestListModel = new DefaultListModel<>();
            JList<String> requestList = new JList<>(requestListModel);
            JScrollPane listScroll = new JScrollPane(requestList);
            panel.add(listScroll, BorderLayout.CENTER);

            JButton deleteBtn = new JButton("Delete Selected Request");
            panel.add(deleteBtn, BorderLayout.SOUTH);

            List<String> requests = FileManager.readAllLines("enrollments.txt");
            for (String req : requests) {
                String[] parts = req.split(",");
                if (parts.length == 3 && parts[1].equals(studentId)) {
                    requestListModel.addElement("Drop: " + parts[2] + ", Add: " + parts[0]);
                }
            }

            sendBtn.addActionListener(e -> {
                String drop = dropField.getText().trim();
                String add = addField.getText().trim();
                if (drop.isEmpty() || add.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Both fields required.");
                    return;
                }
                FileManager.appendLine("enrollments.txt", add + "," + studentId + "," + drop);
                requestListModel.addElement("Drop: " + drop + ", Add: " + add);
                dropField.setText(""); addField.setText("");
            });

            deleteBtn.addActionListener(e -> {
                int idx = requestList.getSelectedIndex();
                if (idx == -1) return;
                String selected = requestListModel.get(idx);
                String[] parts = selected.replace("Drop: ", "").replace("Add: ", "").split(", ");
                if (parts.length == 2) {
                    String drop = parts[0];
                    String add = parts[1];
                    List<String> lines = FileManager.readAllLines("enrollments.txt");
                    lines.removeIf(l -> l.equals(add + "," + studentId + "," + drop));
                    FileManager.writeAllLines("enrollments.txt", lines);
                }
                requestListModel.remove(idx);
            });

            return panel;
        }

        // Panel for viewing payment status and balance
        private JPanel createPaymentPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Payment Status", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.NORTH);

            JTextArea paymentArea = new JTextArea(10, 40);
            paymentArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(paymentArea);
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton refreshBtn = new JButton("Refresh Status");
            panel.add(refreshBtn, BorderLayout.SOUTH);

            refreshBtn.addActionListener(e -> {
                paymentArea.setText(getPaymentStatus());
            });

            paymentArea.setText(getPaymentStatus());

            return panel;
        }

        private String getPaymentStatus() {
            StringBuilder sb = new StringBuilder();
            String[] student = null;
            for (String line : FileManager.readAllLines("students.txt")) {
                String[] parts = line.split(";");
                if (parts.length >= 8 && (parts[0].equals(studentName) || parts[1].equals(studentId))) {
                    student = parts;
                    break;
                }
            }
            if (student == null) return "No payment record found.";
            sb.append("Name: ").append(student[0]).append("\n");
            sb.append("Student ID: ").append(student[1]).append("\n");
            sb.append("Subjects: ").append(student[6]).append("\n");
            sb.append("Outstanding Balance: RM ").append(student[7]).append("\n");
            sb.append("\nRecent Payments:\n");
            List<String> receipts = FileManager.readAllLines("all_receipts.txt");
            int count = 0;
            for (int i = receipts.size() - 1; i >= 0 && count < 5; i--) {
                String rec = receipts.get(i);
                if (rec.contains(student[1]) || rec.contains(student[0])) {
                    sb.append(rec).append("\n");
                    count++;
                }
            }
            return sb.toString();
        }

        // Panel for updating student profile
        private JPanel createProfilePanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel nameLabel = new JLabel("Name:");
            JTextField nameField = new JTextField(20);
            JLabel contactLabel = new JLabel("Contact:");
            JTextField contactField = new JTextField(20);
            JLabel addressLabel = new JLabel("Address:");
            JTextField addressField = new JTextField(20);
            JButton updateBtn = new JButton("Update Profile");

            String[] profile = null;
            for (String line : FileManager.readAllLines("students.txt")) {
                String[] parts = line.split(";");
                if (parts.length >= 5 && (parts[0].equals(studentName) || parts[1].equals(studentId))) {
                    profile = parts;
                    break;
                }
            }
            if (profile != null) {
                nameField.setText(profile[0]);
                contactField.setText(profile[3]);
                addressField.setText(profile[4]);
            }

            gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(contactLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; panel.add(contactField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; panel.add(addressLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 2; panel.add(addressField, gbc);
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(updateBtn, gbc);

            updateBtn.addActionListener(e -> {
                String newName = nameField.getText().trim();
                String newContact = contactField.getText().trim();
                String newAddress = addressField.getText().trim();
                if (newName.isEmpty() || newContact.isEmpty() || newAddress.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields required.");
                    return;
                }
                List<String> lines = FileManager.readAllLines("students.txt");
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(";");
                    if (parts.length >= 5 && (parts[0].equals(studentName) || parts[1].equals(studentId))) {
                        parts[0] = newName;
                        parts[3] = newContact;
                        parts[4] = newAddress;
                        lines.set(i, String.join(";", parts));
                        break;
                    }
                }
                FileManager.writeAllLines("students.txt", lines);
                JOptionPane.showMessageDialog(this, "Profile updated!");
            });

            return panel;
        }
    }

    // --- LoginPanel ---
    static class LoginPanel extends JFrame {
        JTextField usernameField;
        JPasswordField passwordField;
        JLabel messageLabel;
        int loginAttempts = 0;
        final int MAX_ATTEMPTS = 3;
        public LoginPanel() {
            setTitle("ATC Tuition Centre - Login");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel userLabel = new JLabel("Username:");
            usernameField = new JTextField(20);
            JLabel passLabel = new JLabel("Password:");
            passwordField = new JPasswordField(20);
            JButton loginBtn = new JButton("Login");
            messageLabel = new JLabel("", SwingConstants.CENTER);
            messageLabel.setForeground(Color.RED);
            gbc.gridx = 0; gbc.gridy = 0; form.add(userLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; form.add(usernameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; form.add(passLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; form.add(passwordField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; form.add(loginBtn, gbc);
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; form.add(messageLabel, gbc);
            add(form, BorderLayout.CENTER);
            loginBtn.addActionListener(e -> handleLogin());
            getRootPane().setDefaultButton(loginBtn);
        }
        private void handleLogin() {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = detectRole(username, password);
            if (role != null) {
                messageLabel.setText("");
                dispose();
                launchDashboard(role, username);
            } else {
                loginAttempts++;
                if (loginAttempts >= MAX_ATTEMPTS) {
                    JOptionPane.showMessageDialog(this, "Maximum login attempts reached.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                } else {
                    messageLabel.setText("Invalid credentials. Attempts left: " + (MAX_ATTEMPTS - loginAttempts));
                }
            }
        }
        private String detectRole(String username, String password) {
            for (String line : FileManager.readAllLines("admin.txt")) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) return "admin";
            }
            for (String line : FileManager.readAllLines("receptionist.txt")) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) return "receptionist";
            }
            for (String line : FileManager.readAllLines("tutors.txt")) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) return "tutor";
            }
            for (String line : FileManager.readAllLines("students.txt")) {
                String[] parts = line.split(";");
                if (parts.length >= 3 && (parts[0].equals(username) || parts[1].equals(username)) && parts[2].equals(password)) return "student";
            }
            return null;
        }
        private void launchDashboard(String role, String username) {
            SwingUtilities.invokeLater(() -> {
                switch (role) {
                    case "admin":
                        new AdminDashboard(username).setVisible(true);
                        break;
                    case "receptionist":
                        new ReceptionistDashboard(username).setVisible(true);
                        break;
                    case "tutor":
                        new TutorDashboard(username).setVisible(true);
                        break;
                    case "student":
                        new StudentDashboard(username).setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Unknown role.");
                }
            });
        }
    }

    // --- Data File Initialization ---
    static void initializeDataFiles() {
        FileManager.ensureFile("admin.txt", "admin,admin123,Admin User,admin@atc.edu");
        FileManager.ensureFile("receptionist.txt", "recept1,pass123,Receptionist User,recept@atc.edu");
        FileManager.ensureFile("tutors.txt", "tutor1,pass123,John Doe,0123456789");
        FileManager.ensureFile("students.txt", "moses;TC002;345;567;home;Form 1;Mathematics,English;270.0");
        FileManager.ensureFile("classes.txt", "C001,Mathematics,120.0,Mon 3-5pm,tutor1");
        FileManager.ensureFile("enrollments.txt", "C001,S001");
        FileManager.ensureFile("all_receipts.txt", "====== PAYMENT RECEIPT LOG ======\nSystem: Tuition Management\nCreated: " + new Date());
    }

    public static void main(String[] args) {
        initializeDataFiles();
        SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
    }
} 