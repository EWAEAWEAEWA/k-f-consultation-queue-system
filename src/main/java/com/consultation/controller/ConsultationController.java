package com.consultation.controller;

import com.consultation.model.User;
import com.consultation.model.Appointment;
import com.consultation.model.QueueManager;
import com.consultation.model.TimeSlot;
import com.consultation.model.Notification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ConsultationController {
    private Map<String, User> users;
    private Map<String, QueueManager> queues;
    private Map<String, Map<LocalDate, List<TimeSlot>>> professorSchedules;
    private Map<Integer, Appointment> appointments;
    private Map<String, List<Notification>> userNotifications;
    private int nextAppointmentId;

    public ConsultationController() {
        users = new HashMap<>();
        queues = new HashMap<>();
        professorSchedules = new HashMap<>();
        appointments = new HashMap<>();
        userNotifications = new HashMap<>();
        nextAppointmentId = 1;
        initializeTimeSlots();
    }

    private void initializeTimeSlots() {
        // Initialize time slots for next 7 days
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            for (User user : users.values()) {
                if (user.getRole().equals("PROFESSOR") || user.getRole().equals("COUNSELOR")) {
                    Map<LocalDate, List<TimeSlot>> schedule = professorSchedules
                        .computeIfAbsent(user.getUsername(), k -> new HashMap<>());
                    
                    List<TimeSlot> slots = new ArrayList<>();
                    // Morning slots: 9:00-12:00
                    slots.add(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0)));
                    slots.add(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0)));
                    slots.add(new TimeSlot(LocalTime.of(11, 0), LocalTime.of(12, 0)));
                    // Afternoon slots: 1:00-4:00
                    slots.add(new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0)));
                    slots.add(new TimeSlot(LocalTime.of(14, 0), LocalTime.of(15, 0)));
                    slots.add(new TimeSlot(LocalTime.of(15, 0), LocalTime.of(16, 0)));
                    
                    schedule.put(date, slots);
                }
            }
        }
    }

    public User registerUser(String username, String password, String role, String name, String email) {
        if (users.containsKey(username)) {
            return null;
        }
        User user = new User(username, password, role, name, email);
        users.put(username, user);
        if (role.equals("PROFESSOR") || role.equals("COUNSELOR")) {
            queues.put(username, new QueueManager());
        }
        return user;
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful for user: " + username);
            System.out.println("Total appointments in system: " + appointments.size());
            return user;
        }
        System.out.println("Login failed for user: " + username);
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Appointment> getUserAppointments(User user) {
        System.out.println("Getting appointments for user: " + user.getUsername());
        System.out.println("Total appointments in system: " + appointments.size());
        List<Appointment> userAppointments = new ArrayList<>();
        for (Appointment appointment : appointments.values()) {
            System.out.println("Checking appointment: " + appointment.getId() + 
                " Student: " + appointment.getStudent().getUsername() + 
                " Professor: " + appointment.getProfessorOrCounselor().getUsername());
            if (appointment.getStudent().getUsername().equals(user.getUsername()) || 
                appointment.getProfessorOrCounselor().getUsername().equals(user.getUsername())) {
                userAppointments.add(appointment);
                System.out.println("Added appointment to user's list");
            }
        }
        System.out.println("Found " + userAppointments.size() + " appointments for user");
        return userAppointments;
    }

    public int getQueueSize(String username) {
        QueueManager queue = queues.get(username);
        return queue != null ? queue.getQueueSize() : 0;
    }

    public QueueManager getQueueManager(String username) {
        return queues.get(username);
    }

    public List<TimeSlot> getAvailableTimeSlots(String username, LocalDate date) {
        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(username);
        if (schedule != null) {
            List<TimeSlot> slots = schedule.get(date);
            if (slots != null) {
                return slots.stream()
                    .filter(TimeSlot::isAvailable)
                    .toList();
            }
        }
        return new ArrayList<>();
    }

    public Appointment createAppointment(User student, User professorOrCounselor, 
            String subject, int duration) {
        System.out.println("\nAttempting to create appointment:");
        System.out.println("Student: " + student.getUsername());
        System.out.println("Professor/Counselor: " + professorOrCounselor.getUsername());
        System.out.println("Subject: " + subject);
        System.out.println("Duration: " + duration + " minutes");

        if (professorOrCounselor.getRole().equals("STUDENT")) {
            System.out.println("Failed: Professor/Counselor is a student");
            return null;
        }

        // Check subject restrictions
        if (professorOrCounselor.getRole().equals("PROFESSOR")) {
            if (!professorOrCounselor.canTeach(subject)) {
                System.out.println("Failed: Professor does not teach this subject");
                return null;
            }
            if (!student.isEnrolledIn(subject)) {
                System.out.println("Failed: Student is not enrolled in this subject");
                return null;
            }
        }

        // Find the next available time slot
        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(professorOrCounselor.getUsername());
        if (schedule == null) {
            System.out.println("Failed: No schedule found for " + professorOrCounselor.getUsername());
            return null;
        }

        // Sort dates to ensure we check in chronological order
        List<LocalDate> sortedDates = new ArrayList<>(schedule.keySet());
        Collections.sort(sortedDates);

        TimeSlot selectedSlot = null;
        LocalDateTime appointmentTime = null;

        // Find the first available slot
        for (LocalDate date : sortedDates) {
            List<TimeSlot> slots = schedule.get(date);
            for (TimeSlot slot : slots) {
                if (slot.canAccommodate(duration)) {
                    selectedSlot = slot;
                    appointmentTime = LocalDateTime.of(date, slot.getStartTime());
                    System.out.println("Found available slot: " + appointmentTime);
                    break;
                }
            }
            if (selectedSlot != null) break;
        }

        if (selectedSlot == null) {
            System.out.println("Failed: No available slots found");
            return null;
        }

        // Create appointment
        Appointment appointment = new Appointment(
            nextAppointmentId++,
            student,
            professorOrCounselor,
            appointmentTime,
            subject,
            duration
        );

        try {
            // Add to time slot
            selectedSlot.addAppointment(appointment);
            
            // Add to appointments map
            appointments.put(appointment.getId(), appointment);
            
            // Add to FIFO queue
            QueueManager queue = queues.get(professorOrCounselor.getUsername());
            if (queue != null) {
                queue.addAppointment(appointment);
                System.out.println("Appointment created successfully with ID: " + appointment.getId());
                return appointment;
            } else {
                System.out.println("Warning: No queue found for " + professorOrCounselor.getUsername());
                // Rollback time slot if queue doesn't exist
                selectedSlot.removeAppointment();
                appointments.remove(appointment.getId());
                return null;
            }
        } catch (IllegalStateException e) {
            System.out.println("Failed to create appointment: " + e.getMessage());
            return null;
        }
    }

    public boolean cancelAppointment(Appointment appointment) {
        if (appointments.remove(appointment.getId()) != null) {
            QueueManager queue = queues.get(appointment.getProfessorOrCounselor().getUsername());
            if (queue != null) {
                queue.removeAppointment(appointment);
            }
            return true;
        }
        return false;
    }

    public Appointment getNextAppointment(String username) {
        QueueManager queue = queues.get(username);
        if (queue != null) {
            Appointment nextAppointment = queue.getNextAppointment();
            if (nextAppointment != null) {
                nextAppointment.setStatus("IN_PROGRESS");
                // Remove from time slot
                Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(username);
                if (schedule != null) {
                    List<TimeSlot> slots = schedule.get(nextAppointment.getAppointmentTime().toLocalDate());
                    if (slots != null) {
                        for (TimeSlot slot : slots) {
                            if (slot.getAppointment() != null && 
                                slot.getAppointment().getId() == nextAppointment.getId()) {
                                slot.removeAppointment();
                                break;
                            }
                        }
                    }
                }
            }
            return nextAppointment;
        }
        return null;
    }

    public boolean updateAppointmentStatus(Appointment appointment, String status) {
        if (appointments.containsKey(appointment.getId())) {
            appointment.setStatus(status);
            return true;
        }
        return false;
    }

    public boolean setPriority(Appointment appointment, boolean priority) {
        QueueManager queue = queues.get(appointment.getProfessorOrCounselor().getUsername());
        if (queue != null) {
            // If setting priority, move to front of priority queue
            if (priority) {
                // Remove from current time slot
                Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(
                    appointment.getProfessorOrCounselor().getUsername());
                if (schedule != null) {
                    // Find all appointments that need to be moved
                    List<Appointment> appointmentsToMove = new ArrayList<>();
                    LocalDateTime currentTime = appointment.getAppointmentTime();
                    
                    // Find all regular appointments before the priority appointment
                    for (Map.Entry<LocalDate, List<TimeSlot>> entry : schedule.entrySet()) {
                        for (TimeSlot slot : entry.getValue()) {
                            LocalDateTime slotTime = LocalDateTime.of(entry.getKey(), slot.getStartTime());
                            if (slotTime.isBefore(currentTime) && slot.getAppointment() != null) {
                                Appointment existingAppointment = slot.getAppointment();
                                if (!existingAppointment.isPriority()) {
                                    appointmentsToMove.add(existingAppointment);
                                }
                            }
                        }
                    }

                    // Sort appointments by time
                    appointmentsToMove.sort((a1, a2) -> a1.getAppointmentTime().compareTo(a2.getAppointmentTime()));

                    // Move each appointment to the next available slot
                    for (Appointment appToMove : appointmentsToMove) {
                        // Remove from current slot
                        for (TimeSlot slot : schedule.get(appToMove.getAppointmentTime().toLocalDate())) {
                            if (slot.getAppointment() != null && 
                                slot.getAppointment().getId() == appToMove.getId()) {
                                slot.removeAppointment();
                                break;
                            }
                        }

                        // Find next available slot
                        LocalDateTime nextAvailableTime = null;
                        TimeSlot nextAvailableSlot = null;
                        for (Map.Entry<LocalDate, List<TimeSlot>> entry : schedule.entrySet()) {
                            for (TimeSlot slot : entry.getValue()) {
                                LocalDateTime slotTime = LocalDateTime.of(entry.getKey(), slot.getStartTime());
                                if (slot.isAvailable() && 
                                    (nextAvailableTime == null || 
                                     slotTime.isAfter(appToMove.getAppointmentTime()))) {
                                    nextAvailableTime = slotTime;
                                    nextAvailableSlot = slot;
                                    break;
                                }
                            }
                            if (nextAvailableSlot != null) break;
                        }

                        if (nextAvailableSlot != null) {
                            nextAvailableSlot.addAppointment(appToMove);
                            appToMove.setAppointmentTime(nextAvailableTime);
                            
                            // Notify the student about the change
                            notifyAppointmentChange(appToMove, 
                                "Your appointment has been rescheduled to " + nextAvailableTime + 
                                " due to a priority appointment");
                        }
                    }

                    // Move priority appointment to earliest available slot
                    LocalDateTime earliestTime = null;
                    TimeSlot earliestSlot = null;
                    for (Map.Entry<LocalDate, List<TimeSlot>> entry : schedule.entrySet()) {
                        for (TimeSlot slot : entry.getValue()) {
                            LocalDateTime slotTime = LocalDateTime.of(entry.getKey(), slot.getStartTime());
                            if (slot.isAvailable() && 
                                (earliestTime == null || 
                                 slotTime.isBefore(earliestTime))) {
                                earliestTime = slotTime;
                                earliestSlot = slot;
                            }
                        }
                    }

                    if (earliestSlot != null) {
                        earliestSlot.addAppointment(appointment);
                        appointment.setAppointmentTime(earliestTime);
                        
                        // Notify the student about the priority status
                        notifyAppointmentChange(appointment, 
                            "Your appointment has been marked as priority and scheduled for " + earliestTime);
                    }
                }
            }
            return queue.setPriority(appointment, priority);
        }
        return false;
    }

    private void notifyAppointmentChange(Appointment appointment, String message) {
        Notification notification = new Notification(
            LocalDateTime.now(),
            message
        );
        
        // Add notification to student's list
        String studentUsername = appointment.getStudent().getUsername();
        userNotifications.computeIfAbsent(studentUsername, k -> new ArrayList<>())
            .add(notification);
    }

    public int getEstimatedWaitTime(String username) {
        QueueManager queue = queues.get(username);
        return queue != null ? queue.getEstimatedWaitTime() : 0;
    }

    public void initializeAllTimeSlots() {
        System.out.println("\nInitializing time slots...");
        professorSchedules.clear();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            System.out.println("\nCreating slots for date: " + date);
            for (User user : users.values()) {
                if (user.getRole().equals("PROFESSOR") || user.getRole().equals("COUNSELOR")) {
                    System.out.println("Creating slots for " + user.getUsername());
                    Map<LocalDate, List<TimeSlot>> schedule = professorSchedules
                        .computeIfAbsent(user.getUsername(), k -> new HashMap<>());
                    
                    List<TimeSlot> slots = new ArrayList<>();
                    // Morning slots: 9:00-12:00
                    slots.add(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0)));
                    slots.add(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0)));
                    slots.add(new TimeSlot(LocalTime.of(11, 0), LocalTime.of(12, 0)));
                    // Afternoon slots: 1:00-4:00
                    slots.add(new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0)));
                    slots.add(new TimeSlot(LocalTime.of(14, 0), LocalTime.of(15, 0)));
                    slots.add(new TimeSlot(LocalTime.of(15, 0), LocalTime.of(16, 0)));
                    
                    schedule.put(date, slots);
                    System.out.println("Created " + slots.size() + " slots for " + user.getUsername());
                }
            }
        }
        System.out.println("Time slot initialization complete");
    }

    public List<Notification> getUserNotifications(String username) {
        return userNotifications.getOrDefault(username, new ArrayList<>());
    }
} 