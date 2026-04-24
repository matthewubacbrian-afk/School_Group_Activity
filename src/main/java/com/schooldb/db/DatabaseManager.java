package com.schooldb.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/school_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234"; // change this

    // -------------------------------------------------------------------------
    // Schema Initialization
    // -------------------------------------------------------------------------

    private static final String SQL_CREATE_DEPARTMENTS = "CREATE TABLE IF NOT EXISTS college_departments (" +
            "  college_dept VARCHAR(100) PRIMARY KEY," +
            "  department_head VARCHAR(100)," +
            "  dean VARCHAR(100)" +
            ")";

    private static final String SQL_CREATE_STUDENTS = "CREATE TABLE IF NOT EXISTS students (" +
            "  student_id SERIAL PRIMARY KEY," +
            "  last_name VARCHAR(100)," +
            "  first_name VARCHAR(100)," +
            "  address VARCHAR(100)," +
            "  date_of_birth DATE," +
            "  place_of_birth VARCHAR(100)" +
            ")";

    private static final String SQL_CREATE_PROGRAMS = "CREATE TABLE IF NOT EXISTS programs (" +
            "  program_name VARCHAR(100) PRIMARY KEY," +
            "  college_dept VARCHAR(100)," +
            "  FOREIGN KEY (college_dept) REFERENCES college_departments(college_dept)" +
            ")";

    private static final String SQL_CREATE_COURSES = "CREATE TABLE IF NOT EXISTS courses (" +
            "  course_code VARCHAR(20) PRIMARY KEY," +
            "  descriptive_title VARCHAR(200)," +
            "  credits INTEGER" +
            ")";

    private static final String SQL_CREATE_INSTRUCTORS = "CREATE TABLE IF NOT EXISTS instructors (" +
            "  instructor_id SERIAL PRIMARY KEY," +
            "  instructor_name VARCHAR(100)," +
            "  college_dept VARCHAR(100)," +
            "  FOREIGN KEY (college_dept) REFERENCES college_departments(college_dept)" +
            ")";

    private static final String SQL_CREATE_SECTIONS = "CREATE TABLE IF NOT EXISTS sections (" +
            "  section_name VARCHAR(50) PRIMARY KEY," +
            "  course_code VARCHAR(20)," +
            "  instructor_id INTEGER," +
            "  days VARCHAR(20)," +
            "  time VARCHAR(50)," +
            "  room VARCHAR(50)," +
            "  FOREIGN KEY (course_code) REFERENCES courses(course_code)," +
            "  FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id)" +
            ")";

    private static final String SQL_CREATE_ENROLLMENTS = "CREATE TABLE IF NOT EXISTS enrollments (" +
            "  enrollment_id SERIAL PRIMARY KEY," +
            "  student_id INTEGER," +
            "  program_name VARCHAR(100)," +
            "  school_year VARCHAR(20)," +
            "  term VARCHAR(20)," +
            "  date_enrolled DATE DEFAULT CURRENT_DATE," +
            "  FOREIGN KEY (student_id) REFERENCES students(student_id)," +
            "  FOREIGN KEY (program_name) REFERENCES programs(program_name)" +
            ")";

    private static final String SQL_CREATE_COURSE_ENLISTMENTS = "CREATE TABLE IF NOT EXISTS course_enlistments (" +
            "  enlistment_id SERIAL PRIMARY KEY," +
            "  enrollment_id INTEGER," +
            "  section_name VARCHAR(50)," +
            "  date_enlisted DATE DEFAULT CURRENT_DATE," +
            "  grade NUMERIC(3,2)," +
            "  FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)," +
            "  FOREIGN KEY (section_name) REFERENCES sections(section_name)" +
            ")";

    private static final String SQL_CREATE_PROGRAM_COURSES = "CREATE TABLE IF NOT EXISTS program_courses (" +
            "  program_name VARCHAR(100)," +
            "  course_code VARCHAR(20)," +
            "  PRIMARY KEY (program_name, course_code)," +
            "  FOREIGN KEY (program_name) REFERENCES programs(program_name)," +
            "  FOREIGN KEY (course_code) REFERENCES courses(course_code)" +
            ")";

    // -------------------------------------------------------------------------
    // SQL Constants — Students
    // -------------------------------------------------------------------------

    private static final String SQL_SELECT_ALL_STUDENTS = "SELECT * FROM students ORDER BY student_id";

    private static final String SQL_INSERT_STUDENT = "INSERT INTO students (last_name, first_name, address, date_of_birth, place_of_birth) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_STUDENT = "UPDATE students SET last_name=?, first_name=?, address=?, date_of_birth=?, place_of_birth=? WHERE student_id=?";

    private static final String SQL_DELETE_STUDENT = "DELETE FROM students WHERE student_id=?";

    // -------------------------------------------------------------------------
    // SQL Constants — Courses
    // -------------------------------------------------------------------------

    private static final String SQL_SELECT_ALL_COURSES = "SELECT * FROM courses ORDER BY course_code";

    private static final String SQL_INSERT_COURSE = "INSERT INTO courses (course_code, descriptive_title, credits) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE_COURSE = "UPDATE courses SET descriptive_title=?, credits=? WHERE course_code=?";

    private static final String SQL_DELETE_COURSE = "DELETE FROM courses WHERE course_code=?";

    // -------------------------------------------------------------------------
    // SQL Constants — Enrollments / Enlistments
    // -------------------------------------------------------------------------

    private static final String SQL_INSERT_ENROLLMENT = "INSERT INTO enrollments (student_id, program_name, school_year, term) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_COURSE_ENLISTMENT = "INSERT INTO course_enlistments (enrollment_id, section_name, grade) VALUES (?, ?, ?)";

    private static final String SQL_SELECT_ENROLLMENT_REPORT = "SELECT s.first_name, s.last_name, c.descriptive_title, sec.section_name, i.instructor_name, ce.grade "
            +
            "FROM course_enlistments ce " +
            "JOIN sections sec ON ce.section_name = sec.section_name " +
            "JOIN courses c ON sec.course_code = c.course_code " +
            "JOIN instructors i ON sec.instructor_id = i.instructor_id " +
            "JOIN enrollments e ON ce.enrollment_id = e.enrollment_id " +
            "JOIN students s ON e.student_id = s.student_id " +
            "JOIN programs p ON e.program_name = p.program_name";

    // -------------------------------------------------------------------------
    // SQL Constants — Departments
    // -------------------------------------------------------------------------

    private static final String SQL_SELECT_ALL_DEPARTMENTS = "SELECT * FROM college_departments ORDER BY college_dept";

    private static final String SQL_INSERT_DEPARTMENT = "INSERT INTO college_departments (college_dept, department_head, dean) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE_DEPARTMENT = "UPDATE college_departments SET department_head=?, dean=? WHERE college_dept=?";

    private static final String SQL_DELETE_DEPARTMENT = "DELETE FROM college_departments WHERE college_dept=?";

    // -------------------------------------------------------------------------
    // SQL Constants — Programs
    // -------------------------------------------------------------------------

    private static final String SQL_SELECT_ALL_PROGRAMS = "SELECT * FROM programs ORDER BY program_name";

    private static final String SQL_INSERT_PROGRAM = "INSERT INTO programs (program_name, college_dept) VALUES (?, ?)";

    private static final String SQL_UPDATE_PROGRAM = "UPDATE programs SET college_dept=? WHERE program_name=?";

    private static final String SQL_DELETE_PROGRAM = "DELETE FROM programs WHERE program_name=?";

    // -------------------------------------------------------------------------
    // SQL Constants — Instructors
    // -------------------------------------------------------------------------

    private static final String SQL_SELECT_ALL_INSTRUCTORS = "SELECT * FROM instructors ORDER BY instructor_id";

    private static final String SQL_INSERT_INSTRUCTOR = "INSERT INTO instructors (instructor_name, college_dept) VALUES (?, ?)";

    private static final String SQL_UPDATE_INSTRUCTOR = "UPDATE instructors SET instructor_name=?, college_dept=? WHERE instructor_id=?";

    private static final String SQL_DELETE_INSTRUCTOR = "DELETE FROM instructors WHERE instructor_id=?";

    // -------------------------------------------------------------------------
    // SQL Constants — Sections
    // -------------------------------------------------------------------------

    private static final String SQL_SELECT_ALL_SECTIONS = "SELECT * FROM sections ORDER BY section_name";

    private static final String SQL_INSERT_SECTION = "INSERT INTO sections (section_name, course_code, instructor_id, days, time, room) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_SECTION = "UPDATE sections SET course_code=?, instructor_id=?, days=?, time=?, room=? WHERE section_name=?";

    private static final String SQL_DELETE_SECTION = "DELETE FROM sections WHERE section_name=?";

    // =========================================================================
    // Connection
    // =========================================================================

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // =========================================================================
    // Schema Init
    // =========================================================================

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

    // =========================================================================
    // CRUD — Students
    // =========================================================================

    public List<Object[]> getAllStudents() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_STUDENTS)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getInt("student_id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("address"),
                        rs.getDate("date_of_birth"),
                        rs.getString("place_of_birth")
                });
            }
        }
        return rows;
    }

    public void addStudent(String lastName, String firstName, String address, String birthdate, String placeOfBirth)
            throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_STUDENT)) {
            ps.setString(1, lastName);
            ps.setString(2, firstName);
            ps.setString(3, address);
            ps.setDate(4, Date.valueOf(birthdate));
            ps.setString(5, placeOfBirth);
            ps.executeUpdate();
        }
    }

    public void updateStudent(int studentId, String lastName, String firstName,
            String address, String birthdate, String placeOfBirth) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STUDENT)) {
            ps.setString(1, lastName);
            ps.setString(2, firstName);
            ps.setString(3, address);
            ps.setDate(4, Date.valueOf(birthdate));
            ps.setString(5, placeOfBirth);
            ps.setInt(6, studentId);
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

    // =========================================================================
    // CRUD — Courses
    // =========================================================================

    public List<Object[]> getAllCourses() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_COURSES)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getString("course_code"),
                        rs.getString("descriptive_title"),
                        rs.getInt("credits")
                });
            }
        }
        return rows;
    }

    public void addCourse(String code, String title, int units) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_COURSE)) {
            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, units);
            ps.executeUpdate();
        }
    }

    public void updateCourse(String courseCode, String subjectName, int units) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_COURSE)) {
            ps.setString(1, subjectName);
            ps.setInt(2, units);
            ps.setString(3, courseCode);
            ps.executeUpdate();
        }
    }

    public void deleteCourse(String courseCode) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE_COURSE)) {
            ps.setString(1, courseCode);
            ps.executeUpdate();
        }
    }

    // =========================================================================
    // Enrollments / Enlistments
    // =========================================================================

    public void enrollStudent(int studentId, String programName, String schoolYear, String term) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_ENROLLMENT)) {
            ps.setInt(1, studentId);
            ps.setString(2, programName);
            ps.setString(3, schoolYear);
            ps.setString(4, term);
            ps.executeUpdate();
        }
    }

    public void enlistInSection(int enrollmentId, String sectionName, double grade) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_COURSE_ENLISTMENT)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, sectionName);
            ps.setDouble(3, grade);
            ps.executeUpdate();
        }
    }

    public List<Object[]> getEnrollmentReport() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECT_ENROLLMENT_REPORT)) {

            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("descriptive_title"),
                        rs.getDouble("grade")
                });
            }
        }
        return rows;
    }

    // =========================================================================
    // CRUD — Departments
    // =========================================================================

    /** Returns all departments as [college_dept, department_head, dean]. */
    public List<Object[]> getAllDepartments() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_DEPARTMENTS)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getString("college_dept"),
                        rs.getString("department_head"),
                        rs.getString("dean")
                });
            }
        }
        return rows;
    }

    public void addDepartment(String collegeDept, String departmentHead, String dean) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_DEPARTMENT)) {
            ps.setString(1, collegeDept);
            ps.setString(2, departmentHead);
            ps.setString(3, dean);
            ps.executeUpdate();
        }
    }

    /**
     * college_dept is the PK and cannot be changed; only head and dean are
     * updatable.
     */
    public void updateDepartment(String collegeDept, String departmentHead, String dean) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_DEPARTMENT)) {
            ps.setString(1, departmentHead);
            ps.setString(2, dean);
            ps.setString(3, collegeDept);
            ps.executeUpdate();
        }
    }

    public void deleteDepartment(String collegeDept) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE_DEPARTMENT)) {
            ps.setString(1, collegeDept);
            ps.executeUpdate();
        }
    }

    // =========================================================================
    // CRUD — Programs
    // =========================================================================

    /** Returns all programs as [program_name, college_dept]. */
    public List<Object[]> getAllPrograms() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_PROGRAMS)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getString("program_name"),
                        rs.getString("college_dept")
                });
            }
        }
        return rows;
    }

    public void addProgram(String programName, String collegeDept) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_PROGRAM)) {
            ps.setString(1, programName);
            ps.setString(2, collegeDept);
            ps.executeUpdate();
        }
    }

    /**
     * program_name is the PK and cannot be changed; only college_dept is updatable.
     */
    public void updateProgram(String programName, String collegeDept) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PROGRAM)) {
            ps.setString(1, collegeDept);
            ps.setString(2, programName);
            ps.executeUpdate();
        }
    }

    public void deleteProgram(String programName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE_PROGRAM)) {
            ps.setString(1, programName);
            ps.executeUpdate();
        }
    }

    // =========================================================================
    // CRUD — Instructors
    // =========================================================================

    /**
     * Returns all instructors as [instructor_id, instructor_name, college_dept].
     */
    public List<Object[]> getAllInstructors() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_INSTRUCTORS)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getInt("instructor_id"),
                        rs.getString("instructor_name"),
                        rs.getString("college_dept")
                });
            }
        }
        return rows;
    }

    public void addInstructor(String instructorName, String collegeDept) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_INSTRUCTOR)) {
            ps.setString(1, instructorName);
            ps.setString(2, collegeDept);
            ps.executeUpdate();
        }
    }

    public void updateInstructor(int instructorId, String instructorName, String collegeDept) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_INSTRUCTOR)) {
            ps.setString(1, instructorName);
            ps.setString(2, collegeDept);
            ps.setInt(3, instructorId);
            ps.executeUpdate();
        }
    }

    public void deleteInstructor(int instructorId) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE_INSTRUCTOR)) {
            ps.setInt(1, instructorId);
            ps.executeUpdate();
        }
    }

    // =========================================================================
    // CRUD — Sections
    // =========================================================================

    /**
     * Returns all sections as [section_name, course_code, instructor_id, days,
     * time, room].
     */
    public List<Object[]> getAllSections() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_SECTIONS)) {
            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getString("section_name"),
                        rs.getString("course_code"),
                        rs.getInt("instructor_id"),
                        rs.getString("days"),
                        rs.getString("time"),
                        rs.getString("room")
                });
            }
        }
        return rows;
    }

    public void addSection(String sectionName, String courseCode, int instructorId,
            String days, String time, String room) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_SECTION)) {
            ps.setString(1, sectionName);
            ps.setString(2, courseCode);
            ps.setInt(3, instructorId);
            ps.setString(4, days);
            ps.setString(5, time);
            ps.setString(6, room);
            ps.executeUpdate();
        }
    }

    /**
     * section_name is the PK and cannot be changed; all other fields are updatable.
     */
    public void updateSection(String sectionName, String courseCode, int instructorId,
            String days, String time, String room) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_SECTION)) {
            ps.setString(1, courseCode);
            ps.setInt(2, instructorId);
            ps.setString(3, days);
            ps.setString(4, time);
            ps.setString(5, room);
            ps.setString(6, sectionName);
            ps.executeUpdate();
        }
    }

    public void deleteSection(String sectionName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE_SECTION)) {
            ps.setString(1, sectionName);
            ps.executeUpdate();
        }
    }
}