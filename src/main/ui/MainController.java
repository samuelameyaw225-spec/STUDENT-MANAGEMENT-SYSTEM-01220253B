package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import service.StudentService;
import repository.StudentRepositoryImpl;

import java.io.IOException;

public class MainController {


    @FXML private Button btnStudents;
    @FXML private Button btnReports;
    @FXML private Button btnImportExport;
    @FXML private Button btnSettings;
    @FXML private Button btnDashboard;




    @FXML
    public void initialize() {


        // --- BUTTON CLICKS (NAVIGATION) ---

        // 1. Link the Students button!
        btnStudents.setOnAction(e -> loadScreen("/Students.fxml"));

        // Placeholder for future screens
        btnReports.setOnAction(e -> System.out.println("Reports coming next..."));
        btnReports.setOnAction(e -> loadScreen("/Reports.fxml"));



        btnImportExport.setOnAction(e -> loadScreen("/ImportExport.fxml"));

        btnDashboard.setOnAction(e -> loadScreen("/Dashboard.fxml"));
        btnSettings.setOnAction(e -> loadScreen("/Settings.fxml"));





    }



    // --- NAVIGATION HELPER METHOD ---
    private void loadScreen(String fxmlPath) {
        try {
            // Load the new FXML screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newScreen = loader.load();

            // Grab the main BorderPane layout from the current scene
            BorderPane mainLayout = (BorderPane) btnStudents.getScene().getRoot();

            // Swap the center content!
            mainLayout.setCenter(newScreen);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not load screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}