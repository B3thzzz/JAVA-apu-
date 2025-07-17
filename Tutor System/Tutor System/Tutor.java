package src;

import java.util.*;

public class Tutor {
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

    // Login method (static)
    public static Tutor login(Scanner scanner) {
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            Tutor t = login(username, password);
            if (t != null) {
                System.out.println("Login successful. Welcome, " + t.getName() + "!");
                return t;
            } else {
                attempts++;
                System.out.println("Invalid credentials. Attempts left: " + (3 - attempts));
            }
        }
        System.out.println("Maximum login attempts reached.");
        return null;
    }

    // Login method (static) for GUI
    public static Tutor login(String username, String password) {
        java.util.List<String> lines = src.FileManager.readAllLines("src/tutors.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return new Tutor(parts[0], parts[1], parts[2], parts[3]);
                }
            }
        }
        return null;
    }

    // Profile management (CLI)
    public void updateProfile(Scanner scanner) {
        System.out.print("Enter new name (current: " + name + "): ");
        String newName = scanner.nextLine().trim();
        System.out.print("Enter new contact (current: " + contact + "): ");
        String newContact = scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();
        if (!newName.isEmpty()) this.name = newName;
        if (!newContact.isEmpty()) this.contact = newContact;
        if (!newPassword.isEmpty()) this.password = newPassword;
        java.util.List<String> lines = src.FileManager.readAllLines("src/tutors.txt");
        String updatedLine = username + "," + password + "," + name + "," + contact;
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length >= 4 && parts[0].equals(username)) {
                lines.set(i, updatedLine);
                break;
            }
        }
        src.FileManager.writeAllLines("src/tutors.txt", lines);
        System.out.println("Profile updated successfully!");
    }

    // Add class (CLI)
    public void addClass(Scanner scanner) {
        System.out.print("Enter subject name: ");
        String subject = scanner.nextLine().trim();
        System.out.print("Enter charges: ");
        String chargesStr = scanner.nextLine().trim();
        System.out.print("Enter schedule: ");
        String schedule = scanner.nextLine().trim();
        if (subject.isEmpty() || chargesStr.isEmpty() || schedule.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }
        double charges;
        try {
            charges = Double.parseDouble(chargesStr);
            if (charges < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            System.out.println("Charges must be a positive number.");
            return;
        }
        java.util.List<String> lines = src.FileManager.readAllLines("src/classes.txt");
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
        String newClassLine = newId + "," + subject + "," + charges + "," + schedule + "," + username;
        src.FileManager.appendLine("src/classes.txt", newClassLine);
        System.out.println("Class added successfully!");
    }

    // Update class (CLI)
    public void updateClass(Scanner scanner) {
        java.util.List<String> lines = src.FileManager.readAllLines("src/classes.txt");
        java.util.List<String> myClasses = new java.util.ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[4].equals(username)) {
                myClasses.add(line);
            }
        }
        if (myClasses.isEmpty()) {
            System.out.println("You have no classes to update.");
            return;
        }
        for (int i = 0; i < myClasses.size(); i++) {
            System.out.println((i+1) + ". " + myClasses.get(i));
        }
        System.out.print("Select class number to update: ");
        int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (idx < 0 || idx >= myClasses.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        String[] parts = myClasses.get(idx).split(",");
        System.out.print("Enter new subject name (current: " + parts[1] + "): ");
        String subject = scanner.nextLine().trim();
        System.out.print("Enter new charges (current: " + parts[2] + "): ");
        String chargesStr = scanner.nextLine().trim();
        System.out.print("Enter new schedule (current: " + parts[3] + "): ");
        String schedule = scanner.nextLine().trim();
        if (subject.isEmpty()) subject = parts[1];
        if (chargesStr.isEmpty()) chargesStr = parts[2];
        if (schedule.isEmpty()) schedule = parts[3];
        double charges;
        try {
            charges = Double.parseDouble(chargesStr);
            if (charges < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            System.out.println("Charges must be a positive number.");
            return;
        }
        String updatedLine = parts[0] + "," + subject + "," + charges + "," + schedule + "," + username;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals(myClasses.get(idx))) {
                lines.set(i, updatedLine);
                break;
            }
        }
        src.FileManager.writeAllLines("src/classes.txt", lines);
        System.out.println("Class updated successfully!");
    }

    // Delete class (CLI)
    public void deleteClass(Scanner scanner) {
        java.util.List<String> lines = src.FileManager.readAllLines("src/classes.txt");
        java.util.List<String> myClasses = new java.util.ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[4].equals(username)) {
                myClasses.add(line);
            }
        }
        if (myClasses.isEmpty()) {
            System.out.println("You have no classes to delete.");
            return;
        }
        for (int i = 0; i < myClasses.size(); i++) {
            System.out.println((i+1) + ". " + myClasses.get(i));
        }
        System.out.print("Select class number to delete: ");
        int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (idx < 0 || idx >= myClasses.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        String selected = myClasses.get(idx);
        lines.remove(selected);
        src.FileManager.writeAllLines("src/classes.txt", lines);
        // Remove related enrollments
        String classId = selected.split(",")[0];
        java.util.List<String> enrollments = src.FileManager.readAllLines("src/enrollments.txt");
        java.util.List<String> updatedEnrollments = new java.util.ArrayList<>();
        for (String enroll : enrollments) {
            if (!enroll.startsWith(classId + ",")) {
                updatedEnrollments.add(enroll);
            }
        }
        src.FileManager.writeAllLines("src/enrollments.txt", updatedEnrollments);
        System.out.println("Class deleted successfully!");
    }

    // View enrolled students (CLI)
    public void viewEnrolledStudents(Scanner scanner) {
        java.util.List<String> classLines = src.FileManager.readAllLines("src/classes.txt");
        java.util.List<String> myClasses = new java.util.ArrayList<>();
        for (String line : classLines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[4].equals(username)) {
                myClasses.add(line);
            }
        }
        if (myClasses.isEmpty()) {
            System.out.println("You have no classes.");
            return;
        }
        for (int i = 0; i < myClasses.size(); i++) {
            System.out.println((i+1) + ". " + myClasses.get(i));
        }
        System.out.print("Select class number to view students: ");
        int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (idx < 0 || idx >= myClasses.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        String classId = myClasses.get(idx).split(",")[0];
        java.util.List<String> enrollments = src.FileManager.readAllLines("src/enrollments.txt");
        java.util.List<String> studentIds = new java.util.ArrayList<>();
        for (String enroll : enrollments) {
            String[] parts = enroll.split(",");
            if (parts.length == 2 && parts[0].equals(classId)) {
                studentIds.add(parts[1]);
            }
        }
        if (studentIds.isEmpty()) {
            System.out.println("No students enrolled in this class.");
            return;
        }
        java.util.List<String> students = src.FileManager.readAllLines("src/students.txt");
        System.out.println("Enrolled Students:");
        for (String studentLine : students) {
            String[] parts = studentLine.split(",");
            if (parts.length >= 3 && studentIds.contains(parts[0])) {
                System.out.println("ID: " + parts[0] + ", Name: " + parts[1] + ", Contact: " + parts[2]);
            }
        }
    }
} 