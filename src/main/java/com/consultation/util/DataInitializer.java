package com.consultation.util;

import com.consultation.controller.ConsultationController;
import com.consultation.model.User;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

public class DataInitializer {
    public static void initializeData(ConsultationController controller) {
        // Initialize Professors (CS Department)
        User profSantos = controller.registerUser("prof.santos", "pass123", "PROFESSOR", 
            "Prof. Maria Santos", "msantos@tip.edu.ph"); // OS Professor
        profSantos.addSubject("Operating Systems");
        
        User profCruz = controller.registerUser("prof.cruz", "pass123", "PROFESSOR", 
            "Prof. Juan Cruz", "jcruz@tip.edu.ph"); // Data Structures Professor
        profCruz.addSubject("Data Structures");

        // Initialize Counselors
        controller.registerUser("counselor.garcia", "pass123", "COUNSELOR", 
            "Ms. Sofia Garcia", "sgarcia@tip.edu.ph");

        // Initialize Students
        User student1 = controller.registerUser("student1", "pass123", "STUDENT", 
            "John Doe", "jdoe@tip.edu.ph");
        student1.addSubject("Operating Systems");
        student1.addSubject("Data Structures");
        
        User student2 = controller.registerUser("student2", "pass123", "STUDENT", 
            "Jane Smith", "jsmith@tip.edu.ph");
        student2.addSubject("Operating Systems");
        student2.addSubject("Data Structures");

        // Initialize time slots for all professors and counselors
        controller.initializeAllTimeSlots();

        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime morningSlot = LocalTime.of(9, 0); // First morning slot
        LocalTime afternoonSlot = LocalTime.of(13, 0); // First afternoon slot

        // Create appointments using direct user references from the controller
        controller.createAppointment(
            student1,
            profSantos,
            "Operating Systems", 30);

        controller.createAppointment(
            student2,
            profSantos,
            "Operating Systems", 30);

        controller.createAppointment(
            student1,
            profCruz,
            "Data Structures", 30);

        controller.createAppointment(
            student2,
            controller.login("counselor.garcia", "pass123"),
            "Academic Advising", 45);

        System.out.println("Initialization complete. Total appointments created: " + 
            controller.getUserAppointments(student1).size());
    }
} 