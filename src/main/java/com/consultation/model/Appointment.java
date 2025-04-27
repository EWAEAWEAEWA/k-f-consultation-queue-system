package com.consultation.model;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private User student;
    private User professorOrCounselor;
    private LocalDateTime appointmentTime;
    private String status; // "PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    private boolean isPriority;
    private String subject; // For professor appointments only
    private int estimatedDuration; // in minutes

    public Appointment(int id, User student, User professorOrCounselor, LocalDateTime appointmentTime, 
                      String subject, int estimatedDuration) {
        this.id = id;
        this.student = student;
        this.professorOrCounselor = professorOrCounselor;
        this.appointmentTime = appointmentTime;
        this.status = "PENDING";
        this.isPriority = false;
        this.subject = subject;
        this.estimatedDuration = estimatedDuration;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public User getStudent() {
        return student;
    }

    public User getProfessorOrCounselor() {
        return professorOrCounselor;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public void setPriority(boolean priority) {
        isPriority = priority;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }
} 