package com.example.appmeditrack;

public class User {
    private String id; // Firestore document ID
    private String username;
    private String role;
    private String email;
    private String phone;
    private int profilePicResId; // Use drawable resource ID

    public User(String id, String username, String role, String email, String phone, int profilePicResId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.profilePicResId = profilePicResId;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getProfilePicResId() { return profilePicResId; }
}