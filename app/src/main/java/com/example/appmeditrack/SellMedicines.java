package com.example.appmeditrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class SellMedicines extends AppCompatActivity {

    RecyclerView recyclerView, recyclerViewCart;
    FormAdapter formAdapter;
    CartAdapter cartAdapter;
    FirebaseFirestore db;
    List<Map<String, Object>> cart = new ArrayList<>();
    TextView txtCartTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_medicines);

        db = FirebaseFirestore.getInstance();
        txtCartTotal = findViewById(R.id.txtCartTotal);

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
                    intent = new Intent(SellMedicines.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(SellMedicines.this, CashierDashboardActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                // Fallback: go to cashier dashboard
                Intent intent = new Intent(SellMedicines.this, CashierDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SellMedicines.this, LoginActivity.class));
            finish();
        });

        recyclerView = findViewById(R.id.recyclerViewSellMedicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cart);
        recyclerViewCart.setAdapter(cartAdapter);

        List<FormItem> formItems = new ArrayList<>();
        formItems.add(new FormItem(FormItem.TYPE_HEADER, "Sell Medicine", "", null));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Customer Name", "Enter name", "text"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Phone", "Enter phone", "number"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Email", "Enter email", "text"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Medicine Name", "Enter medicine name", "text"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Price (PKR)", "Enter price", "number"));
        formItems.add(new FormItem(FormItem.TYPE_FIELD, "Quantity", "Enter quantity", "number"));
        formItems.add(new FormItem(FormItem.TYPE_BUTTON, "Add to Cart", "", null));

        db.collection("medicines")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> medicineNames = new ArrayList<>();
                    Map<String, Double> medicinePriceMap = new HashMap<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        Double price = doc.getDouble("price");
                        if (name != null && price != null) {
                            medicineNames.add(name);
                            medicinePriceMap.put(name, price);
                        }
                    }
                    formAdapter = new FormAdapter(formItems, recyclerView, medicineNames, medicinePriceMap);
                    recyclerView.setAdapter(formAdapter);

                    formAdapter.setOnButtonClickListener(() -> {
                        String medicineName = formAdapter.getValueForLabel("Medicine Name");
                        String priceStr = formAdapter.getValueForLabel("Price (PKR)");
                        String quantityStr = formAdapter.getValueForLabel("Quantity");
                        if (medicineName.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
                            Toast.makeText(this, "Enter medicine, price, and quantity", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int quantity = Integer.parseInt(quantityStr);
                        double price = Double.parseDouble(priceStr);
                        double total = price * quantity;
                        Map<String, Object> cartItem = new HashMap<>();
                        cartItem.put("name", medicineName);
                        cartItem.put("price", price);
                        cartItem.put("quantity", quantity);
                        cartItem.put("total", total);
                        cart.add(cartItem);
                        cartAdapter.notifyDataSetChanged();
                        updateCartTotal();
                        // Optionally clear medicine fields
                        for (int i = 0; i < formItems.size(); i++) {
                            if (formItems.get(i).getLabel().equals("Medicine Name") ||
                                    formItems.get(i).getLabel().equals("Price (PKR)") ||
                                    formItems.get(i).getLabel().equals("Quantity")) {
                                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
                                if (holder instanceof FormAdapter.FieldViewHolder) {
                                    ((FormAdapter.FieldViewHolder) holder).input.setText("");
                                }
                            }
                        }
                    });
                });

        // Sell Medicine Button (outside RecyclerView)
        Button btnSellMedicine = findViewById(R.id.btnSellMedicine);
        btnSellMedicine.setOnClickListener(v -> {
            String customerName = formAdapter.getValueForLabel("Customer Name");
            String phone = formAdapter.getValueForLabel("Phone");
            String email = formAdapter.getValueForLabel("Email");
            if (customerName.isEmpty() || phone.isEmpty() || email.isEmpty() || cart.isEmpty()) {
                Toast.makeText(this, "Fill customer info and add at least one medicine", Toast.LENGTH_SHORT).show();
                return;
            }
            final double[] totalAmount = {0};
            for (Map<String, Object> item : cart) {
                totalAmount[0] += (double) item.get("total");
            }
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            Map<String, Object> sale = new HashMap<>();
            sale.put("customerName", customerName);
            sale.put("phoneNumber", phone);
            sale.put("email", email);
            sale.put("dateTime", dateTime);
            sale.put("totalAmount", totalAmount[0]);
            sale.put("medicines", cart);

            db.collection("sales")
                    .add(sale)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Sale recorded successfully", Toast.LENGTH_SHORT).show();
                        // Update stock for each medicine
                        for (Map<String, Object> item : cart) {
                            updateMedicineStock((String) item.get("name"), (int) item.get("quantity"));
                        }
                        addOrUpdateCustomer(customerName, phone, email, dateTime);

                        // Go to receipt page (pass only summary info or the whole cart as needed)
                        Intent receiptIntent = new Intent(SellMedicines.this, ReceiptActivity.class);
                        receiptIntent.putExtra("customerName", customerName);
                        receiptIntent.putExtra("phone", phone);
                        receiptIntent.putExtra("email", email);
                        receiptIntent.putExtra("totalAmount", String.valueOf(totalAmount[0]));
                        receiptIntent.putExtra("dateTime", dateTime);
                        // Pass the cart BEFORE clearing it!
                        receiptIntent.putExtra("cart", new ArrayList<>(cart));
                        startActivity(receiptIntent);

                        // Now clear cart and update total
                        cart.clear();
                        cartAdapter.notifyDataSetChanged();
                        updateCartTotal();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to record sale: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void updateCartTotal() {
        double total = 0;
        for (Map<String, Object> item : cart) {
            total += (double) item.get("total");
        }
        txtCartTotal.setText("₨" + total);
    }

    private void updateMedicineStock(String medicineName, int soldQuantity) {
        db.collection("medicines")
                .whereEqualTo("name", medicineName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long currentQty = doc.getLong("quantity");
                        if (currentQty != null && currentQty >= soldQuantity) {
                            db.collection("medicines").document(doc.getId())
                                    .update("quantity", currentQty - soldQuantity);
                        }
                    }
                });
    }

    private void addOrUpdateCustomer(String name, String phone, String email, String registrationDate) {
        db.collection("customers")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Customer does not exist, add new
                        Map<String, Object> customer = new HashMap<>();
                        customer.put("name", name);
                        customer.put("phone", phone);
                        customer.put("email", email);
                        customer.put("registrationDate", registrationDate);
                        db.collection("customers").add(customer);
                    } else {
                        // Customer exists, optionally update their info
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            db.collection("customers").document(doc.getId())
                                    .update("name", name, "phone", phone, "registrationDate", registrationDate);
                        }
                    }
                });
    }
}