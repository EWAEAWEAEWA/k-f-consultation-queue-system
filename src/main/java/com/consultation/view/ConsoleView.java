package com.consultation.view;

import com.consultation.controller.ConsultationController;
import com.consultation.model.User;
import com.consultation.model.Appointment;

import java.time.LocalDateTime;
import java.util.Scanner;

public class ConsoleView {
    private ConsultationController controller;
    private Scanner scanner;
    private User currentUser;

    public ConsoleView(ConsultationController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n=== Consultation Queue System ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = controller.login(username, password);
        if (currentUser == null) {
            System.out.println("Invalid credentials!");
        }
    }

    private void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Role (STUDENT/PROFESSOR/COUNSELOR): ");
        String role = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        if (controller.registerUser(username, password, role, name, email)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Username already exists!");
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== Welcome, " + currentUser.getName() + " ===");
        System.out.println("1. Create Appointment");
        System.out.println("2. View My Appointments");
        System.out.println("3. Cancel Appointment");
        System.out.println("4. View Queue Status");
        System.out.println("5. Logout");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                createAppointment();
                break;
            case 2:
                viewAppointments();
                break;
            case 3:
                cancelAppointment();
                break;
            case 4:
                viewQueueStatus();
                break;
            case 5:
                currentUser = null;
                break;
            default:
                System.out.println("Invalid option!");
        }
    }

    private void createAppointment() {
        if (!currentUser.getRole().equals("STUDENT")) {
            System.out.println("Only students can create appointments!");
            return;
        }

        System.out.print("Professor/Counselor Username: ");
        String profUsername = scanner.nextLine();
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        System.out.print("Duration (minutes): ");
        int duration = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        LocalDateTime appointmentTime = LocalDateTime.now().plusMinutes(5); // Example: schedule 5 minutes from now
        Appointment appointment = controller.createAppointment(currentUser, 
            new User(profUsername, "", "", "", ""), appointmentTime, subject, duration);

        if (appointment != null) {
            System.out.println("Appointment created successfully!");
        } else {
            System.out.println("Failed to create appointment!");
        }
    }

    private void viewAppointments() {
        // Implementation would show user's appointments
        System.out.println("View appointments functionality to be implemented");
    }

    private void cancelAppointment() {
        // Implementation would allow canceling appointments
        System.out.println("Cancel appointment functionality to be implemented");
    }

    private void viewQueueStatus() {
        if (currentUser.getRole().equals("STUDENT")) {
            System.out.print("Enter Professor/Counselor Username: ");
            String profUsername = scanner.nextLine();
            int waitTime = controller.getEstimatedWaitTime(profUsername);
            if (waitTime >= 0) {
                System.out.println("Estimated wait time: " + waitTime + " minutes");
            } else {
                System.out.println("Invalid professor/counselor username!");
            }
        } else {
            int waitTime = controller.getEstimatedWaitTime(currentUser.getUsername());
            System.out.println("Current queue size: " + waitTime + " minutes");
        }
    }
} 