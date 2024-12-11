package com.example.mobile_programming_test_nov_2024.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_programming_test_nov_2024.Adapter.UserAdapter;
import com.example.mobile_programming_test_nov_2024.api.GitHubApi;
import com.example.mobile_programming_test_nov_2024.api.RetrofitClient;
import com.example.mobile_programming_test_nov_2024.databinding.ActivityMainBinding;
import com.example.mobile_programming_test_nov_2024.models.User;
import com.example.mobile_programming_test_nov_2024.response.SharedPreferencesHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private UserAdapter userAdapter;

    // Biến toàn cục để lưu trạng thái phân trang
    private int perPage = 20;
    private int since = 0;
    private boolean isLoading = false;

    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo SharedPreferencesHelper
        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        // Lấy danh sách người dùng từ SharedPreferences nếu có
        List<User> savedUsers = sharedPreferencesHelper.getUsersFromPreferences();
        if (savedUsers != null && !savedUsers.isEmpty()) {
            // Nếu có, hiển thị luôn
            userAdapter = new UserAdapter(MainActivity.this, savedUsers);
            binding.rvUsers.setAdapter(userAdapter);
            Toast.makeText(MainActivity.this, "da lưu", Toast.LENGTH_SHORT).show();
        } else {
            // Nếu không có, tải từ API
            fetchUsers();
            Toast.makeText(MainActivity.this, "chua duoc luu", Toast.LENGTH_SHORT).show();
        }

        setupRecyclerView();

        // Xử lý sự kiện khi nhấn vào nút "navigation" trên Toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));

        // Thêm sự kiện scroll listener
        binding.rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadMoreUsers();
                    }
                }
            }
        });
    }

    private void loadMoreUsers() {
        isLoading = true;
        GitHubApi api = RetrofitClient.getInstance().create(GitHubApi.class);
        Call<List<User>> call = api.getUsers(perPage, since);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> newUsers = response.body();

                    if (userAdapter == null) {
                        userAdapter = new UserAdapter(MainActivity.this, newUsers);
                        binding.rvUsers.setAdapter(userAdapter);
                    } else {
                        userAdapter.addUsers(newUsers);
                    }

                    // Lưu vào SharedPreferences với Content-Type
                    sharedPreferencesHelper.saveUsersToPreferences(userAdapter.getUsers());

                    // Cập nhật `since` với ID user cuối cùng
                    if (!newUsers.isEmpty()) {
                        since = newUsers.get(newUsers.size() - 1).getId();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load more data", Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isLoading = false;
            }
        });
    }

    private void fetchUsers() {
        binding.progressBar.setVisibility(View.VISIBLE);

        GitHubApi api = RetrofitClient.getInstance().create(GitHubApi.class);
        Call<List<User>> call = api.getUsers(perPage, since);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    userAdapter = new UserAdapter(MainActivity.this, users);
                    binding.rvUsers.setAdapter(userAdapter);

                    // Lưu danh sách vào SharedPreferences với Content-Type
                    sharedPreferencesHelper.saveUsersToPreferences(users);

                    if (!users.isEmpty()) {
                        since = users.get(users.size() - 1).getId();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
