package com.consultation.view;

import com.consultation.controller.ConsultationController;
import com.consultation.model.User;
import com.consultation.model.Appointment;
import com.consultation.model.QueueManager;
import com.consultation.model.Notification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class ConsultationGUI extends JFrame {
    private ConsultationController controller;
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel loginPanel;
    private JPanel studentPanel;
    private JPanel professorPanel;
    private JPanel counselorPanel;
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public ConsultationGUI(ConsultationController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TIP Consultation Queue System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Create panels
        loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(new JPanel(), "STUDENT");
        mainPanel.add(new JPanel(), "PROFESSOR");
        mainPanel.add(new JPanel(), "COUNSELOR");

        add(mainPanel);
        showLoginPanel();
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("TIP Consultation Queue System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Login form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JTextField usernameField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR);
        JButton registerButton = createStyledButton("Register", SECONDARY_COLOR);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        formPanel.add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(formPanel, gbc);

        // Add action listeners
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            currentUser = controller.login(username, password);
            if (currentUser != null) {
                showRolePanel();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> showRegistrationDialog());

        return panel;
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create user info panel
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        userInfoPanel.setBackground(BACKGROUND_COLOR);
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, PRIMARY_COLOR),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getName() + 
            " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(TEXT_COLOR);
        userInfoPanel.add(userLabel);
        
        // Create tabs with custom styling
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tabbedPane.addTab("Create Appointment", createAppointmentPanel());
        tabbedPane.addTab("My Appointments", createMyAppointmentsPanel());
        tabbedPane.addTab("Queue Status", createQueueStatusPanel());
        tabbedPane.addTab("Notifications", createNotificationsPanel());
        
        panel.add(userInfoPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add logout button
        JButton logoutButton = createStyledButton("Logout", PRIMARY_COLOR);
        logoutButton.addActionListener(e -> showLoginPanel());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(logoutButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createProfessorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create user info panel
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        userInfoPanel.setBackground(BACKGROUND_COLOR);
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, PRIMARY_COLOR),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getName() + 
            " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(TEXT_COLOR);
        userInfoPanel.add(userLabel);
        
        // Create tabs with custom styling
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tabbedPane.addTab("Current Queue", createQueueManagementPanel());
        tabbedPane.addTab("Appointments", createAppointmentsListPanel());
        
        panel.add(userInfoPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add logout button
        JButton logoutButton = createStyledButton("Logout", PRIMARY_COLOR);
        logoutButton.addActionListener(e -> showLoginPanel());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(logoutButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createCounselorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create user info panel
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        userInfoPanel.setBackground(BACKGROUND_COLOR);
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, PRIMARY_COLOR),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getName() + 
            " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(TEXT_COLOR);
        userInfoPanel.add(userLabel);
        
        // Create tabs with custom styling
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tabbedPane.addTab("Current Queue", createQueueManagementPanel());
        tabbedPane.addTab("Appointments", createAppointmentsListPanel());
        
        panel.add(userInfoPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add logout button
        JButton logoutButton = createStyledButton("Logout", PRIMARY_COLOR);
        logoutButton.addActionListener(e -> showLoginPanel());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(logoutButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
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
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Subject selection
        JComboBox<String> subjectComboBox = createStyledComboBox();
        JComboBox<String> professorComboBox = createStyledComboBox();
        JTextField subjectField = createStyledTextField();
        
        // Update subject combo box with student's enrolled subjects
        for (String subject : currentUser.getSubjects()) {
            subjectComboBox.addItem(subject);
        }
        subjectComboBox.addItem("Academic Advising");
        
        // Update professor combo box when subject changes
        subjectComboBox.addActionListener(e -> {
            professorComboBox.removeAllItems();
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            
            if (selectedSubject.equals("Academic Advising")) {
                for (User user : controller.getAllUsers()) {
                    if (user.getRole().equals("COUNSELOR")) {
                        professorComboBox.addItem(user.getName() + " (" + user.getUsername() + ")");
                    }
                }
            } else {
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
        panel.add(createStyledLabel("Subject:"), gbc);
        gbc.gridx = 1;
        panel.add(subjectComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createStyledLabel("Professor/Counselor:"), gbc);
        gbc.gridx = 1;
        panel.add(professorComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createStyledLabel("Additional Notes:"), gbc);
        gbc.gridx = 1;
        panel.add(subjectField, gbc);

        // Create appointment button
        JButton createButton = createStyledButton("Create Appointment", PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createButton, gbc);

        createButton.addActionListener(e -> {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            String selectedUser = (String) professorComboBox.getSelectedItem();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "Please select a professor or counselor", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = selectedUser.substring(selectedUser.indexOf("(") + 1, selectedUser.indexOf(")"));
            User professorOrCounselor = controller.getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

            if (professorOrCounselor == null) {
                JOptionPane.showMessageDialog(this, "Selected user not found", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String notes = subjectField.getText().trim();
            if (notes.isEmpty()) {
                notes = selectedSubject;
            }

            int duration = 30;
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
                    "Subject: " + appointment.getSubject(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the appointments table
                Component[] components = studentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JTabbedPane) {
                        JTabbedPane tabbedPane = (JTabbedPane) comp;
                        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                            if (tabbedPane.getTitleAt(i).equals("My Appointments")) {
                                Component tabComponent = tabbedPane.getComponentAt(i);
                                if (tabComponent instanceof JPanel) {
                                    JPanel tabPanel = (JPanel) tabComponent;
                                    Component[] tabComponents = tabPanel.getComponents();
                                    for (Component tabComp : tabComponents) {
                                        if (tabComp instanceof JScrollPane) {
                                            JScrollPane scrollPane = (JScrollPane) tabComp;
                                            Component view = scrollPane.getViewport().getView();
                                            if (view instanceof JTable) {
                                                JTable table = (JTable) view;
                                                refreshAppointmentsTable((DefaultTableModel) table.getModel());
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to create appointment. No available slots found.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createMyAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create table model
        String[] columnNames = {"ID", "With", "Subject", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        
        JTable appointmentsTable = createStyledTable(model);
        appointmentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        appointmentsTable.setRowHeight(30);
        
        // Set column widths to match queue table
        TableColumnModel columnModel = appointmentsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(150); // With
        columnModel.getColumn(2).setPreferredWidth(150); // Subject
        columnModel.getColumn(3).setPreferredWidth(150); // Time
        columnModel.getColumn(4).setPreferredWidth(100); // Status
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton cancelButton = createStyledButton("Cancel Appointment", PRIMARY_COLOR);
        JButton refreshButton = createStyledButton("Refresh", SECONDARY_COLOR);
        
        cancelButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int appointmentId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                Appointment appointment = getAppointmentById(appointmentId);
                if (appointment != null && controller.cancelAppointment(appointment)) {
                    refreshAppointmentsTable(model);
                    JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel appointment!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment to cancel!",
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create table model
        String[] columnNames = {"Professor/Counselor", "Current Queue Size", "Estimated Wait Time"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable queueTable = createStyledTable(model);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(queueTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JButton refreshButton = createStyledButton("Refresh", SECONDARY_COLOR);
        refreshButton.addActionListener(e -> refreshQueueTable(model));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create table model
        String[] columnNames = {"Time", "Message"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable notificationsTable = createStyledTable(model);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(notificationsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JButton refreshButton = createStyledButton("Refresh", SECONDARY_COLOR);
        refreshButton.addActionListener(e -> refreshNotificationsTable(model));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
            // Get all appointments and sort them by time
            List<Appointment> allAppointments = new ArrayList<>();
            
            // Add priority appointments first
            for (Appointment app : queue.getPriorityQueue()) {
                if (!allAppointments.contains(app)) {
                    allAppointments.add(app);
                }
            }
            
            // Add regular appointments
            for (Appointment app : queue.getRegularQueue()) {
                if (!allAppointments.contains(app)) {
                    allAppointments.add(app);
                }
            }
            
            // Sort by time
            allAppointments.sort(Comparator.comparing(Appointment::getAppointmentTime));
            
            // Add to table
            for (Appointment appointment : allAppointments) {
                model.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getStudent().getName(),
                    appointment.getSubject(),
                    appointment.getAppointmentTime(),
                    appointment.isPriority() ? "High" : "Normal"
                });
            }
        }
    }

    private void refreshAppointmentsListTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Appointment> appointments = controller.getUserAppointments(currentUser);
        
        // Sort appointments by time
        appointments.sort(Comparator.comparing(Appointment::getAppointmentTime));
        
        for (Appointment appointment : appointments) {
            model.addRow(new Object[]{
                appointment.getId(),
                appointment.getStudent().getName(),
                appointment.getSubject(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
            });
        }
    }

    // Helper methods for creating styled components
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return passwordField;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return comboBox;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Style the table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        
        // Style the table cells
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(new Color(230, 230, 230));
        
        // Custom renderer for cells
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, 
                    hasFocus, row, column);
                c.setForeground(TEXT_COLOR);
                c.setBackground(isSelected ? SECONDARY_COLOR : Color.WHITE);
                return c;
            }
        });
        
        // Custom renderer for header
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, 
                    hasFocus, row, column);
                c.setForeground(Color.WHITE);
                c.setBackground(PRIMARY_COLOR);
                c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                return c;
            }
        });
        
        return table;
    }
} 