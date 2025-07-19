import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class StudentGUI extends JFrame {
    private JPanel mainPanel;
    private JLabel welcomeLabel;

    public StudentGUI() {
        setTitle("Student Dashboard - Tuition Management System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel setup
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header panel
        JPanel headerPanel = createHeaderPanel();

        // Navigation panel
        JPanel navPanel = createNavigationPanel();

        // Content panel (initial view)
        JPanel contentPanel = createInitialContentPanel();

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("STUDENT DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        welcomeLabel = new JLabel("Welcome, Student", SwingConstants.RIGHT);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navPanel.setBackground(new Color(240, 240, 240));

        String[] buttons = {
                "View Profile",
                "View Courses",
                "View Grades",
                "View Schedule",
                "Make Payment",
                "Logout"
        };

        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setPreferredSize(new Dimension(180, 40));
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            btn.addActionListener(new ButtonListener());
            navPanel.add(btn);
        }

        return navPanel;
    }

    private JPanel createInitialContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(createProfilePanel(), BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new GridLayout(6, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] labels = {"Student ID:", "Full Name:", "Email:", "Phone:", "Program:", "Enrollment Date:"};
        String[] values = {
                "S10001",
                "John Doe",
                "john.doe@university.edu",
                "(123) 456-7890",
                "Computer Science",
                "September 2023"
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            profilePanel.add(label);

            JLabel value = new JLabel(values[i]);
            value.setFont(new Font("Arial", Font.PLAIN, 14));
            profilePanel.add(value);
        }

        return profilePanel;
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            JPanel contentPanel = (JPanel) mainPanel.getComponent(2);
            contentPanel.removeAll();

            switch (command) {
                case "View Profile":
                    contentPanel.add(createProfilePanel(), BorderLayout.CENTER);
                    break;

                case "View Courses":
                    JPanel coursesPanel = new JPanel(new BorderLayout());

                    String[] columns = {"Course Code", "Course Name", "Instructor", "Schedule", "Room"};
                    Object[][] data = {
                            {"CS101", "Introduction to Programming", "Dr. Smith", "Mon/Wed 10-11:30", "Building A-201"},
                            {"MATH202", "Calculus II", "Prof. Johnson", "Tue/Thu 1-2:30", "Building B-105"},
                            {"ENG105", "Academic Writing", "Dr. Williams", "Fri 9-12", "Building C-302"}
                    };

                    JTable coursesTable = new JTable(data, columns);
                    coursesTable.setFont(new Font("Arial", Font.PLAIN, 14));
                    coursesTable.setRowHeight(25);
                    coursesTable.setEnabled(false);

                    coursesPanel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);
                    contentPanel.add(coursesPanel, BorderLayout.CENTER);
                    break;

                case "View Grades":
                    JPanel gradesPanel = new JPanel(new BorderLayout());

                    String[] gradeColumns = {"Course", "Assignment", "Grade", "Comments"};
                    Object[][] gradeData = {
                            {"CS101", "Project 1", "A", "Excellent work"},
                            {"CS101", "Midterm", "B+", "Good understanding"},
                            {"MATH202", "Quiz 1", "A-", "Minor calculation errors"},
                            {"ENG105", "Essay 1", "A", "Well researched"}
                    };

                    JTable gradesTable = new JTable(gradeData, gradeColumns);
                    gradesTable.setFont(new Font("Arial", Font.PLAIN, 14));
                    gradesTable.setRowHeight(25);
                    gradesTable.setEnabled(false);

                    // Add GPA summary
                    JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JLabel gpaLabel = new JLabel("Current GPA: 3.75");
                    gpaLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    summaryPanel.add(gpaLabel);

                    gradesPanel.add(new JScrollPane(gradesTable), BorderLayout.CENTER);
                    gradesPanel.add(summaryPanel, BorderLayout.SOUTH);
                    contentPanel.add(gradesPanel, BorderLayout.CENTER);
                    break;

                case "View Schedule":
                    JPanel schedulePanel = new JPanel(new BorderLayout());

                    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
                    String[][] scheduleData = {
                            {"CS101\n10:00-11:30\nBuilding A-201", "MATH202\n13:00-14:30\nBuilding B-105", "", "CS101 Lab\n14:00-16:00\nLab 3", "ENG105\n09:00-12:00\nBuilding C-302"},
                            {"Office Hours\n15:00-17:00", "Study Group\n16:00-18:00", "Club Meeting\n12:00-13:00", "", "Library Time\n13:00-15:00"}
                    };

                    JTable scheduleTable = new JTable(scheduleData, days);
                    scheduleTable.setFont(new Font("Arial", Font.PLAIN, 14));
                    scheduleTable.setRowHeight(80);
                    scheduleTable.setEnabled(false);

                    schedulePanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
                    contentPanel.add(schedulePanel, BorderLayout.CENTER);
                    break;

                case "Make Payment":
                    JPanel paymentPanel = new JPanel(new GridBagLayout());
                    paymentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(10, 10, 10, 10);
                    gbc.anchor = GridBagConstraints.WEST;

                    // Payment information
                    JLabel dueLabel = new JLabel("Amount Due:");
                    dueLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    JLabel amountDue = new JLabel("$1,200.00");
                    amountDue.setFont(new Font("Arial", Font.PLAIN, 14));

                    JLabel methodLabel = new JLabel("Payment Method:");
                    methodLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    JComboBox<String> methods = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Bank Transfer", "Scholarship"});
                    methods.setFont(new Font("Arial", Font.PLAIN, 14));

                    JLabel amountLabel = new JLabel("Amount to Pay:");
                    amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    JTextField amountField = new JTextField(15);
                    amountField.setFont(new Font("Arial", Font.PLAIN, 14));

                    JButton submitButton = new JButton("Submit Payment");
                    submitButton.setFont(new Font("Arial", Font.BOLD, 14));
                    submitButton.addActionListener(ev -> {
                        if (amountField.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(StudentGUI.this,
                                    "Please enter an amount",
                                    "Payment Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(StudentGUI.this,
                                    "Payment of $" + amountField.getText() + " submitted successfully!",
                                    "Payment Confirmation",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    });

                    // Layout
                    gbc.gridx = 0; gbc.gridy = 0;
                    paymentPanel.add(dueLabel, gbc);

                    gbc.gridx = 1;
                    paymentPanel.add(amountDue, gbc);

                    gbc.gridx = 0; gbc.gridy = 1;
                    paymentPanel.add(methodLabel, gbc);

                    gbc.gridx = 1;
                    paymentPanel.add(methods, gbc);

                    gbc.gridx = 0; gbc.gridy = 2;
                    paymentPanel.add(amountLabel, gbc);

                    gbc.gridx = 1;
                    paymentPanel.add(amountField, gbc);

                    gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
                    paymentPanel.add(submitButton, gbc);

                    contentPanel.add(paymentPanel, BorderLayout.CENTER);
                    break;

                case "Logout":
                    int confirm = JOptionPane.showConfirmDialog(
                            StudentGUI.this,
                            "Are you sure you want to logout?",
                            "Confirm Logout",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        dispose();
                        new LoginWindow().setVisible(true);
                    }
                    return;
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
}