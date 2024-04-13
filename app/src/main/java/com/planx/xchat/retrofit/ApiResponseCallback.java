package com.planx.xchat.retrofit;

public interface ApiResponseCallback<T> {
    void onSuccess(T response);
    void onFailure(Throwable throwable);
}
