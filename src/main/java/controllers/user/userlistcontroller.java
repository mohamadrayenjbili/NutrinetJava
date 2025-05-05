package controllers.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.User;
import services.user.log_historyService;
import services.user.userlist;
import utils.session;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class userlistcontroller implements Initializable {

    public Label users_list;
    @FXML
    private ListView<User> usersListView;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button updateUserButton;

    private userlist userService;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> roleFilter;
    @FXML
    private Button banUserButton;

    private ObservableList<User> allUsers;
    private ObservableList<User> filteredUsers;

    //stats________________________
    @FXML
    private PieChart userStatsChart;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label bannedUsersLabel;
    @FXML
    private GridPane statsGrid;


    //stats function_____________________________
    private void updateStats() {
        // Calculer les stats par rôle
        Map<String, Long> roleStats = allUsers.stream()
                .collect(Collectors.groupingBy(
                        User::getRole,
                        Collectors.counting()
                ));

        // Mettre à jour le graphique avec les pourcentages
        userStatsChart.getData().clear();
        int totalUsers = allUsers.size();
        roleStats.forEach((role, count) -> {
            double percentage = (count * 100.0) / totalUsers;
            PieChart.Data slice = new PieChart.Data(role + " (" + String.format("%.1f", percentage) + "%)", count);
            userStatsChart.getData().add(slice);
        });

        // Calculer l'âge moyen
        double averageAge = allUsers.stream()
                .mapToInt(User::getAge)
                .average()
                .orElse(0);

        // Calculer le nombre d'utilisateurs bannis
        long bannedUsers = allUsers.stream()
                .filter(u -> u.getIsBanned().equals("1"))
                .count();

        // Mettre à jour les labels
        totalUsersLabel.setText(String.valueOf(totalUsers));
        bannedUsersLabel.setText(String.valueOf(bannedUsers));

        // Ajouter un label pour l'âge moyen
        Label averageAgeLabel = new Label("Âge moyen : " + String.format("%.1f", averageAge));
        statsGrid.add(averageAgeLabel, 0, 2); // Ajouter à la grille des stats
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userService = new userlist();

        // Initialize filtered list
        allUsers = FXCollections.observableArrayList();
        filteredUsers = FXCollections.observableArrayList();

        // Setup role filter
        roleFilter.getItems().addAll("client", "admin", "doctor", "Tous");
        roleFilter.setValue("Tous");

        // Setup search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers();
        });

        // Setup role filter listener
        roleFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers();
        });

        // Create column headers and attach to ListView
        // We'll fix the createColumnHeaders method to work with ScrollPane
        createColumnHeaders();
        usersListView.setCellFactory(listView -> new UserListCell());
        loadUsers();

        deleteUserButton.setDisable(true);
        updateUserButton.setDisable(true);
        banUserButton.setDisable(true);

        usersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = (newSelection != null);
            deleteUserButton.setDisable(!hasSelection);
            updateUserButton.setDisable(!hasSelection);
            banUserButton.setDisable(!hasSelection);

            if (hasSelection) {
                banUserButton.setText(newSelection.getIsBanned().equals("1") ? "UNBAN" : "BAN");
                banUserButton.setStyle(newSelection.getIsBanned().equals("1") ?
                        "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10 20;" :
                        "-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10 20;");
            }
        });
    }

    //ban user------------------------------------------
    @FXML
    private void handleBanUser(ActionEvent event) {
        User selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur.");
            return;
        }

        boolean currentlyBanned = selectedUser.getIsBanned().equals("1");
        String newStatus = currentlyBanned ? "0" : "1";
        String action = currentlyBanned ? "débannir" : "bannir";

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer " + action);
        confirmAlert.setHeaderText("Voulez-vous " + action + " cet utilisateur ?");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir " + action + " " +
                selectedUser.getName() + " " + selectedUser.getPrename() + " ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    selectedUser.setIsBanned(newStatus);
                    userService.updateUser(selectedUser);

                    // Mettre à jour l'interface
                    banUserButton.setText(newStatus.equals("1") ? "UNBAN" : "BAN");
                    banUserButton.setStyle(newStatus.equals("1") ?
                            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;" :
                            "-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;");

                    // Recharger la liste
                    loadUsers();

                    showAlert(Alert.AlertType.INFORMATION,
                            "L'utilisateur a été " + (currentlyBanned ? "débanni" : "banni") + " avec succès !");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR,
                            "Erreur lors du " + action + " de l'utilisateur : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }



    private void createColumnHeaders() {
        try {
            // Create header HBox
            HBox headerBox = new HBox();
            headerBox.setStyle("-fx-padding: 5px; -fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");

            // ID column - fixed width
            Label idLabel = new Label("ID");
            idLabel.setPrefWidth(50);
            idLabel.setStyle("-fx-font-weight: bold;");

            // Name column
            Label nameLabel = new Label("Name");
            nameLabel.setPrefWidth(120);
            nameLabel.setStyle("-fx-font-weight: bold;");

            // Email column
            Label emailLabel = new Label("Email");
            emailLabel.setMaxWidth(Double.MAX_VALUE);
            emailLabel.setStyle("-fx-font-weight: bold;");
            HBox.setHgrow(emailLabel, Priority.ALWAYS);

            // Role column - fixed width
            Label roleLabel = new Label("Role");
            roleLabel.setPrefWidth(100);
            roleLabel.setStyle("-fx-font-weight: bold;");

            // Status column
            Label statusLabel = new Label("Status");
            statusLabel.setPrefWidth(80);
            statusLabel.setStyle("-fx-font-weight: bold;");

            // Add columns to header
            headerBox.getChildren().addAll(idLabel, nameLabel, emailLabel, roleLabel, statusLabel);

            // With ScrollPane, we need to find the correct parent
            // We need to get the direct parent of the ListView which should be a VBox inside the ScrollPane's content
            Parent listViewParent = usersListView.getParent();

            // If we're using the newly structured FXML, we need to find the VBox that contains the ListView
            if (listViewParent instanceof VBox) {
                // We're directly inside a VBox, we can add at index 0
                ((VBox) listViewParent).getChildren().add(0, headerBox);
            } else {
                // Create a new container for both header and ListView
                VBox container = new VBox();

                // Get current parent of ListView
                Pane currentParent = (Pane) usersListView.getParent();

                // Get the index of ListView in its parent
                int index = currentParent.getChildren().indexOf(usersListView);

                // Remove ListView from its current parent
                currentParent.getChildren().remove(usersListView);

                // Add header and ListView to container
                container.getChildren().addAll(headerBox, usersListView);

                // Add container back to parent at the same index
                currentParent.getChildren().add(index, container);
            }
        } catch (Exception e) {
            // Print detailed error for debugging
            System.err.println("Error creating column headers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Récupérer l'utilisateur avant de vider la session
            User currentUser = session.getCurrentUser();

            // Créer une instance de log_historyService
            log_historyService logService = new log_historyService();

            // Ajouter le log de déconnexion
            if (currentUser != null) {
                String details = "User " + currentUser.getName() + " " + currentUser.getPrename() + " logged out";
                logService.addLog(currentUser.getEmail(), "Logout", details);
            }

            // Vider la session
            session.clearSession();

            // Redirection vers la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usersListView.getScene().getWindow();
            stage.setTitle("Sign In");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterUsers() {
        filteredUsers.clear();
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedRole = roleFilter.getValue();

        for (User user : allUsers) {
            boolean matchesSearch = searchText.isEmpty() ||
                    user.getName().toLowerCase().contains(searchText) ||
                    user.getPrename().toLowerCase().contains(searchText);

            boolean matchesRole = selectedRole.equals("Tous") ||
                    selectedRole.equalsIgnoreCase(user.getRole());

            if (matchesSearch && matchesRole) {
                filteredUsers.add(user);
            }
        }

        usersListView.setItems(filteredUsers);
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        roleFilter.setValue("Tous");
        usersListView.setItems(allUsers);
    }


    // Custom ListCell that displays User data in columns
    private class UserListCell extends ListCell<User> {
        private final HBox content;
        private final Label idLabel;
        private final Label nameLabel;
        private final Label emailLabel;
        private final Label roleLabel;
        private final Label banStatusLabel;  // Nouveau label

        public UserListCell() {
            content = new HBox();
            content.setStyle("-fx-padding: 5px;");

            idLabel = new Label();
            idLabel.setPrefWidth(50);

            nameLabel = new Label();
            nameLabel.setPrefWidth(120);

            emailLabel = new Label();
            emailLabel.setPrefWidth(200);

            roleLabel = new Label();
            roleLabel.setPrefWidth(100);

            banStatusLabel = new Label();  // Nouveau label pour le statut
            banStatusLabel.setPrefWidth(80);

            content.getChildren().addAll(idLabel, nameLabel, emailLabel, roleLabel, banStatusLabel);
        }

        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setGraphic(null);
            } else {
                idLabel.setText(String.valueOf(user.getId()));
                nameLabel.setText(user.getName() + " " + user.getPrename());
                emailLabel.setText(user.getEmail());
                roleLabel.setText(user.getRole());

                // Afficher le statut de bannissement
                boolean isBanned = user.getIsBanned().equals("1");
                banStatusLabel.setText(isBanned ? "BANNED" : "ACTIVE");
                banStatusLabel.setStyle(isBanned ?
                        "-fx-text-fill: #f44336; -fx-font-weight: bold;" :
                        "-fx-text-fill: #4CAF50; -fx-font-weight: bold;");

                if (getIndex() % 2 == 0) {
                    content.setStyle("-fx-padding: 5px; -fx-background-color: #f9f9f9;");
                } else {
                    content.setStyle("-fx-padding: 5px; -fx-background-color: white;");
                }

                setGraphic(content);
            }
        }
    }

    // Method to load users into the ListView
    void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            allUsers.setAll(users);
            filteredUsers.setAll(users);
            usersListView.setItems(filteredUsers);
            updateStats();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des utilisateurs : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteUser(ActionEvent event) {
        User selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a user to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete User");
        confirmAlert.setContentText("Are you sure you want to delete " +
                selectedUser.getName() + " " +
                selectedUser.getPrename() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Call service to delete user
                    userService.deleteUser(selectedUser.getId());

                    // Refresh the list
                    loadUsers();

                    showAlert(Alert.AlertType.INFORMATION, "User deleted successfully!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error deleting user: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // Method to handle update user button click
    @FXML
    private void handleUpdateUser(ActionEvent event) {
        User selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a user to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/update_user.fxml"));
            AnchorPane updatePane = loader.load();

            // Get the controller and pass the selected user
            UpdateUserController controller = loader.getController();
            controller.setUser(selectedUser);

            // Create a new scene for the update form
            Scene scene = new Scene(updatePane);

            // Get the current stage and set the scene to the update form
            Stage stage = (Stage) usersListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Update User");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error navigating to update user form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to navigate to the "Add User" form
    @FXML
    private void navigateToAddUserForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_up.fxml"));
            AnchorPane signUpPane = loader.load();
            Scene scene = new Scene(signUpPane);
            Stage stage = (Stage) usersListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sign Up - Add User");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error navigating to add user form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to display alerts
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/dashboard.fxml"));
            Scene dashboardScene = new Scene(loader.load());

            Stage stage = (Stage) usersListView.getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error returning to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToLogHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/log_history.fxml"));
            Scene logHistoryScene = new Scene(loader.load());

            Stage stage = (Stage) usersListView.getScene().getWindow();
            stage.setScene(logHistoryScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error navigating to log history: " + e.getMessage());
            e.printStackTrace();
        }
    }

}