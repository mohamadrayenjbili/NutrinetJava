package models;

import java.sql.Date;

public class User {

    private int id;
    private String name;
    private String prename;
    private String email;
    private String password;
    private int age;
    private String phoneNumber;
    private String address;
    private String role;
    private String isBanned = "0"; // Default value
    private Date date; // New date field

    public User() {
    }

    // Constructor without date
    public User(int id, String nom, String prenom, String email, String password, int age, String phone, String address, String role) {
        this.id = id;
        this.name = nom;
        this.prename = prenom;
        this.email = email;
        this.password = password;
        this.age = age;
        this.phoneNumber = phone;
        this.address = address;
        this.role = role;
    }

    // Constructor with isBanned
    public User(int id, String name, String prename, String email, String password, int age,
                String phoneNumber, String address, String role, String isBanned) {
        this.id = id;
        this.name = name;
        this.prename = prename;
        this.email = email;
        this.password = password;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.isBanned = isBanned;
    }

    // ✅ New constructor with date
    public User(int id, String name, String prename, String email, String password, int age,
                String phoneNumber, String address, String role, String isBanned, Date date) {
        this.id = id;
        this.name = name;
        this.prename = prename;
        this.email = email;
        this.password = password;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.isBanned = isBanned;
        this.date = date;
    }

    // Getters and Setters

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

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(String isBanned) {
        this.isBanned = isBanned;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
