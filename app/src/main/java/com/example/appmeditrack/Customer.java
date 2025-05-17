package com.example.appmeditrack;

public class Customer {
    private String id; // Firestore document ID (optional)
    private String name;
    private String phone;
    private String email;
    private String registrationDate;

    public Customer(String id, String name, String phone, String email, String registrationDate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getRegistrationDate() { return registrationDate; }
}