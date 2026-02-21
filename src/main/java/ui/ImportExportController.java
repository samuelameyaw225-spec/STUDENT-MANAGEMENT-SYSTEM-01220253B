package ui;

import domain.Student;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import repository.StudentRepositoryImpl;
import service.FileService;
import service.StudentService;

import java.io.File;
import java.util.List;

public class ImportExportController {

    @FXML private TextField txtFilePath;
    @FXML private ListView<String> listImportReport;

    private FileService fileService;
    private StudentService studentService;
    private File selectedFile;

    @FXML
    public void initialize() {
        fileService = new FileService();
        studentService = new StudentService(new StudentRepositoryImpl());
    }

    @FXML
    private void handleExportAll() {
        try {
            List<Student> allStudents = studentService.getAllStudents();
            if (allStudents.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Export Error", "There are no students in the database to export.");
                return;
            }

            String result = fileService.exportToCSV(allStudents, "Full_Student_List");
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", result);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Export Error", "Failed to export data: " + e.getMessage());
        }
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File to Import");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Get the current window to tie the dialog to it
        selectedFile = fileChooser.showOpenDialog(txtFilePath.getScene().getWindow());

        if (selectedFile != null) {
            txtFilePath.setText(selectedFile.getAbsolutePath());
            listImportReport.getItems().clear(); // Clear old logs
        }
    }

    @FXML
    private void handleImport() {
        if (selectedFile == null || !selectedFile.exists()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a valid CSV file first.");
            return;
        }

        // Add a loading message
        listImportReport.setItems(FXCollections.observableArrayList("Processing import... Please wait."));

        // Run the import via the FileService
        List<String> report = fileService.importFromCSV(selectedFile, studentService);

        // Display the results in the ListView
        listImportReport.setItems(FXCollections.observableArrayList(report));

        // Clear the file selection so they don't accidentally click import twice
        selectedFile = null;
        txtFilePath.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}