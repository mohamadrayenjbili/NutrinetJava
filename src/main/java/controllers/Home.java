package controllers;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.ObjectiveService;
public class Home extends Application {

    @Override
    public void start(Stage primaryStage) {
        boolean isConnected = ObjectiveService.isConnectionSuccessful();

        if (isConnected) {
            // Proceed with the rest of the logic or show a success message
            System.out.println("Database connection is successful.");
        } else {
            // Handle the error or show a failure message
            System.err.println("Failed to connect to the database.");
        }

        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitFront.fxml"));
          //  FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/sign_up.fxml"));

            AnchorPane root = loader.load();

            // Créer la scène et définir les paramètres
            Scene scene = new Scene(root);
            primaryStage.setTitle("Liste des objectifs");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}