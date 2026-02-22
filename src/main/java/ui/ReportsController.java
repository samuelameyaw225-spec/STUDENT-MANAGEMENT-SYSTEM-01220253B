package ui;

import domain.Student;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.StudentRepositoryImpl;
import service.FileService;
import service.ReportService;
import service.StudentService;

import java.util.List;
import java.util.Map;

public class ReportsController {

    // Top Performers Controls
    @FXML private ComboBox<String> cmbTopProgramme;
    @FXML private ComboBox<Integer> cmbTopLevel;
    @FXML private TableView<Student> tableTop;
    @FXML private TableColumn<Student, String> colTopId, colTopName, colTopProg;
    @FXML private TableColumn<Student, Integer> colTopLevel;
    @FXML private TableColumn<Student, Double> colTopGpa;

    // At-Risk Controls
    @FXML private TextField txtRiskThreshold;
    @FXML private TableView<Student> tableRisk;
    @FXML private TableColumn<Student, String> colRiskId, colRiskName, colRiskProg;
    @FXML private TableColumn<Student, Integer> colRiskLevel;
    @FXML private TableColumn<Student, Double> colRiskGpa;

    // Summary Controls
    @FXML private ListView<String> listGpaDist;
    @FXML private ListView<String> listProgSummary;

    private ReportService reportService;
    private FileService fileService;

    @FXML
    public void initialize() {
        StudentService studentService = new StudentService(new StudentRepositoryImpl());
        reportService = new ReportService(studentService);
        fileService = new FileService();

        // Setup Dropdowns
        cmbTopProgramme.setItems(FXCollections.observableArrayList("All", "Computer Science", "Engineering", "Business", "Nursing"));
        cmbTopLevel.setItems(FXCollections.observableArrayList(null, 100, 200, 300, 400, 500, 600, 700));

        // Setup Table Columns
        setupColumns(colTopId, colTopName, colTopProg, colTopLevel, colTopGpa);
        setupColumns(colRiskId, colRiskName, colRiskProg, colRiskLevel, colRiskGpa);

        // Load Initial Data
        loadTopPerformers();
        loadAtRiskStudents();
        loadSummaries();
    }

    private void setupColumns(TableColumn<Student, String> id, TableColumn<Student, String> name,
                              TableColumn<Student, String> prog, TableColumn<Student, Integer> level,
                              TableColumn<Student, Double> gpa) {
        id.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        name.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        prog.setCellValueFactory(new PropertyValueFactory<>("programme"));
        level.setCellValueFactory(new PropertyValueFactory<>("level"));
        gpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
    }

    @FXML
    private void loadTopPerformers() {
        try {
            String prog = cmbTopProgramme.getValue();
            Integer level = cmbTopLevel.getValue();
            List<Student> topStudents = reportService.getTopPerformers(prog, level);
            tableTop.setItems(FXCollections.observableArrayList(topStudents));
        } catch (Exception e) {
            showAlert("Error", "Could not load top performers: " + e.getMessage());
        }
    }

    @FXML
    private void loadAtRiskStudents() {
        try {
            double threshold = Double.parseDouble(txtRiskThreshold.getText());
            List<Student> riskStudents = reportService.getAtRiskStudents(threshold);
            tableRisk.setItems(FXCollections.observableArrayList(riskStudents));
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Threshold must be a valid number.");
        } catch (Exception e) {
            showAlert("Error", "Could not load at-risk students: " + e.getMessage());
        }
    }

    private void loadSummaries() {
        try {
            // Load GPA Distribution
            listGpaDist.getItems().clear();
            Map<String, Long> dist = reportService.getGpaDistribution();
            dist.forEach((band, count) -> listGpaDist.getItems().add(band + ": " + count + " students"));

            // Load Programme Summary
            listProgSummary.setItems(FXCollections.observableArrayList(reportService.getProgrammeSummary()));
        } catch (Exception e) {
            showAlert("Error", "Could not load summaries: " + e.getMessage());
        }
    }

    @FXML
    private void exportTopPerformers() {
        String result = fileService.exportToCSV(tableTop.getItems(), "Top_Performers_Report");
        showAlert("Export Status", result);
    }

    @FXML
    private void exportAtRiskStudents() {
        String result = fileService.exportToCSV(tableRisk.getItems(), "At_Risk_Report");
        showAlert("Export Status", result);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}