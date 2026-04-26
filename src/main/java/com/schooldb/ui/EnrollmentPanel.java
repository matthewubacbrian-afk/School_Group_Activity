package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class EnrollmentPanel extends JPanel {
    
    private final DatabaseManager dbManager;

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    //Top table - Enrollments
    private final DefaultTableModel enrollModel;
    private final JTable enrollJTable;
    private int selectedEnrollentId = -1;

    //Bottom table - Enlistments
    private final DefaultTableModel enlistModel;
    private final JTable enlisTable;
    private int selectedEnlistmentId = -1;

    //Enrollment Form
    JComboBox<String> comboStudent = new JComboBox<>();
    JComboBox<String> comboProgram = new JComboBox<>();
    JTextField fieldSchoolYear = new JTextField();
    JComboBox<String> comboTerm = new JComboBox<>();

    //Enlistment Form
    JTextField fieldEnrollId = new JTextField();
    JComboBox<String> comboSection = new JComboBox<>();
    JTextField fieldGrade = new JTextField();

    //Buttons Enrollment
    JButton btnEnroll = new JButton("Enroll");
    JButton btnRefresh = new JButton("Refresh");
    JButton btnClearEnroll = new JButton("Clear");

    //Buttons Enlistment
    JButton btnEnlist = new JButton("Enlist");
    JButton btnUpdateGrade = new JButton("Update Grade");
    JButton btnClearEnlist = new JButton("Clear");

    public EnrollmentPanel(DatabaseManager  dbManager){
        this.dbManager = dbManager;

        //Initialize Table Models
        enrollModel = new DefaultTableModel();
        enrollTable = new JTable(enrollModel);

        enlistModel = new DefaultTableModel();
        enlistTable = new JTable(enlistModel);
    }
}