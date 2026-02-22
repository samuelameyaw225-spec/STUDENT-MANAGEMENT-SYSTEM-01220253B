package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationUtil {

    public static void loadScreen(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    NavigationUtil.class.getResource("/" + fxml)
            );

            Parent root = loader.load();

            Stage stage = (Stage) javafx.stage.Stage.getWindows()
                    .filtered(window -> window.isShowing())
                    .get(0);

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}