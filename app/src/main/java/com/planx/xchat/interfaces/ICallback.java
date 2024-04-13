package com.planx.xchat.interfaces;

public interface ICallback<T> {
    void onSuccess(T result);
    void onFailure(String error);
}
