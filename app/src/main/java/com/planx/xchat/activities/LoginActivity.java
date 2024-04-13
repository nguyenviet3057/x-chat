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
import com.planx.xchat.retrofit.response.ResponseStatus;
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

        binding.etUsername.requestFocus();

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            RetrofitClient.getInstance().sendRequest(RetrofitClient.getInstance().getApiService().login(new LoginRequest(username, password)), new ApiResponseCallback<LoginResponse>() {
                @Override
                public void onSuccess(LoginResponse response) {
                    switch (response.getStatus()) {
                        case ResponseStatus.OK:
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
                                } else {
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                        case ResponseStatus.ERROR:
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.accountNotFound), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e(this.toString(), throwable.toString());
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                }
            });

            // Local test
//            if (username.equals("admin") && password.equals("123456")) {
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
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
        });
    }
}