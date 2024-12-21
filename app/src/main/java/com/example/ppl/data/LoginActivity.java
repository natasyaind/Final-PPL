package com.example.ppl.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ppl.R;
import com.example.ppl.admin.AdminHomeActivity;
import com.example.ppl.apiservice.ApiService;
import com.example.ppl.apiservice.RetrofitClient;
import com.example.ppl.data.LoginRequest;
import com.example.ppl.data.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText accountEditText, unitKerjaEditText;
    private Button loginButton;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hubungkan ID layout ke view
        accountEditText = findViewById(R.id.emailEditText); // Ganti ID sesuai layout XML
        unitKerjaEditText = findViewById(R.id.passwordEditText); // Ganti ID sesuai layout XML
        loginButton = findViewById(R.id.loginButton);

        // Tambahkan listener untuk loginButton
        loginButton.setOnClickListener(view -> {
            String account = accountEditText.getText().toString().trim();
            String unitKerja = unitKerjaEditText.getText().toString().trim();

            if (account.isEmpty() || unitKerja.isEmpty()) {
                showToast("Silakan isi semua kolom");
            } else {
                loginUser(account, unitKerja);
            }
        });
    }

    private void loginUser(String account, String unitKerja) {
        // Pastikan ada koneksi internet sebelum melakukan login
        if (!isNetworkAvailable()) {
            showToast("Tidak ada koneksi internet");
            return;
        }

        // Buat instance dari ApiService
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Siapkan request body
        LoginRequest loginRequest = new LoginRequest(account, unitKerja);

        // Panggil API login
        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Dapatkan token dari response
                    String accessToken = response.body().getAccessToken();

                    // Pastikan token tidak kosong
                    if (accessToken != null && !accessToken.isEmpty()) {
                        saveAccessToken(accessToken);
                        navigateToAdminHome();
                    } else {
                        showToast("Token tidak valid.");
                        Log.e(TAG, "Token kosong atau tidak valid.");
                    }
                } else {
                    // Tanggapi error HTTP dengan kode status yang sesuai
                    if (response.code() == 401) {
                        showToast("Login gagal: Akun atau Unit Kerja salah.");
                    } else if (response.code() == 422) {
                        showToast("Login gagal: Data tidak valid.");
                    } else {
                        showToast("Login gagal, coba lagi.");
                    }
                    Log.e(TAG, "Login gagal: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showToast("Login gagal, periksa koneksi Anda");
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void saveAccessToken(String token) {
        // Simpan token ke SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.apply();
    }

    private void navigateToAdminHome() {
        // Navigasikan langsung ke halaman AdminHomeActivity
        Intent intent = new Intent(this, AdminHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Fungsi untuk memeriksa apakah koneksi internet tersedia
    private boolean isNetworkAvailable() {
        // Logika untuk memeriksa koneksi internet, menggunakan ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
