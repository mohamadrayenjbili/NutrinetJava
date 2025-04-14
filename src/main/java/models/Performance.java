package models;

import java.time.LocalDateTime;

public class Performance {
    private int id;
    private int objectiveId;
    private LocalDateTime date;
    private String notes;
    private String metricName;
    private double value;
    private String unit;

    // Constructeur par défaut
    public Performance() {
    }

    // Constructeur avec paramètres
    public Performance(int objectiveId, LocalDateTime date, String notes, String metricName, double value, String unit) {
        this.objectiveId = objectiveId;
        this.date = date;
        this.notes = notes;
        this.metricName = metricName;
        this.value = value;
        this.unit = unit;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(int objectiveId) {
        this.objectiveId = objectiveId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Performance{" +
                "id=" + id +
                ", objectiveId=" + objectiveId +
                ", date=" + date +
                ", metricName='" + metricName + '\'' +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                '}';
    }
}