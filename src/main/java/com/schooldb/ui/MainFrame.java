package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    public MainFrame(DatabaseManager dbManager) {
        setTitle("School Database Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.decode("#1a1a1a"));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBounds(10, 10, 760, 490);
        tabs.setFont(myFont);
        tabs.setBackground(Color.decode("#2a2a2a"));
        tabs.setForeground(Color.decode("#F5E642"));
        tabs.addTab("Students", new StudentPanel(dbManager));
        tabs.addTab("Subjects", new SubjectPanel(dbManager));
        tabs.addTab("Enrollments", new EnrollmentPanel(dbManager));

        add(tabs);
        setVisible(true);
    }
}