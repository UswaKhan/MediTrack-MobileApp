package com.example.appmeditrack;

import java.util.List;

public class Sale {
    private String id; // Firestore document ID (optional, for delete/edit)
    private String customerName;
    private String phoneNumber;
    private List<MedicineSold> medicines;
    private String dateTime;
    private double totalAmount;

    public Sale(String id, String customerName, String phoneNumber, List<MedicineSold> medicines, String dateTime, double totalAmount) {
        this.id = id;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.medicines = medicines;
        this.dateTime = dateTime;
        this.totalAmount = totalAmount;
    }

    // Getters
    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public List<MedicineSold> getMedicines() { return medicines; }
    public String getDateTime() { return dateTime; }
    public double getTotalAmount() { return totalAmount; }

    // Static inner class for MedicineSold
    public static class MedicineSold {
        private String name;
        private int quantity;
        private double price;
        private double total;

        public MedicineSold(String name, int quantity, double price, double total) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public double getTotal() { return total; }
    }
}