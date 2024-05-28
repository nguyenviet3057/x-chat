package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.planx.xchat.R;
import com.planx.xchat.XChat;
import com.planx.xchat.constants.AppLoginStatus;
import com.planx.xchat.retrofit.status.LoginResponseStatus;
import com.planx.xchat.retrofit.status.ResponseStatus;
import com.planx.xchat.contexts.SharedPreferencesManager;
import com.planx.xchat.databinding.ActivityLoginBinding;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.retrofit.ApiResponseCallback;
import com.planx.xchat.retrofit.request.LoginRequest;
import com.planx.xchat.retrofit.response.LoginResponse;
import com.planx.xchat.retrofit.RetrofitClient;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferencesManager.getInstance().setLoginStatus(AppLoginStatus.NOT_LOGGED_IN);

        binding.etUsername.requestFocus();

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (username.trim().equals("") || password.trim().equals("")) {
                Toast.makeText(this, getString(R.string.fillForm), Toast.LENGTH_SHORT).show();
                if (username.trim().equals("")) {
                    binding.etUsername.requestFocus();
                } else {
                    binding.etPassword.requestFocus();
                }
            } else {
                binding.btnLogin.setEnabled(false);
                binding.btnLogin.setText(getString(R.string.btnLoggingIn));

                RetrofitClient.getInstance().sendRequest(RetrofitClient.getInstance().getApiService().login(new LoginRequest(username, password)), new ApiResponseCallback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse response) {
                        switch (response.getStatus()) {
                            case LoginResponseStatus.OK:
                                SharedPreferencesManager.getInstance().setLoginStatus(AppLoginStatus.LOGIN_SUCCESS);
                                MainUser.getInstance().setInstance(response.getData());
                                SharedPreferencesManager.getInstance().setUserData();
                                SharedPreferencesManager.getInstance().setJwtToken(MainUser.getInstance().getJwtToken());

                                XChat.firestore.collection(XChat.colUsers).document(MainUser.getInstance().getId()).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        MainUser.getInstance().setFriends(task.getResult().toObject(MainUser.class).getFriends());
                                        MainUser.getInstance().setRooms(task.getResult().toObject(MainUser.class).getRooms());
                                        SharedPreferencesManager.getInstance().setUserData();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            case LoginResponseStatus.ERROR:
                                SharedPreferencesManager.getInstance().setLoginStatus(AppLoginStatus.ACCOUNT_NOT_FOUND);
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.accountNotFound), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(this.toString(), throwable.toString());
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                        SharedPreferencesManager.getInstance().setLoginStatus(AppLoginStatus.SERVER_ERROR);
                        binding.btnLogin.setText(getString(R.string.btnLogin));
                        binding.btnLogin.setEnabled(true);
                    }
                });
            }
        });

        binding.clMainLayout.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        binding.btnToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });
    }
}