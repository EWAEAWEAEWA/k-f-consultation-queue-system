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
import java.util.stream.Collectors;

public class ConsultationController {
    private Map<String, User> users;
    private Map<String, QueueManager> queues;
    private Map<String, Map<LocalDate, List<TimeSlot>>> professorSchedules;
    private Map<Integer, Appointment> appointments;
    private Map<String, List<Notification>> userNotifications;
    private int nextAppointmentId;
    private List<TimeSlot> timeSlots;
    private static final int MAX_APPOINTMENT_DURATION = 60; // Maximum appointment duration in minutes
    private static final int MIN_APPOINTMENT_DURATION = 15; // Minimum appointment duration in minutes
    private static final int TIME_SLOT_INTERVAL = 15; // Time slots are divided into 15-minute intervals
    private static final int MAX_DAYS_AHEAD = 7; // Maximum number of days to schedule ahead
    private static final LocalTime WORKDAY_START = LocalTime.of(9, 0);
    private static final LocalTime WORKDAY_END = LocalTime.of(16, 0);
    private static final LocalTime LUNCH_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_END = LocalTime.of(13, 0);

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
        System.out.println("\nInitializing time slots...");
        professorSchedules.clear();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < MAX_DAYS_AHEAD; i++) {
            LocalDate date = today.plusDays(i);
            System.out.println("\nCreating slots for date: " + date);
            
            for (User user : users.values()) {
                if (user.getRole().equals("PROFESSOR") || user.getRole().equals("COUNSELOR")) {
                    System.out.println("Creating slots for " + user.getUsername());
                    Map<LocalDate, List<TimeSlot>> schedule = professorSchedules
                        .computeIfAbsent(user.getUsername(), k -> new HashMap<>());
                    
                    List<TimeSlot> slots = new ArrayList<>();
                    LocalTime currentTime = WORKDAY_START;
                    
                    while (currentTime.isBefore(WORKDAY_END)) {
                        // Skip lunch break
                        if (currentTime.equals(LUNCH_START)) {
                            currentTime = LUNCH_END;
                            continue;
                        }
                        
                        LocalTime endTime = currentTime.plusMinutes(TIME_SLOT_INTERVAL);
                        if (endTime.isAfter(WORKDAY_END)) break;
                        
                        slots.add(new TimeSlot(currentTime, endTime, user));
                        currentTime = endTime;
                    }
                    
                    schedule.put(date, slots);
                    System.out.println("Created " + slots.size() + " slots for " + user.getUsername());
                }
            }
        }
        
        timeSlots = new ArrayList<>(professorSchedules.values().stream()
            .flatMap(schedule -> schedule.values().stream())
            .flatMap(List::stream)
            .toList());
        System.out.println("Time slot initialization complete");
    }

    public void refreshTimeSlots() {
        // Clean up old appointments and time slots
        cleanupOldAppointments();
        // Reinitialize time slots for the next MAX_DAYS_AHEAD days
        initializeTimeSlots();
    }

    private void cleanupOldAppointments() {
        LocalDateTime now = LocalDateTime.now();
        List<Integer> appointmentsToRemove = new ArrayList<>();
        
        // Find appointments to remove
        for (Appointment appointment : appointments.values()) {
            // Remove completed appointments older than 7 days
            if (appointment.getStatus().equals("COMPLETED") && 
                appointment.getAppointmentTime().plusDays(7).isBefore(now)) {
                appointmentsToRemove.add(appointment.getId());
            }
            // Remove cancelled appointments and free up their time slots
            else if (appointment.getStatus().equals("CANCELLED")) {
                // Free up the time slot before removing the appointment
                Map<LocalDate, List<TimeSlot>> schedule = professorSchedules
                    .get(appointment.getProfessorOrCounselor().getUsername());
                if (schedule != null) {
                    LocalDate appointmentDate = appointment.getAppointmentTime().toLocalDate();
                    List<TimeSlot> slots = schedule.get(appointmentDate);
                    if (slots != null) {
                        for (TimeSlot slot : slots) {
                            if (slot.getAppointment() != null && 
                                slot.getAppointment().getId() == appointment.getId()) {
                                slot.removeAppointment();
                                break;
                            }
                        }
                    }
                }
                appointmentsToRemove.add(appointment.getId());
            }
            // Handle missed appointments (no need to free up slots as time has passed)
            else if (appointment.getStatus().equals("PENDING") && 
                     appointment.getAppointmentTime().isBefore(now)) {
                appointment.setStatus("MISSED");
                createNotification(appointment.getStudent().getUsername(),
                    "Your appointment for " + appointment.getSubject() + " was missed.");
                createNotification(appointment.getProfessorOrCounselor().getUsername(),
                    "Appointment with " + appointment.getStudent().getName() + " was missed.");
            }
        }
        
        // Remove the identified appointments
        for (Integer appointmentId : appointmentsToRemove) {
            appointments.remove(appointmentId);
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

        // Validate duration
        if (duration < MIN_APPOINTMENT_DURATION || duration > MAX_APPOINTMENT_DURATION) {
            System.out.println("Failed: Invalid duration. Must be between " + MIN_APPOINTMENT_DURATION + 
                " and " + MAX_APPOINTMENT_DURATION + " minutes");
            return null;
        }

        // Round duration to nearest time slot interval
        duration = (int) (Math.ceil((double)duration / TIME_SLOT_INTERVAL) * TIME_SLOT_INTERVAL);

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

        // Find the first available slot that can accommodate the duration
        for (LocalDate date : sortedDates) {
            List<TimeSlot> slots = schedule.get(date);
            for (TimeSlot slot : slots) {
                if (slot.isAvailable() && slot.canAccommodate(duration)) {
                    // Check if this slot overlaps with any existing appointments
                    boolean hasOverlap = false;
                    for (Appointment existingApp : appointments.values()) {
                        if (existingApp.getProfessorOrCounselor().equals(professorOrCounselor) &&
                            existingApp.getStatus().equals("PENDING") &&
                            existingApp.getAppointmentTime().toLocalDate().equals(date)) {
                            
                            LocalDateTime slotStart = LocalDateTime.of(date, slot.getStartTime());
                            LocalDateTime slotEnd = slotStart.plusMinutes(duration);
                            LocalDateTime existingStart = existingApp.getAppointmentTime();
                            LocalDateTime existingEnd = existingStart.plusMinutes(existingApp.getEstimatedDuration());
                            
                            if ((slotStart.isBefore(existingEnd) && slotEnd.isAfter(existingStart))) {
                                hasOverlap = true;
                                break;
                            }
                        }
                    }
                    
                    if (!hasOverlap) {
                        selectedSlot = slot;
                        appointmentTime = LocalDateTime.of(date, slot.getStartTime());
                        System.out.println("Found available slot: " + appointmentTime);
                        break;
                    }
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
        if (appointment == null || !appointment.getStatus().equals("PENDING")) {
            return false;
        }

        // Remove from queue if it exists
        QueueManager queue = getQueueManager(appointment.getProfessorOrCounselor().getUsername());
        if (queue != null) {
            queue.removeAppointment(appointment);
        }

        // Update appointment status
        appointment.setStatus("CANCELLED");

        // Free up the time slot
        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(appointment.getProfessorOrCounselor().getUsername());
        if (schedule != null) {
            LocalDate appointmentDate = appointment.getAppointmentTime().toLocalDate();
            List<TimeSlot> slots = schedule.get(appointmentDate);
            if (slots != null) {
                for (TimeSlot slot : slots) {
                    if (slot.getAppointment() != null && slot.getAppointment().getId() == appointment.getId()) {
                        slot.removeAppointment();
                        break;
                    }
                }
            }
        }

        // Create notification for both parties
        createNotification(appointment.getStudent().getUsername(), 
            "Appointment cancelled: " + appointment.getSubject() + " with " + 
            appointment.getProfessorOrCounselor().getName());
        createNotification(appointment.getProfessorOrCounselor().getUsername(),
            "Appointment cancelled: " + appointment.getSubject() + " with " + 
            appointment.getStudent().getName());

        return true;
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
            String oldStatus = appointment.getStatus();
            appointment.setStatus(status);
            
            // Only free up time slot if appointment is being cancelled
            if (status.equals("CANCELLED")) {
                Map<LocalDate, List<TimeSlot>> schedule = professorSchedules
                    .get(appointment.getProfessorOrCounselor().getUsername());
                if (schedule != null) {
                    LocalDate appointmentDate = appointment.getAppointmentTime().toLocalDate();
                    List<TimeSlot> slots = schedule.get(appointmentDate);
                    if (slots != null) {
                        for (TimeSlot slot : slots) {
                            if (slot.getAppointment() != null && 
                                slot.getAppointment().getId() == appointment.getId()) {
                                slot.removeAppointment();
                                break;
                            }
                        }
                    }
                }
            }
            
            // Create notification for status change
            if (!oldStatus.equals(status)) {
                String message = "Appointment status changed from " + oldStatus + " to " + status;
                createNotification(appointment.getStudent().getUsername(), message);
                createNotification(appointment.getProfessorOrCounselor().getUsername(), message);
            }
            
            return true;
        }
        return false;
    }

    public boolean setPriority(Appointment appointment, boolean isPriority) {
        if (appointment == null) return false;
        
        // Get the queue manager for the professor/counselor
        QueueManager queueManager = queues.get(appointment.getProfessorOrCounselor().getUsername());
        if (queueManager == null) return false;
        
        // If setting priority
        if (isPriority && !appointment.isPriority()) {
            // Find the earliest available slot
            LocalDateTime currentTime = appointment.getAppointmentTime();
            LocalDateTime earliestSlot = findNextAvailableSlot(appointment.getProfessorOrCounselor(), LocalDateTime.now());
            
            if (earliestSlot != null) {
                // Get all regular appointments that need to be moved
                List<Appointment> appointmentsToMove = new ArrayList<>();
                for (Appointment app : queueManager.getRegularQueue()) {
                    if (app.getAppointmentTime().isBefore(currentTime)) {
                        appointmentsToMove.add(app);
                    }
                }
                
                // Sort appointments by time to maintain FIFO order
                appointmentsToMove.sort(Comparator.comparing(Appointment::getAppointmentTime));
                
                // Move each appointment to the next available slot
                LocalDateTime nextSlot = earliestSlot;
                for (Appointment app : appointmentsToMove) {
                    // Find next available slot after the current one
                    LocalDateTime availableSlot = findNextAvailableSlot(appointment.getProfessorOrCounselor(), nextSlot);
                    if (availableSlot != null) {
                        // Free up the old time slot
                        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules
                            .get(app.getProfessorOrCounselor().getUsername());
                        if (schedule != null) {
                            LocalDate oldDate = app.getAppointmentTime().toLocalDate();
                            List<TimeSlot> slots = schedule.get(oldDate);
                            if (slots != null) {
                                for (TimeSlot slot : slots) {
                                    if (slot.getAppointment() != null && 
                                        slot.getAppointment().getId() == app.getId()) {
                                        slot.removeAppointment();
                                        break;
                                    }
                                }
                            }
                        }
                        
                        // Update appointment time
                        app.setAppointmentTime(availableSlot);
                        notifyAppointmentChange(app, "Your appointment has been rescheduled to " + 
                            availableSlot.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + 
                            " due to a priority appointment.");
                        nextSlot = availableSlot;
                    }
                }
                
                // Move the priority appointment to the earliest slot
                appointment.setAppointmentTime(earliestSlot);
                appointment.setPriority(true);
                queueManager.addToPriorityQueue(appointment);
                
                // Sort priority queue to maintain FIFO order
                List<Appointment> priorityList = new ArrayList<>(queueManager.getPriorityQueue());
                priorityList.sort(Comparator.comparing(Appointment::getAppointmentTime));
                queueManager.getPriorityQueue().clear();
                queueManager.getPriorityQueue().addAll(priorityList);
                
                return true;
            }
        } 
        // If removing priority
        else if (!isPriority && appointment.isPriority()) {
            appointment.setPriority(false);
            queueManager.removeFromPriorityQueue(appointment);
            return true;
        }
        
        return false;
    }

    private LocalDateTime findNextAvailableSlot(User professorOrCounselor, LocalDateTime currentTime) {
        // Find the next available slot after currentTime
        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(professorOrCounselor.getUsername());
        if (schedule == null) return null;

        // Sort dates to ensure chronological order
        List<LocalDate> sortedDates = new ArrayList<>(schedule.keySet());
        Collections.sort(sortedDates);

        for (LocalDate date : sortedDates) {
            // Skip dates before current date
            if (date.isBefore(currentTime.toLocalDate())) continue;

            List<TimeSlot> slots = schedule.get(date);
            for (TimeSlot slot : slots) {
                LocalDateTime slotStart = LocalDateTime.of(date, slot.getStartTime());
                
                // Skip slots before current time on the same day
                if (date.equals(currentTime.toLocalDate()) && slotStart.isBefore(currentTime)) continue;

                // Check if slot is available and doesn't overlap with existing appointments
                if (slot.isAvailable()) {
                    boolean hasOverlap = false;
                    for (Appointment existingApp : appointments.values()) {
                        if (existingApp.getProfessorOrCounselor().equals(professorOrCounselor) &&
                            existingApp.getStatus().equals("PENDING") &&
                            existingApp.getAppointmentTime().toLocalDate().equals(date)) {
                            
                            LocalDateTime existingStart = existingApp.getAppointmentTime();
                            LocalDateTime existingEnd = existingStart.plusMinutes(existingApp.getEstimatedDuration());
                            
                            if ((slotStart.isBefore(existingEnd) && 
                                 slotStart.plusMinutes(existingApp.getEstimatedDuration()).isAfter(existingStart))) {
                                hasOverlap = true;
                                break;
                            }
                        }
                    }
                    
                    if (!hasOverlap) {
                        return slotStart;
                    }
                }
            }
        }
        
        return null;
    }

    private void notifyAppointmentChange(Appointment appointment, String message) {
        Notification notification = new Notification(message);
        
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
                    slots.add(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), user));
                    slots.add(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0), user));
                    slots.add(new TimeSlot(LocalTime.of(11, 0), LocalTime.of(12, 0), user));
                    // Afternoon slots: 1:00-4:00
                    slots.add(new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0), user));
                    slots.add(new TimeSlot(LocalTime.of(14, 0), LocalTime.of(15, 0), user));
                    slots.add(new TimeSlot(LocalTime.of(15, 0), LocalTime.of(16, 0), user));
                    
                    schedule.put(date, slots);
                    System.out.println("Created " + slots.size() + " slots for " + user.getUsername());
                }
            }
        }
        timeSlots = new ArrayList<>(professorSchedules.values().stream()
            .flatMap(schedule -> schedule.values().stream())
            .flatMap(List::stream)
            .toList());
        System.out.println("Time slot initialization complete");
    }

    public List<Notification> getUserNotifications(String username) {
        return userNotifications.getOrDefault(username, new ArrayList<>());
    }

    public List<Appointment> getFilteredAppointments(User user, String statusFilter) {
        List<Appointment> appointments = getUserAppointments(user);
        return appointments.stream()
            .filter(app -> statusFilter.equals("All") || app.getStatus().equalsIgnoreCase(statusFilter))
            .sorted(Comparator.comparing(Appointment::getAppointmentTime))
            .collect(Collectors.toList());
    }

    public Appointment findAppointmentFromQueueTable(String username, int selectedRow) {
        QueueManager qm = getQueueManager(username);
        if (qm == null) return null;

        List<Appointment> priorityQueue = new ArrayList<>(qm.getPriorityQueue());
        List<Appointment> regularQueue = new ArrayList<>(qm.getRegularQueue());

        priorityQueue.sort(Comparator.comparing(Appointment::getAppointmentTime));
        regularQueue.sort(Comparator.comparing(Appointment::getAppointmentTime));

        List<Appointment> displayedQueue = new ArrayList<>();
        displayedQueue.addAll(priorityQueue);
        displayedQueue.addAll(regularQueue);

        List<Appointment> currentlyVisibleInTable = displayedQueue.stream()
            .filter(a -> a.getStatus().equals("PENDING") || a.getStatus().equals("IN_PROGRESS"))
            .collect(Collectors.toList());

        if (selectedRow >= 0 && selectedRow < currentlyVisibleInTable.size()) {
            return currentlyVisibleInTable.get(selectedRow);
        }
        return null;
    }

    public void createNotification(String username, String message) {
        List<Notification> notifications = userNotifications.computeIfAbsent(username, k -> new ArrayList<>());
        notifications.add(new Notification(message));
    }
} 