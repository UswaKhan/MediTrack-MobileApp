package com.example.appmeditrack;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.RelativeLayout;
import android.widget.GridLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends AppCompatActivity {

    Button btnLogout;
    CardView cardAddMedicine, cardViewMedicines, cardAddUser, cardViewUsers, cardSalesHistory, cardViewCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finish();
        });

        // Initialize card views
        cardAddMedicine = findViewById(R.id.cardAddMedicine);
        cardViewMedicines = findViewById(R.id.cardViewMedicines);
        cardAddUser = findViewById(R.id.cardAddUser);
        cardViewUsers = findViewById(R.id.cardViewUsers);
        cardSalesHistory = findViewById(R.id.cardSalesHistory);
        cardViewCustomers = findViewById(R.id.cardViewCustomers);

        // Set click listeners
        cardAddMedicine.setOnClickListener(v -> startActivity(new Intent(this, AddMedicine.class)));
        cardViewMedicines.setOnClickListener(v -> startActivity(new Intent(this, ViewMedicines.class)));
        cardAddUser.setOnClickListener(v -> startActivity(new Intent(this, AddUser.class)));
        cardViewUsers.setOnClickListener(v -> startActivity(new Intent(this, ViewUsers.class)));
        cardSalesHistory.setOnClickListener(v -> startActivity(new Intent(this, SalesHistory.class)));
        cardViewCustomers.setOnClickListener(v -> startActivity(new Intent(this, ViewCustomers.class)));
    }
}
