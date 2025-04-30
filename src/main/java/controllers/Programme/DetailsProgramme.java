package controllers.Programme;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import models.Programme;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class DetailsProgramme implements Initializable {

    @FXML
    private VBox rootPane;

    @FXML
    private Label lblTitre;

    @FXML
    private Label lblType;

    @FXML
    private Label lblAuteur;

    @FXML
    private TextArea lblDescription;

    @FXML
    private Label lblVideoUrl;

    @FXML
    private ImageView imgProgramme;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootPane.getStylesheets().add(getClass().getResource("/details_style.css").toExternalForm());
    }

    public void setProgrammeDetails(Programme p) {
        lblTitre.setText(p.getTitre());
        lblType.setText(p.getType());
        lblAuteur.setText(p.getAuteur());
        lblDescription.setText(p.getDescription());
        lblVideoUrl.setText(p.getVideoUrl());

        if (p.getImage() != null && !p.getImage().isEmpty()) {
            Image image = new Image("file:src/main/resources/images/" + p.getImage());
            imgProgramme.setImage(image);
        }
    }
}


