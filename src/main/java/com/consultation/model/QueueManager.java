package com.consultation.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Comparator;

public class QueueManager {
    private Queue<Appointment> regularQueue;
    private PriorityQueue<Appointment> priorityQueue;
    private User professorOrCounselor;

    public QueueManager() {
        this.regularQueue = new LinkedList<>();
        this.priorityQueue = new PriorityQueue<>(Comparator.comparing(Appointment::getAppointmentTime));
    }

    public QueueManager(User professorOrCounselor) {
        this.professorOrCounselor = professorOrCounselor;
        this.regularQueue = new LinkedList<>();
        this.priorityQueue = new PriorityQueue<>(Comparator.comparing(Appointment::getAppointmentTime));
    }

    public void addAppointment(Appointment appointment) {
        if (appointment.isPriority()) {
            priorityQueue.add(appointment);
        } else {
            regularQueue.add(appointment);
        }
    }

    public Appointment getNextAppointment() {
        if (!priorityQueue.isEmpty()) {
            return priorityQueue.poll();
        }
        return regularQueue.poll();
    }

    public void removeAppointment(Appointment appointment) {
        if (appointment.isPriority()) {
            priorityQueue.remove(appointment);
        } else {
            regularQueue.remove(appointment);
        }
    }

    public int getQueueSize() {
        return regularQueue.size() + priorityQueue.size();
    }

    public int getEstimatedWaitTime() {
        int totalMinutes = 0;
        for (Appointment app : regularQueue) {
            totalMinutes += app.getEstimatedDuration();
        }
        for (Appointment app : priorityQueue) {
            totalMinutes += app.getEstimatedDuration();
        }
        return totalMinutes;
    }

    public Queue<Appointment> getRegularQueue() {
        return regularQueue;
    }

    public PriorityQueue<Appointment> getPriorityQueue() {
        return priorityQueue;
    }

    public boolean setPriority(Appointment appointment, boolean priority) {
        if (regularQueue.contains(appointment) || priorityQueue.contains(appointment)) {
            removeAppointment(appointment);
            appointment.setPriority(priority);
            addAppointment(appointment);
            return true;
        }
        return false;
    }
} 