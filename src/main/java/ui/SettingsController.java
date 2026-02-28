package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.prefs.Preferences;

public class SettingsController {

    @FXML private TextField txtGpaThreshold;
    @FXML private TextField txtNewProgramme;
    @FXML private ListView<String> listProgrammes;

    private final ObservableList<String> programmeList = FXCollections.observableArrayList();

    // Preferences API (persistent storage)
    private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);

    private static final String THRESHOLD_KEY = "gpaThreshold";
    private static final String PROGRAMMES_KEY = "programmeList";

    @FXML
    public void initialize() {
        loadThreshold();
        loadProgrammes();
        listProgrammes.setItems(programmeList);
    }

    /* ===================== GPA THRESHOLD ===================== */

    @FXML
    private void handleSaveThreshold() {
        try {
            double threshold = Double.parseDouble(txtGpaThreshold.getText());

            if (threshold < 0.0 || threshold > 4.0) {
                showError("Threshold must be between 0.0 and 4.0.");
                return;
            }

            prefs.putDouble(THRESHOLD_KEY, threshold);
            showInfo("Threshold updated successfully.");

        } catch (NumberFormatException e) {
            showError("Please enter a valid numeric GPA value.");
        }
    }

    private void loadThreshold() {
        double savedThreshold = prefs.getDouble(THRESHOLD_KEY, 2.0); // default = 2.0
        txtGpaThreshold.setText(String.valueOf(savedThreshold));
    }

    /* ===================== PROGRAMME MANAGEMENT ===================== */

    @FXML
    private void handleAddProgramme() {

        String newProgramme = txtNewProgramme.getText().trim();

        if (newProgramme.isEmpty()) {
            showError("Programme name cannot be empty.");
            return;
        }

        if (programmeList.contains(newProgramme)) {
            showError("Programme already exists.");
            return;
        }

        programmeList.add(newProgramme);
        saveProgrammes();

        txtNewProgramme.clear();
        showInfo("Programme added successfully.");
    }

    private void loadProgrammes() {

        String savedProgrammes = prefs.get(PROGRAMMES_KEY, "");

        if (!savedProgrammes.isEmpty()) {
            String[] programmes = savedProgrammes.split(",");
            programmeList.addAll(programmes);
        }
    }

    private void saveProgrammes() {
        String joined = String.join(",", programmeList);
        prefs.put(PROGRAMMES_KEY, joined);
    }

    /* ===================== ALERT HELPERS ===================== */

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}