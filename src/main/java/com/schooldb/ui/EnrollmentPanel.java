package com.schooldb.ui;

import com.schooldb.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class EnrollmentPanel extends JPanel {
    
    private final DatabaseManager dbManager;

    public EnrollmentPanel(DatabaseManager  dbManager){
        this.dbManager = dbManager;
    }
}