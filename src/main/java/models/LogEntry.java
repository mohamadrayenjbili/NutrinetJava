package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogEntry {
    private final StringProperty date;
    private final StringProperty user;
    private final StringProperty action;
    private final StringProperty details;

    public LogEntry(String date, String user, String action, String details) {
        this.date = new SimpleStringProperty(date);
        this.user = new SimpleStringProperty(user);
        this.action = new SimpleStringProperty(action);
        this.details = new SimpleStringProperty(details);
    }

    // Getters for properties
    public StringProperty dateProperty() { return date; }
    public StringProperty userProperty() { return user; }
    public StringProperty actionProperty() { return action; }
    public StringProperty detailsProperty() { return details; }

    // Getters for values
    public String getDate() { return date.get(); }
    public String getUser() { return user.get(); }
    public String getAction() { return action.get(); }
    public String getDetails() { return details.get(); }
}