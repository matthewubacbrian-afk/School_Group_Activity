package com.schooldb.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/school_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "";

    // Schema Initialization
    private static final String SQL_CREATE_DEPARTMENTS = 
        "CREATE TABLE IF NOT EXISTS college_departments (" +
        "  college_dept VARCHAR(100) PRIMARY KEY," +
        "  department_head VARCHAR(100)," +
        "  dean VARCHAR(100)" +
        ")";

    private static final String SQL_CREATE_STUDENTS =
        "CREATE TABLE IF NOT EXISTS students (" +
        "  student_id SERIAL PRIMARY KEY," +
        "  student_name VARCHAR(100)," +
        "  address VARCHAR(100)," +
        "  date_of_birth DATE," +
        "  place_of_birth VARCHAR(100)" +
        ")";

    private static final String SQL_CREATE_PROGRAMS =
        "CREATE TABLE IF NOT EXISTS programs (" +
        "  program_name VARCHAR(100) PRIMARY KEY," +
        "  college_dept VARCHAR(100)," +
        "  FOREIGN KEY (college_dept) REFERENCES college_departments(college_dept)" +
        ")";

    private static final String SQL_CREATE_COURSES =
        "CREATE TABLE IF NOT EXISTS courses (" +
        "  course_code VARCHAR(20) PRIMARY KEY," +
        "  descriptive_title VARCHAR(20)," +
        "  credits INTEGER" +
        ")";

    private static final String SQL_CREATE_INSTRUCTORS =
        "CREATE TABLE IF NOT EXISTS instructors (" +
        "  instructor_id SERIAL PRIMARY KEY," +
        "  instructor_name VARCHAR(100)," +
        "  college_dept VARCHAR(100)," +
        "  FOREIGN KEY (college_dept) REFERENCES college_departments(college_dept)" +
        ")";

    private static final String SQL_CREATE_SECTIONS =
        "CREATE TABLE IF NOT EXISTS sections (" +
        "  section_id SERIAL PRIMARY KEY," + 
        "  section_name VARCHAR(50)," +
        "  course_code VARCHAR(20)," +
        "  instructor_id INTEGER," +
        "  days VARCHAR(20)," +
        "  time VARCHAR(50)," + 
        "  room VARCHAR(50)," +
        "  FOREIGN KEY (course_code) REFERENCES courses(course_code)," +
        "  FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id)" +
        ")";

    private static final String SQL_CREATE_ENROLLMENTS =
        "CREATE TABLE IF NOT EXISTS enrollments (" +
        "  enrollment_id SERIAL PRIMARY KEY," +
        "  student_id INTEGER," +
        "  program_name VARCHAR(100)," +
        "  school_year VARCHAR(20)," +
        "  term VARCHAR(20)," +
        "  date_enrolled DATE DEFAULT CURRENT_DATE," +
        "  FOREIGN KEY (student_id) REFERENCES students(student_id)," +
        "  FOREIGN KEY (program_name) REFERENCES programs(program_name)" +
        ")";

    private static final String SQL_CREATE_COURSE_ENLISTMENTS =
        "CREATE TABLE IF NOT EXISTS course_enlistments (" +
        "  enlistment_id SERIAL PRIMARY KEY," +
        "  enrollment_id INTEGER," +
        "  course_code VARCHAR(20)," +
        "  instructor_id INTEGER," +
        "  section_id INTEGER," +
        "  date_enlisted DATE DEFAULT CURRENT_DATE," +
        "  grade NUMERIC(3,2)," +
        "  FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)," +
        "  FOREIGN KEY (course_code) REFERENCES courses(course_code)," +
        "  FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id)," +
        "  FOREIGN KEY (section_id) REFERENCES sections(section_id)" +
        ")";

    private static final String SQL_CREATE_PROGRAM_COURSES =
        "CREATE TABLE IF NOT EXISTS program_courses (" +
        "  program_name VARCHAR(100)," +
        "  course_code VARCHAR(20)," +
        "  PRIMARY KEY (program_name, course_code)," +
        "  FOREIGN KEY (program_name) REFERENCES programs(program_name)," +
        "  FOREIGN KEY (course_code) REFERENCES courses(course_code)" +
        ")";

    // Operations
    private static final String SQL_SELECT_ALL_STUDENTS =
        "SELECT * FROM students ORDER BY student_id";

    private static final String SQL_INSERT_STUDENT =
        "INSERT INTO students (student_name, address, date_of_birth, place_of_birth) VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE_STUDENT =
        "UPDATE students SET student_name=?, address=?, date_of_birth=?, place_of_birth=? WHERE student_id=?";

    private static final String SQL_DELETE_STUDENT =
        "DELETE FROM students WHERE student_id=?";

    private static final String SQL_SELECT_ALL_COURSES =
        "SELECT * FROM courses ORDER BY course_code";

    private static final String SQL_INSERT_COURSE =
        "INSERT INTO courses (course_code, descriptive_title, credits) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE_COURSE =
        "UPDATE courses SET descriptive_title=?, credits=? WHERE course_code=?";

    private static final String SQL_DELETE_COURSE =
        "DELETE FROM courses WHERE course_code=?";

    private static final String SQL_INSERT_ENROLLMENT =
        "INSERT INTO enrollments (student_id, program_name, school_year, term) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_COURSE_ENLISTMENT =
        "INSERT INTO course_enlistments (enrollment_id, course_code, instructor_id, section_id, grade) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_ENROLLMENT_REPORT =
        "SELECT s.student_name, p.program_name, c.descriptive_title, sec.section_name, i.instructor_name, ce.grade " +
        "FROM course_enlistments ce " +
        "JOIN enrollments e ON ce.enrollment_id = e.enrollment_id " +
        "JOIN students s ON e.student_id = s.student_id " +
        "JOIN programs p ON e.program_name = p.program_name " +
        "JOIN courses c ON ce.course_code = c.course_code " +
        "JOIN sections sec ON ce.section_id = sec.section_id " +
        "JOIN instructors i ON ce.instructor_id = i.instructor_id";
    
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void initializeSchema() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(SQL_CREATE_DEPARTMENTS);
            stmt.execute(SQL_CREATE_STUDENTS);
            stmt.execute(SQL_CREATE_COURSES);
            stmt.execute(SQL_CREATE_PROGRAMS);
            stmt.execute(SQL_CREATE_INSTRUCTORS);
            stmt.execute(SQL_CREATE_PROGRAM_COURSES);
            stmt.execute(SQL_CREATE_SECTIONS);
            stmt.execute(SQL_CREATE_ENROLLMENTS);
            stmt.execute(SQL_CREATE_COURSE_ENLISTMENTS);

            System.out.println("Database schema initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing schema: " + e.getMessage());
            throw e;
        }
    }


    //CRUD
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

    public List<Object[]> getAllSubjects() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_COURSES)) {

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
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_COURSE)) {
            ps.setString(1, subjectName);
            ps.setInt(2, units);
            ps.executeUpdate();
        }
    }

    public void updateSubject(int subjectId, String subjectName, int units) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_COURSE)) {
            ps.setString(1, subjectName);
            ps.setInt(2, units);
            ps.setInt(3, subjectId);
            ps.executeUpdate();
        }
    }

    public void deleteSubject(int subjectId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_COURSE)) {
            ps.setInt(1, subjectId);
            ps.executeUpdate();
        }
    }

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
