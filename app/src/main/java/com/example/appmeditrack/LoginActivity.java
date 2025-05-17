package com.example.appmeditrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Spinner spinnerRole;
    Button btnLogin;
    String selectedRole;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtUsername); // Use email as username
        edtPassword = findViewById(R.id.edtPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString().toLowerCase();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = "admin";
            }
        });

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Check user role in Firestore
                                String uid = mAuth.getCurrentUser().getUid();
                                db.collection("users").document(uid).get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String role = documentSnapshot.getString("role");
                                                if (role != null && role.equals(selectedRole)) {
                                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                    if (role.equals("admin")) {
                                                        startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                                    } else {
                                                        startActivity(new Intent(LoginActivity.this, CashierDashboardActivity.class));
                                                    }
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Role mismatch", Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }
                                        });
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}