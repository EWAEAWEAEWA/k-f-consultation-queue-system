package com.consultation;

import com.consultation.controller.ConsultationController;
import com.consultation.view.ConsultationGUI;
import com.consultation.util.DataInitializer;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConsultationController controller = new ConsultationController();
            DataInitializer.initializeData(controller);
            ConsultationGUI gui = new ConsultationGUI(controller);
            gui.setVisible(true);
        });
    }
} 