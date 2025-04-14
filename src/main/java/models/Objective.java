package models;

import java.time.LocalDate;

public class Objective {
    private int id;
    private String name;
    private double targetValue;
    private String unit;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructeurs
    public Objective() {
    }

    public Objective(String name, double targetValue, String unit, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.targetValue = targetValue;
        this.unit = unit;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Objective{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", targetValue=" + targetValue +
                ", unit='" + unit + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
