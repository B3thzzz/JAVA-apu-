//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
package src;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Advanced Tuition Centre Management System");
        Tutor tutor = null;
        while (tutor == null) {
            tutor = Tutor.login(scanner);
            if (tutor == null) {
                System.out.println("Login failed. Please try again.");
            }
        }
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- Tutor Menu ---");
            System.out.println("1. Add Class");
            System.out.println("2. Update Class");
            System.out.println("3. Delete Class");
            System.out.println("4. View Enrolled Students");
            System.out.println("5. Update Profile");
            System.out.println("6. Logout");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    tutor.addClass(scanner);
                    break;
                case "2":
                    tutor.updateClass(scanner);
                    break;
                case "3":
                    tutor.deleteClass(scanner);
                    break;
                case "4":
                    tutor.viewEnrolledStudents(scanner);
                    break;
                case "5":
                    tutor.updateProfile(scanner);
                    break;
                case "6":
                    exit = true;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
        System.out.println("Thank you for using the system.");
    }
}