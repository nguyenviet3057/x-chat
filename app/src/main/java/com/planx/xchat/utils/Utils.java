package com.planx.xchat.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.TypedValue;

import com.planx.xchat.XChat;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static final int dp2px(float dp) {
        return (int) (dp * XChat.resources.getDisplayMetrics().density + 0.5f);
    }

    public static String formatLastTime(long timeMillis) {
        Instant now = Instant.now();
        Instant messageTime = Instant.ofEpochMilli(timeMillis);
        Duration duration = Duration.between(messageTime, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "just now";
        } else if (seconds < 60 * 60) {
            long minutes = seconds / 60;
            return minutes + "m ago";
        } else if (seconds < 60 * 60 * 24 * 2) {
            long hours = seconds / (60 * 60);
            return hours + "h ago";
        } else {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(messageTime, ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            return localDateTime.format(formatter);
        }
    }

    public static String readJsonFileFromAssets(Context context, String filename) {
        AssetManager assetManager = context.getAssets();
        try {
            assetManager.open(filename).close();
            String json = null;
            try {
                InputStream inputStream = assetManager.open(filename);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        } catch (IOException e) {
            return "";
        }
    }

    public static JSONArray loadJSONArrayFromAsset(Context context, String fileName) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            return new JSONArray(json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
