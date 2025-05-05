package controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.User;
import services.user.log_historyService;
import utils.session;

import java.io.IOException;

public class DashboardController {

    private final log_historyService logService = new log_historyService();


    public void handleLogout(ActionEvent actionEvent) {
        try {
            // Récupérer l'utilisateur avant de vider la session
            User currentUser = session.getCurrentUser();

            // Ajouter le log de déconnexion
            if (currentUser != null) {
                String details = "User " + currentUser.getName() + " " + currentUser.getPrename() + " logged out";
                logService.addLog(currentUser.getEmail(), "Logout", details);
            }

            // Vider la session
            session.clearSession();

            // Redirection vers la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/sign_in.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showUsersList(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/users_list.fxml"));
            Scene usersListScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(usersListScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAjouterProduit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/AjouterProduit.fxml"));
            Scene ajouterProduitScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(ajouterProduitScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void navigateToAfficherObjective(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherObjective.fxml"));
            Scene afficherObjectiveScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(afficherObjectiveScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}