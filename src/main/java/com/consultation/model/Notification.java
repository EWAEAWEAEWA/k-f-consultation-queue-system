package com.consultation.model;

import java.time.LocalDateTime;

public class Notification {
    private LocalDateTime timestamp;
    private String message;

    public Notification(LocalDateTime timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
} 