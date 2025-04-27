package com.consultation.util;

import com.consultation.controller.ConsultationController;
import com.consultation.model.User;
import java.time.LocalDateTime;

public class DataInitializer {
    public static void initializeData(ConsultationController controller) {
        // Initialize Professors (CS Department)
        controller.registerUser("prof.santos", "pass123", "PROFESSOR", 
            "Prof. Maria Santos", "msantos@tip.edu.ph"); // OS Professor
        controller.registerUser("prof.cruz", "pass123", "PROFESSOR", 
            "Prof. Juan Cruz", "jcruz@tip.edu.ph"); // Data Structures Professor

        // Initialize Counselors
        controller.registerUser("counselor.garcia", "pass123", "COUNSELOR", 
            "Ms. Sofia Garcia", "sgarcia@tip.edu.ph");

        // Initialize Students
        controller.registerUser("student1", "pass123", "STUDENT", 
            "John Doe", "jdoe@tip.edu.ph");
        controller.registerUser("student2", "pass123", "STUDENT", 
            "Jane Smith", "jsmith@tip.edu.ph");

        // Create some initial appointments
        User student1 = controller.login("student1", "pass123");
        User student2 = controller.login("student2", "pass123");
        User profSantos = controller.login("prof.santos", "pass123");
        User profCruz = controller.login("prof.cruz", "pass123");
        User counselorGarcia = controller.login("counselor.garcia", "pass123");

        // Create appointments with Prof. Santos (OS)
        controller.createAppointment(student1, profSantos, 
            LocalDateTime.now().plusMinutes(30), "Operating Systems", 30);
        controller.createAppointment(student2, profSantos, 
            LocalDateTime.now().plusMinutes(60), "Operating Systems", 30);

        // Create appointments with Prof. Cruz (Data Structures)
        controller.createAppointment(student1, profCruz, 
            LocalDateTime.now().plusMinutes(45), "Data Structures", 30);

        // Create appointments with Counselor Garcia
        controller.createAppointment(student2, counselorGarcia, 
            LocalDateTime.now().plusMinutes(15), "Academic Advising", 45);
    }
} 