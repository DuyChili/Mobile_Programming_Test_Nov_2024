package com.example.mobile_programming_test_nov_2024.api;

import com.example.mobile_programming_test_nov_2024.models.User;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubApi {
    // Phương thức để lấy danh sách người dùng
    @GET("users")
    Call<List<User>> getUsers(@Query("per_page") int perPage, @Query("since") int since);
    // Phương thức để lấy thông tin chi tiết của một người dùng cụ thể
    @GET("users/{login}")
    Call<User> getUserDetails(@Path("login") String loginUsername);
}
