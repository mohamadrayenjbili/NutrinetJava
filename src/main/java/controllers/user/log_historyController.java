package controllers.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.LogEntry;
import models.User;
import services.user.log_historyService;
import utils.session;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class log_historyController implements Initializable {

    @FXML
    private TableView<LogEntry> logTable;

    @FXML
    private TableColumn<LogEntry, String> dateColumn;

    @FXML
    private TableColumn<LogEntry, String> userColumn;

    @FXML
    private TableColumn<LogEntry, String> actionColumn;

    @FXML
    private TableColumn<LogEntry, String> detailsColumn;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private ComboBox<String> actionFilter;

    private final session session = new session(); // Correction ici


    private ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();
    private final log_historyService logService = new log_historyService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration des colonnes (code existant)
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        userColumn.setCellValueFactory(cellData -> cellData.getValue().userProperty());
        actionColumn.setCellValueFactory(cellData -> cellData.getValue().actionProperty());
        detailsColumn.setCellValueFactory(cellData -> cellData.getValue().detailsProperty());

        // Configuration du filtre (code existant)
        actionFilter.getItems().addAll("All", "Login", "Logout","Google Login");
        actionFilter.setValue("All");

        loadLogs();
    }

    private void loadLogs() {
        try {
            logEntries = logService.getAllLogs();
            logTable.setItems(logEntries);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des logs: " + e.getMessage());
        }
    }

    @FXML
    private void applyFilters() {
        try {
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();
            String action = actionFilter.getValue();

            logEntries = logService.getFilteredLogs(start, end, action);
            logTable.setItems(logEntries);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du filtrage des logs: " + e.getMessage());
        }
    }

    @FXML
    private void resetFilters() {
        startDate.setValue(null);
        endDate.setValue(null);
        actionFilter.setValue("All");
        loadLogs();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/users_list.fxml"));
            Scene usersListScene = new Scene(loader.load());

            Stage stage = (Stage) logTable.getScene().getWindow();
            stage.setScene(usersListScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error returning to users list: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }


    @FXML
    private void handleUsersList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/users_list.fxml"));
            Scene usersListScene = new Scene(loader.load());

            Stage stage = (Stage) logTable.getScene().getWindow();
            stage.setTitle("Users List");
            stage.setScene(usersListScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error navigating to users list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) logTable.getScene().getWindow();
            stage.setTitle("Sign In");
            stage.setScene(loginScene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }


    
}