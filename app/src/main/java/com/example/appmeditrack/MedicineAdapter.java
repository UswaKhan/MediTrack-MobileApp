package com.example.appmeditrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private final List<Medicine> medicineList;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(String medicineId);
    }

    public MedicineAdapter(List<Medicine> medicineList, OnDeleteClickListener onDeleteClickListener) {
        this.medicineList = medicineList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_item, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);

        holder.medicineName.setText(medicine.getName());
        holder.medicineDescription.setText(medicine.getDescription());
        holder.medicineQuantity.setText(String.valueOf(medicine.getQuantity()));
        holder.medicinePrice.setText(String.format("₨ %.2f", medicine.getPrice()));
        holder.medicineExpiryDate.setText(medicine.getExpiryDate());

        holder.btnDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(medicine.getId()));
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView medicineId, medicineName, medicineDescription, medicineQuantity, medicinePrice, medicineExpiryDate;
        Button btnDelete;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicineName);
            medicineDescription = itemView.findViewById(R.id.medicineDescription);
            medicineQuantity = itemView.findViewById(R.id.medicineQuantity);
            medicinePrice = itemView.findViewById(R.id.medicinePrice);
            medicineExpiryDate = itemView.findViewById(R.id.medicineExpiryDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
