package com.planx.xchat.retrofit.response;

import com.planx.xchat.models.User;

import java.util.ArrayList;

public class SearchResponse {
    private int status;
    private String message;
    private ArrayList<User> data;

    public SearchResponse(int status, String message, ArrayList<User> data) {
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

    public ArrayList<User> getData() {
        return data;
    }

    public void setData(ArrayList<User> data) {
        this.data = data;
    }
}
