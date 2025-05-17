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

public class CashierDashboardActivity extends AppCompatActivity {

    Button btnLogout;
    CardView cardSellMedicines, cardViewMedicines, cardSalesHistory, cardViewCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_dashboard);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(CashierDashboardActivity.this, LoginActivity.class));
            finish();
        });

        // Initialize card views
        cardSellMedicines = findViewById(R.id.cardSellMedicines);
        cardViewMedicines = findViewById(R.id.cardViewMedicines);
        cardSalesHistory = findViewById(R.id.cardSalesHistory);
        cardViewCustomers = findViewById(R.id.cardViewCustomers);

        // Set click listeners
        cardSellMedicines.setOnClickListener(v -> startActivity(new Intent(this, SellMedicines.class)));
        cardViewMedicines.setOnClickListener(v -> startActivity(new Intent(this, ViewMedicines.class)));
        cardSalesHistory.setOnClickListener(v -> startActivity(new Intent(this, SalesHistory.class)));
        cardViewCustomers.setOnClickListener(v -> startActivity(new Intent(this, ViewCustomers.class)));
    }
}

