package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentPanel extends JPanel {

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    private final DatabaseManager   dbManager;
    private final DefaultTableModel tableModel;
    private final JTable            table;

    JTextField fieldLastName = new JTextField();
    JTextField fieldFirstName = new JTextField();
    JTextField fieldAddress = new JTextField();
    JTextField fieldBirthdate = new JTextField();
    JTextField fieldPlaceOfBirth = new JTextField();

    JButton btnAdd    = new JButton("Add");
    JButton btnUpdate = new JButton("Update");
    JButton btnDelete = new JButton("Delete");
    JButton btnClear  = new JButton("Clear");

    StudentPanel(DatabaseManager dbManager) {
        this.dbManager  = dbManager;
        this.tableModel = new DefaultTableModel(
            new String[]{"ID","Last Name","First Name","Address","Date of Birth","Place of Birth"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        this.table = new JTable(tableModel);

        setLayout(null);
        setBackground(Color.decode("#1a1a1a"));

        buildTable();
        buildForm();
        buildButtons();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });

        refreshTable();
    }

    void buildTable() {
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
        addLabel("Last Name:", 10, 218);
        addLabel("First Name:", 10, 253);    
        addLabel("Address:", 10, 288);
        addLabel("Date of Birth (YYYY-MM-DD):", 10, 323);
        addLabel("Place of Birth:", 10, 358);

        styleField(fieldLastName); fieldLastName.setBounds(230, 215, 200, 28);
        styleField(fieldFirstName);  fieldFirstName.setBounds(230, 250, 200, 28);
        styleField(fieldAddress);    fieldAddress.setBounds(230, 285, 200, 28);
        styleField(fieldBirthdate); fieldBirthdate.setBounds(230, 320, 200, 28);
        styleField(fieldPlaceOfBirth); fieldPlaceOfBirth.setBounds(230, 355, 200, 28);

        add(fieldLastName);
        add(fieldFirstName);
        add(fieldAddress);
        add(fieldBirthdate);
        add(fieldPlaceOfBirth);
    }

    void buildButtons() {
        styleButton(btnAdd,    Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        styleButton(btnUpdate, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        styleButton(btnDelete, Color.decode("#3a1a1a"), Color.decode("#cc6666"));
        styleButton(btnClear,  Color.decode("#2a2a2a"), Color.decode("#888860"));

        btnAdd.setBounds(10, 400, 100, 40);
        btnUpdate.setBounds(120, 400, 100, 40);
        btnDelete.setBounds(230, 400, 100, 40);
        btnClear.setBounds(340, 400, 100, 40);

        btnAdd.addActionListener(e    -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e  -> clearFields());

        add(btnAdd); add(btnUpdate); add(btnDelete); add(btnClear);
    }

    void onAdd() {
        if (!validateInputs()) return;
        try {
            dbManager.addStudent(
                fieldLastName.getText().trim(),
                fieldFirstName.getText().trim(),
                fieldAddress.getText().trim(),
                fieldBirthdate.getText().trim(),
                fieldPlaceOfBirth.getText().trim()
            );
            refreshTable();
            clearFields();
        } catch (SQLException ex) {
            showError("Failed to add: " + ex.getMessage());
        }
    }

    void onUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select a student to update."); return; }
        if (!validateInputs()) return;
        int id = (int) tableModel.getValueAt(row, 0);
        try {
           dbManager.updateStudent(id,
                fieldLastName.getText().trim(),
                fieldFirstName.getText().trim(),
                fieldAddress.getText().trim(),
                fieldBirthdate.getText().trim(),
                fieldPlaceOfBirth.getText().trim()
            );
            refreshTable();
            clearFields();
        } catch (SQLException ex) {
            showError("Failed to update: " + ex.getMessage());
        }
    }

    void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select a student to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            dbManager.deleteStudent(id);
            refreshTable();
            clearFields();
        } catch (SQLException ex) {
            showError("Failed to delete: " + ex.getMessage());
        }
    }

    void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        fieldLastName.setText(tableModel.getValueAt(row,1).toString());
        fieldFirstName.setText(tableModel.getValueAt(row,2).toString());
        fieldAddress.setText(tableModel.getValueAt(row,3).toString());
        fieldBirthdate.setText(tableModel.getValueAt(row,4).toString());
        fieldPlaceOfBirth.setText(tableModel.getValueAt(row,5).toString());
    }

    void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Object[]> rows = dbManager.getAllStudents();
            for (Object[] r : rows) tableModel.addRow(r);
        } catch (SQLException ex) {
            showError("Failed to load: " + ex.getMessage());
        }
    }

    boolean validateInputs() {
        if (fieldLastName.getText().isBlank() || fieldFirstName.getText().isBlank() || fieldAddress.getText().isBlank()    
                || fieldBirthdate.getText().isBlank() || fieldPlaceOfBirth.getText().isBlank()) {
            showError("All fields are required.");
            return false;
        }
        return true;
    }

    void clearFields() {
        fieldLastName.setText("");
        fieldFirstName.setText("");
        fieldAddress.setText("");
        fieldBirthdate.setText("");
        fieldPlaceOfBirth.setText("");
        table.clearSelection();
    }

    void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(myFont);
        lbl.setForeground(Color.decode("#cccc66"));
        lbl.setBounds(x, y, 220, 25);
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