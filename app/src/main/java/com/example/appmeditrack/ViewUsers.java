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

public class ViewUsers extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;
    private Button btnLogout;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        db = FirebaseFirestore.getInstance();

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

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
                    intent = new Intent(ViewUsers.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(ViewUsers.this, CashierDashboardActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                // Fallback: go to cashier dashboard
                Intent intent = new Intent(ViewUsers.this, CashierDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ViewUsers.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, this::deleteUser);
        recyclerViewUsers.setAdapter(userAdapter);

        fetchUsers();
    }

    private void fetchUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String username = doc.getString("username");
                        String role = doc.getString("role");
                        String email = doc.getString("email");
                        String phone = doc.getString("phone");
                        // For now, use a default profile picture
                        int profilePicResId = R.drawable.defaultproflie;
                        userList.add(new User(id, username, role, email, phone, profilePicResId));
                    }
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch users: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteUser(String userId, int position) {
        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                    userAdapter.removeUser(position);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}