package com.example.javaproject;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Random;

public class StudentGUI extends Application {

    // Main Student Class
    public static class Student {
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty id = new SimpleStringProperty();
        private final StringProperty major = new SimpleStringProperty();
        private final ObservableList<Course> courses = FXCollections.observableArrayList();

        public Student(String name, String id, String major) {
            this.name.set(name);
            this.id.set(id);
            this.major.set(major);

            // Sample courses
            courses.add(new Course("CS101", "Introduction to Programming"));
            courses.add(new Course("MATH201", "Calculus II"));
            courses.add(new Course("ENG102", "Academic Writing"));
            courses.add(new Course("PHY101", "General Physics I"));
        }

        // Property getters
        public StringProperty nameProperty() { return name; }
        public StringProperty idProperty() { return id; }
        public StringProperty majorProperty() { return major; }
        public ObservableList<Course> getCourses() { return courses; }

        public String getName() { return name.get(); }
        public String getId() { return id.get(); }
        public String getMajor() { return major.get(); }
    }

    // Course Class
    public static class Course {
        private final StringProperty code = new SimpleStringProperty();
        private final StringProperty name = new SimpleStringProperty();

        public Course(String code, String name) {
            this.code.set(code);
            this.name.set(name);
        }

        public StringProperty codeProperty() { return code; }
        public StringProperty nameProperty() { return name; }

        public String getCode() { return code.get(); }
        public String getName() { return name.get(); }

        @Override
        public String toString() {
            return code.get() + " - " + name.get();
        }
    }

    // New Class for Enrollment Request
    public static class EnrollmentRequest {
        private final StringProperty courseToDrop = new SimpleStringProperty();
        private final StringProperty courseToAdd = new SimpleStringProperty();
        private final StringProperty status = new SimpleStringProperty("Pending");

        public EnrollmentRequest(String courseToDrop, String courseToAdd) {
            this.courseToDrop.set(courseToDrop);
            this.courseToAdd.set(courseToAdd);
        }

        public StringProperty courseToDropProperty() { return courseToDrop; }
        public StringProperty courseToAddProperty() { return courseToAdd; }
        public StringProperty statusProperty() { return status; }
    }

    // New Class for Payment
    public static class Payment {
        private final StringProperty description = new SimpleStringProperty();
        private final StringProperty amount = new SimpleStringProperty();
        private final StringProperty status = new SimpleStringProperty();

        public Payment(String description, double amount, String status) {
            this.description.set(description);
            this.amount.set(String.format("$%.2f", amount));
            this.status.set(status);
        }

        public StringProperty descriptionProperty() { return description; }
        public StringProperty amountProperty() { return amount; }
        public StringProperty statusProperty() { return status; }
    }

    private Student currentStudent;
    private final ObservableList<EnrollmentRequest> pendingRequests = FXCollections.observableArrayList();
    private final ObservableList<Payment> paymentHistory = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        currentStudent = new Student("Alex Johnson", "S1001", "Computer Science");

        // Add sample payment history
        paymentHistory.add(new Payment("Tuition Fee - Fall 2023", 5000.00, "Paid"));
        paymentHistory.add(new Payment("Lab Fee - Fall 2023", 50.00, "Paid"));
        paymentHistory.add(new Payment("Late Registration Fee", 25.00, "Unpaid"));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        // Header
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #3498db;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Student Portal");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label studentName = new Label(currentStudent.getName());
        studentName.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        studentName.setTextFill(Color.WHITE);

        header.getChildren().addAll(title, spacer, studentName);
        root.setTop(header);

        // Content Area
        TabPane mainTabs = new TabPane();

        // Pass the TabPane to the createDashboard method so buttons can switch tabs
        Tab dashboardTab = new Tab("Dashboard", createDashboard(mainTabs));
        dashboardTab.setClosable(false);

        Tab scheduleTab = new Tab("Schedule", createScheduleView());
        scheduleTab.setClosable(false);

