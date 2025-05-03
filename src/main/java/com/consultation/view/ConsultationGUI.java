package com.consultation.view;

// ----- NECESSARY IMPORTS -----
import com.consultation.controller.ConsultationController; // Added
import com.consultation.model.User;                       // Added
import com.consultation.model.Appointment;               // Added
import com.consultation.model.QueueManager;               // Added
import com.consultation.model.Notification;             // Added

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;                               // Added
import java.time.LocalDateTime;                         // Added
import java.time.LocalTime;                             // Added
import java.time.ZoneId;                                // Added (Keep if used later)
import java.util.ArrayList;                             // Use java.util.ArrayList
import java.util.Calendar;                              // Keep if used later
import java.util.Comparator;                            // Use java.util.Comparator
import java.util.Date;                                  // Keep if used later for JSpinner
import java.util.List;                                  // Use java.util.List (NOT java.awt.List)
import java.util.stream.Collectors;                       // Added

// ----- END IMPORTS -----

public class ConsultationGUI extends JFrame {
    private ConsultationController controller;
    private User currentUser;

    // Main Panels
    private JPanel mainCardPanel;
    private CardLayout mainCardLayout;
    private JPanel loginPanel;
    private JPanel dashboardPanel;

    // Dashboard Components
    private JPanel leftNavPanel;
    private JPanel centerContentPanel;
    private CardLayout centerCardLayout;
    private JPanel rightNotificationsPanel;
    private JLabel userInfoLabel;

    // Styling Constants (Keep from previous enhancement)
    // ... (COLOR_*, FONT_*, BORDER_*) ...
    private static final Color COLOR_BACKGROUND = new Color(245, 247, 250);
    private static final Color COLOR_BACKGROUND_DARK = new Color(45, 55, 72);
    private static final Color COLOR_PRIMARY = new Color(59, 130, 246);
    private static final Color COLOR_PRIMARY_DARK = new Color(37, 99, 235);
    private static final Color COLOR_SECONDARY = new Color(14, 165, 233);
    private static final Color COLOR_SUCCESS = new Color(16, 185, 129);
    private static final Color COLOR_DANGER = new Color(239, 68, 68);
    private static final Color COLOR_DANGER_DARK = new Color(220, 38, 38);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(31, 41, 55);
    private static final Color COLOR_TEXT_LIGHT = new Color(229, 231, 235);
    private static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(209, 213, 219);
    private static final Color COLOR_TABLE_HEADER = new Color(243, 244, 246);
    private static final Color COLOR_TABLE_ROW_ODD = new Color(249, 250, 251);
    private static final Color COLOR_TABLE_ROW_EVEN = COLOR_WHITE;
    private static final Color COLOR_TABLE_SELECTION_BG = new Color(191, 219, 254);
    private static final Color COLOR_TABLE_SELECTION_FG = COLOR_TEXT_DARK;

    private static final Font FONT_MAIN = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private static final Border BORDER_PANEL_PADDING = new EmptyBorder(20, 25, 20, 25);
    private static final Border BORDER_COMPONENT_PADDING = new EmptyBorder(5, 8, 5, 8);
    private static final Border BORDER_BUTTON_PADDING = new EmptyBorder(8, 18, 8, 18);
    private static final Border BORDER_INPUT_DEFAULT = new CompoundBorder(new LineBorder(COLOR_BORDER, 1), BORDER_COMPONENT_PADDING);


    // ----- Card names for center panel (Needs to be declared) -----
    private static final String CARD_BOOK_APPOINTMENT = "BookAppointment";
    private static final String CARD_MY_APPOINTMENTS = "MyAppointments";
    private static final String CARD_NOTIFICATIONS = "Notifications";
    private static final String CARD_QUEUE_STATUS_STAFF = "QueueStatusStaff";
    private static final String CARD_MANAGE_APPOINTMENTS = "ManageAppointments";
    // ----- END Card names -----

    // ----- Table Models (Needs to be declared) -----
    private DefaultTableModel myAppointmentsTableModel;
    private DefaultTableModel queueStatusStaffTableModel;
    private DefaultTableModel manageAppointmentsTableModel;
    // ----- END Table Models -----

    // ----- Components needing refresh access (Needs to be declared) -----
    private JLabel queueSizeLabel; // Keep as JLabel if original used label, or JPanel if using enhanced info box
    private JLabel avgWaitTimeLabel; // Keep as JLabel or change to JPanel
    private JLabel completedTodayLabel; // Keep as JLabel or change to JPanel
    private JList<String> notificationList;
    private DefaultListModel<String> notificationListModel;
    // ----- END Components -----

    // --- Constructor ---
    public ConsultationGUI(ConsultationController controller) {
        this.controller = controller;
        initializeUI();
    }

