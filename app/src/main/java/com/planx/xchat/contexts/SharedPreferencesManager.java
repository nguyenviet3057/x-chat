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
    private static final String LOGIN_STATUS = "LOGIN_STATUS";
    private static final String USER_ID = "USER_ID";
    private static final String USER_FIRSTNAME = "USER_FIRSTNAME";
    private static final String USER_LASTNAME = "USER_LASTNAME";
    private static final String USER_FULLNAME = "USER_FULLNAME";
    private static final String USER_AVATAR = "USER_AVATAR";
    private static final String USER_FCMTOKEN = "USER_FCMTOKEN";

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
        editor.putString(USER_ID, MainUser.getInstance().getId());
        editor.putString(USER_FIRSTNAME, MainUser.getInstance().getFirstName());
        editor.putString(USER_LASTNAME, MainUser.getInstance().getLastName());
        editor.putString(USER_FULLNAME, MainUser.getInstance().getFullName());
        editor.putString(USER_AVATAR, MainUser.getInstance().getAvatar());
        editor.putString(USER_FCMTOKEN, MainUser.getInstance().getFcmToken());
        editor.apply();
    }

    public void getUserData() {
        MainUser.getInstance().setId(sharedPreferences.getString(USER_ID, null));
        MainUser.getInstance().setFirstName(sharedPreferences.getString(USER_FIRSTNAME, null));
        MainUser.getInstance().setLastName(sharedPreferences.getString(USER_LASTNAME, null));
        MainUser.getInstance().setFullName(sharedPreferences.getString(USER_FULLNAME, null));
        MainUser.getInstance().setAvatar(sharedPreferences.getString(USER_AVATAR, null));
        MainUser.getInstance().setFcmToken(sharedPreferences.getString(USER_FCMTOKEN, null));
    }

    public void clearData() {
        MainUser.getInstance().setInstance(null);

        editor.clear();
        editor.commit();

        XChat.firestore.clearPersistence();
    }

    public void setJwtToken(String token) {
        editor.putString(JWT_TOKEN, token);
        editor.apply();
    }

    public void clearJwtToken() {
        editor.putString(JWT_TOKEN, null);
        editor.apply();
    }

    public String getJwtToken() {
        return sharedPreferences.getString(JWT_TOKEN, "");
    }

    public String getJwtTokenCookie() {
        return JWT_TOKEN.concat("=" + getJwtToken());
    }

    public void setCookies(Set<String> cookieString) {
        editor.putStringSet(PREF_COOKIES, cookieString);
        editor.commit();
    }

    public Set<String> getCookies() {
        return sharedPreferences.getStringSet(PREF_COOKIES, new HashSet<>());
    }

    public String getUserId() {
        return sharedPreferences.getString(USER_ID, null);
    }

    public void setFCMToken(String fcmToken) {
        editor.putString(USER_FCMTOKEN, fcmToken);
        editor.apply();
    }

    public int getLoginStatus() {
        return sharedPreferences.getInt(LOGIN_STATUS, -1);
    }

    public void setLoginStatus(int status) {
        editor.putInt(LOGIN_STATUS, status).commit();
    }
}