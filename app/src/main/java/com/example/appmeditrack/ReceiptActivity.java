package com.example.appmeditrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;

public class ReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Get sale details from intent
        Intent intent = getIntent();
        String customerName = intent.getStringExtra("customerName");
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String totalAmount = intent.getStringExtra("totalAmount");
        String dateTime = intent.getStringExtra("dateTime");

        // Set values in receipt layout
        ((TextView) findViewById(R.id.txtReceiptCustomer)).setText("Customer: " + customerName);
        ((TextView) findViewById(R.id.txtReceiptPhone)).setText("Phone: " + phone);
        ((TextView) findViewById(R.id.txtReceiptEmail)).setText("Email: " + email);
        ((TextView) findViewById(R.id.txtReceiptTotal)).setText("Total: ₨" + totalAmount);
        ((TextView) findViewById(R.id.txtReceiptDate)).setText("Date: " + dateTime);

        // Show all medicines in the cart
        ArrayList<Map<String, Object>> cart = (ArrayList<Map<String, Object>>) intent.getSerializableExtra("cart");
        LinearLayout medicinesListLayout = findViewById(R.id.medicinesListLayout);
        if (cart != null) {
            for (Map<String, Object> item : cart) {
                String medName = (String) item.get("name");
                int qty = (int) item.get("quantity");
                double price = (double) item.get("price");
                double total = (double) item.get("total");

                TextView medView = new TextView(this);
                medView.setText("Medicine: " + medName + " | Qty: " + qty + " | Price: ₨" + price + " | Total: ₨" + total);
                medView.setTextColor(getResources().getColor(android.R.color.black));
                medView.setTextSize(15);
                medicinesListLayout.addView(medView);
            }
        }

        Button btnDashboard = findViewById(R.id.btnDashboard);
        btnDashboard.setOnClickListener(v -> {
            Intent intentDash = new Intent(ReceiptActivity.this, CashierDashboardActivity.class);
            intentDash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentDash);
            finish();
        });
    }

    private void downloadReceipt() {
        View receiptView = findViewById(R.id.receiptLayout);
        Bitmap bitmap = Bitmap.createBitmap(receiptView.getWidth(), receiptView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        receiptView.draw(canvas);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas pdfCanvas = page.getCanvas();
        pdfCanvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        try {
            File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Receipts");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, "receipt_" + System.currentTimeMillis() + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(this, "Receipt saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save receipt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}