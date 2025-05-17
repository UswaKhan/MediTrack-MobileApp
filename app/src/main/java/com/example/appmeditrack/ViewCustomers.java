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

public class ViewCustomers extends AppCompatActivity {

    RecyclerView recyclerViewCustomers;
    CustomerAdapter customerAdapter;
    List<Customer> customerList;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customers);

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
                    intent = new Intent(ViewCustomers.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(ViewCustomers.this, CashierDashboardActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                // Fallback: go to cashier dashboard
                Intent intent = new Intent(ViewCustomers.this, CashierDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ViewCustomers.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerViewCustomers = findViewById(R.id.recyclerViewCustomers);
        recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(this));
        customerList = new ArrayList<>();

        // Get current user UID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        // Check isAdmin from Firestore
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            boolean isAdmin = documentSnapshot.getBoolean("isAdmin") != null && documentSnapshot.getBoolean("isAdmin");

            customerAdapter = new CustomerAdapter(customerList, isAdmin);
            recyclerViewCustomers.setAdapter(customerAdapter);

            // Set delete listener only if admin
            if (isAdmin) {
                customerAdapter.setOnDeleteClickListener((customer, position) -> {
                    db.collection("customers").document(customer.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                customerList.remove(position);
                                customerAdapter.notifyDataSetChanged();
                                Toast.makeText(ViewCustomers.this, "Customer deleted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ViewCustomers.this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
            }

            fetchCustomers();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to check admin status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Fallback: show customers without delete
            customerAdapter = new CustomerAdapter(customerList, false);
            recyclerViewCustomers.setAdapter(customerAdapter);
            fetchCustomers();
        });
    }

    private void fetchCustomers() {
        db.collection("customers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    customerList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        String phone = doc.getString("phone");
                        String email = doc.getString("email");
                        String registrationDate = doc.getString("registrationDate");
                        customerList.add(new Customer(id, name, phone, email, registrationDate));
                    }
                    customerAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch customers: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}