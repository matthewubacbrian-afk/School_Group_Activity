package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class InstructorPanel extends JPanel {

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    private final DatabaseManager dbManager;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private int selectedInstructorId = -1;

    JTextField fieldName = new JTextField();
    JComboBox<String> comboDept = new JComboBox<>();

    JButton btnAdd = new JButton("Add");
    JButton btnUpdate = new JButton("Update");
    JButton btnDelete = new JButton("Delete");
    JButton btnClear = new JButton("Clear");

    InstructorPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.tableModel = new DefaultTableModel(
                new String[]{"ID", "Instructor Name", "College Dept"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        this.table = new JTable(tableModel);

        setLayout(null);
        setBackground(Color.decode("#1a1a1a"));

        buildTable();
        buildForm();
        buildButtons();

        // Populate comboDept
        try {
            for (Object[] r : dbManager.getAllDepartments()) {
                comboDept.addItem(r[0].toString());
            }
        } catch (SQLException ex) {
            showError("Failed to load departments: " + ex.getMessage());
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                onRowSelected();
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
        addLabel("Instructor Name:", 10, 218);
        addLabel("College Dept:", 10, 253);

        styleField(fieldName);
        fieldName.setBounds(180, 215, 260, 28);

        comboDept.setFont(myFont);
        comboDept.setBackground(Color.decode("#2a2a2a"));
        comboDept.setForeground(Color.decode("#e0d060"));
        comboDept.setBounds(180, 250, 260, 28);

        add(fieldName);
        add(comboDept);
    }

    void buildButtons() {
        styleButton(btnAdd, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        styleButton(btnUpdate, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        styleButton(btnDelete, Color.decode("#3a1a1a"), Color.decode("#cc6666"));
        styleButton(btnClear, Color.decode("#2a2a2a"), Color.decode("#888860"));

        btnAdd.setBounds(10, 300, 100, 40);
        btnUpdate.setBounds(120, 300, 100, 40);
        btnDelete.setBounds(230, 300, 100, 40);
        btnClear.setBounds(340, 300, 100, 40);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearFields());

        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);
    }

    void onAdd() {
        if (fieldName.getText().isBlank()) {
            showError("Instructor Name is required.");
            return;
        }
        try {
            dbManager.addInstructor(
                    fieldName.getText().trim(),
                    comboDept.getSelectedItem().toString());
            refreshTable();
            clearFields();
        } catch (SQLException ex) {
            showError("Failed to add: " + ex.getMessage());
        }
    }

    void onUpdate() {
        if (selectedInstructorId == -1) {
            showError("Select an instructor to update.");
            return;
        }
        if (fieldName.getText().isBlank()) {
            showError("Instructor Name is required.");
            return;
        }
        try {
            dbManager.updateInstructor(
                    selectedInstructorId,
                    fieldName.getText().trim(),
                    comboDept.getSelectedItem().toString());
            refreshTable();
            clearFields();
        } catch (SQLException ex) {
            showError("Failed to update: " + ex.getMessage());
        }
    }

    void onDelete() {
        if (selectedInstructorId == -1) {
            showError("Select an instructor to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this instructor?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        try {
            dbManager.deleteInstructor(selectedInstructorId);
            refreshTable();
            clearFields();
        } catch (SQLException ex) {
            showError("Failed to delete: " + ex.getMessage());
        }
    }

    void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedInstructorId = (int) tableModel.getValueAt(row, 0);
        fieldName.setText(tableModel.getValueAt(row, 1).toString());
        comboDept.setSelectedItem(tableModel.getValueAt(row, 2).toString());
    }

    void refreshTable() {
        tableModel.setRowCount(0);
        try {
            for (Object[] r : dbManager.getAllInstructors()) {
                tableModel.addRow(r);
            }
        } catch (SQLException ex) {
            showError("Failed to load: " + ex.getMessage());
        }
    }

    void clearFields() {
        fieldName.setText("");
        comboDept.setSelectedIndex(0);
        table.clearSelection();
        selectedInstructorId = -1;
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