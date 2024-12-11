package com.example.mobile_programming_test_nov_2024.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobile_programming_test_nov_2024.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "user_prefs";
    private static final String CONTENT_TYPE_KEY = "Content-Type";  // Đặt tên là "Content-Type"

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Lưu vào SharedPreferences với kiểm tra trùng lặp
    public void saveUsersToPreferences(List<User> newUsers) {
        List<User> existingUsers = getUsersFromPreferences();  // Lấy người dùng đã lưu từ SharedPreferences

        if (existingUsers == null) {
            existingUsers = new ArrayList<>();
        }

        // Thêm người dùng mới vào danh sách, tránh trùng lặp
        for (User newUser : newUsers) {
            boolean exists = false;
            for (User existingUser : existingUsers) {
                if (existingUser.getId() == newUser.getId()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                existingUsers.add(newUser);  // Thêm mới nếu chưa có
            }
        }

        // Lưu lại danh sách đã cập nhật
        Gson gson = new Gson();
        String json = gson.toJson(existingUsers);
        editor.putString(CONTENT_TYPE_KEY, json);  // Lưu vào với tên "Content-Type"
        editor.apply();  // Áp dụng thay đổi
    }


    // Lấy danh sách người dùng từ SharedPreferences
    public List<User> getUsersFromPreferences() {
        String json = sharedPreferences.getString(CONTENT_TYPE_KEY, null);  // Lấy chuỗi JSON với khóa "Content-Type"
        if (json == null) {
            return null;  // Nếu không có dữ liệu, trả về null
        }
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<User>>(){}.getType());  // Chuyển chuỗi JSON về danh sách đối tượng
    }
}

