package com.example.mobile_programming_test_nov_2024.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobile_programming_test_nov_2024.api.GitHubApi;
import com.example.mobile_programming_test_nov_2024.api.RetrofitClient;
import com.example.mobile_programming_test_nov_2024.databinding.ActivityUserdetailsBinding;
import com.example.mobile_programming_test_nov_2024.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserdetailsActivity extends AppCompatActivity {

    private ActivityUserdetailsBinding binding;
    private String loginUsername;
    private RetrofitClient retrofitClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserdetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Lấy login_username từ Intent
        loginUsername = getIntent().getStringExtra("login_username");
        fetchUserDetailsFromApi();
        binding.toolbar.setNavigationOnClickListener(v -> {
            // Kết thúc Activity hiện tại (thoát trang)
            finish();
        });
    }
    private void fetchUserDetailsFromApi() {
        binding.progressBar.setVisibility(View.VISIBLE);
        // Lấy Retrofit instance từ RetrofitClient
        Retrofit retrofit = retrofitClient.getInstance();

        // Tạo instance của GitHubApi
        GitHubApi gitHubApi = retrofit.create(GitHubApi.class);

        // Gọi API để lấy thông tin chi tiết người dùng
        Call<User> call = gitHubApi.getUserDetails(loginUsername);

        // Xử lý phản hồi của API
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    // Cập nhật UI với thông tin người dùng
                    binding.loginTextView.setText(user.getLogin());
                    binding.locationTextView.setText(user.getLocation());
                    binding.followersTextView.setText(""+user.getFollowers());
                    binding.followingTextView.setText(""+user.getFollowing());

                    // Tải avatar với Glide
                    Glide.with(UserdetailsActivity.this)
                            .load(user.getAvatarUrl())
                            .circleCrop()
                            .into(binding.avatarImageView);
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(UserdetailsActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserdetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}