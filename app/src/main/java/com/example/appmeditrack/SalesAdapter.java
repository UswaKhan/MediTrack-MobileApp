package com.example.appmeditrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {

    private Context context;
    private ArrayList<Sale> salesList;
    private OnDeleteClickListener deleteClickListener;
    private boolean isAdmin;

    // Constructor with isAdmin flag
    public SalesAdapter(Context context, ArrayList<Sale> salesList, boolean isAdmin) {
        this.context = context;
        this.salesList = salesList;
        this.isAdmin = isAdmin;
    }

    // Delete button callback interface
    public interface OnDeleteClickListener {
        void onDeleteClick(Sale sale, int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public static class SalesViewHolder extends RecyclerView.ViewHolder {
        TextView txtCustomerName, txtPhone, txtMedicine, txtDateTime, txtAmount;
        Button btnDelete;

        public SalesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtMedicine = itemView.findViewById(R.id.txtMedicine);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sale_item, parent, false);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder holder, int position) {
        Sale sale = salesList.get(position);

        holder.txtCustomerName.setText("Customer: " + sale.getCustomerName());
        holder.txtPhone.setText("Phone: " + sale.getPhoneNumber());

        // Show all medicines in a single TextView (multiline)
        StringBuilder medicinesStr = new StringBuilder();
        for (Sale.MedicineSold med : sale.getMedicines()) {
            medicinesStr.append("Medicine: ").append(med.getName())
                    .append(" | Qty: ").append(med.getQuantity())
                    .append(" | Price: ₨").append(med.getPrice())
                    .append(" | Total: ₨").append(med.getTotal())
                    .append("\n");
        }
        holder.txtMedicine.setText(medicinesStr.toString().trim());

        holder.txtDateTime.setText("Date: " + sale.getDateTime());
        holder.txtAmount.setText("Total: ₨" + sale.getTotalAmount());

        // Show or hide delete button based on isAdmin
        holder.btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(sale, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }
}