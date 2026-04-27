package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class EnrollmentPanel extends JPanel {

    private final DatabaseManager dbManager;

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    // Top table - Enrollments
    private final DefaultTableModel enrollModel;
    private final JTable enrollTable;
    private int selectedEnrollmentId = -1; // FIX: was "selectedEnrollentId" (typo)

    // Bottom table - Enlistments
    private final DefaultTableModel enlistModel;
    private final JTable enlistTable;
    private int selectedEnlistmentId = -1;

    // Enrollment Form
    JComboBox<String> comboStudent = new JComboBox<>();
    JComboBox<String> comboProgram = new JComboBox<>();
    JTextField fieldSchoolYear = new JTextField();
    JComboBox<String> comboTerm = new JComboBox<>();

    // Enlistment Form
    JTextField fieldEnrollId = new JTextField();
    JComboBox<String> comboSection = new JComboBox<>();
    JTextField fieldGrade = new JTextField();

    // Buttons Enrollment
    JButton btnEnroll = new JButton("Enroll");
    JButton btnRefresh = new JButton("Refresh");
    JButton btnClearEnroll = new JButton("Clear");

    // Buttons Enlistment
    JButton btnEnlist = new JButton("Enlist");
    JButton btnUpdateGrade = new JButton("Update Grade");
    JButton btnClearEnlist = new JButton("Clear");

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 140, 28);
        lbl.setForeground(Color.decode("#F5E642"));
        lbl.setFont(myFont);
        add(lbl);
    }

    private void styleField(JTextField field) {
        field.setBackground(Color.decode("#2d2d1a"));
        field.setForeground(Color.decode("#F5E642"));
        field.setCaretColor(Color.WHITE);
        field.setFont(myFont);
        field.setBorder(BorderFactory.createLineBorder(Color.decode("#F5E642")));
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(myFont);
        btn.setFocusPainted(false);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public EnrollmentPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;

        // Enrollment Table Model
        enrollModel = new DefaultTableModel(
            new String[]{
                "Enrollment ID", "Last Name", "First Name", "Program",
                "School Year", "Term", "Date Enrolled"
            }, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollTable = new JTable(enrollModel);

        // Enlistment Table Model
        enlistModel = new DefaultTableModel(
            new String[]{
                "Enlistment ID", "Section", "Course Code", "Title", "Instructor",
                "Days", "Time", "Room", "Date Enlisted", "Grade"
            }, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enlistTable = new JTable(enlistModel);

        setLayout(null);
        setBackground(Color.decode("#1a1a1a"));
        setPreferredSize(new Dimension(750, 520));

        // Populate Combos
        try {
            for (Object[] r : dbManager.getAllStudents()) {
                comboStudent.addItem(r[0] + " - " + r[1] + ", " + r[2]);
            }
            for (Object[] r : dbManager.getAllPrograms()) {
                comboProgram.addItem(r[0].toString());
            }
            for (Object[] r : dbManager.getAllSections()) { // FIX: was "Objec[]" typo
                comboSection.addItem(r[0].toString());
            }
        } catch (SQLException ex) {
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
        enrollTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = enrollTable.getSelectedRow();
                if (row < 0) return;

                selectedEnrollmentId = (int) enrollModel.getValueAt(row, 0); // FIX: consistent name

                fieldEnrollId.setText(String.valueOf(selectedEnrollmentId));

                refreshEnlistTable();
            }
        });

        // Bottom table row click → fill grade for update
        enlistTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = enlistTable.getSelectedRow();
                if (row < 0) return;

                selectedEnlistmentId = (int) enlistModel.getValueAt(row, 0);

                Object grade = enlistModel.getValueAt(row, 9);
                fieldGrade.setText(grade != null ? grade.toString() : "");
            }
        });

        refreshEnrollTable();
    }

    private void buildEnrollTable() {
        enrollTable.setFont(myFont);
        enrollTable.setRowHeight(24);

        enrollTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        enrollTable.getTableHeader().setBackground(Color.decode("#F5E642"));
        enrollTable.getTableHeader().setForeground(Color.decode("#1a1a1a"));

        enrollTable.setBackground(Color.decode("#2d2d1a"));
        enrollTable.setGridColor(Color.decode("#555533"));
        enrollTable.setForeground(Color.decode("#e0d060"));

        enrollTable.setSelectionBackground(Color.decode("#3a3a20"));
        enrollTable.setSelectionForeground(Color.decode("#F5E642"));

        JScrollPane scroll = new JScrollPane(enrollTable);
        scroll.setBounds(10, 10, 720, 100);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#F5E642")));

        add(scroll);
    }

    private void buildEnrollForm() {
        addLabel("Student:", 10, 123);
        addLabel("Program:", 10, 148);
        addLabel("School Year:", 10, 173);
        addLabel("Term:", 10, 198);

        // FIX: was calling setFont/setBackground/setForeground on "this" (the panel) instead of on each combo
        comboStudent.setFont(myFont);
        comboStudent.setBackground(Color.decode("#2a2a2a"));
        comboStudent.setForeground(Color.decode("#e0d060"));
        comboStudent.setBounds(160, 120, 300, 28);

        comboProgram.setFont(myFont);
        comboProgram.setBackground(Color.decode("#2a2a2a"));
        comboProgram.setForeground(Color.decode("#e0d060"));
        comboProgram.setBounds(160, 145, 280, 28);

        styleField(fieldSchoolYear);
        fieldSchoolYear.setBounds(160, 170, 160, 28);

        comboTerm.setFont(myFont);
        comboTerm.setBackground(Color.decode("#2a2a2a"));
        comboTerm.setForeground(Color.decode("#e0d060"));
        comboTerm.setBounds(160, 195, 160, 28);

        add(comboStudent);
        add(comboProgram);
        add(fieldSchoolYear);
        add(comboTerm);
    }

    private void buildEnrollButtons() {
        styleButton(btnEnroll, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        styleButton(btnRefresh, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        styleButton(btnClearEnroll, Color.decode("#2a2a2a"), Color.decode("#888860"));

        btnEnroll.setBounds(10, 235, 100, 30);
        btnRefresh.setBounds(120, 235, 100, 30);
        btnClearEnroll.setBounds(230, 235, 100, 30);

        btnEnroll.addActionListener(e -> onEnroll());
        btnRefresh.addActionListener(e -> refreshEnrollTable());
        btnClearEnroll.addActionListener(e -> clearEnrollFields());

        add(btnEnroll);
        add(btnRefresh);
        add(btnClearEnroll);
    }

    private void onEnroll() {
        if (fieldSchoolYear.getText().isBlank()) {
            showError("School Year is required");
            return;
        }

        try {
            String combo = comboStudent.getSelectedItem().toString();
            int studentId = Integer.parseInt(combo.split(" - ")[0].trim()); // FIX: was "studetId"

            String program = comboProgram.getSelectedItem().toString();
            String sy = fieldSchoolYear.getText().trim();
            String term = comboTerm.getSelectedItem().toString();

            dbManager.enrollStudent(studentId, program, sy, term);

            refreshEnrollTable();
            clearEnrollFields();

        } catch (NumberFormatException e) {
            showError("Invalid student selection.");
        } catch (SQLException ex) {
            showError("Failed to enroll: " + ex.getMessage());
        }
    }

    private void refreshEnrollTable() {
        enrollModel.setRowCount(0);

        try {
            for (Object[] r : dbManager.getAllEnrollments()) {
                enrollModel.addRow(r);
            }
        } catch (SQLException ex) {
            showError("Failed to load enrollments.");
        }
    }

    private void clearEnrollFields() {
        comboStudent.setSelectedIndex(0);
        comboProgram.setSelectedIndex(0);
        fieldSchoolYear.setText("");
        comboTerm.setSelectedIndex(0);

        enrollTable.clearSelection();
        selectedEnrollmentId = -1; // FIX: now matches the field name

        enlistModel.setRowCount(0);
        fieldEnrollId.setText("");
    }

    private void buildEnlistTable() {
        enlistTable.setFont(myFont);
        enlistTable.setRowHeight(24);

        enlistTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        enlistTable.getTableHeader().setBackground(Color.decode("#F5E642"));
        enlistTable.getTableHeader().setForeground(Color.decode("#1a1a1a"));

        enlistTable.setBackground(Color.decode("#2d2d1a"));
        enlistTable.setGridColor(Color.decode("#555533"));
        enlistTable.setForeground(Color.decode("#e0d060"));

        enlistTable.setSelectionBackground(Color.decode("#3a3a20"));
        enlistTable.setSelectionForeground(Color.decode("#F5E642"));

        JScrollPane scroll = new JScrollPane(enlistTable);
        scroll.setBounds(10, 280, 720, 100);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#F5E642")));

        add(scroll);
    }

    private void buildEnlistForm() {
        addLabel("Enrollment ID:", 10, 393);
        addLabel("Section:", 10, 418);
        addLabel("Grade:", 10, 443);

        styleField(fieldEnrollId);
        fieldEnrollId.setBounds(160, 390, 80, 28);
        fieldEnrollId.setEditable(false);

        comboSection.setFont(myFont);
        comboSection.setBackground(Color.decode("#2a2a2a"));
        comboSection.setForeground(Color.decode("#e0d060"));
        comboSection.setBounds(160, 415, 200, 28);

        styleField(fieldGrade);
        fieldGrade.setBounds(160, 440, 80, 28);

        add(fieldEnrollId);
        add(comboSection);
        add(fieldGrade);
    }

    private void buildEnlistButtons() {
        styleButton(btnEnlist, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        styleButton(btnUpdateGrade, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        styleButton(btnClearEnlist, Color.decode("#2a2a2a"), Color.decode("#888860"));

        btnEnlist.setBounds(10, 480, 100, 30);
        btnUpdateGrade.setBounds(120, 480, 130, 30);
        btnClearEnlist.setBounds(260, 480, 100, 30);

        btnEnlist.addActionListener(e -> onEnlist());
        btnUpdateGrade.addActionListener(e -> onUpdateGrade());
        btnClearEnlist.addActionListener(e -> clearEnlistFields());

        // FIX: was add(btnEnlist()) — method-call syntax instead of field reference
        add(btnEnlist);
        add(btnUpdateGrade);
        add(btnClearEnlist);
    }

    private void onEnlist() {
        if (selectedEnrollmentId == -1) {
            showError("Select an enrollment row first.");
            return;
        }

        if (comboSection.getSelectedItem() == null) {
            showError("No sections available.");
            return;
        }

        if (fieldGrade.getText().isBlank()) {
            showError("Grade is required.");
            return;
        }

        try {
            double grade = Double.parseDouble(fieldGrade.getText().trim());

            if (grade < 1.0 || grade > 5.0) {
                showError("Grade must be between 1.0 and 5.0.");
                return;
            }

            String section = comboSection.getSelectedItem().toString();

            dbManager.enlistInSection(selectedEnrollmentId, section, grade);

            refreshEnlistTable();
            clearEnlistFields();

        } catch (NumberFormatException e) {
            showError("Grade must be a valid number (e.g. 1.75)");
        } catch (SQLException ex) {
            showError("Failed to enlist: " + ex.getMessage());
        }
    }

    private void onUpdateGrade() {
        if (selectedEnlistmentId == -1) {
            showError("Select an enlistment row to update.");
            return;
        }

        if (fieldGrade.getText().isBlank()) {
            showError("Grade is required.");
            return;
        }

        try {
            double grade = Double.parseDouble(fieldGrade.getText().trim());

            if (grade < 1.0 || grade > 5.0) {
                showError("Grade must be between 1.0 and 5.0.");
                return;
            }

            dbManager.updateEnlistmentGrade(selectedEnlistmentId, grade);

            refreshEnlistTable();
            clearEnlistFields();

        } catch (NumberFormatException e) {
            showError("Grade must be a valid number.");
        } catch (SQLException ex) {
            showError("Failed to update: " + ex.getMessage());
        }
    }

    private void refreshEnlistTable() {
        enlistModel.setRowCount(0);

        if (selectedEnrollmentId == -1) return;

        try {
            for (Object[] r : dbManager.getEnlistmentsByEnrollment(selectedEnrollmentId)) {
                enlistModel.addRow(r);
            }
        } catch (SQLException ex) {
            showError("Failed to load enlistments.");
        }
    }

    private void clearEnlistFields() {
        if (comboSection.getItemCount() > 0) comboSection.setSelectedIndex(0);
        fieldGrade.setText("");
        fieldEnrollId.setText("");

        enlistTable.clearSelection();
        selectedEnlistmentId = -1;
    }
}