package com.example.mobile_programming_test_nov_2024.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobile_programming_test_nov_2024.SharedPreferences.SharedPreferencesHelper;
import com.example.mobile_programming_test_nov_2024.api.GitHubApi;
import com.example.mobile_programming_test_nov_2024.api.RetrofitClient;
import com.example.mobile_programming_test_nov_2024.databinding.ActivityUserdetailsBinding;
import com.example.mobile_programming_test_nov_2024.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserdetailsActivity extends AppCompatActivity {

    private ActivityUserdetailsBinding binding;
    private String loginUsername;
    private RetrofitClient retrofitClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserdetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Khởi tạo SharedPreferencesHelper
        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        // Lấy login_username từ Intent
        loginUsername = getIntent().getStringExtra("login_username");

        // Check if the loginUsername exists in SharedPreferences
        List<User> userList = sharedPreferencesHelper.getUsersFromPreferences();
        User matchingUser = null;

        for (User user : userList) {
            if (user.getLogin().equals(loginUsername)) {
                if (user.isCheck()){
                    matchingUser = user;
                    break;
                }
            }
        }

        // If the user exists in SharedPreferences, display it, otherwise fetch from API
        if (matchingUser != null) {
            displayUserDetails(matchingUser);
            Toast.makeText(UserdetailsActivity.this, "da luu", Toast.LENGTH_SHORT).show();

        } else {
            fetchUserDetailsFromApi();
            Toast.makeText(UserdetailsActivity.this, "chưa luu", Toast.LENGTH_SHORT).show();
        }

        binding.toolbar.setNavigationOnClickListener(v -> {
            // Kết thúc Activity hiện tại (thoát trang)
            finish();
        });
    }
    private void displayUserDetails(User user) {
        binding.loginTextView.setText(user.getLogin());
        binding.locationTextView.setText(user.getLocation());
        binding.followersTextView.setText(String.valueOf(user.getFollowers()));
        binding.followingTextView.setText(String.valueOf(user.getFollowing()));

        // Load avatar using Glide
        Glide.with(UserdetailsActivity.this)
                .load(user.getAvatarUrl())
                .circleCrop()
                .into(binding.avatarImageView);
    }

    private void fetchUserDetailsFromApi() {
        binding.progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = retrofitClient.getInstance();
        GitHubApi gitHubApi = retrofit.create(GitHubApi.class);

        // Gọi API để lấy thông tin chi tiết người dùng
        Call<User> call = gitHubApi.getUserDetails(loginUsername);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // Cập nhật SharedPreferences với thông tin người dùng mới
                    sharedPreferencesHelper.updateUserInPreferences(user);

                    // Cập nhật UI với thông tin người dùng
                    displayUserDetails(user);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}