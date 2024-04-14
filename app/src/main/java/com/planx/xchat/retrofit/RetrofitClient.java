package com.planx.xchat.retrofit;

import com.planx.xchat.models.User;
import com.planx.xchat.retrofit.interceptor.AddCookiesInterceptor;
import com.planx.xchat.retrofit.interceptor.ReceivedCookiesInterceptor;
import com.planx.xchat.retrofit.request.SearchRequest;
import com.planx.xchat.retrofit.response.SearchResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance = null;
    private ApiService apiService;

    private RetrofitClient() {
        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new AddCookiesInterceptor());
        builder.addInterceptor(new ReceivedCookiesInterceptor());
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    public <T> void sendRequest(Call<T> call, final ApiResponseCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Error " + response.code() + ": " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}
