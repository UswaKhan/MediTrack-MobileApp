package com.example.appmeditrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<Map<String, Object>> cart;

    public CartAdapter(List<Map<String, Object>> cart) {
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Map<String, Object> item = cart.get(position);
        holder.txtName.setText("Medicine: " + item.get("name"));
        holder.txtQty.setText("Qty: " + item.get("quantity"));
        holder.txtPrice.setText("Price: ₨" + item.get("price"));
        holder.txtTotal.setText("Total: ₨" + item.get("total"));
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtQty, txtPrice, txtTotal;
        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCartMedicineName);
            txtQty = itemView.findViewById(R.id.txtCartQuantity);
            txtPrice = itemView.findViewById(R.id.txtCartPrice);
            txtTotal = itemView.findViewById(R.id.txtCartTotal);
        }
    }
}