package models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CodePromo {

    private final StringProperty code;
    private final DoubleProperty pourcentage;
    private final BooleanProperty actif;
    private int id;  // L'ID peut rester comme une propriété simple sans être une propriété JavaFX

    public CodePromo() {
        this.code = new SimpleStringProperty();
        this.pourcentage = new SimpleDoubleProperty();
        this.actif = new SimpleBooleanProperty();
    }

    public CodePromo(int id, String code, double pourcentage, boolean actif) {
        this();  // Initialisation des propriétés JavaFX via le constructeur par défaut
        this.id = id;
        this.code.set(code);
        this.pourcentage.set(pourcentage);
        this.actif.set(actif);
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public String getCode() {
        return code.get();
    }

    public double getPourcentage() {
        return pourcentage.get();
    }

    public boolean isActif() {
        return actif.get();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public void setPourcentage(double pourcentage) {
        this.pourcentage.set(pourcentage);
    }

    public void setActif(boolean actif) {
        this.actif.set(actif);
    }

    // Propriétés JavaFX pour le binding avec la TableView
    public StringProperty codeProperty() {
        return code;
    }

    public DoubleProperty pourcentageProperty() {
        return pourcentage;
    }

    public BooleanProperty actifProperty() {
        return actif;
    }
}
