package controllers.reclamation;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import models.Reclamation;
import services.reclamation.ReclamationService;
import utils.EmailService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListReclamationAdminController {

    @FXML private TableView<Reclamation> reclamationTable;
    @FXML private TableColumn<Reclamation, Integer> idCol;
    @FXML private TableColumn<Reclamation, String> nomCol;
    @FXML private TableColumn<Reclamation, String> prenomCol;
    @FXML private TableColumn<Reclamation, String> sujetCol;
    @FXML private TableColumn<Reclamation, String> statusCol;
    @FXML private TableColumn<Reclamation, String> imageCol;
    @FXML private TableColumn<Reclamation, String> reponseCol;
    @FXML private TableColumn<Reclamation, Void> actionsCol;
    @FXML private BarChart<String, Number> statsChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Button retourBtn;
    @FXML private TextField searchField;

    private final ReclamationService service = new ReclamationService();
    private ObservableList<Reclamation> observableList;

    @FXML
    public void initialize() {
        setupTable();
        loadData();
        setupStatsChart();

        retourBtn.setOnAction(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/user/dashboard.fxml"));
                retourBtn.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        setupSearchFilter();
    }

    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getName()));
        prenomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getPrename()));
        sujetCol.setCellValueFactory(new PropertyValueFactory<>("sujet"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        imageCol.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image img = new Image(getClass().getResourceAsStream("/" + item));
                        imageView.setImage(img);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(new Label("❌"));
                    }
                }
            }
        });
        imageCol.setCellValueFactory(new PropertyValueFactory<>("attachmentFile"));

        reponseCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getReponse() != null ? cellData.getValue().getReponse() : "Aucune réponse"
        ));

        addActionsButtons();
    }

    private void addActionsButtons() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑️");
            private final Button repondreBtn = new Button("✉️");

            {
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand;");
                repondreBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand;");

                deleteBtn.setOnAction(e -> {
                    Reclamation r = getTableView().getItems().get(getIndex());
                    supprimerReclamation(r);
                });

                repondreBtn.setOnAction(e -> {
                    Reclamation r = getTableView().getItems().get(getIndex());
                    repondreReclamation(r);
                });
            }

            private final HBox pane = new HBox(10, repondreBtn, deleteBtn);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadData() {
        List<Reclamation> list = service.getAllReclamations();
        observableList = FXCollections.observableArrayList(list);
        reclamationTable.setItems(observableList);
    }

    private void setupSearchFilter() {
        FilteredList<Reclamation> filteredData = new FilteredList<>(observableList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(reclamation -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lower = newValue.toLowerCase();
                String nom = reclamation.getUser().getName().toLowerCase();
                String prenom = reclamation.getUser().getPrename().toLowerCase();

                return nom.contains(lower) || prenom.contains(lower);
            });
        });

        SortedList<Reclamation> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(reclamationTable.comparatorProperty());
        reclamationTable.setItems(sortedData);
    }

    private void supprimerReclamation(Reclamation r) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression");
        alert.setHeaderText("Supprimer la réclamation ID : " + r.getId());
        alert.setContentText("Êtes-vous sûr ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.supprimer(r);
                loadData();
                setupSearchFilter();
                setupStatsChart();
            }
        });
    }

    private void repondreReclamation(Reclamation r) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Répondre à la réclamation");
        dialog.setHeaderText("Répondre à : " + r.getUser().getName() + " " + r.getUser().getPrename());
        dialog.setContentText("Votre réponse :");
        dialog.showAndWait().ifPresent(reponse -> {
            if (!reponse.trim().isEmpty()) {
                service.repondre(r.getId(), reponse);
                boolean emailSent = EmailService.sendEmail(
                        r.getUser().getEmail(),
                        "Réponse à votre réclamation",
                        "<html><body><h2>Bonjour " + r.getUser().getName() + ",</h2>" +
                                "<p>Nous avons bien reçu votre réclamation :</p>" +
                                "<blockquote>" + r.getSujet() + "</blockquote>" +
                                "<p>Notre réponse :</p>" +
                                "<blockquote>" + reponse + "</blockquote>" +
                                "<p>Merci de votre confiance.<br/>Support Team.</p>" +
                                "</body></html>"
                );
                Alert alert = new Alert(emailSent ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setTitle(emailSent ? "Succès" : "Erreur Email");
                alert.setHeaderText(null);
                alert.setContentText(emailSent ? "Réponse envoyée et email envoyé !" : "Réponse enregistrée mais email non envoyé.");
                alert.showAndWait();
                loadData();
                setupSearchFilter();
                setupStatsChart();
            }
        });
    }

    private void setupStatsChart() {
        Map<String, Integer> categoryCounts = new HashMap<>();
        categoryCounts.put("Livraison", 0);
        categoryCounts.put("Paiement", 0);
        categoryCounts.put("Consultation", 0);
        categoryCounts.put("Technique", 0);
        categoryCounts.put("Autres", 0);

        for (Reclamation r : service.getAllReclamations()) {
            String sujet = r.getSujet().toLowerCase();
            if (sujet.contains("livraison") || sujet.contains("retard") || sujet.contains("colis")) {
                categoryCounts.put("Livraison", categoryCounts.get("Livraison") + 1);
            } else if (sujet.contains("paiement") || sujet.contains("facture") || sujet.contains("carte")) {
                categoryCounts.put("Paiement", categoryCounts.get("Paiement") + 1);
            } else if (sujet.contains("docteur") || sujet.contains("annulation") || sujet.contains("ordonnance")) {
                categoryCounts.put("Consultation", categoryCounts.get("Consultation") + 1);
            } else if (sujet.contains("bug") || sujet.contains("erreur") || sujet.contains("technique")) {
                categoryCounts.put("Technique", categoryCounts.get("Technique") + 1);
            } else {
                categoryCounts.put("Autres", categoryCounts.get("Autres") + 1);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Réclamations");

        String[] barColors = {"#3498db", "#e67e22", "#2ecc71", "#9b59b6", "#e74c3c"};

        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);
        }

        statsChart.getData().clear();
        statsChart.getData().add(series);

        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<String, Number> data = series.getData().get(i);
            String color = barColors[i % barColors.length];
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle(
                            "-fx-bar-fill: " + color + ";" +
                                    "-fx-background-radius: 10 10 0 0;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 5, 0.3, 0, 2);"
                    );
                }
            });
        }

        statsChart.setStyle("-fx-background-color: transparent;");
        xAxis.setTickLabelFill(javafx.scene.paint.Color.web("#2c3e50"));
        xAxis.setTickLabelFont(javafx.scene.text.Font.font("Arial", 14));
        yAxis.setTickLabelFill(javafx.scene.paint.Color.web("#2c3e50"));
        yAxis.setTickLabelFont(javafx.scene.text.Font.font("Arial", 14));
        statsChart.setLegendVisible(false);
    }
}
