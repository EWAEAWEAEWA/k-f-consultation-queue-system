package com.consultation.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;
    private Appointment appointment; // Only one appointment per slot
    private boolean isAvailable;

    public TimeSlot(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.appointment = null;
        this.isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable && appointment == null;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean canAccommodate(int duration) {
        return isAvailable() && 
               (endTime.toSecondOfDay() - startTime.toSecondOfDay()) >= duration * 60;
    }

    public void addAppointment(Appointment appointment) {
        if (canAccommodate(appointment.getEstimatedDuration())) {
            this.appointment = appointment;
            isAvailable = false;
        } else {
            throw new IllegalStateException("Time slot cannot accommodate this appointment");
        }
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void removeAppointment() {
        this.appointment = null;
        isAvailable = true;
    }
} 