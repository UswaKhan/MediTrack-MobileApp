package com.example.appmeditrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SalesHistory extends AppCompatActivity {

    private RecyclerView recyclerViewSales;
    private SalesAdapter salesAdapter;
    private ArrayList<Sale> salesList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        db = FirebaseFirestore.getInstance();

        Button dashboardBtn = findViewById(R.id.btnDashboard);
        dashboardBtn.setOnClickListener(v -> {
            // Check if user is admin and go to the correct dashboard
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                boolean isAdmin = documentSnapshot.getBoolean("isAdmin") != null && documentSnapshot.getBoolean("isAdmin");
                Intent intent;
                if (isAdmin) {
                    intent = new Intent(SalesHistory.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(SalesHistory.this, CashierDashboardActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                // Fallback: go to cashier dashboard
                Intent intent = new Intent(SalesHistory.this, CashierDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SalesHistory.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerViewSales = findViewById(R.id.recyclerViewSales);
        recyclerViewSales.setLayoutManager(new LinearLayoutManager(this));

        salesList = new ArrayList<>();

        // Get current user UID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        // Check isAdmin from Firestore
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            boolean isAdmin = documentSnapshot.getBoolean("isAdmin") != null && documentSnapshot.getBoolean("isAdmin");

            salesAdapter = new SalesAdapter(this, salesList, isAdmin);
            recyclerViewSales.setAdapter(salesAdapter);

            // Set delete listener only if admin
            if (isAdmin) {
                salesAdapter.setOnDeleteClickListener((sale, position) -> {
                    db.collection("sales").document(sale.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(SalesHistory.this, "Sale deleted", Toast.LENGTH_SHORT).show();
                                fetchSales(); // Re-fetch the list from Firestore
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(SalesHistory.this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
            }

            fetchSales();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to check admin status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Fallback: show sales without delete
            salesAdapter = new SalesAdapter(this, salesList, false);
            recyclerViewSales.setAdapter(salesAdapter);
            fetchSales();
        });
    }

    private void fetchSales() {
        db.collection("sales")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    salesList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String customerName = doc.getString("customerName");
                        String phoneNumber = doc.getString("phoneNumber");
                        String dateTime = doc.getString("dateTime");
                        double totalAmount = doc.getDouble("totalAmount") != null ? doc.getDouble("totalAmount") : 0.0;

                        // Parse medicines list
                        List<Map<String, Object>> medicinesData = (List<Map<String, Object>>) doc.get("medicines");
                        List<Sale.MedicineSold> medicines = new ArrayList<>();
                        if (medicinesData != null) {
                            for (Map<String, Object> med : medicinesData) {
                                String medName = (String) med.get("name");
                                int medQty = med.get("quantity") != null ? ((Long) med.get("quantity")).intValue() : 0;
                                double medPrice = med.get("price") != null ? (double) med.get("price") : 0.0;
                                double medTotal = med.get("total") != null ? (double) med.get("total") : 0.0;
                                medicines.add(new Sale.MedicineSold(medName, medQty, medPrice, medTotal));
                            }
                        }

                        salesList.add(new Sale(id, customerName, phoneNumber, medicines, dateTime, totalAmount));
                    }
                    salesAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch sales: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}