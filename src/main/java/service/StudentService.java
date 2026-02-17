package service;

import domain.Student;
import repository.IStudentRepository;

import java.util.Arrays;
import java.util.List;

public class StudentService {

    private final IStudentRepository repository;

    // We pass the repository in via the constructor (Dependency Injection)
    public StudentService(IStudentRepository repository) {
        this.repository = repository;
    }

    // --- CRUD OPERATIONS ---

    public void addStudent(Student student) throws Exception {
        validateStudent(student); // Strict validation before saving
        repository.addStudent(student);
    }

    public void updateStudent(Student student) throws Exception {
        validateStudent(student); // Strict validation before updating
        repository.updateStudent(student);
    }

    public void deleteStudent(String studentId) throws Exception {
        repository.deleteStudent(studentId);
    }

    public List<Student> getAllStudents() throws Exception {
        return repository.getAllStudents();
    }

    // --- VALIDATION RULES (Section 6 of your rubric) ---

    public void validateStudent(Student student) {
        // 1. Student ID: 4 to 20 characters, letters and digits only
        if (student.getStudentId() == null || !student.getStudentId().matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new IllegalArgumentException("Student ID must be 4 to 20 alphanumeric characters.");
        }

        // 2. Full name: 2 to 60 characters, must not contain digits
        if (student.getFullName() == null || student.getFullName().length() < 2 ||
                student.getFullName().length() > 60 || student.getFullName().matches(".*\\d.*")) {
            throw new IllegalArgumentException("Full name must be 2 to 60 characters and contain no numbers.");
        }

        // 3. Programme: required
        if (student.getProgramme() == null || student.getProgramme().trim().isEmpty()) {
            throw new IllegalArgumentException("Programme is required.");
        }

        // must be 100, 200, 300, 400.
        List<Integer> validLevels = Arrays.asList(100, 200, 300, 400, 500, 600, 700);
        if (!validLevels.contains(student.getLevel())) {
            throw new IllegalArgumentException("Level must be one of: 100, 200, 300, 400, 500, 600, 700.");
        }

        // 5. GPA: between 0.0 and 5.0
        if (student.getGpa() < 0.0 || student.getGpa() > 4.0) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0.");
        }

        // 6. Email: must contain '@' and '.'
        if (student.getEmail() == null || !student.getEmail().contains("@") || !student.getEmail().contains(".")) {
            throw new IllegalArgumentException("Please enter a valid email address containing '@' and '.'.");
        }

        // 7. Phone number: 10 to 15 digits only
        if (student.getPhoneNumber() == null || !student.getPhoneNumber().matches("^\\d{10,15}$")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 to 15 digits.");
        }
    }
}