package com.planx.xchat.retrofit.response;

import com.planx.xchat.models.MainUser;

public class PingResponse {
    private int status;
    private String message;
    private MainUser data;

    public PingResponse(int status, String message, MainUser data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MainUser getData() {
        return data;
    }

    public void setData(MainUser data) {
        this.data = data;
    }
}
