package com.example.appmeditrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddUser extends AppCompatActivity {

    RecyclerView recyclerView;
    FormAdapter formAdapter;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        mAuth = FirebaseAuth.getInstance();
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
                    intent = new Intent(AddUser.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(AddUser.this, CashierDashboardActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                // Fallback: go to cashier dashboard
                Intent intent = new Intent(AddUser.this, CashierDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AddUser.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.recyclerViewAddUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<FormItem> formItems = new ArrayList<>();
        formItems.add(new FormItem(FormItem.TYPE_HEADER, "Add New User", "", null));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Username", "Enter username", "text"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Password", "Enter password", "text"));
        formItems.add(new FormItem(FormItem.TYPE_DROPDOWN, "Role", new String[]{"Admin", "Cashier"}));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Email", "Enter email", "text"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Phone Number", "Enter phone", "number"));
        formItems.add(new FormItem(FormItem.TYPE_BUTTON, "Add User", "", null));

        formAdapter = new FormAdapter(formItems, recyclerView);
        recyclerView.setAdapter(formAdapter);

        formAdapter.setOnButtonClickListener(() -> {
            String username = formAdapter.getValueForLabel("Username");
            String password = formAdapter.getValueForLabel("Password");
            String email = formAdapter.getValueForLabel("Email");
            String phone = formAdapter.getValueForLabel("Phone Number");

            // Get role from dropdown
            String role = "cashier"; // default
            for (int i = 0; i < formItems.size(); i++) {
                FormItem item = formItems.get(i);
                if (item.getType() == FormItem.TYPE_DROPDOWN && item.getLabel().equals("Role")) {
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
                    if (holder instanceof FormAdapter.DropdownViewHolder) {
                        Spinner spinner = ((FormAdapter.DropdownViewHolder) holder).spinner;
                        role = spinner.getSelectedItem().toString().toLowerCase();
                    }
                }
            }

            // Basic validation
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in Firebase Auth
            String finalRole = role;
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                // Save user profile in Firestore
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("username", username);
                                userMap.put("email", email);
                                userMap.put("phone", phone);
                                userMap.put("role", finalRole);

                                db.collection("users").document(uid)
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();
                                            // Optionally clear form fields here
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to save user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to add user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}