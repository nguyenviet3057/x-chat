package com.planx.xchat.retrofit.interceptor;

import com.planx.xchat.contexts.SharedPreferencesManager;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = (HashSet<String>) SharedPreferencesManager.getInstance().getCookies();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }

            SharedPreferencesManager.getInstance().setCookies(cookies);
        }

        return originalResponse;
    }
}
