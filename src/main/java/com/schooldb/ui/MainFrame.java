package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    public MainFrame(DatabaseManager dbManager) {
        setTitle("School Database Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(800, 620);

        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.decode("#1a1a1a"));

        JTabbedPane tabs = new JTabbedPane();

        tabs.setBounds(10, 10, 760, 560);

        tabs.setFont(myFont);
        tabs.setBackground(Color.decode("#2a2a2a"));
        tabs.setForeground(Color.decode("#F5E642"));

        //tabs.addTab("Students", new StudentPanel(dbManager));
        //tabs.addTab("Courses", new CoursePanel(dbManager));
        //tabs.addTab("Departments", new DepartmentPanel(dbManager));
        //tabs.addTab("Programs", new ProgramPanel(dbManager));
        //tabs.addTab("Instructors", new InstructorPanel(dbManager));
        //tabs.addTab("Sections", new SectionPanel(dbManager));
        //tabs.addTab("Enrollments", new EnrollmentPanel(dbManager));
        tabs.addTab("Transcript", new TranscriptPanel(dbManager));
        tabs.addTab("Class List", new ClassListPanel(dbManager));

        //pakidevelop muna ng commented lines, else error
        add(tabs);
        setVisible(true);
    }
}