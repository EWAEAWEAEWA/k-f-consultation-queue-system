package com.consultation.controller;

import com.consultation.model.*;
import java.time.LocalDateTime;
import java.util.*;

public class ConsultationController {
    private Map<String, User> users;
    private Map<String, QueueManager> queues;
    private Map<Integer, Appointment> appointments;
    private int appointmentIdCounter;

    public ConsultationController() {
        this.users = new HashMap<>();
        this.queues = new HashMap<>();
        this.appointments = new HashMap<>();
        this.appointmentIdCounter = 1;
    }

    public boolean registerUser(String username, String password, String role, String name, String email) {
        if (users.containsKey(username)) {
            return false;
        }
        User user = new User(username, password, role, name, email);
        users.put(username, user);
        
        if (!role.equals("STUDENT")) {
            queues.put(username, new QueueManager(user));
        }
        return true;
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public Appointment createAppointment(User student, User professorOrCounselor, 
                                       LocalDateTime appointmentTime, String subject, 
                                       int estimatedDuration) {
        if (!professorOrCounselor.getRole().equals("STUDENT")) {
            Appointment appointment = new Appointment(appointmentIdCounter++, student, 
                                                    professorOrCounselor, appointmentTime, 
                                                    subject, estimatedDuration);
            QueueManager queue = queues.get(professorOrCounselor.getUsername());
            if (queue != null) {
                queue.addAppointment(appointment);
                appointments.put(appointment.getId(), appointment);
            }
            return appointment;
        }
        return null;
    }

    public boolean cancelAppointment(Appointment appointment) {
        User professorOrCounselor = appointment.getProfessorOrCounselor();
        QueueManager queue = queues.get(professorOrCounselor.getUsername());
        if (queue != null) {
            queue.removeAppointment(appointment);
            appointment.setStatus("CANCELLED");
            return true;
        }
        return false;
    }

    public boolean setPriority(Appointment appointment, boolean isPriority) {
        if (appointment.getStatus().equals("PENDING")) {
            appointment.setPriority(isPriority);
            return true;
        }
        return false;
    }

    public int getEstimatedWaitTime(String professorOrCounselorUsername) {
        QueueManager queue = queues.get(professorOrCounselorUsername);
        if (queue != null) {
            return queue.getEstimatedWaitTime();
        }
        return -1;
    }

    public Appointment getNextAppointment(String professorOrCounselorUsername) {
        QueueManager queue = queues.get(professorOrCounselorUsername);
        if (queue != null) {
            return queue.getNextAppointment();
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Appointment> getUserAppointments(User user) {
        List<Appointment> userAppointments = new ArrayList<>();
        for (Appointment appointment : appointments.values()) {
            if (appointment.getStudent().equals(user) || 
                appointment.getProfessorOrCounselor().equals(user)) {
                userAppointments.add(appointment);
            }
        }
        return userAppointments;
    }

    public int getQueueSize(String username) {
        QueueManager queue = queues.get(username);
        return queue != null ? queue.getQueueSize() : 0;
    }

    public QueueManager getQueueManager(String username) {
        return queues.get(username);
    }
} 