package controllers.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import models.User;
import services.user.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class userlistcontroller implements Initializable {

    @FXML
    private ListView<User> usersListView;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button updateUserButton;

    private userlist userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = new userlist();

        createColumnHeaders();

        usersListView.setCellFactory(listView -> new UserListCell());

        loadUsers();

        deleteUserButton.setDisable(true);
        updateUserButton.setDisable(true);

        usersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // Enable or disable buttons based on selection
            boolean hasSelection = (newSelection != null);
            deleteUserButton.setDisable(!hasSelection);
            updateUserButton.setDisable(!hasSelection);
        });
    }

    // Create a header row above the ListView to show column names
    private void createColumnHeaders() {
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

        // Add columns to header
        headerBox.getChildren().addAll(idLabel, nameLabel, emailLabel, roleLabel);

        // Add header to parent container (VBox that contains ListView)
        VBox parent = (VBox) usersListView.getParent();
        parent.getChildren().add(0, headerBox);
    }

    // Custom ListCell that displays User data in columns
    private class UserListCell extends javafx.scene.control.ListCell<User> {
        private final HBox content;
        private final Label idLabel;
        private final Label nameLabel;
        private final Label emailLabel;
        private final Label roleLabel;

        public UserListCell() {
            content = new HBox();
            content.setStyle("-fx-padding: 5px;");

            // ID column - fixed width
            idLabel = new Label();
            idLabel.setPrefWidth(50);

            // Name column
            nameLabel = new Label();
            nameLabel.setPrefWidth(120);

            // Email column - variable width
            emailLabel = new Label();
            emailLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(emailLabel, Priority.ALWAYS);

            // Role column - fixed width
            roleLabel = new Label();
            roleLabel.setPrefWidth(100);

            content.getChildren().addAll(idLabel, nameLabel, emailLabel, roleLabel);
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

                // Add alternating row colors for better readability
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
            ObservableList<User> usersList = FXCollections.observableArrayList(users);
            usersListView.setItems(usersList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to handle delete user button click
    @FXML
    private void handleDeleteUser(ActionEvent event) {
        User selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a user to delete.");
            return;
        }

        // Confirm deletion
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
}