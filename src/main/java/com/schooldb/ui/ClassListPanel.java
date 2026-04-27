package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClassListPanel extends JPanel {

    Font myFont = new Font("Arial", Font.PLAIN, 14);

    private final DatabaseManager dbManager;
    private final DefaultTableModel tableModel;
    private JTable table;

    JComboBox<String> comboSection = new JComboBox<>();
    JComboBox<String> comboTerm = new JComboBox<>();

    JLabel lblCourseCode, lblTitle, lblDays, lblTime, lblRoom;
    JLabel lblEnrolled, lblPassed, lblFailed, lblInc;

    JButton btnLoad, btnPrint;

    public ClassListPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;

        setLayout(null);
        setBackground(Color.decode("#1a1a1a"));

        tableModel = new DefaultTableModel(
                new String[]{
                        "No.","Student ID","Student Name",
                        "Grade","Credits","Course Code"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        try {
            for (Object[] row : dbManager.getAllSections()) {
                comboSection.addItem(row[0].toString());
            }
        } catch (SQLException ex) {
            showError("Error: " + ex.getMessage());
        }

        if (comboSection.getItemCount() > 0) {
            comboSection.setSelectedIndex(0);
        }

        comboTerm.addItem("1st Sem");
        comboTerm.addItem("2nd Sem");
        comboTerm.addItem("Summer");
        comboTerm.setSelectedIndex(0);

        buildFilterRow();
        buildInfoBlock();
        buildTable();
        buildSummaryBlock();
        buildButtons();
    }

    private void buildFilterRow() {
        addLabel("Section:", 10, 15);

        comboSection.setFont(myFont);
        comboSection.setBackground(Color.decode("#2a2a2a"));
        comboSection.setForeground(Color.decode("#e0d060"));
        comboSection.setBounds(100, 10, 200, 28);

        addLabel("Term:", 315, 15);

        comboTerm.setFont(myFont);
        comboTerm.setBackground(Color.decode("#2a2a2a"));
        comboTerm.setForeground(Color.decode("#e0d060"));
        comboTerm.setBounds(375, 10, 150, 28);

        btnLoad = new JButton("Load");
        styleButton(btnLoad, Color.decode("#F5E642"), Color.decode("#1a1a1a"));
        btnLoad.setBounds(540, 10, 80, 28);
        btnLoad.addActionListener(e -> loadClassList());

        add(comboSection);
        add(comboTerm);
        add(btnLoad);
    }

    private void buildInfoBlock() {
        lblCourseCode = new JLabel("Course Code: ");
        lblTitle = new JLabel("Title: ");
        lblDays = new JLabel("Days: ");
        lblTime = new JLabel("Time: ");
        lblRoom = new JLabel("Room: ");

        JLabel[] labels = {lblCourseCode, lblTitle, lblDays, lblTime, lblRoom};

        for (JLabel lbl : labels) {
            lbl.setFont(new Font("Arial", Font.PLAIN, 13));
            lbl.setForeground(Color.decode("#cccc66"));
        }

        lblCourseCode.setBounds(10, 50, 250, 22);
        lblTitle.setBounds(10, 72, 500, 22);
        lblDays.setBounds(10, 94, 200, 22);
        lblTime.setBounds(220, 94, 250, 22);
        lblRoom.setBounds(10, 116, 200, 22);

        add(lblCourseCode);
        add(lblTitle);
        add(lblDays);
        add(lblTime);
        add(lblRoom);
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
        scroll.setBounds(10, 145, 720, 170);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#F5E642")));

        add(scroll);
    }

    private void buildSummaryBlock() {
        lblEnrolled = new JLabel("No. Enrolled: 0");
        lblPassed = new JLabel("Passed: 0");
        lblFailed = new JLabel("Failed: 0");
        lblInc = new JLabel("Incomplete: 0");

        JLabel[] labels = {lblEnrolled, lblPassed, lblFailed, lblInc};

        for (JLabel lbl : labels) {
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            lbl.setForeground(Color.decode("#F5E642"));
        }

        lblEnrolled.setBounds(10, 325, 200, 22);
        lblPassed.setBounds(220, 325, 150, 22);
        lblFailed.setBounds(380, 325, 150, 22);
        lblInc.setBounds(540, 325, 180, 22);

        add(lblEnrolled);
        add(lblPassed);
        add(lblFailed);
        add(lblInc);
    }

    private void buildButtons() {
        btnPrint = new JButton("Print");

        styleButton(btnPrint, Color.decode("#3a3a20"), Color.decode("#F5E642"));
        btnPrint.setBounds(10, 355, 100, 35);

        btnPrint.addActionListener(e -> {
            try {
                table.print();
            } catch (Exception ex) {
                showError("Print failed: " + ex.getMessage());
            }
        });

        add(btnPrint);
    }

    private void loadClassList() {
        if (comboSection.getSelectedItem() == null) {
            showError("No sections available.");
            return;
        }

        String section = comboSection.getSelectedItem().toString().trim();
        String term = comboTerm.getSelectedItem().toString().trim();

        try {
            for (Object[] row : dbManager.getAllSections()) {
                if (row[0].toString().equals(section)) {
                    lblCourseCode.setText("Course Code: " + row[1]);
                    lblDays.setText("Days: " + row[3]);
                    lblTime.setText("Time: " + row[4]);
                    lblRoom.setText("Room: " + row[5]);

                    for (Object[] c : dbManager.getAllCourses()) {
                        if (c[0].toString().equals(row[1].toString())) {
                            lblTitle.setText("Title: " + c[1]);
                            break;
                        }
                    }
                    break;
                }
            }

            tableModel.setRowCount(0);

            List<Object[]> rows =
                    dbManager.getClassListBySection(section, term);

            int rowNum = 1;
            int passed = 0, failed = 0, inc = 0;

            for (Object[] r : rows) {
                String name = r[1] + ", " + r[2];
                Object grade = r[4];

                if (grade == null) {
                    inc++;
                } else {
                    double g = ((Number) grade).doubleValue();
                    if (g <= 3.0) passed++;
                    else failed++;
                }

                tableModel.addRow(new Object[]{
                        rowNum++,
                        r[0],
                        name,
                        grade != null ? grade : "INC",
                        r[3],
                        r[5]
                });
            }

            int enrolled = rows.size();

            lblEnrolled.setText("No. Enrolled: " + enrolled);
            lblPassed.setText("Passed: " + passed);
            lblFailed.setText("Failed: " + failed);
            lblInc.setText("Incomplete: " + inc);

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