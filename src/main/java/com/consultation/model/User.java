package com.consultation.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String role; // "STUDENT", "PROFESSOR", "COUNSELOR"
    private String name;
    private String email;
    private List<String> subjects; // For professors: subjects they teach, for students: subjects they're enrolled in

    public User(String username, String password, String role, String name, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.email = email;
        this.subjects = new ArrayList<>();
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void addSubject(String subject) {
        if (!subjects.contains(subject)) {
            subjects.add(subject);
        }
    }

    public boolean canTeach(String subject) {
        return role.equals("PROFESSOR") && subjects.contains(subject);
    }

    public boolean isEnrolledIn(String subject) {
        return role.equals("STUDENT") && subjects.contains(subject);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
} 