package com.example.appmeditrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<User> userList;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(String userId, int position);
    }

    public UserAdapter(Context context, ArrayList<User> userList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.userList = userList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfilePic;
        TextView userName, userRole, userEmail, userPhone;
        Button btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfilePic = itemView.findViewById(R.id.userProfilePic);
            userName = itemView.findViewById(R.id.userName);
            userRole = itemView.findViewById(R.id.userRole);
            userEmail = itemView.findViewById(R.id.userEmail);
            userPhone = itemView.findViewById(R.id.userPhone);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.userProfilePic.setImageResource(user.getProfilePicResId());
        holder.userName.setText(user.getUsername());
        holder.userRole.setText("Role: " + user.getRole());
        holder.userEmail.setText("Email: " + user.getEmail());
        holder.userPhone.setText("Phone: " + user.getPhone());

        holder.btnDeleteUser.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(user.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void removeUser(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userList.size());
    }
}