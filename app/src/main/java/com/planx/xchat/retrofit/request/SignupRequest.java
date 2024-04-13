package com.planx.xchat.retrofit.request;

public class SignupRequest {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String confirmPassword;

    public SignupRequest(String id, String firstName, String lastName, String username, String password, String confirmPassword) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
