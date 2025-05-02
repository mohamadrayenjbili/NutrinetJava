package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Home extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {



            Parent root = FXMLLoader.load(getClass().getResource("/Produit/AfficherProduitFront.fxml"));



            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Programme/modern_list.css").toExternalForm());
            stage.setTitle("Ajouter Consultation");

            // 🔥 Ajout de l'icône
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Nutrinet.png")));

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.out.println("Erreur de chargement FXML : " + e.getMessage());
        }
    }
}
