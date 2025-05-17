package com.example.appmeditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class ViewMedicines extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicineAdapter medicineAdapter;
    private List<Medicine> medicineList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medicines);

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
                    intent = new Intent(ViewMedicines.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(ViewMedicines.this, CashierDashboardActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                // Fallback: go to cashier dashboard
                Intent intent = new Intent(ViewMedicines.this, CashierDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ViewMedicines.this, LoginActivity.class));
            finish();
        });

        recyclerView = findViewById(R.id.recyclerViewMedicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicineList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(medicineList, this::deleteMedicine);
        recyclerView.setAdapter(medicineAdapter);

        db = FirebaseFirestore.getInstance();

        fetchMedicines();
    }

    private void fetchMedicines() {
        db.collection("medicines")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    medicineList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Medicine medicine = new Medicine(
                                doc.getId(),
                                doc.getString("name"),
                                doc.getString("description"),
                                doc.getLong("quantity") != null ? doc.getLong("quantity").intValue() : 0,
                                doc.getDouble("price") != null ? doc.getDouble("price") : 0.0,
                                doc.getString("expiry_date")
                        );
                        medicineList.add(medicine);
                    }
                    medicineAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch medicines: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteMedicine(String medicineId) {
        db.collection("medicines").document(medicineId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Medicine deleted", Toast.LENGTH_SHORT).show();
                    // Remove from the list and notify adapter
                    for (int i = 0; i < medicineList.size(); i++) {
                        if (medicineList.get(i).getId().equals(medicineId)) {
                            medicineList.remove(i);
                            medicineAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
