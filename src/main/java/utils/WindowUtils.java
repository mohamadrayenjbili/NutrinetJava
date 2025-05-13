package utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowUtils {
    // Dimensions standard pour toutes les fenêtres
    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 800;

    /**
     * Configure une fenêtre avec les paramètres standards
     * @param stage La fenêtre à configurer
     * @param title Le titre de la fenêtre
     */
    public static void configureStage(Stage stage, String title) {
        stage.setTitle(title);
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.centerOnScreen();
    }

    /**
     * Charge une scène FXML et configure la fenêtre
     * @param fxmlPath Le chemin vers le fichier FXML
     * @param title Le titre de la fenêtre
     * @return La scène chargée
     * @throws IOException Si le chargement du FXML échoue
     */
    public static Scene loadScene(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(WindowUtils.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        // Appliquer les styles CSS si nécessaire
        scene.getStylesheets().add(WindowUtils.class.getResource("/Programme/modern_list.css").toExternalForm());
        
        return scene;
    }

    /**
     * Ouvre une nouvelle fenêtre avec les paramètres standards
     * @param fxmlPath Le chemin vers le fichier FXML
     * @param title Le titre de la fenêtre
     * @return La fenêtre créée
     * @throws IOException Si le chargement du FXML échoue
     */
    public static Stage openNewWindow(String fxmlPath, String title) throws IOException {
        Stage stage = new Stage();
        Scene scene = loadScene(fxmlPath, title);
        stage.setScene(scene);
        configureStage(stage, title);
        stage.show();
        return stage;
    }

    /**
     * Change la scène d'une fenêtre existante
     * @param stage La fenêtre existante
     * @param fxmlPath Le chemin vers le fichier FXML
     * @param title Le titre de la fenêtre
     * @throws IOException Si le chargement du FXML échoue
     */
    public static void changeScene(Stage stage, String fxmlPath, String title) throws IOException {
        Scene scene = loadScene(fxmlPath, title);
        stage.setScene(scene);
        configureStage(stage, title);
    }
} 