package ui;

import domain.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.StudentRepositoryImpl;
import service.StudentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StudentsController {

    // Table Controls
    @FXML private TableView<Student> tableStudents;
    @FXML private TableColumn<Student, String> colId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colProgramme;
    @FXML private TableColumn<Student, Integer> colLevel;
    @FXML private TableColumn<Student, Double> colGpa;
    @FXML private TableColumn<Student, String> colStatus;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, Integer> phoneNumber;
    @FXML private TableColumn<Student, String> dateAdded;




    // Filter Controls
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilterProgramme;
    @FXML private ComboBox<Integer> cmbFilterLevel;
    @FXML private ComboBox<String> cmbFilterStatus;

    // Form Controls
    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtProgramme;
    @FXML private TextField txtGpa;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private ComboBox<Integer> cmbLevel;
    @FXML private ComboBox<String> cmbStatus;

    private StudentService studentService;
    private ObservableList<Student> studentList;
    private FilteredList<Student> filteredData;

    @FXML
    public void initialize() {
        studentService = new StudentService(new StudentRepositoryImpl());

        // Setup Form ComboBoxes
        ObservableList<Integer> levels = FXCollections.observableArrayList(100, 200, 300, 400);
        cmbLevel.setItems(levels);
        cmbFilterLevel.setItems(levels);

        ObservableList<String> statuses = FXCollections.observableArrayList("Active", "Inactive");
        cmbStatus.setItems(statuses);

        // Add an "All" option for the filter status
        cmbFilterStatus.setItems(FXCollections.observableArrayList("All", "Active", "Inactive"));

        // Hardcoded programmes for filtering (Optional: You can dynamically extract these later)
        cmbFilterProgramme.setItems(FXCollections.observableArrayList("All", "Computer Science", "Engineering", "Business", "Nursing"));

        // Map Table Columns to Student Object Properties
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colProgramme.setCellValueFactory(new PropertyValueFactory<>("programme"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        dateAdded.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));




        loadStudents();
        setupSearchAndFilter();
    }

    @FXML
    private void loadStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            studentList = FXCollections.observableArrayList(students);

            // Wrap the ObservableList in a FilteredList
            filteredData = new FilteredList<>(studentList, b -> true);

            // Wrap the FilteredList in a SortedList to allow column header sorting!
            SortedList<Student> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableStudents.comparatorProperty());

            tableStudents.setItems(sortedData);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load students: " + e.getMessage());
        }
    }

    private void setupSearchAndFilter() {
        // Listen for changes in the text field and combo boxes
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cmbFilterProgramme.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cmbFilterLevel.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cmbFilterStatus.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        filteredData.setPredicate(student -> {
            // 1. Search filter (ID or Name)
            String searchText = txtSearch.getText() != null ? txtSearch.getText().toLowerCase() : "";
            boolean matchesSearch = searchText.isEmpty() ||
                    student.getStudentId().toLowerCase().contains(searchText) ||
                    student.getFullName().toLowerCase().contains(searchText);

            // 2. Programme filter
            String progFilter = cmbFilterProgramme.getValue();
            boolean matchesProg = progFilter == null || progFilter.equals("All") || student.getProgramme().equalsIgnoreCase(progFilter);

            // 3. Level filter
            Integer levelFilter = cmbFilterLevel.getValue();
            boolean matchesLevel = levelFilter == null || student.getLevel() == levelFilter;

            // 4. Status filter
            String statusFilter = cmbFilterStatus.getValue();
            boolean matchesStatus = statusFilter == null || statusFilter.equals("All") || student.getStatus().equalsIgnoreCase(statusFilter);

            return matchesSearch && matchesProg && matchesLevel && matchesStatus;
        });
    }

    @FXML
    private void handleClearFilters() {
        txtSearch.clear();
        cmbFilterProgramme.setValue(null);
        cmbFilterLevel.setValue(null);
        cmbFilterStatus.setValue(null);
    }

    // ... [KEEP YOUR EXISTING handleSave, handleDelete, clearForm, and showAlert METHODS HERE] ...

    @FXML
    private void handleSave() {
        try {
            String id = txtId.getText();
            String name = txtName.getText();
            String prog = txtProgramme.getText();
            int level = cmbLevel.getValue() != null ? cmbLevel.getValue() : 0;
            double gpa = Double.parseDouble(txtGpa.getText().isEmpty() ? "-1" : txtGpa.getText());
            String email = txtEmail.getText();
            String phone = txtPhone.getText();
            String status = cmbStatus.getValue() != null ? cmbStatus.getValue() : "Active";

            Student newStudent = new Student(id, name, prog, level, gpa, email, phone, LocalDate.now(), status);
            studentService.addStudent(newStudent);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Student saved successfully!");
            clearForm();
            loadStudents();
            setupSearchAndFilter(); // Re-apply listeners to the fresh data

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "GPA must be a valid number.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Could not save student: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Student selected = tableStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a student to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Student: " + selected.getFullName());
        confirm.setContentText("Are you sure you want to permanently delete this record?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                studentService.deleteStudent(selected.getStudentId());
                loadStudents();
                setupSearchAndFilter();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Delete Error", "Failed to delete: " + e.getMessage());
            }
        }
    }

    @FXML
    private void clearForm() {
        txtId.clear();
        txtName.clear();
        txtProgramme.clear();
        txtGpa.clear();
        txtEmail.clear();
        txtPhone.clear();
        cmbLevel.setValue(null);
        cmbStatus.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}