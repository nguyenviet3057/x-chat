package com.planx.xchat.retrofit;

import com.planx.xchat.retrofit.request.LoginRequest;
import com.planx.xchat.retrofit.request.PingRequest;
import com.planx.xchat.retrofit.request.SearchRequest;
import com.planx.xchat.retrofit.request.SignupRequest;
import com.planx.xchat.retrofit.response.LoginResponse;
import com.planx.xchat.retrofit.response.PingResponse;
import com.planx.xchat.retrofit.response.SearchResponse;
import com.planx.xchat.retrofit.response.SignupResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
//    String API_BASE_URL = "http://192.168.1.24:8000/api/";
//    String API_BASE_URL = "http://10.0.2.2:8000/api/";
    String API_BASE_URL = "https://planx-dev000.000webhostapp.com/api/";
    String API_PING = "ping";
    String API_LOGIN = "login";
    String API_SIGNUP = "signup";
    String API_SEARCH = "search";

    @POST(API_PING)
    Call<PingResponse> ping(@Body PingRequest data);

    @POST(API_LOGIN)
    Call<LoginResponse> login(@Body LoginRequest data);

    @POST(API_SIGNUP)
    Call<SignupResponse> signup(@Body SignupRequest data);

    @POST(API_SEARCH)
    Observable<SearchResponse> search(@Body SearchRequest data);
}
