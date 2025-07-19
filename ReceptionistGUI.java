import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ReceptionistGUI extends JFrame {
    // Student data
    private List<Student> students = new ArrayList<>();
    private DefaultTableModel studentTableModel;

    // Subject configuration
    private Map<String, List<String>> courseSubjects = new HashMap<>();
    private Map<String, Double> subjectPrices = new HashMap<>();

    // Current user
    private String receptionistName;
    private String receptionistId;
    private String receptionistEmail;

    // UI Components
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JTextField currentStudentField;

    // File name for student data
    private static final String STUDENT_DATA_FILE = "students.txt";
    private static final String RECEIPTS_FILE = "all_receipts.txt";

    // Student ID counter
    private int nextStudentIdCounter = 0;

    // Constructors
    public ReceptionistGUI() {
        this("Default Receptionist", "RC000", "reception@school.edu");
    }

    public ReceptionistGUI(String name, String id, String email) {
        this.receptionistName = name;
        this.receptionistId = id;
        this.receptionistEmail = email;

        setTitle("Receptionist Dashboard - " + name);
        initializeSubjectData();
        setupUI();
        loadStudents();
        determineNextStudentIdCounter();
        initializeReceiptFile();
    }

    private void initializeSubjectData() {
        courseSubjects.put("Form 1", Arrays.asList("Mathematics", "English", "Science", "History", "Geography", "Bahasa Melayu"));
        courseSubjects.put("Form 2", Arrays.asList("Mathematics", "English", "Science", "History", "Geography", "Bahasa Melayu", "Arts"));
        courseSubjects.put("Form 3", Arrays.asList("Mathematics", "English", "Science", "History", "Geography", "Bahasa Melayu", "Living Skills"));
        courseSubjects.put("Form 4", Arrays.asList("Additional Mathematics", "Physics", "Chemistry", "Biology", "Bahasa Melayu", "English"));
        courseSubjects.put("Form 5", Arrays.asList("Additional Mathematics", "Physics", "Chemistry", "Biology", "Bahasa Melayu", "English"));

        subjectPrices.put("Mathematics", 90.0);
        subjectPrices.put("English", 100.0);
        subjectPrices.put("Science", 80.0);
        subjectPrices.put("History", 70.0);
        subjectPrices.put("Geography", 70.0);
        subjectPrices.put("Bahasa Melayu", 100.0);
        subjectPrices.put("Arts", 100.0);
        subjectPrices.put("Living Skills", 100.0);
        subjectPrices.put("Additional Mathematics", 120.0);
        subjectPrices.put("Physics", 120.0);
        subjectPrices.put("Chemistry", 120.0);
        subjectPrices.put("Biology", 120.0);
    }

    private void setupUI() {
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
        setLocationRelativeTo(null);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem homeItem = new JMenuItem("Home");
        JMenuItem logoutItem = new JMenuItem("Logout");

        homeItem.addActionListener(e -> showHomePage());
        logoutItem.addActionListener(e -> logout());

        fileMenu.add(homeItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Main panels
        mainPanel.add(createHomePage(), "HOME");
        mainPanel.add(createRegisterPanel(), "REGISTER");
        mainPanel.add(createUpdatePanel(), "UPDATE");
        mainPanel.add(createPaymentPanel(), "PAYMENT");
        mainPanel.add(createDeletePanel(), "DELETE");
        mainPanel.add(createProfilePanel(), "PROFILE");

        add(mainPanel);
    }

    private JPanel createHomePage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("RECEPTIONIST DASHBOARD", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));

        JButton registerBtn = createMenuButton("Register New Student");
        registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

        JButton updateBtn = createMenuButton("Update Student Enrollment");
        updateBtn.addActionListener(e -> cardLayout.show(mainPanel, "UPDATE"));

        JButton paymentBtn = createMenuButton("Accept Payment");
        paymentBtn.addActionListener(e -> cardLayout.show(mainPanel, "PAYMENT"));

        JButton deleteBtn = createMenuButton("Delete Student Record");
        deleteBtn.addActionListener(e -> cardLayout.show(mainPanel, "DELETE"));

        JButton profileBtn = createMenuButton("Update My Profile");
        profileBtn.addActionListener(e -> cardLayout.show(mainPanel, "PROFILE"));

        buttonPanel.add(registerBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(paymentBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(profileBtn);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }

    private JPanel createBackButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> showHomePage());
        panel.add(backButton);
        return panel;
    }

    private void saveReceipt(String studentId, String name, double amountPaid, double newBalance) {
        String receipt = "----------------------------------\n" +
                "Date: " + new Date() + "\n" +
                "Student: " + name + " (" + studentId + ")\n" +
                "Amount Paid: RM " + String.format("%.2f", amountPaid) + "\n" +
                "New Balance: RM " + String.format("%.2f", newBalance) + "\n" +
                "Processed By: " + receptionistName + "\n" +
                "----------------------------------\n";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECEIPTS_FILE, true))) {
            writer.write(receipt);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save receipt: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeReceiptFile() {
        if (!new File(RECEIPTS_FILE).exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECEIPTS_FILE))) {
                writer.write("====== PAYMENT RECEIPT LOG ======\n");
                writer.write("System: Tuition Management\n");
                writer.write("Created: " + new Date() + "\n\n");
            } catch (IOException e) {
                System.err.println("Could not initialize receipt file: " + e.getMessage());
            }
        }
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createBackButtonPanel(), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField(25);

        JLabel icLabel = new JLabel("IC/Passport:");
        JTextField icField = new JTextField(25);

        JLabel contactLabel = new JLabel("Contact Number:");
        JTextField contactField = new JTextField(25);

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField(25);

        JLabel courseLabel = new JLabel("Course Level:");
        JComboBox<String> courseCombo = new JComboBox<>(new String[]{"Form 1", "Form 2", "Form 3", "Form 4", "Form 5"});

        JLabel subjectLabel = new JLabel("Subjects (Max 3):");
        JPanel subjectPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JComboBox<String> subject1 = new JComboBox<>();
        JComboBox<String> subject2 = new JComboBox<>();
        JComboBox<String> subject3 = new JComboBox<>();
        subjectPanel.add(subject1);
        subjectPanel.add(subject2);
        subjectPanel.add(subject3);

        updateSubjects(courseCombo, subject1, subject2, subject3);
        courseCombo.addActionListener(e -> updateSubjects(courseCombo, subject1, subject2, subject3));

        JButton registerButton = new JButton("Register Student");
        registerButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String ic = icField.getText().trim();
            String contact = contactField.getText().trim();
            String address = addressField.getText().trim();
            String course = (String)courseCombo.getSelectedItem();

            List<String> subjects = new ArrayList<>();
            if (subject1.getSelectedIndex() >= 0) {
                String selectedSubject = (String) subject1.getSelectedItem();
                if (selectedSubject != null && !selectedSubject.isEmpty()) {
                    subjects.add(selectedSubject);
                }
            }
            if (subject2.getSelectedIndex() >= 0) {
                String selectedSubject = (String) subject2.getSelectedItem();
                if (selectedSubject != null && !selectedSubject.isEmpty() && !subjects.contains(selectedSubject)) {
                    subjects.add(selectedSubject);
                }
            }
            if (subject3.getSelectedIndex() >= 0) {
                String selectedSubject = (String) subject3.getSelectedItem();
                if (selectedSubject != null && !selectedSubject.isEmpty() && !subjects.contains(selectedSubject)) {
                    subjects.add(selectedSubject);
                }
            }

            if (name.isEmpty() || ic.isEmpty() || contact.isEmpty() || address.isEmpty() || subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields and select at least one subject.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String studentId = String.format("TC%03d", ++nextStudentIdCounter);
            double totalFees = 0.0;
            for (String subj : subjects) {
                Double price = subjectPrices.get(subj);
                if (price != null) {
                    totalFees += price;
                } else {
                    System.err.println("Warning: Price not found for subject: " + subj);
                }
            }

            Student newStudent = new Student(
                    name, studentId, ic, contact, address,
                    course, subjects.toArray(new String[0]), totalFees
            );

            students.add(newStudent);
            studentTableModel.addRow(new Object[]{
                    studentId, name, course,
                    String.join(", ", subjects),
                    String.format("RM %.2f", totalFees)
            });

            saveStudents();
            JOptionPane.showMessageDialog(this,
                    "Student registered successfully!\nStudent ID: " + studentId,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm(nameField, icField, contactField, addressField);
        });

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(icLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(icField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(contactLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(contactField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(courseLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(courseCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(subjectLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(subjectPanel, gbc);
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(registerButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private void updateSubjects(JComboBox<String> courseCombo,
                                JComboBox<String> subject1,
                                JComboBox<String> subject2,
                                JComboBox<String> subject3) {
        String course = (String)courseCombo.getSelectedItem();
        List<String> subjects = courseSubjects.get(course);

        DefaultComboBoxModel<String> model1 = new DefaultComboBoxModel<>();
        model1.addElement("");
        for (String subject : subjects) {
            model1.addElement(subject);
        }
        subject1.setModel(model1);

        DefaultComboBoxModel<String> model2 = new DefaultComboBoxModel<>();
        model2.addElement("");
        for (String subject : subjects) {
            model2.addElement(subject);
        }
        subject2.setModel(model2);

        DefaultComboBoxModel<String> model3 = new DefaultComboBoxModel<>();
        model3.addElement("");
        for (String subject : subjects) {
            model3.addElement(subject);
        }
        subject3.setModel(model3);

        subject1.setSelectedIndex(0);
        subject2.setSelectedIndex(0);
        subject3.setSelectedIndex(0);
    }

    private JPanel createUpdatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createBackButtonPanel(), BorderLayout.NORTH);

        studentTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Course", "Subjects", "Balance"}, 0);

        JTable studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel updateForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel currentLabel = new JLabel("Selected Student:");
        currentStudentField = new JTextField(20);
        currentStudentField.setEditable(false);

        JLabel courseLabel = new JLabel("New Course:");
        JComboBox<String> courseCombo = new JComboBox<>(new String[]{"Form 1", "Form 2", "Form 3", "Form 4", "Form 5"});

        JLabel subjectLabel = new JLabel("New Subjects:");
        JPanel subjectPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JComboBox<String> subject1 = new JComboBox<>();
        JComboBox<String> subject2 = new JComboBox<>();
        JComboBox<String> subject3 = new JComboBox<>();
        subjectPanel.add(subject1);
        subjectPanel.add(subject2);
        subjectPanel.add(subject3);

        updateSubjects(courseCombo, subject1, subject2, subject3);
        courseCombo.addActionListener(e -> updateSubjects(courseCombo, subject1, subject2, subject3));

        JButton updateButton = new JButton("Update Enrollment");
        updateButton.addActionListener(e -> {
            String studentId = currentStudentField.getText();
            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a student first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String course = (String)courseCombo.getSelectedItem();
            List<String> subjects = new ArrayList<>();
            if (subject1.getSelectedIndex() > 0) subjects.add((String)subject1.getSelectedItem());
            if (subject2.getSelectedIndex() > 0 && !subjects.contains(subject2.getSelectedItem())) subjects.add((String)subject2.getSelectedItem());
            if (subject3.getSelectedIndex() > 0 && !subjects.contains(subject3.getSelectedItem())) subjects.add((String)subject3.getSelectedItem());

            if (subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one subject", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (Student student : students) {
                if (student.getId().equals(studentId)) {
                    student.setCourseLevel(course);
                    student.setSubjects(subjects.toArray(new String[0]));

                    double newBalance = 0.0;
                    for (String subj : subjects) {
                        Double price = subjectPrices.get(subj);
                        if (price != null) {
                            newBalance += price;
                        } else {
                            System.err.println("Warning: Price not found for subject: " + subj);
                        }
                    }
                    student.setBalance(newBalance);

                    for (int i = 0; i < studentTableModel.getRowCount(); i++) {
                        if (studentTableModel.getValueAt(i, 0).equals(studentId)) {
                            studentTableModel.setValueAt(course, i, 2);
                            studentTableModel.setValueAt(String.join(", ", subjects), i, 3);
                            studentTableModel.setValueAt(String.format("RM %.2f", newBalance), i, 4);
                            break;
                        }
                    }

                    saveStudents();
                    JOptionPane.showMessageDialog(this, "Enrollment updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
        });

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            int row = studentTable.getSelectedRow();
            if (row >= 0) {
                currentStudentField.setText(studentTable.getValueAt(row, 0).toString());
                courseCombo.setSelectedItem(studentTable.getValueAt(row, 2).toString());

                String[] currentSubjects = students.get(row).getSubjects();
                List<String> availableSubjectsForCourse = courseSubjects.get(courseCombo.getSelectedItem());

                subject1.setSelectedIndex(0);
                subject2.setSelectedIndex(0);
                subject3.setSelectedIndex(0);

                if (currentSubjects.length >= 1 && availableSubjectsForCourse.contains(currentSubjects[0])) {
                    subject1.setSelectedItem(currentSubjects[0]);
                }
                if (currentSubjects.length >= 2 && availableSubjectsForCourse.contains(currentSubjects[1])) {
                    subject2.setSelectedItem(currentSubjects[1]);
                }
                if (currentSubjects.length >= 3 && availableSubjectsForCourse.contains(currentSubjects[2])) {
                    subject3.setSelectedItem(currentSubjects[2]);
                }
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        updateForm.add(currentLabel, gbc);
        gbc.gridx = 1;
        updateForm.add(currentStudentField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        updateForm.add(courseLabel, gbc);
        gbc.gridx = 1;
        updateForm.add(courseCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        updateForm.add(subjectLabel, gbc);
        gbc.gridx = 1;
        updateForm.add(subjectPanel, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        updateForm.add(updateButton, gbc);

        panel.add(updateForm, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createBackButtonPanel(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout());
        JTable paymentTable = new JTable(studentTableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel currentLabel = new JLabel("Student ID:");
        JTextField paymentStudentField = new JTextField(15);
        paymentStudentField.setEditable(false);

        JLabel balanceLabel = new JLabel("Current Balance:");
        JLabel balanceValue = new JLabel("RM 0.00");

        JLabel amountLabel = new JLabel("Payment Amount:");
        JTextField amountField = new JTextField(15);

        JTextArea receiptArea = new JTextArea(10, 30);
        receiptArea.setEditable(false);

        JButton paymentButton = new JButton("Process Payment");
        paymentButton.addActionListener(e -> {
            String studentId = paymentStudentField.getText();
            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a student first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double paymentAmount = Double.parseDouble(amountField.getText());
                if (paymentAmount <= 0) {
                    throw new NumberFormatException();
                }

                for (Student student : students) {
                    if (student.getId().equals(studentId)) {
                        double currentBalance = student.getBalance();
                        if (paymentAmount > currentBalance) {
                            JOptionPane.showMessageDialog(this, "Payment exceeds balance!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        double newBalance = currentBalance - paymentAmount;
                        student.setBalance(newBalance);

                        for (int i = 0; i < studentTableModel.getRowCount(); i++) {
                            if (studentTableModel.getValueAt(i, 0).equals(studentId)) {
                                studentTableModel.setValueAt(String.format("RM %.2f", newBalance), i, 4);
                                break;
                            }
                        }

                        saveReceipt(studentId, student.getName(), paymentAmount, newBalance);

                        String receipt = "TUITION PAYMENT RECEIPT\n\n" +
                                "Student ID: " + studentId + "\n" +
                                "Student Name: " + student.getName() + "\n" +
                                "Date: " + new Date() + "\n" +
                                "Amount Paid: RM " + String.format("%.2f", paymentAmount) + "\n" +
                                "Balance Due: RM " + String.format("%.2f", newBalance) + "\n\n" +
                                "Thank you for your payment!";

                        receiptArea.setText(receipt);
                        amountField.setText("");
                        balanceValue.setText(String.format("RM %.2f", newBalance));
                        saveStudents();

                        JOptionPane.showMessageDialog(this, "Payment processed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        paymentTable.getSelectionModel().addListSelectionListener(e -> {
            int row = paymentTable.getSelectedRow();
            if (row >= 0) {
                paymentStudentField.setText(paymentTable.getValueAt(row, 0).toString());
                balanceValue.setText(paymentTable.getValueAt(row, 4).toString());
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        rightPanel.add(currentLabel, gbc);
        gbc.gridx = 1;
        rightPanel.add(paymentStudentField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        rightPanel.add(balanceLabel, gbc);
        gbc.gridx = 1;
        rightPanel.add(balanceValue, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        rightPanel.add(amountLabel, gbc);
        gbc.gridx = 1;
        rightPanel.add(amountField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(paymentButton, gbc);
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        rightPanel.add(new JScrollPane(receiptArea), gbc);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDeletePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createBackButtonPanel(), BorderLayout.NORTH);

        JTable deleteTable = new JTable(studentTableModel);
        deleteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(deleteTable), BorderLayout.CENTER);

        JButton deleteButton = new JButton("Delete Selected Student");
        deleteButton.addActionListener(e -> {
            int row = deleteTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a student first",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String studentId = deleteTable.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete student " + studentId + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                students.removeIf(student -> student.getId().equals(studentId));
                studentTableModel.removeRow(row);
                saveStudents();
                determineNextStudentIdCounter();
                JOptionPane.showMessageDialog(this,
                        "Student deleted successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panel.add(deleteButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createBackButtonPanel(), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(receptionistName, 20);

        JLabel idLabel = new JLabel("Staff ID:");
        JTextField idField = new JTextField(receptionistId, 20);
        idField.setEditable(false);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(receptionistEmail, 20);

        JButton updateButton = new JButton("Update Profile");
        updateButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            receptionistName = newName;
            receptionistEmail = newEmail;

            JOptionPane.showMessageDialog(this, "Profile updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(updateButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private void showHomePage() {
        cardLayout.show(mainPanel, "HOME");
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            saveStudents();
            JOptionPane.showMessageDialog(this, "Successfully logged out",
                    "Logout", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the application?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            saveStudents();
            System.exit(0);
        }
    }

    private void loadStudents() {
        File file = new File(STUDENT_DATA_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                students.clear();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";", -1);
                    if (parts.length == 8) {
                        String name = parts[0];
                        String id = parts[1];
                        String icPassport = parts[2];
                        String contactNumber = parts[3];
                        String address = parts[4];
                        String courseLevel = parts[5];
                        String[] subjects = parts[6].isEmpty() ? new String[0] : parts[6].split(",");
                        double balance = Double.parseDouble(parts[7]);

                        students.add(new Student(name, id, icPassport, contactNumber,
                                address, courseLevel, subjects, balance));
                    }
                }

                studentTableModel.setRowCount(0);
                for (Student student : students) {
                    studentTableModel.addRow(new Object[]{
                            student.getId(),
                            student.getName(),
                            student.getCourseLevel(),
                            String.join(", ", student.getSubjects()),
                            String.format("RM %.2f", student.getBalance())
                    });
                }
            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error loading student data from text file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void determineNextStudentIdCounter() {
        int maxIdNum = 0;
        Pattern pattern = Pattern.compile("TC(\\d+)");
        for (Student student : students) {
            Matcher matcher = pattern.matcher(student.getId());
            if (matcher.find()) {
                try {
                    int idNum = Integer.parseInt(matcher.group(1));
                    maxIdNum = Math.max(maxIdNum, idNum);
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Invalid student ID format: " + student.getId());
                }
            }
        }
        nextStudentIdCounter = maxIdNum;
    }

    private void saveStudents() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENT_DATA_FILE))) {
            for (Student student : students) {
                String subjectsString = String.join(",", student.getSubjects());
                writer.write(student.getName() + ";" +
                        student.getId() + ";" +
                        student.getIcPassport() + ";" +
                        student.getContactNumber() + ";" +
                        student.getAddress() + ";" +
                        student.getCourseLevel() + ";" +
                        subjectsString + ";" +
                        student.getBalance());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving student data to text file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearForm(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String loggedInUser = JOptionPane.showInputDialog("Enter receptionist name:");
            String loggedInId = JOptionPane.showInputDialog("Enter staff ID:");

            ReceptionistGUI system;
            if (loggedInUser != null && loggedInId != null) {
                system = new ReceptionistGUI(loggedInUser, loggedInId, loggedInUser + "@school.edu");
            } else {
                system = new ReceptionistGUI();
            }
            system.setVisible(true);
        });
    }

    static class Student {
        private String name;
        private String id;
        private String icPassport;
        private String contactNumber;
        private String address;
        private String courseLevel;
        private String[] subjects;
        private double balance;

        public Student(String name, String id, String icPassport, String contactNumber,
                       String address, String courseLevel, String[] subjects, double balance) {
            this.name = name;
            this.id = id;
            this.icPassport = icPassport;
            this.contactNumber = contactNumber;
            this.address = address;
            this.courseLevel = courseLevel;
            this.subjects = subjects;
            this.balance = balance;
        }

        public String getName() { return name; }
        public String getId() { return id; }
        public String getIcPassport() { return icPassport; }
        public String getContactNumber() { return contactNumber; }
        public String getAddress() { return address; }
        public String getCourseLevel() { return courseLevel; }
        public String[] getSubjects() { return subjects; }
        public double getBalance() { return balance; }

        public void setCourseLevel(String courseLevel) { this.courseLevel = courseLevel; }
        public void setSubjects(String[] subjects) { this.subjects = subjects; }
        public void setBalance(double balance) { this.balance = balance; }
    }
}