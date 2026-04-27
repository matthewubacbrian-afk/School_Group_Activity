package com.schooldb.ui;
 
import com.schooldb.db.DatabaseManager;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
 
public class SectionPanel extends JPanel {
 
    Font myFont = new Font("Arial", Font.PLAIN, 14);
 
    private final DatabaseManager dbManager;
    private final DefaultTableModel tableModel;
    private final JTable table;
 
    JTextField fieldSection = new JTextField();
    JComboBox<String> comboCourse = new JComboBox<>();
    JComboBox<String> comboInstructor = new JComboBox<>();
    JTextField fieldDays = new JTextField();
    JTextField fieldTime = new JTextField();
    JTextField fieldRoom = new JTextField();
 
    JButton btnAdd = new JButton("Add");
    JButton btnUpdate = new JButton("Update");
    JButton btnDelete = new JButton("Delete");
    JButton btnClear = new JButton("Clear");
 
    SectionPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
 
        this.tableModel = new DefaultTableModel(
                new String[]{
                        "Section Name", "Course Code",
                        "Instructor ID", "Days", "Time", "Room"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
 
        table = new JTable(tableModel);
 
        setLayout(null);
        setBackground(Color.decode("#1a1a1a"));
 
        buildTable();
        buildForm();
        buildButtons();
 
       
        try {
            for (Object[] r : dbManager.getAllCourses()) {
                comboCourse.addItem(r[0].toString());
            }
            for (Object[] r : dbManager.getAllInstructors()) {
                comboInstructor.addItem(r[0] + " - " + r[1]);
            }
        } catch (SQLException ex) {
            showError("Failed to load courses/instructors: " + ex.getMessage());
        }
 
        table.getSelectionModel().addListSelectionListener(
                e -> {
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
        scroll.setBounds(10, 10, 720, 150);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#F5E642")));
        add(scroll);
    }
 
    void buildForm() {
        addLabel("Section Name:", 10, 173);
        addLabel("Course Code:", 10, 208);
        addLabel("Instructor:", 10, 243);
        addLabel("Days:", 10, 278);
        addLabel("Time:", 10, 313);
        addLabel("Room:", 10, 348);
 
        styleField(fieldSection);
        fieldSection.setBounds(180, 170, 160, 28);
 
        comboCourse.setFont(myFont);
        comboCourse.setBackground(Color.decode("#2a2a2a"));
        comboCourse.setForeground(Color.decode("#e0d060"));
        comboCourse.setBounds(180, 205, 200, 28);
 
        comboInstructor.setFont(myFont);
        comboInstructor.setBackground(Color.decode("#2a2a2a"));
        comboInstructor.setForeground(Color.decode("#e0d060"));
        comboInstructor.setBounds(180, 240, 280, 28);
 
        styleField(fieldDays);
        fieldDays.setBounds(180, 275, 100, 28);
 
        styleField(fieldTime);
        fieldTime.setBounds(180, 310, 200, 28);
 
        styleField(fieldRoom);
        fieldRoom.setBounds(180, 345, 160, 28);
 
        add(fieldSection);
        add(comboCourse);
        add(comboInstructor);
        add(fieldDays);
        add(fieldTime);
        add(fieldRoom);
    }
 
    void buildButtons() {
        styleButton(btnAdd, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        styleButton(btnUpdate, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        styleButton(btnDelete, Color.decode("#2a2a2a"), Color.decode("#888860"));
        styleButton(btnClear, Color.decode("#2a2a2a"), Color.decode("#888860"));
 
        btnAdd.setBounds(10, 395, 100, 40);
        btnUpdate.setBounds(120, 395, 100, 40);
        btnDelete.setBounds(230, 395, 100, 40);
        btnClear.setBounds(340, 395, 100, 40);
 
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearFields());
 
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);
    }
 
    private int getSelectedInstructorId() {
        String val = comboInstructor.getSelectedItem().toString();
        return Integer.parseInt(val.split(" - ")[0].trim());
    }
 
    void onAdd() {
        if (fieldSection.getText().isBlank() || fieldDays.getText().isBlank()
                || fieldTime.getText().isBlank() || fieldRoom.getText().isBlank()) {
            showError("All fields are required.");
            return;
        }
        try {
            dbManager.addSection(
                    fieldSection.getText().trim(),
                    comboCourse.getSelectedItem().toString(),
                    getSelectedInstructorId(),
                    fieldDays.getText().trim(),
                    fieldTime.getText().trim(),
                    fieldRoom.getText().trim());
            refreshTable();
            clearFields();
        } catch (SQLException ex) {
            showError("Failed to add section: " + ex.getMessage());
        }
    }
 
    void onUpdate() {
        if (table.getSelectedRow() < 0) {
            showError("Select a section to update.");
            return;
        }
        if (fieldSection.getText().isBlank() || fieldDays.getText().isBlank()
                || fieldTime.getText().isBlank() || fieldRoom.getText().isBlank()) {
            showError("All fields are required.");
            return;
        }
        try {
            dbManager.updateSection(
                    fieldSection.getText().trim(),
                    comboCourse.getSelectedItem().toString(),
                    getSelectedInstructorId(),
                    fieldDays.getText().trim(),
                    fieldTime.getText().trim(),
                    fieldRoom.getText().trim());
            refreshTable();
            clearFields();
        } catch (SQLException ex) {
            showError("Failed to update section: " + ex.getMessage());
        }
    }
 
    void onDelete() {
        if (table.getSelectedRow() < 0) {
            showError("Select a section to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete section \"" + fieldSection.getText().trim() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbManager.deleteSection(fieldSection.getText().trim());
                refreshTable();
                clearFields();
            } catch (SQLException ex) {
                showError("Failed to delete section: " + ex.getMessage());
            }
        }
    }
 
    void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
 
        fieldSection.setText(tableModel.getValueAt(row, 0).toString());
        comboCourse.setSelectedItem(tableModel.getValueAt(row, 1).toString());
 
        int instId = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
        for (int i = 0; i < comboInstructor.getItemCount(); i++) {
            if (comboInstructor.getItemAt(i).startsWith(instId + " -")) {
                comboInstructor.setSelectedIndex(i);
                break;
            }
        }
 
        fieldDays.setText(tableModel.getValueAt(row, 3).toString());
        fieldTime.setText(tableModel.getValueAt(row, 4).toString());
        fieldRoom.setText(tableModel.getValueAt(row, 5).toString());
    }
 
    void refreshTable() {
        tableModel.setRowCount(0);
        try {
            for (Object[] r : dbManager.getAllSections()) {
                tableModel.addRow(r);
            }
        } catch (SQLException ex) {
            showError("Failed to load sections: " + ex.getMessage());
        }
    }
 
    void clearFields() {
        fieldSection.setText("");
        fieldDays.setText("");
        fieldTime.setText("");
        fieldRoom.setText("");
        comboCourse.setSelectedIndex(0);
        comboInstructor.setSelectedIndex(0);
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