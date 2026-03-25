package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SubjectPanel extends JPanel {

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    private final DatabaseManager dbManager;
    private final DefaultTableModel tableModel;
    private final JTable table;

    JTextField fieldSubjectName = new JTextField();
    JTextField fieldUnits = new JTextField();

    JButton btnAdd = new JButton("Add");
    JButton btnUpdate = new JButton("Update");
    JButton btnDelete = new JButton("Delete");
    JButton btnClear = new JButton("Clear");

    SubjectPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.tableModel = new DefaultTableModel(
                new String[] { "ID", "Subject Name", "Units" }, 0) {
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
        addLabel("Subject Name:", 10, 218);
        addLabel("Units:", 10, 253);

        styleField(fieldSubjectName);
        fieldSubjectName.setBounds(160, 215, 260, 28);
        styleField(fieldUnits);
        fieldUnits.setBounds(160, 250, 100, 28);

        add(fieldSubjectName);
        add(fieldUnits);
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
        if (!validateInputs())
            return;
        try {
            dbManager.addSubject(
                    fieldSubjectName.getText().trim(),
                    Integer.parseInt(fieldUnits.getText().trim()));
            refreshTable();
            clearFields();
        } catch (NumberFormatException ex) {
            showError("Units must be a valid number.");
        } catch (SQLException ex) {
            showError("Failed to add: " + ex.getMessage());
        }
    }

    void onUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select a subject to update.");
            return;
        }
        if (!validateInputs())
            return;
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            dbManager.updateSubject(id,
                    fieldSubjectName.getText().trim(),
                    Integer.parseInt(fieldUnits.getText().trim()));
            refreshTable();
            clearFields();
        } catch (NumberFormatException ex) {
            showError("Units must be a valid number.");
        } catch (SQLException ex) {
            showError("Failed to update: " + ex.getMessage());
        }
    }

    void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select a subject to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this subject?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            dbManager.deleteSubject(id);
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
        fieldSubjectName.setText((String) tableModel.getValueAt(row, 1));
        fieldUnits.setText(String.valueOf(tableModel.getValueAt(row, 2)));
    }

    void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Object[]> rows = dbManager.getAllSubjects();
            for (Object[] r : rows)
                tableModel.addRow(r);
        } catch (SQLException ex) {
            showError("Failed to load: " + ex.getMessage());
        }
    }

    boolean validateInputs() {
        if (fieldSubjectName.getText().isBlank() || fieldUnits.getText().isBlank()) {
            showError("All fields are required.");
            return false;
        }
        return true;
    }

    void clearFields() {
        fieldSubjectName.setText("");
        fieldUnits.setText("");
        table.clearSelection();
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