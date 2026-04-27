package com.schooldb.ui;
 
import com.schooldb.db.DatabaseManager;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
 
public class CoursePanel extends JPanel {
 
    Font myFont = new Font("Arial", Font.PLAIN, 14);
    private final DatabaseManager dbManager;
    private final DefaultTableModel tableModel;
    private final JTable table;
    JTextField fieldCode = new JTextField();
    JTextField fieldTitle = new JTextField();
    JTextField fieldCredits = new JTextField();
    JButton btnAdd = new JButton("Add");
    JButton btnUpdate = new JButton("Update");
    JButton btnDelete = new JButton("Delete");
    JButton btnClear = new JButton("Clear");
 
    CoursePanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.tableModel = new DefaultTableModel(
                new String[] { "Course Code", "Descriptive Title", "Credits" }, 0) {
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
        addLabel("Course Code:", 10, 218);
        addLabel("Descriptive Title:", 10, 253);
        addLabel("Credits:", 10, 288);
 
        styleField(fieldCode);
        fieldCode.setBounds(180, 215, 160, 28);
        styleField(fieldTitle);
        fieldTitle.setBounds(180, 250, 300, 28);
        styleField(fieldCredits);
        fieldCredits.setBounds(180, 285, 80, 28);
 
        add(fieldCode);
        add(fieldTitle);
        add(fieldCredits);
    }
 
    void buildButtons() {
        styleButton(btnAdd, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        styleButton(btnUpdate, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        styleButton(btnDelete, Color.decode("#3a1a1a"), Color.decode("#cc6666"));
        styleButton(btnClear, Color.decode("#2a2a2a"), Color.decode("#888860"));
 
        btnAdd.setBounds(10, 335, 100, 40);
        btnUpdate.setBounds(120, 335, 100, 40);
        btnDelete.setBounds(230, 335, 100, 40);
        btnClear.setBounds(340, 335, 100, 40);
 
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
            int credits = Integer.parseInt(fieldCredits.getText().trim());
            dbManager.addCourse(
                    fieldCode.getText().trim(),
                    fieldTitle.getText().trim(),
                    credits);
            refreshTable();
            clearFields();
        } catch (NumberFormatException ex) {
            showError("Credits must be a valid number.");
        } catch (SQLException ex) {
            showError("Failed to add: " + ex.getMessage());
        }
    }
 
    void onUpdate() {
        if (table.getSelectedRow() < 0) {
            showError("Select a course to update.");
            return;
        }
        if (!validateInputs())
            return;
        try {
            dbManager.updateCourse(
                    fieldCode.getText().trim(),
                    fieldTitle.getText().trim(),
                    Integer.parseInt(fieldCredits.getText().trim()));
            refreshTable();
            clearFields();
        } catch (NumberFormatException ex) {
            showError("Credits must be a valid number.");
        } catch (SQLException ex) {
            showError("Failed to update: " + ex.getMessage());
        }
    }
 
    void onDelete() {
        if (table.getSelectedRow() < 0) {
            showError("Select a course to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this course?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        try {
            dbManager.deleteCourse(fieldCode.getText().trim());
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
        fieldCode.setText(tableModel.getValueAt(row, 0).toString());
        fieldTitle.setText(tableModel.getValueAt(row, 1).toString());
        fieldCredits.setText(tableModel.getValueAt(row, 2).toString());
    }
 
    void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Object[]> rows = dbManager.getAllCourses();
            for (Object[] r : rows)
                tableModel.addRow(r);
        } catch (SQLException ex) {
            showError("Failed to load: " + ex.getMessage());
        }
    }
 
    boolean validateInputs() {
        if (fieldCode.getText().isBlank() ||
                fieldTitle.getText().isBlank() ||
                fieldCredits.getText().isBlank()) {
            showError("All fields are required.");
            return false;
        }
        return true;
    }
 
    void clearFields() {
        fieldCode.setText("");
        fieldTitle.setText("");
        fieldCredits.setText("");
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