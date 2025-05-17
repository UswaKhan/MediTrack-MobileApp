package com.example.appmeditrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMedicine extends AppCompatActivity {

    RecyclerView recyclerView;
    List<FormItem> formItems;
    FormAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medicine);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AddMedicine), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewForm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        formItems = new ArrayList<>();
        formItems.add(new FormItem(FormItem.TYPE_HEADER, "Add Medicine", "", ""));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Name", "Enter medicine name", "text"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Description", "Enter description", "text"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Quantity", "Enter quantity", "number"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Expiry Date", "Select expiry date", "date"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Price (PKR)", "Enter price", "decimal"));
        formItems.add(new FormItem(FormItem.TYPE_BUTTON, "Add Medicine", "", ""));

        adapter = new FormAdapter(formItems, recyclerView);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        adapter.setOnButtonClickListener(() -> {
            String name = "", desc = "", date = "";
            int quantity = 0;
            double price = 0.0;

            for (FormItem item : formItems) {
                if (item.getType() == FormItem.TYPE_FIELD) {
                    String label = item.getLabel();
                    String value = adapter.getValueForLabel(label);
                    if (label.equals("Name")) name = value;
                    else if (label.equals("Description")) desc = value;
                    else if (label.equals("Quantity")) {
                        try { quantity = Integer.parseInt(value); } catch (Exception e) { quantity = 0; }
                    }
                    else if (label.equals("Expiry Date")) date = value;
                    else if (label.equals("Price (PKR)")) {
                        try { price = Double.parseDouble(value); } catch (Exception e) { price = 0.0; }
                    }
                }
            }

            // Validation
            if (name.isEmpty() || desc.isEmpty() || date.isEmpty() || quantity <= 0 || price <= 0) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare data for Firestore
            Map<String, Object> medicine = new HashMap<>();
            medicine.put("name", name);
            medicine.put("description", desc);
            medicine.put("quantity", quantity);
            medicine.put("expiry_date", date);
            medicine.put("price", price);

            db.collection("medicines")
                    .add(medicine)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Medicine added", Toast.LENGTH_SHORT).show();
                        // Optionally clear form fields here
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

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
                    intent = new Intent(AddMedicine.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(AddMedicine.this, CashierDashboardActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                // Fallback: go to cashier dashboard
                Intent intent = new Intent(AddMedicine.this, CashierDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        Button logoutBtn = findViewById(R.id.btnLogout);
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AddMedicine.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}