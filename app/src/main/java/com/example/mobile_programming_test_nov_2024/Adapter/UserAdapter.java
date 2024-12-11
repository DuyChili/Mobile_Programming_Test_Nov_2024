package com.example.mobile_programming_test_nov_2024.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobile_programming_test_nov_2024.Activity.UserdetailsActivity;
import com.example.mobile_programming_test_nov_2024.databinding.ItemUserBinding;
import com.example.mobile_programming_test_nov_2024.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }
    // Phương thức để lấy danh sách người dùng
    public List<User> getUsers() {
        return userList;
    }
    public void addUser(User newUser) {
        userList.add(newUser);
        notifyItemInserted(userList.size() - 1);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.binding.tvUserName.setText(user.getLogin());
        holder.binding.tvUserLink.setText(user.getHtmlUrl());

        Glide.with(context)
                .load(user.getAvatarUrl())
                .circleCrop()
                .into(holder.binding.ivAvatar);

        holder.itemView.setOnClickListener(v ->{
            String login_username = user.getLogin();

            // Tạo Intent để chuyển đến UserdetailsActivity
            Intent intent = new Intent(holder.itemView.getContext(), UserdetailsActivity.class);
            intent.putExtra("login_username", login_username); // Truyền login_username cho UserdetailsActivity
            holder.itemView.getContext().startActivity(intent);
        } );
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
