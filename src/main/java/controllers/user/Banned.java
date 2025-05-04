package controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class Banned {
    @FXML
    private Button backButton;

    @FXML
    private void handleBackToSignIn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/sign_in.fxml"));
            Scene signInScene = new Scene(loader.load());

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(signInScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}