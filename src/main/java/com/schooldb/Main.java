package com.schooldb;

import com.schooldb.db.DatabaseManager;
import com.schooldb.ui.MainFrame;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();

        try {
            dbManager.initializeSchema();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Could not connect to the database.\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(dbManager);
            frame.setVisible(true);
        });
    }
}
