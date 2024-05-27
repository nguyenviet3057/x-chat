package com.planx.xchat.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;

import com.google.auth.oauth2.GoogleCredentials;
import com.planx.xchat.R;
import com.planx.xchat.XChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Utils {
    public static final int dp2px(float dp) {
        return (int) (dp * XChat.resources.getDisplayMetrics().density + 0.5f);
    }

    public static String formatLastTime(long timeMillis) {
        Instant now = Instant.now();
        Instant messageTime = Instant.ofEpochMilli(timeMillis);
        Duration duration = Duration.between(messageTime, now);

        long seconds = duration.getSeconds();

        List<TimeRepresentTemplate> timeRepresents = new ArrayList<>();

        timeRepresents.add(new TimeRepresentTemplate(60*30, "30m ago"));
        timeRepresents.add(new TimeRepresentTemplate(60*15, "15m ago"));
        timeRepresents.add(new TimeRepresentTemplate(60*10, "10m ago"));
        timeRepresents.add(new TimeRepresentTemplate(60*5, "5m ago"));
        timeRepresents.add(new TimeRepresentTemplate(60, "1m ago"));
        timeRepresents.add(new TimeRepresentTemplate(30, "30s ago"));
        timeRepresents.add(new TimeRepresentTemplate(15, "15s ago"));
        timeRepresents.add(new TimeRepresentTemplate(10, "10s ago"));
        timeRepresents.add(new TimeRepresentTemplate(5, "5s ago"));
        timeRepresents.add(new TimeRepresentTemplate(0, "just now"));

        timeRepresents.sort((o1, o2) -> Integer.compare(o2.getTime(), o1.getTime()));

        if (seconds < 0) return "just now";
        if (3600*36 <= seconds) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(messageTime, ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            return localDateTime.format(formatter);
        }
        if (3600*24 <= seconds) return "yesterday";
        if (3600 <= seconds) return (int) Math.floor((double) seconds / (3600)) + "h ago";
        Optional<TimeRepresentTemplate> represent = timeRepresents.stream().filter(timeRepresent -> seconds >= timeRepresent.getTime()).findFirst();
        if (represent.isPresent()) {
            return represent.get().getAlias();
        }
        return "null";
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

    private static final String TAG = "FCMHelper";
    private static final String FCM_SEND_URL = "https://fcm.googleapis.com/v1/projects/xchat-59115/messages:send";
    private static String getAccessToken(Context context) {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.service_account);

            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(inputStream)
                    .createScoped("https://www.googleapis.com/auth/firebase.messaging");
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            Log.e(TAG, "Error getting access token", e);
        }
        return null;
    }

    public static void sendMessageToToken(Context context, String token, String title, String body, String roomId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String accessToken = getAccessToken(context);
                    if (accessToken != null) {
                        URL url = new URL(FCM_SEND_URL);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setUseCaches(false);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                        conn.setRequestProperty("Content-Type", "application/json");

                        JSONObject json = new JSONObject();
                        JSONObject message = new JSONObject();
                        JSONObject notification = new JSONObject();
                        JSONObject data = new JSONObject();

                        notification.put("title", title);
                        notification.put("body", body);

                        data.put("roomId", roomId);

                        message.put("token", token);
                        message.put("notification", notification);
                        message.put("data", data);

                        json.put("message", message);


                        OutputStream outputStream = conn.getOutputStream();
                        outputStream.write(json.toString().getBytes());
                        outputStream.flush();
                        outputStream.close();

                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            Log.d(TAG, "Message sent to token successfully");
                        } else {
                            Log.e(TAG, "Failed to send message to token. Response Code: " + responseCode);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception occurred while sending message to token", e);
                }
                return null;
            }
        }.execute();
    }
}

class TimeRepresentTemplate {
    private int time;
    private String alias;

    public TimeRepresentTemplate(int time, String alias) {
        this.time = time;
        this.alias = alias;
    }

    public int getTime() {
        return time;
    }

    public String getAlias() {
        return alias;
    }
}