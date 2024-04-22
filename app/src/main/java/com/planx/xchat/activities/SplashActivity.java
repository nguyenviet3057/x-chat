package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.planx.xchat.XChat;
import com.planx.xchat.contexts.SharedPreferencesManager;
import com.planx.xchat.databinding.ActivitySplashBinding;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.retrofit.ApiResponseCallback;
import com.planx.xchat.retrofit.RetrofitClient;
import com.planx.xchat.retrofit.request.PingRequest;
import com.planx.xchat.retrofit.response.PingResponse;
import com.planx.xchat.retrofit.status.PingResponseStatus;
import com.planx.xchat.retrofit.status.ResponseStatus;
import com.planx.xchat.service.StatusService;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferencesManager.getInstance().getUserData();
        if (MainUser.getInstance().getId() == null) {
            Toast.makeText(SplashActivity.this, "Empty user", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        else {
            RetrofitClient.getInstance().sendRequest(RetrofitClient.getInstance().getApiService().ping(new PingRequest(SharedPreferencesManager.getInstance().getJwtToken())), new ApiResponseCallback<PingResponse>() {
                @Override
                public void onSuccess(PingResponse response) {
                    if (response.getStatus() == PingResponseStatus.OK) {
                        MainUser.getInstance().setInstance(response.getData());
                        XChat.firestore.collection(XChat.colUsers).document(MainUser.getInstance().getId()).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                Intent serviceIntent = new Intent(SplashActivity.this, StatusService.class);
                                startService(serviceIntent);
                                finish();
                            } else {
                                Toast.makeText(SplashActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(SplashActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e(this.toString(), throwable.getMessage());
                    Toast.makeText(SplashActivity.this, "Error auto log in", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}