package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.planx.xchat.R;
import com.planx.xchat.XChat;
import com.planx.xchat.retrofit.response.ResponseStatus;
import com.planx.xchat.contexts.SharedPreferencesManager;
import com.planx.xchat.databinding.ActivitySignupBinding;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.retrofit.ApiResponseCallback;
import com.planx.xchat.retrofit.response.SignupResponse;
import com.planx.xchat.retrofit.RetrofitClient;
import com.planx.xchat.retrofit.request.SignupRequest;
import com.planx.xchat.models.User;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etFirstName.requestFocus();

        binding.clMainLayout.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        binding.btnSignup.setOnClickListener(v -> {
            String firstName;
            String lastName;
            String username;
            String password;
            String confirmPassword;
            if (binding.etFirstName.getText() != null && binding.etLastName.getText() != null && binding.etUsername.getText() != null && binding.etPassword.getText() != null && binding.etPasswordConfirm.getText() != null) {
                firstName = binding.etFirstName.getText().toString().trim();
                lastName = binding.etLastName.getText().toString().trim();
                username = binding.etUsername.getText().toString().trim();
                password = binding.etPassword.getText().toString().trim();
                confirmPassword = binding.etPasswordConfirm.getText().toString().trim();

                if (firstName.equals("") || lastName.equals("") || username.equals("") || password.equals("") || confirmPassword.equals("")) {
                    Toast.makeText(this, getResources().getString(R.string.pleaseFillTheForm), Toast.LENGTH_LONG).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(this, getResources().getString(R.string.incorrectConfirmPassword), Toast.LENGTH_LONG).show();
                    binding.etPasswordConfirm.requestFocus();
                } else {
                    User user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);

                    CollectionReference colUsers = XChat.firestore.collection(XChat.colUsers);
                    colUsers.add(user)
                            .addOnSuccessListener(task -> {
                                String id = task.getId();
                                Toast.makeText(this, id, Toast.LENGTH_LONG).show();
                                RetrofitClient.getInstance().sendRequest(RetrofitClient.getInstance().getApiService().signup(new SignupRequest(id, firstName, lastName, username, password, confirmPassword)), new ApiResponseCallback<SignupResponse>() {
                                            @Override
                                            public void onSuccess(SignupResponse response) {
                                                switch (response.getStatus()) {
                                                    case ResponseStatus.OK:
                                                        MainUser.getInstance().setInstance(response.getData());

                                                        colUsers.document(id).update(MainUser.getInstance().toMap()).addOnCompleteListener(updateTask -> {
                                                            if (updateTask.isSuccessful()) {
                                                                SharedPreferencesManager.getInstance().setUserData();
                                                                SharedPreferencesManager.getInstance().setJwtToken(MainUser.getInstance().getJwtToken());
                                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Log.e(this.toString(), updateTask.getException().getMessage());
                                                                Toast.makeText(SignupActivity.this, getResources().getString(R.string.signupFailed), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                        break;
                                                    case ResponseStatus.ERROR:
                                                        Log.e(this.toString(), response.getMessage());
                                                        Toast.makeText(SignupActivity.this, getResources().getString(R.string.signupFailed), Toast.LENGTH_LONG).show();
                                                        break;
                                                    case ResponseStatus.USERNAME_EXISTED:
                                                        Toast.makeText(SignupActivity.this, getResources().getString(R.string.usernameExisted), Toast.LENGTH_LONG).show();
                                                        binding.etUsername.requestFocus();
                                                        break;
                                                }
                                            }

                                            @Override
                                            public void onFailure(Throwable throwable) {
                                                Log.e(this.toString(), throwable.getMessage());
                                                Toast.makeText(SignupActivity.this, getResources().getString(R.string.signupFailed), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> {

                            });
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.pleaseFillTheForm), Toast.LENGTH_LONG).show();
            }
        });

        binding.btnToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}