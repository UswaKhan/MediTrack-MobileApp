package com.example.appmeditrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customerList;
    private OnDeleteClickListener deleteClickListener;
    private boolean isAdmin;

    public CustomerAdapter(List<Customer> customerList, boolean isAdmin) {
        this.customerList = customerList;
        this.isAdmin = isAdmin;
    }

    // Delete button callback interface
    public interface OnDeleteClickListener {
        void onDeleteClick(Customer customer, int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.name.setText("Name: " + customer.getName());
        holder.phone.setText("Phone: " + customer.getPhone());
        holder.email.setText("Email: " + customer.getEmail());
        holder.registrationDate.setText("Registered: " + customer.getRegistrationDate());

        // Show or hide delete button based on isAdmin
        holder.btnDeleteCustomer.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        holder.btnDeleteCustomer.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(customer, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, email, registrationDate;
        Button btnDeleteCustomer;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtCustomerName);
            phone = itemView.findViewById(R.id.txtCustomerPhone);
            email = itemView.findViewById(R.id.txtCustomerEmail);
            registrationDate = itemView.findViewById(R.id.txtRegistrationDate);
            btnDeleteCustomer = itemView.findViewById(R.id.btnDeleteCustomer);
        }
    }
}