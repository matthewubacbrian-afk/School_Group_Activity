package com.schooldb.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // --- Connection Constants ---
    private static final String DB_URL      = "jdbc:postgresql://localhost:5432/school_db";
    private static final String DB_USER     = "postgres";
    private static final String DB_PASSWORD = "@matthew."; // Change before running

    // --- SQL: Schema Initialization ---
    private static final String SQL_CREATE_STUDENTS =
        "CREATE TABLE IF NOT EXISTS students (" +
        "  student_id SERIAL PRIMARY KEY," +
        "  first_name VARCHAR(50)," +
        "  last_name  VARCHAR(50)," +
        "  birthdate  DATE," +
        "  course     VARCHAR(50)" +
        ")";

    private static final String SQL_CREATE_SUBJECTS =
        "CREATE TABLE IF NOT EXISTS subjects (" +
        "  subject_id   SERIAL PRIMARY KEY," +
        "  subject_name VARCHAR(100)," +
        "  units        INTEGER" +
        ")";

    private static final String SQL_CREATE_ENROLLMENTS =
        "CREATE TABLE IF NOT EXISTS enrollments (" +
        "  enrollment_id SERIAL PRIMARY KEY," +
        "  student_id    INTEGER," +
        "  subject_id    INTEGER," +
        "  semester      VARCHAR(20)," +
        "  grade         NUMERIC(3,2)," +
        "  FOREIGN KEY (student_id) REFERENCES students(student_id)," +
        "  FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)" +
        ")";

    // --- SQL: Student Operations ---
    private static final String SQL_SELECT_ALL_STUDENTS =
        "SELECT * FROM students ORDER BY student_id";

    private static final String SQL_INSERT_STUDENT =
        "INSERT INTO students (first_name, last_name, birthdate, course) VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE_STUDENT =
        "UPDATE students SET first_name=?, last_name=?, birthdate=?, course=? WHERE student_id=?";

    private static final String SQL_DELETE_STUDENT =
        "DELETE FROM students WHERE student_id=?";

    // --- SQL: Subject Operations ---
    private static final String SQL_SELECT_ALL_SUBJECTS =
        "SELECT * FROM subjects ORDER BY subject_id";

    private static final String SQL_INSERT_SUBJECT =
        "INSERT INTO subjects (subject_name, units) VALUES (?, ?)";

    private static final String SQL_UPDATE_SUBJECT =
        "UPDATE subjects SET subject_name=?, units=? WHERE subject_id=?";

    private static final String SQL_DELETE_SUBJECT =
        "DELETE FROM subjects WHERE subject_id=?";

    // --- SQL: Enrollment Operations ---
    private static final String SQL_INSERT_ENROLLMENT =
        "INSERT INTO enrollments (student_id, subject_id, semester, grade) VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_ENROLLMENT_REPORT =
        "SELECT s.first_name, s.last_name, sub.subject_name, e.grade " +
        "FROM enrollments e " +
        "JOIN students s   ON e.student_id  = s.student_id " +
        "JOIN subjects sub ON e.subject_id  = sub.subject_id";

    // -------------------------------------------------------------------------
    // Connection
    // -------------------------------------------------------------------------

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // -------------------------------------------------------------------------
    // Schema Initialization
    // -------------------------------------------------------------------------

    public void initializeSchema() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(SQL_CREATE_STUDENTS);
            stmt.execute(SQL_CREATE_SUBJECTS);
            stmt.execute(SQL_CREATE_ENROLLMENTS);
        }
    }

    // -------------------------------------------------------------------------
    // Student CRUD
    // -------------------------------------------------------------------------

    public List<Object[]> getAllStudents() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_STUDENTS)) {

            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("birthdate"),
                    rs.getString("course")
                });
            }
        }
        return rows;
    }

    public void addStudent(String firstName, String lastName, String birthdate, String course)
            throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_STUDENT)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setDate(3, Date.valueOf(birthdate));  //"YYYY-MM-DD"
            ps.setString(4, course);
            ps.executeUpdate();
        }
    }

    public void updateStudent(int studentId, String firstName, String lastName,
                              String birthdate, String course) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STUDENT)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setDate(3, Date.valueOf(birthdate));
            ps.setString(4, course);
            ps.setInt(5, studentId);
            ps.executeUpdate();
        }
    }

    public void deleteStudent(int studentId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_STUDENT)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // Subject CRUD
    // -------------------------------------------------------------------------

    public List<Object[]> getAllSubjects() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_SUBJECTS)) {

            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("subject_id"),
                    rs.getString("subject_name"),
                    rs.getInt("units")
                });
            }
        }
        return rows;
    }

    public void addSubject(String subjectName, int units) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_SUBJECT)) {
            ps.setString(1, subjectName);
            ps.setInt(2, units);
            ps.executeUpdate();
        }
    }

    public void updateSubject(int subjectId, String subjectName, int units) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_SUBJECT)) {
            ps.setString(1, subjectName);
            ps.setInt(2, units);
            ps.setInt(3, subjectId);
            ps.executeUpdate();
        }
    }

    public void deleteSubject(int subjectId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_SUBJECT)) {
            ps.setInt(1, subjectId);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // Enrollment Operations
    // -------------------------------------------------------------------------

    public void enrollStudent(int studentId, int subjectId, String semester, double grade)
            throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_ENROLLMENT)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setString(3, semester);
            ps.setDouble(4, grade);
            ps.executeUpdate();
        }
    }

    public List<Object[]> getEnrollmentReport() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ENROLLMENT_REPORT)) {

            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("subject_name"),
                    rs.getDouble("grade")
                });
            }
        }
        return rows;
    }
}
