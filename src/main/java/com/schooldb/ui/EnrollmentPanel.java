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
    private final JTable enlistTable;
    private int selectedEnlistmentId = -1;

    //Enrollment Form
    JComboBox comboStudent = new JComboBox<>();
    JComboBox comboProgram = new JComboBox<>();
    JTextField fieldSchoolYear = new JTextField();
    JComboBox comboTerm = new JComboBox<>();

    //Enlistment Form
    JTextField fieldEnrollId = new JTextField();
    JComboBox comboSection = new JComboBox<>();
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

        //Enrollment Table Model
        enrollModel = new DefaultTableModel(
            new String[]{
                "Enrollment ID","Last Name","First Name","Program",
                "School Year","Term","Date Enrolled"
            }, 0
        ){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        enrollTable = new JTable(enrollModel);

        //Enlistment Table Model
        enlistModel = new DefaultTableModel(
            new String[]{
                "Enlistment ID","Section","Course Code","Title","Instructor",
                "Days","Time","Room","Date Enlisted","Grade"
        }, 0
        ){
        public boolean isCellEditable(int row, int column) {
            return false;
            }
        };
        enlistTable = new JTable(enlistModel);

        setLayout(null);
        setBackground(Color.decode("#1a1a1a"));

        //Populate Combos
        try {
            for (Object[] r : dbManager.getAllStudents()){
                comboStudent.addItem(
                    r[0] + " - " + r[1] + ", " + r[2]
                );
            }

            for (Object[] r : dbManager.getAllPrograms()){
                comboProgram.addItem(r[0].toString());
            }

            for (Objec[] r : dbManager.getAllSections()){
                comboSection.addItem(r[0].toString());
            }
        } catch (SQLException ex){
            showError("Failed to load data: " + ex.getMessage());
        }

        comboTerm.addItem("1st Sem");
        comboTerm.addItem("2nd Sem");
        comboTerm.addItem("Summer");

        buildEnrollTable();
        buildEnrollForm();
        buildEnrollButtons();
        buildEnlistTable();
        buildEnlistForm();
        buildEnlistButtons();

        // Top table row click → load enlistments
        enrollTable.getSelectionModel().addListSelectionListener(e ->{
            if (!e.getValueIsAdjusting()){
                int row = enrollTable.getSelectedRow();
                if (row < 0) return;

                selectedEnrollentId = (int) enrollModel.getValueAt(row, 0);

                fieldEnrollId.setText(
                    String.valueOf(selectedEnrollentId)
                );

                refreshEnlistTable();
            }
        });

        // Bottom table row click → fill grade for update
        enlistTable.getSelectionModel().addListSelectionListener(e->{
            if (!e.getValueIsAdjusting()){
                int row = enlistTable.getSelectedRow();
                if (row < 0) return;

                selectedEnlistmentId = (int) enlistModel.getValueAt(row, 0);

                Object grade = enlistModel.getValueAt(row, 9);

                fieldGrade.setText(grade != null ? grade.toString() : "");
            }
        });

        refreshEnrollTable();
    }

    private void buildEnrollTable(){
        enrollTable.setFont(myFont);
        enrollTable.setRowHeight(24);

        enrollTable.getTableHeader()
            .setFont(new Font("Arial", Font.BOLD, 14));
        enrollTable.getTableHeader()
            .setBackground(Color.decode("#F5E642"));
        enrollTable.getTableHeader()
            .setForeground(Color.decode("#1a1a1a"));

        enrollTable.setBackground(Color.decode("#2d2d1a"));
        enrollTable.setGridColor(Color.decode("#555533"));
        enrollTable.setForeground(Color.decode("#e0d060"));

        enrollTable.setSelectionBackground(Color.decode("#3a3a20"));
        enrollTable.setSelectionForeground(Color.decode("#F5E642"));

        JScrollPane scroll = new JScrollPane(enrollTable);
        scroll.setBounds(10, 10, 720, 140);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#F5E642")));

        add(scroll);
    }

    private void buildEnrollForm() {

        addLabel("Student:", 10, 163);
        addLabel("Program:", 10, 198);
        addLabel("School Year:", 10, 233);
        addLabel("Term:", 10, 268);

        //Style combo boxes
        setFont(myFont);
        setBackground(Color.decode("#2a2a2a"));
        setForeground(Color.decode("#e0d060"));

        comboStudent.setBounds(160, 160, 300, 28);
        comboProgram.setBounds(160, 195, 280, 28);
        
        styleField(fieldSchoolYear);
        fieldSchoolYear.setBounds(160, 230, 160, 28);

        comboTerm.setBounds(160, 265, 160, 28);

        //Add All 4
        add(comboStudent);
        add(comboProgram);
        add(fieldSchoolYear);
        add(comboTerm);
    }

    private void buildEnrollButtons() {

        styleButton(btnEnroll, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        styleButton(btnRefresh, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        styleButton(btnClearEnroll, Color.decode("#2a2a2a"), Color.decode("#888860"));

        btnEnroll.setBounds(10, 305, 100, 35);
        btnRefresh.setBounds(120, 305, 100, 35);
        btnClearEnroll.setBounds(230, 305, 100, 35);

        btnEnroll.addActionListener(e -> onEnroll());
        btnRefresh.addActionListener(e -> refreshEnrollTable());
        btnClearEnroll.addActionListener(e -> clearEnrollFields());

        //Add All 3
        add(btnEnroll);
        add(btnRefresh);
        add(btnClearEnroll);
    }

    private void onEnroll(){
        
        if (fieldSchoolYear.getText().isBlank()){
            showError("School Year is required");
            return;
        }

        try{
            String combo = comboStudent.getSelectedItem().toString();

            int studetId = Integer.parseInt(combo.split(" - ")[0].trim());

            String program = comboProgram.getSelectedItem().toString();
            String sy = fieldSchoolYear.getText().trim();
            String term = comboTerm.getSelectedItem().toString();

            dbManager.enrollStudent(studentId, program, sy, term);

            refreshEnrollTable();
            clearEnrollFields();

        } catch (NumberFormatException e){
            showError("Invalid student selection.");
        } catch (SQLException ex){
            showError("Failed to enroll: " + ex.getMessage());
        }
    }

    private void refreshEnrollTable(){
        enrollModel.setRowCount(0);

        try{
            for (Object[] r : dbManager.getAllEnrollments()){
                enrollModel.addRow(r);
            }
        } catch (SQLException ex){
            showError("Failed to load enrollments.");
        }
    }

    private void clearEnrollFields(){
        
        comboStudent.setSelectedIndex(0);
        comboProgram.setSelectedIndex(0);
        fieldSchoolYear.setText("");
        comboTerm.setSelectedIndex(0);

        enrollTable.clearSelection();
        selectedEnrollmentId = -1;

        enlistModel.setRowCount(0);
        fieldEnrollId.setText("");
    }

    private void buildEnlistTable(){
        //enlistTable — same styling as enrollTable
        JScrollPane scroll = new JScrollPane(enlistTable);
        scroll.setBounds(10, 350, 720, 130);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#F5E642")));

        add(scroll);
    }

    private void buildEnlistForm(){

        addLabel("Enrollment ID:", 10, 493);
        addLabel("Section:", 10, 528);
        addLabel("Grade:", 10, 563);

        styleField(fieldEnrollId);
        fieldEnrollId.setBounds(160, 490, 80, 28);
        fieldEnrollId.setEditable(false);

        comboSection.setFont(myFont);
        comboSection.setBackground(Color.decode("#2a2a2a"));
        comboSection.setForeground(Color.decode("#e0d060"));
        comboSection.setBounds(160, 525, 200, 28);

        styleField(fieldGrade);
        fieldGrade.setBounds(160, 560, 80, 28);

        add(fieldEnrollId);
        add(comboSection);
        add(fieldGrade);
    }
}