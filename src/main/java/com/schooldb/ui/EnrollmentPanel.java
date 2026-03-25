package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EnrollmentPanel extends JPanel {

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    private final DatabaseManager dbManager;
    private final DefaultTableModel tableModel;

    JTextField fieldStudentId = new JTextField();
    JTextField fieldSubjectId = new JTextField();
    JTextField fieldSemester = new JTextField();
    JTextField fieldGrade = new JTextField();

    JButton btnEnroll = new JButton("Enroll");
    JButton btnRefresh = new JButton("Refresh");
    JButton btnClear = new JButton("Clear");

    EnrollmentPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.tableModel = new DefaultTableModel(
                new String[] { "First Name", "Last Name", "Subject", "Grade" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        setLayout(null);
        setBackground(Color.decode("#1a1a1a"));

        buildTable();
        buildForm();
        buildButtons();

        refreshReport();
    }

    void buildTable() {
        JTable table = new JTable(tableModel);
        table.setFont(myFont);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.decode("#F5E642"));
        table.getTableHeader().setForeground(Color.decode("#1a1a1a"));
        table.setBackground(Color.decode("#2d2d1a"));
        table.setGridColor(Color.decode("#555533"));
        table.setForeground(Color.decode("#e0d060"));
        table.setSelectionBackground(Color.decode("#3a3a20"));
        table.setSelectionForeground(Color.decode("#F5E642"));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(10, 10, 720, 190);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#F5E642")));
        add(scroll);
    }

    void buildForm() {
        addLabel("Student ID:", 10, 218);
        addLabel("Subject ID:", 10, 253);
        addLabel("Semester:", 10, 288);
        addLabel("Grade:", 10, 323);

        styleField(fieldStudentId);
        fieldStudentId.setBounds(160, 215, 100, 28);
        styleField(fieldSubjectId);
        fieldSubjectId.setBounds(160, 250, 100, 28);
        styleField(fieldSemester);
        fieldSemester.setBounds(160, 285, 160, 28);
        styleField(fieldGrade);
        fieldGrade.setBounds(160, 320, 100, 28);

        add(fieldStudentId);
        add(fieldSubjectId);
        add(fieldSemester);
        add(fieldGrade);
    }

    void buildButtons() {
        styleButton(btnEnroll, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        styleButton(btnRefresh, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        styleButton(btnClear, Color.decode("#2a2a2a"), Color.decode("#888860"));

        btnEnroll.setBounds(10, 365, 100, 40);
        btnRefresh.setBounds(120, 365, 100, 40);
        btnClear.setBounds(230, 365, 100, 40);

        btnEnroll.addActionListener(e -> onEnroll());
        btnRefresh.addActionListener(e -> refreshReport());
        btnClear.addActionListener(e -> clearFields());

        add(btnEnroll);
        add(btnRefresh);
        add(btnClear);
    }

    void onEnroll() {
        if (!validateInputs())
            return;
        try {
            dbManager.enrollStudent(
                    Integer.parseInt(fieldStudentId.getText().trim()),
                    Integer.parseInt(fieldSubjectId.getText().trim()),
                    fieldSemester.getText().trim(),
                    Double.parseDouble(fieldGrade.getText().trim()));
            refreshReport();
            clearFields();
        } catch (NumberFormatException ex) {
            showError("IDs and Grade must be valid numbers.");
        } catch (SQLException ex) {
            showError("Failed to enroll: " + ex.getMessage());
        }
    }

    void refreshReport() {
        tableModel.setRowCount(0);
        try {
            List<Object[]> rows = dbManager.getEnrollmentReport();
            for (Object[] r : rows)
                tableModel.addRow(r);
        } catch (SQLException ex) {
            showError("Failed to load report: " + ex.getMessage());
        }
    }

    boolean validateInputs() {
        if (fieldStudentId.getText().isBlank() || fieldSubjectId.getText().isBlank()
                || fieldSemester.getText().isBlank() || fieldGrade.getText().isBlank()) {
            showError("All fields are required.");
            return false;
        }
        return true;
    }

    void clearFields() {
        fieldStudentId.setText("");
        fieldSubjectId.setText("");
        fieldSemester.setText("");
        fieldGrade.setText("");
    }

    void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(myFont);
        lbl.setForeground(Color.decode("#cccc66"));
        lbl.setBounds(x, y, 150, 25);
        add(lbl);
    }

    void styleField(JTextField field) {
        field.setFont(myFont);
        field.setBackground(Color.decode("#2a2a2a"));
        field.setForeground(Color.decode("#e0d060"));
        field.setCaretColor(Color.decode("#F5E642"));
        field.setBorder(BorderFactory.createLineBorder(Color.decode("#555533")));
    }

    void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(myFont);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusable(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.decode("#555533")));
    }

    void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}