package com.consultation.view;

import com.consultation.controller.ConsultationController;
import com.consultation.model.User;
import com.consultation.model.Appointment;
import com.consultation.model.QueueManager;
import com.consultation.model.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ConsultationGUI extends JFrame {
    private ConsultationController controller;
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel loginPanel;
    private JPanel studentPanel;
    private JPanel professorPanel;
    private JPanel counselorPanel;

    public ConsultationGUI(ConsultationController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TIP Consultation Queue System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels
        loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LOGIN");

        // Add panels to main panel
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(new JPanel(), "STUDENT"); // Placeholder
        mainPanel.add(new JPanel(), "PROFESSOR"); // Placeholder
        mainPanel.add(new JPanel(), "COUNSELOR"); // Placeholder

        add(mainPanel);
        showLoginPanel();
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("TIP Consultation Queue System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(loginButton, gbc);
        gbc.gridx = 1;
        panel.add(registerButton, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            currentUser = controller.login(username, password);
            if (currentUser != null) {
                showRolePanel();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        registerButton.addActionListener(e -> showRegistrationDialog());

        return panel;
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Create Appointment", createAppointmentPanel());
        tabbedPane.addTab("My Appointments", createMyAppointmentsPanel());
        tabbedPane.addTab("Queue Status", createQueueStatusPanel());
        tabbedPane.addTab("Notifications", createNotificationsPanel());
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> showLoginPanel());
        panel.add(logoutButton, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createProfessorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Current Queue", createQueueManagementPanel());
        tabbedPane.addTab("Appointments", createAppointmentsListPanel());
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> showLoginPanel());
        panel.add(logoutButton, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createCounselorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Current Queue", createQueueManagementPanel());
        tabbedPane.addTab("Appointments", createAppointmentsListPanel());
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> showLoginPanel());
        panel.add(logoutButton, BorderLayout.SOUTH);
        
        return panel;
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Register New User", true);
        dialog.setLayout(new GridLayout(6, 2));
        
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"STUDENT", "PROFESSOR", "COUNSELOR"});
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        
        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Role:"));
        dialog.add(roleCombo);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");
        
        registerButton.addActionListener(e -> {
            User newUser = controller.registerUser(
                usernameField.getText(),
                new String(passwordField.getPassword()),
                (String) roleCombo.getSelectedItem(),
                nameField.getText(),
                emailField.getText()
            );
            if (newUser != null) {
                JOptionPane.showMessageDialog(dialog, "Registration successful!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Username already exists!");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(registerButton);
        dialog.add(cancelButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showLoginPanel() {
        currentUser = null;
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void showRolePanel() {
        if (currentUser == null) {
            showLoginPanel();
            return;
        }
        
        // Create role-specific panel if it doesn't exist
        switch (currentUser.getRole()) {
            case "STUDENT":
                if (studentPanel == null) {
                    studentPanel = createStudentPanel();
                    mainPanel.add(studentPanel, "STUDENT");
                }
                cardLayout.show(mainPanel, "STUDENT");
                break;
            case "PROFESSOR":
                if (professorPanel == null) {
                    professorPanel = createProfessorPanel();
                    mainPanel.add(professorPanel, "PROFESSOR");
                }
                cardLayout.show(mainPanel, "PROFESSOR");
                break;
            case "COUNSELOR":
                if (counselorPanel == null) {
                    counselorPanel = createCounselorPanel();
                    mainPanel.add(counselorPanel, "COUNSELOR");
                }
                cardLayout.show(mainPanel, "COUNSELOR");
                break;
        }
    }

    // Helper methods to create various panels
    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Subject selection
        JComboBox<String> subjectComboBox = new JComboBox<>();
        JComboBox<String> professorComboBox = new JComboBox<>();
        JTextField subjectField = new JTextField(20);
        
        // Update subject combo box with student's enrolled subjects
        for (String subject : currentUser.getSubjects()) {
            subjectComboBox.addItem(subject);
        }
        
        // Add "Academic Advising" option for counselors
        subjectComboBox.addItem("Academic Advising");
        
        // Update professor combo box when subject changes
        subjectComboBox.addActionListener(e -> {
            professorComboBox.removeAllItems();
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            
            if (selectedSubject.equals("Academic Advising")) {
                // Show all counselors
                for (User user : controller.getAllUsers()) {
                    if (user.getRole().equals("COUNSELOR")) {
                        professorComboBox.addItem(user.getName() + " (" + user.getUsername() + ")");
                    }
                }
            } else {
                // Show only professors who teach this subject
                for (User user : controller.getAllUsers()) {
                    if (user.getRole().equals("PROFESSOR") && user.canTeach(selectedSubject)) {
                        professorComboBox.addItem(user.getName() + " (" + user.getUsername() + ")");
                    }
                }
            }
        });

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        panel.add(subjectComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Professor/Counselor:"), gbc);
        gbc.gridx = 1;
        panel.add(professorComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Additional Notes:"), gbc);
        gbc.gridx = 1;
        panel.add(subjectField, gbc);

        // Create appointment button
        JButton createButton = new JButton("Create Appointment");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(createButton, gbc);

        createButton.addActionListener(e -> {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            String selectedUser = (String) professorComboBox.getSelectedItem();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "Please select a professor or counselor");
                return;
            }

            // Extract username from the display string
            String username = selectedUser.substring(selectedUser.indexOf("(") + 1, selectedUser.indexOf(")"));
            User professorOrCounselor = controller.getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

            if (professorOrCounselor == null) {
                JOptionPane.showMessageDialog(this, "Selected user not found");
                return;
            }

            String notes = subjectField.getText().trim();
            if (notes.isEmpty()) {
                notes = selectedSubject; // Use subject as default notes
            }

            // Default duration of 30 minutes
            int duration = 30;

            // Create appointment with automatic time slot assignment
            Appointment appointment = controller.createAppointment(
                currentUser,
                professorOrCounselor,
                selectedSubject,
                duration
            );

            if (appointment != null) {
                JOptionPane.showMessageDialog(this, 
                    "Appointment created successfully!\n" +
                    "Date: " + appointment.getAppointmentTime().toLocalDate() + "\n" +
                    "Time: " + appointment.getAppointmentTime().toLocalTime() + "\n" +
                    "Subject: " + appointment.getSubject());
                refreshAppointmentsTable((DefaultTableModel) ((JTable) ((JScrollPane) ((JPanel) ((JTabbedPane) studentPanel.getComponent(0)).getComponent(1)).getComponent(0)).getViewport().getView()).getModel());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create appointment. No available slots found.");
            }
        });

        return panel;
    }

    private JPanel createMyAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"ID", "With", "Subject", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable appointmentsTable = new JTable(model);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel Appointment");
        JButton refreshButton = new JButton("Refresh");
        
        cancelButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int appointmentId = (int) model.getValueAt(selectedRow, 0);
                Appointment appointment = getAppointmentById(appointmentId);
                if (appointment != null && controller.cancelAppointment(appointment)) {
                    refreshAppointmentsTable(model);
                    JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel appointment!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment to cancel!");
            }
        });
        
        refreshButton.addActionListener(e -> refreshAppointmentsTable(model));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initial refresh
        refreshAppointmentsTable(model);
        
        return panel;
    }

    private JPanel createQueueStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"Professor/Counselor", "Current Queue Size", "Estimated Wait Time"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable queueTable = new JTable(model);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(queueTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshQueueTable(model));
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        // Initial refresh
        refreshQueueTable(model);
        
        return panel;
    }

    private JPanel createQueueManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"ID", "Student", "Subject", "Time", "Priority"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable queueTable = new JTable(model);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(queueTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton nextButton = new JButton("Next Appointment");
        JButton priorityButton = new JButton("Toggle Priority");
        JButton refreshButton = new JButton("Refresh");
        
        nextButton.addActionListener(e -> {
            Appointment nextAppointment = controller.getNextAppointment(currentUser.getUsername());
            if (nextAppointment != null) {
                JOptionPane.showMessageDialog(this, 
                    "Starting consultation with:\n" +
                    "Student: " + nextAppointment.getStudent().getUsername() + "\n" +
                    "Subject: " + nextAppointment.getSubject());
                refreshQueueManagementTable(model);
            } else {
                JOptionPane.showMessageDialog(this, "No appointments in queue");
            }
        });
        
        priorityButton.addActionListener(e -> {
            int selectedRow = queueTable.getSelectedRow();
            if (selectedRow >= 0) {
                int appointmentId = (int) model.getValueAt(selectedRow, 0);
                Appointment appointment = getAppointmentById(appointmentId);
                if (appointment != null) {
                    boolean currentPriority = appointment.isPriority();
                    if (controller.setPriority(appointment, !currentPriority)) {
                        refreshQueueManagementTable(model);
                        JOptionPane.showMessageDialog(this, 
                            "Priority " + (!currentPriority ? "set" : "removed") + 
                            " for appointment");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment");
            }
        });
        
        refreshButton.addActionListener(e -> refreshQueueManagementTable(model));
        
        buttonPanel.add(nextButton);
        buttonPanel.add(priorityButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createAppointmentsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"ID", "Student", "Subject", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable appointmentsTable = new JTable(model);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton completeButton = new JButton("Mark as Complete");
        JButton cancelButton = new JButton("Cancel Appointment");
        JButton refreshButton = new JButton("Refresh");
        
        completeButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int appointmentId = (int) model.getValueAt(selectedRow, 0);
                Appointment appointment = getAppointmentById(appointmentId);
                if (appointment != null) {
                    appointment.setStatus("COMPLETED");
                    refreshAppointmentsListTable(model);
                    JOptionPane.showMessageDialog(this, "Appointment marked as completed!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment!");
            }
        });
        
        cancelButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int appointmentId = (int) model.getValueAt(selectedRow, 0);
                Appointment appointment = getAppointmentById(appointmentId);
                if (appointment != null && controller.cancelAppointment(appointment)) {
                    refreshAppointmentsListTable(model);
                    JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel appointment!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment!");
            }
        });
        
        refreshButton.addActionListener(e -> refreshAppointmentsListTable(model));
        
        buttonPanel.add(completeButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Initial refresh
        refreshAppointmentsListTable(model);
        
        return panel;
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"Time", "Message"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable notificationsTable = new JTable(model);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(notificationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshNotificationsTable(model));
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        // Initial refresh
        refreshNotificationsTable(model);
        
        return panel;
    }

    private void refreshNotificationsTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Notification> notifications = controller.getUserNotifications(currentUser.getUsername());
        for (Notification notification : notifications) {
            model.addRow(new Object[]{
                notification.getTimestamp().toString(),
                notification.getMessage()
            });
        }
    }

    private Appointment getAppointmentById(int id) {
        for (Appointment appointment : controller.getUserAppointments(currentUser)) {
            if (appointment.getId() == id) {
                return appointment;
            }
        }
        return null;
    }

    private void refreshAppointmentsTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing data
        
        for (Appointment appointment : controller.getUserAppointments(currentUser)) {
            model.addRow(new Object[]{
                appointment.getId(),
                appointment.getProfessorOrCounselor().getName(),
                appointment.getSubject(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
            });
        }
    }

    private void refreshQueueTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing data
        
        for (User user : controller.getAllUsers()) {
            if (user.getRole().equals("PROFESSOR") || user.getRole().equals("COUNSELOR")) {
                int waitTime = controller.getEstimatedWaitTime(user.getUsername());
                int queueSize = controller.getQueueSize(user.getUsername());
                
                model.addRow(new Object[]{
                    user.getName(),
                    queueSize,
                    waitTime + " minutes"
                });
            }
        }
    }

    private void refreshQueueManagementTable(DefaultTableModel model) {
        model.setRowCount(0);
        QueueManager queue = controller.getQueueManager(currentUser.getUsername());
        if (queue != null) {
            // Add priority appointments first
            for (Appointment appointment : queue.getPriorityQueue()) {
                model.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getStudent().getName(),
                    appointment.getSubject(),
                    appointment.getAppointmentTime(),
                    "High"
                });
            }
            // Add regular appointments
            for (Appointment appointment : queue.getRegularQueue()) {
                model.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getStudent().getName(),
                    appointment.getSubject(),
                    appointment.getAppointmentTime(),
                    "Normal"
                });
            }
        }
    }

    private void refreshAppointmentsListTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Appointment appointment : controller.getUserAppointments(currentUser)) {
            model.addRow(new Object[]{
                appointment.getId(),
                appointment.getStudent().getName(),
                appointment.getSubject(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
            });
        }
    }
} 