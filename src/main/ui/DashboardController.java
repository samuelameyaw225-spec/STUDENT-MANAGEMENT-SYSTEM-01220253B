package ui;

import domain.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import repository.StudentRepositoryImpl;
import service.StudentService;
import java.util.List;

public class DashboardController {

    @FXML private Label lblTotalStudents;
    @FXML private Label lblActiveStudents;
    @FXML private Label lblInactiveStudents;
    @FXML private Label lblAvgGpa;

    private StudentService studentService;

    @FXML
    public void initialize() {
        // Initialize the service with the repository
        studentService = new StudentService(new StudentRepositoryImpl());

        // Fill the labels with data from the database
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        try {
            // Fetch all student records
            List<Student> students = studentService.getAllStudents();

            // 1. Total Students count
            int total = students.size();

            // 2. Active vs Inactive counts
            long active = students.stream()
                    .filter(s -> "Active".equalsIgnoreCase(s.getStatus()))
                    .count();
            long inactive = total - active;

            // 3. Average GPA calculation
            double avgGpa = students.stream()
                    .mapToDouble(Student::getGpa)
                    .average()
                    .orElse(0.0);

            // Update the UI labels
            lblTotalStudents.setText(String.valueOf(total));
            lblActiveStudents.setText(String.valueOf(active));
            lblInactiveStudents.setText(String.valueOf(inactive));
            lblAvgGpa.setText(String.format("%.2f", avgGpa));

        } catch (Exception e) {
            // If the database fails, show an error state
            lblTotalStudents.setText("Error");
            e.printStackTrace();
        }
    }
}