        Tab gradesTab = new Tab("Grades", createGradesView());
        gradesTab.setClosable(false);

        Tab enrollmentTab = new Tab("Enrollment", createEnrollmentView());
        enrollmentTab.setClosable(false);

        Tab paymentsTab = new Tab("Payments", createPaymentsView());
        paymentsTab.setClosable(false);

        mainTabs.getTabs().addAll(dashboardTab, scheduleTab, gradesTab, enrollmentTab, paymentsTab);
        root.setCenter(mainTabs);

        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.setTitle("Student Portal - " + currentStudent.getName());
        primaryStage.show();
    }

    // --- New Methods for New Functionalities ---

    private VBox createEnrollmentView() {
        VBox enrollmentPane = new VBox(20);
        enrollmentPane.setPadding(new Insets(20));

        // Request Form
        TitledPane requestPane = new TitledPane("Send Enrollment Request", createRequestForm());
        requestPane.setCollapsible(false);

        // Pending Requests Table
        TitledPane pendingPane = new TitledPane("Pending Requests", createPendingRequestsTable());
        pendingPane.setCollapsible(false);

        enrollmentPane.getChildren().addAll(requestPane, pendingPane);
        return enrollmentPane;
    }

    private VBox createRequestForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));

        ComboBox<Course> dropCourse = new ComboBox<>();
        dropCourse.setPromptText("Select Course to Drop");
        dropCourse.setItems(currentStudent.getCourses());
        dropCourse.setPrefWidth(200);

        ComboBox<String> addCourse = new ComboBox<>();
        addCourse.setPromptText("Select Course to Add");
        addCourse.setItems(FXCollections.observableArrayList(
                "CS201 - Data Structures",
                "MATH301 - Linear Algebra",
                "PHY201 - Electromagnetism"));
        addCourse.setPrefWidth(200);

        Button sendButton = new Button("Send Request");
        sendButton.setStyle("-fx-base: #2ecc71;");

        sendButton.setOnAction(e -> {
            if (dropCourse.getValue() != null && addCourse.getValue() != null) {
                pendingRequests.add(new EnrollmentRequest(
                        dropCourse.getValue().getCode(),
                        addCourse.getValue().split(" ")[0]));
                dropCourse.getSelectionModel().clearSelection();
                addCourse.getSelectionModel().clearSelection();
                showAlert("Request Sent", "Your request has been sent to the receptionist.");
            } else {
                showAlert("Error", "Please select a course to drop and a course to add.");
            }
        });

        form.getChildren().addAll(
                new HBox(10, new Label("Drop:"), dropCourse),
                new HBox(10, new Label("Add:"), addCourse),
                sendButton
        );
        return form;
    }

    private VBox createPendingRequestsTable() {
        TableView<EnrollmentRequest> table = new TableView<>();
        table.setItems(pendingRequests);

        TableColumn<EnrollmentRequest, String> dropCol = new TableColumn<>("Course to Drop");
        dropCol.setCellValueFactory(new PropertyValueFactory<>("courseToDrop"));
        dropCol.setPrefWidth(150);

        TableColumn<EnrollmentRequest, String> addCol = new TableColumn<>("Course to Add");
        addCol.setCellValueFactory(new PropertyValueFactory<>("courseToAdd"));
        addCol.setPrefWidth(150);

        TableColumn<EnrollmentRequest, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        TableColumn<EnrollmentRequest, Void> deleteCol = new TableColumn<>("Action");
        deleteCol.setPrefWidth(80);
        deleteCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setOnAction(event -> {
                    EnrollmentRequest request = getTableView().getItems().get(getIndex());
                    pendingRequests.remove(request);
                    showAlert("Request Deleted", "The request has been deleted.");
                });
                deleteButton.setStyle("-fx-base: #e74c3c;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        table.getColumns().addAll(dropCol, addCol, statusCol, deleteCol);

        VBox tableContainer = new VBox(table);
        tableContainer.setPadding(new Insets(10));
        return tableContainer;
    }

    private VBox createPaymentsView() {
        VBox paymentsPane = new VBox(20);
        paymentsPane.setPadding(new Insets(20));

        // Total Balance
        double totalBalance = paymentHistory.stream()
                .filter(p -> Objects.equals(p.status.get(), "Unpaid"))
                .mapToDouble(p -> Double.parseDouble(p.amount.get().substring(1)))
                .sum();
        Label totalBalanceLabel = new Label("Total Balance Due: " + String.format("$%.2f", totalBalance));
        totalBalanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        totalBalanceLabel.setTextFill(totalBalance > 0 ? Color.RED : Color.web("#2ecc71"));

        // Payments Table
        TableView<Payment> table = new TableView<>();
        table.setItems(paymentHistory);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Payment, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Payment, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);

        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(descCol, amountCol, statusCol);

        paymentsPane.getChildren().addAll(totalBalanceLabel, new Separator(), table);
        return paymentsPane;
    }

    // Pass the TabPane to the createDashboard method
    private VBox createDashboard(TabPane mainTabs) {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));

        // Profile Section - Now editable
        VBox profileBox = new VBox(15);
        profileBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-padding: 20;");

        // Profile Header
        HBox profileHeader = new HBox(10);
        profileHeader.setAlignment(Pos.CENTER_LEFT);
        ImageView avatar = new ImageView(new Image("https://placehold.co/100x100?text=" +
                currentStudent.getName().charAt(0)));
        avatar.setFitWidth(80);
        avatar.setFitHeight(80);
        Label profileTitle = new Label("Student Profile");
        profileTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        profileHeader.getChildren().addAll(avatar, profileTitle);

        // Profile Info - View Mode
        VBox profileInfoView = new VBox(5);
        profileInfoView.setId("profileInfoView");
        profileInfoView.getChildren().addAll(
                createInfoRow("Name:", currentStudent.getName()),
                createInfoRow("ID:", currentStudent.getId()),
                createInfoRow("Major:", currentStudent.getMajor())
        );

        // Profile Info - Edit Mode
        VBox profileInfoEdit = new VBox(5);
        profileInfoEdit.setId("profileInfoEdit");
        profileInfoEdit.setVisible(false);
        profileInfoEdit.setManaged(false);
        TextField nameField = new TextField(currentStudent.getName());
        TextField majorField = new TextField(currentStudent.getMajor());
        profileInfoEdit.getChildren().addAll(
                new HBox(5, new Label("Name:"), nameField),
                new HBox(5, new Label("ID:"), new Label(currentStudent.getId())), // ID is not editable
                new HBox(5, new Label("Major:"), majorField)
        );

        // Action Buttons
        HBox profileButtons = new HBox(10);
        Button editButton = new Button("Edit Profile");
        Button saveButton = new Button("Save");
        saveButton.setVisible(false);
        saveButton.setManaged(false);

        editButton.setOnAction(e -> {
            profileInfoView.setVisible(false);
            profileInfoView.setManaged(false);
            profileInfoEdit.setVisible(true);
            profileInfoEdit.setManaged(true);
            editButton.setVisible(false);
            editButton.setManaged(false);
            saveButton.setVisible(true);
            saveButton.setManaged(true);
        });

        saveButton.setOnAction(e -> {
            currentStudent.nameProperty().set(nameField.getText());
            currentStudent.majorProperty().set(majorField.getText());

            profileInfoView.getChildren().setAll(
                    createInfoRow("Name:", currentStudent.getName()),
                    createInfoRow("ID:", currentStudent.getId()),
                    createInfoRow("Major:", currentStudent.getMajor())
            );

            profileInfoView.setVisible(true);
            profileInfoView.setManaged(true);
            profileInfoEdit.setVisible(false);
            profileInfoEdit.setManaged(false);
            editButton.setVisible(true);
            editButton.setManaged(true);
            saveButton.setVisible(false);
            saveButton.setManaged(false);
            showAlert("Profile Updated", "Your profile has been successfully updated.");
        });

        profileButtons.getChildren().addAll(editButton, saveButton);
        profileBox.getChildren().addAll(profileHeader, profileInfoView, profileInfoEdit, profileButtons);

        // Quick Actions
        HBox quickActions = new HBox(15);
        quickActions.setAlignment(Pos.CENTER);

        Button viewSchedule = new Button("View Schedule");
        Button viewPayments = new Button("View Payments");

        viewSchedule.setStyle("-fx-base: #2ecc71;");
        viewPayments.setStyle("-fx-base: #3498db;");

        // SETTING THE CORRECT ACTION HANDLERS
        // This is the key fix. We use the 'mainTabs' object to select the correct tab by its index.
        viewSchedule.setOnAction(e -> mainTabs.getSelectionModel().select(1)); // Schedule tab is at index 1
        viewPayments.setOnAction(e -> mainTabs.getSelectionModel().select(4)); // Payments tab is at index 4

        quickActions.getChildren().addAll(viewSchedule, viewPayments);

        dashboard.getChildren().addAll(new TitledPane("My Profile", profileBox), new TitledPane("Quick Actions", quickActions));
        return dashboard;
    }

    private BorderPane createScheduleView() {
        BorderPane schedulePane = new BorderPane();

        // Week Navigation
        HBox weekNav = new HBox(10);
        weekNav.setPadding(new Insets(10));
        weekNav.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        Button prevWeek = new Button("Previous Week");
        Button nextWeek = new Button("Next Week");
        Label weekLabel = new Label("Week of Sept 18, 2023");
        weekLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        weekNav.getChildren().addAll(prevWeek, spacer, weekLabel, nextWeek);
        schedulePane.setTop(weekNav);

        // Schedule Grid
        GridPane scheduleGrid = new GridPane();
        scheduleGrid.setHgap(10);
        scheduleGrid.setVgap(10);
        scheduleGrid.setPadding(new Insets(15));

        // Column Headers
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setStyle("-fx-font-weight: bold;");
            scheduleGrid.add(dayLabel, i+1, 0);
        }

        // Time Slots and dynamic class population
        String[] times = {"8:00 AM", "10:00 AM", "12:00 PM", "2:00 PM", "4:00 PM"};
        Random rand = new Random();
        for (int i = 0; i < times.length; i++) {
            scheduleGrid.add(new Label(times[i]), 0, i+1);

            for (int j = 0; j < days.length; j++) {
                if (rand.nextDouble() > 0.6) { // Randomly populate some classes
                    Course course = currentStudent.getCourses().get(rand.nextInt(currentStudent.getCourses().size()));
                    Label classLabel = new Label(course.getCode() + "\nRoom " + (100 + rand.nextInt(100)));
                    classLabel.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 5; -fx-padding: 5;");
                    scheduleGrid.add(classLabel, j+1, i+1);
                }
            }
        }

        schedulePane.setCenter(new ScrollPane(scheduleGrid));
        return schedulePane;
    }

    private BorderPane createGradesView() {
        BorderPane gradesPane = new BorderPane();

        // Create table
        TableView<Course> table = new TableView<>();
        table.setItems(currentStudent.getCourses());

        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(cellData -> cellData.getValue().codeProperty());

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Course, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(cellData -> {
            // Assign random grades for demo
            String[] grades = {"A", "A-", "B+", "B", "B-", "C+", "C"};
            return new SimpleStringProperty(grades[new Random().nextInt(grades.length)]);
        });

        table.getColumns().addAll(codeCol, nameCol, gradeCol);

        // GPA Summary
        Label gpaLabel = new Label("Current GPA: 3.42");
        gpaLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        HBox summaryBox = new HBox(gpaLabel);
        summaryBox.setPadding(new Insets(10));

        gradesPane.setTop(summaryBox);
        gradesPane.setCenter(table);
        return gradesPane;
    }

    // Helper Methods
    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(5);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        row.getChildren().addAll(lbl, new Label(value));
        return row;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}