package com.planx.xchat.contexts;

import android.content.Context;
import android.content.SharedPreferences;

import com.planx.xchat.entities.User;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "Config";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_AVATAR = "user_avatar";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setUserData() {
        editor.putInt(USER_ID, User.getInstance().getId());
        editor.putString(USER_NAME, User.getInstance().getName());
        editor.putString(USER_AVATAR, User.getInstance().getAvatar());
    }
}
