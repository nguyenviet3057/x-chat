package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.planx.xchat.R;
import com.planx.xchat.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.etUsername.setBackgroundTintList(null);
            } else {
                binding.etUsername.setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(android.R.color.transparent))));
            }
        });

        binding.etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.etPassword.setBackgroundTintList(null);
            } else {
                binding.etPassword.setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(android.R.color.transparent))));
            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            // Local test
            if (username.equals("admin") && password.equals("123456")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        binding.clMainLayout.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            binding.etUsername.clearFocus();
            binding.etPassword.clearFocus();
        });
    }
}