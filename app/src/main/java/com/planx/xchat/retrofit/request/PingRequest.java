package com.planx.xchat.retrofit.request;

public class PingRequest {
    private String jwtToken;

    public PingRequest(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
