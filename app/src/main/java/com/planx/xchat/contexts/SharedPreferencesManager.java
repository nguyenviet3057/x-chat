package com.planx.xchat.contexts;

import android.content.Context;
import android.content.SharedPreferences;

import com.planx.xchat.XChat;
import com.planx.xchat.models.MainUser;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferencesManager instance;
    private static final String PREF_NAME = "Config";
    private static final String PREF_COOKIES = "COOKIES";
    private static final String JWT_TOKEN = "jwt_token";
    private static final String USER_ID = "USER_ID";
    private static final String USER_FIRSTNAME = "USER_FIRSTNAME";
    private static final String USER_LASTNAME = "USER_LASTNAME";
    private static final String USER_FULLNAME = "USER_FULLNAME";

    private SharedPreferencesManager() {
        sharedPreferences = XChat.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPreferencesManager getInstance() {
        if (instance == null) {
            instance = new SharedPreferencesManager();
        }
        return instance;
    }

    public void setUserData() {
        editor.putString(USER_ID, MainUser.getInstance().getId()).apply();
        editor.putString(USER_FIRSTNAME, MainUser.getInstance().getFirstName()).apply();
        editor.putString(USER_LASTNAME, MainUser.getInstance().getLastName()).apply();
        editor.putString(USER_FULLNAME, MainUser.getInstance().getFullName()).apply();
        editor.commit();
    }

    public void getUserData() {
        MainUser.getInstance().setId(sharedPreferences.getString(USER_ID, null));
        MainUser.getInstance().setFirstName(sharedPreferences.getString(USER_FIRSTNAME, null));
        MainUser.getInstance().setLastName(sharedPreferences.getString(USER_LASTNAME, null));
        MainUser.getInstance().setFullName(sharedPreferences.getString(USER_FULLNAME, null));
    }

    public void setJwtToken(String token) {
        editor.putString(JWT_TOKEN, token).apply();
        editor.commit();
    }

    public void clearJwtToken() {
        editor.putString(JWT_TOKEN, null).apply();
        editor.commit();
    }

    public String getJwtToken() {
        return sharedPreferences.getString(JWT_TOKEN, "");
    }

    public String getJwtTokenCookie() {
        return JWT_TOKEN.concat("=" + getJwtToken());
    }

    public void setCookies(Set<String> cookieString) {
        editor.putStringSet(PREF_COOKIES, cookieString).apply();
        editor.commit();
    }

    public Set<String> getCookies() {
        return sharedPreferences.getStringSet(PREF_COOKIES, new HashSet<>());
    }
}
