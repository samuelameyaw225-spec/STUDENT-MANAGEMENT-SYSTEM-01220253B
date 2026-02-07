package repository;

import domain.Student;
import util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentRepositoryImpl implements IStudentRepository {

    @Override
    public void addStudent(Student student) throws Exception {
        String sql = "INSERT INTO students (student_id, full_name, programme, level, gpa, email, phone_number, date_added, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getProgramme());
            pstmt.setInt(4, student.getLevel());
            pstmt.setDouble(5, student.getGpa());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getPhoneNumber());
            pstmt.setString(8, student.getDateAdded().toString());
            pstmt.setString(9, student.getStatus());

            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Student> getAllStudents() throws Exception {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("programme"),
                        rs.getInt("level"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        LocalDate.parse(rs.getString("date_added")),
                        rs.getString("status")
                );
                students.add(student);
            }
        }
        return students;
    }

    @Override
    public void updateStudent(Student student) throws Exception {
        String sql = "UPDATE students SET full_name = ?, programme = ?, level = ?, gpa = ?, " +
                "email = ?, phone_number = ?, status = ? WHERE student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getFullName());
            pstmt.setString(2, student.getProgramme());
            pstmt.setInt(3, student.getLevel());
            pstmt.setDouble(4, student.getGpa());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhoneNumber());
            pstmt.setString(7, student.getStatus());
            pstmt.setString(8, student.getStudentId()); // WHERE clause

            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteStudent(String studentId) throws Exception {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.executeUpdate();
        }
    }
}