package com.consultation.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Comparator;

public class QueueManager {
    private Queue<Appointment> regularQueue;
    private Queue<Appointment> priorityQueue;
    private User professorOrCounselor;

    public QueueManager() {
        this.regularQueue = new LinkedList<>();
        this.priorityQueue = new LinkedList<>();
    }

    public QueueManager(User professorOrCounselor) {
        this.professorOrCounselor = professorOrCounselor;
        this.regularQueue = new LinkedList<>();
        this.priorityQueue = new LinkedList<>();
    }

    public void addToRegularQueue(Appointment appointment) {
        regularQueue.add(appointment);
    }

    public void addToPriorityQueue(Appointment appointment) {
        priorityQueue.add(appointment);
    }

    public void removeFromRegularQueue(Appointment appointment) {
        regularQueue.remove(appointment);
    }

    public void removeFromPriorityQueue(Appointment appointment) {
        priorityQueue.remove(appointment);
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

    public Queue<Appointment> getPriorityQueue() {
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

    public boolean isEmpty() {
        return regularQueue.isEmpty() && priorityQueue.isEmpty();
    }
} 