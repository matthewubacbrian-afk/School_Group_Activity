package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DepartmentPanel {
    Font myFont = new Font("Arial", Font.PLAIN, 14);

    private final DatabaseManager dbManager;
    private final DefaultTableModel tableModel;
    private final JTable table;

    JTextField fieldDept = new JTextField();
    JTextField fieldHead = new JTextField();
    JTextField fieldDean = new JTextField();

    JButton btnAdd = new JButton("Add");
    JButton btnUpdate = new JButton("Update");
    JButton btnDelete = new JButton("Delete");
    JButton btnClear = new JButton("Clear");

    public DepartmentPanel(DatabaseManager dbManager){
        this.dbManager = dbManager;

        tableModel = new DefaultTableModel(
            new String[]{
                "College Dept","Department Head","Dean"
            }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        setLayout(null);
        setBackground(Color.decode("#1a1a1a"));

        buildTable();
        buildForm();
        buildButtons();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRowSelected();
            }
        });

        refreshTable();
    }

    private void buildTable(){

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

    private void buildForm(){
        
        addLabel("College Depet: ", 10, 218);
        addLabel("Department Head: ", 10, 253);
        addLabel("Dean: ", 10, 288);
        
        styleField(fieldDept);
        fieldDept.setBounds(180, 215, 240, 28);

        styleField(fieldHead);
        fieldHead.setBounds(180, 250, 240, 28);

        styleField(fieldDean);
        fieldDean.setBounds(180, 285,240,28);

        add(fieldDept);
        add(fieldHead);
        add(fieldDean);
    }

    private void buildButtons(){

        styleButton(btnAdd, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        styleButton(btnUpdate, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        styleButton(btnDelete, Color.decode("#3a1a1a"), Color.decode("#cc6666"));
        styleButton(btnClear, Color.decode("#2a2a2a"), Color.decode("#888860"));

        btnAdd.setBounds(10, 355, 100, 40);
        btnUpdate.setBounds(120, 355, 100, 40);
        btnDelete.setBounds(230, 335, 100, 40);
        btnClear.setBounds(340, 335, 100, 40);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> onFields());

        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);
    }

    private void onAdd(){

        if (!validateInputs()){
            return;
        }

        try{
            dbManager.addDepartment(
                fieldDept.getText().trim(),
                fieldHead.getText().trim(),
                fieldDean.getText().trim()
            );

            refreshTable();
            clearFields();

        } catch (SQLException ex){
            showError(ex.getMessage());
        }
    }

    private void onUpdate(){

        if (table.getSelectedRow() < 0){
            showError("Select a department to update.");
            return;
        }

        if (!validateInputs()){
            return;
        }

        try{
            dbManager.updateDepartment(
                fieldDept.getText().trim(),
                fieldHead.getText().trim(),
                fieldDean.getText().trim()
            );

            refreshTable();
            clearFields();

        } catch (SQLException ex){
            showError(ex.getMessage());
        }
    }

    private void onDelete(){

        if (table.getSelectedRow() < 0){
            showError("Select a department to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete this department?",
            "Comfirm",
            JOptionPane.YES_NO_CANCEL_OPTION
        );
        
        if (confirm != JOptionPane.YES_OPTION){
            return;
        }

        try{
            dbManager.deleteDepartment(
                fieldDept.getText().trim()
            );

            refreshTable();
            clearFields();

        } catch (SQLException ex){
            showError(ex.getMessage());
        }
    }

    private void onRowSelected() {

        int row = table.getSelectedRow();
        if (row < 0) return;

        fieldDept.setText(tableModel.getValueAt(row,0).toString());
        fieldHead.setText(tableModel.getValueAt(row,1).toString());
        fieldDean.setText(tableModel.getValueAt(row,2).toString());
    }

    private void refreshTable() {

        tableModel.setRowCount(0);

        try {
            for (Object[] r : dbManager.getAllDepartments()) {
                tableModel.addRow(r);
            }
        } catch (SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private boolean validateInputs() {

        if (fieldDept.getText().isBlank() ||
            fieldHead.getText().isBlank() ||
            fieldDean.getText().isBlank()) {

            showError("All fields are required.");
            return false;
        }

        return true;
    }

    private void clearFields() {

        fieldDept.setText("");
        fieldHead.setText("");
        fieldDean.setText("");

        table.clearSelection();
    }
}