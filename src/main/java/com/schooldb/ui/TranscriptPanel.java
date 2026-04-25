package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TranscriptPanel extends JPanel {

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    private final DatabaseManager dbManager;
    private final DefaultTableModel tableModel;
    private JTable table;

    JTextField fieldStudentId = new JTextField();
    JLabel lblName, lblAddress, lblDob, lblPob;
    JLabel lblUnits, lblGwa;
    JButton btnLoad, btnPrint;

    public TranscriptPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;

        setLayout(null);
        setBackground(Color.decode("#1a1a1a"));

        tableModel = new DefaultTableModel(
                new String[]{
                        "Term","School Year","Course Code",
                        "Descriptive Title","Credits",
                        "Instructor","Grade","Remarks"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        buildSearchRow();
        buildInfoBlock();
        buildTable();
        buildSummaryRow();
        buildButtons();
    }

    private void buildSearchRow() {
        addLabel("Student ID:", 10, 15);

        styleField(fieldStudentId);
        fieldStudentId.setBounds(160, 10, 120, 28);

        btnLoad = new JButton("Load");
        styleButton(btnLoad, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        btnLoad.setBounds(290, 10, 80, 28);
        btnLoad.addActionListener(e -> loadTranscript());

        add(fieldStudentId);
        add(btnLoad);
    }

    private void buildInfoBlock() {
        lblName = new JLabel("Name: ");
        lblAddress = new JLabel("Address: ");
        lblDob = new JLabel("Date of Birth: ");
        lblPob = new JLabel("Place of Birth: ");

        JLabel[] labels = {lblName, lblAddress, lblDob, lblPob};

        for (JLabel lbl : labels) {
            lbl.setFont(new Font("Arial", Font.PLAIN, 13));
            lbl.setForeground(Color.decode("#cccc66"));
        }

        lblName.setBounds(10, 50, 500, 22);
        lblAddress.setBounds(10, 72, 500, 22);
        lblDob.setBounds(10, 94, 300, 22);
        lblPob.setBounds(10, 116, 300, 22);

        add(lblName);
        add(lblAddress);
        add(lblDob);
        add(lblPob);
    }

    private void buildTable() {
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
        scroll.setBounds(10, 145, 720, 190);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#F5E642")));

        add(scroll);
    }

    private void buildSummaryRow() {
        lblUnits = new JLabel("Total Units: 0");
        lblGwa = new JLabel("GWA: 0.00");

        JLabel[] labels = {lblUnits, lblGwa};

        for (JLabel lbl : labels) {
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            lbl.setForeground(Color.decode("#F5E642"));
        }

        lblUnits.setBounds(10, 345, 200, 22);
        lblGwa.setBounds(220, 345, 200, 22);

        add(lblUnits);
        add(lblGwa);
    }

    private void buildButtons() {
        btnPrint = new JButton("Print");

        styleButton(btnPrint, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        btnPrint.setBounds(10, 375, 100, 35);

        btnPrint.addActionListener(e -> {
            try {
                table.print();
            } catch (Exception ex) {
                showError("Print failed: " + ex.getMessage());
            }
        });

        add(btnPrint);
    }

    private void loadTranscript() {
        if (fieldStudentId.getText().isBlank()) {
            showError("Enter a Student ID.");
            return;
        }

        try {
            int id = Integer.parseInt(fieldStudentId.getText().trim());

            List<Object[]> students = dbManager.getAllStudents();
            for (Object[] s : students) {
                if ((int) s[0] == id) {
                    lblName.setText("Name: " + s[1] + ", " + s[2]);
                    lblAddress.setText("Address: " + s[3]);
                    lblDob.setText("Date of Birth: " + s[4]);
                    lblPob.setText("Place of Birth: " + s[5]);
                    break;
                }
            }

            tableModel.setRowCount(0);

            List<Object[]> rows = dbManager.getTranscriptByStudent(id);

            int totalUnits = 0;
            double weightedSum = 0.0;

            for (Object[] r : rows) {
                String term = r[0].toString();
                String sy = r[1].toString();
                String code = r[2].toString();
                String title = r[3].toString();
                int credits = (int) r[4];
                String instructor = r[5].toString();

                String grade = r[6] != null ? r[6].toString() : "INC";

                String remarks = "INC";

                if (r[6] != null) {
                    double g = ((Number) r[6]).doubleValue();
                    remarks = g <= 3.0 ? "PASSED" : "FAILED";
                    weightedSum += g * credits;
                    totalUnits += credits;
                }

                tableModel.addRow(new Object[]{
                        term, sy, code, title,
                        credits, instructor, grade, remarks
                });
            }

            lblUnits.setText("Total Units: " + totalUnits);

            double gwa = totalUnits > 0 ? weightedSum / totalUnits : 0.0;

            lblGwa.setText(String.format("GWA: %.2f", gwa));

        } catch (NumberFormatException e) {
            showError("Student ID must be a number.");
        } catch (SQLException ex) {
            showError("Error: " + ex.getMessage());
        }
    }


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
}