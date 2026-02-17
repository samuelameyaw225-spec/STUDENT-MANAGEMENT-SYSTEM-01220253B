package repository;

import domain.Student;
import java.util.List;

public interface IStudentRepository {
    void addStudent(Student student) throws Exception;
    List<Student> getAllStudents() throws Exception;
    void updateStudent(Student student) throws Exception;
    void deleteStudent(String studentId) throws Exception;
}