    // --- UI Initialization ---
    private void initializeUI() {
        setTitle("Consultation Queue System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);
        mainCardPanel.setBackground(COLOR_BACKGROUND);

        // Create Panels
        loginPanel = createLoginPanel();
        dashboardPanel = createDashboardPanel();

        mainCardPanel.add(loginPanel, "LOGIN");
        mainCardPanel.add(dashboardPanel, "DASHBOARD");

        add(mainCardPanel);
        showLoginPanel(); // Ensure this method exists
    }

    // --- Panel Creation Methods ---

    private JPanel createLoginPanel() {
        // ... (Keep implementation from previous enhancement)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND); // Use main background
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("TIP Consultation Queue System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32)); // Larger Title
        titleLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 50, 0); // More space below title
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Form Box
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1), // Simple line border
                new EmptyBorder(40, 50, 40, 50) // Increased Padding inside box
        ));
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(12, 10, 12, 10); // More spacing in form
        formGbc.anchor = GridBagConstraints.LINE_END;

        JTextField usernameField = createStyledTextField(25); // Wider field
        JPasswordField passwordField = createStyledPasswordField(25);
        JButton loginButton = createStyledButton("Login", COLOR_PRIMARY, COLOR_WHITE);
        JButton registerButton = createStyledButton("Register", COLOR_SECONDARY, COLOR_WHITE);

        // Add hover effects to buttons
        addHoverEffect(loginButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK);
        addHoverEffect(registerButton, COLOR_SECONDARY, COLOR_SECONDARY.darker());

        formGbc.gridx = 0; formGbc.gridy = 0;
        formPanel.add(createStyledLabel("Username:"), formGbc);
        formGbc.gridx = 1; formGbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(usernameField, formGbc);

        formGbc.gridx = 0; formGbc.gridy = 1; formGbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(createStyledLabel("Password:"), formGbc);
        formGbc.gridx = 1; formGbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(passwordField, formGbc);

        // Button Panel within Form Box
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Wider button spacing
        buttonPanel.setBackground(COLOR_WHITE); // Match form background
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Space above buttons
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        formGbc.gridx = 0; formGbc.gridy = 2;
        formGbc.gridwidth = 2;
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.fill = GridBagConstraints.HORIZONTAL; // Make button panel take width
        formPanel.add(buttonPanel, formGbc);

        // Add Form Box to main Login Panel
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(formPanel, gbc);

        // --- Login/Register Actions ---
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            currentUser = controller.login(username, password);
            if (currentUser != null) {
                usernameField.setText(""); // Clear fields on success
                passwordField.setText("");
                setupDashboard(); // Populate dashboard based on user role
                showDashboardPanel(); // Ensure this method exists
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        registerButton.addActionListener(e -> showRegistrationDialog()); // Ensure this method exists

        return panel;

    }

    private JPanel createDashboardPanel() {
        // ... (Keep implementation from previous enhancement)
        JPanel panel = new JPanel(new BorderLayout(15, 15)); // Increased gaps
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Overall dashboard padding

        leftNavPanel = new JPanel(); // Structure only, populated later
        leftNavPanel.setBackground(COLOR_BACKGROUND_DARK);

        centerCardLayout = new CardLayout();
        centerContentPanel = new JPanel(centerCardLayout);
        centerContentPanel.setBackground(COLOR_WHITE);
        centerContentPanel.setBorder(new LineBorder(COLOR_BORDER, 1));

        rightNotificationsPanel = createNotificationsPanelInternal(); // Create structure

        panel.add(leftNavPanel, BorderLayout.WEST);
        panel.add(centerContentPanel, BorderLayout.CENTER);
        panel.add(rightNotificationsPanel, BorderLayout.EAST);

        return panel;
    }

    // --- Method Definitions (Ensure these exist) ---

    private void setupDashboard() {
        if (currentUser == null) return;
        setupLeftNavPanel();
        setupCenterContentPanel();
        refreshAllViews(); // Ensure this method exists
        showDefaultViewForRole(); // Ensure this method exists
    }

    private void setupLeftNavPanel() {
        // ... (Keep implementation from previous enhancement)
        leftNavPanel.removeAll();
        leftNavPanel.setLayout(new BoxLayout(leftNavPanel, BoxLayout.Y_AXIS));
        leftNavPanel.setBackground(COLOR_BACKGROUND_DARK);
        leftNavPanel.setPreferredSize(new Dimension(220, 0)); // Slightly wider nav
        leftNavPanel.setBorder(new EmptyBorder(25, 15, 25, 15)); // More vertical padding

        // User Info Section
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(COLOR_BACKGROUND_DARK);
        userInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userInfoLabel = new JLabel(currentUser.getName());
        userInfoLabel.setFont(FONT_HEADER);
        userInfoLabel.setForeground(COLOR_TEXT_LIGHT);
        userInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfoPanel.add(userInfoLabel);

        JLabel userRoleLabel = new JLabel(currentUser.getRole()); // Show role
        userRoleLabel.setFont(FONT_SMALL);
        userRoleLabel.setForeground(COLOR_TEXT_MUTED); // Muted color for role
        userRoleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfoPanel.add(userRoleLabel);


        leftNavPanel.add(userInfoPanel);
        leftNavPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Space below user info

        // Add separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(COLOR_TEXT_MUTED.darker());
        separator.setBackground(COLOR_TEXT_MUTED.darker());
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); // Make it fill width
        leftNavPanel.add(separator);
        leftNavPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space after separator


        // Navigation Buttons
        String role = currentUser.getRole();
        if (role.equals("STUDENT")) {
            leftNavPanel.add(createNavButton("Book Appointment", CARD_BOOK_APPOINTMENT)); // Use constant
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            leftNavPanel.add(createNavButton("My Appointments", CARD_MY_APPOINTMENTS)); // Use constant
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            leftNavPanel.add(createNavButton("Notifications", CARD_NOTIFICATIONS)); // Use constant
        } else if (role.equals("PROFESSOR") || role.equals("COUNSELOR")) {
            leftNavPanel.add(createNavButton("Queue Status", CARD_QUEUE_STATUS_STAFF)); // Use constant
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            leftNavPanel.add(createNavButton("Manage Appointments", CARD_MANAGE_APPOINTMENTS)); // Use constant
            leftNavPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            leftNavPanel.add(createNavButton("Notifications", CARD_NOTIFICATIONS)); // Use constant
        }

        leftNavPanel.add(Box.createVerticalGlue()); // Pushes logout to bottom

        // Logout Button
        JButton logoutButton = createNavButton("Logout", "LOGOUT_ACTION");
        logoutButton.setBackground(COLOR_DANGER_DARK.darker());
        logoutButton.setForeground(COLOR_WHITE);
        addHoverEffect(logoutButton, COLOR_DANGER_DARK.darker(), COLOR_DANGER);
        leftNavPanel.add(logoutButton);

        leftNavPanel.revalidate();
        leftNavPanel.repaint();

    }

    private void showDefaultViewForRole() {
        // ... (Keep implementation from previous enhancement)
         if (currentUser == null) return;
        String defaultCard = "";
        switch (currentUser.getRole()) {
            case "STUDENT":
                defaultCard = CARD_BOOK_APPOINTMENT; // Use constant
                break;
            case "PROFESSOR":
            case "COUNSELOR":
                defaultCard = CARD_QUEUE_STATUS_STAFF; // Use constant
                break;
            default:
                System.err.println("Warning: Unknown user role: " + currentUser.getRole());
                break;
        }
        if (!defaultCard.isEmpty()) {
            centerCardLayout.show(centerContentPanel, defaultCard);
        } else {
            System.err.println("Could not determine default view for role: " + currentUser.getRole());
            showLoginPanel(); // Ensure this method exists
        }
    }

    private JButton createNavButton(String text, String actionCommand) {
        // ... (Keep implementation from previous enhancement)
         JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setBackground(COLOR_BACKGROUND_DARK); // Base background
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(12, 20, 12, 20)); // Padding inside button
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT); // Align button itself left
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));

        Color originalBg = button.getBackground();
        Color hoverBg = COLOR_PRIMARY_DARK; // Use primary dark for hover highlight

        // Rollover effect (excluding logout which has custom handling)
        if (!actionCommand.equals("LOGOUT_ACTION")) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(hoverBg);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(originalBg);
                }
            });
        }

        // Action Listener
         button.addActionListener(e -> {
            if (actionCommand.equals("LOGOUT_ACTION")) {
                 int confirm = JOptionPane.showConfirmDialog(
                     this,
                     "Are you sure you want to logout?",
                     "Confirm Logout",
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.QUESTION_MESSAGE);
                 if (confirm == JOptionPane.YES_OPTION) {
                    showLoginPanel(); // Ensure this method exists
                 }
            } else {
                 refreshSpecificView(actionCommand); // Ensure this method exists
                 centerCardLayout.show(centerContentPanel, actionCommand);
            }
        });
        return button;

    }

    private void setupCenterContentPanel() {
        // ... (Keep implementation from previous enhancement)
        centerContentPanel.removeAll();
        centerContentPanel.add(createBookAppointmentPanel(), CARD_BOOK_APPOINTMENT); // Use constant
        centerContentPanel.add(createMyAppointmentsPanel(), CARD_MY_APPOINTMENTS);   // Use constant
        centerContentPanel.add(createNotificationsViewPanel(), CARD_NOTIFICATIONS); // Use constant
        centerContentPanel.add(createQueueStatusStaffPanel(), CARD_QUEUE_STATUS_STAFF); // Use constant
        centerContentPanel.add(createManageAppointmentsPanel(), CARD_MANAGE_APPOINTMENTS); // Use constant
        centerContentPanel.revalidate();
        centerContentPanel.repaint();
    }

    // --- Content Panel Creation Methods ---
    // ... createBookAppointmentPanel ... (Keep implementation)
    // ... createMyAppointmentsPanel ... (Keep implementation, check variable names like myAppointmentsTableModel)
    // ... createQueueStatusStaffPanel ... (Keep implementation, check variable names)
    // ... findAppointmentFromQueueTable ... (Keep implementation)
    // ... selectAppointmentInTable ... (Keep implementation)
    // ... createManageAppointmentsPanel ... (Keep implementation, check variable names)

     private JPanel createBookAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15)); // Gaps
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BORDER_PANEL_PADDING); // Use standard padding

        JLabel titleLabel = new JLabel("Book Appointment");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0)); // Space below title
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(COLOR_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 15); // Adjusted insets
        gbc.anchor = GridBagConstraints.LINE_END;

        JComboBox<String> professorComboBox = createStyledComboBox();
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(30, 15, 120, 15));
        durationSpinner.setFont(FONT_MAIN);
        ((JSpinner.DefaultEditor) durationSpinner.getEditor()).getTextField().setColumns(5);
        JComboBox<String> subjectComboBox = createStyledComboBox();
        JTextArea descriptionArea = new JTextArea(5, 30);
        descriptionArea.setFont(FONT_MAIN);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));

        subjectComboBox.removeAllItems();
        if (currentUser != null && currentUser.getRole().equals("STUDENT")) {
            subjectComboBox.addItem("-- Select Subject --");
            if (currentUser.getSubjects() != null) {
                currentUser.getSubjects().forEach(subjectComboBox::addItem);
            }
            subjectComboBox.addItem("Academic Advising");
            subjectComboBox.setEnabled(true);
        } else {
            subjectComboBox.addItem("-- Login as Student --");
            subjectComboBox.setEnabled(false);
        }

        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        formGrid.add(createStyledLabel("Professor/Counselor:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.LINE_START;
        formGrid.add(professorComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        formGrid.add(createStyledLabel("Subject:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.LINE_START;
        formGrid.add(subjectComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        formGrid.add(createStyledLabel("Duration (minutes):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_START;
        formGrid.add(durationSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(8, 5, 8, 15);
        formGrid.add(createStyledLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.LINE_START;
        formGrid.add(descriptionScrollPane, gbc);

        panel.add(formGrid, BorderLayout.CENTER);

        JButton bookButton = createStyledButton("Book Next Available Slot", COLOR_PRIMARY, COLOR_WHITE);
        addHoverEffect(bookButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(COLOR_WHITE);
        buttonPanel.setBorder(new EmptyBorder(25, 0, 10, 0));
        buttonPanel.add(bookButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        subjectComboBox.addActionListener(e -> {
            professorComboBox.removeAllItems();
            Object selectedItem = subjectComboBox.getSelectedItem();
            if (selectedItem == null || selectedItem.toString().startsWith("-- Select")) {
                professorComboBox.addItem("-- Select Subject First --");
                professorComboBox.setEnabled(false);
                return;
            }
            professorComboBox.setEnabled(true);
            String selectedSubject = selectedItem.toString();
            if (selectedSubject.equals("Academic Advising")) {
                List<User> counselors = controller.getAllUsers().stream()
                        .filter(user -> user.getRole().equals("COUNSELOR"))
                        .collect(Collectors.toList());
                if (counselors.isEmpty()) {
                    professorComboBox.addItem("No Counselors Available");
                    professorComboBox.setEnabled(false);
                } else {
                    professorComboBox.addItem("-- Select Counselor --");
                    counselors.forEach(c -> professorComboBox.addItem(c.getName() + " (" + c.getUsername() + ")"));
                }
            } else {
                List<User> professors = controller.getAllUsers().stream()
                        .filter(user -> user.getRole().equals("PROFESSOR") && user.getSubjects().contains(selectedSubject))
                        .collect(Collectors.toList());
                if (professors.isEmpty()) {
                    professorComboBox.addItem("No Professors for this Subject");
                    professorComboBox.setEnabled(false);
                } else {
                    professorComboBox.addItem("-- Select Professor --");
                    professors.forEach(p -> professorComboBox.addItem(p.getName() + " (" + p.getUsername() + ")"));
                }
            }
            if (professorComboBox.getItemCount() > 0 && !professorComboBox.getItemAt(0).toString().contains("No ")) {
                 professorComboBox.setSelectedIndex(0);
            } else if (professorComboBox.getItemCount() == 0) {
                 professorComboBox.addItem("None Available");
                 professorComboBox.setEnabled(false);
            }
        });

        professorComboBox.removeAllItems();
        professorComboBox.addItem("-- Select Subject First --");
        professorComboBox.setEnabled(false);

        bookButton.addActionListener(e -> {
            try {
                Object profSelectedItem = professorComboBox.getSelectedItem();
                Object subjectSelectedItem = subjectComboBox.getSelectedItem();
                int duration = (int) durationSpinner.getValue();
                // String descriptionText = descriptionArea.getText().trim(); // Optional

                if (profSelectedItem == null || profSelectedItem.toString().startsWith("-- Select") || profSelectedItem.toString().contains("No ") || profSelectedItem.toString().equals("None Available")) {
                     JOptionPane.showMessageDialog(this, "Please select a valid Professor/Counselor.", "Input Error", JOptionPane.WARNING_MESSAGE); return;
                }
                 if (subjectSelectedItem == null || subjectSelectedItem.toString().startsWith("-- Select Subject")) {
                    JOptionPane.showMessageDialog(this, "Please select a Subject.", "Input Error", JOptionPane.WARNING_MESSAGE); return;
                }

                String selectedProfString = profSelectedItem.toString();
                String selectedSubject = subjectSelectedItem.toString();

                String username = selectedProfString.substring(selectedProfString.indexOf("(") + 1, selectedProfString.indexOf(")"));
                User professorOrCounselor = controller.getAllUsers().stream()
                        .filter(u -> u.getUsername().equals(username))
                        .findFirst()
                        .orElse(null);

                if (professorOrCounselor == null) {
                    throw new Exception("Selected Professor/Counselor object not found.");
                }

                Appointment appointment = controller.createAppointment(
                        currentUser, professorOrCounselor, selectedSubject, duration
                );

                if (appointment != null) {
                    JOptionPane.showMessageDialog(this,
                            "Appointment request submitted!\n" +
                            "You will be assigned the next available slot.\n\n" +
                            "Assigned Time: " + appointment.getAppointmentTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                            "With: " + appointment.getProfessorOrCounselor().getName() + "\n" +
                            "Subject: " + appointment.getSubject(),
                            "Request Submitted", JOptionPane.INFORMATION_MESSAGE);

                    subjectComboBox.setSelectedIndex(0);
                    durationSpinner.setValue(30);
                    descriptionArea.setText("");
                    refreshSpecificView(CARD_MY_APPOINTMENTS); // Use constant
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to create appointment request.\n" +
                            "Possible reasons:\n" +
                            "- No available time slots currently.\n" +
                            "- Subject enrollment/teaching mismatch.\n" + // Simplified msg
                            "Please check selections or try again later.",
                            "Request Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error processing request: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        return panel;
    }

    private JPanel createMyAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BORDER_PANEL_PADDING);

        JLabel titleLabel = new JLabel("My Appointments");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Professor/Counselor", "Date & Time", "Subject", "Status"};
        myAppointmentsTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        JTable appointmentsTable = createStyledTable(myAppointmentsTableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(COLOR_WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JButton cancelButton = createStyledButton("Cancel Selected", COLOR_DANGER, COLOR_WHITE);
        JButton refreshButton = createStyledButton("Refresh", COLOR_SECONDARY, COLOR_WHITE);

        addHoverEffect(cancelButton, COLOR_DANGER, COLOR_DANGER_DARK);
        addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker());

        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> {
             int selectedRow = appointmentsTable.getSelectedRow();
             if (selectedRow >= 0) {
                 List<Appointment> userAppointments = controller.getUserAppointments(currentUser);
                 // CRITICAL: Filter out non-cancelable statuses IF NECESSARY before sorting for display mapping
                 // This example assumes the table ONLY shows cancelable items or cancellation checks status later
                 userAppointments.sort(Comparator.comparing(Appointment::getAppointmentTime));
                 Appointment appointmentToCancel = (selectedRow < userAppointments.size()) ? userAppointments.get(selectedRow) : null;

                 if (appointmentToCancel != null) {
                     // Add status check before confirmation
                     if (!appointmentToCancel.getStatus().equals("PENDING")) {
                          JOptionPane.showMessageDialog(this, "Only 'PENDING' appointments can be cancelled.", "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
                          return;
                     }

                     int confirm = JOptionPane.showConfirmDialog(this,
                             "Cancel appointment with " + appointmentToCancel.getProfessorOrCounselor().getName() + " at " + appointmentToCancel.getAppointmentTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + "?",
                             "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                     if (confirm == JOptionPane.YES_OPTION) {
                         if (controller.cancelAppointment(appointmentToCancel)) {
                             refreshMyAppointmentsTable(myAppointmentsTableModel); // Use variable
                             JOptionPane.showMessageDialog(this, "Appointment cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                         } else {
                             JOptionPane.showMessageDialog(this, "Failed to cancel appointment.", "Error", JOptionPane.ERROR_MESSAGE);
                         }
                     }
                 } else {
                     JOptionPane.showMessageDialog(this, "Could not map selection to appointment data.", "Error", JOptionPane.ERROR_MESSAGE);
                 }
             } else {
                 JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
             }
        });
        refreshButton.addActionListener(e -> refreshMyAppointmentsTable(myAppointmentsTableModel)); // Use variable

        return panel;
    }

    private JPanel createQueueStatusStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BORDER_PANEL_PADDING);

        JLabel titleLabel = new JLabel("Queue Status");
        titleLabel.setFont(FONT_TITLE); titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Pos", "Student", "Time", "Duration", "Subject", "Priority", "Status"};
        queueStatusStaffTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        JTable queueTable = createStyledTable(queueStatusStaffTableModel); // Use variable
        TableColumnModel columnModel = queueTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40); columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(3).setPreferredWidth(70); columnModel.getColumn(5).setPreferredWidth(60);
        columnModel.getColumn(6).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(queueTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(10, 20));
        southPanel.setBackground(COLOR_WHITE);
        southPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        infoPanel.setBackground(COLOR_WHITE);
        // Initialize JLabels first
        queueSizeLabel = new JLabel("Queue: 0");
        avgWaitTimeLabel = new JLabel("Avg Wait: 0 min");
        completedTodayLabel = new JLabel("Completed: 0");
        // Pass JLabels to the info box creator helper (if using JLabel version)
        // Or directly create JPanels (if using enhanced JPanel version)
        JPanel queueSizeBox = createInfoBoxPanel(queueSizeLabel, "Queue Size");
        JPanel avgWaitTimeBox = createInfoBoxPanel(avgWaitTimeLabel, "Estimated Wait Time");
        JPanel completedTodayBox = createInfoBoxPanel(completedTodayLabel, "Appointments Completed Today");
        infoPanel.add(queueSizeBox); infoPanel.add(avgWaitTimeBox); infoPanel.add(completedTodayBox);
        southPanel.add(infoPanel, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(COLOR_WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JButton startNextButton = createStyledButton("Start Next", COLOR_PRIMARY, COLOR_WHITE);
        JButton completeButton = createStyledButton("Complete Selected", COLOR_SUCCESS, COLOR_WHITE);
        JButton refreshButton = createStyledButton("Refresh Queue", COLOR_SECONDARY, COLOR_WHITE);

        addHoverEffect(startNextButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK);
        addHoverEffect(completeButton, COLOR_SUCCESS, COLOR_SUCCESS.darker());
        addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker());

        buttonPanel.add(startNextButton); buttonPanel.add(completeButton); buttonPanel.add(refreshButton);
        southPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(southPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> {
            refreshQueueStatusStaffTable(queueStatusStaffTableModel); // Use variable
            refreshQueueInfoLabels(); // Ensure this method exists
        });

        startNextButton.addActionListener(e -> {
            Appointment nextApp = controller.getNextAppointment(currentUser.getUsername());
            if (nextApp != null) {
                 refreshQueueStatusStaffTable(queueStatusStaffTableModel); // Use variable
                 refreshQueueInfoLabels(); // Ensure this method exists
                 selectAppointmentInTable(queueTable, nextApp); // Ensure this method exists
                 JOptionPane.showMessageDialog(this, "Started appointment with " + nextApp.getStudent().getName(), "Appointment Started", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "No pending appointments in the queue.", "Queue Empty", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        completeButton.addActionListener(e -> {
            int selectedRow = queueTable.getSelectedRow();
            if (selectedRow >= 0) {
                Appointment selectedAppointment = findAppointmentFromQueueTable(queueTable, selectedRow); // Ensure this method exists
                if (selectedAppointment != null && selectedAppointment.getStatus().equals("IN_PROGRESS")) {
                    if (controller.updateAppointmentStatus(selectedAppointment, "COMPLETED")) {
                        refreshQueueStatusStaffTable(queueStatusStaffTableModel); // Use variable
                        refreshQueueInfoLabels(); // Ensure this method exists
                        JOptionPane.showMessageDialog(this, "Appointment completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                         JOptionPane.showMessageDialog(this, "Failed to mark appointment as completed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (selectedAppointment == null) {
                    JOptionPane.showMessageDialog(this, "Could not map selection to appointment data.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                     JOptionPane.showMessageDialog(this, "Selected appointment is not 'IN PROGRESS'.", "Action Not Applicable", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                 JOptionPane.showMessageDialog(this, "Please select an 'IN PROGRESS' appointment to complete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createManageAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BORDER_PANEL_PADDING);

        JLabel titleLabel = new JLabel("Manage Appointments");
        titleLabel.setFont(FONT_TITLE); titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Time", "Student", "Subject", "Duration", "Status", "Priority"};
        manageAppointmentsTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        JTable appointmentsTable = createStyledTable(manageAppointmentsTableModel); // Use variable
        TableColumnModel columnModel = appointmentsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(130); columnModel.getColumn(3).setPreferredWidth(70);
        columnModel.getColumn(4).setPreferredWidth(80); columnModel.getColumn(5).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(COLOR_WHITE);
        southPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(COLOR_WHITE);
        filterPanel.add(createStyledLabel("Filter by Status:"));
        JComboBox<String> filterComboBox = createStyledComboBox();
        filterComboBox.addItem("All"); filterComboBox.addItem("Pending"); filterComboBox.addItem("In Progress");
        filterComboBox.addItem("Completed"); filterComboBox.addItem("Cancelled");
        filterComboBox.addActionListener(e -> refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem())); // Use variable
        filterPanel.add(filterComboBox);
        southPanel.add(filterPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(COLOR_WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton togglePriorityButton = createStyledButton("Toggle Priority", COLOR_PRIMARY, COLOR_WHITE);
        JButton deleteButton = createStyledButton("Delete Selected", COLOR_DANGER, COLOR_WHITE);
        JButton refreshButton = createStyledButton("Refresh List", COLOR_SECONDARY, COLOR_WHITE);

        addHoverEffect(togglePriorityButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK);
        addHoverEffect(deleteButton, COLOR_DANGER, COLOR_DANGER_DARK);
        addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker());

        buttonPanel.add(togglePriorityButton); buttonPanel.add(deleteButton); buttonPanel.add(refreshButton);
        southPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(southPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem())); // Use variable

        togglePriorityButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Appointment selectedAppointment = findAppointmentFromManageTable(appointmentsTable, selectedRow, (String) filterComboBox.getSelectedItem()); // Ensure this method exists
                if (selectedAppointment != null) {
                    if(selectedAppointment.getStatus().equals("COMPLETED") || selectedAppointment.getStatus().equals("CANCELLED")) {
                         JOptionPane.showMessageDialog(this, "Cannot change priority of completed or cancelled appointments.", "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
                         return;
                    }
                    boolean newPriorityState = !selectedAppointment.isPriority();
                    String confirmMessage = newPriorityState ? "Set high priority? (May reschedule others)" : "Remove high priority?";
                    int confirm = JOptionPane.showConfirmDialog(this, confirmMessage, "Confirm Priority", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = controller.setPriority(selectedAppointment, newPriorityState);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Priority updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem()); // Use variable
                            refreshSpecificView(CARD_QUEUE_STATUS_STAFF); // Use constant & ensure method exists
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to update priority.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else { JOptionPane.showMessageDialog(this, "Could not find appointment data.", "Error", JOptionPane.ERROR_MESSAGE); }
            } else { JOptionPane.showMessageDialog(this, "Select appointment to change priority.", "No Selection", JOptionPane.WARNING_MESSAGE); }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Appointment appointmentToDelete = findAppointmentFromManageTable(appointmentsTable, selectedRow, (String) filterComboBox.getSelectedItem()); // Ensure method exists
                if (appointmentToDelete != null) {
                     if(appointmentToDelete.getStatus().equals("COMPLETED")) {
                         JOptionPane.showMessageDialog(this, "Cannot delete completed appointments.", "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
                         return;
                     }
                    int confirm = JOptionPane.showConfirmDialog(this, "Delete appointment for " + appointmentToDelete.getStudent().getName() + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (controller.cancelAppointment(appointmentToDelete)) {
                            refreshManageAppointmentsTable(manageAppointmentsTableModel, (String) filterComboBox.getSelectedItem()); // Use variable
                             refreshSpecificView(CARD_QUEUE_STATUS_STAFF); // Use constant & ensure method exists
                            JOptionPane.showMessageDialog(this, "Appointment deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else { JOptionPane.showMessageDialog(this, "Failed to delete.", "Error", JOptionPane.ERROR_MESSAGE); }
                    }
                } else { JOptionPane.showMessageDialog(this, "Could not find appointment data.", "Error", JOptionPane.ERROR_MESSAGE); }
            } else { JOptionPane.showMessageDialog(this, "Select appointment to delete.", "No Selection", JOptionPane.WARNING_MESSAGE); }
        });

        return panel;
    }

    // Helper to find Appointment from Manage table (ensure this exists)
     private Appointment findAppointmentFromManageTable(JTable table, int selectedRow, String statusFilter) {
         if (selectedRow < 0 || currentUser == null) return null;
         List<Appointment> appointments = controller.getFilteredAppointments(currentUser, statusFilter);
         return selectedRow < appointments.size() ? appointments.get(selectedRow) : null;
    }

    private void showEditAppointmentDialog(Appointment appointment) {
        // Keep placeholder or implement later
        JOptionPane.showMessageDialog(this, "Edit functionality TBD for appointment ID: " + appointment.getId(), "Edit Placeholder", JOptionPane.INFORMATION_MESSAGE);
    }

    // Panel for the center card layout when "Notifications" is selected
    private JPanel createNotificationsViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BORDER_PANEL_PADDING);

        JLabel titleLabel = new JLabel("My Notifications");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        if (notificationList == null) {
            notificationListModel = new DefaultListModel<>();
            notificationList = new JList<>(notificationListModel);
            notificationList.setFont(FONT_MAIN);
            notificationList.setBackground(COLOR_WHITE);
            notificationList.setForeground(COLOR_TEXT_DARK);
            notificationList.setSelectionBackground(COLOR_TABLE_SELECTION_BG);
            notificationList.setSelectionForeground(COLOR_TABLE_SELECTION_FG);
            notificationList.setCellRenderer(new NotificationListRenderer());

            // Add mouse listener to mark notifications as read when clicked
            notificationList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int index = notificationList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        List<Notification> notifications = controller.getUserNotifications(currentUser.getUsername());
                        if (index < notifications.size()) {
                            Notification notification = notifications.get(index);
                            if (!notification.isRead()) {
                                notification.markAsRead();
                                refreshNotificationsList(notificationListModel);
                            }
                        }
                    }
                }
            });
        }

        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(COLOR_WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton markAllReadButton = createStyledButton("Mark All as Read", COLOR_PRIMARY, COLOR_WHITE);
        JButton refreshButton = createStyledButton("Refresh", COLOR_SECONDARY, COLOR_WHITE);

        addHoverEffect(markAllReadButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK);
        addHoverEffect(refreshButton, COLOR_SECONDARY, COLOR_SECONDARY.darker());

        markAllReadButton.addActionListener(e -> {
            List<Notification> notifications = controller.getUserNotifications(currentUser.getUsername());
            notifications.forEach(Notification::markAsRead);
            refreshNotificationsList(notificationListModel);
        });

        refreshButton.addActionListener(e -> refreshNotificationsList(notificationListModel));

        buttonPanel.add(markAllReadButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Creates the RIGHT notification panel structure
    private JPanel createNotificationsPanelInternal() {
        // ... (Keep implementation, check variable names notificationList, notificationListModel)
        JPanel panel = new JPanel(new BorderLayout(5, 15));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 0, 0, COLOR_BORDER),
                new EmptyBorder(15, 15, 15, 15))
        );

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(FONT_HEADER); titleLabel.setForeground(COLOR_TEXT_DARK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton clearButton = new JButton("Clear All");
        clearButton.setFont(FONT_SMALL);
        clearButton.setForeground(COLOR_TEXT_MUTED);
        clearButton.setOpaque(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setBorderPainted(false);
        clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearButton.setToolTipText("Clear all displayed notifications");
        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Clear all notifications?", "Confirm Clear", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
             if (confirm == JOptionPane.YES_OPTION && notificationListModel != null) { // Use variable
                 notificationListModel.clear(); // Use variable
             }
        });
        clearButton.addMouseListener(new MouseAdapter() {
             @Override public void mouseEntered(MouseEvent e) { clearButton.setForeground(COLOR_DANGER); }
             @Override public void mouseExited(MouseEvent e) { clearButton.setForeground(COLOR_TEXT_MUTED); }
        });

        headerPanel.add(clearButton, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        if (notificationListModel == null) { // Use variable
             notificationListModel = new DefaultListModel<>(); // Use variable
        }
        if (notificationList == null) { // Use variable
             notificationList = new JList<>(notificationListModel); // Use variables
             notificationList.setFont(FONT_MAIN);
             notificationList.setBackground(COLOR_WHITE);
             notificationList.setForeground(COLOR_TEXT_DARK);
             notificationList.setSelectionBackground(COLOR_TABLE_SELECTION_BG);
             notificationList.setSelectionForeground(COLOR_TABLE_SELECTION_FG);
             notificationList.setCellRenderer(new NotificationListRenderer()); // Ensure class exists
        }

        JScrollPane scrollPane = new JScrollPane(notificationList); // Use variable
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // --- Custom List Cell Renderer ---
    class NotificationListRenderer extends DefaultListCellRenderer {
        private final Border cellBorder = new EmptyBorder(5, 8, 5, 8);
        private final Border separatorBorder = new MatteBorder(0, 0, 1, 0, COLOR_BORDER);
        private final Border compoundBorder = new CompoundBorder(separatorBorder, cellBorder);

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(compoundBorder);
            label.setOpaque(true);
            label.setFont(FONT_MAIN);
            label.setToolTipText(value.toString());

            // Get the actual notification to check read status
            List<Notification> notifications = controller.getUserNotifications(currentUser.getUsername());
            if (index < notifications.size()) {
                Notification notification = notifications.get(index);
                if (!notification.isRead()) {
                    label.setFont(FONT_BOLD); // Make unread notifications bold
                }
            }

            if (!isSelected) {
                label.setBackground(COLOR_WHITE);
                label.setForeground(COLOR_TEXT_DARK);
            } else {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        }
    }

    // --- Dialogs ---
    private void showRegistrationDialog() {
        // ... (Keep implementation)
        JDialog dialog = new JDialog(this, "Register New User", true);
        dialog.setLayout(new BorderLayout(10, 15));
        dialog.getRootPane().setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 10);
        gbc.anchor = GridBagConstraints.LINE_END;

        JTextField usernameField = createStyledTextField(20);
        JPasswordField passwordField = createStyledPasswordField(20);
        JComboBox<String> roleCombo = createStyledComboBox();
        roleCombo.addItem("STUDENT"); roleCombo.addItem("PROFESSOR"); roleCombo.addItem("COUNSELOR");
        JTextField nameField = createStyledTextField(20);
        JTextField emailField = createStyledTextField(20);

        int gridY = 0;
        gbc.gridx = 0; gbc.gridy = gridY++; formPanel.add(createStyledLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; formPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = gridY++; gbc.anchor = GridBagConstraints.LINE_END; formPanel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; formPanel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = gridY++; gbc.anchor = GridBagConstraints.LINE_END; formPanel.add(createStyledLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = gridY++; gbc.anchor = GridBagConstraints.LINE_END; formPanel.add(createStyledLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; formPanel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = gridY++; gbc.anchor = GridBagConstraints.LINE_END; formPanel.add(createStyledLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; formPanel.add(roleCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton registerButton = createStyledButton("Register", COLOR_PRIMARY, COLOR_WHITE);
        JButton cancelButton = createStyledButton("Cancel", COLOR_TEXT_MUTED, COLOR_WHITE);

        addHoverEffect(registerButton, COLOR_PRIMARY, COLOR_PRIMARY_DARK);
        addHoverEffect(cancelButton, COLOR_TEXT_MUTED, COLOR_TEXT_DARK);

        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

         registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim(); String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem(); String name = nameField.getText().trim(); String email = emailField.getText().trim();
            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty() || role == null) {
                 JOptionPane.showMessageDialog(dialog, "All fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE); return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid email address.", "Input Error", JOptionPane.WARNING_MESSAGE); return;
            }
            // *** FIX: Use correct class name 'User' ***
            User newUser = controller.registerUser(username, password, role, name, email);
            if (newUser != null) {
                JOptionPane.showMessageDialog(dialog, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                 JOptionPane.showMessageDialog(dialog, "Username already exists. Please choose another.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // --- Refresh Methods (Ensure these exist) ---

    private void refreshSpecificView(String cardName) {
        // ... (Keep implementation, check variable names like myAppointmentsTableModel, notificationListModel, etc.)
        if (currentUser == null) return;

        switch (cardName) {
            case CARD_BOOK_APPOINTMENT: // Use constant
                // Optional: Refresh professor list if needed
                break;
            case CARD_MY_APPOINTMENTS: // Use constant
                if (myAppointmentsTableModel != null) refreshMyAppointmentsTable(myAppointmentsTableModel); // Use variable
                break;
            case CARD_NOTIFICATIONS: // Use constant
                 if (notificationListModel != null) refreshNotificationsList(notificationListModel); // Use variable
                break;
            case CARD_QUEUE_STATUS_STAFF: // Use constant
                 if (queueStatusStaffTableModel != null) refreshQueueStatusStaffTable(queueStatusStaffTableModel); // Use variable
                 refreshQueueInfoLabels(); // Ensure method exists
                break;
            case CARD_MANAGE_APPOINTMENTS: // Use constant
                 String currentFilter = "All";
                 Component managePanelComp = findPanelInCenter(CARD_MANAGE_APPOINTMENTS); // Use constant & ensure method exists
                 if (managePanelComp instanceof Container) {
                     Container managePanelContainer = (Container) managePanelComp;
                     JComboBox<String> filterCombo = findComboBox(managePanelContainer); // Ensure method exists
                     if(filterCombo != null) {
                         currentFilter = (String) filterCombo.getSelectedItem();
                     }
                 } else {
                      System.err.println("Warning: Manage Appointments panel is not a Container!");
                 }
                 if (manageAppointmentsTableModel != null) refreshManageAppointmentsTable(manageAppointmentsTableModel, currentFilter); // Use variable
                break;
        }
    }

    // Helper to find a panel (ensure this exists)
    private Component findPanelInCenter(String cardName) {
         // This lookup is tricky with CardLayout by name.
         // A better approach might be to keep references to the panels if needed.
         // Returning null for now, assuming filter combo search works.
         return null;
    }

    // Helper to find combo box (ensure this exists)
    private JComboBox<String> findComboBox(Container container) {
        // ... (Keep implementation)
        for (Component comp : container.getComponents()) {
            if (comp instanceof JComboBox) {
                 try {
                    @SuppressWarnings("unchecked")
                    JComboBox<String> comboBox = (JComboBox<String>) comp;
                    return comboBox;
                } catch (ClassCastException e) { /* Ignore */ }
            } else if (comp instanceof Container) {
                JComboBox<String> found = findComboBox((Container) comp);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void refreshAllViews() {
        // ... (Keep implementation, check variable names)
         if (currentUser == null) return;

        if (myAppointmentsTableModel != null) refreshMyAppointmentsTable(myAppointmentsTableModel);
        if (notificationListModel != null) refreshNotificationsList(notificationListModel);
        if (queueStatusStaffTableModel != null) refreshQueueStatusStaffTable(queueStatusStaffTableModel);

        String currentFilter = "All";
         Component managePanelComp = findPanelInCenter(CARD_MANAGE_APPOINTMENTS); // Use constant
         if (managePanelComp instanceof Container) {
             Container managePanelContainer = (Container) managePanelComp;
             JComboBox<String> filterCombo = findComboBox(managePanelContainer);
             if(filterCombo != null) {
                 currentFilter = (String) filterCombo.getSelectedItem();
             }
         } else {
              System.err.println("Warning: Manage Appointments panel is not a Container during full refresh!");
         }
        if (manageAppointmentsTableModel != null) refreshManageAppointmentsTable(manageAppointmentsTableModel, currentFilter);

        refreshQueueInfoLabels();
    }

    private void refreshMyAppointmentsTable(DefaultTableModel model) {
        // ... (Keep implementation, use correct List/Comparator/Appointment)
         if (model == null || currentUser == null) return;
        model.setRowCount(0);
        List<Appointment> appointments = controller.getUserAppointments(currentUser); // java.util.List
        appointments.sort(Comparator.comparing(Appointment::getAppointmentTime)); // java.util.Comparator
        for (Appointment app : appointments) { // Correct class name
            String dateTimeStr = app.getAppointmentTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            model.addRow(new Object[]{
                    app.getProfessorOrCounselor().getName(), dateTimeStr, app.getSubject(), app.getStatus()
            });
        }
    }

    private void refreshNotificationsList(DefaultListModel<String> listModel) {
        // ... (Keep implementation, use correct Notification/Comparator)
        if (listModel == null || currentUser == null) return;
        listModel.clear();
        List<Notification> notifications = controller.getUserNotifications(currentUser.getUsername()); // java.util.List
         notifications.sort(Comparator.comparing(Notification::getTimestamp).reversed()); // java.util.Comparator
        for (Notification n : notifications) { // Correct class name
            listModel.addElement(n.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + ": " + n.getMessage());
        }
    }

    private void refreshQueueStatusStaffTable(DefaultTableModel model) {
        // ... (Keep implementation, use correct QueueManager/List/Comparator/Appointment/Collectors)
         if (model == null || currentUser == null) return;
        model.setRowCount(0);
        QueueManager queue = controller.getQueueManager(currentUser.getUsername());
        if (queue != null) {
            List<Appointment> displayQueue = new ArrayList<>(); // java.util.List/ArrayList
            List<Appointment> priority = new ArrayList<>(queue.getPriorityQueue()); // java.util.List/ArrayList
            List<Appointment> regular = new ArrayList<>(queue.getRegularQueue()); // java.util.List/ArrayList
            priority.sort(Comparator.comparing(Appointment::getAppointmentTime)); // java.util.Comparator
            regular.sort(Comparator.comparing(Appointment::getAppointmentTime)); // java.util.Comparator
            displayQueue.addAll(priority);
            displayQueue.addAll(regular);

            List<Appointment> filteredDisplay = displayQueue.stream()
                    .filter(a -> a.getStatus().equals("PENDING") || a.getStatus().equals("IN_PROGRESS"))
                    .collect(Collectors.toList()); // java.util.stream.Collectors

            int position = 1;
            for (Appointment app : filteredDisplay) { // Correct class name
                String dateTimeStr = app.getAppointmentTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                model.addRow(new Object[]{
                        position++, app.getStudent().getName(), dateTimeStr, app.getEstimatedDuration() + " min",
                        app.getSubject(), app.isPriority() ? "Yes" : "No", app.getStatus() // Removed Actions column data
                });
            }
        }
    }

    private void refreshManageAppointmentsTable(DefaultTableModel model, String statusFilter) {
        // ... (Keep implementation, use correct List/Comparator/Appointment/Collectors)
        if (model == null || currentUser == null) return;
        model.setRowCount(0);
        List<Appointment> appointments = controller.getFilteredAppointments(currentUser, statusFilter);
        for (Appointment app : appointments) { // Correct class name
             String dateTimeStr = app.getAppointmentTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
             model.addRow(new Object[]{
                     dateTimeStr, app.getStudent().getName(), app.getSubject(), app.getEstimatedDuration() + " min",
                     app.getStatus(), app.isPriority() ? "Yes" : "No" // Removed Actions column data
             });
        }
    }

    // Default refresh overload (ensure this exists)
    private void refreshManageAppointmentsTable(DefaultTableModel model) { refreshManageAppointmentsTable(model, "All"); }

    // --- Refresh Queue Info Labels ---
    // (Keep the version using updateInfoBoxText or adjust if using JLabels directly)
    private void refreshQueueInfoLabels() {
        if (currentUser == null || (!currentUser.getRole().equals("PROFESSOR") && !currentUser.getRole().equals("COUNSELOR"))) {
            // Check if the components are JPanels (using the enhanced version)
             if (queueSizeLabel != null && queueSizeLabel.getParent() instanceof JPanel) updateInfoBoxText((JPanel)queueSizeLabel.getParent(), "Queue: N/A");
             if (avgWaitTimeLabel != null && avgWaitTimeLabel.getParent() instanceof JPanel) updateInfoBoxText((JPanel)avgWaitTimeLabel.getParent(), "Avg Wait: N/A");
             if (completedTodayLabel != null && completedTodayLabel.getParent() instanceof JPanel) updateInfoBoxText((JPanel)completedTodayLabel.getParent(), "Completed: N/A");

            // OR if using simple JLabels directly:
            // if(queueSizeLabel != null) queueSizeLabel.setText("Queue: N/A");
            // if(avgWaitTimeLabel != null) avgWaitTimeLabel.setText("Avg Wait: N/A");
            // if(completedTodayLabel != null) completedTodayLabel.setText("Completed: N/A");
            return;
        }

        // Update info boxes (JPanel version)
         if (queueSizeLabel != null && queueSizeLabel.getParent() instanceof JPanel) {
              updateInfoBoxText((JPanel)queueSizeLabel.getParent(), "Queue: " + controller.getQueueSize(currentUser.getUsername()));
         }
         if (avgWaitTimeLabel != null && avgWaitTimeLabel.getParent() instanceof JPanel) {
              updateInfoBoxText((JPanel)avgWaitTimeLabel.getParent(), "Avg Wait: " + controller.getEstimatedWaitTime(currentUser.getUsername()) + " min");
         }
         if (completedTodayLabel != null && completedTodayLabel.getParent() instanceof JPanel) {
              LocalDate today = LocalDate.now();
              long completedCount = controller.getUserAppointments(currentUser).stream()
                     .filter(a -> a.getStatus().equals("COMPLETED") && a.getAppointmentTime().toLocalDate().equals(today))
                     .count();
             updateInfoBoxText((JPanel)completedTodayLabel.getParent(), "Completed: " + completedCount);
          }

        // OR if using simple JLabels directly:
        // if (queueSizeLabel != null) queueSizeLabel.setText("Queue: " + controller.getQueueSize(currentUser.getUsername()));
        // if (avgWaitTimeLabel != null) avgWaitTimeLabel.setText("Avg Wait: " + controller.getEstimatedWaitTime(currentUser.getUsername()) + " min");
        // if (completedTodayLabel != null) { /* ... calculate count ... */ completedTodayLabel.setText("Completed: " + completedCount); }
    }


    // --- View Switching (Ensure these methods exist) ---
    private void showLoginPanel() {
        currentUser = null;
        mainCardLayout.show(mainCardPanel, "LOGIN");
    }

    private void showDashboardPanel() {
        mainCardLayout.show(mainCardPanel, "DASHBOARD");
    }

    // --- Styling Helper Methods ---
    // ... addHoverEffect ...
    // ... createStyledButton ...
    // ... createStyledTextField ...
    // ... createStyledPasswordField ...
    // ... createStyledComboBox ...
    // ... createStyledLabel ...
    // ... createInfoBoxPanel (modified helper for JPanel) ...
    // ... updateInfoBoxText (helper for JPanel version) ...
    // ... createStyledTable ...

    private void addHoverEffect(JButton button, Color baseColor, Color hoverColor) {
        // ... (Keep implementation)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { if (button.isEnabled()) button.setBackground(hoverColor); }
            @Override
            public void mouseExited(MouseEvent e) { if (button.isEnabled()) button.setBackground(baseColor); }
            @Override
            public void mousePressed(MouseEvent e) { if (button.isEnabled()) button.setBackground(hoverColor.darker()); }
            @Override
            public void mouseReleased(MouseEvent e) {
                 if (button.isEnabled()) {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(p, button);
                    if(button.contains(p)) button.setBackground(hoverColor);
                    else button.setBackground(baseColor);
                 }
            }
        });
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        // ... (Keep implementation)
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                new LineBorder(background.darker(), 1),
                BORDER_BUTTON_PADDING
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setToolTipText(text);
        return button;
    }

    private JTextField createStyledTextField(int columns) {
        // ... (Keep implementation)
         JTextField textField = new JTextField(columns);
        textField.setFont(FONT_MAIN);
        textField.setBorder(BORDER_INPUT_DEFAULT);
        return textField;
    }

    private JPasswordField createStyledPasswordField(int columns) {
        // ... (Keep implementation)
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_MAIN);
        pf.setBorder(BORDER_INPUT_DEFAULT);
        return pf;
    }

    private JComboBox<String> createStyledComboBox() {
        // ... (Keep implementation)
         JComboBox<String> cb = new JComboBox<>();
        cb.setFont(FONT_MAIN);
        cb.setBackground(COLOR_WHITE);
        cb.setBorder(new LineBorder(COLOR_BORDER, 1)); // Minimal border
        return cb;
    }

    private JLabel createStyledLabel(String text) {
        // ... (Keep implementation)
        JLabel label = new JLabel(text);
        label.setFont(FONT_MAIN);
        label.setForeground(COLOR_TEXT_DARK);
        return label;
    }

    // Helper to create info box as JPanel containing a JLabel
    private JPanel createInfoBoxPanel(JLabel label, String tooltip) {
        JPanel infoBox = new JPanel(new BorderLayout(5, 0));
        infoBox.setBackground(COLOR_TABLE_HEADER);
        infoBox.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        infoBox.setToolTipText(tooltip);
        label.setHorizontalAlignment(SwingConstants.CENTER); // Center text in label
        label.setFont(FONT_BOLD);
        label.setForeground(COLOR_TEXT_DARK);
        // label.setName("InfoLabel"); // Optional: Name the inner label
        infoBox.add(label, BorderLayout.CENTER);
        return infoBox;
    }

    // Helper to update text in the JLabel inside the info box JPanel
    private void updateInfoBoxText(JPanel infoBoxPanel, String newText) {
        for (Component comp : infoBoxPanel.getComponents()) {
            // Find the JLabel within the panel to update its text
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText(newText);
                return;
            }
        }
    }

    private JTable createStyledTable(DefaultTableModel model) {
        // ... (Keep implementation from previous enhancement)
         JTable table = new JTable(model);
        table.setFont(FONT_MAIN);
        table.setRowHeight(30);
        table.setGridColor(COLOR_BORDER);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(COLOR_TABLE_HEADER);
        header.setForeground(COLOR_TEXT_DARK);
        header.setOpaque(true);
        header.setBorder(new LineBorder(COLOR_BORDER));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        header.setReorderingAllowed(false);

        table.setSelectionBackground(COLOR_TABLE_SELECTION_BG);
        table.setSelectionForeground(COLOR_TABLE_SELECTION_FG);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Border cellPadding = new EmptyBorder(5, 8, 5, 8);
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cellComponent = super.getTableCellRendererComponent(tbl, val, isSelected, hasFocus, row, column);
                if (cellComponent instanceof JLabel) {
                     JLabel label = (JLabel) cellComponent;
                     label.setBorder(cellPadding);
                     label.setOpaque(true);
                     label.setHorizontalAlignment(SwingConstants.LEFT);
                     String colName = tbl.getColumnName(column);
                      if ("Pos".equals(colName) || "Status".equals(colName) || "Priority".equals(colName) || "Duration".equals(colName)) {
                         label.setHorizontalAlignment(SwingConstants.CENTER);
                      }
                     if (isSelected) {
                        label.setBackground(tbl.getSelectionBackground());
                        label.setForeground(tbl.getSelectionForeground());
                     } else {
                        label.setBackground(row % 2 == 0 ? COLOR_TABLE_ROW_EVEN : COLOR_TABLE_ROW_ODD);
                        label.setForeground(tbl.getForeground());
                     }
                }
                 return cellComponent;
            }
        });
        return table;
    }
    // --- Helper Methods for Queue Table Interaction ---

    /**
     * Finds the Appointment object corresponding to a specific row in the Queue Status table.
     * This is necessary because the table view might be sorted or filtered differently
     * than the raw queue data. It reconstructs the displayed order.
     *
     * @param table The JTable displaying the queue status.
     * @param selectedRow The visual index of the selected row in the table.
     * @return The corresponding Appointment object, or null if not found or mapping fails.
     */
    private Appointment findAppointmentFromQueueTable(JTable table, int selectedRow) {
        return controller.findAppointmentFromQueueTable(currentUser.getUsername(), selectedRow);
    }

    /**
     * Selects and scrolls to the row in the specified JTable that corresponds
     * to the given target Appointment.
     *
     * @param table The JTable to perform the selection in.
     * @param targetAppointment The Appointment to find and select.
     */
    private void selectAppointmentInTable(JTable table, Appointment targetAppointment) {
        if (targetAppointment == null || table == null) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            // Use the helper to find the appointment corresponding to the *visual* row 'i'
            Appointment rowAppointment = findAppointmentFromQueueTable(table, i);

            // Check if the appointment for this visual row matches the target appointment's ID
            if (rowAppointment != null && rowAppointment.getId() == targetAppointment.getId()) {
                table.setRowSelectionInterval(i, i); // Select the row
                // Scroll the table viewport to make the selected row visible
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                break; // Found it, no need to continue loop
            }
        }
    }

    // --- End Helper Methods ---
} // End of ConsultationGUI class

