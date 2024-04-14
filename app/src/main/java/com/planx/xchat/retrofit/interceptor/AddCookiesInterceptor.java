package com.planx.xchat.retrofit.interceptor;

import com.planx.xchat.contexts.SharedPreferencesManager;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        HashSet<String> preferences = (HashSet<String>) SharedPreferencesManager.getInstance().getCookies();

        StringBuilder cookiestring = new StringBuilder();
        for (String cookie : preferences) {
            String[] parser = cookie.split(";");
            cookiestring.append(parser[0]).append("; ");
        }
        builder.addHeader("Cookie", cookiestring.toString());

        return chain.proceed(builder.build());
    }
}
